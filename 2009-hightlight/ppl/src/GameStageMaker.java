import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

/**
* ��Ϸ����
*/
public class GameStageMaker extends GameCanvas implements Runnable
{
	private Paopao midlet=null;
	private MyLine myLine=null;
	private Graphics g=null;
	
	//�߳�����״̬��־
	private boolean isActive=true;
	private boolean isPause=true;
	Thread thread=null;		//�����߳�
	
	//flag=0Ϊ��ӹؿ���flag=1Ϊ�༭�ؿ���selectLayerIDΪ��Ҫ�����Ĺؿ�ID
	private int flag=0;
	private int selectLayerID=1;
	
	/**
	* ͼ���뾫�����ر���
	*/
	private PaopaoSprite paopaoSprite=null;	//���ݾ���
	private PaopaoSprite rectangle=null;	//���ξ��飬����鿴��ǰ���ƻ���������
	private TiledLayer backLayer=null;		//����ͼ��
	private LayerManager layermanager=null;	//ͼ�����
	private Image imgBack=null;
	
	//�������ݱ�־����ȷ�ϼ���˱�־��Ϊtrue��Ȼ������Ӧ��ͼ�������Ӧ������
	private boolean isRender=false;
	
	//���ݾ������������������������
	private int paopao_row=0;
	private int paopao_col=0;
	
	//���ݾ�������ͼƬ�ϵ�λ��
	private int paopao_img_x=15;
	private int paopao_img_y=17;
	
	//���ݾ���������Ļ�ϵ�x��y����
	private int paopao_x=paopao_img_x;
	private int paopao_y=paopao_img_y;
	
	//��ǰ���ݾ�����ѡ�����ɫ��֡��
	private int paopaoSelectColor=1;
	
	//�������ԣ�ͼ��������
	private int layerID=-1;
	
	//�������飬���һ���Ǳ�־�У���־��ǰ���Ƿ����������������ȣ�0���ǲ�������1��������
	private byte[][] gameStageDate;
	
	
	/**
	* ���캯��
	*/
	public GameStageMaker(Paopao midlet) 
	{
		super(false);
		
		//ʹ��ȫ��ģʽ
		this.setFullScreenMode(true);
		
		this.midlet=midlet;
		myLine=new MyLine();
		
		//���ͼ��
		g = getGraphics();
		
		//��ʼ��ͼ�������ݾ���
		initLayerMenu();
		
	}
	
	/**
	* ����PaopaoSprite�����
	*/
	private PaopaoSprite createSprite(String spriteName,int paopaoWidth,int paopaoHeight) 
	{
		return new PaopaoSprite(midlet.createMenuImage(spriteName),paopaoWidth,paopaoHeight,0,0);
	}
	
	/**
	* ��ʼ��ͼ�������ݾ���
	*/
	private void initLayerMenu()
	{
		//��������ͼƬ
		imgBack=midlet.createMenuImage(Paopao.image_select_back);
		backLayer=new TiledLayer(1, 1, imgBack, Paopao.GAME_IMAGE_WIDTH, Paopao.GAME_IMAGE_HEIGHT);
		backLayer.setCell(0,0,1);
		
		//�������ξ���
		rectangle=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT);
		
