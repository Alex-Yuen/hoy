package ws.hoyland.cs.listener;

import java.net.URL;
import java.net.URLDecoder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class Log4j2ConfigListener implements ServletContextListener {

	public Log4j2ConfigListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		String xpath = null;
		try {
			URL url = this.getClass().getClassLoader().getResource("");
			xpath = url.getPath();

			xpath = xpath.substring(0, xpath.indexOf("/WEB-INF/"));
			xpath = URLDecoder.decode(xpath, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Setting webapp.base=" + xpath + "/WEB-INF/logs/");
		System.setProperty("webapp.base", xpath + "/WEB-INF/logs/");
		System.setProperty("webapp.home", xpath + "/WEB-INF/logs/");
		LoggerContext ctx = (LoggerContext) LogManager
				.getContext(false);
		ctx.reconfigure();
	}

}
