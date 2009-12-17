import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

/**
* �˵���
*/
public class GameMenu extends GameCanvas implements Runnable
{
	private Paopao midlet=null;
	private Graphics g=null;
	private Font font=null;
	private Thread thread=null;		//�����߳�
	
	//�߳�����״̬
	private boolean isActive=true;
	
	/**
	* �˵���ʶ��0����ģʽ���˵���1�����ս���˵���2����ģʽ���˵���3�ؿ��༭�����˵���4����ģʽ�����˵���
	* 5��6��7�ؿ��༭�����˲˵���8��Ϸ״̬��9�ؿ�����ѡ�����
	*/
	private int menuIndex=0;
	
	//������Ϸ�����˵���ؿ��༭�������˵����뱾����ģ�0Ϊ��Ϸ�����˵���1Ϊ�ؿ��༭�������˵�
	private int enterFlag=0;
	
	//��Ϸ�˵���ǰѡ��ID
	private int gemeMenu_select=1;
	private boolean isSelectOK=false;
	private int textMenuBack_y=46;
	
	//��Ϸ�˵���ر���
	private LayerManager layermanager=null;	//ͼ�����
	private PaopaoSprite textSprite0,textSprite1,textSprite2,textSprite3,textSprite4,layerMenuSpriteBack;
	private TiledLayer backLayer=null;		//����ͼ��
	
	//��Ϸ�����˵���ر���
	private final String[] GAMESTAGE_MENUTEXT={"������Ϸ","���¿�ʼ","���浱ǰ����","�˳�"};
	private Image gameStageMenuImage=null;
	private int gameStageMenu_x=30;
	private int gameStageMenu_y=48;
	
	//��Ϸ״̬��Ϣ����ر���
	private int infoFrameIndex=0;
	private LayerManager layerInfoFrame=null;
	private TiledLayer infoBackLayer=null;
	private PaopaoSprite infoText=null;
	private Image blackboard=null;
	
	//�ؿ��༭�����˵���ر���
	private LayerManager makerMenu=null;	//ͼ�����
	private PaopaoSprite makerMenuSprite0,makerMenuSprite1,makerMenuSprite2,makerMenuSprite3,makerMenuSprite4;
	private boolean isSelectLayerID=false;
	private int selectLayerID=1;	//��Ҫ���в����Ĺؿ�ID
	
	//�ؿ��༭�������˵���ر���
	private final String[] MAKER_MENUTEXT={"ɾ����ǰ����","����ػ�","����ؿ�","�˳�"};
	
	
	/**
	* ���캯��
	*/
	public GameMenu(Paopao midlet)
	{
		super(false);
		
		//ʹ��ȫ��ģʽ
		this.setFullScreenMode(true);
		this.midlet=midlet;
		
		//���ͼ��
		g = getGraphics();
		
		initLayerMenu();
		initMakerMenu();
		initLayerMenu2();
		initInfoFrame();
	}
	
	/**
	* ����PaopaoSprite�����
	*/
	private PaopaoSprite createSprite(String spriteName,int width,int height) 
	{
		return new PaopaoSprite(midlet.createMenuImage(spriteName),width,height,0,0);
	}
	
	/**
	* ��ʼ���˵�ͼ��
	*/
	private void initLayerMenu()
	{
		//��������ͼƬ
		backLayer=new TiledLayer(1, 1, Paopao.imgBack, Paopao.GAME_IMAGE_WIDTH, Paopao.GAME_IMAGE_HEIGHT);
		backLayer.setCell(0,0,1);
		textSprite0=createSprite(Paopao.IMAGE_TITLE,102,22);
		textSprite1=createSprite(Paopao.IMAGE_TITLE,102,22);
		textSprite2=createSprite(Paopao.IMAGE_TITLE,102,22);
		textSprite3=createSprite(Paopao.IMAGE_TITLE,102,22);
		textSprite4=createSprite(Paopao.IMAGE_TITLE,102,22);
		layerMenuSpriteBack=createSprite(Paopao.IMAGE_TEXTMENUBACK,176,30);
		
		//ͼ�������뱳���㡢�����
		layermanager=new LayerManager();
		layermanager.append(textSprite0);
		layermanager.append(textSprite1);
		layermanager.append(textSprite2);
		layermanager.append(textSprite3);
		layermanager.append(textSprite4);
		layermanager.append(layerMenuSpriteBack);
		layermanager.append(backLayer);
		
		textSprite0.setFrame(0);
		textSprite1.setFrame(1);
		textSprite2.setFrame(2);
		textSprite3.setFrame(3);
		textSprite4.setFrame(4);
		
		textSprite0.setPosition(40,15);
		textSprite1.setPosition(40,50);
		textSprite2.setPosition(40,80);
		textSprite3.setPosition(40,110);
		textSprite4.setPosition(40,140);
		layerMenuSpriteBack.setPosition(0,textMenuBack_y);
	}
	
