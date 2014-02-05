package ws.hoyland.qqgm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.sql.DataSource;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class GPServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7633331277396719497L;

	private String module = "w5pR+xIC918OIPaRyONwvPp80rdf1YjK2sVJrfHwPP2qzLn7pdchnKSj5A+TJBIUdL6FNVzxeODTvQcZ7fhZ1g0kh0sQX6xz7wZ97pYvXRLH25gwObpe4Bg0eZIxdIhqLEWs/VRBwbL8wgg5UgFsZmMYhFJ1hf9Ea7xPdWBu+Hs=";
//	private String exponentString = "AQAB";
	private String delement = "cdMGq9zyXvMwrJvvgABiZYY6RwCwwvkEWsR9uLxWeZd/4fzEZOBIzfe864Tosg/XWYxYxhHc7uOeM5zDSQjBdVjkJKJN8H1JISm9qTWqmZATL03xgItf5glVxupsMBqBXr3FdYJe8PjOmIYXpREBSWvkMfqwcpuaU+zRuOu+FSk=";

	public GPServlet() {
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.getOutputStream().println("OK");
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return;
		// RequestDispatcher reqDispatcher =
		// req.getRequestDispatcher("/WEB-INF/view/index.jsp");
		// reqDispatcher.forward(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		byte[] key = null;
		Crypter crypter = new Crypter();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String result = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

		try {
			BufferedReader sis = req.getReader();
			char[] buf = new char[1024];
			int len = 0;
			StringBuffer sb = new StringBuffer();
			while ((len = sis.read(buf)) != -1) {
				sb.append(buf, 0, len);
			}
			// System.out.println("A");
			// System.out.println(sb.toString());
			// 解密

			boolean valid = false;
			key = Converts.hexStringToByte(sb.toString().substring(0, 32));
			byte[] fmc = Converts.hexStringToByte(sb.toString().substring(32,
					96)); // System.out.println("B");
			String lmc = Converts.bytesToHexString(crypter.decrypt(fmc, key));// 机器码

			String JNDINAME = "java:comp/env/jdbc/qqgm";
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;

			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(JNDINAME);
			conn = ds.getConnection();
			stmt = conn.createStatement();

			rs = stmt
					.executeQuery("select * from t_qqgm where machine_code = '"
							+ lmc + "'");
			// System.out.println("select * from t_qqgm where machine_code = '"+lmc+"'");
			// sdf.format(Calendar.getInstance().getTime())

			if (rs.next()) {
				// System.out.println("1");
				String expire = rs.getString("expire");
				System.out.println(expire);
				long itv = sdf.parse(expire).getTime()
						- Calendar.getInstance().getTime().getTime();
//				System.out.println(itv / (24 * 60 * 60 * 1000));
//				System.out.println(itv % (24 * 60 * 60 * 1000));
//				long day = (itv / (24 * 60 * 60 * 1000))
//						+ (((itv % (24 * 60 * 60 * 1000)) > 0) ? 1 : 0);
				if (itv > 0) {
					valid = true;
				} else {
					valid = false;
				}
				// System.out.println("2");
			}else{
				valid = false;
			}
			// System.out.println("3");
			// sdf.parse(result)

			rs.close();
			stmt.close();

			if(valid){
				System.out.println("RSA:" + sb.toString().substring(96));
				byte[] rsa = Converts.hexStringToByte(sb.toString().substring(96));
	
				byte[] expBytes = Base64.decode(delement);
				byte[] modBytes = Base64.decode(module);
	
				BigInteger modules = new BigInteger(1, modBytes);
				BigInteger exponent = new BigInteger(1, expBytes);
	
				KeyFactory factory = KeyFactory.getInstance("RSA");
				Cipher cipher = Cipher.getInstance("RSA");
	
				RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules,
						exponent);
				PrivateKey privKey = factory.generatePrivate(privSpec);
				cipher.init(Cipher.DECRYPT_MODE, privKey);
				byte[] decrypted = cipher.doFinal(rsa);
	
				String content = new String(decrypted);
	
				String[] cts = content.split("&");
				String password = cts[0].split("=")[1];
				String salt = cts[1].split("=")[1];
				String vcode = cts[2].split("=")[1];
	
				byte[] rsx = Converts.MD5Encode(password.getBytes());
				int psz = rsx.length;
	
				byte[] rsb = new byte[psz + 8];
				for (int i = 0; i < psz; i++) {
					rsb[i] = rsx[i];
				}
	
				salt = salt.substring(2);
	
				String[] salts = salt.split("\\\\x");
	
				for (int i = 0; i < salts.length; i++) {
					rsb[psz + i] = (byte) Integer.parseInt(salts[i], 16);
				}
	
				rsx = Converts.MD5Encode(rsb);
				String resultString = Converts.bytesToHexString(rsx).toUpperCase();
				rsx = Converts.MD5Encode((resultString + vcode.toUpperCase())
						.getBytes());
	
				resultString = Converts.bytesToHexString(rsx).toUpperCase();
				result = resultString;
			}else{
				result = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";	
			}
		} catch (Exception e) {
			e.printStackTrace();
			// resp.getOutputStream().println("OK");
		}

		// result = Converts.bytesToHexString(crypter.encrypt(result.getBytes(),
		// key));

		resp.setContentType("text/html;charset=UTF-8");
		resp.getOutputStream().print(result);
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return;
	}
}
