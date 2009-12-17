import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/**
 * ��Ϸ��Midlet��
 */
public class Paopao extends MIDlet 
{
	private Display display = null;
	ShowLogo showLogo=null;
	ShowLogo2 showLogo2=null;
	Synthesis synthesis=null;
	Menu menu=null;
	GameMenu gameMenu=null;
	Game gamePlay=null;
	GameStageMaker stageMaker=null;
	
	/**
	* Paopao��Ĺ��캯��
	*/
	public Paopao() 
	{
		showLogo=new ShowLogo(this);
		showLogo2=new ShowLogo2(this);
		synthesis=new Synthesis(this);
		menu=new Menu(this);
		gameMenu=new GameMenu(this);
		gamePlay=new Game(this);
		stageMaker=new GameStageMaker(this);
		display = Display.getDisplay(this);
		getPaopaoSet();
	}

	protected void startApp()
	{
		showLogo.showMe();
	}

	protected void pauseApp() 
	{
		
	}

	protected void destroyApp(boolean p1) 
	{
		
	}
	
	/**
    * ��ʾָ����displayable������Ҫ�������������
    */
    public void setDisplayable(Displayable displayable)
    {
        display.setCurrent(displayable);
    }
	
	/**
	* �˳���Ϸ
	*/
	public void quitGame()
	{
		try
        {
            destroyApp(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        notifyDestroyed();
	}
	
	/**
	* �����˵�����ͼ
	*/
	public Image createMenuImage(String str)
	{
		//System.out.println(str);
		Image image=null;
		try
		{
			image=Image.createImage(str);
		}
		catch (Exception ex)
		{
			System.out.println("ͼƬ������");
		}
		return image;
	}
	
	/**
	* ��ȡ��Ϸ���ã�������Ƿ���ڹؿ�
	*/
	private void getPaopaoSet()
	{
		RecordStoreManage rsm=new RecordStoreManage();
		//rsm.deleteRS(GAME_STAGE_FILE_NAME);
		//rsm.deleteRS(PAOPAO_SETNAME);
		//��ȡ��Ϸ����
		rsm.readGameSetInfo();
		//System.out.println(gameStageInitializtion);
		if (gameStageInitializtion==0)
		{
			rsm.readAndSave();
			gameStageInitializtion=1;
			rsm.saveGameSetInfo(1);
		}
		else
		{
			gameStageTotal=rsm.readStageTotal();
		}
		
		rsm=null;
	}
	
	/*******************************************************************************************************/
	/***************
	* ��Ϸ�������� *
	****************/
	
	/**
	* �����Զ���תʱ��
	*/
	public static final int SHOWLOGO_TIME=2000;
	
	/**
	* ��Ϸÿ֡����ʱ�䣬��λΪms
	*/
	public static final int spf=4;
	public static final int ranspf=2;
	
	/**
	* �������ݵķ����ٶ�
	*/
	public static final int speed=4;
	
	/**
	* ÿ����ppCount�����ݣ������������һ������
	*/
	public static final int ppCount=8;
	
	/**
	* ��Ϸ��ͼ��С
	*/
	public static final int GAME_IMAGE_WIDTH=176;
	public static final int GAME_IMAGE_HEIGHT=208;
	
	/**
	* �ֻ���Ļ��С
	*/
    public static int screenWidth=0;
    public static int screenHeight=0;
	
	/**
	* ����Ŀ�͸�
	*/
	public static final int SPRITE_WIDTH=16;
	public static final int SPRITE_HEIGHT=16;
	
	/*******************************************************************************************************/
	/***************
	*   ͼƬ·��   *
	****************/
	
	public static final String IMAGE_ARROW="/arrow.png";
	public static final String IMAGE_BACK="/back.png";
	public static final String IMAGE_BACK1="/back1.png";
	public static final String IMAGE_BACK2="/back2.png";
	public static final String IMAGE_BACK3="/back3.png";
	public static final String IMAGE_BACK4="/back4.png";
	public static final String IMAGE_LOGO="/logo.png";
	public static final String IMAGE_LOGO2="/GameCollege.png";
	public static final String IMAGE_MENU="/menu.png";
	public static final String IMAGE_PAOPAO="/paopao.png";
	public static final String IMAGE_SPECIALTIES="/specialties.png";
	public static final String IMAGE_GAMESTAGEMENU="/gameStageMenu.png";
	public static final String IMAGE_TEXTMENUBACK="/textMenuBack.png";
	public static final String IMAGE_MENUTEXT="/menutext.png";
	public static final String IMAGE_NUMBERBACK="/numberBack.png";
	public static final String IMAGE_RANK="/rank.png";
	public static final String IMAGE_HELP="/help.png";
	public static final String IMAGE_ABOUT="/about.png";
	public static final String IMAGE_INFOTEXT="/text.png";
	public static final String IMAGE_BLACKBOARD="/blackboard.png";
	public static final String IMAGE_TITLE="/gameMenu.png";
	public static final String IMAGE_LINE="/line.png";
	public static final String IMAGE_TIMEOUT="/timeOut.gif";
	
	/**
	* �û���ѡ�����Ϸ����ͼ
	*/
	public static String image_select_back=IMAGE_BACK1;
	
	/*******************************************************************************************************/
	/***************
	*   ��Ϸ�˵�   *
	****************/
	
	/**
	* �˵�ѡ��
	*/
	//public static final String[] MENU_OPTIONS={"����ģʽ","�����ս","����ģʽ","��    ��","�� �� ��","��    ��","��    ��","��    ��"};
	
	/**
	* �˵�ѡ����ʾ��x��y��λ��
	*/
	public static final int MENU_X=10;
	public static final int MENU_Y=184;
	
	/*******************************************************************************************************/
	/*********************
	* �����˵���������ʾ *
	**********************/
	
	/**
	* ��������ͼ
	*/
	public static Image imgBack=null;
	
	/**
	* �����˵���������ʾ��x��y��λ�ü���ʾ�ĳ�����߶�
	*/
	public static final int TEXT_X=10;
	public static final int TEXT_Y=16;
	public static final int TEXT_WIDTH=165;
	public static final int TEXT_HEIGHT=150;
	
	/**
	* ���а񡢰����͹�������
	*/
	public static final String ABOUT_TEXT="��Ϸ���ƣ���JAVA������ƴ��������\n��Ϸ�汾��V1.0\n��       ����59B\nѧԱ��ţ�034-0710-017-10\n��       �ߣ��»�\nEmail��a_1012@yahoo.com.cn\nQQ:1654937\nָ����ʦ�����\n��ϷѧԺ������ѵ����";
	public static final String HELP_TEXT="    �����Ϊ�˵����������Ϊ��ǰ����İ�������\n    ���������͡������Ҽ��������ּ�4��6���в��ݷ��䷽�����ת����ת����������ȷ�ϼ��������ּ�5����ȷ�Ϸ������ݡ�";
	public static final String GAME_HELP_TEXT="    �����Ϊ�˵����������Ϊ��ǰ����İ�������\n    ���������͡������Ҽ��������ּ�4��6���в��ݷ��䷽�����ת����ת�����͡����ϡ��¼��������ּ�2��8����ѡ��Ҫʹ�õĵ��ߣ��ڵ���ģʽ�°����ϡ��¼���ͽ������ѡ��ģʽ����ȷ�ϼ�����뿪����ѡ��ģʽ������������ȷ�ϼ��������ּ�5����ȷ�Ϸ������ݺ�ȷ��Ҫѡ��ʹ�õĵ��ߡ�";
	public static String RANK_TEXT="";
	
	/**
    * ���������ڽ��������ʾ
    */
    public static final String TEXT_TIP="UP(2)��DOWN(8)����ҳ";
	public static final String TEXT_TIP2="����������";
	
	/*******************************************************************************************************/
	/***************
	* ��Ϸ�洢��Ϣ *
	****************/
	
	/**
	* ��Ϸ���ô洢�ļ���
	*/
	public static final String PAOPAO_SETNAME="paopaoSet.dat";
	
	/**
	* �������а�洢�ļ���
	*/
	public static final String PAOPAOR_ANKNAME="paopaoRank.dat";
	
	/**
	* ��Ϸ�ؿ��洢�ļ���
	*/
	public static final String GAME_STAGE_FILE_NAME="gameStage.dat";
	
	/**
	* ��Ϸ�ܹؿ���
	*/
	public static int gameStageTotal=0;
	
	/**
	* ��Ϸ�ؿ���ͼ�趨��176��208���趨�ؿ�ÿ����ÿ�е�����������8����8+1��72���������һ���Ǵ�ŵ�ǰ��������־
	*/
	public static final int PAOPAO_ROW=10;
	public static final int PAOPAO_COL=10;
	
	/**
	* ��Ϸÿ�ؿ��洢��С
	*/
	public static final int STAGE_LENGTH=PAOPAO_ROW*PAOPAO_COL;
	
	/**
	* �������
	*/
	public static String playerName=" ";
	
	/**
	* ����ͼƬ
	*/
	public static int backImageIndex=0;
	
	/**
	* ��Ч���أ�0Ϊ���ã�1Ϊ�ر�
	*/
	public static int acousticEffect=0;
	
	/**
	* �������ֿ��أ�0Ϊ���ã�1Ϊ�ر�
	*/
	public static int backgrounMusic=0;
	
	/**
	* �������ӷ�ʽ��0ΪCMWAP��1ΪCMNET
	*/
	public static int netLinkType=0;
	
	/**
	* �������أ�0Ϊ���ã�1Ϊ�ر�
	*/
	public static int bluetooth=1;
	
	/**
	* �Զ����¹ؿ����أ�0Ϊ���ã�1Ϊ�ر�
	*/
	public static int gameStageInitializtion=0;
	
	/**
	* ��Ϸ�ؿ�����
	*/
	public static int stageIndex=1;
}