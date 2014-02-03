package ws.hoyland.qqgm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.*;
import java.security.interfaces.*;

import sun.misc.BASE64Encoder;
import ws.hoyland.util.Converts;

public class Tet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
//			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//			// 密钥位数
//			keyPairGen.initialize(1024);
//			// 密钥对
//			KeyPair keyPair = keyPairGen.generateKeyPair();
//			// 公钥
//			PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//			// 私钥
//			PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//			// 得到公钥字符串
//			String publicKeyString = getKeyString(publicKey);
//			// 得到私钥字符串
//			String privateKeyString = getKeyString(privateKey);
//			// 将密钥对写入到文件
//			FileWriter pubfw = new FileWriter("C:/publicKey.keystore");
//			FileWriter prifw = new FileWriter("C:/privateKey.keystore");
//			BufferedWriter pubbw = new BufferedWriter(pubfw);
//			BufferedWriter pribw = new BufferedWriter(prifw);
//			pubbw.write(publicKeyString);
//			pribw.write(privateKeyString);
//			pubbw.flush();
//			pubbw.close();
//			pubfw.close();
//			pribw.flush();
//			pribw.close();
//			prifw.close();

			String content = "password=981019.+&salt=\\x00\\x00\\x00\\x00\\x60\\xff\\xff\\x5b&vcode=!GQH";
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		String s = (new BASE64Encoder()).encode(keyBytes);
		return s;
	}
}
