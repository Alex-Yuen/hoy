package net.xland.aqq.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class RootHandler extends AbstractHandler {

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		String context = request.getRequestURI();
		PrintWriter writer = response.getWriter();
		if(context.equals("/")){
			writer.println("<h1>Android QQ Service</h1>");
			writer.println("Usage:<br/><br/>");
			writer.println("第一步[提交手机号码]：<br/>&nbsp;&nbsp;http://127.0.0.1:8084/register?action=mobile&value=13682760033<br/><br/>");
			writer.println("第二步[提交验证码]：<br/>&nbsp;&nbsp;http://127.0.0.1:8084/register?action=code&value=123456&sid=hbwg4ky9pu1ui5hc<br/><br/>");
			writer.println("第三步[提交昵称]：<br/>&nbsp;&nbsp;http://127.0.0.1:8084/register?action=nick&value=xland&sid=hbwg4ky9pu1ui5hc<br/><br/>");
		}else if(context.equals("/register")){
			//mobile, bind, code, nick  
			String action = request.getQueryString();			
			writer.println("<h1>Android QQ Service</h1>");
			writer.println(action);
		}else{
			writer.println("<h1>Android QQ Service</h1>");
			writer.println("Bad Request");
		}
		
		if(writer!=null){
			writer.close();
		}
	}

}
