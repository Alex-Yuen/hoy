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
	private Random rnd;
	private String[] smtp;

	public Task(TableItem item, Map<String, Byte> ss, String[] mail,
			ICallback cb) {
		this.item = item;
		this.cb = cb;
		this.ss = ss;
		this.title = mail[0];
		this.content = mail[1].replaceFirst("\\{\\*\\}", rs());		
		this.to = item.getText(1);
		this.rnd = new Random();
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
				if (ss.get(key) == 2) {
					ss.remove(key);
				} else {
					ss.put(key, (byte) (ss.get(key) + 1));
				}
				smtp = key.split("----");
			}
		}

		try {
			Properties props = new Properties(); // 获取系统环境
			Authenticator auth = new EmailAutherticator(smtp[0], smtp[1]); // 进行邮件服务器用户认证
			String host = "smtp."+smtp[0].split("@")[1];
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");
			Session session = Session.getInstance(props, auth);
//			session.setDebug(true);
			// 设置session,和邮件服务器进行通讯。
			MimeMessage message = new MimeMessage(session);
			// message.setContent("foobar, "application/x-foobar"); // 设置邮件格式
			message.setSubject(title); // 设置邮件主题
			//message.setText(content); // 设置邮件正文
			message.setContent(content, "text/html; charset=utf-8");
			//message.setHeader("BQM", "BQM"); // 设置邮件标题
			message.setSentDate(new Date()); // 设置邮件发送日期
			Address address = null;
//			address = new InternetAddress(smtp[0]);
			if(smtp[0].endsWith("163.com")){
				address = new InternetAddress(smtp[0], smtp[0].split("@")[0]);
			}else if(smtp[0].endsWith("qq.com")){
				address = new InternetAddress(smtp[0], "hoyland.ws");
			}
			System.err.println(address);
			message.setFrom(address); // 设置邮件发送者的地址
			Address toAddress = new InternetAddress(to); // 设置邮件接收方的地址
			message.addRecipient(Message.RecipientType.TO, toAddress);
			Transport.send(message); // 发送邮件
			
			call(ICallback.SUCC, 0); // 统计 成功的次数
			info("发送成功");
		} catch (Exception e) {
			call(ICallback.FAIL, 0); // 统计 失败的次数
			info("发送失败:"+e.getMessage());
			System.err.println(smtp[0]);
			System.err.println(smtp[1]);
			System.err.println("smtp."+smtp[0].split("@")[1]);
			e.printStackTrace();
		}

		//info("发送结束");
		setSelection();
	}
	
	private String rs(){
		String cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		StringBuffer sb = new StringBuffer();
		Random rnd = new Random();
		for(int i=0;i<11+rnd.nextInt(4);i++){
			sb.append(cs.charAt(rnd.nextInt(62)));
		}
		return sb.toString();
	}
	
	private void info(final String status) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// qm.log(uin + "->" + status + "\r\n");
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
