package ws.hoyland.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import sun.security.ec.NamedCurve;

//import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
//import org.bouncycastle.asn1.x9.ECNamedCurveTable;
//import org.bouncycastle.math.ec.ECPoint;

public class ECDH {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			byte[] serverPBK = new byte[]{
				0x04, (byte)0x92, (byte)0x8D, (byte)0x88, 0x50, 0x67, 0x30, (byte)0x88, 
				(byte)0xB3, 0x43, 0x26, 0x4E, 0x0C, 0x6B, (byte)0xAC, (byte)0xB8, 
				0x49, 0x6D, 0x69, 0x77, (byte)0x99, (byte)0xF3, 0x72, 0x11, 
				(byte)0xDE, (byte)0xB2, 0x5B, (byte)0xB7, 0x39, 0x06, (byte)0xCB, 0x08, 
				(byte)0x9F, (byte)0xEA, (byte)0x96, 0x39, (byte)0xB4, (byte)0xE0, 0x26, 0x04, 
				(byte)0x98, (byte)0xB5, 0x1A, (byte)0x99, 0x2D, 0x50, (byte)0x81, (byte)0x3D, (byte)0xA8	
			};
			
			KeyPairGenerator keyGen = KeyPairGenerator
					.getInstance("ECDH", "BC");
			
//			EllipticCurve curve = new EllipticCurve(
//				new ECFieldFp(new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFEE37", 16)), 
//				new BigInteger("000000000000000000000000000000000000000000000000", 16),
//				new BigInteger("000000000000000000000000000000000000000000000003", 16)
//			);
//
//			ECParameterSpec ecSpec = new ECParameterSpec(
//				curve, 
//				new ECPoint(
//					new BigInteger("DB4FF10EC057E9AE26B07D0280B7F4341DA5D1B1EAE06C7D", 16), 
//					new BigInteger("9b2f2f6d9c5628a7844163d015be86344082aa88d95e2f9d", 16)
//				), 
//				new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFE26F2FC170F69466A74DEFD8D", 16), 
//				1
//			);
			//ECParameterSpec ecSpec = NamedCurve.getECParameterSpec("secp192k1");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp192k1");
			
			ECKeyPairGenerator ECKeyPairGen = new ECKeyPairGenerator();
			
			//keyGen.initialize(ecSpec, new SecureRandom()); //公私钥 工厂
			ECKeyPairGen.init(ecSpec);
			
			AsymmetricCipherKeyPair pair = keyGen.generateKeyPair(); //生成公私钥
			
			
			ECPublicKeyParameters pubKey = (ECPublicKeyParameters)pair.getPublic();
			
			ECPublicKey cpk =(ECPublicKey) pair.getPublic();
//			System.out.println(pair.getPublic().getEncoded().length);
//			System.out.println(cpk.getEncoded().length);
//			ECPoint point = cpk.getQ();
			
			ECPoint point = cpk.getW();
			//ecSpec.getCurve().e

			System.out.println(cpk.getEncoded().length);
			System.out.println(Converts.bytesToHexString(pair.getPublic().getEncoded()));
			//cpk.engineGetQ()
//			ecPubKeyParams.getW()
			
//			BCECPrivateKey ecPrivateKeyParams =(BCECPrivateKey) pair.getPublic();
//			point = ecPrivateKeyParams.get;
			
//			System.out.println(point.getEncoded().length);
			//ECPoint ecPublicKey = ecPubKeyParams.getQ();
			 
			//pair.getPublic(); //如何转为25字节？
			//getPublic
			//根据服务器公钥计算共享秘密
//			KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
//			agreement.init(pair.getPrivate());
//			agreement.doPhase(pair.getPublic(), true);
//			
//			byte[] xxx = agreement.generateSecret();
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
