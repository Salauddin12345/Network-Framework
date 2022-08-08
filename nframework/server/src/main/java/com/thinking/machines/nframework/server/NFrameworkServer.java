package com.thinking.machines.nframework.server;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;
import com.google.gson.*;
import com.thinking.machines.nframework.server.annotations.*;
import com.thinking.machines.nframework.common.*;

import java.io.*;

public class NFrameworkServer
{
private ServerSocket serverSocket;
private Set<Class> tcpNetworkServiceClasses;
private Map<String,TCPService> services;
public NFrameworkServer()
{
this.tcpNetworkServiceClasses=new HashSet<>();
this.services=new HashMap<>();
}
public void registerClass(Class c)
{
Path pathOnType;
Path pathOnMethod;
Method methods[];
String fullPath;
TCPService tcpService=null;
pathOnType=(Path)c.getAnnotation(Path.class);
if(pathOnType==null) return;
methods=c.getDeclaredMethods();
int count=0;
for(Method method:methods)
{
pathOnMethod=(Path)method.getAnnotation(Path.class);
if(pathOnMethod==null) continue;
count++;
fullPath=pathOnType.value()+pathOnMethod.value();
tcpService=new TCPService();
tcpService.c=c;
tcpService.method=method;
services.put(fullPath,tcpService);
}
if(count>0) this.tcpNetworkServiceClasses.add(c);
}

public TCPService getTCPService(String path)
{
return services.get(path);
}

public void start()
{
try
{
int port;
FileReader fileReader=new FileReader(new File("config.json"));
Gson gson=new Gson();
Configuration c=gson.fromJson(fileReader,Configuration.class);
port=c.port;
serverSocket=new ServerSocket(port);
Socket socket;
RequestProcessor requestProcessor;
while(true)
{
System.out.println("server is ready to accept request at port number "+port);
socket=serverSocket.accept();
requestProcessor=new RequestProcessor(this,socket);
}

}catch(Exception e)
{
System.out.println(e.getMessage());
// to do 
}
}

}