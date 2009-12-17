import javax.microedition.lcdui.*;

/**
* 关于、帮助类
*/
public class Synthesis extends Canvas
{
	private Paopao midlet=null;
	private StringLayout sl,sl0,sl1,sl2,sl3;
	private Font font=null;
	private Image titleImg=null;
	
	//绘制文字的具体位置
	private int text_x;
	private int text_y;
	
	//显示类型
	private int type=0;
	
	private String Rank="";
	
	/**
	* 构造函数，引用Paopao类，并接收相关参数
	*/
	public Synthesis(Paopao midlet)
	{
		//设置全屏模式
		this.setFullScreenMode(true);
		this.midlet=midlet;
		
		//计算文字的具体位置
		text_x=(getWidth()-Paopao.GAME_IMAGE_WIDTH)/2+Paopao.TEXT_X;
		text_y=(getHeight()-Paopao.GAME_IMAGE_HEIGHT)/2+Paopao.TEXT_Y;
		
		font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		
		sl0 = new StringLayout(Paopao.HELP_TEXT, text_x, text_y, Paopao.TEXT_WIDTH, Paopao.TEXT_HEIGHT, 0, font);
		sl1 = new StringLayout(Paopao.ABOUT_TEXT, text_x, text_y, Paopao.TEXT_WIDTH, Paopao.TEXT_HEIGHT, 0, font);
		
		RecordStoreManage rsm=new RecordStoreManage();
		Paopao.RANK_TEXT=Paopao.RANK_TEXT+rsm.readGameRankInfo();
		rsm=null;
		sl2 = new StringLayout(Paopao.RANK_TEXT, text_x, text_y, Paopao.TEXT_WIDTH, Paopao.TEXT_HEIGHT, 0, font);
		sl3 = new StringLayout(Paopao.GAME_HELP_TEXT, text_x, text_y, Paopao.TEXT_WIDTH, Paopao.TEXT_HEIGHT, 0, font);
    }
	
	//显示自己
	protected void showMe()
	{
		midlet.setDisplayable(this);
	}
	
	//绘制屏幕
	protected void paint(Graphics g)
	{
		//清屏
		g.setColor(0xFFFFFF);
		g.fillRect(0,0,getWidth(),getHeight());
		
		//绘制菜单背景
		g.drawImage(Paopao.imgBack,(Paopao.screenWidth-Paopao.imgBack.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getHeight())/2,Graphics.TOP|Graphics.LEFT);
		
		//设置字体颜色
        g.setColor(0x000000);
		
		//绘制线条
		g.drawLine(text_x, text_y+11, text_x+Paopao.TEXT_WIDTH-5, text_y+11);
		
		switch (type)
		{
		case 0:
			drawHelp(g);
			break;
		case 1:
			drawAbout(g);
			break;
		case 2:
			drawRank(g);
			break;
		case 3:
			drawGameHelp(g);
			break;
		}
		
		//绘制线条
		g.drawLine(text_x, text_y+Paopao.TEXT_HEIGHT+2, text_x+Paopao.TEXT_WIDTH-5, text_y+Paopao.TEXT_HEIGHT+2);
		
        //在屏幕右下方绘制提示
        g.drawString(Paopao.TEXT_TIP, Paopao.screenWidth/2, text_y+Paopao.TEXT_HEIGHT+2,Graphics.TOP|Graphics.HCENTER);
		g.drawString(Paopao.TEXT_TIP2, Paopao.screenWidth/2, text_y+Paopao.TEXT_HEIGHT+14,Graphics.TOP|Graphics.HCENTER);
	}
	
	/**
	* 按键事件
	*/
	protected void keyPressed(int keyCode)
    {
		switch (keyCode)
		{
		case Canvas.KEY_NUM2:
		case -1:
			sl.prev();
            repaint();
			break;
		case Canvas.KEY_NUM8:
		case -2:
			sl.next();
            repaint();
			break;
		default:
            back();
            break;
		}
		
    }
    
    /**
    * 返回菜单界面
    */
    private void back()
    {
		if (type<3)
		{
			midlet.menu.showMe();
		}
		else if (type==3)
		{
			midlet.gameMenu.showMe();
		}
    }
	
	/**
	* 设置显示类型
	*/
	protected void setType(int type)
	{
		this.type=type;
	}
	
	/**
	* 绘制帮助
	*/
	private void drawHelp(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_HELP);
		
		//绘制标题
		g.drawImage(midlet.createMenuImage(Paopao.IMAGE_HELP),(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getWidth())/2-6,Graphics.TOP|Graphics.LEFT);
		
        //绘制内容
		sl=sl0;
        sl.draw(g, text_x, text_y+15);
	}
	
	/**
	* 绘制关于
	*/
	private void drawAbout(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_ABOUT);
		
		//绘制标题
		g.drawImage(titleImg,(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.GAME_IMAGE_WIDTH)/2-6,Graphics.TOP|Graphics.LEFT);
		
        //绘制内容
		sl=sl1;
        sl.draw(g, text_x, text_y+15);
	}
	
	/**
	* 绘制排行榜
	*/
	private void drawRank(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_RANK);
		
		//绘制标题
		g.drawImage(midlet.createMenuImage(Paopao.IMAGE_RANK),(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getWidth())/2-6,Graphics.TOP|Graphics.LEFT);
		
        //绘制内容
		sl=sl2;
        sl.draw(g, text_x, text_y+15);
	}
	
	/**
	* 绘制游戏帮助
	*/
	private void drawGameHelp(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_HELP);
		
		//绘制标题
		g.drawImage(midlet.createMenuImage(Paopao.IMAGE_HELP),(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getWidth())/2-6,Graphics.TOP|Graphics.LEFT);
		
        //绘制内容
		sl=sl3;
        sl.draw(g, text_x, text_y+15);
	}
}