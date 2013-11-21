package ws.hoyland.sszs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;



/**
 * 核心引擎，UI以及其他线程观察此Engine
 * @author Administrator
 *
 */
public class Engine extends Observable {
	
	private static Engine instance;
	private List<String> accounts;
	private List<String> mails;
	private boolean login = false;
	private boolean cptType = true;
	private boolean running = false;
	private ThreadPoolExecutor pool;
	
	private Engine(){
		
	}
	
	public static Engine getInstance(){
		if(instance==null){
			instance = new Engine();
		}
		return instance;
	}

	/**
	 * 消息处理机制
	 * @param type
	 * @param message
	 */
	public void fire(EngineMessage message){
		int type = message.getType();
		Object data = message.getData();
		EngineMessage msg = null;
		
		switch(type){
			case EngineMessageType.IM_CONFIG_UPDATED:
				//暂不做处理
				//this.setChanged();
				break;
			case EngineMessageType.IM_USERLOGIN:
				uulogin(data);
				break;
			case EngineMessageType.IM_UL_STATUS:
				Integer it = (Integer)data;
				msg = new EngineMessage();
				
				this.setChanged();
				if(it==0x2586){
					msg.setType(EngineMessageType.OM_LOGINING);
					this.notifyObservers(msg);
				}else if(it>0){
					login = true;
					msg.setType(EngineMessageType.OM_LOGINED);
					msg.setData(it);
					this.notifyObservers(msg);
					ready();
				}else{
					login = false;
					msg.setType(EngineMessageType.OM_LOGIN_ERROR);
					msg.setData(it);
					this.notifyObservers(msg);
					ready();
				}
				break;
			case EngineMessageType.IM_LOAD_ACCOUNT:
				String path = (String)message.getData();
				
				try {
					msg = new EngineMessage();
					msg.setType(EngineMessageType.OM_CLEAR_ACC_TBL);
					
					this.setChanged();
					this.notifyObservers(msg);
					
					accounts = new ArrayList<String>();
					File ipf = new File(path);
					FileInputStream is = new FileInputStream(ipf);
					InputStreamReader isr = new InputStreamReader(
							is);
					BufferedReader reader = new BufferedReader(isr);
					String line = null;
					int i = 1;
					while ((line = reader.readLine()) != null) {
						if (!line.equals("")) {
							line = i + "----" + line;
							accounts.add(line);
							List<String> lns = new ArrayList<String>();
							lns.addAll(Arrays.asList(line.split("----")));
							lns.add("初始化");
//							if (lns.size() == 3) {
//								lns.add("0");
//								lns.add("初始化");
//								//line += "----0----初始化";
//							} else {
//								//line += "----初始化";
//								lns.add("初始化");
//							}
							
							String[] items = new String[lns.size()];
					        lns.toArray(items);
					        					        
					        msg = new EngineMessage();
					        msg.setType(EngineMessageType.OM_ADD_ACC_TBIT);
					        msg.setData(items);
					        
					        this.setChanged();
							this.notifyObservers(msg);							
						}
						i++;
					}

					reader.close();
					isr.close();
					is.close();
					
					if (accounts.size() > 0) {
						List<String> params = new ArrayList<String>();
						params.add(String.valueOf(accounts.size()));
						params.add(path);
						
						msg = new EngineMessage();
				        msg.setType(EngineMessageType.OM_ACCOUNT_LOADED);
				        msg.setData(params);

				        this.setChanged();
						this.notifyObservers(msg);
					}

					ready();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case EngineMessageType.IM_LOAD_MAIL:
				path = (String)message.getData();
				
				try {
					msg = new EngineMessage();
					msg.setType(EngineMessageType.OM_CLEAR_MAIL_TBL);
					
					this.setChanged();
					this.notifyObservers(msg);
					
					mails = new ArrayList<String>();
					File ipf = new File(path);
					FileInputStream is = new FileInputStream(ipf);
					InputStreamReader isr = new InputStreamReader(
							is);
					BufferedReader reader = new BufferedReader(isr);
					String line = null;
					int i = 1;
					while ((line = reader.readLine()) != null) {
						if (!line.equals("")) {
							line = i + "----" + line;
							mails.add(line);

							List<String> lns = new ArrayList<String>();
							lns.addAll(Arrays.asList(line.split("----")));
							lns.add("0");
//							if (lns.size() == 3) {
//								lns.add("0");
//								lns.add("初始化");
//								//line += "----0----初始化";
//							} else {
//								//line += "----初始化";
//								lns.add("初始化");
//							}
							
							String[] items = new String[lns.size()];
					        lns.toArray(items);
					        					        
					        msg = new EngineMessage();
					        msg.setType(EngineMessageType.OM_ADD_MAIL_TBIT);
					        msg.setData(items);
					        
					        this.setChanged();
							this.notifyObservers(msg);							
						}
						i++;
					}

					reader.close();
					isr.close();
					is.close();
					
					if (mails.size() > 0) {
						List<String> params = new ArrayList<String>();
						params.add(String.valueOf(mails.size()));
						params.add(path);
						
						msg = new EngineMessage();
				        msg.setType(EngineMessageType.OM_MAIL_LOADED);
				        msg.setData(params);

				        this.setChanged();
						this.notifyObservers(msg);
					}

					ready();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case EngineMessageType.IM_CAPTCHA_TYPE:
				cptType = (Boolean)message.getData();
				ready();				
				break;
			case EngineMessageType.IM_PROCESS:
				running = !running;
				
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_RUNNING);
				msg.setData(running);
				this.setChanged();
				this.notifyObservers(msg);
				
				if(running){
					int tc = Integer.parseInt(Configuration.getInstance().getProperty("THREAD_COUNT"));
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

					for (int i = 0; i < accounts.size(); i++) {
						try {
							Task task = new Task(accounts.get(i));
							Engine.getInstance().addObserver(task);
							pool.execute(task);
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
							//System.out.println(i + ":" + accounts.get(i));
						}
					}
				}else{
					//停止情况下的处理
					if(pool!=null){
						pool.shutdownNow();
					}
				}
				break;
			case EngineMessageType.IM_IMAGE_DATA:				
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_IMAGE_DATA);
				msg.setData(message.getData());
				this.setChanged();
				this.notifyObservers(msg);
				
				break;
			case EngineMessageType.IM_REQUIRE_MAIL:
				
				Random rnd = new Random();
				String[] ms = mails.get(rnd.nextInt(mails.size())).split("----");
				
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_REQUIRE_MAIL);
				msg.setData(ms);
				this.setChanged();
				this.notifyObservers(msg);
				
				break;
			case EngineMessageType.IM_INFO:
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_INFO);
				msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;
			default:
				break;
		}
	}

	@SuppressWarnings("unchecked")
	private void uulogin(Object message){
		final List<String> msg = (List<String>)message;
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				EngineMessage message = new EngineMessage();
				message.setType(EngineMessageType.IM_UL_STATUS);
				message.setData(new Integer(0x2586));
				
				Engine.getInstance().fire(message);
				
				int userID;
				DM.INSTANCE.uu_setSoftInfoA(94034, "0c324570e9914c20ad2fab51b50b3fdc");				
				userID = DM.INSTANCE.uu_loginA(msg.get(0), msg.get(1));
				
				message = new EngineMessage();
				message.setType(EngineMessageType.IM_UL_STATUS);
				message.setData(new Integer(userID));
				Engine.getInstance().fire(message);
			}			
		});
		
		t.start();
	}
	
	private void ready(){
		if(accounts!=null&&accounts.size()>0&&mails!=null&&mails.size()>0&&(!cptType||(cptType&&login))){
			EngineMessage msg = new EngineMessage();
	        msg.setType(EngineMessageType.OM_READY);
	        this.setChanged();
			this.notifyObservers(msg);
		}else{
			EngineMessage msg = new EngineMessage();
	        msg.setType(EngineMessageType.OM_UNREADY);
	        this.setChanged();
			this.notifyObservers(msg);
		}
	}
}
