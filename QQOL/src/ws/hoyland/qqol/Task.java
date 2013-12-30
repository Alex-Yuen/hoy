package ws.hoyland.qqol;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Map;
import java.util.zip.CRC32;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.math.ec.ECPoint;

import sun.security.ec.NamedCurve;
import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;
import ws.hoyland.util.DM;
import ws.hoyland.util.YDM;

public class Task implements Runnable {
	public final static byte TYPE_0825 = 0x01;
	public final static byte TYPE_0836 = 0x02;
	public final static byte TYPE_00BA = 0x03;
	public final static byte TYPE_ERR_VC = 0x04;
	public final static byte TYPE_0828 = 0x05;
	public final static byte TYPE_00EC = 0x06;
	public final static byte TYPE_005C = 0x07;
	public final static byte TYPE_00CE = 0x08;
	public final static byte TYPE_00CD = 0x09;
	public final static byte TYPE_0017 = 0x10;

	private byte type;
	private String ip = "183.60.19.100";// 默认IP
	private byte[] ips = new byte[] { (byte) 183, (byte) 60, (byte) 19,
			(byte) 100 };

	//private byte[] buffer;

	private ByteArrayOutputStream baos = null;
	private ByteArrayOutputStream bsofplain = null;
	private Crypter crypter = new Crypter();
	
	private int id = 0;
	private String account = null;
	private String password = null;
	private int status = 1;
	private byte[] seq = null;
	private byte[] encrypt = null;
	private Map<String, byte[]> details = null;
	
	public Task(byte type, String account) {
		this.type = type;
		this.account = account;
		
		this.details = Engine.getInstance().getAcccounts().get(account);
		
		this.id = Integer.parseInt(new String(details.get("id")));
		this.password = new String(details.get("password"));
		this.status = Integer.parseInt(Configuration.getInstance().getProperty("LOGIN_TYPE"));
		
//		if(details.get("seq")!=null){
//			this.seq = details.get("seq");
//			Integer ss = (int)Short.parseShort(Converts.bytesToHexString(this.seq), 16);
//			ss++;
//			this.seq = Converts.hexStringToByte(Integer.toHexString((short)ss.intValue()));
//		}else{
//			this.seq = new byte[]{0x11, 0x23};
//		}
//		details.put("seq", this.seq);
		this.seq = Util.genKey(2);
		
		if(details.get("ips")!=null){//需要新的IP
			this.ips = details.get("ips");
			this.ip = (ips[0]&0xFF)+"."+(ips[1]&0xFF)+"."+(ips[2]&0xFF)+"."+(ips[3]&0xFF);
		}
	}
	
