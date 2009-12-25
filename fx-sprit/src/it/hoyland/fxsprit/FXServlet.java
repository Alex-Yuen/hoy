package it.hoyland.fxsprit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FXServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9011758636253668237L;

	private boolean flag = true;

	public FXServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (flag) {
			this.doPost(req, resp);
		} else {
			PrintWriter out = new PrintWriter(resp.getOutputStream());
			out.println("The SNS Photo Service requires POST method.");
			out.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");

		PrintWriter out = null;
		try {
			out = new PrintWriter(resp.getOutputStream());
			int i = 0;
			while (i++<50) {
				out.println("hello");
				Thread.sleep(200);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

}
