package mobi.samov.client.game;
import java.util.Hashtable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

import mobi.samov.client.XMIDlet;


public class Chat extends TextBox implements CommandListener{
	
	public static String LeftStr = "ȷ��",RightStr = "����";
	
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
//		comOK = new Command("ȷ��",Command.OK,0);
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
			if(dp.getTitle().equals("�ʺ�:1-12���ַ�"))
				p.user = getString();
			else if(dp.getTitle().equals("�޸��ǳ�:1-12���ַ�"))
				p.tempMyinfo[1] = getString();
			
			else if (dp.getTitle().equals("����:1-12���ַ�"))
			{
				p.PassWord = getString();
			}
			else if(dp.getTitle().equals("�޸�����:1-12���ַ�"))
			{
				p.tempMyinfo[2] = getString();
			}
			else if(dp.getTitle().equals("���������к�"))
			{
				
			}
			else if(dp.getTitle().equals("����������"))
			{
				
			}
			else if(dp.getTitle().equals("����������"))
			{
				String s = getString();
				Hashtable h = new Hashtable();
				h.put("Type", "SYNC");
				h.put("Cmd", "USE");
				h.put("UID", ""+p.userID);	
				h.put("OID", ""+p.UseGoodsOid);	
				h.put("CONTENT", ""+s);	
				p.SetWait("���Ժ�");
				p.Connection(h);
				p.Affiche+="��˵��"+s+"|";
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
