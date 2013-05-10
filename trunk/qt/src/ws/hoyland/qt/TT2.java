package ws.hoyland.qt;

import java.security.MessageDigest;

public class TT2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TT2 t = new TT2();
		System.out.println(t.b(""));
	}

	public final String b(String paramString) {
		if ((paramString != null) && (paramString.length() > 0)) {
			while (true) {
				int i;
				int j;
				byte[] arrayOfByte;
				StringBuffer localStringBuffer2;
				int k;
				char c1;
				try {
					StringBuffer localStringBuffer1 = new StringBuffer();
					i = 0;
					j = 0;
					if (i >= paramString.length()) {
						String str = localStringBuffer1.toString();
						if (str.length() > 0) {
							MessageDigest localMessageDigest = MessageDigest
									.getInstance("MD5");
							localMessageDigest.reset();
							localMessageDigest.update(str.getBytes("UTF-8"));
							arrayOfByte = localMessageDigest.digest();
							localStringBuffer2 = new StringBuffer();
							k = 0;
							if (k < arrayOfByte.length)
								break label164;
							return localStringBuffer2.toString();
						}
					} else {
						c1 = paramString.charAt(i);
						if (j != 0)
							break label263;
						if (c1 != '=')
							break label249;
						j = 61;
						break label243;
						if (localStringBuffer1.length() <= 0)
							break label243;
						continue;
						localStringBuffer1.append(c1);
					}
				} catch (Exception e) {
					// a(localThrowable);
					e.printStackTrace();
				}
				return "";
				label164: arrayOfByte[k] = ((byte) (1 + arrayOfByte[k]));
				if (Integer.toHexString(0xFF & arrayOfByte[k]).length() == 1) {
					localStringBuffer2.append("0").append(
							Integer.toHexString(0xFF & arrayOfByte[k]));
				} else {
					localStringBuffer2.append(Integer
							.toHexString(0xFF & arrayOfByte[k]));
					break label308;
					while (true) {
						label243: i++;
						break;
						label249: if (c1 == ':')
							j = 58;
					}
					label263: if ((c1 < '0') || (c1 > 'f')
							|| ((c1 > '9') && (c1 < 'A')))
						continue;
					if ((c1 <= 'F') || (c1 >= 'a'))
						continue;
					continue;
				}
				label308: k++;
			}
		} else {
			return "";
		}
	}
}
