import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;
import java.util.Random;

/**
* ��Ϸ����
*/
public class GameMain extends GameCanvas implements Runnable
{
	protected Paopao midlet=null;
	protected MyLine myLine=null;
	protected Graphics g=null;
	protected Player player = null;
	protected Font font=null;
	protected Random ran=new Random();
	protected projectTimeOut pTimeOut=null;
	
	
	//װ�ر�־���߳����С���ͣ��װ�عؿ����������ݡ����ݷ���״̬���Ƿ��ڷ����У�
	//��ʱ��ʱ�����б�־���Ƿ���NEW�����ݷ����ࡢ���ݸ��±�־��Ϊ��ʱ���������ݸ��µ�����̨�ϣ�
	//�Ƿ����������ݡ��߳��Ƿ��ڷ�æ״̬
	protected boolean isActive=true;
	protected boolean isPause=false;
	protected boolean isLoading=false;
	protected boolean isSelectOK=false;
	protected boolean isFly=false;
	protected boolean isReckonStart=true;
	protected boolean isNew=false;
	//protected boolean isUpdate=false;
	private boolean isRanAddPP=false;
	protected boolean isBusy=false;
	Thread thread=null;		//�����߳�
	
	//�ؿ�װ�ؽ���
	protected int loadPercent = 0;
	
	//��Ϸ�÷�
	protected int score=0;
	
	//������������
	protected int paopaoCount=0;
	
	//��������������
	protected int ranAddPPNum=0;
	
	/**
	* ͼ���뾫�����ر���
	*/
	protected LayerManager layermanager=null;	//ͼ�����
	protected PaopaoSprite paopaoSprite=null;	//���ݾ���
	protected PaopaoSprite newPaopaoSprite=null;	//�������ݾ���
	protected PaopaoSprite arrow=null;		//����̨����
	protected PaopaoSprite timeOutNum=null;	//��ʱ����ͼƬ
	protected TiledLayer backLayer=null;		//����ͼ��
	protected Image imgBack=null;				//����ͼƬ
	
	
	//���ݾ�������ͼƬ�ϵ�λ��
	protected int paopao_img_x=15;
	protected int paopao_img_y=17;
	
	//���ݾ���������Ļ��Ĭ�ϵ���x��y���꣬����̨�뷢������λ������
	protected int paopao_new_x,paopao_new_y,paopao_default_x,paopao_default_y;
	protected int revise=8;
	
	//���ݾ��鷢��������ʼ����
	protected int startX,startY;
	
	//��ǰ���ݾ������ɫ��֡��������������ɫ��Ĭ�Ϸ�̨�Ƕȣ�90�Ƚǣ�
	//��ǰ����̨����ĽǶȶ�ӦͼƬ
	protected int paopaoSelectColor;
	protected int paopaoRandomColor;
	protected int arrowDefaultFrame=35;
	protected int arrowSelectFrame=arrowDefaultFrame;
	//protected int arrowArc=35;
	
	//�������ԣ�ͼ��������
	protected int layerID=-1;
	
	/**
	* �������飬���һ���Ǳ�־�У���־��ǰ���Ƿ����������������ȣ�0���ǲ�������1��������
	* ����������Էֱ�Ϊ��0��Ϊ���ݲ㣬1��Ϊ���߲㣬2��Ϊ�ҵ��
	*/
	protected byte gameStage[][][]=new byte[Paopao.PAOPAO_COL][Paopao.PAOPAO_ROW+4][3];;
	
	//��ǰ��Ϸ�ؿ�ID��������������ѡ����ID����ǰ������ɫ������
	protected int stageIndex=0;
	protected int toolsTotal=0;
	protected int toolsIndex=0;
	protected int colorTotal=8;
	
	//��Ϸ״̬��־����
	protected boolean flag=true;
	
	/**
	* ����̨�����������
	*/
	//����ת����Ƕȶ�Ӧ��sinֵ(�Ŵ�100000��)
	protected static final int[] ARROW_ARC_SIN=
	{
		4362,8716,13053,17365,21644,25882,30071,34202,38268,42262,
		46175,50000,53730,57358,60876,64279,67559,70711,73728,76604,
		79335,81915,84339,86603,88701,90631,92388,93969,95372,96593,
		97630,98481,99144,99619,99905,100000,
		99905,99619,99144,98481,97630,96593,95372,93969,92388,90631,
		88701,86603,84339,81915,79335,76604,73728,70711,67559,64279,
		60876,57358,53730,50000,46175,42262,38268,34202,30071,25882,
		21644,17365,13053,8716,4362
		
	};
	//����ת����Ƕȶ�Ӧ��cosֵ(�Ŵ�100000��)
	protected static final int[] ARROW_ARC_COS=
	{
		-99905,-99619,-99144,-98481,-97630,-96593,-95372,-93969,-92388,-90631,
		-88701,-86603,-84339,-81915,-79335,-76604,-73728,-70711,-67559,-64279,
		-60876,-57358,-53730,-50000,-46175,-42262,-38268,-34202,-30071,-25882,
		-21644,-17365,-13053,-8716,-4362,0,
		4362,8716,13053,17365,21644,25882,30071,34202,38268,42262,
		46175,50000,53730,57358,60876,64279,67559,70711,73728,76604,
		79335,81915,84339,86603,88701,90631,92388,93969,95372,96593,
		97630,98481,99144,99619,99905
	};
	
	
	/**
	* ���캯��
	*/
	public GameMain(Paopao midlet)
	{
		super(false);
		
		//ʹ��ȫ��ģʽ
		this.setFullScreenMode(true);
		this.midlet=midlet;
		this.stageIndex=Paopao.stageIndex;
		
		myLine=new MyLine();
		
		//���ͼ��
		g = getGraphics();
		
		//ͼ���뾫����ر�����ʼ��
		initLayerAndSprite();
		
		//�𶯳�ʱ��ʱ��
		pTimeOut=new projectTimeOut();
	}
	
	/**
	* ����PaopaoSprite�����
	*/
	protected PaopaoSprite createSprite(String spriteName,int paopaoWidth,int paopaoHeight,int width,int height) 
	{
		return new PaopaoSprite(midlet.createMenuImage(spriteName),paopaoWidth,paopaoHeight,width,height);
	}
	
