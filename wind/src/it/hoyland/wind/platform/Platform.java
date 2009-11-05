package it.hoyland.wind.platform;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.hoyland.wind.core.Component;
//import it.hoyland.wind.core.ServletStore;

public class Platform implements Component {

	public Platform() {
		// TODO Auto-generated constructor stub
		// ServletStore.getInstance().put("/index", new TestServlet());
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		System.out.println("load...");
	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub
		System.out.println("unload...");
	}

}

class TestServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2647380994245540367L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(arg0, arg1);
		// System.out.println("here");
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(arg0, arg1);
		// System.out.println("here2");
	}
	
	
}