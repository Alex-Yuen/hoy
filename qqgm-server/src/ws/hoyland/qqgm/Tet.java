package ws.hoyland.qqgm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.*;
import java.security.interfaces.*;

import sun.misc.BASE64Encoder;

public class Tet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {  
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");  
            // 密钥位数  
            keyPairGen.initialize(1024);  
            // 密钥对  
            KeyPair keyPair = keyPairGen.generateKeyPair();  
            // 公钥  
            PublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
            // 私钥  
            PrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
            //得到公钥字符串  
            String publicKeyString = getKeyString(publicKey);  
            //得到私钥字符串  
            String privateKeyString = getKeyString(privateKey);  
            //将密钥对写入到文件  
            FileWriter pubfw = new FileWriter("C:/publicKey.keystore");  
            FileWriter prifw = new FileWriter("C:/privateKey.keystore");  
            BufferedWriter pubbw = new BufferedWriter(pubfw);  
            BufferedWriter pribw = new BufferedWriter(prifw);  
            pubbw.write(publicKeyString);  
            pribw.write(privateKeyString);  
            pubbw.flush();  
            pubbw.close();  
            pubfw.close();  
            pribw.flush();  
            pribw.close();  
            prifw.close();    
             
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
