package it.hoyland.wind;

import it.hoyland.wind.core.Component;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1801226144419611409L;

	private Object pf;
	
	public InitServlet() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("destroy...");
		if(this.pf!=null && this.pf instanceof Component){
			((Component)this.pf).onUnload();
		}
	}

	@Override
	public ServletConfig getServletConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("init...");
		
		// Looking for Platform class
		try {
			Class<?> clazz = Class.forName("it.hoyland.wind.platform.Platform");
			this.pf = clazz.newInstance();
			// System.out.println(this.pf!=null);
			if(this.pf instanceof Component){
				((Component)this.pf).onLoad();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
