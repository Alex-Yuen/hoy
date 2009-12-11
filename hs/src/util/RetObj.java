package util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RetObj implements Serializable {

	private static final long serialVersionUID = 1L;

	public boolean success;

	public String errMsg;

	private String info;

	public RetObj(boolean success) {
		this.success = success;
		errMsg = "";
		info = Utils.objToXml(new HashMap<String, String>());
	}

	public RetObj(boolean success, String errMsg) {
		this.success = success;
		this.errMsg = errMsg;
		info = Utils.objToXml(new HashMap<String, String>());
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

	@SuppressWarnings("unchecked")
	public RetObj put(String key, Object value) {
		HashMap<String, String> values = (HashMap<String, String>) Utils
				.xmlToObj(info);
		values.put(key, Utils.objToXml(value));
		info = Utils.objToXml(values);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Object get(String key) {
		HashMap<String, String> values = (HashMap<String, String>) Utils
				.xmlToObj(info);
		String value = values.get(key);
		if (Utils.isNullString(value))
			return null;
		return Utils.xmlToObj(value);
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		if (success) {
			sb.append("success");
		} else {
			sb.append("fail: ").append(errMsg);
		}
		HashMap<String, String> values = (HashMap<String, String>) Utils
				.xmlToObj(info);
		for (Map.Entry<String, String> entry : values.entrySet()) {
			sb.append("\n[" + entry.getKey() + "] "
					+ entry.getValue().toString());
		}
		return sb.toString();
	}
	
}
