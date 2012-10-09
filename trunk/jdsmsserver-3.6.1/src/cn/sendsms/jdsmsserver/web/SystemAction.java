/*     */ package cn.sendsms.jdsmsserver.web;
/*     */ 
/*     */ import cn.sendsms.jdsmsserver.JDSMSServer;
/*     */ import cn.sendsms.jdsmsserver.interfaces.Interface;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Properties;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.commons.io.FileUtils;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class SystemAction extends JDWebAction
/*     */ {
/*     */   private String delete_after_processing;
/*     */   private String send_mode;
/*     */   private String log_level;
/*     */   private Integer inbound_interval;
/*     */   private Integer outbound_interval;
/*     */   private Integer port;
/*     */   private String password_send;
/*     */   private String password_read;
/*     */   private String sendURL;
/*     */   private String readURL;
/*     */ 
/*     */   public void Init(HttpServletRequest req, HttpServletResponse resp)
/*     */     throws ServletException, IOException
/*     */   {
/*  34 */     this.delete_after_processing = JDSMSServer.getInstance().getProperties().getProperty("settings.delete_after_processing", "");
/*  35 */     this.send_mode = JDSMSServer.getInstance().getProperties().getProperty("settings.send_mode", "");
/*  36 */     this.inbound_interval = Integer.valueOf(Integer.parseInt(JDSMSServer.getInstance().getProperties().getProperty("settings.inbound_interval", "0")));
/*  37 */     this.outbound_interval = Integer.valueOf(Integer.parseInt(JDSMSServer.getInstance().getProperties().getProperty("settings.outbound_interval", "0")));
/*  38 */     this.log_level = this.log.getLevel().toString();
/*  39 */     this.port = Integer.valueOf(Integer.parseInt(JDSMSServer.getInstance().getProperties().getProperty("httpServer.port", "0")));
/*  40 */     this.password_send = JDSMSServer.getInstance().getProperties().getProperty("httpServer.password.send", "");
/*  41 */     this.password_read = JDSMSServer.getInstance().getProperties().getProperty("httpServer.password.read", "");
/*     */ 
/*  43 */     this.sendURL = ("http://127.0.0.1:" + this.port + "/send?password=[密码]&text=[内容]&recipient=[手机号]");
/*  44 */     this.readURL = ("http://127.0.0.1:" + this.port + "/read?password=[密码]&gateway=modem[设备序号]");
/*     */ 
/*  46 */     findForward("serviceManager.jsp", true, req, resp);
/*     */   }
/*     */ 
/*     */   public void swit(HttpServletRequest req, HttpServletResponse resp)
/*     */     throws ServletException, IOException
/*     */   {
			    DbHelper hepler = null;
			    try {
			      hepler = DbHelper.getDbHelper(JDSMSServer.getInstance().getProperties(), "db1");
			      boolean[] st = hepler.getSwitchStatus();
			      req.setAttribute("st", st);
			      findForward("/swit.jsp", true, req, resp);
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      req.setAttribute("st", null);
			      req.setAttribute("message", "访问数据源失败，请确认数据源是否配置正确");
			      findForward("/swit.jsp", true, req, resp);
			    }
/*     */   }

/*     */   public void list(HttpServletRequest req, HttpServletResponse resp)
/*     */     throws ServletException, IOException
/*     */   {
	
/*  46 */     findForward("list.jsp", true, req, resp);
/*     */   }

/*     */   public void saveSystem(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
/*     */   {
/*     */     try
/*     */     {
/*  53 */       FileOutputStream f = FileUtils.openOutputStream(FileUtils.getFile(new String[] { System.getProperty("user.dir"), "conf", "JDSMSServer.conf" }));
/*     */ 
/*  55 */       FileInputStream log = FileUtils.openInputStream(FileUtils.getFile(new String[] { System.getProperty("user.dir"), "conf", "debug.conf" }));
/*     */ 
/*  57 */       if (req.getParameter("delete_after_processing") != null)
/*  58 */         JDSMSServer.getInstance().getProperties().setProperty("settings.delete_after_processing", "yes");
/*     */       else
/*  60 */         JDSMSServer.getInstance().getProperties().setProperty("settings.delete_after_processing", "no");
/*  61 */       JDSMSServer.getInstance().getProperties().setProperty("settings.send_mode", this.send_mode);
/*  62 */       JDSMSServer.getInstance().getProperties().setProperty("settings.inbound_interval", this.inbound_interval.toString());
/*  63 */       JDSMSServer.getInstance().getProperties().setProperty("settings.outbound_interval", this.outbound_interval.toString());
/*     */ 
/*  65 */       JDSMSServer.getInstance().getProperties().store(f, "");
/*  66 */       f.flush();
/*  67 */       f.close();
/*     */ 
/*  69 */       Properties lp = new Properties();
/*  70 */       lp.load(log);
/*  71 */       log.close();
/*  72 */       lp.setProperty("log4j.rootLogger", this.log_level + ", A1, A2");
/*  73 */       FileOutputStream lo = FileUtils.openOutputStream(FileUtils.getFile(new String[] { System.getProperty("user.dir"), "conf", "debug.conf" }));
/*  74 */       lp.store(lo, "");
/*  75 */       lo.flush();
/*  76 */       lo.close();
/*  77 */       this.log.setLevel(Level.toLevel(this.log_level));
/*     */     }
/*     */     catch (Exception e) {
/*  80 */       this.log.error("系统设置失败", e);
/*  81 */       req.setAttribute("message", "系统设置失败！");
/*  82 */       Init(req, resp);
/*  83 */       return;
/*     */     }
/*  85 */     req.setAttribute("message", "系统设置已生效！");
/*  86 */     Init(req, resp);
/*     */   }
/*     */ 
/*     */   public void saveHttpServer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
/*     */   {
/*     */     try
/*     */     {
/*  93 */       FileOutputStream f = FileUtils.openOutputStream(FileUtils.getFile(new String[] { System.getProperty("user.dir"), "conf", "JDSMSServer.conf" }));
/*     */ 
/*  95 */       boolean needRestart = !JDSMSServer.getInstance().getProperties().get("httpServer.port").equals(this.port.toString());
/*     */ 
/*  97 */       JDSMSServer.getInstance().getProperties().setProperty("httpServer.port", this.port.toString());
/*  98 */       JDSMSServer.getInstance().getProperties().setProperty("httpServer.password.read", this.password_read);
/*  99 */       JDSMSServer.getInstance().getProperties().setProperty("httpServer.password.send", this.password_send);
/*     */ 
/* 101 */       JDSMSServer.getInstance().getProperties().store(f, "");
/* 102 */       f.flush();
/* 103 */       f.close();
/*     */ 
/* 105 */       if (needRestart) {
/* 106 */         for (Interface inf : JDSMSServer.getInstance().getInfList())
/* 107 */           if (inf.getId().equals("httpServer")) {
/* 108 */             inf.stop();
/* 109 */             inf.start();
/*     */           }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 115 */       req.setAttribute("message", "设置HttpServer接口失败！");
/* 116 */       this.log.error("设置HttpServer接口失败！", e);
/* 117 */       Init(req, resp);
/* 118 */       return;
/*     */     }
/* 120 */     req.setAttribute("message", "设置HttpServer接口成功！");
/*     */ 
/* 122 */     Init(req, resp);
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\Desktop\jdsmsserver-3.6.1.jar
 * Qualified Name:     cn.sendsms.jdsmsserver.web.SystemAction
 * JD-Core Version:    0.6.0
 */