	/**
	* ��ʼ���ؿ��༭�����˵�
	*/
	private void initMakerMenu()
	{
		makerMenuSprite0=createSprite(Paopao.IMAGE_MENUTEXT,92,22);
		makerMenuSprite1=createSprite(Paopao.IMAGE_MENUTEXT,92,22);
		makerMenuSprite2=createSprite(Paopao.IMAGE_MENUTEXT,92,22);
		makerMenuSprite3=createSprite(Paopao.IMAGE_MENUTEXT,92,22);
		makerMenuSprite4=createSprite(Paopao.IMAGE_MENUTEXT,92,22);
		
		makerMenu=new LayerManager();
		makerMenu.append(makerMenuSprite0);
		makerMenu.append(makerMenuSprite1);
		makerMenu.append(makerMenuSprite2);
		makerMenu.append(makerMenuSprite3);
		makerMenu.append(makerMenuSprite4);
		makerMenu.append(layerMenuSpriteBack);
		makerMenu.append(backLayer);
		
		makerMenuSprite0.setFrame(0);
		makerMenuSprite1.setFrame(1);
		makerMenuSprite2.setFrame(2);
		makerMenuSprite3.setFrame(3);
		makerMenuSprite4.setFrame(4);
		
		makerMenuSprite0.setPosition(42,15);
		makerMenuSprite1.setPosition(42,50);
		makerMenuSprite2.setPosition(42,80);
		makerMenuSprite3.setPosition(42,110);
		makerMenuSprite4.setPosition(42,140);
		layerMenuSpriteBack.setPosition(0,textMenuBack_y);
		
	}
	
	/**
	* ��ʼ�������˵�
	*/
	private void initLayerMenu2()
	{
		gameStageMenuImage=midlet.createMenuImage(Paopao.IMAGE_GAMESTAGEMENU);
	}
	
	/**
	* ��ʼ����Ϣ��
	*/
	private void initInfoFrame()
	{
		//��������ͼƬ
		blackboard=midlet.createMenuImage(Paopao.IMAGE_BLACKBOARD);
		infoBackLayer=new TiledLayer(1, 1, blackboard, 176, 140);
		infoBackLayer.setCell(0,0,1);
		infoText=createSprite(Paopao.IMAGE_INFOTEXT,122,35);
		
		layerInfoFrame=new LayerManager();
		layerInfoFrame.append(infoText);
		layerInfoFrame.append(infoBackLayer);
		layerInfoFrame.append(backLayer);
		
		//���þ����λ��
		infoText.setPosition((Paopao.GAME_IMAGE_WIDTH-infoText.getWidth())/2,(Paopao.GAME_IMAGE_HEIGHT-infoText.getHeight())/2);
		infoBackLayer.setPosition((Paopao.GAME_IMAGE_WIDTH-infoBackLayer.getWidth())/2,(Paopao.GAME_IMAGE_HEIGHT-infoBackLayer.getHeight())/2);
	}
	