	@Override
	public void run() {
		// 发送UDP数据
		switch (type) {
		case TYPE_0825:
			try {
				byte[] serverPBK = new byte[] { 0x04, (byte) 0x92, (byte) 0x8D,
						(byte) 0x88, 0x50, 0x67, 0x30, (byte) 0x88,
						(byte) 0xB3, 0x43, 0x26, 0x4E, 0x0C, 0x6B, (byte) 0xAC,
						(byte) 0xB8, 0x49, 0x6D, 0x69, 0x77, (byte) 0x99,
						(byte) 0xF3, 0x72, 0x11, (byte) 0xDE, (byte) 0xB2,
						0x5B, (byte) 0xB7, 0x39, 0x06, (byte) 0xCB, 0x08,
						(byte) 0x9F, (byte) 0xEA, (byte) 0x96, 0x39,
						(byte) 0xB4, (byte) 0xE0, 0x26, 0x04, (byte) 0x98,
						(byte) 0xB5, 0x1A, (byte) 0x99, 0x2D, 0x50,
						(byte) 0x81, (byte) 0x3D, (byte) 0xA8 };
				byte[] key0825 = Util.genKey(0x10);
				
				// ecdhkey 随机生成的公钥，然后key0836x 即为共享秘密，由算法计算得出

				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

				ECParameterSpec ecSpec = NamedCurve
						.getECParameterSpec("secp192k1");

				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH",
						"BC");
				keyGen.initialize(ecSpec, new SecureRandom()); // 公私钥 工厂
				KeyPair pair = keyGen.generateKeyPair(); // 生成公私钥
				BCECPublicKey cpk = (BCECPublicKey) pair.getPublic();
				ECPoint.Fp point = (ECPoint.Fp) cpk.getQ();

				byte[] ecdhkey = point.getEncoded(true); // ecdhkey
				// System.out.println(ecdhkey.length);

				java.security.spec.ECPoint sp = ECPointUtil.decodePoint(
						ecSpec.getCurve(), serverPBK);
				KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
				ECPublicKeySpec pubSpec = new ECPublicKeySpec(sp, ecSpec);
				ECPublicKey myECPublicKey = (ECPublicKey) kf
						.generatePublic(pubSpec);

				// System.out.println(((BCECPublicKey)myECPublicKey).getQ().getEncoded().length);

				KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
				agreement.init(pair.getPrivate());
				agreement.doPhase(myECPublicKey, true);
				//
				byte[] xx = agreement.generateSecret();
				byte[] key0836x = new byte[16]; // key0836x
				for (int i = 0; i < key0836x.length; i++) {
					key0836x[i] = xx[i];
				}

				baos = new ByteArrayOutputStream();
				baos.write(new byte[] { 0x02, 0x34, 0x4B, 0x08, 0x25 });
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(
						Long.valueOf(account)).toUpperCase()));
				baos.write(new byte[] { 0x03, 0x00, 0x00, 0x00, 0x01, 0x01,
						0x01, 0x00, 0x00, 0x66, (byte) 0xA2, 0x00, 0x00, 0x00,
						0x00 });
				baos.write(key0825);
				// System.err.println(Converts.bytesToHexString(rndkey));
				// System.out.println(Converts.bytesToHexString(baos.toByteArray()));
				// 以下需要加密
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[] { 0x00, 0x18, 0x00, 0x16, 0x00,
						0x01, 0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01,
						0x00, 0x00, 0x14, (byte) 0x9B });
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(
						Long.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x03,
						0x09, 0x00, 0x08, 0x00, 0x01 });
				bsofplain.write(ips);
				// bsofplain.write(new byte[]{
				// 0x00, 0x00, 0x00, 0x00
				// });
				bsofplain.write(new byte[] {
						// 0x00, 0x04, 0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00,
						// 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
						0x00, 0x02, 0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00,
						0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x14, 0x00,
						0x1D, 0x01, 0x02 });
				bsofplain.write(new byte[] { 0x00, 0x19 });
				bsofplain.write(ecdhkey);
				// System.out.println(Converts.bytesToHexString(bsofplain.toByteArray()));
				encrypt = crypter.encrypt(bsofplain.toByteArray(), key0825);
				// 加密完成
				// byte[] ts = crypter.decrypt(encrypt, rndkey);
				// System.out.println(Converts.bytesToHexString(ts));
				baos.write(encrypt);
				baos.write(new byte[] { 0x03 });
				//buffer = baos.toByteArray();
				
				this.details.put("key0825", key0825);
				this.details.put("ecdhkey", ecdhkey);
				this.details.put("key0836x", key0836x);
				
				//Engine.getInstance().getAcccounts().put(account, this.details);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_0836:
			try{
				byte[][] crcs = new byte[][]{Util.genKey(16), Util.genKey(16), Util.genKey(16)};
				CRC32 crc = new CRC32();
				
				//第一段
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(Util.genKey(4));
				bsofplain.write(new byte[]{
						0x00, 0x01 
				});
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						//0x00, 0x00, 0x04, 0x36, 0x06, 0x00, 0x05, 0x11, 0x00, 0x00, 0x01, 0x00,
						0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B,
						0x00, 0x00, 0x01 // 不记住密码
				});
				bsofplain.write(Converts.MD5Encode(password));
				bsofplain.write(details.get("logintime"));
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
				});
				bsofplain.write(details.get("loginip"));
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0B, 0x00,
						0x00, 0x10,
						(byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0
						//(byte)0xE3, 0x04, 0x2B, (byte)0xB2, (byte)0xF7, (byte)0xEA, 0x62, 0x40, (byte)0x99, 0x62, (byte)0x81, 0x11, 0x44, 0x52, 0x17, 0x22
						// F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 ?
				});
				byte[] key0836 = Util.genKey(0x10);
				bsofplain.write(key0836);
				
				//System.out.println("XX:"+bsofplain.toByteArray().length);
				//pwdkey
				ByteArrayOutputStream bsofpwd = new ByteArrayOutputStream();
				bsofpwd.write(Converts.MD5Encode(password));
				bsofpwd.write(new byte[]{
						0x00, 0x00, 0x00, 0x00
				});
				bsofpwd.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				byte[] pwdkey = Converts.MD5Encode(bsofpwd.toByteArray());
				//System.out.println(Converts.bytesToHexString(pwdkey));
				
				byte[] first = crypter.encrypt(bsofplain.toByteArray(), pwdkey);
				//System.out.println(Converts.bytesToHexString(first));
				//System.err.println(first.length);
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
				
				//System.out.println("YY:"+bsofplain.toByteArray().length);
				byte[] second = crypter.encrypt(bsofplain.toByteArray(), key0836);
				//System.err.println(second.length);
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
				bsofplain.write(details.get("token"));
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
				bsofplain.write(Util.genHostName(0x0F).getBytes());// 计算机名
				
				bsofplain.write(new byte[]{
						0x00, 0x05, 0x00, 0x06, 0x00, 0x02
				});
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
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
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x00,  // 记住密码00 01
						0x00, 0x00, 0x01, 0x03, 0x00, 0x14, 0x00, 0x01, // 固定
						0x00, 0x10 //长度
				});
				//bsofplain.write(Util.genKey(0x10));//机器固定验证key
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // 机器固定验证key
				});
				
				//如果是验证码已经识别后的情况，需要加入一段数据
				if(details.get("vctoken")!=null){
					bsofplain.write(new byte[]{
							0x01, 0x10, 0x00, 0x3C, 0x00, 0x01
					});
					bsofplain.write(new byte[]{
							0x00, 0x38
					});
					bsofplain.write(details.get("vctoken"));
					/////
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
				bsofplain.write(Util.genKey(0x38));
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
				encrypt = crypter.encrypt(bsofplain.toByteArray(), details.get("key0836x"));
				System.err.println(">>"+encrypt.length);
				//加密完成
				
//				System.out.println("XXXXXX");
//				System.out.println(Converts.bytesToHexString(key0836));
//				System.out.println(Converts.bytesToHexString(key0836x));
//				System.out.println(Converts.bytesToHexString(pwdkey));
				
				//整段			
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x08, 0x36
				});
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				baos.write(new byte[]{
						0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x00, 0x00, 0x00
				});
				baos.write(new byte[]{
						0x00, 0x01, 0x01, 0x02 
				});
				baos.write(new byte[]{
						0x00, 0x19
				});
				baos.write(details.get("ecdhkey"));
				baos.write(new byte[]{
						0x00, 0x00,
						0x00, 0x10	//长度? 
				});
				baos.write(Util.genKey(0x10));
	//			baos.write(new byte[]{
	//					(byte)0x94, (byte)0xF5, 0x56, (byte)0xD7, (byte)0xDC, (byte)0xE1, (byte)0x84, (byte)0xB8, 0x2F, 0x44, (byte)0x8C, 0x4D, (byte)0xB1, 0x3D, 0x65, (byte)0xB8,
	//					(byte)0x97, (byte)0xC1, 0x39
	//			});
				
				baos.write(encrypt);
				baos.write(new byte[]{
						0x03
				});

				this.details.remove("vctoken");
				this.details.put("key0836", key0836);
				this.details.put("crc1", crcs[1]);
				this.details.put("crc2", crcs[2]);
			}catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_00BA:
			try{
				if(details.get("dlvc")==null){ //识别验证码
					ByteArrayOutputStream bsofpng = new ByteArrayOutputStream();
					bsofpng.write(details.get("pngfirst"));
					bsofpng.write(details.get("pngsecond"));
					//resultByte = new byte[30]; // 为识别结果申请内存空间
//					StringBuffer rsb = new StringBuffer(30);
					
					byte[] by = bsofpng.toByteArray();
					String rsb = "0000";
					byte[] resultByte = rsb.getBytes();

//					info("识别验证码");
					int codeID = 0;
					if(Engine.getInstance().getCptType()==0){
						codeID = YDM.INSTANCE.YDM_DecodeByBytes(by, by.length, 1004, resultByte);//result byte
					}else{
						codeID = DM.INSTANCE.uu_recognizeByCodeTypeAndBytesA(by,
								by.length, 1, resultByte); // 调用识别函数,resultBtye为识别结果
					}
					String result = new String(resultByte, "UTF-8").trim();					
					System.out.println("result:"+resultByte.length+":"+result);
					
					details.remove("pngfirst");
					details.remove("pngsecond");
					details.put("codeID", String.valueOf(codeID).getBytes());
				}
				
				byte[] key00BA = Util.genKey(0x10);
				
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[]{
						0x00, 0x02, 0x00, 0x00, 0x08, 0x04, 0x01, (byte)0xE0, 
						0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B
				});

				if(details.get("dlvc")!=null){
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
				bsofplain.write(details.get("token"));
				
				bsofplain.write(new byte[]{
						0x01, 0x02
				});
				
				bsofplain.write(new byte[]{
						0x00, 0x19
				});
				bsofplain.write(details.get("ecdhkey"));
				
				if(details.get("dlvc")!=null){ //继续下载验证码
					bsofplain.write(new byte[]{
							0x13, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x01
							
					});					

					bsofplain.write(new byte[]{
							0x00, 0x28
					});
					bsofplain.write(details.get("tokenfor00ba"));
					
				}else{ //发送验证请求
					bsofplain.write(new byte[]{
							0x14, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00
					});
					bsofplain.write(new byte[]{
							0x04 // 验证码长度
					});
//					byte[] ccode = new byte[]{
//						0x79, 0x6F, 0x6F, 0x62 
//					};
					bsofplain.write(details.get("resultByte"));							

					bsofplain.write(new byte[]{
							0x00, 0x38
					});
					//bsofplain.write(Util.genKey(0x38));
					bsofplain.write(details.get("tokenfor00ba"));
				}
				
				
				bsofplain.write(new byte[]{
						0x00, 0x10
				});
				bsofplain.write(details.get("keyfor00ba"));				
				
				encrypt = crypter.encrypt(bsofplain.toByteArray(), key00BA);
				
				//total
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x00, (byte)0xBA
				});
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				baos.write(new byte[]{
						0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x00, 0x00, 0x00
				});
				baos.write(key00BA);
				baos.write(encrypt);
				baos.write(new byte[]{
						0x03
				});
				
				//this.details.remove("dlvc");
				this.details.put("key00BA", key00BA);
			}catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_ERR_VC:
			try {
				//
				int reportErrorResult = -1;
				int codeID = Integer.parseInt(new String(details.get("codeID")));
				if(Engine.getInstance().getCptType()==0){
					reportErrorResult = YDM.INSTANCE.YDM_Report(codeID, false);
				}else{
					reportErrorResult = DM.INSTANCE.uu_reportError(codeID);
				}
				System.err.println(reportErrorResult);
				
				details.remove("codeID");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_0828:
			try{
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[]{
						0x00, 0x07, 0x00, (byte)0x88, 0x00, 0x04
				});
//				System.err.println(Converts.bytesToHexString(Util.slice(decrypt, rbof0836.indexOf("00880004")/2+4, 4)));
//				System.err.println(Converts.bytesToHexString(Util.slice(decrypt, rbof0836.indexOf("00880004")/2+8, 4)));
//				System.err.println(Converts.bytesToHexString(ips));
				bsofplain.write(details.get("logintime"));
				bsofplain.write(details.get("loginip"));
//				bsofplain.write(Util.genKey(4));
//				bsofplain.write(Util.genKey(4));
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00
				});
				bsofplain.write(new byte[]{
						0x00, 0x78
				});
				bsofplain.write(details.get("tokenat0078"));
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
				CRC32 crc = new CRC32();
				crc.reset();
				crc.update(details.get("crc1"));
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x10
				});
				bsofplain.write(details.get("crc1"));
				
				bsofplain.write(new byte[]{
						0x02
				});
				crc.reset();
				crc.update(details.get("crc2"));
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x10
				});
				bsofplain.write(details.get("crc2"));
				
				bsofplain.write(new byte[]{
						0x00, 0x36, 0x00, 0x12, 0x00, 0x02, 0x00, 0x01, 0x00, 0x00, 0x00, // 固定
						0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // 固定 如果有验证码的包第一位为13
						0x00, 0x18, 0x00, 0x16, 0x00, 0x01, // 固定
						0x00, 0x00, 0x04, 0x36, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x14, (byte)0x9B
				});
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x00, 0x00, 0x1F, 0x00, 0x22, 0x00, 0x01
				});
				bsofplain.write(Util.genKey(0x20));// 32位机器码
				bsofplain.write(new byte[]{
						0x01, 0x05, 0x00, 0x46, 0x00, 0x01, 0x01, 0x03
						//0x01, 0x05, 0x00, 0x30, 0x00, 0x01, 0x01, 0x02
				});
				bsofplain.write(new byte[]{
						0x00, 0x14, 0x01, 0x01,
						0x00, 0x10
				});
				bsofplain.write(Util.genKey(0x10));
				
				bsofplain.write(new byte[]{
						0x00, 0x14, 0x01, 0x01,
						0x00, 0x10
				});
				bsofplain.write(Util.genKey(0x10));
				
				bsofplain.write(new byte[]{
						0x00, 0x14, 0x01, 0x02,
						0x00, 0x10
				});
				bsofplain.write(Util.genKey(0x10));
				bsofplain.write(new byte[]{
						//0x01, 0x0B, 0x00, 0x38, 0x00, 0x01
						0x01, 0x0B, 0x00, 0x20, 0x00, 0x01 //去掉0x18的情况
				});
				bsofplain.write(new byte[]{
						(byte)0xCF, (byte)0x99, (byte)0xD8, (byte)0xE3, 0x79, (byte)0x95, 0x2A, (byte)0xF1, (byte)0xCA, 0x6D, (byte)0xEC, 0x42, 0x0A, (byte)0xC7, (byte)0xC5, 0x10// QQ file MD5 //TODO
				});			
				//bsofplain.write(Util.genKey(0x10)); 
				bsofplain.write(new byte[]{
						(byte)0xF8, //flag
						0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02 // 固定
				});
				bsofplain.write(new byte[]{
						0x00, 0x00 //去掉0x18的情况 ，原来是 0x00, 0x18
				});
