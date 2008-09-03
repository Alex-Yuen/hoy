package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.core.Request;
import pro.ddz.server.model.Desk;
import pro.ddz.server.model.Room;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class DeskRequest extends Request {

	public DeskRequest(Map<String, String[]> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现房间资料功能
		//DESK|0~9|CURRENTCOUNT|ROOM1COUNT|...@time
		String deskId = parameters.get("Desk-ID")[0];
		this.userId = parameters.get("UID")!=null?Integer.parseInt(parameters.get("UID")[0]):0;
		
		Desk reqDesk = null;
		User currentUser = null;
		
		Scene currentScene = null;
		Room currentRoom = null;
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
		if(currentRoom!=null&&deskId!=null){
			Iterator<Desk> it = currentRoom.getDesks().iterator();
			int i = 0;
			if(it.hasNext()&&i<Integer.parseInt(deskId)){
				reqDesk = (Desk)it.next();
				i++;
			}
		}
		
		StringBuffer data = new StringBuffer();
		
		if(currentUser!=null&&reqDesk!=null){

			//更新用户在线信息之位置
			currentUser.setDeskId(Integer.parseInt(deskId));
			
			//用户加入reqDesk
			reqDesk.sitDown(currentUser);
			
			data.append("DESK");
			data.append('|');
			data.append("1");
			data.append('|');
			data.append(reqDesk.getUsers().size());
			data.append('|');
			for(String pos:reqDesk.getUsers().keySet()){
				User u = (User)reqDesk.getUsers().get(pos);
				data.append('|');
				data.append(pos);
				data.append('|');
				data.append(u.isSexual()?1:0);
				data.append('|');
				data.append(u.isStart()?1:0);
				data.append('|');
			}
			
			data.deleteCharAt(data.length()-1);
		}else{
			data.append("DESK");
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
