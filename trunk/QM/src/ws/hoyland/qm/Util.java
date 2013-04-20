package ws.hoyland.qm;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;

public class Util {
	private static PublicKey publicKey = null;

	static {
		try {
			CertificateFactory localCertificateFactory = CertificateFactory
					.getInstance("X.509");
			InputStream localInputStream = new FileInputStream(
					"/Theoservice.cer");
			Certificate localCertificate = localCertificateFactory
					.generateCertificate(localInputStream);
			localInputStream.close();
			publicKey = localCertificate.getPublicKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(String passsword) {
		try {
			Cipher localCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			localCipher.init(1, publicKey);
			byte[] arrayOfByte = localCipher.doFinal(passsword.getBytes());
			String str = com.tencent.qqmail.Utilities.p03i.C325a.MPa(
					arrayOfByte, arrayOfByte.length);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
