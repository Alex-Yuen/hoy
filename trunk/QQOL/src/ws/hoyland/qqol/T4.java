package ws.hoyland.qqol;

import ws.hoyland.util.Cookie;

public class T4 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cookie cookie = Cookie.getInstance();
		
		//cookie.put("123", "X", "F".getBytes());
		System.out.println(cookie.get("123"));
	}
}
