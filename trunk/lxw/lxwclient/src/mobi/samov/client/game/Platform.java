package mobi.samov.client.game;

//import java.io.DataInputStream;
import gr.fire.browser.Browser;
import gr.fire.browser.util.Page;
import gr.fire.browser.util.PageListener;
import gr.fire.core.BoxLayout;
import gr.fire.core.CommandListener;
import gr.fire.core.Component;
import gr.fire.core.Container;
import gr.fire.core.FireScreen;
import gr.fire.core.KeyListener;
import gr.fire.core.Panel;
import gr.fire.core.SplashScreen;
import gr.fire.test.Console;
import gr.fire.ui.Alert;
import gr.fire.ui.FireTheme;
import gr.fire.ui.InputComponent;
import gr.fire.ui.SpriteAnimation;
import gr.fire.ui.TextComponent;
import gr.fire.util.Lang;
import gr.fire.util.Log;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStore;

import javax.microedition.lcdui.Font;
import javax.microedition.midlet.MIDletStateChangeException;

//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

//import com.sun.perseus.model.Switch;

import mobi.samov.client.XMIDlet;
import mobi.samov.client.core.Observable;
import mobi.samov.client.core.XGame;
import mobi.samov.client.net.XConnection;

public class Platform extends XGame implements Const, CommandListener, PageListener, KeyListener
{
	//Graphics g = getGraphics();
	private FireScreen fireScreen;
	private Browser b;
	private Console console; 
	private Command consoleCmd = new Command(Lang.get("����̨"),Command.OK,1); 
	private Command menuCmd = new Command(Lang.get("�˵�"),Command.OK,1);
	private Command mainCmd = new Command(Lang.get("��ҳ"),Command.OK,1);
	
	private Command exitCmd = new Command(Lang.get("����"),Command.OK,1);
	private Command closeMenuCmd = new Command(Lang.get("�ر�"),Command.OK,1);
	private Command cancelCmd = new Command(Lang.get("ȡ��"),Command.OK,1);
		
	private Display disp;
	XMIDlet MID;
	private RecordStore rms;
	private MusicPlayer music;
	public Font ft;
	public boolean WAP_NET;
	
	public int FontH ,FontW; 
	private final int Name_PassWordLen = 12;
	private String Version;
	private static String cutLine="";//��ȡ������Ϣ
//	public Hashtable h = new Hashtable();
	public UI ui ;
	private final int SleepTime = 100;
	int SW = 240;
	int SH = 320;
	private Card card;
	private byte SaveState[] = new byte[30],SaveStateIndex;//��¼״̬�ͼ�¼����
	private int delay;//�ӳ�
	private StrCode code = new StrCode();
	private String TempStr;//��ȡ�ַ���ʱ����
	private String TempStr2[][];//��ȡ�ַ���ʱ����
	private int TempIndex,TempIndex2;//����ѡ���Ч������
	
//	-------------------------Key---------------------------//
	static  final byte UP = -1;
	static  final byte DOWN = -2;
	static  final byte LEFT = -3;
	static  final byte RIGHT = -4;	  
	static  final byte Enter = -5;
	static  final byte LeftCom = -6;
	static  final byte RightCom = -7;
	static  final byte Num_0 = 48;
	static  final byte Num_1 = 49;
	static  final byte Num_2 = 50;
	static  final byte Num_3 = 51;
	static  final byte Num_4 = 52;
	static  final byte Num_5 = 53;
	static  final byte Num_6 = 54;
	static  final byte Num_7 = 55;
	static  final byte Num_8 = 56;
	static  final byte Num_9 = 57;
	
	private int KEY;
//	--------------------�˵�����״̬-------------------------------//
	
	private final byte REGISTER  = 0;//ע��
	private final byte LOGIN  = 1;//��½
	private final byte LOOKUP  = 2;//��������
	private final byte MyInfo = 3;//��������
	private final byte LOGO = 7;
	private final byte COVER = 8;
	private final byte MenuOption = 9;//���˵�
	
	private final byte SCENES = 10;//����
	private final byte SET = 11;
	private final byte About = 12;//����
	private final byte Help = 13;//�ͻ�����
	private final byte RommList = 14;//�������
	private final byte DeskHALL = 15;//���Ӵ���
	public  final  byte Deskinfo = 16;//����������Ϣ
	private final byte Game = 17;
	private final byte LOAD = 18;
	private final byte SHOP = 19;//ѡ���Ǹ��̵�
	private final byte SHOP1 = 20;//�̵�2��
	private final byte DEPOT = 25;//�ֿ�
	private final byte PIAZZA = 26;//��Ϸ�㳡
	private final byte ��ֵ = 27;
	private final byte ��ֵ2 = 28;
	
	private final byte ��Ϸ = 29;//���˵�
	
//	byte LastSTATE,LastSTATE2;//��¼��һ״̬  ��¼LastSTATE2 = 2���˵�
	public boolean WAIT ;//�Ƿ��еȴ�
	public boolean CLUE;//�Ƿ�����ʾ
	
	private byte LoadState;
	private byte STATE ;

	//---------------��ͼƬW,H
	public static int W2,H2,W9,H9,WClue,HClue;
	
//--------------------�˵�����.����---------------------------------//
	private int MAXplayer=70; //�������
	/**
	 * ��������
	 */
	private int DeskAmount ;//��������
	private int DeskNum;//��ǰ����
	private int ScenesLength;//������
	private int ScenesTypeLen[];//����������
	private int room ;//�������͸���
	private Image img_LOGO;
	private Image img_Cover;//����
	private Image img_MenuStr,img_MenuStr2;//��������
	public Image img_desk_chair;
//	private Image img_MenuBG;//�˵�����
	private Image img_desk;//����
	private Image img_Ready;//�Ƿ�׼��
	private Image img_Playing;//��Ϸ��
	private Image img_square_icon_bg;//�㳡����
	private Image img_VIP;
//	private Image img_icon;//ѡ������
	 
	private Sprite SP_menuBG;//�˵�����
	private Sprite SP_Arrow;//�˵���ͷ
	private Sprite SP_catch;//��
	private Sprite SP_3;//��
	private Sprite SP_4;//��
	private Sprite SP_44;
	private Sprite SP_badge;//��־
	private Sprite SP_wait;//�ȴ�
	private Sprite SP_Hand;//����ͷ��
	private Sprite SP_body;//����
	private Sprite SP_pos;
	private Sprite SP_piazza,SP_GamePiazza;//�㳡ͼ��
	private NumImgTools NT_deskID;
	
	private int LoadTime;//��ȡʱ��
	
	private int MenuIndex;//�˵�����
	
	private int MenuStrY ;//��񶷵����ַ�Y   
	
	private int DeskLeftX,DeskLeftY,DeskMiddleX,DeskMiddleY,DeskRightX,DeskRightY;//��ֲʱ�����������˵�����
	
	private int LogoTinme;//LOGOʱ��
	
	private int PiazzaIndex;//�㳡����
	
	private int PiazzaLenth;//�㳡�˵�����
	
	private int LoginIndex;//��½��������
	
	private boolean BOOLremember = true;//��ס����
	
	public static int KCTime=1;//������ʱ��
	
	public final int  KCStartTime=30;//���������
	
	private int SetIndex;//��������
	
	private byte SetIndex2[]=new byte[3];
	
//	public String userID="";
//	
//	public String user="";//�û���
//	
//	public String PassWord="";//����
//	
//	public String nickname="";//�ǳ�
	
	public String userID="10000214";
	
	public String user="966167";//�û���
	
	public String PassWord="507720";//����
	
	public String nickname="966167";//�ǳ�]
	
	public String sex="";
	
	private int MusicOpen;
	
	private int HallIndex,HallIndex2;      //��������
	
	private int RommHallIndex;
	
	private int badgeIndex;          //�����Ǹ�������������
	
	private int ThisPlayer[];          //��ǰ����
	
	private int colorIndex;         //��ҫ��ɫ����
	
	private int MyinfoIndex;      //�鿴��������
	
	private int HelepIndex;            //˵������
	
//	private int OptionNum;         //���½�ѡ�����
	
	private int DesKamountIndex;      //�ұ����ӽ�������
	/**
	 * 1ά:����ID 2ά ����λ�� 3ά  0����λ��,1�Ա�,2�Ƿ�׼��,3ͷ��ID
	 */
	private int DeskInfo[][][] ;
	/**
	 * 
 	 * λ��|score|����|sex|ID|nickname|start|handID|VIP
	 */
	public String SitDeskInfo[][] ;
	
	private int DeskInfoIndex ;//����������
	
	public int DeskMyPos;   //�������ҵ�λ��
	/**
	 * 0:user 1:nickname 2,����,3:SEX, 4:score 5:ͷ��ID,6:�Ƿ�VIP,7:VIP����
	 * 8:�Ƿ��з�����,9������Сʱ��,,10:�Ƿ�����Ʒ������,11:��Ʒ������,
	 */
	public String Myinfo[],tempMyinfo[];
	
	public 
	String WaitStr="";       //�ȴ�ʱ��ʾ�ַ�
	
	public String ClueStr="",ClueLeft="",ClueRight="";           //��ʾ�ַ�
	
	private boolean Ready;        //�Ƿ�׼��
	
//	private int ClueFrame=1;
	
	private int SCENENum;              //������
	
	public int SceneID=1;//      ������ 
	public int RoomID;//        ��������
	public int DeskID;//         ��������
	
	private boolean Option;//�Ƿ���ʾ���½�ѡ��
	
	public String OptionStr[];//���½�ѡ���ַ�
	
	private int OptionIndex;//ѡ������
	//--------------------------------�̳����----------------------------///
//	private String GoodsName[];//��������
	private Image img_Shop,img_Shop1,img_Shop2,img_Shop3,img_Shop4;//�̳�
	/**
	 * �̵������
	 */
	private Sprite SP_ShopIntro;
	private Image Shop_Goods[];
	private Image Column;//��Ʒ��������
	private Sprite SP_ShopName;//�̳�����
	private boolean SEE;  //�򿪲鿴��Ʒ
	private Image img_select;//����ѡ��
	private boolean OpenBuy; //�Ƿ���ʾ�������
	private int BOOL_Buy;//����ȷ��
	String GoodsNum = "";//������߱��
	private String ��ֵ2ͷ����;
	/**
	 * 0=��� 1=�� 2=���� 3=ͼƬID  4=��Ǯ  5=�Ƿ����̵��е���
	 */
	private String GoodsInfo[][];//���߽���
	/**
	 * �̵�˵�����
	 */
	private int ShopMenuIndex;
	
	/**
	 * �̵�1����
	 */
	private int ShopIndexLR,ShopIndexUD;//1==��������...2 ==��������
	
	private int DepotIndexLR,DepotIndexUD;//�ֿ�������������
	
	private Image img_Depot_Goods[];//�ֿ���ƷͼƬ
	/**
	 * //�ֿ���Ʒ����0:��Ʒ���,1��Ʒ����,2��Ʒ����,,3��ƷOID,4��Ʒ����
	 * 5�״�ʹ��ʱ��|6��Ч����
	 */
	private String DepotNum[][];
	
	private int DepotCount;//�ֿ�����
	
	private int UDMaxDepotIndex;//�ֿ������Ʒ����
	
	private int DepotPage;//�ֿ���Ʒ��ҳ
	
	private int MaxDrawIndex;
	
	private int UDMaxShopIndex;//�̵������Ʒ����
	
	private int ��ֵLR,��ֵUD,��ֵ2UD,��ֵѡ���;//��ֵ�������Һ���������
	
	private boolean OpenParMenu;//�Ƿ����ֵ����
	
//	private int ParMenuIndex;//��ֵѡ��Ŀ
//	
//	private int ParMenuLenth;//��ֵ����
	
	private String Par[];//��ֵ
	public String Affiche="";//����
	private String ThisAffiche="";
	private int AfficheX;//
	private String UseGoodsIndex;
	public String UseGoodsOid;
	private int Time_EnterDesk;//���ܽ���̨��ʱ��
	private int KICK_DeskID;

	
//------------------------��Ϸ�߼�-----------------------------------//
	
