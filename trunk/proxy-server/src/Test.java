import java.security.MessageDigest;

public class Test {

	public String convertStringToHex(String str) {

		char[] chars = str.toCharArray();

		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}

		return hex.toString();
	}

	public String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		// 49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}
		System.out.println("Decimal : " + temp.toString());

		return sb.toString();
	}
	
	public byte[] hexStr2Bytes(String src)  
    {  
        int m=0,n=0;  
        int l=src.length()/2;  
        System.out.println(l);  
        byte[] ret = new byte[l];  
        for (int i = 0; i < l; i++)  
        {  
            m=i*2+1;  
            n=m+1;  
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));  
        }  
        return ret;  
    }  
  
	
	public String md5s(byte[] src) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			//md.update(plainText.getBytes("unicode"));
			md.update(src);
			byte b[] = md.digest();

			int i;
			String str = null;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
			return str;
			//System.out.println("result: " + buf.toString());// 32位的加密
			//System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {

		Test ts = new Test();
//		System.out.println("\n***** Convert ASCII to Hex *****");
//		String str = "I Love Java!";
//		System.out.println("Original input : " + str);
//
//		String hex = strToHex.convertStringToHex(str);
		//System.out.println("Hex : " + hex);
//		String hex = "7573657269643D353732313430343226656D61696C3D686F797A68616E67403136332E636F6D2670617373776F72643D313233343536373826757064617465666C61673D3026617574686B65793D3236363137393533266578743D286E756C6C29266D61633D3744353584A338343234384238463439373641344239454436423933463338424334265665723D372E322E31323032323426506172746E657269643D302673613D3026696E695665723D53414446343335353446444023244066775E6B2A2870255E256566776473234040235E5E2523245E2633343536455745444644464444535349444ABFC9B0AE616161A1AA";//BFC9B0AE616161A1AA
//		//System.out.println("Hex : " + hex);
//		System.out.println("\n***** Convert Hex to ASCII *****");
//		System.out.println("Hex : " + hex);
//		String org = strToHex.convertHexToString(hex);
//		System.out.println("ASCII : " + org);
//		//06ebb85ee6245f7f50ae695cc9c6cde9
////org = "userid=57214042&email=hoyzhang@163.com&password=12345678&updateflag=0&authkey=26617953&ext=(null)&mac=7D55£84248B8F4976A4B9ED6B93F38BC4&Ver=7.2.120224&Partnerid=0&sa=0&iniVer=SADF43554FD@#$@fw^k*(p%^%efwds#@@#^^%#$^&3456EWEDFDFDDSSIDJ可爱aaa—";
//		System.out.println("MD5 : " + strToHex.md5s(strToHex.hexStr2Bytes("7573657269643D353732313430343226656D61696C3D686F797A68616E67403136332E636F6D2670617373776F72643D313233343536373826757064617465666C61673D3026617574686B65793D3236363137393533266578743D286E756C6C29266D61633D3744353584A338343234384238463439373641344239454436423933463338424334265665723D372E322E31323032323426506172746E657269643D302673613D3026696E695665723D53414446343335353446444023244066775E6B2A2870255E256566776473234040235E5E2523245E2633343536455745444644464444535349444ABFC9B0AE616161A1AA")));
//		//System.out.println(strToHex.md5s("6ac6901b0f5446dc260b63531921b6bf"));
		//System.out.println("MD5 : " + strToHex.md5s("is_hoyzhang@163.com"));
		byte[] src = new byte[]{
				0x52, 0x75, 0x6E, 0x55, 0x70, 0x64, 0x61, 0x74, 0x65, 0x3D, 0x31, 0x26, 0x45, 0x6D, 0x61, 0x69,
				0x6C, 0x3D, 0x68, 0x6F, 0x79, 0x7A, 0x68, 0x61, 0x6E, 0x67, 0x40, 0x31, 0x36, 0x33, 0x2E, 0x63,
				0x6F, 0x6D, 0x26, 0x50, 0x61, 0x73, 0x73, 0x77, 0x6F, 0x72, 0x64, 0x3D, 0x31, 0x32, 0x33, 0x34,
				0x35, 0x36, 0x37, 0x38, 0x26, 0x67, 0x61, 0x6D, 0x65, 0x6E, 0x61, 0x6D, 0x65, 0x3D, 0x26, 0x61,
				0x75, 0x74, 0x68, 0x6B, 0x65, 0x79, 0x3D, 0x33, 0x35, 0x30, 0x32, 0x35, 0x39, 0x33, 0x26, 0x65,
				0x78, 0x74, 0x3D, 0x28, 0x6E, 0x75, 0x6C, 0x6C, 0x29, 0x26, 0x6D, 0x61, 0x63, 0x3D, 0x37, 0x44,
				0x35, 0x33, 0x39, 0x34, 0x32, 0x34, 0x38, 0x42, 0x38, 0x46, 0x34, 0x39, 0x37, 0x36, 0x41, 0x34,//3, 0x38
				0x42, 0x39, 0x45, 0x44, 0x36, 0x42, 0x39, 0x33, 0x46, 0x33, 0x38, 0x42, 0x43, 0x34, 0x26, 0x56,
				0x65, 0x72, 0x3D, 0x37, 0x2E, 0x32, 0x2E, 0x31, 0x32, 0x30, 0x32, 0x32, 0x34, 0x26, 0x50, 0x61,
				0x72, 0x74, 0x6E, 0x65, 0x72, 0x69, 0x64, 0x3D, 0x30, 0x26, 0x73, 0x61, 0x3D, 0x30, 0x26, 0x69,
				0x6E, 0x69, 0x56, 0x65, 0x72, 0x3D, 0x37, 0x2E, 0x32, 0x2E, 0x31, 0x32, 0x30, 0x36, 0x30, 0x35,
				0x53, 0x41, 0x44, 0x46, 0x34, 0x33, 0x35, 0x35, 0x34, 0x46, 0x44, 0x40, 0x23, 0x24, 0x40, 0x66,
				0x77, 0x5E, 0x6B, 0x2A, 0x28, 0x70, 0x25, 0x5E, 0x25, 0x65, 0x66, 0x77, 0x64, 0x73, 0x23, 0x40,
				0x40, 0x23, 0x5E, 0x5E, 0x25, 0x23, 0x24, 0x5E, 0x26, 0x33, 0x34, 0x35, 0x36, 0x45, 0x57, 0x45,
				0x44, 0x46, 0x44, 0x46, 0x44, 0x44, 0x53, 0x53, 0x49, 0x44, 0x4A, 0xBF-256, 0xC9-256, 0xB0-256, 0xAE-256, 0x61,
				0x61, 0x61, 0xA1-256, 0xAA-256
		};
		System.out.println(ts.md5s(src));
	}
}
