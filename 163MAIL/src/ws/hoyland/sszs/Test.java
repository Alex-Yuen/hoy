package ws.hoyland.sszs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
//			String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";

			DefaultHttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					5000);
			client.addResponseInterceptor(new HttpResponseInterceptor() {

				@Override
				public void process(HttpResponse response, HttpContext context)
						throws HttpException, IOException {
					 HttpEntity entity = response.getEntity();  
				        Header ceheader = entity.getContentEncoding();  
				        if (ceheader != null) {  
				            HeaderElement[] codecs = ceheader.getElements();  
				            for (int i = 0; i < codecs.length; i++) {  
				                if (codecs[i].getName().equalsIgnoreCase("gzip")) {  
				                    response.setEntity(new GzipDecompressingEntity(  
				                            response.getEntity()));  
				                    return;  
				                }  
				            }  
				        }  					
				}  						  
			});  
			
			HttpHost proxy = new HttpHost("116.226.49.175", 8080);
			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);

			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			InputStream instream = Test.class
					.getResourceAsStream("/my.truststore");
			// 密匙库的密码
			trustStore.load(instream, "Hoy133".toCharArray());
			// 注册密匙库
			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
			// 不校验域名
			socketFactory
					.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", 443, socketFactory);
			client.getConnectionManager().getSchemeRegistry().register(sch);
			// 获得HttpGet对象
			HttpGet httpGet = null;
			httpGet = new HttpGet(
					"https://ynote.youdao.com/login/acc/reg/query?app=client&product=YNOTE&ClientVer=30500000000&GUID=PCacef4b7bf9ee6a1d3&LoginFormABTest=LoginFormATest&client_ver=30500000000&device_id=PCacef4b7bf9ee6a1d3&device_name=ZHU-PC&device_type=PC&os=Windows&os_ver=Windows%207&vendor=null");
			// 发送请求
			HttpResponse response = client.execute(httpGet);
			// 输出返回值
			InputStream is = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
