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

import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.cipher.Rijndael;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * DOCUMENT ME
 * 
 * @author $LastChangedBy$
 * @version $LastChangedRevision$
 */
public class ReadOnlyAccessFile extends RandomAccessFile implements
		IReadOnlyAccess {

	private byte[] salt;
	private Queue<Byte> data = new LinkedList<Byte>();
	private Rijndael rin;
	private byte[] AESKey = new byte[16];
	private byte[] AESInit = new byte[16];

	/**
	 * @param file
	 *            the file
	 * @throws FileNotFoundException
	 */
	public ReadOnlyAccessFile(File file) throws FileNotFoundException {
		super(file, "r");
	}

	public int readFully(byte[] buffer, int count) throws IOException {
		assert (count > 0) : count;

		// read salt
		if (this.salt != null) {
			// System.out.println("salt is not null");

			int cs = data.size();
			int sizeToRead = count - cs;

			if (sizeToRead > 0) {
				int alignedSize = sizeToRead + ((~sizeToRead + 1) & 0xf);
				for(int i=0;i<alignedSize/16;i++){
					byte[] tr = new byte[16];
					this.readFully(tr, 0, 16);
	
					/**
					 * decrypt & add to data list
					 * 
					 */
					byte[] out = new byte[16];
					this.rin.decryptBlock(tr, 0, out, 0);
					for(int j=0;j<out.length;j++){
						this.data.add((byte)(out[j]^AESInit[j%16])); //32:114, 33:101
					}

					for(int j=0;j<AESInit.length;j++){
						AESInit[j] = tr[j];
					}
				}
				//System.out.println(out);
			}

			for (int i = 0; i < count; i++) {
				buffer[i] = data.poll();
			}

		} else {
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

	public void setSalt(byte[] salt) {
		this.salt = salt;

		if(salt!=null){
		// caculate aes key and aes iv and then init rinj
			String password = "1234";
			this.rin = new Rijndael();
			initAES(this.rin, password, salt, AESInit, AESKey);
		}


//		System.out.println(AESKey);
//		System.out.println(AESInit);
	}
	
	public void initAES(Rijndael rin, String password, byte[] salt, byte[] AESInit, byte[] AESKey){
		int rawLength = 2 * password.length();
		Byte[] rawpsw = new Byte[rawLength + 8];
		byte[] pwd = password.getBytes();
		for (int i = 0; i < password.length(); i++) {
			rawpsw[i * 2] = pwd[i];
			rawpsw[i * 2 + 1] = 0;
		}
		for (int i = 0; i < salt.length; i++) {
			rawpsw[i + rawLength] = salt[i];
		}

		// rawLength += 8;

		// SHA-1
		try {
			MessageDigest sha = MessageDigest.getInstance("sha-1");

			final int HashRounds = 0x40000;
			List<Byte> content = new ArrayList<Byte>();
			for (int i = 0; i < HashRounds; i++) {
				content.addAll(Arrays.asList(rawpsw));
				Byte[] pswNum = new Byte[3];
				pswNum[0] = (byte) i;
				pswNum[1] = (byte) (i >> 8);
				pswNum[2] = (byte) (i >> 16);
				content.addAll(Arrays.asList(pswNum));
				if (i % (HashRounds / 16) == 0) {
					byte[] input = new byte[content.size()];
					for(int j=0;j<input.length;j++){
						input[j] = content.get(j);
					}
					
					sha.update((byte[])input);
					byte[] digest = sha.digest();
					AESInit[i / (HashRounds / 16)] = digest[digest.length-1];
				}
			}
			
			byte[] input = new byte[content.size()];
			for(int j=0;j<input.length;j++){
				input[j] = content.get(j);
			}
			sha.update(input);
			byte[] digest = sha.digest();
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					AESKey[i * 4 + j] = (byte) (((digest[i*4]*0x1000000)&0xff000000|((digest[i*4+1]*0x10000)&0xff0000)|((digest[i*4+2]*0x100)&0xff00)|digest[i*4+3]&0xff) >> (j * 8));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

        try {
    		Map<Object, Object> map = new HashMap<Object, Object>();
            map.put(IBlockCipher.KEY_MATERIAL, AESKey);
            map.put(IBlockCipher.CIPHER_BLOCK_SIZE, new Integer(16));
			rin.init(map);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void resetData(){
		this.data.clear();
	}

	@Override
	public int paddedSize() {
		// TODO Auto-generated method stub
		return this.data.size();
	}
}
