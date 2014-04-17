package ws.hoyland.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;

public class EntityUtil {

	public static HttpEntity getEntity(List<NameValuePair> nvps) {
		StringEntity entity = null;
		try {
			String entityValue = URLEncodedUtils.format(nvps, "UTF-8");
			// Do your replacement here in entityValue

			// entityValue = URLEncoder.encode(entityValue, "UTF-8");
			// entityValue = entityValue.replaceAll("-", "%2D");
			// entityValue = entityValue.replaceAll("\\+", "%20");
			entity = new StringEntity(entityValue, "UTF-8");
			entity.setContentType(URLEncodedUtils.CONTENT_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entity;
	}

	public static String getContent(HttpEntity entity) {
		String result = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"));
			StringBuffer sbf = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sbf.append(line + "\r\n");
			}
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
