package net.xland.aqq.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.xland.aqq.service.task.*;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class RootHandler extends AbstractHandler {
	private QQServer server;
//	private static Logger logger = LogManager.getLogger(PacketSender.class.getName());
	
	public RootHandler(QQServer server) {
		this.server = server;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		
		String context = request.getRequestURI();
		PrintWriter writer = response.getWriter();
		if(context.equals("/")){
			writer.println("<h1>Android QQ Service</h1>");
			writer.println("Usage:<br/><br/>");
			writer.println("第一步[提交手机号码]：<br/>&nbsp;&nbsp;http://127.0.0.1:8084/register?action=mobile&value=13682760033<br/><br/>");
			writer.println("第二步[提交验证码]：<br/>&nbsp;&nbsp;http://127.0.0.1:8084/register?action=code&value=123456&sid=07b1f82cb7fa631d373276866ebf5762<br/><br/>");
			writer.println("第三步[提交昵称]：<br/>&nbsp;&nbsp;http://127.0.0.1:8084/register?action=nick&value=xland&sid=07b1f82cb7fa631d373276866ebf5762<br/><br/>");
		}else if(context.equals("/register")){
			//mobile, code, nick
			boolean valid = true;
			String action = request.getParameter("action");
			String value = request.getParameter("value");
			String sid = request.getParameter("sid");
			
//			logger.info("*"+" [REQUEST] "+action);
//			System.out.println("* [REQUEST] "+action);
			Map<String, Object> session = null; //task future
			
			if(action!=null){
				if("mobile".equals(action)){
					if(value!=null){
						session = this.server.addTask(new MobileTask(value)); //有任务来就新建线程，组装bytecontent，并交给发送引擎发送
					}else{
						valid = false;
					}
				}else if("code".equals(action)){
					if(value!=null&&sid!=null){
						session = this.server.addTask(new CodeTask(sid, value));
					}else{
						valid = false;
					}
				}else if("nick".equals(action)){
					if(value!=null&&sid!=null){
						session = this.server.addTask(new NickTask(sid, value));
					}else {
						valid = false;
					}
				}else{
					valid = false;
				}
			}else{
				valid = false;
			}
			
			if(valid&&session!=null){
				int wt = 4;
				if("mobile".equals(action)){
					wt += 6 + 5;
				}
				synchronized(session){
					try{
						session.wait(wt*1000);    //等待TCP返回
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
//				if("1".equals(session.get("x-nf"))){
//					synchronized(session){
//						try{
//							session.wait(5000);    //等待TCP返回 BIND TASK
//						}catch(Exception e){
//							e.printStackTrace();
//						}
//					}
//				}
				//根据session, 打印不同的结果
				if("-1".equals(session.get("x-status"))||("-4".equals(session.get("x-status"))&&("mobile".equals(session.get("x-cmd"))||"bind".equals(session.get("x-cmd"))))){
					writer.println(session.get("x-status"));
					writer.println("send-packet-timeout:"+session.get("x-cmd"));
				}else{
					writer.println(session.get("x-status"));
					if("nick".equals(session.get("x-cmd"))&&"0".equals(session.get("x-status"))){
						writer.println(session.get("x-result")+":"+session.get("x-qqnumber")+"="+session.get("x-pwd"));
					}else{
						writer.println(session.get("x-result"));
					}
				}
				writer.println(session.get("x-sid"));
			}else {
				writer.println("<h1>Android QQ Service</h1>");
				writer.println("Bad Request");
			}
		}else{
			writer.println("<h1>Android QQ Service</h1>");
			writer.println("Bad Request");
		}
		
		if(writer!=null){
			writer.close();
		}
	}
}
