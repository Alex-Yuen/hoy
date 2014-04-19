package ws.hoyland.cs.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class GCServlet extends HttpServlet {

	private static final long serialVersionUID = 2740472298119695409L;

	public GCServlet() {
		
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String rs = Cookies.getInstance().peek();
		resp.setContentType("text/html;charset=UTF-8");
		resp.getOutputStream().println(rs);
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return;
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}
}
