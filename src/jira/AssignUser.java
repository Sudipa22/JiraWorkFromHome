package jira;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.jgit.revwalk.filter.SubStringRevFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AssignUser {
	static List<String> list1 = new ArrayList<>();
	static List<String> list2 = new ArrayList<>();
	static List<String> list3 = new ArrayList<>(list1);
	 static List<String> different = new ArrayList();
		public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	    String str = "";
		String url = "https://jirado.atlassian.net";
		String userName = "sudipa.behera@wipro.com";
		String password = "QnfTMp84f8f6z5KioeFI8840";
		list1.add('"' + "ashish.090singh@gmail.com" + '"');
		list1.add('"' + "subhasanket.satapathy@wipro.com" + '"');
		list1.add('"' + "admin" + '"');
		list1.add('"' + "sern.had@gmail.com" + '"');
		
		System.out.println(list1);
		
		/*
		 * for(Object proj3 : list1) { JSONObject projObj3 = (JSONObject) proj3;
		 * JSONArray list1 = (JSONArray) projObj3.get("user");
		 */
		for(int i=0;i<list1.size();i++)
		{
			String s = list1.get(i).toString();
			for(int j=0;j<s.length();j++)
			{
				if(s.contains("admin"))
				{
					list3.add('"'+"admin"+'"');
					break;
				}
				
				String temp=s.substring(1,s.indexOf("@"));
				
				list3.add('"'+temp+'"');
				System.out.println(temp);
				break;
			}
			System.out.println(list3);
	              
	
		}
		String url1 = getUser(userName, password);
		System.out.println(url1);
		JSONParser parser = new JSONParser();
		JSONArray obj= (JSONArray) parser.parse(url1);
		System.out.println(" obj::"+ obj);
		 //Iterator itr2 =  obj.iterator(); 
         
	       /* while (itr2.hasNext()) { 
	            System.out.println( " : " +itr2.next()); 
	            Map.Entry pair = itr1.next(); 
	            System.out.println(pair.getKey() + " : " + pair.getValue()); 
	        } */
	    
	     // iterating phoneNumbers 
	        Iterator itr2 = obj.iterator(); 
            List<String> keySet=new ArrayList<>();
	        while (itr2.hasNext())  
	        { 
	          Map<String,Object>  map = ((Map) itr2.next());
	           for(String name:map.keySet())
	           {
	        	   String s=(String) map.get("key");
	        	   s='"'+s+'"';
	        	   keySet.add(s);
	        	  str=keySet.toString();
	        	  break;
	           }
	        }
	        
	        
	    
	        System.out.println("keySet::"+keySet);
			different.addAll(list3);
		//	list3.retainAll(keySet);
			different.addAll(keySet);
			list3.retainAll(keySet);
	          different.removeAll( list3 );
			System.out.println(list3);
			System.out.println(different);
		//	System.out.println(list2);
		assignUser(userName, password, list3);
		System.out.println("Users Assigned");
		assignUser(userName,password,different);
		

	
	}

	public static String assignUser(String username, String password, List<String> list3) {
		System.out.println("Assigning users");
		String userUrl = "https://jirado.atlassian.net/rest/api/3/project/ANJ/role/10002";

		try {
			URL obj9 = new URL(userUrl);
			HttpURLConnection conn = (HttpURLConnection) obj9.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String userpass = username + ":" + password;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);

			String data = "{\r\n" + "  \"user\": " + list3 + "\r\n" + "}\r\n" + "";
			System.out.print(data);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			int code = conn.getResponseCode();
			System.out.println(code);
			if (code != 201) {
				switch (code) {
				case 400:
					System.out.println("Request is incorrect.");
					break;
				case 401:
					System.out.println("Authorization error");
					break;
				case 404:
					System.out.println("We can't find the user in any accessible user directory");
					break;
				}
				//throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					return line;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			return "error";
		}
		return "success";

	}

	public static String getUser(String username, String password) throws IOException {
		System.out.println("Get users");
		String userUrl = "https://jirado.atlassian.net/rest/api/3/user/assignable/search?project=ANJ";
		String arr = "";
		URL obj7 = new URL(userUrl);
		HttpURLConnection conn = (HttpURLConnection) obj7.openConnection();
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		String userpass = username + ":" + password;
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
		conn.setRequestProperty("Authorization", basicAuth);
		int code = conn.getResponseCode();
		System.out.println(code);
		String readLine = null;
		if (code == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();
			// print result
			arr = response.toString();
			// System.out.println("JSON String Result " + arr);

			// GetAndPost.POSTRequest(response.toString());
		} else {
			System.out.println("GET NOT WORKED");
		}

		return arr;
	}
}
