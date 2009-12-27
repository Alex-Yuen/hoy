package net.fxsprit.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.fxsprit.util.IdGenerator;

public class PostServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2122367685846192293L;
	
	private static final IdGenerator IDGENERATOR = new IdGenerator();
	
	public PostServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("This service requires POST method.");
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();		
		JSONObject jo = new JSONObject();
		
		try{
			if(req.getParameter("really")!=null&&"yes".equals(req.getParameter("really"))){
				// 把消息加入消息池
				String content = req.getParameter("message")!=null?(String)req.getParameter("message"):"";
				String fnId = IDGENERATOR.generateId(16);	
				
				ForexNews fn = new ForexNews();			
				fn.setId(fnId);
				fn.setContent(content);
				fn.setProvider("FXSPRIT");
				fn.setTime(System.currentTimeMillis());
				
				Messages.INFOMATION.add(fn);
				jo.put("status", "success");
			}else{
				jo.put("status", "failure");
			}
		}catch(Exception e){
			try{
				jo.put("status", "failure");
			}catch(Exception ex){
				ex.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			out.print(jo.toString());
			out.flush();
			out.close();
		}
		
	}
	
	

}
