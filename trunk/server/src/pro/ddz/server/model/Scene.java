package pro.ddz.server.model;

import java.util.ArrayList;

public class Scene {
	private int id;
	protected int type; //≥° Ù–‘, ±£¡Ù
	private ArrayList<Room> rooms;
	public static int DESK_PER_ROOM = 12;
	
	public Scene(int id, int size){
		this.id = id;
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
}
