package net.fxsprit.servlet;

import java.io.IOException;
import java.net.ServerSocket;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class FXServlet implements Servlet {

	public FXServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

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
		try {
			boolean listening = true; // �Ƿ�Կͻ��˽��м���
			ServerSocket server = null; // ��������Socket����

			try {
				// ����һ��ServerSocket�ڶ˿�2121�����ͻ�����
				server = new ServerSocket(8012);
				System.out.println("Server starts...");
			} catch (Exception e) {
				System.out.println("Can not listen to. " + e);
			}

			while (listening) {
				// �������ͻ�����,���ݵõ���Socket����Ϳͻ��������������߳�,������֮
				new Thread(new ServiceThread(server.accept())).start();
			}
		} catch (Exception e) {
			System.out.println("Error. " + e);
		}
	}

	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