	/**
	* ���Լ���ʾ����Ļ��
	*/
	protected void showMe()
	{
		switch (menuIndex)
		{
		case 0:
			gemeMenu_select=1;
			layermanager.paint(g,(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2,(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2);
			break;
		case 1:
			
			break;
		case 2:
			
			break;
		case 3:		//�ؿ��༭�����˵�
			gemeMenu_select=1;
			makerMenu.paint(g,(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2,(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2);
			break;
		case 4:
			gemeMenu_select=0;
			drawStageMenu2();
			break;
		case 5:
			
			break;
		case 6:
			
			break;
		case 7:
			gemeMenu_select=0;
			drawMakerMenu();
			break;
		case 8:
			drawInfoFrame();
			break;
		}
		
		if (thread==null)
        {
            thread=new Thread(this);
            thread.start();
        }
		
		//����Paopao��setDisplayable�࣬���Լ���ʾ����Ļ��
		midlet.setDisplayable(this);
		
		System.gc();
	}
	
	/**
	* ���ò˵���ʶ
	*/
	protected void setMenuIndex(int menuIndex)
	{
		this.menuIndex=menuIndex;
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
			//input();
			
			//�߼��ж�
			logic();
			
			//���Ƶ�ǰ��Ļ
			drawScreen();
			
			times= System.currentTimeMillis()-times;
			if( times<100 )
			{
				try
				{
					Thread.sleep(100-times );
				}
				catch(InterruptedException ie)
				{
					isActive=false;
				}
			}
		}
	}
	
	/**
	* �����¼�
	*/
	protected void keyPressed(int keyCode)
    {
		if (menuIndex<8)
		{
			switch (keyCode)
			{
			case Canvas.KEY_NUM2:
			case -1:
				gemeMenu_select--;
				break;
			case Canvas.KEY_NUM4:
			case -3:
				gemeMenu_select--;
				break;
			case Canvas.KEY_NUM5:
			case -5:
				isSelectOK=true;
				break;
			case Canvas.KEY_NUM6:
			case -4:
				gemeMenu_select++;
				break;
			case Canvas.KEY_NUM8:
			case -2:
				gemeMenu_select++;
				break;
			}
		}
		else if (menuIndex==8)
		{
			isSelectOK=true;
		}
		else if (menuIndex==9)
		{
			switch (keyCode)
			{
			case Canvas.KEY_NUM2:
			case -1:
				selectLayerID+=10;
				break;
			case Canvas.KEY_NUM4:
			case -3:
				selectLayerID--;
				break;
			case Canvas.KEY_NUM5:
			case -5:
				isSelectOK=true;
				break;
			case Canvas.KEY_NUM6:
			case -4:
				selectLayerID++;
				break;
			case Canvas.KEY_NUM8:
			case -2:
				selectLayerID-=10;
				break;
			default:
				menuIndex=3;
				break;
			}
			
		}
    }
    
    /**
	* �߼��ж�
	*/
	private void logic()
	{
		switch (menuIndex)
		{
		case 0:		//����ģʽ���˵�
			if (gemeMenu_select<1) gemeMenu_select=4;
			else if (gemeMenu_select>4) gemeMenu_select=1;
			
			//ִ����ѡ����
			if (isSelectOK)
			{
				isSelectOK=false;
				switch (gemeMenu_select)
				{
				case 1:
					midlet.gamePlay.showMe();
					midlet.gamePlay.startPlay(1);
					break;
				case 2:
					midlet.gamePlay.setStageIndex();
					midlet.gamePlay.startPlay(Paopao.stageIndex);
					break;
				case 3:
					midlet.synthesis.setType(3);
					midlet.synthesis.showMe();
					break;
				case 4:
					//���ز˵�����
					midlet.menu.showMe();
					System.gc();
					break;
				}
			}
			break;
		case 1:
			
			break;
		case 2:
			
			break;
		case 3:		//�ؿ��༭�����˵� 
			if (gemeMenu_select<1) gemeMenu_select=4;
			else if (gemeMenu_select>4) gemeMenu_select=1;
			
			//ִ����ѡ����
			if (isSelectOK)
			{
				isSelectOK=false;
				switch (gemeMenu_select)
				{
				case 1:
					midlet.stageMaker.startMaker(0);
					break;
				case 2:
					menuIndex=9;
					break;
				case 3:
					menuIndex=9;
					break;
				case 4:
					//���ز˵�����
					midlet.menu.showMe();
					System.gc();
					break;
				}
			}
			break;
		case 4:		//����ģʽ�����˵�
			if (gemeMenu_select<0) gemeMenu_select=3;
			else if (gemeMenu_select>3) gemeMenu_select=0;
			
			//ִ����ѡ����
			if (isSelectOK)
			{
				isSelectOK=false;
				switch (gemeMenu_select)
				{
				case 0:
					midlet.gamePlay.showMe();
					midlet.gamePlay.pause();
					break;
				case 1:
					midlet.gamePlay.startPlay(midlet.gamePlay.getStageIndex());
					midlet.gamePlay.showMe();
					break;
				case 2:
					midlet.gamePlay.setSaveStageIndex();
					RecordStoreManage rsm=new RecordStoreManage();
					if (rsm.saveGameSetInfo(1))	infoFrameIndex=2;
					else infoFrameIndex=3;
					menuIndex=8;
					rsm=null;
					break;
				case 3:
					//midlet.gamePlay.pTimeOut.closeThread();
					midlet.gamePlay.closeThread();
					menuIndex=0;
					
					//���ز˵�����
					midlet.gameMenu.showMe();
					System.gc();
					break;
				}
			}
			break;
		case 5:
		case 6:
			break;
		case 7:		//�ؿ��༭�����˲˵�
			if (gemeMenu_select<0) gemeMenu_select=3;
			else if (gemeMenu_select>3) gemeMenu_select=0;
			
			//ִ����ѡ����
			if (isSelectOK)
			{
				isSelectOK=false;
				switch (gemeMenu_select)
				{
				case 0:
					midlet.stageMaker.delPaopao();
					midlet.stageMaker.showMe();
					break;
				case 1:
					midlet.stageMaker.clearGameStageDate();
					midlet.stageMaker.showMe();
					break;
				case 2:
					midlet.stageMaker.saveStage();
					break;
				case 3:
					midlet.stageMaker.clearGameStageDate();
					menuIndex=3;
					
					//���ز˵�����
					midlet.gameMenu.showMe();
					break;
				}
			}
			break;
		case 8:		//��Ϸ״̬
			//ִ����ѡ����
			if (isSelectOK)
			{
				isSelectOK=false;
				switch (infoFrameIndex)
				{
				case 0:
					midlet.gamePlay.nextStage();
					break;
				case 1:
					midlet.gamePlay.startPlay(midlet.gamePlay.getStageIndex());
					break;
				case 2:
				case 3:
					if (enterFlag==0)
					{
						midlet.gamePlay.showMe();
						midlet.gamePlay.pause();
					}
					else
					{
						midlet.stageMaker.showMe();
					}
					break;
				case 4:
				case 5:
					menuIndex=3;
					break;
				case 6:
					menuIndex=0;
					break;
				}
			}
			break;
		case 9:		//�ؿ�����ѡ�����
			if (selectLayerID<1) selectLayerID=Paopao.gameStageTotal;
			else if (selectLayerID>Paopao.gameStageTotal) selectLayerID=1;
			
			//ִ����ѡ����
			if (isSelectOK)
			{
				isSelectOK=false;
				if (gemeMenu_select==2)
				{
					midlet.stageMaker.setSelectLayerID(selectLayerID);
					midlet.stageMaker.startMaker(1);
				}
				else if (gemeMenu_select==3)
				{
					midlet.stageMaker.setSelectLayerID(selectLayerID);
					midlet.stageMaker.delStage();
				}
			}
			break;
		}
	}
	
	/**
	* ������Ļ
	*/
	private void drawScreen()
	{
		switch (menuIndex)
		{
		case 0:
			drawStageMenu();
			break;
		case 1:
			break;
		case 2:
			break;
		case 3:
			drawMakerMenu();
			break;
		case 4:
			drawStageMenu2();
			break;
		case 5:
		case 6:
			break;
		case 7:
			drawMakerMenu2();
			break;
		case 8:
			drawInfoFrame();
			break;
		case 9:
			drawSelectLayerID();
			break;
		}
		flushGraphics();
	}
	
	/**
	* ���ƹؿ����˵�
	*/
	private void drawStageMenu()
	{
		layerMenuSpriteBack.setPosition(0,textMenuBack_y+(gemeMenu_select-1)*30);
		layermanager.paint(g,(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2,(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2);
	}
	
	/**
	* ���ƹؿ��༭���˵�
	*/
	private void drawMakerMenu()
	{
		layerMenuSpriteBack.setPosition(0,textMenuBack_y+(gemeMenu_select-1)*30);
		makerMenu.paint(g,(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2,(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2);
	}
	
	/**
	* ������Ϸ�����˵�
	*/
	private void drawStageMenu2()
	{
		g.drawImage(Paopao.imgBack,(getWidth()-Paopao.imgBack.getWidth())/2,(getHeight()-Paopao.imgBack.getHeight())/2,g.TOP|g.LEFT);
		g.drawImage(gameStageMenuImage,(getWidth()-gameStageMenuImage.getWidth())/2,(getHeight()-gameStageMenuImage.getHeight())/2,g.TOP|g.LEFT);
		
		for (int i=0;i<4;i++)
		{
			if (gemeMenu_select==i)
			{
				g.setColor(0xFFFFFF00);
				font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
				g.setFont(font);
				g.drawString(GAMESTAGE_MENUTEXT[i],(getWidth()-gameStageMenuImage.getWidth())/2+gameStageMenu_x,(getHeight()-gameStageMenuImage.getHeight())/2+gameStageMenu_y+i*16,Graphics.TOP|Graphics.LEFT);
			}
			else
			{
				g.setColor(0xFFFFFFFF);
				font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
				g.setFont(font);
				g.drawString(GAMESTAGE_MENUTEXT[i],(getWidth()-gameStageMenuImage.getWidth())/2+gameStageMenu_x,(getHeight()-gameStageMenuImage.getHeight())/2+gameStageMenu_y+i*16,Graphics.TOP|Graphics.LEFT);
			}
			
		}
	}
	
	/**
	* ���ƹؿ��༭�������˵�
	*/
	private void drawMakerMenu2()
	{
		g.drawImage(Paopao.imgBack,(getWidth()-Paopao.imgBack.getWidth())/2,(getHeight()-Paopao.imgBack.getHeight())/2,g.TOP|g.LEFT);
		g.drawImage(gameStageMenuImage,(getWidth()-gameStageMenuImage.getWidth())/2,(getHeight()-gameStageMenuImage.getHeight())/2,g.TOP|g.LEFT);
		
		for (int i=0;i<4;i++)
		{
			if (gemeMenu_select==i)
			{
				g.setColor(0xFFFFFF00);
				font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
				g.setFont(font);
				g.drawString(MAKER_MENUTEXT[i],(getWidth()-gameStageMenuImage.getWidth())/2+gameStageMenu_x,(getHeight()-gameStageMenuImage.getHeight())/2+gameStageMenu_y+i*16,Graphics.TOP|Graphics.LEFT);
			}
			else
			{
				g.setColor(0xFFFFFFFF);
				font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
				g.setFont(font);
				g.drawString(MAKER_MENUTEXT[i],(getWidth()-gameStageMenuImage.getWidth())/2+gameStageMenu_x,(getHeight()-gameStageMenuImage.getHeight())/2+gameStageMenu_y+i*16,Graphics.TOP|Graphics.LEFT);
			}
			
		}
	}
	
	/**
	* ������Ϣ���ʶ
	*/
	protected void setInfoFrameIndex(int infoFrameIndex)
	{
		this.infoFrameIndex=infoFrameIndex;
	}
	
	/**
	* ���ý��뱾����ı�־
	*/
	protected void setEnterFlag(int enterFlag)
	{
		this.enterFlag=enterFlag;
	}
	
	/**
	* ������Ϣ��0ʤ����1ʧ�ܡ�2����ɹ���3����ʧ�ܡ�4ɾ���ɹ���5ɾ��ʧ�ܡ���Ϸ����
	*/
	private void drawInfoFrame()
    {
		switch (infoFrameIndex)
		{
		case 0:
			infoText.setFrame(0);
			break;
		case 1:	
			infoText.setFrame(1);
			break;
		case 2:
			infoText.setFrame(2);
			break;
		case 3:
			infoText.setFrame(3);
			break;
		case 4:
			infoText.setFrame(4);
			break;
		case 5:
			infoText.setFrame(5);
			break;
		case 6:
			infoText.setFrame(6);
			break;
		}
        layerInfoFrame.paint(g,(getWidth()-backLayer.getWidth())/2,(getHeight()-backLayer.getHeight())/2); 
    }
	
	/**
	* ����ѡ������Ĺؿ�����
	*/
	private void drawSelectLayerID()
	{
		g.drawImage(blackboard,(getWidth()-blackboard.getWidth())/2,(getHeight()-blackboard.getHeight())/2,g.TOP|g.LEFT);
		g.setColor(0x0FFFFFF00);
		font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
		g.setFont(font);
		g.drawString(Integer.toString(selectLayerID),getWidth()/2-5,getHeight()/2-5,Graphics.TOP|Graphics.HCENTER);
	}
}