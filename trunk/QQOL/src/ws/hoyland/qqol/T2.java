package ws.hoyland.qqol;

import java.io.ByteArrayOutputStream;
import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.CRC32;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.math.ec.ECPoint;

import sun.security.ec.NamedCurve;

import ws.hoyland.util.YDM;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class T2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		YDM.INSTANCE.YDM_SetAppInfo(105, "4c0e22fb79a8afff2331d34786364b68");
		int userID = YDM.INSTANCE.YDM_Login("hoyland", "Hoy133");
		System.out.println("userID:"+userID);
		
		byte status = 1; //0在线，//1离开（自动那个回复）//2忙碌 //3隐身 
		String message = "您好，我现在有事不在，一会再和您联系。";
		final String account = "744625551"; //1627389787 //744625551
		String password = "981019.*";
		String ip = "183.60.19.100";//默认IP
		byte[] ips = new byte[]{
				(byte)183, (byte)60, (byte)19, (byte)100
		};

		byte[] serverPBK = new byte[]{
			0x04, (byte)0x92, (byte)0x8D, (byte)0x88, 0x50, 0x67, 0x30, (byte)0x88, 
			(byte)0xB3, 0x43, 0x26, 0x4E, 0x0C, 0x6B, (byte)0xAC, (byte)0xB8, 
			0x49, 0x6D, 0x69, 0x77, (byte)0x99, (byte)0xF3, 0x72, 0x11, 
			(byte)0xDE, (byte)0xB2, 0x5B, (byte)0xB7, 0x39, 0x06, (byte)0xCB, 0x08, 
			(byte)0x9F, (byte)0xEA, (byte)0x96, 0x39, (byte)0xB4, (byte)0xE0, 0x26, 0x04, 
			(byte)0x98, (byte)0xB5, 0x1A, (byte)0x99, 0x2D, 0x50, (byte)0x81, (byte)0x3D, (byte)0xA8	
		};
		
		short seq = 0x1123; //包序号
		
		//联接服务器,发送数据
		DatagramSocket ds = null;
		DatagramPacket dpIn = null;
		DatagramPacket dpOut = null;
		
		byte[] buf = null;
		byte[] buffer = null;
		byte[] content = null;
		
		ByteArrayOutputStream baos = null; 		
		ByteArrayOutputStream bsofplain = null; 
		
		ByteArrayOutputStream bsofpng = null; 
		
		byte[] key0825 = null;
		byte[] ecdhkey = null;
		byte[] token = null;
		byte[] key0836 = null;
		byte[] key0836x = null;
		byte[] pwdkey = null;
		byte[] key00BA = null;
//		byte[] data00BA = null;
		
		byte[] key0828 = null;
		byte[] key0828recv = null;
		byte[] sessionkey = null;
		
		byte[] vctoken = null;
		byte[] resultByte = null;
		
		Crypter crypter = new Crypter();
		byte[] encrypt = null;
		byte[] decrypt = null;

		byte[][] crcs = new byte[][]{genKey(16), genKey(16), genKey(16)};
		CRC32 crc = new CRC32();
		
		boolean redirect = false;
		boolean nvc = false;//是否需要验证码
		boolean dlvc = false;
		
		byte[] logintime = new byte[4];
		byte[] loginip = new byte[4];
		
		try{
			ds = new DatagramSocket(5023);			
			//------------------------------------------------------------------------------			
			//0825
			
			do{
				redirect = false;
				seq++;
				key0825 = genKey(0x10);
								
				//ecdhkey 随机生成的公钥，然后key0836x 即为共享秘密，由算法计算得出
				{
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
					
					ECParameterSpec ecSpec = NamedCurve.getECParameterSpec("secp192k1");
					
					KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH", "BC");			
					keyGen.initialize(ecSpec, new SecureRandom()); //公私钥 工厂
					KeyPair pair = keyGen.generateKeyPair(); //生成公私钥
					BCECPublicKey cpk =(BCECPublicKey) pair.getPublic();
					ECPoint.Fp point = (ECPoint.Fp)cpk.getQ();

					ecdhkey = point.getEncoded(true);	//ecdhkey
					System.out.println(ecdhkey.length);
					
					java.security.spec.ECPoint sp = ECPointUtil.decodePoint(ecSpec.getCurve(), serverPBK);
					KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
					ECPublicKeySpec pubSpec = new ECPublicKeySpec(sp, ecSpec);
					ECPublicKey myECPublicKey = (ECPublicKey) kf.generatePublic(pubSpec);
					
					System.out.println(((BCECPublicKey)myECPublicKey).getQ().getEncoded().length);
					
					KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
					agreement.init(pair.getPrivate());
					agreement.doPhase(myECPublicKey, true);
//					
					byte[] xx = agreement.generateSecret();
					key0836x = new byte[16];	//key0836x
					for(int i=0;i<key0836x.length;i++){
						key0836x[i] = xx[i];
					}
				}
				
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x08, 0x25
				});
				baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
				baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
				baos.write(new byte[]{
						0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x00, 0x00, 0x00
				});
				baos.write(key0825);
				//System.err.println(Converts.bytesToHexString(rndkey));
				//System.out.println(Converts.bytesToHexString(baos.toByteArray()));
				//以下需要加密
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[]{
						0x00, 0x18, 0x00, 0x16, 0x00, 0x01,
						0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B
				});
				bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x03, 0x09, 0x00, 0x08, 0x00, 0x01
				});
				bsofplain.write(ips);
