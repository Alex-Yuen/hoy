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

public class ReadyRequest extends Request {

	public ReadyRequest(HashMap<String, String> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现房间资料功能
		//DESK|0~9|CURRENTCOUNT|ROOM1COUNT|...@time
		this.userId = parameters.get("uid")!=null?Integer.parseInt(parameters.get("uid")):0;
		String ready = parameters.get("cmd");
		
		User currentUser = null;
		Scene currentScene = null;
		Room currentRoom = null;
		Desk currentDesk = null;
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
		
		//获取当前房间
		if(currentScene!=null){
			Iterator<Room> it = currentScene.getRooms().iterator();
			int i = 0;
			if(it.hasNext()&&i<currentUser.getRoomId()){
				currentRoom = (Room)it.next();
				i++;
			}
		}
		
		//获取进入的桌子
		if(currentRoom!=null){
			for(Desk d:currentRoom.getDesks()){
				if(d.getId()==currentUser.getDeskId()){
					currentDesk = d;
					break;
				}
			}
		}
		
		StringBuffer data = new StringBuffer();
		
		if(currentDesk!=null&&currentUser!=null){
			//更新用户在线信息之位置
			if("READY".equals(ready)){
				currentUser.setStart(true);
			}else{
				currentUser.setStart(false);
			}
			
			String position = "-1";
			
			for(String p:currentDesk.getUsers().keySet()){
				User u = currentDesk.getUsers().get(p);
				if(u.getId()==this.userId){
					position = p;
					break;
				}
			}
			
			//发送信息给桌子其他人
			if(currentDesk.currentCount()>1){
				for(String pos:currentDesk.getUsers().keySet()){
					User u = currentDesk.getUsers().get(pos);
					if(u.getId()!=currentUser.getId()){
						Message m = getMessage(String.valueOf(u.getId()));
						StringBuffer bs = new StringBuffer();
						bs.append(ready);
						bs.append("|");
						bs.append("3");
						bs.append("|");
						bs.append(position);
						m.add(bs.toString());
					}
				}
			}
			
			//发送信息给房间其他人
			for(User u:currentRoom.getUsers()){
				Message m = getMessage(String.valueOf(u.getId()));
				StringBuffer bs = new StringBuffer();
				bs.append(ready);
				bs.append("|");
				bs.append("3");
				bs.append("|");
				bs.append(currentDesk.getId());
				bs.append("|");
				bs.append(position);
				m.add(bs.toString());
			}
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
