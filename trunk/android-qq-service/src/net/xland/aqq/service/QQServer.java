package net.xland.aqq.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

public class QQServer {
	public static void main(String[] args) {
	    Server server = new Server(8084);
	    ContextHandler context = new ContextHandler();  
        context.setContextPath("/");  
        context.setResourceBase(".");  
        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setHandler(new RootHandler());        
        server.setHandler(context);
        try{
	        server.start();  
	        server.join();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
}
