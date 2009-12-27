package net.fxsprit.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;

public class ServiceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9011758636253668237L;

	public ServiceServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = new PrintWriter(resp.getOutputStream());
		out.println("This service requires POST method.");
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		String sessionId = req.getParameter("SESSIONID");
				
		List<ForexNews> list = null;
		JSONObject jo = new JSONObject();
		try {
			if(sessionId==null||!Messages.SESSIONS.keySet().contains(sessionId)){
				jo.put("status", "failure");
				jo.put("error", "INVALID SESSIONID");
			}else{
				JSONArray ja = new JSONArray();
				Long lastRequestTime = Messages.SESSIONS.get(sessionId);
				long currentTime = System.currentTimeMillis();
				// 根据获取新闻列表
				if(lastRequestTime==null){ //返回全部消息
					for(ForexNews fn : Messages.INFOMATION){
						ja.put(fn);
					}
				}else{ //返回>lastRequestTime 并 <=currentTime的所有消息
					list = getForexNewsList(lastRequestTime.longValue(), currentTime);
					for(ForexNews fn : list){
						ja.put(fn);
					}
				}
				// 更新最后请求时间
				Messages.SESSIONS.put(sessionId, currentTime);
				jo.put("status", "success");
				jo.put("list", ja);
			}
			
			out.print(jo.toString());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	private List<ForexNews> getForexNewsList(long lastRequestTime, long currentTime) {
		List<ForexNews> list = new ArrayList<ForexNews>();
		for(ForexNews fn : Messages.INFOMATION){
			if(fn.getTime()>lastRequestTime&&fn.getTime()<=currentTime){
				list.add(fn);
			}
		}
		return list;
	}
}
