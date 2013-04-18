package qqmail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QQMail {

	private static final Log logger = LogFactory.getLog(QQMail.class);
	
	private static List<Account> accountList = new ArrayList<Account>();
	private static List<Account> successAccountList = new ArrayList<Account>();
	private static List<Account> logginErrorAccountList = new ArrayList<Account>();
	private static List<Account> noQQMailGroupAccountList = new ArrayList<Account>();
	private static int finishedAccountNumber = 0;
	private static int COUNT_TO_SEND_PER_ACCOUNT = 0;
	private static boolean SENT_TO_ALL_GROUPS = true;
	private static QQMailTask mailTask;
	
	public static QQMailUI ui;
	
	public static void setSentToAllGroups(boolean flag) {
		SENT_TO_ALL_GROUPS = flag;
	}
	
	public static boolean getSentToAllGroups() {
		return SENT_TO_ALL_GROUPS;
	}
	
	public static void setCountToSendPerAccount(int count) {
		COUNT_TO_SEND_PER_ACCOUNT = count;
	}
	
	public static int getCountToSendPerAccount() {
		return COUNT_TO_SEND_PER_ACCOUNT;
	}
	
	public static void init() {
		try {
			logger.info("======================================================");

			mailTask = new QQMailTask();
			
			InputStream is = new FileInputStream("QQ列表.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String line;
			while ((line = br.readLine()) != null) {
			    String[] items = line.split("----");
			    Account account = new Account();
			    account.setNumber(items[0]);
			    account.setPassword(items[1]);
			    if(items.length == 4) {
			    	account.setTotalGroupCount(Integer.parseInt(items[2]));
			    	account.setCurrentGroupIndex(Integer.parseInt(items[3]));
			    }			    
			    mailTask.addAccount(account);				
				accountList.add(account);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void startSend() {
		String info = "启动";
		logger.info(info);
		QQMail.appendLogToUI(info);
		mailTask.start();
	}
	
	private static void doResult() {
		updateQQList();
		writeFailureToFile(logginErrorAccountList, "登录失败.txt");
		writeFailureToFile(noQQMailGroupAccountList, "没有群邮件.txt");
		
		String info = "结束!";
		logger.info(info);
		QQMail.appendLogToUI(info);
		logger.info("=======================================================");
		
		info = "总共 " + accountList.size() + " 个QQ!";
		logger.info(info);
		QQMail.appendLogToUI(info);
		
		info = accountList.size() - logginErrorAccountList.size() - noQQMailGroupAccountList.size() + "个发送成功!";
		logger.info(info);
		QQMail.appendLogToUI(info);
		
		if(logginErrorAccountList.size() > 0) {
			info = logginErrorAccountList.size() + "个登录失败 , 详细信息查看 登录失败.txt";
			logger.info(info);
			QQMail.appendLogToUI(info);
		}
		
		if(noQQMailGroupAccountList.size() > 0) {
			info = noQQMailGroupAccountList.size() + "个没有群邮件, 详细信息查看 没有群邮件.txt";
			logger.info(info);
			QQMail.appendLogToUI(info);
		}
		
		logger.info("=======================================================");
		logger.info("=======================================================");
	}
	
	private static void updateQQList() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("QQ列表.txt"));
			for(int i = 0; i < accountList.size(); i++) {
				Account account =accountList.get(i); 
				writer.write(account.getNumber() + "----" + account.getPassword()  + "----"
						+ account.getTotalGroupCount() + "----"
						+ account.getCurrentGroupIndex());
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	private static void writeFailureToFile(List<Account> accounts, String fileName) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			for(int i = 0; i < accounts.size(); i++) {
				Account account =accounts.get(i); 
				writer.write(account.getNumber() + "----" + account.getPassword());
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addFinishedAccountNumber() {
		synchronized(QQMail.class) {
			finishedAccountNumber++;
			if(finishedAccountNumber >= accountList.size()) {
				doResult();
				ui.doFinishSend();
			}
		}
		
	}
	
	public static void appendLogToUI(String info) {
		ui.appendLog(info);
	}
	
	public static void notifyNeedCaptcha(Account account, String captchaFileName) {
		mailTask.stop();
		ui.notifyNeedCaptCha(captchaFileName);
		File file = new File(captchaFileName);
		if(file.exists()) {
			file.delete();
		}
	}
	
	//处理用户输入的验证码
	public static void notifyPostCaptcha(String captcha) {
		mailTask.currentAccount.setCaptcha(captcha);
		mailTask.reStart();
	}
	
	public static List<Account> getAccountList() {
		return accountList;
	}
	
	public static void addAccountList(Account account) {
		accountList.add(account);
	}
	
	public static void addLoginFailureAccount(Account account) {
		synchronized(QQMail.class) {
			logginErrorAccountList.add(account);
			ui.setFailureCount(logginErrorAccountList.size());
		}		
	}
	
	public static void addNoQQMailGroupAccount(Account account) {
		synchronized(QQMail.class) {
			noQQMailGroupAccountList.add(account);
		}
	}
	
	public static void addSuccessAccount(Account account) {
		synchronized(QQMail.class) {
			successAccountList.add(account);
			ui.setSuccessCount(successAccountList.size());
		}		
	}
}

class QQMailTask {
	
	private LinkedList<Account> accountList = new LinkedList<Account>();
	Account currentAccount;
	private boolean isStop = false;
	
	public void addAccount(Account account) {
		this.accountList.addLast(account);
	}
	
	public void start() {
		run();
	}
	
	public void reStart() {
		this.currentAccount.start();
		this.isStop = false;
		run();
	}
	
	@SuppressWarnings("deprecation")
	public void stop() {
		this.isStop = true;
	}
	
	private void run() {
		while(!this.accountList.isEmpty()) {
			if(this.isStop) {
				return;
			}
			
			this.currentAccount = this.accountList.removeFirst();
			this.currentAccount.start();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}
}
