package net.fxsprit.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7195225526662457905L;

	public IndexServlet() {
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		//resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<Meta http-equiv=\"Content-Type\" Content=\"text/html; Charset=utf-8\">");
		out.println("<title>");
		out.println("Input Message");
		out.println("</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<form action=\"../fx-api/post\" method=\"post\">");
		out.println("<textarea name=\"message\" rows=\"10\" cols=\"50\"></textarea><br/>");
		out.println("<input name=\"really\" type=\"checkbox\" value=\"yes\"> <a href=\"#\" onClick=\"javascript: document.all('really').checked = !document.all('really').checked;\">I am sure.</a><br/><br/>");
		out.println("<input name=\"submit\" type=\"submit\" value=\"submit\"/>");
		out.println("</form>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = new PrintWriter(resp.getOutputStream());
		out.println("This service requires GET method.");
		out.close();
	}

	
}
