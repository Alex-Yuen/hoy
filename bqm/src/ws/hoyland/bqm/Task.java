package ws.hoyland.bqm;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

public class Task implements Runnable {

	private TableItem item;
	private ICallback cb;
	private String to;
	private String title;
	private String content;
	private Map<String, Byte> ss;
	private Map<String, Byte> sbs;
	private Random rnd;
	private String[] smtp;
	private int MSS = 3;
	private int MSBS = 3;

	public Task(TableItem item, Map<String, Byte> ss, Map<String, Byte> sbs, String mail,
			ICallback cb) {
		this.item = item;
		this.cb = cb;
		this.ss = ss;
		this.sbs = sbs;
		//this.title = mail[0].replaceFirst("\\{\\*\\}", rs(6, 2));		
		//this.content = mail[1].replaceFirst("\\{\\*\\}", rs(8, 4));
//		this.title = mail[0];
//		while(this.title.contains("{*}")){
//			this.title = this.title.replaceFirst("\\{\\*\\}", rs(6, 2));
//		}
		
		this.content = mail;
		this.content = deal(this.content);
		
		this.to = item.getText(1);
		this.rnd = new Random();
	}

	private String deal(String ct) {
		while(ct.contains("{c}")){
			ct = ct.replaceFirst("\\{c\\}", cs(9, 3)); 
		}
		
		while(ct.contains("{*}")){
			ct = ct.replaceFirst("\\{\\*\\}", gc(9, 3));
		}
		
		while(ct.contains("{e}")){
			ct = ct.replaceFirst("\\{e\\}", es(9, 3));
		}
		
		while(ct.contains("{d}")){
			ct = ct.replaceFirst("\\{d\\}", ds(9, 3));
		}
		
		ct = ct.replaceAll("\\{ex\\}", es(2, 1));
		ct = ct.replaceAll("\\{dx\\}", ds(2, 1));
		ct = ct.replaceAll("\\{cx\\}", cs(2, 1));
		
		if(ct.contains("hlflag=0")){
			ct = ct.replaceFirst("hlflag=0", "hlflag=1");
		}
		
		return ct;
	}

	public void set(int mss, int msbs){
		this.MSS = mss;
		this.MSBS = msbs;
	}
	