//				bsofplain.write(new byte[]{
//						0x00, 0x00, 0x00, 0x00 
//				});
				bsofplain.write(new byte[]{
						//0x00, 0x04, 0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
						0x00, 0x02, 0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x14, 0x00, 0x1D, 0x01, 0x02 
				});
				bsofplain.write(new byte[]{
						0x00, 0x19 
				});
				bsofplain.write(ecdhkey);
				//System.out.println(Converts.bytesToHexString(bsofplain.toByteArray()));
				encrypt = crypter.encrypt(bsofplain.toByteArray(), key0825);
				//加密完成
	//			byte[] ts = crypter.decrypt(encrypt, rndkey);
	//			System.out.println(Converts.bytesToHexString(ts));
				baos.write(encrypt);
				baos.write(new byte[]{
						0x03
				});
				buf = baos.toByteArray();
				System.out.println("0825["+Converts.bytesToHexString(key0825)+"]");
				System.out.println(Converts.bytesToHexString(baos.toByteArray()));

				//OUT:	
				dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
				ds.send(dpOut);
				
				//IN:
				buffer = new byte[1024];
				dpIn = new DatagramPacket(buffer, buffer.length);
								
	//			byte[] ts = crypter.decrypt(encrypt, rndkey);
	//			System.out.println(Converts.bytesToHexString(ts));
				
				//while(true){
				ds.receive(dpIn);
				//还需判断buffer 121等位置，看是否是转向，也可能是104字节，不需转向
				buffer = pack(buffer);
				//System.out.println(buffer.length);
				if(buffer.length==135){
					redirect = true;
					System.out.println("重定向:"+buffer[128]+","+buffer[129]+","+buffer[130]);
					
					content = slice(buffer, 14, 120);
					decrypt = crypter.decrypt(content, key0825);
					
					ips = slice(decrypt, 95, 4);
					ip = (ips[0]&0xFF)+"."+(ips[1]&0xFF)+"."+(ips[2]&0xFF)+"."+(ips[3]&0xFF);
				}
			}while(redirect);
			
			content = slice(buffer, 14, 104);
			