		//ͼ�������뱳���㡢�����
		layermanager=new LayerManager();
		layermanager.append(rectangle);
		layermanager.append(createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT));
		layermanager.append(backLayer);
		paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
		
		//���þ������ɫ(֡)�ͳ�ʼλ��
		rectangle.setFrame(9);
		rectangle.setPosition(paopao_x,paopao_y);
		paopaoSprite.setFrame(paopaoSelectColor);
		paopaoSprite.setPosition(paopao_x,paopao_y);
	}
	
	/**
    * ��ʼ���ƹؿ���flagΪ��ӻ�༭�ؿ��ı�־
    */
    public void startMaker(int flag)
    {
		this.flag=flag;
		
		//�����߳�
        if (thread==null)
        {
            thread=new Thread(this);
            thread.start();
        }
		//��ʼ���ؿ�����
		setGameStageDate();
		//������Ļ
		layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
		
		midlet.setDisplayable(this);
		System.gc();
    }
	
	/**
	* ���Լ���ʾ����Ļ��
	*/
	protected void showMe()
	{
		layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
		midlet.setDisplayable(this);
	}
	
	/**
	* ���ý�Ҫ���в����Ĺؿ�ID
	*/
	protected void setSelectLayerID(int selectLayerID)
	{
		this.selectLayerID=selectLayerID;
	}
	
	/**
	* ��ʼ��gameStageDate����
	*/
	private void initializtionGameStageDate()
	{
		gameStageDate=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW];
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL;j++)
			{
				if (i%2!=0 && j==Paopao.PAOPAO_COL-1)
				{
					gameStageDate[j][i]=1;
				}
				else
				{
					gameStageDate[j][i]=0;
				}
			}
		}
	}
	
	/**
	* ���ƹؿ�
	*/
	private void drawStage()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL-1;j++)
			{
				if (gameStageDate[j][i]!=0)
				{
					paopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT);
					myLine.insertNode(2,i,j,gameStageDate[j][i],1,true);
					layermanager.insert(paopaoSprite,2);
					paopaoSprite.setFrame(gameStageDate[j][i]);
					paopaoSprite.setPosition(getRectX(j,i),getRectY(i));
					
					paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
				}
			}
		}
	}
	
	/**
	* ����gameStageDate����
	*/
	private void setGameStageDate()
	{
		if (flag==0)
		{
			//��ʼ��gameStageDate����
			initializtionGameStageDate();
		}
		else
		{
			//��ȡgameStageDate����
			RecordStoreManage rsm=new RecordStoreManage();
			gameStageDate=rsm.readGameStageDate(selectLayerID);
			rsm=null;
			if (gameStageDate==null)
			{
				initializtionGameStageDate();
				flag=0;
			}
		}
		drawStage();
	}
	
	/**
	* ��þ��鵱ǰ�ο������ڸ��X�������
	*/
	private int getRectX(int col,int row)
	{
		int x=0;
		if (gameStageDate[Paopao.PAOPAO_COL-1][row]==1)
		{
			if (col==0) col=1;
			x=col*Paopao.SPRITE_WIDTH-Paopao.SPRITE_WIDTH/2+paopao_img_x;
		}
		else
		{
			x=col*Paopao.SPRITE_WIDTH+paopao_img_x;
		}
		return x;
	}
	
	/**
	* ��þ��鵱ǰ�ο������ڸ��Y�������
	*/
	private int getRectY(int row)
	{
		return (row*(Paopao.SPRITE_HEIGHT-2)+paopao_img_y);
	}
	
	/**
	* �����߳�
	*/
	public void run()
	{
		while (isActive)
		{
			//�������������Ϣ
			input();
			
			if (!isPause)
			{
				//�߼��ж�
				logic();
				
				//���Ʋ�ˢ����Ļ
				drawScreen(g);
				
				//��ͣ
				isPause=true;
			}
			
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ie)
			{
				isActive=false;
			}
		}
	}
	
	/**
	* �������������Ϣ
	*/
	private void input()
	{
		int keyStates = getKeyStates();
		
		if ((keyStates & DOWN_PRESSED) != 0) 
		{
			isPause=false;
			paopao_row++;
		}
		else if ((keyStates & UP_PRESSED) != 0) 
		{
			isPause=false;
			paopao_row--;
		}
		
		if ((keyStates & RIGHT_PRESSED) != 0) 
		{
			isPause=false;
			paopao_col++;
		}
		else if ((keyStates & LEFT_PRESSED) != 0) 
		{
			isPause=false;
			paopao_col--;
		}
		
		if ((keyStates & FIRE_PRESSED) != 0)
		{
			isPause=false;
			isRender=true;
		}
	}
	
	/**
	* �û������¼�
	*/
	protected void keyPressed(int keyCode)
	{
		isPause=false;
		
		switch (keyCode)
		{
		case Canvas.KEY_NUM1:
			paopao_row--;
			paopao_col--;
			break;
		case Canvas.KEY_NUM2:
			paopao_row--;
			break;
		case Canvas.KEY_NUM3:
			paopao_row--;
			paopao_col++;
			break;
		case Canvas.KEY_NUM4:
			paopao_col--;
			break;
		case Canvas.KEY_NUM5:
			isRender=true;
			break;
		case Canvas.KEY_NUM6:
			paopao_col++;
			break;
		case Canvas.KEY_NUM7:
			paopao_row++;
			paopao_col--;
			break;
		case Canvas.KEY_NUM8:
			paopao_row++;
			break;
		case Canvas.KEY_NUM9:
			paopao_row++;
			paopao_col++;
			break;
		case Canvas.KEY_NUM0:
			printGameStageDate();
			break;
		case Canvas.KEY_STAR:
			paopaoSelectColor--;
			break;
		case Canvas.KEY_POUND:
			paopaoSelectColor++;
			break;
		}
		
		if (getKeyName(keyCode).equals("SOFT1") || getKeyName(keyCode).equals("SOFT2"))
		{
			midlet.gameMenu.setMenuIndex(7);
			midlet.gameMenu.showMe();
		}
	}
	
	/**
	* �߼��ж�
	*/
	private void logic()
	{
		//������ѡ������ɫ���ƶ������еķ�Χ
		if (paopaoSelectColor<1) paopaoSelectColor=8;
		else if (paopaoSelectColor>8) paopaoSelectColor=1;
		if (paopao_row<0) paopao_row=0;
		else if (paopao_row>Paopao.PAOPAO_ROW-1) paopao_row=Paopao.PAOPAO_ROW-1;
		if (paopao_col<0) paopao_col=0;
		else if (paopao_col>Paopao.PAOPAO_COL-2) paopao_col=Paopao.PAOPAO_COL-2;
		
		//���㾫�����ڵ�x��y������
		if (gameStageDate[Paopao.PAOPAO_COL-1][paopao_row]==1)
		{
			if (paopao_col==0) paopao_col=1;
			paopao_x=paopao_col*Paopao.SPRITE_WIDTH-Paopao.SPRITE_WIDTH/2+paopao_img_x;
		}
		else
		{
			paopao_x=paopao_col*Paopao.SPRITE_WIDTH+paopao_img_x;
		}
		paopao_y=paopao_row*(Paopao.SPRITE_HEIGHT-2)+paopao_img_y;
		
		//���þ������ɫ
		paopaoSprite.setFrame(paopaoSelectColor);
		
		//�ƶ�����
		rectangle.setPosition(paopao_x,paopao_y);
		paopaoSprite.setPosition(paopao_x,paopao_y);
	}
	
	/**
	* ���ùؿ���ر���
	*/
	protected void clearGameStageDate()
	{
		initializtionGameStageDate();
		myLine.setNodeNull();
		while (layermanager.getSize()>3)
		{
			layermanager.remove(layermanager.getLayerAt(2));
		}
		System.gc();
	}
	
	/**
	* ���ƻ���
	*/
	private void drawScreen(Graphics g)
	{
		//�ж��Ƿ���ȷ�ϼ����������ڵ�ǰλ�û�������
		if (isRender)
		{
			//��¼��ǰλ�����ݵ���ɫ
			gameStageDate[paopao_col][paopao_row]=(byte)paopaoSelectColor;
			
			//�������н����е������滻���µ����ݻ��������
			if ((layerID=myLine.findNode(paopao_row,paopao_col))==-1)
			{
				myLine.insertNode(2,paopao_row,paopao_col,paopaoSelectColor,1,true);
				layermanager.insert(createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT),1);
				paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
			}
			else
			{
				myLine.setNode(layerID,paopaoSelectColor);
				paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(layerID);
				paopaoSprite.setFrame(paopaoSelectColor);
				paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
			}
			
			isRender=false;
		}
		
		//����ͼ��
		layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
		
		// ˢ����Ļ
		flushGraphics();
	}
	
	/**
	* �ڿ���̨��ӡgameStageDate���飬�Ա����Ƿ���ȷ
	*/
	private void printGameStageDate()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL;j++)
			{
				System.out.print(gameStageDate[j][i]+",");
			}
			System.out.println();
		}
	}
	
	/**
	* ɾ����ǰλ������
	*/
	protected void delPaopao()
	{
		if ((layerID=myLine.findNode(paopao_row,paopao_col))!=-1)
		{
			layermanager.remove(layermanager.getLayerAt(layerID));
			myLine.delNode(layerID);
			gameStageDate[paopao_col][paopao_row]=0;
		}
	}
	
	/**
	* ���浱ǰ�༭�Ĺؿ�
	*/
	protected void saveStage()
	{
		RecordStoreManage rsm=new RecordStoreManage();
		//��ӹؿ�
		if (flag==0)
		{
			if (rsm.addGameStageDate(gameStageDate))
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(2);
				midlet.gameMenu.showMe();
			}
			else
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(3);
				midlet.gameMenu.showMe();
			}
		}
		//�����޸ĺ�Ĺؿ�
		else
		{
			if (rsm.setGameStageDate(gameStageDate,selectLayerID))
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(2);
				midlet.gameMenu.showMe();
			}
			else
			{
				midlet.gameMenu.setEnterFlag(1);
				midlet.gameMenu.setMenuIndex(8);
				midlet.gameMenu.setInfoFrameIndex(3);
				midlet.gameMenu.showMe();
			}
		}
	}
	
	/**
	* ɾ��ָ���ؿ�
	*/
	protected void delStage()
	{
		RecordStoreManage rsm=new RecordStoreManage();
		if (rsm.delGameStageDate(selectLayerID))
		{
			Paopao.gameStageTotal--;
			midlet.gameMenu.setEnterFlag(1);
			midlet.gameMenu.setMenuIndex(8);
			midlet.gameMenu.setInfoFrameIndex(4);
			midlet.gameMenu.showMe();
		}
		else
		{
			midlet.gameMenu.setEnterFlag(1);
			midlet.gameMenu.setMenuIndex(8);
			midlet.gameMenu.setInfoFrameIndex(5);
			midlet.gameMenu.showMe();
		}
	}
}