package de.innosystec.unrar;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] key = "123456788DE54321".getBytes();
		byte[] buffer = "123dsfsfdsfdff21".getBytes();
		
		System.out.print("buffer:");
	    for(int x=0;x<buffer.length;x++){
	    	System.out.print(buffer[x]+",");
	    }
	    System.out.println();
	    
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			byte[] encrypted = cipher
					.doFinal(buffer);
			
			System.out.print("encrypted:");
		    for(int x=0;x<encrypted.length;x++){
		    	System.out.print(encrypted[x]+",");
		    }
		    System.out.println("["+encrypted.length+"]");
		    //System.out.println();
		    
		    cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		    
			byte[] decrypted = cipher
			.doFinal(encrypted);
	
			System.out.print("decrypted:");
		    for(int x=0;x<decrypted.length;x++){
		    	System.out.print(decrypted[x]+",");
		    }
		    System.out.println();
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
