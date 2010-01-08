package cn.buxland.servlet;

//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImgServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8809710091061869374L;

	public ImgServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
//		resp.setContentType("image/png;charset=UTF-8");
//		OutputStream out = resp.getOutputStream();
		
		String id = req.getParameter("id");
		try{
			if(id!=null&&"B9600E707A32CCE6".equals(id)){
				//read image and send
				
//				InputStream ins = getServletContext().getResourceAsStream("/images/B9600E707A32CCE6.png");
//				BufferedInputStream bis = new BufferedInputStream(ins);//输入缓冲流
//				BufferedOutputStream bos = new BufferedOutputStream(out);//输出缓冲流
//				
//				byte data[]=new byte[4096];//缓冲字节数
//				int size=0; 
//				size=bis.read(data);
//				while (size!=-1)
//				{
//				   bos.write(data,0,size);
//				   size=bis.read(data);
//				}
//				bis.close();
//				bos.flush();//清空输出缓冲流
//			    bos.close();
				resp.sendRedirect("/buxland/neobux/images/B9600E707A32CCE6.png");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
//			if(out!=null){
//				out.close();
//			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

	
}
