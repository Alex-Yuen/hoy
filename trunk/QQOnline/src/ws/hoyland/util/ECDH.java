package ws.hoyland.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
//import java.security.spec.ECParameterSpec;

import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import sun.security.ec.NamedCurve;

//import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
//import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
//import org.bouncycastle.crypto.params.ECPublicKeyParameters;
//import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
//
//import sun.security.ec.NamedCurve;

//import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
//import org.bouncycastle.asn1.x9.ECNamedCurveTable;
//import org.bouncycastle.math.ec.ECPoint;

public class ECDH {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			byte[] serverPBK = new byte[]{
				0x04, (byte)0x92, (byte)0x8D, (byte)0x88, 0x50, 0x67, 0x30, (byte)0x88, 
				(byte)0xB3, 0x43, 0x26, 0x4E, 0x0C, 0x6B, (byte)0xAC, (byte)0xB8, 
				0x49, 0x6D, 0x69, 0x77, (byte)0x99, (byte)0xF3, 0x72, 0x11, 
				(byte)0xDE, (byte)0xB2, 0x5B, (byte)0xB7, 0x39, 0x06, (byte)0xCB, 0x08, 
				(byte)0x9F, (byte)0xEA, (byte)0x96, 0x39, (byte)0xB4, (byte)0xE0, 0x26, 0x04, 
				(byte)0x98, (byte)0xB5, 0x1A, (byte)0x99, 0x2D, 0x50, (byte)0x81, (byte)0x3D, (byte)0xA8	
			};
			
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			ECParameterSpec ecSpec = NamedCurve.getECParameterSpec("secp192k1");
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH", "BC");			
			keyGen.initialize(ecSpec, new SecureRandom()); //公私钥 工厂
			KeyPair pair = keyGen.generateKeyPair(); //生成公私钥
			BCECPublicKey cpk =(BCECPublicKey) pair.getPublic();
			ECPoint.Fp point = (ECPoint.Fp)cpk.getQ();

			System.out.println(point.getEncoded(true).length);
			
			//ECPoint.Fp
			//ECCurve.Fp cur = (ECCurve.Fp)ecSpec.getCurve();
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
			byte[] rk = new byte[16];
			for(int i=0;i<rk.length;i++){
				rk[i] = xx[i];
			}
			
			System.out.println(rk.length);
			
//			EllipticCurve curve = new EllipticCurve(
//				new ECFieldFp(new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37", 16)), 
//				new BigInteger("000000000000000000000000000000000000000000000000", 16),x
//				new BigInteger("000000000000000000000000000000000000000000000003", 16)y
//			);
//
//			ECParameterSpec ecSpec = new ECParameterSpec(
//				curve, 
//				new ECPoint(
//					new BigInteger("DB4FF10EC057E9AE26B07D0280B7F4341DA5D1B1EAE06C7D", 16), a
//					new BigInteger("9b2f2f6d9c5628a7844163d015be86344082aa88d95e2f9d", 16)b
//				), 
//				new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D", 16), 
//				1
//			);
			/**
			ECParameterSpec ecSpec = NamedCurve.getECParameterSpec("secp192k1");
			//ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp192k1");
			
			//ECKeyPairGenerator ECKeyPairGen = new ECKeyPairGenerator();
			
			keyGen.initialize(ecSpec, new SecureRandom()); //公私钥 工厂
			//ECKeyPairGen.init(ecSpec);
			//AsymmetricCipher
			KeyPair pair = keyGen.generateKeyPair(); //生成公私钥
			
			
			//BCECPublicKey pubKey = (BCECPublicKey)pair.getPublic();
			
			//ECPublicKey cpk =(ECPublicKey) pair.getPublic();
			BCECPublicKey cpk =(BCECPublicKey) pair.getPublic();
			ECPoint.Fp point = (ECPoint.Fp)cpk.getQ();
			//ECPoint.Fp fp = (ECPoint.Fp)point;
			//fp.
			System.out.println(point.getEncoded(true).length);
//			System.out.println(pair.getPublic().getEncoded().length);
//			System.out.println(cpk.getEncoded().length);
//			ECPoint point = cpk.getQ();
			
			//ECPoint point = cpk.getW();
			//ecSpec.getCurve().e

			//point.
//			System.out.println(cpk.getEncoded().length);
//			System.out.println(Converts.bytesToHexString(cpk.getEncoded()));
			//cpk.engineGetQ()
//			ecPubKeyParams.getW()
			
//			BCECPrivateKey ecPrivateKeyParams =(BCECPrivateKey) pair.getPublic();
//			point = ecPrivateKeyParams.get;
			
//			System.out.println(point.getEncoded().length);
			//ECPoint ecPublicKey = ecPubKeyParams.getQ();
			 
			//pair.getPublic(); //如何转为25字节？
			//getPublic
			//根据服务器公钥计算共享秘密
			KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
			java.security.spec.ECPoint sp = ECPointUtil.decodePoint(ecSpec.getCurve(), serverPBK);
			//sp.
			ECPublicKeySpec pubSpec = new ECPublicKeySpec(sp, ecSpec);
			ECPublicKey myECPublicKey = (ECPublicKey) kf.generatePublic(pubSpec);
//			if(sp instanceof ECPoint.Fp){
//				System.out.println("OK");
//			}
			//sp.
//			//sp.
//			PublicKeyFactory.createKey(serverPBK);
			
			//ECPoint.Fp x =new ECPoint.Fp();
//			 KeyFactory          keyFac = KeyFactory.getInstance("ECDH", "BC");
//		        X509EncodedKeySpec  pubX509 = new X509EncodedKeySpec(serverPBK);
//		        ECPublicKey         pubKey = (ECPublicKey)keyFac.generatePublic(pubX509);
		        
//			ECPublicKeyParameters bpubKey = (ECPublicKeyParameters)PublicKeyFactory.createKey(key)
					
			KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
			agreement.init(pair.getPrivate());
			agreement.doPhase(myECPublicKey, true);
//			
			byte[] xxx = agreement.generateSecret();
			System.out.println(xxx.length);
			
//			System.out.println(pair.getPrivate().getEncoded().length);
//			System.out.println(pair.getPublic().getEncoded().length);
			//System.out.println(Converts.bytesToHexString(pair.getPublic().getEncoded()));
			
//			ECPoint point = ECPointUtil.decodePoint(ecSpec.getCurve(), serverPBK);
//			ECPublicKeySpec pubKey = new ECPublicKeySpec(point, ecSpec);
			
					//(point, ecSpec.getCurve());
			
//			System.out.println(point);
			//point.
//			
//			KeyAgreement aKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
//			KeyPair aPair = keyGen.generateKeyPair();
//			KeyAgreement bKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
//			KeyPair bPair = keyGen.generateKeyPair();
//
//			// System.out.println(aPair.getPrivate().getEncoded().length);
//
//			aKeyAgree.init(aPair.getPrivate());
//			bKeyAgree.init(bPair.getPrivate());
//
//			aKeyAgree.doPhase(bPair.getPublic(), true);
//			bKeyAgree.doPhase(aPair.getPublic(), true);
//
//			MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");
//
//			System.out.println(new String(hash.digest(aKeyAgree
//					.generateSecret())));
//			System.out.println(new String(hash.digest(bKeyAgree
//					.generateSecret())));
 
 */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
