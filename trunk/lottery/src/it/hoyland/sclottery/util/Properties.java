package it.hoyland.sclottery.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class Properties extends Hashtable {
	protected String lang;

	public Properties() {
		this.lang = "";
		loadResource();
	}

	public Properties(String lang) {
		this.lang = lang;
		loadResource();
	}

	private void loadResource() {
		String l = "/language.properties";
		if (this.lang != null && !this.lang.equals("")) {
			l = l.substring(0, l.indexOf(".")) + "_" + this.lang
					+ l.substring(l.indexOf("."));
		}

		InputStream is = this.getClass().getResourceAsStream(l);
		InputStreamReader isr = new InputStreamReader(is);
		String line;
		String key;
		String value;
		int index = -1;
		while ((line = readLine(isr)) != null) {
			if (!line.startsWith("#")) {
				index = line.indexOf("=");
				if (index != -1) {
					key = line.substring(0, index);
					value = line.substring(index + 1);
					put(key, convert(value));
				}
			}
		}
	}

	private String readLine(InputStreamReader reader) {
		StringBuffer stringBuffer = new StringBuffer();
		int i = -1;
		do {
			try {
				if ((i = reader.read()) == -1) {
					break;
				}
				char c = (char) i;
				if (c == '\n') {
					break;
				}
				if (c != '\r') {
					stringBuffer.append(c);
				}
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} while (true);

		if (stringBuffer.length() == 0) {
			if (i == -1) {
				return null;
			} else {
				return "";
			}
		} else {
			return stringBuffer.toString();
		}
	}

	private static String convert(String s) {
		int i = s.length();
		StringBuffer stringbuffer = new StringBuffer(i);
		char c;
		for (int j = 0; j < i;) {
			if ((c = s.charAt(j++)) == '\\') {
				if ((c = s.charAt(j++)) == 'u') {
					int k = 0;
					for (int l = 0; l < 4; l++) {
						switch (c = s.charAt(j++)) {
						case 48: // '0'
						case 49: // '1'
						case 50: // '2'
						case 51: // '3'
						case 52: // '4'
						case 53: // '5'
						case 54: // '6'
						case 55: // '7'
						case 56: // '8'
						case 57: // '9'
							k = ((k << 4) + c) - 48;
							break;

						case 97: // 'a'
						case 98: // 'b'
						case 99: // 'c'
						case 100: // 'd'
						case 101: // 'e'
						case 102: // 'f'
							k = ((k << 4) + 10 + c) - 97;
							break;

						case 65: // 'A'
						case 66: // 'B'
						case 67: // 'C'
						case 68: // 'D'
						case 69: // 'E'
						case 70: // 'F'
							k = ((k << 4) + 10 + c) - 65;
							break;

						case 58: // ':'
						case 59: // ';'
						case 60: // '<'
						case 61: // '='
						case 62: // '>'
						case 63: // '?'
						case 64: // '@'
						case 71: // 'G'
						case 72: // 'H'
						case 73: // 'I'
						case 74: // 'J'
						case 75: // 'K'
						case 76: // 'L'
						case 77: // 'M'
						case 78: // 'N'
						case 79: // 'O'
						case 80: // 'P'
						case 81: // 'Q'
						case 82: // 'R'
						case 83: // 'S'
						case 84: // 'T'
						case 85: // 'U'
						case 86: // 'V'
						case 87: // 'W'
						case 88: // 'X'
						case 89: // 'Y'
						case 90: // 'Z'
						case 91: // '['
						case 92: // '\\'
						case 93: // ']'
						case 94: // '^'
						case 95: // '_'
						case 96: // '`'
						default:
							return "?";
						}
					}

					stringbuffer.append((char) k);
				} else {
					if (c == 't') {
						c = '\t';
					} else if (c == 'r') {
						c = '\r';
					} else if (c == 'n') {
						c = '\n';
					} else if (c == 'f') {
						c = '\f';
					}
					stringbuffer.append(c);
				}
			} else {
				stringbuffer.append(c);
			}
		}

		return stringbuffer.toString();
	}

}
