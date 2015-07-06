package net.xland.aqq.service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jce.ECPointUtil;

import net.xland.util.Converts;
import net.xland.util.NamedCurveX;

public class Test {
	private static byte[] bspubk = Converts
			.hexStringToByte("04928D8850673088B343264E0C6BACB8496D697799F37211DEB25BB73906CB089FEA9639B4E0260498B51A992D50813DA8");


	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		HashMap<Integer, StringBuffer> futures =  new LinkedHashMap<Integer, StringBuffer>();
//		StringBuffer sb = new StringBuffer();
////		sb.append("1234");
//		futures.put(0x1123, sb);
//		
//		sb.append("KKK");
//		
//		System.out.println(futures.get(0x1123).toString());
		
		//保存ecdh share key和public key
		try {
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

			System.out.println(Converts.bytesToHexString(ecdhkey));
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
			System.out.println(Converts.bytesToHexString(sharekey));
			
			
			String x = "38 36 2D 31 35 38 33 32 39 39 33 38 33 31".trim();
			byte[] bs = new PacketContent(x).toByteArray();
			
//			byte[] bs = Converts.int2Byte((int)(System.currentTimeMillis()/1000));
			System.out.println(Converts.bytesToHexString(bs));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
