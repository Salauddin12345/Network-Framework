package com.thinking.machines.nframework.server;
import java.net.*;
import java.nio.charset.*;
import com.thinking.machines.nframework.common.*;
import java.io.*;
import java.lang.reflect.*;

class RequestProcessor extends Thread
{
private NFrameworkServer server;
private Socket socket;
RequestProcessor(NFrameworkServer server,Socket socket)
{
this.server=server;
this.socket=socket;
start();
}
public void run()
{
try
{
InputStream inputStream=socket.getInputStream();
byte header[]=new byte[1024];
byte tmp[]=new byte[1024];
int i,j,k;
j=0;
int bytesToReceive=1024;
int bytesReadCount=0;
k=0;
while(j<bytesToReceive)
{
bytesReadCount=inputStream.read(tmp);
for(i=0;i<bytesReadCount;i++)
{
header[k]=tmp[i];
k++;
}
j=j+bytesReadCount;
}

bytesToReceive=0;
i=1023;
j=1;
while(i>=0)
{
bytesToReceive+=(header[i]*j);
j=j*10;
i--;
}

byte requestBytes[]=new byte[bytesToReceive];
j=0;
bytesReadCount=0;
k=0;
while(j<bytesToReceive)
{
bytesReadCount=inputStream.read(tmp);
for(i=0;i<bytesReadCount;i++)
{
requestBytes[k]=tmp[i];
k++;
}
j=j+bytesReadCount;
}
String requestJsonString=new String(requestBytes,StandardCharsets.UTF_8);
Request requestObject=JSONUtil.fromJSON(requestJsonString,Request.class);
TCPService tcpService=server.getTCPService(requestObject.getServicePath());
Class para[]=tcpService.method.getParameterTypes();
requestObject=JSONUtil.getRequest(requestJsonString,para);
Object newarr[]=requestObject.getArguments();
System.out.println("\nRequest has been received\n");
Response responseObject=null;
if(tcpService==null)
{
responseObject=new Response();
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("invalid path : "+requestObject.getServicePath()));
}
else
{
Class c=tcpService.c;
Method m=tcpService.method;
try
{
Object obj=c.newInstance();
Object result=m.invoke(obj,requestObject.getArguments());
responseObject=new Response();
responseObject.setSuccess(true);
responseObject.setResult(result);
responseObject.setException(null);
}catch(InstantiationException instantiationException)
{
responseObject=new Response();
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("unable to create object of class associated with path : "+requestObject.getServicePath()));
}
catch(IllegalAccessException illegalAccessException)
{
responseObject=new Response();
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("unable to access method of object of class associated with path : "+requestObject.getServicePath()));
}
catch(InvocationTargetException invocationTargetException)
{
Throwable t=invocationTargetException.getCause();
responseObject=new Response();
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(t);
}
}

String responseJsonString=JSONUtil.toJSON(responseObject);
byte responseBytes[]=responseJsonString.getBytes(StandardCharsets.UTF_8);

header=new byte[1024];
int x=responseBytes.length;
i=1023;
while(x>0)
{
j=x%10;
x=x/10;
header[i]=(byte)j;
i--;
}
OutputStream outputStream=socket.getOutputStream();
outputStream.write(header,0,1024);
outputStream.flush();

j=0;
int chunkSize=1024;
while(j<responseBytes.length)
{
if((responseBytes.length-j)<chunkSize) chunkSize=responseBytes.length-j;
outputStream.write(responseBytes,j,chunkSize);
outputStream.flush();
j=j+chunkSize;
}
System.out.println("\nResponse has been sent\n");
socket.close();
}catch(IOException ioException)
{
System.out.println(ioException);
}
}

}