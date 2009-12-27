package net.fxsprit.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Messages {
	/**
	 * 保存财经新闻列表
	 */
	public static List<ForexNews> INFOMATION = new ArrayList<ForexNews>();
	
	/**
	 * 保存SessionId 和 LastRequestTime
	 */
	public static Map<String, Long> SESSIONS = new HashMap<String, Long>();
}
