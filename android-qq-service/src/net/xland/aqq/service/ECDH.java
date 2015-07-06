package net.xland.aqq.service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;

import net.xland.util.Converts;

import org.bouncycastle.jce.ECPointUtil;

public class ECDH {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 try {
			 byte[] bspubk = Converts
						.hexStringToByte("04928D8850673088B343264E0C6BACB8496D697799F37211DEB25BB73906CB089FEA9639B4E0260498B51A992D50813DA8");

			 
			 	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			 
	            KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH", "BC");
	            ECGenParameterSpec params = new ECGenParameterSpec("secp192k1");
	            kpg.initialize(params, new SecureRandom());
	            KeyPair kp = kpg.generateKeyPair();
	            PrivateKey privateKey = kp.getPrivate();
	            PublicKey publicKey = kp.getPublic();
	            
	            ECParameterSpec spec = ((ECPublicKey)publicKey).getParams();
	            ECPoint point = ECPointUtil.decodePoint(spec.getCurve(), bspubk);
	            KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
	            ECPublicKeySpec specx = new ECPublicKeySpec(point, spec);
	            PublicKey peerPublicKey = kf.generatePublic(specx);
	            
	            KeyAgreement agreement = KeyAgreement.getInstance("ECDH", "BC");
				agreement.init(privateKey);
				agreement.doPhase(peerPublicKey, true);

				byte[] secret = agreement.generateSecret();
				byte[] sharekey = new byte[16]; // share key
				System.arraycopy(secret, 0, sharekey, 0, sharekey.length);
				
				byte[] ek = ((ECPublicKey)publicKey).getEncoded();
				System.out.println(Converts.bytesToHexString(ek));
				System.out.println(Converts.bytesToHexString(sharekey));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

}
