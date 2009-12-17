import javax.microedition.lcdui.*;

/**
* ��Ϸ����
*/
public class Game extends GameMain implements Runnable
{
	private paopaoFly ppFly=null;
	private int gameLocationWidth,gameLocationHeight;
	
	/**
	* ���캯��
	*/
	public Game(Paopao midlet)
	{
		super(midlet);
		gameLocationWidth=(getWidth()-imgBack.getWidth())/2;
		gameLocationHeight=(getHeight()-imgBack.getHeight())/2;
	}
	
	/**
	* �����߳�
	*/
	public void run()
	{
		while (isActive)
		{
			long times= System.currentTimeMillis();
			
			//�������������Ϣ
			input();
			
			if (!isPause)
			{
				//�߼��ж�
				gameLogic();
				
				//���Ƶ�ǰ��Ļ
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
    * ���Ƶ�ǰ��Ļ
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
			// ����״̬
			drawLoadingFrame();
        }
		flushGraphics();
	}

	/**
	* �߼��ж�
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