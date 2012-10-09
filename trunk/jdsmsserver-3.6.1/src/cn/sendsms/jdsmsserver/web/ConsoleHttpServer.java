package cn.sendsms.jdsmsserver.web;

import cn.sendsms.jdsmsserver.JDSMSServer;
import cn.sendsms.jdsmsserver.interfaces.IPconfig;
import java.util.*;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

// Referenced classes of package cn.sendsms.jdsmsserver.web:
//            LoginServlet, LogoutServlet, DataSourceAction, DeviceConfigAction, 
//            SmsAction, LogAction, SecurityAction, SystemAction

public class ConsoleHttpServer
{
    class WebServer extends Thread
    {

        int port;
        final ConsoleHttpServer this$0;

        public void run()
        {
            Server httpServer = new Server();
            QueuedThreadPool pool = new QueuedThreadPool();
            pool.setMaxThreads(200);
            pool.setMinThreads(10);
            pool.setLowThreads(20);
            pool.setSpawnOrShrinkAt(2);
            httpServer.setThreadPool(pool);
            IPconfig iPconfig = new IPconfig();
            List ipList = null;
            try
            {
                ipList = iPconfig.getIPs();
            }
            catch(Exception e1)
            {
                ConsoleHttpServer.log.error("\u83B7\u53D6\u670D\u52A1\u5668IP\u5730\u5740\u5931\u8D25\uFF01", e1);
            }
            SelectChannelConnector connector;
            for(Iterator iterator = ipList.iterator(); iterator.hasNext(); httpServer.addConnector(connector))
            {
                String ip = (String)iterator.next();
                connector = new SelectChannelConnector();
                connector.setHost(ip);
                connector.setPort(port);
                connector.setMaxIdleTime(30000);
                connector.setAcceptors(2);
                connector.setStatsOn(false);
                connector.setConfidentialPort(8443);
                connector.setLowResourcesConnections(5000L);
            }

            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath("/");
            webapp.setWar("./console");
            webapp.setDefaultsDescriptor("./console/webdefault.xml");
            httpServer.addHandler(webapp);
            LoginServlet login = new LoginServlet();
            ServletHolder holder = new ServletHolder(login);
            webapp.addServlet(holder, "/logon");
            LogoutServlet logout = new LogoutServlet();
            holder = new ServletHolder(logout);
            webapp.addServlet(holder, "/logout");
            DataSourceAction datasource = new DataSourceAction();
            holder = new ServletHolder(datasource);
            webapp.addServlet(holder, "/datasource-config");
            webapp.addServlet("cn.sendsms.jdsmsserver.web.DataSourceAction", "/datasource-config");
            DeviceConfigAction device = new DeviceConfigAction();
            holder = new ServletHolder(device);
            webapp.addServlet(holder, "/device-config");
            SmsAction action = new SmsAction();
            holder = new ServletHolder(action);
            webapp.addServlet(holder, "/smsAction");
            LogAction log = new LogAction();
            holder = new ServletHolder(log);
            webapp.addServlet(holder, "/log");
            SecurityAction security = new SecurityAction();
            holder = new ServletHolder(security);
            webapp.addServlet(holder, "/security");
            SystemAction system = new SystemAction();
            holder = new ServletHolder(system);
            webapp.addServlet(holder, "/system");
            webapp.addHandler(new SessionHandler());
            try
            {
                httpServer.start();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        public WebServer(int myPort)
        {
            this$0 = ConsoleHttpServer.this;
            //super();
            port = myPort;
        }
    }


    WebServer webServer;
    private static ConsoleHttpServer server;
    private static Logger log = Logger.getRootLogger();

    private ConsoleHttpServer()
    {
        log.info("init ConsoleHttpServer");
    }

    public static ConsoleHttpServer getInstance()
    {
        if(server == null)
        {
            server = new ConsoleHttpServer();
        }
        return server;
    }

    public void start()
    {
        log.info("starting ConsoleHttpServer...");
        try
        {
            setWebServer(new WebServer(Integer.parseInt(JDSMSServer.getInstance().getProperties().getProperty("console.port", "8080"))));
            getWebServer().start();
        }
        catch(NumberFormatException e)
        {
            log.error("start ConsoleHttpServer error", e);
            return;
        }
        log.info("ConsoleHttpServer started");
    }

    public void stop()
    {
        log.info("stopping ConsoleHttpServer...");
        try
        {
            getWebServer().interrupt();
        }
        catch(Exception e)
        {
            log.error("stop ConsoleHttpServer error", e);
            return;
        }
        log.info("ConsoleHttpServer stopped");
    }

    WebServer getWebServer()
    {
        return webServer;
    }

    void setWebServer(WebServer myWebServer)
    {
        webServer = myWebServer;
    }


}
