package util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class RetObj implements Serializable {

	private static final long serialVersionUID = 1L;

	public boolean success;

	public String errMsg;

	private String info;

	public RetObj(boolean success) {
		this.success = success;
		errMsg = "";
		info = Utils.objToXml(new Hashtable());
	}

	public RetObj(boolean success, String errMsg) {
		this.success = success;
		this.errMsg = errMsg;
		info = Utils.objToXml(new Hashtable());
	}

	public static RetObj succeed() {
		return new RetObj(true);
	}

	public static RetObj fail() {
		return fail("");
	}

	public static RetObj fail(String errMsg) {
		return new RetObj(false, errMsg);
	}

	public RetObj put(String key, Object value) {
		Hashtable values = (Hashtable) Utils
				.xmlToObj(info);
		values.put(key, Utils.objToXml(value));
		info = Utils.objToXml(values);
		return this;
	}

	public Object get(String key) {
		Hashtable values = (Hashtable) Utils
				.xmlToObj(info);
		String value = (String)values.get(key);
		if (Utils.isNullString(value))
			return null;
		return Utils.xmlToObj(value);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("");
		if (success) {
			sb.append("success");
		} else {
			sb.append("fail: ").append(errMsg);
		}
		Hashtable values = (Hashtable) Utils
				.xmlToObj(info);
		Enumeration en = values.elements();
		while(en.hasMoreElements()){
			String key = (String)en.nextElement();
			sb.append("\n[" + key + "] "
					+ values.get(key));
		}
		return sb.toString();
	}
}
