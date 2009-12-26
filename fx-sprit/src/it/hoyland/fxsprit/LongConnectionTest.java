package it.hoyland.fxsprit;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LongConnectionTest extends javax.servlet.http.HttpServlet
		implements javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

	public LongConnectionTest() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

//		System.out.println(response.getBufferSize());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter pr = response.getWriter();
		//PrintWriter pr = new PrintWriter(response.getOutputStream(), true);
		
		try {
			int i = 0;
			while (true) {
				if(i==0){
				pr.println("有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信有时候你不得不相信");
				
				// flush的作用很重要，当你任务写给客户端的数据总够多的时候
				// 调用之，客户端方能读取到。
				// 否则，在数据长度达到上限或者连接关闭之前，客户端读不到数据
				//System.out.println(i++);
				i = 1;
				pr.flush();
				System.out.println(response.getBufferSize());
				System.out.println(pr.checkError());
				}
				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}