import javax.microedition.lcdui.*;

/**
* 游戏主类
*/
public class Game extends GameMain implements Runnable
{
	private paopaoFly ppFly=null;
	private int gameLocationWidth,gameLocationHeight;
	
	/**
	* 构造函数
	*/
	public Game(Paopao midlet)
	{
		super(midlet);
		gameLocationWidth=(getWidth()-imgBack.getWidth())/2;
		gameLocationHeight=(getHeight()-imgBack.getHeight())/2;
	}
	
	/**
	* 运行线程
	*/
	public void run()
	{
		while (isActive)
		{
			long times= System.currentTimeMillis();
			
			//监听玩家输入信息
			input();
			
			if (!isPause)
			{
				//逻辑判断
				gameLogic();
				
				//绘制当前屏幕
				drawScreen(g);
			}
			
			times= System.currentTimeMillis()-times;
			if( times<Paopao.spf )
			{
				try
				{
					Thread.sleep(Paopao.spf-times );
				}
				catch(InterruptedException ie)
				{
					isActive=false;
				}
			}
		}
	}
	
	/**
    * 绘制当前屏幕
    */
    protected void drawScreen(Graphics g)
    {
		if (!isLoading)
		{
			arrow.setFrame(arrowSelectFrame);
			layermanager.paint(g,gameLocationWidth,gameLocationHeight);
			
			drawScore();
		}
		else
		{
			// 加载状态
			drawLoadingFrame();
        }
		flushGraphics();
	}

	/**
	* 逻辑判断
	*/
	private void gameLogic()
	{
		if (arrowSelectFrame<0) arrowSelectFrame=0;
		else if (arrowSelectFrame>70) arrowSelectFrame=70;
		if (toolsIndex<0) toolsIndex=toolsTotal;
		else if (toolsIndex>toolsTotal) toolsIndex=0;
		
		if (isSelectOK)
		{
			isReckon();
		}
		if (isFly)
		{
			if (isNew)
			{
				isNew=false;
				ppFly=new paopaoFly(paopaoSprite,paopaoSelectColor,paopao_default_x,paopao_default_y,arrowSelectFrame,1,1);
				ppFly.setPaopaoFlag(false);
				ppFly.setSPF(Paopao.spf);
			}
		}
	}
}