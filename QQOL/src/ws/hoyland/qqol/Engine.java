package ws.hoyland.qqol;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.channels.DatagramChannel;
//import java.net.URL;
//import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.HashMap;
//import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import ws.hoyland.util.Configuration;
import ws.hoyland.util.DM;
import ws.hoyland.util.EngineMessage;
import ws.hoyland.util.YDM;

/**
 * 核心引擎，UI以及其他线程观察此Engine
 * @author Administrator
 *
 */
public class Engine extends Observable {
	
	private static Engine instance;
	private Map<String, Map<String, byte[]>> accounts = null;
	Map<String, byte[]> details = null;
	private Map<String, DatagramChannel> channels = null;
	private List<String> mails = null;
	private boolean login = false;
	private int cptType = 0;
	private boolean running = false;
	private ThreadPoolExecutor pool; //Task, Receiver专用
	private ThreadPoolExecutor poolx; //checker专用
	//private int mindex = 0;
	//private int mcount = 0;
	private Configuration configuration = Configuration.getInstance();
	private Queue<String> queue = null;
	
	private static int CORE_COUNT = 200;
	private static int CORE_COUNT_X = 400;
	
	private Object[] accs = null;
//	private int recc = 0;//reconnect count
//	private int frecc = 0;//finished
//	private String cip = null; //current ip
	
//	private int atrecc; //config data
	
	//private BufferedWriter[] output = new BufferedWriter[5]; //成功，失败，未运行
	//private String[] fns = new String[]{"成功", "失败", "未运行帐号", "已使用邮箱", "未使用邮箱"}; 
	//private String[] fns = new String[]{"成功", "失败", "密码错误", "帐号冻结", "未运行帐号"};
	//private URL url = Engine.class.getClassLoader().getResource("");
	//private String xpath = url.getPath();
//	private int lastTid = 0;
//	private boolean pause = false;
	//private boolean freq = false;
	private Timer timer = null;
	
	private int pc = 0;//pause count;
	
	//private Map<String, Long> ips = new HashMap<String ,Long>();
	
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
				login(data);
				break;
			case EngineMessageType.IM_UL_STATUS:
				msg = new EngineMessage();
				
				this.setChanged();
				if(data==null){
					msg.setType(EngineMessageType.OM_LOGINING);
					this.notifyObservers(msg);
				}else{
					Object[] objs = (Object[])data;
					Integer it = (Integer)objs[0];
					if(it>0){						
						login = true;
						msg.setType(EngineMessageType.OM_LOGINED);
						msg.setData(data);
						this.notifyObservers(msg);
					ready();
					}else{
						login = false;
						msg.setType(EngineMessageType.OM_LOGIN_ERROR);
						msg.setData(data);
						this.notifyObservers(msg);
						ready();
					}
				}
				break;
			case EngineMessageType.IM_LOAD_ACCOUNT:
				String path = (String)message.getData();
				
