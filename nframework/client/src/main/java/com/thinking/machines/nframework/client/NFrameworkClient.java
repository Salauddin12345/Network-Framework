package com.thinking.machines.nframework.client;
import java.net.*;
import java.io.*;
import com.thinking.machines.nframework.common.*;
import java.nio.charset.*;
import com.google.gson.*;

public class NFrameworkClient
{
public Object execute(String servicePath,Object ...arguments) throws Throwable
{
try
{
Configuration c;
FileReader fileReader=new FileReader(new File("config.json"));
Gson gson=new Gson();
c=gson.fromJson(fileReader,Configuration.class);
Socket socket=new Socket(c.ip,c.port);
Request requestObject=new Request();
requestObject.setServicePath(servicePath);
requestObject.setArguments(arguments);
String requestJsonString=JSONUtil.toJSON(requestObject);
byte requestBytes[]=requestJsonString.getBytes(StandardCharsets.UTF_8);
byte header[]=new byte[1024];
int x=requestBytes.length;
int i,j,k;
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
InputStream inputStream=socket.getInputStream();
int bytesReadCount;
j=0;
int chunkSize=1024;
while(j<requestBytes.length)
{
if((requestBytes.length-j)<chunkSize) chunkSize=requestBytes.length-j;
outputStream.write(requestBytes,j,chunkSize);
outputStream.flush();
j=j+chunkSize;
}

header=new byte[1024];
byte tmp[]=new byte[1024];
j=0;
int bytesToReceive=1024;
bytesReadCount=0;
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

byte responseBytes[]=new byte[bytesToReceive];
j=0;
bytesReadCount=0;
k=0;
while(j<bytesToReceive)
{
bytesReadCount=inputStream.read(tmp);
for(i=0;i<bytesReadCount;i++)
{
responseBytes[k]=tmp[i];
k++;
}
j=j+bytesReadCount;
}

String responseJsonString=new String(responseBytes,StandardCharsets.UTF_8);
Response responseObject=JSONUtil.fromJSON(responseJsonString,Response.class);
if(responseObject.getSuccess())
{
return responseObject.getResult();
}
else
{
throw responseObject.getException();
}
}catch(Exception exception)
{
System.out.println(exception);
}
return null;
}
}