package net.fxsprit.servlet;

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

	public FXServlet() {

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = new PrintWriter(resp.getOutputStream());
		out.println("This service requires POST method.");
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		if (!Messages.UPDATED_FLAG.containsKey(req)) {
			Messages.UPDATED_FLAG.put(req, new Boolean(true)); // 默认需要更新
		}
		PrintWriter out = null;
		List<Object> list = null;
		try {
			out = resp.getWriter();
			out
					.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			out.flush();
			// 监视消息池
			while (true) {
				// 复制数组
				if (Messages.UPDATED_FLAG.get(req).booleanValue()
						&& Messages.INFOMATION.size() > 0) {
					list = Arrays.asList(Messages.INFOMATION.toArray().clone());
					Messages.UPDATED_FLAG.put(req, new Boolean(false));// 已读
				}

				if (list != null) {
					Iterator<Object> it = list.iterator();
					while (it.hasNext()) {
						out.println((String) it.next());
						out.flush();
					}
				}
				// out.flush();
				list = null;
				Thread.sleep(500);
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
