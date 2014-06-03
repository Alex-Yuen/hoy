package ws.hoyland.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import ws.hoyland.security.ClientDetecter;

public class HoylandClassLoader extends ClassLoader {
	private String account;
	private String password;
	
	private static String expBytes = "010001";
	private static String modBytes = "C39A51FB1202F75F0E20F691C8E370BCFA7CD2B75FD588CADAC549ADF1F03CFDAACCB9FBA5D7219CA4A3E40F9324121474BE85355CF178E0D3BD0719EDF859D60D24874B105FAC73EF067DEE962F5D12C7DB983039BA5EE0183479923174886A2C45ACFD5441C1B2FCC2083952016C66631884527585FF446BBC4F75606EF87B";


	public HoylandClassLoader() {
		// TODO Auto-generated constructor stub
	}

	public HoylandClassLoader(ClassLoader arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public Class<?> loadClass(String name, String account, String password)
			throws ClassNotFoundException {
		this.account = account;
		this.password = password;
		return super.loadClass(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// load from internet accroding to account & password
		Class<?> r = null;
		try {
			// URL
			URL url = new URL("http://www.y3y4qq.com/gc");

			Crypter crypt = new Crypter();
			byte[] mid = Converts.hexStringToByte(ClientDetecter
					.getMachineID("SMZS"));

			// String url = "http://www.y3y4qq.com/ge";
			byte[] key = Util.genKey();
			String header = Converts.bytesToHexString(key).toUpperCase()
					+ Converts.bytesToHexString(crypt.encrypt(mid, key))
							.toUpperCase();
			// Console.WriteLine(byteArrayToHexString(key).ToUpper());
			// Console.WriteLine(content);
			// client.UploadString(url, content);
			// client.UploadString(url,
			// client.Encoding = Encoding.UTF8;

			String content = "account=" + account + "&password=" + password;
			// RSA加密
			KeyFactory factory = KeyFactory.getInstance("RSA");
			Cipher cipher = Cipher.getInstance("RSA");
			BigInteger modules = new BigInteger(modBytes, 16);
			BigInteger exponent = new BigInteger(expBytes, 16);
			
			RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modules,
					exponent);
			PublicKey pubKey = factory.generatePublic(pubSpec);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] encrypted = cipher.doFinal(content.getBytes());
			
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);// 允许连接提交信息
			connection.setRequestMethod("POST");// 网页提交方式“GET”、“POST”
			// connection.setRequestProperty("User-Agent",
			// "Mozilla/4.7 [en] (Win98; I)");
			connection.setRequestProperty("Content-Type",
					"text/plain; charset=UTF-8");
			StringBuffer sb = new StringBuffer();
			sb.append(header);
			sb.append(Converts.bytesToHexString(encrypted));
			OutputStream os = connection.getOutputStream();
			os.write(sb.toString().getBytes());
			os.flush();
			os.close();

			InputStream input = connection.getInputStream();
			byte[] bs = new byte[input.available()];
			input.read(bs);
			r = super.defineClass(name, bs, 0, bs.length);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClassNotFoundException(name);
		}
		return r;

		// ws.hoyland.sm.Dynamicer
	}
}
