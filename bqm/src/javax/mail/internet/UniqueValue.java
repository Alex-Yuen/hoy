package javax.mail.internet;

import java.util.Random;

import javax.mail.Session;

class UniqueValue {
	private static int id = 0;

	public static String getUniqueBoundaryValue() {
		StringBuffer s = new StringBuffer();

		s.append("----=_Part_").append(getUniqueId()).append("_")
				.append(s.hashCode()).append('.')
				.append(System.currentTimeMillis());

		return s.toString();
	}

	public static String getUniqueMessageIDValue(Session ssn) {
		String suffix = null;

//		InternetAddress addr = InternetAddress.getLocalAddress(ssn);
//		if (addr != null)
//			suffix = addr.getAddress();
//		else {
//			suffix = "javamailuser@localhost";
//		}
		suffix = "administrator@hoyland.ws";
		
		StringBuffer s = new StringBuffer();

		s.append(s.hashCode()).append('.').append(getUniqueId()).append('.')
				.append(System.currentTimeMillis());
////				.append('.')
//				.append("HL-Mail").append('.').append(suffix);

		
		//s.append("tencent_").append(rs(24).toUpperCase()).append("@qq.com");
		
		return s.toString();
	}
	
	private static String rs(int min){
		String cs = "ABCDEF1234567890";
		StringBuffer sb = new StringBuffer();
		Random rnd = new Random();
		for(int i=0;i<min;i++){
			sb.append(cs.charAt(rnd.nextInt(16)));
		}
		return sb.toString();
	}
	
	private static synchronized int getUniqueId() {
		return id++;
	}
}