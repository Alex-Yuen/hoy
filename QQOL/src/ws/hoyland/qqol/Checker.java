package ws.hoyland.qqol;

import java.util.Date;

import ws.hoyland.util.Converts;
import ws.hoyland.util.EngineMessage;

public class Checker implements Runnable {
	private Task task;
	
	public Checker(Task task){
		this.task = task;
	}
	@Override
	public void run() {
		//休眠一段时间之后，进行检测
		double itv = Math.pow(2, task.getRetry()+1)+1;
		try{
			Thread.sleep((long)(1000*itv));
		}catch(Exception e){
			e.printStackTrace();
		}
		System.err.println("checker:"+task.getAccount()+"/"+task.getRetry()+"/"+task.getST());
		if(Engine.getInstance().getAcccounts().get(task.getAccount()).get(task.getST()+"_"+Converts.bytesToHexString(task.getSEQ()))==null&&Engine.getInstance().getAcccounts().get(task.getAccount()).get("0017L")==null){
			Task taskx = null;
			System.err.println("checker is null:"+task.getAccount()+"/"+task.getRetry()+"/"+task.getST());
			if(task.getRetry()==2){//超时
				tf();
				info("超时, 重新登录");
				synchronized(Engine.getInstance().getChannels()) {
					try {
						Engine.getInstance().getChannels().get(task.getAccount()).close();
						info("Channel closed");
					} catch (Exception e) {
						e.printStackTrace();
					}
					Engine.getInstance().getChannels().remove(task.getAccount());
				}
				
				if(Engine.getInstance().getAcccounts().get(task.getAccount()).get("login")!=null){
					Engine.getInstance().getAcccounts().get(task.getAccount()).put("timeout", "T".getBytes());//已经登录的，设置免码登录
				}
				Engine.getInstance().getAcccounts().get(task.getAccount()).remove("login");
				Engine.getInstance().getAcccounts().get(task.getAccount()).remove("0058DOING");
				taskx = new Task(Task.TYPE_0825, task.getAccount());//不行就重新登录
			}else{
				taskx = new Task(task.getType(), task.getAccount());
				taskx.setRetry((byte)(task.getRetry()+1));
			}
			Engine.getInstance().addTask(taskx);
		}else{
			Engine.getInstance().getAcccounts().get(task.getAccount()).remove(task.getST()+"_"+Converts.bytesToHexString(task.getSEQ()));
//			if("0058".equals(task.getST())){
//				Engine.getInstance().getAcccounts().get(task.getAccount()).remove("0058DOING");
//			}
		}
	}
	
	private void tf() {//task finish
		int id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(task.getAccount()).get("id")));
		EngineMessage message = new EngineMessage();
		message.setTid(id);
		message.setType(EngineMessageType.IM_TF);
		String tm = Util.format(new Date());		
		System.err.println("["+task.getAccount()+"]"+"任务终止"+"("+tm+")");
		Engine.getInstance().fire(message);
	}
	
	private void info(String info){
		int id = Integer.parseInt(new String(Engine.getInstance().getAcccounts().get(task.getAccount()).get("id")));
		
		EngineMessage message = new EngineMessage();
		message.setTid(id);
		message.setType(EngineMessageType.IM_INFO);
		message.setData(info);
		
		String tm = Util.format(new Date());		
		System.err.println("["+task.getAccount()+"]"+info+"("+tm+")");
		Engine.getInstance().fire(message);
	}
}
