package it.hoyland.wind;

import it.hoyland.wind.core.ServletStore;

import java.io.IOException;

//import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通过此类进行转发
 * @author Administrator
 *
 */
public class DispatchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -426776010760141990L;

	public DispatchServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doPost(req, resp);
		//RequestDispatcher requestDis = req.getRequestDispatcher("");
		
		String key = req.getServletPath();
		if(ServletStore.getInstance().get(key)!=null){
			Object servlet = ServletStore.getInstance().get(key);
			if(servlet instanceof HttpServlet){
				((HttpServlet)servlet).service(req, resp);
			}
			return;
		}
		//this.getServletContext().
		
		System.out.println("come here");
		resp.getWriter().write("hello");
		resp.getWriter().flush();
	}

}