	public Platform(XMIDlet midlet) 
	{
		super(midlet);
		MID = midlet;
		disp = Display.getDisplay(MID);
		Version = MID.getAppProperty("MIDlet-Version");
		ft=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		FontH = ft.getHeight();
		FontW = ft.stringWidth("һ");
		readRms();
		ui = new UI(ft,SW,SH);
		try {
		//	img_LOGO = Image.createImage("/LOGO.png");
			img_Cover = Image.createImage("/cover.png");
			Image img = Image.createImage("/waiting.png");
			SP_wait = new Sprite(img,img.getWidth(),img.getHeight()/4);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
//		card = new Card(SW,SH,this);
//		STATE = LOAD;
//		LoadState = Game;
//		KCStartTime = 40;
//		if(SetIndex2[0]==0)//����
//			KCStartTime = 30;
		STATE = COVER;
	//	LoadState = PIAZZA;
		LoadState = MenuOption;
		Init();		
		WAP_NET = true;
	}

	private void Init()
	{
		Option = false;
		OpenBuy = false;
		switch (SH) 
		{
			case 160:
				
				break;
				
			case 208:
			case 220:
				if(SW == 176)
				{
					W2 = 148;
					H2 = 20;
					W9 = 175;
					H9 = 26;
					WClue = 148;
					HClue = 26;
				}
				else
				{
					W2 = 208;
					H2 = 20;
					W9 = 175;
					H9 = 26;
					WClue = 175;
					HClue = 26;
				}
				MenuStrY = 80;
				DeskLeftX = 6;
				DeskLeftY = 18;
				DeskMiddleX = 29;
				DeskMiddleY = 11;
				DeskRightX = 55;
				DeskRightY = 18;
				break;
	
			case 320:
				W2 = 240;
				H2 = 26;
				W9 = 210;
				H9 = 26;
				WClue = 220;
				HClue = 26;
				MenuStrY = 150;
				DeskLeftX = 11;
				DeskLeftY = 24;
				DeskMiddleX = 42;
				DeskMiddleY = 15;
				DeskRightX = 72;
				DeskRightY = 24;
				break;
		}
	}
	private void LoadRes(int type)
	{
		try {
		Image img = null;
		switch (type) 
		{
			case MenuOption://���˵���Դ
			case DeskHALL:
			case SCENES:
			case RommList:
			case PIAZZA:
				switch (LoadTime) 
				{
					case 0://�ͷ��̵���Դ
						if(img_Shop!=null)
						{
							FreeRes(SHOP);
						}
						
						break;
					case 1: 
						img_MenuStr = Image.createImage("/menustr.png");
						img_MenuStr2 = Image.createImage("/menustr2.png");
						img_Ready = Image.createImage("/ready.png");
						img_Playing = Image.createImage("/playing.png");
						img_square_icon_bg = Image.createImage("/square_icon_bg.png");
						break;
	
					case 2:   
						Column = Image.createImage("/column.png");
						img_desk_chair = Image.createImage("/desk_chair.png");
						img = Image.createImage("/jiandou.png");
						SP_Arrow = new Sprite(img,img.getWidth()/2,img.getHeight());
						img = Image.createImage("/menuBG.png");
						SP_menuBG = new Sprite(img,img.getWidth()/2,img.getHeight());
						break;
						
					case 3:
						img = Image.createImage("/catch.png");
						SP_catch = new Sprite(img,img.getWidth(),img.getHeight()/2);
						img = Image.createImage("/3.png");
						SP_3 = new Sprite(img,img.getWidth(),img.getHeight()/2);
						img = Image.createImage("/4.png");
						SP_4 = new Sprite(img,img.getWidth(),img.getHeight()/2);
						img = Image.createImage("/badge.png");
						SP_badge = new Sprite(img,img.getWidth(),img.getHeight()/7);
						img = Image.createImage("/44.png");
						SP_44 = new Sprite(img,img.getWidth(),img.getHeight()/2);
						img = Image.createImage("/number_halll.png");
						NT_deskID = new NumImgTools(img,img.getWidth(),img.getHeight()/13);
						img = Image.createImage("/body.png");
						SP_body = new Sprite(img,img.getWidth(),img.getHeight()/6);
						img = Image.createImage("/hand.png");
						SP_Hand = new Sprite(img,img.getWidth(),img.getHeight()/5);
						img = Image.createImage("/position.png");
						SP_pos = new Sprite(img,img.getWidth(),img.getHeight()/3);
						img = Image.createImage("/square_icon.png");
						SP_piazza = new Sprite(img,img.getWidth(),img.getHeight()/12);
						img = Image.createImage("/square_icon2.png");
						SP_GamePiazza = new Sprite(img,img.getWidth(),img.getHeight()/12);
						break;
						
					case 4:
						SwitchState(LoadState,true);
						break;
				}
				LoadTime++;
				break;
	
			case Game://��ȡ��Ϸ����
				card.LoadGameRes(LoadTime);

				if(LoadTime==10)
				{
					SwitchState(Game,true);
					img = Image.createImage("/badge.png");
					SP_badge = new Sprite(img,img.getWidth(),img.getHeight()/7);
					img = Image.createImage("/3.png");
					SP_3 = new Sprite(img,img.getWidth(),img.getHeight()/2);
				}
				LoadTime++;
				break;
				
			case SHOP:
				switch (LoadTime) 
				{
					case 0:
						FreeRes(MenuOption);
						break;
		
					case 1:
						img_select = Image.createImage("/select.png");
						img_Shop = Image.createImage("/shop.png");
						img_Shop1 = Image.createImage("/Shop_1.png");
						img_Shop2 = Image.createImage("/Shop_2.png");
						if(SP_ShopIntro==null)
						{
						img = Image.createImage("/shopoption.png");
						SP_ShopIntro = new Sprite(img,img.getWidth(),img.getHeight()/2);
						}

						img = Image.createImage("/shop_name.png");
						SP_ShopName = new Sprite(img,img.getWidth(),img.getHeight()/4);
						break;
						
					case 2:
						Shop_Goods = new Image[Const.GoodsNum];
						for (int i = 0; i < Shop_Goods.length; i++) 
						{
							Shop_Goods[i] = Image.createImage("/ITEM_"+i+".png"); 
						}
						if(GoodsInfo==null)//��ȡ�̵���Ϣ
						{
							code.LoadStrings2(0, code.FILE_NAME_STRING2,6);
							GoodsInfo = code.s_commonStrings2;
							code.FreeStr();
						}
						break;
						
					case 3:
						OptionIndex = 0;
						SwitchState(SHOP,true);
						break;
				}
				LoadTime++;
				break;
				
			case DEPOT://��ȡ�ֿ���Ϣ
				switch (LoadTime) 
				{
					case 0:
						if(SP_ShopIntro == null)
						{
							img = Image.createImage("/shopoption.png");
							SP_ShopIntro = new Sprite(img,img.getWidth(),img.getHeight()/2);
						}
						if(GoodsInfo==null)
						{
							code.LoadStrings2(0,code.FILE_NAME_STRING2,6);//��ȡ���߽��ܺ���
							GoodsInfo = code.s_commonStrings2;
							code.FreeStr();
						}
						break;
	
					case 1:
						int index = 0;
						img_Depot_Goods = null;
						img_Depot_Goods = new Image[GoodsInfo.length];//���ݲֿ����Ǹ���Ʒ
						for (int i = 0; i < GoodsInfo.length; i++){
							for (int j = 0; j < DepotCount; j++) {
								if(GoodsInfo[i][0].equals(DepotNum[j][0]))   //�����Ҳֿ����Ʒ������ͼƬ
								{
									index = Integer.parseInt(GoodsInfo[i][3]);
									DepotNum[j][1] = GoodsInfo[i][1];
									DepotNum[j][2] = GoodsInfo[i][2];
									if(img_Depot_Goods[index]==null)
									{
										img_Depot_Goods[index] = Image.createImage("/ITEM_"+GoodsInfo[i][3]+".png");
									}
								}
							}
						}
						break;
						
					case 5://��ȡ����Դ..����ֿ�
						WAIT = false;
						SwitchState(DEPOT,false);
						break;
				}
				LoadTime++;
				break;
		}
		} catch (IOException e) {
			System.out.println("LOAD ERR"+LoadTime);
		}
	}
	public void SetClue(String str,String left,String right)
	{
		CLUE = true;
		WAIT = false;
		ClueStr = str;
		ClueRight = right;
		ClueLeft = left;
		
	}
	public void SetWait(String str)
	{
		WAIT = true;
		WaitStr = str;
		repaint();
	}
	private void FreeRes(int type)
	{
		switch (type)
		{
			case LOGO:
				img_Cover = null;
				img_LOGO = null;
				break;
			case MenuOption:
				
	//			img_MenuBG = null;
	//			img_2 = null;
			//	img_Ready = null;
	//			img_desk_chair = null;
				
				SP_catch = null;
				SP_3 = null;
				SP_4 = null;
				SP_44 = null;
		//		NT_deskID = null;
		//		SP_body = null;
		//		SP_Hand = null;
				break;
				
			case Game:
				card = null;
				break;
				
			case SHOP:
				img_Shop = null;
				img_Shop1 = null;
				img_Shop2 = null;
				img_Shop3 = null;
				img_Shop4 = null;
				img_select = null;
	//			Goods_Games = null;
				Shop_Goods = null;
	//			Goods_Assistant = null;
				SP_ShopIntro = null;
				GoodsInfo = null;
				SP_ShopName = null;
				Column = null;
				
				System.out.println("�ͷ��̵���Դ");
			//	GoodsIntro = null;
				break;
				
			case DEPOT:
				img_Depot_Goods = null;
				DepotCount = 0;
				DepotNum = null;
				img_Shop3 = null;
				img_Shop4 = null;
				GoodsInfo = null;
				SP_ShopIntro = null;
				System.out.println("�ͷŲֿ���Դ");
				break;
				
			case PIAZZA:
				SP_piazza = null;
				img_square_icon_bg = null;
				img_MenuStr = null;
				img_MenuStr2 = null;
				SP_Arrow = null;
				break;
		}
	}
	private void DrawLOGO(Graphics g)
	{
		g.setColor(255,255,255);
		g.fillRect(0, 0, SW, SH);
		g.drawImage(img_LOGO, (SW-img_LOGO.getWidth())/2, (SH-img_LOGO.getHeight())/2, 0);
		LogoTinme++;
		if(LogoTinme>20)
		{
			STATE = COVER;
			img_LOGO = null;
		}
	}
	private void DrawLOOKUP(Graphics g)
	{
		DrawBG(g);
		Draw2(g, 0, 0);
		g.setColor(0,0,0);
		drawBeautyString(g, GetPossW, (SW-ft.stringWidth(GetPossW))/2, (H2-FontH)/2, 0xffffff,0x000000);
	//	g.drawString("�һ�����", (SW-ft.stringWidth("�һ�����"))/2, (H2-FontH)/2, 0);
		DrawL_R(g, GetPossW, Return2);
	}
	private void DrawLoading(Graphics g)
	{
		g.setColor(0,0,0);
		g.fillRect(0, 0, SW, SH);
		LoadRes(LoadState);
		DrawWaiting(g,"��ȡ��Դ");
	}
	private void DrawLOGIN(Graphics g)
	{
		int W = ft.stringWidth("��ס�ʺź�����")+80;
		int H = 3*FontH+SH/5;;
		int rectW = 110;//ѡ���Ŀ�
		int rectH = FontH+5;
		int x,y;
		DrawBG(g);
		g.drawImage(img_MenuStr2, (SW-img_MenuStr2.getWidth())/2, 0, 0);
		x = (SW-W)/2+10;
		y = img_MenuStr2.getHeight()+1+10;
		g.setColor(255,255,255);
		g.fillRoundRect((SW-W)/2,y-6, W, H, 5, 5);
		g.setColor(255,168,188);
		g.drawRoundRect((SW-W)/2+1,y-5, W-3, H-3, 5, 5);
		

		g.setColor(0,0,0);
		g.drawString("�ʺ�", x, y, 0);
		g.setColor(251,114,152);
		g.drawRect(x+g.getFont().stringWidth("�ʺ�")+10, y, rectW, rectH);
		if(LoginIndex==0)
		{
			g.fillRect(x+g.getFont().stringWidth("�ʺ�")+10+2, y+2, rectW-3, rectH-3);
			g.setColor(255,255,255);
		}
		else
			g.setColor(255,168,188);
		
		
		g.drawString(user, x+g.getFont().stringWidth("�ʺ�")+13, y+2, 0);

		y+=35;
		g.setColor(0,0,0);
		g.drawString("����", x, y, 0);
		g.setColor(251,114,152);
		g.drawRect(x+g.getFont().stringWidth("����")+10, y, rectW, rectH);
		
		if(LoginIndex==1)
		{
			
			g.fillRect(x+g.getFont().stringWidth("�ʺ�")+10+2, y+2, rectW-3, rectH-3);
			g.setColor(255,255,255);
		}
		else
			g.setColor(255,168,188);
		
		g.drawString(PassWord, x+g.getFont().stringWidth("�ʺ�")+13, y+2, 0);
		
		y+=30;
		
		if(LoginIndex==2)
		{
			g.setColor(251,114,152);
			g.fillRect(x+g.getFont().stringWidth("�ʺ�")+10, y+1, 
					g.getFont().stringWidth("��ס�ʺź�����")+1+SP_catch.getWidth()+5, g.getFont().getHeight());
			g.setColor(255,255,255);
			SP_catch.setFrame(1);
		}
		else
		{
			g.setColor(255,168,188);
			SP_catch.setFrame(0);
		}
		
		g.drawRect(x+g.getFont().stringWidth("����")+11, y+2, SP_catch.getWidth()+3, SP_catch.getHeight()+3);
		SP_catch.setPosition(x+g.getFont().stringWidth("����")+12, y+4);
		if(BOOLremember)
			SP_catch.paint(g);
		g.drawString("��ס�ʺź�����", x+g.getFont().stringWidth("����")+10+SP_catch.getWidth()+5, y, 0);
		if(Option)
			DrawL_R(g,"ѡ��","ȡ��");
		else
			DrawL_R(g,Const.Option,"��½");
	}
	private void DrawMenuCover(Graphics g)
	{
	//	g.drawImage(img_Cover, 0, 0, 0);
		drawBeautyString(g, "�����������", (SW-ft.stringWidth("�����������"))/2, SH/5*4,0xffffff,0x000000);
	}
	private void DrawBG(Graphics g)
	{
		for (int i = 0; i < SH/SP_menuBG.getHeight()+1; i++) 
		{
			for (int j = 0; j < SW/SP_menuBG.getWidth(); j++) {
				SP_menuBG.setPosition(j*SP_menuBG.getWidth(), i*SP_menuBG.getHeight());
				SP_menuBG.setFrame((i+j)%2);
				SP_menuBG.paint(g);
			}
		}
	}
	public void Draw2(Graphics g,int x,int y)
	{
		g.setColor(0xfff4d84);
		g.drawRect(x, y, W2, H2);
		g.setColor(0xfffedfd);
		g.drawLine(x+2, y+1, x+W2-4, y+1);
		g.setColor(0xfffb0e0);
		g.drawLine(x+1, y+2, x+W2-2, y+2);
		g.setColor(0xfff86bd);
		g.fillRect(x+1, y+3, W2-2, H2-3);
//		g.setClip(0, 0, W2, H2);
		g.setColor(0xfff4d84);
		g.fillRect(x+1, y+H2/3*2, W2-2, y+H2-(y+H2/3*2)-1 );
	//	g.setClip(0, 0, SW, SH);
		g.setColor(0xfff86bd);
		g.fillRect(x+1, y+H2-1, W2-2, 1);
	}
	public void DrawAffiche(Graphics g,String str)
	{
		Draw2(g, 0, 0);
		if(!Affiche.equals("") || (!ThisAffiche.equals("")&&AfficheX+ft.stringWidth(ThisAffiche)>0))
		{
			drawBeautyString(g, ThisAffiche,AfficheX, (H2-ft.getHeight())/2, 0xffffff,0x000000);
			AfficheX-=5;
			if(AfficheX+ft.stringWidth(ThisAffiche)<0)
			{
				if(!Affiche.equals(""))
					AfficheX = SW;
				if(Affiche.length()>0)
				{
					ThisAffiche = cutAffiche();
				}
			}
		}
		else
		{
			drawBeautyString(g, str, (SW-ft.stringWidth(str))/2, (H2-ft.getHeight())/2, 0xffffff,0x000000);
		}
		
	}
	public void Draw9(Graphics g,int x,int y)//��ͼƬ9
	{
		g.setColor(0xfff75ac);
		g.drawRect(x, y, W9, H9);
		g.setColor(0xfffddee);
		g.fillRect(x+1, y+1, W9-1, H9-1);
		g.setColor(255,255,255);
	}
	/**
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param b  �Ƿ�ѡ��
	 */
	private void Draw10(Graphics g,int x,int y,boolean b)
	{
		
	}
	private void DrawShopOption(Graphics g)
	{
		
	}
	/**
	 * ���㳡
	 */
	private void DrawPiazza(Graphics g)
	{
		DrawBG(g);
		Draw2(g, 0, 0);
		drawBeautyString(g, "UU�㳡", (SW-ft.stringWidth("��Ϸ�㳡"))/2, (H2-ft.getHeight())/2, 0xffffff,0x000000);
		DrawAffiche(g,"UU�㳡");
		String str[] = {"�����ȵ�","��������","����Ϣ","��Ϸ","����Ⱥ","�������","άȨͶ��","������ѵ","ҽ�Ʊ���"};
		PiazzaLenth = str.length;
		int spaceX = SW/3;//ÿ�����
		int spaceY = (SH-H2*2)/3;//ÿ�����
		int spaceW = (spaceX - SP_piazza.getWidth())/2;//����
		int spaceH = (spaceY - SP_piazza.getHeight())/2;//����
		g.drawImage(img_square_icon_bg, (PiazzaIndex%3)*spaceX+(spaceX-img_square_icon_bg.getHeight())/2-1, H2+(PiazzaIndex/3)*spaceY+2, 0);
		g.setColor(255,255,255);
		for (int i = 0; i < PiazzaLenth; i++) 
		{
			SP_piazza.setFrame(i);
			SP_piazza.setPosition((i%3)*spaceX+spaceW, H2 + (i/3)*spaceY+3);
			SP_piazza.paint(g);
			g.drawString(str[i],(i%3)*spaceX+spaceW +(SP_piazza.getWidth()-ft.stringWidth(str[i]))/2, H2+ (i/3)*spaceY+SP_piazza.getHeight()+4, 0);
		}
		
		DrawL_R(g,Const.Option,Return2);
	}
	private void DrawGamePiazza(Graphics g)
	{
		DrawBG(g);
		Draw2(g, 0, 0);
		drawBeautyString(g, "��Ϸ�㳡", (SW-ft.stringWidth("��Ϸ�㳡"))/2, (H2-ft.getHeight())/2, 0xffffff,0x000000);
		DrawAffiche(g,"��Ϸ�㳡");
		String str[] = {"������","����","������","���а�","����","������","�̳�","�ֿ�","����"};
		PiazzaLenth = str.length;
		int spaceX = SW/3;//ÿ�����
		int spaceY = (SH-H2*2)/3;//ÿ�����
		int spaceW = (spaceX - SP_GamePiazza.getWidth())/2;//����
		int spaceH = (spaceY - SP_GamePiazza.getHeight())/2;//����
		g.drawImage(img_square_icon_bg, (PiazzaIndex%3)*spaceX+(spaceX-img_square_icon_bg.getHeight())/2-1, H2+(PiazzaIndex/3)*spaceY+2, 0);
		g.setColor(255,255,255);
		for (int i = 0; i < PiazzaLenth; i++) 
		{
			SP_GamePiazza.setFrame(i);
			SP_GamePiazza.setPosition((i%3)*spaceX+spaceW, H2 + (i/3)*spaceY+3);
			SP_GamePiazza.paint(g);
			g.drawString(str[i],(i%3)*spaceX+spaceW +(SP_GamePiazza.getWidth()-ft.stringWidth(str[i]))/2, H2+ (i/3)*spaceY+SP_GamePiazza.getHeight()+4, 0);
		}
		
		DrawL_R(g,Const.Option,Return2);
	}
	private void Draw��ֵ(Graphics g)
	{
		int Num = 5;
		String str[] = {"���ٳ�ֵ","��ֵ��¼","��ֵ˵��"};
		DrawBG(g);
		int x = 0;
		int y = 0;
		int RectH = 0;//���ܿ��
		int img1H = 0;//��ͷͼƬ��
		if(img_Shop1 == null)
		{
			
		}
		else
		{
			
			img1H = img_Shop1.getHeight();
			//������ѡ���
			for (int i = 0; i <str.length ; i++)
			{
				if(��ֵLR==i)
				{
					g.drawImage(img_Shop2, x, 0, 0);
					
					drawBeautyString(g, str[i], x+(img_Shop2.getWidth()-ft.stringWidth(str[i]))/2,
							(img_Shop2.getHeight()-FontH)/2, 0xffd2964,0xffffff);
					x+=img_Shop2.getWidth();
				}
				else
				{
					g.drawImage(img_Shop1, x, 0, 0);
					
					drawBeautyString(g, str[i], x+(img_Shop1.getWidth()-ft.stringWidth(str[i]))/2,
							(img_Shop1.getHeight()-FontH)/2, 0xffd2964,0xffffff);
					x+=img_Shop1.getWidth();
				}
			}
			switch (��ֵLR) 
			{
			case 0:
				//��������
				RectH+=SH-H2-(SP_44.getHeight()+SP_44.getHeight()/7)*5-img1H;
				g.setColor(255,255,255);
				g.fillRect(1, img1H, SW-3, RectH);
				g.setColor(0xf8306b);
				g.drawRect(1, img1H, SW-3, RectH);
				g.setColor(0xffb4ca);
				g.drawRect(3, img1H+2, SW-7, RectH-4);
				ui.DrawStr(g, TempStr2[��ֵUD][1], 0, img_Shop1.getHeight()+2, SW, RectH, SW-FontH*2, FontH, 0, false); 
				//����ֵ��Ǯ

				
				for (int i = 0; i < Num; i++) 
				{
					y = 3+RectH+img1H+i*(SP_44.getHeight()+SP_44.getHeight()/7);
					if(TempIndex == i)
						SP_44.setFrame(0);
					else
						SP_44.setFrame(1);
					SP_44.setPosition((SW-SP_44.getWidth())/2, y);
					SP_44.paint(g);
					g.drawString(TempStr2[i+TempIndex2][0], (SW-SP_44.getWidth())/2+5, y+(SP_44.getHeight()-FontH)/2, 0);
					g.drawString(TempStr2[i+TempIndex2][2], (SW-SP_44.getWidth())/2+SP_44.getWidth()-ft.stringWidth(TempStr2[i+TempIndex2][2])-10-SP_badge.getWidth()-5, y+(SP_44.getHeight()-FontH)/2, 0);
					if(!TempStr2[i+TempIndex2][2].equals(""))
					{
					SP_badge.setFrame(0);
					SP_badge.setPosition((SW-SP_44.getWidth())/2+SP_44.getWidth()-SP_badge.getWidth()-10, y+(SP_44.getHeight()-SP_badge.getHeight())/2);
					SP_badge.paint(g);
					}
					
				}
				ui.DrawRoll(g,img1H+RectH+2,SH-(img1H+RectH)-H2,TempStr2.length-Num+1,TempIndex2,SW,SH);
				break;
			case 1:
				break;
			default:
				break;
			}
		}
		DrawL_R(g,Const.Option,Return2);
	}
	private void Draw��ֵ2(Graphics g)
	{
		//String str[] = {"��ֵ�ʺţ�","��ֵ��ң�","��ֵ��"};
		String temp[]=null;
		if(��ֵUD == 2)
		{
			String str[] = {"��ֵ�ʺţ�","��ֵ��ң�","��ֵ��","���кţ�","���룺"};
			temp = str;
		}
		else{
			String str[] = {"��ֵ�ʺţ�","��ֵ��ң�","��ֵ��"};
			temp = str;
		}
		
		DrawBG(g);
		Draw2(g, 0, 0);
		drawBeautyString(g, ��ֵ2ͷ����, (SW-ft.stringWidth(��ֵ2ͷ����))/2, 5,0xffd2964,0xffffff);
		
		��ֵѡ��� = temp.length-3;
		boolean bool = false;
		for (int i = 0; i < temp.length; i++) 
		{
			g.setColor(0,0,0);
			g.drawString(temp[i], 5, H2+i*(FontH+5)+5, 0);
			bool = false;
			switch (i)
			{
				case 0:
					g.drawString(user, 5+ft.stringWidth(temp[i]), H2+i*(FontH+5)+5, 0);
					break;
				case 1:
					g.drawString(Par[ui.ParMenuIndex], 5+ft.stringWidth(temp[i]), H2+i*(FontH+5)+5, 0);
					break;
//				case 2:
//					ui.drawRect(g,Par,5+ft.stringWidth(temp[i]),H2+i*(FontH+5)+5,OpenParMenu,FontH,ui.ParMenuIndex,bool);
//					break;
				case 3:
					if(��ֵ2UD == 1)
						bool = true;
					ui.DrawRect(g, 5+ft.stringWidth(temp[i]), H2+i*(FontH+5)+5, ft.stringWidth("123456789012345"), FontH, -1, -1, bool);
					break;
				case 4:
					if(��ֵ2UD == 2)
						bool = true;
					ui.DrawRect(g, 5+ft.stringWidth(temp[3]), H2+i*(FontH+5)+5, ft.stringWidth("123456789012345"), FontH, -1, -1, bool);
					break;
			}
		}
		if(��ֵ2UD == 0)
			bool = true;
		else
			bool = false;
		ui.drawRect(g,Par,5+ft.stringWidth(temp[2]),H2+2*(FontH+5)+5,OpenParMenu,FontH,ui.ParMenuIndex,bool);
		DrawL_R(g,"ȷ��",Return2);
	}


	/**
	 * ���˵�
	 * @param g
	 */
	private void DrawMenuOption(Graphics g)
	{
		int arrW = img_MenuStr.getWidth()+SP_Arrow.getWidth()+SW/10;//��ͷ�Ŀ�
		int arrX = (SW-arrW)/2;
		DrawBG(g);
		g.drawImage(img_MenuStr, (SW-img_MenuStr.getWidth())/2, MenuStrY, 0);
	//	g.drawImage(img_MenuStr2, (SW-img_MenuStr2.getWidth())/2, 0, 0);
		
		for (int i = 0; i < 2; i++) 
		{
			SP_Arrow.setFrame(i);
			SP_Arrow.setPosition(arrX+(i*arrW-SP_Arrow.getWidth()/2), MenuIndex*(img_MenuStr.getHeight()/7)+MenuStrY);
			SP_Arrow.paint(g);
		}
		drawBeautyString(g, "V"+Version, SW-ft.stringWidth("V"+Version)-2, SH-H2-FontH,0xffffff,0x000000);
		DrawL_R(g,"ѡ��","�˳�");
	}

	private void DrawScene(Graphics g)
	{
		DrawBG(g);
		String str[] = {"������","��Ϸ˵��"};		
		String str2[][] = {{"�����ͨ��","1.5��"},{"������ͨ��","1��"}};
		int x=0;
		
		for (int i = 0; i < 2; i++) 
		{
			if(HallIndex==0)
			{
				SP_3.setPosition(i*SP_3.getWidth(), 0);
				if(i==0)
					x = (SP_3.getWidth()-ft.stringWidth(str[i]))/2;
				else
					x = SP_3.getWidth()+(SW-SP_3.getWidth()-ft.stringWidth(str[i]))/2;
				SP_3.setFrame(i);
			}
			else
			{
				if(i==0)
				{
					x = (SW-SP_3.getWidth()-ft.stringWidth(str[i]))/2;
					SP_3.setPosition(0, 0);
				}
				else
				{
					x = SW-SP_3.getWidth()+(SP_3.getWidth()-ft.stringWidth(str[i]))/2;
					SP_3.setPosition(SW-SP_3.getWidth(), 0);
				}
				SP_3.setFrame(1-i);
			}
			
			SP_3.paint(g);
			drawBeautyString(g,str[i],x,(SP_3.getHeight()-ft.getHeight())/2,0xffd2964,0xffffff);
		}
		
		switch (HallIndex) 
		{
			case 0:
				x = (SW-SP_4.getWidth())/2;
				int y=0;
				if(ScenesTypeLen!=null)
				for (int i = 0; i < ScenesLength; i++) 
				{
					y = SP_3.getHeight()+10+i*(SP_4.getHeight()+10);
					if(HallIndex2==i)
					{
						SP_4.setFrame(0);
						badgeIndex = ScenesTypeLen[i];
					}
					else
						SP_4.setFrame(1);
					SP_4.setPosition(x,y );
					SP_4.paint(g);
					SP_badge.setPosition(x+5, y+5);
					SP_badge.setFrame(ScenesTypeLen[i]);
					SP_badge.paint(g);
					g.drawString(str2[SP_badge.getFrame()][0], x+SP_badge.getWidth()+10, y+5, 0);
					g.drawString(str2[SP_badge.getFrame()][1], x+5, y+SP_badge.getHeight()+5, 0);
				}
				if(!Option)
					DrawL_R(g,Const.Option,Return2);
				else
					DrawL_R(g,"ȷ��","ȡ��");
				break;
	
			default:
				DrawSpecification(g);
				DrawL_R(g,"","����");
				break;
		}
		
	}
	private void DrawRoomList(Graphics g)//�������б�
	{
		DrawBG(g);
		String str[] = {"�����б�","��Ϸ˵��"};		
		String str2[] = {"��ҷ���","���ַ���"};
		
		int x=0;
	//	if()
		for (int i = 0; i < 2; i++) 
		{
			if(HallIndex==0)
			{
				
				SP_3.setPosition(i*SP_3.getWidth(), 0);
				if(i==0)
					x = (SP_3.getWidth()-ft.stringWidth(str[i]))/2;
				else
					x = SP_3.getWidth()+(SW-SP_3.getWidth()-ft.stringWidth(str[i]))/2;
				SP_3.setFrame(i);
			}
			else
			{
				if(i==0)
				{
					x = (SW-SP_3.getWidth()-ft.stringWidth(str[i]))/2;
					SP_3.setPosition(0, 0);
				}
				else
				{
					x = SW-SP_3.getWidth()+(SP_3.getWidth()-ft.stringWidth(str[i]))/2;
					SP_3.setPosition(SW-SP_3.getWidth(), 0);
				}
				SP_3.setFrame(1-i);
			}
			
			SP_3.paint(g);
			drawBeautyString(g,str[i],x,(SP_3.getHeight()-ft.getHeight())/2,0xffd2964,0xffffff);
		}
		
		switch (HallIndex) 
		{
			case 0:
				x = (SW-SP_44.getWidth())/2;
				int y=0;
				for (int i = 0; i < room; i++) 
				{
					y = SP_3.getHeight()+10+i*(SP_44.getHeight()+10);
					if(RommHallIndex==i)
						SP_44.setFrame(0);
					else
						SP_44.setFrame(1);
					SP_44.setPosition(x,y );
					SP_44.paint(g);
					SP_badge.setPosition(x+5, y+5);
					SP_badge.setFrame(3);
					SP_badge.paint(g);
					g.drawString(str2[1],x+SP_badge.getWidth()+5, y+(SP_44.getHeight()-ft.getHeight())/2, 0);
					g.drawString("��"+""+ThisPlayer[i]+"/"+MAXplayer+"��",
							x+SP_44.getWidth()-ft.stringWidth(("��"+""+ThisPlayer[i]+"/"+MAXplayer+"��")), 
										y+(SP_44.getHeight()-ft.getHeight())/2, 0);
					
				}
				break;
	
			default:
				DrawSpecification(g);
				DrawL_R(g,"",Return2);
				break;
		}
		if(!Option)
			DrawL_R(g,Const.Option,Return2);
		else
			DrawL_R(g,"ȷ��","ȡ��");
	}
	private void DrawDeskHall(Graphics g)//�����Ӵ���
	{
		int[] color = {0xfffcff00,0xf00ff18,0xfff6182};
		DrawBG(g);
		Draw2(g, 0, 0);
		int deskW = (SW)/2;//ÿ�����ӿ�
		int deskH = (SH-H2-H2)/3;//ÿ�����Ӹ�
		int ImgW = img_desk_chair.getWidth();
		int ImgH = img_desk_chair.getHeight();
		
		g.setColor(color[(colorIndex++)%color.length]);
		g.drawRect((HallIndex%2)*SW/2+1, H2+(HallIndex/2)*deskH, deskW-8, deskH);
		int num = DesKamountIndex*2;
		int x=0,x2=0,y=0;
		int λ��=0;
		boolean playing = false;//�Ƿ�������Ϸ
		int deskX=0;//��������
		int deskY=0;
		for (int i = 0; i < 3; i++) //��ǰ��6��
		{
			for (int j = 0; j < 2; j++) 
			{
				deskX = j*deskW+(deskW-ImgW)/2-2;
				deskY = i*deskH+H2+(deskH-ImgH)/2+10;
				g.drawImage(img_desk_chair, deskX,deskY, 0);
				NT_deskID.drawNums(g, num+1, deskX, i*deskH+H2+2, 1);
				playing = false;
				for (int k = 0; k <3; k++) 
				{
					
					if(DeskInfo!=null && DeskInfo[num][k][0]!=-1)//����
					{
						x = deskX;
						y = deskY;
						if(SW==240)
							switch (DeskInfo[num][k][0]) 
							{
								case 0:  
									x += DeskLeftX;  y += DeskLeftY;
									x2= x - 10;
									λ�� = 0;
									break;
								
								case 1: 
									x += DeskMiddleX; 
									y += DeskMiddleY;
									x2= x - (SP_Hand.getWidth()-SP_body.getWidth())/2 ;
									λ�� = 1;
									break;
								
								case 2: 
									x +=DeskRightX; x2= x - 3 ;y += DeskRightY;
									λ�� = 2;
									break;
							}
						else
							switch (DeskInfo[num][k][0]) 
							{
								case 0:  
									x += 6; y+=18;
						//			x2= x - (SP_Hand.getWidth()-SP_body.getWidth())/2-4;
									x2= x - 6;
									λ�� = 0;
									break;
								
								case 1: 
									x += 29; 
									x2= x - (SP_Hand.getWidth()-SP_body.getWidth())/2 ;
									λ�� = 1;
									y += 11;
									break;
								
								case 2: 
									x += 55; x2= x - 4 ;y+=18;
									λ�� = 2;
									break;
							}
							SP_body.setFrame(λ��+DeskInfo[num][k][1]*3);
							SP_Hand.setFrame(DeskInfo[num][k][3]);
							
							SP_body.setPosition(x, y);
							SP_Hand.setPosition(x2, y-SP_Hand.getHeight());
							SP_Hand.paint(g);
							SP_body.paint(g);
							int ready = 0;
//							for (int index = 0; index < 3; index++) 
//							{
//								if()
//							}
							//�ж������������׼������ʾ��Ϸ��
							if(DeskInfo[num][k][2] == 1 && DeskInfo[num][(k+1)%3][2] == 1 && DeskInfo[num][(k+2)%3][2]==1)
							{
								playing = true;
							}
							if(DeskInfo[num][k][2] == 1 && !playing)
							{
								g.drawImage(img_Ready, x2+SP_Hand.getWidth()-img_Ready.getWidth(), y-SP_Hand.getHeight(), 0);
							}
					}
					if(playing)//���Ƿ���Ϸ��
					{
						if(SW==240)
							g.drawImage(img_Playing, deskX+img_Playing.getWidth(),deskY+15, 0);
						else
							g.drawImage(img_Playing, deskX+23,deskY+16, 0);
					}
				}
				num++;
			}
		
		}
		//--------------��������-----------
		ui.DrawRoll(g,H2+1,SH-H2-H2,DeskAmount/2-2,DesKamountIndex,SW,SH);
		DrawL_R(g,"ѡ��","����");;
		g.drawString("��ǰ����: "+DeskNum,( SW-ft.stringWidth("��ǰ����: "+DeskNum))/2, SH-ft.getHeight()-3, 0);
		String str="";
		switch (SceneID) 
		{
			case 1:
				str = "��ͨ���ֳ�";
				break;
	
			case 2:
				str = "��ҳ�";
				break;
		}
		DrawAffiche(g, str);
//		g.drawString(str,( SW-ft.stringWidth(str))/2, (H2-ft.getHeight())/2, 0);
	}
	public void DrawDeskInfo(Graphics g)//������������Ϣ
	{
		int RectX=0;
		int RectY=0;
		int λ��=0;
		int[] color = {0xfffcff00,0xf00ff18,0xfff6182};
		String strInfo[] = {"�ȼ�:","����:","����","�Ա�:","�û���:","�ǳ�:"};
		String StrName[] = {"��ƶ","�̹�","����","�軧","ƶũ","��ũ","����","�ʱ���","���ڼ�","���м�","�׸�"};
		int index=0;
		DrawBG(g);
		int c[] = ceateTranslucenceRect(SW,strInfo.length*(SH/2/strInfo.length)+10,0xf160007,(byte) 60);
	//	int c[] = ceateTranslucenceRect(SW,SH/2,0xf160007,(byte) 60);
		g.drawRGB(c,0,SW,0,0,SW,strInfo.length*(SH/2/strInfo.length)+10,true);
		int num = DeskID-(SceneID*1000+RoomID*100);
		int x = 0,y = 0,x2 = 0;
	//	g.drawRGB(c,0,SW,0,0,SW,SH/2,true);
		g.setColor(255,255,255);
		for (int i = 0; i < strInfo.length; i++)//������
		{
			g.drawString(strInfo[i], 20, 10+i*(SH/2/strInfo.length), 0);
//			g.drawLine(0, 10+i*(SH/2/strInfo.length)+ft.getHeight()+3, SW, 10+i*(SH/2/strInfo.length)+ft.getHeight()+3);
			
			if(!SitDeskInfo[DeskInfoIndex][0].equals("-1") && i>0)//��������²Ż�����
			{
				
				int tempNum = Integer.parseInt(SitDeskInfo[DeskInfoIndex][1]);
				if(tempNum<0)         index = 0;
				else if(tempNum<300)   index = 1;
				else if(tempNum<700)   index = 2;
				else if(tempNum<1500)   index = 3;
				else if(tempNum<3100)   index = 4;
				else if(tempNum<6300)    index = 5;
				else if(tempNum<12700)   index = 6;
				else if(tempNum<25000)   index = 7;
				else if(tempNum<51000)    index = 8;
				else if(tempNum<102300)   index = 9;
				else if(tempNum<204700)   index = 10;
				
				g.drawString(StrName[index], 80, 10, 0);
				if(i!=3)
				{
					g.drawString(""+SitDeskInfo[DeskInfoIndex][i], 80, 10+i*(SH/2/strInfo.length), 0);
				}
				else 
				{
					String sex = "Ů";
					if(SitDeskInfo[DeskInfoIndex][3].equals("0"))
						sex = "��";
					g.drawString(sex, 80, 10+i*(SH/2/strInfo.length), 0);
				}
			}
		}
		int deskY = SH-H2-img_desk_chair.getHeight()-5;
		int deskX = (SW-img_desk_chair.getWidth())/2;
		g.drawImage(img_desk_chair, deskX, deskY, 0);
		NT_deskID.drawNums(g, num, deskX-20,deskY-10, 1);

		for (int k = 0; k <3; k++) 
		{					
			if(SitDeskInfo!=null && !SitDeskInfo[k][0].equals("-1"))//����
			{
				x = deskX;
				y = deskY;
				if(SW == 240)
					switch (Integer.parseInt(SitDeskInfo[k][0])) //�ж���λ
					{
						case 0:  
							x += DeskLeftX; y+=DeskLeftY;
				//			x2= x - (SP_Hand.getWidth()-SP_body.getWidth())/2-3;
							x2= x - 10;
							λ�� = 0;
							break;
						
						case 1: 
							x += DeskMiddleX; 
							x2= x - (SP_Hand.getWidth()-SP_body.getWidth())/2 ;
							y +=DeskMiddleY; 
							λ�� = 1;
							break;
						
						case 2: 
							x += DeskRightX; x2= x - 3 ;y+=DeskRightY;
							λ�� = 2;
							break;
					}
				else if(SW == 176)
					switch (Integer.parseInt(SitDeskInfo[k][0])) //�ж���λ
					{
						case 0:  
							x += DeskLeftX; y+=DeskLeftY;
				//			x2= x - (SP_Hand.getWidth()-SP_body.getWidth())/2-4;
							x2= x - 6;
							λ�� = 0;
							break;
						
						case 1: 
							x += DeskMiddleX; 
							x2= x - (SP_Hand.getWidth()-SP_body.getWidth())/2 ;
							y += DeskMiddleY;
							λ�� = 1;
							break;
						
						case 2: 
							x += DeskRightX; x2= x - 3 ;y+=DeskRightY;
							λ�� = 2;
							break;
					}				
				    SP_body.setFrame(λ��+Integer.parseInt(SitDeskInfo[k][3])*3);
					SP_Hand.setFrame(Integer.parseInt(SitDeskInfo[k][7]));
				
					SP_body.setPosition(x, y);
					SP_Hand.setPosition(x2, y-SP_Hand.getHeight());
					SP_Hand.paint(g);
					SP_body.paint(g);
					SP_pos.setPosition(x-5, y-SP_Hand.getHeight()-SP_Hand.getWidth());
					if(DeskMyPos==k)//������
						SP_pos.setFrame(0);
					else if((DeskMyPos+1)%3 == k)
						SP_pos.setFrame(2);
					else//�¼�
						SP_pos.setFrame(1);
					SP_pos.paint(g);
					if(Integer.parseInt(SitDeskInfo[k][6]) == 1 && STATE != Game)
					g.drawImage(img_Ready, x2+SP_Hand.getWidth()-img_Ready.getWidth(), y-SP_Hand.getHeight(), 0);
			}
		}
		g.setColor(color[(colorIndex++)%color.length]);
		if(SW==240)//��P
		{
		RectX = (SW-img_desk_chair.getWidth())/2+((SP_Hand.getWidth())*DeskInfoIndex);
		RectY = deskY+img_desk_chair.getHeight()-SP_Hand.getHeight()-SP_body.getHeight()-8;
		if(DeskInfoIndex==1)
			RectY -=17;
		g.drawRect(RectX, RectY, SP_Hand.getWidth()+1, SP_Hand.getHeight()+SP_body.getHeight()+3);
		}
		else//��P 
		{
			RectX = (SW-img_desk_chair.getWidth())/2+((SP_Hand.getWidth())*DeskInfoIndex)-1;
			RectY = deskY+img_desk_chair.getHeight()-SP_Hand.getHeight()-SP_body.getHeight()-5;
			if(DeskInfoIndex==1)
			{
				RectY -=13;
			}
			g.drawRect(RectX, RectY, SP_Hand.getWidth()+1, SP_Hand.getHeight()+SP_body.getHeight()+5);
		}
		String str = "��ʼ��Ϸ",str2 =  "ѡ��";
		
		if(Ready)
		{
			str = "ȡ����ʼ";
			str2 = "";
		}
		if(STATE==Game)//����Ϸ����鿴����
		{
			str = Return2;
			str2 = "";
			Option = false;
		}
		DrawL_R(g, str2, str);
	}
	private void DrawL_R(Graphics g,String Left,String Right)
	{
	//	g.drawImage(img_2, 0, SH-img_2.getHeight(), 0);
		Draw2(g, 0, SH-H2);
		int y = SH-H2+(H2-ft.getHeight())/2;
		if(Right!=null)
		drawBeautyString(g,Right,SW-ft.stringWidth(Right)-10,y,0xffd2964,0xffffff);
		if(Left!=null)
		drawBeautyString(g,Left,10,y,0xffd2964,0xffffff);
	}
	/**
	 ��������
	 * @param g
	 */
	int Time=0;
	private void DrawMyinfo(Graphics g)
	{
		DrawBG(g);
		String strInfo[] = {"ͷ��: ","�û���:","�ǳ�:","����","�Ա�:","����:"};
		
		int Y = SH/22;
		drawBeautyString(g, "��������", (SW-ft.stringWidth("��������"))/2, Y,0xffd2964,0xffffff);
//		int c[] = ceateTranslucenceRect(SW,SH-img_2.getHeight(),0xf160007,(byte) 60);
//		g.drawRGB(c,0,SW,0,0,SW,SH-img_2.getHeight(),true);
		Y+=30;
		if(MyinfoIndex==0)
		{
			DrawArr(g,90-10,Y+SP_Hand.getHeight()/2,SP_Hand.getWidth()+20,3);
			g.setColor(0xff0dd5b);
		}
		else
			g.setColor(255,255,255);
		g.fillRect(90-2, Y-2, SP_Hand.getWidth()+4, SP_Hand.getHeight()+4);
		SP_Hand.setPosition(90, Y);
		SP_Hand.setFrame(Integer.parseInt(tempMyinfo[5]));
		SP_Hand.paint(g);
		if(Myinfo[6].equals("1"))
			g.drawImage(img_VIP, 90+SP_Hand.getWidth()+SP_Hand.getWidth()/2, Y, 0);
		
		int y = SH/2/strInfo.length+5;
		int w = 0;
		for (int i = 0; i < strInfo.length; i++)//������
		{
			g.setColor(255,255,255);
			g.drawString(strInfo[i], 20, Y+i*y, 0);
			if(i==0)
			{
				Y+=SP_Hand.getHeight()/2;
			}
		}
		Y+=y;
		for (int i = 0; i < tempMyinfo.length-2; i++) 
		{
			if(i==2 || i==1)//���޸Ŀ�
			{
				g.setColor(255,255,255);
				g.fillRect(85, Y+i*y-5, SW-20-86, ft.getHeight()+5);
				g.setColor(0xffe406b);
				g.drawRect(85, Y+i*y-5, SW-20-86, ft.getHeight()+5);
				if(i==MyinfoIndex)
				{
					g.setColor(0xfffa8bc);
					g.fillRect(85+2, Y+i*y-5+2, SW-20-86-3, ft.getHeight()+5-3);
					if(Time%5==0)
					{
						g.setColor(255,255,255);
						g.drawString("|", 85+ft.stringWidth(tempMyinfo[i])+7, Y+i*y-1, 0);
						Time=1;
					}
					Time++;
				}
				
			}
			if(i==3)//���Ա�
			{
				String sex ="��";
				if(tempMyinfo[i].equals("1"))
						sex ="Ů";
				g.setColor(0,0,0);
				g.drawString(sex, 90, Y+i*y, 0);
				if(MyinfoIndex==3)
				{
					DrawArr(g, 90-ft.stringWidth("Ů")/2, Y+i*y+FontH/2,ft.stringWidth("ŮŮ"), 3);
				}
			}
			else
			{
				if(i==MyinfoIndex &&( i==1||i==2))
					g.setColor(255,255,255);
				else
					g.setColor(0,0,0);
				g.drawString(tempMyinfo[i], 90, Y+i*y, 0);
			}
		}
	//	drawBeautyString(g, "����ӱ���", 85, y, 0xffd2964,0xffffff);
		DrawL_R(g,"����",Return2);
	}
	private void DrawSet(Graphics g)
	{
		DrawBG(g);
		String str[] = {"���硡��","��������","�𶯡���"};
		String str2[][] = {{"����","��ͨ"},{"��","�ر�"},{"��","�ر�"}};
		int W = SW-SW/4;
		int H = str.length*g.getFont().getHeight()+40;
		int x=0,y=0;
	//	DrawBG(g);
		if(img_MenuStr2!=null)
		{
			g.drawImage(img_MenuStr2, (SW-img_MenuStr2.getWidth())/2, 0, 0);
			y = img_MenuStr2.getHeight()+10;
		}
		else
		{
			y = (SH-H)/2;
		}
		x = (SW-ft.stringWidth(str[0]+str2[0][0]+"һһ"))/2;
		g.setColor(255,255,255);
		g.fillRoundRect((SW-W)/2,y, W, H, 0, 0);
		g.setColor(255,168,188);
		g.drawRoundRect((SW-W)/2+1,y+1, W-3, H-3, 0, 0);
		y+=5;
		
		for (int i = 0; i < str.length; i++) 
		{
			g.setColor(255,168,188);
			if(SetIndex==i)
			{
				g.setColor(255,168,188);
				g.fillRect((SW-W)/2+1, y+i*25, W-3, ft.getHeight());
				g.setColor(255,255,255);
				DrawArr(g,ft.stringWidth("���硡��"+"һ")+x,y+i*25+ft.getHeight()/2,
						ft.stringWidth(str2[i][SetIndex2[i]]+"һһ"),3);
			}
			g.drawString(str[i], x, y+i*25, 0);
			g.drawString(str2[i][SetIndex2[i]], ft.stringWidth("���硡��"+"һһ")+x, y+i*25, 0);		
		}
		DrawL_R(g,"����",Return2);
	
	}

	private void DrawWaiting(Graphics g,String str)
	{			
		int c[] = ceateTranslucenceRect(SW,SH,0xf000000,(byte) 60);
		g.drawRGB(c,0,SW,0,0,SW,SH,true);
		
		Draw9(g, (SW-W9)/2, SW*2/3);
		SP_wait.setPosition((SW-W9)/2+10, SW*2/3+5);
		SP_wait.nextFrame();
		SP_wait.paint(g);
		g.setColor(0,0,0);
		g.drawString(str, (SW-W9)/2 + (W9 - ft.stringWidth(str))/2, 
				SW*2/3+(H9-ft.getHeight())/2, 0);
		if(str.equals("���ڼ�����Ϸ��Դ"))
		{
			LoadRes(DEPOT);
		}
	}
	public  int[] ceateTranslucenceRect(int w,int h,int color,byte limit){
		   int data[] = new int[w*h];
		   int newcolor = (color &= 0x00ffffff)|(limit*2<<24);
		  // System.out.println("new color"+Integer.toHexString(newcolor));
		   for (int i = 0; i < data.length; i++) {
			data[i] = newcolor;
		   }
		   return data;
	}
	private void DrawAbout(Graphics g)
	{
//		String str[] = {"��Ȩ����:","����ΰҵ���������Ƽ����޹�˾","�ͷ��绰:","010-59002556","�ͷ�����:",
//				"haihaiwangwang@gmail.com","�ٷ���վ:","wap.juu.cn"};
//		String str = code.GetString(0);
		DrawBG(g);
		Draw2(g, 0, SH-H2);
		drawBeautyString(g, "��������", (SW-g.getFont().stringWidth("��������"))/2, 5,0xffd2964,0xffffff);
		int index=0 ; 
		int colcr[] = {0x000000,0x000986};
		int Cindex=0;
		ui.DrawStr(g,TempStr,5,H2,SW,H2,SW,SH/10,0,false);
//		for (int i = 0; i < str.length; i++) 
//		{
//			g.setColor(colcr[i%2]);	
//			g.drawString(str[i], 5, SH/10+i*(SH/10), 0);
//		}
		
	//	g.drawImage(img_2, 0, SH-img_2.getHeight(), 0);
		DrawL_R(g, "", Return2);
}
	private void DrawHelp(Graphics g)
	{
	//	String str[] = {"���οͷ��绰����:","010-59002556","�ͷ���ϯ����ʱ��:","��һ����������9:30-����18:00",};
//		String str = code.GetString(1);
		DrawBG(g);
		drawBeautyString(g, "��  ��", (SW-g.getFont().stringWidth("��  ��"))/2, 5,0xffd2964,0xffffff);
		int colcr[] = {0x000000,0x000986};
			
//		for (int i = 0; i < str.length; i++) 
//		{
//			g.setColor(colcr[i%2]);	
//			g.drawString(str[i], 5, SH/10+i*(SH/10), 0);
//		}
		ui.DrawStr(g,TempStr,5,H2,SW,H2,SW,SH/10,0,false);
		Draw2(g, 0, SH-H2);
	//	g.drawImage(img_2, 0, SH-img_2.getHeight(), 0);
		drawBeautyString(g,Return2,SW-g.getFont().stringWidth("��½")-10,SH-g.getFont().getHeight()-2,0xffd2964,0xffffff);
	}
	private void DrawArr(Graphics g,int x,int y,int w,int w2)//����ͷ
	{
		g.setColor(0,0,0);
		g.drawLine(x, y, x+w2, y-w2);
		g.drawLine(x, y, x+w2, y+w2);
		g.drawLine(x+w, y, x+w-w2, y-w2);
		g.drawLine(x+w, y, x+w-w2, y+w2);
	}
	protected void paint(Graphics g) 
	{
		g.setFont(ft);
		
		switch (STATE) 
		{
			case LOGIN:DrawLOGIN(g);break;
			
			case LOGO: DrawLOGO(g);break;
			
			case LOOKUP:DrawLOOKUP(g);break;
			
			case MyInfo: DrawMyinfo(g);break;
			
			case COVER: DrawMenuCover(g); break;
				
			case MenuOption: DrawMenuOption(g); break;
			
			case SCENES: DrawScene(g); break;
			
			case SET: DrawSet(g); break;
			
			case About: DrawAbout(g); break;
			
			case Help:DrawHelp(g); break;
			
			case RommList:DrawRoomList(g);break;
			
			case DeskHALL:DrawDeskHall(g);break;
			
			case Deskinfo:DrawDeskInfo(g);break;
			
			case Game:DrawGame(g);break;
			
			case LOAD:DrawLoading(g);break;
			
			case SHOP:DrawShop(g);break;
			
			case SHOP1:DrawShop1(g);break;
			
			case DEPOT:DrawDepot(g);break;
			
			case PIAZZA:DrawPiazza(g);break;
			
			case ��ֵ:Draw��ֵ(g);break;
			
			case ��ֵ2:Draw��ֵ2(g);break;
			
			case ��Ϸ:DrawGamePiazza(g);break;
		}
		if(CLUE)
		{
			DrawClue(g,ClueStr,ClueLeft,ClueRight);
		}
		if(WAIT)
		{
			DrawWaiting(g, WaitStr);
		}
		if(Option)
		{
			ui.DrawOption(g,OptionIndex,OptionStr);
		}
		if(Time_EnterDesk>0)
			Time_EnterDesk--;
	}
	private void drawclue(Graphics g,int x,int y)
	{
		g.setColor(0xfff719c);
		g.drawRect(x, y, WClue, HClue);
		g.setColor(0xfffaac3);
		g.fillRect(x+1, y+1, WClue-1, HClue-1);
		g.setColor(0xfff719c);
		g.fillRect(x+1, y+HClue/2, WClue-1, HClue/2-1);
	}
	public void DrawClue(Graphics g,String str,String Left,String right)
	{		
		int c[] = ceateTranslucenceRect(SW,SH,0xf000000,(byte) 60);
		g.drawRGB(c,0,SW,0,0,SW,SH,true);
		int y = SH/10;
		int x = (SW-WClue)/2;
		drawclue(g,x,y);
		SP_badge.setFrame(6);
		SP_badge.setPosition(x+5, y+(HClue-SP_badge.getHeight())/2);
		SP_badge.paint(g);
		g.setColor(255,255,255);
		g.drawString("��ʾ", x+SP_badge.getWidth()+10, y+(HClue-SP_badge.getHeight())/2+3, 0);		
		y+=HClue;
		ui.DrawStr(g, str, x, y,WClue,HClue, WClue-15, FontH,0,true);
		y+=FontH*ui.row;
		drawclue(g,x,y);
		drawBeautyString(g,right, x+WClue-ft.stringWidth(right)-5, y+(HClue-ft.getHeight())/2,0xffd2964,0xffffff);
		drawBeautyString(g,Left, x+10, y+(HClue-ft.getHeight())/2,0xffd2964,0xffffff);
		
	}
	private void DrawSpecification(Graphics g)
	{		
		g.setClip(0, SP_3.getHeight(), SW, SH-SP_3.getHeight()-H2);
		ui.DrawStr(g,TempStr,15,5,WClue,SH-H2*2,SW-40,HClue,HelepIndex,false);
		g.setClip(0, SP_3.getHeight(), SW, SH);
		ui.DrawRoll(g, SP_3.getHeight(), SH-H2-SP_3.getHeight(), ui.RollLen-9, HelepIndex*-1/30, SW, SH);
	}
	/**
	 * ���̵�
	 */
	private void DrawShop(Graphics g)
	{
		int imgW = img_Shop.getWidth();
		int imgH = img_Shop.getHeight();
		int[] color = {0xfffcff00,0xf00ff18,0xfff6182};
		int space = imgH/5;//���
		DrawBG(g);
		Draw2(g, 0, 0);
		g.setColor(255,255,255);
		g.drawString("�̳�", (SW-ft.stringWidth("�̳�"))/2, (H2-ft.getHeight())/2, 0);
	//	String ShopStr[] = {"�����̵�","BB���̵�","ͷ���̵�","�����̵�"};
		//���̵�
		int NameIndex = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
			
				g.drawImage(img_Shop, j*SW/2+(SW/2-imgW)/2, H2+space+i*(space+imgH), 0);
				SP_ShopName.setFrame(NameIndex);
				SP_ShopName.setPosition(j*SW/2+(SW/2-imgW)/2, H2+space+i*(space+imgH)+SP_ShopName.getHeight());
				SP_ShopName.paint(g);
				NameIndex++;
//				if(SW==240)
//					drawBeautyString(g,ShopStr[i+j], j*SW/2+(SW/2-imgW)/2+img_Shop.getWidth()-ft.stringWidth(ShopStr[i])-4, 
//									22+H2+space+i*(space+imgH), 0xffd2964,0xffffff);
					
			}
		}
		//����
		g.setColor(color[(colorIndex++)%color.length]);
		g.drawRect(((ShopMenuIndex%2)*SW/2)+((SW/2-imgW)/2)-imgW/9, 
						(ShopMenuIndex/2*(space+imgH))+H2+space-imgH/9, imgW+imgW/9*2, imgH+imgH/9*2);
		
