package ws.hoyland.ps;

import java.net.*;
import java.util.*;
import java.io.*;

public class ProxyThread extends Thread {
	private Socket socket = null;
	private static final int BUFFER_SIZE = 32768;

	public ProxyThread(Socket socket) {
		super("ProxyThread");
		this.socket = socket;
	}

	public void run() {
		try {
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String inputLine;
			int cnt = 0;
			String urlToCall = "";
			String method = "";
			Map<String, String> rps = new HashMap<String, String>();

			//StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				try {
					StringTokenizer tok = new StringTokenizer(inputLine);
					tok.nextToken();
				} catch (Exception e) {
					break;
				}

				if (cnt == 0) {
					String[] tokens = inputLine.split(" ");
					method = tokens[0];
					urlToCall = tokens[1];
				} else {
					String[] tokens = inputLine.split(": ");
					rps.put(tokens[0], tokens[1]);
					//content.append(inputLine + "\n");
				}

				cnt++;
			}

			BufferedReader rd = null;
			try {
				System.out.println(urlToCall);
				if (!urlToCall.startsWith("http://")) {
					return;
				}
				URL url = new URL(urlToCall);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();

				conn.setRequestMethod(method);
				conn.setConnectTimeout(3000);
				conn.setReadTimeout(3000);
				conn.setDoInput(true);
				conn.setDoOutput(true);

				InputStream is = null;
				//OutputStream os = null;

				for(String key: rps.keySet()){
					conn.setRequestProperty(key, rps.get(key));
				}
				//conn.setRequestProperty("Content-Type", "UTF-8");
				
				try {
//					os = conn.getOutputStream();
////					BufferedWriter bw = new BufferedWriter(
////							new OutputStreamWriter(os));
////					bw.write(content.toString());
////					bw.flush();
////					bw.close();
//					os.close();
//					
//					conn.connect();
//					if(os!=null){
//						os.close();
//					}
					conn.connect();
					   Map responseHeader = conn.getHeaderFields();
	                   System.out.println(responseHeader.toString()); 
	                   
					//System.out.println(conn.getResponseMessage());
					is = conn.getInputStream();
				} catch (IOException e) {
					return;
				}

				//conn.
//				if (conn.getContentType()!=null&&conn.getContentType().startsWith("text/html")) {
//					rd = new BufferedReader(new InputStreamReader(is));
//					String line = null;
//					StringBuffer sb = new StringBuffer();
//					while ((line = rd.readLine()) != null) {
//						sb.append(line);
//					}
//					String text = sb.toString();
//					if (sb.toString().contains("focusFlag = 0;")) {
//						text = sb.toString().replace("focusFlag = 0;",
//								"focusFlag = 1;");
//						System.out.println(text);
//					}
//					is.close();
//					is = new ByteArrayInputStream(text.getBytes("UTF-8"));
//				}

				byte by[] = new byte[BUFFER_SIZE];
				int index = is.read(by, 0, BUFFER_SIZE);
				while (index != -1) {
					out.write(by, 0, index);
					index = is.read(by, 0, BUFFER_SIZE);
				}
				out.flush();

			} catch (Exception e) {
				e.printStackTrace();
				out.writeBytes("");
			}

			if (rd != null) {
				rd.close();
			}
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}

			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}