package it.hoyland.fxsprit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
			out.println("This service requires POST method.");
			out.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		if(!Message.UPDATED_FLAG.containsKey(req)){
			Message.UPDATED_FLAG.put(req, new Boolean(true)); // 默认需要更新
		}
		PrintWriter out = null;
		List<Object> list = null;
		try {
			out = resp.getWriter();
			//监视消息池
			while (true) {
				//复制数组
				if(Message.UPDATED_FLAG.get(req).booleanValue() && Message.INFOMATION.size()>0){					
					list = Arrays.asList(Message.INFOMATION.toArray().clone());
					Message.UPDATED_FLAG.put(req, new Boolean(false));// 已读
				}
				
				Iterator<Object> it = list.iterator();
				while(it.hasNext()){
					out.println((String)it.next());
				}
				Thread.sleep(5000);
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
