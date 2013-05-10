package ws.hoyland.qt;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class AD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String UAG = "Dalvik/1.2.0 (Linux; U; Android 2.2; sdk Build/FRF91)";
		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();

			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpGet httpGet = null;
			String line = null;
			httpGet = new HttpGet(
					"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_query_captcha?aq_base_sid=M5eYiEfoF7hmSQynTkFYQJfGC8lohySZ&uin=68159276&scenario_id=1");
			httpGet.setHeader("User-Agent", UAG);
			httpGet.setHeader("Connection", "Keep-Alive");
			response = httpclient.execute(httpGet);
			entity = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			line = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
			httpGet.releaseConnection();
			System.out.println(line);
			//GET /cn/mbtoken3/mbtoken3_query_captcha?aq_base_sid=ggQqPgtmnSp0ky1i0vtZn6V0yn1nIiSZ&uin=68159276&scenario_id=1
					
			//[truncated] GET /cn/mbtoken3/mbtoken3_upgrade_determin_v2?uin=68159276&sess_id=ggQqPgtmnSp0ky1i0vtZn6V0yn1nIiSZ&data=F93B583CEF02C61776056504A4F937FEE66AF811124B1D5262C382CFAC1AF987BE31E5A4413C7A18699C04D58EC15ED94882BF741B5D709001F0CBD298
			httpGet = new HttpGet(
					"http://w.aq.qq.com/cn/mbtoken3/mbtoken3_upgrade_determin_v2?uin=68159276&sess_id=M5eYiEfoF7hmSQynTkFYQJfGC8lohySZ&data=8EAA3E6A94D328D74E2B6BA1AD73940EEBB16D00A5300B61BECA2AD043582A1E8B5152A94BAA76C347B516CE16C741B231622C943EE3EEA71BF0DA9172C337B8E383EDF85E622115B66DF98835387E21C506BCB7EB129EE5");
			httpGet.setHeader("User-Agent", UAG);
			httpGet.setHeader("Connection", "Keep-Alive");

			response = httpclient.execute(httpGet);
			entity = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			line = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
			httpGet.releaseConnection();
			System.out.println(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
