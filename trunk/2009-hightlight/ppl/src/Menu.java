import javax.microedition.lcdui.*;

/**
* 菜单类
*/
public class Menu extends Canvas
{
	private Paopao midlet=null;
	private PaopaoSet paopaoSet=null;
	
	private Image imgMenu=null;
	private Image imgMenuText[]=new Image[9];
	
	//当前菜单索引号
	private int selectedIndex=0;
	
	//菜单选项具体绘制菜单
	private int menu_selected_x;
	private int menu_selected_y;
	
	/**
	* 构造函数，引用Paopao类，并初始化相关变量
	*/
	public Menu(Paopao midlet)
	{
		this.midlet=midlet;
		
		//创建背景图片
		imgMenu=midlet.createMenuImage(midlet.IMAGE_MENU);
		Paopao.imgBack=midlet.createMenuImage(midlet.IMAGE_BACK);
		
		//创建菜单选项
		for (int i=0;i<9;i++)
		{
			imgMenuText[i]=midlet.createMenuImage("/menutext"+i+".png");
		}
		
		//初始化屏幕宽和高
		Paopao.screenWidth=getWidth();
		Paopao.screenHeight=getHeight();
		
		//计算菜单选项所在的x、y轴位置
		menu_selected_x=(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2+Paopao.MENU_X;
		menu_selected_y=(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2+Paopao.MENU_Y;
	}
	
	/**
	* 将自己显示在屏幕上
	*/
	protected void showMe()
	{
		//使用全屏模式
		this.setFullScreenMode(true);
		
		//调用Paopao的setDisplayable类，将自己显示在屏幕上
		midlet.setDisplayable(this);
		
		System.gc();
	}
	
	/**
	* 绘制菜单
	*/
	protected void paint(Graphics g)
	{
		//清屏
		g.setColor(0xFFFFFF);
		g.fillRect(0,0,getWidth(),getHeight());
		
		//绘制菜单背景
		g.drawImage(imgMenu,(getWidth()-imgMenu.getWidth())/2,(getHeight()-imgMenu.getHeight())/2,Graphics.TOP|Graphics.LEFT);
		
		//绘制菜单选项
		g.drawImage(imgMenuText[selectedIndex],menu_selected_x,menu_selected_y,Graphics.TOP|Graphics.LEFT);
	}
	
	/**
	* 按键事件
	*/
	protected void keyPressed(int keyCode)
	{
		switch (keyCode)
		{
		case Canvas.KEY_NUM2:
		case Canvas.KEY_NUM4:
			selectedIndex--;
			break;
		case Canvas.KEY_NUM5:
			logic();
			break;
		case Canvas.KEY_NUM8:
		case Canvas.KEY_NUM6:
			selectedIndex++;
			break;
		}
		switch (getGameAction(keyCode))
		{
		case UP:
		case LEFT:
			selectedIndex--;
			break;
		case DOWN:
		case RIGHT:
			selectedIndex++;
			break;
		case FIRE:
			logic();
			break;
		}
		if (selectedIndex<0) selectedIndex=8;
		else if (selectedIndex==1) selectedIndex=4;
		else if (selectedIndex==3) selectedIndex=0;
		else if (selectedIndex>8) selectedIndex=0;
		repaint();
	}
	
	/**
	* 逻辑判断
	*/
	private void logic()
	{
		switch (selectedIndex)
		{
		case 0:
			midlet.gameMenu.setMenuIndex(0);
			midlet.gameMenu.showMe();
			break;
		case 1:
			
			break;
		case 2:
			
			break;
		case 3:
			midlet.synthesis.setType(2);
			midlet.synthesis.showMe();
			break;
		case 4:
			paopaoSet=new PaopaoSet(midlet);
			paopaoSet.paintSet();
			break;
		case 5:
			midlet.gameMenu.setMenuIndex(3);
			midlet.gameMenu.showMe();
			break;
		case 6:
			midlet.synthesis.setType(0);
			midlet.synthesis.showMe();
			break;
		case 7:
			midlet.synthesis.setType(1);
			midlet.synthesis.showMe();
			break;
		case 8:
			midlet.quitGame();
			break;
		}
	}
}