	/**
	* ͼ���뾫����ر�����ʼ��
	*/
	private void initLayerAndSprite()
	{
		//��������ͼƬ���������ݡ�����̨�����Լ�����̨
		imgBack=midlet.createMenuImage(Paopao.image_select_back);
		backLayer=new TiledLayer(1, 1, imgBack, Paopao.GAME_IMAGE_WIDTH, Paopao.GAME_IMAGE_HEIGHT);
		backLayer.setCell(0,0,1);
		newPaopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
		paopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
		arrow=createSprite(midlet.IMAGE_ARROW,46,46,27,27);
		timeOutNum=createSprite(midlet.IMAGE_TIMEOUT,24,28,0,0);
		
		
		//ͼ�����������ݡ�����̨�ͱ���
		layermanager=new LayerManager();
		layermanager.append(newPaopaoSprite);
		layermanager.append(paopaoSprite);
		layermanager.append(timeOutNum);
		layermanager.append(arrow);
		layermanager.append(backLayer);
		
		//���ñ������ݡ�����̨���ݵ�Ĭ��λ�������ݷ��еĳ�ʼλ��
		paopao_new_x=40;
		paopao_new_y=184;
		paopao_default_x=(Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH)/2;
		paopao_default_y=Paopao.GAME_IMAGE_HEIGHT-29;
		startX=paopao_default_x;
		startY=paopao_default_y;
		
		//��ʼ��������ɫ
		//paopaoRandomColor=Math.abs(ran.nextInt(7))+1;
		//paopaoSelectColor=Math.abs(ran.nextInt(7))+1;
		
		//���ñ������ݡ�����̨���ݡ�����̨��֡�����ǵĳ�ʼλ��
		//newPaopaoSprite.setFrame(paopaoRandomColor);
		newPaopaoSprite.setPosition(paopao_new_x,paopao_new_y);
		//paopaoSprite.setFrame(paopaoSelectColor);
		paopaoSprite.setPosition(paopao_default_x,paopao_default_y);
		arrow.setFrame(arrowSelectFrame);
		arrow.setPosition((Paopao.GAME_IMAGE_WIDTH-46)/2,Paopao.GAME_IMAGE_HEIGHT-40);
		timeOutNum.setPosition(110,170);
		timeOutNum.setVisible(false);
	}
	
	/**
	* �������������ɫ
	*/
	protected int randomPaopao()
	{
		boolean isSame=false;
		int j=0;
		int n=layermanager.getSize()-3;		//��ǰ��Ĭ��������������е���������
		int[] paopaoColor=myLine.getPaopaoColor();	//��ȡ���������ݵ���ɫ����
		
		//������������ɫ׷�ӽ���ɫ����������ȥ
		for (int i=0;i<8;i++)
		{
			if (paopaoColor[i]==0) continue;
			if (paopaoColor[i]==paopaoSelectColor) 	//�ж�������ɫ�����������Ƿ��Ѵ��ڱ������ݵ���ɫ
			{
				isSame=true;
				continue;
			}
			j++;
		}
		colorTotal=j+1;
		if (!isSame) paopaoColor[j]=paopaoSelectColor;	//����������ݵ���ɫ��δ���������У������ӽ�ȥ
		if (j==0) return paopaoColor[0];
		return paopaoColor[Math.abs(ran.nextInt(j))+1];		//��������޶���Χ�ڵ�������ɫ
	}
	
	/**
    * ��ʼ��ָ���ؿ�����Ϸ��stageIndex �ؿ��ţ�
    */
    public void startPlay(int stageIndex)
    {
		//LOAD�ؿ�����ʼ����ر���
        new Loading(stageIndex);
		
		//��ʼ����ʱ������ΪĬ��ֵ
		isReckonStart=true;
        pTimeOut.setTime();
		timeOutNum.setVisible(false);
		
		isPause=false;
		
        if (thread==null)
        {
            thread=new Thread(this);
            thread.start();
        }
		System.out.println("�����߳�");
		midlet.setDisplayable(this);
		System.gc();
    }
	
