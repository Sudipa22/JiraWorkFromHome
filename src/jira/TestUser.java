package jira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestUser {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String username ="sudipa.behera@wipro.com" ;//(String) toolObj.get("userName");
	     String password = "QnfTMp84f8f6z5KioeFI8840";//(String) toolObj.get("password");
	     
	     assignUser(username,password);
	}
	public static String assignUser(String username, String password) throws IOException {
		System.out.println("Assignable users");
	String userUrl ="https://jirado.atlassian.net/rest/api/3/user/assignable/search?project=ANJ";

			URL obj7 = new URL(userUrl);
			HttpURLConnection conn = (HttpURLConnection) obj7.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			String userpass = username + ":" + password;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);
			/*OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			System.out.println(out);
			out.close();*/
			int code = conn.getResponseCode();
			System.out.println(code);
					 String readLine = null;
			 if (code == HttpURLConnection.HTTP_OK) {
			        BufferedReader in = new BufferedReader(
			            new InputStreamReader(conn.getInputStream()));
			        StringBuffer response = new StringBuffer();
			        while ((readLine = in .readLine()) != null) {
			            response.append(readLine);
			        } in .close();
			        // print result
			        System.out.println("JSON String Result " + response.toString());
			        //GetAndPost.POSTRequest(response.toString());
			    } else {
			        System.out.println("GET NOT WORKED");
			    }
			return readLine;
		}
	}


