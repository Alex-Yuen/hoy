package ws.hoyland.investment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradingHalt {

	private static List<String> LIST = new ArrayList<String>();
	
	public static String get(String url) {
		return get(url, null);
	}
	
	public static String get(String url, String charset) {
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
			if(charset!=null){
				isr = new InputStreamReader(in, charset);
			}else{
				isr = new InputStreamReader(in, "utf-8");
			}
			br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
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

	public static void printHalting(String url, String date){
		String content = null;
		String link = null;
		try {
			content = get(url, "GB2312");
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
					if(i==0){
						code = m.group();
					}
					if(i==4){
						action = m.group();
					}
					if(i==5){
						reason = m.group();
					}
					i++;
				}
				
				if(action.indexOf("取消停牌")==-1&&action.indexOf("停牌") != -1&&reason.indexOf("重大事项") != -1){
					if(url.endsWith("chstpyl.html")){
						LIST.add(code.substring(4, code.length()-5)+".SH");
					}else{
						LIST.add(code.substring(4, code.length()-5)+".SZ");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printInvestors(String code){
		String content = null;
		try {
			content = get("http://www.windin.com/home/stock/stock-mh/"+code+".shtml");
			//System.out.println(content);
			
			content = content.substring(content.indexOf("<tr class=\"title3\">"));
			content = content.substring(0, content.indexOf("</table>"));
//			System.out.println(content);
			
			Pattern pattern = Pattern.compile("<tr.*?</tr>");
			Matcher matcher = pattern.matcher(content);
			pattern = Pattern.compile("<td.*?</td>");
			while (matcher.find()) {
				String line = matcher.group();
				line = line.substring(line.indexOf("<td"), line.length() - 5);
//				System.out.println(line);
				
				Matcher m = pattern.matcher(line);
				int i = 0;
				String name = null;
				String type = null;
				while (m.find()) {
					// System.out.println(m.group());
					if(i==0){
						name = m.group();
						if(name.contains("机构名称")){
							break;
						}
					}
					if(i==1){
						type = m.group();
						type = type.substring(11, type.length()-5);
					}
					i++;
				}
				if(type!=null&&type.equals("基金")){
					name = name.substring(name.indexOf("/home/fund/html/")+16, (name.indexOf(".shtml")));
					System.out.println("\t"+name+"->"+type);
				}
				/**
				if(action.indexOf("取消停牌")==-1&&action.indexOf("停牌") != -1&&reason.indexOf("重大事项") != -1){
					if(url.endsWith("chstpyl.html")){
						LIST.add(code.substring(4, code.length()-5)+".SH");
					}else{
						LIST.add(code.substring(4, code.length()-5)+".SZ");
					}
				}
				**/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		// http://wap.eastmoney.com/NewsList.aspx?m=145&c=401 //沪市停牌
		// http://wap.eastmoney.com/NewsList.aspx?m=145&c=402 //深市停牌

		// http://stock.eastmoney.com/news/chstpyl.html
		// http://stock.eastmoney.com/news/csstpyl.html

		// http://www.windin.com/home/stock/html/600389.SH.shtml?q=600389&t=1
		//http://www.windin.com/home/stock/stock-mh/002574.SZ.shtml
		//http://fund.eastmoney.com/f10/FundArchivesDatas.aspx?type=jjcc&code=150001&year=2015&month=3&rt=0.9432023034896702

		SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
		Date date = new Date();
		String title = sdf.format(date);
		System.out.println(title);
		System.out.println("沪深300市盈率(TTM):");
		System.out.println("========");
//		System.out.println("沪市停牌");
		printHalting("http://stock.eastmoney.com/news/chstpyl.html", title);
//		System.out.println("深市停牌");
		printHalting("http://stock.eastmoney.com/news/csstpyl.html", title);
		
		for(String code:LIST){
			System.out.println(code);
			printInvestors(code);
//			break;
		}
	}
}
