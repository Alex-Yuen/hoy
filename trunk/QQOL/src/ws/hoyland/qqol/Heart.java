package ws.hoyland.qqol;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;
import ws.hoyland.util.EngineMessage;

/*
 * 处理心跳的机制，对于socketland每个的对象，自动发送心跳
 * 每分钟运行一次
 */
public class Heart extends TimerTask {
	private ThreadPoolExecutor pool = null;
	
	public Heart(){
		int tc = 20000; //支持20000个号码
		int corePoolSize = tc;// minPoolSize
		int maxPoolSize = tc;
		int maxTaskSize = (1024 + 512) * 100 * 40;// 缓冲队列
		long keepAliveTime = 0L;
		TimeUnit unit = TimeUnit.MILLISECONDS;

		BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
				maxTaskSize);
		RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略
		
		// 创建线程池
		pool = new ThreadPoolExecutor(corePoolSize,
				maxPoolSize, keepAliveTime, unit,
				workQueue, handler);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Heart beat");
		//读取SocketLand中的Clients
		//每个发送一个心跳包
		synchronized(SocketLand.getInstance().getClients()){
			for(SSClient client:SocketLand.getInstance().getClients()){
				pool.execute(new BeatTask(client));
			}
		}
	}

}

class BeatTask implements Runnable {
	private static DateFormat format = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private SSClient client;
	
	private byte[] encrypt;
	private byte[] buf;
	
	private Crypter crypter;
	
	private Random rnd = null;
	
	private DatagramPacket dpOut = null;
	private ByteArrayOutputStream bsofplain = null;
	private ByteArrayOutputStream baos = null;
		
	public BeatTask(SSClient client){
		this.client = client;
		this.crypter = new Crypter();
		this.rnd = new Random();
	}
	
	@Override
	public void run() {
		byte x = 0;
		client.setHeart(false);
		while(!client.isHeart()&&x<10){
			info("心跳["+format.format(new Date())+"]");
			x++;
			short seq = (short)rnd.nextInt(0xFFFF);						
			try{
				bsofplain = new ByteArrayOutputStream();
				bsofplain.write(client.getAccount().getBytes());
					
				encrypt = crypter.encrypt(bsofplain.toByteArray(), client.getSessionKey());
										
				baos = new ByteArrayOutputStream();
				baos.write(new byte[]{
						0x02, 0x34, 0x4B, 0x00, 0x58
				});
				baos.write(Converts.hexStringToByte(Integer.toHexString(seq).toUpperCase()));
				baos.write(Converts.hexStringToByte(Long.toHexString(Long.valueOf(client.getAccount())).toUpperCase()));
				baos.write(new byte[]{
						//0x03, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x30
						//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2, 0x00, 0x30, 0x00, 0x3A
						//0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, 0x68, 0x00, 0x30, 0x00, 0x3A//(byte)0xA2?
						0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x00, 0x00, 0x66, (byte)0xA2 
				});
				baos.write(encrypt);
				baos.write(new byte[]{
						0x03
				});
					
				buf = baos.toByteArray();
					
				System.out.println("0058["+Converts.bytesToHexString(client.getSessionKey())+"]");
				System.out.println(Converts.bytesToHexString(baos.toByteArray()));
					
				dpOut = new DatagramPacket(buf, buf.length, InetAddress.getByName(client.getIp()), 8000);
				client.getDs().send(dpOut);
					
				//not need
				//IN:
				/**
				byte[] buffer = new byte[1024];
				DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);
									
				dsx.receive(dpIn);
					
				buffer = pack(buffer);
				System.out.println(buffer.length);
				System.out.println(Converts.bytesToHexString(buffer));
				**/
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				Thread.sleep(1000*x);
			}catch(Exception e){
				e.printStackTrace();
			}
		}//end while
		
		if(!client.isHeart()){//还没反应，考虑断开线程，重新登录
			quit();
			//IM_RELOGIN
			new Thread(new Runnable(){
				@Override
				public void run() {
					try{
						Thread.sleep(1000*60*Integer.parseInt(Configuration.getInstance().getProperty("EX_ITV")));
					}catch(Exception e){
						e.printStackTrace();
					}
					EngineMessage message = new EngineMessage();
					message.setTid(BeatTask.this.client.getId());
					message.setType(EngineMessageType.IM_RELOGIN);
													
					Engine.getInstance().fire(message);
				}						
			}).start();		
		}
	}	
	
	private void quit(){
		//删除此client，在clients中
		synchronized(SocketLand.getInstance().getClients()){
			SocketLand.getInstance().getClients().remove(this.client);
		}
		//关闭socket
		try{
			client.getDs().close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//终止当前线程
		//run = false;
		//通知引擎任务完成
		EngineMessage message = new EngineMessage();
		message.setTid(client.getId());
		message.setType(EngineMessageType.IM_TF);
		//message.setData(info);
		Engine.getInstance().fire(message);
	}
	
	private void info(String info){
		EngineMessage message = new EngineMessage();
		message.setTid(client.getId());
		message.setType(EngineMessageType.IM_INFO);
		message.setData(info);
		
		String tm = format.format(new Date());
		
		System.err.println("["+client.getAccount()+"]"+info+"("+tm+")");
		Engine.getInstance().fire(message);
	}
}