package ws.hoyland.qqgm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import sun.misc.BASE64Decoder;
import ws.hoyland.util.Converts;

public class GPServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7633331277396719497L;

	private String pvkey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJWvFHklRH+1235jIUMSt8pyyUFI"
			+ "dhXfnUygxKIvHRY6+fGPbhDoVxzizBroeVmT1D+HwJ9tBFnlBZ4wG92NF9ir/xI8e7PBKguQ+sMA"
			+ "dly7cOS8IvGPB0roINfI1q4wKnDBN829g52IZk0SugN22o4t2Oy6+D1c7quspphGUWwPAgMBAAEC"
			+ "gYAoO2HGWhT6OpgfdWeITqyltoqPiHls0ajG4uNXMFCiHAH4QnP0CW74raDsjpWnVQuUc9l7QGMO"
			+ "AnerGAU1XGoPAdiCN9m9fSKG3HDOm7++gzI0OqOdloarUjiHQPjF5lSDcPm0NA73eunSwysxMl7S"
			+ "Q+HKcglRrc1BlG3yqFiIkQJBANyWYGuNeShxlCf6bCNMk8m1ulnwEtnRUQ9v9CC6QjPRSpej73w2"
			+ "tqzI4zNz75M1KSdvxsHYVlzSIdm7gczYhCkCQQCttrx6anPfijyf1uPp7sMpNTy2Sg+50GgU1LxH"
			+ "gmM/9vAbWP379e2WovSTbSMkCrW++tokYOJCykaRxf+Fw7V3AkEA3C1BKrl4kLTlxA3CuG0sKq84"
			+ "xaLKvh2fAoCltbEUeXJTOQ8J8PsP1WFi3q2/GTl7zYBVbJFKirW30gPGKLiJGQJASQAzIqqbuUQp"
			+ "h+5RZxyMbUPPGLf09dxL8P0Zhl+X/+hxLLzwNzM5PgWLDG1fyCSsDe7zPzhrvS+uClKPuBHvrwJB"
			+ "AIuPYVvBLfVUSQjzVpBFdoI9cThmvAtZctay2o2shpWcq4FLPCFgNBR7kb7C9tcxdRvfFDP/cSc3"
			+ "b87jbDGYVRg=";

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
//		byte[] key = null;
//		Crypter crypter = new Crypter();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
			key = Converts.hexStringToByte(sb.toString().substring(0, 32));
			byte[] fmc = Converts.hexStringToByte(sb.toString().substring(32, 96));
			//System.out.println("B");
			String lmc = Converts.bytesToHexString(crypter.decrypt(fmc, key));//机器码
			**/
			System.out.println("RSA:"+sb.toString().substring(96));
			byte[] rsa = Converts.hexStringToByte(sb.toString().substring(96));
			
			byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(pvkey);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			
			Cipher cipher = Cipher.getInstance("RSA");  
			
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] deBytes = cipher.doFinal(rsa);//(new BASE64Decoder()).decodeBuffer(sb.toString())  
            String content = new String(deBytes);  
            
            
            /**

                    System.Security.Cryptography.MD5CryptoServiceProvider md5CSP = new System.Security.Cryptography.MD5CryptoServiceProvider();

                    byte[] results = md5CSP.ComputeHash(Encoding.UTF8.GetBytes(this.password));

                    int psz = results.Length;
                    byte[] rs = new byte[psz + 8];
                    for (int i = 0; i < psz; i++)
                    {
                        rs[i] = results[i];
                    }

                    string[] salts = Regex.Split(this.salt.Substring(2), "\\\\x");

                    //string[] salts = this.salt.Substring(2).split("\\\\x");
                    //System.out.println(salts.length);
                    for (int i = 0; i < salts.Length; i++)
                    {
                        rs[psz + i] = (byte)Int32.Parse(salts[i], System.Globalization.NumberStyles.HexNumber);
                    }

                    results = md5CSP.ComputeHash(rs);
                    string resultString = byteArrayToHexString(results).ToUpper();

                    //vcode = "!RQM";
                    results = md5CSP.ComputeHash(Encoding.UTF8.GetBytes(resultString + vcode.ToUpper()));
                    resultString = byteArrayToHexString(results).ToUpper();
             */
            result = content;
		} catch (Exception e) {
			e.printStackTrace();
			// resp.getOutputStream().println("OK");
		}

//		result = Converts.bytesToHexString(crypter.encrypt(result.getBytes(),
//				key));

		resp.setContentType("text/html;charset=UTF-8");
		resp.getOutputStream().print(result);
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return;
	}
}
