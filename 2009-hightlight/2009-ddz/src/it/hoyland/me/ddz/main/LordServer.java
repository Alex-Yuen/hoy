/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.hoyland.me.ddz.main;

import java.util.Random;

/**
 *
 * @author Ashrum
 */

public class LordServer implements Runnable{
    int gameState =0;//��׼����Ϸ�Ľ���״̬.0:δ��ʼ;1:����;2;���ڽ���;3:�ֳ�ʤ��
    int subState = 0;//ÿ���׶ε�С�׶�
    private int playernum=3;
    LordPlayer lPlayer[] = new LordPlayer[playernum];
    int lord=-1;
    FLDMidlet midlet;
    LordCanvas lCanvas;
    AIServer ai;
    Random rnd=new Random(System.currentTimeMillis());
    int[] pai= new int[54];
    int lordcards[] = new int[3];
    int firstcall;
    int activeplayer =-1;
    int rightType=-1;

     Thread serverthread;

    LordServer(FLDMidlet aMidlet,LordCanvas lCanvas) {
        this.midlet = aMidlet;
        this.lCanvas = lCanvas;
        ai = new AIServer(this);

    }
    public void StartServer()
    {
        serverthread = new Thread(this);
        serverthread.start();
    }
    public void run() {
        while (true)
        {
            if(lCanvas.ispaused)
                continue;
            switch(gameState)
            {
                case 1:
                    if(subState==1)
                    {
                       activeplayer = firstcall;
                        if(activeplayer ==1|| lPlayer[activeplayer].callscore!=-1)
                             break;
                        lPlayer[activeplayer].chooseCall();
                        send(gameState,subState);
                         // activeplayer = (activeplayer+1)%3;
                       
                    }
                    else if(subState==2)//�������ҽз�
                    {
                        if(activeplayer ==1|| lPlayer[activeplayer].callscore!=-1)
                             break;

                        lPlayer[activeplayer].chooseCall();                      
                        send(gameState,subState);                       
                    }
                    else if(subState==3)
                        {
                           if(activeplayer ==1|| lPlayer[activeplayer].callscore!=-1)
                             break;

                       
                        lPlayer[activeplayer].chooseCall();
                     
                       // subState++;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        send(gameState,subState);
                        
                    }
                    else if(subState ==4)
                    {
                           lPlayer[1].callscore=lCanvas.lPlayer[1].callscore ;
                           enLord();
                         
                            send(gameState,subState);
                              gameState++;
                              activeplayer = lord;
                           
                    }
                    break;
                case 2:
                    if(activeplayer ==2)//���2����
                    {
                       lPlayer[2].pass = false;
                       if(lPlayer[2].sendright!=0)
                       {
                            if(lPlayer[1].pass)
                            {
                                if(lPlayer[0].pass)//1��2ȫ��Ҫ
                                    lPlayer[2].sendright = 0;
                                else
                                    lPlayer[2].sendright = 2;
                            }
                            else
                                lPlayer[2].sendright = 1;
                       }
                        ai.getStrategy(2,lPlayer[2].sendright);
                        dealCardsSend(lPlayer[2].sendCards,lPlayer[2].sendnum,2);
                         try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        activeplayer = 0;
                        send(gameState, activeplayer);
                    }
                    else if(activeplayer ==0)//���0����
                    {
                       lPlayer[0].pass = false;
                        if(lPlayer[0].sendright!=0)
                       {
                            if(lPlayer[2].pass)
                            {
                                if(lPlayer[1].pass)//1��2ȫ��Ҫ
                                    lPlayer[0].sendright = 0;
                                else
                                    lPlayer[0].sendright = 2;
                            }
                            else
                                lPlayer[0].sendright = 1;
                        }
                        ai.getStrategy(0,lPlayer[0].sendright);
                        dealCardsSend(lPlayer[0].sendCards,lPlayer[0].sendnum,0);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        activeplayer = 1;
                        send(gameState, activeplayer);
                    }
                }            
        }
    }

    void receive(int state, int substate) {
        switch(state)
        {
            case 1://��Ϸ��ʼ
                if(substate ==0)
                {
                     for(int i=0; i!=playernum; ++i)
                     {
                           this. lPlayer[i] = new LordPlayer();
                           this. lPlayer[i].name = lCanvas.lPlayer[i].name;
                      }
                       this.gameState = 1;
                       this.subState =0;
                       allotCards();//����,ȷ��˭�Ƚз�
                       send(gameState,subState);
                       subState=1;//����зֽ׶�        
                }
                else
                {
                    this.subState = substate;
                     this.activeplayer = lCanvas.activeplayer;
                }              
                break;
            case 2://�Ѿ���ʼ
                //activeplayer = substate;
                //ע��,activeplayer�ǵ�ǰ��ң���Ҫͬ�����Ǹոճ����Ƶ������Ϣ
                int lastplayer = (substate+2)%3;
                switch(lastplayer)
                {
                    case 0:
                        lPlayer[0].pass = lCanvas.lPlayer[0].pass;
                        lPlayer[0].sendnum = lCanvas.sendnum0;
                        for(int i=0;i!= lPlayer[0].sendnum;++i)
                              lPlayer[0].sendCards[i] = lCanvas.sendpai0[i];
                         break;
                    case 1://���1�ոճ���
                        lPlayer[1].pass = lCanvas.lPlayer[1].pass;
                        lPlayer[1].sendnum = lCanvas.sendnum1;
                        for(int i=0;i!= lPlayer[1].sendnum;++i)
                              lPlayer[1].sendCards[i] = lCanvas.sendpai1[i];
                         break;
                      
                    case 2:
                        lPlayer[2].pass = lCanvas.lPlayer[2].pass;
                        lPlayer[2].sendnum = lCanvas.sendnum2;
                        for(int i=0;i!= lPlayer[2].sendnum;++i)
                              lPlayer[2].sendCards[i] = lCanvas.sendpai2[i];
                         break;  
                }//end of switch lastplayer
                activeplayer = substate;
   
                break;
        }
    }

