package pro.ddz.server;

import java.util.ArrayList;

import pro.ddz.server.model.User;

public class Watcher implements Runnable {
	private ArrayList<User> onlineList;
	
	public Watcher(ArrayList<User> onlineList){
		this.onlineList = onlineList;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//���������б�
		System.out.println(onlineList);
	}
}
