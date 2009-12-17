import javax.microedition.lcdui.*;

/**
* �˵���
*/
public class Menu extends Canvas
{
	private Paopao midlet=null;
	private PaopaoSet paopaoSet=null;
	
	private Image imgMenu=null;
	private Image imgMenuText[]=new Image[9];
	
	//��ǰ�˵�������
	private int selectedIndex=0;
	
	//�˵�ѡ�������Ʋ˵�
	private int menu_selected_x;
	private int menu_selected_y;
	
	/**
	* ���캯��������Paopao�࣬����ʼ����ر���
	*/
	public Menu(Paopao midlet)
	{
		this.midlet=midlet;
		
		//��������ͼƬ
		imgMenu=midlet.createMenuImage(midlet.IMAGE_MENU);
		Paopao.imgBack=midlet.createMenuImage(midlet.IMAGE_BACK);
		
		//�����˵�ѡ��
		for (int i=0;i<9;i++)
		{
			imgMenuText[i]=midlet.createMenuImage("/menutext"+i+".png");
		}
		
		//��ʼ����Ļ��͸�
		Paopao.screenWidth=getWidth();
		Paopao.screenHeight=getHeight();
		
		//����˵�ѡ�����ڵ�x��y��λ��
		menu_selected_x=(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2+Paopao.MENU_X;
		menu_selected_y=(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2+Paopao.MENU_Y;
	}
	
	/**
	* ���Լ���ʾ����Ļ��
	*/
	protected void showMe()
	{
		//ʹ��ȫ��ģʽ
		this.setFullScreenMode(true);
		
		//����Paopao��setDisplayable�࣬���Լ���ʾ����Ļ��
		midlet.setDisplayable(this);
		
		System.gc();
	}
	
	/**
	* ���Ʋ˵�
	*/
	protected void paint(Graphics g)
	{
		//����
		g.setColor(0xFFFFFF);
		g.fillRect(0,0,getWidth(),getHeight());
		
		//���Ʋ˵�����
		g.drawImage(imgMenu,(getWidth()-imgMenu.getWidth())/2,(getHeight()-imgMenu.getHeight())/2,Graphics.TOP|Graphics.LEFT);
		
		//���Ʋ˵�ѡ��
		g.drawImage(imgMenuText[selectedIndex],menu_selected_x,menu_selected_y,Graphics.TOP|Graphics.LEFT);
	}
	
	/**
	* �����¼�
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
	* �߼��ж�
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