    void reset() {
       for(int i=0;i!=3;++i)
       {
           lPlayer[i].isLord = false;
           lPlayer[i].callscore = -1;
           lPlayer[i].left=0;
           lPlayer[i].pass=false;
           lPlayer[i].sendright=-1;
       }
      
    }
     private void dealCardsSend(int []sendcards,int sendnum,int player) {
        int leftnum = lPlayer[player].left-sendnum;
        int leftcards[] = new int[leftnum];
        int h=0;
        int k = 0;
        GameRules rule= new GameRules();
        rule.setCards(sendcards, sendnum);
        if(rule.getCardType()==2||rule.getCardType()==1)
            lCanvas.callTime*=2;
         for(int j=0;j!= lPlayer[player].left;++j)
         {              
             for(h=0;h!=sendnum;++h)
                  if(sendcards[h]==lPlayer[player].Cards[j])
                      break;
             if(h==sendnum)
             {
                  leftcards[k]=lPlayer[player].Cards[j];
                  k++;
                  continue;
             }
         }
          for(int i=0;i!=leftnum;++i)
          {
              lPlayer[player].Cards[i] = leftcards[i];
          }
          lPlayer[player].left = leftnum;
          lPlayer[player].sendright = -1;
    }
     public void allotCards()
	{//������
		int cardnum=0;
		int qu=0;
		int n=0;
		while(cardnum<54) //while�������ã���ѡ��54�����ݣ���ѡ������˳�����η���pai[]��
		{
			qu = Math.abs(rnd.nextInt())%54+1;
			for(int m = 0; m<=cardnum; m++) //�жϻ�ѡ��������ֵqu�Ƿ��Ѿ���pai[]��
			{
				if(pai[m]==qu)
				{
					n=0;
					break;
				}
				else n= 1;
			}
			if(n==1) //�����ѡ��������ֵ����pai[]�У����qu��ֵ���뵽pai[]������Ӧ��λ��
			{
				pai[cardnum]=qu;
				cardnum++;
			}
		}
		int k,m;      
		for(k=0,m=0;m<17;k++,m++)
		{
			lPlayer[0].Cards[k] = pai[m];           
        }
        
		for(k=0,m=17;m<34;k++,m++)
		{
			lPlayer[1].Cards[k] = pai[m];
		}
		for(k=0,m=34;m<51;k++,m++)
		{
			lPlayer[2].Cards[k] = pai[m];
		}
        for(int i=0;i!=3;++i)
        {
            lordcards[i] = pai[i+51];
            Sort(lPlayer[i].Cards,17);//����
             lPlayer[i].left =17;
        }      
        firstcall =  Math.abs(rnd.nextInt())%3;//��������Ƚзֵ���
	}
    public void Sort(int array[],int length)
    {
        int max = 0;
        int pos = 0;
        for(int i=0;i!=length;++i)
        {
            for(int j=i;j!=length;++j)
            {
                if(array[j]>max)
                {
                    max = array[j];
                    pos = j;
                }
            }
            array[pos]=array[i];
            array[i]= max;
            max = 0;
        }
    }

    private void enLord() {
        // firstcall = (firstcall +1)%3;//�ص��ʼ�зֵ����
         int maxCall = -1;
        for(int i=0;i!=3;++i)
        {
             if(lPlayer[firstcall].callscore>maxCall)
             {
                     maxCall = lPlayer[firstcall].callscore;
                     lord = firstcall;
             }
              firstcall = (firstcall +1)%3;
        }
         //
      //   lord=0;
        for(int i=0;i!=3;++i)
        {
            lPlayer[lord].Cards[i+17]= lordcards[i];
        }
         lPlayer[lord].left = 20;
         Sort(lPlayer[lord].Cards,20);//����
         //
         String p0="";
         for(int i=0;i!=lPlayer[0].left;++i)
         {
              p0+=Integer.toString(lPlayer[0].Cards[i])+" ";
         }
         System.out.println(p0);
          String p2="";
         for(int i=0;i!=lPlayer[2].left;++i)
         {
              p2+=Integer.toString(lPlayer[2].Cards[i])+" ";
         }
         System.out.println(p2);
         lPlayer[lord].sendright=0;//�����������ɳ���
    }

    private void send(int state,int substate) {
        lCanvas.receive(state, substate);
    }


}
