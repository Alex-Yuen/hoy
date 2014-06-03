package ws.hoyland.as.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.sql.DataSource;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class GEServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1583815910306027840L;

	public GEServlet() {
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
        resp.getOutputStream().println("OK");
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
        return;
		// RequestDispatcher reqDispatcher =
		// req.getRequestDispatcher("/WEB-INF/view/index.jsp");
		// reqDispatcher.forward(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		byte[] key = null;
		Crypter crypter = new Crypter();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String result = "0";
		
		try{
			BufferedReader sis = req.getReader();
			char[] buf = new char[1024];
			int len = 0;
			StringBuffer sb = new StringBuffer();
			while ((len = sis.read(buf)) != -1) {
				sb.append(buf, 0, len);
			}
			//System.out.println("A");
			//System.out.println(sb.toString());
			//解密
			key = Converts.hexStringToByte(sb.toString().substring(0, 32));
			byte[] fmc = Converts.hexStringToByte(sb.toString().substring(32));
			//System.out.println("B");
			String lmc = Converts.bytesToHexString(crypter.decrypt(fmc, key));
			
			//System.out.println("0");
			String JNDINAME = "java:comp/env/jdbc/assistants";
			Connection conn = null;
			Statement stmt = null;
		 	ResultSet rs = null;
		 	
		 	Context ctx = new InitialContext();
			DataSource ds = (DataSource)ctx.lookup(JNDINAME);
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("select * from t_machine where machine_code = '"+lmc+"'");
			//System.out.println("select * from t_qqgm where machine_code = '"+lmc+"'");
			//sdf.format(Calendar.getInstance().getTime())
			
			if(rs.next()){
				//System.out.println("1");
				String expire = rs.getString("expire");
				int ct = rs.getInt("ct");
				int aid = rs.getInt("aid");
				System.out.println("ct="+ct);
				System.out.println("aid="+aid);
				//System.out.println(expire);
				long itv = sdf.parse(expire).getTime()-Calendar.getInstance().getTime().getTime();
				//System.out.println(itv/(24*60*60*1000));
				//System.out.println(itv%(24*60*60*1000));
				long day = (itv/(24*60*60*1000))+(((itv%(24*60*60*1000))>0)?1:0);
				if(itv>0){
					result = String.valueOf(day);
				}else{
					result = "0";
				}
				
				if(aid==4){
					result = result + "|" + ct;
				}
				//System.out.println("2");
			}
			System.out.println(lmc+"="+result);
			//sdf.parse(result)
			
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
	        //resp.getOutputStream().println("OK");
		}

		result = Converts.bytesToHexString(crypter.encrypt(result.getBytes(), key));
		
		resp.setContentType("text/html;charset=UTF-8");		
		resp.getOutputStream().print(result);
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
        return;
	}
}