//				bsofplain.write(new byte[]{//Util.genKey(0x18);
//						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//						0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
//				}); // 0836 receive token3, 没有收到?
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x2D, 0x00, 0x06, 0x00, 0x01, // 固定
						(byte)0xC0, (byte)0xA8, 0x01, 0x66 // 本地IP
				});
				//System.out.println("V:"+Converts.bytesToHexString(bsofplain.toByteArray()));	
				encrypt = crypter.encrypt(bsofplain.toByteArray(), details.get("key0828"));
				//System.out.println(Converts.bytesToHexString(encrypt));		
				
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x08, 0x28
				});
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				baos.write(new byte[]{
						//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
						//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
						0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
				});
				baos.write(new byte[]{
						0x00, 0x38
				});
				baos.write(details.get("tokenfor0828"));
				baos.write(encrypt);
				baos.write(new byte[]{
						0x03
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_00EC:
			try{
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
				encrypt = crypter.encrypt(bsofplain.toByteArray(), details.get("sessionkey"));
				
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x00, (byte)0xEC
				});
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_005C:
			try{
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(new byte[]{
						(byte)0x88
				});
				bsofplain.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				bsofplain.write(new byte[]{
						0x00
				});
				
				encrypt = crypter.encrypt(bsofplain.toByteArray(), details.get("sessionkey"));
				
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x00, (byte)0x5C
				});
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TYPE_00CE:
			try{
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(details.get("rc"));
				encrypt = crypter.encrypt(bsofplain.toByteArray(), details.get("sessionkey"));
									
				baos = new ByteArrayOutputStream();
				baos.write(details.get("rh"));
				baos.write(new byte[]{0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2});
				baos.write(encrypt);
				baos.write(new byte[]{0x03});
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		case TYPE_00CD:
			try{
				byte[] time = Converts.hexStringToByte(Long.toHexString(System.currentTimeMillis()/1000).toUpperCase());
				byte[] msgs = Configuration.getInstance().getProperty("LEFT_MSG").getBytes();
				
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(details.get("00CDA"));
				bsofplain.write(details.get("00CDB"));
				bsofplain.write(new byte[]{
						0x00, 0x00, 0x00, 0x0D, 0x00, 0x01, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x01,
						0x34, 0x4B
				});
				bsofplain.write(details.get("00CDA"));
				bsofplain.write(details.get("00CDB"));

				byte[] tn = details.get("00CDA");
				byte[] msghd = new byte[20];
				for(int i=0;i<4;i++){ 
					msghd[i] = tn[i];
				}
				for(int i=0;i<details.get("sessionkey").length;i++){
					msghd[i+4] = details.get("sessionkey")[i];
				}
				bsofplain.write(Converts.MD5Encode(msghd));
				bsofplain.write(new byte[]{
						0x00, 0x0B
				});
				bsofplain.write(Util.genKey(2)); //seq + 0x1111
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
				bsofplain.write(Util.genKey(4));
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
						0x00,(byte)msgs.length,  //消息快大小
				});
