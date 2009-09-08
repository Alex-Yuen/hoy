package mobi.samov.client.game;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Card 
{
	//---------------------��Ϸ״̬--------------------------//
	private final byte BET = 1;//�з�
	private final byte DEAL = 0;//����
	private final byte PLAY = 2;//����
	private final byte WAITCALL=3;//�ȴ��з�
	private final byte WAITPLAY=4;//�ȴ��з�
	private final byte JUDGE = 5;//�ж�
	private final byte SHOW = 6;//����
	private final byte STAT = 7;//ͳ��
	private final byte WIN = 8;//�Է�����,..��Ӯ
	boolean playerInfo ;//�����Ϣ
	private final byte WAIT = 20;
	
	private final byte DeskNum = 6;//�����ϵ���һ�л�������
	
	byte STATE;
	private int SW,SH;
	private Image img_BG ;//��Ϸ����
	private Image img_colck;//��
	private Image img_select;//ѡ��
	private Image img_pass;//PASS
	private Image img_bomb,img_bomb2;//ը�����
	private Sprite SP_card;//��
	private Sprite SP_cardNumber;//����
	private Sprite SP_cardColor;//��ɫ
	private Sprite SP_hand;//ͷ��
	private Sprite SP_BET;//�з�
	private Sprite SP_AUTO;//�Զ���ʾ
	private Sprite SP_win_lose;
	private NumImgTools NT_Clock,NT_medium,NT_score;
	private int LeftNum;//���ʣ������
	private int RightNum;//�ұ�ʣ������
	private int Remainder;//��ʣ�������
	
	private int MyCard[]={52,53,26,39,5,6,7,8,9,10,11,12,13,33,20,6,10,35,12,53};//����������
	private int signY[];//���Ʊ��Y����
	private int sign[];//���Ʊ��
	private int LordCard[];//������������
	private int SendCard[];//��������
	private int DeskCard[];//������ʾ��
	private int signIndex;//���Ʊ������
	private int DeskCardLen;//��������
	private boolean �Ƿ��г���;
	private int WHO;//˭����
	private int LeftPlaryCard[];//�ϼ�������
	private int LeftDeskCard[];//�ϼҴ������
//	private int LeftDeskCard[]={0,13,26,39};//�ϼҴ������
	private int ThisPlaryCard[];
	private int LeftPlaryPos;//�ϼ�λ��
	private int RightPlaryPos;//�¼�λ��
	private boolean LeftPass;//���PASS
	private boolean RightPass;//�ұ�PASS
	private int LeftBet;//��߽еķ�
	private int RightBet;//�ұ߽еķ�
	private int MyBet;//�Լ��еķ�
	private boolean MyPass;
	private int RightPlaryCard[];//�¼�������
	private int RightDeskCard[];//�¼Ҵ������
//	private int myself = 1;//�Լ�
	private int selectIndex;//ѡ��������
	private int ColckTime;//����ʱ��
	private int SystemTime;//����ʱ��
	private int MyCardType;//�Ƶ�����,ը��?˳���Ǻ�«
	private int ThisCardType;//��ǰҪ���Ƶ�����,ը��?˳���Ǻ�«
	private int CardNum;//�Է�������
	private int MyCardMax;//�ҵ��Ƶ�����������5552,���5
	private int ThisCardMax;//�Է����������
	private boolean û���Ʊ��ϼҴ�;
	private boolean ALLPASS;
	public int multiple;//����
	private int ����,����;//����
	public int Score;//����
	private int BetIndex;//�з�����
	private boolean PASS;//���½ǳ�������
	private Platform P;
	private boolean First;//˭����
	int WinPos;//Ӯ����λ��
	private int ThisPos;//��ǰ�ж���λ��
	private int PlayPos;//������λ��
	private boolean CLUE;//��ʾ
//	private boolean Option;
	private int OptionIndex;//ѡ������
	public boolean Quit; //�Ƿ�ǿ��
	
	private int ClueTime;
	/**
	 * �����Ϣ    0:�ǳ�,1:�Ա� 2λ��,������,ͷ��ID
	 */
	private String PlayerInfo[][];
	private int MyPos;//�ҵ�λ��
	private int LordPos;//����λ��
	private int EscapePos;//������λ��
	private int CallScore;//�еķ���
	private String ClueStr;
	private boolean WIN_LOSE;//��Ӯֵ
	private int bombY;//ը���ƶ�����
	private String QuitStr="����";//���ܻ������Ϣ
	private boolean BOOLbomb;
	private boolean WINS;//˭Ӯ  F = ũ��,T=����
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
				tempScore = Integer.parseInt(P.SitDeskInfo[i][1]);//�ܷ�
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
//		System.out.println("�ҵ�λ��"+MyPos);
//		System.out.println("�ϼ�λ��"+LeftPlaryPos);
//		System.out.println("�¼�λ��"+RightPlaryPos);
		�Ƿ��г��� = false;
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
		���� = 1;
		���� = 1;
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
		LeftNum=17;//���ʣ������
		RightNum=17;//�ұ�ʣ������
		Remainder=17;//��ʣ�������
		if(WinPos!=-1)//���ǵ�һ��
		{
			RightDeskCard = null;
			ThisPlaryCard = null;
			LeftDeskCard = null;
			if(MyPos==WinPos)//�����һ�ֲ���������һ��λ��
			{
				STATE = BET;
			}
			else
				STATE = WAITCALL;
		}
		else //��һ��
		{
			if(MyPos==0)//�����һ�ֲ���������һ��λ��
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
		//��ͳ��
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
						(SW-((SP_cardNumber.getWidth()+3)*(deskNum)+SP_card.getWidth()))-1, SP_card.getHeight()+45, false,DeskNum);//���ұ����ϵ���
		}
		if(LeftDeskCard!=null)
		{
			DrawCard(g, LeftDeskCard, LeftDeskCard.length, 1, SP_card.getHeight()+45, false,DeskNum);//����ߴ������ϵ���
		}
		if(DeskCard!=null)
		{
			int num = DeskCard.length;
			if(num>8)
				num = 8;
			x = (SW-(num)*SP_cardNumber.getWidth()-SP_card.getWidth())/2;//�������ϵ���X����
			y = SH - P.H2-SP_card.getHeight()*(Remainder/11+1)-SP_card.getHeight()-5-DeskCard.length/8*SP_card.getHeight();
			DrawCard(g, DeskCard, DeskCardLen,x,y,false,8);//���Ҵ���������
		}
