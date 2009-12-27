package net.fxsprit.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface Messages {
	public static Object LOCKER = new Object();
	public static List<String> INFOMATION = new ArrayList<String>();
	public static Map<HttpServletRequest, Boolean> UPDATED_FLAG = new HashMap<HttpServletRequest, Boolean>();
}
