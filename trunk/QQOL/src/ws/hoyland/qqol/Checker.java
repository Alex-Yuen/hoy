package ws.hoyland.qqol;

import java.util.Date;

import ws.hoyland.util.EngineMessage;

public class Checker implements Runnable {
	private String account;
	private String type;
	private String seq;
	private int retry;
	
	public static int RT = 4;//总共尝试次数
	
	public Checker(String account, String type, String seq, int retry){
		this.account = account;
		this.type = type;
		this.seq = seq;
		this.retry = retry;
	}
	@Override
	public void run() {
		//休眠一段时间之后，进行检测
		double itv = Math.pow(2, retry+1)+1;
		try{
			Thread.sleep((long)(1000*itv));
		}catch(Exception e){
			//e.printStackTrace();
		}
		//System.err.println("checker:"+task.getAccount()+"/"+task.getRetry()+"/"+task.getST());
		if(Engine.getInstance().getAcccounts().get(account).get(type+"_"+seq)==null&&Engine.getInstance().getAcccounts().get(account).get("0017L")==null){
			Task taskx = null;
			System.err.println("checker is null:"+account+"/"+retry+"/"+type);
			if(retry==RT-1){//超时
				tf();
				info("超时");
				synchronized(Engine.getInstance().getChannels()) {
					try {
						Engine.getInstance().getChannels().get(account).close();
						info("连接关闭");
					} catch (Exception e) {
						e.printStackTrace();
					}
					Engine.getInstance().getChannels().remove(account);
				}

				/**
				if(Engine.getInstance().getAcccounts().get(task.getAccount()).get("login")!=null){
					Engine.getInstance().getAcccounts().get(task.getAccount()).put("timeout", "T".getBytes());//已经登录的，设置免码登录
				}
				**/
				Engine.getInstance().getAcccounts().get(account).remove("login");
				Engine.getInstance().getAcccounts().get(account).remove("0058DOING");
				System.err.println(account+" removing2 0058DOING:"+Engine.getInstance().getAcccounts().get(account).get("0058DOING"));
				taskx = new Task(Task.TYPE_0825, account);//不行就重新登录
			}else{
				taskx = new Task(findType(type), account);
				taskx.setRetry((byte)(retry+1));
			}
			Engine.getInstance().addTask(taskx);
		}else{
			Engine.getInstance().getAcccounts().get(account).remove(type+"_"+seq);
//			if("0058".equals(task.getST())){
//				Engine.getInstance().getAcccounts().get(task.getAccount()).remove("0058DOING");
//			}
		}
	}

	private byte findType(String type){
		for(byte i=0;i<Task.STS.length;i++){
			if(type.equals(Task.STS[i])){
				return (byte)(i+1);
			}
		}
		return -1;
	}
	
	private void tf() {//task finish
		int id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(account).get("id")));
		EngineMessage message = new EngineMessage();
		message.setTid(id);
		message.setType(EngineMessageType.IM_TF);
		String tm = Util.format(new Date());		
		System.err.println("["+account+"]"+"任务终止"+"("+tm+")");
		Engine.getInstance().fire(message);
	}
	
	private void info(String info){
		int id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(account).get("id")));
		
		EngineMessage message = new EngineMessage();
		message.setTid(id);
		message.setType(EngineMessageType.IM_INFO);
		message.setData((Engine.getInstance().getAcccounts().get(account).get("login")!=null)+"|"+info);
		
		String tm = Util.format(new Date());		
		System.err.println("["+account+"]"+info+"("+tm+")");
		Engine.getInstance().fire(message);
	}
}
