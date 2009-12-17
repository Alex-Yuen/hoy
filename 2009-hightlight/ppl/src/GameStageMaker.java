import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

/**
* 游戏主类
*/
public class GameStageMaker extends GameCanvas implements Runnable
{
	private Paopao midlet=null;
	private MyLine myLine=null;
	private Graphics g=null;
	
	//线程运行状态标志
	private boolean isActive=true;
	private boolean isPause=true;
	Thread thread=null;		//绘制线程
	
	//flag=0为添加关卡，flag=1为编辑关卡，selectLayerID为所要操作的关卡ID
	private int flag=0;
	private int selectLayerID=1;
	
	/**
	* 图层与精灵的相关变量
	*/
	private PaopaoSprite paopaoSprite=null;	//泡泡精灵
	private PaopaoSprite rectangle=null;	//矩形精灵，方便查看当前绘制画笔在哪里
	private TiledLayer backLayer=null;		//背景图层
	private LayerManager layermanager=null;	//图层管理
	private Image imgBack=null;
	
	//绘制泡泡标志，按确认键后此标志设为true，然后在相应的图层绘制相应的泡泡
	private boolean isRender=false;
	
	//泡泡精灵所在泡泡数组里的行与列
	private int paopao_row=0;
	private int paopao_col=0;
	
	//泡泡精灵所在图片上的位置
	private int paopao_img_x=15;
	private int paopao_img_y=17;
	
	//泡泡精灵所在屏幕上的x、y坐标
	private int paopao_x=paopao_img_x;
	private int paopao_y=paopao_img_y;
	
	//当前泡泡精灵所选择的颜色（帧）
	private int paopaoSelectColor=1;
	
	//链接属性：图层索引号
	private int layerID=-1;
	
	//泡泡数组，最后一列是标志列，标志当前列是否进行缩进半个精灵宽度，0表是不缩进，1表是缩进
	private byte[][] gameStageDate;
	
	
	/**
	* 构造函数
	*/
	public GameStageMaker(Paopao midlet) 
	{
		super(false);
		
		//使用全屏模式
		this.setFullScreenMode(true);
		
		this.midlet=midlet;
		myLine=new MyLine();
		
		//获得图笔
		g = getGraphics();
		
		//初始化图层与泡泡精灵
		initLayerMenu();
		
	}
	
	/**
	* 创建PaopaoSprite类对象
	*/
	private PaopaoSprite createSprite(String spriteName,int paopaoWidth,int paopaoHeight) 
	{
		return new PaopaoSprite(midlet.createMenuImage(spriteName),paopaoWidth,paopaoHeight,0,0);
	}
	
	/**
	* 初始化图层与泡泡精灵
	*/
	private void initLayerMenu()
	{
		//创建背景图片
		imgBack=midlet.createMenuImage(Paopao.image_select_back);
		backLayer=new TiledLayer(1, 1, imgBack, Paopao.GAME_IMAGE_WIDTH, Paopao.GAME_IMAGE_HEIGHT);
		backLayer.setCell(0,0,1);
		
		//创建矩形精灵
		rectangle=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT);
		