				try {
					msg = new EngineMessage();
					msg.setType(EngineMessageType.OM_CLEAR_ACC_TBL);
					
					this.setChanged();
					this.notifyObservers(msg);
					
					accounts = new LinkedHashMap<String, Map<String, byte[]>>();
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
							String[] nlns = line.split("----");
							details = new HashMap<String, byte[]>();
							details.put("id", nlns[0].getBytes());
							details.put("password", nlns[2].getBytes());
							accounts.put(nlns[1], details);
							List<String> lns = new ArrayList<String>();
							lns.add(nlns[0]);
							lns.add(nlns[1]);
							lns.add(nlns[2]);
							//lns.addAll(Arrays.asList(line.split("----")));
							lns.add("");
							lns.add("");
							lns.add("");
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
				cptType = (Integer)message.getData();
				ready();
				break;
			case EngineMessageType.IM_PROCESS:
				Integer[] flidx = (Integer[]) message.getData();
				int tc = Integer.parseInt(Configuration.getInstance().getProperty("THREAD_COUNT"));
							
				//是否启动
				if(!running){				
										
					//创建日志文件
					//long tm = System.currentTimeMillis();
					/**
					DateFormat format = new java.text.SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
					String tm = format.format(new Date());
					for(int i=0;i<output.length;i++){
						File fff = new File(xpath + fns[i] + "-" + tm + ".txt");
						try {
							if (!fff.exists()) {
								fff.createNewFile();
							}
							
							output[i] = new BufferedWriter(
									new FileWriter(fff));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					**/
					int corePoolSize = CORE_COUNT;// 固定100个线程
					int maxPoolSize = CORE_COUNT;
					int maxTaskSize = (1024 + 512) * 100 * 20;// 缓冲队列
					long keepAliveTime = 0L;
					TimeUnit unit = TimeUnit.MILLISECONDS;

					BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
							maxTaskSize);
					RejectedExecutionHandler handler = new AbortPolicy();// 饱和处理策略
					
					// 创建线程池
					pool = new ThreadPoolExecutor(corePoolSize,
							maxPoolSize, keepAliveTime, unit,
							workQueue, handler);					
					
					corePoolSize = CORE_COUNT_X;
					maxPoolSize = CORE_COUNT_X;
					maxTaskSize = (1024 + 512) * 100 * 40;
					workQueue = new ArrayBlockingQueue<Runnable>(
							maxTaskSize);
					poolx = new ThreadPoolExecutor(corePoolSize,
							maxPoolSize, keepAliveTime, unit,
							workQueue, handler);
					/**
					mindex = flidx[2]; //mfirst of SSZS
					if(mindex==-1){
						mindex = 0;
					}**/
					queue = new LinkedList<String>();					
					channels = new HashMap<String, DatagramChannel>();
					
					//for (int i = 0; i < accounts.size(); i++) {
					
					timer = new Timer();
					timer.schedule(new Heart(), 1000*30, 1000*60); //用于发送心跳包
					timer.schedule(new TimerTask(){ //用于刷新界面
						//private long starttime = System.currentTimeMillis();
						
						@Override
						public void run() {
							long time = System.currentTimeMillis();
							
							EngineMessage msg = new EngineMessage();
							msg.setType(EngineMessageType.OM_BEAT);
							msg.setData(time);
							Engine.this.setChanged();
							Engine.this.notifyObservers(msg);
						}
						
					}, 1000, 1000);
					
					//monitor = new Monitor();
					new Thread(Monitor.getInstance()).start();//开始监听
					
					running = true;
				}else{
					//停止情况下的处理
					if(flidx[3]==0){
						shutdown();
					}
				}				

				//添加任务
				if(running){
					accs = accounts.keySet().toArray();
					for (int i = flidx[0]; i <= flidx[1]; i++) {
						queue.add((String)accs[i]);
					}
					
					for(int i=0;i<tc&&queue.size()>0;i++){	//开启TC个登录线程，发送请求						
						try {
							Task task = new Task(Task.TYPE_0825, queue.remove());
							//Engine.getInstance().addObserver(task);
							addTask(task);
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
							//System.out.println(i + ":" + accounts.get(i));
						}
					}
				}
								
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_RUNNING);
				msg.setData(running);
				this.setChanged();
				this.notifyObservers(msg);
				break;
			case EngineMessageType.IM_IMAGE_DATA:				
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_IMAGE_DATA);
				msg.setData(message.getData());
				this.setChanged();
				this.notifyObservers(msg);
				
				break;
			case EngineMessageType.IM_COMPLETE:
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_COMPLETE);
				msg.setData(message.getData());
				this.setChanged();
				this.notifyObservers(msg);
				break;
			case EngineMessageType.IM_RELOGIN:
				accs = accounts.keySet().toArray();
				Task task = new Task(Task.TYPE_0825, (String)accs[message.getTid()-1]);
				//Engine.getInstance().addObserver(task);
				pool.execute(task);
				break;
