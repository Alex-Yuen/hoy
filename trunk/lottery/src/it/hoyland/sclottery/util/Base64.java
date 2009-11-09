package it.hoyland.sclottery.util;

public final class Base64 {

	private static char array[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();

	public Base64() {

	}

	public static String convert(byte abyte0[]) {
		int i = abyte0.length;
		StringBuffer stringbuffer = new StringBuffer((abyte0.length * 3) / 2);
		int j = i - 3;
		int k = 0;
		int l = 0;
		do {
			if (k > j) {
				break;
			}
			int i1 = (abyte0[k] & 0xff) << 16 | (abyte0[k + 1] & 0xff) << 8
					| abyte0[k + 2] & 0xff;
			stringbuffer.append(array[i1 >> 18 & 0x3f]);
			stringbuffer.append(array[i1 >> 12 & 0x3f]);
			stringbuffer.append(array[i1 >> 6 & 0x3f]);
			stringbuffer.append(array[i1 & 0x3f]);
			k += 3;
			if (l++ >= 14) {
				l = 0;
				stringbuffer.append("\r\n");
			}
		} while (true);
		if (k == (i + 0) - 2) {
			int j1 = (abyte0[k] & 0xff) << 16 | (abyte0[k + 1] & 0xff) << 8;
			stringbuffer.append(array[j1 >> 18 & 0x3f]);
			stringbuffer.append(array[j1 >> 12 & 0x3f]);
			stringbuffer.append(array[j1 >> 6 & 0x3f]);
			stringbuffer.append("=");
		} else if (k == (i + 0) - 1) {
			int k1 = (abyte0[k] & 0xff) << 16;
			stringbuffer.append(array[k1 >> 18 & 0x3f]);
			stringbuffer.append(array[k1 >> 12 & 0x3f]);
			stringbuffer.append("==");
		}
		return stringbuffer.toString();
	}
}
