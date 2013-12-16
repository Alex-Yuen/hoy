package ws.hoyland.qqonline;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;
import java.util.zip.CRC32;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class T2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String account = "287705149";
		String password = "ghsjiygrtr";
		String ip = "183.60.19.100";//默认IP
		byte[] ips = new byte[]{
				(byte)183, (byte)60, (byte)19, (byte)100
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
		
		byte[] key0825 = null;
		byte[] ecdhkey = null;
		byte[] token = null;
		byte[] key0836 = null;
		byte[] key0836x = null;
		byte[] pwdkey = null;
		
		Crypter crypter = new Crypter();
		byte[] encrypt = null;
		byte[] decrypt = null;

		CRC32 crc = new CRC32();
		
		boolean redirect = false;
		
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
				
				//根据随机的内置KEY, 生成ECDH KEY
				//ecdhkey = genKey(0x19);
				{
					key0836x = genKey(0x10);
					ecdhkey = null;
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
				bsofplain.write(new byte[]{
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
				System.out.println("["+Converts.bytesToHexString(key0825)+"]");
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
			seq++;
			
			//第一段
			bsofplain = new ByteArrayOutputStream();
			bsofplain.write(genKey(4));
			bsofplain.write(new byte[]{
					0x00, 0x01 
			});
			bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x04, 0x36, 0x06, 0x00, 0x05, 0x11, 0x00, 0x00, 0x01, 0x00,
					0x00, 0x00, 0x00 // 不记住密码
			});
			bsofplain.write(Converts.MD5Encode(password));
			bsofplain.write(logintime);
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
			});
			bsofplain.write(loginip);
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0B,
					0x00, 0x10,
					(byte)0xE3, 0x04, 0x2B, (byte)0xB2, (byte)0xF7, (byte)0xEA, 0x62, 0x40, (byte)0x99, 0x62, (byte)0x81, 0x11, 0x44, 0x52, 0x17, 0x22
					// F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 F0 ?
			});
			key0836 = genKey(0x10);
			bsofplain.write(key0836);
			
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
			byte[][] crcs = new byte[][]{genKey(16), genKey(16), genKey(16)};
			bsofplain.write(new byte[]{
					0x01
			});
			crc.update(crcs[1]);
			bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x10
			});
			bsofplain.write(crcs[1]);
			
			bsofplain.write(new byte[]{
					0x02
			});
			crc.update(crcs[2]);
			bsofplain.write(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x10
			});
			bsofplain.write(crcs[2]);
			
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
					0x00, 0x11
			});
			//00 0F // 长度
			//48 46 5A 4D 43 5A 35 44 4A 50 30 53 4A 46 35 // 计算机名
			
			bsofplain.write(new byte[]{
					0x00, 0x05, 0x00, 0x06, 0x00, 0x02
			});
			bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			bsofplain.write(new byte[]{
					0x06
			});
			bsofplain.write(new byte[]{
					(byte)first.length
					//first length
			});
			bsofplain.write(first);		//first
			
			bsofplain.write(new byte[]{
					0x1A
			});
			bsofplain.write(new byte[]{
					(byte)second.length
					//second length
			});
			bsofplain.write(second);		//second
			bsofplain.write(new byte[]{
					0x00, 0x18, 0x00, 0x16, 0x00, 0x01,
					0x00, 0x00, 0x04, 0x36, 0x06, 0x00, 0x05, 0x11, 0x00, 0x00, 0x01, 0x00 
					//00 00 04 36 00 00 00 01 00 00 14 9B?
			});
			bsofplain.write(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(account)).toUpperCase()));
			bsofplain.write(new byte[]{
					0x00, 0x00,  // 记住密码00 01
					0x00, 0x00, 0x01, 0x03, 0x00, 0x14, 0x00, 0x01, // 固定
					0x00, 0x10 //长度
			});
			bsofplain.write(genKey(0x10));//机器固定验证key
			bsofplain.write(new byte[]{
					0x00, 0x32, 0x00, 0x37, 0x3E, 0x00, 0x37, 0x01, 0x03, 0x04, 0x02, 0x00, 0x00, 0x04, 0x00	//固定
			});
			bsofplain.write(new byte[]{
					0x22, (byte)0x88, // unknow
					(byte)0x00, 0x00, 0x00, 0x00, 0x03, 0x5A, 0x44, 0x22, (byte)0x00, 
					(byte)0xFC, (byte)0xCF, 0x68, (byte)0xF5, 0x53, 0x26, 0x3B, (byte)0xB8, (byte)0xE6, 0x61, 0x76, (byte)0xF1, (byte)0x9D, 0x1C, 0x7A, (byte)0xD8,
					(byte)0xA9, (byte)0xAE, 0x04, (byte)0xE0, (byte)0xD6, (byte)0xBF, (byte)0xBA, (byte)0x8F, 0x55, (byte)0xF0, 0x36, 0x7A, (byte)0xF7, (byte)0xD6, (byte)0xED, (byte)0x92 // unknow是一个验证，未知算法，可以用随机的，0826包不返回0018的数据，0018的数据也可以直接随机
			});
			bsofplain.write(new byte[]{
					0x68, 0x01, 0x14, 0x00, 0x1D, 0x01, 0x02	//固定
			});
			bsofplain.write(new byte[]{
					0x00, 0x19  // 长度
			});
			bsofplain.write(ecdhkey);
			bsofplain.write(new byte[]{
					0x01, 0x02, 0x00, 0x62, 0x00, 0x01 //固定
			});
			bsofplain.write(new byte[]{
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // 应该是用于出现验证码时计算key，全放0，后面可以直接用0826send key解密
			});
			bsofplain.write(new byte[]{
					0x00, 0x38  // 长度
			});
			bsofplain.write(genKey(0x38));
			bsofplain.write(new byte[]{
					0x00, 0x14  // 长度
			});
			
			crcs[0] = new byte[]{
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00// 应该是用于出现验证码时计算key，全放0，后面可以直接用0826send key解密 
			};
			baos.write(crcs[0]);
			crc.update(crcs[0]);
			bsofplain.write(reverse(Converts.hexStringToByte(Long.toHexString(crc.getValue()).toUpperCase()))); // here should reverse
						
			//用于ecdh算法，加密发送
			key0836x = genKey(0x10);
			BigInteger root = new BigInteger("711");
			BigInteger d = new BigInteger("04928D8850673088B343264E0C6BACB8496D697799F37211DEB25BB73906CB089FEA9639B4E0260498B51A992D50813DA8",
					16);//B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3
			BigInteger e = new BigInteger(key0836x);

			// generate my crypt-pub-key
			byte[] fcpk = Converts.hexStringToByte(root.modPow(e, d).toString(16).toUpperCase());			
			//0x04, 0x92, 0x8D, 0x88, 0x50, 0x67, 0x30, 0x88, 0xB3, 0x43, 0x26, 0x4E, 0x0C, 0x6B, 0xAC, 0xB8, 0x49, 0x6D, 0x69, 0x77, 0x99, 0xF3, 0x72, 0x11, 0xDE, 0xB2, 0x5B, 0xB7, 0x39, 0x06, 0xCB, 0x08, 0x9F, 0xEA, 0x96, 0x39, 0xB4, 0xE0, 0x26, 0x04, 0x98, 0xB5, 0x1A, 0x99, 0x2D, 0x50, 0x81, 0x3D, 0xA8,
			System.out.println(Converts.bytesToHexString(fcpk));
			// should be 25 长度
			
			//------------------------------------------------------------------------------
			//00BA
			
			
			
			
			//------------------------------------------------------------------------------
			//0828
			
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
}
