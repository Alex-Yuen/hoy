package mobi.samov.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;

import mobi.samov.client.XMIDlet;
import mobi.samov.client.core.Observable;
import mobi.samov.client.core.Observer;
import mobi.samov.client.game.Platform;



public class XConnection extends Observable implements Runnable {
	//Singleton Object
	//Use this to connect to internet:
	public static String CMNET_SOCKET = "cmnet-s";
	public static String CMNET_HTTP = "cmnet-h";
	public static String BLUETOOTH = "bluetooth";
	public String s_urlAddr = "218.93.127.238";
	public final String IP = "http://218.93.127.238/server/";
//	public final String IP = "http://localhost/server/";
	public String s_content = "";
	private boolean wap_net;
	private String content;
	private  XMIDlet mid;
	private Hashtable h;
	private Platform p;
	
	private SocketConnection conn;
	private  String userName;
	private  InputStream in;
	private  OutputStream out;
//	  /**
//	   * //接收数据包头部
//	   */
//	  DataHead recvDataHead=new DataHead();
//	  /**
//	   * //发送数据包头部
//	   */
//	  DataHead sendDataHead=new DataHead();
//	  /**
//	   * 接收缓冲
//	   */
//	  byte[] recvBuffer;
//	  /**
//	   * 发送缓冲
//	   */
//	  byte[] sendBuffer;
//	  /**
//	   * 数据头缓冲
//	   */
//	  byte[] recvHeadBuffer=new byte[4];
//	/**
//	 * above use ssl connetion.
//	 * 暂不考虑线程池技术
//	 * 建立连接，发送数据，获取数据
//	 */
	public XConnection(boolean bool,XMIDlet mid,Hashtable h,Platform p)
	{
		this.addObserver(mid);
		this.mid = mid;
		this.h = h;
		this.p=p;
		wap_net = bool;
//		this.content = content;
		Thread t = new Thread(this);
		t.setPriority(t.MIN_PRIORITY);
		t.start();
	}
	/**
	 * cmwap-s://
	 * cmnet-h://
	 * bluetooth://
	 * @param URL
	 * @return
	 */
//	public static XConnection getConnection(Observer observer, String URL){
//		XConnection connection = new XConnection(false,URL);
//		connection.addObserver(observer);
//		return connection;
//	}

	/**
	 * socket/bluetooth 是长连接
	 * http 是短连接， 具体需要反映到run方法来
	 * 如果建立线程池， 则socket/bluetooth可以重用， http呢？
	 * 全部应该从线程池获取。
	 */
	public void run() {
		HttpConnection  httpConn = null;
		DataInputStream input;
		DataOutputStream ouput;
		try {
		Enumeration keys = h.keys();
	
		if(wap_net)
		{
			httpConn = (HttpConnection) Connector.open(

					"http://10.0.0.172:80" + "/server/",

					Connector.READ_WRITE, true);
			httpConn.setRequestMethod(HttpConnection.POST);
	//		httpConn.setRequestProperty("X-Online-Host", s_urlAddr);
//				
//				   while( keys.hasMoreElements()) 
//				   {
//				        Object key = keys.nextElement();
//				        Object value = h.get(key);
//				        httpConn.setRequestProperty(""+key, ""+value);
//				   }
		}
		else
		{
//			conn = (SocketConnection)Connector.open(IP);
//			conn.
			httpConn = (HttpConnection) Connector.open(
					IP , Connector.READ_WRITE, true);		
			httpConn.setRequestMethod(HttpConnection.POST);
		}
		String s = null;
		 while( keys.hasMoreElements() ) 
		 {
		      Object key = keys.nextElement();
		      Object value = h.get(key);
		      httpConn.setRequestProperty(""+key, ""+value);
			  System.out.println("发送: "+key+value);
		 }	
		 p.repaint();
		 p.serviceRepaints();
	//	ouput =  (DataOutputStream) httpConn.openOutputStream();
	//	ouput.write(s.getBytes());
		input = httpConn.openDataInputStream();
	//	s_content = new String(null, input.readByte(), index);
		s_content = input.readUTF();
		if(!s_content.equals(" No new messages"))
			System.out.println("------服务器回复: "+s_content);
		
		input.close();
		httpConn.close();
		

	}catch (SecurityException e) {
		e.printStackTrace();
	}catch (EOFException e){
		e.printStackTrace();
		p.SetClue("当前没有网络", "", "关闭");
	}catch (ConnectionNotFoundException e){
		e.printStackTrace();
		p.SetClue("连接超时", "", "退出");
	//	p.SetClue("与服务器失去连接", "", "退出");
	}catch (IOException e){
		
		e.printStackTrace();
	}
	finally{
		/**
		 *  
		 */
		String[] messages = toArray(s_content);
//		for(int i=0;i<index;i++){
//			System.out.println("messages:  "+messages[i]);
//		}
		for(int i=0;i<index;i++){
			this.setChanged();
			this.notifyObservers(messages[i]);	
			
		}
//		this.setChanged();
//		this.notifyObservers(s_content);	
	}
		// TODO Auto-generated method stub
//		while(true){
////			if(flag=true){
////				send....;
////			}
//		}
	}
	int index = 0;
	public String[] toArray(String str)
	{
		String  a[] = new String[30];	
		for (int i = 0; i < str.length(); i++) 
		{
			int temp = str.indexOf("\n");
			if(temp == -1)
			{
				a[index] = str;
				index++;
				return a;
			}
			else
			{
				String result = str.substring(0, temp);
				a[index] = result;
				str =  str.substring(temp+1);		
				index++;
			}
		}
		return a;
	}
	public  void setJ2MERequestHeaders(HttpConnection c) throws IOException 
	{
		String conf = System.getProperty("microedition.configuration");
		String prof = System.getProperty("microedition.profiles");
		String locale = System.getProperty("microedition.locale");
		String ua = "Profile/" + prof +
		" Configuration/" + conf;
		c.setRequestProperty("User-Agent", ua);
		if (locale != null)
		{
			c.setRequestProperty("Content-Language", locale);
		}
	} 
	public void send(String content){
//		//心跳包是异步的， 手动可以是同步，也可以是异步
		//if (同步 并且 尚未返回） Thread.sleep
	}
}
