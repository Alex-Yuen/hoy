package pro.ddz.server.request;

import java.util.ArrayList;
import java.util.HashMap;

import pro.ddz.server.dao.DataAccessObject;
import pro.ddz.server.core.Message;
import pro.ddz.server.core.Request;
import pro.ddz.server.model.Desk;
import pro.ddz.server.model.Room;
import pro.ddz.server.model.Scene;
import pro.ddz.server.model.User;

public class BetRequest extends Request {

	public BetRequest(HashMap<String, String> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//ʵ�ַ������Ϲ���
		//DESK|0~9|CURRENTCOUNT|ROOM1COUNT|...@time
		String deskId = parameters.get("desk-id");
		this.userId = parameters.get("uid")!=null?Integer.parseInt(parameters.get("uid")):0;
		int score = parameters.get("score")!=null?Integer.parseInt(parameters.get("score")):0;
		
		Desk reqDesk = null;
		User currentUser = null;
		
		Scene currentScene = null;
		Room currentRoom = null;
		//��ȡ��ǰ�û�
		if(this.userId!=0){
			for(User u:this.onlineList){
				if(u.getId()==this.userId){
					currentUser = u;
					break;
				}
			}
		}
				
		//��ȡ��ǰ����
		if(currentUser!=null){
			for(Scene s:this.scenes){
				if(s.getId()==currentUser.getSceneId()){//��ȡ��ǰ�û�λ��
					currentScene = s;
					break;
				}
			}
		}
		
		//��ȡ��ǰ����
		if(currentScene!=null){
			for(Room room:currentScene.getRooms()){
				if(room.getId()==currentUser.getRoomId()){
					currentRoom = room;
					break;
				}
			}
		}
		
		//��ȡ���������
		if(currentRoom!=null&&deskId!=null){
			for(Desk d:currentRoom.getDesks()){
				if(d.getId()==Integer.parseInt(deskId)){
					reqDesk = d;
					break;
				}
			}
		}
		
		//��ȡ�û�λ��
		String position = "-1";
		
		for(String p:reqDesk.getUsers().keySet()){
			User u = reqDesk.getUsers().get(p);
			if(u.getId()==this.userId){
				position = p;
				break;
			}
		}
		
		StringBuffer data = new StringBuffer();
		
		if(reqDesk!=null&&currentUser!=null){
			int[] lastScore = reqDesk.bet(position, score);
			if(lastScore[0]>-1){
				//�����سɹ���ֱ�ӵķ�����Ϣ
				for(User u:reqDesk.getUsers().values()){
					Message m = getMessage(String.valueOf(u.getId()));
					StringBuffer bs = new StringBuffer();
					bs.append("LORD");
					bs.append('|');
					bs.append("1");
					bs.append(lastScore[0]);
					bs.append('|');
					bs.append(lastScore[1]);
					m.add(bs.toString());
				}
			}else{
				data.append("BET");
				data.append('|');
				data.append("1");
			}
		}else{
			data.append("BET");
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
