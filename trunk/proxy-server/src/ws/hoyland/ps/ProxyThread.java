package ws.hoyland.ps;

import java.net.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.*;

public class ProxyThread extends Thread {
	private Socket socket = null;
	private static final int BUFFER_SIZE = 32768;
	private BufferedWriter out;
	private BufferedReader in;
	private String host;
	private static final String CRLF = "\r\n";
	private static final String SERVER = "http://www.hoyland.ws";
	private String url = "";
	private String method = "";
	
	public ProxyThread(Socket socket) {
		super("ProxyThread");
		this.socket = socket;
	}

	public void run() {
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String line;
			boolean hf = true; // header flag
//			boolean cf = false; //content flag
			Map<String, String> rps = new HashMap<String, String>();

			String[] tokens = null;
			StringBuffer content = new StringBuffer();

			// 分析请求
			while ((line = in.readLine()) != null) {
				if ("".equals(line)) {
					break;
				}
				
//				if(cf){
//					content.append(line).append(CRLF);
//				}else{
					if (hf) {
						tokens = line.split(" ");
						method = tokens[0];
						url = tokens[1];
						hf = false;
					} else {
						tokens = line.split(": ");
						rps.put(tokens[0], tokens[1]);
						if("Host".equals(tokens[0])){
							host = tokens[1];
						}
					}
//				}
			}
			
			int index = -1;
			if(method!=null&&"POST".equals(method)){
				char[] cs = new char[1024];
				index = in.read(cs, 0, 1024);
				while (index != -1) {
					content.append(cs, 0, index);
					if(index<1024)
					{
						break;
					}
					index = in.read(cs, 0, 1024);
				}
			}
			
			// in.close();

			if (!url.startsWith("http://")) {
				return;
			}
			// System.out.println(method);
			System.out.println(url);
			System.out.println(rps);
			// System.out.println(content);

			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod(method);
			conn.setDoInput(true);
			conn.setDoOutput(true);
//			conn.setUseCaches(false);

			for (String key : rps.keySet()) {
				conn.setRequestProperty(key, rps.get(key));
			}

			try{
				conn.connect();
				if(method!=null&&"POST".equals(method)){
					OutputStream ops = conn.getOutputStream();
					ops.write(content.toString().getBytes());
					ops.flush();
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
				return;
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			try{
				if(conn.getResponseCode()==411){
					System.out.println(411);
					return;
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
				return;
			}
			
			StringBuffer hs = new StringBuffer();
			Map<String, List<String>> headers = conn.getHeaderFields();
			if (headers.size() > 0) {
				// 状态码
				String vs = "";
				for (String v : headers.get(null)) {
					vs += v;
				}

				hs.append(vs).append(CRLF);

				for (String key : headers.keySet()) {
					if (key != null && !"Transfer-Encoding".equals(key)) {
						//vs = "";
						for (String v : headers.get(key)) {
							hs.append(key + ": " + v).append(CRLF);
						}
					}
				}

				hs.append(CRLF);
				// out.println(hs.toString());
				out.write(hs.toString());
				// out.append(CRLF);
				out.flush();
			}

			// if
			// (conn.getContentType()!=null&&conn.getContentType().startsWith("text/html"))
			// {
			InputStream is = conn.getInputStream();
			OutputStream os = socket.getOutputStream();
//			OutputStream datum = new ByteArrayOutputStream();
			
			// System.out.println();
			// System.out.println(hs);
			// OutputStream os;
			// System.out.println(conn.getContentEncoding());
			if (conn.getContentType()!=null&&conn.getContentType().startsWith("text/html")) {
				if ("gzip".equals(conn.getContentEncoding())) {
					is = new GZIPInputStream(is);
					os = new GZIPOutputStream(os);
	//				datum =  new GZIPOutputStream(datum);  
				}
			}
			// out.close();

			// baos.write("HTTP/1.1 200 OK".getBytes());
			// baos.write(CRLF.getBytes());
			// baos.write(CRLF.getBytes());
			// baos.write("OK".getBytes());

			// 读取内容
			byte[] bs = new byte[BUFFER_SIZE];
			index = is.read(bs, 0, BUFFER_SIZE);
			while (index != -1) {
				// System.out.println(socket.isConnected());
				baos.write(bs, 0, index);
				index = is.read(bs, 0, BUFFER_SIZE);
			}

			// 转发
			// System.out.println(new String(baos.toByteArray()));
			// ByteArrayOutputStream ba = new ByteArrayOutputStream();
			//稳定之后，再做产品，设计
			//时间限制（可选）,自动登录（可选）
			//focus限制,  自动点击, 验证码,  
			if (conn.getContentType()!=null&&conn.getContentType().startsWith("text/html")) {
				String ct = new String(baos.toByteArray());
				
				boolean edited = false;
//				if(host.endsWith("hoolbux.com")||host.endsWith("nvbux.com")){//解决focus问题, 没有限制时间
//					if (ct.contains("if(!fc && !fc_override) {")) {				
//						ct = ct.replace("if(!fc && !fc_override) {",
//								"if(false) {");
//						ct = ct.replace("numbercounter_n -= 0.5;", "numbercounter_n = 0;");
//						edited = true;
//					}
//				}else 
				
				if(host.endsWith("hoolbux.com")||host.endsWith("nvbux.com")||host.endsWith("sekbux.com")||host.endsWith("termbux.com")||host.endsWith("dearbux.com")||host.endsWith("koolbux.com")){ //解决focus问题, 展示时间有限制
					//hoolbux.com 和 nvbux.com 不做解除时间限制，本来可以解除 2013.07.26
					if (ct.contains("if(!fc && !fc_override) {")) {
						ct = ct.replace("if(!fc && !fc_override) {",	//解决focus 问题
								"if(false) {");
//						ct = ct.replace("function captcha_callback(ID) {",
//								"function captcha_callback(ID) {\n          alert('ok');\n");
						edited = true;
					}
				}else if(host.endsWith("probux.com")){ //不需解决focus问题? 展示时间有限制
					if(ct.contains("$(\"#m_ok\").show();")){//自动关闭，自动点击，需要先进入 view ads
						ct = ct.replace("$(\"#m_ok\").show();",
							"$(\"#m_ok\").show();\n				window.opener.nt();\n				window.close();");
						edited = true;
					}
					if(ct.contains("/js/m/viewads7.js")){//自动点击
						//ct = ct.replace("$(document).ready(function(){", "function nt(){\n	alert(\"next\");\n};\n$(document).ready(function(){");
						ct = ct.replace("$(document).ready(function(){", "$(document).ready(function(){\n		setTimeout(\"nt();\", 5000);");
						ct = ct.replace("/js/m/viewads7.js", SERVER+"/ptcsky/probux/viewads7.js"); 
						edited = true;
					}
					//自动点Grid
//					if(ct.contains("var cnt = 1;")){
//						ct = ct.replace("var cnt = 1;",
//								"var cnt = 50;");
//						edited = true;
//					}
				}else if(host.endsWith("neobux.com")){//不需要首先解决对焦问题?
					if(ct.contains("https://fullcache-neodevlda.netdna-ssl.com/js/jv_107.js")){
						ct = ct.replace("https://fullcache-neodevlda.netdna-ssl.com/js/jv_107.js",
								SERVER+"/ptcsky/neobux/jv_107.js");	//浏览完广告后，自动点adprize
						//ct = ct.replace("/cdi2.swf", "");
						edited = true;
					}
					if(ct.contains("l1l();jQuery(document).ready(function() {")){
						StringBuffer sb = new StringBuffer();
						sb.append("						var links = new Array();\n");
						sb.append("						function nt(){\n");
						sb.append("							if(links.length>0){\n");
						sb.append("								var lk = links.shift();\n");
						sb.append("								window.open(lk);\n");
						sb.append("							}else if(d00('ap_h')!=null){\n");
						sb.append("								window.open(d00('ap_h').href);\n");
						sb.append("							}\n");
						sb.append("						};\n");
						sb.append("l1l();jQuery(document).ready(function() {\n");
						sb.append("				jQuery(\"img[src*='estrela_16.gif']\").each(function(){\n");
						sb.append("					jQuery(\"a[id=l\"+this.id.substring(4)+\"]\").each(function() {\n");
						sb.append("						links.push(this.href);\n");
						sb.append("					});\n");
						sb.append("				});\n");
						sb.append("				setTimeout(\"nt();\", 5000);");
						ct = ct.replace("l1l();jQuery(document).ready(function() {", sb.toString());
						edited = true;
					}
					
					if(ct.contains("<div class=\"f_b\" style=\"font-size:18px;color:#bd0000;\">")){ //adv 出错情况处理
						ct = ct.replace("<div class=\"f_b\" style=\"font-size:18px;color:#bd0000;\">", "<div id=\"ptcerr\" class=\"f_b\" style=\"font-size:18px;color:#bd0000;\">");
						edited = true;
					}
				}else if(ct.contains("beforeunload")){//关闭页面时候出现对话窗口
					ct = ct.replace("beforeunload", "x");
					edited = true;
				}

				if(edited){//修改过
					baos = new ByteArrayOutputStream();
					baos.write(ct.getBytes());
				}
			}
			
			
			//最终需要发出内容
//			datum.write(baos.toByteArray());
//			datum.flush();
			
			os.write(baos.toByteArray());
			if(os instanceof GZIPOutputStream){
				((GZIPOutputStream)os).finish();
			}
			os.flush();
			// out.write(baos.toByteArray());
			// out.flush();
		} catch (Exception e) {
			System.out.println(method+"->"+url);
			e.printStackTrace();
		} finally {
			try {
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
			// System.out.println("=========================");
		}
	}
}