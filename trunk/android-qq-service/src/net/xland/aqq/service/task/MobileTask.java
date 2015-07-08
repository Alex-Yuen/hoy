package net.xland.aqq.service.task;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import net.xland.aqq.service.PacketContent;
import net.xland.aqq.service.PacketSender;
import net.xland.aqq.service.Task;
import net.xland.util.Converts;
import net.xland.util.XLandUtil;

public class MobileTask extends Task {
	private String mobile = null;	

	private static byte[] bspubk = Converts
			.hexStringToByte("04928D8850673088B343264E0C6BACB8496D697799F37211DEB25BB73906CB089FEA9639B4E0260498B51A992D50813DA8");
	private static Logger logger = LogManager.getLogger(PacketSender.class.getName());
	
	public MobileTask(String mobile) {
		this.sid = null;
		this.mobile = mobile;
	}

	@Override
	public void run() {
		try {
			this.session = this.server.getSession(this.sid);
			
			// 保存ECDH share key和public key
			Security.addProvider(new BouncyCastleProvider());
			ECGenParameterSpec ecps = new ECGenParameterSpec("secp192k1");

			//ECParameterSpec spec = NamedCurveX.getECParameterSpec("secp192k1");//secp160k1
			// ECParameterSpec specx =
			// (ECParameterSpec)ECNamedCurveTable.getParameterSpec("secp192k1");
			// params.get

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH", "BC");
			kpg.initialize(ecps, new SecureRandom()); // 公私钥 工厂
			KeyPair pair = kpg.generateKeyPair(); // 生成公私钥

//			System.out.println(((ECPublicKey)pair.getPublic()).getEncoded().length);
						
			// BC ECDH key
			BCECPublicKey cpk = (BCECPublicKey) pair.getPublic();
			ECParameterSpec spec = cpk.getParams();
//			System.out.println(cpk.getQ().getEncoded(true).length);
			
//			org.bouncycastle.math.ec.ECPoint.Fp cp = (org.bouncycastle.math.ec.ECPoint.Fp) cpk
//					.getQ();
			byte[] ecdhkey = cpk.getQ().getEncoded(true); // ECDH key
//			System.out.println(ecdhkey.length);
			// Parse server pub key
			KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
			ECPoint sp = ECPointUtil.decodePoint(spec.getCurve(), bspubk);
			ECPublicKeySpec pkspec = new ECPublicKeySpec(sp, spec);
			ECPublicKey spubk = (ECPublicKey) kf.generatePublic(pkspec);

			KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
			agreement.init(pair.getPrivate());
			agreement.doPhase(spubk, true);

			byte[] secret = agreement.generateSecret();
			byte[] sharekey = null; // share key
//			System.arraycopy(secret, 0, sharekey, 0, sharekey.length);			
			sharekey = Converts.MD5Encode(secret);
			
			logger.info(sid+" [SHARE-KEY] "+Converts.bytesToHexString(sharekey));
			logger.info(sid+" [MOBILE] "+this.mobile);
			//for debug
//			sharekey = Converts.hexStringToByte("A34589C2E8F78437233C5E3559007B75");//"4EF64FE41459387BD65448BE91A7AA77"
//			ecdhkey = Converts.hexStringToByte("02CAB0D6B926C73887A2822DCB64572AF0EC5705C242F03765");//"03C98A8CFAC34CF168885FC9489D288A1E2D46D3E982FCDDAB"
			
			byte[] flag =  XLandUtil.genKey(4); //09 86 B2 6A 	
			byte[] xkey = XLandUtil.genKey(16); //2F 2D 97 C8 CF E4 9C 1F 38 12 43 C9 4C 0B 29 F6 
			
			flag = Converts.hexStringToByte("0986B26A");
			xkey = Converts.hexStringToByte("2F2D97C8CFE49C1F381243C94C0B29F6");
			
//			System.out.println(sid+"[SHARE-KEY]"+Converts.bytesToHexString(sharekey));
//			System.out.println(sid+"[MOBILE]"+this.mobile);
//			System.out.println("ecdhkey="+Converts.bytesToHexString(ecdhkey));
			this.session.put("x-cmd", "mobile");
			this.session.put("x-mobile", this.mobile);
			this.session.put("x-ek", ecdhkey);
			this.session.put("x-sk", sharekey);
			this.session.put("x-flag", flag);
			this.session.put("x-xkey", xkey);  //保存会话的值
			
			bos.reset();
			bos.write(new PacketContent("00 00 25 00 00 00 00 00 00 00 5F 00 00 00").toByteArray());
			bos.write(Converts.int2Byte((int)(System.currentTimeMillis()/1000)));
			bos.write(new PacketContent("02 00 21 00 01 00 07 04 00 00 00 00 01").toByteArray());
			bos.write(new PacketContent("0E").toByteArray());
			bos.write(("86-"+this.mobile).getBytes());
			bos.write(new PacketContent("00 00 00 10 03").toByteArray());			
			try{
				content = cryptor.encrypt(bos.toByteArray(), sharekey); //第一次加密
			}catch(Exception e){
				e.printStackTrace();
				logger.info(sid+" [MOBILE-ENCRYPT-A] " + Converts.bytesToHexString(bos.toByteArray()));
				logger.info(sid+" [MOBILE-ENCRYPT-B] " + Converts.bytesToHexString(sharekey));
			}
			
			bos.reset();
			bos.write(new PacketContent("00 00 00 74 00 00").toByteArray());
			bos.write(Converts.short2Byte(this.seq));
			bos.write(new PacketContent("20 02 9D EC 20 02 9D EC").toByteArray());
			bos.write(new PacketContent("67 00 00 00 00 00 00 00 00 00 00 00 ").toByteArray());
			bos.write(new PacketContent("00 00 00 04").toByteArray());
			bos.write(new PacketContent("00 00 00 15").toByteArray());
			bos.write(new PacketContent("77 74 6C 6F 67 69 6E 2E 74 72 61 6E 73 5F 65 6D 70").toByteArray());
			bos.write(new PacketContent("00 00 00 08").toByteArray());
			bos.write(flag);
			bos.write(new PacketContent("00 00 00 13").toByteArray());
			bos.write(new PacketContent("30 30 30 30 30 30 30 30 30 30 30 30 30 30 30").toByteArray());
			bos.write(new PacketContent("00 00 00 04").toByteArray());
			bos.write(new PacketContent("00 20").toByteArray());
			bos.write(new PacketContent("7C 33 31 30 32 36 30 30 30 30 30 30 30 30 30 30").toByteArray());
			bos.write(new PacketContent("7C 41 35 2E 37 2E 32 2E 31 34 38 33 32 31").toByteArray());
			bos.write(new PacketContent("00 00").toByteArray());
			bos.write(Converts.short2Byte((short)(content.length+0x50)));
			bos.write(new PacketContent("02").toByteArray());
			bos.write(Converts.short2Byte((short)(content.length+0x4C)));
			bos.write(new PacketContent("1F 41 08 12").toByteArray());
			bos.write(new PacketContent("00 01").toByteArray());
			bos.write(new PacketContent("00 00 00 00").toByteArray());
			bos.write(new PacketContent("03 07 00 00 00").toByteArray());
			bos.write(new PacketContent("00 02").toByteArray());
			bos.write(new PacketContent("00 00 00 00").toByteArray());
			bos.write(new PacketContent("00 00 00 00").toByteArray());
			bos.write(new PacketContent("01 01").toByteArray());
			bos.write(xkey);
			bos.write(new PacketContent("01 02").toByteArray());
			bos.write(new PacketContent("00 19").toByteArray());
			bos.write(ecdhkey);
			bos.write(content);
			bos.write(new PacketContent("03").toByteArray());
			try{
				content = cryptor.encrypt(bos.toByteArray(), outterkey); //第二次加密
			}catch(Exception e){
				e.printStackTrace();
				logger.info(sid+" [MOBILE-ENCRYPT-A] " + Converts.bytesToHexString(bos.toByteArray()));
				logger.info(sid+" [MOBILE-ENCRYPT-B] " + Converts.bytesToHexString(outterkey));
			}
			
			bos.reset();
			bos.write(new PacketContent("00 00 01 23 00 00 00 08 02 00 00 00 04 00 00 00").toByteArray());
			bos.write(new PacketContent("00 05 30").toByteArray());
			bos.write(content);			
			content = bos.toByteArray();
			
			submit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
