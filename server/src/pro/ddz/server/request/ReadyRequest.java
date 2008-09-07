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

public class ReadyRequest extends Request {

	public ReadyRequest(HashMap<String, String> parameters, HashMap<String, Message> messageMap, DataAccessObject dao, ArrayList<User> onlineList, ArrayList<Scene> scenes){
		super(parameters, messageMap, dao, onlineList, scenes);
	}
	
	@Override
	public void execute() {
		//ʵ�ַ������Ϲ���
		//DESK|0~9|CURRENTCOUNT|ROOM1COUNT|...@time
		this.userId = parameters.get("uid")!=null?Integer.parseInt(parameters.get("uid")):0;
		String ready = parameters.get("cmd");
		
		User currentUser = null;
		Scene currentScene = null;
		Room currentRoom = null;
		Desk currentDesk = null;
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
		if(currentRoom!=null){
			for(Desk d:currentRoom.getDesks()){
				if(d.getId()==currentUser.getDeskId()){
					currentDesk = d;
					break;
				}
			}
		}
		
		StringBuffer data = new StringBuffer();

		int start = 0;
		
		//�����ʼ�ˣ���������
		if(currentDesk!=null&&currentUser!=null&&!currentDesk.isStart()){
			//�����û�������Ϣ֮λ��
			if("READY".equals(ready)){
				currentUser.setStart(true);
				for(User u:currentDesk.getUsers().values()){
					if(u.isStart()){
						start++;
					}
				}
				
				if(start==currentDesk.size()){
					//���ӷ���
					currentDesk.deal();
					currentDesk.setStart(true);
				}
				
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
			
			//������Ϣ������������
			if(currentDesk.currentCount()>1){
				for(String pos:currentDesk.getUsers().keySet()){
					User u = currentDesk.getUsers().get(pos);
					Message m = getMessage(String.valueOf(u.getId()));
					
					if(u.getId()!=currentUser.getId()){
						StringBuffer bs = new StringBuffer();
						if(currentDesk.isStart()){
							bs.append("START");
							bs.append("|");
							bs.append("1");
						}else{
							bs.append(ready);
							bs.append("|");
							bs.append("3");
							bs.append("|");
							bs.append(position);
						}
						m.add(bs.toString());
					}
					
					//����, ֪ͨ����������
					if(currentDesk.isStart()){
						StringBuffer bs = new StringBuffer();
						bs.append("DEAL");
						bs.append("|");
						bs.append("1");
						bs.append("|");
						for(String card:currentDesk.getCards().get(pos)){
							bs.append(card);
							bs.append("|");
						}
						bs.deleteCharAt(data.length()-1);
						m.add(bs.toString());
					}
				}
			}
			
			//������Ϣ������������
			for(User u:currentRoom.getUsers()){
				Message m = getMessage(String.valueOf(u.getId()));
				StringBuffer bs = new StringBuffer();
				if(currentDesk.isStart()){
					bs.append("START");
					bs.append("|");
					bs.append("1");
					bs.append("|");
					bs.append(currentDesk.getId());
				}else{
					bs.append(ready);
					bs.append("|");
					bs.append("3");
					bs.append("|");
					bs.append(currentDesk.getId());
					bs.append("|");
					bs.append(position);
				}
				m.add(bs.toString());
			}
			
			if(currentDesk.isStart()){
				data.append("START");
				data.append("|");
				data.append("1");
			}else{
				data.append(ready);
				data.append("|");
				data.append("1");
			}
		}else{
			if(currentDesk.isStart()){
				data.append(ready);
				data.append("|");
				data.append("4");
			}else{
				data.append(ready);
				data.append("|");
				data.append("2");
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
