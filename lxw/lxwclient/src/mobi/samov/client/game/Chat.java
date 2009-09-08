package mobi.samov.client.game;
import java.util.Hashtable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

import mobi.samov.client.XMIDlet;


public class Chat extends TextBox implements CommandListener{
	
	public static String LeftStr = "确定",RightStr = "返回";
	
	public Command Left;
 
	public Command Right;
	
	public Command comOK;
	
	public XMIDlet MID;
	private Platform p;
	
	public Chat(String arg0, String arg1, int arg2, int arg3,XMIDlet mid,Platform p) {
		super(arg0, arg1, arg2, arg3);
		Left = new Command(LeftStr,Command.OK,0);
		Right = new Command(RightStr,Command.BACK,0);
		this.addCommand(Left);
		this.addCommand(Right);
		this.setCommandListener(this);
		MID = mid;
		this.p = p;
	}
//	public Chat(XMIDlet mid,String str){
//		super(str, "",8, TextField.ANY);
//		comOK = new Command("确定",Command.OK,0);
//		Right = new Command(RightStr,Command.BACK,0);
//		this.addCommand(comOK);
//		this.addCommand(Right);
//		this.setCommandListener(this);
//		MID = mid;
//		Display dis = Display.getDisplay(MID);
//		dis.setCurrent(this);
//	}
	public void ShowChat(){
		Display dis = Display.getDisplay(MID);
		dis.setCurrent(this);
	}
	private String Content;
	
	public String getContent(){
		return Content;
	}
//		public String Form(String str)
//	{
//		return str.replace(HttpConnect.info_spaceGlyph, 'x');
//	}
	public void commandAction(Command cd, Displayable dp) 
	{
		if(cd==Left)
		{
			if(this.size()>0)
			{
			if(dp.getTitle().equals("帐号:1-12个字符"))
				p.user = getString();
			else if(dp.getTitle().equals("修改昵称:1-12个字符"))
				p.tempMyinfo[1] = getString();
			
			else if (dp.getTitle().equals("密码:1-12个字符"))
			{
				p.PassWord = getString();
			}
			else if(dp.getTitle().equals("修改密码:1-12个字符"))
			{
				p.tempMyinfo[2] = getString();
			}
			else if(dp.getTitle().equals("请输入序列号"))
			{
				
			}
			else if(dp.getTitle().equals("请输入密码"))
			{
				
			}
			else if(dp.getTitle().equals("请输入内容"))
			{
				String s = getString();
				Hashtable h = new Hashtable();
				h.put("Type", "SYNC");
				h.put("Cmd", "USE");
				h.put("UID", ""+p.userID);	
				h.put("OID", ""+p.UseGoodsOid);	
				h.put("CONTENT", ""+s);	
				p.SetWait("请稍后");
				p.Connection(h);
				p.Affiche+="我说："+s+"|";
			}
				Display dis = Display.getDisplay(MID);
				dis.setCurrent(MID.currentGame);
				MID.currentGame.setFullScreenMode(true);
			}
		}
		else if(cd==Right)
		{	
			Display dis = Display.getDisplay(MID);
			dis.setCurrent(MID.currentGame);
			MID.currentGame.setFullScreenMode(true);
		}
		
	}
}
