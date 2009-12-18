/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.hoyland.me.ddz.main;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

/**
 *
 * @author Ashrum
 */
public class LordCanvas extends GameCanvas implements CommandListener, Runnable
{
    int gameState =0;//标准着游戏的进行状态.0:未开始;1:叫牌;2;正在进行;3:分出胜负
    int subState = 0;//每个阶段的小阶段
    int playernum = 3;//本游戏的玩家数量
    int activeplayer = -1;//当前的活动玩家
    int leftpai[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};//最多20张牌
	int x=0;//每次画牌时，计算的起点。
	//int leftpai[]=new int[20];
	int state[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};//每张牌是否是被选中
	//boolean isOver=false;//本局是否结束,若结束则绘制各玩家得分情况；确定后，将该变量重新置为false；
	boolean drawPlsReady=false;//画“请按确定按钮”语句。
	boolean isWin=false;//本局结束后，是否赢。
	//
	int sendpai0[]=new int[20];//存放上家每次出的牌
	int sendnum0=0;//每次出牌数

	int sendpai1[]=new int[20];//存放自己每次出的牌
	int sendnum1=0;
	int sendpai2[]=new int[20];//存放下家每次出的牌
	int sendnum2=0;
    boolean playercalled = false;
	boolean ispaused = false;
	//
	int pai3[]={0,0,0};//地主的三张牌

	boolean isLord=false;//是否是地主？

	int num=17;//当初发牌的总数，如果是地主num=20，否则num=17；????
    FLDMidlet lMidlet;
    Graphics g;
	int bgcolor[]={42,199,30};//背景颜色
	int width,height;
	Font f = Font.getDefaultFont();
	int fontHeight;
	int cardw,cardh;
	Image pic1,pic2,pic3,pic4,picq,pick,pica;
    int cardNum=17;//当前所剩牌数
	int arrowPos=0;//箭头位置
	boolean repetition=true;
	//CanvasWaitingRoom cwr;
	//////////////////////
	int mincall=1;//最低叫牌倍数
	//boolean iscall=false;//是否正在叫牌,注意是正在叫牌
	int wcall=40;//叫牌按钮图片宽 “一倍”
	int hcall=20;
	int selected=0;
	Image piccall;//fangxingtupian
	boolean overcall=false;//叫牌是否已结束？若叫牌结束，则可打出牌
	//int callTime=0;//叫牌倍数
	///////////////////////////////
	Image pichat,picboy,picgirl,picback,pichatme,picboyme,picgirlme;
	Image pickingh,picqueenh;
	int dup=1;//上边页边距
	
	Command readyCmd=new Command("开始",Command.ITEM,1);
    Command analysis = new Command("debug", Command.ITEM,1);
    Command suggest  = new Command("提示", Command.ITEM,1);
	Command exitCmd=new Command("退出",Command.EXIT,1);
	Image picsad,pichap;
    LordPlayer lPlayer[] = new LordPlayer[playernum];
    LordServer lServer;
    int callTime=-1;
    boolean isOver = false;  
    int rightType=-1;

    Thread t;
     private Audio audio;
     boolean playingsound=false;

    LordCanvas(FLDMidlet aMidlet) {
        super(true);
        this.lMidlet =  aMidlet;
        setFullScreenMode(true);
        g=this.getGraphics();
		width=getWidth();
		height=getHeight();
		cardw=28;
		cardh=40;
        lServer = new LordServer(this.lMidlet,this);
		//this.addCommand(passCmd);
        this.addCommand(readyCmd);
       // this.addCommand(analysis);
        this.addCommand(exitCmd);
       // this.addCommand(suggest);
       // this.addCommand(anaCmd);
		this.setCommandListener(this);
        g.setFont(Font.getFont(Font.FACE_SYSTEM,Font.STYLE_BOLD, Font.SIZE_SMALL));
         for(int i=0; i!=playernum; ++i)
        {
            lPlayer[i] = new LordPlayer();
            lPlayer[i].init(i);
        }
		try
		{
			pic1=Image.createImage("/fangk.png");
			pic2=Image.createImage("/meih.png");
			pic3=Image.createImage("/hongt.png");
			pic4=Image.createImage("/heit.png");
			picq=Image.createImage("/queen.png");
			pick=Image.createImage("/king.png");
			pica=Image.createImage("/arrow.png");
			piccall=Image.createImage("/call.png");
			///
			pichat=Image.createImage("/hat.png");
			picboy=Image.createImage("/boy.png");
			picgirl=Image.createImage("/girl.png");
			picback=Image.createImage("/back.png");
			//
			pichatme=Image.createImage("/hatme.png");
			picboyme=Image.createImage("/boyme.png");
			picgirlme=Image.createImage("/girlme.png");
			//
			pickingh=Image.createImage("/kingh.png");
			picqueenh=Image.createImage("/queenh.png");
			//
			picsad=Image.createImage("/sad.png");
			pichap=Image.createImage("/laugh.png");
		}
		catch(Exception e){}
		try
		{
			fontHeight=f.getHeight();
			g.setColor(0, 0, 0);////
			//g.drawLine((getWidth()-17)/2, 0, (getWidth()-17)/2, getHeight());///
			isLord=true;
			//num=20;
			cardNum=20;
			arrowPos=5;

			g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
			g.fillRect(0, 0, width, height);
		}
		catch(Exception e)
		{
			System.out.println("Gou zao han shu :"+e.toString());/////////
		}
    }

