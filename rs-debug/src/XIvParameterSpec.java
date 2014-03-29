import javax.crypto.spec.IvParameterSpec;

import ws.hoyland.util.Converts;


public class XIvParameterSpec extends IvParameterSpec {

	public XIvParameterSpec(byte[] arg0) {
		super(arg0);
		System.out.println("XIvParameterSpec:"+Converts.bytesToHexString(arg0));
		// TODO Auto-generated constructor stub
	}

}
