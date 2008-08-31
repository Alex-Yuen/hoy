package pro.ddz.server.model;

import java.util.ArrayList;

public class Scene {
	private int id;
	protected int type; //≥° Ù–‘, ±£¡Ù
	private ArrayList<Room> rooms;
	public static int NOMAL_ROOM = 1;
	public static int GOLD_ROOM = 2;
	public static int DESK_PER_ROOM = 12;
	
	public Scene(int id, int size, int type){
		this.id = id;
		this.type = type;
		this.rooms = new ArrayList<Room>();
		Room room = null;
		for(int i=0; i<size; i++){
			room = new Room(id*10+i+1, DESK_PER_ROOM);
			this.rooms.add(room);
		}
	}
	
	public int size(){
		return this.rooms.size();
	}
	
	public int getId(){
		return this.id;
	}
	
	public ArrayList<Room> getRooms(){
		return this.rooms;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}
