import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

/**
* 泡泡龙设置类
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
	* 构造函数
	*/
	public PaopaoSet(Paopao midlet)
	{
		super(false);
		this.midlet=midlet;
		
		//添加操作按键
		cmdBack=new Command("返回",Command.BACK,1);
		cmdOK=new Command("保存",Command.OK,2);
		
		//获得图笔
		g = getGraphics();
		
		/**信息框图层，显示保存成功或失败信息**/
		//创建背景图片
		setOKImg=midlet.createMenuImage("/setOK.png");
		setLostImg=midlet.createMenuImage("/setLost.png");
		blackboard=midlet.createMenuImage("/blackboard.png");
	}
	
	/**
	* 绘制设置选项
	*/
	public void paintSet()
	{
		//创建高级用户界面
		f=new Form("游戏设置");
		TextField playerName=new TextField("玩家名称：",Paopao.playerName,10,TextField.ANY);
		f.append(playerName);
		
		ChoiceGroup backImageIndex=new ChoiceGroup("请选择游戏背景图片",Choice.EXCLUSIVE);
		backImageIndex.append("向日葵的之季",null);
		backImageIndex.append("山野小村",null);
		backImageIndex.append("白云深处有人家",null);
		backImageIndex.append("黄昏",null);
		backImageIndex.setSelectedIndex(Paopao.backImageIndex,true);
		f.append(backImageIndex);
		
		ChoiceGroup acousticEffect=new ChoiceGroup("音效",Choice.EXCLUSIVE);
		acousticEffect.append("启用",null);
		acousticEffect.append("关闭",null);
		acousticEffect.setSelectedIndex(Paopao.acousticEffect,true);
		f.append(acousticEffect);
		
		ChoiceGroup backgrounMusic=new ChoiceGroup("背景音乐",Choice.EXCLUSIVE);
		backgrounMusic.append("启用",null);
		backgrounMusic.append("关闭",null);
		backgrounMusic.setSelectedIndex(Paopao.backgrounMusic,true);
		f.append(backgrounMusic);
		
		ChoiceGroup netLinkType=new ChoiceGroup("网络连接方式",Choice.EXCLUSIVE);
		netLinkType.append("CMWAP",null);
		netLinkType.append("CMNET",null);
		netLinkType.setSelectedIndex(Paopao.netLinkType,true);
		f.append(netLinkType);
		
		ChoiceGroup bluetooth=new ChoiceGroup("蓝牙",Choice.EXCLUSIVE);
		bluetooth.append("启用",null);
		bluetooth.append("关闭",null);
		bluetooth.setSelectedIndex(Paopao.bluetooth,true);
		f.append(bluetooth);
		
		ChoiceGroup gameStageInitializtion=new ChoiceGroup("打开游戏时，是否开启自动更新关卡（如果玩游戏时没有关卡或添加新的关卡资料要追加进关卡记录库，则开启它，有的请不要打开，这样会使用关卡重复的）",Choice.EXCLUSIVE);
		gameStageInitializtion.append("启用",null);
		gameStageInitializtion.append("关闭",null);
		gameStageInitializtion.setSelectedIndex(Paopao.gameStageInitializtion,true);
		f.append(gameStageInitializtion);
		
		//在高级用户界面添加按键
		f.addCommand(cmdBack);
		f.addCommand(cmdOK);
		
		//监听用户按键
		f.setCommandListener(this);
		
		//将高级用户界面显示在手机屏幕上
		midlet.setDisplayable(f);
	}
	
	public void commandAction(Command c, Displayable d) 
	{
		if (c==cmdBack)
		{
			//返回菜单界面
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
			
			//保存游戏配置信息
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
	* 按任意健进入下一屏
	*/
	protected void keyPressed(int KeyCode)
	{
		midlet.setDisplayable(f);
	}
}