//			String data = Converts.bytesToHexString(content);
//			System.out.println(data);
			
			//解密
			decrypt = crypter.decrypt(content, key0825);
			//System.out.println(Converts.bytesToHexString(decrypt));
			token = slice(decrypt, 5, 0x38);
			logintime = slice(decrypt, 67, 4);
			loginip = slice(decrypt, 71, 4);
			//System.out.println(Converts.bytesToHexString(loginip));
			//------------------------------------------------------------------------------
			//0836
			do{
				seq++;
				//nvc = false;
				
				//第一段
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(genKey(4));
				bsofplain.write(new byte[]{
						0x00, 0x01 
				});
				bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						//0x00, 0x00, 0x04, 0x36, 0x06, 0x00, 0x05, 0x11, 0x00, 0x00, 0x01, 0x00,
						0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B,
						0x00, 0x00, 0x01 // 不记住密码
				});
				bsofplain.write(Converts.MD5Encode(password));
				bsofplain.write(logintime);
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
				});
				bsofplain.write(loginip);
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0B, 0x00,
						0x00, 0x10,
						(byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0
						//(byte)0xE3, 0x04, 0x2B, (byte)0xB2, (byte)0xF7, (byte)0xEA, 0x62, 0x40, (byte)0x99, 0x62, (byte)0x81, 0x11, 0x44, 0x52, 0x17, 0x22
						// F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 ?
				});
				key0836 = genKey(0x10);
				bsofplain.write(key0836);
				
				System.out.println("XX:"+bsofplain.toByteArray().length);
				//pwdkey
				ByteArrayOutputStream bsofpwd = new ByteArrayOutputStream();
				bsofpwd.write(Converts.MD5Encode(password));
				bsofpwd.write(new byte[]{
						0x00, 0x00, 0x00, 0x00
				});
				bsofpwd.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
				pwdkey = Converts.MD5Encode(bsofpwd.toByteArray());
				//System.out.println(Converts.bytesToHexString(pwdkey));
				
				byte[] first = crypter.encrypt(bsofplain.toByteArray(), pwdkey);
				//System.out.println(Converts.bytesToHexString(first));
				System.err.println(first.length);
				//第二段
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[]{
						0x00, 0x15, 0x00, 0x30, 0x00, 0x00 
				});
				bsofplain.write(new byte[]{
						0x01
				});
				crc.reset();
				crc.update(crcs[1]);
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x10
				});
				bsofplain.write(crcs[1]);
				
				bsofplain.write(new byte[]{
						0x02
				});
				crc.reset();
				crc.update(crcs[2]);
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x10
				});
				bsofplain.write(crcs[2]);
				
				System.out.println("YY:"+bsofplain.toByteArray().length);
				byte[] second = crypter.encrypt(bsofplain.toByteArray(), key0836);
				System.err.println(second.length);
				//System.out.println(Converts.bytesToHexString(second));
				//System.out.println(Converts.bytesToHexString(crcs[1]));
				//System.out.println(crc.getValue());
				//byte[] kk = Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase());
				//System.out.println(Converts.bytesToHexString(kk));
				
				//对一、二段和 0x00, 0x18 进行加密
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[]{
						0x01, 0x12,
						0x00, 0x38
				});
				bsofplain.write(token);
				bsofplain.write(new byte[]{
						0x03, 0x0F,
						0x00, 0x11 //是计算机名加上计算机名长度的长度
				});
				bsofplain.write(new byte[]{
						0x00, 0x0F // 长度
				});
				
	//			String kk = genHostName(0x0F);
	//			byte[] xx = kk.getBytes();
				//48 46 5A 4D 43 5A 35 44 4A 50 30 53 4A 46 35 
				bsofplain.write(genHostName(0x0F).getBytes());// 计算机名
				
				bsofplain.write(new byte[]{
						0x00, 0x05, 0x00, 0x06, 0x00, 0x02
				});
				bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x06
				});
				bsofplain.write(new byte[]{
						0x00, (byte)first.length
						//first length
				});
				//System.out.println("V:"+bsofplain.toByteArray().length);
				bsofplain.write(first);		//first
				
				bsofplain.write(new byte[]{
						0x00, 0x1A
				});
				bsofplain.write(new byte[]{
						0x00, (byte)second.length
						//second length
				});
				bsofplain.write(second);		//second
				bsofplain.write(new byte[]{
						0x00, 0x18, 0x00, 0x16, 0x00, 0x01,
						//0x00, 0x00, 0x04, 0x36, 0x06, 0x00, 0x05, 0x11, 0x00, 0x00, 0x01, 0x00 
						0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B //提示重新输入密码？
						//00 00 04 36 00 00 00 01 00 00 14 9B?
				});
				bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x00,  // 记住密码00 01
						0x00, 0x00, 0x01, 0x03, 0x00, 0x14, 0x00, 0x01, // 固定
						0x00, 0x10 //长度
				});
				//bsofplain.write(genKey(0x10));//机器固定验证key
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // 机器固定验证key
				});
				
				//如果是验证码已经识别后的情况，需要加入一段数据
				if(nvc){
					bsofplain.write(new byte[]{
							0x01, 0x10, 0x00, 0x3C, 0x00, 0x01
					});
					bsofplain.write(new byte[]{
							0x00, 0x38
					});
					bsofplain.write(vctoken);
					/////TODO
					/**
					【注意这个位置 如果是有验证码已经识别了验证码，需要在这个地方加上识别验证码返回的token
					01 10 00 3C 00 01
					00 00 // 识别验证码返回token长度
					00 00 00 00 00 ..... // 识别验证码返回token
					】
					**/
				}
				//SP5不需要?
	//			bsofplain.write(new byte[]{
	//					0x00, 0x32, 0x00, 0x37, 0x3E, 0x00, 0x37, 0x01, 0x03, 0x04, 0x02, 0x00, 0x00, 0x04, 0x00	//固定
	//			});
	//			bsofplain.write(new byte[]{
	//					0x22, (byte)0x88, // unknow
	//					(byte)0x00, 0x00, 0x00, 0x00, 0x03, 0x5A, 0x44, 0x22, (byte)0x00, 
	//					(byte)0xFC, (byte)0xCF, 0x68, (byte)0xF5, 0x53, 0x26, 0x3B, (byte)0xB8, (byte)0xE6, 0x61, 0x76, (byte)0xF1, (byte)0x9D, 0x1C, 0x7A, (byte)0xD8,
	//					(byte)0xA9, (byte)0xAE, 0x04, (byte)0xE0, (byte)0xD6, (byte)0xBF, (byte)0xBA, (byte)0x8F, 0x55, (byte)0xF0, 0x36, 0x7A, (byte)0xF7, (byte)0xD6, (byte)0xED, (byte)0x92 // unknow是一个验证，未知算法，可以用随机的，0826包不返回0018的数据，0018的数据也可以直接随机
	//			});
	//			bsofplain.write(new byte[]{
	//					0x68, 0x01, 0x14, 0x00, 0x1D, 0x01, 0x02	//固定
	//			});
	//			bsofplain.write(new byte[]{
	//					0x00, 0x19  // 长度
	//			});
	//			bsofplain.write(ecdhkey);
				bsofplain.write(new byte[]{
						0x01, 0x02, 0x00, 0x62, 0x00, 0x01 //固定
				});
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // 应该是用于出现验证码时计算key，全放0，后面可以直接用0826send key解密
				});
				bsofplain.write(new byte[]{
						0x00, 0x38  // 长度
				});
				bsofplain.write(genKey(0x38));
				bsofplain.write(new byte[]{
						0x00, 0x14  // 长度
				});
				
				crcs[0] = new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00// 应该是用于出现验证码时计算key，全放0，后面可以直接用0826send key解密 
				};
				bsofplain.write(crcs[0]);
				//crc.reset();
				//crc.update(crcs[0]);
				//bsofplain.write(reverse(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()))); // here should reverse
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00
				});
	
				System.out.println("ZZ:"+bsofplain.toByteArray().length);
				//用key0836x 进行加密
				encrypt = crypter.encrypt(bsofplain.toByteArray(), key0836x);
				System.err.println(">>"+encrypt.length);
				//加密完成
				
				System.out.println("XXXXXX");
				System.out.println(Converts.bytesToHexString(key0836));
				System.out.println(Converts.bytesToHexString(key0836x));
				System.out.println(Converts.bytesToHexString(pwdkey));
				
				//整段			
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x08, 0x36
				});
				baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
				baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
				baos.write(new byte[]{
						0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x00, 0x00, 0x00
				});
				baos.write(new byte[]{
						0x00, 0x01, 0x01, 0x02 
				});
				baos.write(new byte[]{
						0x00, 0x19
				});
				baos.write(ecdhkey);
				baos.write(new byte[]{
						0x00, 0x00,
						0x00, 0x10	//长度? //TODO
				});
				baos.write(genKey(0x10));
	//			baos.write(new byte[]{
	//					(byte)0x94, (byte)0xF5, 0x56, (byte)0xD7, (byte)0xDC, (byte)0xE1, (byte)0x84, (byte)0xB8, 0x2F, 0x44, (byte)0x8C, 0x4D, (byte)0xB1, 0x3D, 0x65, (byte)0xB8,
	//					(byte)0x97, (byte)0xC1, 0x39
	//			});
				
				baos.write(encrypt);
				baos.write(new byte[]{
						0x03
				});
				buf = baos.toByteArray();
				
				System.out.println("0836["+Converts.bytesToHexString(key0836x)+"]");
				System.out.println(Converts.bytesToHexString(baos.toByteArray()));			
				
				dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
				ds.send(dpOut);
				
				//IN:
				buffer = new byte[1024];
				dpIn = new DatagramPacket(buffer, buffer.length);
								
	//			byte[] ts = crypter.decrypt(encrypt, rndkey);
	//			System.out.println(Converts.bytesToHexString(ts));
				
				//while(true){
				ds.receive(dpIn);
				//还需判断buffer 121等位置，看是否是转向，也可能是104字节，不需转向
				buffer = pack(buffer);
				System.out.println("TK:"+buffer.length);
				//System.out.println("OUT:"+Converts.bytesToHexString(buffer));
				nvc = false; //清空nvc状态
				
				//175密码错误，871需要验证码
				if(buffer.length==871){
					//00BA 处理验证码
					System.out.println("需要验证码处理");
					nvc = true; //需要再次请求0836
					bsofpng = new ByteArrayOutputStream();
					
					content = slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, key0836x);
					
					//截取png数据，以及相关key, data
					//token = slice(decrypt, 22, 0x38); //new token?
					byte[] ilbs = slice(decrypt, 22+0x38, 2);
					int imglen = ilbs[0]*0x100 + (ilbs[1] & 0xFF);
					
					//byte[] png = slice(decrypt, 24+0x38, imglen);
					bsofpng.write(slice(decrypt, 24+0x38, imglen));
					//System.out.println(Converts.bytesToHexString(png));
					dlvc = (slice(decrypt, 25+0x38+imglen, 1)[0]==1);
					System.out.println("dlvc:"+dlvc);
					System.out.println("imglen:"+imglen);
					
					//no use?
