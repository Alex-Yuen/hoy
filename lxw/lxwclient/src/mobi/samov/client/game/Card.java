package mobi.samov.client.game;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Card 
{
	//---------------------游戏状态--------------------------//
	private final byte BET = 1;//叫分
	private final byte DEAL = 0;//发牌
	private final byte PLAY = 2;//出牌
	private final byte WAITCALL=3;//等待叫分
	private final byte WAITPLAY=4;//等待叫分
	private final byte JUDGE = 5;//判断
	private final byte SHOW = 6;//开牌
	private final byte STAT = 7;//统计
	private final byte WIN = 8;//对方逃跑,..我赢
	boolean playerInfo ;//玩家信息
	private final byte WAIT = 20;
	
	private final byte DeskNum = 6;//桌子上的牌一行画多少张
	
	byte STATE;
	private int SW,SH;
	private Image img_BG ;//游戏背景
	private Image img_colck;//钟
	private Image img_select;//选择
	private Image img_pass;//PASS
	private Image img_bomb,img_bomb2;//炸弹相关
	private Sprite SP_card;//牌
	private Sprite SP_cardNumber;//牌数
	private Sprite SP_cardColor;//花色
	private Sprite SP_hand;//头像
	private Sprite SP_BET;//叫分
	private Sprite SP_AUTO;//自动提示
	private Sprite SP_win_lose;
	private NumImgTools NT_Clock,NT_medium,NT_score;
	private int LeftNum;//左边剩余牌数
	private int RightNum;//右边剩余牌数
	private int Remainder;//我剩余的牌数
	
	private int MyCard[]={52,53,26,39,5,6,7,8,9,10,11,12,13,33,20,6,10,35,12,53};//手上牌数组
	private int signY[];//出牌标记Y坐标
	private int sign[];//出牌标记
	private int LordCard[];//地主的三张牌
	private int SendCard[];//出牌牌数
	private int DeskCard[];//桌面显示牌
	private int signIndex;//出牌标记索引
	private int DeskCardLen;//桌面牌数
	private boolean 是否有出牌;
	private int WHO;//谁出牌
	private int LeftPlaryCard[];//上家手上牌
	private int LeftDeskCard[];//上家打出的牌
//	private int LeftDeskCard[]={0,13,26,39};//上家打出的牌
	private int ThisPlaryCard[];
	private int LeftPlaryPos;//上家位置
	private int RightPlaryPos;//下家位置
	private boolean LeftPass;//左边PASS
	private boolean RightPass;//右边PASS
	private int LeftBet;//左边叫的分
	private int RightBet;//右边叫的分
	private int MyBet;//自己叫的分
	private boolean MyPass;
	private int RightPlaryCard[];//下家手上牌
	private int RightDeskCard[];//下家打出的牌
//	private int myself = 1;//自己
	private int selectIndex;//选择牌索引
	private int ColckTime;//出牌时间
	private int SystemTime;//出牌时间
	private int MyCardType;//牌的类型,炸弹?顺还是葫芦
	private int ThisCardType;//当前要打牌的类型,炸弹?顺还是葫芦
	private int CardNum;//对方出牌数
	private int MyCardMax;//我的牌的类型最大的数5552,最大5
	private int ThisCardMax;//对方出牌最大牌
	private boolean 没有牌比上家大;
	private boolean ALLPASS;
	public int multiple;//倍数
	private int 春天,反春;//春天
	public int Score;//分数
	private int BetIndex;//叫分索引
	private boolean PASS;//右下角出不出牌
	private Platform P;
	private boolean First;//谁出牌
	int WinPos;//赢的人位置
	private int ThisPos;//当前行动人位置
	private int PlayPos;//出牌人位置
	private boolean CLUE;//提示
//	private boolean Option;
	private int OptionIndex;//选项索引
	public boolean Quit; //是否强退
	
	private int ClueTime;
	/**
	 * 玩家信息    0:昵称,1:性别 2位置,３积分,头像ID
	 */
	private String PlayerInfo[][];
	private int MyPos;//我的位置
	private int LordPos;//地主位置
	private int EscapePos;//逃跑者位置
	private int CallScore;//叫的分数
	private String ClueStr;
	private boolean WIN_LOSE;//输赢值
	private int bombY;//炸弹移动坐标
	private String QuitStr="逃跑";//逃跑或掉线信息
	private boolean BOOLbomb;
	private boolean WINS;//谁赢  F = 农民,T=地主
	private int AllScroe[];
	private final int Time = 45; 
	public Card(int SW,int SH,Platform P)
	{
		this.SW = SW;
		this.SH = SH;
		this.P = P;
		
		MyPos = P.DeskMyPos ; 
		WinPos = -1;
		PlayPos = 1;
		
	}
	public void LoadGameRes(int LoadTime)
	{
		Image img;
			switch (LoadTime) 
			{
				case 0:
					if(img_BG == null)
					{
					try {
						img_BG = Image.createImage("/ddz_bg.png");
						img_select = Image.createImage("/select.png");
						img_colck = Image.createImage("/clock.png");
						img_pass = Image.createImage("/pass.png");
						img_bomb = Image.createImage("/bomb.png");
						img_bomb2 = Image.createImage("/bomb2.png");
						img = Image.createImage("/card.png");
						SP_card = new Sprite(img,img.getWidth(),img.getHeight()/4);
						img = Image.createImage("/card_number.png");
						SP_cardNumber = new Sprite(img,img.getWidth(),img.getHeight()/26);
						img = Image.createImage("/number_small.png");
						NT_score = new NumImgTools(img,img.getWidth(),img.getHeight()/12);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					break;
					
				case 5:
					if(SP_cardColor==null)
					{
					try {
						img = Image.createImage("/card_color.png");
						SP_cardColor = new Sprite(img,img.getWidth(),img.getHeight()/4);
						img = Image.createImage("/number_clock.png");
						NT_Clock = new NumImgTools(img,img.getWidth(),img.getHeight()/12);
						img = Image.createImage("/hand.png");
						SP_hand = new Sprite(img,img.getWidth(),img.getHeight()/5);
						img = Image.createImage("/number_medium.png");
						NT_medium = new NumImgTools(img,img.getWidth(),img.getHeight()/12);
						img = Image.createImage("/bet.png");
						SP_BET = new Sprite(img,img.getWidth(),img.getHeight()/4);
						img = Image.createImage("/auto.png");
						SP_AUTO = new Sprite(img,img.getWidth(),img.getHeight()/2);
						img = Image.createImage("/win_lose.png");
						SP_win_lose = new Sprite(img,img.getWidth(),img.getHeight()/2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					}
					break;
				case 8:
					Init();
					break;
			}
	}
	public void Init()
	{
		signY = new int[20];
		AllScroe = new int[3];
		sign = new int[20];
		SendCard = new int[20];
		DeskCard = new int[20];
		LordCard = new int[3];
		PlayerInfo = new String[3][5];
		STATE = WAITCALL;
		
		ThisPlaryCard = LeftDeskCard;
		for (int i = 0; i < PlayerInfo.length; i++) 
		{
			PlayerInfo[i][0]=P.SitDeskInfo[i][5];
			PlayerInfo[i][1]=P.SitDeskInfo[i][3];
			PlayerInfo[i][2]=P.SitDeskInfo[i][0];
			PlayerInfo[i][3]=P.SitDeskInfo[i][1];
			PlayerInfo[i][4]=P.SitDeskInfo[i][7];
			
			int index = -1;
			int tempScore=0;
			index = PlayerInfo[i][3].indexOf("-");
			if(index==-1)
			{
				tempScore = Integer.parseInt(P.SitDeskInfo[i][1]);//总分
			}
			else
			{
				tempScore = Integer.parseInt(P.SitDeskInfo[i][1].substring(1));
			}
			AllScroe[i] = tempScore;
		}
//		for (int i = 0; i < PlayerInfo.length; i++) 
//		{
//			PlayerInfo[i][0]=""+0;
//			PlayerInfo[i][1]=""+1;
//			PlayerInfo[i][2]=""+0;
//			PlayerInfo[i][3]=""+1000;
//		}
		LeftPlaryPos = Integer.parseInt(PlayerInfo[(MyPos+2)%3][2]);
		RightPlaryPos = Integer.parseInt(PlayerInfo[(MyPos+1)%3][2]);
//		System.out.println("我的位置"+MyPos);
//		System.out.println("上家位置"+LeftPlaryPos);
//		System.out.println("下家位置"+RightPlaryPos);
		是否有出牌 = false;
	//	Option = false;
		LeftPass = false;
		MyPass = false;
		ALLPASS = false;
		selectIndex = 0;
		Quit = false;
		RightPass = false;
		BOOLbomb = false;
		WIN_LOSE = false;
		PASS = false;
		playerInfo = false;
		First = true;
		multiple = 1;
		春天 = 1;
		反春 = 1;
		CallScore = 0;
		LeftBet = -1;
		RightBet = -1;
		MyBet = -1;
		MyCardType = -1;
		BetIndex = 0;
		ThisCardType = -1;
		SystemTime = 0;
		ClueTime = 0;
		ColckTime = Time;
		LordPos = -1;
		LeftNum=17;//左边剩余牌数
		RightNum=17;//右边剩余牌数
		Remainder=17;//我剩余的牌数
		if(WinPos!=-1)//不是第一局
		{
			RightDeskCard = null;
			ThisPlaryCard = null;
			LeftDeskCard = null;
			if(MyPos==WinPos)//如果第一局并且我坐第一个位置
			{
				STATE = BET;
			}
			else
				STATE = WAITCALL;
		}
		else //第一局
		{
			if(MyPos==0)//如果第一局并且我坐第一个位置
			{
				STATE = BET;
			}
			else
				STATE = WAITCALL;
		} 
//	    STATE = PLAY;
//		STATE = JUDGE;
//		STATE = WIN;
//		First = false;
		SequenceCard(MyCard,Remainder);
//		PlayRule(ThisPlaryCard,ThisPlaryCard.length,false);
//		AUTO(ThisPlaryCard);
	//	SequenceCard(LeftDeskCard,LeftDeskCard.length);
		System.out.println("Card.Init()");
	}
	
	public void Paint(Graphics g)
	{
		if(!playerInfo)
		{
		g.drawImage(img_BG, 0, 0, 0);
		//画统计
		int x = 0;
		int y = SH-SP_card.getHeight()*2-P.H2-1;
		if(Remainder<11)
			y = SH-SP_card.getHeight()-P.H2-1;
		DrawCard(g,MyCard,Remainder,x,y,true,10);

		if(RightDeskCard!=null)
		{
			int deskNum = RightDeskCard.length-1;
			if(RightDeskCard.length>DeskNum)
				deskNum = DeskNum-1;
			DrawCard(g, RightDeskCard, RightDeskCard.length, 
						(SW-((SP_cardNumber.getWidth()+3)*(deskNum)+SP_card.getWidth()))-1, SP_card.getHeight()+45, false,DeskNum);//画右边桌上的牌
		}
		if(LeftDeskCard!=null)
		{
			DrawCard(g, LeftDeskCard, LeftDeskCard.length, 1, SP_card.getHeight()+45, false,DeskNum);//画左边打在桌上的牌
		}
		if(DeskCard!=null)
		{
			int num = DeskCard.length;
			if(num>8)
				num = 8;
			x = (SW-(num)*SP_cardNumber.getWidth()-SP_card.getWidth())/2;//我桌子上的牌X坐标
			y = SH - P.H2-SP_card.getHeight()*(Remainder/11+1)-SP_card.getHeight()-5-DeskCard.length/8*SP_card.getHeight();
			DrawCard(g, DeskCard, DeskCardLen,x,y,false,8);//画我打在桌上牌
		}
//		if(CLUE)
//		{
//			
//		}
		}
		DrawDeskinfo(g);//画桌子信息
		
//		g.drawImage(img_2, 0, SH-img_2.getHeight(), 0);
		P.Draw2(g, 0, SH-P.H2);
		String temp =  "底分"+CallScore+"倍数"+(multiple);
		g.setColor(0,0,0);
		g.drawString(temp, (SW-P.ft.stringWidth(temp))/2, 
				SH-P.H2+(P.H2-P.FontH)/2, 0);
		
		if(STATE==STAT)
		{
			drawBeautyString(g,"继续",SW-P.ft.stringWidth("继续")-10,SH-P.H2+(P.H2-g.getFont().getHeight())/2);
			return;
		}
		int selectY = 0;
		if(Remainder<11)
		{
			selectY = SP_card.getHeight();
		}
		drawBeautyString(g,"选项",10,SH-P.H2+(P.H2-g.getFont().getHeight())/2);
		g.drawImage(img_select, (selectIndex%10)*(SP_cardNumber.getWidth()+3), 
				SH-SP_card.getHeight()*2-P.H2-img_select.getHeight()+5+(selectIndex/10)*SP_card.getHeight()+selectY,0);
		DrawElse(g);
	}
	private void DrawClue(Graphics g)
	{
		if(ClueTime>0)
		{
			int w = SH/5;//宽的偏移
			int h = P.FontH+P.FontH/2;//框高度
			int x = (SW-P.ft.stringWidth(ClueStr))/2;
			ClueTime--;
			g.setColor(0xfde4e4);
			g.fillRect(x-w/2, (SH-h)/2, P.ft.stringWidth(ClueStr)+w,h);
			g.setColor(0xf88d8d);
			g.drawRect(x-w/2, (SH-h)/2, P.ft.stringWidth(ClueStr)+w,h);
			drawBeautyString(g, ClueStr, x, SH/2+(h-P.FontH)/2-h/2);
		}
	}
	private void DrawElse(Graphics g)//画其他
	{

//		if(Option)
//		{
//	//		String str[] = {"发送消息","玩家资料","强制退出"};
//	//		String str[] = {"玩家资料","强制退出"};
//			P.ui.DrawOption(g,OptionIndex,P.OptionStr);
//		}
		g.setColor(255,255,255);
		int fontY = SH-P.H2+(P.H2-g.getFont().getHeight())/2;
		switch (STATE) 
		{
			case BET:
				for (int i = 0; i < 4; i++) 
				{
					int tempY=0;
					if(BetIndex==i)
						tempY = -5;
					SP_BET.setPosition((SW-(SP_BET.getWidth()+5)*4)/2+i*(SP_BET.getWidth()+5), SH/2-SP_BET.getHeight()+tempY);
					SP_BET.setFrame(i);
					SP_BET.paint(g);
				}
				if(SystemTime%10==0 && ColckTime>0)
				{
					ColckTime--;
					SystemTime = 1;
				}
				SystemTime++;
				if(ColckTime<=0)
				{
					Hashtable h = new Hashtable();
					h.put("Type", "SYNC");
					h.put("Cmd", "BET");
					h.put("UID", ""+P.userID);
					BetIndex = 0;
					h.put("Score", ""+BetIndex);
					ThisPos++;
					STATE = WAITCALL;
					ColckTime  = Time;
					P.Connection(h);
				}
				break;
			case PLAY:
				if(SystemTime%10==0 && ColckTime>0)
				{
					ColckTime--;
					SystemTime = 1;
				}
				SystemTime++;
				if(ColckTime==0)//时间到出牌
				{
					if(ALLPASS || First)
					{
						AUTO(MyCard);
						for (int i = 0; i < signIndex; i++) 
						{
							SendCard[i] = MyCard[sign[i]];
			//				System.out.println("我要打出去的牌"+SendCard[i]);
						}

							boolean bool = PlayRule(SendCard,signIndex,true) ;
							if((ALLPASS && bool) || 
							(bool&& MyCardType == 3 && ThisCardMax == 3 && MyCardMax>ThisCardMax) ||
							(bool && MyCardType == 3 )||
							(First && PlayRule(SendCard,signIndex,true))  || 
										(bool && ThisCardType == MyCardType 
											&& MyCardMax>ThisCardMax))//检查是否合法牌和比上家牌大
							{
								ThisPos++;
								是否有出牌 = true;
								First = false;
							 Hashtable h = new Hashtable();
								h.put("Type", "SYNC");
								h.put("Cmd", "PLAY");
								h.put("UID", ""+P.userID);
								String Data=signIndex+"X";
								for (int i = 0; i < signIndex; i++) 
								{
									Data+=SendCard[i]+"X";
								}
								h.put("DATA", ""+Data);
								STATE = WAITPLAY;
								
								if(ThisPos%3 == LeftPlaryPos)
									LeftDeskCard = null;
								else if(ThisPos%3 == RightPlaryPos)
									RightDeskCard = null;
								 P.Connection(h);
							}
							else
								是否有出牌 = false;
							RoundInit();
					}
					else
					{
						MyPass = true;
						ThisPos++;
						是否有出牌 = true;
						 Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "PLAY");
						h.put("UID", ""+P.userID);
						String Data="";
						h.put("DATA", ""+Data);
						STATE = WAITPLAY;
						P.Connection(h);
					}
				}
				
//				if(没有牌比上家大)
//				{
				if(!First)
					for (int i = 0; i < 2; i++) 
					{
						SP_AUTO.setPosition((SW-(SP_AUTO.getWidth()+5)*2)/2+i*(SP_AUTO.getWidth()+5), SH/2+20);
						SP_AUTO.setFrame(i);
						SP_AUTO.paint(g);
					}
//				}
				PASS = true;
				for (int i = 0; i < Remainder; i++) {
					if(signY[i]==-5)//如果有选了牌就为出牌选项
					{
						PASS = false;
						break;
					}
				}
				if(ThisPos==0 || ALLPASS)
					PASS = false;
						
				if(PASS==false)
					drawBeautyString(g,"出牌",SW-P.ft.stringWidth("出牌")-10,fontY);
				else
					drawBeautyString(g,"不出",SW-P.ft.stringWidth("不出")-10,fontY);
				

				g.setColor(255,255,255);
				g.fillRect((SW-P.ft.stringWidth("到我出牌了"))/2-15, SH-P.H2+1, P.ft.stringWidth("到我出牌了")+30, P.H2-2);
				g.setColor(255,0,0);
				g.drawString("到我出牌了", (SW-P.ft.stringWidth("到我出牌了"))/2, SH-P.H2+5, 0);
				break;
			case WAITCALL:
				ClueTime = 5;
				ClueStr = "等待["+PlayerInfo[ThisPos%3][0]+"]叫分";
				if(SystemTime%10==0 && ColckTime>0)
				{
					ColckTime--;
					SystemTime = 1;
				}
				SystemTime++;
				DrawClue(g);
				break;
				
			case WAITPLAY:
				if(SystemTime%10==0 && ColckTime>0)
				{
					ColckTime--;
					SystemTime = 1;
				}
//				if(ColckTime==1)
//				{
//					P.KCTime = P.KCStartTime-10;
//				}
				SystemTime++;
				break;
			case WAIT:
				g.drawString("请稍后",(SW-P.ft.stringWidth("请稍后"))/2, SH/2, 0);
				break;
			case JUDGE:
				if((WIN_LOSE && MyPos!=LordPos) || (!WIN_LOSE && MyPos==LordPos))
					SP_win_lose.setFrame(0); 
				else if((WIN_LOSE && MyPos==LordPos) || (!WIN_LOSE && MyPos!=LordPos))
					SP_win_lose.setFrame(1);
				int c[] = P.ceateTranslucenceRect(SW,SH,0xf000000,(byte) 60);
				g.drawRGB(c,0,SW,0,0,SW,SH-P.H2,true);
				SP_win_lose.setPosition((SW-SP_win_lose.getWidth())/2, 5);
				SP_win_lose.paint(g);
				int x = 30;
				for (int i = 0; i < 3; i++) 
				{
					if(Integer.parseInt(PlayerInfo[i][2])==LordPos)
						SP_hand.setFrame(4);
					else
						//SP_hand.setFrame(2+Integer.parseInt(PlayerInfo[(LordPos+i)%3][1]));
					    SP_hand.setFrame(Integer.parseInt(PlayerInfo[i][4]));
					SP_hand.setPosition(x, SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2));
					if(i == MyPos)
						g.setColor(0xffd7c9a);
					else
						g.setColor(255,255,225);
					g.fillRect(x-1, SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2), SP_hand.getWidth()+2, SP_hand.getHeight()+3);
					SP_hand.paint(g);
					g.drawString(PlayerInfo[(LordPos+i)%3][0], 30+SP_hand.getWidth()+5, SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2), 0);
					g.drawString("本局："+"　　　　总分：", 
							x-1, SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+5, 0);
					
					if(WINS && i!=LordPos)
					{
							NT_score.sprite.setFrame(10);
							NT_score.sprite.setPosition(x+P.ft.stringWidth("本局："), 
									SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
							NT_score.sprite.paint(g);

					}
					else if(i==LordPos && !WINS)
					{
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("本局："), 
								SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					int tempScore = Score;//本局积分
					if(i!=LordPos)
						tempScore /=2;

					
					NT_score.drawNums(g, tempScore, x+P.ft.stringWidth("本局：")+NT_score.getW()+2,
							SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);
					int tempY = 0;
					if(AllScroe[(LordPos+i)%3]<0)//画-号
					{
						tempY = NT_score.getW();
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("本局：　　　　总分："), 
								SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					NT_score.drawNums(g, Math.abs(AllScroe[(LordPos+i)%3]), x+P.ft.stringWidth("本局：　　　　总分：")+tempY,
							SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);

				}
			//	g.drawImage(img_bomb, 30,  SP_win_lose.getHeight()+(SP_hand.getHeight()*6), 0);
				drawBeautyString(g,"继续",SW-P.ft.stringWidth("继续")-10,fontY);
				break;
				
			case SHOW:
				drawBeautyString(g,"继续",SW-P.ft.stringWidth("继续")-10,fontY);
				break;
				
			case WIN:
				int b[] = P.ceateTranslucenceRect(SW,SH,0xf000000,(byte) 60);
				g.drawRGB(b,0,SW,0,0,SW,SH-P.H2,true);
				
				x = SW/8;
				
				for (int i = 0; i < 3; i++) 
				{
					SP_hand.setFrame(2+Integer.parseInt(PlayerInfo[(ThisPos+i)%3][1]));
					SP_hand.setPosition(x, SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2));
					if(i == MyPos)
						g.setColor(0xffd7c9a);
					else
						g.setColor(255,255,225);
					if(i==ThisPos)
					{
						g.drawString(QuitStr,30+P.ft.stringWidth(PlayerInfo[(ThisPos+i)%3][0])+SP_hand.getWidth()+10, SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2),0);
					}
					g.fillRect(x-1, SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2), SP_hand.getWidth()+2, SP_hand.getHeight()+3);
					SP_hand.paint(g);
					g.drawString(PlayerInfo[(ThisPos+i)%3][0], 30+SP_hand.getWidth()+5, SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2), 0);
					g.setColor(255,255,255);
					g.drawString("本局："+"　　　　总分：", 
							x-1, SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+5, 0);
					
					int tempScore = Score;
					if(i>0)
						tempScore/=2;
					if(i==0)
					{
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("本局："), 
								SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					NT_score.drawNums(g, tempScore, x+P.ft.stringWidth("本局：")+NT_score.getW()+2,
							SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);
					
					int index = -1;
					int tempScore2=0;
					index = PlayerInfo[(ThisPos+i)%3][3].indexOf("-");
					
					if(index==-1)
					{
						tempScore2 = Integer.parseInt(PlayerInfo[(ThisPos+i)%3][3]);//总分
					}
					else
					{
						tempScore2 = Integer.parseInt(PlayerInfo[(ThisPos+i)%3][3].substring(1));
					}
					if(index!=-1)//画-号
					{
						index = NT_score.getW()+5;
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("本局：　　　　总分："), 
								SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					NT_score.drawNums(g, tempScore2, x+P.ft.stringWidth("本局：　　　　总分：")+index,
							SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);

				}
		//		g.drawImage(img_bomb, 30,  SP_win_lose.getHeight()+(SP_hand.getHeight()*6), 0);
				drawBeautyString(g,"继续",SW-P.ft.stringWidth("继续")-10,fontY);
				break;
		}
		if(playerInfo)
		{
			P.DrawDeskInfo(g);
		}
	}
	/**
	 * 
	 * @param g
	 */
	private void DrawCard(Graphics g ,int Card[],int Remainder,int x,int y,boolean bool,int newline)
	{	
		int temp=0,temp2=0;
		for (int i = 0; i < Remainder; i++) 
		{
			if(bool)
				temp2 = signY[i];
			
			if(Card[i] != -1)
			{
				if(Card[i]==52)
					SP_card.setFrame(3);
				else if(Card[i]==53)
					SP_card.setFrame(2);
				else
					SP_card.setFrame(0);
				
				SP_card.setPosition(x+(i%newline)*(SP_cardNumber.getWidth()+3), y+(i/newline)*SP_card.getHeight()+temp2);
				SP_card.paint(g);
				
				if(Card[i]!=52 && Card[i]!=53)//不等于鬼时候才画花色
				{
				SP_cardColor.setFrame(Card[i]/13);
				SP_cardColor.setPosition(x+(i%newline)*(SP_cardNumber.getWidth()+3)+2, y+(i/newline)*SP_card.getHeight()+SP_cardNumber.getHeight()+5+temp2);
				SP_cardColor.paint(g);
				temp=0;
				if(SP_cardColor.getFrame()==1 || SP_cardColor.getFrame()==3)//如果是红字
					temp = 13;
				SP_cardNumber.setPosition(x+(i%newline)*(SP_cardNumber.getWidth()+3)+2, y+(i/newline)*SP_card.getHeight()+2+temp2);
				SP_cardNumber.setFrame(Card[i]%13+temp);
				SP_cardNumber.paint(g);
				}
			}
		}
	}
	public void drawBeautyString(Graphics g,String str,int x,int y)
	{
		g.setColor(0xffd2964);
	    g.drawString(str, x-1, y, 0x10 | 0x4);
		g.drawString(str, x+1, y, 0x10 | 0x4);
		g.drawString(str, x, y-1, 0x10 | 0x4);
		g.drawString(str, x, y+1, 0x10 | 0x4);
		g.setColor(0xffffff);
		g.drawString(str, x, y, 0x10 | 0x4);
	}
	/**
	 * 
	 * @param g
	 */
	private void DrawDeskinfo(Graphics g)
	{
		//---------画地主的三张牌------------//
		int x = SP_card.getWidth()+SP_cardNumber.getWidth()*2+4;//三张牌居中
		for (int i = 0; i < 3; i++) 
		{
			SP_card.setPosition((SW-x)/2+i*(SP_cardNumber.getWidth()+3), 0);

			
			if(LordPos==-1)
				SP_card.setFrame(1);
			else
			{
				if(LordCard[i]==52)
					SP_card.setFrame(3);
				else if(LordCard[i]==53)
					SP_card.setFrame(2);
				else
					SP_card.setFrame(0);
			}
			
			SP_card.paint(g);
			if(SP_card.getFrame()!=1 && SP_card.getFrame()!=3 && SP_card.getFrame()!=2)
			{
				SP_cardColor.setFrame(LordCard[i]/13);
				SP_cardColor.setPosition((SW-x)/2+i*(SP_cardNumber.getWidth()+3)+2, SP_cardNumber.getHeight()+5);
				SP_cardColor.paint(g);
				int temp=0;
				if(SP_cardColor.getFrame()==1 || SP_cardColor.getFrame()==3)//如果是红字
					temp = 13;
				SP_cardNumber.setPosition((SW-x)/2+i*(SP_cardNumber.getWidth()+3)+2, 2);
				SP_cardNumber.setFrame((LordCard[i]%13)+temp);
				SP_cardNumber.paint(g);
			}
		}
		for (int i = 0; i < 3; i++) 
		{
			if(ThisPos%3==i)
				g.setColor(0xfffe450);
				
			else
				g.setColor(255,255,255);
			if(i==LeftPlaryPos)
			{
				g.fillRoundRect(1, 1, SP_hand.getWidth()+3, SP_hand.getHeight()+3, 5, 5);
				SP_hand.setPosition(2, 2);
				if(LordPos != LeftPlaryPos)
				{
					SP_hand.setFrame(Integer.parseInt(PlayerInfo[(MyPos+2)%3][1]));
				}
				else
					SP_hand.setFrame(4);
			}
			else if(i==RightPlaryPos)
			{
				g.fillRoundRect(SW-SP_hand.getWidth()-3, 1, SP_hand.getWidth()+2, SP_hand.getHeight()+2, 5, 5);
				SP_hand.setPosition(SW-SP_hand.getWidth()-2, 2);
				if(LordPos != RightPlaryPos)
				{
					SP_hand.setFrame(Integer.parseInt(PlayerInfo[(MyPos+1)%3][1]));
				}
				else
					SP_hand.setFrame(4);
			}
			else
			{
		//		System.out.println("ThisPos"+ThisPos);
				g.fillRoundRect(SW-SP_hand.getWidth()-4, SH-P.H2-SP_hand.getHeight()-4, SP_hand.getWidth()+3, SP_hand.getHeight()+3, 5, 5);
				SP_hand.setPosition(SW-SP_hand.getWidth()-2, SH-P.H2-SP_hand.getHeight()-2);
				if(LordPos != MyPos)
				{
					SP_hand.setFrame(Integer.parseInt(PlayerInfo[MyPos%3][1]));
				}
				else
					SP_hand.setFrame(4);
			}
			SP_hand.paint(g);
		}
		//-------------画COLCK---------------//
		int x2=0,y=0,y2=0,x3=0;
		
		if(ThisPos%3 == LeftPlaryPos)
		{
			x = SP_hand.getWidth()+5;
			y = 0;
			x2 = x;
			x3 = x;
		}
		else if(ThisPos%3 == MyPos)
		{
			x = SW-SP_hand.getWidth()-4;
			x2 = x;
			y = SH-P.H2-SP_hand.getHeight();
			y2 = y - img_colck.getHeight()-5;
		}
		else
		{
			x = SW-SP_hand.getWidth()-5;
			y = 0;
			x2 = x - img_colck.getWidth();
			x3 = x - P.ft.stringWidth(PlayerInfo[ThisPos%3][0]);
		}
		g.setColor(255,255,255);
		if(ThisPos%3!=MyPos)
		g.drawString(PlayerInfo[ThisPos%3][0], x3, y+img_colck.getHeight()+3, 0);
		//------------------------------------
		g.drawImage(img_colck, x2, y2, 0);
		if(ColckTime<10)
			x2 += (img_colck.getWidth()-NT_Clock.getW())/2;
			
		else
			x2 += (img_colck.getWidth()-NT_Clock.getW()*2)/2-1;
		
		NT_Clock.drawNums(g, ColckTime, x2, y2+12, 1);
		
		//-------------------画剩余的牌--------------
		int RectW = NT_medium.getW()*2+10;
		int RectH = NT_medium.getH()+10;
		x = (SP_hand.getWidth()-RectW)/2+2;
		for (int i = 0; i < 2; i++) 
		{
			g.setColor(0xffffff);
			g.drawRect(x+i*(SW-SP_hand.getWidth()-2)-i*4, SP_hand.getHeight()+8, RectW, RectH);
			g.setColor(0xfffd4dc);
			g.fillRect(x+i*(SW-SP_hand.getWidth()-2)-i*4+1, SP_hand.getHeight()+9, RectW-1, RectH-1);
		}

		NT_medium.drawNums(g, LeftNum, x+(RectW-(NT_medium.getW()+NT_medium.getW()*(LeftNum/10)))/2, 
				SP_hand.getHeight()+8+(RectH-NT_medium.getH())/2, 1);
	
		NT_medium.drawNums(g, RightNum, x+(SW-SP_hand.getWidth()-2)-4+(RectW-(NT_medium.getW()+NT_medium.getW()*(RightNum/10)))/2, 
				SP_hand.getHeight()+8+(RectH-NT_medium.getH())/2, 1);
		if(LeftPass)
			g.drawImage(img_pass, 5, SP_hand.getHeight()+15+RectH, 0);
		if(RightPass)
			g.drawImage(img_pass, SW-5-img_pass.getWidth(), SP_hand.getHeight()+15+RectH, 0);
		if(MyPass)
			g.drawImage(img_pass, (SW-img_pass.getWidth())/2, SH/2+15, 0);
//		----------------------画叫的分-----------------//
		if(LeftBet!=-1)
		{
			SP_BET.setPosition(5, SP_hand.getHeight()+15+28);
			SP_BET.setFrame(LeftBet);
			SP_BET.paint(g);
		}
		if(RightBet!=-1)
		{
			SP_BET.setPosition(SW-5-SP_BET.getWidth(), SP_hand.getHeight()+15+28);
			SP_BET.setFrame(RightBet);
			SP_BET.paint(g);
		}
		if(MyBet !=-1)
		{
			SP_BET.setPosition((SW-SP_BET.getWidth())/2, SH-P.H2-SP_card.getHeight()*2-SP_BET.getHeight());
			SP_BET.setFrame(MyBet);
			SP_BET.paint(g);
		}
	//	DrawClue(g);//画游戏里的提示
		//-----------画炸弹----------
		if(BOOLbomb)
		{
			bombY+=10;
			
			if(bombY<SH/2)
			{
				g.drawImage(img_bomb, (SW-img_bomb.getWidth())/2, bombY, 0);
			}
			else
			{
				g.drawImage(img_bomb2, (SW-img_bomb2.getWidth())/2, SH/2-img_bomb2.getHeight()/2, 0);
			}
			if(bombY>SH)
			{
				BOOLbomb = false;
				bombY = -img_bomb.getHeight();
			}
			
		}
		
//		if(没有牌比上家大)
//		{
//			Platform.drawBeautyString(g, "没有牌比上家大", 50, 200);
//		}
	}
	/**
	 * 判断是否合法出牌
	 * @return
	 */
	private boolean PlayRule(int Card[],int CardNum,boolean boo)
	{
		//boo = true//是我出牌最大数
		SequenceCard(Card,CardNum);
		boolean bool = false;
		if(Remainder<this.CardNum)
			return false;
//		if(signIndex != CardNum)
//			return bool;
//		System.out.println("牌的张数:"+CardNum);
		if(CardNum==1)
		{
			if(boo)
			{
				if(Card[0]<52)
					MyCardMax = Card[0]%13;
				else
					MyCardMax = Card[0];
				MyCardType = 0;
			}
			else
			{
				if(Card[0]<52)
					ThisCardMax = Card[0]%13;
				else
					ThisCardMax = Card[0];
				ThisCardType = 0;
				this.CardNum = 1;
			}
			bool =  true;
		}
		else if(CardNum==2)//对牌
		{
	//		System.out.println("是否对牌");
			if(Card[0]%13 == Card[1]%13 || (Card[0]>=52 && Card[1]>=52))
			{
				bool =  true;
				
				if(Card[0]>51)
				{

					if(boo)
					{
						MyCardType = 3;
						MyCardMax = Card[0];
						
					}
					else
					{
						ThisCardMax = Card[0];
						ThisCardType = 3;
						this.CardNum = 2;
					}
					BOOLbomb = true;
					multiple *=2;
				//	System.out.println(MyCardMax+" 炸弹BOMB");
				}
				else
				{
					if(boo)
					{
						MyCardMax = Card[0]%13;
						MyCardType = 1;
				//		System.out.println(MyCardMax+" 一对");
					}
					else
					{
						ThisCardMax = Card[0]%13;
						ThisCardType = 1;
						this.CardNum = 2;
				//		System.out.println(ThisCardMax+" 一对");
					}					
				}				
			}
		}
		else if(CardNum==3)
		{
			if(Card[0]%13 == Card[1]%13 && Card[0]%13 == Card[2]%13)
			{
				bool =  true;
				if(boo)
				{
					MyCardMax = Card[0]%13;
					MyCardType = 2;
					System.out.println(MyCardMax+" 三张");
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 1;
					System.out.println(ThisCardMax+" 三张");
					this.CardNum = 3;
				}
				
				
				
			}
		}
		else if(CardNum==4)
		{
			if(Card[0]%13 == Card[1]%13 && Card[0]%13 == Card[2]%13 && Card[0]%13 == Card[3]%13)
			{
				if(boo)
				{
					MyCardMax = Card[0]%13;
					MyCardType = 3;
			//		System.out.println(MyCardMax+" 炸弹BOMB");
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 3;
			//		System.out.println(ThisCardMax+" 炸弹BOMB");
					this.CardNum = 4;
				}
				BOOLbomb = true;
				multiple *=2;
				bool =  true;
				
			}
			else 
			{
				int tempindex=0;
				for (int i = 0; i < CardNum; i++) 
				{
					if(Card[1]%13==Card[i]%13 && Card[1]!=Card[i])
					{
						tempindex++;
					}
				}
				if(tempindex==2)
				{
					if(boo)
					{
						MyCardMax = Card[1]%13;
						MyCardType = 4;
						System.out.println(MyCardMax+" 三带一");
					}
					else
					{
						ThisCardMax = Card[1]%13;
						ThisCardType = 4;
						this.CardNum = 4;
						System.out.println(ThisCardMax+" 三带一");
						SequenceDeskCard(Card, ThisCardMax,CardNum);
					}
					bool =  true;
				}
			}
		}
		else if(CardNum>=5)
		{
			//----------葫芦---------//
			int 四张 = 0;
			int 三张 = 0;
			int 一对 = 0;
			int index=0;
			int n=0;
			int MAX=-1;
			int 三张索引[]=new int[Card.length];
			int 三张数组[]=new int[Card.length];
			for (int i = 0; i < CardNum; i++) 
			{
				index=0;
				n=1;
				三张索引[i] = -1;
				for (int j = i+1; j < CardNum; j++) 
				{
					if(Card[i]%13==Card[j]%13)
					{
						index+=n;
						n+=n;
						i = j;
					}
				}
				switch (index) 
				{
					case 1:							
						一对++;
						System.out.println("有"+一对+"一对");
						break;
					case 3:
						System.out.println("有"+三张+"个三张");
						三张索引[三张]=i;
						if(Card[i]%13>MAX)
							MAX = Card[i]%13;
						三张++;
						break;
					case 7:
						四张++;
						System.out.println("有四张");
						MAX = Card[i]%13;
						break;
				}					
			}
			int index2=0;
			if(三张==1 || 四张==1)
			{
				SequenceDeskCard(Card, MAX,CardNum);
			}
			else if(三张>1)//三连张的三张的排序
			{
				for (int i = 0; i < 三张; i++) 
				{
					if(三张索引[i]!=-1)
					{
						for (int j = 0; j < 3; j++) 
						{
							三张数组[index2] = Card[三张索引[i]-j];
							Card[三张索引[i]-j] = -1;
							index2++;
						}
					}
				}

				SequenceCard(三张数组, 三张*3);
				
				for (int i =0; i < 三张数组.length; i++) {
					if(Card[i] !=-1)
					{
						三张数组[index2] = Card[i];
						index2++;
					}
				}
				System.arraycopy(三张数组, 0, Card, 0, Card.length);
			}
			//-----------------------------
			if(一对>=3)
			{
				for (int i = 0; i < CardNum/2-1; i++) 
				{
					if(Card[i*2]%13-1 != Card[i*2+2]%13)//检查是不是连三对
					{
						System.out.println("错误.你没有连对");
						return false;
					}
					if(boo)
					{
						if(Card[i]%13>MyCardMax)
							MyCardMax = Card[i]%13;
						MyCardType = 7;
						this.CardNum = 一对*2;
					}
					else
					{
						if(Card[i]%13>ThisCardMax)
							ThisCardMax = Card[i]%13;
						ThisCardType = 7;
						this.CardNum = 一对*2;
					}
				}
				System.out.println("有"+一对+"连对"+" |最大牌是"+(MyCardMax+3));
				
				
				return bool = true;
			}
			else if(三张>=2 && CardNum%(三张*3)==0)
			{
				for (int i = 0; i < CardNum/3-1; i++) 
				{
					if(Card[i*3]%13-1 != Card[i*3+3]%13)//检查是不是连三对
					{
						System.out.println("错误.你没有连三张");
						return false;
					}
					if(boo)
					{
						MyCardType = 8;
						if(Card[i]%13>MyCardMax)
							MyCardMax = Card[i]%13;
					}
				}
				if(!boo)
				{					
					ThisCardMax = Card[三张*3-1]%13;
					ThisCardType = 8;
					this.CardNum = 三张*3;
				}
				System.out.println("有连三张");
				
				return bool = true;
			}
			else if(三张==一对 && 三张!=0 && 一对!=0)//判断葫芦或连葫芦
			{
					if(三张==1)
					{
						if(boo)
						{
							MyCardMax = Card[0]%13;
							MyCardType = 5;	
						}
						else
						{
							ThisCardMax = Card[0]%13;
							ThisCardType = 5;
							this.CardNum = 三张*3+一对*2;
						}
						
					}
					else
					{
						for (int i = 0; i < 三张-1; i++) 
						{
							if(Card[i*3]%13-1 != Card[i*3+3]%13)//检查是不是连三张
							{
								System.out.println("错误.你没有连三张的葫芦");
								return false;
							}
						}
						if(boo)
						{
							MyCardMax = Card[0]%13;
							MyCardType = 9;	
						}
						else
						{
							ThisCardMax = Card[三张*3]%13;
							ThisCardType = 9;
							this.CardNum = 三张*3+一对*2;
						}
						
					}
				
				return bool = true;
			}
			else if(三张>=1 && CardNum%(三张*3)!=0  )
			{
				for (int i = 0; i < 三张-1; i++) 
				{
					if(Card[i*3]%13-1 != Card[i*3+3]%13)
					{
						System.out.println("错误.你没有连三带一");
						return false;
					}
				}
				if(boo)
				{
					MyCardMax = Card[0]%13;
					MyCardType = 10;
				}
				else
				{
					ThisCardMax = Card[三张*3-1]%13;
					ThisCardType = 10;
					this.CardNum = 三张*3+三张;
				}
				System.out.println("有连三带一");
				return bool = true;
			}
			else if(四张==1 && CardNum==6)//4444 1 2
			{
				System.out.println("有4444 1 2");
				if(boo)
				{
					MyCardMax = Card[0]%13;
					MyCardType = 11;
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 11;
					this.CardNum = 6;
				}
				return bool = true;
			}
			else if(四张==1 && 一对==2)//4444 11 22
			{
				System.out.println("有4444 11 22");
				if(boo)
				{
					MyCardMax = Card[0]%13;
					MyCardType = 12;
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 12;
					this.CardNum = 四张*4+一对*2;
				}
				return bool = true;
			}	
			
			if(一对==0 && 三张==0)
			{
			//---------------顺--------------//
				System.out.println("--顺---");
				for (int i = 0; i < CardNum-1; i++) 
				{
					if(Card[i]%13>=12 || (Card[i]%13)-1 != Card[i+1]%13)
					{
						return  bool = false;
					}
				}
				if(boo)
				{
					MyCardMax = Card[0]%13;
					MyCardType = 6;
					System.out.println(MyCardMax+" 顺");
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 6;
					this.CardNum = CardNum;
					System.out.println(ThisCardMax+" 顺");
				}
				bool = true;
			}
		}
		if(bool)
			System.out.println("合法规则");
		return bool;
	}
	/**
	 * 自动提示 
	 * @param card
	 */
	private void AUTO(int card[])//自动提示
	{
		boolean temp123 = true;
		System.out.println("自动提示类型"+ThisCardType);
		if(Remainder>=CardNum)//判断长度
		switch (ThisCardType) 
		{
			case -1:
				System.out.println(Remainder);
				signY[Remainder-1] = -5;
				sign[signIndex] = Remainder-1;
				signIndex++;
				没有牌比上家大 = false;
				return;
		//		break;
			case 0://单个
				for (int i = Remainder-1; i >=0; i--) 
				{
					if(MyCard[i]%13 > ThisCardMax || (MyCard[i]>=52 && MyCard[i] > ThisCardMax))
					{
						signY[i] = -5;
						sign[signIndex] = i;
						signIndex++;
						没有牌比上家大 = false;
						return;
					}
				}
				break;
			case 1://一对
				for (int i = Remainder-1; i >=1; i--) 
				{
					if(MyCard[i]%13 > ThisCardMax && MyCard[i]%13==MyCard[i-1]%13)
					{
						signY[i] = -5;
						sign[signIndex] = i;
						signIndex++;
						signY[i-1] = -5;
						sign[signIndex] = i-1;
						signIndex++;
						没有牌比上家大 = false;
						return;
					}
				}
				break;
			case 2://三张
				for (int i = Remainder-1; i >=0; i--) 
				{
					if(MyCard[i]%13 > ThisCardMax && MyCard[i]%13==MyCard[i-1]%13 
							&& MyCard[i]%13==MyCard[i-2]%13)
					{
						没有牌比上家大 = false;
						
						for (int j = 0; j < 3; j++) 
						{
							sign[signIndex] = i-j;
							signY[i-j] = -5;
							signIndex++;
						}
						return;
					}
				}
				break;
			case 3://炸弹
				if(ThisCardMax>51)//最大牌..不用判断
					return;
				
				for (int i = Remainder-1; i >=0; i--) 
				{
					if(MyCard[i]%13<ThisCardMax)
					{
						continue;
					}
					
					for (int j = i; j > i-CardNum; j--) 
					{
						if(j-1<0)//判断是不是尽头了
							return;
						
						if(MyCard[j]%13 == MyCard[j-1]%13 )
						{
							signY[j] = -5;;
							sign[signIndex] = j;
							signIndex++;
							if(signIndex == CardNum-1)
							{
								sign[signIndex] = j-1;
								signY[j-1] = -5;
								signIndex++;
								没有牌比上家大 = false;
								return;
							}
						}
						else if(MyCard[j]==52 && MyCard[j-1]==53)
						{
							signY[j] = -5;;
							sign[signIndex] = j;
							signIndex++;

							sign[signIndex] = j-1;
							signY[j-1] = -5;
							signIndex++;
							没有牌比上家大 = false;
							return;
						}
						else
						{
							i = j-1;
							if(signIndex>0)
							for (int k = 0; k < sign.length; k++) 
							{
								sign[k] = -1;
								signY[k] = 0;
							}
							signIndex = 0;
							continue;
						}
					}
				}
				break;
			case 4://三带一
				boolean three2 = false;//是否有三张
				boolean pair2 = false;//是否有一张
				int temp2=-1;
				for (int i = Remainder-1; i >=2; i--) 
				{					
					if(!three2 && MyCard[i]%13 != temp2 && MyCard[i]%13 > ThisCardMax && 
							MyCard[i]%13==MyCard[i-1]%13 && MyCard[i]%13==MyCard[i-2]%13)
					{
						for (int j = 0; j < 3; j++) 
						{
							sign[signIndex] = i-j;
							signY[i-j] = -5;
							temp2 = MyCard[i]%13;
							signIndex++;
						}
						three2 = true;
						i = Remainder-1;
					}
					else if(three2 && MyCard[i]%13 != temp2 )
					{
						signY[i] = -5;
						sign[signIndex] = i;
						signIndex++;
						temp2 = MyCard[i]%13;
						pair2 = true;
						if(signIndex == CardNum)
						{
							没有牌比上家大 = false;
							return;
						}
						else if(signIndex>0)
						{
							for (int k = 0; k < sign.length; k++) 
							{
								sign[k] = -1;
								signY[k] = 0;
							}
							signIndex = 0;
							没有牌比上家大 = true;
						}
					}
				}

				break;
			case 5://葫芦
				boolean three = false;//是否有三张
				boolean pair = false;//是否有一对
				int temp=-1;
				for (int i = Remainder-1; i >=2; i--) 
				{					
					if(!three && MyCard[i]%13 != temp && MyCard[i]%13 > ThisCardMax && 
							MyCard[i]%13==MyCard[i-1]%13 && MyCard[i]%13==MyCard[i-2]%13)
					{
						for (int j = 0; j < 3; j++) 
						{
							sign[signIndex] = i-j;
							signY[i-j] = -5;
							temp = MyCard[i]%13;
							signIndex++;
						}
						three = true;
						i = Remainder-1;
					}
					else if(three && MyCard[i]%13 != temp  && MyCard[i]%13==MyCard[i-1]%13)
					{
						signY[i] = -5;
						sign[signIndex] = i;
						signIndex++;
						signY[i-1] = -5;
						sign[signIndex] = i-1;
						signIndex++;
						temp = MyCard[i]%13;
						
						if(signIndex == CardNum)
						{
							没有牌比上家大 = false;
							return;
						}
						else
						{
							for (int k = 0; k < sign.length; k++) 
							{
								sign[k] = -1;
								signY[k] = 0;
							}
							signIndex = 0;
							没有牌比上家大 = true;
							return;
						}
					}
				}

				break;
			case 6:
				if(ThisCardMax==11)//顺里面带A.不用判断最大牌.
					break;
				int mix = card[card.length-1]%13;
				
				for (int i = Remainder-1; i >=0; i--) 
				{
					if(MyCard[i]%13>mix)
					{
						for (int j = i; j > 0; j--) 
						{	
							int a = MyCard[j]%13;
							if(MyCard[j]%13<12 && MyCard[j]<52)
							{
								if((MyCard[j]%13) == (MyCard[j-1]%13)- 1 && (MyCard[j-1]%13)<12)
								{	
									signY[j] = -5;;
									sign[signIndex] = j;
									signIndex++;
									if(signIndex==CardNum-1)//有牌可以打
									{
										sign[signIndex] = j-1;
										signY[j-1] = -5;
										signIndex++;
										没有牌比上家大 = false;
										return;
									}
								}
								else if((MyCard[j]%13) != (MyCard[j-1]%13) && signIndex>0)
								{
									i = j-1;
									for (int k = 0; k < signY.length; k++) 
									{
										sign[k] = -1;
										signY[k] = 0;
									}
									signIndex = 0;
									continue;
								}
							}
						}
					}
				}
				break;
				
			case 7://自动提示连对
				int save=-1;
				for (int i = Remainder-1; i >0; i--) 
				{
					int index=0;

					if(MyCard[i]%13 > ThisCardMax && save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 )//判断这个牌有没有比对方出的大并且有没有连对
					{							
						save =MyCard[i]%13;
						for (int j = 0; j < 2; j++) 
						{
							signY[i-j] = -5;
							sign[signIndex] = i-j;
							signIndex++;
						}
						i--;
						if(signIndex==CardNum)
						{
							没有牌比上家大 = false;
							return ;
						}
					}
					else if(MyCard[i]%13!=save)
					{
						for (int k = 0; k < sign.length; k++) {
							sign[k] = -1;
							signY[k] = 0;
						}
						signIndex = 0;
					}
					
				}
				//没有牌打
				for (int i = 0; i < sign.length; i++) {
					sign[i] = -1;
					signY[i] = 0;
				}
				signIndex =0;
				没有牌比上家大 = true;
				break;  
				
			case 8://连三张
				save=-1;
				for (int i = Remainder-1; i >1; i--) 
				{
					int index=0;

					if(MyCard[i]%13 > ThisCardMax && save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 && MyCard[i-2]%13 == MyCard[i]%13 )//判断这个牌有没有比对方出的大并且有没有连对
					{							
						save =MyCard[i]%13;
						for (int j = 0; j < 3; j++) 
						{
							signY[i-j] = -5;
							sign[signIndex] = i-j;
							signIndex++;
						}
						i-=2;
						if(signIndex==CardNum)
						{
							没有牌比上家大 = false;
							return ;
						}
					}
					else if(MyCard[i]%13!=save && signIndex>0)
					{
						for (int k = 0; k < sign.length; k++) {
							sign[k] = -1;
							signY[k] = 0;
						}
						signIndex = 0;
					}
					
				}
				//没有牌打
				for (int i = 0; i < sign.length; i++) {
					sign[i] = -1;
					signY[i] = 0;
				}
				signIndex =0;
				没有牌比上家大 = true;
				break;
			case 9://连葫芦
				save=-1;
				three2 = false;
				pair2 = false;
				for (int i = Remainder-1; i >1; i--) 
				{
					int index=0;

					if(MyCard[i]%13 > ThisCardMax && save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 && MyCard[i-2]%13 == MyCard[i]%13 )//判断这个牌有没有比对方出的大并且有没有连对
					{							
						save =MyCard[i]%13;
						for (int j = 0; j < 3; j++) 
						{
							signY[i-j] = -5;
							sign[signIndex] = i-j;
							signIndex++;
						}
						i-=2;
						if(CardNum/5*2+signIndex==CardNum)
						{
							three2 = true;
							break ;
						}
					}
					else if(MyCard[i]%13!=save && signIndex>0)
					{
						for (int k = 0; k < sign.length; k++) {
							sign[k] = -1;
							signY[k] = 0;
						}
						signIndex = 0;
					}
					
				}
				save=-1;
				if(three2)
				for (int i = Remainder-1; i >1; i--) 
				{
					int index=0;

					if(save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 )//判断这个牌有没有比对方出的大并且有没有连对
					{		
						if(signY[i] == -5)//表示有相同的了
						{
							continue;
						}
							
						save =MyCard[i]%13;
						for (int j = 0; j < 2; j++) 
						{
							signY[i-j] = -5;
							sign[signIndex] = i-j;
							signIndex++;
						}
						i--;
						if(signIndex>=CardNum)
						{
							break ;
						}
					}
//					else if(MyCard[i]%13!=save)
//					{
//						for (int k = 0; k < sign.length; k++) {
//							sign[k] = -1;
//							signY[k] = 0;
//						}
//						signIndex = 0;
//					}
				}
				//没有牌打
				if(signIndex!=CardNum)
				{
					for (int i = 0; i < sign.length; i++) {
						sign[i] = -1;
						signY[i] = 0;
					}
					signIndex =0;
					没有牌比上家大 = true;
				}
				else
				{
					没有牌比上家大 = false;
				}
				break;
				
			case 10:
				save=-1;
				three2 = false;
				pair2 = false;
				for (int i = Remainder-1; i >1; i--) 
				{
					int index=0;

					if(!three2&&MyCard[i]%13 > ThisCardMax && save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 && MyCard[i-2]%13 == MyCard[i]%13 )//判断这个牌有没有比对方出的大并且有没有连对
					{							
						save =MyCard[i]%13;
						for (int j = 0; j < 3; j++) 
						{
							signY[i-j] = -5;
							sign[signIndex] = i-j;
							signIndex++;
						}
						i-=2;
						if(signIndex+signIndex/3==CardNum)
						{
							three2 = true;
							break ;
						}
					}
					else if(MyCard[i]%13!=save && signIndex>0)
					{
						for (int k = 0; k < sign.length; k++) {
							sign[k] = -1;
							signY[k] = 0;
						}
						signIndex = 0;
					}
					
				}
				if(three2)
				for (int i = Remainder-1; i >1; i--) 
				{
						if(signY[i] == -5)//表示有相同的了
						{
							continue;
						}
						signY[i] = -5;
						sign[signIndex] = i;
						signIndex++;
						if(signIndex>=CardNum)
						{
							没有牌比上家大 = false;
							return ;
						}
				}
				没有牌比上家大 = true;
				break;
		}
			if(ThisCardMax>51 && CardNum<4 && !没有牌比上家大 && ThisCardType==-1)//最大牌..不用判断
				return;
			for (int i = Remainder-1; i >=0; i--) 
			{
				if(ThisCardType == 3 && MyCard[i]%13<ThisCardMax)
				{
					continue;
				}
				
				for (int j = i; j > i-4; j--) 
				{
					if(j-1<0)//判断是不是尽头了
					{
						if(signIndex!=2 || signIndex!=4)//没炸弹
						{
						for (int k = 0; k < sign.length; k++) 
						{
							sign[k] = -1;
							signY[k] = 0;
						}
						signIndex = 0;
						}
						return;
					}
					if(MyCard[j]%13 == MyCard[j-1]%13 )
					{
						signY[j] = -5;;
						sign[signIndex] = j;
						signIndex++;
						if(signIndex == 4-1)
						{
							sign[signIndex] = j-1;
							signY[j-1] = -5;
							signIndex++;
							没有牌比上家大 = false;
							return;
						}
					}
					else if(MyCard[j]==52 && MyCard[j-1]==53)
					{
						signY[j] = -5;;
						sign[signIndex] = j;
						signIndex++;

						sign[signIndex] = j-1;
						signY[j-1] = -5;
						signIndex++;
						没有牌比上家大 = false;
						return;
					}
					else
					{
						i = j-1;
						if(signIndex>0)
						for (int k = 0; k < sign.length; k++) 
						{
							sign[k] = -1;
							signY[k] = 0;
						}
						signIndex = 0;
						continue;
					}
				}
			}

	}
	/**
	 * 分析对方发送信息
	 *
	 */
	String cutLine="";
	public void Analyse(String Type,String content)
	{
		cutLine = content;
		if(Type.equals("DEAL"))//分牌
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1:
					for (int i = 0; i < MyCard.length; i++) {
						MyCard[i] = Integer.parseInt(cutLineFromContent());
					}
					break;
			}

		}
		else if(Type.equals("BET"))
		{
			ColckTime  = Time;
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 3:
					ThisPos++;
				//	System.out.println("Card.Analyse()"+ThisPos);
					int pos = Integer.parseInt(cutLineFromContent());
					int S = Integer.parseInt(cutLineFromContent());
					if(MyPos-1%3 == pos)
					{
						Score = S;//对方叫的分数
			//			ClueStr = "["+PlayerInfo[ThisPos%3][0]+"]叫了"+Score+"分";
						STATE = BET;
					}
					if(pos == LeftPlaryPos)
					{
						LeftBet = S;
					}
					else if(pos == RightPlaryPos)
					{
						RightBet = S;
					}
					break;
			}
		}
		else if(Type.equals("LORD"))
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 1:
					ColckTime  = Time;
					LordPos = Integer.parseInt(cutLineFromContent());
					CallScore = Integer.parseInt(cutLineFromContent());
					ClueStr = PlayerInfo[LordPos][0]+"成为了地主";
					ClueTime = 30;
	//				System.out.println("地主位置"+LordPos);
					if(LordPos==MyPos)
					{
						ThisPos = MyPos;
						Remainder = 20;
			//			System.out.println("我是地主");
						for (int i = 0; i < LordCard.length; i++) {
							LordCard[i] = MyCard[i+17];
						}
						SequenceCard(MyCard,Remainder);
						STATE = PLAY;
					}
					else if(LordPos == LeftPlaryPos)
					{
						ThisPos = LeftPlaryPos;
						LeftNum = 20;
			//			System.out.println("左边是地主");
						STATE = WAITPLAY;
						for (int i = 0; i < LordCard.length; i++) {
							LordCard[i] = MyCard[i+17];
						}
						LeftBet = CallScore;
					}
					else
					{
						ThisPos = RightPlaryPos;
						RightNum = 20;
				//		System.out.println("右边是地主");
						STATE = WAITPLAY;
						for (int i = 0; i < LordCard.length; i++) {
							LordCard[i] = MyCard[i+17];
						}
						RightBet = CallScore;
					}
					
					break;
			}
		}
		else if(Type.equals("PLAY"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
				case 1:

					break;
	
				case 3:
					ColckTime  = Time;
					PlayPos = Integer.parseInt(cutLineFromContent());
					LeftBet = -1;
					RightBet = -1;
					MyBet = -1;
		//			System.out.println("出牌者位置"+PlayPos);
//					if(STATE ==SHOW)
//					{
//						int lennum = Integer.parseInt(cutLineFromContent());
//						if(PlayPos == LeftPlaryPos)
//						{
//							if(lennum>0)
//							{
//								LeftDeskCard = new int[lennum];
//								for (int i = 0; i < LeftDeskCard.length; i++) {
//									LeftDeskCard[i] = Integer.parseInt(cutLineFromContent());
//								}
//							}
//						}
//						else if(PlayPos == RightPlaryPos)
//						{
//							RightDeskCard = new int[lennum];
//							for (int i = 0; i < RightDeskCard.length; i++) {
//								RightDeskCard[i] = Integer.parseInt(cutLineFromContent());
//							}
//						}
//						if(RightDeskCard!=null && LeftDeskCard!=null)
//						{
//							P.WAIT = false;
//						}
//						return;
//					}
					if(cutLine.equals(""))//后面没有带牌PASS
					{
						if(PlayPos == LeftPlaryPos)
						{
		//					System.out.println("上家PASS");
							LeftPass = true;
							LeftDeskCard = null;
							if(RightPass!=false && RightDeskCard!=null)
								PlayRule(RightDeskCard, RightDeskCard.length, false);
							STATE = PLAY;
							DeskCard = null;
							MyPass = false;
						}
						else if(PlayPos == RightPlaryPos)
						{
		//					System.out.println("下家PASS");
							RightPass = true;
							RightDeskCard = null;
							if(LeftPass!=false && RightDeskCard!=null)
								PlayRule(LeftDeskCard, LeftDeskCard.length, false);
							
						}
						if(LeftPass && RightPass)
						{
							STATE = PLAY;
							ALLPASS = true;
		//					System.out.println("全部PASS");
							DeskCard = null;
							MyPass = false;
							ThisCardType = -1;
							ThisCardMax = -1;
							CardNum = 0;
						}
						ThisPos++;
						return;
					}
					
					if(PlayPos == LeftPlaryPos)
					{
						ALLPASS = false;
						
						LeftDeskCard = new int[Integer.parseInt(cutLineFromContent2())];
						for (int i = 0; i < LeftDeskCard.length; i++) {
							LeftDeskCard[i] = Integer.parseInt(cutLineFromContent2());
						}
						ThisPlaryCard = new int[LeftDeskCard.length];
						System.arraycopy(LeftDeskCard, 0, ThisPlaryCard, 0, LeftDeskCard.length);
						STATE = PLAY;
						DeskCard = null;
						MyPass = false;
						First = false;
						PlayRule(LeftDeskCard, LeftDeskCard.length, false);
				//		System.out.println("上家出了类型为"+ThisCardType+"牌最大数是"+ThisCardMax);
						LeftPass = false;
						LeftNum -= LeftDeskCard.length;
						if(LeftNum<=0)//出完了牌
						{
							Formulary();
							WinPos = LeftPlaryPos;
					//		System.out.println("判断胜负!!!!");
							if(LordPos !=LeftPlaryPos && MyPos != LordPos)//左边不是地主.我也不是
							{
				//				System.out.println("我赢了");
								WINS = false;
								WIN_LOSE = true;
							}
							else //他是地主或者我是地主他不是
							{
								
				//				System.out.println("我输了");
								WINS = true;
								WIN_LOSE = false;
							}
							if(LordPos==MyPos)
								WINS = false;
							for (int i = 0; i < PlayerInfo.length; i++) {
								int S = 0;
								if(WINS)
								{
									if(i==LordPos)
										S = Score;
									else
										S = -(Score/2);
								}
								else 
								{
									if(i==LordPos)
										S = -Score;
									else
										S = Score/2;
								}
								AllScroe[(LordPos+i)%3] = +S;
					
							}
							STATE = JUDGE;
//							Hashtable h = new Hashtable();
//							h.put("Type", "SYNC");
//							h.put("Cmd", "PLAY");
//							h.put("UID", ""+P.userID);
//							
//							String Data;
//							Data=MyCard.length+"|";
//							for (int i = 0; i < MyCard.length; i++) 
//							{
//								Data+=MyCard[i]+"|";
//							}
//
//							h.put("DATA", ""+Data);
//							new XConnection(false,P.MID,h);
						}
					}else if(PlayPos == RightPlaryPos)
					{
						ALLPASS = false;
						RightDeskCard = new int[Integer.parseInt(cutLineFromContent2())];
						for (int i = 0; i < RightDeskCard.length; i++) {
							RightDeskCard[i] = Integer.parseInt(cutLineFromContent2());
						}
						ThisPlaryCard = new int[RightDeskCard.length];
						System.arraycopy(RightDeskCard, 0, ThisPlaryCard, 0, RightDeskCard.length);
						PlayRule(RightDeskCard, RightDeskCard.length, false);
			//			System.out.println("下家出了类型为"+ThisCardType);
						RightPass = false;
						First = false;
						RightNum-=RightDeskCard.length;
						
						if(RightNum<=0)//出完了牌
						{
							Formulary();
							WinPos = RightPlaryPos;
					//		System.out.println("判断胜负!!!!");
							WINS = true;
							if(LordPos !=RightPlaryPos && MyPos != LordPos)//右不是地主.我也不是
							{
						//		System.out.println("我赢了");
								WINS = false;
								WIN_LOSE = true;
							}
							else //他是地主
							{
						//		System.out.println("我输了");
								WINS = true;
								WIN_LOSE = false;
							}
							if(LordPos==MyPos)
								WINS = false;
							for (int i = 0; i < PlayerInfo.length; i++) {
								int S = 0;
								if(WINS)
								{
									if(i==LordPos)
										S = Score;
									else
										S = -(Score/2);
								}
								else 
								{
									if(i==LordPos)
										S = -Score;
									else
										S = Score/2;
								}
								AllScroe[(LordPos+i)%3] = +S;
					
							}
							STATE = JUDGE;
							
//							int temps=Score;
//							if(LordPos != MyPos)//我是地主
//								temps/=2;
				//			P.SitDeskInfo[MyPos][1] = ""+(Integer.parseInt(P.SitDeskInfo[MyPos][1])-temps); 
//							Hashtable h = new Hashtable();
//							h.put("Type", "SYNC");
//							h.put("Cmd", "PLAY");
//							h.put("UID", ""+P.userID);
//							
//							String Data;
//							Data=MyCard.length+"|";
//							for (int i = 0; i < MyCard.length; i++) 
//							{
//								Data+=MyCard[i]+"|";
//							}
//
//							h.put("DATA", ""+Data);
//							new XConnection(false,P.MID,h);
						}
					}
					ThisPos++;
					break;
			}
		}
		else if(Type.equals("OVER"))
		{
			switch (Integer.parseInt(cutLineFromContent())) 
			{
				case 3:
					int DESKID = Integer.parseInt(cutLineFromContent());
					P.SetClue("["+PlayerInfo[DESKID][0]+"]逃跑了", "", "关闭");
					break;
			}
		}
		else if(Type.equals("QUIT"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
//				case 1:
//					Score = 3*50*multiple*2;
//		//			P.SitDeskInfo[MyPos][1] = ""+(Integer.parseInt(P.SitDeskInfo[MyPos][1]) - Score);
//					P.h = new Hashtable();
//					P.h.put("Type", "SYNC");
//					P.h.put("Cmd", "DESK");
//					P.h.put("UID", ""+P.userID);
//					P.h.put("Desk-ID", ""+ P.DeskID);
//					P.SetWait("读取桌子数据");
//					P.CLUE = false;
//			//		P.SwitchState(P.Deskinfo);
//					break;
				case 2:
					P.SetClue("退出", "", "关闭");
					break;
				case 3://对方有人逃跑
					if(STATE!=WIN)
					{
						ThisPos = Integer.parseInt(cutLineFromContent());//逃跑人位置
						P.SetClue(PlayerInfo[ThisPos][0]+"逃跑了", "", "关闭");
						STATE = WIN;
						QuitStr = "逃跑";
						Score = 3*50*multiple*2;
						for (int i = 0; i < PlayerInfo.length; i++) {
							if(i!=ThisPos)
							{
								PlayerInfo[i][3] = ""+(Integer.parseInt(PlayerInfo[i][3])+Score/2);
								P.SitDeskInfo[i][1] = 
									""+(Integer.parseInt(P.SitDeskInfo[i][1])+Score/2);
							}
							else 
							{
								PlayerInfo[i][3] = ""+(Integer.parseInt(PlayerInfo[i][3])-Score);
								P.SitDeskInfo[i][1] = 
									""+(Integer.parseInt(P.SitDeskInfo[i][1]) - Score);
							}
						}
					}
					break;
			}
		}
		else if(Type.equals("DROP"))
		{
			switch (Integer.parseInt(cutLineFromContent()))
			{
				case 3:
					if(STATE!=WIN)
					{
						ThisPos = Integer.parseInt(cutLineFromContent());
						P.SetClue(PlayerInfo[ThisPos][0]+"掉线了", "", "关闭");
						P.SitDeskInfo[ThisPos][0] = "-1";
						QuitStr = "掉线";
						Score = 3*50*2;
						STATE = WIN;
						for (int i = 0; i < PlayerInfo.length; i++) {
							if(i!=ThisPos)
							{
								PlayerInfo[i][3] = ""+(Integer.parseInt(PlayerInfo[i][3])+Score/2);
							}
							else 
								PlayerInfo[i][3] = ""+(Integer.parseInt(PlayerInfo[i][3])-Score);
						}
					}
					break;
			}
		}
	}
	public String cutLineFromContent() {

		String result;
		int index = cutLine.indexOf('|');
		if (index == -1) {
			result = cutLine;
			cutLine = "";
			return result;
		} else {
			result = cutLine.substring(0, index);
			cutLine = cutLine.substring(index+1);
			return result;
		}
	}
	public String cutLineFromContent2() {

		String result;
		int index = cutLine.indexOf('X');
		if (index == -1) {
			result = cutLine;
			cutLine = "";
			return result;
		} else {
			result = cutLine.substring(0, index);
			cutLine = cutLine.substring(index+1);
			return result;
		}
	}
	/**
	 * 整理MYCARD
	 *
	 */
	private void TrimMyCard()
	{
		int temp[] = new int[20];//MYCARD临时数据
		int index=0;
		for (int i = 0; i < MyCard.length; i++) //整哩MYCARD
		{
			if(MyCard[i]!=-1)
			{
			  temp[index] = MyCard[i];
			  index++;
			}
		}
		
		for (int i = 0; i < index; i++) 
		{
			MyCard[i] = temp[i];
		}
	}
	/**
	 * 排序
	 * @param card
	 * @param Remainder
	 */
	private void SequenceCard(int card[],int Remainder)
	{

		int temp2=0;
		for (int i = 0; i < Remainder; i++) 
		{
			for (int j = 0; j < Remainder; j++) 
			{
				if(card[i]>51 && card[i] > card[j])//判断鬼牌排序
				{
					temp2 = card[i];
					card[i] = card[j];
					card[j] = temp2;
				}
				else if(card[i]%13 > card[j]%13 && card[j]<52)
				{
					temp2 = card[i];
					card[i] = card[j];
					card[j] = temp2;
				}
			}
		}
	}
	
	private void SequenceDeskCard(int Card[],int MAX,int CardNum)
	{
		int kk = MAX;
		int temp = 0;
		for (int j = 0; j < CardNum; j++) 
		{
			if(Card[j]%13 == kk)
			{
				
				for (int k = 0; k < CardNum; k++) {
					if(Card[k]%13 != kk)
					{
						temp = Card[j] ;
						Card[j] = Card[k];
						Card[k] = temp;
					}
				}
			}
		}
	}

	/**
	 * 每个回合初次化
	 *
	 */
	private void RoundInit()
	{
		if(是否有出牌)
		{
			if(Remainder>10)
			{
				if(Remainder-signIndex<=10)
				{
					selectIndex = 0;
				}
			}
			
			for (int i = 0; i < signIndex; i++) 
			{
				MyCard[sign[i]] = -1;
			}
			DeskCard = new int[signIndex];
			System.arraycopy(SendCard, 0, DeskCard, 0, signIndex);
	//		SequenceCard(DeskCard,signIndex);
			DeskCardLen = signIndex;
			Remainder -= signIndex;
			if(Remainder<=0)
			{
				WinPos = MyPos;
		//		System.out.println("判断胜负,我赢了");
				Formulary();
				int temps=Score;
				if(LordPos == MyPos)
					WINS = true;
				else
					WINS = false;
				for (int i = 0; i < PlayerInfo.length; i++) {
					int S = 0;
					if(WINS)
					{
						if(i==LordPos)
							S = Score;
						else
							S = -(Score/2);
					}
					else 
					{
						if(i==LordPos)
							S = -Score;
						else
							S = Score/2;
					}
					AllScroe[(LordPos+i)%3] = +S;
				}
				
				if(LordPos != MyPos)//我不是地主
				{
					WINS = false;
					temps/=2;
				}
	//			P.SitDeskInfo[MyPos][1] = ""+(Integer.parseInt(P.SitDeskInfo[MyPos][1])+temps); 
				STATE = JUDGE;
				WIN_LOSE = true;
				Hashtable h = new Hashtable();
				h.put("Type", "SYNC");
				h.put("Cmd", "OVER");
				h.put("UID", ""+P.userID);
				h.put("multiple", ""+multiple);
				P.Connection(h);
			}
			signIndex = 0;
			SequenceCard(MyCard,Remainder);
			if(selectIndex>=Remainder)
			{
				selectIndex = Remainder-1;
			}
			TrimMyCard();
			ThisCardType = -1;
		//	MyCardType = -1;
			ColckTime  = Time;
			LeftBet = -1;
			RightBet = -1;
			MyBet = -1;
		}
		else
		{
			for (int i = 0; i < signIndex; i++) 
			{
				SendCard[i] = 0;
			}
			signIndex = 0;
		}
		
		for (int i = 0; i < signY.length; i++) {//清空选择坐标
			signY[i] = 0;
		}
		没有牌比上家大 = true;
		LeftPass = false;
		RightPass = false;
	}
	private int Formulary()
	{
		if(WIN_LOSE)//我赢
		{
			if(LordPos == MyPos)//我是地主
			{
				if(LeftNum==17 && RightNum==17)//两家没出过牌
					春天++;
			}
			else//我不是地主
			{
				if((LordPos == LeftPlaryPos && LeftNum==20) || (LordPos == RightPlaryPos && RightNum==20))
				{
					反春++;
				}
			}
		}
		multiple*=春天*反春;
		Score = CallScore*50*multiple;
		return 0;
	}
	public void Input(int key)
	{
//		if(Option)
//		{
//			KeyOption(key);
//			return;
//		}
//		else if(STATE != JUDGE && STATE != SHOW &&
//					STATE != WIN && P.LeftCom == key && !playerInfo)//启动菜单
//		{
//			Option = !Option;
//			return;
//		}
		if(playerInfo)//查看玩家资料状态
		{
			KeyplayerInfo(key);
			return;
		}
		switch (STATE) 
		{
			case BET: KeyBET(key); break;
	
			case PLAY: 
			case WAITPLAY: KeyPLAY(key);break;
			
			case JUDGE: KeyJUDGE(key);break;
				
			case SHOW:KeySHOW(key);break;
			
			case WIN:KeyWin(key);break;
		}

	}
	private void KeyplayerInfo(int key)
	{
		P.KeyDeskInfo(key);	
	}
	private void KeyWin(int key)
	{
		switch (key) 
		{
			case Platform.RightCom:
				
//				P.h.clear();
//				P.h.put("Type", "SYNC");
//				P.h.put("Cmd", "DESK");
//				P.h.put("UID", ""+P.userID);
//				P.h.put("Desk-ID", ""+ P.DeskID);
//				P.CLUE = false;
//		//		P.SwitchState(P.Deskinfo);
//				P.SetWait("读取桌子数据");
				P.SwitchState(P.Deskinfo, false);
				break;
		}
	}
