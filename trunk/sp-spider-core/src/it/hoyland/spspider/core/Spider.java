package it.hoyland.spspider.core;

import java.util.Observable;
import java.util.Observer;

public abstract class Spider extends Observable implements Runnable{
	
	protected Observer obs;
	protected StringBuffer message;
	
	protected Spider(Observer obs){
		this.obs = obs;
		this.message = new StringBuffer();
	}
	
}
