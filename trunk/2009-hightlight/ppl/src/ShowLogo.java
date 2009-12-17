import java.util.*;
import javax.microedition.lcdui.*;

/**
* LOGO������
*/
public class ShowLogo extends Canvas
{
	private Paopao midlet=null;
	private Timer timer=null;
	private ShowLogo2 showLogo2=null;
	
	/**
	* ���캯��������Paopao��
	*/
	public ShowLogo(Paopao midlet)
	{
		this.midlet=midlet;
	}
	
	/**
	* ��ʾ�˵�
	*/
	private void showLogo2()
	{
		//�رձ����е��߳�
		timer.cancel();		
		timer=null;
		midlet.showLogo=null;
		
		//��ʾ�ڶ���LOGO
		midlet.showLogo2.showMe();
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
				showLogo2();
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
		g.setColor(0xFFFFFF);
		g.fillRect(0,0,getWidth(),getHeight());
		
		try
		{
			Image imgLogo=Image.createImage(Paopao.IMAGE_LOGO);
			
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
		showLogo2();
	}
}

