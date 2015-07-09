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
import net.xland.util.Cryptor;
import net.xland.util.NamedCurveX;
import net.xland.util.XLandUtil;

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
			
			byte[] body = Converts.hexStringToByte("000000310000112700000000000000040000001577746C6F67696E2E7472616E735F656D7000000008A9BC61C000000000000000FD0200F91F41081200010000000000000055CF9AA61E0C9AFE48ADDCFC5BE40A5ACDBEBF4498023959B54079E6A079094556D56F57B76CC10B13BB94508691F658FA90576FCA0EF546A87C19A5E176758ED05DA0AB8185A810F6752DF58EF41EE6F9678B02A2E4D3A9C0B592A5A2C278091CB040ACA49E7EDECF85C21BE7B66D57ACB168ED2E875CC026F6332E3E28A1EC28EB91E26482D6B1CA40A870DD464E81C0B7D25DB8206EF349431804015FF21FCA72F8FECC4D475866DCC675FE2F770DD21E7CE21A1748986F05F13ABEEFC28BDF88AB31B7D3F86B3DB1A68430FC7B36E3B7D0C006C20B4CD219D8492DC69041A61E8D5F651D956403");
			byte[] sk = Converts.hexStringToByte("DDEC53F18BE6F87C8002F379277BD787");
			byte[] ck = Converts.hexStringToByte("E06255BD393539C8E29E766C823C8D9B");
			
			Cryptor cryptor = new Cryptor();
			byte[] ibody = cryptor.decrypt(XLandUtil.slice(body, 69, body.length-69-1), sk);
			System.out.println("ibody:"+Converts.bytesToHexString(ibody));
			int qbodylength = (ibody[0x12]&0xff);
			System.out.println(qbodylength);
			byte[] qbody = cryptor.decrypt(XLandUtil.slice(ibody, 0x13, qbodylength), ck);//QQ body
			System.out.println("qbody:"+Converts.bytesToHexString(qbody));
			byte[] xqq = XLandUtil.slice(qbody, 32, 4);
			long qqnumber = Long.valueOf(Converts.bytesToHexString(xqq), 16);//Integer.parseInt(Converts.bytesToHexString(xqq), 16);
			System.out.println(qqnumber);
			
			byte[] content = Converts.hexStringToByte("000025000000000000005F000000559D3D0D020021000100070400000000010E38362D31353038353033373034320000001003");
			byte[] key = Converts.hexStringToByte("084A6CABE0A51E12AC4CE1BB244304A2");
			
			System.out.println(Converts.bytesToHexString(cryptor.encrypt(content, key)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
