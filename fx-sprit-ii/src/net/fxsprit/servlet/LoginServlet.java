package net.fxsprit.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.fxsprit.util.IdGenerator;

public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1842524239413309747L;

	private static final IdGenerator IDGENERATOR = new IdGenerator();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = new PrintWriter(resp.getOutputStream());
		out.println("This service requires POST method.");
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		JSONObject jo = new JSONObject();
		try{
			// 没有必要做验证
			String sessionId = IDGENERATOR.generateId(16);
			while(Messages.SESSIONS.keySet().contains(sessionId)){
				sessionId = IDGENERATOR.generateId(16);
			}
			Messages.SESSIONS.put(sessionId, null);
			jo.put("sessionid", sessionId);
			out.print(jo.toString());
			out.flush();			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			out.close();
		}
	}	
}