//			case EngineMessageType.IM_REQUIRE_MAIL:
//
//				String[] ms = null;
//				synchronized(MailObject.getInstance()){
//					//Random rnd = new Random();
//					System.err.println("X:A");
//					System.err.println("X1:"+mcount+"/"+mindex+"/"+mails.size()+"/"+message.getTid());
//					if(mcount==Integer.parseInt(configuration.getProperty("EMAIL_TIMES"))){
//						System.err.println("X:B");
//						mcount = 0;
//						mindex++;
//					}
//					
//					System.err.println("X:C");
//					
//					if(mindex<mails.size()){
//						System.err.println("X:D");
//						ms = mails.get(mindex).split("----");
//						mcount++;
//					}
//					System.err.println("X:E");
//					System.err.println("X2:"+mcount+"/"+mindex+"/"+mails.size()+"/"+message.getTid());
//				}
//				msg = new EngineMessage();
//				msg.setTid(message.getTid());
//				msg.setType(EngineMessageType.OM_REQUIRE_MAIL);
//				msg.setData(ms);
//				this.setChanged();
//				this.notifyObservers(msg);
//				
//				break;
			case EngineMessageType.IM_INFO:
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_INFO);
				msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;
			case EngineMessageType.IM_INFOACT:
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_INFOACT);
				msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;
			case EngineMessageType.IM_NICK:
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_NICK);
				msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;
			case EngineMessageType.IM_PROFILE:
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_PROFILE);
				msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;		
			case EngineMessageType.IM_TF:
				msg = new EngineMessage();
				msg.setTid(message.getTid());
				msg.setType(EngineMessageType.OM_TF);
				msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;		
			case EngineMessageType.IM_NO_EMAILS:
				shutdown();				
				break;
			case EngineMessageType.IM_START: 
				
				/**
					recc++;
					atrecc = Integer.parseInt(configuration.getProperty("AUTO_RECON"));
					System.err.println("通知重拨:"+atrecc+"/"+recc+"/"+frecc);
					if(atrecc!=0&&atrecc==recc){//重拨的触发条件
						recc = 0;
						
						msg = new EngineMessage();
						msg.setTid(-1); //所有task
						msg.setType(EngineMessageType.OM_RECONN);
						//msg.setData(message.getData());
						
						this.setChanged();
						this.notifyObservers(msg);
					}
				**/
				break;
			case EngineMessageType.IM_FINISH:
					//写入日志
					//lastTid = message.getTid();
					//String[] dt = (String[])message.getData();
					
					/**
					if("1".equals(dt[0])){//成功
						try{
//							output[0].write(dt[1]+"----"+dt[2]+"----"+dt[3]+"----"+dt[4]+"----"+dt[5]+"----"+cip + "\r\n");
							output[0].write(dt[1]+"----"+dt[2]+ "\r\n");
							output[0].flush();
						}catch(Exception e){
							e.printStackTrace();
						};
					}else if("2".equals(dt[0])){//密码错误
						try{
							output[2].write(dt[1]+"----"+dt[2]+ "\r\n");
							output[2].flush();
						}catch(Exception e){
							e.printStackTrace();
						};
					}else if("3".equals(dt[0])){//帐号冻结
						try{
							output[3].write(dt[1]+"----"+dt[2]+ "\r\n");
							output[3].flush();
						}catch(Exception e){
							e.printStackTrace();
						};
					}else{//失败
						try{
							output[1].write(dt[1]+"----"+dt[2]+ "\r\n");
							output[1].flush();
						}catch(Exception e){
							e.printStackTrace();
						};
					}
					**/
					System.err.println("--------------act count:"+pool.getActiveCount()+":"+pool.getQueue().size());
					//if((pool.getActiveCount()==1&&!"1".equals(this.configuration.getProperty("THREAD_COUNT")))||(pool.getTaskCount()==1&&"1".equals(this.configuration.getProperty("THREAD_COUNT")))){ //当前是最后一个线程
					if(pool.getActiveCount()==1&&pool.getQueue().size()==0){
						//线程数不为1时，判断是否只有一个线程在运行； X
						//线程数为1时，判断pool的线程总数 X						
						running = !running;
						
						msg = new EngineMessage();
						msg.setType(EngineMessageType.OM_RUNNING);
						msg.setData(running);
						this.setChanged();
						this.notifyObservers(msg);
						
						//自动关闭
						//不自动关闭
						/**
						Thread t = new Thread(new Runnable(){
							@Override
							public void run() {
								shutdown();
							}							
						});
						t.start();
						**/
						//shutdown();
					}
					/**
					frecc++;
					
					atrecc = Integer.parseInt(configuration.getProperty("AUTO_RECON"));
					System.err.println(atrecc+"/"+frecc+"/"+freq+"/"+recc);
					//System.err.println("ACT:"+pool.getActiveCount());
					//只剩下当前线程，因为START时候发出停止新后之后，可能有遗漏线程已经开始执行了，需等待最后一个线程执行完毕
					
					if((atrecc!=0&&atrecc==frecc)||(freq==true&&frecc==recc)){//执行重拨 第二个条件不一定可行
						System.err.println("Y0");
						if(freq==true&&frecc==recc){
							System.err.println("Y1");
							freq = false;
							recc = 0;
							frecc = 0;
						}else{
							System.err.println("Y2");
							frecc = 0;
						}
						
						Thread t = new Thread(new Runnable(){
	
							final String account = configuration.getProperty("ADSL_ACCOUNT");
							final String password = configuration.getProperty("ADSL_PASSWORD");
							
							@Override
							public void run() {
								System.err.println("正在重拨");
	
								boolean st = false;
								String cut = "rasdial 宽带连接 /disconnect";
								String link = "rasdial 宽带连接 "
										+ account
										+ " "
										+ password;
								try{
									Thread.sleep(1000*Integer.parseInt(configuration.getProperty("RECON_DELAY")));
									boolean fo = true;
									boolean fi = true;
									
									int tfo = 0;
									int tfi = 0;
									
									while(fo&&tfo<4){
										String result = execute(cut);
										System.err.println("CUT:"+result);
										if (result
												.indexOf("没有连接") == -1) {
											System.err.println("CUT1");
											fo = false; // 断线成功，将跳出外循环
											fi = true;
											
											tfi = 0;
											System.err.println("CUT2:"+fi+"/"+configuration.getProperty("AWCONN")+"/"+tfi);
											while(fi&&("true".equals(configuration.getProperty("AWCONN"))||("false".equals(configuration.getProperty("AWCONN"))&&tfi<4))){
												System.err.println("CUT3");
												result = execute(link);
												System.err.println("LINK:"+result);
												if (result
														.indexOf("已连接") > 0 || result
														.indexOf("已经连接") > 0) {
													System.err.println("CUT4");
													//1
	//												URL url = new URL("http://iframe.ip138.com/ic.asp");
	//												InputStream is = url.openStream();
	//												BufferedReader br = new BufferedReader(new InputStreamReader(is, "GB2312"));  
	//										        String line = null;
	//										        StringBuffer sb = new StringBuffer();
	//										        while ((line=br.readLine())!= null) {
	//										        	sb.append(line);
	//										        }
	//										
	//												String ip = sb.toString();
	//												
	//										        int index = ip.indexOf("您的IP是：[");
	//										        ip = ip.substring(index+7);
	//										        
	//										        index = ip.indexOf("]");
	//										        ip = ip.substring(0, index);
											        
											        // 2
	//												InetAddress addr = InetAddress.getLocalHost();
	//												String ip = addr.getHostAddress().toString();
													
													//3
													result = execute("ipconfig");
													result = result.substring(result.indexOf("宽带连接"));
													if(result.indexOf("IP Address")!=-1){
														result = result.substring(result.indexOf("IP Address"));
													}
													if(result.indexOf("IPv4 地址")!=-1){
														result = result.substring(result.indexOf("IPv4 地址"));
													}
													
													result = result.substring(result.indexOf(":")+2);
													result = result.substring(0, result.indexOf("\n "));
													//String ip = result;
													String rip = result;
													String ip = result.substring(0, result.lastIndexOf("."));
													
											        System.err.println("ip="+ip);
													if(ips.containsKey(ip)){
														long time = ips.get(ip);
														if(System.currentTimeMillis()-time>=1*60*60*1000){
															System.err.println("IP重复，但超过1小时，拨号成功:"+ip);
															cip = rip;
															ips.put(ip, System.currentTimeMillis());
															fi = false;//跳出内循环
															st = true;
															//break;
														}else{
															System.err.println("IP重复，未超过1小时，重新拨号:"+ip);
															fo = true;
															fi = false;
															tfo = 0;
															st = false;
															//continue;
														}
													}else{
														System.err.println("IP不重复，拨号成功:"+ip);
														cip = rip;
														ips.put(ip, new Long(System.currentTimeMillis()));
														fi = false;
														st = true;
														//break;
													}
												}else {
													System.err.println("CUT5");
													System.err.println("连接失败("+tfi+")");
													try{
														Thread.sleep(1000*30);
													}catch(Exception e){
														e.printStackTrace();
													}
													tfi++;//允许3次循环
													//break;
												}
											}//while in
										}else {
											System.err.println("CUT6");
											System.err.println("没有连接("+tfo+")");
											try{
												Thread.sleep(1000*30);
											}catch(Exception e){
												e.printStackTrace();
											}
											tfo++; //允许3次循环
											//break;
										}
									}//while out
								}catch(Exception e){
									e.printStackTrace();
								}
								
								if(st){
									//通知其他未阻塞线程，重拨结束，无需再进行等待
									EngineMessage msg = new EngineMessage();
									msg.setTid(-1); //所有task
									msg.setType(EngineMessageType.OM_RECONN);
									//msg.setData(message.getData());
									
									Engine.this.setChanged();
									Engine.this.notifyObservers(msg);
									
									synchronized(ReconObject.getInstance()){
										try{
											ReconObject.getInstance().notifyAll();
										}catch(Exception e){
											e.printStackTrace();
										}
									}
									
									System.err.println("重拨结束");
								}else{
									System.err.println("重拨失败");
								}
							}
							
							private String execute(String cmd) throws Exception {
								Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
								StringBuilder result = new StringBuilder();
								BufferedReader br = new BufferedReader(new InputStreamReader(
										p.getInputStream(), "GB2312"));
								String line;
								while ((line = br.readLine()) != null) {
									result.append(line + "\n");
								}
								return result.toString();
							}
							
						});
						
						//t.start();
						
					}
					**/
				break;
			case EngineMessageType.IM_EXIT:
				//关闭日志文件
				shutdown();
				System.exit(0);
				break;
