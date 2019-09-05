package jira;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestClass {
public static void main(String args[])
{
	 String userName ="sudipa.behera@wipro.com" ;//(String) toolObj.get("userName");
     String password = "QnfTMp84f8f6z5KioeFI8840";//(String) toolObj.get("password");
     String  emailAddress= "ashish.090singh@gmail.com\"";
     String displayName= "Ashish Singh";
     createUser(userName,password,emailAddress,displayName);
}

public static String createUser(String username,String password,String emailAddress,String displayName) {
	  System.out.println("Creating users");
	  System.out.println("Creating Components"); 
	  String userUrl ="https://jirado.atlassian.net/rest/api/3/user"; 
	  
	  try 
	  { 
		  URL obj7 =new URL(userUrl); 
		  HttpURLConnection conn = (HttpURLConnection)obj7.openConnection(); 
		  conn.setRequestProperty("Content-Type","application/json"); 
		  conn.setDoOutput(true); 
		  conn.setRequestMethod("POST");
	  String userpass = username + ":" + password; 
	  String basicAuth = "Basic " +javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8")); 
	  conn.setRequestProperty ("Authorization", basicAuth); 
	  String data =
			  "{\r\n" + 
	  "    \"emailAddress\": \""+emailAddress+"\",\r\n" +
	  "    \"displayName\": \""+displayName+"\"\r\n" +
	   "              }"; 
	  System.out.println(data); 
	  OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream()); 
	  out.write(data); out.close(); 
	  int code = conn.getResponseCode(); 
	  System.out.println("Response code::: "+code); 
	  if(code!=201) 
	  { 
		  switch(code) 
		  { 
		  case 401:
			  System.out.println("Check the credentials.Unauthorized access");
			  break;
	      case 403:
	    	  System.out.println("Caller dosesnot have permission to create components");
	    	  break; 
	      case 404:System.out.println("User doesnot exist"); } 
		  throw new
	  RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode()); }
	  
	  else 
	  { 
		  BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
		  String line = ""; 
		  while ((line = reader.readLine())!= null) 
		  { 
			  return line; 
			  } 
		  } 
	  } 
	  catch(Exception e) 
	  { 
		  System.out.println(e);
		  return "error"; 
		  } 
	  return "success";
	  
	  
	  }
}
