import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;
import java.util.Random;

/**
* 游戏主类
*/
public class GameMain extends GameCanvas implements Runnable
{
	protected Paopao midlet=null;
	protected MyLine myLine=null;
	protected Graphics g=null;
	protected Player player = null;
	protected Font font=null;
	protected Random ran=new Random();
	protected projectTimeOut pTimeOut=null;
	
	
	//装载标志、线程运行、暂停、装载关卡、发射泡泡、泡泡飞行状态（是否在飞行中）
	//超时计时器运行标志、是否已NEW了泡泡飞行类、泡泡更新标志（为真时将备用泡泡更新到发射台上）
	//是否随机添加泡泡、线程是否处于繁忙状态
	protected boolean isActive=true;
	protected boolean isPause=false;
	protected boolean isLoading=false;
	protected boolean isSelectOK=false;
	protected boolean isFly=false;
	protected boolean isReckonStart=true;
	protected boolean isNew=false;
	//protected boolean isUpdate=false;
	private boolean isRanAddPP=false;
	protected boolean isBusy=false;
	Thread thread=null;		//绘制线程
	
	//关卡装载进度
	protected int loadPercent = 0;
	
	//游戏得分
	protected int score=0;
	
	//发射泡泡数量
	protected int paopaoCount=0;
	
	//随机添加泡泡数量
	protected int ranAddPPNum=0;
	
	/**
	* 图层与精灵的相关变量
	*/
	protected LayerManager layermanager=null;	//图层管理
	protected PaopaoSprite paopaoSprite=null;	//泡泡精灵
	protected PaopaoSprite newPaopaoSprite=null;	//备用泡泡精灵
	protected PaopaoSprite arrow=null;		//发射台精灵
	protected PaopaoSprite timeOutNum=null;	//超时数字图片
	protected TiledLayer backLayer=null;		//背景图层
	protected Image imgBack=null;				//背景图片
	
	
	//泡泡精灵所在图片上的位置
	protected int paopao_img_x=15;
	protected int paopao_img_y=17;
	
	//泡泡精灵所在屏幕上默认的认x、y坐标，发射台与发射泡泡位置修正
	protected int paopao_new_x,paopao_new_y,paopao_default_x,paopao_default_y;
	protected int revise=8;
	
	//泡泡精灵发射后飞行起始坐标
	protected int startX,startY;
	
	//当前泡泡精灵的颜色（帧）、备用泡泡颜色、默认发台角度（90度角）
	//当前发射台发射的角度对应图片
	protected int paopaoSelectColor;
	protected int paopaoRandomColor;
	protected int arrowDefaultFrame=35;
	protected int arrowSelectFrame=arrowDefaultFrame;
	//protected int arrowArc=35;
	
	//链接属性：图层索引号
	protected int layerID=-1;
	
