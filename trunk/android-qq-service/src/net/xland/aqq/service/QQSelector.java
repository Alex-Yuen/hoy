package net.xland.aqq.service;

import java.nio.channels.Selector;

public class QQSelector {
	public static Selector selector;
	
	static{
		try{
			selector = Selector.open();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
