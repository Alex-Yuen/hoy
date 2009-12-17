import java.util.*;
import javax.microedition.lcdui.*;

/**
* LOGO������
*/
public class ShowLogo2 extends Canvas
{
	private Paopao midlet=null;
	private Timer timer=null;
	
	/**
	* ���캯��������Paopao��
	*/
	public ShowLogo2(Paopao midlet)
	{
		this.midlet=midlet;
	}
	
	/**
	* ��ʾ�˵�
	*/
	private void showMenu()
	{
		//�رձ����е��߳�
		timer.cancel();		
		timer=null;
		midlet.showLogo2=null;
		
		//��ʾ�˵�
		midlet.menu.showMe();
	}
	
	/**
	* ���Լ���ʾ����Ļ��
	*/
	protected void showMe()
	{
		//ʹ��ȫ��ģʽ
		this.setFullScreenMode(true);	
		
		//��ʼ���̣߳��ڵȴ�һ��ʱ����Զ���ת��ָ���Ľ���
		timer=new Timer();
		timer.schedule(new TimerTask()
		{
			public void run()
			{
				showMenu();
			}
		},Paopao.SHOWLOGO_TIME);
		
		//����Paopao��setDisplayable�࣬��ָ��������ʾ����Ļ��
		midlet.setDisplayable(this);
	}
	
	/**
	* ��LOGO��������Ļ��
	*/
	protected void paint(Graphics g)
	{
		//����
		g.setColor(0);
		g.fillRect(0,0,getWidth(),getHeight());
		
		try
		{
			Image imgLogo=Image.createImage(Paopao.IMAGE_LOGO2);
			
			//����LOGO
			g.drawImage(imgLogo,(getWidth()-imgLogo.getWidth())/2,(getHeight()-imgLogo.getHeight())/2,Graphics.LEFT|Graphics.TOP);
		}
		catch (Exception ex)
		{
			System.out.println("ͼƬ������");
		}
	}
	
	/**
	* �����⽡������һ��
	*/
	protected void keyPressed(int KeyCode)
	{
		showMenu();
	}
}

