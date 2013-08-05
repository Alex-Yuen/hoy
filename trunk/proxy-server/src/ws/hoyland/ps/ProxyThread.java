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
	private static final String SERVER = "www.ptcsky.net";
	private String url = "";
	private String method = "";
	private Map<String, String> rps;
	
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
			rps = new HashMap<String, String>();

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

			OutputStream os = socket.getOutputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			if(url.endsWith("/ptcskygetcode")){
				StringBuffer hs = new StringBuffer();
				hs.append("HTTP/1.1 200 OK"+CRLF);
				hs.append("Content-Type: text/html"+CRLF);
				//hs.append("Content-Length: 2"+CRLF);
				hs.append(CRLF);
//				hs.append(ProxyServer.CODE);
				// out.println(hs.toString());
				out.write(hs.toString());
//				out.write(ProxyServer.CODE);
				// out.append(CRLF);
				out.flush();
//				out.close();
//				in.close();
//				socket.close();

				//baos = new ByteArrayOutputStream();
				baos.write(ProxyServer.CODE.getBytes());
//				os.write(baos.toByteArray());
//				os.flush();
			//	return;
			}else{
				if(host.equals(SERVER)){
					StringBuffer hs = new StringBuffer();
					hs.append("HTTP/1.1 200 OK"+CRLF);
					hs.append("Content-Type: application/javascript"+CRLF);
					//hs.append("Content-Length: 2"+CRLF);
					hs.append(CRLF);
//					hs.append(ProxyServer.CODE);
					// out.println(hs.toString());
					out.write(hs.toString());
//					out.write(ProxyServer.CODE);
					// out.append(CRLF);
					out.flush();
					
					//读取文件
					String path = url.substring(21);
					InputStream fis = ProxyServer.class.getResourceAsStream("/res"+path);
					
					byte[] bs = new byte[BUFFER_SIZE];
					index = fis.read(bs, 0, BUFFER_SIZE);
					while (index != -1) {
						// System.out.println(socket.isConnected());
						baos.write(bs, 0, index);
						index = fis.read(bs, 0, BUFFER_SIZE);
					}
					
					if(fis!=null){
						fis.close();
					}
				}else {
					// System.out.println(method);
					System.out.println(url);
					System.out.println(rps);
					// System.out.println(content);
	
					HttpURLConnection.setFollowRedirects(false);//不自动处理302错误
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
					
					if (conn.getContentType()!=null&&conn.getContentType().startsWith("image/jpeg")&&url.contains("captcha_proc.php")) {
						if(host.endsWith("sekbux.com")||host.endsWith("jeetbux.com")){
							//写入image
							//识别image
							//记录image
							ProxyServer.CODE = "";//68 
						}
					}
					
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
						
						if(host.endsWith("jeetbux.com")||host.endsWith("sekbux.com")||host.endsWith("mettabux.com")){ //解决focus问题, 展示时间有限制
							//hoolbux.com 和 nvbux.com 不做解除时间限制，本来可以解除 2013.07.26
							//tested for sekbux
							if (ct.contains("if(!fc && !fc_override) {")) {
								ct = ct.replace("if(!fc && !fc_override) {",	//解决focus 问题
										"if(false) {");
		//						ct = ct.replace("function captcha_callback(ID) {",
		//								"function captcha_callback(ID) {\n          alert('ok');\n");
								
								StringBuffer sb = new StringBuffer();
								sb.append("if(getObject('captchaenter')) {\n");
								sb.append("		getObject('captchaenter').focus();\n");
								sb.append("		xmlHttp = new XMLHttpRequest();\n");
								sb.append("		xmlHttp.onreadystatechange = stateChanged;\n");
								sb.append("		xmlHttp.open(\"GET\", \"/ptcskygetcode\", true);\n");
								sb.append("		xmlHttp.send(null);\n");
								sb.append("}\n");
								
								ct = ct.replace("if(getObject('captchaenter')) getObject('captchaenter').focus();", sb.toString());
								
								sb = new StringBuffer();
								
								sb.append("var xmlHttp;\n");
								sb.append("function stateChanged(){ \n");
								sb.append("	if(xmlHttp.readyState==4){ \n");
								//sb.append("alert(xmlHttp.status);\n");
								sb.append("	if(xmlHttp.status==200||xmlHttp.status==0){\n");
								sb.append("		getObject('captchaenter').value = xmlHttp.responseText;\n");
								sb.append("		getObject('captchaenter').docaptcha(xmlHttp.responseText);\n");
								sb.append("	}\n");
								sb.append("	}\n");
								sb.append("}\n");
								sb.append("\n");
								sb.append("function bust() {\n");
								
								ct = ct.replace("function bust() {", sb.toString());
								
								ct = ct.replace("if(parent.window.opener) {parent.window.opener.disablead(ID);}", "if(parent.window.opener) {parent.window.opener.disablead(ID);parent.window.opener.nt();window.close();}");
								edited = true;
							}
							
							if(url.endsWith("/pages/clickads")&&ct.contains("(function() {")){
								StringBuffer sb = new StringBuffer();
								sb.append("var ids = new Array();\n");
								sb.append("function nt(){\n");
								sb.append("if(ids.length>0){\n");
								sb.append("var id = ids.shift();\n");
								sb.append("var ex = document.getElementById(\"clickads_checkimg_\"+id).onclick;\n");
								//sb.append("alert(ex);");
								sb.append("//var exl = ex.split('\\n');\n");
								sb.append("ex.call();\n");
								sb.append("}else{\n");
								if(!host.endsWith("mettabux.com")){
									sb.append("window.location.href=\"http://"+host+"/pages/acc/adgrid\";\n");
								}
								sb.append("}\n");
								sb.append("}\n");
								sb.append("  \n");
								sb.append("(function() {\n");
								sb.append("$(\".clickads_wrapper1\").each(function(){\n");
								if(!host.endsWith("jeetbux.com")){
									sb.append("if(this.style.opacity==''){\n");
								}else{
									sb.append("{\n");
									//sb.append("alert(this.id.substring(18))\n");
								}
								sb.append("ids.push(this.id.substring(18));\n");
								sb.append("}\n");
								sb.append("});	\n");
								sb.append("setTimeout(\"nt();\", 2000);\n");
								ct = ct.replace("(function() {", sb.toString());
								
								if(host.endsWith("jeetbux.com")){
									ct = ct.replace("if((lastopened==ID && viaball==1) ) {", "if((viaball==1) ) {");
								}
								edited = true;
							}
							
							if(url.endsWith("/pages/acc/adgrid")&&this.rps.get("Referer")!=null&&this.rps.get("Referer").contains("/pages/clickads")){
								StringBuffer sb = new StringBuffer();
								sb.append("window.open('/pages/acc/adgridopen/'+i+'/'+j,'','');\n");							
								sb.append("       }\n");
								sb.append("\n");
								sb.append("function nt(){\n");
								sb.append("if($(getObject('chancesleftspan')).text()>0){\n");
								sb.append("	var x = parseInt(15*Math.random())+1;\n");
								sb.append("	var y = parseInt(24*Math.random())+1;\n");
								sb.append("	if(boxclicked(x+'_'+y)) {nt();return;}\n");
								sb.append("	agc(x, y);\n");
								sb.append("}\n");
								sb.append("}\n");
								sb.append("\n");
								sb.append("setTimeout(\"nt();\", 2000);\n");
								sb.append("\n");
								System.out.println(ct.indexOf("window.open('/pages/acc/adgridopen/'+i+'/'+j,'','');\n        }"));
								ct = ct.replace("window.open('/pages/acc/adgridopen/'+i+'/'+j,'','');\n        }", sb.toString());
								edited = true;
							}
							
							if(url.contains("/pages/acc/adgridopen/")){
								ct = ct.replace(",status);}", ",status); parent.window.opener.nt();window.close();}");
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
								ct = ct.replace("/js/m/viewads7.js", "http://"+SERVER+"/ptcsky/probux/viewads7.js"); 
								edited = true;
							}
							if(ct.contains("$(\"#m_error\").show();")){
								ct = ct.replace("$(\"#m_error\").show();", "$(\"#m_error\").show();\nwindow.opener.location.href=window.opener.location.href;\nwindow.close();"); 
								edited = true;
							}
							//自动点Grid
							if(ct.contains("drawTable();")){
								StringBuffer sb = new StringBuffer();
								sb.append("var cs = new Array();\n");
								sb.append("function nt(){\n");
								sb.append("	var idx = parseInt(cs.length*Math.random());\n");
								sb.append("	var url = cs[idx];\n");
								sb.append("	cs.splice(idx, 1);\n");
								sb.append("	if($(\"#myChances\").text()>0){\n");
								sb.append("		$(url).click();\n");
								sb.append("	}\n");
								sb.append("}\n\n");
								sb.append("$(document).ready(function(){\n");
								sb.append("drawTable();");
								ct = ct.replace("$(document).ready(function(){\r\ndrawTable();", sb.toString());
								
								sb = new StringBuffer();
								sb.append("$(this).attr(\"clicked\",\"1\");\n\t}else{\n");
								sb.append("\t\tcs.push($(this).find(\"a\")[0]);\n");
								sb.append("\t}");
								ct = ct.replace("$(this).attr(\"clicked\",\"1\");\r\n\t}", sb.toString());
								
								sb = new StringBuffer();
								sb.append("var d1 = d.getDate();\n");
								sb.append("setTimeout(\"nt();\", 2000);");
								ct = ct.replace("var d1 = d.getDate();", sb.toString());
								
								edited = true;
							}
		//					if(ct.contains("var cnt = 1;")){
		//						ct = ct.replace("var cnt = 1;",
		//								"var cnt = 50;");
		//						edited = true;
		//					}
						}else if(host.endsWith("neobux.com")){//不需要首先解决对焦问题?
							if(ct.contains("https://fullcache-neodevlda.netdna-ssl.com/js/jv_107.js")){
								ct = ct.replace("https://fullcache-neodevlda.netdna-ssl.com/js/jv_107.js",
										"http://"+SERVER+"/ptcsky/neobux/jv_107.js");	//浏览完广告后，自动点adprize
								//ct = ct.replace("/cdi2.swf", "");
								ct = ct.replace("d00('nxt_bt_a').href = '/v/?xc=' + u;", "d00('nxt_bt_a').href = '/v/?xc=' + u;\nd00('nxt_bt_a').onclick.call();");
								ct = ct.replace("d00('nxt_bt_a').href='/v/?xc='+u;", "d00('nxt_bt_a').href='/v/?xc='+u;\nd00('nxt_bt_a').onclick.call();");
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
								sb.append("				setTimeout(\"nt();\", 2000);");
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
						}else if(ct.contains("top.location")){
							ct = ct.replace("top.location", "window.location");
							edited = true;
						}
		
						if(edited){//修改过
							baos = new ByteArrayOutputStream();
							baos.write(ct.getBytes());
						}
					}else if (conn.getContentType()!=null&&conn.getContentType().contains("javascript")){
						String ct = new String(baos.toByteArray());						
						boolean edited = false;
						
						if(ct.contains("beforeunload")){//关闭页面时候出现对话窗口
							ct = ct.replace("beforeunload", "x");
							edited = true;
						}else if(ct.contains("top.location")){
							ct = ct.replace("top.location", "window.location");
							edited = true;
						}
						
						if(edited){//修改过
							baos = new ByteArrayOutputStream();
							baos.write(ct.getBytes());
						}
					}
				
				}
				//最终需要发出内容
	//			datum.write(baos.toByteArray());
	//			datum.flush();
			}
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