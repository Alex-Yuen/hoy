package net.xland.aqq.service.task;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jce.ECPointUtil;

import net.xland.aqq.service.PacketContent;
import net.xland.aqq.service.Task;
import net.xland.util.Converts;
import net.xland.util.NamedCurveX;
import net.xland.util.XLandUtil;

public class MobileTask extends Task {
	private String mobile = null;	

	private static byte[] bspubk = Converts
			.hexStringToByte("04928D8850673088B343264E0C6BACB8496D697799F37211DEB25BB73906CB089FEA9639B4E0260498B51A992D50813DA8");

	public MobileTask(String mobile) {
		this.sid = null;
		this.mobile = mobile;
	}

	@Override
	public void run() {
		try {			
			// 保存ECDH share key和public key
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			ECParameterSpec spec = NamedCurveX.getECParameterSpec("secp192k1");
			// ECParameterSpec specx =
			// (ECParameterSpec)ECNamedCurveTable.getParameterSpec("secp192k1");
			// params.get

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH", "BC");
			kpg.initialize(spec, new SecureRandom()); // 公私钥 工厂
			KeyPair pair = kpg.generateKeyPair(); // 生成公私钥

			// BC ECDH key
			org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey cpk = (org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey) pair
					.getPublic();
			org.bouncycastle.math.ec.ECPoint.Fp cp = (org.bouncycastle.math.ec.ECPoint.Fp) cpk
					.getQ();
			byte[] ecdhkey = cp.getEncoded(true); // ECDH key

			// Parse server pub key
			KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
			ECPoint sp = ECPointUtil.decodePoint(spec.getCurve(), bspubk);
			ECPublicKeySpec pkspec = new ECPublicKeySpec(sp, spec);
			ECPublicKey spubk = (ECPublicKey) kf.generatePublic(pkspec);

			KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
			agreement.init(pair.getPrivate());
			agreement.doPhase(spubk, true);

			byte[] secret = agreement.generateSecret();
			byte[] sharekey = new byte[16]; // share key
			System.arraycopy(secret, 0, sharekey, 0, sharekey.length);
			
			byte[] flag =  XLandUtil.genKey(4);
			byte[] xkey = XLandUtil.genKey(16);
			
			bos.reset();
			bos.write(new PacketContent("00 00 25 00 00 00 00 00 00 00 5F 00 00 00").toByteArray());
			bos.write(Converts.int2Byte((int)(System.currentTimeMillis()/1000)));
			bos.write(new PacketContent("02 00 21 00 01 00 07 04 00 00 00 00 01").toByteArray());
			bos.write(new PacketContent("0E").toByteArray());
			bos.write(("86-"+this.mobile).getBytes());
			bos.write(new PacketContent("00 00 00 10 03").toByteArray());			
			content = cryptor.encrypt(bos.toByteArray(), sharekey); //第一次加密
			
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
			bos.write(new PacketContent("00 00 00 90").toByteArray());
			bos.write(new PacketContent("02").toByteArray());
			bos.write(new PacketContent("00 8C").toByteArray());
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
			content = cryptor.encrypt(bos.toByteArray(), outterkey); //第二次加密
			
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
