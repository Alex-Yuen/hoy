package ws.hoyland.util;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import javax.crypto.KeyAgreement;

public class ECDH {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			 Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDH", "BC");
			    EllipticCurve curve = new EllipticCurve(new ECFieldFp(new BigInteger(
			        "fffffffffffffffffffffffffffffffeffffffffffffffff", 16)), new BigInteger(
			        "fffffffffffffffffffffffffffffffefffffffffffffffc", 16), new BigInteger(
			        "fffffffffffffffffffffffffffffffefffffffffffffffc", 16));

			    ECParameterSpec ecSpec = new ECParameterSpec(curve, new ECPoint(new BigInteger(
			        "fffffffffffffffffffffffffffffffefffffffffffffffc", 16), new BigInteger(
			        "fffffffffffffffffffffffffffffffefffffffffffffffc", 16)), new BigInteger(
			        "fffffffffffffffffffffffffffffffefffffffffffffffc", 16), 1);

			    keyGen.initialize(ecSpec, new SecureRandom());

			    KeyAgreement aKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
			    KeyPair aPair = keyGen.generateKeyPair();
			    KeyAgreement bKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
			    KeyPair bPair = keyGen.generateKeyPair();

			    System.out.println(aPair.getPrivate().getEncoded().length);
			    
			    aKeyAgree.init(aPair.getPrivate());
			    bKeyAgree.init(bPair.getPrivate());

			    aKeyAgree.doPhase(bPair.getPublic(), true);
			    bKeyAgree.doPhase(aPair.getPublic(), true);

			    MessageDigest hash = MessageDigest.getInstance("SHA1", "BC");

			    System.out.println(new String(hash.digest(aKeyAgree.generateSecret())));
			    System.out.println(new String(hash.digest(bKeyAgree.generateSecret())));
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
