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
		String roomId = parameters.get("room-id");
		this.userId = parameters.get("uid")!=null?Integer.parseInt(parameters.get("uid")):0;
		
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
		
		if(reqRoom!=null&&currentUser!=null){
			//更新用户在线信息之位置
			currentUser.setRoomId(Integer.parseInt(roomId));
			//处理用户离开桌子
			Desk currentDesk = null;
			if(currentUser.getDeskId()!=0){
				for(Desk desk:reqRoom.getDesks()){
					if(desk.getId()==currentUser.getDeskId()){
						currentDesk = desk;
						break;
					}
				}
				currentDesk.leftUp(currentUser);
				currentUser.setDeskId(0);
				//如果还有人，则通知其他人，你离开桌子了
				if(currentDesk.currentCount()>0){
					for(User u:currentDesk.getUsers().values()){
						Message m = getMessage(String.valueOf(u.getId()));
						StringBuffer bs = new StringBuffer();
						bs.append("LEFT");
						bs.append("|");
						bs.append("1");
						bs.append("|");
						bs.append(this.userId);
						m.add(bs.toString());
					}
				}
			}
				
			//用户加入reqRoom
			boolean join = reqRoom.joinRoom(currentUser);
			//debug
			join = true;
			if(join){
				data.append("ROOM");
				data.append('|');
				data.append("1");
				data.append('|');
				data.append(reqRoom.getDesks().size());
				data.append('|');
				data.append(reqRoom.getUsers().size());
				data.append('|');
				for(Desk d:reqRoom.getDesks()){
					if(d.currentCount()>0){
						data.append(d.getId());
						data.append('|');
						for(int i=0;i<3;i++){
							User u = (User)d.getUsers().get(String.valueOf(i));
							if(u!=null){
								data.append(i);
								data.append('|');
								data.append(u.isSexual()?1:0);
								data.append('|');
								data.append(u.isStart()?1:0);
								data.append('|');
							}
						}
					}
				}
					
				data.deleteCharAt(data.length()-1);
			}else{
				//人满
				data.append("ROOM");
				data.append('|');
				data.append("3");
			}
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
