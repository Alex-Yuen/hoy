package ws.hoyland.sszs;

import java.util.List;
import java.util.Observable;

/**
 * 核心引擎，UI以及其他线程观察此Engine
 * @author Administrator
 *
 */
public class Engine extends Observable {
	
	private static Engine instance;
	
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
	public void fire(int type, Object message){
		switch(type){
			case EngineMessage.IM_CONFIG_UPDATED:
				//暂不做处理
				//this.setChanged();
				break;
			case EngineMessage.IM_USERLOGIN:
				uulogin(message);
				break;
			case EngineMessage.IM_UL_STATUS:
				Integer i = (Integer)message;				
				this.setChanged();
				if(i==0x2586){
					//this.setChanged();
					this.notifyObservers(String.valueOf(EngineMessage.OM_LOGINING));
				}else{
					this.notifyObservers(EngineMessage.OM_LOGINED+":"+i);
				}
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
				Engine.getInstance().fire(EngineMessage.IM_UL_STATUS, 0x2586);
				
				int userID;
				DM.INSTANCE.uu_setSoftInfoA(94034, "0c324570e9914c20ad2fab51b50b3fdc");				
				userID = DM.INSTANCE.uu_loginA(msg.get(0), msg.get(1));
				
				Engine.getInstance().fire(EngineMessage.IM_UL_STATUS, userID);
			}			
		});
		
		t.start();
	}
}
