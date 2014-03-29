import javax.crypto.spec.SecretKeySpec;

import ws.hoyland.util.Converts;


public class XSecretKeySpec extends SecretKeySpec {

	public XSecretKeySpec(byte[] arg0, String arg1) {
		super(arg0, arg1);
		System.out.println("XSecretKeySpec:"+Converts.bytesToHexString(arg0));
		System.out.println(arg1);
		// TODO Auto-generated constructor stub
	}

}