	/**
	* ���³�ʼ��gameStage����
	*/
	protected void initializtionGameStageDate()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW+3;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL;j++)
			{
				if (i%2!=0 && j==Paopao.PAOPAO_COL-1)
				{
					gameStage[j][i][0]=1;
					//System.out.print(gameStage[j][i][0]+",");
				}
				else
				{
					gameStage[j][i][0]=0;
					//System.out.print(gameStage[j][i][0]+",");
				}
			}
			//System.out.println();
		}
	}
	
	/**
	* �����߳�
	*/
	public void run()
	{
		
	}

	/**
	* ���±������ݵ�����̨��
	*/
	protected void updatePaopao()
	{
		startX=paopao_default_x;
		startY=paopao_default_y;
		paopaoSprite=newPaopaoSprite;					//����߱��õ��������óɷ���̨����
		paopaoSelectColor=paopaoRandomColor;			//��ȡ�ñ������ݵ���ɫ
		paopaoSprite.setPosition(paopao_default_x,paopao_default_y);	//������̨�����ƶ�������̨��
		paopaoRandomColor=randomPaopao();				//���ɱ������ݵ���ɫ
		newPaopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);	//������������
		layermanager.insert(newPaopaoSprite,0);			//��ͼ���в��뱸������
		//newPaopaoSprite=(PaopaoSprite)layermanager.getLayerAt(0);	//���²���������뱸�����ݾ����������
		newPaopaoSprite.setFrame(paopaoRandomColor);	//���ñ������ݵ���ɫΪ�ղ����ɵ���ɫ
		newPaopaoSprite.setPosition(paopao_new_x,paopao_new_y);	//�������ɵ������ƶ�����������ԭλ����
	}
	
	/**
	* �������������Ϣ
	*/
	protected void input()
	{
		if (ranAddPPNum==0)
		{
			int keyStates = getKeyStates();
			
			if ((keyStates & DOWN_PRESSED) != 0) 
			{
				toolsIndex++;
			}
			else if ((keyStates & UP_PRESSED) != 0) 
			{
				toolsIndex--;
			}
			
			if ((keyStates & RIGHT_PRESSED) != 0) 
			{
				arrowSelectFrame++;
			}
			else if ((keyStates & LEFT_PRESSED) != 0) 
			{
				arrowSelectFrame--;
			}
			
			if ((keyStates & FIRE_PRESSED) != 0)
			{
				if (!isFly)	isSelectOK=true;
			}
		}
	}
	
	/**
	* �û������¼�
	*/
	protected void keyPressed(int keyCode)
	{
		if (ranAddPPNum==0)
		{
			switch (keyCode)
			{
			case Canvas.KEY_NUM2:
				toolsIndex--;
				break;
			case Canvas.KEY_NUM4:
				arrowSelectFrame--;
				break;
			case Canvas.KEY_NUM5:
				if (!isFly)	isSelectOK=true;
				break;
			case Canvas.KEY_NUM6:
				arrowSelectFrame++;
				break;
			case Canvas.KEY_NUM8:
				toolsIndex++;
				break;
			case Canvas.KEY_STAR:
				printArray();
				break;
			}
			if (getKeyName(keyCode).equals("SOFT1"))
			{
				pause();
			}
		}
	}
	
	/**
	* �û������¼�
	*/
	protected void keyRepeated(int keyCode)
	{
		if (ranAddPPNum==0)
		{
			switch (keyCode)
			{
			case Canvas.KEY_NUM2:
				toolsIndex--;
				break;
			case Canvas.KEY_NUM4:
				arrowSelectFrame--;
				break;
			case Canvas.KEY_NUM5:
				if (!isFly)	isSelectOK=true;
				break;
			case Canvas.KEY_NUM6:
				arrowSelectFrame++;
				break;
			case Canvas.KEY_NUM8:
				toolsIndex++;
				break;
			}
			
			if (getKeyName(keyCode).equals("SOFT1") || getKeyName(keyCode).equals("SOFT2"))
			{
				pause();
			}
			
		}
	}
	
	/**
	* ������N������
	*/
	protected void randomAddPaopao(int n)
	{
		int[] paopaoPlace=new int[n];		//��Ҫ��ӵ����������е�ID
		String filtration=",";				//���˵�������ID
		int x,y,paopaoColor;
		PaopaoSprite tempPP=null;
		y=Paopao.GAME_IMAGE_HEIGHT-22;
		isRanAddPP=true;
		ranAddPPNum=n;
		isReckonStart=false;
		
		for (int i=0;i<n;i++)
		{
			paopaoPlace[i]=getRandomPlace(Paopao.PAOPAO_ROW-1,filtration);
			filtration=filtration+paopaoPlace[i]+",";
		}
		
		for (int i=0;i<n;i++)
		{
			//����������ݵĳ�ʼλ��
			x=paopaoPlace[i]*Paopao.SPRITE_WIDTH+paopao_img_x;
			tempPP=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
			paopaoColor=randomPaopao();
			layermanager.insert(tempPP,i+2);
			tempPP.setFrame(paopaoColor);
			
			//System.out.println(paopaoColor+" "+(i+2));
			//i+2  �����������ڵĲ�ID��������Ǳ������ݣ�ÿһ���Ƿ���̨���ݣ������������ݶ��Ǵӵڶ��㿪ʼ�����
			new paopaoFly(tempPP,paopaoColor,x,y,arrowDefaultFrame,i+2,n);	
			
		}
	}
	
	/**
	* ��ȡ���λ��
	*/
	private int getRandomPlace(int n,String filtration)
	{
		int randomPlace=ran.nextInt(n);
		if (filtration.indexOf(","+randomPlace+",")==-1)
		{
			return randomPlace;
		}
		return getRandomPlace(n,filtration);
	}
	
	/**
	* ���˷��������Է���������
	*/
	protected void isReckon()
	{
		pTimeOut.setTime();
		timeOutNum.setVisible(false);
		//arrowArc=arrowSelectFrame;
		isSelectOK=false;
		isNew=true;
		isFly=true;
		paopaoCount++;		//�ܷ�����������
	}
	
	/**
	* ���ƹؿ�
	*/
	protected void drawStage()
	{
		for (int i=0;i<Paopao.PAOPAO_ROW;i++)
		{
			for (int j=0;j<Paopao.PAOPAO_COL-1;j++)
			{
				if (gameStage[j][i][0]!=0)
				{
					paopaoSprite=createSprite(midlet.IMAGE_PAOPAO,Paopao.SPRITE_WIDTH,Paopao.SPRITE_HEIGHT,8,8);
					myLine.insertNode(2,i,j,gameStage[j][i][0],1,true);
					layermanager.insert(paopaoSprite,2);
					paopaoSprite.setFrame(gameStage[j][i][0]);
					paopaoSprite.setPosition(getRectX(j,i),getRectY(i));
					
					paopaoSprite=(PaopaoSprite)layermanager.getLayerAt(1);
				}
				//System.out.print(gameStage[j][i][0]+",");
			}
			//System.out.print(gameStage[9][i][0]+",");
			//System.out.println();
		}
	}
	
	/**
	* ��þ��鵱ǰ�ο������ڸ��X�������
	*/
	private int getRectX(int col,int row)
	{
		int x=0;
		if (gameStage[Paopao.PAOPAO_COL-1][row][0]==1)
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
    * ת����һ��
    */
    protected void nextStage()
    {
		if (++stageIndex>Paopao.gameStageTotal)
		{
			midlet.gameMenu.setMenuIndex(8);
			midlet.gameMenu.setInfoFrameIndex(6);
			midlet.gameMenu.showMe();
		}
        else
		{
			startPlay(stageIndex);
		}
    }
	
	/**
	* װ����Ч
	*/
	private void loadAcousticEffect()
	{
		try
        {
            if (player != null)
            {
                player.close();
                player = null;
            }
            player = Manager.createPlayer(getClass().getResourceAsStream("/onestop.mid"), "audio/midi");
            player.setLoopCount(-1);// ����ѭ��
            player.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
	}
    
	/**
    * ��ͣ
    */ 
    protected void pause()
    {
        isPause = !isPause;
		isReckonStart=!isReckonStart;	//��ͣ���ݷ��䳬ʱ��ʱ��
        try
        {
            if (isPause)
			{
				//player.stop();
				midlet.gameMenu.setMenuIndex(4);
				midlet.gameMenu.showMe();
			}
            else
			{
                //player.start();
			}
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
	
	/**
    * ���Ƶ�ǰ��Ļ
    */
    protected void drawScreen(Graphics g)
    {
	
	}
	
	/**
	* ���Ƶ÷�
	*/
	protected void drawScore()
	{
		g.setColor(0xFFFFFF00);
		g.drawString("�÷�:"+score,(Paopao.screenWidth-Paopao.GAME_IMAGE_WIDTH)/2+20,(Paopao.screenHeight-Paopao.GAME_IMAGE_HEIGHT)/2+10,Graphics.TOP|Graphics.LEFT);
	}
	
	/**
	* ͬɫ���ݶ���3��ʱ����
	*/
	protected boolean paopaoBao(int nowCol,int nowRow)
	{
		int[] expressionsOROW={1,0,-1,-1,0,1};	//y������£������ϣ����ϣ��ң�����λ�ã������λ�ڲ�ƫ����
		int[] expressionsOCOL={0,-1,0,1,1,1};	//x������£������ϣ����ϣ��ң�����λ�ã������λ�ڲ�ƫ����
		int[] expressionsJROW={1,0,-1,-1,0,1};	//y������£������ϣ����ϣ��ң�����λ�ã������λ��ƫ����
		int[] expressionsJCOL={-1,-1,-1,0,1,0};	//x������£������ϣ����ϣ��ң�����λ�ã������λ��ƫ����
		String str;	//�������ݸ�ʽ����|x,y|x1,y1|x2,y2|������������Ҫ���������ݵ����꣬���ڱ�����𤸽�������Ƿ��Ѿ��ȽϹ�
		int[] baoX=new int[90];	//����Ҫ���������ݵ�X������
		int[] baoY=new int[90];	//����Ҫ���������ݵ�Y������
		int n=0;				//���浱ǰ���ڼ������ݵ�ָ��
		int amount=0;			//���浱ǰͬɫ���ݵ������ϼ�
		boolean isBao=false;
		//System.out.println(nowCol+"  "+nowRow);
		//���ݷ����ȥճ���������ݺ����Ƚ��Լ���x��y�����꣨����λ�õ�ͼ����������꣩��ʼ��������
		str="|"+nowCol+","+nowRow+"|";
		baoX[n]=nowCol;
		baoY[n]=nowRow;
		
		while (true)
		{
			//ʹ��forѭ����������ǰ�����������е�����
			for (int i=0;i<6;i++)
			{
				//�жϵ�ǰ���Ƿ�Ϊ��ƫ����
				if (gameStage[Paopao.PAOPAO_COL-1][baoY[n]][0]==0)
				{
					//����±��Ƿ����
					int newCol=baoX[n]+expressionsOCOL[i];
					int newRow=baoY[n]+expressionsOROW[i];
					if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
					{
						continue;
					}
					
					//��ʼ��������Ƿ�������
					if (checkPaopao(newCol,newRow))
					{
						//System.out.println("��:"+baoX[n]+"  "+newCol+" ��:"+baoY[n]+"  "+newRow+" ��ɫ:"+gameStage[newCol][newRow][0]);
						//��������ж��Ƿ�Ϊͬɫ���Ա�һ���Ա����ݵ�ID�Ƿ��뵱ǰλ�����ݵ�IDֵ��ȣ�
						if (gameStage[baoX[n]][baoY[n]][0]==gameStage[newCol][newRow][0])
						{
							//���ͬɫ��
							if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)		//�жϸ�λ���Ƿ��Ѿ�������
							{
								amount++;
								str=str+newCol+","+newRow+"|";
								baoX[amount]=newCol;
								baoY[amount]=newRow;
							}
							else	//�Ѿ������ˣ�������һ��λ��
							{
								continue;
							}
						}
					}
				}
				else
				{
					//����±��Ƿ����
					int newCol=baoX[n]+expressionsJCOL[i];
					int newRow=baoY[n]+expressionsJROW[i];
					//System.out.println(newCol+"  "+newRow);
					if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
					{
						continue;
					}
					
					//��ʼ��������Ƿ�������
					if (checkPaopao(newCol,newRow))
					{
						//System.out.println("��:"+baoX[n]+"  "+newCol+" ��:"+baoY[n]+"  "+newRow+" ��ɫ:"+gameStage[newCol][newRow][0]);
						//��������ж��Ƿ�Ϊͬɫ���Ա�һ��������ݵ�ID�Ƿ��뵱ǰλ�����ݵ�IDֵ��ȣ�
						if (gameStage[baoX[n]][baoY[n]][0]==gameStage[newCol][newRow][0])
						{
							//���ͬɫ��
							if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)		//�жϸ�λ���Ƿ��Ѿ�������
							{
								amount++;
								str=str+newCol+","+newRow+"|";
								baoX[amount]=newCol;
								baoY[amount]=newRow;
							}
							else	//�Ѿ������ˣ�������һ��λ��
							{
								continue;
							}
						}
					}
				}
			}
			//�����һȦ��ָ��ָ����һ��λ��
			n++;
			//�����ж��Ƿ��Ѿ���������
			if (n>amount)//���ָ��n����amount���ޣ����ʾ�Ѿ��������ˣ��˳�ѭ�����������
			{
				//System.out.println(str+" n:"+n+"  amount:"+amount);
				n=0;
				break;
			}
		}
		
		//ȫ�������ɣ��������3��ͬɫ����������ɾ��
		if (amount>=2)
		{
			//����÷֣�amount��ֵΪ�±꣬0Ϊ1�����ݣ�1Ϊ�������ݣ��Դ����ƣ�
			//ͬʱ���������ݼ�2�֣��ĸ���3�֣������5�֣��������ϼ�8��
			if (colorTotal>2)	//�����ף���������ɫ����С�ڶ���ʱ�����Ʒ�
			{
				switch (amount)
				{
				case 2:
					score+=2;
					break;
				case 3:
					score+=3;
					break;
				case 4:
					score+=5;
					break;
				default:
					score+=8;
					break;
				}
			}
			//System.out.println(colorTotal+"  "+amount);
			for (int i=0;i<=amount;i++)
			{
				//�������в��ҵ���Ҫ���������ݵ�ͼ��ID��Ȼ��ɾ�������ж�Ӧ�Ľڵ㡢���ͼ���Ӧ����ı���Լ�ɾ����ͼ��
				layerID=myLine.findNode(baoY[i],baoX[i]);
				myLine.delNode(layerID);
				gameStage[baoX[i]][baoY[i]][0]=0;
				layermanager.remove((PaopaoSprite)layermanager.getLayerAt(layerID));
			}
			isBao=true;
		}
		
		/* System.out.println("���ݱ��ƺ������");
		//��ӡ���ݱ��ƺ�����飨���ڼ��BUG��
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		} */
		
		return isBao;
	}
	
	/**
	* ����Ƿ������ݵĺ���
	*/
	protected boolean checkPaopao(int baoX, int baoY)
	{
		//if (baoX<0 || baoX>Paopao.PAOPAO_COL-2) return false;
		//if (baoY<0 || baoY>Paopao.PAOPAO_ROW-1) return false;
		if (gameStage[baoX][baoY][0]==0) return false;
		return true;
	}
	
	/**
	* û�йҵ����ɫ������׹
	*/
	protected void paopaoDrop()
	{
		int[] expressionsOROW={1,0,-1,-1,0,1};	//y������£������ϣ����ϣ��ң�����λ�ã������λ�ڲ�ƫ����
		int[] expressionsOCOL={0,-1,0,1,1,1};	//x������£������ϣ����ϣ��ң�����λ�ã������λ�ڲ�ƫ����
		int[] expressionsJROW={1,0,-1,-1,0,1};	//y������£������ϣ����ϣ��ң�����λ�ã������λ��ƫ����
		int[] expressionsJCOL={-1,-1,-1,0,1,0};	//x������£������ϣ����ϣ��ң�����λ�ã������λ��ƫ����
		String str;	//�������ݸ�ʽ����|x,y|x1,y1|x2,y2|������������Ҫ���������ݵ����꣬���ڱ�����𤸽�������Ƿ��Ѿ��ȽϹ�
		int[] baoX=new int[90];	//����Ҫ��׹�����ݵ�X������
		int[] baoY=new int[90];	//����Ҫ��׹�����ݵ�Y������
		int n=0;				//���浱ǰ���ڼ������ݵ�ָ��
		int amount=0;			//���浱ǰ��׹���ݵ������ϼ�
		boolean isFlag=false;
		
		//��������λ��
		for (int j=1;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL-1;k++)
			{
				n=0;
				amount=0;
				str="|"+k+","+j+"|";
				baoX[n]=k;
				baoY[n]=j;
				
				while (true)
				{
					//��ǰλ�������ݣ�����Ϊ-1
					if (gameStage[baoX[n]][baoY[n]][0]==0)
					{
						gameStage[baoX[n]][baoY[n]][2]=-1;
						break;
					}
					//��ǰλ���йҵ��������׹���ݱ��������
					else if (gameStage[baoX[n]][baoY[n]][2]==1 || gameStage[baoX[n]][baoY[n]][2]==2)
					{
						break;
					}
					else
					{
						//ʹ��forѭ����������ǰ�����������е�����
						for (int i=0;i<6;i++)
						{
							//�жϵ�ǰ���Ƿ�Ϊ��ƫ����
							if (gameStage[Paopao.PAOPAO_COL-1][baoY[n]][0]==0)
							{
								//����±��Ƿ����
								int newCol=baoX[n]+expressionsOCOL[i];
								int newRow=baoY[n]+expressionsOROW[i];
								if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
								{
									continue;
								}
								
								//��ʼ��������Ƿ�������
								if (checkPaopao(newCol,newRow))
								{
									//���������ж��Ƿ��йҵ�
									if (gameStage[newCol][newRow][2]==1)
									{
										//����йҵ��������������ȫ�������Ϊ�йҵ�
										for (int l=0;l<=amount;l++)
										{
											gameStage[baoX[l]][baoY[l]][2]=1;
										}
										//�����ѱ�������һ�����ݵı��
										isFlag=true;
										
										//�˳�������forѭ��
										break;
									}
									//�޹ҵ�
									else
									{
										//�жϸ�λ���Ƿ��Ѿ�������û�м����򽫸�λ�ü���������
										if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)
										{
											amount++;
											str=str+newCol+","+newRow+"|";
											baoX[amount]=newCol;
											baoY[amount]=newRow;
										}
										//�Ѿ������ˣ�������һ��λ��
										else	
										{
											continue;
										}
									}
								}
							}
							else
							{
								//����±��Ƿ����
								int newCol=baoX[n]+expressionsJCOL[i];
								int newRow=baoY[n]+expressionsJROW[i];
								if (newCol<0 || newRow<0 || newCol>Paopao.PAOPAO_COL-2)
								{
									continue;
								}
								
								//��ʼ��������Ƿ�������
								if (checkPaopao(newCol,newRow))
								{
									//���������ж��Ƿ��йҵ�
									if (gameStage[newCol][newRow][2]==1)
									{
										//����йҵ��������������ȫ�������Ϊ�йҵ�
										for (int l=0;l<=amount;l++)
										{
											gameStage[baoX[l]][baoY[l]][2]=1;
										}
										//�����ѱ�������һ�����ݵı��
										isFlag=true;
										
										//�˳�������forѭ��
										break;
									}
									//�޹ҵ�
									else
									{
										//�жϸ�λ���Ƿ��Ѿ�������û�м����򽫸�λ�ü���������
										if (str.indexOf(("|"+newCol+","+newRow+"|"))==-1)
										{
											amount++;
											str=str+newCol+","+newRow+"|";
											baoX[amount]=newCol;
											baoY[amount]=newRow;
										}
										//�Ѿ������ˣ�������һ��λ��
										else	
										{
											continue;
										}
									}
								}
							}
						}
						//������ѱ�ǹ������˳�ѭ��witchѭ��
						if (isFlag)
						{
							isFlag=false;
							break;
						}
						//�ޱ�ǹ�������������һ��λ��
						else
						{
							//ָ��ָ����һ��λ��
							n++;
							
							//�ж��Ƿ��Ѿ�ȫ�������ϣ��������������е�����ȫ�����Ϊ��׹���ݣ�
							//���˳�witchѭ�����������
							if (n>amount)
							{
								for (int l=0;l<amount+1;l++)
								{
									gameStage[baoX[l]][baoY[l]][2]=2;
								}
								
								//�˳�witchѭ��
								break;
							}
						}
					}
				}
			}
		}
		/* System.out.println("���ݱ��ƺ������");
		//��ӡ���ݱ��ƺ�����飨���ڼ��BUG��
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		}
		System.out.println("��׹�������"); */
		
		//��������λ��ȡ����׹����
		for (int j=1;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL-1;k++)
			{
				if (gameStage[k][j][2]==2)
				{
					baoX[amount]=k;
					baoY[amount]=j;
					amount++;
					//System.out.print(gameStage[k][j][2]+",");
					gameStage[k][j][2]=0;
				}
				//��������ǻָ�ΪĬ��ֵ
				else
				{
					//System.out.print(gameStage[k][j][2]+",");
					gameStage[k][j][2]=0;
					
				}
			}
			//System.out.print(gameStage[9][j][2]+",");
			//System.out.println();
		}
		
		//ȫ�������ɣ����������׹����
		if (amount>0)
		{
			//����÷֣�ͬʱ��һ�����ݼ�1�֣�������2�֣��������ϼ�5��
			if (colorTotal>2)	//�����ף���������ɫ����С�ڶ���ʱ�����Ʒ�
			{
				switch (amount)
				{
				case 1:
					score+=1;
					break;
				case 2:
					score+=2;
					break;
				default:
					score+=5;
					break;
				}
			}
			
			for (int i=0;i<amount;i++)
			{
				//�������в��ҵ���׹���ݵ�ͼ��ID��Ȼ��ɾ�������ж�Ӧ�Ľڵ㡢���ͼ���Ӧ����ı���Լ�ɾ����ͼ��
				if ((layerID=myLine.findNode(baoY[i],baoX[i]))!=-1)
				{
					myLine.delNode(layerID);
					gameStage[baoX[i]][baoY[i]][0]=0;
					//System.out.print(baoY[i]+"  ");
					layermanager.remove((PaopaoSprite)layermanager.getLayerAt(layerID));
				}
			}
			amount=0;
		}
		//System.out.println();
	}
	
	/**
	* ���õ�ǰ��Ϸ����
	*/
	protected void setSaveStageIndex()
	{
		Paopao.stageIndex=stageIndex;
	}
	
	/**
	* ���õ�ǰ��Ϸ����
	*/
	protected void setStageIndex()
	{
		stageIndex=Paopao.stageIndex;
	}
	
	/**
	* ��ȡ��ǰ��Ϸ�ؿ�ID
	*/
	protected int getStageIndex()
	{
		return stageIndex;
	}
	
	/**
	* �ر���Ϸ�߳�
	*/
	protected void closeThread()
	{
		isReckonStart=false;
		isPause=true;
		//thread=null;
	}
	
	/**
	* ���Լ���ʾ����Ļ��
	*/
	protected void showMe()
	{
		midlet.setDisplayable(this);
	}
	
	/**
    * �ڲ��࣬����װ����Դ����
    */
    class Loading implements Runnable
    {
        // ���߳�
        Thread innerThread = null;
        int stageIndex = 1;
		
        public Loading(int stageIndex)
        {
            this.stageIndex = stageIndex;
            innerThread = new Thread(this);
            innerThread.start();
        }
		
        public void run()
        {
            isLoading = true;
            loadStage(stageIndex);
            System.gc();
            isLoading = false;
        }
		
		/**
		* װ�عؿ�
		*/
		private void loadStage(int stageIndex)
		{
			
			loadPercent = 0;
			
			//��ʼ����������
			initializtionGameStageDate();
			
			//�������÷���̨�����뱸�����ݵ���ɫ
			paopaoRandomColor=Math.abs(ran.nextInt(7))+1;
			paopaoSelectColor=Math.abs(ran.nextInt(7))+1;
			newPaopaoSprite.setFrame(paopaoRandomColor);
			paopaoSprite.setFrame(paopaoSelectColor);
			System.out.println(paopaoSelectColor);
			loadPercent = 10;
			
			//���������ɾ���������ʼ�����������ͼ��
			myLine.setNodeNull();
			while (layermanager.getSize()>5)
			{
				layermanager.remove(layermanager.getLayerAt(2));
			}
			
			loadPercent = 20;
			
			//��ȡָ���ؿ�����
			RecordStoreManage rsm=new RecordStoreManage();
			gameStage=rsm.loadGameStage(stageIndex);
			rsm=null;
			
			loadPercent = 50;
			System.out.println(loadPercent);
			//����Ļ�ϻ��ƹؿ����ݣ��������ݣ�
			drawStage();
			
			loadPercent = 75;
			
			//װ����Ч
			//if (Paopao.acousticEffect==0) loadAcousticEffect();
			
			loadPercent = 90;
			
			//����ͼ��
			layermanager.paint(g,(getWidth()-imgBack.getWidth())/2,(getHeight()-imgBack.getHeight())/2);
			
			loadPercent = 100;
			
			try
			{
				Thread.sleep(200);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
    }
	
	
	/**
    * ����װ����Ϣ��
    */
    protected void drawLoadingFrame()
    {
        int x=(getWidth()-Paopao.GAME_IMAGE_WIDTH)/2;
		int y=getHeight()/2;
        
        // ����
        g.setColor(0x0FFBDBDBD);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // ������Ϣ��
        g.setColor(0x0FFBDBDBD);
        g.fillRect(x, y-30, Paopao.GAME_IMAGE_WIDTH, 60);
        g.setColor(0x000000);
        g.drawRect(x, y-30, Paopao.GAME_IMAGE_WIDTH, 60);
        
        // ���ƽ�����
        g.setColor(0xFFFFFF00);
        g.fillRect(x, y-5, (Paopao.GAME_IMAGE_WIDTH * loadPercent) / 100, 10);
        g.setColor(0xFFFF0000);
        g.drawRect(x, y-5, Paopao.GAME_IMAGE_WIDTH, 10);
        
    }
	
	/**
    * �ڲ��࣬����ÿ�η������ݳ�ʱʱ�䣬������ʣ�����������
    */
    class projectTimeOut implements Runnable
    {
        // ���߳�
        Thread timeOutThread = null;
        private int timeOutDefault=9;		//Ĭ�ϳ�ʱʱ��
		private int timeOut=timeOutDefault;	//��ʱʱ��
		private boolean isReckonRun=true;
		
        public projectTimeOut()
        {
            timeOutThread = new Thread(this);
            timeOutThread.start();
        }
		
        public void run()
        {
			while (isReckonRun)
			{
				if (isReckonStart)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					
					timeOut--;
					//System.out.println(timeOut);
					switch (timeOut)
					{
					case 3:
						drawTimeOutNum(0);
						break;
					case 2:
						drawTimeOutNum(1);
						break;
					case 1:
						drawTimeOutNum(2);
						break;
					case 0:
						isReckon();
						break;
					}
				}
			}
        }
		
		//���ó�ʱʱ��
		public void setTime()
		{
			timeOut=timeOutDefault;
		}
		
		/**
		* ���Ƴ�ʱʱ��
		*/
		private void drawTimeOutNum(int index)
		{
			timeOutNum.setFrame(index);
			timeOutNum.setVisible(true);
		}
    }
	
	
	/**
    * �ڲ��࣬���ݷ�����
    */
    class paopaoFly implements Runnable
    {
        // ���߳�
        Thread flyThread = null;
		private boolean isRun=true;				//���������߳�״̬
		private PaopaoSprite ppSprite=null;		//�����е����ݾ���
		private int paopaoColor;				//���������ݵ���ɫ
		private int startX,startY,paopao_x,paopao_y;	//�������ݵ���ʼ����������е�����
		private int arrowArc;					//���ڷ����е����ݵķ��нǶ�
		private int layerID=0;					//�����е��������ڵĲ�ID
		private int count=0;					//���ݷ��в�����ÿ�����е�������ˣ�������Ļÿˢ��һ�Σ����ݾ������ʼ��ľ���
		private int spf=Paopao.ranspf;			//��Ϸÿ֡����ʱ��
		private boolean paopaoFlag=true;		//�����Ƿ�Ϊ�����ӵı�־����������Ҫʹ�õ��߼��ж�
		private boolean isCollide=false;		//��ײ�ı�־
		private int paopao_row=0;				//�����е����ݾ��������������������
		private int paopao_col=0;				//�����е����ݾ��������������������
		private int ranAddNum=0;				//���������ݵ�����
		
		/**
		* ������
		*/
        public paopaoFly(PaopaoSprite paopaoSprite,int paopaoColor,int startX,int startY,int arrowArc,int layerID,int ranAddNum)
        {
			ppSprite=paopaoSprite;
			this.paopaoColor=paopaoColor;
			this.startX=this.paopao_x=startX;
			this.startY=this.paopao_y=startY;
			this.arrowArc=arrowArc;
			this.layerID=layerID;
			this.ranAddNum=ranAddNum;
			
            flyThread = new Thread(this);
            flyThread.start();
			//System.out.println(paopaoFlag+" "+isRun+" "+isPause+" "+arrowArc+" "+layerID);
        }
		
        public void run()
        {
			while (isRun)
			{
				long times= System.currentTimeMillis();
				
				if (!isPause)
				{
					//�ƶ�����
					paopaoMove();
					
					if (paopaoFlag)
					{
						//�����������߼��ж�
						randomLogic();
						
					}
					else
					{
						//�߼��ж�
						logic();
					}
				}
				
				times= System.currentTimeMillis()-times;
				if( times<spf )
				{
					try
					{
						Thread.sleep(spf-times );
					}
					catch(InterruptedException ie)
					{
						isRun=false;
					}
				}
			}
        }
		
		/**
		* ���õ�ǰ����Ϊ��������
		*/
		public void setPaopaoFlag(boolean flag)
		{
			paopaoFlag=flag;
		}
		
		/**
		* ���õ�ǰ����Ϊ��������
		*/
		public void setSPF(int spf)
		{
			this.spf=spf;
		}
		
		/**
		* �����ƶ�
		*/
		private void paopaoMove() 
		{
			count++;
			paopao_x=startX+(count*Paopao.speed)*ARROW_ARC_COS[arrowArc]/100000;
			paopao_y=startY-(count*Paopao.speed)*ARROW_ARC_SIN[arrowArc]/100000;
			ppSprite.setPosition(paopao_x,paopao_y);
		}
		
		/**
		* �߼��ж�
		*/
		private void logic()
		{
			if (isFly)
			{
				if (paopao_x<paopao_img_x)
				{
					paopao_x=paopao_img_x;
				}
				else if (paopao_x>(Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH-paopao_img_x))
				{
					paopao_x=Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH-paopao_img_x;
				}
				if (paopao_y<paopao_img_y) 
				{
					paopao_y=paopao_img_y;
				}
				else if (paopao_y>(Paopao.GAME_IMAGE_HEIGHT-Paopao.SPRITE_HEIGHT-paopao_img_y)) 
				{
					paopao_y=Paopao.GAME_IMAGE_HEIGHT-Paopao.SPRITE_HEIGHT-paopao_img_y;
				}
				
				paopao_row=getRow();
				paopao_col=getCol();
				
				if (paopao_row<0) paopao_row=0;
				if (paopao_col<0) paopao_col=0;
				else if (paopao_col>Paopao.PAOPAO_COL-2) paopao_col=Paopao.PAOPAO_COL-2;
				
				//�ж������Ƿ��Ѿ�ײ������
				if (paopao_x==paopao_img_x)
				{
					arrowArc=ARROW_ARC_SIN.length-1-arrowArc;
					ppSprite.setPosition(paopao_x,paopao_y);
					count=0;
					startX=paopao_x;
					startY=paopao_y;
				}
				else if (paopao_x==(Paopao.GAME_IMAGE_WIDTH-Paopao.SPRITE_WIDTH-paopao_img_x))
				{
					arrowArc=ARROW_ARC_SIN.length-1-arrowArc;
					count=0;
					startX=paopao_x;
					startY=paopao_y;
				}
				
				//�Ѿ�����������
				if (paopao_y==paopao_img_y)
				{
					isCollide=true;
					isFly=false;
				}
				else
				{
					//�����ھ��������²�����ײ���
					if (paopao_row<Paopao.PAOPAO_ROW+1)
					{
						//������ײ���
						collide(layerID);
					}
				}
				
				//System.out.println(paopao_row);
				if (isCollide)
				{
					synchronized(this)
					{
						if (isBusy)
						{
							try 
							{
								wait();//������ֱ�����¿ͻ�����
							} 
							catch (InterruptedException e) 
							{ 
							
							}
						}
						isBusy=true;
						ppSprite.setPosition(getRectX(paopao_col,paopao_row),getRectY(paopao_row));	//𤸽����
						//���³�ʼ�����ݷ�����в���
						count=0;
						myLine.insertNode(layerID+1,paopao_row,paopao_col,paopaoColor,ranAddNum,true);	//�������в������������ݲ�
						gameStage[paopao_col][paopao_row][0]=(byte)paopaoColor;			//�ڵ�ǰλ�ü�¼������ɫ
						isCollide=false;
						isFly=false;
						updatePaopao();			//���������ݸ��µ�����̨��
						
						//System.out.println(isReckonStart);
						//�Ƿ��Ѿ�ʧ����
						if (paopao_row>Paopao.PAOPAO_ROW-1)
						{
							//pTimeOut.closeThread();			//�ر����ݳ�ʱ�����߳�
							//isActive=false;				//�����ⲿ���߳�
							midlet.gameMenu.setMenuIndex(8);
							midlet.gameMenu.setInfoFrameIndex(1);
							midlet.gameMenu.showMe();
							//System.out.println("��ʧ��");
							isReckonStart=false;
							paopaoCount=0;
						}
						else
						{	
							//����ͬɫ����3�������ݣ���ͬʱ��û�йҵ������ɾ��
							if (paopaoBao(paopao_col,paopao_row))
							{
								paopaoDrop();
								if (myLine.isNull())
								{
									//����÷֣�ÿ��һ�ؼ�100��
									score+=100;
									
									//isActive=false;		//�����ⲿ���߳�
									//pTimeOut.closeThread();			//�ر����ݳ�ʱ�����߳�
									
									//��ת��ʤ����Ϣ��
									midlet.gameMenu.setMenuIndex(8);
									midlet.gameMenu.setInfoFrameIndex(0);
									midlet.gameMenu.showMe();
									isReckonStart=false;
									paopaoCount=0;
								}
							}
						}
						
						//ÿ����ppCount�����ݣ������������һ������
						if (paopaoCount%Paopao.ppCount==0 && paopaoCount!=0)
						{
							//randomAddPaopao(Paopao.PAOPAO_ROW-1);
							randomAddPaopao(5);
						}
						
						isRun=false;			//���������ƶ��߳�
						
						isBusy=false;
						notify(); //ͬʱ���Ѵ����߳�
					}
				}
			}
		}
		
		/**
		* �߼��ж�
		*/
		private void randomLogic()
		{
			if (paopao_y<paopao_img_y) 
			{
				paopao_y=paopao_img_y;
			}
			paopao_row=getRow();
			paopao_col=getCol();
			if (paopao_row<0) paopao_row=0;
			if (paopao_col<0) paopao_col=0;
			else if (paopao_col>Paopao.PAOPAO_COL-2) paopao_col=Paopao.PAOPAO_COL-2;
			
			//�Ѿ�����������
			if (paopao_y==paopao_img_y)
			{
				isCollide=true;
			}
			else
			{
				//�����ھ��������²�����ײ���
				if (paopao_row<Paopao.PAOPAO_ROW+1)
				{
					//������ײ���
					collide(layerID);
				}
			}
			
			if (isCollide)
			{
				synchronized(this)
				{	
					count=0;
					if (isBusy)
					{
						try 
						{
							wait();//������ֱ�����¿ͻ�����
						} 
						catch (InterruptedException e) 
						{ 
							
						}
					}
					isBusy=true;
					ppSprite.setPosition(getRectX(paopao_col,paopao_row),getRectY(paopao_row));	//𤸽����
					//���³�ʼ�����ݷ�����в���
					myLine.insertNode(layerID,paopao_row,paopao_col,paopaoColor,ranAddNum,isRanAddPP);	//�������в������������ݲ�
					gameStage[paopao_col][paopao_row][0]=(byte)paopaoColor;			//�ڵ�ǰλ�ü�¼������ɫ
					isRanAddPP=false;
					isBusy=false;
					notify(); //ͬʱ���Ѵ����߳�
				}
				
				isCollide=false;
				isRun=false;			//���������ƶ��߳�
				ranAddPPNum--;
				if (ranAddPPNum<=0)
				{
					ranAddPPNum=0;
					isReckonStart=true;
				}
				
				/* System.out.println(layerID);
				System.out.println("���ݲ���������(��)");
				//��ӡ���ݲ��������飨���ڼ��BUG��
				for (int j=0;j<Paopao.PAOPAO_ROW;j++)
				{
					for (int k=0;k<Paopao.PAOPAO_COL;k++)
					{
						System.out.print(myLine.findNode(j,k)+",");
						//System.out.print(gameStage[k][j][0]+",");
					}
					System.out.println();
				}				
				System.out.println("���ݲ���������(��ɫ)");
				//��ӡ���ݲ��������飨���ڼ��BUG��
				for (int j=0;j<Paopao.PAOPAO_ROW;j++)
				{
					for (int k=0;k<Paopao.PAOPAO_COL;k++)
					{
						//System.out.print(myLine.findNode(j,k)+",");
						System.out.print(gameStage[k][j][0]+",");
					}
					System.out.println();
				} */		
				
				//System.out.println("row:"+paopao_row);
				//�Ƿ��Ѿ�ʧ����
				if (paopao_row>Paopao.PAOPAO_ROW-1)
				{
					//pTimeOut.closeThread();			//�ر����ݳ�ʱ�����߳�
					//isActive=false;
					midlet.gameMenu.setMenuIndex(8);
					midlet.gameMenu.setInfoFrameIndex(1);
					midlet.gameMenu.showMe();
					//System.out.println("��ʧ��");
					isReckonStart=false;
				}
			}
		}
		
		/**
		* ��þ��鵱ǰ�ο������ڸ��������
		*/
		private int getRow()
		{
			return (ppSprite.getRefPixelY()-paopao_img_y)/(Paopao.SPRITE_HEIGHT-2);
		}
		
		/**
		* ��þ��鵱ǰ�ο������ڸ��������
		*/
		private int getCol()
		{
			if (gameStage[Paopao.PAOPAO_COL-1][paopao_row][0]==1)
			{
				return (ppSprite.getRefPixelX()-paopao_img_x+Paopao.SPRITE_WIDTH/2)/Paopao.SPRITE_WIDTH;
			}
			else
			{
				return (ppSprite.getRefPixelX()-paopao_img_x)/Paopao.SPRITE_WIDTH;
			}
		}
		
		/**
		* ����ײ
		*/
		private synchronized void collide(int layerID)
		{
			if (isBusy)
			{
				try 
				{
					wait();//������ֱ�����¿ͻ�����
				} 
				catch (InterruptedException e) 
				{ 
					
				}
			}
			isBusy=true;
			for (int i=2;i<layermanager.getSize()-3;i++)
			{
				if (i!=layerID) 
				{
					if (ppSprite.collidesWith(((PaopaoSprite)layermanager.getLayerAt(i)), true)) 
					{
						isCollide=true;
					}
				}
			}
			isBusy=false;
			notify(); //ͬʱ���Ѵ����߳�
		}
    }
	
	private void printArray()
	{
		System.out.println(layerID);
		System.out.println("���ݲ���������(��)");
		//��ӡ���ݲ��������飨���ڼ��BUG��
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				System.out.print(myLine.findNode(j,k)+",");
				//System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		}				
		System.out.println("���ݲ���������(��ɫ)");
		//��ӡ���ݲ��������飨���ڼ��BUG��
		for (int j=0;j<Paopao.PAOPAO_ROW;j++)
		{
			for (int k=0;k<Paopao.PAOPAO_COL;k++)
			{
				//System.out.print(myLine.findNode(j,k)+",");
				System.out.print(gameStage[k][j][0]+",");
			}
			System.out.println();
		}		
	}
}