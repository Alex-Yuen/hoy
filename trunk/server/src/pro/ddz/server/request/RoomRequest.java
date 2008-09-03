package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.core.Request;
import pro.ddz.server.model.Desk;
import pro.ddz.server.model.Room;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class RoomRequest extends Request {

	public RoomRequest(HashMap<String, String> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现房间资料功能
		//ROOM|0~9|CURRENTCOUNT|ROOM1COUNT|...@time
		String roomId = parameters.get("Room-ID");
		this.userId = parameters.get("UID")!=null?Integer.parseInt(parameters.get("UID")):0;
		
		Room reqRoom = null;
		User currentUser = null;
		Scene currentScene = null;
		//获取当前用户
		if(this.userId!=0){
			for(User u:this.onlineList){
				if(u.getId()==this.userId){
					currentUser = u;
					break;
				}
			}
		}
		
		//获取当前场景
		if(currentUser!=null){
			for(Scene s:this.scenes){
				if(s.getId()==currentUser.getSceneId()){//获取当前用户位置
					currentScene = s;
					break;
				}
			}
		}
		
		//获取进入房间
		if(currentScene!=null&&roomId!=null){
			Iterator<Room> it = currentScene.getRooms().iterator();
			int i = 0;
			if(it.hasNext()&&i<Integer.parseInt(roomId)){
				reqRoom = (Room)it.next();
				i++;
			}
		}
		
		StringBuffer data = new StringBuffer();
		
		if(currentUser!=null&&reqRoom!=null){

			//更新用户在线信息之位置
			currentUser.setRoomId(Integer.parseInt(roomId));
			//处理用户离开桌子
			Desk desk = null;
			if(currentUser.getDeskId()!=0){
				Iterator<Desk> it = reqRoom.getDesks().iterator();
				int i = 0;
				if(it.hasNext()&&i<currentUser.getDeskId()){
					desk = (Desk)it.next();
					i++;
				}
				desk.leftUp(currentUser);
				currentUser.setDeskId(0);
			}
			
			//用户加入reqRoom
			reqRoom.jionRoom(currentUser);
			
			data.append("ROOM");
			data.append('|');
			data.append("1");
			data.append('|');
			data.append(reqRoom.getDesks().size());
			data.append('|');
			data.append(reqRoom.getUsers().size());
			data.append('|');
			for(Desk d:reqRoom.getDesks()){
//				data.append(u2.getId());
//				data.append('|');
				if(d.currentCount()>0){
					data.append(d.getId());
					for(String pos:d.getUsers().keySet()){
						User u = (User)d.getUsers().get(pos);
						data.append('|');
						data.append(pos);
						data.append('|');
						data.append(u.isSexual()?1:0);
						data.append('|');
						data.append(u.isStart()?1:0);
						data.append('|');
					}
				}
			}
			
			data.deleteCharAt(data.length()-1);
		}else{
			data.append("ROOM");
			data.append('|');
			data.append("2");
		}

		if(this.isAsync){
			getMessage().add(data.toString());
		}else{
			this.result = data.toString();
		}
	}

	@Override
	public boolean isExecutable() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
