package util;

import java.util.Enumeration;
import java.util.Vector;

public class Utils {

	// object to xml
	public static String objToXml(Object obj) {
		return "";
	}

	// xml to object
	public static Object xmlToObj(String xml) {
		if (isNullString(xml))
			return null;
		return "";

	}

	public static boolean isNullString(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static String join(Vector list, String splitter) {
		if (list == null)
			return "";
		String str = "";
		String sp = "";
		Enumeration en = list.elements();
		while (en.hasMoreElements()) {
			String v = (String) en.nextElement();
			str += sp + v;
			sp = splitter;
		}

		return str;
	}

}
