package it.hoyland.spspider.core.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("OK");

		String[] list = new String[] {
				//"https://www.getafreelancer.com/projects/by-job/NET.html", 
		        "http://www.getafreelancer.com/projects/by-job/Java.html"};

		HttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String line = null;
		StringBuffer content = new StringBuffer();

		for (int i = 0; i < list.length; i++) {
			HttpGet httpget = new HttpGet(list[i]);
			try {
				HttpResponse response = httpclient.execute(httpget,
						localContext);
				HttpEntity entity = response.getEntity();
				System.out.println(list[i]);
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				if (entity != null) {
					System.out.println("Response content length: "
							+ entity.getContentLength());
					InputStream is = entity.getContent();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader in = new BufferedReader(isr);
					while ((line = in.readLine()) != null) {
						content.append(line);
						System.out.println(line);
					}
				}
				// Consume response content
				if (entity != null) {
					entity.consumeContent();
				}
				System.out.println("----------------------------------------");
				
				// ·ÖÎö
				String regex = "";
				List<String> lt = null;
				Pattern pa = null;
				Matcher ma = null;
				
				if(content.length()!=0){
					String cs = content.toString();
					// total
					// lt = new ArrayList<String>();
					System.out.println("total:");
					regex = "<h3>.*? Freelance Jobs Found</h3>";  
					pa = Pattern.compile(regex, Pattern.CANON_EQ);  
					ma = pa.matcher(cs);
					
					while (ma.find()) {  
						System.out.println(ma.group());
						// lt.add(ma.group());  
					}  
					
					System.out.println("");
					
					//detail
					System.out.println("detail:");
					regex = "<tr onclick=\"onResultsRowClick('../../projects/.*?</td> </tr>";  
					pa = Pattern.compile(regex, Pattern.CANON_EQ);  
					ma = pa.matcher(cs);
					
					while (ma.find()) {  
						System.out.println(ma.group());
						// lt.add(ma.group());  
					}  
					
					System.out.println("");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				httpclient.getConnectionManager().shutdown();
			}
		}
	}

}
