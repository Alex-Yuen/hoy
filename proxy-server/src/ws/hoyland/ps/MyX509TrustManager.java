package ws.hoyland.ps;


import java.security.cert.CertificateException;

import javax.net.ssl.X509TrustManager;

public class MyX509TrustManager  implements X509TrustManager  {

	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] arg0,
			String arg1) throws CertificateException {
		// TODO Auto-generated method stub
		System.out.println("1");
	}

	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] arg0,
			String arg1) throws CertificateException {
		// TODO Auto-generated method stub
		System.out.println("2");
	}

	@Override
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		System.out.println("3");
		return null;
		
	}

}
