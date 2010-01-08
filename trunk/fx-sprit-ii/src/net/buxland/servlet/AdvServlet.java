package net.buxland.servlet;

import java.io.IOException;
//import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

//import javax.servlet.RequestDispatcher;
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
		// super.doGet(req, resp);
		// resp.setContentType("text/html;charset=UTF-8");
		//rintWriter out = resp.getWriter();

		String id = req.getParameter("id");
		try {
			if (id != null && "B9600E707A32CCE6".equals(id)) {
				// RequestDispatcher rd =
				// getServletContext().getRequestDispatcher("/neobux/index.html");
				// rd.forward(req, resp);
				// new Thread(new
				// ClickSaver(getServletContext().getRealPath("/WEB-INF/log/"+id))).start();
				// new
				// ClickSaver(getServletContext().getRealPath("/WEB-INF/log/"+id),
				// out).run();
				//new Thread(new ClickSaver(id)).start();
			//	new ClickSaver(id, out).run();
				resp.sendRedirect("http://www.neobux.com/?rh=686F797A68616E67");
				// out.print("");
				// out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// out.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

}

class ClickSaver implements Runnable {
	// private String path;
	//private PrintWriter out;
	private String id;

	public ClickSaver(String id) {
		this.id = id;
	//	this.out = out;
	}

	@Override
	public void run() {
		// File file = null;
		// FileInputStream fin = null;
		// BufferedReader reader = null;
		// FileOutputStream fout = null;
		// BufferedWriter writer = null;
		// try{
		// out.println("1");
		// int count = 0;
		// file = new File(path);
		// boolean createFlag = false;
		// if(!file.exists()){
		// out.println("2"+path);
		// createFlag = file.createNewFile();
		// if(!createFlag){
		// throw new Exception("Can't not create files");
		// }
		// }
		// out.println("3");
		//			
		// fin = new FileInputStream(file);
		// reader = new BufferedReader(new InputStreamReader(fin));
		// String line = reader.readLine();
		//			
		// if(line!=null){
		// count = Integer.parseInt(line);
		// }
		// out.println("4");
		// // 写文件
		// reader.close();
		// fin.close();
		//
		// count++;
		// fout = new FileOutputStream(file);
		// writer = new BufferedWriter(new OutputStreamWriter(fout));
		// writer.write(""+count);
		// out.println("5");
		// writer.close();
		// fout.close();
		// }catch(Exception e){
		// e.printStackTrace();
		// }
		Statement stmt = null;
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//out.println("1");
			Properties prop = new Properties();
			prop.setProperty("useUnicode", "true");
			prop.setProperty("characterEncoding", "utf-8");
			String url = "jdbc:mysql://127.0.0.1:3306/x?user=x&password=x";
			//out.println("2");
			con = DriverManager.getConnection(url, prop);
			//out.println("3");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			//out.println("4");

			stmt.executeUpdate("update advertisement set clicks = clicks+1 where id='"
							+ this.id + "'");
			stmt.close();
			con.close();
			//out.println("5"+r);
		} catch (Exception e) {
			e.printStackTrace();
			//out.println("6"+e.getMessage());
		}
	}

}