//					byte[] key0836r = slice(decrypt, 25+0x38+imglen, 0x10);
					
					System.out.println(Converts.bytesToHexString(decrypt));
					byte[] tokenfor00ba = slice(decrypt, 28+0x38+imglen, 0x28);
					//System.out.println(Converts.bytesToHexString(tokenfor00ba));
					byte[] keyfor00ba = slice(decrypt, 32+0x38+0x28+imglen, 0x10);
					//System.out.println("keyfor00ba:"+Converts.bytesToHexString(keyfor00ba));
					

//					//data00BA0 = token;
//					data00BA1 = genKey(0x28);
//					data00BA2 = genKey(0x10);
//					
//					byte pidx = 0x00;
					//需要继续下载验证码数据
					boolean rv = false; //request verify
					
					do{
						//00BA: 继续下载验证码
						//-----------------------------------------------------------
						if(!dlvc){
							rv = true; //此次发送验证请求
						}
						
						seq++;
						dlvc = false;
						
						key00BA = genKey(0x10);
//						data00BA = genKey(0x15);
//						data00BA1 = genKey(0x28);
//						data00BA2 = genKey(0x10);
						
						bsofplain = new ByteArrayOutputStream();
						bsofplain.write(new byte[]{
								0x00, 0x02, 0x00, 0x00, 0x08, 0x04, 0x01, (byte)0xE0, 
								0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B
						});
	
						if(!rv){
							bsofplain.write(new byte[]{
									0x00
							});
						}else{
							bsofplain.write(new byte[]{
									0x01
							});
						}
						
						bsofplain.write(new byte[]{
								0x00, 0x38
						});
						bsofplain.write(token);
						
						bsofplain.write(new byte[]{
								0x01, 0x02
						});
						
						bsofplain.write(new byte[]{
								0x00, 0x19
						});
						bsofplain.write(ecdhkey);
						
						if(!rv){ //继续下载验证码
							bsofplain.write(new byte[]{
									0x13, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x01
									
							});
							

							bsofplain.write(new byte[]{
									0x00, 0x28
							});
							bsofplain.write(tokenfor00ba);
							
						}else{ //发送验证请求
							bsofplain.write(new byte[]{
									0x14, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00
							});
							bsofplain.write(new byte[]{
									0x04 // 验证码长度
							});
//							byte[] ccode = new byte[]{
//								0x79, 0x6F, 0x6F, 0x62 
//							};
							bsofplain.write(resultByte);							

							bsofplain.write(new byte[]{
									0x00, 0x38
							});
							//bsofplain.write(genKey(0x38));
							bsofplain.write(tokenfor00ba);
						}
						
						
						bsofplain.write(new byte[]{
								0x00, 0x10
						});
						bsofplain.write(keyfor00ba);				
						
						encrypt = crypter.encrypt(bsofplain.toByteArray(), key00BA);
						
						//total
						baos = new ByteArrayOutputStream();
						baos.write(new byte[]{
								0x02, 0x34, 0x4B, 0x00, (byte)0xBA
						});
						baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
						baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
						baos.write(new byte[]{
								0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x00, 0x00, 0x00
						});
						baos.write(key00BA);
						baos.write(encrypt);
						baos.write(new byte[]{
								0x03
						});
						
						buf = baos.toByteArray();
						
						System.out.println("00BA["+Converts.bytesToHexString(key00BA)+"]");
						System.out.println(Converts.bytesToHexString(baos.toByteArray()));			
						
						dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
						ds.send(dpOut);
						
						//IN:
						buffer = new byte[1024];
						dpIn = new DatagramPacket(buffer, buffer.length);
										
						ds.receive(dpIn);
						
						buffer = pack(buffer);
						System.out.println(buffer.length);
						System.out.println(Converts.bytesToHexString(buffer));
						
						
						if(!rv){
							content = slice(buffer, 14, buffer.length-15);
							decrypt = crypter.decrypt(content, key0836x);//??????!!!!!! 第二次才是key00ba
							System.out.println(Converts.bytesToHexString(decrypt));
							byte[] imglenbs = slice(decrypt, 10+0x38, 2);
							//System.out.println(Converts.bytesToHexString(imglenbs));
							imglen = imglenbs[0]*0x100 + (imglenbs[1] & 0xFF);
							bsofpng.write(slice(decrypt, 12+0x38, imglen));
							//System.out.println(Converts.bytesToHexString(png));
							dlvc = (slice(decrypt, 13+0x38+imglen, 1)[0]==1);
							System.out.println("dlvc:"+dlvc);
							System.out.println("imglen:"+imglen);
							
							//需要更新 tokenfor00ba keyfor00ba ?
							//tokenfor00ba = slice(decrypt, 16+0x38+imglen, 0x28);
							//keyfor00ba = slice(decrypt, 18+0x38+0x28+imglen, 0x10);
							tokenfor00ba = slice(decrypt, 10, 0x38);
							
							byte[] by = bsofpng.toByteArray();
							//resultByte = new byte[30]; // 为识别结果申请内存空间
//							StringBuffer rsb = new StringBuffer(30);
							String rsb = "0000";
							resultByte = rsb.getBytes();

							int codeID = YDM.INSTANCE.YDM_DecodeByBytes(by, by.length, 1004, resultByte);//result byte
//								result = "xxxx";
//								for(int i=0;i<resultByte.length;i++){
//									System.out.println(resultByte[i]);
//								}
							System.out.println("TTT:"+codeID);
							String result = new String(resultByte, "UTF-8").trim();
							
							System.out.println("result:"+resultByte.length+":"+result);
								
						
							/**
							File file = new File("c:/t.png");
							FileOutputStream fileOutputStream  = new FileOutputStream(file);
							//写到文件中
							fileOutputStream.write(bsofpng.toByteArray());
							//reader.close();
							bsofpng.close();
							fileOutputStream.close();
							**/
						}else{
							System.out.println("验证结果:"+buffer.length);
							//其他情况，报告错误							
							//95正常?
							//TODO
							content = slice(buffer, 14, buffer.length-15);
							decrypt = crypter.decrypt(content, key00BA);
							//获取vctoken
							vctoken = slice(decrypt, 10, 0x38); 
							System.out.println("KK1:");
							System.out.println(Converts.bytesToHexString(decrypt));
							System.out.println(Converts.bytesToHexString(vctoken));
						}
//						System.out.println(decrypt.length);
//						System.out.println("XXX");
//						System.out.println(Converts.bytesToHexString(decrypt));
						
					}while(!rv);//提交验证码后，则退出

					//return;
				}else if(buffer.length==175){
					//System.out.println("用户名或密码错误, 退出任务");
					byte[] ts = slice(buffer, 14, buffer.length-15);
					ts = crypter.decrypt(ts, key0836x);
					System.out.println(Converts.bytesToHexString(ts));
					System.out.println(new String(slice(ts, 15, ts.length-15), "utf-8"));
					return;
				}else if(buffer.length==95){					
					byte[] ts = slice(buffer, 14, buffer.length-15);
					ts = crypter.decrypt(ts, key0836x);
					System.out.println(Converts.bytesToHexString(ts));
					System.out.println(new String(slice(ts, 15, ts.length-15), "utf-8"));
					return;
//					System.out.println("LENGTH: 95");
//					content = slice(buffer, 14, buffer.length-15);
//					decrypt = crypter.decrypt(content, key0836x);
//					System.out.println(Converts.bytesToHexString(decrypt));
				}