	/**
	* 泡泡数组，最后一列是标志列，标志当前列是否进行缩进半个精灵宽度，0表是不缩进，1表是缩进
	* 三个层的属性分别为：0层为泡泡层，1层为道具层，2层为挂点层
	*/
	protected byte gameStage[][][]=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW+4][3];;
	
	//当前游戏关卡ID、道具总数、所选道具ID、当前泡泡颜色总数量
	protected int stageIndex=0;
	protected int toolsTotal=0;
	protected int toolsIndex=0;
	protected int colorTotal=8;
	
	//游戏状态标志索引
	protected boolean flag=true;
	
	/**
	* 发射台相关数组设置
	*/
	//所旋转后各角度对应的sin值(放大100000倍)
	protected static final int[] ARROW_ARC_SIN=
	{
		4362,8716,13053,17365,21644,25882,30071,34202,38268,42262,
		46175,50000,53730,57358,60876,64279,67559,70711,73728,76604,
		79335,81915,84339,86603,88701,90631,92388,93969,95372,96593,
		97630,98481,99144,99619,99905,100000,
		99905,99619,99144,98481,97630,96593,95372,93969,92388,90631,
		88701,86603,84339,81915,79335,76604,73728,70711,67559,64279,
		60876,57358,53730,50000,46175,42262,38268,34202,30071,25882,
		21644,17365,13053,8716,4362
		
	};
	//所旋转后各角度对应的cos值(放大100000倍)
	protected static final int[] ARROW_ARC_COS=
	{
		-99905,-99619,-99144,-98481,-97630,-96593,-95372,-93969,-92388,-90631,
		-88701,-86603,-84339,-81915,-79335,-76604,-73728,-70711,-67559,-64279,
		-60876,-57358,-53730,-50000,-46175,-42262,-38268,-34202,-30071,-25882,
		-21644,-17365,-13053,-8716,-4362,0,
		4362,8716,13053,17365,21644,25882,30071,34202,38268,42262,
		46175,50000,53730,57358,60876,64279,67559,70711,73728,76604,
		79335,81915,84339,86603,88701,90631,92388,93969,95372,96593,
		97630,98481,99144,99619,99905
	};
	
	
	/**
	* 构造函数
	*/
	public GameMain(Paopao midlet)
	{
		super(false);
		
		//使用全屏模式
		this.setFullScreenMode(true);
		this.midlet=midlet;
		this.stageIndex=Paopao.stageIndex;
		
		myLine=new MyLine();
		
		//获得图笔
		g = getGraphics();
		
		//图层与精灵相关变量初始化
		initLayerAndSprite();
		
		//起动超时计时器
		pTimeOut=new projectTimeOut();
	}
	
	/**
	* 创建PaopaoSprite类对象
	*/
	protected PaopaoSprite createSprite(String spriteName,int paopaoWidth,int paopaoHeight,int width,int height) 
	{
		return new PaopaoSprite(midlet.createMenuImage(spriteName),paopaoWidth,paopaoHeight,width,height);
	}
	
	/**
	* 图层与精灵相关变量初始化
	*/
	private void initLayerAndSprite()
	{
		//创建背景图片、备用泡泡、发射台泡泡以及发射台
		imgBack=midlet.createMenuImage(Paopao.image_select_back);
		backLayer=new TiledLayer(1, 1, imgBack, Paopao.GAME_IMAGE_WIDTH, Paopao.GAME_IMAGE_HEIGHT);
		backLayer.setCell(0,0,1);
		newPaopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
		paopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
		arrow=createSprite(midlet.IMAGE_ARROW,46,46,27,27);
		timeOutNum=createSprite(midlet.IMAGE_TIMEOUT,24,28,0,0);
		
		
		//图层管理加入泡泡、发射台和背景
		layermanager=new LayerManager();
		layermanager.append(newPaopaoSprite);
		layermanager.append(paopaoSprite);
		layermanager.append(timeOutNum);
		layermanager.append(arrow);
		layermanager.append(backLayer);
		
		//设置备用泡泡、发射台泡泡的默认位置与泡泡飞行的初始位置
		paopao_new_x=40;
		paopao_new_y=184;
		paopao_default_x=(Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH)/2;
		paopao_default_y=Paopao.GAME_IMAGE_HEIGHT-29;
		startX=paopao_default_x;
		startY=paopao_default_y;
		
		//初始化泡泡颜色
		//paopaoRandomColor=Math.abs(ran.nextInt(7))+1;
		//paopaoSelectColor=Math.abs(ran.nextInt(7))+1;
		
		//设置备用泡泡、发射台泡泡、发射台的帧和它们的初始位置
		//newPaopaoSprite.setFrame(paopaoRandomColor);
		newPaopaoSprite.setPosition(paopao_new_x,paopao_new_y);
		//paopaoSprite.setFrame(paopaoSelectColor);
		paopaoSprite.setPosition(paopao_default_x,paopao_default_y);
		arrow.setFrame(arrowSelectFrame);
		arrow.setPosition((Paopao.GAME_IMAGE_WIDTH-46)/2,Paopao.GAME_IMAGE_HEIGHT-40);
		timeOutNum.setPosition(110,170);
		timeOutNum.setVisible(false);
	}
	
	/**
	* 产生随机泡泡颜色
	*/
	protected int randomPaopao()
	{
		boolean isSame=false;
		int j=0;
		int n=layermanager.getSize()-3;		//当前除默认泡泡以外的所有的泡泡数量
		int[] paopaoColor=myLine.getPaopaoColor();	//获取链表中泡泡的颜色种类
		
		//将备份泡泡颜色追加进颜色种类数组中去
		for (int i=0;i<8;i++)
		{
			if (paopaoColor[i]==0) continue;
			if (paopaoColor[i]==paopaoSelectColor) 	//判断泡泡颜色种类数组中是否已存在备份泡泡的颜色
			{
				isSame=true;
				continue;
			}
			j++;
		}
		colorTotal=j+1;
		if (!isSame) paopaoColor[j]=paopaoSelectColor;	//如果备份泡泡的颜色尚未加入数组中，则将它加进去
		if (j==0) return paopaoColor[0];
		return paopaoColor[Math.abs(ran.nextInt(j))+1];		//随机产生限定范围内的泡泡颜色
	}
	
	/**
    * 开始玩指定关卡的游戏（stageIndex 关卡号）
    */
    public void startPlay(int stageIndex)
    {
		//LOAD关卡及初始化相关变量
        new Loading(stageIndex);
		
		//初始化计时器变量为默认值
		isReckonStart=true;
        pTimeOut.setTime();
		timeOutNum.setVisible(false);
		
		isPause=false;
		
        if (thread==null)
        {
            thread=new Thread(this);
            thread.start();
        }
		System.out.println("启动线程");
		midlet.setDisplayable(this);
		System.gc();
    }
	
	/**
	* 重新初始化gameStage数组
	*/
	protected void initializtionGameStageDate()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW+3;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL;j++)
			{
				if (i%2!=0 && j==Paopao.PAOPAO_COL-1)
				{
					gameStage[j][i][0]=1;
					//System.out.print(gameStage[j][i][0]+",");
				}
				else
				{
					gameStage[j][i][0]=0;
					//System.out.print(gameStage[j][i][0]+",");
				}
			}
			//System.out.println();
		}
	}
	
	/**
	* 运行线程
	*/
	public void run()
	{
		
	}

	/**
	* 更新备用泡泡到发射台上
	*/
	protected void updatePaopao()
	{
		startX=paopao_default_x;
		startY=paopao_default_y;
		paopaoSprite=newPaopaoSprite;					//将左边备用的泡泡设置成发射台泡泡
		paopaoSelectColor=paopaoRandomColor;			//将取得备用泡泡的颜色
		paopaoSprite.setPosition(paopao_default_x,paopao_default_y);	//将发射台泡泡移动到发射台上
		paopaoRandomColor=randomPaopao();				//生成备用泡泡的颜色
		newPaopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);	//创建备用泡泡
		layermanager.insert(newPaopaoSprite,0);			//在图层中插入备用泡泡
		//newPaopaoSprite=(PaopaoSprite)layermanager.getLayerAt(0);	//将新插入的泡泡与备用泡泡精灵关联起来
		newPaopaoSprite.setFrame(paopaoRandomColor);	//设置备用泡泡的颜色为刚才生成的颜色
		newPaopaoSprite.setPosition(paopao_new_x,paopao_new_y);	//将刚生成的泡泡移动到备用泡泡原位置上
	}
	
	/**
	* 监听玩家输入信息
	*/
	protected void input()
	{
		if (ranAddPPNum==0)
		{
			int keyStates = getKeyStates();
			
			if ((keyStates & DOWN_PRESSED) != 0) 
			{
				toolsIndex++;
			}
			else if ((keyStates & UP_PRESSED) != 0) 
			{
				toolsIndex--;
			}
			
			if ((keyStates & RIGHT_PRESSED) != 0) 
			{
				arrowSelectFrame++;
			}
			else if ((keyStates & LEFT_PRESSED) != 0) 
			{
				arrowSelectFrame--;
			}
			
			if ((keyStates & FIRE_PRESSED) != 0)
			{
				if (!isFly)	isSelectOK=true;
			}
		}
	}
	
	/**
	* 用户按键事件
	*/
	protected void keyPressed(int keyCode)
	{
		if (ranAddPPNum==0)
		{
			switch (keyCode)
			{
			case Canvas.KEY_NUM2:
				toolsIndex--;
				break;
			case Canvas.KEY_NUM4:
				arrowSelectFrame--;
				break;
			case Canvas.KEY_NUM5:
				if (!isFly)	isSelectOK=true;
				break;
			case Canvas.KEY_NUM6:
				arrowSelectFrame++;
				break;
			case Canvas.KEY_NUM8:
				toolsIndex++;
				break;
			case Canvas.KEY_STAR:
				printArray();
				break;
			}
			if (getKeyName(keyCode).equals("SOFT1"))
			{
				pause();
			}
		}
	}
	
	/**
	* 用户按键事件
	*/
	protected void keyRepeated(int keyCode)
	{
		if (ranAddPPNum==0)
		{
			switch (keyCode)
			{
			case Canvas.KEY_NUM2:
				toolsIndex--;
				break;
			case Canvas.KEY_NUM4:
				arrowSelectFrame--;
				break;
			case Canvas.KEY_NUM5:
				if (!isFly)	isSelectOK=true;
				break;
			case Canvas.KEY_NUM6:
				arrowSelectFrame++;
				break;
			case Canvas.KEY_NUM8:
				toolsIndex++;
				break;
			}
			
			if (getKeyName(keyCode).equals("SOFT1") || getKeyName(keyCode).equals("SOFT2"))
			{
				pause();
			}
			
		}
	}
	
	/**
	* 随机添加N个泡泡
	*/
	protected void randomAddPaopao(int n)
	{
		int[] paopaoPlace=new int[n];		//将要添加的泡泡所在列的ID
		String filtration=",";				//过滤的泡泡列ID
		int x,y,paopaoColor;
		PaopaoSprite tempPP=null;
		y=Paopao.GAME_IMAGE_HEIGHT-22;
		isRanAddPP=true;
		ranAddPPNum=n;
		isReckonStart=false;
		
		for (int i=0;i<n;i++)
		{
			paopaoPlace[i]=getRandomPlace(Paopao.PAOPAO_ROW-1,filtration);
			filtration=filtration+paopaoPlace[i]+",";
		}
		
		for (int i=0;i<n;i++)
		{
			//设置随机泡泡的初始位置
			x=paopaoPlace[i]*Paopao.SPRITE_WIDTH+paopao_img_x;
			tempPP=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
			paopaoColor=randomPaopao();
			layermanager.insert(tempPP,i+2);
			tempPP.setFrame(paopaoColor);
			
			//System.out.println(paopaoColor+" "+(i+2));
			//i+2  插入泡泡所在的层ID，第零层是备用泡泡，每一层是发射台泡泡，所以所有泡泡都是从第二层开始插入的
			new paopaoFly(tempPP,paopaoColor,x,y,arrowDefaultFrame,i+2,n);	
			
		}
	}
	
	/**
	* 获取随机位置
	*/
	private int getRandomPlace(int n,String filtration)
	{
		int randomPlace=ran.nextInt(n);
		if (filtration.indexOf(","+randomPlace+",")==-1)
		{
			return randomPlace;
		}
		return getRandomPlace(n,filtration);
	}
	
	/**
	* 按了发射键或可以发射泡泡了
	*/
	protected void isReckon()
	{
		pTimeOut.setTime();
		timeOutNum.setVisible(false);
		//arrowArc=arrowSelectFrame;
		isSelectOK=false;
		isNew=true;
		isFly=true;
		paopaoCount++;		//总发射泡泡数量
	}
	
	/**
	* 绘制关卡
	*/
	protected void drawStage()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL-1;j++)
			{
				if (gameStage[j][i][0]!=0)
				{
					paopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
					myLine.insertNode(2,i,j,gameStage[j][i][0],1,true);
					layermanager.insert(paopaoSprite,2);
					paopaoSprite.setFrame(gameStage[j][i][0]);
					paopaoSprite.setPosition(getRectX(j,i),getRectY(i));
					
					paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
				}
				//System.out.print(gameStage[j][i][0]+",");
			}
			//System.out.print(gameStage[9][i][0]+",");
			//System.out.println();
		}
	}
	
	/**
	* 获得精灵当前参考点所在格的X轴的坐标
	*/
	private int getRectX(int col,int row)
	{
		int x=0;
		if (gameStage[Paopao.PAOPAO_COL-1][row][0]==1)
		{
			if (col==0) col=1;
			x=col*Paopao.SPRITE_WIDTH-Paopao.SPRITE_WIDTH/2+paopao_img_x;
		}
		else
		{
			x=col*Paopao.SPRITE_WIDTH+paopao_img_x;
		}
		return x;
	}
	
	/**
	* 获得精灵当前参考点所在格的Y轴的坐标
	*/
	private int getRectY(int row)
	{
		return (row*(Paopao.SPRITE_HEIGHT-2)+paopao_img_y);
	}

	/**
    * 转至下一关
    */
    protected void nextStage()
    {
		if (++stageIndex>Paopao.gameStageTotal)
		{
			midlet.gameMenu.setMenuIndex(8);
			midlet.gameMenu.setInfoFrameIndex(6);
			midlet.gameMenu.showMe();
		}
        else
		{
			startPlay(stageIndex);
		}
    }
	
	/**
	* 装载音效
	*/
	private void loadAcousticEffect()
	{
		try
        {
            if (player != null)
            {
                player.close();
                player = null;
            }
            player = Manager.createPlayer(getClass().getResourceAsStream("/onestop.mid"), "audio/midi");
            player.setLoopCount(-1);// 音乐循环
            player.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
	}
    
	/**
    * 暂停
    */ 
    protected void pause()
    {
        isPause = !isPause;
		isReckonStart=!isReckonStart;	//暂停泡泡发射超时计时器
        try
        {
            if (isPause)
			{
				//player.stop();
				midlet.gameMenu.setMenuIndex(4);
				midlet.gameMenu.showMe();
			}
            else
			{
                //player.start();
			}
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
	
	/**
    * 绘制当前屏幕
    */
    protected void drawScreen(Graphics g)
    {
	
	}
	
	/**
	* 绘制得分
	*/
	protected void drawScore()
	{
		g.setColor(0xFFFFFF00);
		g.drawString("得分:"+score,(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2+20,(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2+10,Graphics.TOP|Graphics.LEFT);
	}
	
	/**
	* 同色泡泡多于3个时爆掉
	*/
	protected boolean paopaoBao(int nowCol,int nowRow)
	{
		int[] expressionsOROW={1,0,-1,-1,0,1};	//y轴的左下，左，左上，右上，右，右下位置，坐标点位于不偏移行
		int[] expressionsOCOL={0,-1,0,1,1,1};	//x轴的左下，左，左上，右上，右，右下位置，坐标点位于不偏移行
		int[] expressionsJROW={1,0,-1,-1,0,1};	//y轴的左下，左，左上，右上，右，右下位置，坐标点位于偏移行
		int[] expressionsJCOL={-1,-1,-1,0,1,0};	//x轴的左下，左，左上，右上，右，右下位置，坐标点位于偏移行
		String str;	//保存内容格式：“|x,y|x1,y1|x2,y2|……”，保存要爆掉的泡泡的坐标，用于遍历所黏附的泡泡是否已经比较过
		int[] baoX=new int[90];	//保存要爆掉的泡泡的X轴坐标
		int[] baoY=new int[90];	//保存要爆掉的泡泡的Y轴坐标
		int n=0;				//保存当前正在检查的泡泡的指针
		int amount=0;			//保存当前同色泡泡的数量合计
		boolean isBao=false;
		//System.out.println(nowCol+"  "+nowRow);
		//泡泡发射出去粘上其他泡泡后，首先将自己的x与y轴坐标（所在位置的图层数组的坐标）初始化，即：
		str="|"+nowCol+","+nowRow+"|";
		baoX[n]=nowCol;
		baoY[n]=nowRow;
		
		while (true)
		{
			//使用for循环，遍历当前泡泡四周所有的泡泡
			for (int i=0;i<6;i++)
			{
				//判断当前行是否为不偏移行
				if (gameStage[Paopao.PAOPAO_COL-1][baoY[n]][0]==0)
				{
					//检查下标是否溢出
					int newCol=baoX[n]+expressionsOCOL[i];
					int newRow=baoY[n]+expressionsOROW[i];
					if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
					{
						continue;
					}
					
					//开始检查四周是否有泡泡
					if (checkPaopao(newCol,newRow))
					{
						//System.out.println("列:"+baoX[n]+"  "+newCol+" 行:"+baoY[n]+"  "+newRow+" 颜色:"+gameStage[newCol][newRow][0]);
						//如果有再判断是否为同色（对比一下旁边泡泡的ID是否与当前位置泡泡的ID值相等）
						if (gameStage[baoX[n]][baoY[n]][0]==gameStage[newCol][newRow][0])
						{
							//如果同色则
							if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)		//判断该位置是否已经检查过了
							{
								amount++;
								str=str+newCol+","+newRow+"|";
								baoX[amount]=newCol;
								baoY[amount]=newRow;
							}
							else	//已经检查过了，则检查下一个位置
							{
								continue;
							}
						}
					}
				}
				else
				{
					//检查下标是否溢出
					int newCol=baoX[n]+expressionsJCOL[i];
					int newRow=baoY[n]+expressionsJROW[i];
					//System.out.println(newCol+"  "+newRow);
					if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
					{
						continue;
					}
					
					//开始检查四周是否有泡泡
					if (checkPaopao(newCol,newRow))
					{
						//System.out.println("列:"+baoX[n]+"  "+newCol+" 行:"+baoY[n]+"  "+newRow+" 颜色:"+gameStage[newCol][newRow][0]);
						//如果有再判断是否为同色（对比一下左边泡泡的ID是否与当前位置泡泡的ID值相等）
						if (gameStage[baoX[n]][baoY[n]][0]==gameStage[newCol][newRow][0])
						{
							//如果同色则
							if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)		//判断该位置是否已经检查过了
							{
								amount++;
								str=str+newCol+","+newRow+"|";
								baoX[amount]=newCol;
								baoY[amount]=newRow;
							}
							else	//已经检查过了，则检查下一个位置
							{
								continue;
							}
						}
					}
				}
			}
			//检查完一圈后，指针指向下一个位置
			n++;
			//跟着判断是否已经检查完成了
			if (n>amount)//如果指针n超出amount界限，则表示已经检查完成了，退出循环，否则继续
			{
				//System.out.println(str+" n:"+n+"  amount:"+amount);
				n=0;
				break;
			}
		}
		
		//全部检查完成，如果多于3个同色的则将其引爆删除
		if (amount>=2)
		{
			//计算得分，amount的值为下标，0为1个泡泡，1为两个泡泡，以此类推，
			//同时爆三个泡泡加2分，四个加3分，五个加5分，六个以上加8分
			if (colorTotal>2)	//防作弊，当泡泡颜色总数小于二种时，不计分
			{
				switch (amount)
				{
				case 2:
					score+=2;
					break;
				case 3:
					score+=3;
					break;
				case 4:
					score+=5;
					break;
				default:
					score+=8;
					break;
				}
			}
			//System.out.println(colorTotal+"  "+amount);
			for (int i=0;i<=amount;i++)
			{
				//在链表中查找到将要引爆的泡泡的图层ID，然后删除链表中对应的节点、清空图层对应坐标的标记以及删除该图层
				layerID=myLine.findNode(baoY[i],baoX[i]);
				myLine.delNode(layerID);
				gameStage[baoX[i]][baoY[i]][0]=0;
				layermanager.remove((PaopaoSprite)layermanager.getLayerAt(layerID));
			}
			isBao=true;
		}
		
		/* System.out.println("泡泡爆破后的数组");
		//打印泡泡爆破后的数组（用于检测BUG）
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		} */
		
		return isBao;
	}
	
	/**
	* 检查是否有泡泡的函数
	*/
	protected boolean checkPaopao(int baoX, int baoY)
	{
		//if (baoX<0 || baoX>Paopao.PAOPAO_COL-2) return false;
		//if (baoY<0 || baoY>Paopao.PAOPAO_ROW-1) return false;
		if (gameStage[baoX][baoY][0]==0) return false;
		return true;
	}
	
	/**
	* 没有挂点的异色泡泡下坠
	*/
	protected void paopaoDrop()
	{
		int[] expressionsOROW={1,0,-1,-1,0,1};	//y轴的左下，左，左上，右上，右，右下位置，坐标点位于不偏移行
		int[] expressionsOCOL={0,-1,0,1,1,1};	//x轴的左下，左，左上，右上，右，右下位置，坐标点位于不偏移行
		int[] expressionsJROW={1,0,-1,-1,0,1};	//y轴的左下，左，左上，右上，右，右下位置，坐标点位于偏移行
		int[] expressionsJCOL={-1,-1,-1,0,1,0};	//x轴的左下，左，左上，右上，右，右下位置，坐标点位于偏移行
		String str;	//保存内容格式：“|x,y|x1,y1|x2,y2|……”，保存要爆掉的泡泡的坐标，用于遍历所黏附的泡泡是否已经比较过
		int[] baoX=new int[90];	//保存要下坠的泡泡的X轴坐标
		int[] baoY=new int[90];	//保存要下坠的泡泡的Y轴坐标
		int n=0;				//保存当前正在检查的泡泡的指针
		int amount=0;			//保存当前下坠泡泡的数量合计
		boolean isFlag=false;
		
		//遍历所有位置
		for (int j=1;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL-1;k++)
			{
				n=0;
				amount=0;
				str="|"+k+","+j+"|";
				baoX[n]=k;
				baoY[n]=j;
				
				while (true)
				{
					//当前位置无泡泡，则标记为-1
					if (gameStage[baoX[n]][baoY[n]][0]==0)
					{
						gameStage[baoX[n]][baoY[n]][2]=-1;
						break;
					}
					//当前位置有挂点或已有下坠泡泡标记则跳过
					else if (gameStage[baoX[n]][baoY[n]][2]==1 || gameStage[baoX[n]][baoY[n]][2]==2)
					{
						break;
					}
					else
					{
						//使用for循环，遍历当前泡泡四周所有的泡泡
						for (int i=0;i<6;i++)
						{
							//判断当前行是否为不偏移行
							if (gameStage[Paopao.PAOPAO_COL-1][baoY[n]][0]==0)
							{
								//检查下标是否溢出
								int newCol=baoX[n]+expressionsOCOL[i];
								int newRow=baoY[n]+expressionsOROW[i];
								if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
								{
									continue;
								}
								
								//开始检查四周是否有泡泡
								if (checkPaopao(newCol,newRow))
								{
									//有泡泡则判断是否有挂点
									if (gameStage[newCol][newRow][2]==1)
									{
										//如果有挂点则将数组里的泡泡全部都标记为有挂点
										for (int l=0;l<=amount;l++)
										{
											gameStage[baoX[l]][baoY[l]][2]=1;
										}
										//设置已遍历完这一串泡泡的标记
										isFlag=true;
										
										//退出最里层的for循环
										break;
									}
									//无挂点
									else
									{
										//判断该位置是否已经检查过，没有检查过则将该位置加入数组里
										if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)
										{
											amount++;
											str=str+newCol+","+newRow+"|";
											baoX[amount]=newCol;
											baoY[amount]=newRow;
										}
										//已经检查过了，则检查下一个位置
										else	
										{
											continue;
										}
									}
								}
							}
							else
							{
								//检查下标是否溢出
								int newCol=baoX[n]+expressionsJCOL[i];
								int newRow=baoY[n]+expressionsJROW[i];
								if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
								{
									continue;
								}
								
								//开始检查四周是否有泡泡
								if (checkPaopao(newCol,newRow))
								{
									//有泡泡则判断是否有挂点
									if (gameStage[newCol][newRow][2]==1)
									{
										//如果有挂点则将数组里的泡泡全部都标记为有挂点
										for (int l=0;l<=amount;l++)
										{
											gameStage[baoX[l]][baoY[l]][2]=1;
										}
										//设置已遍历完这一串泡泡的标记
										isFlag=true;
										
										//退出最里层的for循环
										break;
									}
									//无挂点
									else
									{
										//判断该位置是否已经检查过，没有检查过则将该位置加入数组里
										if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)
										{
											amount++;
											str=str+newCol+","+newRow+"|";
											baoX[amount]=newCol;
											baoY[amount]=newRow;
										}
										//已经检查过了，则检查下一个位置
										else	
										{
											continue;
										}
									}
								}
							}
						}
						//如果都已标记过，则退出循环witch循环
						if (isFlag)
						{
							isFlag=false;
							break;
						}
						//无标记过，则继续检查下一个位置
						else
						{
							//指针指向下一个位置
							n++;
							
							//判断是否已经全部检查完毕，如果完成则将数组中的泡泡全部标记为下坠泡泡，
							//并退出witch循环，否则继续
							if (n>amount)
							{
								for (int l=0;l<amount+1;l++)
								{
									gameStage[baoX[l]][baoY[l]][2]=2;
								}
								
								//退出witch循环
								break;
							}
						}
					}
				}
			}
		}
		/* System.out.println("泡泡爆破后的数组");
		//打印泡泡爆破后的数组（用于检测BUG）
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		}
		System.out.println("下坠标记数组"); */
		
		//遍历所有位置取出下坠泡泡
		for (int j=1;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL-1;k++)
			{
				if (gameStage[k][j][2]==2)
				{
					baoX[amount]=k;
					baoY[amount]=j;
					amount++;
					//System.out.print(gameStage[k][j][2]+",");
					gameStage[k][j][2]=0;
				}
				//将其他标记恢复为默认值
				else
				{
					//System.out.print(gameStage[k][j][2]+",");
					gameStage[k][j][2]=0;
					
				}
			}
			//System.out.print(gameStage[9][j][2]+",");
			//System.out.println();
		}
		
		//全部检查完成，如果存在下坠泡泡
		if (amount>0)
		{
			//计算得分，同时掉一个泡泡加1分，二个加2分，三个以上加5分
			if (colorTotal>2)	//防作弊，当泡泡颜色总数小于二种时，不计分
			{
				switch (amount)
				{
				case 1:
					score+=1;
					break;
				case 2:
					score+=2;
					break;
				default:
					score+=5;
					break;
				}
			}
			
			for (int i=0;i<amount;i++)
			{
				//在链表中查找到下坠泡泡的图层ID，然后删除链表中对应的节点、清空图层对应坐标的标记以及删除该图层
				if ((layerID=myLine.findNode(baoY[i],baoX[i]))!=-1)
				{
					myLine.delNode(layerID);
					gameStage[baoX[i]][baoY[i]][0]=0;
					//System.out.print(baoY[i]+"  ");
					layermanager.remove((PaopaoSprite)layermanager.getLayerAt(layerID));
				}
			}
			amount=0;
		}
		//System.out.println();
	}
	
	/**
	* 设置当前游戏进度
	*/
	protected void setSaveStageIndex()
	{
		Paopao.stageIndex=stageIndex;
	}
	
	/**
	* 设置当前游戏进度
	*/
	protected void setStageIndex()
	{
		stageIndex=Paopao.stageIndex;
	}
	
	/**
	* 获取当前游戏关卡ID
	*/
	protected int getStageIndex()
	{
		return stageIndex;
	}
	
	/**
	* 关闭游戏线程
	*/
	protected void closeThread()
	{
		isReckonStart=false;
		isPause=true;
		//thread=null;
	}
	
	/**
	* 将自己显示在屏幕上
	*/
	protected void showMe()
	{
		midlet.setDisplayable(this);
	}
	
	/**
    * 内部类，绘制装载资源进度
    */
    class Loading implements Runnable
    {
        // 内线程
        Thread innerThread = null;
        int stageIndex = 1;
		
        public Loading(int stageIndex)
        {
            this.stageIndex = stageIndex;
            innerThread = new Thread(this);
            innerThread.start();
        }
		
        public void run()
        {
            isLoading = true;
            loadStage(stageIndex);
            System.gc();
            isLoading = false;
        }
		
		/**
		* 装载关卡
		*/
		private void loadStage(int stageIndex)
		{
			
			loadPercent = 0;
			
			//初始化泡泡数组
			initializtionGameStageDate();
			
			//重新设置发射台泡泡与备用泡泡的颜色
			paopaoRandomColor=Math.abs(ran.nextInt(7))+1;
			paopaoSelectColor=Math.abs(ran.nextInt(7))+1;
			newPaopaoSprite.setFrame(paopaoRandomColor);
			paopaoSprite.setFrame(paopaoSelectColor);
			System.out.println(paopaoSelectColor);
			loadPercent = 10;
			
			//清空链表与删除除最初初始化以外的泡泡图层
			myLine.setNodeNull();
			while (layermanager.getSize()>5)
			{
				layermanager.remove(layermanager.getLayerAt(2));
			}
			
			loadPercent = 20;
			
			//读取指定关卡数据
			RecordStoreManage rsm=new RecordStoreManage();
			gameStage=rsm.loadGameStage(stageIndex);
			rsm=null;
			
			loadPercent = 50;
			System.out.println(loadPercent);
			//在屏幕上绘制关卡内容（绘制泡泡）
			drawStage();
			
			loadPercent = 75;
			
			//装载音效
			//if (Paopao.acousticEffect==0) loadAcousticEffect();
			
			loadPercent = 90;
			
			//绘制图层
			layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
			
			loadPercent = 100;
			
			try
			{
				Thread.sleep(200);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
    }
	
	
	/**
    * 绘制装载消息框
    */
    protected void drawLoadingFrame()
    {
        int x=(getWidth()-Paopao.GAME_IMAGE_WIDTH)/2;
		int y=getHeight()/2;
        
        // 清屏
        g.setColor(0x0FFBDBDBD);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // 绘制消息框
        g.setColor(0x0FFBDBDBD);
        g.fillRect(x, y-30, Paopao.GAME_IMAGE_WIDTH, 60);
        g.setColor(0x000000);
        g.drawRect(x, y-30, Paopao.GAME_IMAGE_WIDTH, 60);
        
        // 绘制进度条
        g.setColor(0xFFFFFF00);
        g.fillRect(x, y-5, (Paopao.GAME_IMAGE_WIDTH * loadPercent) / 100, 10);
        g.setColor(0xFFFF0000);
        g.drawRect(x, y-5, Paopao.GAME_IMAGE_WIDTH, 10);
        
    }
	
	/**
    * 内部类，计算每次发射泡泡超时时间，并绘制剩余三秒的提醒
    */
    class projectTimeOut implements Runnable
    {
        // 内线程
        Thread timeOutThread = null;
        private int timeOutDefault=9;		//默认超时时间
		private int timeOut=timeOutDefault;	//超时时间
		private boolean isReckonRun=true;
		
        public projectTimeOut()
        {
            timeOutThread = new Thread(this);
            timeOutThread.start();
        }
		
        public void run()
        {
			while (isReckonRun)
			{
				if (isReckonStart)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					
					timeOut--;
					//System.out.println(timeOut);
					switch (timeOut)
					{
					case 3:
						drawTimeOutNum(0);
						break;
					case 2:
						drawTimeOutNum(1);
						break;
					case 1:
						drawTimeOutNum(2);
						break;
					case 0:
						isReckon();
						break;
					}
				}
			}
        }
		
		//重置超时时间
		public void setTime()
		{
			timeOut=timeOutDefault;
		}
		
		/**
		* 绘制超时时间
		*/
		private void drawTimeOutNum(int index)
		{
			timeOutNum.setFrame(index);
			timeOutNum.setVisible(true);
		}
    }
	
	
	/**
    * 内部类，泡泡飞行类
    */
    class paopaoFly implements Runnable
    {
        // 内线程
        Thread flyThread = null;
		private boolean isRun=true;				//飞行泡泡线程状态
		private PaopaoSprite ppSprite=null;		//飞行中的泡泡精灵
		private int paopaoColor;				//飞行中泡泡的颜色
		private int startX,startY,paopao_x,paopao_y;	//飞行泡泡的起始坐标与飞行中的坐标
		private int arrowArc;					//正在飞行中的泡泡的飞行角度
		private int layerID=0;					//飞行中的泡泡所在的层ID
		private int count=0;					//泡泡飞行步数与每步飞行的像素相乘，计算屏幕每刷新一次，泡泡距飞行起始点的距离
		private int spf=Paopao.ranspf;			//游戏每帧绘制时间
		private boolean paopaoFlag=true;		//泡泡是否为随机添加的标志，用于区分要使用的逻辑判断
		private boolean isCollide=false;		//碰撞的标志
		private int paopao_row=0;				//飞行中的泡泡精灵所在泡泡数组里的行
		private int paopao_col=0;				//飞行中的泡泡精灵所在泡泡数组里的列
		private int ranAddNum=0;				//随机添加泡泡的数量
		
		/**
		* 构造类
		*/
        public paopaoFly(PaopaoSprite paopaoSprite,int paopaoColor,int startX,int startY,int arrowArc,int layerID,int ranAddNum)
        {
			ppSprite=paopaoSprite;
			this.paopaoColor=paopaoColor;
			this.startX=this.paopao_x=startX;
			this.startY=this.paopao_y=startY;
			this.arrowArc=arrowArc;
			this.layerID=layerID;
			this.ranAddNum=ranAddNum;
			
            flyThread = new Thread(this);
            flyThread.start();
			//System.out.println(paopaoFlag+" "+isRun+" "+isPause+" "+arrowArc+" "+layerID);
        }
		
        public void run()
        {
			while (isRun)
			{
				long times= System.currentTimeMillis();
				
				if (!isPause)
				{
					//移动泡泡
					paopaoMove();
					
					if (paopaoFlag)
					{
						//添加随机泡泡逻辑判断
						randomLogic();
						
					}
					else
					{
						//逻辑判断
						logic();
					}
				}
				
				times= System.currentTimeMillis()-times;
				if( times<spf )
				{
					try
					{
						Thread.sleep(spf-times );
					}
					catch(InterruptedException ie)
					{
						isRun=false;
					}
				}
			}
        }
		
		/**
		* 设置当前泡泡为那种泡泡
		*/
		public void setPaopaoFlag(boolean flag)
		{
			paopaoFlag=flag;
		}
		
		/**
		* 设置当前泡泡为那种泡泡
		*/
		public void setSPF(int spf)
		{
			this.spf=spf;
		}
		
		/**
		* 泡泡移动
		*/
		private void paopaoMove() 
		{
			count++;
			paopao_x=startX+(count*Paopao.speed)*ARROW_ARC_COS[arrowArc]/100000;
			paopao_y=startY-(count*Paopao.speed)*ARROW_ARC_SIN[arrowArc]/100000;
			ppSprite.setPosition(paopao_x,paopao_y);
		}
		
		/**
		* 逻辑判断
		*/
		private void logic()
		{
			if (isFly)
			{
				if (paopao_x<paopao_img_x)
				{
					paopao_x=paopao_img_x;
				}
				else if (paopao_x>(Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH-paopao_img_x))
				{
					paopao_x=Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH-paopao_img_x;
				}
				if (paopao_y<paopao_img_y) 
				{
					paopao_y=paopao_img_y;
				}
				else if (paopao_y>(Paopao.GAME_IMAGE_HEIGHT-Paopao.SPRITE_HEIGHT-paopao_img_y)) 
				{
					paopao_y=Paopao.GAME_IMAGE_HEIGHT-Paopao.SPRITE_HEIGHT-paopao_img_y;
				}
				
				paopao_row=getRow();
				paopao_col=getCol();
				
				if (paopao_row<0) paopao_row=0;
				if (paopao_col<0) paopao_col=0;
				else if (paopao_col>Paopao.PAOPAO_COL-2) paopao_col=Paopao.PAOPAO_COL-2;
				
				//判断泡泡是否已经撞到边了
				if (paopao_x==paopao_img_x)
				{
					arrowArc=ARROW_ARC_SIN.length-1-arrowArc;
					ppSprite.setPosition(paopao_x,paopao_y);
					count=0;
					startX=paopao_x;
					startY=paopao_y;
				}
				else if (paopao_x==(Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH-paopao_img_x))
				{
					arrowArc=ARROW_ARC_SIN.length-1-arrowArc;
					count=0;
					startX=paopao_x;
					startY=paopao_y;
				}
				
				//已经碰到顶部了
				if (paopao_y==paopao_img_y)
				{
					isCollide=true;
					isFly=false;
				}
				else
				{
					//泡泡在警界线以下不做碰撞检测
					if (paopao_row<Paopao.PAOPAO_ROW+1)
					{
						//进行碰撞检查
						collide(layerID);
					}
				}
				
				//System.out.println(paopao_row);
				if (isCollide)
				{
					synchronized(this)
					{
						if (isBusy)
						{
							try 
							{
								wait();//阻塞，直到有新客户连接
							} 
							catch (InterruptedException e) 
							{ 
							
							}
						}
						isBusy=true;
						ppSprite.setPosition(getRectX(paopao_col,paopao_row),getRectY(paopao_row));	//黏附泡泡
						//重新初始化泡泡发射飞行参数
						count=0;
						myLine.insertNode(layerID+1,paopao_row,paopao_col,paopaoColor,ranAddNum,true);	//在链表中插入新增的泡泡层
						gameStage[paopao_col][paopao_row][0]=(byte)paopaoColor;			//在当前位置记录泡泡颜色
						isCollide=false;
						isFly=false;
						updatePaopao();			//将备用泡泡更新到发射台上
						
						//System.out.println(isReckonStart);
						//是否已经失败了
						if (paopao_row>Paopao.PAOPAO_ROW-1)
						{
							//pTimeOut.closeThread();			//关闭泡泡超时计算线程
							//isActive=false;				//结束外部类线程
							midlet.gameMenu.setMenuIndex(8);
							midlet.gameMenu.setInfoFrameIndex(1);
							midlet.gameMenu.showMe();
							//System.out.println("已失败");
							isReckonStart=false;
							paopaoCount=0;
						}
						else
						{	
							//爆掉同色多于3个的泡泡，则同时将没有挂点的泡泡删除
							if (paopaoBao(paopao_col,paopao_row))
							{
								paopaoDrop();
								if (myLine.isNull())
								{
									//计算得分，每过一关加100分
									score+=100;
									
									//isActive=false;		//结束外部类线程
									//pTimeOut.closeThread();			//关闭泡泡超时计算线程
									
									//跳转到胜利信息框
									midlet.gameMenu.setMenuIndex(8);
									midlet.gameMenu.setInfoFrameIndex(0);
									midlet.gameMenu.showMe();
									isReckonStart=false;
									paopaoCount=0;
								}
							}
						}
						
						//每发射ppCount个泡泡，下面随机增加一排泡泡
						if (paopaoCount%Paopao.ppCount==0 && paopaoCount!=0)
						{
							//randomAddPaopao(Paopao.PAOPAO_ROW-1);
							randomAddPaopao(5);
						}
						
						isRun=false;			//结束泡泡移动线程
						
						isBusy=false;
						notify(); //同时唤醒处理线程
					}
				}
			}
		}
		
		/**
		* 逻辑判断
		*/
		private void randomLogic()
		{
			if (paopao_y<paopao_img_y) 
			{
				paopao_y=paopao_img_y;
			}
			paopao_row=getRow();
			paopao_col=getCol();
			if (paopao_row<0) paopao_row=0;
			if (paopao_col<0) paopao_col=0;
			else if (paopao_col>Paopao.PAOPAO_COL-2) paopao_col=Paopao.PAOPAO_COL-2;
			
			//已经碰到顶部了
			if (paopao_y==paopao_img_y)
			{
				isCollide=true;
			}
			else
			{
				//泡泡在警界线以下不做碰撞检测
				if (paopao_row<Paopao.PAOPAO_ROW+1)
				{
					//进行碰撞检查
					collide(layerID);
				}
			}
			
			if (isCollide)
			{
				synchronized(this)
				{	
					count=0;
					if (isBusy)
					{
						try 
						{
							wait();//阻塞，直到有新客户连接
						} 
						catch (InterruptedException e) 
						{ 
							
						}
					}
					isBusy=true;
					ppSprite.setPosition(getRectX(paopao_col,paopao_row),getRectY(paopao_row));	//黏附泡泡
					//重新初始化泡泡发射飞行参数
					myLine.insertNode(layerID,paopao_row,paopao_col,paopaoColor,ranAddNum,isRanAddPP);	//在链表中插入新增的泡泡层
					gameStage[paopao_col][paopao_row][0]=(byte)paopaoColor;			//在当前位置记录泡泡颜色
					isRanAddPP=false;
					isBusy=false;
					notify(); //同时唤醒处理线程
				}
				
				isCollide=false;
				isRun=false;			//结束泡泡移动线程
				ranAddPPNum--;
				if (ranAddPPNum<=0)
				{
					ranAddPPNum=0;
					isReckonStart=true;
				}
				
				/* System.out.println(layerID);
				System.out.println("泡泡插入后的数组(层)");
				//打印泡泡插入后的数组（用于检测BUG）
				for (int j=0;j<Paopao.PAOPAO_ROW;j++)
				{
					for (int k=0;k<Paopao.PAOPAO_COL;k++)
					{
						System.out.print(myLine.findNode(j,k)+",");
						//System.out.print(gameStage[k][j][0]+",");
					}
					System.out.println();
				}				
				System.out.println("泡泡插入后的数组(颜色)");
				//打印泡泡插入后的数组（用于检测BUG）
				for (int j=0;j<Paopao.PAOPAO_ROW;j++)
				{
					for (int k=0;k<Paopao.PAOPAO_COL;k++)
					{
						//System.out.print(myLine.findNode(j,k)+",");
						System.out.print(gameStage[k][j][0]+",");
					}
					System.out.println();
				} */		
				
				//System.out.println("row:"+paopao_row);
				//是否已经失败了
				if (paopao_row>Paopao.PAOPAO_ROW-1)
				{
					//pTimeOut.closeThread();			//关闭泡泡超时计算线程
					//isActive=false;
					midlet.gameMenu.setMenuIndex(8);
					midlet.gameMenu.setInfoFrameIndex(1);
					midlet.gameMenu.showMe();
					//System.out.println("已失败");
					isReckonStart=false;
				}
			}
		}
		
		/**
		* 获得精灵当前参考点所在格的行索引
		*/
		private int getRow()
		{
			return (ppSprite.getRefPixelY()-paopao_img_y)/(Paopao.SPRITE_HEIGHT-2);
		}
		
		/**
		* 获得精灵当前参考点所在格的列索引
		*/
		private int getCol()
		{
			if (gameStage[Paopao.PAOPAO_COL-1][paopao_row][0]==1)
			{
				return (ppSprite.getRefPixelX()-paopao_img_x+Paopao.SPRITE_WIDTH/2)/Paopao.SPRITE_WIDTH;
			}
			else
			{
				return (ppSprite.getRefPixelX()-paopao_img_x)/Paopao.SPRITE_WIDTH;
			}
		}
		
		/**
		* 检查磁撞
		*/
		private synchronized void collide(int layerID)
		{
			if (isBusy)
			{
				try 
				{
					wait();//阻塞，直到有新客户连接
				} 
				catch (InterruptedException e) 
				{ 
					
				}
			}
			isBusy=true;
			for (int i=2;i<layermanager.getSize()-3;i++)
			{
				if (i!=layerID) 
				{
					if (ppSprite.collidesWith(((PaopaoSprite)layermanager.getLayerAt(i)), true)) 
					{
						isCollide=true;
					}
				}
			}
			isBusy=false;
			notify(); //同时唤醒处理线程
		}
    }
	
	private void printArray()
	{
		System.out.println(layerID);
		System.out.println("泡泡插入后的数组(层)");
		//打印泡泡插入后的数组（用于检测BUG）
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				System.out.print(myLine.findNode(j,k)+",");
				//System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		}				
		System.out.println("泡泡插入后的数组(颜色)");
		//打印泡泡插入后的数组（用于检测BUG）
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				//System.out.print(myLine.findNode(j,k)+",");
				System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		}		
	}
}