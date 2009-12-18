package it.hoyland.me.ddz.main;

import it.hoyland.me.ddz.menu.BinaryPageItem;
import it.hoyland.me.ddz.menu.DefaultMenuPainter;
import it.hoyland.me.ddz.menu.ItemAction;
import it.hoyland.me.ddz.menu.Menu;
import it.hoyland.me.ddz.menu.MenuListener;
import it.hoyland.me.ddz.menu.MenuPage;
import it.hoyland.me.ddz.menu.PageItem;
import it.hoyland.me.ddz.popup.Popup;
import it.hoyland.me.ddz.popup.PopupListener;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Canvas;

public class MenuCanvas extends Canvas
	implements PopupListener, MenuListener, ItemAction, CommandListener
{

  protected static final int ITEM_HELP = 0;
  protected static final int ACTION_START_NEW = 1;
  protected static final int ACTION_RESUME_GAME = 2;
  protected static final int ACTION_QUIT = 3;
  protected static final int ACTION_SCORE = 4;
 // protected static final int ACTION_CONNECT = 5;
  protected static final int ACTION_AUDIO = 100;
  protected static final int ACTION_VIBRA = 101;
  protected static final int ACTION_ABOUT = 200;
 
  protected static final Command CMD_BACK = new Command("������һ��", Command.BACK, 1);
  protected static final Command CMD_HELP = new Command("����", Command.ITEM, 1);

  protected Menu menu;
	protected Popup popup;
  protected Image backImg;
  protected Image audioonImg;
  protected Image audiooffImg;
  protected Image vibraonImg;
  protected Image vibraoffImg;
  protected MenuPage mainPage; 
  protected MenuPage settingsPage;
  protected PageItem resumeItem;
  protected PageItem scoresItem;
  protected PageItem startItem;
  protected PageItem settingsItem;
 // protected PageItem connectItem;
  protected PersistenceFlagItem audioItem;
  protected PersistenceFlagItem vibraItem;
  protected SoftButtonControl softButtons;
  protected FLDMidlet midlet;
  protected String aboutStr = "    ���ٽ��˵�������Ѫ��"+
                              "�ڵľ����˿���Ϸ��������"+
                              "��Ȼ�в���ĥ��ĳ������"+
                              "��������ֻ��Ϻ��ڶ���Ů"+
                              "���Ƶĸо�ô������ԥʲô"+
                              "�������ֻ�����ʼ��Ϸ�ɣ�"+
                               "\n\n" +
                              "ʹ���������ң�8246��������ѡ"+
                              "�ƣ�ȷ������5�����ƣ�7��"+
                              "������*���˳���9����ʾ��#��ȡ������Ϸ���Զ�"+
                              "��¼�µ�ǰ�÷֣���������ս����";
  protected String scoreStr = "��ʾ���������óɼ���";
  protected String audioStr = "���������Ŀ���ء�";  
  protected String vibraStr = "�����𶯵Ŀ���ء�"; 
  protected String settingStr = "������Ϸ���������𶯡�";   
  public MenuCanvas(FLDMidlet midlet)
  {
  	this.midlet = midlet;
    try
      {
        backImg = Image.createImage("/background.png");
        audioonImg = Image.createImage("/audio_on.png");
        audiooffImg = Image.createImage("/audio_off.png");
        vibraonImg = Image.createImage("/vibra_on.png");
        vibraoffImg = Image.createImage("/vibra_off.png"); 
      }
      catch (Exception e)
      {

        e.printStackTrace();
      }
    mainPage = new MenuPage("������񶷵���", null);//���˵�
    settingsPage = new MenuPage("��Ϸ����", null);//���ò˵�ҳ��
    menu = new Menu(mainPage, this, new RaidenMenuPainter(getWidth()));
    softButtons = new SoftButtonControl();    
    
    startItem = new PageItem("��ʼ��Ϸ", null, this, null, ACTION_START_NEW);//��ʼ��Ϸѡ��
    resumeItem = new PageItem("������Ϸ", null, this, null, ACTION_RESUME_GAME);//�߷ּ�¼ѡ��
    scoresItem = new PageItem("�߷ּ�¼", null, this, null, ACTION_SCORE);//�߷ּ�¼ѡ��
   // connectItem = new PageItem("������ս", null, this, null, ACTION_CONNECT);//������սѡ��
    scoresItem.setProperty(ITEM_HELP, scoreStr.toCharArray());
    settingsItem = new PageItem("��Ϸ����", null, null, settingsPage);//��Ϸ����ѡ��
    settingsItem.setProperty(ITEM_HELP, settingStr.toCharArray());
    mainPage.addItem(startItem);//��ӿ�ʼ��Ϸѡ��
    mainPage.addItem(scoresItem);//��Ӹ߷ּ�¼ѡ��
    mainPage.addItem(settingsItem);//�����Ϸ����ѡ��
//    mainPage.addItem(connectItem);
    mainPage.addItem(new PageItem("����", null, this, null, ACTION_ABOUT));
    mainPage.addItem(new PageItem("�˳���Ϸ", null, this, null, ACTION_QUIT));
  

   audioItem = new PersistenceFlagItem(//����
        midlet.AUDIO,  "����",
        audioonImg,
        audiooffImg, ACTION_AUDIO);
    audioItem.setProperty(ITEM_HELP, audioStr.toCharArray());
    vibraItem = new PersistenceFlagItem(//��
        midlet.VIBRA,  "��",
        vibraonImg,
        vibraoffImg, ACTION_VIBRA);
    vibraItem.setProperty(ITEM_HELP, vibraStr.toCharArray());
    settingsPage.addItem(audioItem);
    settingsPage.addItem(vibraItem);


    setFullScreenMode(true);
    
    softButtons.init(this,
        Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE),
        CMD_BACK, CMD_HELP);
    softButtons.setCommandListener(this);
    softButtons.enable(CMD_HELP, false);  
    
    popup = new Popup();
    int menuPadding = 16;
    menu.setLocation(0, menuPadding);
    menu.setDimensions(getWidth(), getHeight() - menuPadding * 2);
    menu.setFrameData(10, 20);
    menu.setListener(this);
    menu.start();
  }
  public void insertResumeGame()
  {
  	mainPage.insertItemAt(resumeItem,1);
      
  }
  public void removeResumeGame()
  {
     mainPage.removeItem(resumeItem);
  }
  protected void paint(Graphics g)
  {
    g.setColor(0x338844);//������ɫ
    g.fillRect(0, 0, getWidth(), getHeight());//���������Ļ
    g.drawImage(backImg, getWidth() / 2, getHeight() / 2, Graphics.VCENTER | Graphics.HCENTER);//���Ʊ���
    menu.paint(g);//���Ʋ˵�
    softButtons.paint(g);//�������
    if (popup.isActive())
    {
      popup.paint(g);
    }
  }


  protected void keyPressed(int keyCode)
  {
    if (popup.isActive())
    {
      popup.keyPressed(keyCode, getGameAction(keyCode));
      repaint();
    }
    else
    {
      menu.keyPressed(keyCode);
      softButtons.keyPressed(keyCode);
    }

  }
  
  public void commandAction(Command c, Displayable d)
  {
  	
    if (c == CMD_BACK)
    {
      menu.goBack();//������һ��
    }
    else if (c == CMD_HELP)
    {
      PageItem item = menu.getSelectedItem();
      if (item != null)
      {
        char[] helpTxt = (char[])item.getProperty(ITEM_HELP);
        if (helpTxt != null)
        {
          showPopup(helpTxt, Popup.ALT_OK, 0, 0, 0);
        }
      }
    }
  }
  public void showPopup(char[] text, char[][] altTexts,
      int timeOutInSeconds, int defaultChoice, int timeOutChoice)
   //   PopupListener listener)
  {

    if (popup.isActive())
    {

      selectedChoice(popup.getTimeOutChoice(), true);
      popup.dispose();
      
    }

    popup.init(text, altTexts, (byte) timeOutInSeconds, (byte) defaultChoice,
        (byte) timeOutChoice, this, getWidth(),
        getHeight());
    repaint();

  }  
  
   public void selectedChoice(byte choice, boolean timeOut)
  {
      repaint();      
  }
   public void itemAction(MenuPage page, PageItem item)
  {
    int id = item.getId();
    switch(id)
    {
    case ACTION_START_NEW:
       midlet.StartGame();//��ʼ��Ϸ
      break;
    case ACTION_RESUME_GAME:
      removeResumeGame();
      midlet.resumeGame();//������Ϸ
      break;
    case ACTION_QUIT:
    	
        midlet.quit();//�˳�
      break;
    case ACTION_SCORE:

      String str =
        "��Ϸʱ��:"+midlet.lastGameDate + "\n\n" +
        "�÷�:"+midlet.highScore+ "\n\n";
      char[] text = str.toCharArray();
      showPopup(text, Popup.ALT_OK, 0, 0, 0);
      break;
  //  case ACTION_CONNECT:
    	//midlet.showConnection();
    //    break;
    case ACTION_AUDIO:
    	
     
      break;
    case ACTION_VIBRA:
  
      midlet.vibrate(100,100,3);

      break;
    case ACTION_ABOUT:
      showPopup(aboutStr.toCharArray(), Popup.ALT_OK, 0, 0, 0);
      break;
    }
  } 

  
  public void newPage(MenuPage fromPage, MenuPage toPage, boolean back)
  {
    softButtons.enable(CMD_BACK, toPage != menu.getStartPage());
  }

  public void itemSelected(MenuPage page, PageItem oldItem, PageItem newItem)
  {
    if (newItem != null)
    {
      Object helpTxt = newItem.getProperty(ITEM_HELP);
      softButtons.enable(CMD_HELP, helpTxt != null);
    }
  }

  class PersistenceFlagItem extends BinaryPageItem
  {
    protected int m_key;

    public PersistenceFlagItem(int key, String label,
        Image imageTrue, Image imageFalse, int id)
    {
      super(label, imageTrue, imageFalse, null, MenuCanvas.this, id);
      m_key = key;
    }

   public boolean getBoolean()
    {
    	return midlet.getBoolean(m_key);
   
    }

    public void setBoolean(boolean value)
    {
       midlet.setBoolean(m_key, value);
    }
  } 
  static class RaidenMenuPainter extends DefaultMenuPainter
  {
    protected int[] m_rgbData;
    protected int m_canvasWidth;
    
    public RaidenMenuPainter(int canvasWidth)
    {
      m_canvasWidth = canvasWidth;
      m_rgbData = new int[m_canvasWidth * 4];
      int bgcol = 0x008800;
      for (int i = 0; i < m_canvasWidth; i++)
      {
        double alpha = (double)Math.abs(i - m_canvasWidth / 2) /
        			   (double)m_canvasWidth;
        int col = bgcol | (128 - (int)(255 * alpha) << 24);
        m_rgbData[i]                     = col;
        m_rgbData[i + m_canvasWidth    ] = col;
        m_rgbData[i + m_canvasWidth * 2] = col;
        m_rgbData[i + m_canvasWidth * 3] = col;
        
      }
}
    protected void paintItem(Graphics g, PageItem item, boolean selected, int x,
        int y, int w, int iMaxW, boolean to, boolean from)
    {
      if (selected)
      {
        int itemH = getItemHeight(item);
	  	for (int by = y;
	  			by < y + itemH;
	  			by += 4)
	  	{
		  	g.drawRGB(m_rgbData, 0, m_canvasWidth, x - m_canvasWidth/2, by, m_canvasWidth,
		  	    Math.min(4, y + itemH - (by - y)), true);
	  	}

      }
    
        super.paintItem(g, item, selected, x, y, w, iMaxW, to, from);
    }

    
  } // end of RaidenMenuPainter
}