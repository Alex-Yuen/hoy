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

	public byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		//System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			try {
				ret[i] = Byte.decode("0x" + src.substring(i * 2, m)
						+ src.substring(m, n));
			} catch (NumberFormatException e) {
				ret[i] = (byte) (Integer.decode("0x"
						+ src.substring(i * 2, m) + src.substring(m, n))-256);
			}
		}
		return ret;
	}

	public String md5s(byte[] src) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			// md.update(plainText.getBytes("unicode"));
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
			// System.out.println("result: " + buf.toString());// 32位的加密
			// System.out.println("result: " + buf.toString().substring(8,
			// 24));// 16位的加密
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {

		Test ts = new Test();
		// System.out.println("\n***** Convert ASCII to Hex *****");
		// String str = "I Love Java!";
		// System.out.println("Original input : " + str);
		//
		// String hex = strToHex.convertStringToHex(str);
		// System.out.println("Hex : " + hex);
		// String hex =
		// "7573657269643D353732313430343226656D61696C3D686F797A68616E67403136332E636F6D2670617373776F72643D313233343536373826757064617465666C61673D3026617574686B65793D3236363137393533266578743D286E756C6C29266D61633D3744353584A338343234384238463439373641344239454436423933463338424334265665723D372E322E31323032323426506172746E657269643D302673613D3026696E695665723D53414446343335353446444023244066775E6B2A2870255E256566776473234040235E5E2523245E2633343536455745444644464444535349444ABFC9B0AE616161A1AA";//BFC9B0AE616161A1AA
		// //System.out.println("Hex : " + hex);
		// System.out.println("\n***** Convert Hex to ASCII *****");
		// System.out.println("Hex : " + hex);
		// String org = strToHex.convertHexToString(hex);
		// System.out.println("ASCII : " + org);
		// //06ebb85ee6245f7f50ae695cc9c6cde9
		// //org =
		// "userid=57214042&email=hoyzhang@163.com&password=12345678&updateflag=0&authkey=26617953&ext=(null)&mac=7D55£84248B8F4976A4B9ED6B93F38BC4&Ver=7.2.120224&Partnerid=0&sa=0&iniVer=SADF43554FD@#$@fw^k*(p%^%efwds#@@#^^%#$^&3456EWEDFDFDDSSIDJ可爱aaa—";
		// System.out.println("MD5 : " +
		// strToHex.md5s(strToHex.hexStr2Bytes("7573657269643D353732313430343226656D61696C3D686F797A68616E67403136332E636F6D2670617373776F72643D313233343536373826757064617465666C61673D3026617574686B65793D3236363137393533266578743D286E756C6C29266D61633D3744353584A338343234384238463439373641344239454436423933463338424334265665723D372E322E31323032323426506172746E657269643D302673613D3026696E695665723D53414446343335353446444023244066775E6B2A2870255E256566776473234040235E5E2523245E2633343536455745444644464444535349444ABFC9B0AE616161A1AA")));
		// //System.out.println(strToHex.md5s("6ac6901b0f5446dc260b63531921b6bf"));
		// System.out.println("MD5 : " + strToHex.md5s("is_hoyzhang@163.com"));
		//662b939fe15bcb854feba5857b53c30c		
		//ad474b00391880f85500a108eb95ce07
		//String cmd = "52756E5570646174653D3126456D61696C3D686F797A68616E67403136332E636F6D2650617373776F72643D31323334353637382667616D656E616D653D26617574686B65793D383838333030266578743D286E756C6C29266D61633D3541303032463937384633303458E137454639454644303644433042354434323242265665723D372E322E31323032323426506172746E657269643D302673613D3026696E695665723D372E322E313230363035266B65793D3636326239333966653135626362383534666562613538353762353363333063";
		//System.out.println("52756e5570646174653d3126456d61696c3d686f797a68616e67403136332e636f6d2650617373776f72643d31323334353637382667616d656e616d653d26617574686b65793d33353032353933266578743d286e756c6c29266d61633d3744353338343234384238463439373641344239454436423933463338424334265665723d372e322e31323032323426506172746e657269643d302673613d3026696e695665723d372e322e313230363035266b65793d6164343734623030333931383830663835353030613130386562393563653037".toUpperCase());
		String cmd = "52756e5570646174653d3126456d61696c3d686f797a68616e67403136332e636f6d2650617373776f72643d31323334353637382667616d656e616d653d26617574686b65793d35343335363838266578743d286e756c6c29266d61633d3744353338343234384238463439373641344239454436423933463338424334265665723d372e322e31323032323426506172746e657269643d302673613d3026696e695665723d372e322e313230363035".toUpperCase();
//		System.out.println(cmd);
		String tail = "53414446343335353446444023244066775E6B2A2870255E256566776473234040235E5E2523245E2633343536455745444644464444535349444ABFC9B0AE616161A1AA";
		//String tail = "53414446343335353446444023244066775E6B2A2870255E256566776473234040235E5E2523245E2633343536455745444644464444535349444ABFC9B0AE616161A1AA";
//		cmd = cmd.substring(0, cmd.indexOf("266B65793D"));
		cmd += tail;
		//System.out.println(cmd);
		byte[] src = ts.hexStr2Bytes(cmd);
		System.out.println(ts.md5s(src));

		
		//测试：
		//authkey变换是否可行
		
		//生成原始字符串，可以生成MD5，如何从原始字符生成变化后的MAC，则是另外一个问题
	}
}
