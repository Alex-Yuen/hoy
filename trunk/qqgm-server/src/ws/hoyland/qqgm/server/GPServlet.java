package ws.hoyland.qqgm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.Cipher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import ws.hoyland.util.Converts;

public class GPServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7633331277396719497L;

	private String module = "w5pR+xIC918OIPaRyONwvPp80rdf1YjK2sVJrfHwPP2qzLn7pdchnKSj5A+TJBIUdL6FNVzxeODTvQcZ7fhZ1g0kh0sQX6xz7wZ97pYvXRLH25gwObpe4Bg0eZIxdIhqLEWs/VRBwbL8wgg5UgFsZmMYhFJ1hf9Ea7xPdWBu+Hs=";
	private String exponentString = "AQAB";
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
		// byte[] key = null;
		// Crypter crypter = new Crypter();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String result = "Nothing";

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
			/**
			 * key = Converts.hexStringToByte(sb.toString().substring(0, 32));
			 * byte[] fmc = Converts.hexStringToByte(sb.toString().substring(32,
			 * 96)); //System.out.println("B"); String lmc =
			 * Converts.bytesToHexString(crypter.decrypt(fmc, key));//机器码
			 **/
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

			byte[] rs = new byte[psz + 8];
			for (int i = 0; i < psz; i++) {
				rs[i] = rsx[i];
			}

			salt = salt.substring(2);

			String[] salts = salt.split("\\\\x");

			for (int i = 0; i < salts.length; i++) {
				rs[psz + i] = (byte)Integer.parseInt(salts[i], 16);
			}
			
			rsx = Converts.MD5Encode(rs); 
			String resultString = Converts.bytesToHexString(rsx).toUpperCase();
			rsx = Converts.MD5Encode((resultString+vcode.toUpperCase()).getBytes());
			
			resultString = Converts.bytesToHexString(rsx).toUpperCase();
			/**
			 * System.Security.Cryptography.MD5CryptoServiceProvider md5CSP =
			 * new System.Security.Cryptography.MD5CryptoServiceProvider();
			 * 
			 * byte[] results =
			 * md5CSP.ComputeHash(Encoding.UTF8.GetBytes(this.password));
			 * 
			 * int psz = results.Length; byte[] rs = new byte[psz + 8]; for (int
			 * i = 0; i < psz; i++) { rs[i] = results[i]; }
			 * 
			 * string[] salts = Regex.Split(this.salt.Substring(2), "\\\\x");
			 * 
			 * //string[] salts = this.salt.Substring(2).split("\\\\x");
			 * //System.out.println(salts.length); for (int i = 0; i <
			 * salts.Length; i++) { rs[psz + i] = (byte)Int32.Parse(salts[i],
			 * System.Globalization.NumberStyles.HexNumber); }
			 * 
			 * results = md5CSP.ComputeHash(rs); string resultString =
			 * byteArrayToHexString(results).ToUpper();
			 * 
			 * //vcode = "!RQM"; results =
			 * md5CSP.ComputeHash(Encoding.UTF8.GetBytes(resultString +
			 * vcode.ToUpper())); resultString =
			 * byteArrayToHexString(results).ToUpper();
			 */
			result = resultString;
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
