package cn.buxland.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdvServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8242711730700073646L;

	public AdvServlet() {
		// TODO Auto-generated constructor stub.
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
//		resp.setContentType("text/html;charset=UTF-8");
//		PrintWriter out = resp.getWriter();
		
		String id = req.getParameter("id");
		try{
			if(id!=null&&"B9600E707A32CCE6".equals(id)){
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/neobux");  
				rd.forward(req, resp);   
//				out.print("");
//				out.flush();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			//out.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

	
}