//		if(CLUE)
//		{
//			
//		}
		}
		DrawDeskinfo(g);//��������Ϣ
		
//		g.drawImage(img_2, 0, SH-img_2.getHeight(), 0);
		P.Draw2(g, 0, SH-P.H2);
		String temp =  "�׷�"+CallScore+"����"+(multiple);
		g.setColor(0,0,0);
		g.drawString(temp, (SW-P.ft.stringWidth(temp))/2, 
				SH-P.H2+(P.H2-P.FontH)/2, 0);
		
		if(STATE==STAT)
		{
			drawBeautyString(g,"����",SW-P.ft.stringWidth("����")-10,SH-P.H2+(P.H2-g.getFont().getHeight())/2);
			return;
		}
		int selectY = 0;
		if(Remainder<11)
		{
			selectY = SP_card.getHeight();
		}
		drawBeautyString(g,"ѡ��",10,SH-P.H2+(P.H2-g.getFont().getHeight())/2);
		g.drawImage(img_select, (selectIndex%10)*(SP_cardNumber.getWidth()+3), 
				SH-SP_card.getHeight()*2-P.H2-img_select.getHeight()+5+(selectIndex/10)*SP_card.getHeight()+selectY,0);
		DrawElse(g);
	}
	private void DrawClue(Graphics g)
	{
		if(ClueTime>0)
		{
			int w = SH/5;//���ƫ��
			int h = P.FontH+P.FontH/2;//��߶�
			int x = (SW-P.ft.stringWidth(ClueStr))/2;
			ClueTime--;
			g.setColor(0xfde4e4);
			g.fillRect(x-w/2, (SH-h)/2, P.ft.stringWidth(ClueStr)+w,h);
			g.setColor(0xf88d8d);
			g.drawRect(x-w/2, (SH-h)/2, P.ft.stringWidth(ClueStr)+w,h);
			drawBeautyString(g, ClueStr, x, SH/2+(h-P.FontH)/2-h/2);
		}
	}
	private void DrawElse(Graphics g)//������
	{

//		if(Option)
//		{
//	//		String str[] = {"������Ϣ","�������","ǿ���˳�"};
//	//		String str[] = {"�������","ǿ���˳�"};
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
				if(ColckTime==0)//ʱ�䵽����
				{
					if(ALLPASS || First)
					{
						AUTO(MyCard);
						for (int i = 0; i < signIndex; i++) 
						{
							SendCard[i] = MyCard[sign[i]];
			//				System.out.println("��Ҫ���ȥ����"+SendCard[i]);
						}

							boolean bool = PlayRule(SendCard,signIndex,true) ;
							if((ALLPASS && bool) || 
							(bool&& MyCardType == 3 && ThisCardMax == 3 && MyCardMax>ThisCardMax) ||
							(bool && MyCardType == 3 )||
							(First && PlayRule(SendCard,signIndex,true))  || 
										(bool && ThisCardType == MyCardType 
											&& MyCardMax>ThisCardMax))//����Ƿ�Ϸ��ƺͱ��ϼ��ƴ�
							{
								ThisPos++;
								�Ƿ��г��� = true;
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
								�Ƿ��г��� = false;
							RoundInit();
					}
					else
					{
						MyPass = true;
						ThisPos++;
						�Ƿ��г��� = true;
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
				
//				if(û���Ʊ��ϼҴ�)
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
					if(signY[i]==-5)//�����ѡ���ƾ�Ϊ����ѡ��
					{
						PASS = false;
						break;
					}
				}
				if(ThisPos==0 || ALLPASS)
					PASS = false;
						
				if(PASS==false)
					drawBeautyString(g,"����",SW-P.ft.stringWidth("����")-10,fontY);
				else
					drawBeautyString(g,"����",SW-P.ft.stringWidth("����")-10,fontY);
				

				g.setColor(255,255,255);
				g.fillRect((SW-P.ft.stringWidth("���ҳ�����"))/2-15, SH-P.H2+1, P.ft.stringWidth("���ҳ�����")+30, P.H2-2);
				g.setColor(255,0,0);
				g.drawString("���ҳ�����", (SW-P.ft.stringWidth("���ҳ�����"))/2, SH-P.H2+5, 0);
				break;
			case WAITCALL:
				ClueTime = 5;
				ClueStr = "�ȴ�["+PlayerInfo[ThisPos%3][0]+"]�з�";
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
				g.drawString("���Ժ�",(SW-P.ft.stringWidth("���Ժ�"))/2, SH/2, 0);
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
					g.drawString("���֣�"+"���������ܷ֣�", 
							x-1, SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+5, 0);
					
					if(WINS && i!=LordPos)
					{
							NT_score.sprite.setFrame(10);
							NT_score.sprite.setPosition(x+P.ft.stringWidth("���֣�"), 
									SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
							NT_score.sprite.paint(g);

					}
					else if(i==LordPos && !WINS)
					{
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("���֣�"), 
								SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					int tempScore = Score;//���ֻ���
					if(i!=LordPos)
						tempScore /=2;

					
					NT_score.drawNums(g, tempScore, x+P.ft.stringWidth("���֣�")+NT_score.getW()+2,
							SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);
					int tempY = 0;
					if(AllScroe[(LordPos+i)%3]<0)//��-��
					{
						tempY = NT_score.getW();
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("���֣����������ܷ֣�"), 
								SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					NT_score.drawNums(g, Math.abs(AllScroe[(LordPos+i)%3]), x+P.ft.stringWidth("���֣����������ܷ֣�")+tempY,
							SP_win_lose.getHeight()+15+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);

				}
			//	g.drawImage(img_bomb, 30,  SP_win_lose.getHeight()+(SP_hand.getHeight()*6), 0);
				drawBeautyString(g,"����",SW-P.ft.stringWidth("����")-10,fontY);
				break;
				
			case SHOW:
				drawBeautyString(g,"����",SW-P.ft.stringWidth("����")-10,fontY);
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
					g.drawString("���֣�"+"���������ܷ֣�", 
							x-1, SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+5, 0);
					
					int tempScore = Score;
					if(i>0)
						tempScore/=2;
					if(i==0)
					{
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("���֣�"), 
								SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					NT_score.drawNums(g, tempScore, x+P.ft.stringWidth("���֣�")+NT_score.getW()+2,
							SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);
					
					int index = -1;
					int tempScore2=0;
					index = PlayerInfo[(ThisPos+i)%3][3].indexOf("-");
					
					if(index==-1)
					{
						tempScore2 = Integer.parseInt(PlayerInfo[(ThisPos+i)%3][3]);//�ܷ�
					}
					else
					{
						tempScore2 = Integer.parseInt(PlayerInfo[(ThisPos+i)%3][3].substring(1));
					}
					if(index!=-1)//��-��
					{
						index = NT_score.getW()+5;
						NT_score.sprite.setFrame(10);
						NT_score.sprite.setPosition(x+P.ft.stringWidth("���֣����������ܷ֣�"), 
								SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH());
						NT_score.sprite.paint(g);
					}
					NT_score.drawNums(g, tempScore2, x+P.ft.stringWidth("���֣����������ܷ֣�")+index,
							SP_win_lose.getHeight()+i*(SP_hand.getHeight()*2)+SP_hand.getHeight()+P.ft.getHeight()-NT_score.getH(), 2);

				}
		//		g.drawImage(img_bomb, 30,  SP_win_lose.getHeight()+(SP_hand.getHeight()*6), 0);
				drawBeautyString(g,"����",SW-P.ft.stringWidth("����")-10,fontY);
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
				
				if(Card[i]!=52 && Card[i]!=53)//�����ڹ�ʱ��Ż���ɫ
				{
				SP_cardColor.setFrame(Card[i]/13);
				SP_cardColor.setPosition(x+(i%newline)*(SP_cardNumber.getWidth()+3)+2, y+(i/newline)*SP_card.getHeight()+SP_cardNumber.getHeight()+5+temp2);
				SP_cardColor.paint(g);
				temp=0;
				if(SP_cardColor.getFrame()==1 || SP_cardColor.getFrame()==3)//����Ǻ���
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
		//---------��������������------------//
		int x = SP_card.getWidth()+SP_cardNumber.getWidth()*2+4;//�����ƾ���
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
				if(SP_cardColor.getFrame()==1 || SP_cardColor.getFrame()==3)//����Ǻ���
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
		//-------------��COLCK---------------//
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
		
		//-------------------��ʣ�����--------------
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
//		----------------------���еķ�-----------------//
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
	//	DrawClue(g);//����Ϸ�����ʾ
		//-----------��ը��----------
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
		
//		if(û���Ʊ��ϼҴ�)
//		{
//			Platform.drawBeautyString(g, "û���Ʊ��ϼҴ�", 50, 200);
//		}
	}
	/**
	 * �ж��Ƿ�Ϸ�����
	 * @return
	 */
	private boolean PlayRule(int Card[],int CardNum,boolean boo)
	{
		//boo = true//���ҳ��������
		SequenceCard(Card,CardNum);
		boolean bool = false;
		if(Remainder<this.CardNum)
			return false;
//		if(signIndex != CardNum)
//			return bool;
//		System.out.println("�Ƶ�����:"+CardNum);
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
		else if(CardNum==2)//����
		{
	//		System.out.println("�Ƿ����");
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
				//	System.out.println(MyCardMax+" ը��BOMB");
				}
				else
				{
					if(boo)
					{
						MyCardMax = Card[0]%13;
						MyCardType = 1;
				//		System.out.println(MyCardMax+" һ��");
					}
					else
					{
						ThisCardMax = Card[0]%13;
						ThisCardType = 1;
						this.CardNum = 2;
				//		System.out.println(ThisCardMax+" һ��");
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
					System.out.println(MyCardMax+" ����");
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 1;
					System.out.println(ThisCardMax+" ����");
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
			//		System.out.println(MyCardMax+" ը��BOMB");
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 3;
			//		System.out.println(ThisCardMax+" ը��BOMB");
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
						System.out.println(MyCardMax+" ����һ");
					}
					else
					{
						ThisCardMax = Card[1]%13;
						ThisCardType = 4;
						this.CardNum = 4;
						System.out.println(ThisCardMax+" ����һ");
						SequenceDeskCard(Card, ThisCardMax,CardNum);
					}
					bool =  true;
				}
			}
		}
		else if(CardNum>=5)
		{
			//----------��«---------//
			int ���� = 0;
			int ���� = 0;
			int һ�� = 0;
			int index=0;
			int n=0;
			int MAX=-1;
			int ��������[]=new int[Card.length];
			int ��������[]=new int[Card.length];
			for (int i = 0; i < CardNum; i++) 
			{
				index=0;
				n=1;
				��������[i] = -1;
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
						һ��++;
						System.out.println("��"+һ��+"һ��");
						break;
					case 3:
						System.out.println("��"+����+"������");
						��������[����]=i;
						if(Card[i]%13>MAX)
							MAX = Card[i]%13;
						����++;
						break;
					case 7:
						����++;
						System.out.println("������");
						MAX = Card[i]%13;
						break;
				}					
			}
			int index2=0;
			if(����==1 || ����==1)
			{
				SequenceDeskCard(Card, MAX,CardNum);
			}
			else if(����>1)//�����ŵ����ŵ�����
			{
				for (int i = 0; i < ����; i++) 
				{
					if(��������[i]!=-1)
					{
						for (int j = 0; j < 3; j++) 
						{
							��������[index2] = Card[��������[i]-j];
							Card[��������[i]-j] = -1;
							index2++;
						}
					}
				}

				SequenceCard(��������, ����*3);
				
				for (int i =0; i < ��������.length; i++) {
					if(Card[i] !=-1)
					{
						��������[index2] = Card[i];
						index2++;
					}
				}
				System.arraycopy(��������, 0, Card, 0, Card.length);
			}
			//-----------------------------
			if(һ��>=3)
			{
				for (int i = 0; i < CardNum/2-1; i++) 
				{
					if(Card[i*2]%13-1 != Card[i*2+2]%13)//����ǲ���������
					{
						System.out.println("����.��û������");
						return false;
					}
					if(boo)
					{
						if(Card[i]%13>MyCardMax)
							MyCardMax = Card[i]%13;
						MyCardType = 7;
						this.CardNum = һ��*2;
					}
					else
					{
						if(Card[i]%13>ThisCardMax)
							ThisCardMax = Card[i]%13;
						ThisCardType = 7;
						this.CardNum = һ��*2;
					}
				}
				System.out.println("��"+һ��+"����"+" |�������"+(MyCardMax+3));
				
				
				return bool = true;
			}
			else if(����>=2 && CardNum%(����*3)==0)
			{
				for (int i = 0; i < CardNum/3-1; i++) 
				{
					if(Card[i*3]%13-1 != Card[i*3+3]%13)//����ǲ���������
					{
						System.out.println("����.��û��������");
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
					ThisCardMax = Card[����*3-1]%13;
					ThisCardType = 8;
					this.CardNum = ����*3;
				}
				System.out.println("��������");
				
				return bool = true;
			}
			else if(����==һ�� && ����!=0 && һ��!=0)//�жϺ�«������«
			{
					if(����==1)
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
							this.CardNum = ����*3+һ��*2;
						}
						
					}
					else
					{
						for (int i = 0; i < ����-1; i++) 
						{
							if(Card[i*3]%13-1 != Card[i*3+3]%13)//����ǲ���������
							{
								System.out.println("����.��û�������ŵĺ�«");
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
							ThisCardMax = Card[����*3]%13;
							ThisCardType = 9;
							this.CardNum = ����*3+һ��*2;
						}
						
					}
				
				return bool = true;
			}
			else if(����>=1 && CardNum%(����*3)!=0  )
			{
				for (int i = 0; i < ����-1; i++) 
				{
					if(Card[i*3]%13-1 != Card[i*3+3]%13)
					{
						System.out.println("����.��û��������һ");
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
					ThisCardMax = Card[����*3-1]%13;
					ThisCardType = 10;
					this.CardNum = ����*3+����;
				}
				System.out.println("��������һ");
				return bool = true;
			}
			else if(����==1 && CardNum==6)//4444 1 2
			{
				System.out.println("��4444 1 2");
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
			else if(����==1 && һ��==2)//4444 11 22
			{
				System.out.println("��4444 11 22");
				if(boo)
				{
					MyCardMax = Card[0]%13;
					MyCardType = 12;
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 12;
					this.CardNum = ����*4+һ��*2;
				}
				return bool = true;
			}	
			
			if(һ��==0 && ����==0)
			{
			//---------------˳--------------//
				System.out.println("--˳---");
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
					System.out.println(MyCardMax+" ˳");
				}
				else
				{
					ThisCardMax = Card[0]%13;
					ThisCardType = 6;
					this.CardNum = CardNum;
					System.out.println(ThisCardMax+" ˳");
				}
				bool = true;
			}
		}
		if(bool)
			System.out.println("�Ϸ�����");
		return bool;
	}
	/**
	 * �Զ���ʾ 
	 * @param card
	 */
	private void AUTO(int card[])//�Զ���ʾ
	{
		boolean temp123 = true;
		System.out.println("�Զ���ʾ����"+ThisCardType);
		if(Remainder>=CardNum)//�жϳ���
		switch (ThisCardType) 
		{
			case -1:
				System.out.println(Remainder);
				signY[Remainder-1] = -5;
				sign[signIndex] = Remainder-1;
				signIndex++;
				û���Ʊ��ϼҴ� = false;
				return;
		//		break;
			case 0://����
				for (int i = Remainder-1; i >=0; i--) 
				{
					if(MyCard[i]%13 > ThisCardMax || (MyCard[i]>=52 && MyCard[i] > ThisCardMax))
					{
						signY[i] = -5;
						sign[signIndex] = i;
						signIndex++;
						û���Ʊ��ϼҴ� = false;
						return;
					}
				}
				break;
			case 1://һ��
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
						û���Ʊ��ϼҴ� = false;
						return;
					}
				}
				break;
			case 2://����
				for (int i = Remainder-1; i >=0; i--) 
				{
					if(MyCard[i]%13 > ThisCardMax && MyCard[i]%13==MyCard[i-1]%13 
							&& MyCard[i]%13==MyCard[i-2]%13)
					{
						û���Ʊ��ϼҴ� = false;
						
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
			case 3://ը��
				if(ThisCardMax>51)//�����..�����ж�
					return;
				
				for (int i = Remainder-1; i >=0; i--) 
				{
					if(MyCard[i]%13<ThisCardMax)
					{
						continue;
					}
					
					for (int j = i; j > i-CardNum; j--) 
					{
						if(j-1<0)//�ж��ǲ��Ǿ�ͷ��
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
								û���Ʊ��ϼҴ� = false;
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
							û���Ʊ��ϼҴ� = false;
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
			case 4://����һ
				boolean three2 = false;//�Ƿ�������
				boolean pair2 = false;//�Ƿ���һ��
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
							û���Ʊ��ϼҴ� = false;
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
							û���Ʊ��ϼҴ� = true;
						}
					}
				}

				break;
			case 5://��«
				boolean three = false;//�Ƿ�������
				boolean pair = false;//�Ƿ���һ��
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
							û���Ʊ��ϼҴ� = false;
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
							û���Ʊ��ϼҴ� = true;
							return;
						}
					}
				}

				break;
			case 6:
				if(ThisCardMax==11)//˳�����A.�����ж������.
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
									if(signIndex==CardNum-1)//���ƿ��Դ�
									{
										sign[signIndex] = j-1;
										signY[j-1] = -5;
										signIndex++;
										û���Ʊ��ϼҴ� = false;
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
				
			case 7://�Զ���ʾ����
				int save=-1;
				for (int i = Remainder-1; i >0; i--) 
				{
					int index=0;

					if(MyCard[i]%13 > ThisCardMax && save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 )//�ж��������û�бȶԷ����Ĵ�����û������
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
							û���Ʊ��ϼҴ� = false;
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
				//û���ƴ�
				for (int i = 0; i < sign.length; i++) {
					sign[i] = -1;
					signY[i] = 0;
				}
				signIndex =0;
				û���Ʊ��ϼҴ� = true;
				break;  
				
			case 8://������
				save=-1;
				for (int i = Remainder-1; i >1; i--) 
				{
					int index=0;

					if(MyCard[i]%13 > ThisCardMax && save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 && MyCard[i-2]%13 == MyCard[i]%13 )//�ж��������û�бȶԷ����Ĵ�����û������
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
							û���Ʊ��ϼҴ� = false;
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
				//û���ƴ�
				for (int i = 0; i < sign.length; i++) {
					sign[i] = -1;
					signY[i] = 0;
				}
				signIndex =0;
				û���Ʊ��ϼҴ� = true;
				break;
			case 9://����«
				save=-1;
				three2 = false;
				pair2 = false;
				for (int i = Remainder-1; i >1; i--) 
				{
					int index=0;

					if(MyCard[i]%13 > ThisCardMax && save !=MyCard[i]%13 &&
						MyCard[i-1]%13 == MyCard[i]%13 && MyCard[i-2]%13 == MyCard[i]%13 )//�ж��������û�бȶԷ����Ĵ�����û������
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
						MyCard[i-1]%13 == MyCard[i]%13 )//�ж��������û�бȶԷ����Ĵ�����û������
					{		
						if(signY[i] == -5)//��ʾ����ͬ����
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
				//û���ƴ�
				if(signIndex!=CardNum)
				{
					for (int i = 0; i < sign.length; i++) {
						sign[i] = -1;
						signY[i] = 0;
					}
					signIndex =0;
					û���Ʊ��ϼҴ� = true;
				}
				else
				{
					û���Ʊ��ϼҴ� = false;
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
						MyCard[i-1]%13 == MyCard[i]%13 && MyCard[i-2]%13 == MyCard[i]%13 )//�ж��������û�бȶԷ����Ĵ�����û������
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
						if(signY[i] == -5)//��ʾ����ͬ����
						{
							continue;
						}
						signY[i] = -5;
						sign[signIndex] = i;
						signIndex++;
						if(signIndex>=CardNum)
						{
							û���Ʊ��ϼҴ� = false;
							return ;
						}
				}
				û���Ʊ��ϼҴ� = true;
				break;
		}
			if(ThisCardMax>51 && CardNum<4 && !û���Ʊ��ϼҴ� && ThisCardType==-1)//�����..�����ж�
				return;
			for (int i = Remainder-1; i >=0; i--) 
			{
				if(ThisCardType == 3 && MyCard[i]%13<ThisCardMax)
				{
					continue;
				}
				
				for (int j = i; j > i-4; j--) 
				{
					if(j-1<0)//�ж��ǲ��Ǿ�ͷ��
					{
						if(signIndex!=2 || signIndex!=4)//ûը��
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
							û���Ʊ��ϼҴ� = false;
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
						û���Ʊ��ϼҴ� = false;
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
	 * �����Է�������Ϣ
	 *
	 */
	String cutLine="";
	public void Analyse(String Type,String content)
	{
		cutLine = content;
		if(Type.equals("DEAL"))//����
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
						Score = S;//�Է��еķ���
			//			ClueStr = "["+PlayerInfo[ThisPos%3][0]+"]����"+Score+"��";
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
					ClueStr = PlayerInfo[LordPos][0]+"��Ϊ�˵���";
					ClueTime = 30;
	//				System.out.println("����λ��"+LordPos);
					if(LordPos==MyPos)
					{
						ThisPos = MyPos;
						Remainder = 20;
			//			System.out.println("���ǵ���");
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
			//			System.out.println("����ǵ���");
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
				//		System.out.println("�ұ��ǵ���");
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
		//			System.out.println("������λ��"+PlayPos);
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
					if(cutLine.equals(""))//����û�д���PASS
					{
						if(PlayPos == LeftPlaryPos)
						{
		//					System.out.println("�ϼ�PASS");
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
		//					System.out.println("�¼�PASS");
							RightPass = true;
							RightDeskCard = null;
							if(LeftPass!=false && RightDeskCard!=null)
								PlayRule(LeftDeskCard, LeftDeskCard.length, false);
							
						}
						if(LeftPass && RightPass)
						{
							STATE = PLAY;
							ALLPASS = true;
		//					System.out.println("ȫ��PASS");
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
				//		System.out.println("�ϼҳ�������Ϊ"+ThisCardType+"���������"+ThisCardMax);
						LeftPass = false;
						LeftNum -= LeftDeskCard.length;
						if(LeftNum<=0)//��������
						{
							Formulary();
							WinPos = LeftPlaryPos;
					//		System.out.println("�ж�ʤ��!!!!");
							if(LordPos !=LeftPlaryPos && MyPos != LordPos)//��߲��ǵ���.��Ҳ����
							{
				//				System.out.println("��Ӯ��");
								WINS = false;
								WIN_LOSE = true;
							}
							else //���ǵ����������ǵ���������
							{
								
				//				System.out.println("������");
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
			//			System.out.println("�¼ҳ�������Ϊ"+ThisCardType);
						RightPass = false;
						First = false;
						RightNum-=RightDeskCard.length;
						
						if(RightNum<=0)//��������
						{
							Formulary();
							WinPos = RightPlaryPos;
					//		System.out.println("�ж�ʤ��!!!!");
							WINS = true;
							if(LordPos !=RightPlaryPos && MyPos != LordPos)//�Ҳ��ǵ���.��Ҳ����
							{
						//		System.out.println("��Ӯ��");
								WINS = false;
								WIN_LOSE = true;
							}
							else //���ǵ���
							{
						//		System.out.println("������");
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
//							if(LordPos != MyPos)//���ǵ���
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
					P.SetClue("["+PlayerInfo[DESKID][0]+"]������", "", "�ر�");
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
//					P.SetWait("��ȡ��������");
//					P.CLUE = false;
//			//		P.SwitchState(P.Deskinfo);
//					break;
				case 2:
					P.SetClue("�˳�", "", "�ر�");
					break;
				case 3://�Է���������
					if(STATE!=WIN)
					{
						ThisPos = Integer.parseInt(cutLineFromContent());//������λ��
						P.SetClue(PlayerInfo[ThisPos][0]+"������", "", "�ر�");
						STATE = WIN;
						QuitStr = "����";
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
						P.SetClue(PlayerInfo[ThisPos][0]+"������", "", "�ر�");
						P.SitDeskInfo[ThisPos][0] = "-1";
						QuitStr = "����";
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
	 * ����MYCARD
	 *
	 */
	private void TrimMyCard()
	{
		int temp[] = new int[20];//MYCARD��ʱ����
		int index=0;
		for (int i = 0; i < MyCard.length; i++) //����MYCARD
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
	 * ����
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
				if(card[i]>51 && card[i] > card[j])//�жϹ�������
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
	 * ÿ���غϳ��λ�
	 *
	 */
	private void RoundInit()
	{
		if(�Ƿ��г���)
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
		//		System.out.println("�ж�ʤ��,��Ӯ��");
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
				
				if(LordPos != MyPos)//�Ҳ��ǵ���
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
		
		for (int i = 0; i < signY.length; i++) {//���ѡ������
			signY[i] = 0;
		}
		û���Ʊ��ϼҴ� = true;
		LeftPass = false;
		RightPass = false;
	}
	private int Formulary()
	{
		if(WIN_LOSE)//��Ӯ
		{
			if(LordPos == MyPos)//���ǵ���
			{
				if(LeftNum==17 && RightNum==17)//����û������
					����++;
			}
			else//�Ҳ��ǵ���
			{
				if((LordPos == LeftPlaryPos && LeftNum==20) || (LordPos == RightPlaryPos && RightNum==20))
				{
					����++;
				}
			}
		}
		multiple*=����*����;
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
//					STATE != WIN && P.LeftCom == key && !playerInfo)//�����˵�
//		{
//			Option = !Option;
//			return;
//		}
		if(playerInfo)//�鿴�������״̬
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
//				P.SetWait("��ȡ��������");
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
//						P.SetClue("�Ƿ�ǿ���˳�?", "��", "��");
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
//					P.SetWait("���Ժ�");
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
			P.SetWait("��ȡ��������");
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
//		P.WaitStr = "���Ժ�";
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
						�Ƿ��г��� = true;
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
//							System.out.println("��Ҫ���ȥ����"+SendCard[i]);
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
							if((ALLPASS && bool) || //�ж��Ƿ�Ϸ��ͶԷ�PASS
							(bool&& MyCardType == 3 && ThisCardType == 3 && MyCardMax>ThisCardMax) ||//�ж�ը����ը��
							(bool && MyCardType == 3 && ThisCardType!=3)||//�ж�ը������ͨ��
							(First && PlayRule(SendCard,signIndex,true))////��һ�ų�����
							|| (bool && ThisCardType == MyCardType && MyCardMax>ThisCardMax))//����Ƿ�Ϸ��ƺͱ��ϼ��ƴ�
							{
								ThisPos++;
								�Ƿ��г��� = true;
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
								�Ƿ��г��� = false;
							RoundInit();
					}
				}
				break;
				
			case Platform.LeftCom:
				
				break;
			case Platform.Num_7:
				if(STATE ==PLAY)
				{
					for (int i = 0; i < signY.length; i++) {//���ѡ������
						sign[i] = -1;
						signY[i] = 0;
						signIndex = 0;
					}
					AUTO(ThisPlaryCard);
				}
				break;
			case Platform.Num_9:
				for (int i = 0; i < signY.length; i++) {//���ѡ������
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
				if(BetIndex==3)//�����������
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
