package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.core.Request;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class ScenesRequest extends Request {

	public ScenesRequest(Map<String, String[]> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//实现所有场资料
		//SCENES|0~9|SCENECOUNT|ROOM1TYPE|...@time
		this.userId = parameters.get("UID")!=null?Integer.parseInt(parameters.get("UID")[0]):0;
		
		User currentUser = null;
		//当前用户
		if(this.userId!=0){
			for(User u:this.onlineList){
				if(u.getId()==this.userId){
					currentUser = u;
					break;
				}
			}
		}
				
		StringBuffer data = new StringBuffer();
		
		if(currentUser!=null){

			//更新用户在线信息之位置
			for(User u:onlineList){
				if(this.userId==u.getId()){
					u.setSceneId(0);
					break;
				}
			}
			
			data.append("SCENES");
			data.append('|');
			data.append("1");
			data.append('|');
			data.append(this.scenes.size());
			data.append('|');
			for(Scene s:scenes){
				data.append(s.getType());
				data.append('|');
			}
			
			data.deleteCharAt(data.length()-1);
		}else{
			data.append("SCENES");
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
