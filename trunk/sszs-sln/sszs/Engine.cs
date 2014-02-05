using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ws.hoyland.util;
using System.Collections;
using System.IO;
using System.Text.RegularExpressions;

namespace ws.hoyland.sszs
{
    public class Engine : Observable
    {
        	private static Engine instance;
	private List<String> accounts;
	private List<String> mails;
	private bool login = false;
	private int cptType = 0;
	private bool running = false;
	private HashSet<Task> tasks = new HashSet<Task>();
	private int mindex = 0;
	private int mcount = 0;
	private int recc = 0;//reconnect count
	private int frecc = 0;//finished
	//private String cip = null; //current ip
	
	private int atrecc; //config data
	
	private int pausec = 0;
	private int fpausec = 0;
	private int atpausec;
	
	private StreamWriter[] output = new StreamWriter[5]; //成功，失败，未运行
	private String[] fns = new String[]{"成功", "失败", "未运行帐号", "已使用邮箱", "未使用邮箱"}; 
	private String xpath = AppDomain.CurrentDomain.BaseDirectory;
	private int lastTid = 0;
	private bool pause = false;
	private bool freq = false;
	private String recflag = "false";
	
	private int pc = 0;//pause count;
	
	private Hashtable ips = new Hashtable();
	
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
				
				if(data==null){
					msg.setType(EngineMessageType.OM_LOGINING);
					this.notifyObservers(msg);
				}else{
					Object[] objs = (Object[])data;
					Int32 it = (Int32)objs[0];
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
				String[] paths = Regex.Split((String)message.getData(), "\\|");
				
				try {
					msg = new EngineMessage();
					msg.setType(EngineMessageType.OM_CLEAR_ACC_TBL);
					
					this.notifyObservers(msg);
					
					accounts = new ArrayList<String>();
					
					 Encoding ecdtype = EncodingType.GetType(paths[1]);
                    FileStream fs = new FileStream(paths[1], FileMode.Open);
                    StreamReader m_streamReader = new StreamReader(fs, ecdtype);
                    m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);

					String line = null;
					int i = 0;
					while ((line = m_streamReader.ReadLine()) != null) {
						if (!line.Equals("")) {
                            line = (++i) + "----" + line;
                            string[] lns = Regex.Split(line, "----");
                            List<string> listArr = new List<string>();
                            listArr.AddRange(lns);
                            listArr.Insert(3, "初始化");
                            lns = listArr.ToArray();

							accounts.add(line);
//							if (lns.size() == 3) {
//								lns.add("0");
//								lns.add("初始化");
//								//line += "----0----初始化";
//							} else {
//								//line += "----初始化";
//								lns.add("初始化");
//							}
							
							String[] items = lns.ToArray();
					        					        
					        msg = new EngineMessage();
					        msg.setType(EngineMessageType.OM_ADD_ACC_TBIT);
					        msg.setData(items);
					        
							this.notifyObservers(msg);							
						}
						i++;
					}

					 m_streamReader.Close();
                    m_streamReader.Dispose();
                    fs.Close();
                    fs.Dispose();
					
					if (accounts.size() > 0) {
						List<String> params = new ArrayList<String>();
						params.add(String.valueOf(accounts.size()));
						params.add(paths[1]);
						
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
				String path = (String)message.getData();
				
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
						if (!line.Equals("")) {
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
				running = !running;
				
				msg = new EngineMessage();
				msg.setType(EngineMessageType.OM_RUNNING);
				msg.setData(running);
				this.setChanged();
				this.notifyObservers(msg);
				
				if(running){
					recflag = configuration.getProperty("REC_TYPE"); //每次开始，读一次
					//创建日志文件
					//long tm = System.currentTimeMillis();
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

					Integer[] flidx = (Integer[]) message.getData();
					
					mindex = flidx[2]; //mfirst of SSZS
					if(mindex==-1){
						mindex = 0;
					}
					//for (int i = 0; i < accounts.size(); i++) {
					for (int i = flidx[0]; i <= flidx[1]; i++) {
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
					shutdown();
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

				String[] ms = null;
				lock(MailObject.getInstance()){
					//Random rnd = new Random();
					System.err.println("X:A");
					System.err.println("X1:"+mcount+"/"+mindex+"/"+mails.size()+"/"+message.getTid());
					if(mcount==Integer.parseInt(configuration.getProperty("EMAIL_TIMES"))){
						System.err.println("X:B");
						mcount = 0;
						mindex++;
					}
					
					System.err.println("X:C");
					
					if(mindex<mails.size()){
						System.err.println("X:D");
						ms = mails.get(mindex).split("----");
						mcount++;
					}
					System.err.println("X:E");
					System.err.println("X2:"+mcount+"/"+mindex+"/"+mails.size()+"/"+message.getTid());
				}
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
			case EngineMessageType.IM_NO_EMAILS:
				shutdown();				
				break;
			case EngineMessageType.IM_START: 
					//优先处理暂停
					pausec++;
					atpausec = Integer.parseInt(configuration.getProperty("ACC_ITV_COUNT"));
					//暂停通知的触发
					if("true".Equals(configuration.getProperty("ACC_ITV_FLAG"))&&pausec==atpausec){
						pausec = 0;
						
						msg = new EngineMessage();
						msg.setTid(-1); //所有task
						msg.setType(EngineMessageType.OM_NP); //need pause
						//msg.setData(message.getData());
						
						this.setChanged();
						this.notifyObservers(msg);
					}
				
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
				
				break;
			case EngineMessageType.IM_FINISH:
					//写入日志
					lastTid = message.getTid();
					String[] dt = (String[])message.getData();
					
					if("1".Equals(dt[0])){//成功
						try{
							//output[0].write(dt[1]+"----"+dt[2]+"----"+dt[3]+"----"+dt[4]+"----"+dt[5]+"----"+cip + "\r\n");
							output[0].write(dt[1]+"----"+dt[2]+"----"+dt[3]+"----"+dt[4]+"----"+dt[5]+ "\r\n");
							output[0].flush();
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
					
					System.err.println("--------------act count:"+pool.getActiveCount()+"/"+pool.getQueue().size());
					//if(pool.getActiveCount()==1){ //当前是最后一个线程
					if(pool.getActiveCount()==1&&pool.getQueue().size()==0){
						//自动关闭
						//TODO						
						running = !running;
						
						msg = new EngineMessage();
						msg.setType(EngineMessageType.OM_RUNNING);
						msg.setData(running);
						this.setChanged();
						this.notifyObservers(msg);
						
						//自动关闭
						Thread t = new Thread(new Runnable(){
							@Override
							public void run() {
								shutdown();
							}							
						});
						t.start();
						//shutdown();
					}
					
					fpausec++;
					atpausec = Integer.parseInt(configuration.getProperty("ACC_ITV_COUNT"));
					//执行暂停
					if("true".Equals(configuration.getProperty("ACC_ITV_FLAG"))&&fpausec==atpausec){
						fpausec = 0;
						try{
							System.err.println("自动暂停...");
							Thread.sleep(60*1000*Integer.parseInt(configuration.getProperty("ACC_ITV_PERIOD")));
							
							//所有线程切换状态
							msg = new EngineMessage();
							msg.setTid(-1); //所有task
							msg.setType(EngineMessageType.OM_NP); 
							//msg.setData(message.getData());
							
							Engine.this.setChanged();
							Engine.this.notifyObservers(msg);
							
							synchronized(PauseXObject.getInstance()){
								try{
									PauseXObject.getInstance().notifyAll();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							
							System.err.println("自动暂停完毕, 继续运行...");
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					
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
	
								bool st = false;
								String cut = "rasdial 宽带连接 /disconnect";
								String link = "rasdial 宽带连接 "
										+ account
										+ " "
										+ password;
								try{
									Thread.sleep(1000*Integer.parseInt(configuration.getProperty("RECON_DELAY")));
									bool fo = true;
									bool fi = true;
									
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
											while(fi&&("true".Equals(configuration.getProperty("AWCONN"))||("false".Equals(configuration.getProperty("AWCONN"))&&tfi<4))){
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
													String ip = rip;
													if("true".Equals(recflag)){
														ip = result.substring(0, result.lastIndexOf("."));
													}
													
											        System.err.println("ip="+ip);
													if(ips.containsKey(ip)){
														long time = ips.get(ip);
														if(System.currentTimeMillis()-time>=1000*60*60*Integer.parseInt(configuration.getProperty("REC_ITV"))){
															System.err.println("IP重复，但超过"+configuration.getProperty("REC_ITV")+"小时，拨号成功:"+ip);
															//cip = rip;
															ips.put(ip, System.currentTimeMillis());
															fi = false;//跳出内循环
															st = true;
															//break;
														}else{
															System.err.println("IP重复，未超过"+configuration.getProperty("REC_ITV")+"小时，重新拨号:"+ip);
															fo = true;
															fi = false;
															tfo = 0;
															st = false;
															//continue;
														}
													}else{
														String[] ipx = ip.split(".");
														if("true".Equals(configuration.getProperty("IP3FLAG"))){
															if((ipx[0].Equals(configuration.getProperty("IP3_1")))&&(ipx[1].Equals(configuration.getProperty("IP3_2")))&&(ipx[2].Equals(configuration.getProperty("IP3_3")))){
																System.err.println("前3段IP不符合条件，重新拨号");
																fo = true;
																fi = false;
																tfo = 0;
																st = false;
															}
														}else{
															System.err.println("IP符合设定条件，拨号成功:"+ip);
															//cip = rip;
															ips.put(ip, new Long(System.currentTimeMillis()));
															fi = false;
															st = true;
														}
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
						
						t.start();
						
					}
				break;
			case EngineMessageType.IM_EXIT:
				//关闭日志文件
				shutdown();
				System.exit(0);
				break;
			case EngineMessageType.IM_PAUSE:
				pause = !pause;
				
				if(pause){
					pc = 0; //pause count;
				}
				
				msg = new EngineMessage();
				msg.setTid(-1); //所有task
				msg.setType(EngineMessageType.OM_PAUSE);
				//msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);	
				
				if(!pause){//继续运行
					//新建成功文件
					DateFormat format = new java.text.SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
					String tm = format.format(new Date());
					//for(int i=0;i<output.length;i++){
						File fff = new File(xpath + fns[0] + "-" + tm + ".txt");
						try {
							if (!fff.exists()) {
								fff.createNewFile();
							}
							
							output[0] = new BufferedWriter(
									new FileWriter(fff));
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					//}
					
					synchronized(PauseObject.getInstance()){
						PauseObject.getInstance().notifyAll();
					}
				}else{// 停止情况下，关闭output[0]
					//see EngineMessageType.IM_PAUSE_COUNT
				}
				break;
			case EngineMessageType.IM_FREQ:
//				recc = 0;
//				frecc = 0;
				freq = true;
				//通知其他需要重拨
				msg = new EngineMessage();
				msg.setTid(-1); //所有task
				msg.setType(EngineMessageType.OM_RECONN);
				//msg.setData(message.getData());
				
				this.setChanged();
				this.notifyObservers(msg);
				break;
			case EngineMessageType.IM_PAUSE_COUNT:
				pc++;
				if(pc==Integer.parseInt(Configuration.getInstance().getProperty("THREAD_COUNT"))){
					//关闭output[0]
					try{
						if(output[0]!=null){
							output[0].close();
							output[0] = null;
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
		}
	}

	private void shutdown() {
		if(pool!=null){
			//pool.shutdown();
			pool.shutdownNow();
		}
				
		//等待所有运行线程执行完毕，关闭日志文件
		while(pool!=null&&pool.getActiveCount()!=0){
			try{
				Thread.sleep(1000);
			}catch(Exception e){
				//
			}
		}
		
		//if(pool!=null){
			//写入未运行帐号日志
			try{
				//if(lastTid!=-1){
				if(output[2]!=null){
					for(int i=lastTid;i<accounts.size();i++){
						String[] accl = accounts.get(i).split("----");
						output[2].write(accl[1]+"----"+accl[2]+ "\r\n");
						output[2].flush();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			};
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
		//}
		
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
					YDM.INSTANCE.YDM_SetAppInfo(66, "1c67c6d83effe082803066704a398229");
				}else{
					DM.INSTANCE.uu_setSoftInfoA(94034, "0c324570e9914c20ad2fab51b50b3fdc");
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
					if("true".Equals(msg.get(2))){
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
		if(accounts!=null&&accounts.size()>0&&mails!=null&&mails.size()>0&&(cptType==2||(cptType!=2&&login))){
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
    }
}
