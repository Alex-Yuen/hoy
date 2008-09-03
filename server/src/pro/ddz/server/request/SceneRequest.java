package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.core.Request;
import pro.ddz.server.model.Room;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class SceneRequest extends Request {

	public SceneRequest(HashMap<String, String> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现场景主要人数资料功能
		//SCENE|0~9|ROOMCOUNT|ROOM1COUNT|...@time
		String sceneId = parameters.get("scene-id");
		this.userId = parameters.get("uid")!=null?Integer.parseInt(parameters.get("uid")):0;
		
		User currentUser = null;
		Scene reqScene = null;
		//当前用户
		if(this.userId!=0){
			for(User u:this.onlineList){
				if(u.getId()==this.userId){
					currentUser = u;
					break;
				}
			}
		}
		
		//当前场景
		if(sceneId!=null){
			for(Scene scene:scenes){
				if(scene.getId()==Integer.parseInt(sceneId)){
					reqScene = scene;
					break;
				}
			}
		}
		
		StringBuffer data = new StringBuffer();
		
		if(currentUser!=null&&reqScene!=null){

			//更新用户在线信息之位置
			for(User u:onlineList){
				if(this.userId==u.getId()){
					u.setSceneId(reqScene.getId());
					break;
				}
			}
			
			//处理用户离开房间
			Room room = null;
			if(currentUser.getDeskId()!=0){
				Iterator<Room> it = reqScene.getRooms().iterator();
				int i = 0;
				if(it.hasNext()&&i<currentUser.getDeskId()){
					room = (Room)it.next();
					i++;
				}
				room.leftRoom(currentUser);
				currentUser.setRoomId(0);
			}
			
			data.append("SCENE");
			data.append('|');
			data.append("1");
			data.append('|');
			data.append(reqScene.size());
			data.append('|');
			for(Room r:reqScene.getRooms()){
				data.append(r.getUsers().size());
				data.append('|');
			}
			
			data.deleteCharAt(data.length()-1);
		}else{
			data.append("SCENE");
			data.append('|');
			data.append("2");
		}

		if(this.isAsync){
			//System.out.println("[COME TO HERE]");
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
		StringBuffer data = new StringBuffer();
		for(int i=0;i<5;i++){
			data.append("A");
			data.append(">");
		}
		data.deleteCharAt(data.length()-1);
		System.out.println(data.toString());
	}

}
