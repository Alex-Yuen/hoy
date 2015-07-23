package ws.hoyland.investment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class Scanner {

	private static List<String> LIST = new ArrayList<String>();
//	private static double CSIRATIO = (17.6d / 4907.06d);
	private static DecimalFormat df = new DecimalFormat("##.00");
	private static String kzsession = "";
	
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
			// e.printStackTrace();
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
			if (content.indexOf(date) == -1) {
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

	/**
	private static void printHaltingSSE(ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(
					"http://query.sse.com.cn/infodisplay/querySpecialTipsInfoByPage.do?jsonCallBack=jsonpCallback71393&isPagination=true&searchDate=&bgFlag=1&searchDo=1&pageHelp.pageSize=100&_="
							+ System.currentTimeMillis());
			httpGet.setHeader("Accept", "* /*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader("Referer",
					"http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody.substring(responseBody.indexOf("{"),
					responseBody.lastIndexOf("}") + 1);
			// System.out.println("----------------------------------------");
			// System.out.println(responseBody);
			JSONObject json = new JSONObject(responseBody);
			JSONArray jsonArray = json.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject stock = (JSONObject) jsonArray.get(i);
				boolean flag = false;
				// ||stock.getString("bulletinType").equals("1")临时停牌不做统计
				// ||stock.getString("bulletinType").equals("2") 即将复牌的不做统计
				if ((stock.getString("bulletinType").equals("3") && stock
						.getString("stopReason").indexOf("重要事项未公告") != -1)) {
					flag = true;
				}
				if (flag) {
					// if(stock.getString("productCode").startsWith("0")||stock.getString("productCode").startsWith("3")){
					// LIST.add(stock.getString("productCode")+".SZ");
					// }
					if (stock.getString("productCode").startsWith("6")) {
						LIST.add(stock.getString("productCode") + ".SH");
					}
				}
				// System.out.println(stock);
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
	**/

	/**
	private static void printHaltingSZSE(ResponseHandler<String> responseHandler) {
		List<String> tmpList = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		List<String> queue = new ArrayList<String>();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(
					"http://www.szse.cn/szseWeb/FrontController.szse?randnum="
							+ Math.random());
			httpPost.setHeader("Accept", "* /*");
			httpPost.setHeader("Accept-Encoding", "gzip, deflate");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			// httpPost.setHeader("Cache-Control", "max-age=0");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.setHeader("Origin", "http://www.szse.cn");
			httpPost.setHeader("Referer",
					"http://www.szse.cn/main/disclosure/news/tfpts/");
			httpPost.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			// List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			// nvps.add(new BasicNameValuePair("ACTIONID", "7"));
			// nvps.add(new BasicNameValuePair("AJAX", "AJAX-TRUE"));
			// nvps.add(new BasicNameValuePair("CATALOGID", "1978"));
			// nvps.add(new BasicNameValuePair("txtKsrq", "2014-06-04"));
			// nvps.add(new BasicNameValuePair("txtZzrq", "2015-06-04"));
			// nvps.add(new BasicNameValuePair("TABKEY", "tab1"));
			// nvps.add(new BasicNameValuePair("tab1PAGECOUNT", "145"));
			// nvps.add(new BasicNameValuePair("tab1RECORDCOUNT", "4335"));
			// nvps.add(new BasicNameValuePair("REPORT_ACTION", "navigate"));
			// nvps.add(new BasicNameValuePair("tab1PAGENUM", "1"));
			// nvps.add(new BasicNameValuePair("AJAX", "AJAX-TRUE"));
			// httpPost.setEntity(new
			// UrlEncodedFormEntity(nvps));//UrlEncodedFormEntity
			Calendar calnow = Calendar.getInstance();
			Calendar calstart = (Calendar) calnow.clone();
			calstart.add(Calendar.YEAR, -1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String from = sdf.format(calstart.getTime());
			String now = sdf.format(calnow.getTime());
			// System.out.println(from);
			// System.out.println(now);
			httpPost.setEntity(new StringEntity(
					"ACTIONID=7&AJAX=AJAX-TRUE&CATALOGID=1798&TABKEY=tab1&txtDmorjc=&txtKsrq="
							+ from + "&txtZzrq=" + now
							+ "&REPORT_ACTION=search"));

			String responseBody = httpclient.execute(httpPost, responseHandler);
			// System.out.println(responseBody);
			String ts = responseBody.substring(responseBody.indexOf("共") + 1);
			ts = ts.substring(0, ts.indexOf("页"));
			System.out.println(ts);
			int total = Integer.parseInt(ts);

			String rc = responseBody.substring(responseBody
					.indexOf("tab1RECORDCOUNT="));
			rc = rc.substring(rc.indexOf("=") + 1);
			rc = rc.substring(0, rc.indexOf("&"));
			System.out.println(rc);
			int recordCount = Integer.parseInt(rc);

			String data = "ACTIONID=7&AJAX=AJAX-TRUE&CATALOGID=1798&txtKsrq="
					+ from + "&txtZzrq=" + now + "&TABKEY=tab1&tab1PAGECOUNT="
					+ total + "&tab1RECORDCOUNT=" + recordCount
					+ "&REPORT_ACTION=navigate&tab1PAGENUM=";

			for (int i = total; i > 0; i--) {
				StringEntity entity = new StringEntity(data + i);
				httpPost = new HttpPost(
						"http://www.szse.cn/szseWeb/FrontController.szse?randnum="
								+ Math.random());
				httpPost.setHeader("Accept", "* /*");
				httpPost.setHeader("Accept-Encoding", "gzip, deflate");
				httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
				// httpPost.setHeader("Cache-Control", "max-age=0");
				httpPost.setHeader("Connection", "keep-alive");
				httpPost.setHeader("Content-Type",
						"application/x-www-form-urlencoded; charset=UTF-8");
				httpPost.setHeader("Origin", "http://www.szse.cn");
				httpPost.setHeader("Referer",
						"http://www.szse.cn/main/disclosure/news/tfpts/");
				httpPost.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

				httpPost.setEntity(entity);
				responseBody = httpclient.execute(httpPost, responseHandler);
				responseBody = responseBody.substring(responseBody
						.indexOf("REPORTID_tab1"));
				responseBody = responseBody.substring(responseBody
						.indexOf("证券代码"));
				responseBody = responseBody.substring(responseBody
						.indexOf("<tr"));
				responseBody = responseBody.substring(0,
						responseBody.indexOf("</table>"));
				// System.out.println(responseBody);

				Pattern pattern = Pattern.compile("<tr.*?</tr>");
				Matcher matcher = pattern.matcher(responseBody);
				pattern = Pattern.compile("<td.*?</td>");
				while (matcher.find()) {
					String line = matcher.group();
					// line = line.substring(4, line.length() - 5);
					// System.out.println(line);
					Matcher m = pattern.matcher(line);
					int x = 0;
					String code = null;
					String action = null;
					String reason = null;
					while (m.find()) {
						// System.out.println(m.group());
						if (x == 0) {
							code = m.group();
							code = code.substring(code.indexOf(">") + 1);
							code = code.substring(0, code.indexOf("<")).trim();

						}
						if (x == 4) {
							action = m.group();
							action = action.substring(action.indexOf(">") + 1);
							action = action.substring(0, action.indexOf("<"))
									.trim();
						}
						if (x == 5) {
							reason = m.group();
							reason = reason.substring(reason.indexOf(">") + 1);
							reason = reason.substring(0, reason.indexOf("<"))
									.trim();
						}
						x++;
					}
					stack.push(code + ":" + action + ":" + reason);
					// System.out.print(code);
					// System.out.print(action);
					// System.out.println(reason);
				}
				while (stack.size() > 0) {
					queue.add(stack.pop());
				}
				System.out.println(queue.size());
				// System.out.println(stack.size());
				// break;
			}

			for (String line : queue) {
				String[] ls = line.split(":");
				String code = ls[0];
				String action = ls[1];
				String reason = ls[2];

				if (action.contains("取消停牌")) {
					if (tmpList.contains(code)) {
						tmpList.remove(code);
					}
				}

				if (action.equals("停牌") || action.contains("1天")) {
					if (reason.contains("重大事项")) {
						if (!tmpList.contains(code)) {
							tmpList.add(code);
						}
					}
				}
			}

			System.out.println(tmpList.size());

			for (String line : tmpList) {
				if (line.startsWith("0") || line.startsWith("3")) {
					LIST.add(line + ".SZ");
				}
			}

			System.out.println(LIST.size());
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
	**/

	private static void printInvestors(String code) {
		String content = null;
		String codex = code.substring(0, code.length() - 3);
		try {
			content = get("http://www.windin.com/home/stock/stock-mh/" + code
					+ ".shtml");
			if (content == null) {
				System.err.println("ERROR CONTENT");
				return;
			}
			// System.out.println(content);
			try {
				content = content.substring(content
						.indexOf("<tr class=\"title3\">"));
			} catch (Exception e) {
				System.err.println("ERR:" + code);
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
					// System.out.println("NAME:"+name);
					if (name.indexOf("/home/fund/html/") != -1) {
						name = name.substring(
								name.indexOf("/home/fund/html/") + 16,
								(name.indexOf(".shtml")));

						name = name.substring(0, name.length() - 3);
					} else {
						name = name.replaceAll("<.*?>", "");
						name = name.replaceAll("</.*?>", "");
						name = name.replaceAll("&nbsp;", "");
						name = name.replaceAll(" ", "");
						name = "[*]" + name;
					}
					// System.out.println("\t" + name + "->" + type);
					System.out.println("\t" + name);
					if (!name.contains("*")) { // 打印基金的持仓
						content = get(
								"http://fund.eastmoney.com/f10/FundArchivesDatas.aspx?type=jjcc&code="
										+ name + "&year=2015&month=3&rt="
										+ Math.random(), "GB2312");
						if (content.indexOf("<table") != -1) {// 持仓非空
						// System.out.println("TT:"+code);
							content = content.substring(content
									.indexOf("<table"));
							content = content.substring(0,
									content.indexOf("</table>"));
							if (content.contains(codex)) { // 当前基金是否持有大量的停牌股票
								content = content.substring(content
										.indexOf("<tbody"));
								content = content.substring(content
										.indexOf("<tr>"));
								content = content.substring(0,
										content.indexOf("</tbody>"));

								pattern = Pattern.compile("<tr.*?</tr>");
								matcher = pattern.matcher(content);
								pattern = Pattern.compile("<td.*?</td>");
								while (matcher.find()) {
									boolean cflag = false;
									line = matcher.group();
									// line =
									// line.substring(line.indexOf("<td"),
									// line.length() - 5);
									// System.out.println(line);

									m = pattern.matcher(line);
									int x = 0;
									String ccode = null;
									String cname = null;
									String cpercent = null;
									while (m.find()) {
										// System.out.println(m.group());
										if (x == 1) {
											ccode = m.group();
											ccode = ccode.substring(ccode
													.indexOf(">") + 1);
											ccode = ccode.substring(ccode
													.indexOf(">") + 1);
											ccode = ccode.substring(0,
													ccode.indexOf("<"));
											// if (name.contains("机构名称")) {
											// break;
											// }
											if (ccode.equals(codex)) {
												cflag = true;
											}
										}
										if (x == 2) {
											cname = m.group();
											cname = cname.substring(cname
													.indexOf(">") + 1);
											cname = cname.substring(cname
													.indexOf(">") + 1);
											cname = cname.substring(0,
													cname.indexOf("<"));
										}
										// cpercent = m.group();
										// if (x == 6) {
										if (m.group().contains("%")) {
											cpercent = m.group();
											cpercent = cpercent
													.substring(cpercent
															.indexOf(">") + 1);
											cpercent = cpercent.substring(0,
													cpercent.indexOf("<"));
										}
										x++;
									}
									if (cflag) {
										System.out.println("\t\t" + ccode
												+ "\t" + cname + "->"
												+ cpercent);
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

	/**
	private static void printCSIPE(ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(
					"http://hq.sinajs.cn/?_=1434525165656&list=rt_hkCSI300");
			httpGet.setHeader("Accept", "* /*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody
					.substring(responseBody.indexOf("\"") + 1);
			responseBody = responseBody.substring(0,
					responseBody.lastIndexOf("\""));
			String[] rs = responseBody.split(",");
			// System.out.println("----------------------------------------");
			System.out.println(CSIRATIO * Double.parseDouble(rs[6]));

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

	**/
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
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody.substring(
					responseBody.indexOf("HSI Volatility Index (VHSI)"),
					responseBody.indexOf(" x</td>"));
			responseBody = responseBody
					.substring(responseBody.lastIndexOf(">") + 1);
			// System.out.println("----------------------------------------");
			System.out.print(responseBody + " --> ");

			// http://qt.gtimg.cn/r=2015072023241437405887&q=r_hkHSI
			httpGet = new HttpGet("http://qt.gtimg.cn/r=" + Math.random()
					+ "&q=r_hkHSI");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody
					.substring(responseBody.indexOf("\"") + 1);
			responseBody = responseBody
					.substring(0, responseBody.indexOf("\""));

			String[] pbs = responseBody.split("~");
			String percent = df.format((Double.parseDouble(pbs[3])
					/ Double.parseDouble(pbs[4]) - 1) * 100);
			if (percent.startsWith(".")) {
				percent = "0" + percent;
			}
			if (percent.startsWith("-.")) {
				percent = "-0" + percent.substring(1);
			}

			System.out.println(pbs[3] + " " + percent + "%");

			// System.out.println("----------------------------------------");
			// System.out.println(responseBody);
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

	private static void printSPXPE(ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet("http://www.multpl.com/");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody.substring(responseBody
					.indexOf("Current S&amp;P 500 PE Ratio is ") + 32);
			responseBody = responseBody.substring(0, responseBody.indexOf(","));
			// System.out.println("----------------------------------------");
			System.out.print(responseBody + " --> ");

			// http://qt.gtimg.cn/r=0.1559772426262498q=usINX
			httpGet = new HttpGet("http://qt.gtimg.cn/r=" + Math.random()
					+ "&q=usINX");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody
					.substring(responseBody.indexOf("\"") + 1);
			responseBody = responseBody
					.substring(0, responseBody.indexOf("\""));

			String[] pbs = responseBody.split("~");
			String percent = df.format((Double.parseDouble(pbs[3])
					/ Double.parseDouble(pbs[4]) - 1) * 100);
			if (percent.startsWith(".")) {
				percent = "0" + percent;
			}
			if (percent.startsWith("-.")) {
				percent = "-0" + percent.substring(1);
			}

			System.out.println(pbs[3] + " " + percent + "%");
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

	private static void printCPI(ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(
					"http://data.eastmoney.com/cjsj/consumerpriceindex.aspx?p=1");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody.substring(responseBody
					.indexOf("secondTr"));
			responseBody = responseBody
					.substring(responseBody.indexOf("<span"));
			responseBody = responseBody
					.substring(responseBody.indexOf(">") + 1);
			responseBody = responseBody.substring(0,
					responseBody.indexOf("</span>")).trim();
			// System.out.println("----------------------------------------");
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

	private static void printCSIPEX(ResponseHandler<String> responseHandler,
			ResponseHandler<InputStream> responseHandlerX) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		Workbook workbook = null;
		try {
			HttpGet httpGet = new HttpGet(
					"http://www.csindex.com.cn/sseportal/ps/zhs/hqjt/csi/Csi300Perf.xls");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			InputStream responseBody = httpclient.execute(httpGet,
					responseHandlerX);

			workbook = WorkbookFactory.create(responseBody);

			Sheet sheet = workbook.getSheetAt(0);
			Row row = sheet.getRow(1);
			// Cell cell = row.getCell(14);
			System.out.print(row.getCell(14).getStringCellValue() + " / "
					+ row.getCell(15).getStringCellValue() + " --> ");

			httpGet = new HttpGet("http://hq.sinajs.cn/list=sh000300");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String priceBody = httpclient.execute(httpGet, responseHandler);
			priceBody = priceBody.substring(priceBody.indexOf("\"") + 1);
			priceBody = priceBody.substring(0, priceBody.indexOf("\""));
			String[] pbs = priceBody.split(",");
			String percent = df.format((Double.parseDouble(pbs[3])
					/ Double.parseDouble(pbs[2]) - 1) * 100);
			if (percent.startsWith(".")) {
				percent = "0" + percent;
			}
			if (percent.startsWith("-.")) {
				percent = "-0" + percent.substring(1);
			}

			System.out.println(pbs[3] + " " + percent + "%");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (httpclient != null) {
					httpclient.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void printAliPayRate(ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(
					"http://www.howbuy.com/fund/000198/index.htm?source=aladdin&HTAG=0.0040010007900000");
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody = httpclient.execute(httpGet, responseHandler);
			responseBody = responseBody.substring(responseBody
					.indexOf("<span class=\"cRed\">"));
			responseBody = responseBody
					.substring(responseBody.indexOf(">") + 1);
			// responseBody =
			// responseBody.substring(responseBody.indexOf(">")+1);
			responseBody = responseBody.substring(0,
					responseBody.indexOf("</span>")).trim();
			// System.out.println("----------------------------------------");
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

	// 封闭式基金 closed-end fund
	// http://fund.eastmoney.com/f10/jjgg_184721_2.html
	// http://fund.eastmoney.com/f10/F10DataApi.aspx?type=jjgg&code=184721&page=1&per=20&class=2&rt=0.7149084734264761
	private static void printClosedEndFund(
			ResponseHandler<String> responseHandler) {
		String[] codes = new String[] { "150001", "161222", "169101", "184721",
				"184722", "184728", "500038", "500056", "500058", "505888" };
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = null;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			for (int i = 0; i < codes.length; i++) {
				httpGet = new HttpGet(
						"http://fund.eastmoney.com/f10/F10DataApi.aspx?type=jjgg&code="
								+ codes[i] + "&page=1&per=20&class=2&rt="
								+ Math.random());
				httpGet.setHeader("Accept", "*/*");
				httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
				httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
				httpGet.setHeader("Cache-Control", "max-age=0");
				httpGet.setHeader("Connection", "keep-alive");
				// httpGet.setHeader("Referer",
				// "http://www.sse.com.cn/disclosure/dealinstruc/");
				httpGet.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

//				System.out.println("http://fund.eastmoney.com/f10/F10DataApi.aspx?type=jjgg&code="
//								+ codes[i] + "&page=1&per=20&class=2&rt="
//								+ Math.random());
				String responseBody = httpclient.execute(httpGet,
						responseHandler);
				responseBody = responseBody.substring(responseBody
						.indexOf("</tr>"));
				responseBody = responseBody.substring(responseBody
						.indexOf("<tr>"));
				responseBody = responseBody.substring(0,
						responseBody.indexOf("</tbody>")).trim();
				// System.out.println("----------------------------------------");
				// System.out.println(responseBody);

				Pattern pattern = Pattern.compile("<tr.*?</tr>");
				Matcher matcher = pattern.matcher(responseBody);
				pattern = Pattern.compile("<td.*?</td>");
				String sdate = null;
				if (matcher.find()) {
					String line = matcher.group();
					// line = line.substring(4, line.length() - 5);
//					if(i==1){
//						System.out.println(line);
//					}
					Matcher m = pattern.matcher(line);
					int idx = 0;
					while (m.find()) {
						line = m.group();
						if (idx == 2) {
							sdate = line.replaceAll("<.*?>", "");
						}
						idx++;
					}
				}
				if (sdate != null) {
					System.out.print(codes[i] + "-->" + sdate + "\t");
					System.out.println(maturity(sdate, 7));
				}
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

	private static void printClosedEndFundMaturity(
			ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet("http://www.jisilu.cn/data/cf/cf_list/?___t="
					+ System.currentTimeMillis());
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			String responseBody = httpclient.execute(httpGet, responseHandler);
			JSONObject json = new JSONObject(responseBody);
			JSONArray ja = json.getJSONArray("rows");
			for(int i=0; i<ja.length();i++){
				json = ja.getJSONObject(i);
				JSONObject jsx = json.getJSONObject("cell");
				System.out.print(json.getString("id") + "-->" + jsx.getString("maturity_dt"));
				System.out.print("\t" + jsx.getString("discount_rt") + " on " + jsx.getString("annualize_dscnt_rt"));
				System.out.print("\t\t");				
				
				System.out.println(maturity(jsx.getString("maturity_dt"), 30));
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
	
	private static String maturity(String date, int interval){
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar aCalendar = Calendar.getInstance();
			// System.out.println(aCalendar.getTime());
			int year1 = aCalendar.get(Calendar.YEAR);
			int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
			// System.out.println(day1);
			aCalendar.setTime(sdf.parse(date));
			// System.out.println(aCalendar.getTime());
			int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
			int year2 = aCalendar.get(Calendar.YEAR);
			// System.out.println(day2);
	
			// System.out.println(Math.abs(day2-day1));
			if (year1 == year2 && Math.abs(day2 - day1) < interval) {
				return "Notification!!!";
			} else {
				return "NotFound";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "Exception!";
	}
	
	private static void printRecaculateOfClassificationFund(
			ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = null;
		try {//
			httpGet = new HttpGet("http://www.jisilu.cn/data/sfnew/funda_list/?___t="
					+ System.currentTimeMillis());
			httpGet.setHeader("Accept", "*/*");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Cache-Control", "max-age=0");
			httpGet.setHeader("Connection", "keep-alive");
			// httpGet.setHeader("Referer",
			// "http://www.sse.com.cn/disclosure/dealinstruc/");
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			Map<String, String> mapl = new HashMap<String, String>(); 
			Map<String, String> mapx = new HashMap<String, String>();
			
			String responseBody = httpclient.execute(httpGet, responseHandler);
			JSONObject json = new JSONObject(responseBody);
			JSONArray ja = json.getJSONArray("rows");
			for(int i=0; i<ja.length();i++){
				json = ja.getJSONObject(i);
				JSONObject jsx = json.getJSONObject("cell"); //lower_recalc_profit_rt
				String lr = jsx.getString("funda_lower_recalc_rt");
				float ilr = Float.parseFloat(lr.substring(0, lr.length()-1));
				if(ilr<10){
					if(ilr<4){
						mapl.put(json.getString("id"), jsx.getString("funda_lower_recalc_rt")+"\t\t"+ jsx.getString("funda_current_price") + "\t" + jsx.getString("lower_recalc_profit_rt")+"\tNotification!!!");
					}else{
						mapl.put(json.getString("id"), jsx.getString("funda_lower_recalc_rt")+"\t\t"+ jsx.getString("funda_current_price") + "\t" + jsx.getString("lower_recalc_profit_rt")+"\tNotFound");
					}
				}
				
				float fv = Float.parseFloat(jsx.getString("funda_value"));
				String snrd = jsx.getString("next_recalc_dt");
				snrd = snrd.replaceAll("<.*?>", "").substring(0, 10);
				if(maturity(snrd, 30).contains("!")){
					if(fv>1.05f){
						mapx.put(json.getString("id"), snrd+"\t\t"+jsx.getString("funda_value")+"\tNotification!!!");
					}else{
						mapx.put(json.getString("id"), snrd+"\t\t"+jsx.getString("funda_value")+"\tNotFound");
					}
				}
//				System.out.print(json.getString("id") + "-->" + jsx.getString("funda_lower_recalc_rt"));
//				System.out.print("\t" + jsx.getString("lower_recalc_profit_rt"));
			}

			List<Map.Entry<String, String>> set = new ArrayList<Map.Entry<String, String>>(mapl.entrySet()); 
			Collections.sort(set, new Comparator<Map.Entry<String, String>>() {  
			    public int compare(Map.Entry<String, String> fst,  
			            Map.Entry<String, String> sec) {  
			    	String sfst = fst.getValue().split("\t\t")[0];
			    	String ssec = sec.getValue().split("\t\t")[0];
			    	float ifst = Float.parseFloat(sfst.substring(0, sfst.length()-1));
			    	float isec = Float.parseFloat(ssec.substring(0, ssec.length()-1));		    	
			    	if(ifst>isec) return 1;
			    	if(ifst<isec) return -1;
			    	return 0;
			    }  
			});

			System.out.println("分级基金下折");
			System.out.println("----------------");
			for (int i = 0; i < set.size(); i++) {  
			    Entry<String, String> ent = set.get(i);  
			    System.out.println(ent.getKey()+"-->"+ent.getValue());			      
			}  
			System.out.println();
			
			set = new ArrayList<Map.Entry<String, String>>(mapx.entrySet()); 
			Collections.sort(set, new Comparator<Map.Entry<String, String>>() {  
			    public int compare(Map.Entry<String, String> fst,  
			            Map.Entry<String, String> sec) {  
			    	String sfst = fst.getValue().split("\t\t")[0];
			    	String ssec = sec.getValue().split("\t\t")[0];
//			    	float ifst = Float.parseFloat(sfst.substring(0, sfst.length()-1));
//			    	float isec = Float.parseFloat(ssec.substring(0, ssec.length()-1));		    	
//			    	if(ifst>isec) return 1;
//			    	if(ifst<isec) return -1;
			    	return sfst.compareTo(ssec);
			    }  
			});
			System.out.println("分级基金定折");
			System.out.println("----------------");
			for (int i = 0; i < set.size(); i++) {  
			    Entry<String, String> ent = set.get(i);  
			    System.out.println(ent.getKey()+"-->"+ent.getValue());			      
			}  
			System.out.println();
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

	private static void printMergeOfClassificationFund(
			ResponseHandler<String> responseHandler) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = null;
		HttpPost httpPost = null;
		try {
			//1437627768,1437631995,1437636767,1437639566
//			String t1 = "1437627768";
//			String t2 = "1437631995";//,1437636767,1437639566"
//			String t3 = "1437636767";
//			String t4 = "1437639566";
			Properties prop = new Properties();
			prop.load(new FileInputStream("cookie"));
			String t = prop.getProperty("t");
			System.out.println(t);
			
			httpGet = new HttpGet("http://www.jisilu.cn/");
			httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* /*;q=0.8");
			httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Connection", "keep-alive");
			httpGet.setHeader("Cookie", "kbz_r_uname=hoyzhang; kbz__user_login=1ubd08_P1ebax9aX39Hv29vZz9eCr6blyuzf7tHoxdHVjNSV1dzYmrKdrcipxtmxlqnH1dysyqzSqZWrxKiqmaPClbSi3uLQ1b-hk6mvkqiCr6bKqtfJoq_l29zkzdGQqaeliaHD4NDa0Orrgb61lK-jmrSMzrHNl6ehgbHR5OXawN7OwsvqkKirmJ6UqpmdtMHAxK6igd_hzNWBu97Y1OiVl6Xe0-Llxp-UrKell6udqZekkqSpgcPC2trn0qihqpmklKk.; kbz_newcookie=1; Hm_lvt_164fe01b1433a19b507595a43bf58262="+t);
			httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

//			httpclient.execute(httpGet, responseHandler);
//			Thread.sleep(5000);
			
			t = t.substring(t.indexOf(",")+1);
			t = t + "," + System.currentTimeMillis()/1000;
			System.out.println(t);			
			
			httpGet = new HttpGet("http://www.jisilu.cn/home/ajax/notifications/");
			httpGet.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
			httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("Referer", "http://www.jisilu.cn/");
			httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
			httpGet.setHeader("Connection", "keep-alive");		httpGet.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");

			httpGet.setHeader("Cookie", "kbz_r_uname=hoyzhang; kbz__user_login=1ubd08_P1ebax9aX39Hv29vZz9eCr6blyuzf7tHoxdHVjNSV1dzYmrKdrcipxtmxlqnH1dysyqzSqZWrxKiqmaPClbSi3uLQ1b-hk6mvkqiCr6bKqtfJoq_l29zkzdGQqaeliaHD4NDa0Orrgb61lK-jmrSMzrHNl6ehgbHR5OXawN7OwsvqkKirmJ6UqpmdtMHAxK6igd_hzNWBu97Y1OiVl6Xe0-Llxp-UrKell6udqZekkqSpgcPC2trn0qihqpmklKk.; kbz_newcookie=1; Hm_lvt_164fe01b1433a19b507595a43bf58262="+t+"; Hm_lpvt_164fe01b1433a19b507595a43bf58262="+System.currentTimeMillis()/1000);
	
//			httpclient.execute(httpGet, responseHandler);
			
			prop.setProperty("t", t);
			prop.store(new FileOutputStream("cookie"), "Copyright (c) xland.net 2015");
			/**
			 * 
GET http://www.jisilu.cn/home/ajax/notifications/ HTTP/1.1
Host: www.jisilu.cn
Connection: keep-alive
Accept: application/json, text/javascript, * /*; q=0.01
X-Requested-With: XMLHttpRequest
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36
Referer: http://www.jisilu.cn/
Accept-Encoding: gzip,deflate,sdch
Accept-Language: zh-CN,zh;q=0.8
Cookie: kbz_r_uname=hoyzhang; kbz__user_login=1ubd08_P1ebax9aX39Hv29vZz9eCr6blyuzf7tHoxdHVjNSV1dzYmrKdrcipxtmxlqnH1dysyqzSqZWrxKiqmaPClbSi3uLQ1b-hk6mvkqiCr6bKqtfJoq_l29zkzdGQqaeliaHD4NDa0Orrgb61lK-jmrSMzrHNl6ehgbHR5OXawN7OwsvqkKirmJ6UqpmdtMHAxK6igd_hzNWBu97Y1OiVl6Xe0-Llxp-UrKell6udqZekkqSpgcPC2trn0qihqpmklKk.; kbz_newcookie=1; kbz__Session=c1sk1fbjfcrmlneajne1h2mf17; Hm_lvt_164fe01b1433a19b507595a43bf58262=1437644992,1437645160,1437645239,1437645284; Hm_lpvt_164fe01b1433a19b507595a43bf58262=1437645284


			 */
//			kzsession = "c1sk1fbjfcrmlneajne1h2mf17";
//			String t = "1437636765,1437639565,1437640455,1437641171";
			
//			long t5 = System.currentTimeMillis()/1000;
			httpPost = new HttpPost("http://www.jisilu.cn/data/sfnew/arbitrage_vip_list/?___t="
					+ System.currentTimeMillis());
			httpPost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
			httpPost.setHeader("Accept-Encoding", "gzip,deflate");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			//httpPost.setHeader("Cache-Control", "max-age=0");
			httpPost.setHeader("Connection", "keep-alive");
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.setHeader("Origin", "http://www.jisilu.cn");
			httpPost.setHeader("Referer", "http://www.jisilu.cn/data/sfnew/");
			httpPost.setHeader("X-Requested-With", "XMLHttpRequest");				
			httpPost.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");
//			httpPost.setHeader("Cookie", "kbz_r_uname=hoyzhang; kbz__user_login=1ubd08_P1ebax9aX39Hv29vZz9eCr6blyuzf7tHoxdHVjNSV1dzYmrKdrcipxtmxlqnH1dysyqzSqZWrxKiqmaPClbSi3uLQ1b-hk6mvkqiCr6bKqtfJoq_l29zkzdGQqaeliaHD4NDa0Orrgb61lK-jmrSMzrHNl6ehgbHR5OXawN7OwsvqkKirmJ6UqpmdtMHAxK6igd_hzNWBu97Y1OiVl6Xe0-Llxp-UrKell6udqZekkqSpgcPC2trn0qihqpmklKk.; kbz_newcookie=1; kbz__Session=2gv4cu4f4gv89eunlo42cvi8b5; Hm_lvt_164fe01b1433a19b507595a43bf58262=1437447566,1437531090,1437619814,1437620170; Hm_lpvt_164fe01b1433a19b507595a43bf58262=1437620172");
//			httpPost.setHeader("Cookie", "kbz_r_uname=hoyzhang; kbz__user_login=1ubd08_P1ebax9aX39Hv29vZz9eCr6blyuzf7tHoxdHVjNSV1dzYmrKdrcipxtmxlqnH1dysyqzSqZWrxKiqmaPClbSi3uLQ1b-hk6mvkqiCr6bKqtfJoq_l29zkzdGQqaeliaHD4NDa0Orrgb61lK-jmrSMzrHNl6ehgbHR5OXawN7OwsvqkKirmJ6UqpmdtMHAxK6igd_hzNWBu97Y1OiVl6Xe0-Llxp-UrKell6udqZekkqSpgcPC2trn0qihqpmklKk.; kbz_newcookie=1; kbz__Session=ptmuorudhkjt5pp7n684lu81k2; Hm_lvt_164fe01b1433a19b507595a43bf58262=1437531090,1437619814,1437620170,1437627768; Hm_lpvt_164fe01b1433a19b507595a43bf58262=1437627772");
			httpPost.setHeader("Cookie", "kbz_r_uname=hoyzhang; kbz__user_login=1ubd08_P1ebax9aX39Hv29vZz9eCr6blyuzf7tHoxdHVjNSV1dzYmrKdrcipxtmxlqnH1dysyqzSqZWrxKiqmaPClbSi3uLQ1b-hk6mvkqiCr6bKqtfJoq_l29zkzdGQqaeliaHD4NDa0Orrgb61lK-jmrSMzrHNl6ehgbHR5OXawN7OwsvqkKirmJ6UqpmdtMHAxK6igd_hzNWBu97Y1OiVl6Xe0-Llxp-UrKell6udqZekkqSpgcPC2trn0qihqpmklKk.; kbz_newcookie=1; kbz__Session=qepclh5uprfri7u8leppjah2f6;");// kbz__Session="+kzsession+";"); //kbz__Session=8juveau4ltr3be9dvjiua0i9i6; //Hm_lvt_164fe01b1433a19b507595a43bf58262="+t+"; Hm_lpvt_164fe01b1433a19b507595a43bf58262="+System.currentTimeMillis()/1000
//			httpPost.setHeader("Cookie", "kbz_r_uname=hoyzhang; kbz__user_login=1ubd08_P1ebax9aX39Hv29vZz9eCr6blyuzf7tHoxdHVjNSV1dzYmrKdrcipxtmxlqnH1dysyqzSqZWrxKiqmaPClbSi3uLQ1b-hk6mvkqiCr6bKqtfJoq_l29zkzdGQqaeliaHD4NDa0Orrgb61lK-jmrSMzrHNl6ehgbHR5OXawN7OwsvqkKirmJ6UqpmdtMHAxK6igd_hzNWBu97Y1OiVl6Xe0-Llxp-UrKell6udqZekkqSpgcPC2trn0qihqpmklKk.; kbz_newcookie=1; kbz__Session=8juveau4ltr3be9dvjiua0i9i6; Hm_lvt_164fe01b1433a19b507595a43bf58262=1437619814,1437620170,1437627768,1437631995; Hm_lpvt_164fe01b1433a19b507595a43bf58262=1437627772");
			
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	        nvps.add(new BasicNameValuePair("is_search", "1"));
	        nvps.add(new BasicNameValuePair("avolume", ""));
	        nvps.add(new BasicNameValuePair("bvolume", ""));
	        nvps.add(new BasicNameValuePair("market[]", "sh"));
	        nvps.add(new BasicNameValuePair("market[]", "sz"));
	        nvps.add(new BasicNameValuePair("ptype", "sell"));
	        nvps.add(new BasicNameValuePair("rp", "50"));
	        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			
			Map<String, String> map = new HashMap<String, String>();  
			
			String responseBody = httpclient.execute(httpPost, responseHandler);
//			System.out.println(">>>"+responseBody);;
			JSONObject json = new JSONObject(responseBody);
			JSONArray ja = json.getJSONArray("rows");
			for(int i=0; i<ja.length();i++){
				json = ja.getJSONObject(i);
				JSONObject jsx = json.getJSONObject("cell"); //lower_recalc_profit_rt
				String lr = jsx.getString("est_dis_rt");
				float ilr = Float.parseFloat(lr.substring(0, lr.length()-1));
				if(ilr<-1.2){
//					System.out.println("ilr:"+lr.substring(0, lr.length()-1));
					String sira = jsx.getString("increase_rtA");
					String sirb = jsx.getString("increase_rtB");
					float fira = Float.parseFloat(sira.substring(0, sira.length()-1));
			    	float firb = Float.parseFloat(sirb.substring(0, sirb.length()-1));
//			    	System.out.println(fira+"/"+firb);
					if(ilr<-1.5&&fira<9.9f&&firb<9.9f){
						map.put(json.getString("id"), jsx.getString("est_dis_rt")+"\t\t"+jsx.getString("fundA_id")+ "=" + sira + "["+jsx.getString("sell1A")+"]\t" + jsx.getString("fundB_id") + "=" + sirb + "["+jsx.getString("sell1B")+"]\t" + jsx.getString("idx_incr_rt") + "\tNotification!!!");
					}else{
						map.put(json.getString("id"), jsx.getString("est_dis_rt")+"\t\t"+jsx.getString("fundA_id")+ "=" + sira + "["+jsx.getString("sell1A")+"]\t" + jsx.getString("fundB_id") + "=" + sirb + "["+jsx.getString("sell1B")+"]\t" + jsx.getString("idx_incr_rt") + "\tNotFound");
					}
				}
//				System.out.print(json.getString("id") + "-->" + jsx.getString("funda_lower_recalc_rt"));
//				System.out.print("\t" + jsx.getString("lower_recalc_profit_rt"));
			}

			List<Map.Entry<String, String>> set = new ArrayList<Map.Entry<String, String>>(map.entrySet()); 
			Collections.sort(set, new Comparator<Map.Entry<String, String>>() {  
			    public int compare(Map.Entry<String, String> fst,  
			            Map.Entry<String, String> sec) {  
			    	String sfst = fst.getValue().split("\t\t")[0];
			    	String ssec = sec.getValue().split("\t\t")[0];
			    	float ifst = Float.parseFloat(sfst.substring(0, sfst.length()-1));
			    	float isec = Float.parseFloat(ssec.substring(0, ssec.length()-1));
			    	if(ifst>isec) return 1;
			    	if(ifst<isec) return -1;
			    	return 0;
			    }  
			});
			
			for (int i = 0; i < set.size(); i++) {  
			    Entry<String, String> ent = set.get(i);  
			    System.out.println(ent.getKey()+"-->"+ent.getValue());			      
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
	
	// 建立股债平衡，多市场指数基金的投资组合，再平衡（动态再平衡策略）之后，尝试根据资金流的策略。
	public static void main(String[] args) {
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			@Override
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					/**
					for(Header header:response.getAllHeaders()){
						if(header.getValue().startsWith("kbz__Session")){
							kzsession = header.getValue();
							kzsession = kzsession.substring(kzsession.indexOf("=")+1, kzsession.indexOf(";"));
							System.out.println(kzsession);
//							System.out.println((header.getName())+"="+header.getValue());	
						}
					}**/
//					System.out.println(entity.getContentEncoding());
					//new GzipDecompressingEntity(
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
				}
			}
		};

		ResponseHandler<InputStream> responseHandlerX = new ResponseHandler<InputStream>() {
			@Override
			public InputStream handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ByteArrayInputStream bais = null;
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					// System.out.println("XXXXXXXXXXXXXXXXX:"+entity);
					if (entity != null) {
						InputStream is = entity.getContent();
						byte[] tmp = new byte[1024];
						int size = 0;
						while ((size = is.read(tmp)) != -1) {
							baos.write(tmp, 0, size);
						}
						bais = new ByteArrayInputStream(baos.toByteArray());
					}
					return bais; // EntityUtils.toString(entity)
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
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
		System.out.println(title + "市场总览");
		System.out.println("----------------");
		System.out.print("CPI: ");
		printCPI(responseHandler);
		System.out.print("AliPay Yields: ");
		printAliPayRate(responseHandler);
		System.out.print("CSI PE Ratio: ");
		printCSIPEX(responseHandler, responseHandlerX);
		System.out.print("HSI PE Ratio: ");
		printHSIPE(responseHandler);
		System.out.print("SPX PE Ratio: ");
		printSPXPE(responseHandler);

		// System.out.println("------------");
		// System.out.println("沪市停牌");
		// printHalting("http://stock.eastmoney.com/news/chstpyl.html", title);
		// System.out.println("深市停牌");
		// printHalting("http://stock.eastmoney.com/news/csstpyl.html", title);

		// http://query.sse.com.cn/infodisplay/querySpecialTipsInfoByPage.do?jsonCallBack=jsonpCallback71393&isPagination=true&searchDate=&bgFlag=1&searchDo=1&pageHelp.pageSize=100&_=1433417470230
		// http://www.szse.cn/szseWeb/FrontController.szse?randnum=0.9035767468158156
		// printHaltingSSE(responseHandler);
		// printHaltingSZSE(responseHandler);

		for (String code : LIST) {
			System.out.println(code);
			printInvestors(code);
			// break;
		}

		System.out.println();
		System.out.println("封基到期");
		System.out.println("----------------");
		printClosedEndFundMaturity(responseHandler);
		System.out.println();
		System.out.println("封基分红送配");
		System.out.println("----------------");
		printClosedEndFund(responseHandler);
		System.out.println();
		//分级基金下折和定折
		printRecaculateOfClassificationFund(responseHandler);
//		System.out.println();
		System.out.println("分级基金合并折价");
		System.out.println("----------------");
		printMergeOfClassificationFund(responseHandler);
	}
}
