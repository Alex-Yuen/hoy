package pro.ddz.server.model;

import java.util.HashMap;
import java.util.Random;

public class Desk {
	private int id;
	private int size;
	private boolean start;
	private HashMap<String, User> users;
	private HashMap<String, String[]> cards;
	private HashMap<String, Integer> scores;
	
	public Desk(int id, int size){
		this.id = id;
		this.size = size;
		this.start = false;
		this.scores = new HashMap<String, Integer>();
		this.users = new HashMap<String, User>();
		this.cards = new HashMap<String, String[]>();
	}
	
	public int size(){
		return this.size;
	}
	
	public int currentCount(){
		return this.users.size();
	}
	
	public int getId(){
		return this.id;
	}
	
	public HashMap<String, User> getUsers(){
		return this.users;
	}
	
	public int sitDown(User user){
		boolean contain = false;
		synchronized(this){
			for(int i=0;i<this.size;i++){
				User u = (User)this.users.get(String.valueOf(i));
				if(u!=null&&u.getId()==user.getId()){
					contain = true;
					break;
				}
			}
			
			if(!contain&&this.users.size()<this.size){
				for(int i=0;i<this.size;i++){
					User u = (User)this.users.get(String.valueOf(i));
					if(u==null){
						this.users.put(String.valueOf(i), user);
						return i;
					}
				}	
			}
		}
		return -1;
	}
	
	public int leftUp(User user){
		for(String key:this.users.keySet()){
			User u = (User)this.users.get(key);
			if(u.getId()==user.getId()){
				this.users.remove(key);
				this.start = false;//有人离开，重新设置为默认为准备好状态
				return Integer.parseInt(key);
			}
		}
		return -1;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}
	
	public void deal(){
		this.cards.clear();
		String[] toBeDealCards = new String[54];
		for(int i=0;i<54;i++){
			toBeDealCards[i] = String.valueOf(i);
		}
		
		//洗牌
		String tmp = null;
		int index = -1;
		Random rnd = new Random();
		for(int i=0;i<54;i++){
			tmp = toBeDealCards[i];
			index = rnd.nextInt(54);
			toBeDealCards[i] = toBeDealCards[index];
			toBeDealCards[index] = tmp;
		}
		
		//分组
		int m = 0;
		for(int i=0;i<3;i++){
			String[] result = new String[20];
			for(int j=0;j<17;j++){
				result[j] = toBeDealCards[m++];
			}
			result[17] = toBeDealCards[51];
			result[18] = toBeDealCards[52];
			result[19] = toBeDealCards[53];
			
			this.cards.put(String.valueOf(i), result);
		}
	}

	public HashMap<String, String[]> getCards() {
		return cards;
	}
	
	public synchronized int[] bet(String position, int score){
		int[] result = new int[2];
		if(this.scores.size()<3){
			this.scores.put(position, new Integer(score));
			if(this.scores.size()!=3){
				result[0] = -1;
			}else {
				result[0] = 0;
				result[1] = this.scores.get("0").intValue();
				for(String pos:this.scores.keySet()){
					int v = this.scores.get(pos).intValue();
					if(v>result[1]){
						result[0] = Integer.parseInt(pos);
						result[1] = v;
					}
				}
			}
		}else{
			result[0] = -1;
		}
		return result;
	}
}
