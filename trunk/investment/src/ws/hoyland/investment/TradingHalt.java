package ws.hoyland.investment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class TradingHalt {

	private static List<String> LIST = new ArrayList<String>();

	public static String get(String url) {
		return get(url, null);
	}

	private static String get(String url, String charset) {
		URLConnection conn = null;
		InputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line = null;
		String result = null;
		try {
			conn = new URL(url).openConnection();
			conn.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			conn.setRequestProperty("Cache-Control", "max-age=0");
			in = conn.getInputStream();
			if (charset != null) {
				isr = new InputStreamReader(in, charset);
			} else {
				isr = new InputStreamReader(in, "utf-8");
			}
			br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		return result;
	}

	private static void printHalting(String url, String date) {
		String content = null;
		String link = null;
		try {
			content = get(url, "GB2312");
			if(content.indexOf(date)==-1){
				System.out.println("当日无数据");
				return;
			}
			content = content.substring(0, content.indexOf(date));
			content = content.substring(0, content.indexOf("\" title="));
			link = content.substring(content.lastIndexOf("a href=\"") + 8);
			// System.out.println(link);

			content = get(link, "GB2312");
			content = content.substring(content.indexOf("停牌原因</td></tr>") + 14);
			content = content.substring(0, content.indexOf("</tbody>"));
			// System.out.println(content);

			Pattern pattern = Pattern.compile("<tr>.*?</tr>");
			Matcher matcher = pattern.matcher(content);
			pattern = Pattern.compile("<td>.*?</td>");
			while (matcher.find()) {
				String line = matcher.group();
				line = line.substring(4, line.length() - 5);
				// System.out.println(line);
				Matcher m = pattern.matcher(line);
				int i = 0;
				String code = null;
				String action = null;
				String reason = null;
				while (m.find()) {
					// System.out.println(m.group());
					if (i == 0) {
						code = m.group();
					}
					if (i == 4) {
						action = m.group();
					}
					if (i == 5) {
						reason = m.group();
					}
					i++;
				}

				if (action.indexOf("取消停牌") == -1 && action.indexOf("停牌") != -1
						&& reason.indexOf("重大事项") != -1) {
					if (url.endsWith("chstpyl.html")) {
						LIST.add(code.substring(4, code.length() - 5) + ".SH");
					} else {
						LIST.add(code.substring(4, code.length() - 5) + ".SZ");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printHaltingSSE(ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(
					"http://query.sse.com.cn/infodisplay/querySpecialTipsInfoByPage.do?jsonCallBack=jsonpCallback71393&isPagination=true&searchDate=&bgFlag=1&searchDo=1&pageHelp.pageSize=100&_="
							+ System.currentTimeMillis());
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader("Referer",
					"http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody  = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody.substring(responseBody.indexOf("{"), responseBody.lastIndexOf("}")+1);
//			System.out.println("----------------------------------------");
//            System.out.println(responseBody);
			JSONObject json = new JSONObject(responseBody);
			JSONArray jsonArray = json.getJSONArray("result");  
			 for(int i=0;i<jsonArray.length();i++){ 
			        JSONObject stock = (JSONObject) jsonArray.get(i);
			        boolean flag = false;
			        //||stock.getString("bulletinType").equals("1")临时停牌不做统计
			        //||stock.getString("bulletinType").equals("2") 即将复牌的不做统计
			        if((stock.getString("bulletinType").equals("3")&&stock.getString("stopReason").indexOf("重要事项未公告")!=-1)){
			        	flag = true;
			        }
			        if(flag){
//				        if(stock.getString("productCode").startsWith("0")||stock.getString("productCode").startsWith("3")){
//				        	LIST.add(stock.getString("productCode")+".SZ");
//				        }
				        if(stock.getString("productCode").startsWith("6")){
				        	LIST.add(stock.getString("productCode")+".SH");
				        }
			        }
//			        System.out.println(stock);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void printHaltingSZSE(ResponseHandler<String> responseHandler) {
		List<String> tmpList = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		List<String> queue = new ArrayList<String>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(
					"http://www.szse.cn/szseWeb/FrontController.szse?randnum="+Math.random());
			httpPost.setHeader("Accept", "*/*");
			httpPost.setHeader("Accept-Encoding", "gzip, deflate");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//			httpPost.setHeader("Cache-Control", "max-age=0");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.setHeader("Origin", "http://www.szse.cn");
			httpPost.setHeader("Referer",
					"http://www.szse.cn/main/disclosure/news/tfpts/");
			httpPost.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

//			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
//	        nvps.add(new BasicNameValuePair("ACTIONID", "7"));
//	        nvps.add(new BasicNameValuePair("AJAX", "AJAX-TRUE"));
//	        nvps.add(new BasicNameValuePair("CATALOGID", "1978"));
//	        nvps.add(new BasicNameValuePair("txtKsrq", "2014-06-04"));
//	        nvps.add(new BasicNameValuePair("txtZzrq", "2015-06-04"));
//	        nvps.add(new BasicNameValuePair("TABKEY", "tab1"));
//	        nvps.add(new BasicNameValuePair("tab1PAGECOUNT", "145"));
//	        nvps.add(new BasicNameValuePair("tab1RECORDCOUNT", "4335"));
//	        nvps.add(new BasicNameValuePair("REPORT_ACTION", "navigate"));
//	        nvps.add(new BasicNameValuePair("tab1PAGENUM", "1"));
//	        nvps.add(new BasicNameValuePair("AJAX", "AJAX-TRUE"));
//	        httpPost.setEntity(new UrlEncodedFormEntity(nvps));//UrlEncodedFormEntity
			Calendar calnow = Calendar.getInstance();
			Calendar calstart = (Calendar)calnow.clone();
			calstart.add(Calendar.YEAR, -1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String from = sdf.format(calstart.getTime());
			String now = sdf.format(calnow.getTime());
//			System.out.println(from);
//			System.out.println(now);
			httpPost.setEntity(new StringEntity("ACTIONID=7&AJAX=AJAX-TRUE&CATALOGID=1798&TABKEY=tab1&txtDmorjc=&txtKsrq="+from+"&txtZzrq="+now+"&REPORT_ACTION=search"));

			String responseBody  = httpclient.execute(httpPost, responseHandler);
//			System.out.println(responseBody);
			String ts = responseBody.substring(responseBody.indexOf("共")+1);
			ts = ts.substring(0, ts.indexOf("页"));
			System.out.println(ts);
			int total = Integer.parseInt(ts);
			
			String rc = responseBody.substring(responseBody.indexOf("tab1RECORDCOUNT="));			
			rc = rc.substring(rc.indexOf("=")+1);
			rc = rc.substring(0, rc.indexOf("&"));
			System.out.println(rc);
			int recordCount = Integer.parseInt(rc);
			
			String data = "ACTIONID=7&AJAX=AJAX-TRUE&CATALOGID=1798&txtKsrq="+from+"&txtZzrq="+now+"&TABKEY=tab1&tab1PAGECOUNT="+total+"&tab1RECORDCOUNT="+recordCount+"&REPORT_ACTION=navigate&tab1PAGENUM=";

			for(int i= total;i>0;i--){
				StringEntity entity = new StringEntity(data+i);
				httpPost = new HttpPost(
						"http://www.szse.cn/szseWeb/FrontController.szse?randnum="+Math.random());
				httpPost.setHeader("Accept", "*/*");
				httpPost.setHeader("Accept-Encoding", "gzip, deflate");
				httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//				httpPost.setHeader("Cache-Control", "max-age=0");
				httpPost.setHeader("Connection", "keep-alive");
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				httpPost.setHeader("Origin", "http://www.szse.cn");
				httpPost.setHeader("Referer",
						"http://www.szse.cn/main/disclosure/news/tfpts/");
				httpPost.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");
				
				httpPost.setEntity(entity);
				responseBody = httpclient.execute(httpPost, responseHandler);				
				responseBody = responseBody.substring(responseBody.indexOf("REPORTID_tab1"));
				responseBody = responseBody.substring(responseBody.indexOf("证券代码"));
				responseBody = responseBody.substring(responseBody.indexOf("<tr"));
				responseBody = responseBody.substring(0, responseBody.indexOf("</table>"));
//				System.out.println(responseBody);
				
				Pattern pattern = Pattern.compile("<tr.*?</tr>");
				Matcher matcher = pattern.matcher(responseBody);
				pattern = Pattern.compile("<td.*?</td>");
				while (matcher.find()) {
					String line = matcher.group();
//					line = line.substring(4, line.length() - 5);
//					System.out.println(line);
					Matcher m = pattern.matcher(line);
					int x = 0;
					String code = null;
					String action = null;
					String reason = null;
					while (m.find()) {
//						System.out.println(m.group());
						if (x == 0) {
							code = m.group();
							code = code.substring(code.indexOf(">")+1);
							code = code.substring(0, code.indexOf("<")).trim();
							
						}
						if (x == 4) {
							action = m.group();
							action = action.substring(action.indexOf(">")+1);
							action = action.substring(0, action.indexOf("<")).trim();
						}
						if (x == 5) {
							reason = m.group();
							reason = reason.substring(reason.indexOf(">")+1);
							reason = reason.substring(0, reason.indexOf("<")).trim();
						}
						x++;
					}
					stack.push(code+":"+action+":"+reason);
//					System.out.print(code);
//					System.out.print(action);
//					System.out.println(reason);
				}
				while(stack.size()>0){
					queue.add(stack.pop());
				}
				System.out.println(queue.size());
//				System.out.println(stack.size());
//				break;
			}

			for(String line:queue){
				String[] ls = line.split(":");
				String code = ls[0];
				String action = ls[1];
				String reason = ls[2];
				
				if(action.contains("取消停牌")){
					if(tmpList.contains(code)){
						tmpList.remove(code);
					}
				}
				
				if(action.equals("停牌")||action.contains("1天")){
					if(reason.contains("重大事项")){
						if(!tmpList.contains(code)){
							tmpList.add(code);
						}
					}
				}
			}
			
			System.out.println(tmpList.size());
			
			for(String line:tmpList){
		        if(line.startsWith("0")||line.startsWith("3")){
	        		LIST.add(line+".SZ");
	        	}
			}
			
			System.out.println(LIST.size());
			/**
			responseBody = responseBody.substring(responseBody.indexOf("{"), responseBody.lastIndexOf("}")+1);
//			System.out.println("----------------------------------------");
//            System.out.println(responseBody);
			JSONObject json = new JSONObject(responseBody);
			JSONArray jsonArray = json.getJSONArray("result");  
			 for(int i=0;i<jsonArray.length();i++){ 
			        JSONObject stock = (JSONObject) jsonArray.get(i);
			        boolean flag = false;
			        //||stock.getString("bulletinType").equals("1")临时停牌不做统计
			        if((stock.getString("bulletinType").equals("3")&&stock.getString("stopReason").indexOf("重要事项未公告")!=-1)||stock.getString("bulletinType").equals("2")){
			        	flag = true;
			        }
			        if(flag){
//				        if(stock.getString("productCode").startsWith("0")||stock.getString("productCode").startsWith("3")){
//				        	LIST.add(stock.getString("productCode")+".SZ");
//				        }
//				        if(stock.getString("productCode").startsWith("6")){
				        	LIST.add(stock.getString("productCode")+".SH");
//				        }
			        }
//			        System.out.println(stock);
			 }
			 **/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printInvestors(String code) {
		String content = null;
		String codex = code.substring(0, code.length()-3);
		try {
			content = get("http://www.windin.com/home/stock/stock-mh/" + code
					+ ".shtml");
			if(content==null){
				System.err.println("ERROR CONTENT");
				return;
			}
			// System.out.println(content);
			try{
			content = content.substring(content
					.indexOf("<tr class=\"title3\">"));
			}catch(Exception e){
				System.err.println("ERR:"+code);
			}
			content = content.substring(0, content.indexOf("</table>"));
			// System.out.println(content);

			Pattern pattern = Pattern.compile("<tr.*?</tr>");
			Matcher matcher = pattern.matcher(content);
			pattern = Pattern.compile("<td.*?</td>");
			while (matcher.find()) {
				String line = matcher.group();
				line = line.substring(line.indexOf("<td"), line.length() - 5);
				// System.out.println(line);

				Matcher m = pattern.matcher(line);
				int i = 0;
				String name = null;
				String type = null;
				while (m.find()) {
					// System.out.println(m.group());
					if (i == 0) {
						name = m.group();
						if (name.contains("机构名称")) {
							break;
						}
					}
					if (i == 1) {
						type = m.group();
						type = type.substring(11, type.length() - 5);
					}
					i++;
				}
				if (type != null && type.equals("基金")) {
//					System.out.println("NAME:"+name);
					if(name.indexOf("/home/fund/html/")!=-1){
						name = name.substring(
								name.indexOf("/home/fund/html/") + 16,
								(name.indexOf(".shtml")));
						
						name = name.substring(0, name.length()-3);
					}else{
						name = name.replaceAll("<.*?>", "");
						name = name.replaceAll("</.*?>", "");
						name = name.replaceAll("&nbsp;", "");
						name = name.replaceAll(" ", "");
						name = "[*]"+name;
					}
//					System.out.println("\t" + name + "->" + type);		
					System.out.println("\t" + name);
					if(!name.contains("*")){ //打印基金的持仓
						content = get("http://fund.eastmoney.com/f10/FundArchivesDatas.aspx?type=jjcc&code="+name+"&year=2015&month=3&rt="+Math.random(), "GB2312");
						if(content.indexOf("<table")!=-1){//持仓非空
//							System.out.println("TT:"+code);
							content = content.substring(content.indexOf("<table"));
							content = content.substring(0, content.indexOf("</table>"));
							if(content.contains(codex)){ //当前基金是否持有大量的停牌股票
								content = content.substring(content.indexOf("<tbody"));
								content = content.substring(content.indexOf("<tr>"));
								content = content.substring(0, content.indexOf("</tbody>"));
								
								pattern = Pattern.compile("<tr.*?</tr>");
								matcher = pattern.matcher(content);
								pattern = Pattern.compile("<td.*?</td>");
								while (matcher.find()) {
									boolean cflag = false;
									line = matcher.group();
//									line = line.substring(line.indexOf("<td"), line.length() - 5);
//									System.out.println(line);

									m = pattern.matcher(line);
									int x = 0;
									String ccode = null;
									String cname = null;
									String cpercent = null;
									while (m.find()) {
										// System.out.println(m.group());
										if (x == 1) {
											ccode = m.group();
											ccode = ccode.substring(ccode.indexOf(">")+1);
											ccode = ccode.substring(ccode.indexOf(">")+1);
											ccode = ccode.substring(0, ccode.indexOf("<"));
//											if (name.contains("机构名称")) {
//												break;
//											}
											if(ccode.equals(codex)){
												cflag = true;
											}
										}
										if (x == 2) {
											cname = m.group();
											cname = cname.substring(cname.indexOf(">")+1);
											cname = cname.substring(cname.indexOf(">")+1);
											cname = cname.substring(0, cname.indexOf("<"));
										}
//										cpercent = m.group();
										//if (x == 6) {
										if(m.group().contains("%")){
											cpercent = m.group();
											cpercent = cpercent.substring(cpercent.indexOf(">")+1);
											cpercent = cpercent.substring(0, cpercent.indexOf("<"));
										}
										x++;
									}
									if(cflag){
										System.out.println("\t\t"+ccode+"\t"+cname+"->"+cpercent);
										break;
									}
								}
							}
						}
					}
				}
				/**
				 * if(action.indexOf("取消停牌")==-1&&action.indexOf("停牌") !=
				 * -1&&reason.indexOf("重大事项") != -1){
				 * if(url.endsWith("chstpyl.html")){ LIST.add(code.substring(4,
				 * code.length()-5)+".SH"); }else{ LIST.add(code.substring(4,
				 * code.length()-5)+".SZ"); } }
				 **/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printHSIPE(ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(
					"http://alphainvestments.hk/en/market.php");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
//			httpGet.setHeader("Referer",
//					"http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody  = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody.substring(responseBody.indexOf("HSI Volatility Index (VHSI)"), responseBody.indexOf(" x</td>"));
			responseBody = responseBody.substring(responseBody.lastIndexOf(">")+1);
//			System.out.println("----------------------------------------");
            System.out.println(responseBody);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        
		// http://wap.eastmoney.com/NewsList.aspx?m=145&c=401 //沪市停牌
		// http://wap.eastmoney.com/NewsList.aspx?m=145&c=402 //深市停牌

		// http://stock.eastmoney.com/news/chstpyl.html
		// http://stock.eastmoney.com/news/csstpyl.html

		// http://www.windin.com/home/stock/html/600389.SH.shtml?q=600389&t=1
		// http://www.windin.com/home/stock/stock-mh/002574.SZ.shtml
		// http://fund.eastmoney.com/f10/FundArchivesDatas.aspx?type=jjcc&code=150001&year=2015&month=3&rt=0.9432023034896702

		SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
		Date date = new Date();
		String title = sdf.format(date);
		System.out.println(title);
		System.out.println("000300 PE Ratio(TTM): ");
		System.out.print("HSI PE Ratio: ");
		printHSIPE(responseHandler);
		System.out.println("========================");
		// System.out.println("沪市停牌");
		printHalting("http://stock.eastmoney.com/news/chstpyl.html", title);
		// System.out.println("深市停牌");
		printHalting("http://stock.eastmoney.com/news/csstpyl.html", title);

		// http://query.sse.com.cn/infodisplay/querySpecialTipsInfoByPage.do?jsonCallBack=jsonpCallback71393&isPagination=true&searchDate=&bgFlag=1&searchDo=1&pageHelp.pageSize=100&_=1433417470230
		// http://www.szse.cn/szseWeb/FrontController.szse?randnum=0.9035767468158156
//		printHaltingSSE(responseHandler);
//		printHaltingSZSE(responseHandler);

		for (String code : LIST) {
			System.out.println(code);
			printInvestors(code);
			// break;
		}
	}

}