//				bsofplain.write(new byte[]{	
//						0x00,(byte)msgs.length,  //消息快大小
//						//0x61,0x73,0x64,0x66  //消息
//						//(byte)0xE4,(byte)0xBD,(byte)0xA0,(byte)0xE5,(byte)0xA5,(byte)0xBD,(byte)0xEF,(byte)0xBC,(byte)0x8C,(byte)0xE6,(byte)0x88,(byte)0x91,(byte)0xE7,(byte)0x8E,(byte)0xB0,(byte)0xE5,(byte)0x9C,(byte)0xA8,(byte)0xE4,(byte)0xB8,(byte)0x8D,(byte)0xE5,(byte)0x9C,(byte)0xA8,0x21
//				});
				bsofplain.write(msgs);
				
				encrypt = crypter.encrypt(bsofplain.toByteArray(), details.get("sessionkey"));
									
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x00, (byte)0xCD
				});
				baos.write(seq);
				baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(account)).toUpperCase()));
				baos.write(new byte[]{0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2});
				baos.write(encrypt);
				baos.write(new byte[]{0x03});
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		try {
			DatagramChannel dc = Engine.getInstance().getChannels().get(this.account);
			if(dc==null){
				SocketAddress sa = new InetSocketAddress(ip, 8000);
				dc = DatagramChannel.open();
				dc.configureBlocking(false);
				dc.connect(sa);
				
				Monitor.getInstance().setWakeup(true);
				QQSelector.selector.wakeup();
				dc.register(QQSelector.selector, SelectionKey.OP_READ);
				Monitor.getInstance().setWakeup(false);
				
				Engine.getInstance().getChannels().put(this.account, dc);
			}
						
			System.out.println("SEND:");
			System.out.println(Converts.bytesToHexString(baos.toByteArray()));
			dc.write(ByteBuffer.wrap(baos.toByteArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
