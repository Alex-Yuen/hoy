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

public class DeskRequest extends Request {

	public DeskRequest(HashMap<String, String> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//ʵ�ַ������Ϲ���
		//DESK|0~9|CURRENTCOUNT|ROOM1COUNT|...@time
		String deskId = parameters.get("desk-id");
		this.userId = parameters.get("uid")!=null?Integer.parseInt(parameters.get("uid")):0;
		
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
			Iterator<Room> it = currentScene.getRooms().iterator();
			int i = 0;
			if(it.hasNext()&&i<currentUser.getRoomId()){
				currentRoom = (Room)it.next();
				i++;
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
		
		StringBuffer data = new StringBuffer();
		
		if(reqDesk!=null&&currentUser!=null){
			//�����û�������Ϣ֮λ��
			currentUser.setDeskId(Integer.parseInt(deskId));
				
			//�û�����reqDesk
			int sitDown = reqDesk.sitDown(currentUser);
								
			//debug
			//sitDown = true;
			if(sitDown>-1){

				//��ǰ�����û���>1������֪ͨ
				if(reqDesk.currentCount()>1){
					for(User u:reqDesk.getUsers().values()){
						if(u.getId()!=currentUser.getId()){
							Message m = getMessage(String.valueOf(u.getId()));
							StringBuffer bs = new StringBuffer();
							bs.append("SIT");
							bs.append("|");
							bs.append("1");
							bs.append("|");
							bs.append(reqDesk.getId());
							bs.append("|");
							bs.append(sitDown);
							bs.append('|');
							bs.append(currentUser.getNickName());
							bs.append('|');
							bs.append(currentUser.getScore());
							bs.append('|');
							bs.append(currentUser.isSexual()?1:0);
							bs.append('|');
							bs.append(currentUser.isStart()?1:0);
							m.add(bs.toString());
						}
					}
				}
					
				//֪ͨ�������������ң�������ȥ��
				for(User ux:currentRoom.getUsers()){
					if(ux.getDeskId()==0){
						Message m = getMessage(String.valueOf(ux.getId()));
						StringBuffer bs = new StringBuffer();
						bs.append("SIT");
						bs.append("|");
						bs.append("1");
						bs.append("|");
						bs.append(reqDesk.getId());
						bs.append("|");
						bs.append(sitDown);
						bs.append('|');
						bs.append(currentUser.isSexual()?1:0);
						bs.append('|');
						bs.append(currentUser.isStart()?1:0);
						m.add(bs.toString());
					}
				}
					
				data.append("DESK");
				data.append('|');
				data.append("1");
				data.append('|');
				data.append(reqDesk.getUsers().size());
				data.append('|');
				for(int i=0;i<3;i++){
					User u = (User)reqDesk.getUsers().get(String.valueOf(i));
					if(u!=null){
						data.append(i);
						data.append('|');
						data.append(u.getNickName());
						data.append('|');
						data.append(u.getScore());
						data.append('|');
						data.append(u.isSexual()?1:0);
						data.append('|');
						data.append(u.isStart()?1:0);
						data.append('|');
					}
				}
					
				data.deleteCharAt(data.length()-1);
			}else{
				//����
				data.append("DESK");
				data.append('|');
				data.append("3");
			}
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
