/*
 * Copyright (c) 2007 innoSysTec (R) GmbH, Germany. All rights reserved.
 * Original author: Edmund Wagner
 * Creation date: 23.05.2007
 *
 * Source: $HeadURL$
 * Last changed: $LastChangedDate$
 * 
 * the unrar licence applies to all junrar source and binary distributions 
 * you are not allowed to use this source to re-create the RAR compression algorithm
 * 
 * Here some html entities which can be used for escaping javadoc tags:
 * "&":  "&#038;" or "&amp;"
 * "<":  "&#060;" or "&lt;"
 * ">":  "&#062;" or "&gt;"
 * "@":  "&#064;" 
 */
package de.innosystec.unrar.io;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.innosystec.unrar.rarfile.BaseBlock;

/**
 * DOCUMENT ME
 *
 * @author $LastChangedBy$
 * @version $LastChangedRevision$
 */
public class ReadOnlyAccessFile extends RandomAccessFile
        implements IReadOnlyAccess{

	private byte[] salt;
	private Queue<Byte> data = new LinkedList<Byte>();
		
	/**
	 * @param file the file
	 * @throws FileNotFoundException
	 */
	public ReadOnlyAccessFile(File file) throws FileNotFoundException {
		super(file, "r");
	}

	public int readFully(byte[] buffer, int count) throws IOException {
        assert (count > 0) : count;

        //read salt
	    if(this.salt!=null){
	    	//System.out.println("salt is not null");
	    	 
	    	int cs = data.size();
	    	int sizeToRead = count - cs;

	    	if(sizeToRead>0){
	    		int alignedSize = sizeToRead+((~sizeToRead+1)&0xf);
	    		byte[] tr = new byte[alignedSize];
		    	this.readFully(tr, 0, alignedSize);
		    	
		    	/**
		    	 * decrypt
		    	 * 
		    	 */
		    	//string
		    	byte[] input = "1234".getBytes();
		    	byte[] ipx = new byte[input.length+salt.length];
		    	for(int x=0;x<input.length;x++){
		    		ipx[x] = input[x];
		    	}
		    	for(int x=0;x<salt.length;x++){
		    		ipx[x+input.length] = salt[x];
		    	}
		    	
		    	//key & iv
		    	IMessageDigest md = HashFactory.getInstance("SHA-256");
		    	md.update(ipx, 0, ipx.length);
		    	byte[] digest = md.digest();
		    	
		    	byte[] key = new byte[16];
		    	byte[] iv = new byte[16];
		    	for(int x=0;x<key.length;x++){
		    		key[x] = digest[x];
		    	}
		    	for(int x=0;x<iv.length;x++){
		    		iv[x] = digest[x+key.length];
		    	}
		    	
//		    	tr = new byte[]{
//		    			71,-93,39,21,58,40,-94,6,80,-42,-79,-14,-69,-109,-56,-13,64,-63,2,-24,29,100,80,-79,2,-35,-71,80,86,50,-49,27
//		    	};
		    //	key = "123456788DE54321".getBytes();
		    	
		    	//decrypt
				SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
				try {
					Cipher cipher = Cipher.getInstance("AES");
					cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	
					byte[] decrypted = cipher
							.doFinal(tr);
					
					for(int i=0;i<decrypted.length;i++){
						data.add(decrypted[i]);
					}
					
//					System.out.print("decrypted:");
//				    for(int x=0;x<decrypted.length;x++){
//				    	System.out.print(decrypted[x]+",");
//				    }
//				    System.out.println();
				    
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	for(int i=0;i<count;i++){
	    		buffer[i] = data.poll();
	    	}
	    		
	    }else {
	    	this.readFully(buffer, 0, count);
	    }
	    
        return count;
    }

	public long getPosition() throws IOException {
        return this.getFilePointer();
	}

	public void setPosition(long pos) throws IOException {
        this.seek(pos);
	}
	
	public void setSalt(byte[] salt){
		this.salt = salt;
	}
}
