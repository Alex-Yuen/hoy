import java.util.*;
import javax.microedition.lcdui.*;

/**
* LOGO闪屏类
*/
public class ShowLogo extends Canvas
{
	private Paopao midlet=null;
	private Timer timer=null;
	private ShowLogo2 showLogo2=null;
	
	/**
	* 构造函数，引用Paopao类
	*/
	public ShowLogo(Paopao midlet)
	{
		this.midlet=midlet;
	}
	
	/**
	* 显示菜单
	*/
	private void showLogo2()
	{
		//关闭本类中的线程
		timer.cancel();		
		timer=null;
		midlet.showLogo=null;
		
		//显示第二个LOGO
		midlet.showLogo2.showMe();
	}
	
	/**
	* 将自己显示在屏幕上
	*/
	protected void showMe()
	{
		//使用全屏模式
		this.setFullScreenMode(true);	
		
		//初始化线程，在等待一定时间后自动跳转到指定的界面
		timer=new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				showLogo2();
			}
		},Paopao.SHOWLOGO_TIME);
		
		//调用Paopao的setDisplayable类，将指定界面显示在屏幕上
		midlet.setDisplayable(this);
	}
	
	/**
	* 将LOGO绘制在屏幕上
	*/
	protected void paint(Graphics g)
	{
		//清屏
		g.setColor(0xFFFFFF);
		g.fillRect(0,0,getWidth(),getHeight());
		
		try
		{
			Image imgLogo=Image.createImage(Paopao.IMAGE_LOGO);
			
			//绘制LOGO
			g.drawImage(imgLogo,(getWidth()-imgLogo.getWidth())/2,(getHeight()-imgLogo.getHeight())/2,Graphics.LEFT|Graphics.TOP);
		}
		catch (Exception ex)
		{
			System.out.println("图片不存在");
		}
	}
	
	/**
	* 按任意健进入下一屏
	*/
	protected void keyPressed(int KeyCode)
	{
		showLogo2();
	}
}