		if(Option)//�����½�ѡ��
		{
			DrawL_R(g,"ѡ��","ȡ��");
		}
		else 
			DrawL_R(g,Const.Option,Return2);
	}
	private void DrawShop1(Graphics g)//��������̵��ڲ�(�̶�����)
	{
		DrawBG(g);
		String Goodstype[] = null;//��������
		
		Image Seeimg = null;
		switch (ShopMenuIndex) 
		{
			case 0://�����̵�
				String type2[] = {"�������","��Ϸ����","��������"};
				Goodstype = type2;				
				break;
	
			case 1:
				String type3[] = {"�����̵�","XX�̵�","YY�̵�"};
				Goodstype = type3;
				break;
				
			case 2:
				String type4[] = {"�����̵�","XX�̵�","YY�̵�"};
				Goodstype = type4;
				break;
//			default:
//				String type4[] = {"�������","��Ϸ����","��������"};
//				Goodstype = type4;	
//				break;
		}
		int x = 0;
		//����ǰѡ�������
		if(Goodstype!=null)
		for (int i = 0; i <Goodstype.length ; i++)
		{
			if(ShopIndexLR==i)
			{
				g.drawImage(img_Shop2, x, 0, 0);
				
				drawBeautyString(g, Goodstype[i], x+(img_Shop2.getWidth()-ft.stringWidth(Goodstype[i]))/2,
						(img_Shop2.getHeight()-FontH)/2, 0xffd2964,0xffffff);
				x+=img_Shop2.getWidth();
			}
			else
			{
				g.drawImage(img_Shop1, x, 0, 0);
				
				drawBeautyString(g, Goodstype[i], x+(img_Shop1.getWidth()-ft.stringWidth(Goodstype[i]))/2,
						(img_Shop1.getHeight()-FontH)/2, 0xffd2964,0xffffff);
				x+=img_Shop1.getWidth();
			}
		}
		//������
		int w = SW - SP_ShopIntro.getWidth()-2;
		SP_badge.setFrame(0);
		int y = 0;
		UDMaxShopIndex = 0;
		GoodsNum = "-1";
		if(GoodsInfo!=null)
		for (int i = 0; i < GoodsInfo.length; i++) //������,��Ǯ 
		{
			if(GoodsInfo[i][0].substring(0, 1).equals(""+ShopMenuIndex)
					&& GoodsInfo[i][0].substring(1, 2).equals(""+ShopIndexLR)
					&& GoodsInfo[i][5].equals("1"))
			{
				if(ShopIndexUD == UDMaxShopIndex)
				{
					GoodsNum = GoodsInfo[i][0];//�õ���ѡ����Ʒ�ı��
					SP_ShopIntro.setFrame(0);
					Seeimg = Shop_Goods[Integer.parseInt(GoodsInfo[i][3])];
				}
				else
					SP_ShopIntro.setFrame(1);
				
				SP_ShopIntro.setPosition(0, 
						img_Shop2.getHeight()/5+img_Shop2.getHeight()+ y+(Shop_Goods[Integer.parseInt(GoodsInfo[i][3])].getHeight()-SP_ShopIntro.getHeight()));
				SP_ShopIntro.paint(g);
				
	//			if(i<=GoodImgId.length)
	//			{
					g.drawImage(Shop_Goods[Integer.parseInt(GoodsInfo[i][3])], 2, 3+img_Shop2.getHeight()+y, 0);
					
	//			}
				
				SP_badge.setPosition(SW-SP_badge.getWidth()-w-1, 
						img_Shop2.getHeight()+y+3+
							(SP_ShopIntro.getHeight() - SP_badge.getHeight())/2);
				
				SP_badge.paint(g);
//				
//				
				int PriceRim=0;//��Ǯ��
				switch (SW) 
				{
					case 240:
						PriceRim = 53;
						break;
				}
				g.setColor(0,0,0);
				g.drawString(GoodsInfo[i][1], 2+Shop_Goods[Integer.parseInt(GoodsInfo[i][3])].getWidth()+5, //����������
						img_Shop2.getHeight()/5+img_Shop2.getHeight()+ y + (SP_ShopIntro.getHeight() - FontH)/2, 0);
	//			
				g.drawString(GoodsInfo[i][4], //�����߼�Ǯ
						SW - w - (SW-SP_ShopIntro.getWidth()) - SP_badge.getWidth() - PriceRim +(PriceRim-ft.stringWidth(""+GoodsInfo[i][4]))/2, 
						img_Shop2.getHeight()/5+img_Shop2.getHeight()+ y +(Shop_Goods[Integer.parseInt(GoodsInfo[i][3])].getHeight()-SP_ShopIntro.getHeight())+
							(SP_ShopIntro.getHeight() - FontH)/2, 0);

				y+=Shop_Goods[Integer.parseInt(GoodsInfo[i][3])].getHeight()+3;
				UDMaxShopIndex++;
			}
		}
		//��������
		g.setColor(255,255,255);
		g.fillRect(SP_ShopIntro.getWidth()+2, img_Shop2.getHeight(),w, SH);
		g.setColor(0xff719c);
		g.fillRect(SP_ShopIntro.getWidth()+3, img_Shop2.getHeight(), w-2, SH);
		if(!SEE)
			DrawL_R(g, "�鿴", Return2);
		else
		{
			DrawSEE(g,Seeimg,GoodsInfo,GoodsNum);
			DrawL_R(g, "����", Return2);
		}
		if(OpenBuy)
		{
			DrawBuy(g,GoodsInfo[ShopIndexUD][1]);
		}
	}
	private void DrawBuy(Graphics g,String str)
	{
		int c[] = ceateTranslucenceRect(SW,SH,0xf160007,(byte) 60);
		g.drawRGB(c,0,SW,0,0,SW,SH,true);
		
		int RectW = SW-SW/10;//��Ŀ�
		int RectH = FontH+FontH/2;//��ĸ�
		int RectX = (SW-RectW)/2;//���X
		int RectY = (SH-RectH)/2;//���Y
		g.setColor(0xfffddee);
		g.fillRect(RectX, RectY, RectW, RectH);
		g.setColor(0xfff75ac);
		g.drawRect(RectX, RectY, RectW, RectH);
		
		int y = RectY + (RectH-FontH)/2;
		g.setColor(255,0,0);
		g.drawString(str, (SW-ft.stringWidth(str))/2,y,0);
		g.setColor(0,0,0);
		g.drawString("��", RectX+5, y, 0);
		g.drawString("��", RectX+RectW-ft.stringWidth("һ")-5, y, 0);
		g.drawImage(img_select, RectX+BOOL_Buy*(RectW-img_select.getWidth()), RectY-img_select.getHeight()-2, 0);
		drawBeautyString(g, "ȷ�Ϲ���",( SW-ft.stringWidth("ȷ�Ϲ���"))/2, RectY-FontH-3,0xffd2964,0xffffff);
	}
	/**
	 * 
	 * @param g
	 * @param img    ��ƷͼƬ
	 * @param Info   ��Ʒ��Ϣ
	 * @param ID   �� Ҫ��ʾ����Ʒ˵��
	 */
	private void DrawSEE(Graphics g,Image img,String Info[][],String ID)
	{
		int Index= 0;
		for (int i = 0; i < Info.length; i++) 
		{
			if(ID.equals(Info[i][0]))//�ҳ���ӦID��Ʒ����
			{
				Index = i;
				break;
			}
		}
		g.setColor(255,255,255);
		int H = 0;//���ܿ�Ĵ�С
		switch (SW) 
		{
			case 176:
				break;
				
			case 240: H = 120;break;
		}
		g.fillRect(0, SH-H2-H, SW, H);
		g.setColor(0xff44578);
		g.drawRect(0, SH-H2-H, SW-1, H-1);
		g.drawRect(2, SH-H2-H+2, SW-1-4, H-1-4);
		g.setColor(0,0,0);
		g.drawString(Info[Index][1]+"��", 15, SH-H2-H+FontH/2, 0);//��������
//		g.drawString(intro, 15, SH-H2-H+FontH/2+FontH, 0);//���߽���
		ui.DrawStr(g,Info[Index][2],0,SH-H2-H+FontH/2+FontH,SW,H,SW-FontW*3,FontH,0,false);
	//	g.drawString("�۸� "+Info[Index][4]+" ���", SW/5*3, (SH-H2-H)+H/5*4, 0);
		g.drawImage(Column, 10, SH-H2-H-Column.getHeight(), 0);//����Ʒ������
	//	int ImgID = Integer.parseInt(Info[Index][3]);
		g.drawImage(img, 10+(Column.getWidth()-img.getWidth())/2, 
				SH-H2-H-Column.getHeight()+ (Column.getHeight()-img.getHeight())/2, 0);
	}
	private void DrawDepot(Graphics g)
	{
		DrawBG(g);
		int x=0;
		int w = 10;
		Image SeeImg = null;
	//	String GoodsNum ="";
		String str[] = {"����","UU��","����","ͷ��"};
		for (int i = 0; i < str.length; i++)//�����������ֺͿ�
		{
			if(DepotIndexLR==i)
			{
				g.drawImage(img_Shop3, x, 0, 0);
				drawBeautyString(g, str[i], x+(img_Shop3.getWidth()-ft.stringWidth(str[i]))/2,
						(img_Shop3.getHeight()-FontH)/2, 0xffd2964,0xffffff);
				x+=img_Shop3.getWidth();
			}
			else
			{
				g.drawImage(img_Shop4, x, 0, 0);
				drawBeautyString(g, str[i], x+(img_Shop4.getWidth()-ft.stringWidth(str[i]))/2,
						(img_Shop4.getHeight()-FontH)/2, 0xffd2964,0xffffff);
				x+=img_Shop4.getWidth();
			}
		}
		int PriceRim=0;//������
		int PriceRimX=0;
		switch (SW) 
		{
			case 240:
				PriceRim = 53;
				PriceRimX = 155;
				MaxDrawIndex = 7;
				break;

			case 176:
				
				break;
		}
		//������
		UDMaxDepotIndex = 0;
		if(img_Depot_Goods!=null && WAIT == false)
		for (int i = 0; i < DepotNum.length-DepotPage; i++) //������.����.���� -DepotPage��Ϊ�˲��÷�ҳ������Խ�糬��
		{
//			if(DepotNum[i][0].substring(0,1).equals(""+ShopMenuIndex)
//					&& DepotNum[i][0].substring(1,2).equals(""+DepotIndexLR))//���߷���
//			{	
			int IMGID=0;
			//�õ���ӦIMG ID
			for (int j = 0; j < GoodsInfo.length; j++) {
				if(DepotNum[i+DepotPage][0].equals(GoodsInfo[j][0]))
				{
					IMGID = Integer.parseInt(GoodsInfo[j][3]);
					break;
				}
			}
			if(img_Depot_Goods[IMGID]!=null)
			{
			if(UDMaxDepotIndex<MaxDrawIndex &&DepotNum[i+DepotPage][0].substring(0,1).equals(""+DepotIndexLR))//���߷���
			{
				if(DepotIndexUD == UDMaxDepotIndex)
				{
					UseGoodsIndex = DepotNum[i+DepotPage][0];//�õ���ѡ����Ʒ�ı��
					UseGoodsOid = DepotNum[i+DepotPage][3];
					SP_ShopIntro.setFrame(0);
					SeeImg = img_Depot_Goods[IMGID];
				}
				else
				{
					SP_ShopIntro.setFrame(1);
				}
				SP_ShopIntro.setPosition(0, 
				5+img_Shop3.getHeight()+ UDMaxDepotIndex*(img_Depot_Goods[IMGID].getHeight()+3)
				+(img_Depot_Goods[IMGID].getHeight()-SP_ShopIntro.getHeight()));
				SP_ShopIntro.paint(g);
				g.drawImage(img_Depot_Goods[IMGID], 2, 3+img_Shop3.getHeight()+UDMaxDepotIndex*(img_Depot_Goods[IMGID].getHeight()+3), 0);
				
				g.setColor(0,0,0);
				g.drawString(DepotNum[i+DepotPage][4], //����������
						PriceRimX+(PriceRim-ft.stringWidth(""+DepotNum[i+DepotPage][3]))/2, 
						5+img_Shop3.getHeight()+ UDMaxDepotIndex*(img_Depot_Goods[IMGID].getHeight()+3)+(img_Depot_Goods[IMGID].getHeight()-SP_ShopIntro.getHeight())+
							(SP_ShopIntro.getHeight() - FontH)/2, 0);
//				//����������
				g.drawString(DepotNum[i+DepotPage][1], 2+img_Depot_Goods[IMGID].getWidth()+5, //����������
						5+img_Shop3.getHeight()+ UDMaxDepotIndex*(img_Depot_Goods[IMGID].getHeight()+3)+(img_Depot_Goods[IMGID].getHeight()-SP_ShopIntro.getHeight())+
						(SP_ShopIntro.getHeight() - FontH)/2, 0);
				UDMaxDepotIndex++;
			}
			}
//			else 
//			{
//				UseGoodsIndex = "-1";
//			}
		}
		if(SEE)
		{
			DrawSEE(g, SeeImg, DepotNum, UseGoodsIndex);
		}
		g.setColor(255,255,255);
		g.fillRect(SW-6, img_Shop3.getHeight(),6, SH);
		g.setColor(0xff719c);
		g.fillRect(SW-6+1, img_Shop3.getHeight(), 6-2, SH);
		DrawL_R(g, Const.Option, Return2);
		
	}
	private void DrawGame(Graphics g)
	{
//		if(card.STATE!=card.playerInfo)
			card.Paint(g);
//		else
//			DrawDeskInfo(g);
	}
	public static void drawBeautyString(Graphics g,String str,int x,int y,int colcr,int colcr2)
	{
	//	g.setColor(0xffd2964);
		g.setColor(colcr);
	    g.drawString(str, x-1, y, 0x10 | 0x4);
		g.drawString(str, x+1, y, 0x10 | 0x4);
		g.drawString(str, x, y-1, 0x10 | 0x4);
		g.drawString(str, x, y+1, 0x10 | 0x4);
//		g.setColor(0xffffff);
		g.setColor(colcr2);
		g.drawString(str, x, y, 0x10 | 0x4);
	}

	public void keyPressed(int keyCode)
	{
		if(CLUE)
		{
			KeyClue(keyCode);
			return;
		}
		if(WAIT)
			return;
		
		if(Option)
		{
			KeyOption(keyCode);
			return;
		}
		if(keyCode == LeftCom)
		{
			switch (STATE) 
			{
				case LOGIN:
					String str3[] = {Land,Login,GetPossW,Return};
					OptionStr = str3;
					Option = true;
					break;
					
				case SCENES:
				case RommList:
				case PIAZZA:
					String str[] = {Quick,IndividualInof,Set,Depot,EnterShop,Const.Help};
					OptionStr = str;
					Option = true;
					break;
					
				case DeskHALL:
					String str2[] = {Quick,IndividualInof,Set,Depot,EnterShop,Const.Help};
					OptionStr = str2;
					Option = true;
					break;
					
				case Deskinfo:
					String str7[] = {Kick,Return};
					OptionStr = str7;
					Option = true;
					break;
					
				case Game:
					String str4[] = {"�������","ǿ���˳�"};
					OptionStr = str4;
					Option = true;
					break;
					
				case SHOP:
					String str1[]={IndividualInof,Depot,Const.��ֵ};
					OptionStr = str1;
					Option = true;
					break;
					
				case DEPOT:
					String str5[]={Use,Speak,EnterShop,Const.��ֵ};
					OptionStr = str5;
					Option = true;
					break;
					
				case ��ֵ:
					String str6[]={Const.Speak};
					OptionStr = str6;
					Option = true;
					break;
					
					
					
					
//				case SHOP1:
//				case DEPOT:
//					String str2[]={"�鿴","��������","�ֿ�","��ֵ����"};
//					OptionStr = str2;
//					Option = true;
//					
//					break;
			}
		}
		

		
		switch (STATE) 
		{
			case REGISTER: break;
	
			case LOGIN: KeyLogin(keyCode); break;
				
			case LOOKUP:keyLookup(keyCode);break;
			
			case MyInfo: KeyMyinfo(keyCode);break;
			
			case COVER:
				if(keyCode!=0)
				{
					FreeRes(LOGO);
					SwitchState(LOAD,false);
					LoadState = MenuOption;
				//	LoadState = PIAZZA;
				}
				break;
				
			case MenuOption: KeyMenu(keyCode); break;
				
			case SCENES: KeySCENE(keyCode); break;
			
			case SET: KeySet(keyCode); break;
				
			case About: KeyAbout(keyCode);break;
			
			case Help: keyHelp(keyCode);break;
			
			case RommList:KeyRoomList(keyCode);break;
			
			case DeskHALL:KeyDeskHall(keyCode);break;
			
			case Deskinfo:KeyDeskInfo(keyCode);break;
			
			case Game:card.Input(keyCode);break;
			
			case SHOP:KeyShop(keyCode);break;
			
			case SHOP1:KeyShop1(keyCode);break;
			
			case DEPOT:KeyDepot(keyCode);break;
			
			case PIAZZA:KeyPiazza(keyCode);break;
			
			case ��ֵ:Key��ֵ(keyCode);break;
			
			case ��ֵ2:Key��ֵ2(keyCode);break;
			
			case ��Ϸ:KeyGamePiazza(keyCode);break;
		}
	}
	private void KeyGamePiazza(int key)
	{
		switch (key) 
		{
			case UP:
				if(PiazzaIndex>2)
					PiazzaIndex-=3;
				break;
				
			case DOWN:
				if(PiazzaIndex<PiazzaLenth-3)
					PiazzaIndex+=3;
				break;
				
			case LEFT:
				if(PiazzaIndex>0)
					PiazzaIndex--;
				break;
				
			case RIGHT:
				if(PiazzaIndex<PiazzaLenth-1)
					PiazzaIndex++;
				break;
				
			case RightCom:
				
//				SwitchState(LOGIN, false);
//				Hashtable h = new Hashtable();
//				h.put("Type", "SYNC");
//				h.put("Cmd", "EXIT");
//				h.put("UID", ""+userID);
//			    Connection(h);
				//ReturnState();
				SwitchState(PIAZZA, false);
				try {
					img_MenuStr2 = Image.createImage("/menustr2.png");
				} catch (IOException e1) {
				}
				break;
				
			case Enter:
				Hashtable h = new Hashtable();
				h = new Hashtable();
				switch (PiazzaIndex) 
				{
					case 0: //������
						
						h.put("Type", "SYNC");
						h.put("Cmd", "SCENES");
						h.put("UID", ""+userID);
						SetWait("��ȡ��������");
						HallIndex = 0;
					    Connection(h);
						break;
//					case 1: break;
//					case 2: break;
					case 3: 
						break;
					case 4: break;
					case 5: //�̵�
//						SaveState[SaveStateIndex++] = STATE;
//						SwitchState(LOAD,false);
//						LoadState = SHOP;
						
						break;
					case 6: //��ֵ����
	//					SwitchState(��ֵ,true);
						break;
					
					case 7:       //�ֿ�
//						try {
//							img_Shop3 = Image.createImage("/Shop_3.png");
//							img_Shop4 = Image.createImage("/Shop_4.png");
//						} catch (IOException e) {
//						}
//						h.put("Type", "SYNC");
//						h.put("Cmd", "ITEMS");
//						h.put("UID", ""+userID);
//						
//						SwitchState(DEPOT,true);
//						SetWait("���ڶ�ȡ�ֿ���Ϣ");
//						Connection(h);
						break;
					case 8: break;
					case 9: break;
					case 10: SwitchState(SET,true); break;
					case 11: 
//							SwitchState(Help,true);
//							TempStr = code.GetString(1,code.FILE_NAME_STRING);
						break;
				}
				break;
		}
	}
	private void KeyPiazza(int key)
	{
		switch (key) 
		{
			case UP:
				if(PiazzaIndex>2)
					PiazzaIndex-=3;
				break;
				
			case DOWN:
				if(PiazzaIndex<PiazzaLenth-3)
					PiazzaIndex+=3;
				break;
				
			case LEFT:
				if(PiazzaIndex>0)
					PiazzaIndex--;
				break;
				
			case RIGHT:
				if(PiazzaIndex<PiazzaLenth-1)
					PiazzaIndex++;
				break;
				
			case RightCom:
				SwitchState(LOGIN, false);
				Hashtable h = new Hashtable();
				h.put("Type", "SYNC");
				h.put("Cmd", "EXIT");
				h.put("UID", ""+userID);
			    Connection(h);
				//ReturnState();
				try {
					img_MenuStr2 = Image.createImage("/menustr2.png");
				} catch (IOException e1) {
				}
				break;
				
			case Enter:
				h = new Hashtable();
				switch (PiazzaIndex) 
				{
					case 0: //��Ϸ����
						
//						h.put("Type", "SYNC");
//						h.put("Cmd", "SCENES");
//						h.put("UID", ""+userID);
//						SetWait("��ȡ��������");
//						HallIndex = 0;
//					    Connection(h);
						try{			
							try{
								// First create a splash screen.
								// Displaying a slash screen apart from been a good practice to notify the user 
								// that the application is loading, also helps to avoid an annoing bug on some phones 
								// that cannot switch to fullscreen canvas if there not a canvas already on the screen
								

//								SplashScreen splash = new SplashScreen();
//								splash.setBgColor(0x00FFFFFF);
//								splash.setFgColor(0x00395f79);
//								try
//								{
//									Image logo = Image.createImage(this.getClass().getResourceAsStream("/icon.png"));
//									splash.setLogo(logo);
//								} catch (Exception e)
//								{ // failed to set image as logo, set text.
//									splash.setTitle("Fire Browser");
//								}
//								disp.setCurrent(splash);
								
								// now continue with the application initialization.
								fireScreen = FireScreen.getScreen(Display.getDisplay(MID));
								fireScreen.setFullScreenMode(true);	
								
								// set the theme. The theme can be stored either locally or remotely accesible via http
								FireScreen.setTheme(new FireTheme("file://theme.properties"));
								
								b = new Browser();
								b.getHttpClient().loadCookies("testingcookies"); // load previously stored cookies. This way we can keep session info across application invocations. see the javadoc for more info.
								b.setListener(this); // i want all form and link events to come to this class
								b.setPageListener(this); // i want to be notified when a request to loadPageAsync completes. The default method that handles link and form events in the browser calls the loadPageAsync.
								
								showMainMenu(); // load the main menu.
								
								// ok so far we displayed the main menu. Now lets do something more fancy.
								
								// create an animation to run on the Screen.
								// For this examples I will use the SpriteAnimation class, created to demonstrate how to create custom animations.
								// Check the SpriteAnimation javadoc and in source comments for more details
								// The SpriteAnimation loads a png sprite and is actually a wrapper for the Sprite class inside FireScreen.
//								SpriteAnimation anim = new SpriteAnimation(new Sprite(Image.createImage(this.getClass().getResourceAsStream("/sheep-anim.png")),40,29));
//								anim.setPosition(fireScreen.getWidth()-140,fireScreen.getHeight()-29-30);
//								anim.setAutoMoveData(-4,0,10,10,200,200,false,true);
//								anim.setAutoMove(true);
//								fireScreen.addComponent(anim,-1); // ZINDEX -1 will make FireScreen display the animation below the panel. 
								// This will cause the animation to apear behing the html page. You can change this value to move it above or below a component.
								// By default all components set to the FireScreen using the setCurrent method get ZINDEX=0.
								// NOTE: Adding a component on the same ZINDEX as another will remove the oldest one from the FireScreen.
								
								// Ok Display an alert. FireScreen.showAlert is a utility method for displaying alerts fast and easy.  
//								fireScreen.showAlert(Lang.get("Welcome to the BrowserTest middlet. Demonstrating the capabilities of the Fire2.2 Browser component."),Alert.TYPE_INFO,Alert.USER_SELECTED_OK,null,null);

								Log.logInfo("Phones supported keyRepeated events: "+fireScreen.hasRepeatEvents());
								
								// Use a console in this sample. 
								console = new Console(); // create the instance of the Console which implements Logger 
								Log.addLogDestination(console); // add the logger to the Log destinations.
								// now each time a method from Log (logInfo, logWarn, logError, logDebug) is called the 
								// logger will also append the output to the console
								//Log.showDebug=true;
								Log.logDebug("Console initialized."); // This will only be displayed if Log.showDebug==true. 
								
								
							}catch(Throwable e)
							{
								Log.logError("Application Failed to start",e);
							}
						}catch(Exception e) // failed to load main menu.
						{
							// show console
							//console.showConsole();
							// log the event.
							Log.logError("Failed to load main page.",e);
						}
						break;
//					case 1: break;
//					case 2: break;
					case 3: 
						SwitchState(��Ϸ,true);
						break;
					case 4: break;
					case 5: //�̵�
//						SaveState[SaveStateIndex++] = STATE;
//						SwitchState(LOAD,false);
//						LoadState = SHOP;
						
						break;
					case 6: //��ֵ����
	//					SwitchState(��ֵ,true);
						break;
					
					case 7:       //�ֿ�
//						try {
//							img_Shop3 = Image.createImage("/Shop_3.png");
//							img_Shop4 = Image.createImage("/Shop_4.png");
//						} catch (IOException e) {
//						}
//						h.put("Type", "SYNC");
//						h.put("Cmd", "ITEMS");
//						h.put("UID", ""+userID);
//						
//						SwitchState(DEPOT,true);
//						SetWait("���ڶ�ȡ�ֿ���Ϣ");
//						Connection(h);
						break;
					case 8: break;
					case 9: break;
					case 10: SwitchState(SET,true); break;
					case 11: 
//							SwitchState(Help,true);
//							TempStr = code.GetString(1,code.FILE_NAME_STRING);
						break;
				}
				break;
		}
	}
	protected void Key��ֵ(int key)
	{
		switch (key) 
		{
			case LEFT:
				if(��ֵLR>0)
					��ֵLR--;
				break;
				
			case RIGHT:
				if(��ֵLR<2)
					��ֵLR++;
				break;
			case UP:
				if(��ֵUD>0)
				{
					��ֵUD--;
					if(TempIndex>0)
						TempIndex--;
					else
						TempIndex2--;
				}
				break;
				
			case DOWN:
				if(��ֵUD<TempStr2.length-1)
				{
					��ֵUD++;
					if(TempIndex<4)
						TempIndex++;
					else
						TempIndex2++;
				}
				break;
				
			case RightCom:
				if(img_Shop1!=null)
				{
					img_Shop1 = null;
					img_Shop2 = null;
				}
				ReturnState();
				break;
			
			case Enter:
				switch (��ֵLR) 
				{
					case 0:
						switch (��ֵUD) 
						{
							case 0://���ų�ֵ/���Ͷ���
								
								break;
	
							default:
								��ֵ2ͷ���� = TempStr2[��ֵUD][0];
								OpenParMenu = false;
								ui.ParMenuIndex = 0;
								SwitchState(��ֵ2, true);
								break;
						}
						
						break;
				}
			
				break;
		}
	}
	protected void Key��ֵ2(int key)
	{
		switch (key) 
		{
			case UP:
				if(OpenParMenu && ui.ParMenuIndex>0)
					ui.ParMenuIndex--;
				else
				{
					if(��ֵ2UD>0)
						��ֵ2UD--;
				}
				
				break;
				
			case DOWN:
				if(OpenParMenu && ui.ParMenuIndex<ui.ParMenuLenth-1)
					ui.ParMenuIndex++;
				else
				{
					if(��ֵ2UD<��ֵѡ���)
						��ֵ2UD++;
				}
				break;				
			case RightCom:
				Par = null;
				
				ReturnState();
				break;
			case LeftCom:
				Hashtable h = new Hashtable();
				h.put("Type", "SYNC");
				h.put("Cmd", "DESPOSIT");
				h.put("DP-TYPE",""+��ֵUD);
				h.put("COST",""+100);
				Connection(h);
				break;
			case Enter:
				switch (��ֵ2UD) 
				{
					case 0:
						OpenParMenu = !OpenParMenu;
						break;
	
					case 1:
						Chat c = new Chat("���������к�","",15,TextField.NUMERIC,MID,this);
						c.ShowChat();
						break;
					case 2:
						Chat c1 = new Chat("����������","",11,TextField.NUMERIC,MID,this);
						c1.ShowChat();
						break;
				}
				break;
		}
	}
	private void KeyClue(int key)//���������ʾ
	{
		
			switch (key) 
			{
				case RightCom:
					if(ClueRight.equals("�˳�"))
					{
						MID.exitMID();
					}
					else if(ClueRight.equals("��"))
					{
						card.Quit = false;
						CLUE=!CLUE;
					}
					else if(ClueRight.equals("ȷ��"))
					{
						Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "ROOM");
						h.put("Room-ID", ""+(SceneID*10+RoomID));
						h.put("UID", ""+userID);
						Connection(h);
					}
					else if(ClueRight.equals(ReturnDepot))
					{
						SetWait("���ڶ�ȡ�ֿ���Ϣ");
						Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "ITEMS");
						h.put("UID", ""+userID);;
					//	SwitchState(DEPOT,true);
						Connection(h);
					}
					else
						CLUE=!CLUE;
					break;
	
				case LeftCom:
					if(ClueLeft.equals("��"))//ȷ���˳�
					{
						if(card!=null)
						{
							if(card.Quit)
							{
								 Hashtable h = new Hashtable();
								h.put("Type", "SYNC");
								h.put("Cmd", "QUIT");
								h.put("UID", ""+userID);
								h.put("MULTIPLE", ""+card.multiple);
								CLUE = false;
								SwitchState(Deskinfo,true);
								SitDeskInfo[DeskMyPos][1] = 
									""+(Integer.parseInt(SitDeskInfo[DeskMyPos][1]) - 3*50*card.multiple*2);
								 Connection(h);
							//	SetWait("���Ժ�");
							//	Connection();
							}
					//		card.KeyOption(key);
							
							ClueLeft = "";
						}
					}
					else if(ClueLeft.equals(Const.��ֵ))
					{
						
					}
					else if(ClueLeft.equals(Login2))
					{
						MD5 mymd5 = new MD5();
						 Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "LOGIN");
						h.put("Username", user);
						h.put("Password", mymd5.getMD5ofStr(PassWord));
						SetWait("���ڵ�½");
						Connection(h);
					}
					else if(ClueLeft.equals(Const.Depot))
					{
//						try {
//							img_Shop3 = Image.createImage("/Shop_3.png");
//							img_Shop4 = Image.createImage("/Shop_4.png");
//						} catch (IOException e) {
//						}
						 Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "ITEMS");
						h.put("UID", ""+userID);
						SEE = false;
						SwitchState(DEPOT,true);
						 Connection(h);
					}
					else if(ClueLeft.equals(Const.BuyVip))
					{
						ShopMenuIndex = 2;
						SaveState[SaveStateIndex++] = STATE;
						SwitchState(LOAD,false);
						LoadState = SHOP1;
//						SwitchState(SHOP1,true);
					}
					break;
			}
	}
	/**
	 * ��������ѡ��
	 * @param key
	 */
	private void KeyOption(int key)
	{
		switch (key)
		{
			case UP:
				OptionIndex--;
				if(OptionIndex<0)
					OptionIndex = OptionStr.length-1;
				break;
	
			case DOWN:
				OptionIndex++;
				if(OptionIndex>OptionStr.length-1)
					OptionIndex = 0;
				break;
				
			case Enter:
			case LeftCom:
				if(OptionStr[OptionIndex].equals(Const.IndividualInof))
				{
					tempMyinfo = new String[Myinfo.length];
					System.arraycopy(Myinfo, 0, tempMyinfo, 0, tempMyinfo.length);
					SwitchState(MyInfo,true);
				}
				else if(OptionStr[OptionIndex].equals(Set))
				{
					SwitchState(SET,true);
				}
				else if(OptionStr[OptionIndex].equals(Const.Return))
				{
					if(STATE == Deskinfo)
					{
						Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "ROOM");
						h.put("Room-ID", ""+(SceneID*10+RoomID));
						h.put("UID", ""+userID);
						Ready = false;
						WAIT = true;
						WaitStr = "��ȡ��������";	
						DeskInfo=null;
						if(SP_menuBG==null)//����Ϸ���ش�����ȡ�˵���Դ
						{
							System.out.println("------");
							FreeRes(Game);//�ͷ���Ϸ������Դ
							SwitchState(LOAD,false);
							LoadState = MenuOption;
						}
						 Connection(h);
					}
					else if(STATE == RommList)
					{
						SwitchState(MenuOption, false);
					}
					else if(STATE == Game)//�ڲ鿴���Ϸ��ص���Ϸ����
					{
						card.playerInfo = false;
					}
					else if(STATE == LOGIN)
					{
						SwitchState(MenuOption, false);
					}
					else
						ReturnState();
				}
				else if(OptionStr[OptionIndex].equals(Const.EnterShop))
				{
			//		LastSTATE = STATE;
					SaveState[SaveStateIndex++] = STATE;
					SwitchState(LOAD,false);
					LoadState = SHOP;
				}
				else if(OptionStr[OptionIndex].equals(Const.Depot))
				{
				//	STATE = DEPOT;

					 Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "ITEMS");
					h.put("UID", ""+userID);
					SwitchState(DEPOT,true);
					SetWait("���ڶ�ȡ�ֿ���Ϣ");
					 Connection(h);
				
				}
				else if(OptionStr[OptionIndex].equals(Const.Land))
				{
					if(!user.equals("") && !PassWord.equals(""))
					{
						 Hashtable h = new Hashtable();
						MD5 mymd5 = new MD5();
						h.put("Type", "SYNC");
						h.put("Cmd", "LOGIN");
						h.put("Username", user);
						h.put("Password", mymd5.getMD5ofStr(PassWord));
						WAIT = true;
						WaitStr = "���ڵ�½";
						 Connection(h);
					}
					else
					{
						SetClue("�û��������벻��Ϊ��", "", "�ر�");
					}
				}
				else if(OptionStr[OptionIndex].equals(Const.GetPossW))
				{
					SwitchState(LOOKUP,true);
				}
				else if(OptionStr[OptionIndex].equals(Const.Login))
				{
					SetWait("����ע��");
					 Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "QUICK");
					 Connection(h);
				}
				else if(OptionStr[OptionIndex].equals(Const.Kick))
				{
					if(Myinfo[6].equals("0"))
					{
						SetClue("�Բ���,�㲻��VIP����ʹ�����˹���", Const.BuyVip, "�ر�");
						Option = false;
						return;
					}
					if(DeskInfoIndex != DeskMyPos  && !SitDeskInfo[DeskInfoIndex][0].equals("-1"))
					{
						SetWait("���Ժ�...");
						 Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "KICK");
						h.put("UID", ""+userID);
						h.put("PST", ""+DeskInfoIndex);
						 Connection(h);
					}
				}
				else if(OptionStr[OptionIndex].equals(Const.Speak))
				{
					if(UDMaxDepotIndex>0)
						SEE = true;
				}
				else if(OptionStr[OptionIndex].equals(Const.Use))
				{
					if(UDMaxDepotIndex>0)
					UseGoods(UseGoodsIndex);
				}
				else if(OptionStr[OptionIndex].equals(Const.��ֵ))
				{
					SwitchState(��ֵ, true);
				}
				else if(OptionStr[OptionIndex].equals(Const.Quit))
				{
					card.Quit = true;
					SetClue("�Ƿ�ǿ���˳�?", "��", "��");
				}
				else if(OptionStr[OptionIndex].equals(Const.PlayeInfo))
				{
					card.playerInfo = true;
				}
				else if(OptionStr[OptionIndex].equals(Const.Quick))
				{
					Hashtable h = new Hashtable();
					MD5 mymd5 = new MD5();
					h.put("Type", "SYNC");
					h.put("Cmd", "QGAME");
					h.put("Username", user);
					h.put("Password", mymd5.getMD5ofStr(PassWord));
					SetWait("���Ժ�");
					 Connection(h);
				}
				
				Option = !Option;
				OptionIndex = 0;
				break;
				
			case RightCom:
				Option = !Option;
				
				break;
		}
	}
	private void KeyMyinfo(int key)
	{
		switch (key) 
		{
		
			case UP:
				if(MyinfoIndex>0)
					MyinfoIndex--;
				
				break;
			case DOWN:
				if(MyinfoIndex<3)
					MyinfoIndex++;
				break;
				
			case LEFT:
				switch (MyinfoIndex) 
				{
					case 0:
						int temp = Integer.parseInt(tempMyinfo[5]);
						temp-=2;
						if(temp<0)
							temp*=-1;
						tempMyinfo[5] = ""+(temp%4);
						break;
						
					case 3:
						tempMyinfo[3] = "0";
						tempMyinfo[5] = "0";
						break;
				}
				break;
				
			case RIGHT:
				switch (MyinfoIndex) 
				{
					case 0:
						int temp = Integer.parseInt(tempMyinfo[5]);
						temp+=2;
						tempMyinfo[5] = ""+(temp%4);
						break;
						
					case 3:
						tempMyinfo[3] = "1";
						tempMyinfo[5] = "1";
						break;
				}
				break;
			case LeftCom:
				 Hashtable h = new Hashtable();
				MD5 mymd5 = new MD5();
				h.put("Type", "SYNC");
				h.put("Cmd", "INFO");
				h.put("UID", userID);
				h.put("head", tempMyinfo[5]);
				h.put("sex", tempMyinfo[3]);
				h.put("nickname", tempMyinfo[1]);
				h.put("new-pwd", mymd5.getMD5ofStr(tempMyinfo[2]));
				SetWait("���Ժ�");
				 Connection(h);
				break;
				
			case RightCom:
				tempMyinfo = null;		
				ReturnState();
		//		SwitchState(LastSTATE);
				break;
			case Enter:
				switch (MyinfoIndex) 
				{
					case 1:
	                 	Chat c = new Chat("�޸��ǳ�:1-12���ַ�","",11,TextField.NUMERIC,MID,this);
                    	c.ShowChat();
						break;
	
					case 2:
	                 	c = new Chat("�޸�����:1-12���ַ�","",11,TextField.NUMERIC,MID,this);
                    	c.ShowChat();
						break;
				}
				break;
		}
	}
	private void KeyLogin(int key)
	{
		switch (key) 
		{
			case UP:
				if(LoginIndex>0)
					LoginIndex--;
				break;
				
			case DOWN:
				if(LoginIndex<2)
					LoginIndex++;
				break;
				
//			case LeftCom:
//				Option = true;
//				break;
				
			case RightCom:
				if(!user.equals("") && !PassWord.equals(""))
				{
					MD5 mymd5 = new MD5();
					 Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "LOGIN");
					h.put("Username", user);
					h.put("Password", mymd5.getMD5ofStr(PassWord));
					SetWait("���ڵ�½");
				    Connection(h);
			//		new XConnection(false,MID,h,this);
				}
				else
				{
					SetClue("�û��������벻��Ϊ��", "", "�ر�");
				}
			//	SwitchState(MenuOption);
				break;
				
			case Enter:
				switch (LoginIndex) 
				{
					case 0:
	                 	Chat c = new Chat("�ʺ�:1-12���ַ�","",11,TextField.NUMERIC,MID,this);
                    	c.ShowChat();
						break;
						
					case 1:
	                 	Chat c1 = new Chat("����:1-12���ַ�","",12,TextField.NUMERIC,MID,this);
                    	c1.ShowChat();
						break;
	
					case 2:
						BOOLremember=!BOOLremember;
						break;
				}
				break;

		}
	}
	private void keyLookup(int key)
	{
		switch (key) 
		{
			case RightCom:
				ReturnState();
		//		SwitchState(LastSTATE);
				break;
	
			case LeftCom:
				
				break;
		}
	}
	private void KeyShop(int key)
	{
			switch (key) 
			{
				case RightCom:
					LoadState = SaveState[--SaveStateIndex];
					OptionIndex = 0;
					SwitchState(LOAD,false);
					break;
					
				case LeftCom:Option = true;break;
				
				case LEFT:
					ShopMenuIndex--;
					if(ShopMenuIndex<0)
						ShopMenuIndex = 3;
					break;
				case RIGHT:
					ShopMenuIndex++;
					if(ShopMenuIndex>3)
						ShopMenuIndex = 0;
					break;
				case UP:
					ShopMenuIndex-=2;
					if(ShopMenuIndex<0)
						ShopMenuIndex+=4;
					break;
		
				case DOWN:
					ShopMenuIndex+=2;
					if(ShopMenuIndex>3)
						ShopMenuIndex-=4;
					break;
					
				case Enter:
					STATE = SHOP1;
					ShopIndexLR = 0;
					break;
			}
	}
	private void KeyShop1(int key)
	{
		switch (key) 
		{
			case UP:
				if(!SEE)
				{
					ShopIndexUD--;
					if(ShopIndexUD<0)
						ShopIndexUD = UDMaxShopIndex-1;
				}
				break;
				
			case DOWN:
				if(!SEE)
				{
				ShopIndexUD++;
				if(ShopIndexUD>UDMaxShopIndex-1)
					ShopIndexUD = 0;
				}
				break;
				
			case LEFT:
				
				if(ShopIndexLR>0 && !SEE )
				{
					ShopIndexLR--;
					ShopIndexUD = 0;
				}
				else
				{
					if(BOOL_Buy!=0)
						BOOL_Buy--;
				}
				break;
	
			case RIGHT:
				if(ShopIndexLR<2 && !SEE)
				{
					ShopIndexLR++;
					ShopIndexUD = 0;
				}
				else
				{
					if(BOOL_Buy!=1)
						BOOL_Buy++;
				}
				break;
			case LeftCom:
				if(GoodsNum != "-1")
				{
					if(!SEE && GoodsInfo!=null )
					{
						SEE = !SEE;//�鿴��Ʒ
						BOOL_Buy = 0;
					}
					else
					{
						OpenBuy = true;
					}
				}
				break;
				
			case RightCom:
				if(!SEE)
				{
					STATE = SHOP;
				}
				else
				{
					SEE = !SEE;
					OpenBuy = false;
				}
			//	SwitchState(SHOP);
				break;
				
			case Enter:
				if(OpenBuy)
				{
					switch (BOOL_Buy) 
					{
						case 0://ȷ�Ϲ��� 
							Hashtable h = new Hashtable();
							h.put("Type", "SYNC");
							h.put("Cmd", "BUY");
//							String str = ""+ShopIndexUD;
//							if(ShopIndexUD<10)
//								str = "0"+(ShopIndexUD+2);
							
							h.put("itemtype",GoodsNum);
//							h.put("itemtype", ""+ShopMenuIndex+""+ShopIndexLR+str);
							h.put("UID", ""+userID);
							SetWait("���Ժ�");
							Connection(h);
							break;
	
						default:
							OpenBuy = false;
							break;
					}
				}
				break;
		}
	}
	private void KeyDepot(int key)//�ֿ�
	{
		switch (key) 
		{
			case UP:
				if(!SEE)
				{
					if(DepotIndexUD>0)
						DepotIndexUD--;
					else if(DepotPage>0)
						DepotPage--;
					
//				if(DepotIndexUD<0)
//					DepotIndexUD = UDMaxDepotIndex-1;
				}
				break;
	
			case DOWN:
				if(!SEE)
				{
					if(DepotIndexUD<UDMaxDepotIndex-1)
						DepotIndexUD++;
					else if(DepotPage+MaxDrawIndex<DepotCount)
					{
						DepotPage++;
					}
//				if(DepotIndexUD>UDMaxDepotIndex-1)
//					DepotIndexUD = 0;
				}
				break;
				
			case LEFT:
				if(!SEE)
				{
				if(DepotIndexLR>0)
				{
					DepotIndexLR--;
					DepotIndexUD = 0;
				}
				}
				break;
				
			case RIGHT:
				
				if(DepotIndexLR<3)
				{
					DepotIndexLR++;
					DepotIndexUD = 0;
				}
				break;
				
			case RightCom:
				if(SEE)
				{
					SEE = false;
				}
				else
				{
					ReturnState();
					if(STATE == SHOP || STATE==SHOP1)//����������̵�
					{
						img_Depot_Goods = null;
						DepotCount = 0;
						img_Shop3 = null;
						img_Shop4 = null;
					}
					else
					{
			//			FreeRes(DEPOT);
					}
					}
				
				break;
		}
	}
	/**
	 * �˵�����
	 * @param key
	 */
	private void KeyMenu(int key)
	{
		switch (key) 
		{
			case UP:
				MenuIndex--;
				if(MenuIndex<0)
					MenuIndex = 6;
				break;
				
			case DOWN:
				MenuIndex++;
				if(MenuIndex>6)
					MenuIndex = 0;
				break;
				
			case Enter:
			case LeftCom:
				switch (MenuIndex) 
				{
					case 0:
						if(user!="")
						{
							Hashtable h = new Hashtable();
							MD5 mymd5 = new MD5();
							h.put("Type", "SYNC");
							h.put("Cmd", "QGAME");
							h.put("Username", user);
							h.put("Password", mymd5.getMD5ofStr(PassWord));
							SetWait("���Ժ�");
							 Connection(h);
						}
						else
							SetClue("����ע��", "", "�ر�");
						break;
					case 1:
						SwitchState(LOGIN,true);
						break;
					case 2:
						SetWait("����ע��");
						repaint();
						 Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "QUICK");
						 Connection(h);
						break;
					case 3:
						SwitchState(SET,true);
						break;
					case 4:
						SwitchState(About,true);
						TempStr = code.GetString(0,code.FILE_NAME_STRING);
						break;
					case 5:
						SwitchState(Help,true);
						TempStr = code.GetString(1,code.FILE_NAME_STRING);
						break;
					case 6:
						try {
							MID.platformRequest(code.GetString(2,code.FILE_NAME_STRING));
						} catch (ConnectionNotFoundException e) {
							e.printStackTrace();
						}
						break;
				}
				break;
				
			case RightCom:
				MID.exitMID();
				break;
				
			case Num_0:
				CLUE = true;
				break;
		}
	}
	private void KeySCENE(int key)//�������
	{
		switch (key) 
		{
			case LEFT:
			case RIGHT:
				HallIndex++;
				HallIndex%=2;
				if(HallIndex == 1 && TempStr==null)//��ȥ��Ϸ˵��
				{
					TempStr = code.GetString(3,code.FILE_NAME_STRING);
				}
				break;
				
			case UP:
				if(HallIndex==0)
				{
					if(HallIndex2>0)
						HallIndex2--;
				}
				else if(HelepIndex<0)//˵������
					HelepIndex+=30;

				
				break;
			case DOWN:
				if(HallIndex==0)
				{
					if(HallIndex2<ScenesLength-1)
						HallIndex2++;
				}
				else if(ui.STRY >HelepIndex)
				{
					HelepIndex-=30;
					if(HelepIndex <= -ui.STRY)
						HelepIndex = 0;
				}

				break;
				
			case Enter:
	//		
				if(HallIndex==0)
				{
					 Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "SCENE");
					h.put("Scene-ID", ""+(HallIndex+1));
					h.put("UID", ""+userID);
					SetWait("��ȡ��������");
					HallIndex2 = 0;
					HallIndex = 0;
					DeskInfo = null;
					ScenesTypeLen = null;
					 Connection(h);
				}
				break;
				
			case RightCom://���ع㳡
				Image img=null;
				try {
					img_MenuStr = Image.createImage("/menustr.png");
	//				img_MenuStr2 = Image.createImage("/menustr2.png");
					img_square_icon_bg = Image.createImage("/square_icon_bg.png");
					img = Image.createImage("/square_icon.png");
					SP_piazza = new Sprite(img,img.getWidth(),img.getHeight()/12);
					img = Image.createImage("/jiandou.png");
					SP_Arrow = new Sprite(img,img.getWidth()/2,img.getHeight());
				} catch (IOException e) {
				}
//				SetWait("��ȡ��������");
//				h.clear();
//				h.put("Type", "SYNC");
//				h.put("Cmd", "SCENES");
//				h.put("UID", ""+userID);
//				HallIndex = 0;
//				SwitchState(LOAD);
//				LoadState = PIAZZA;
				SwitchState(PIAZZA,false);
//				ReturnState();
				break;
				
			case Num_0:
				CLUE = true;
				break;
				
//			case LeftCom:
//				if(HallIndex==0)
//					Option = !Option;
//				break;
				
		}
	}
	private void KeyRoomList(int key)
	{//��������ƽ�ȥ���Ӵ�����
		switch (key) 
		{
			case LEFT:
			case RIGHT:
				HallIndex++;
				HallIndex%=2;
				if(HallIndex == 1 && TempStr==null)//��ȥ��Ϸ˵��
				{
					TempStr = code.GetString(3,code.FILE_NAME_STRING);
				}
				break;
				
			case UP:

				if(HallIndex==0)
				{
					if(RommHallIndex>0)
						RommHallIndex--;
				}
				else if(HelepIndex<0)
					HelepIndex+=30;
				break;
			case DOWN:

				if(HallIndex==0)
				{
					if(RommHallIndex<room-1)
						RommHallIndex++;
				}
				else if(ui.STRY >HelepIndex)
				{
					HelepIndex-=30;
					if(HelepIndex <= -ui.STRY)
						HelepIndex = 0;
				}
				break;
				
			case Enter:
				if(HallIndex==0)
				{
					 Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "ROOM");
					RoomID = RommHallIndex+1;
					h.put("Room-ID", ""+(RoomID+SceneID*10));;
					h.put("UID", ""+userID);
					SetWait("��ȡ���Ӵ�������");
					DeskInfo = null;
					 Connection(h);
				}
				break;
				
			case RightCom:
				SetWait("��ȡ��������");
				 Hashtable h = new Hashtable();
				h.clear();
				h.put("Type", "SYNC");
				h.put("Cmd", "SCENES");
				h.put("UID", ""+userID);
				HallIndex = 0;
				 Connection(h);
				break;				
//				
//			case LeftCom:
//				if(HallIndex==0)
//					Option = !Option;
//				break;
		}
	}
	private void KeyDeskHall(int key)
	{
		switch (key) 
		{
			case LEFT:
				if(HallIndex>0)
					HallIndex--;
				else if(DesKamountIndex>0)
				{
					DesKamountIndex--;
				}
				break;
			case RIGHT:
				if(HallIndex<5)
					HallIndex++;
				else if(DesKamountIndex<DeskAmount/2-3)
				{
					DesKamountIndex++;
				}
				break;
				
			case UP:
				if(HallIndex>1)
					HallIndex-=2;
				else if(DesKamountIndex>0)
				{
					DesKamountIndex--;
				}
				break;
				
			case DOWN:
				if(HallIndex<4)
				HallIndex+=2;
				else if(DesKamountIndex<DeskAmount/2-3)
				{
					DesKamountIndex++;
				}
				break;
				
			case Enter:
				if(Time_EnterDesk==0)
				{
					Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "DESK");
					DeskID = (DesKamountIndex*2+HallIndex+1);
					DeskID = SceneID*1000+RoomID*100+DeskID;
					h.put("Desk-ID", ""+ DeskID);
					h.put("UID", ""+userID);	
					SetWait("��ȡ��������");
					Connection(h);
				}
				else//����ձ������̨��
				{
					SetClue("�����ոձ��������̨��,�����ʱ���ٽ���,��ΪVIP���Բ�����", Const.BuyVip, "�ر�");
				}
				break;
				
			case RightCom:
				HallIndex = 0;
				 Hashtable h = new Hashtable();
				h.put("Type", "SYNC");
				h.put("Cmd", "SCENE");
				h.put("Scene-ID", ""+(HallIndex+1));
				h.put("UID", ""+userID);
				SetWait("��ȡ��������");
				HallIndex2 = 0;
				HallIndex = 0;
	//			new XConnection(false,MID,h);
				 Connection(h);
				break;			
				
//			case LeftCom:
//				Option = !Option;
//				break;
		}

	}
	public void KeyDeskInfo(int key)
	{
		 switch (key) 
		 {
			 case LEFT:
				 DeskInfoIndex--;
				 if(DeskInfoIndex<0)
					 DeskInfoIndex = 2;
				 break;
				 
			 case RIGHT:
				 DeskInfoIndex++;
				 if(DeskInfoIndex>2)
					 DeskInfoIndex = 0;
				 break;
				 
			case LeftCom:
				if(Ready == false)
				{
					Option = true;
				}
//				if(STATE == Deskinfo)
//				{
//					if(!Ready)
//					{
//					h.clear();
//					h.put("Type", "SYNC");
//					h.put("Cmd", "ROOM");
//					h.put("Room-ID", ""+(SceneID*10+RoomID));
//					h.put("UID", ""+userID);
//					Ready = false;
//					WAIT = true;
//					WaitStr = "��ȡ��������";	
//					DeskInfo=null;
//					}
//					if(SP_menuBG==null)//����Ϸ���ش�����ȡ�˵���Դ
//					{
//						FreeRes(Game);//�ͷ���Ϸ������Դ
//						SwitchState(LOAD,false);
//						LoadState = MenuOption;
//					}
//				}
//				else

				break;
				
			case RightCom:
				if(STATE == Deskinfo)
				{
					 Hashtable h = new Hashtable();
					if(Ready)
					{
						h.put("Type", "SYNC");
						h.put("Cmd", "UNREADY");
						h.put("UID", ""+userID);
					}
					else
					{
						h.put("Type", "SYNC");
						h.put("Cmd", "READY");
						h.put("UID", ""+userID);
					}
					SetWait("���Ժ�");
					 Connection(h);
				}
				else if(STATE == Game)
				{
					card.playerInfo = false;
				}
				
				break;
		}
	}
	private void KeySet(int key)
	{
		switch (key) 
		{
			case LEFT:
			case RIGHT:
				switch (SetIndex) 
				{
					case 0:
						SetIndex2[0]++;
						SetIndex2[0] %= 2;
						break;
	
					case 1:
						SetIndex2[1]++;
						SetIndex2[1] %= 2;
						break;
						
					case 2:
						SetIndex2[2]++;
						SetIndex2[2] %= 2;
						break;
				}
				break;
				
			case UP:
				if(SetIndex>0)
					SetIndex--;
				break;
				
			case DOWN:
				if(SetIndex<2)
					SetIndex++;
				break;
	
			case RightCom:
				ReturnState();
		//		SwitchState(LastSTATE);
				break;
				
			case LeftCom:
				ReturnState();
		//		SwitchState(LastSTATE);
				writeRms();
				break;
				
		}
	}
	private void KeyAbout(int key)
	{
		if(key==RightCom)
		{
			TempStr = null;
			ReturnState();
//			SwitchState(LastSTATE);
		}
	}
	private void keyHelp(int key)
	{
		if(key==RightCom)
		{
			TempStr = null;
			ReturnState();
		//	SwitchState(LastSTATE);
		}
	}
	public void SwitchState(byte state,boolean bool)
	{
		
//		if(state!=LOAD && STATE!=LOAD)//LOAD״̬�������״̬
//		{
		if(bool!=false && state!=LOAD && STATE!=LOAD)
		{
			SaveState[SaveStateIndex++] = STATE;
		}
		STATE = state;
		HallIndex2 = 0;
//		ShopMenuIndex = 0;
		LoadTime = 0;
		WAIT = false;
		CLUE = false;
		if(STATE == Game)
			Ready = false;
		KCTime =  1;
		try {
		switch (state) 
		{
		
			case ��ֵ:
				Image img = Image.createImage("/44.png");
				SP_44 = new Sprite(img,img.getWidth(),img.getHeight()/2);
				img_Shop1 = Image.createImage("/Shop_1.png");
				img_Shop2 = Image.createImage("/Shop_2.png");
				code.LoadStrings2(0, code.FILE_NAME_STRING3,3);
				TempStr2 = code.s_commonStrings2;
				code.FreeStr();		
			break;
			case ��ֵ2:

				ui.img_icon = Image.createImage("/choicegroup_icon.png");
				
				code.LoadStrings(0, code.FILE_NAME_STRING4);
				Par = code.s_commonStrings;
				code.FreeStr();
				break;
			case DEPOT:
				img_Shop3 = Image.createImage("/Shop_3.png");
				img_Shop4 = Image.createImage("/Shop_4.png");
				DepotPage = 0;
				DepotIndexUD = 0;
				break;
			case MyInfo:
				img_VIP = Image.createImage("/ITEM_9.png");
				break;
			case ��Ϸ:
				PiazzaIndex = 0;
				
				break;
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void ReturnState()
	{
		try {
			switch (SaveState[SaveStateIndex]) 
			{
				case ��ֵ2:
				ui.img_icon = null;
				SP_44 = null;
				img_Shop2 = null;
				TempStr2 = null;
				break;
			}
		STATE = SaveState[--SaveStateIndex];
		
		switch (STATE) 
		{
			case ��ֵ:
				if(img_Shop1 == null)
				img_Shop1 = Image.createImage("/Shop_1.png");
				if(img_Shop2 == null)
				img_Shop2 = Image.createImage("/Shop_2.png");
				if(SP_44 == null)
				{
				Image img = Image.createImage("/44.png");
				SP_44 = new Sprite(img,img.getWidth(),img.getHeight()/2);
				}
				code.LoadStrings2(0, code.FILE_NAME_STRING3,3);
				TempStr2 = code.s_commonStrings2;
				code.FreeStr();		
				��ֵ2UD = 0;
				break;
			case DEPOT:
	//			img_Shop3 = Image.createImage("/Shop_3.png");
	//			img_Shop4 = Image.createImage("/Shop_4.png");
	
				break;
			case MyInfo:
				img_VIP = null;
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void UseGoods(String ID)//ʹ�õ���
	{
		boolean temp = false;
		if(ID.equals("0000"))
		{
			for (int i = 0; i < DepotNum.length; i++) 
			{
				if(DepotNum[i][0].equals("0002"))
				{
					Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "USE");
					h.put("UID", ""+userID);	
					h.put("OID", ""+UseGoodsOid);	
					SetWait("���Ժ�");
					Connection(h);
					return;
				}
			}
			if(!temp)
			{
				SetClue("�Բ���,��û����Կ��", "", "�ر�");
			}
		}
		else if(ID.equals("0002"))
		{
			for (int i = 0; i < DepotNum.length; i++) 
			{
				if(DepotNum[i][0] .equals("0000"))
				{
					Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "USE");
					h.put("UID", ""+userID);	
					h.put("OID", ""+UseGoodsOid);	
					SetWait("���Ժ�");
					Connection(h);
					return;
				}
			}
			if(!temp)
			{
				SetClue("�Բ���,��û��������", "", "�ر�");
			}
		}
		else if(ID.equals("0003"))
		{
			for (int i = 0; i < DepotNum.length; i++) 
			{
				if(DepotNum[i][0] == "0001")
				{
					return;
				}
			}
			if(!temp)
			{
				SetClue("�Բ���,��û�н���", "", "�ر�");
			}
		}
		else if(ID.equals("0004"))//����
		{
			Chat c = new Chat("����������","",11,TextField.ANY,MID,this);
			c.ShowChat();
		}
		else if(ID.equals("0200")||ID.equals("0201")||ID.equals("0202")
				||ID.equals("0203")||ID.equals("0204")|| ID.equals("2001"))
		{
			Hashtable h = new Hashtable();
			h.put("Type", "SYNC");
			h.put("Cmd", "USE");
			h.put("UID", ""+userID);	
			h.put("OID", ""+UseGoodsOid);	
			SetWait("���Ժ�");
			Connection(h);
		}
		else if(ID.equals("0202"))
		{
			
		}
		else if(ID.equals("0204"))
		{
			
		}
	}
	public void readRms()  //��ȡ
	{
		try {
			rms = RecordStore.openRecordStore("DDZ", true);
			if(rms.getNumRecords()>0)
			{
				byte data2[] = rms.getRecord(1);
				SetIndex2[0] = data2[0];
				SetIndex2[1] = data2[1];
				SetIndex2[2] = data2[2];
				String[] s = new String[2];
				GetStringsFromBytes(data2, s);
				user = s[0];
				PassWord = s[1];
				rms.closeRecordStore();
			}
		} catch (Exception e) {
			System.out.println(e);
		} 
		
	}
	public void writeRms()     //д��
	{
		byte data2[] = new byte[3+24+1];
			try {
				rms = RecordStore.openRecordStore("DDZ", true);
				data2[0] = SetIndex2[0];
				data2[1] = SetIndex2[1];
				data2[2] = SetIndex2[2];
				String[] s = new String[2];
				s[0] = user;
				s[1] = PassWord;

				SaveStringsToBytes(data2,s);
				
                if (rms.getNumRecords() == 0)
                {
                	rms.addRecord(data2, 0, data2.length);
                }
                else
                {
                	rms.setRecord(1, data2, 0, data2.length);
                }

			//	rms.addRecord(data2, 0, data2.length);
				rms.closeRecordStore();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
    public void SaveStringsToBytes(byte[] byteBuf, String[] string)
    {
        int arraylen = string.length;
        for (int i = 0; i < arraylen; i++)
        {
            byteBuf[3 + i * Name_PassWordLen] = (byte) string[i].length();
            System.arraycopy(string[i].getBytes(), 0, byteBuf,
                             3 + i * (Name_PassWordLen) + 1, string[i].length());
        }

    }

    public void GetStringsFromBytes(byte[] byteBuf, String[] string)
    {
        byte[] strbytes = new byte[12];
        int len = 0;
        for (int i = 0; i < string.length; i++)
        {
            len = byteBuf[3 + i * (Name_PassWordLen)];
            System.arraycopy(byteBuf, 3 + i * (Name_PassWordLen) + 1, strbytes, 0,
            		Name_PassWordLen);
            string[i] = new String(strbytes, 0, len);
        }

    }
    public void Ч��()
    {
    	switch (MusicOpen) 
    	{
			case 0:
				try{
		    	     Display.getDisplay(MID).vibrate(1000);
		    	   }
		    	   catch (Exception ex) {
		
		    	   }
				break;
	
			case 1:
				music.play(1);

				break;
				
			default:
				break;
		}
    }
   
    public void Connection(Hashtable h)
    {
    	new XConnection(WAP_NET,MID,h,this);
    	KCTime =  1;
    }
	public void run() {
		long starttime = 0;
		long endtime = 0;
		try {
		while (true) 
		{
			starttime = System.currentTimeMillis()+SleepTime;

			if(KCTime%KCStartTime==0 )
			{
				KCTime = 1;
				KeepConnection();
			}
			KCTime++;
			repaint(0,0,SW,SH);
			serviceRepaints();
			endtime = System.currentTimeMillis();
			if(endtime - starttime<SleepTime)
			Thread.sleep(SleepTime);
//            while (System.currentTimeMillis() <= starttime)
//            {
//                Thread.yield();
//            }			
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void update(Observable o, Object arg) 
	{
//		if(arg==null)
//			return;
		cutLine = arg.toString();
			
		String type = cutLineFromContent();//�õ���ͷ����
//		byte type = Byte.parseByte(vcutLineFromContent());//�õ���ͷ����
    //  System.out.println("��ͷ"+type);
	//	System.out.println("�ҵ�״̬"+STATE);
		if(STATE != Game)
		{

		if(type.equals("QUICK"))
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1://ע��ɹ�
					userID = cutLineFromContent();
					user = cutLineFromContent();
					nickname = cutLineFromContent();
					PassWord = cutLineFromContent();
					if(cutLineFromContent().equals("false"))
						sex = "��";
					else
						sex = "Ů";
				
			//		WAIT = false;
			//		SwitchState(LOGIN);
					SetClue("��ϲ��ע��ɹ�!"+"\n�ʺţ�"
							+user+"\n�ǳƣ�"+nickname+"\n���룺"+PassWord+"\n�Ա�"+sex, Login2, "�ر�");
					break;
				case 2://ע�᲻�ɹ�
					SetClue("ע��ʧ��", "", "�ر�");
					WAIT = false;
					break;
			}
		}
		else if(type.equals("LOGIN"))//��½
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1://��½�ɹ�
					
					String a = cutLineFromContent();
					if(a.equals(Version))
					{
						userID = cutLineFromContent();
						Myinfo = new String[7];
						Myinfo[0] = user;
						for (int i = 1; i < Myinfo.length; i++)//����Ҹ�������
						{
							if(i==2)
							{
								Myinfo[i] = PassWord;
							}
							else
								Myinfo[i] = cutLineFromContent();
						}		
						SwitchState(PIAZZA,true);
						if(BOOLremember)
							writeRms();
					}
					else
					{
						SetClue("�汾����,��������°汾", "", "�ر�");
					}
					
					break;
	
				case 2://�û���������
					SetClue("�û��������ڻ����������", "", "�ر�");
					break;
					
				case 3://�������
				//	SetClue("�û���������", "", "�ر�");
					SetClue("�û��Ѿ���¼", "", "�ر�");
					break;
					
				case 4://�û��Ѿ���¼
					SetClue("�û��Ѿ���¼", "", "�ر�");
				//	WAIT = true;
					break;
			}
		}
		else if(type.equals("SCENES"))//�������
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1://�ɹ�����ȡ��Ϣ�ɹ�
					boolean b = true;
					ScenesLength = Integer.parseInt(cutLineFromContent());
					ScenesTypeLen = new int[ScenesLength];
					for (int i = 0; i < ScenesTypeLen.length; i++) 
					{
						ScenesTypeLen[i] = Integer.parseInt(cutLineFromContent());
					}
					
					DeskInfo = null;
					if(SP_piazza!=null)//��չ㳡��Դ
						FreeRes(PIAZZA);
					if(SaveState[SaveStateIndex-1] == SCENES)//������ڷ����б��ص�����
					{
						SaveStateIndex--;
						b = false;
					}
					SwitchState(SCENES,b);
					break;
					
				case 2:///���볡ʧ��
					SetClue("�������ʧ��", "", "�ر�");
					break;
				
			}
		}
		else if(type.equals("SCENE"))//���뷿���б�
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1://�ɹ���ȡ������Ϣ
					DeskNum = 0;
					room = Integer.parseInt(cutLineFromContent());
					ThisPlayer = new int[room];
					for (int i = 0; i < ThisPlayer.length; i++) 
					{
						ThisPlayer[i] = Integer.parseInt(cutLineFromContent());
					}
					boolean b = true;
					if(SaveState[SaveStateIndex-1] == RommList)//������ڷ����б��ص�����
					{
						SaveStateIndex--;
						b = false;
					}
					SwitchState(RommList,b);
					break;
	
				case 2://��ȡ����Ϣʧ��
					ReturnState();
				//	SwitchState(LastSTATE);
					SetClue("��ȡ������Ϣʧ��", "", "�ر�");
					break;
				case 3:///���볡ʧ��
					SetClue("������������,��ΪVIP������ʱ�������˷���", "��ΪVIP", "�ر�");
					break;
			}
			WAIT = false;
		}
		else if(type.equals("ROOM"))//���������Ӵ���
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1://�ɹ���ȡ���Ӵ�����Ϣ
					boolean b = true;
					if(SaveState[SaveStateIndex-1] == DeskHALL)//������ڷ����б��ص�����
					{
						SaveStateIndex--;
						b = false;
					}
					SwitchState(DeskHALL,b);
					DeskAmount = Integer.parseInt(cutLineFromContent());
					DeskNum = Integer.parseInt(cutLineFromContent());//��õ�ǰ������
					String tempStr="";
					//��ǰ�����ж��ٸ�����
					if(cutLine.equals(""))
						return;
					if(DeskInfo==null)
						DeskInfo = new int[DeskAmount][DeskAmount*3][5];
					for (int i = 0; i < DeskInfo.length; i++) {
						for (int j = 0; j < DeskInfo[i].length; j++) {
							for (int k = 0; k < DeskInfo[i][j].length; k++) {
								DeskInfo[i][j][k] = -1;
							}
						}
					}
					int λ�� = -1;
					for (int i = 0; i < DeskAmount; i++) 
					{
						if(tempStr.equals(""))
						{
							tempStr = cutLineFromContent();
						}
						if(cutLine.equals(""))
						{
							return;
						}
						
						if(tempStr.length()>=4 && 
								Integer.parseInt(tempStr.substring(2, 4))==i+1)//����Ƿ�������ID
						{
							for (int j = 0; j < 3; j++) 
							{
								if(λ��==-1)
									λ�� = Integer.parseInt(cutLineFromContent());
								if(λ��==j)//���˵���
								{
									DeskInfo[i][j][0] = λ��;//λ��
									DeskInfo[i][j][1] = Integer.parseInt(cutLineFromContent());//��
									DeskInfo[i][j][2] = Integer.parseInt(cutLineFromContent());//READY
									DeskInfo[i][j][3] = Integer.parseInt(cutLineFromContent());//ͷ��ID
									DeskInfo[i][j][4] = Integer.parseInt(cutLineFromContent());//�Ƿ�VIP
							//		System.out.println(tempStr+"����"+λ��+"λ��"+"���Ա�Ϊ"+DeskInfo[i][j][1]+"��"+"�Ƿ�׼��:"+DeskInfo[i][j][2]);
									λ�� = -1;
								}
//								
								if(cutLine.indexOf('|')>=4)
								{
									//�µ�����
									tempStr = "";
									j=4;
								}

								if(cutLine.equals(""))
									return;
							}
						}

					}

					break;
	
				case 2://��ȡ���Ӵ�����Ϣʧ��
					SetClue("��ȡ���Ӵ�����Ϣʧ��", "", "�ر�");
					WAIT = false;
					break;
					
				case 3:
					SetClue("������,�뻻������������", "", "�ر�");
					WAIT = false;
					break;
			}
		}
		else if(type.equals("DESK"))//����������Ϣ
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1://����������
					int seat = -1;//��λ
					int Num = Integer.parseInt(cutLineFromContent());//����������
					if(SitDeskInfo == null)
						SitDeskInfo = new String[3][9];
					for (int i = 0; i < SitDeskInfo.length; i++) {
						for (int j = 0; j < SitDeskInfo[i].length; j++) {
							SitDeskInfo[i][j] = "-1";
						}
					}
					for (int i = 0; i < Num; i++) 
					{
//						if(seat == -1)
//						{
							seat = Integer.parseInt(cutLineFromContent());
							SitDeskInfo[seat][0] = ""+seat;//��λ
							SitDeskInfo[seat][4] = cutLineFromContent();//ID
							SitDeskInfo[seat][5] = cutLineFromContent();//�ǳ�
							SitDeskInfo[seat][1] = cutLineFromContent();//����
				 			SitDeskInfo[seat][3] = cutLineFromContent();//�Ա�
							SitDeskInfo[seat][6] = cutLineFromContent();//�Ƿ�׼����ʼ
							SitDeskInfo[seat][2] = cutLineFromContent();//����
							SitDeskInfo[seat][7] = cutLineFromContent();//ͷ��ID
							SitDeskInfo[seat][8] = cutLineFromContent();//�Ƿ�VIP
							if(user.equals(SitDeskInfo[i][4]))
							{
								DeskMyPos = i;
								DeskInfoIndex = DeskMyPos;
							}
//						}
					}
					SwitchState(Deskinfo,true);
					break;
	
					
				case 2:
					SetClue("��ȡ������Ϣʧ��", "", "�ر�");
					break;
					
				case 3:
					SetClue("������,�뻻����������", "", "�ر�");
					break;
			}
		}
		else if(type.equals("SIT"))
		{
			if(STATE == DeskHALL)//����ڷ���
			{		
				if(DeskInfo==null)
				{
					DeskInfo = new int[DeskAmount][DeskAmount*3][5];
					for (int i = 0; i < DeskInfo.length; i++) 
						for (int j = 0; j < DeskInfo[i].length; j++) 
							for (int k = 0; k < DeskInfo[i][j].length; k++)
								DeskInfo[i][j][k] = -1;
				}
				
				switch (Integer.parseInt(cutLineFromContent())) 
				{
					case 1://����
						int DESKID = Integer.parseInt(cutLineFromContent()); 
						if(DESKID<1000)
							return;
						DESKID -=(SceneID*1000+RoomID*100);
						int DESKPosition =  Integer.parseInt(cutLineFromContent());
						
						DeskInfo[DESKID-1][DESKPosition][0] = DESKPosition;//λ��
						DeskInfo[DESKID-1][DESKPosition][1] = Integer.parseInt(cutLineFromContent());//�Ա�)
						DeskInfo[DESKID-1][DESKPosition][2] = Integer.parseInt(cutLineFromContent());//�Ƿ�׼��)
						DeskInfo[DESKID-1][DESKPosition][3] = Integer.parseInt(cutLineFromContent());//ͷ��ID)
						DeskInfo[DESKID-1][DESKPosition][4] = Integer.parseInt(cutLineFromContent());//�Ƿ�VIP
				//		System.out.println(DESKID+"������"+DESKPosition+"λ����������");
						break;
				}
			}
			else if(STATE == Deskinfo)//���������
			{
				if(card!=null)
					card.WinPos = -1;
				switch (Integer.parseInt(cutLineFromContent())) 
				{
					case 1://����
						int seat = -1;
						String temp = cutLineFromContent();//����������Ϣ
						if(temp.length()>=4)
							return;
					//		seat = Integer.parseInt(cutLineFromContent());
						else
							seat = Integer.parseInt(temp) ;//��λ
						SitDeskInfo[seat][0] = ""+seat;//��λ
						SitDeskInfo[seat][4] = cutLineFromContent();//ID
						SitDeskInfo[seat][5] = cutLineFromContent();//�ǳ�
						SitDeskInfo[seat][1] = cutLineFromContent();//����
			 			SitDeskInfo[seat][3] = cutLineFromContent();//�Ա�
						SitDeskInfo[seat][6] = cutLineFromContent();//�Ƿ�׼����ʼ
						SitDeskInfo[seat][2] = cutLineFromContent();//����
						SitDeskInfo[seat][7] = cutLineFromContent();//ͷ��ID
						SitDeskInfo[seat][8] = cutLineFromContent();//�Ƿ�VIP
						
						
//						SitDeskInfo[seat][0] = ""+seat;//��λ
//						SitDeskInfo[seat][4] = cutLineFromContent();//�û���
//						SitDeskInfo[seat][5] = cutLineFromContent();//�ǳ�
//						SitDeskInfo[seat][1] = cutLineFromContent();//����
//						SitDeskInfo[seat][3] = cutLineFromContent();//�Ա�
//						
//						SitDeskInfo[seat][6] = cutLineFromContent();//�Ƿ�׼����ʼ
//						SitDeskInfo[seat][2] = cutLineFromContent();//����
//						SitDeskInfo[seat][7] = cutLineFromContent();//ͷ��ID
						break;
				}
			}
		}
		else if(type.equals("LEFT"))
		{
			cutLineFromContent();
			if(STATE == DeskHALL)//��������Ӵ���
			{		
				int DESKID = Integer.parseInt(cutLineFromContent()) ;
				if(DESKID<1000)
					return;
				int seat = Integer.parseInt(cutLineFromContent()) ;//��λ

				DESKID -= (SceneID*1000+RoomID*100)+1;
				DeskInfo[DESKID][seat][0] = -1;
	//			System.out.println(DESKID+"������"+seat+"λ�������뿪��");
			}
			else  //�������������
			{
				if(card!=null)
					card.WinPos = -1;//���Ǽ�����Ϸ,�µ�һ��
				String temp = cutLineFromContent();//����������Ϣ
				if(temp.length()>=4 || temp.equals("-1"))
					return;
				int seat = Integer.parseInt(temp) ;//��λ
				SitDeskInfo[seat][0] = "-1";//��λ
				SitDeskInfo[seat][1] = "-1";//��λ
				SitDeskInfo[seat][2] = "-1";//��λ
				SitDeskInfo[seat][3] = "-1";//��λ
				SitDeskInfo[seat][4] = "-1";//��λ
				
			}
		}
		else if(type.equals("READY"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
				case 1:
					WAIT = false;
					Ready = true;
					SitDeskInfo[DeskMyPos][6] = "1";//�Ƿ�׼����ʼ
					break;
				case 3:
					if(DeskInfo==null)
					{
						DeskInfo = new int[DeskAmount][DeskAmount*3][4];
						for (int i = 0; i < DeskInfo.length; i++) 
							for (int j = 0; j < DeskInfo[i].length; j++) 
								for (int k = 0; k < DeskInfo[i][j].length; k++)
									DeskInfo[i][j][k] = -1;
					}
					if(STATE == DeskHALL)//����ڷ���
					{		
						int DESKID = Integer.parseInt(cutLineFromContent()) ;
						int seat = Integer.parseInt(cutLineFromContent()) ;//��λ						
						DESKID -= (SceneID*1000+RoomID*100)+1;//ת��Ϊ������ID
			//			System.out.println(DESKID+"��̨"+seat+"λ����"+"����׼����");
						DeskInfo[DESKID][seat][2] = 1;
					}
					else
					{
						int seat = Integer.parseInt(cutLineFromContent()) ;//��λ
						SitDeskInfo[seat][6] = "1";//�Ƿ�׼����ʼ
					}
					WAIT = false;
					break;
					
				case 4:
					WAIT = false;
					break;
			}

		}
		else if(type.equals("UNREADY"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
				case 1://ȡ���ɹ�
					WAIT = false;
					Ready = false;
					SitDeskInfo[DeskMyPos][6] = "0";//�Ƿ�׼����ʼ
					break;
					
				case 3:
					if(STATE == DeskHALL)//����ڷ���
					{		
						int DESKID = Integer.parseInt(cutLineFromContent()) ;
						int seat = Integer.parseInt(cutLineFromContent()) ;//��λ
						DESKID -= (SceneID*1000+RoomID*100)+1;//ת��Ϊ������ID
						System.out.println(DESKID+"��̨"+seat+"λ����"+"����ȡ����");
						DeskInfo[DESKID][seat][2] = 0;
					}
					else
					{
						int seat = Integer.parseInt(cutLineFromContent()) ;//��λ
						SitDeskInfo[seat][6] = "0";//�Ƿ�׼����ʼ
					}
					WAIT = false;
					break;
					
				case 4:
					SetClue("ȡ��ʧ��", "", "�ر�");
					break;
			}
		}
		else if(type.equals("IN"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
				case 3:
					DeskNum ++;
					if(!cutLine.equals(""))
					{
					String s = cutLineFromContent();
					int Pot = Integer.parseInt(s.substring(0, 1))-1;
					int nuber = Integer.parseInt(s.substring(1, 2));
					if(ThisPlayer!=null)
						ThisPlayer[Pot] += nuber;
					}
					break;
					
			}
		}
		else if(type.equals("OUT"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
				case 3:
					DeskNum --;
					break;
					
			}
		}
		else if(type.equals("INFO"))
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1:
					SetClue("�޸ĳɹ�", "", "�ر�");
					Myinfo = tempMyinfo;
					break;
				case 2:
					SetClue("ʧ��", "", "�ر�");
					break;
				case 3:
					SetClue("�޸�ʧ��", "", "�ر�");
					break;
			}
		}
		else if(type.equals("DEAL"))
		{
			FreeRes(MenuOption);
			SwitchState(LOAD,false);
			LoadState = Game;
			if(card==null)
				card = new Card(SW,SH,this);
			card.Analyse(type,cutLine);
			Ready = false;
		}
		else if(type.equals("DROP"))//����
		{
			try {
				
			
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 3:
					if(STATE == DeskHALL)//����ڷ���
					{	
						if(cutLine!="")
						{
							if(DeskInfo!=null)
							{
							int DESKID = Integer.parseInt(cutLineFromContent());
							DESKID -= (SceneID*1000+RoomID*100)+1;
							int seat = Integer.parseInt(cutLineFromContent()) ;//��λ
							DeskInfo[DESKID][seat][0] = -1;
							}
						}
					}
					else
					{
						if(cutLine!="" && DeskInfo!=null)
						{
							int seat = -1;
							String temp = cutLineFromContent();
							if(temp.length()>=4)
								seat = Integer.parseInt(cutLineFromContent()) ;//��λ
							else
								seat = Integer.parseInt(temp) ;//��λ
							if(SitDeskInfo!=null)
							SitDeskInfo[seat][0] = "-1";
						}
					}
					DeskNum--;
					break;
	
				default:
					break;
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(type.equals("ITEMS"))//�ֿ�
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1:
					int NUM = Integer.parseInt(cutLineFromContent());
					DepotCount = 0;
					DepotNum = new String[NUM][7];
					for (int i = 0; i < DepotNum.length; i++) 
					{
						if(!cutLine.equals(""))//�жϻ���û����Ʒ
						{
							DepotNum[i][0] = cutLineFromContent();
							DepotNum[i][3] = cutLineFromContent();
							DepotNum[i][4] = cutLineFromContent();
							cutLineFromContent();
							cutLineFromContent();
							DepotCount++;
						}
						else
							break;
					}
					SetWait("���ڼ�����Ϸ��Դ");
					break;
	
				case 2:
					SetClue("��ȡ��Ʒ��Ϣʧ��", "", "�ر�");
			//		SetWait("���ڼ�����Ϸ��Դ");
					break;
					
				case 3://û����Ʒ
//					DepotNum = new String[2][2];
//					DepotCount = 2;
//					DepotNum[0][0] = "0001";
//					DepotNum[1][0] = "0201";
//					DepotNum[0][1] = "100";
//					DepotNum[1][1] = "100";
//					SetWait("���ڼ�����Ϸ��Դ");
					WAIT = false;
					break;
			}
		}
		else if(type.equals("USE"))
		{
			String temp="";
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1:
					temp = cutLineFromContent();
					String name = "";
					if(temp.equals("0000")||temp.equals("0001")||temp.equals("0002")
							||temp.equals("0003"))
					{
						for (int i = 0; i < GoodsInfo.length; i++) {
							if(GoodsInfo[i][0].equals(temp))
								name = GoodsInfo[i][1];
						}
						SetClue("�����ˡ�"+name+"������", "", ReturnDepot);
					}
					else if(temp.equals("0100"))
					{
				//		sfd
						SetClue("ʹ�óɹ�", "", ReturnDepot);
					}
					else if(temp.equals("0200")||temp.equals("0201")||temp.equals("0202")||temp.equals("0203")
							||temp.equals("0204")||temp.equals("2001")||temp.equals("0004"))
					{
						
						SetClue("ʹ�óɹ�", "", ReturnDepot);
					}
					break;
	
				case 2:
					
					break;
				case 3:
					temp = cutLineFromContent();
					if(temp.equals("0004"))
					{
						Affiche+=cutLineFromContent()+"|";
					}
					WAIT = false;
					break;
				case 4:
					SetClue("ʹ�õ���ʧ��", "", "�ر�");
					break;
			}
		}
		else if(type.equals("KICK"))
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 0://
					
					break;
	
				case 1://KICK�ɹ�
					SitDeskInfo[DeskInfoIndex][0] = "-1";
					WAIT = false;
				//	SetClue("�Բ���,�㲻��VIP����ʹ�����˹���", "����VIP", "�ر�");
					break;
				case 2:
					SetClue("�Բ���,�㲻��VIP����ʹ�����˹���", Const.BuyVip, "�ر�");
					break;
				case 4://���˼�����

					
					if(SP_menuBG==null)//����Ϸ���ش�����ȡ�˵���Դ
					{
						FreeRes(Game);//�ͷ���Ϸ������Դ
						SwitchState(LOAD,false);
						LoadState = MenuOption;
					}
					SetClue("�㱻XX�����̨", "", "ȷ��");
					Option = false;
					Time_EnterDesk = 100;