//			case EngineMessageType.IM_FREQ:
////				recc = 0;
////				frecc = 0;
//				freq = true;
//				//通知其他需要重拨
//				msg = new EngineMessage();
//				msg.setTid(-1); //所有task
//				msg.setType(EngineMessageType.OM_RECONN);
//				//msg.setData(message.getData());
//				
//				this.setChanged();
//				this.notifyObservers(msg);
//				break;
			case EngineMessageType.IM_PAUSE_COUNT:
				pc++;
				if(pc==Integer.parseInt(Configuration.getInstance().getProperty("THREAD_COUNT"))){
					//关闭output[0]
					/**
					try{
						if(output[0]!=null){
							output[0].close();
							output[0] = null;
						}
					}catch(Exception e){
						e.printStackTrace();
					}**/
				}
				break;
			default:
				break;
		}
	}

	private void shutdown() {
		/**
		EngineMessage msg = new EngineMessage();
		msg.setTid(-1); //所有task
		msg.setType(EngineMessageType.OM_STOP);
		//msg.setData(message.getData());
		
		this.setChanged();
		this.notifyObservers(msg);
		**/
		running = false;
		if(timer!=null){
			timer.cancel();
		}
		if(pool!=null&&channels!=null){
			for(String account : channels.keySet()){
				if(accounts.get(account).get("login")!=null){//已经登录的，发送离线消息
					//send(new TaskSender(new Task(Task.TYPE_0062, account)));
					addTask(new Task(Task.TYPE_0062, account));
				}
			}
		}
		
		while(poolx!=null&&poolx.getActiveCount()!=0){
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				//
			}
		}
		
		//等待所有运行线程执行完毕，关闭日志文件
		while(pool!=null&&pool.getActiveCount()!=0){
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				//
			}
		}
		
		if(poolx!=null){
			//pool.shutdown();
			poolx.shutdownNow();
			poolx = null;
		}
		
		if(pool!=null){
			//pool.shutdown();
			pool.shutdownNow();
			pool = null;
		}
		//if(pool!=null){
			//写入未运行帐号日志
		/**
			try{
				//if(lastTid!=-1){
				if(output[4]!=null){
					for(int i=lastTid;i<accounts.size();i++){
						String[] accl = accounts.get(i).split("----");
						output[4].write(accl[1]+"----"+accl[2]+ "\r\n");
						output[4].flush();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			};
		**/	
			/**
			//写入已使用邮箱日志
			try{
				//if(mindex!=-1){
				if(output[3]!=null){
					for(int i=0;i<mindex+1;i++){
						String[] ml = mails.get(i).split("----");
						output[3].write(ml[1]+"----"+ml[2]+ "\r\n");
						output[3].flush();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			};
			**/
			/**
			//未使用
			try{
				//if(mindex!=-1){
				if(output[4]!=null){
					for(int i=mindex;i<mails.size();i++){
						String[] ml = mails.get(i).split("----");
						output[4].write(ml[1]+"----"+ml[2]+ "\r\n");
						output[4].flush();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			};
			**/
		//}
		
		/**
		for(int i=0;i<output.length;i++){
			try{
				if(output[i]!=null){
					output[i].close();
					output[i] = null;
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		**/
	}

	@SuppressWarnings("unchecked")
	private void login(Object message){
		final List<String> msg = (List<String>)message;
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				EngineMessage message = new EngineMessage();
				message.setType(EngineMessageType.IM_UL_STATUS);
				message.setData(null);
				
				Engine.getInstance().fire(message);
				
				Object[] data = new Object[3];
				
				int userID = 0;
				int score = 0;
				
				if(cptType==0){
					YDM.INSTANCE.YDM_SetAppInfo(105, "4c0e22fb79a8afff2331d34786364b68");
				}else{
					DM.INSTANCE.uu_setSoftInfoA(94808, "5f7ee083434a421bbe50932f85f8cbdb");
				}
				//
				
				//
				if(cptType==0){
					userID = YDM.INSTANCE.YDM_Login(msg.get(0), msg.get(1));
				}else{
					userID = DM.INSTANCE.uu_loginA(msg.get(0), msg.get(1));
				}
				
				if(userID>0){
					//save config
					configuration.put("R_PWD", msg.get(2));
					configuration.put("AUTO_LOGIN", msg.get(3));
					if("true".equals(msg.get(2))){
						configuration.put("T_PWD", msg.get(1));
					}
					configuration.put("T_ACC", msg.get(0));
					configuration.put("CPT_TYPE", msg.get(4));
					configuration.save();
					
					// 
					if(cptType==0){
						score = YDM.INSTANCE.YDM_GetBalance(msg.get(0), msg.get(1));
					}else{
						score = DM.INSTANCE.uu_getScoreA(msg.get(0), msg.get(1));
					}
					
				}
				
				data[0] = new Integer(userID);
				data[1] = new Integer(score);
				data[2] = msg.get(0);
				
				message = new EngineMessage();
				message.setType(EngineMessageType.IM_UL_STATUS);
				message.setData(data);
				Engine.getInstance().fire(message);
			}			
		});
		
		t.start();
	}
	
	private void ready(){
		//if(accounts!=null&&accounts.size()>0&&mails!=null&&mails.size()>0&&(cptType==2||(cptType!=2&&login))){
		if(accounts!=null&&accounts.size()>0&&(cptType==2||(cptType!=2&&login))){
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
	
	public int getCptType(){
		return this.cptType;
	}
	
	public Map<String, Map<String, byte[]>> getAcccounts(){
		return this.accounts;
	}
	
	public Map<String, DatagramChannel> getChannels(){
		return this.channels;
	}
	
	public Queue<String> getQueue(){
		return this.queue;
	}
	
	public void addTask(Task task){
		try{
			pool.execute(task);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void addChecker(Runnable checker){
		try{
			poolx.execute(checker);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	public void send(TaskSender sender, long delay){
//		try{
//			Thread.sleep(delay);
//			send(sender);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	public void addTask(Runnable task){
		try{
			pool.execute(task);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int getActiveCount(){
		return pool.getActiveCount();
	}
	
	public int getQueueCount(){
		return pool.getQueue().size();
	}
}
