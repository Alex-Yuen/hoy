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

import net.xland.aqq.service.Task;
import net.xland.util.Converts;
import net.xland.util.NamedCurveX;

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
			// this.content = null;
			StringBuffer future = this.server.getFuture(this.seq);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
