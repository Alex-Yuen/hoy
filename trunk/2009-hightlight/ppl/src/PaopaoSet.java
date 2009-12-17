import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

/**
* ������������
*/
public class PaopaoSet extends GameCanvas implements CommandListener
{
	private Paopao midlet=null;
	private Command cmdBack;
	private Command cmdOK;
	private Form f;
	
	private Image blackboard=null;
	private Image setOKImg=null;
	private Image setLostImg=null;
	private Graphics g=null;
	
	
	/**
	* ���캯��
	*/
	public PaopaoSet(Paopao midlet)
	{
		super(false);
		this.midlet=midlet;
		
		//��Ӳ�������
		cmdBack=new Command("����",Command.BACK,1);
		cmdOK=new Command("����",Command.OK,2);
		
		//���ͼ��
		g = getGraphics();
		
		/**��Ϣ��ͼ�㣬��ʾ����ɹ���ʧ����Ϣ**/
		//��������ͼƬ
		setOKImg=midlet.createMenuImage("/setOK.png");
		setLostImg=midlet.createMenuImage("/setLost.png");
		blackboard=midlet.createMenuImage("/blackboard.png");
	}
	
	/**
	* ��������ѡ��
	*/
	public void paintSet()
	{
		//�����߼��û�����
		f=new Form("��Ϸ����");
		TextField playerName=new TextField("������ƣ�",Paopao.playerName,10,TextField.ANY);
		f.append(playerName);
		
		ChoiceGroup backImageIndex=new ChoiceGroup("��ѡ����Ϸ����ͼƬ",Choice.EXCLUSIVE);
		backImageIndex.append("���տ���֮��",null);
		backImageIndex.append("ɽҰС��",null);
		backImageIndex.append("��������˼�",null);
		backImageIndex.append("�ƻ�",null);
		backImageIndex.setSelectedIndex(Paopao.backImageIndex,true);
		f.append(backImageIndex);
		
		ChoiceGroup acousticEffect=new ChoiceGroup("��Ч",Choice.EXCLUSIVE);
		acousticEffect.append("����",null);
		acousticEffect.append("�ر�",null);
		acousticEffect.setSelectedIndex(Paopao.acousticEffect,true);
		f.append(acousticEffect);
		
		ChoiceGroup backgrounMusic=new ChoiceGroup("��������",Choice.EXCLUSIVE);
		backgrounMusic.append("����",null);
		backgrounMusic.append("�ر�",null);
		backgrounMusic.setSelectedIndex(Paopao.backgrounMusic,true);
		f.append(backgrounMusic);
		
		ChoiceGroup netLinkType=new ChoiceGroup("�������ӷ�ʽ",Choice.EXCLUSIVE);
		netLinkType.append("CMWAP",null);
		netLinkType.append("CMNET",null);
		netLinkType.setSelectedIndex(Paopao.netLinkType,true);
		f.append(netLinkType);
		
		ChoiceGroup bluetooth=new ChoiceGroup("����",Choice.EXCLUSIVE);
		bluetooth.append("����",null);
		bluetooth.append("�ر�",null);
		bluetooth.setSelectedIndex(Paopao.bluetooth,true);
		f.append(bluetooth);
		
		ChoiceGroup gameStageInitializtion=new ChoiceGroup("����Ϸʱ���Ƿ����Զ����¹ؿ����������Ϸʱû�йؿ�������µĹؿ�����Ҫ׷�ӽ��ؿ���¼�⣬���������е��벻Ҫ�򿪣�������ʹ�ùؿ��ظ��ģ�",Choice.EXCLUSIVE);
		gameStageInitializtion.append("����",null);
		gameStageInitializtion.append("�ر�",null);
		gameStageInitializtion.setSelectedIndex(Paopao.gameStageInitializtion,true);
		f.append(gameStageInitializtion);
		
		//�ڸ߼��û�������Ӱ���
		f.addCommand(cmdBack);
		f.addCommand(cmdOK);
		
		//�����û�����
		f.setCommandListener(this);
		
		//���߼��û�������ʾ���ֻ���Ļ��
		midlet.setDisplayable(f);
	}
	
	public void commandAction(Command c, Displayable d) 
	{
		if (c==cmdBack)
		{
			//���ز˵�����
			midlet.menu.showMe();
		}else if (c==cmdOK)
		{
			Form tmp=(Form)d;
			Paopao.playerName=((TextField)tmp.get(0)).getString();
			
			switch (((ChoiceGroup)tmp.get(2)).getSelectedIndex())
			{
			case 0:
				Paopao.image_select_back=Paopao.IMAGE_BACK1;
				break;
			case 1:
				Paopao.image_select_back=Paopao.IMAGE_BACK2;
				break;
			case 2:
				Paopao.image_select_back=Paopao.IMAGE_BACK3;
				break;
			case 3:
				Paopao.image_select_back=Paopao.IMAGE_BACK4;
				break;
			}
			
			Paopao.backImageIndex=((ChoiceGroup)tmp.get(2)).getSelectedIndex();
			Paopao.acousticEffect=((ChoiceGroup)tmp.get(2)).getSelectedIndex();
			Paopao.backgrounMusic=((ChoiceGroup)tmp.get(3)).getSelectedIndex();
			Paopao.netLinkType=((ChoiceGroup)tmp.get(4)).getSelectedIndex();
			Paopao.bluetooth=((ChoiceGroup)tmp.get(5)).getSelectedIndex();
			Paopao.gameStageInitializtion=((ChoiceGroup)tmp.get(6)).getSelectedIndex();
			
			//������Ϸ������Ϣ
			RecordStoreManage rsm=new RecordStoreManage();
			if (rsm.saveGameSetInfo(1))
			{
				g.setColor(0x0FFBDBDBD);
				g.fillRect(0,0,getWidth(),getHeight());
				g.drawImage(blackboard,(getWidth()-blackboard.getWidth())/2,(getHeight()-blackboard.getHeight())/2,g.TOP|g.LEFT);
				g.drawImage(setOKImg,(getWidth()-setOKImg.getWidth())/2,(getHeight()-setOKImg.getHeight())/2,g.TOP|g.LEFT);
				midlet.setDisplayable(this);
			}
			else
			{
				g.setColor(0x0FFBDBDBD);
				g.fillRect(0,0,getWidth(),getHeight());
				g.drawImage(blackboard,(getWidth()-blackboard.getWidth())/2,(getHeight()-blackboard.getHeight())/2,g.TOP|g.LEFT);
				g.drawImage(setLostImg,(getWidth()-setLostImg.getWidth())/2,(getHeight()-setLostImg.getHeight())/2,g.TOP|g.LEFT);
				midlet.setDisplayable(this);
			}
		}
	}
	
	/**
	* �����⽡������һ��
	*/
	protected void keyPressed(int KeyCode)
	{
		midlet.setDisplayable(f);
	}
}