	@Override
	public void run() {
		info("开始发送");
		synchronized (ss) { // 获取发送方
			if (ss.size() == 0) {
				info("SMTP为0");
				setSelection();
				return;
			} else {
				String[] sl = new String[ss.size()];
				ss.keySet().toArray(sl);

				String key = sl[rnd.nextInt(ss.size())];
				if (MSS!=0&&ss.get(key) == MSS-1) {
					ss.remove(key);
				} else {
					ss.put(key, (byte) (ss.get(key) + 1));
				}
				smtp = key.split("----");
			}
		}

		synchronized (sbs) { // 获取主题
			if (sbs.size() == 0) {

				//随机生成title
				title = gc(9, 3);
				
				//info("主题为0");
				//setSelection();
				//return;
			} else {
				String[] sl = new String[sbs.size()];
				sbs.keySet().toArray(sl);

				String key = sl[rnd.nextInt(sbs.size())];
				if (MSBS!=0&&sbs.get(key) == MSBS-1) {
					sbs.remove(key);
				} else {
					sbs.put(key, (byte) (sbs.get(key) + 1));
				}
				title = key;
			}
		}		
		
		this.title = deal(this.title);
			
		try {
			if(smtp.length!=2){
				System.err.println("SMTP帐号错误，请调整后重新开始软件");
				return;
			}
			Properties props = new Properties(); // 获取系统环境
			Authenticator auth = new EmailAutherticator(smtp[0], smtp[1]); // 进行邮件服务器用户认证
			String host = "smtp."+smtp[0].split("@")[1];
			
			info("smtp="+smtp[0]);
			info("title="+title);
			//host = "smtp.byteset.net";
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");
			if(smtp[0].endsWith("gmail.com")){
				props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
				props.setProperty("mail.smtp.port", "465");
				props.setProperty("mail.smtp.socketFactory.port", "465");
			}
			Session session = Session.getInstance(props, auth);
//			session.setDebug(true);
			// 设置session,和邮件服务器进行通讯。
			MimeMessage message = new MimeMessage(session);
			// message.setContent("foobar, "application/x-foobar"); // 设置邮件格式
			message.setSubject(title, "utf-8"); // 设置邮件主题
			//message.setText(content); // 设置邮件正文
			message.setContent(content, "text/html; charset=utf-8");
			message.setHeader("Content-Transfer-Encoding", "quoted-printable");
			
//			MimeMultipart multipart = new MimeMultipart();
//			
//		    MimeBodyPart txtbodyPart = new MimeBodyPart();
//		    txtbodyPart.setText("这是一封html邮件，请用html方式察看！");
//		    multipart.addBodyPart(txtbodyPart);
//		    
//		    MimeBodyPart htmlbodyPart = new MimeBodyPart();
//		    htmlbodyPart.setContent(content, "text/html;charset=utf-8");
//		    //最最关键的就这么一行
//		    htmlbodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable");
//		    multipart.addBodyPart(htmlbodyPart);
//		    message.setContent(multipart);
//		    message.saveChanges();
		    
			//message.setHeader("BQM", "BQM"); // 设置邮件标题
			message.setSentDate(new Date()); // 设置邮件发送日期
			Address address = null;
//			address = new InternetAddress(smtp[0]);
			if(smtp[0].endsWith("163.com")){
				address = new InternetAddress(smtp[0], smtp[0].split("@")[0]);
			}else if(smtp[0].endsWith("126.com")){
				address = new InternetAddress(smtp[0], smtp[0].split("@")[0]);
			}else if(smtp[0].endsWith("gmail.com")){
				address = new InternetAddress(smtp[0], smtp[0].split("@")[0]);
			}else if(smtp[0].endsWith("byteset.net")){
				address = new InternetAddress(smtp[0], smtp[0].split("@")[0]);
			}else if(smtp[0].endsWith("qq.com")){
				//address = new InternetAddress(smtp[0], "hoyland.ws");
				address = new InternetAddress(smtp[0], smtp[0].split("@")[0]);
			}else{
				address = new InternetAddress(smtp[0], smtp[0].split("@")[0]);
			}
			
			message.setFrom(address); // 设置邮件发送者的地址
			Address toAddress = new InternetAddress(to); // 设置邮件接收方的地址
			message.setRecipient(Message.RecipientType.TO, toAddress);
			
			System.err.println(address+"\n"+toAddress+"\n"+title+"\n"+"----------------");
			
			Transport.send(message); // 发送邮件
			
			call(ICallback.SUCC, 0); // 统计 成功的次数
			info("发送成功");
		} catch (Exception e) {
			call(ICallback.FAIL, 0); // 统计 失败的次数
			info("发送失败:"+e.getMessage());
			System.err.println("err:"+smtp[0]+"\n"+smtp[1]+"\n"+to);
//			System.err.println(smtp[1]);
//			System.err.println("smtp."+smtp[0].split("@")[1]);
			e.printStackTrace();
		}

		//info("发送结束");
		setSelection();
	}
	
	private String cs(int min, int tail){
		String cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		StringBuffer sb = new StringBuffer();
		Random rnd = new Random();
		for(int i=0;i<min+rnd.nextInt(tail);i++){
			sb.append(cs.charAt(rnd.nextInt(62)));
		}
		return sb.toString();
	}
	
	private String es(int min, int tail){
		String cs = "abcdefghijklmnopqrstuvwxyz";
		StringBuffer sb = new StringBuffer();
		Random rnd = new Random();
		for(int i=0;i<min+rnd.nextInt(tail);i++){
			sb.append(cs.charAt(rnd.nextInt(26)));
		}
		return sb.toString();
	}
	
	private String ds(int min, int tail){
		String cs = "1234567890";
		StringBuffer sb = new StringBuffer();
		Random rnd = new Random();
		for(int i=0;i<min+rnd.nextInt(tail);i++){
			sb.append(cs.charAt(rnd.nextInt(10)));
		}
		return sb.toString();
	}
	
    public String gc(int min, int tail) {
    	StringBuffer sb = new StringBuffer();
        int hightPos, lowPos; // 定义高低位
        Random rnd = new Random();
        byte[] b = new byte[2];
        
        for(int i=0;i<min+rnd.nextInt(tail);i++){
	        hightPos = (176 + Math.abs(rnd.nextInt(39)));//获取高位值
	        lowPos = (161 + Math.abs(rnd.nextInt(93)));//获取低位值
	
	        b[0] = (new Integer(hightPos).byteValue());
	        b[1] = (new Integer(lowPos).byteValue());
	
	        try{
	        	sb.append(new String(b, "GB2312"));//转成中文
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
        }
        return sb.toString();
     }
	
	private void info(final String status) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				cb.log(to+"->"+status);
				item.setText(2, status);
			}
		});
	}

	private void setSelection() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				item.getParent().setSelection(item);
			}
		});
	}

	private void call(final int key, final int value) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				cb.call(key, value);
			}
		});
	}
}