//				System.out.println(decrypt.length);
//				System.out.println(Converts.bytesToHexString(decrypt));
			}while(nvc);
			
			//正常情况下的 key0836解密
			content = slice(buffer, 14, buffer.length-15);
			decrypt = crypter.decrypt(content, key0836);
			
			System.out.println(Converts.bytesToHexString(decrypt));
			//需解释出某些值供 0828使用
			key0828 = slice(decrypt, 7, 0x10);
			byte[] tokenfor0828 = slice(decrypt, 9+0x10, 0x38);
			String rbof0836 = Converts.bytesToHexString(decrypt);
			key0828recv = slice(decrypt, rbof0836.indexOf("0000003C0002")/2+6, 0x10);
			//00 00 00 3C 00 02
			//Converts.bytesToHexString(decrypt).indexOf(")
			//------------------------------------------------------------------------------
			//0828
			seq++;
			System.out.println("ip="+ip);
			bsofplain = new ByteArrayOutputStream();
			bsofplain.write(new byte[]{
					0x00, 0x07, 0x00, (byte)0x88, 0x00, 0x04
			});
			System.err.println(Converts.bytesToHexString(slice(decrypt, rbof0836.indexOf("00880004")/2+4, 4)));
			System.err.println(Converts.bytesToHexString(slice(decrypt, rbof0836.indexOf("00880004")/2+8, 4)));
			System.err.println(Converts.bytesToHexString(ips));
			bsofplain.write(slice(decrypt, rbof0836.indexOf("00880004")/2+4, 4));
			bsofplain.write(slice(decrypt, rbof0836.indexOf("00880004")/2+8, 4));
