package it.hoyland.spspider.core.test;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import it.hoyland.spspider.core.Spider;
import it.hoyland.spspider.spider.GetaJavaSpider;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final HashMap<Spider, Integer> counts = new HashMap<Spider, Integer>();
		
		Observer obs = new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				for(Spider spider : counts.keySet()){
					if(o.getClass().getName().equals(spider.getClass().getName()) && counts.get(spider).intValue()!=Integer.parseInt((String)arg)){
						System.out.println(o.getClass().getName()+" count updated:"+arg);
						counts.put(spider, Integer.parseInt((String)arg));
					}
				}
			}
		};

		Spider[] spiders = { new GetaJavaSpider(obs) };
		
		for (int i = 0; i < spiders.length; i++) {
			counts.put(spiders[i], new Integer(0));
		}
		
		while(true){
			for (int i = 0; i < spiders.length; i++) {
				(new Thread(spiders[i])).start();
			}
			try{
				Thread.sleep(60*1000*2); // 2 min 
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
