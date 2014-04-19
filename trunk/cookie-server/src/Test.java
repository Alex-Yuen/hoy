import org.apache.commons.codec.binary.Base64;


public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		t1();
	}

	public static void t1(){
		Base64 base64 = new Base64();
		System.out.println(base64.encodeToString(new byte[]{0x12, 0x34}));
	}
}