//			bsofplain.write(genKey(4));
//			bsofplain.write(genKey(4));
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x00
			});
			bsofplain.write(new byte[]{
					0x00, 0x78
			});
			bsofplain.write(slice(decrypt, rbof0836.indexOf("000000000078")/2+6, 0x78));
			bsofplain.write(new byte[]{
					0x00, 0x0C, 0x00, 0x16, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 //TODO 06->00?
			});
			bsofplain.write(ips);
			bsofplain.write(new byte[]{
					0x1F, 0x40
			});
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x00, 0x00, 0x15, 0x00, 0x30, 0x00, 0x01
			});
			
			/**
			bsofplain.write(new byte[]{
					0x01
			});
			crc.reset();
			crc.update(pwdkey);
			bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x10
			});
			bsofplain.write(pwdkey);
			
			bsofplain.write(new byte[]{
					0x02
			});
			crc.reset();
			crc.update(key0836);
			bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x10
			});
			bsofplain.write(key0836);
			**/
			bsofplain.write(new byte[]{
					0x01
			});
			crc.reset();
			crc.update(crcs[1]);
			bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x10
			});
			bsofplain.write(crcs[1]);
			
			bsofplain.write(new byte[]{
					0x02
			});
			crc.reset();
			crc.update(crcs[2]);
			bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x10
			});
			bsofplain.write(crcs[2]);
			
			bsofplain.write(new byte[]{
					0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00, // 固定
					0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 固定 如果有验证码的包第一位为13
					0x00, 0x18, 0x00, 0x16, 0x00, 0x01, // 固定
					0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B
			});
			bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x00, 0x00, 0x1F, 0x00, 0x22, 0x00, 0x01
			});
			bsofplain.write(genKey(0x20));// 32位机器码
			bsofplain.write(new byte[]{
					0x01, 0x05, 0x00, 0x46, 0x00, 0x01, 0x01, 0x03
					//0x01, 0x05, 0x00, 0x30, 0x00, 0x01, 0x01, 0x02
			});
			bsofplain.write(new byte[]{
					0x00, 0x14, 0x01, 0x01,
					0x00, 0x10
			});
			bsofplain.write(genKey(0x10));
			
			bsofplain.write(new byte[]{
					0x00, 0x14, 0x01, 0x01,
					0x00, 0x10
			});
			bsofplain.write(genKey(0x10));
			
			bsofplain.write(new byte[]{
					0x00, 0x14, 0x01, 0x02,
					0x00, 0x10
			});
			bsofplain.write(genKey(0x10));
			bsofplain.write(new byte[]{
					//0x01, 0x0B, 0x00, 0x38, 0x00, 0x01
					0x01, 0x0B, 0x00, 0x20, 0x00, 0x01 //去掉0x18的情况
			});
			bsofplain.write(new byte[]{
					(byte)0xCF, (byte)0x99, (byte)0xD8, (byte)0xE3, 0x79, (byte)0x95, 0x2A, (byte)0xF1, (byte)0xCA, 0x6D, (byte)0xEC, 0x42, 0x0A, (byte)0xC7, (byte)0xC5, 0x10// QQ file MD5 //TODO
			});			
			//bsofplain.write(genKey(0x10)); 
			bsofplain.write(new byte[]{
					(byte)0xF8, //flag
					0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02 // 固定
			});
			bsofplain.write(new byte[]{
					0x00, 0x00 //去掉0x18的情况 ，原来是 0x00, 0x18
			});
//			bsofplain.write(new byte[]{//genKey(0x18);
//					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
//			}); // 0836 receive token3, 没有收到?
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x2D, 0x00, 0x06, 0x00, 0x01, // 固定
					(byte)0xC0, (byte)0xA8, 0x01, 0x66 // 本地IP
			});
			System.out.println("V:"+Converts.bytesToHexString(bsofplain.toByteArray()));	
			encrypt = crypter.encrypt(bsofplain.toByteArray(), key0828);
			System.out.println(Converts.bytesToHexString(encrypt));		
			
			baos = new ByteArrayOutputStream();
			baos.write(new byte[]{
					0x02, 0x34, 0x4B, 0x08, 0x28
			});
			baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
			baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			baos.write(new byte[]{
					//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
					//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
					0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
			});
			baos.write(new byte[]{
					0x00, 0x38
			});
			baos.write(tokenfor0828);
			baos.write(encrypt);
			baos.write(new byte[]{
					0x03
			});
			
			buf = baos.toByteArray();
			
			System.out.println("0828["+Converts.bytesToHexString(key0828)+"]");
			System.out.println(Converts.bytesToHexString(baos.toByteArray()));			
			
			dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
			ds.send(dpOut);
			
			//IN:
			buffer = new byte[1024];
			dpIn = new DatagramPacket(buffer, buffer.length);
							
			ds.receive(dpIn);
			
			buffer = pack(buffer);
			System.out.println(buffer.length);
			System.out.println(Converts.bytesToHexString(buffer));
			if(buffer.length==127){
				//System.out.println("您的网络环境可能发生了变化，为了您的帐号安全，请重新登录。");
				//System.out.println("退出任务");
				
				byte[] ts = slice(buffer, 14, buffer.length-15);
				ts = crypter.decrypt(ts, key0828);
				System.out.println(Converts.bytesToHexString(ts));
				System.out.println(new String(slice(ts, 15, ts.length-15), "utf-8"));
				return;
			}else{
				System.out.println("OK");
				content = slice(buffer, 14, buffer.length-15);
				System.out.println(Converts.bytesToHexString(key0828recv));
				decrypt = crypter.decrypt(content, key0828recv);
				System.out.println(decrypt.length);
				System.out.println(Converts.bytesToHexString(decrypt));
				
				sessionkey = slice(decrypt, 63, 0x10);				
			}
			
			//00EC 上线包
			//-------------------------------------------------------------------
			seq++;
			
			bsofplain = new ByteArrayOutputStream();
			
			switch(status){
			case 0://在线
				bsofplain.write(new byte[]{
						0x01, 0x00, 0x0A
				});
				break;
			case 1://离开，自动回复
				bsofplain.write(new byte[]{
						0x01, 0x00, 0x1E
				});
				break;
			case 2://忙碌
				bsofplain.write(new byte[]{
						0x01, 0x00, 0x32 
				});
				break;
			case 3://隐身
				bsofplain.write(new byte[]{
						0x01, 0x00, 0x28
				});
				break;
			default:
				bsofplain.write(new byte[]{
						0x01, 0x00, 0x0A //在线;
				});
				break;
			}
			/**
			bsofplain.write(new byte[]{
					0x01, 0x00, 0x0A //在线;
					//0x00, 0x3C //Q我吧
					//0x00, 0x32 //离开
					//	//隐身？
			});
			**/
			encrypt = crypter.encrypt(bsofplain.toByteArray(), sessionkey);
			
			baos = new ByteArrayOutputStream();
			baos.write(new byte[]{
					0x02, 0x34, 0x4B, 0x00, (byte)0xEC
			});
			baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
			baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			baos.write(new byte[]{
					//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
					//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
					//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
					0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x65, (byte)0xCA 
			});
			baos.write(encrypt);
			baos.write(new byte[]{
					0x03
			});
			
			buf = baos.toByteArray();
			
			System.out.println("00EC["+Converts.bytesToHexString(sessionkey)+"]");
			System.out.println(Converts.bytesToHexString(baos.toByteArray()));
			
			dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
			ds.send(dpOut);
			
			//IN:
			buffer = new byte[1024];
			dpIn = new DatagramPacket(buffer, buffer.length);
							
			ds.receive(dpIn);
			
			buffer = pack(buffer);
