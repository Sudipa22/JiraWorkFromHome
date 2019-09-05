
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class jira {
	static List<String> list1 = new ArrayList<>();
	static List<String> list2 = new ArrayList<>();
	static List<String> list3 = new ArrayList<>();
	static List<String> different = new ArrayList();
	static String pwd = System.getProperty("user.dir");
	static File file = new File(pwd);
	static File parentPath = file.getParentFile();
	static String jsonFile = "data.json";
	static String key = "";
	static String url="";

	// To check whether the proj key exist or not
	public static String checkUrl(String url, String userName, String password) {
		try {
			URL obj3 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj3.openConnection();
			conn.setRequestMethod("GET");
			String userpass = userName + ":" + password;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			// String basicAuth = "Basic " + userpass.getBytes("UTF-8");
			conn.setRequestProperty("Authorization", basicAuth);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				return line;
			}

		} catch (Exception e) {
			return "success";
		}
		return "error";
	}

	// Create Components
	public static String createComponent(String name, String description, String leadUserName, String asigneeType,
			String username, String password, String projKey) {
		System.out.println("Creating Components");
		String componentUrl = "https://jirado.atlassian.net/rest/api/3/component";
		try {
			URL obj6 = new URL(componentUrl);
			HttpURLConnection conn = (HttpURLConnection) obj6.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String userpass = username + ":" + password;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);
			String data = "{\r\n" + "    \"name\": \"" + name + "\",\r\n" + "    \"description\": \"" + description
					+ "\",\r\n" + "    \"leadUserName\": \"" + leadUserName + "\",\r\n" + "    \"assigneeType\": \""
					+ asigneeType + "\",\r\n" + "    \"isAssigneeTypeValid\": false,\r\n" + "    \"project\": \""
					+ projKey + "\"\r\n" + "              }";
			// System.out.println(data);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			int code = conn.getResponseCode();
			if (code != 201) {
				switch (code) {
				case 401:
					System.out.println("Check the credentials.Unauthorized access");
					break;
				case 403:
					System.out.println("Caller dosesnot have permission to create components");
					break;
				case 404:
					System.out.println("Project doesnot exist");
				}
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					return line;
				}
			}
		} catch (Exception e) {
			return "error";
		}
		return "success";
	}

	// Create Releases
	public static String createReleases(String description, String name, String userReleaseDate, Boolean archived,
			Boolean released, String username, String password, String projKey) {
		String addReleaseUrl = "https://jirado.atlassian.net/rest/api/latest/version";
		// System.out.println(name);
		try {
			URL obj6 = new URL(addReleaseUrl);
			HttpURLConnection conn = (HttpURLConnection) obj6.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String userpass = username + ":" + password;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			// String basicAuth = "Basic " +userpass.getBytes("UTF-8");
			conn.setRequestProperty("Authorization", basicAuth);
			String data = "{\r\n" + "              \"description\":\"" + description + "\",\r\n"
					+ "              \"name\":\"" + name + "\",\r\n" + "              \"userReleaseDate\": \""
					+ userReleaseDate + "\",\r\n" + "              \"project\": \"" + projKey + "\",\r\n"
					+ "              \"archived\": " + archived + ",\r\n" + "              \"released\": " + released
					+ "\r\n" + "}";
			System.out.println(data);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				return line;
			}
			return "Release created";
		} catch (Exception e) {
			return e.getMessage();
		}

	}

	// Create issues
	public static String createIssues(String url, String rigletName, String username, String password, String key) {
		System.out.println("Uploading Issues");
		JSONParser parser = new JSONParser();
		System.out.println("rigletname " + rigletName);
		try {
			URL obj4 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj4.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String userpass = username + ":" + password;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);
			Object object;
			object = parser.parse(new FileReader(
					file + "/" + rigletName + "/json/jira/uploadedJson/" + "issues-" + rigletName + ".json"));
			JSONArray issueArr = (JSONArray) object;
			System.out.print(issueArr);
			int length = issueArr.size();
			int count = 0;
			String data = "{\r\n" + "\"issueUpdates\": [\r\n";
			java.util.Iterator i = issueArr.iterator();
			while (i.hasNext()) {
				count++;
				JSONObject issue = (JSONObject) i.next();

				if (count < length) {
					data += "{\r\n" + "\"update\":{},\r\n" + "\"fields\": {\r\n" + "\"project\":\r\n" + "{ \r\n"
							+ " \"key\": \"" + key + "\"\r\n" + "},\r\n" + "\"summary\": \"" + issue.get("summary")
							+ "\",\r\n" + "\"description\": \"" + issue.get("description") + "\",\r\n"
							+ "\"issuetype\": {\r\n" + "              \"id\": \"10000\"\r\n" + " },\r\n"
							+ "\"assignee\": {\r\n" + "    \"name\": \"" + issue.get("assignee") + "\"\r\n" + "},\r\n"
							+ "\"reporter\": {\r\n" + "    \"name\": \"" + issue.get("reporter") + "\"\r\n" + "},\r\n"
							+ "\"labels\": [\r\n" + "\"label\"" + "]\r\n" + "}\r\n" + "},";
				} else {
					data += "{\r\n" + "\"update\":{},\r\n" + "\"fields\": {\r\n" + "\"project\":\r\n" + "{ \r\n"
							+ " \"key\": \"" + key + "\"\r\n" + "},\r\n" + "\"summary44\": \"" + issue.get("summary")
							+ "\",\r\n" + "\"description\": \"" + issue.get("description") + "\",\r\n"
							+ "\"issuetype\": {\r\n" + "              \"id\": \"10000\"\r\n" + "},\r\n"
							+ "\"assignee\": {\r\n" + "    \"name\": \"" + issue.get("assignee") + "\"\r\n" + "},\r\n"
							+ "\"reporter\": {\r\n" + "    \"name\": \"" + issue.get("reporter") + "\"\r\n" + "},\r\n"
							+ "\"labels\": [\r\n" + "\"label\"" + "]\r\n" + "}\r\n" + "}" + "]\r\n" + "}\r\n";
				}
			}
			System.out.println(data);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			int code = conn.getResponseCode();
			if (code != 201) {
				switch (code) {
				case 400:
					System.out.println("Issues are not sent in proper format");
					break;

				}
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = "";
//                                            while ((line = reader.readLine())!= null) 
//                                            {
//                                                            //return line;
//                                                            JSONObject json = (JSONObject) parser.parse(line);
//                                                            JSONArray issuearray = (JSONArray) json.get("issues");
//                                                            java.util.Iterator i1 = issuearray.iterator();
//                                                            while(i1.hasNext()) {
//                                                                            JSONObject issue=(JSONObject)i1.next();
//                                                                            String issuekey = (String) issue.get("key");
//                                                                            System.out.println(issuekey);
//                                                                            String[] label = {"label1","label2"}; 
//
//                                                                            //addField(issuekey,label,username,password);
//                                                            }
//
//                                            }
				while ((line = reader.readLine()) != null) {
					return line;
				}
			}

		} catch (Exception e) {

			return "error";
		}
		return "success";
	}

	// To create project
	public static String createProject(String url, String userName, String password, String projName, String key,
			String projectLead, String description) {
		try {
			// System.out.print(key);
			System.out.println("Creating Project" + projName);
			URL obj3 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj3.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			System.out.println("password " + password);
			String userpass = userName + ":" + password;
			String basicAuth = "Basic "
					+ javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);
			String data = "{\r\n" + "  \"key\": \"" + key + "\",\r\n" + "  \"name\": \"" + projName + "\",\r\n"
					+ "  \"projectTypeKey\": \"software\",\r\n"
					+ "  \"projectTemplateKey\": \"com.pyxis.greenhopper.jira:gh-scrum-template\",\r\n"
					+ "  \"description\": \"" + description + "\",\r\n" + "  \"assigneeType\": \"PROJECT_LEAD\",\r\n"
					+ "  \"lead\":\"" + projectLead + "\"\r\n" + "}";
			System.out.println(data);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			int code = conn.getResponseCode();
			if (code != 201) {
				switch (code) {
				case 400:
					System.out.println("Request is incorrect.");
					break;
				case 401:
					System.out.println("Authorization error");
					break;
				case 403:
					System.out.println("User doesnot have rights to create the project");
					break;
				}
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					return line;
				}
			}
		} catch (IOException e) {
			System.out.println("Project Name already Exists");
		} catch (Exception e) {
			return "error";
		}
		return "success";
	}

	// Create user
	/*
	 * public static String createUser(String url, String username, String password,
	 * String emailAddress, String displayName) {
	 * System.out.println("Creating users"); // String userUrl
	 * ="https://jirado.atlassian.net/rest/api/3/user";
	 * 
	 * try { URL obj7 = new URL(url); HttpURLConnection conn = (HttpURLConnection)
	 * obj7.openConnection(); conn.setRequestProperty("Content-Type",
	 * "application/json");
	 * 
	 * conn.setDoOutput(true); conn.setRequestMethod("POST"); String userpass =
	 * username + ":" + password; String basicAuth = "Basic " +
	 * javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8")
	 * ); conn.setRequestProperty("Authorization", basicAuth); String data = "{\r\n"
	 * + "    \"emailAddress\": \"" + emailAddress + "\",\r\n" +
	 * "    \"displayName\": \"" + displayName + "\"\r\n" + "              }";
	 * System.out.println(data); OutputStreamWriter out = new
	 * OutputStreamWriter(conn.getOutputStream()); out.write(data); out.close(); int
	 * code = conn.getResponseCode(); System.out.println(code); if (code != 201) {
	 * switch (code) { case 400: System.out.println("Request is incorrect."); break;
	 * case 401: System.out.println("Authorization error"); break; case 403:
	 * System.out.println("User doesnot have rights to create the user"); break; }
	 * throw new RuntimeException("Failed : HTTP error code : " +
	 * conn.getResponseCode()); } else { BufferedReader reader = new
	 * BufferedReader(new InputStreamReader(conn.getInputStream())); String line =
	 * ""; while ((line = reader.readLine()) != null) { return line; } } } catch
	 * (IOException e) { System.out.println("User already Exists"); } catch
	 * (Exception e) { System.out.println(e); return "error"; } return "success"; }
	 */
	
	
	public static String assignUser(String username, String password, List<String> list1) {
		System.out.println("Assigning users");
		String userUrl = url+"/rest/api/3/project/"+key+"/role/10002";

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

			String data = "{\r\n" + "  \"user\": "+list1+"\r\n" + "}\r\n" + "";
			System.out.print(data);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			int code = conn.getResponseCode();
			System.out.println(code);
			if (code != 201) {
				switch (code) {
				case 400:
					System.out.println("Request is incorrect");
					break;
				case 401:
					System.out.println("Authorization error");
					break;
				/*
				 * case 403: System.out.println("User doesnot have rights to create the user");
				 * break;
				 */
				case 404:
					System.out.println("We can't find the user in any accessible user directory");
					break;
				}
			//	throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
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
		String userUrl = url+"/rest/api/3/user/assignable/search?project="+key;
		
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


	private static String postData1(String ip, String rigletName, List<String> list) {
		try {
			String url = ip + "/api/projects/storeProjectData";
			URL obj3 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj3.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// String userpass = userName + ":" + password;
			// String basicAuth = "Basic " +
			// javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			// conn.setRequestProperty ("Authorization", basicAuth);
			String data = "{\"blueprintData\":" + list + ",\"projectName\":\"" + rigletName + "\"}";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println("line " + line);
				return line;
			}
		} catch (Exception e) {
			System.out.println("error +e");
			return "err";
		}
		return "error";
	}

	private static String postData(String ip, String rigletName, String list) {
		try {
			String url = "http://3.15.143.196:5050/api/riglets/saveToolProjectInfo";
			URL obj3 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj3.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// String userpass = userName + ":" + password;
			// String basicAuth = "Basic " +
			// javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			// conn.setRequestProperty ("Authorization", basicAuth);
			String data = list;
			System.out.println("listin new " + list);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println("**** line " + line);
				return line;
			}
		} catch (Exception e) {
			return "err";
		}
		return "error";
	}

	private static String getServerInfo(String rigletName, String serverIp) {
		try {
			JSONParser parser = new JSONParser();
//                                                String url=serverIp+"/api/projects/getToolserverDetails";
			String url = serverIp + "/riglets/getToolserverDetails";
			System.out.println(url);
			URL obj3 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj3.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// String userpass = userName + ":" + password;
			// String basicAuth = "Basic " +
			// javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			// conn.setRequestProperty ("Authorization", basicAuth);
			System.out.println("rigletname: " + rigletName);
			String data = "{\"rigletName\":\"" + rigletName + "\",\"toolName\":\"jira\"}";
			System.out.println(data);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String temp = "";
			System.out.println("line " + line);
			while ((line = reader.readLine()) != null) {
				System.out.println("temp " + line);
				temp += line;
				return line;
			}

			JSONObject obj98 = (JSONObject) parser.parse(temp);
			System.out.println("temp1S " + temp);
			return temp;
		} catch (SocketException e) {
			System.out.println("inside socket exception \n Aborted");
		} catch (Exception e) {
			System.out.println("inside catch " + e);
			return "err";
		}
		return "error";
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
//  
//		String rigletName = args[0];
//                                                       String serverIp = args[1];
		String rigletName = "new_check";
		String serverIp = "http://23.101.140.72:3010";
//                       String blueprintName = "oss";
		JSONParser parser = new JSONParser();
		Object obj;
		obj = parser.parse(new FileReader(file + "/" + rigletName + "/json/" + jsonFile));
		JSONObject jsonObject = (JSONObject) obj;
		JSONObject almObj = (JSONObject) jsonObject.get("alm");
		JSONObject projsObj = (JSONObject) almObj.get("projects");
		JSONArray projArr = (JSONArray) projsObj.get("project");
		
		
		  JSONObject blueObj = (JSONObject) jsonObject.get("riglet_info");
		  System.out.println(blueObj);
		  String emailArr = (String) blueObj.get("auth_users");
		  
		  String email[]=emailArr.toString().split(";");
		  for (String str : email) {
			  System.out.println(str);
			
		}
		 
		
		 
		Object res = parser.parse(getServerInfo(rigletName, serverIp));
		System.out.println("res" + res);
		JSONObject toolObj = (JSONObject) res;
		url = "https://jirado.atlassian.net";// (String) toolObj.get("url");
		String userName = "sudipa.behera@wipro.com";// (String) toolObj.get("userName");
		String password = "QnfTMp84f8f6z5KioeFI8840";// (String) toolObj.get("password");
		// String password = "digitalrig@123";
//                                String url ="http://ec2-18-191-16-16.us-east-2.compute.amazonaws.com:8080";
//                                String userName = "rig";
//                                String password = "digitalrig@123";
		System.out.println("url" + url);
		System.out.println("username " + userName);
		String projectName = "";
		
		String projectTypeKey = "software";
		String projectLead = "admin";
		String redescription = "";
		String rename = "";
		String releaseDate = "";
		Boolean archived, released;
		//String emailAddress[] = new String[10;
		String displayName = "";
		String name1 = "";
		String compName = "";
		String compDesc = "";
		String compAssigneeType = "PROJECT_LEAD";
		String description;

		// Create Project
		for (Object proj : projArr) {
			JSONObject projObj = (JSONObject) proj;
			Boolean createStatus = (Boolean) projObj.get("create");

			if (createStatus) {
				projectName = (String) projObj.get("project_name");
				projectTypeKey = (String) projObj.get("project_typeKey");
				projectLead = (String) projObj.get("project_lead");
				description = (String) projObj.get("description");
				int length = 3;
				String data = null;
				while (data == null) {
					String name = projectName.replaceAll("[^a-zA-Z0-9]", "");
					// System.out.println("name "+name);
					if (name.length() >= length) {
						key = name.substring(0, Math.min(name.length(), length)).toUpperCase();
						 System.out.println(key);
					} /*
						 * else { String appendStr = ""; int projLength = name.length(); int
						 * currentLength = length; int len = currentLength % projLength; int repeat =
						 * currentLength / projLength; for (int i = 0; i < repeat; i++) { appendStr =
						 * appendStr + name; } appendStr = appendStr + name.substring(0,
						 * Math.min(name.length(), len)); // System.out.println(appendStr); key =
						 * appendStr.toUpperCase(); System.out.println(key); }
						 */
					String projUrl = url + "/rest/api/3/project";
					System.out.println(projUrl);
					boolean createProj = true;
					while (createProj == true) {
						String createProjectStatus = createProject(projUrl, userName, password, projectName, key,
								projectLead, description);
						if (createProjectStatus == "error") {
							System.out.println("error in project creation ");
							projectName = rigletName + "-" + projectName;
							data = "error";
							createProj = false;
						} else {
							System.out.println("Project Created Successfully");
							createProj = false;
					/*String checkUrl = url + "/rest/api/3/project/" + key;
					String checkKey = checkUrl(checkUrl, userName, password);
					if (!checkKey.equalsIgnoreCase("success")) {
						length = length + 1;
					} else {
						String projUrl = url + "/rest/api/3/project";
						boolean createProj = true;
						while (createProj == true) {
							String createProjectStatus = createProject(projUrl, userName, password, projectName, key,
									projectLead, description);
							if (createProjectStatus == "error") {
								System.out.println("error in project creation ");
								projectName = rigletName + "-" + projectName;
								data = "error";
								createProj = false;
							} else {
								System.out.println("Project Created Successfully");
								createProj = false;*/
//                                                                                                                Object obj2;
//                                                                                                                obj2 = parser.parse(new FileReader(file+"/"+rigletName+"/json/jira/uploadedJson/"+"components-"+rigletName+".json"));
//                                                                                                                JSONArray componentArr = (JSONArray) obj2;
//                                                                                                                String createcomp="";
//                                                                                                    for(Object componentObj:componentArr) {
//                                                                                                                JSONObject compObj = (JSONObject) componentObj;
//                                                                                                                compName = (String) compObj.get("componentname");
//                                                                                                                compDesc = (String) compObj.get("componentdescription");
//                                                                                                                projectLead = (String) compObj.get("leadusername");
//                                                                                                                 createcomp = createComponent(compName,compDesc,projectLead,compAssigneeType,userName,password,key);
//                                                                                                                
//                                                                                                    }
//                                                                                                    if(createcomp!="error") {
//                                                                                                                System.out.println("Components uploaded successfully");
//                                                                                               Object obj1;
//                                                                                                                obj1 = parser.parse(new FileReader(file+"/"+rigletName+"/json/jira/uploadedJson/"+"issues-"+rigletName+".json"));
//                                                                                                                JSONArray jsonObject1 = (JSONArray) obj1;
//                                                                                                                                String issueUrl = url + "/rest/api/2/issue/bulk";
//                                                                                                                                String checkIssueCreated = createIssues(issueUrl,rigletName,userName,password,key);
//                                                                                                                                if(checkIssueCreated!="error") {
//                                                                                                                                                System.out.println("Issues uploaded successfully");
//                                                                                                                                }
//                                                                                                                                //String component = createComponent(userName,password,projectLead,key);

							}
							List<String> list = new ArrayList<>();
							list.add("{\"toolName\":\"jira\",\"projectName\":\"" + (String) projectName
									+ "\",\"projectKey\":\"" + key + "\",\"category\":\"alm\"}");
							postData1(serverIp, rigletName, list);

							System.out.println("list " + list);
							String data1 = "{\"toolName\":\"jira\",\"rigletName\":\"" + rigletName
									+ "\",\"data\":{\"projectKey\":\"" + key + "\",\"projectName\":\""
									+ (String) projectName + "\",\"projectUrl\":\"" + url + "/projects/" + key
									+ "/summary\"}}";
							System.out.println("link " + data1);
							postData(serverIp, rigletName, data1);
//                                                                                                                                data = "success";
//                                                                                                                                
//                                                                                                                }

						}
					}
				}
			}
	//	}
		/*
		 * for (Object proj1 : projArr) { JSONObject projObj1 = (JSONObject) proj1;
		 * Boolean createuser = (Boolean) projObj1.get("create"); if (createuser) {
		 * 
		 * emailAddress = (String) projObj1.get("emailAddress"); displayName = (String)
		 * projObj1.get("displayName");
		 * 
		 * // String data= null; String userUrl = url + "/rest/api/3/user"; boolean
		 * createuser1 = true; while (createuser1 == true) { String createUserStatus =
		 * createUser(userUrl, userName, password, emailAddress, displayName); if
		 * (createUserStatus != "error") {
		 * System.out.println("User Created Successfully"); createuser1 = false; } else
		 * 
		 * {
		 * 
		 * System.out.println("error in user creation ");
		 * 
		 * // data="error"; createuser1 = false; } List<String> list1 = new
		 * ArrayList<>(); list1.add("{\"toolName\":\"jira\",\"emailAddress\":\"" +
		 * (String) emailAddress + "\",\"displayName\":\"" + displayName +
		 * "\",\"category\":\"alm\"}"); postData1(serverIp, rigletName, list1);
		 * 
		 * System.out.println("list " + list1); String data2 =
		 * "{\"toolName\":\"jira\",\"rigletName\":\"" + rigletName +
		 * "\",\"data\":{\"displayName\":\"" + displayName + "\",\"emailAddress\":\"" +
		 * (String) emailAddress + "\"}}"; System.out.println("link " + data2);
		 * postData(serverIp, rigletName, data2); } } }
		 */

		/*for (Object proj2 : projArr) {
			System.out.println(proj2);
			
			  JSONObject projObj2 = (JSONObject) proj2; 
			  JSONArray projArr2 = (JSONArray) projObj2.get("user");*/
			 
			
			for (Object object : email) {
				list1.add('"'+(String)object+'"');
			}
			System.out.println("list " + list1);
			/*Boolean createuser = (Boolean) projObj2.get("display");
			
					LIST<STRING> LIST1 = NEW ARRAYLIST<>();
					list1.add("{\"emailAddress\":\"" + (String []) emailAddress);
					postData1(serverIp, rigletName, list1);

					System.out.println("list " + list1);*/
	//	}

		for (int i = 0; i < list1.size(); i++) {
			String s = list1.get(i).toString();
			for (int j = 0; j < s.length(); j++) {
				if(s.contains("admin"))
				{
					list3.add('"'+"admin"+'"');
					break;
				}
				String temp = s.substring(1, s.indexOf("@"));
				list3.add('"' + temp + '"');
				System.out.println(temp);
				break;
			}
			System.out.println(list3);
			}
		String url1 = getUser(userName, password);
		System.out.println(url1);
		JSONParser parser1 = new JSONParser();
		JSONArray obj12 = (JSONArray) parser.parse(url1);
		System.out.println(" obj::" + obj12);
		Iterator itr2 = obj12.iterator();
		List<String> keySet = new ArrayList<>();
		while (itr2.hasNext()) {
			Map<String, Object> map = ((Map) itr2.next());
			for (String name : map.keySet()) {
				String s = (String) map.get("key");
				s = '"' + s + '"';
				keySet.add(s);
				// str=keySet.toString();
				break;
			}
		}

		System.out.println("keySet::" + keySet);
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
}

