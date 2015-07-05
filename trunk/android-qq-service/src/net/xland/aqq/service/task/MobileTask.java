package net.xland.aqq.service.task;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;
import sun.security.ec.NamedCurve;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.math.ec.ECPoint;

import net.xland.aqq.service.Task;

public class MobileTask extends Task {
	private String mobile = null;
	
	private static byte[] serverPK = new byte[] { 
		0x04, (byte) 0x92, (byte) 0x8D,
		(byte) 0x88, 0x50, 0x67, 0x30, (byte) 0x88,
		(byte) 0xB3, 0x43, 0x26, 0x4E, 0x0C, 0x6B, (byte) 0xAC,
		(byte) 0xB8, 0x49, 0x6D, 0x69, 0x77, (byte) 0x99,
		(byte) 0xF3, 0x72, 0x11, (byte) 0xDE, (byte) 0xB2,
		0x5B, (byte) 0xB7, 0x39, 0x06, (byte) 0xCB, 0x08,
		(byte) 0x9F, (byte) 0xEA, (byte) 0x96, 0x39,
		(byte) 0xB4, (byte) 0xE0, 0x26, 0x04, (byte) 0x98,
		(byte) 0xB5, 0x1A, (byte) 0x99, 0x2D, 0x50,
		(byte) 0x81, (byte) 0x3D, (byte) 0xA8 
	};
	
	public MobileTask(String mobile) {
		this.sid = null;
		this.mobile = mobile;
	}

	@Override
	public void run() {
		try{
//			this.content = null;
			StringBuffer future = this.server.getFuture(this.seq);
			//保存ecdh share key和public key
			
			// ecdhkey 随机生成的公钥，然后sk即为共享秘密，由算法计算得出
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			ECParameterSpec ecSpec = NamedCurve.getECParameterSpec("secp192k1");					
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH", "BC");
			keyGen.initialize(ecSpec, new SecureRandom()); // 公私钥 工厂
			KeyPair pair = keyGen.generateKeyPair(); // 生成公私钥
			BCECPublicKey cpk = (BCECPublicKey) pair.getPublic();
			ECPoint.Fp point = (ECPoint.Fp) cpk.getQ();

			byte[] ecdhkey = point.getEncoded(true); // ecdhkey
			// System.out.println(ecdhkey.length);

			java.security.spec.ECPoint sp = ECPointUtil.decodePoint(
					ecSpec.getCurve(), serverPK);
			KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
			ECPublicKeySpec pubSpec = new ECPublicKeySpec(sp, ecSpec);
			ECPublicKey myECPublicKey = (ECPublicKey) kf
					.generatePublic(pubSpec);

			KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
			agreement.init(pair.getPrivate());
			agreement.doPhase(myECPublicKey, true);
			//
			byte[] secret = agreement.generateSecret();
			byte[] sk = new byte[16];
            // 只需要前面16个字节
            System.arraycopy(secret, 0, sk, 0, 16);

			submit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
