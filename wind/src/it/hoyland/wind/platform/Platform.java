package it.hoyland.wind.platform;

import it.hoyland.wind.core.Component;

public class Platform implements Component {

	public Platform() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		System.out.println("load...");
	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub
		System.out.println("unload...");
	}

}