		//图层管理加入背景层、精灵层
		layermanager=new LayerManager();
		layermanager.append(rectangle);
		layermanager.append(createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT));
		layermanager.append(backLayer);
		paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
		
		//设置精灵的颜色(帧)和初始位置
		rectangle.setFrame(9);
		rectangle.setPosition(paopao_x,paopao_y);
		paopaoSprite.setFrame(paopaoSelectColor);
		paopaoSprite.setPosition(paopao_x,paopao_y);
	}
	
	/**
    * 开始绘制关卡，flag为添加或编辑关卡的标志
    */
    public void startMaker(int flag)
    {
		this.flag=flag;
		
		//启动线程
        if (thread==null)
        {
            thread=new Thread(this);
            thread.start();
        }
		//初始化关卡数组
		setGameStageDate();
		//绘制屏幕
		layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
		
		midlet.setDisplayable(this);
		System.gc();
    }
	
	/**
	* 将自己显示在屏幕上
	*/
	protected void showMe()
	{
		layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
		midlet.setDisplayable(this);
	}
	
	/**
	* 设置将要进行操作的关卡ID
	*/
	protected void setSelectLayerID(int selectLayerID)
	{
		this.selectLayerID=selectLayerID;
	}
	
	/**
	* 初始化gameStageDate数组
	*/
	private void initializtionGameStageDate()
	{
		gameStageDate=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW];
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL;j++)
			{
				if (i%2!=0 && j==Paopao.PAOPAO_COL-1)
				{
					gameStageDate[j][i]=1;
				}
				else
				{
					gameStageDate[j][i]=0;
				}
			}
		}
	}
	
	/**
	* 绘制关卡
	*/
	private void drawStage()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL-1;j++)
			{
				if (gameStageDate[j][i]!=0)
				{
					paopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT);
					myLine.insertNode(2,i,j,gameStageDate[j][i],1,true);
					layermanager.insert(paopaoSprite,2);
					paopaoSprite.setFrame(gameStageDate[j][i]);
					paopaoSprite.setPosition(getRectX(j,i),getRectY(i));
					
					paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
				}
			}
		}
	}
	
	/**
	* 设置gameStageDate数组
	*/
	private void setGameStageDate()
	{
		if (flag==0)
		{
			//初始化gameStageDate数组
			initializtionGameStageDate();
		}
		else
		{
			//读取gameStageDate数组
			RecordStoreManage rsm=new RecordStoreManage();
			gameStageDate=rsm.readGameStageDate(selectLayerID);
			rsm=null;
			if (gameStageDate==null)
			{
				initializtionGameStageDate();
				flag=0;
			}
		}
		drawStage();
	}
	
	/**
	* 获得精灵当前参考点所在格的X轴的坐标
	*/
	private int getRectX(int col,int row)
	{
		int x=0;
		if (gameStageDate[Paopao.PAOPAO_COL-1][row]==1)
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
	* 运行线程
	*/
	public void run()
	{
		while (isActive)
		{
			//监听玩家输入信息
			input();
			
			if (!isPause)
			{
				//逻辑判断
				logic();
				
				//绘制并刷新屏幕
				drawScreen(g);
				
				//暂停
				isPause=true;
			}
			
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ie)
			{
				isActive=false;
			}
		}
	}
	
	/**
	* 监听玩家输入信息
	*/
	private void input()
	{
		int keyStates = getKeyStates();
		
		if ((keyStates & DOWN_PRESSED) != 0) 
		{
			isPause=false;
			paopao_row++;
		}
		else if ((keyStates & UP_PRESSED) != 0) 
		{
			isPause=false;
			paopao_row--;
		}
		
		if ((keyStates & RIGHT_PRESSED) != 0) 
		{
			isPause=false;
			paopao_col++;
		}
		else if ((keyStates & LEFT_PRESSED) != 0) 
		{
			isPause=false;
			paopao_col--;
		}
		
		if ((keyStates & FIRE_PRESSED) != 0)
		{
			isPause=false;
			isRender=true;
		}
	}
	
	/**
	* 用户按键事件
	*/
	protected void keyPressed(int keyCode)
	{
		isPause=false;
		
		switch (keyCode)
		{
		case Canvas.KEY_NUM1:
			paopao_row--;
			paopao_col--;
			break;
		case Canvas.KEY_NUM2:
			paopao_row--;
			break;
		case Canvas.KEY_NUM3:
			paopao_row--;
			paopao_col++;
			break;
		case Canvas.KEY_NUM4:
			paopao_col--;
			break;
		case Canvas.KEY_NUM5:
			isRender=true;
			break;
		case Canvas.KEY_NUM6:
			paopao_col++;
			break;
		case Canvas.KEY_NUM7:
			paopao_row++;
			paopao_col--;
			break;
		case Canvas.KEY_NUM8:
			paopao_row++;
			break;
		case Canvas.KEY_NUM9:
			paopao_row++;
			paopao_col++;
			break;
		case Canvas.KEY_NUM0:
			printGameStageDate();
			break;
		case Canvas.KEY_STAR:
			paopaoSelectColor--;
			break;
		case Canvas.KEY_POUND:
			paopaoSelectColor++;
			break;
		}
		
		if (getKeyName(keyCode).equals("SOFT1") || getKeyName(keyCode).equals("SOFT2"))
		{
			midlet.gameMenu.setMenuIndex(7);
			midlet.gameMenu.showMe();
		}
	}
	
	/**
	* 逻辑判断
	*/
	private void logic()
	{
		//限制所选精灵颜色、移动行与列的范围
		if (paopaoSelectColor<1) paopaoSelectColor=8;
		else if (paopaoSelectColor>8) paopaoSelectColor=1;
		if (paopao_row<0) paopao_row=0;
		else if (paopao_row>Paopao.PAOPAO_ROW-1) paopao_row=Paopao.PAOPAO_ROW-1;
		if (paopao_col<0) paopao_col=0;
		else if (paopao_col>Paopao.PAOPAO_COL-2) paopao_col=Paopao.PAOPAO_COL-2;
		
		//计算精灵所在的x、y轴坐标
		if (gameStageDate[Paopao.PAOPAO_COL-1][paopao_row]==1)
		{
			if (paopao_col==0) paopao_col=1;
			paopao_x=paopao_col*Paopao.SPRITE_WIDTH-Paopao.SPRITE_WIDTH/2+paopao_img_x;
		}
		else
		{
			paopao_x=paopao_col*Paopao.SPRITE_WIDTH+paopao_img_x;
		}
		paopao_y=paopao_row*(Paopao.SPRITE_HEIGHT-2)+paopao_img_y;
		
		//设置精灵的颜色
		paopaoSprite.setFrame(paopaoSelectColor);
		
		//移动精灵
		rectangle.setPosition(paopao_x,paopao_y);
		paopaoSprite.setPosition(paopao_x,paopao_y);
	}
	
	/**
	* 重置关卡相关变量
	*/
	protected void clearGameStageDate()
	{
		initializtionGameStageDate();
		myLine.setNodeNull();
		while (layermanager.getSize()>3)
		{
			layermanager.remove(layermanager.getLayerAt(2));
		}
		System.gc();
	}
	
	/**
	* 绘制画布
	*/
	private void drawScreen(Graphics g)
	{
		//判断是否按下确认键，按下则在当前位置绘制泡泡
		if (isRender)
		{
			//记录当前位置泡泡的颜色
			gameStageDate[paopao_col][paopao_row]=(byte)paopaoSelectColor;
			
			//在链表中将已有的泡泡替换成新的泡泡或添加泡泡
			if ((layerID=myLine.findNode(paopao_row,paopao_col))==-1)
			{
				myLine.insertNode(2,paopao_row,paopao_col,paopaoSelectColor,1,true);
				layermanager.insert(createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT),1);
				paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
			}
			else
			{
				myLine.setNode(layerID,paopaoSelectColor);
				paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(layerID);
				paopaoSprite.setFrame(paopaoSelectColor);
				paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
			}
			
			isRender=false;
		}
		
		//绘制图层
		layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
		
		// 刷新屏幕
		flushGraphics();
	}
	
	/**
	* 在控制台打印gameStageDate数组，以便检查是否正确
	*/
	private void printGameStageDate()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL;j++)
			{
				System.out.print(gameStageDate[j][i]+",");
			}
			System.out.println();
		}
	}
	
	/**
	* 删除当前位置泡泡
	*/
	protected void delPaopao()
	{
		if ((layerID=myLine.findNode(paopao_row,paopao_col))!=-1)
		{
			layermanager.remove(layermanager.getLayerAt(layerID));
			myLine.delNode(layerID);
			gameStageDate[paopao_col][paopao_row]=0;
		}
	}
	
	/**
	* 保存当前编辑的关卡
	*/
	protected void saveStage()
	{
		RecordStoreManage rsm=new RecordStoreManage();
		//添加关卡
		if (flag==0)
		{
			if (rsm.addGameStageDate(gameStageDate))
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(2);
				midlet.gameMenu.showMe();
			}
			else
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(3);
				midlet.gameMenu.showMe();
			}
		}
		//保存修改后的关卡
		else
		{
			if (rsm.setGameStageDate(gameStageDate,selectLayerID))
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(2);
				midlet.gameMenu.showMe();
			}
			else
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(3);
				midlet.gameMenu.showMe();
			}
		}
	}
	
	/**
	* 删除指定关卡
	*/
	protected void delStage()
	{
		RecordStoreManage rsm=new RecordStoreManage();
		if (rsm.delGameStageDate(selectLayerID))
		{
			Paopao.gameStageTotal--;
			midlet.gameMenu.setEnterFlag(1);
			midlet.gameMenu.setMenuIndex(8);
			midlet.gameMenu.setInfoFrameIndex(4);
			midlet.gameMenu.showMe();
		}
		else
		{
			midlet.gameMenu.setEnterFlag(1);
			midlet.gameMenu.setMenuIndex(8);
			midlet.gameMenu.setInfoFrameIndex(5);
			midlet.gameMenu.showMe();
		}
	}
}