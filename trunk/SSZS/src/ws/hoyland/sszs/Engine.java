package ws.hoyland.sszs;

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
			case EngineMessage.MSG_CONFIG_UPDATED:
				break;
			default:
				break;
		}
	}
}