//			System.out.println(buffer.length);
//			System.out.println(Converts.bytesToHexString(buffer));
			
			//0058 开启线程，发送心跳包
			//--------------------------------------
			final byte[] skx = sessionkey;
			final String ipx = ip;
			final DatagramSocket dsx = ds;
			Timer timer = new Timer();
			timer.schedule(new TimerTask(){
				private Crypter crypter = new Crypter();
				private Random rnd = new Random();
				
				@Override
				public void run() {
					//int seq = 0x3123;
					short seq = (short)rnd.nextInt(0xFFFF);						
					try{
						ByteArrayOutputStream bsofplain = new ByteArrayOutputStream();
						bsofplain.write(account.getBytes());
							
						byte[] encrypt = crypter.encrypt(bsofplain.toByteArray(), skx);
												
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						baos.write(new byte[]{
								0x02, 0x34, 0x4B, 0x00, 0x58
						});
						baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
						baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
						baos.write(new byte[]{
								//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
								//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
								//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
								0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2 
						});
						baos.write(encrypt);
						baos.write(new byte[]{
								0x03
						});
							
						byte[] buf = baos.toByteArray();
							
						System.out.println("0058["+Converts.bytesToHexString(skx)+"]");
						System.out.println(Converts.bytesToHexString(baos.toByteArray()));
							
						DatagramPacket dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ipx), 8000);
						dsx.send(dpOut);
							
						//not need
						//IN:
						/**
						byte[] buffer = new byte[1024];
						DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);
											
						dsx.receive(dpIn);
							
						buffer = pack(buffer);
						System.out.println(buffer.length);
						System.out.println(Converts.bytesToHexString(buffer));
						**/
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			}, 1000, 1000*60);
			
			while(true){
				//00CE接收消息
				//--------------------------------------------------
				buffer = new byte[1024];
				dpIn = new DatagramPacket(buffer, buffer.length);
				ds.receive(dpIn);
				buffer = pack(buffer);
//				System.out.println("P1:"+buffer.length);
//				System.out.println(Converts.bytesToHexString(buffer));
				
				byte[] header = slice(buffer, 3, 2);
				byte[] rh = slice(buffer, 0, 11);
				System.out.println("RECV[+"+buffer.length+"]:"+header[0]+"|"+header[1]);
				
				if(header[0]==0x00&&header[1]==(byte)0xCE){
					content = slice(buffer, 14, buffer.length-15);
					decrypt = crypter.decrypt(content, sessionkey);
					System.out.println("00CE[RECV]:"+decrypt.length);
					System.out.println(Converts.bytesToHexString(decrypt));
					
					//00CE的反馈
					bsofplain = new ByteArrayOutputStream();
					bsofplain.write(slice(decrypt, 0, 0x010));
					encrypt = crypter.encrypt(bsofplain.toByteArray(), sessionkey);
										
					baos = new ByteArrayOutputStream();
					baos.write(rh);
					baos.write(new byte[]{
							//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
							//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
							//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
							0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2							
							//02 00 00 00 01 							01 01 00 00 66 79?  0x65, (byte)0xCA	
					});
					baos.write(encrypt);
					baos.write(new byte[]{
							0x03
					});
					
					buf = baos.toByteArray();
					
					System.out.println("00CE[SEND]["+Converts.bytesToHexString(sessionkey)+"]");
					System.out.println(Converts.bytesToHexString(baos.toByteArray()));
					
					dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
					ds.send(dpOut);
					
					//0319 ? 01C0?
					//表示已经读取本消息 //TODO
					//----------------------------------------------
					
					//00CD回复消息
					//----------------------------------------------
					if(status==1){ //需要自动回复
						seq++;
						
						byte[] time = Converts.hexStringToByte(Long.toHexString(System.currentTimeMillis()/1000).toUpperCase());
						byte[] msgs = message.getBytes("UTF-8");
						
						bsofplain = new ByteArrayOutputStream();
						bsofplain.write(slice(decrypt, 4, 0x04));
						bsofplain.write(slice(decrypt, 0, 0x04));
						bsofplain.write(new byte[]{			
								0x00, 0x00, 0x00, 0x0D, 0x00, 0x01, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x01,
								0x34, 0x4B
						});
						bsofplain.write(slice(decrypt, 4, 0x04));
						bsofplain.write(slice(decrypt, 0, 0x04));
	
						byte[] tn = slice(decrypt, 0, 0x04);
						byte[] msghd = new byte[20];
						for(int i=0;i<4;i++){ 
							msghd[i] = tn[i];
						}
						for(int i=0;i<sessionkey.length;i++){
							msghd[i+4] = sessionkey[i];
						}
						bsofplain.write(Converts.MD5Encode(msghd));
						bsofplain.write(new byte[]{
								0x00, 0x0B
						});
						bsofplain.write(Converts.hexStringToByte(Integer.toHexString(seq+0x1111).toUpperCase()));
											bsofplain.write(time);
						bsofplain.write(new byte[]{
								//0x52,(byte)0xB9,0x0E,(byte)0x94,  //时间
								0x02,0x37,  //图标？
								0x00,0x00,0x00,
								0x00,  //是否有字体
								0x01,  //分片数
								0x00,  //分片索引
								0x00,0x00, //消息ID
								0x02,  //02,0xauto,0xreply,,0x01,0xnormal
								0x4D,0x53,0x47,0x00,0x00,0x00,0x00,0x00,  //fix
						});
						bsofplain.write(time);
								//0x52,(byte)0xB9,0x0E,(byte)0x94,  //发送时间
								//(byte)0xBE,(byte)0x97,0x4A,(byte)0x92,  //随机？
						bsofplain.write(genKey(4));
						bsofplain.write(new byte[]{		
								0x00,
								0x00,0x00,0x00,  //字体颜色
								0x0A,  //字体大小
								0x00,  //其他属性
								(byte)0x86,0x02,  //字符集
								0x00,0x06,  //字体名称长度
								(byte)0xE5,(byte)0xAE,(byte)0x8B,(byte)0xE4,(byte)0xBD,(byte)0x93,  //宋体
								0x00,0x00,0x01, 
								0x00,(byte)(msgs.length+3),  //消息快总长度
								0x01,  //消息快编号
						});
						bsofplain.write(new byte[]{	
								0x00,(byte)msgs.length,  //消息快大小
								//0x61,0x73,0x64,0x66  //消息
								//(byte)0xE4,(byte)0xBD,(byte)0xA0,(byte)0xE5,(byte)0xA5,(byte)0xBD,(byte)0xEF,(byte)0xBC,(byte)0x8C,(byte)0xE6,(byte)0x88,(byte)0x91,(byte)0xE7,(byte)0x8E,(byte)0xB0,(byte)0xE5,(byte)0x9C,(byte)0xA8,(byte)0xE4,(byte)0xB8,(byte)0x8D,(byte)0xE5,(byte)0x9C,(byte)0xA8,0x21
						});
						bsofplain.write(msgs);
						
						encrypt = crypter.encrypt(bsofplain.toByteArray(), sessionkey);
											
						baos = new ByteArrayOutputStream();
						baos.write(new byte[]{
								0x02, 0x34, 0x4B, 0x00, (byte)0xCD
						});
						baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
						baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
						baos.write(new byte[]{
								//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
								//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
								//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
								0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2 //0x65, (byte)0xCA 
						});
						baos.write(encrypt);
						baos.write(new byte[]{
								0x03
						});
						
						buf = baos.toByteArray();
						
						System.out.println("00CD["+Converts.bytesToHexString(sessionkey)+"]");
						System.out.println(Converts.bytesToHexString(baos.toByteArray()));
						
						dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
						ds.send(dpOut);
						
						//IN: not need
						/**
						buffer = new byte[1024];
						dpIn = new DatagramPacket(buffer, buffer.length);
										
						ds.receive(dpIn);
						
						buffer = pack(buffer);
						System.out.println(buffer.length);
						System.out.println(Converts.bytesToHexString(buffer));
						**/
					}else { //命令离线 //decrypt判断
						//离线的处理
						seq++;
						
						bsofplain.write(new byte[]{			
								0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
						});
						
						encrypt = crypter.encrypt(bsofplain.toByteArray(), sessionkey);
											
						baos = new ByteArrayOutputStream();
						baos.write(new byte[]{
								0x02, 0x34, 0x4B, 0x00, 0x62
						});
						baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
						baos.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
						baos.write(new byte[]{
								//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
								//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
								//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
								0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2 //0x65, (byte)0xCA								
						});
						baos.write(encrypt);
						baos.write(new byte[]{
								0x03
						});
						
						buf = baos.toByteArray();
						
						System.out.println("0062["+Converts.bytesToHexString(sessionkey)+"]");
						System.out.println(Converts.bytesToHexString(baos.toByteArray()));
						
						dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), 8000);
						ds.send(dpOut);
						
						timer.cancel();
						return;
						//IN: not need
						/**
						buffer = new byte[1024];
						dpIn = new DatagramPacket(buffer, buffer.length);
										
						ds.receive(dpIn);
						
						buffer = pack(buffer);
						System.out.println(buffer.length);
						System.out.println(Converts.bytesToHexString(buffer));
						**/						
					}
				}
				//消息处理 0319 已读
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ds.close();
		}
	}
	
	public static byte[] genKey(int length){
		byte[] rs = new byte[length];
		Random rnd = new Random();
		rnd.nextBytes(rs);
		return rs;
	}
	
	public static byte[] slice(byte[] src, int start, int length) {
        byte[] bs = new byte[length];
        for (int i=0; i<length; i++){
            bs[i] = src[start+i];	
        }
        return bs;
	}
	
	public static byte[] pack(byte[] src){
		int index = -1;
		for(int i=src.length;i>0;i--){
			if(src[i-1]!=0x00){
				index = i-1;
				break;
			}
		}
		byte[] rs = new byte[index+1];
		for(int i=0;i<rs.length;i++){
			rs[i] = src[i];
		}
		return rs;
	}
	
	public static byte[] reverse(byte[] src){
		byte[] rs = new byte[src.length];
		for(int i=0;i<rs.length;i++){
			rs[i] = src[src.length-1-i];
		}
		return rs;
	}
	
	public static String genHostName(int length){
		String cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<length;i++){
			sb.append(cs.charAt(rnd.nextInt(cs.length())));
		}
		return sb.toString();
	}
}