    public void commandAction(Command c, Displayable d) {
//        if(c==passCmd)
//        {
//           if(this.rightType==0||gameState!=2)
//               return;
//            lPlayer[1].pass = true;
//            activeplayer = 2;
//            Send(gameState,activeplayer);
//        }
        if (c ==readyCmd)
        {
            //重新开始新的游戏
            lServer.reset();
            reset();
            this.reStart();
            this.removeCommand(readyCmd);
          //  this.addCommand(passCmd);
        }
        else if(c==suggest)
        {
           int right=0;
           if(rightType !=0)
           {
               if(lPlayer[0].pass)
                   right = 2;
               else
                   right = 1;
           }
           lServer.ai.getStrategy(1,right);
        }
        else if (c==exitCmd)
        {
            lMidlet.stopGame();
        }
        else if(c==analysis)
        {
            int right=0;
            lServer.lPlayer[1].left=20;
            lServer.lPlayer[1].Cards[0] =52;
            lServer.lPlayer[1].Cards[1] =51;
            lServer.lPlayer[1].Cards[2] =50;
            lServer.lPlayer[1].Cards[3] =44;
            lServer.lPlayer[1].Cards[4] =43;
            lServer.lPlayer[1].Cards[5] =40;
            lServer.lPlayer[1].Cards[6] =36;
            lServer.lPlayer[1].Cards[7] =29;
            lServer.lPlayer[1].Cards[8] =26;
            lServer.lPlayer[1].Cards[9] =24;
            lServer.lPlayer[1].Cards[10] =23;
            lServer.lPlayer[1].Cards[11] =22;
            lServer.lPlayer[1].Cards[12] =20;
            lServer.lPlayer[1].Cards[13] =19;
            lServer.lPlayer[1].Cards[14] =18;
            lServer.lPlayer[1].Cards[15] =11;
            lServer.lPlayer[1].Cards[16] =10;
            lServer.lPlayer[1].Cards[17] =9;
            lServer.lPlayer[1].Cards[18] =2;
            lServer.lPlayer[1].Cards[19] =1;

            lServer.lPlayer[0].sendnum =5;
            lServer.lPlayer[0].sendCards[0]=18;
            lServer.lPlayer[0].sendCards[1]=14;
            lServer.lPlayer[0].sendCards[2]=12;
             lServer.lPlayer[0].sendCards[3]=6;
            lServer.lPlayer[0].sendCards[4]=1;
            lServer.lPlayer[0].sendCards[5]=0;
             lServer.lPlayer[0].sendCards[6]=0;
            lServer.lPlayer[0].sendCards[7]=0;
            lServer.lPlayer[0].sendCards[8]=0;
             lServer.lPlayer[0].sendCards[9]=0;
            lServer.lPlayer[0].sendCards[10]=0;
            lServer.lPlayer[0].sendCards[11]=0;
             lServer.lPlayer[0].sendCards[12]=0;
            lServer.lPlayer[0].sendCards[13]=0;
            lServer.lPlayer[0].sendCards[14]=0;
             lServer.lPlayer[0].sendCards[15]=0;
            lServer.lPlayer[0].sendCards[16]=0;
            lServer.lPlayer[0].sendCards[17]=0;
             lServer.lPlayer[0].sendCards[18]=0;
            lServer.lPlayer[0].sendCards[19]=0;

           lServer.ai.getStrategy(1,right);
        }       
    }
    void stop ()
    {
        t = null;
        lServer.serverthread = null;
    }
    void StartGame() {
        lPlayer[1].score = lMidlet.highScore;
        t = new Thread(this);
        t.start();
        audio = Audio.getInstance();
        if(lMidlet.set[lMidlet.AUDIO]&&!playingsound)
        {
              audio.playSound(Audio.BACK_GROUND);
              playingsound = true;
        }
        else
            playingsound = false;
        lServer = new LordServer(lMidlet,this);
        lServer.StartServer();
        this.removeCommand(readyCmd);
        Send(1,0);
        gameState = 1;//标志着游戏的开始,叫分阶段
        subState = 0;//等待发牌

    }
    void reStart()
    {
       setFullScreenMode(true);
        t = new Thread(this);
        t.start();        
        isOver = false;       
        gameState = 1;//标志着游戏的开始,叫分阶段
        subState = 0;//等待发牌
        Send(1,0);
    }
public void drawCall()
	{
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(getWidth()/2-5-wcall-10-wcall, height-18-cardh-5-16-hcall, 4*wcall+33,hcall+3);
        if(activeplayer == 1&&gameState ==1&&!playercalled)
        {
		g.setColor(255,255,255);//suibiannongde
		g.drawRect(getWidth()/2-5-wcall-10-wcall, height-18-cardh-5-16-hcall, wcall,hcall);
		g.drawRect(getWidth()/2-5-wcall, height-18-cardh-5-16-hcall,wcall,hcall);
		g.drawRect(getWidth()/2+5, height-18-cardh-5-16-hcall,wcall,hcall);
		g.drawRect(getWidth()/2+5+wcall+10, height-18-cardh-5-16-hcall,wcall,hcall);

		g.drawImage(piccall, getWidth()/2-5-wcall-10-wcall+selected*(10+wcall),height-18-cardh-5-16-hcall,Graphics.LEFT|Graphics.TOP);
		//以上是画选中图片
       // g.drawString(Integer.toString(selected), getWidth()/2-5-wcall-10-wcall/2,height-38-cardh-5-16-1,Graphics.BOTTOM|Graphics.HCENTER);
		g.drawString("一倍", getWidth()/2-5-wcall-10-wcall/2,height-18-cardh-5-16-1,Graphics.BOTTOM|Graphics.HCENTER);
		g.drawString("二倍", getWidth()/2-5-wcall/2,height-18-cardh-5-16-1,Graphics.BOTTOM|Graphics.HCENTER);
		g.drawString("三倍", getWidth()/2+5+wcall/2,height-18-cardh-5-16-1,Graphics.BOTTOM|Graphics.HCENTER);
		g.drawString("不叫", getWidth()/2+5+wcall+10+wcall/2,height-18-cardh-5-16-1,Graphics.BOTTOM|Graphics.HCENTER);
        }
	}
    public void drawCallScore()
    {
       	g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(6, dup+48+28+2+45,18,18);
        g.fillRect(getWidth()/2+5+wcall/2,height-53-cardh-5-16-1,18,18);
        g.fillRect(width-28-1, dup+23+28+2+45,18,18);

        if(gameState==1)//叫分阶段叫完的要画分
        {
            for(int i=0;i!=3;++i)
            {
                if(lPlayer[i].callscore!=-1)
                {
                   g.setColor(255,255,255);
                    if(i==0)
                    {
                        g.drawString(String.valueOf(lPlayer[i].callscore)+"分", 6, dup+48+28+2+45, Graphics.LEFT|Graphics.TOP);
                    }
                    else if(i==1)
                    {
                        g.drawString(String.valueOf(lPlayer[i].callscore)+"分", getWidth()/2+5+wcall/2,height-53-cardh-5-16-1, Graphics.LEFT|Graphics.TOP);
                    }
                    else if(i==2)
                    {
                          g.drawString(String.valueOf(lPlayer[i].callscore)+"分",  width-28-1, dup+23+28+2+45, Graphics.LEFT|Graphics.TOP);
                    }
                }

            }
        }

    }
	public void drawReady()
	{
//		g.setColor(123, 23, 56);//suibiannongde
//		if(cwr.readyState[0]==1)
//			g.drawString("已准备", 28+1,dup+13+28+2+10,Graphics.LEFT|Graphics.TOP);
//		if(cwr.readyState[1]==1)
//			g.drawString("已准备", 0,height,Graphics.LEFT|Graphics.BOTTOM);
//		if(cwr.readyState[2]==1)
//			g.drawString("已准备", width-1,dup+13+28+2+45+2,Graphics.RIGHT|Graphics.TOP);

	}
	public void drawPai3()
	{//置顶的三张牌
		int ww=25,hh=34;
		g.setColor(255, 255, 255);
		g.fillRoundRect(width/2-ww/2, dup, ww, hh,6,6);
		g.fillRoundRect(width/2-ww/2-ww-2, dup, ww, hh,6,6);
		g.fillRoundRect(width/2-ww/2+ww+2, dup, ww, hh,6,6);
		g.setColor(0, 0, 0);
		g.drawRoundRect(width/2-ww/2, dup, ww, hh,6,6);
		g.drawRoundRect(width/2-ww/2-ww-2, dup, ww, hh,6,6);
		g.drawRoundRect(width/2-ww/2+ww+2, dup, ww, hh,6,6);
		for(int i=0;i<3;i++)
		{
			if(pai3[i]==53)
			{
				g.drawImage(picq, width/2-ww/2-ww+i*(ww+2), dup, Graphics.LEFT|Graphics.TOP);
				g.drawImage(picqueenh, width/2-ww/2-ww+i*(ww+2)+14, dup+8, Graphics.HCENTER|Graphics.TOP);
			}
			else if(pai3[i]==54)
			{
				g.drawImage(pick, width/2-ww/2-ww+i*(ww+2), dup, Graphics.LEFT|Graphics.TOP);
				g.drawImage(pickingh, width/2-ww/2-ww+i*(ww+2)+14, dup+8, Graphics.HCENTER|Graphics.TOP);
			}
			else
			{
				g.setColor(0, 0, 0);
				g.drawString(int2String(pai3[i]), width/2-ww/2-ww+i*(ww+2), dup, Graphics.TOP|Graphics.LEFT);
				if(pai3[i]%4==1)
				{
					g.drawImage(pic1, width/2-ww/2-ww+i*(ww+2)+11, dup+12, Graphics.HCENTER|Graphics.TOP);
				}
				else if(pai3[i]%4==2)
				{
					g.drawImage(pic2, width/2-ww/2-ww+i*(ww+2)+11, dup+12, Graphics.HCENTER|Graphics.TOP);
				}
				else if(pai3[i]%4==3)
				{
					g.drawImage(pic3, width/2-ww/2-ww+i*(ww+2)+11, dup+12, Graphics.HCENTER|Graphics.TOP);
				}
				else if(pai3[i]%4==0)
				{
					g.drawImage(pic4, width/2-ww/2-ww+i*(ww+2)+11, dup+12, Graphics.HCENTER|Graphics.TOP);
				}
				//
			}
			//g.drawString(String.valueOf(pai3[i]), width/2-ww/2-ww+i*(ww+2), dup+2, Graphics.TOP|Graphics.LEFT);
		}

	}

