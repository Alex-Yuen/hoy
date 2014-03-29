import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import ws.hoyland.util.Converts;


public class Decrypte {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
//			SecretKeySpec skeySpec = new SecretKeySpec(Converts.hexStringToByte("E03D0E8E4655C94252CB26745A997B0A"), "AES");
//
//			 //SecretKeySpec skeySpec = getKey(skeySpec);
//		        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		        IvParameterSpec iv = new IvParameterSpec(Converts.hexStringToByte("C55A9FEED8A4E1FD3F8F77013B71844D"));
//		        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//		        InputStream is = Decrypte.class.getResourceAsStream("/inner.pack.gz");
//		        DataInputStream dis = new DataInputStream(Decrypte.class.getResourceAsStream("/inner.pack.gz"));
//		        
//		        byte[] bs = new byte[is.available()];
//		        dis.read(bs);
//		        
//		        FileOutputStream fos = new FileOutputStream(new File("C://inner.jar"));
//		        
//		     
//		        byte[] original = cipher.doFinal(bs);
//		        
//		        fos.write(original);
//		        fos.flush();
//		        fos.close();
			
			FileOutputStream fostream = new FileOutputStream("C://inner2.jar");
	        JarOutputStream jostream = new JarOutputStream(fostream);
	        Unpacker unpacker = Pack200.newUnpacker();
	        
			unpacker.unpack(new File("C://inner.pack"), jostream);
			jostream.flush();
			jostream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
