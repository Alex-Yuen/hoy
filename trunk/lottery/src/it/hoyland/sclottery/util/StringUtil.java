package it.hoyland.sclottery.util;

import java.util.Vector;

public class StringUtil {

	public static Vector split(String s, char c) {
		Vector vector = new Vector();
		String s2 = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) {
				vector.addElement(s2);
				s2 = "";
			} else {
				s2 = s2 + s.charAt(i);
			}
		}

		if (s2 != "") {
			vector.addElement(s2);
		}
		vector.trimToSize();
		return vector;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
