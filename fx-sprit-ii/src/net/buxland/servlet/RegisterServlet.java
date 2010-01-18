package net.buxland.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class RegisterServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6515780755304988341L;

	public RegisterServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher reqDispatcher = req.getRequestDispatcher("/WEB-INF/view/register.jsp");  
        reqDispatcher.forward(req ,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//PrintWriter out = resp.getWriter();
		resp.setContentType("text/hmtl; charset=utf-8");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String email = req.getParameter("email");
		
		final String JNDINAME = "java:comp/env/jdbc/buxland";
		Connection conn = null;
		Statement stmt = null;
	 	int rs = 0;
	 	try{
	 	  	Context ctx = new InitialContext();
	 	  	DataSource ds = (DataSource)ctx.lookup(JNDINAME);
	 	  	conn = ds.getConnection();
	 	  	stmt = conn.createStatement();
	 	  	String sql = "insert into BUX_USER (USER_NAME, MD5_PWD, EMAIL) values('"+username+"', md5('"+password+"'), '"+email+"')";
	 	  	rs = stmt.executeUpdate(sql);
	 	  	if(rs>0){
	 	  		req.getSession().setAttribute("REGISTER_RESULT", "1");
	 	  	}else{
	 	  		req.getSession().setAttribute("REGISTER_RESULT", "0");
	 	  	}
	 	}catch(Exception e){
	 		req.getSession().setAttribute("REGISTER_RESULT", "0");
	 		req.getSession().setAttribute("REGISTER_RESULT_MSG", e.getMessage());
	 		//e.printStackTrace(out);
	 	}finally{
	 		try {
	 			if(stmt!=null){
	 				stmt.close();
	 			}
	 			if(conn!=null){
	 				conn.close();
	 			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 	}
	 	
	 	RequestDispatcher reqDispatcher = req.getRequestDispatcher("/WEB-INF/view/register-result.jsp");  
        reqDispatcher.forward(req ,resp);
	}

	
}
