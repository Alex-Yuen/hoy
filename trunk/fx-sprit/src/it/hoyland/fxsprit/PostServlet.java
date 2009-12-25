package it.hoyland.fxsprit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PostServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2122367685846192293L;

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
				System.out.println(req.getParameter("message"));
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
