package ws.hoyland.smtp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SMTPSession extends Thread {
	
	public static final String CMD_HELO="HELO";//信号
	public static final String CMD_EHLO="EHLO";
	public static final String CMD_MAIL="MAIL";//
	public static final String CMD_RCPT="RCPT";
	public static final String CMD_DATA="DATA";
	public static final String CMD_QUIT="QUIT";
	public static final String CMD_RSET="RSET";
	public static final String CMD_NOOP="NOOP";
	private Socket s;
	private BufferedReader br;
	private PrintStream ps;
	
	private String from;
	private String to;
	
	public SMTPSession(Socket s) {
		this.s=s;
	}

	public void run() {
		try {
			br=new BufferedReader(
					new InputStreamReader(s.getInputStream())
			);
			ps=new PrintStream(
					s.getOutputStream()
			);
			doWelcome();
			String line=null;
			line=br.readLine();
			while(line!=null){
				System.out.println(line);
				String command=line.substring(0,4).trim();
				if(command.equalsIgnoreCase(CMD_HELO)||command.equalsIgnoreCase(CMD_EHLO)){
					doHello();
				}
				else if(command.equalsIgnoreCase(CMD_RSET))
					doRset();
				else if(command.equalsIgnoreCase(CMD_MAIL))
					doMail(line);
				else if(command.equalsIgnoreCase(CMD_RCPT))
					doRcpt(line);
				else if(command.equalsIgnoreCase(CMD_DATA))
					doData();
				else if(command.equalsIgnoreCase(CMD_NOOP))
					doNoop();
				else if(command.equalsIgnoreCase(CMD_QUIT)){
					doQuit();
					break;
				}
				line=br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				br.close();
				ps.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doNoop() {
		ps.println("250 OK");
	}

	private void doQuit() {
		ps.println("221 GoodBye");
	}

	private void doData() {
		try {
			ps.println("354  end data with <CR><LF>.</LF></CR>");
			String line=null;
			StringBuffer sb=new StringBuffer();
			while((line=br.readLine())!=null){
				if(line.equals("."))
					break;
				sb.append(line+"\r");				
			}
			sb.deleteCharAt(sb.length()-1);
			String sender=from.substring(0,from.indexOf("@"));
			//将信件拷贝到发件夹
			String path="MailBox/"+sender+"/sender/"+System.currentTimeMillis()+".txt";
			System.out.println(path);
			File file=new File(path);
			if(!file.exists()){
				//创建父级目录
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			PrintWriter pw=new PrintWriter(file);
			pw.println(sb);
			pw.close();
			//将邮件内容拷贝到发件夹
			String receiver=to.substring(0,to.indexOf("@"));
			String to_path="MailBox/"+receiver+"/receiver/"+file.getName();
			File to_file=new File(to_path);
			if(!to_file.exists()){
				//创建父级目录
				to_file.getParentFile().mkdirs();
				to_file.createNewFile();
			}
			PrintStream to_ps=new PrintStream(to_path);
			BufferedReader to_in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String to_line=null;
			 while ((to_line = to_in.readLine()) != null) {
				 to_ps.println(to_line);
	            }
			 to_ps.close();
			 to_in.close();
			ps.println("250 OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doRcpt(String command) {
		String strTo="to:";
		int index=command.indexOf(strTo);
		if(index==-1)
			index=command.indexOf(strTo.toUpperCase());
		int start=command.indexOf("<");
		int end=command.indexOf(">");
		if(index>4&&start>0&&end>start){
			to=command.substring(start+1, end);
			System.out.println(to);
			int addstart=command.indexOf("@");
			System.out.println(addstart);
			System.out.println(end);
			String mailadd;
			mailadd=command.substring(addstart+1, end);
			System.out.println(mailadd);
//			if(mailadd.equalsIgnoreCase("localhost")){
				ps.println("250  OK");
//			}
//			else{
//				ps.println("251 User not local; will forward to <"+to+">");
//			}
		}
	}
	
	private void doMail(String command) {
		String strFrom="from:";
		int index=command.indexOf(strFrom);
		if(index==-1)
			index=command.indexOf(strFrom.toUpperCase());
		int start=command.indexOf("<");
		int end=command.indexOf(">");
		if(index>4&&start>0&&end>start){
			from=command.substring(start+1, end);
			ps.println("250  OK");
		}
		else{
			ps.println("500  ERROR");
		}
	}

	private void doRset() {
		ps.println("250 OK");
	}

	private void doHello() {
		ps.println("250 OK");
	}
	
	private void doWelcome() {
		ps.println("220 wrfei");
	}

}