//					SwitchState(DeskHALL, true);
//					Connection();
					Ready = false;
					DeskInfo=null;
					break;
				case 5:
					int KickPos = Integer.parseInt(cutLineFromContent());
					SitDeskInfo[KickPos][0] = "-1" ;
					break;
			}
		}
		else if(type.equals("QGAME"))
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			
			{
				case 1:
					int seat = -1;//��λ
					userID = cutLineFromContent();//��ȡ��½��USERID
					DeskID = Integer.parseInt(cutLineFromContent());
					SceneID = DeskID/1000;
					RoomID = (DeskID-SceneID*1000)/100;
					
					int Num = Integer.parseInt(cutLineFromContent());//����������
					if(SitDeskInfo == null)
						SitDeskInfo = new String[3][9];
					for (int i = 0; i < SitDeskInfo.length; i++) {
						for (int j = 0; j < SitDeskInfo[i].length; j++) {
							SitDeskInfo[i][j] = "-1";
						}
					}
					
					
					for (int i = 0; i < Num; i++) 
					{

						seat = Integer.parseInt(cutLineFromContent());
						SitDeskInfo[seat][0] = ""+seat;//��λ
						SitDeskInfo[seat][4] = cutLineFromContent();//�û���
						SitDeskInfo[seat][5] = cutLineFromContent();//�ǳ�
						SitDeskInfo[seat][1] = cutLineFromContent();//����
						SitDeskInfo[seat][3] = cutLineFromContent();//�Ա�
						SitDeskInfo[seat][6] = cutLineFromContent();//�Ƿ�׼����ʼ
						SitDeskInfo[seat][2] = cutLineFromContent();//����
						SitDeskInfo[seat][7] = cutLineFromContent();//ͷ��ID
							if(user.equals(SitDeskInfo[i][4]))
							{
								DeskMyPos = i;
								DeskInfoIndex = DeskMyPos;
								Myinfo = new String[7];
								Myinfo[0] = user;
								Myinfo[1] = SitDeskInfo[seat][5];
								Myinfo[2] = PassWord;
								Myinfo[3] = SitDeskInfo[seat][3];
								Myinfo[4] = SitDeskInfo[seat][1];
								Myinfo[5] = SitDeskInfo[seat][7];
								Myinfo[6] = "0";	//����Ҫ�����������Ƿ�VIP��Ϣ	
							}
					}
					
					
					

			//		DeskMyPos = seat;
					
					
					SwitchState(PIAZZA,true);
					if(BOOLremember)
						writeRms();
					
					DeskInfoIndex = seat;
					SwitchState(Deskinfo,true);
					break;
					
				case 2:
					SetClue("��½ʧ��", "", "�ر�");
					break;
			}
		}
		else if(type.equals("BUY"))//������Ʒ
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1:
					SetClue("����ɹ�,�뵽�ֿ����", "�֡�����", "����");
					break;
				case 2 :
				case 4 :
					SetClue("����ʧ��", "", "����");
					break;
					
				case 3:
					SetClue("��Ҳ���", "��ֵ����", "����");
					break;
					
			}
			OpenBuy = false;
		}
		else if(type.equals("QUIT"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
				case 1:
		//			P.SitDeskInfo[MyPos][1] = ""+(Integer.parseInt(P.SitDeskInfo[MyPos][1]) - Score);
//					h.clear();
//					h.put("Type", "SYNC");
//					h.put("Cmd", "DESK");
//					h.put("UID", ""+userID);
//					h.put("Desk-ID", ""+ DeskID);
//					SetWait("��ȡ��������");
//					CLUE = false;
					SwitchState(Deskinfo, false);
					break;
			}
		}
		}
		else//��Ϸ�������
		{
			card.Analyse(type,cutLine);
		}
	}
	public void KeepConnection()
	{
		switch (STATE) 
		{
			case SCENES:
			case RommList:
			case DeskHALL://�����Ӵ���
			case Deskinfo:
			case Game:
			case SHOP:
			case SHOP1:
			case DEPOT:
			case MyInfo:
			case PIAZZA:
			case ��Ϸ:
				if(WAIT == false)
				{
					Hashtable h = new Hashtable();
					h.put("Type", "ASYNC");
					h.put("Cmd", "BEAT");
					h.put("UID", ""+userID);
					Connection(h);
				}
				break;
		}
	}
	public String cutLineFromContent() {

		String result;
		int index = cutLine.indexOf('|');
		if (index == -1) {
			result = cutLine;
			cutLine = "";
//			System.out.println("����  "+result);
			return result;
		} else {
			result = cutLine.substring(0, index);
			cutLine = cutLine.substring(index+1);
	//		System.out.println("����  "+result);
			return result;
		}
	}
	public String cutAffiche() {

		String result;
		int index = Affiche.indexOf('|');
		if (index == -1) {
			result = Affiche;
			Affiche = "";
			return result;
		} else {
			result = Affiche.substring(0, index);
			Affiche = Affiche.substring(index+1);
			return result;
		}
	}
	public void commandAction(Command arg0, Displayable arg1) {
		// TODO Auto-generated method stub
		
	}
	private void showMainMenu()
	{
		try{			
			Page p = b.loadPage("file://index.html",HttpConnection.GET,null,null); // this will load the first screen.
			Container c = p.getPageContainer(); // the page is rendered in a container
			// create a panel for the container
			Panel mainPanel = new Panel(c,Panel.VERTICAL_SCROLLBAR|Panel.HORIZONTAL_SCROLLBAR,true);
			mainPanel.setCommandListener(this);
			mainPanel.setRightSoftKeyCommand(consoleCmd);
			mainPanel.setLeftSoftKeyCommand(menuCmd);
			mainPanel.setKeyListener(this);
			fireScreen.setCurrent(mainPanel);
		}catch(Exception e) // failed to load main menu.
		{
			// show console
			console.showConsole();
			// log the event.
			Log.logError("Failed to load main page.",e);
		}
	}
	
	/**
	 * This method creates a Panel that will apear as a popup menu for the application. 
	 */
	private void showPopupMenu()
	{
		Font f = FireScreen.getTheme().getFontProperty("font");
		Container menuCnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));

		// first calculate a nice size for the popup menu.
		int menuW = f.stringWidth("Longest Item"); // max allowed popup width 
		
		
		// Notice the Lang.get() calls. Lang class will return the correct language for the given screen. 
		// The language bundle can be set using the Lang.setBundle method. Check the Lang javadoc for more.
		TextComponent cmp = new TextComponent(Lang.get("����̨"),menuW,3);
		cmp.setCommand(consoleCmd);
		cmp.setHightlightMode(TextComponent.HIGHLIGHT_MODE_FULL); // set the highlight effect to look like a list item rather than a link 
		cmp.setCommandListener(this); 
		cmp.setFont(f);
		menuCnt.add(cmp);

		cmp = new TextComponent(Lang.get("Start page"),menuW,3);
		cmp.setCommand(mainCmd);
		cmp.setCommandListener(this);
		cmp.setHightlightMode(TextComponent.HIGHLIGHT_MODE_FULL);
		cmp.setFont(f);
		//menuCnt.add(cmp);

		cmp = new TextComponent(Lang.get("Other page"),menuW,3);
		// this menu item will load the other.html file. The browser will handle it as a link command if the URL field is set with a url.
		// NOTE: Using such a command to move from one page to the other with the default browser behaivior will not close the popup.
		// The page will be loaded o ZINDEX 0. If we want to close the popup we can either use commands like the consoleCmd above or 
		// we can overide the browser specific behaivior on the listener and check for open popups before loading the page.
		cmp.setCommand(new gr.fire.browser.util.Command("This is a url link",Command.OK,1,"file://other.html"));
		cmp.setHightlightMode(TextComponent.HIGHLIGHT_MODE_FULL);
		cmp.setFont(f);
		cmp.setCommandListener(this);
		//menuCnt.add(cmp);

		cmp = new TextComponent(Lang.get("Help"),menuW,3);
		cmp.setCommand(new gr.fire.browser.util.Command("Another url link",Command.OK,1,"file://help.html"));
		cmp.setHightlightMode(TextComponent.HIGHLIGHT_MODE_FULL);
		cmp.setFont(f);
		cmp.setCommandListener(this);
		//menuCnt.add(cmp);


		TextComponent exitCmp = new TextComponent(Lang.get("����"),menuW,3);
		exitCmp.setFont(f);
		exitCmp.setHightlightMode(TextComponent.HIGHLIGHT_MODE_FULL);
		exitCmp.setCommand(exitCmd);
		exitCmp.setCommandListener(this);
		menuCnt.add(exitCmp);

		int height = fireScreen.getHeight();
		int menuH = ((f.getHeight() +2 ) * menuCnt.countComponents());
		if(menuH>(height*3)/4) menuH=(3*height)/4; // max allowed popup height


		// ok the container is prepared and filled with memu items now create a Panel for it. That way the menu will also have scrollbars.
		// if you dont need scrollbars, you can just use the Container.
		Panel popup = new Panel(menuCnt, Panel.VERTICAL_SCROLLBAR, false);
		popup.setLeftSoftKeyCommand(closeMenuCmd); // when the popup is open, the right softkey will close it.
		popup.setCommandListener(this);
		popup.setShowBackground(true); // display the theme specific background behind the container. 
		popup.setBorder(true); 
		popup.setPrefSize(menuW,menuH); 
		// locate the Panel on the screen
		// add the component to the firescreen on zinex 2. Since the main panel is on zindex 0 this will add the panel above the main panel.
		fireScreen.showPopupOnLeftSoftkey(popup,2);
		// The above is equivalent to :
		// fireScreen.showPopupOnComponent(popup,fireScreen.getComponent(FireScreen.LEFT_SOFTKEY_ZINDEX), 2);
	}
	
	
	public void commandAction(javax.microedition.lcdui.Command cmd, Component c)
	{
		if(cmd==exitCmd)
		{
			disp.setCurrent(this);
//			fireScreen.removeComponent(2); 
//			MID.notifyDestroyed();
			return;
		}
		if(cmd==mainCmd)
		{
			fireScreen.removeComponent(2);
			showMainMenu();
			return;
		}
		if(cmd==consoleCmd)
		{
			fireScreen.removeComponent(2); 
			console.showConsole();
			return;
		}
		if(cmd==closeMenuCmd)
		{
			fireScreen.removeComponent(2); 
			return;
		}
		if(cmd==menuCmd)
		{
			showPopupMenu();
			return;
		}
		if(cmd==cancelCmd)
		{
			b.cancel();
			return;
		}
		if (c instanceof InputComponent && ((InputComponent) c).getType() == InputComponent.SUBMIT)
		{
			((gr.fire.browser.util.Command)cmd).getForm().submit((InputComponent) c);
			return;
		}
		
		fireScreen.getCurrent().setRightSoftKeyCommand(cancelCmd);
		fireScreen.getCurrent().setLeftSoftKeyCommand(null);
		
		b.commandAction(cmd,c);
	}


	/**
	 * This method is called by the Browser when a request made with loadPageAsync completes. 
	 */
	public void pageLoadCompleted(String url, String method, Hashtable requestParams, Page page)
	{
		// Use the of the log class
		Log.logInfo("Loading of URL["+url+"] completed.");
		fireScreen.getCurrent().setLeftSoftKeyCommand(menuCmd);
		fireScreen.getCurrent().setRightSoftKeyCommand(consoleCmd);

		b.pageLoadCompleted(url,method,requestParams,page);
	}

	/**
	 * This method is called by the Browser when a request using loadPageAsync failes with an Exception (or Error)
	 * 
	 */
	public void pageLoadFailed(String url, String method, Hashtable requestParams, Throwable error)
	{
		// Use the of the log class
		Log.logError("Loading of URL["+url+"] failed with error.",error);
		fireScreen.getCurrent().setLeftSoftKeyCommand(menuCmd);
		fireScreen.getCurrent().setRightSoftKeyCommand(consoleCmd);
		b.pageLoadFailed(url,method,requestParams,error);
	}
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException
	{
		try{
			// The HttpClient will store the Cookies loaded while communicating with Http server 
			// in memory. The developer can choose to persist this cookies using the HttpClient.storeCookies() method.
			// this method will create a record-store with the given name and serialize the cookies in it.
			// Cookies stored using this method can be later loaded using the HttpClient.loadCookies() method. 
			b.getHttpClient().storeCookies("testingcookies");
			FireScreen.getScreen().destroy(); // notify firescreen that the application will close. 
			// This will also stop the animation thread.
		}catch(IOException e){
			Log.logError("Failed to store cookies",e);
		}
	}

	public void keyReleased(int code, Component src)
	{
		if(code==FireScreen.KEY_STAR)
		{ // switch orientation when the star key is pressed
			int orientation = fireScreen.getOrientation();
			switch(orientation)
			{
			case FireScreen.NORMAL:
				fireScreen.setOrientation(FireScreen.LANDSCAPELEFT);
				break;
			case FireScreen.LANDSCAPELEFT:
				fireScreen.setOrientation(FireScreen.LANDSCAPERIGHT);
				break;
			case FireScreen.LANDSCAPERIGHT:
				fireScreen.setOrientation(FireScreen.NORMAL);
				break;				
			}
		}
	}

	public void keyRepeated(int code, Component src)
	{	
	}

	public void pageLoadProgress(String message, byte state, int percent)
	{
		b.pageLoadProgress(message,state,percent); // use the default implementation for gauge handling.
		
	}

	public void keyPressed(int code, Component src) {
		// TODO Auto-generated method stub
		
	}
}