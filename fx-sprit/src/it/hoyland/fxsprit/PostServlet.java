package it.hoyland.fxsprit;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2122367685846192293L;
	
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public PostServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("This service requires POST method.");
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		try{
			if(req.getParameter("really")!=null){
				// 把消息加入消息池
				Date now = Calendar.getInstance().getTime();
				String message = req.getParameter("message")!=null?(String)req.getParameter("message"):"";
				Message.INFOMATION.add(SDF.format(now) + ">>" + message);
				// 更新状态
				for(HttpServletRequest obj : Message.UPDATED_FLAG.keySet()){
					Message.UPDATED_FLAG.put(obj, new Boolean(true));
				}
				//System.out.println(req.getParameter("message"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>");
		out.println("Post Message");
		out.println("</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("Scucess. <a href=\"input\">Back</a>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	

}
