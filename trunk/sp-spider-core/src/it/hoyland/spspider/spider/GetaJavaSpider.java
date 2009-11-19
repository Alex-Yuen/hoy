package it.hoyland.spspider.spider;

import it.hoyland.spspider.core.Spider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class GetaJavaSpider extends Spider {
	
	public GetaJavaSpider(Observer obs){
		super(obs);
	}
	
	@Override
	public void run() {
		System.out.println(this.getClass().getName()+": start");
		String url = "http://www.getafreelancer.com/projects/by-job/Java.html";
		this.message.delete(0, this.message.length());
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String line = null;
		StringBuffer content = new StringBuffer();

		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			System.out.println("");
			System.out.println(url);
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
					// System.out.println(line);
				}
			}
			// Consume response content
			if (entity != null) {
				entity.consumeContent();
			}
			System.out.println("----------------------------------------");

			// ·ÖÎö
			String regex = "";
			// List<String> lt = null;
			Pattern pa = null;
			Matcher ma = null;

			if (content.length() != 0) {
				String cs = content.toString();
				// total
				// lt = new ArrayList<String>();
				System.out.println("total:");
				regex = "<h3>.*? Freelance Jobs Found</h3>";
				pa = Pattern.compile(regex, Pattern.CANON_EQ);
				ma = pa.matcher(cs);

				while (ma.find()) {
					String title = ma.group();
					title = title.substring(4, title.indexOf(" "));
					
					System.out.println(title);
					message.append(title);
					// lt.add(ma.group());
				}

				System.out.println("");

				// detail
				System.out.println("detail:");
				regex = "<tr onclick=\"onResultsRowClick\\('\\.\\./\\.\\./projects/.*?</td> </tr>";
				pa = Pattern.compile(regex, Pattern.CANON_EQ);
				ma = pa.matcher(cs);
				int ix = 0;

				String regex2 = "title=\".*?\"";
				Pattern pa2 = Pattern.compile(regex2, Pattern.CANON_EQ);
				Matcher ma2 = null;

				String regex3 = "<!----><td align=right nowrap>                .*?</td>";
				Pattern pa3 = Pattern.compile(regex3, Pattern.CANON_EQ);
				Matcher ma3 = null;

				String regex4 = "<td align=right nowrap>\\$.*?</td>";
				Pattern pa4 = Pattern.compile(regex4, Pattern.CANON_EQ);
				Matcher ma4 = null;

				while (ma.find()) {
					// System.out.println(ma.group());
					ma2 = pa2.matcher(ma.group());
					while (ma2.find()) {
						System.out.print(ma2.group().substring(7,
								ma2.group().length() - 1)
								+ "");
					}

					ma3 = pa3.matcher(ma.group());
					while (ma3.find()) {
						System.out.print("\t[Bid]:"
								+ ma3.group().substring(46,
										ma3.group().indexOf("</td>")));
					}

					ma4 = pa4.matcher(ma.group());
					while (ma4.find()) {
						System.out.println("\t[Average]:"
								+ ma4.group().substring(23,
										ma4.group().indexOf("</td>")));
					}
					ix++;
					// lt.add(ma.group());
				}
				System.out.println("");
				System.out.println("count=" + ix);
				System.out.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
			this.obs.update(this, message.toString());
		}

	}
}
