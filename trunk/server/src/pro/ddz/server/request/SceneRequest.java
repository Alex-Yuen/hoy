package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.message.Message;
import pro.ddz.server.model.Room;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class SceneRequest extends Request {

	public SceneRequest(HttpServletRequest req, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(req, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现场景主要人数资料功能
		//SCENE|0~9|ROOMCOUNT|ROOM1COUNT|...@time
		String sceneId = req.getHeader("Scene-ID");
		this.userId = req.getHeader("UID")!=null?Integer.parseInt(req.getHeader("UID")):0;
		
		Scene reqScene = null;
		if(sceneId!=null){
			for(Scene scene:scenes){
				if(scene.getId()==Integer.parseInt(sceneId)){
					reqScene = scene;
					break;
				}
			}
		}
		
		StringBuffer data = new StringBuffer();
		
		if(reqScene!=null){
			data.append("SCENE");
			data.append('|');
			data.append("1");
			data.append('|');
			data.append(reqScene.size());
			data.append('|');
			for(Room room:reqScene.getRooms()){
				data.append(room.getUsers().size());
				data.append('|');
			}
			
			data.deleteCharAt(data.length()-1);
		}else{
			data.append("SCENE");
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
		StringBuffer data = new StringBuffer();
		for(int i=0;i<5;i++){
			data.append("A");
			data.append(">");
		}
		data.deleteCharAt(data.length()-1);
		System.out.println(data.toString());
	}

}