//	public void KeyOption(int key)
//	{
//		switch (key) 
//		{
//			case Platform.UP:
//				OptionIndex--;
//				if(OptionIndex<0)
//					OptionIndex = 1;
//				break;
//			case Platform.DOWN:
//				OptionIndex++;
//				if(OptionIndex>1)
//					OptionIndex = 0;
//				break;
//			case Platform.Enter:
//				switch (OptionIndex) 
//				{
//					case 0:
//						playerInfo = true;
//			//			Option = false;
//						break;
//
//					case 1:
//						Quit = true;
//						P.SetClue("是否强制退出?", "是", "否");
//						break;
//				}
//				break;
//				
//			case Platform.RightCom:
//			//	Option = false;
//				Quit = false;
//				break;
//			case Platform.LeftCom:
//				if(Quit)
//				{
//					P.h.clear();
//					P.h.put("Type", "SYNC");
//					P.h.put("Cmd", "QUIT");
//					P.h.put("UID", ""+P.userID);
//					P.h.put("MULTIPLE", ""+multiple);
//					P.CLUE = false;
//					P.SwitchState(P.Deskinfo,true);
//					P.SetWait("请稍后");
//				}
//			//	Option = false;
//				break;
//		}
//	}
	private void KeyJUDGE(int key)
	{
		if(key == P.RightCom)
		{
			 Hashtable h = new Hashtable();
			h.put("Type", "SYNC");
			h.put("Cmd", "DESK");
			h.put("UID", ""+P.userID);
			h.put("Desk-ID", ""+ P.DeskID);
			P.CLUE = false;
			P.SwitchState(P.Deskinfo,true);
			P.SetWait("读取桌子数据");
			P.Connection(h);
		}
	}
	private void KeySHOW(int key)
	{
//		if(key == P.RightCom)
//		{
//		P.h = new Hashtable();
//		P.h.put("Type", "SYNC");
//		P.h.put("Cmd", "DESK");
//		P.h.put("Desk-ID", ""+ P.DeskID);
//		P.h.put("UID", ""+P.userID);
//		P.WAIT = true;
//		P.WaitStr = "请稍后";
//		}
	}
	private void KeyPLAY(int key)
	{
		switch (key) 
		{
			case Platform.UP:
				if(selectIndex>9)
					selectIndex -=10;
				break;
			case Platform.DOWN:
				
				if(selectIndex<10 && Remainder>10)
					selectIndex+=10;
				
				if(selectIndex>Remainder-1)
				{
					selectIndex = Remainder-1;
				}
				
				break;
			case Platform.LEFT:
				
				selectIndex--;
				if(selectIndex<0)
				{
					selectIndex = Remainder-1;
				}
				break;
			case Platform.RIGHT:
				selectIndex++;
				if(selectIndex>Remainder-1)
				{
					selectIndex = 0;
				}
				break;
			case Platform.RightCom:
				if(STATE == PLAY )
				{
					if(PASS && ThisPos!=0)
					{
						MyPass = true;
						ThisPos++;
						是否有出牌 = true;
						 Hashtable h = new Hashtable();
						h.put("Type", "SYNC");
						h.put("Cmd", "PLAY");
						h.put("UID", ""+P.userID);
						String Data="";
						h.put("DATA", ""+Data);
						STATE = WAITPLAY;
						 P.Connection(h);
						
					}
					else
					{
						for (int i = 0; i < signIndex; i++) 
						{
							SendCard[i] = MyCard[sign[i]];
//							System.out.println("我要打出去的牌"+SendCard[i]);
						}

							boolean bool = PlayRule(SendCard,signIndex,true) ;
//							System.out.println("ThisCardType: "+ThisCardType);
//							System.out.println("MyCardType: "+MyCardType);
//							System.out.println("MyCardMax: "+MyCardMax);
//							System.out.println("ThisCardMax: "+ThisCardMax);
//							System.out.println("CardNum: "+CardNum);
//							System.out.println("BOOL: "+bool);
//							System.out.println("ALLPASS:"+ALLPASS);
//							System.out.println("First:"+First);
							if((ALLPASS && bool) || //判断是否合法和对方PASS
							(bool&& MyCardType == 3 && ThisCardType == 3 && MyCardMax>ThisCardMax) ||//判断炸弹对炸弹
							(bool && MyCardType == 3 && ThisCardType!=3)||//判断炸弹对普通牌
							(First && PlayRule(SendCard,signIndex,true))////第一张出的牌
							|| (bool && ThisCardType == MyCardType && MyCardMax>ThisCardMax))//检查是否合法牌和比上家牌大
							{
								ThisPos++;
								是否有出牌 = true;
								First = false;
								 Hashtable h = new Hashtable();
								h.put("Type", "SYNC");
								h.put("Cmd", "PLAY");
								h.put("UID", ""+P.userID);
								String Data=signIndex+"X";
								for (int i = 0; i < signIndex; i++) 
								{
									Data+=SendCard[i]+"X";
								}

								h.put("DATA", ""+Data);
								STATE = WAITPLAY;
								if(ThisPos%3 == LeftPlaryPos)
									LeftDeskCard = null;
								else if(ThisPos%3 == RightPlaryPos)
									RightDeskCard = null;
								P.Connection(h);
							}
							else
								是否有出牌 = false;
							RoundInit();
					}
				}
				break;
				
			case Platform.LeftCom:
				
				break;
			case Platform.Num_7:
				if(STATE ==PLAY)
				{
					for (int i = 0; i < signY.length; i++) {//清空选择坐标
						sign[i] = -1;
						signY[i] = 0;
						signIndex = 0;
					}
					AUTO(ThisPlaryCard);
				}
				break;
			case Platform.Num_9:
				for (int i = 0; i < signY.length; i++) {//清空选择坐标
					sign[i] = -1;
					signY[i] = 0;
					signIndex = 0;
				}
				break;
				
			case Platform.Enter:
				if(signY[selectIndex]==0)
				{
					signY[selectIndex] = -5;
					sign[signIndex] = selectIndex;
					signIndex++;
				}
				else
				{
					signIndex--;
					for (int i = 0; i < sign.length; i++) {
						if(sign[i]==selectIndex)
						{
							sign[i] = sign[signIndex];
							sign[signIndex] = -1;
						}
					}
					signY[selectIndex] = 0;
				
				}
				break;
		}
	}
	private void KeyBET(int key)
	{
		switch (key) 
		{
			case Platform.RIGHT:
				BetIndex++;
				if(BetIndex>3)
					BetIndex = 0;
				break;
	
			case Platform.LEFT:
				BetIndex--;
				if(BetIndex<0)
					BetIndex = 3;
				break;
				
			case Platform.Enter:
				 Hashtable h = new Hashtable();
				h.put("Type", "SYNC");
				h.put("Cmd", "BET");
				h.put("UID", ""+P.userID);
				h.put("Score", ""+BetIndex);
				 P.Connection(h);
				if(BetIndex==3)//如果叫了三分
				{
		//			STATE = PLAY;
					Remainder+=3;
				}
				else
					STATE = WAITCALL;
				MyBet = BetIndex;
				ThisPos++;
			//	STATE = WAIT;
				STATE = WAITCALL;
				break;
		}
	}
}
