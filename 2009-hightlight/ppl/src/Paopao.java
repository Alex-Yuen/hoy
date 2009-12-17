import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/**
 * 游戏主Midlet类
 */
public class Paopao extends MIDlet 
{
	private Display display = null;
	ShowLogo showLogo=null;
	ShowLogo2 showLogo2=null;
	Synthesis synthesis=null;
	Menu menu=null;
	GameMenu gameMenu=null;
	Game gamePlay=null;
	GameStageMaker stageMaker=null;
	
	/**
	* Paopao类的构造函数
	*/
	public Paopao() 
	{
		showLogo=new ShowLogo(this);
		showLogo2=new ShowLogo2(this);
		synthesis=new Synthesis(this);
		menu=new Menu(this);
		gameMenu=new GameMenu(this);
		gamePlay=new Game(this);
		stageMaker=new GameStageMaker(this);
		display = Display.getDisplay(this);
		getPaopaoSet();
	}

	protected void startApp()
	{
		showLogo.showMe();
	}

	protected void pauseApp() 
	{
		
	}

	protected void destroyApp(boolean p1) 
	{
		
	}
	
	/**
    * 显示指定的displayable对象，主要给其他的类调用
    */
    public void setDisplayable(Displayable displayable)
    {
        display.setCurrent(displayable);
    }
	