	public void drawUp()
	{//显示上家信息
		try
		{
		if(lPlayer[0].isLord)
			g.drawImage(pichat, 1,dup,Graphics.LEFT|Graphics.TOP);
		//if(cwr.sex[0].equals("female"))
        if(lPlayer[0].ID==1)//这里画玩家头像,需要改
			g.drawImage(picgirl, 1,dup+13,Graphics.LEFT|Graphics.TOP);
		else
			g.drawImage(picboy, 1,dup+13,Graphics.LEFT|Graphics.TOP);
		g.drawImage(picback, 1,dup+13+28+2,Graphics.LEFT|Graphics.TOP);
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(0, dup+13+28+2+45, width/4, 35);
		g.setColor(123, 23, 56);//suibiannongde
       
		g.drawString(String.valueOf(lPlayer[0].left)+"张", 1, dup+13+28+2+45, Graphics.LEFT|Graphics.TOP);
		g.drawString(lPlayer[0].name, 28+1, dup+1+10, Graphics.LEFT|Graphics.TOP);
		g.drawString(Integer.toString(lPlayer[0].score), 28+1, dup+1+10+fontHeight, Graphics.LEFT|Graphics.TOP);
		}
		catch(Exception e)
		{
			System.out.println("drawUp:"+e.toString());//////////
		}
	}
	public void drawMyself()
	{
		if(lPlayer[1].isLord)
			g.drawImage(pichatme, 1,height-18-cardh-5-16-1-18,Graphics.LEFT|Graphics.BOTTOM);
	//	if(cwr.sex[1].equals("female"))
        if(lPlayer[1].ID==1)
			g.drawImage(picgirlme, 1,height-18-cardh-5-16-1,Graphics.LEFT|Graphics.BOTTOM);
		else
			g.drawImage(picboyme, 1,height-18-cardh-5-16-1,Graphics.LEFT|Graphics.BOTTOM);
		g.setColor(bgcolor[0], bgcolor[1],bgcolor[2]);
		g.fillRect(0, height-18, width, 18);
		g.setColor(123, 23, 56);//suibiannongde
       
		g.drawString(lPlayer[1].name+" "+Integer.toString(lPlayer[1].score),width/2, height, Graphics.HCENTER|Graphics.BOTTOM);
	}
	public void drawDown()
	{//显示下家信息
		if(lPlayer[2].isLord)
			g.drawImage(pichat, width-1,dup,Graphics.RIGHT|Graphics.TOP);
		//if(cwr.sex[2].equals("female"))
        if(lPlayer[2].ID==1)
			g.drawImage(picgirl, width-1,dup+13,Graphics.RIGHT|Graphics.TOP);
		else
			g.drawImage(picgirl, width-1,dup+13,Graphics.RIGHT|Graphics.TOP);
		g.drawImage(picback, width-1,dup+13+28+2,Graphics.RIGHT|Graphics.TOP);
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(width-28-1-40, dup+13+28+2, 40, 35);
		g.setColor(123, 23, 56);//suibiannongde
         if(lPlayer[2].pass)
            g.drawString("不出", width-28-10, dup+13+38+2+45, Graphics.LEFT|Graphics.TOP);
		g.drawString(String.valueOf(lPlayer[2].left)+"张", width-28-1, dup+13+28+15, Graphics.RIGHT|Graphics.TOP);
		g.drawString(lPlayer[2].name,width-28-1, dup+1+10, Graphics.RIGHT|Graphics.TOP);
		g.drawString(Integer.toString(lPlayer[2].score), width-28-1, dup+1+10+fontHeight, Graphics.RIGHT|Graphics.TOP);
	}
    public void clearSends()
    {
       //有两个人不出时，clear
        if(lPlayer[0].pass&&lPlayer[1].pass||
           lPlayer[0].pass&&lPlayer[2].pass||
           lPlayer[2].pass&&lPlayer[1].pass)
        {
           
            for(int i=0;i!=3;++i)
            {
                lPlayer[i].pass= false;
                lServer. lPlayer[i].pass= false;
            }

                sendnum0 = 0;
                sendnum1 = 0;
                sendnum2 = 0;

        g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		int a=40;
        int ww=28,hh=35;
		g.fillRect(28+1,dup+13+28+2, width-56-a, 45);
        g.fillRect(18+1,height-18-cardh-5-16-hh,width-18,hh+2);
        g.fillRect(width/4,dup+13+28+2+45,3*width/4,hh+2);
        }

    }
	public void drawSend0()
	{
		//if(isOver)return;
		if(gameState!=2)return;
		int ww=28,hh=35;
		int a=40;//"left 17"字符串的长度
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(28+1,dup+13+28+2, width-56-a, 45);
		g.setColor(123, 23, 56);//suibiannongde
        if(lPlayer[0].pass)
            g.drawString("不出", 32, dup+13+28+2+45, Graphics.LEFT|Graphics.TOP);

		for(int i=0;i<sendnum0;i++)
		{
			g.setColor(255, 255, 255);
			g.fillRoundRect(28+1+i*11,dup+13+28+2+4, ww, hh,6,6);
			g.setColor(0, 0, 0);
			g.drawRoundRect(28+1+i*11,dup+13+28+2+4, ww, hh,6,6);
			if(sendpai0[i]==53)
			{
				g.drawImage(picq, 28+1+i*11+1,dup+13+28+2+4+1, Graphics.LEFT|Graphics.TOP);
			}
			else if(sendpai0[i]==54)
			{
				g.drawImage(pick, 28+1+i*11+1,dup+13+28+2+4+1, Graphics.LEFT|Graphics.TOP);
			}
			else
			{
				if(sendpai0[i]%4==1)
				{
					g.setColor(255, 0, 0);
					g.drawImage(pic1, 28+1+i*11+1,dup+13+28+2+4+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai0[i]%4==2)
				{
					g.setColor(0, 0, 0);
					g.drawImage(pic2, 28+1+i*11+1,dup+13+28+2+4+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai0[i]%4==3)
				{
					g.setColor(255, 0, 0);
					g.drawImage(pic3, 28+1+i*11+1,dup+13+28+2+4+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai0[i]%4==0)
				{
					g.setColor(0, 0, 0);
					g.drawImage(pic4, 28+1+i*11+1,dup+13+28+2+4+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
                if(!int2String(sendpai0[i]).equals("10"))
                    g.drawString(int2String(sendpai0[i]), 28+1+i*11+1,dup+13+28+2+4, Graphics.TOP|Graphics.LEFT);
                else
                {
                     g.drawString(String.valueOf(1), 28+1+i*11-1,dup+13+28+2+4, Graphics.TOP|Graphics.LEFT);
                     g.drawString(String.valueOf(0), 28+1+i*11+4,dup+13+28+2+4, Graphics.TOP|Graphics.LEFT);
                }
				//
			}
		}
	}
	public void drawSend1()
	{//绘制自己每次出的牌
		//if(isOver)return;

		if(gameState!=2)return;
		int ww=28,hh=35;
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(18+1,height-18-cardh-5-16-hh,width-18,hh+2);
		int n=0;
		if(sendnum1%2==1)
			n=sendnum1/2+1;
		else n=sendnum1/2;
		int xx=(getWidth()-17)/2-n*11;
		int yy=getHeight()-18-cardh-5-16-hh;

        g.setColor(123, 23, 56);//suibiannongde
        if(lPlayer[1].pass)
            g.drawString("不出", 18+1,height-18-cardh-5-16-hh, Graphics.LEFT|Graphics.TOP);

		for(int i=0;i<sendnum1;i++)
		{
			g.setColor(255, 255, 255);
			g.fillRoundRect(xx+i*11,yy,ww,hh,6,6);
			g.setColor(0, 0, 0);
			g.drawRoundRect(xx+i*11,yy,ww,hh,6,6);
			if(sendpai1[i]==53)
			{
				g.drawImage(picq, xx+i*11+1,yy+1, Graphics.LEFT|Graphics.TOP);
			}
			else if(sendpai1[i]==54)
			{
				g.drawImage(pick, xx+i*11+1,yy+1, Graphics.LEFT|Graphics.TOP);
			}
			else
			{
				if(sendpai1[i]%4==1)
				{
					g.setColor(255, 0, 0);
					g.drawImage(pic1, xx+i*11+1,yy+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai1[i]%4==2)
				{
					g.setColor(0, 0, 0);
					g.drawImage(pic2, xx+i*11+1,yy+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai1[i]%4==3)
				{
					g.setColor(255, 0, 0);
					g.drawImage(pic3, xx+i*11+1,yy+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai1[i]%4==0)
				{
					g.setColor(0, 0, 0);
					g.drawImage(pic4, xx+i*11+1,yy+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
                if(!int2String(sendpai1[i]).equals("10"))
                    g.drawString(int2String(sendpai1[i]), xx+i*11+1,yy, Graphics.TOP|Graphics.LEFT);
                else
                {
                    g.drawString(String.valueOf(1), xx+i*11-1,yy, Graphics.TOP|Graphics.LEFT);
                    g.drawString(String.valueOf(0), xx+i*11+4,yy, Graphics.TOP|Graphics.LEFT);
                }
				//
			}
		}
	}
	public void drawSend2()
	{
		//if(isOver)return;
		if(gameState!=2)return;
		int ww=28,hh=35;
		int a=40;//"left 17"字符串的长度
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(width/4,dup+13+28+2+45,3*width/4,hh+2);
        g.setColor(123, 23, 56);//suibiannongde
         if(lPlayer[2].pass)
            g.drawString("不出", 3*width/4,dup+13+28+2+45, Graphics.LEFT|Graphics.TOP);
		for(int i=0;i<sendnum2;i++)
		{
			g.setColor(255, 255, 255);
			g.fillRoundRect(width-1-17-sendnum2*11+i*11,dup+13+28+2+45, ww, hh,6,6);
			g.setColor(0, 0, 0);
			g.drawRoundRect(width-1-17-sendnum2*11+i*11,dup+13+28+2+45, ww, hh,6,6);
			if(sendpai2[i]==53)
			{
				g.drawImage(picq, width-1-17-sendnum2*11+i*11+1,dup+13+28+2+45+1, Graphics.LEFT|Graphics.TOP);
			}
			else if(sendpai2[i]==54)
			{
				g.drawImage(pick, width-1-17-sendnum2*11+i*11+1,dup+13+28+2+45+1, Graphics.LEFT|Graphics.TOP);
			}
			else
			{
				if(sendpai2[i]%4==1)
				{
					g.setColor(255, 0, 0);
					g.drawImage(pic1, width-1-17-sendnum2*11+i*11+1,dup+13+28+2+45+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai2[i]%4==2)
				{
					g.setColor(0, 0, 0);
					g.drawImage(pic2, width-1-17-sendnum2*11+i*11+1,dup+13+28+2+45+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai2[i]%4==3)
				{
					g.setColor(255, 0, 0);
					g.drawImage(pic3, width-1-17-sendnum2*11+i*11+1,dup+13+28+2+45+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
				else if(sendpai2[i]%4==0)
				{
					g.setColor(0, 0, 0);
					g.drawImage(pic4, width-1-17-sendnum2*11+i*11+1,dup+13+28+2+45+fontHeight, Graphics.LEFT|Graphics.TOP);
				}
                if(!int2String(sendpai2[i]).equals("10"))
				    g.drawString(int2String(sendpai2[i]), width-1-17-sendnum2*11+i*11+1,dup+13+28+2+45, Graphics.TOP|Graphics.LEFT);
                else
                {
                    g.drawString(String.valueOf(1), width-1-17-sendnum2*11+i*11-1,dup+13+28+2+45, Graphics.TOP|Graphics.LEFT);
                    g.drawString(String.valueOf(0), width-1-17-sendnum2*11+i*11+4,dup+13+28+2+45, Graphics.TOP|Graphics.LEFT);
                }
			}
		}
	}
	public void drawArrow()
	{
		if(cardNum==0)return;
		g.drawImage(pica, x+arrowPos*11-1, getHeight()-18-cardh-5, Graphics.BOTTOM|Graphics.LEFT);
	}
	public void clearArrow()
	{
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
		g.fillRect(x+arrowPos*11, getHeight()-18-cardh-5-16, 13, 16);
	}
	public void drawCards()
	{
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////////clear
		g.fillRect(0, getHeight()-18-cardh-5, getWidth(), cardh+5+1);
		if(cardNum==0) return;
		//if(isOver)return;
		//清空牌所能占到的全部画面，包括牌被选中的上面5
		int dh=0;
		//if(isLord)
		{
			int cnt=0;
			for(int i=0;i<cardNum;i++)
			{
				if(state[i]==0) dh=0;
				else if(state[i]==1) dh=5;
				//画每张牌前，都待检查该牌是否被选中？
				//根据牌是否被选中，画相应的牌
					g.setColor(255, 255, 255);
					g.fillRoundRect(x+cnt*11, getHeight()-18-cardh-dh, cardw,cardh, 6, 6);
					g.setColor(0, 0, 0);
					g.drawRoundRect(x+cnt*11, getHeight()-18-cardh-dh, cardw,cardh, 6, 6);
					if(leftpai[i]==53)
					{//queen
						g.drawImage(picq, x+cnt*11+1, getHeight()-18-cardh+1-dh, Graphics.TOP|Graphics.LEFT);
						cnt++;
						continue;
					}
					if(leftpai[i]==54)
					{//king
						g.drawImage(pick, x+cnt*11+1, getHeight()-18-cardh+1-dh, Graphics.TOP|Graphics.LEFT);
						cnt++;
						continue;
					}
					int k=0;
					if(leftpai[i]%4==1)
					{
						g.setColor(255, 0, 0);
						k=leftpai[i]/4+3;
						g.drawImage(pic1, x+cnt*11+1, getHeight()-18-cardh+fontHeight+2*1-dh, Graphics.TOP|Graphics.LEFT);
					}
					else if(leftpai[i]%4==2)
					{
						g.setColor(0, 0, 0);
						k=leftpai[i]/4+3;
						g.drawImage(pic2, x+cnt*11+1, getHeight()-18-cardh+fontHeight+2*1-dh, Graphics.TOP|Graphics.LEFT);
					}
					else if(leftpai[i]%4==3)
					{
						g.setColor(255, 0, 0);
						k=leftpai[i]/4+3;
						g.drawImage(pic3, x+cnt*11+1, getHeight()-18-cardh+fontHeight+2*1-dh, Graphics.TOP|Graphics.LEFT);
					}
					else if(leftpai[i]%4==0)
					{
						g.setColor(0, 0, 0);
						k=leftpai[i]/4+2;
						g.drawImage(pic4, x+cnt*11+1, getHeight()-18-cardh+fontHeight+2*1-dh, Graphics.TOP|Graphics.LEFT);
					}
					//以上是根据牌的值，画出其花色，共红黑方梅四种。

					if(k==11)
						g.drawString("J", x+cnt*11+3, getHeight()-18-cardh+1-dh, Graphics.TOP|Graphics.LEFT);
					else if(k==12)
						g.drawString("Q", x+cnt*11+3, getHeight()-18-cardh+1-dh, Graphics.TOP|Graphics.LEFT);
					else if(k==13)
						g.drawString("K", x+cnt*11+3, getHeight()-18-cardh+1-dh, Graphics.TOP|Graphics.LEFT);
					else if(k==14)
						g.drawString("A", x+cnt*11+3, getHeight()-18-cardh+1-dh, Graphics.TOP|Graphics.LEFT);
					else if(k==15)
						g.drawString("2", x+cnt*11+3, getHeight()-18-cardh+1-dh, Graphics.TOP|Graphics.LEFT);
					else if(k<10)
						g.drawString(String.valueOf(k), x+cnt*11+3, getHeight()-18-cardh+2-dh, Graphics.TOP|Graphics.LEFT);
					else if(k==10)
                    {
						g.drawString(String.valueOf(1), x+cnt*11-1, getHeight()-18-cardh+2-dh, Graphics.TOP|Graphics.LEFT);
                        g.drawString(String.valueOf(0), x+cnt*11+4, getHeight()-18-cardh+2-dh, Graphics.TOP|Graphics.LEFT);
                    }
					//以上是画出每张牌的大小
					cnt++;

			}//end of for
		}
		//else
		//{
		//}

		//以下是，画出当前箭头指向的牌，用蓝色框标出
		if(state[arrowPos]==0) dh=0;
		else if(state[arrowPos]==1) dh=5;
		g.setColor(0, 0, 255);
		if(arrowPos==cardNum-1)
		{
			g.drawRect(x+arrowPos*11,getHeight()-18-cardh-dh, cardw,cardh);
			return;
		}//如果是最后一张牌，则需画出整张牌
		//如果不是最后一张牌，则画出一部分的选中框，如下：
		g.drawLine(x+arrowPos*11,getHeight()-18-cardh-dh,x+arrowPos*11+11,getHeight()-18-cardh-dh);
		g.drawLine(x+arrowPos*11,getHeight()-18-cardh-dh,x+arrowPos*11,getHeight()-18-dh);
		g.drawLine(x+arrowPos*11,getHeight()-18-dh,x+arrowPos*11+11,getHeight()-18-dh);

	}
	public void drawDir()
	{//绘制中间的指示方向
		//int size=20;
		//if(isOver)return;
		g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////////clear
		g.fillRect(0, height-18-cardh-5-16-35-fontHeight-3, width, fontHeight+3);
		g.setColor(0, 0, 0);
		String str="";
		if(activeplayer==0&&gameState==2)str="等待上家出牌...";
		else if(activeplayer==1&&gameState==2)str="请出牌...";
		else if(activeplayer==2&&gameState==2)str="等待下家出牌...";
		g.drawString(str, width/2, height-18-cardh-5-16-35-2, Graphics.BOTTOM|Graphics.HCENTER);
		//g.drawArc(width/2-size/2,height-18-cardh-5-16-35-size, size,size, 0,360);
	}
	public void drawResult()
	{
		g.setColor(104,35,104);
		g.fillRect(width/4-5, height/4-5,width/2+10,height/2+10);
		g.setColor(126,194,254);/////////
		g.fillRect(width/4, height/4, width/2, height/2);
		if(isWin)
			g.drawImage(pichap, width/2, height/2-fontHeight-2, Graphics.HCENTER|Graphics.BOTTOM);
		else
			g.drawImage(picsad, width/2, height/2-fontHeight-2, Graphics.HCENTER|Graphics.BOTTOM);
		String str1="",str2="",str3="";
		int score=callTime*5;

		if(lPlayer[0].isLord)
		{
			if(isWin)
			{
				str1=lPlayer[0].name+"    -"+String.valueOf(score*2)+"分";               
				str2=lPlayer[1].name+"    +"+String.valueOf(score)+"分";
				str3=lPlayer[2].name+"    +"+String.valueOf(score)+"分";            
			}
			else
			{
				str1=lPlayer[0].name+"    +"+String.valueOf(score*2)+"分";
				str2=lPlayer[1].name+"    -"+String.valueOf(score)+"分";
				str3=lPlayer[2].name+"    -"+String.valueOf(score)+"分";
			}
		}
		else if(lPlayer[1].isLord)
		{
			if(isWin)
			{
				str1=lPlayer[0].name+"    -"+String.valueOf(score)+"分";
				str2=lPlayer[1].name+"    +"+String.valueOf(score*2)+"分";
				str3=lPlayer[2].name+"    -"+String.valueOf(score)+"分";
			}
			else
			{
				str1=lPlayer[0].name+"    +"+String.valueOf(score)+"分";
				str2=lPlayer[1].name+"    -"+String.valueOf(score*2)+"分";
				str3=lPlayer[2].name+"    +"+String.valueOf(score)+"分";
			}
		}
		else if(lPlayer[2].isLord)
		{
			if(isWin)
			{
				str1=lPlayer[0].name+"    +"+String.valueOf(score)+"分";
				str2=lPlayer[1].name+"    +"+String.valueOf(score)+"分";
				str3=lPlayer[2].name+"    -"+String.valueOf(score*2)+"分";
			}
			else
			{
				str1=lPlayer[0].name+"    -"+String.valueOf(score)+"分";
				str2=lPlayer[1].name+"    -"+String.valueOf(score)+"分";
				str3=lPlayer[2].name+"    +"+String.valueOf(score*2)+"分";
			}
		}
		g.setColor(0, 0, 0);
		g.drawString(str1, width/2, height/2, Graphics.HCENTER|Graphics.BOTTOM);
		g.drawString(str2, width/2, height/2+2, Graphics.HCENTER|Graphics.TOP);
		g.drawString(str3, width/2, height/2+2+fontHeight, Graphics.HCENTER|Graphics.TOP);

		g.setColor(255, 255, 255);
		g.drawImage(piccall,width/2,3*height/4-2,Graphics.HCENTER|Graphics.BOTTOM);
		g.drawString("确定",width/2,3*height/4-2,Graphics.HCENTER|Graphics.BOTTOM);
	}
	public String int2String(int pai)
	{//将相应的整形数转换为字符串，例如将52转换为黑桃2,当然除了大小王的
		String res="";
		int k=0;
		if(pai%4==0)
			k=pai/4+2;
		else
			k=pai/4+3;
		if(k==11)
			res="J";
		else if(k==12)
			res="Q";
		else if(k==13)
			res="K";
		else if(k==14)
			res="A";
		else if(k==15)
			res="2";
		else if(k<=10)
			res=String.valueOf(k);
		return res;
	}
    public void DrawBoard()
    {
       cardNum = lPlayer[1].left;
       // allot(20);
        try
				{
					//clearArrow();
					//keyPressed();
					int n=0;
					if(cardNum%2==1)
						n=cardNum/2+1;
					else n=cardNum/2;
					x=(getWidth()-17)/2-n*11;
                    drawClear();
					drawCallScore();
					drawCall();
                  

					drawUp();//显示上家信息
					drawMyself();
					drawDown();//显示下家信息
					if(cardNum!=0&&gameState!=1)
					{
						drawArrow();
					}
					drawCards();
					if((gameState==1&&subState ==4)||gameState==2)
						drawPai3();
					
					drawSend0();//
					drawSend1();//
					drawSend2();//
					drawDir();
					if(isOver&&gameState==3)
						drawResult();

					if(drawPlsReady==true)
					{
						g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);
						g.fillRect(0, height/2-fontHeight-3, width,fontHeight+4);
						g.setColor(104,35,104);
						g.drawString("请按“准备”按钮进入下一局！", width/2, height/2,Graphics.BOTTOM|Graphics.HCENTER);
					}/////////////////////
					
					Thread.sleep(200);
                     flushGraphics();
				}
				catch(Exception e)
				{
					System.out.println("yichang run: "+e.toString());
				}
    }
    void drawClear()
    {
        g.setColor(bgcolor[0], bgcolor[1],bgcolor[2]);
		g.fillRect(0, 0, width, height);
    }
    void receive(int state, int substate) {
       this.gameState = state;
       this.subState =substate;
        switch(state)
        {
            case 1://服务器发牌
                if(substate ==0)
                {
                    for(int i=0; i!=playernum;++i)
                       {
                           for(int j=0;j!=17;++j)
                           {
                                lPlayer[i].Cards[j] = lServer.lPlayer[i].Cards[j];
                            }
                            lPlayer[i].left = lServer.lPlayer[i].left;
                            lPlayer[i].callscore = lServer. lPlayer[i].callscore;
                      }
                       for(int i = 0; i!=17;++i)
                      {
                         leftpai[i] = lPlayer[1].Cards[i];
                      }
                     this.activeplayer = lServer.firstcall;
                }
                else if(substate == 1)
                {                
                     this.activeplayer = lServer.activeplayer;
                     if(activeplayer!=1)
                     {
                         lPlayer[activeplayer].callscore = lServer.lPlayer[activeplayer].callscore;
                         activeplayer = ( activeplayer+1)%3;
                         DrawBoard();                                
                         Send(gameState,++subState);
                     }
                }
                else if(substate ==2)
                {
                     this.activeplayer = lServer.activeplayer;
                      if(activeplayer!=1)
                     {
                         lPlayer[activeplayer].callscore = lServer.lPlayer[activeplayer].callscore;
                         activeplayer = ( activeplayer+1)%3;
                         DrawBoard();
                         Send(gameState,++subState);
                     }
                }
                 else if(substate ==3)
                 {
                     this.activeplayer = lServer.activeplayer;
                     if(activeplayer!=1)
                     {
                         lPlayer[activeplayer].callscore = lServer.lPlayer[activeplayer].callscore;
                         activeplayer = ( activeplayer+1)%3;
                         DrawBoard();
                         Send(gameState,++subState);
                     }
                 }

                else if(substate ==4)
                {
                    lPlayer[lServer.lord].isLord = true;
                    for(int i=0;i!=3;++i)
                    {
                        lPlayer[i].callscore = lServer. lPlayer[i].callscore;
                        pai3[i] = lServer.lordcards[i];
                    }

                    DrawBoard();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                      ex.printStackTrace();
                    }
                   if(lServer.lord ==1)
                   {
                        lPlayer[1].left = 20;
                        for(int i = 0;i!=20;++i )
                        {
                            leftpai[i] = lServer.lPlayer[1].Cards[i];
                            lPlayer[1].Cards[i]= lServer.lPlayer[1].Cards[i];
                        }
                   }
                   else
                    lPlayer[lServer.lord].left = 20;
                    callTime = lPlayer[lServer.lord].callscore;
                    gameState = 2;
                    subState = 0;
                    activeplayer = lServer.lord;  
                    if(lServer.lord==1)
                        rightType =0;
                    else
                        rightType =1;
                    DrawBoard();
                    break;
                }
                break;
            case 2:
                activeplayer = subState;
                //这里注意一个问题，activeplayer是指当前玩家,而由于接到服务器的信息,需要改变的是当前玩家的上一个玩家
                switch(activeplayer)
                {                 
                    case 0:
                        if(lPlayer[1].pass&&lPlayer[0].pass)//刚才是玩家2自由出牌,清空桌面
                            clearSends();
                        lPlayer[2].pass = lServer.lPlayer[2].pass;
                        sendnum2 = lServer.lPlayer[2].sendnum;
                        for(int i=0;i!=sendnum2;++i)
                        {
                            sendpai2[i] =  lServer.lPlayer[2].sendCards[i];
                        }
                        lPlayer[2].left = lServer. lPlayer[2].left;
                        break;
                    case 1:
                        if(lPlayer[1].pass&&lPlayer[2].pass)//刚才是玩家0自由出牌,清空桌面
                            clearSends();
                        lPlayer[0].pass = lServer.lPlayer[0].pass;                           
                        sendnum0 = lServer.lPlayer[0].sendnum;
                        for(int i=0;i!=sendnum0;++i)
                        {
                            sendpai0[i] =  lServer.lPlayer[0].sendCards[i];
                        }
                        lPlayer[0].left = lServer. lPlayer[0].left;
                        lPlayer[1].pass = false;
                        if(lPlayer[0].pass&&lPlayer[2].pass)
                           rightType = 0;
                        else
                            rightType =1;
                        break;
                }               
        }
    }

    private void JudgeEnd() {
        if(gameState != 2 )
            return;
        else if(lPlayer[0].left==0)
        {
            if(lPlayer[2].isLord)
                isWin = true;
            else
                isWin = false;
            isOver = true;
        }
         else if(lPlayer[1].left==0)
        {
            isWin = true;
            isOver = true;
        }
         else if(lPlayer[2].left==0)
        {
            if(lPlayer[0].isLord)
                isWin = true;
            else
                isWin = false;
            isOver = true;
        }
        if(isOver)
        {
            DrawBoard();
             try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                      ex.printStackTrace();
                    }
            gameState =3;
        }
    }

    private void KeyPressed() {
        int keyState=getKeyStates();       
        if(activeplayer!=1)
        {
            DrawBoard();
            return;
        }       
        if((keyState&GAME_A_PRESSED)!=0)
        {
             if(this.rightType==0||gameState!=2)
               return;
            lPlayer[1].pass = true;
            activeplayer = 2;
            Send(gameState,activeplayer);
        }
         if((keyState&GAME_B_PRESSED)!=0)
        {
            //提示
            int right=0;
           if(rightType !=0)
           {
               if(lPlayer[0].pass)
                   right = 2;
               else
                   right = 1;
           }
           lServer.ai.getStrategy(1,right);
        }
         if((keyState&GAME_C_PRESSED)!=0)
        {
            lMidlet.stopGame();
        }
          if((keyState&GAME_D_PRESSED)!=0)
        {
            //取消
            for(int i=0;i!=20;++i)
                state[i]=0;
        }
		if((keyState&UP_PRESSED)!=0)
        {
            switch (gameState)
            {
                case 1:
                     return;
                case 2:
                   state[arrowPos]=1;
                   clearSends();
                    break;

            }

        }
        else  if((keyState&DOWN_PRESSED)!=0)
        {
            switch (gameState)
            {
                    case 1:
                        return;
                    case 2:
                        state[arrowPos]=0;
                        clearSends();
                        break;

            }

        }
        else  if((keyState&LEFT_PRESSED)!=0)
        {
            switch (gameState)
            {
                    case 1:
                        selected=(selected+3)%4;
                        break;
                   case 2:
                        clearSends();
                        if(arrowPos==0)
                            arrowPos=cardNum-1;
                        else
                            arrowPos--;
                        break;
                default:
                    break;
            }
        }
        else if((keyState&RIGHT_PRESSED)!=0)
        {
            switch (gameState)
            {
                    case 1:
                        selected=(selected+1)%4;
                        break;
                    case 2:
                         clearSends();
                        if(arrowPos==cardNum-1)
                                arrowPos=0;
                        else
                                arrowPos++;
                        break;
                default:
                    break;

            }
        }
        else if((keyState&FIRE_PRESSED)!=0)
        {           
            switch (gameState)
            {
                 case 1:
                       executeCall(selected,1);
                       activeplayer= (activeplayer+1)%3;
                       playercalled = true;
                       lServer.lPlayer[1].callscore = lPlayer[1].callscore;
                       if(subState==0)
                           subState =1;
                       Send(gameState,++subState);////叫分
                       break;
                 case 2:
                    	
                        int sendpai11[]=new int[20];
                        int sendnum11=0;
                        for(int i=0;i<cardNum;i++)
                        {//找出欲打出的牌
                            if(state[i]==1)
                            {
                                sendpai11[sendnum11]=leftpai[i];
                                sendnum11++;
                            }
                        }
                        if(sendnum11==0)
                            return;
                         if(rightType==0)
                        {
                            GameRules rule = new GameRules();
                            rule.setCards(sendpai11, sendnum11);
                            if(rule.getCardType()==0)
                                return;
                            if(rule.getCardType()==1||rule.getCardType()==2)
                                callTime*=2;
                        }
                         else if(rightType ==1)
                         {
                            GameRules card0=new GameRules();
                            GameRules card1=new GameRules();
                            if(!lPlayer[0].pass)
                                card0.setCards(sendpai0, sendnum0);
                            else
                                card0.setCards(sendpai2, sendnum2);
                            card1.setCards(sendpai11, sendnum11);
                            int type0=card0.getCardType();
                            int type1=card1.getCardType();
                            if(type1==0)
                            {
                                return;
                            }
                            if(type0==type1)
                            {
                                if(type0==2)
                                {
                                    int n0=card0.isBomb();
                                    int n1=card1.isBomb();
                                    if(n0>n1)
                                        return;
                                    callTime*=2;
                                }
                                else if(type0==3)
                                {
                                    int n0=card0.isOne();
                                    int n1=card1.isOne();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==4)
                                {
                                    int n0=card0.isTwo();
                                    int n1=card1.isTwo();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==5)
                                {
                                    int n0=card0.isThree();
                                    int n1=card1.isThree();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==6)
                                {
                                    if(card0.num!=card1.num)
                                    {
                                        return;
                                    }
                                    int n0=card0.is3and1();
                                    int n1=card1.is3and1();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==7)
                                {
                                    if(card0.num!=card1.num)
                                    {
                                        return;
                                    }
                                    int n0=card0.isOneSeq();
                                    int n1=card1.isOneSeq();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==8)
                                {
                                    if(card0.num!=card1.num)
                                    {
                                        return;
                                    }
                                    int n0=card0.isTwoSeq();
                                    int n1=card1.isTwoSeq();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==9)
                                {
                                    if(card0.num!=card1.num)
                                    {
                                        return;
                                    }
                                    int n0=card0.isThreeSeq();
                                    int n1=card1.isThreeSeq();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==10)
                                {
                                    if(card0.num!=card1.num)
                                    {
                                        return;
                                    }
                                    int n0=card0.isPlane();
                                    int n1=card1.isPlane();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                                else if(type0==11)
                                {
                                    if(card0.num!=card1.num)
                                    {
                                        return;
                                    }
                                    int n0=card0.is4and2();
                                    int n1=card1.is4and2();
                                    if(n0>=n1)
                                    {
                                        return;
                                    }
                                }
                            }//end of type0==type1
                            else
                            {                              
                                if(type0==1)
                                {
                                    return;
                                }
                                else if(type0==2)
                                {
                                    if(type1!=1)
                                    {
                                        return;
                                    }
                                }
                                else
                                {
                                    if(type1!=1&&type1!=2)
                                    {
                                        return;
                                    }
                                }
                                 if(type1==1||type1==2)
                                   callTime*=2;
                            }//end of else (type0!=type1)

                         }//end of rithttype==1
                         dealCardsSend(sendpai11, sendnum11);
                         break;
                case 3:
                   // this.removeCommand(passCmd);
                    this.addCommand(readyCmd);
                    int score = callTime*5;
                    if(lPlayer[0].isLord)
                    {
                        if(isWin)
                        {
                            lPlayer[0].score -= score*2;
                            lPlayer[1].score += score;
                            lPlayer[2].score += score;
                        }
                        else
                        {
                            lPlayer[0].score += score*2;
                            lPlayer[1].score -= score;
                            lPlayer[2].score -= score;
                        }

                    }
                    else if(lPlayer[1].isLord)
                    {
                        if(isWin)
                        {                          
                            lPlayer[0].score -= score;
                            lPlayer[1].score += score*2;
                            lPlayer[2].score -= score;
                        }
                        else
                        {
                            lPlayer[0].score += score;
                            lPlayer[1].score -= score*2;
                            lPlayer[2].score += score;
                        }
                    }
                    else if(lPlayer[2].isLord)
                    {
                        if(isWin)
                        {
                            lPlayer[0].score += score;
                            lPlayer[1].score += score;
                            lPlayer[2].score -= score*2;
                        }
                        else
                        {
                            lPlayer[0].score -= score;
                            lPlayer[1].score -= score;
                            lPlayer[2].score += score*2;
                        }
                    }
                    isOver = false;
                    drawClear();
                    this.reset();
                    gameState = 4;
                    this.setFullScreenMode(false);
                    break;
                case 4:
                    break;


               }

        }
        DrawBoard();
    }

    private void Send(int state, int substate) {
        lServer.receive( state,  substate);
    }

    private void dealCardsSend(int []sendcards,int sendnum) {
        int leftnum = cardNum-sendnum;
        int leftcards[] = new int[leftnum];
        for(int i=0,j=0;i!=cardNum;++i)
        {
            if(state[i]==0)
            {
                leftcards[j]= leftpai[i];                
                j++;
            }
            else
                state[i]=0;
            leftpai[i]=-1;
        }
        for(int i=0;i!=leftnum;++i)
        {
            leftpai[i]=leftcards[i];
            lPlayer[1].Cards[i]=leftcards[i];
            lServer.lPlayer[1].Cards[i]=leftcards[i];
        }
        for(int i=0;i!=sendnum;++i)
        {
            sendpai1[i]=sendcards[i];
        }
        cardNum = leftnum;
        lPlayer[1].left = leftnum;
        lServer.lPlayer[1].left = leftnum;
        sendnum1 = sendnum;
        arrowPos = cardNum/2;
        activeplayer = 2;
        rightType = 1;//不能乱出了
        Send(gameState,activeplayer);
    }

    private void executeCall(int sel,int playernum) {
        lPlayer[playernum].callscore =(sel+1)%4;
        if(lPlayer[playernum].callscore>callTime)
            callTime = lPlayer[playernum].callscore;
			lPlayer[playernum].iscall=false;

			g.setColor(bgcolor[0],bgcolor[1],bgcolor[2]);//////
			g.fillRect(getWidth()/2-5-wcall-10-wcall, height-18-cardh-5-16-hcall, 4*wcall+30+1,hcall+1);
			
        DrawBoard();
    }
    public void run() {
       Thread currentThread = Thread.currentThread();
       while(currentThread ==t)
       {
          if(!ispaused)
          {
               clearArrow();
               KeyPressed();
               JudgeEnd();
          }
          if(!lMidlet.set[lMidlet.AUDIO]&&playingsound)
          {
              audio.stopSound(Audio.BACK_GROUND);
              playingsound = false;
          }
          else if(lMidlet.set[lMidlet.AUDIO]&&!playingsound)
          {
              audio.playSound(Audio.BACK_GROUND);
              playingsound = true;
          }
       }
    }

    private void reset() {
       this.stop();
       for(int i=0;i!=3;++i)
       {
           lPlayer[i].isLord = false;
           lPlayer[i].callscore = -1;
           lPlayer[i].left=0;
           lPlayer[i].pass=false;
           lPlayer[i].sendright=-1;

       }
       for(int i=0;i!=20;++i)
           state[i]=0;
       playercalled = false;
       sendnum0 = 0;
       sendnum1 = 0;
       sendnum2 = 0;
    }
}
