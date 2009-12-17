import javax.microedition.lcdui.*;

/**
* ���ڡ�������
*/
public class Synthesis extends Canvas
{
	private Paopao midlet=null;
	private StringLayout sl,sl0,sl1,sl2,sl3;
	private Font font=null;
	private Image titleImg=null;
	
	//�������ֵľ���λ��
	private int text_x;
	private int text_y;
	
	//��ʾ����
	private int type=0;
	
	private String Rank="";
	
	/**
	* ���캯��������Paopao�࣬��������ز���
	*/
	public Synthesis(Paopao midlet)
	{
		//����ȫ��ģʽ
		this.setFullScreenMode(true);
		this.midlet=midlet;
		
		//�������ֵľ���λ��
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
	
	//��ʾ�Լ�
	protected void showMe()
	{
		midlet.setDisplayable(this);
	}
	
	//������Ļ
	protected void paint(Graphics g)
	{
		//����
		g.setColor(0xFFFFFF);
		g.fillRect(0,0,getWidth(),getHeight());
		
		//���Ʋ˵�����
		g.drawImage(Paopao.imgBack,(Paopao.screenWidth-Paopao.imgBack.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getHeight())/2,Graphics.TOP|Graphics.LEFT);
		
		//����������ɫ
        g.setColor(0x000000);
		
		//��������
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
		
		//��������
		g.drawLine(text_x, text_y+Paopao.TEXT_HEIGHT+2, text_x+Paopao.TEXT_WIDTH-5, text_y+Paopao.TEXT_HEIGHT+2);
		
        //����Ļ���·�������ʾ
        g.drawString(Paopao.TEXT_TIP, Paopao.screenWidth/2, text_y+Paopao.TEXT_HEIGHT+2,Graphics.TOP|Graphics.HCENTER);
		g.drawString(Paopao.TEXT_TIP2, Paopao.screenWidth/2, text_y+Paopao.TEXT_HEIGHT+14,Graphics.TOP|Graphics.HCENTER);
	}
	
	/**
	* �����¼�
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
    * ���ز˵�����
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
	* ������ʾ����
	*/
	protected void setType(int type)
	{
		this.type=type;
	}
	
	/**
	* ���ư���
	*/
	private void drawHelp(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_HELP);
		
		//���Ʊ���
		g.drawImage(midlet.createMenuImage(Paopao.IMAGE_HELP),(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getWidth())/2-6,Graphics.TOP|Graphics.LEFT);
		
        //��������
		sl=sl0;
        sl.draw(g, text_x, text_y+15);
	}
	
	/**
	* ���ƹ���
	*/
	private void drawAbout(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_ABOUT);
		
		//���Ʊ���
		g.drawImage(titleImg,(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.GAME_IMAGE_WIDTH)/2-6,Graphics.TOP|Graphics.LEFT);
		
        //��������
		sl=sl1;
        sl.draw(g, text_x, text_y+15);
	}
	
	/**
	* �������а�
	*/
	private void drawRank(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_RANK);
		
		//���Ʊ���
		g.drawImage(midlet.createMenuImage(Paopao.IMAGE_RANK),(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getWidth())/2-6,Graphics.TOP|Graphics.LEFT);
		
        //��������
		sl=sl2;
        sl.draw(g, text_x, text_y+15);
	}
	
	/**
	* ������Ϸ����
	*/
	private void drawGameHelp(Graphics g)
	{
		titleImg=midlet.createMenuImage(Paopao.IMAGE_HELP);
		
		//���Ʊ���
		g.drawImage(midlet.createMenuImage(Paopao.IMAGE_HELP),(Paopao.screenWidth-titleImg.getWidth())/2,(Paopao.screenHeight-Paopao.imgBack.getWidth())/2-6,Graphics.TOP|Graphics.LEFT);
		
        //��������
		sl=sl3;
        sl.draw(g, text_x, text_y+15);
	}
}