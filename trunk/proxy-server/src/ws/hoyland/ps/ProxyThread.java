package ws.hoyland.ps;

import java.net.*;
import java.util.*;
import java.io.*;

public class ProxyThread extends Thread {
	private Socket socket = null;
	private static final int BUFFER_SIZE = 32768;
	private PrintStream out;
	private BufferedReader in;	
	
	private static final String CRLF = "\r\n";
	
	public ProxyThread(Socket socket) {
		super("ProxyThread");
		this.socket = socket;
	}

	public void run() {
		try {
			out = new PrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String line;
			boolean hf = true; // header flag
//			boolean cf = false; //content flag
			String url = "";
			String method = "";
			Map<String, String> rps = new HashMap<String, String>();

			String[] tokens = null;
//			StringBuffer content = new StringBuffer();
			
			//分析请求
			while ((line = in.readLine()) != null) {
				if("".equals(line)){
					break;
				}
//				if(cf){
//					content.append(line+"\n");
//				}else{
					if (hf) {
						tokens = line.split(" ");
						method = tokens[0];
						url = tokens[1];
						hf = false;
					} else {
						tokens = line.split(": ");
						rps.put(tokens[0], tokens[1]);
					}
//				}
			}
//			in.close();
			
			if (!url.startsWith("http://")) {
				return;
			}
//			System.out.println(method);
			System.out.println(url);
			System.out.println(rps);
//			System.out.println(content);
			
			
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			
			for(String key: rps.keySet()){
				conn.setRequestProperty(key, rps.get(key));
			}
			
			conn.connect();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			StringBuffer hs = new StringBuffer();			
			Map<String, List<String>> headers = conn.getHeaderFields();
			if(headers.size()>0){
				//状态码
				String vs = "";
				for(String v:headers.get(null)){
					vs += v;
				}
				
				hs.append(vs).append(CRLF);
				
				for(String key:headers.keySet()){					
					if(key!=null){
						vs = "";
						for(String v:headers.get(key)){
							vs += v;
						}
						hs.append(key+": "+vs).append(CRLF);
					}
				}
				
				hs.append(CRLF);
//				out.println(hs.toString());
				baos.write(hs.toString().getBytes());
//				out.append(CRLF);
//				out.flush();
			}
			
//			if (conn.getContentType()!=null&&conn.getContentType().startsWith("text/html")) {
			InputStream is = conn.getInputStream();
//			OutputStream os = socket.getOutputStream();

//			System.out.println();
//			System.out.println(hs);
			
			//System.out.println(conn.getContentEncoding());
//			if("gzip".equals(conn.getContentEncoding())){
//				is = new GZIPInputStream(is);
//				os = new GZIPOutputStream(os);
//			}
			//out.close();

//			baos.write("HTTP/1.1 200 OK".getBytes());
//			baos.write(CRLF.getBytes());
//			baos.write(CRLF.getBytes());
//			baos.write("OK".getBytes());
			
			//读取内容
			byte[] bs = new byte[BUFFER_SIZE];
			int index = is.read(bs, 0, BUFFER_SIZE);
			while (index != -1) {
				//System.out.println(socket.isConnected());
				baos.write(bs, 0, index);
				index = is.read(bs, 0, BUFFER_SIZE);
			}
			
			//转发
//			System.out.println(new String(baos.toByteArray()));
//			ByteArrayOutputStream ba = new ByteArrayOutputStream();
			
			out.write(baos.toByteArray());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (socket != null) {
					socket.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
//			System.out.println("=========================");
		}
	}
}