	/**
	* 退出游戏
	*/
	public void quitGame()
	{
		try
        {
            destroyApp(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        notifyDestroyed();
	}
	
	/**
	* 创建菜单背景图
	*/
	public Image createMenuImage(String str)
	{
		//System.out.println(str);
		Image image=null;
		try
		{
			image=Image.createImage(str);
		}
		catch (Exception ex)
		{
			System.out.println("图片不存在");
		}
		return image;
	}
	
	/**
	* 读取游戏配置，并检测是否存在关卡
	*/
	private void getPaopaoSet()
	{
		RecordStoreManage rsm=new RecordStoreManage();
		//rsm.deleteRS(GAME_STAGE_FILE_NAME);
		//rsm.deleteRS(PAOPAO_SETNAME);
		//读取游戏配置
		rsm.readGameSetInfo();
		//System.out.println(gameStageInitializtion);
		if (gameStageInitializtion==0)
		{
			rsm.readAndSave();
			gameStageInitializtion=1;
			rsm.saveGameSetInfo(1);
		}
		else
		{
			gameStageTotal=rsm.readStageTotal();
		}
		
		rsm=null;
	}
	
	/*******************************************************************************************************/
	/***************
	* 游戏常量设置 *
	****************/
	
	/**
	* 闪屏自动跳转时间
	*/
	public static final int SHOWLOGO_TIME=2000;
	
	/**
	* 游戏每帧绘制时间，单位为ms
	*/
	public static final int spf=4;
	public static final int ranspf=2;
	
	/**
	* 发射泡泡的飞行速度
	*/
	public static final int speed=4;
	
	/**
	* 每发射ppCount个泡泡，下面随机增加一排泡泡
	*/
	public static final int ppCount=8;
	
	/**
	* 游戏地图大小
	*/
	public static final int GAME_IMAGE_WIDTH=176;
	public static final int GAME_IMAGE_HEIGHT=208;
	
	/**
	* 手机屏幕大小
	*/
    public static int screenWidth=0;
    public static int screenHeight=0;
	
	/**
	* 精灵的宽和高
	*/
	public static final int SPRITE_WIDTH=16;
	public static final int SPRITE_HEIGHT=16;
	
	/*******************************************************************************************************/
	/***************
	*   图片路径   *
	****************/
	
	public static final String IMAGE_ARROW="/arrow.png";
	public static final String IMAGE_BACK="/back.png";
	public static final String IMAGE_BACK1="/back1.png";
	public static final String IMAGE_BACK2="/back2.png";
	public static final String IMAGE_BACK3="/back3.png";
	public static final String IMAGE_BACK4="/back4.png";
	public static final String IMAGE_LOGO="/logo.png";
	public static final String IMAGE_LOGO2="/GameCollege.png";
	public static final String IMAGE_MENU="/menu.png";
	public static final String IMAGE_PAOPAO="/paopao.png";
	public static final String IMAGE_SPECIALTIES="/specialties.png";
	public static final String IMAGE_GAMESTAGEMENU="/gameStageMenu.png";
	public static final String IMAGE_TEXTMENUBACK="/textMenuBack.png";
	public static final String IMAGE_MENUTEXT="/menutext.png";
	public static final String IMAGE_NUMBERBACK="/numberBack.png";
	public static final String IMAGE_RANK="/rank.png";
	public static final String IMAGE_HELP="/help.png";
	public static final String IMAGE_ABOUT="/about.png";
	public static final String IMAGE_INFOTEXT="/text.png";
	public static final String IMAGE_BLACKBOARD="/blackboard.png";
	public static final String IMAGE_TITLE="/gameMenu.png";
	public static final String IMAGE_LINE="/line.png";
	public static final String IMAGE_TIMEOUT="/timeOut.gif";
	
	/**
	* 用户所选择的游戏背景图
	*/
	public static String image_select_back=IMAGE_BACK1;
	
	/*******************************************************************************************************/
	/***************
	*   游戏菜单   *
	****************/
	
	/**
	* 菜单选项
	*/
	//public static final String[] MENU_OPTIONS={"单人模式","网络对战","联机模式","设    置","排 行 榜","帮    助","关    于","退    出"};
	
	/**
	* 菜单选项显示的x、y轴位置
	*/
	public static final int MENU_X=10;
	public static final int MENU_Y=184;
	
	/*******************************************************************************************************/
	/*********************
	* 二级菜单及文字显示 *
	**********************/
	
	/**
	* 二级背景图
	*/
	public static Image imgBack=null;
	
	/**
	* 二级菜单或文字显示的x、y轴位置及显示的长度与高度
	*/
	public static final int TEXT_X=10;
	public static final int TEXT_Y=16;
	public static final int TEXT_WIDTH=165;
	public static final int TEXT_HEIGHT=150;
	
	/**
	* 排行榜、帮助和关于文字
	*/
	public static final String ABOUT_TEXT="游戏名称：《JAVA网络版火拼泡泡龙》\n游戏版本：V1.0\n班       级：59B\n学员编号：034-0710-017-10\n作       者：陈焕\nEmail：a_1012@yahoo.com.cn\nQQ:1654937\n指导老师：李楠\n游戏学院深圳培训中心";
	public static final String HELP_TEXT="    左软键为菜单键，右软键为当前界面的帮助键。\n    导航键←和→（左、右键）或数字键4和6进行操纵发射方向的左转与右转。导航键○（确认键）或数字键5进行确认发射泡泡。";
	public static final String GAME_HELP_TEXT="    左软键为菜单键，右软键为当前界面的帮助键。\n    导航键←和→（左、右键）或数字键4和6进行操纵发射方向的左转与右转。↑和↓（上、下键）或数字键2和8进行选择要使用的道具（在道具模式下按下上、下键后就进入道具选择模式，按确认键后才离开道具选择模式）。导航键○（确认键）或数字键5进行确认发射泡泡和确认要选择使用的道具。";
	public static String RANK_TEXT="";
	
	/**
    * 帮助、关于界面操作提示
    */
    public static final String TEXT_TIP="UP(2)或DOWN(8)键翻页";
	public static final String TEXT_TIP2="其它键返回";
	
	/*******************************************************************************************************/
	/***************
	* 游戏存储信息 *
	****************/
	
	/**
	* 游戏设置存储文件名
	*/
	public static final String PAOPAO_SETNAME="paopaoSet.dat";
	
	/**
	* 本地排行榜存储文件名
	*/
	public static final String PAOPAOR_ANKNAME="paopaoRank.dat";
	
	/**
	* 游戏关卡存储文件名
	*/
	public static final String GAME_STAGE_FILE_NAME="gameStage.dat";
	
	/**
	* 游戏总关卡数
	*/
	public static int gameStageTotal=0;
	
	/**
	* 游戏关卡地图设定，176×208屏设定关卡每行与每列的泡泡数，行8×列8+1＝72，多出来的一列是存放当前行缩进标志
	*/
	public static final int PAOPAO_ROW=10;
	public static final int PAOPAO_COL=10;
	
	/**
	* 游戏每关卡存储大小
	*/
	public static final int STAGE_LENGTH=PAOPAO_ROW*PAOPAO_COL;
	
	/**
	* 玩家名称
	*/
	public static String playerName=" ";
	
	/**
	* 背景图片
	*/
	public static int backImageIndex=0;
	
	/**
	* 音效开关，0为启用，1为关闭
	*/
	public static int acousticEffect=0;
	
	/**
	* 背景音乐开关，0为启用，1为关闭
	*/
	public static int backgrounMusic=0;
	
	/**
	* 网络连接方式，0为CMWAP，1为CMNET
	*/
	public static int netLinkType=0;
	
	/**
	* 蓝牙开关，0为启用，1为关闭
	*/
	public static int bluetooth=1;
	
	/**
	* 自动更新关卡开关，0为启用，1为关闭
	*/
	public static int gameStageInitializtion=0;
	
	/**
	* 游戏关卡进度
	*/
	public static int stageIndex=1;
}