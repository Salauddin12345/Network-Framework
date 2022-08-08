package com.thinking.machines.nframework.common;
import com.google.gson.*;

public class JSONUtil
{
private JSONUtil(){}
public static String toJSON(java.io.Serializable serializable)
{
try
{
Gson gson=new Gson();
return gson.toJson(serializable);
}catch(Exception e)
{
System.out.println("message 1 : "+e);
System.out.println("message 2 : "+e.getMessage());
return "{}";
}
}
public static <T> T fromJSON(String jsonString,Class<T> c)
{
try
{
Gson gson=new Gson();
return gson.fromJson(jsonString,c);
}catch(Exception e)
{
return null;
}
}

public static Request getRequest(String jsonString,Class argumentsType[])
{
try
{
Gson gson=new Gson();
JsonObject jsonObject=gson.fromJson(jsonString,JsonObject.class);
Request request=new Request();
JsonElement jsonElement=jsonObject.get("servicePath");
request.setServicePath(jsonElement.getAsString());
JsonArray jsonArray=jsonObject.getAsJsonArray("arguments");
// testing 

JsonArray ele=jsonObject.getAsJsonArray("arguments");
System.out.println(ele);
// testing ends here
int i=0;
Object arguments[]=new Object[argumentsType.length];
JsonElement element;
while(i<jsonArray.size())
{
System.out.println("in json util : ");
element=jsonArray.get(i);
arguments[i]=gson.fromJson(element.toString(),argumentsType[i]);
i++;
}
request.setArguments(arguments);
return request;
}catch(Exception exception)
{
System.out.println("message : "+exception.getMessage());
return null;
}
}

}