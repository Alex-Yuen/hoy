/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.hoyland.me.ddz.main;

import javax.microedition.lcdui.Alert;

/**
 *
 * @author Ashrum
 */
public class AIServer{

    LordServer server;
    int resultBomb[] ;
    int bombnum;
    int resultPlane[],planenum ;
    int resultSquDouble[][];
    int resultSqu[][];
    int resultSingle[],singlenum;
    int resultDouble[],doublenum;
    int resultThree[],threenum ;
    int anaCards[];
    int anaArray[] ;
    int num;

    public AIServer(LordServer aServer){
       server = aServer;      
    }    
    public void getStrategy(int player,int right)
    {
       int lastplayer=-1;
       if(right ==2)
           lastplayer = (player+1)%3;
       else if(right==1)
           lastplayer = (player+2)%3;
        server.lPlayer[player].sendnum=0;
        //��������ƵĽṹ
        cardAnalysis(player,server.lPlayer[player].left);
//        Alert a = new Alert("");
//        a.setString("analysis success");
//        server.midlet.getDisplay().setCurrent(a,server.lCanvas);
        int result[] = new int[20];
        int resultnum = 0;
        System.out.println("choose cards ");
        //������Լ�������
        if(right ==0)
        {            //��һ�����ɻ�
            if(resultPlane[0]!=0)
            {
                for(int i=0;i!=6;++i)
                {
                    result[i] = resultPlane[i];
                }
                resultnum = 6;
                planenum--;               
                if(singlenum>=2)
                {
                    result[6] = resultSingle[0];
                    result[7] = resultSingle[1];
                    resultSingle[0] = 0;
                    resultSingle[1] = 0;
                    singlenum -=2;
                    resultnum = 8;                    
                }
                  //û�ҵ����ŵ���
                else if(doublenum>=2)
                {                   
                        result[6] = resultDouble[0];
                        result[7] = resultDouble[1];
                        result[8] = resultDouble[2];
                        result[9] = resultDouble[3];
                        doublenum-=2;
                        resultnum = 10;                   
                }
             }//end of plane
            //�ٿ�����
            else if(resultSquDouble[0][0]!=0||resultSquDouble[1][0]!=0||resultSquDouble[1][0]!=0)
            {
                int p = 0;
                for(;p!=3;++p)
                    if(resultSquDouble[p][0]!=0)
                        break;
                int i=0;
                while(i<20&&resultSquDouble[p][i]!=0)
                {
                    result[i] = resultSquDouble[p][i];
                    resultnum++;
                    i++;
                }
            }//end of ������
            //����˳��
            else if(resultSqu[0][0]!=0||resultSqu[1][0]!=0||resultSqu[1][0]!=0)
            {
                int p = 0;
                for(;p!=3;++p)
                    if(resultSqu[p][0]!=0)
                        break;
                int i=0;
                while(i<12&&resultSqu[p][i]!=0)
                {
                    result[i] = resultSqu[p][i];
                    resultnum++;
                    i++;
                }
            }//end of ��˳��
            //����һ���ԣ�����
            else
            {
                //�ȿ�3��һ
                if(threenum!=0)
                {
                    int i=0;
                    for(;i!=20;++i)
                        if(resultThree[i]!=0)
                            break;
                    for(int j=0;j!=3;++j)
                        result[j] = resultThree[i+j];
                    resultnum+=3;
                    //�е�����
                    if(singlenum!=0)
                    {
                        int m=0;
                        for(;m!=20;++m)
                            if(resultSingle[m]!=0)
                                break;
                        result[3] = resultSingle[m];
                        resultnum++;
                    }
                    else if(doublenum!=0)
                    {
                        int m=0;
                        for(;m!=20;++m)
                            if(resultDouble[m]!=0)
                                break;
                        result[3] = resultDouble[m];
                        result[4] = resultDouble[m+1];
                        resultnum+=2;
                    }
                }//end of 3and1
                else if(singlenum!=0&&doublenum!=0)
                {                  
                   if(int2Value(resultSingle[0])<int2Value(resultDouble[0]))
                   {
                        result[0] = resultSingle[0];
                        resultnum = 1;
                   }
                   else
                   {
                       result[0] = resultDouble[0];
                       result[1] = resultDouble[1];
                       resultnum=2;
                   }
                }

                else if(singlenum==0&&doublenum!=0)
                {
                       result[0] = resultDouble[0];
                       result[1] = resultDouble[1];
                       resultnum=2;
                }
                else if(singlenum!=0&&doublenum==0)
                {
                     result[0] = resultSingle[0];
                     resultnum = 1;
                }//end of double and single
                  //ֻʣ��2������
                else
                {
                    int p=0;
                    for(int i=0;i!=num;++i)
                    {
                        if(int2Value(anaCards[i])==15)
                        {
                            result[p] = anaCards[i];
                            resultnum++;
                            p++;
                        }
                        else if(p==0&&int2Value(anaCards[i])>15)
                        {
                            result[p] =anaCards[i];
                            resultnum++;
                            p++;
                        }
                    }
                }

            }//3and1,2,1
            //ʣ��һ��ը��
            if(resultnum==0&&bombnum!=0)
            {
                for(int i=0;i!=4;++i)
                    result[i] = resultBomb[i];
                resultnum=4;
            }
            if(resultnum==0)
            {
                Alert a = new Alert("");
                a.setString("����δ�ҵ��ɳ����ƣ�");
                server.midlet.getDisplay().setCurrent(a);                        ;
            }
        }//end of right ==0;
        //����ùܱ��˵���
        else
        {

           //�����жϸò��ù�
           if(server.lord!=player&&server.lord!=lastplayer)//ͬ�����
               if(int2Value(server.lPlayer[lastplayer].sendCards[0])>11)//ͬ���Ѿ����J��
               {
                   server.lPlayer[player].pass =true;
                   return;
               }
           GameRules rules1 = new GameRules();
           GameRules rules2 = new GameRules();
           rules1.setCards(server.lPlayer[lastplayer].sendCards, server.lPlayer[lastplayer].sendnum);
           switch(rules1.getCardType())
           {
               case 1://���
                   server.lPlayer[player].pass =true;
                   break;
               case 2://ը��
                   if(bombnum!=0&&int2Value(resultBomb[0])>
                      int2Value(server.lPlayer[lastplayer].sendCards[0]))
                   {
                       for(int i=0;i!=4;++i)
                           result[i] = resultBomb[i];
                       resultnum = 4;
                   }
                   else//�����
                   {                     
                               if(anaCards[num-1]==54&&anaCards[num-2]==53)
                               {
                                   result[0] = 54;
                                   result[1] = 53;
                                   resultnum = 2;
                               }
                               else
                                   break;
                   }
                   break;
               case 3://����
                   if(singlenum!=0)
                   {
                        for(int i =0;i!=20;++i)//
                        {
                            if(int2Value(resultSingle[i])>int2Value(server.lPlayer[lastplayer].sendCards[0]))
                            {
                                result[0] = resultSingle[i];
                                resultnum = 1;
                                break;
                            }
                        }
                   }
                   if(resultnum==0)
                   {
                       for(int i=0;i!=num;++i)
                       {
                            if(int2Value(anaCards[i])>int2Value(server.lPlayer[lastplayer].sendCards[0]))
                            {
                                result[0] = anaCards[i];
                                resultnum = 1;
                                break;
                            }
                       }                       
                   }
                   break;
               case 4:
                    if(doublenum!=0)
                   {
                        for(int i =0;i!=19;++i)
                        {
                            if(int2Value(resultDouble[i])>int2Value(server.lPlayer[lastplayer].sendCards[0]))
                            {
                                result[0] = resultDouble[i];
                                result[1] = resultDouble[i+1];
                                resultnum = 2;
                                break;
                            }
                        }
                   }
                   if(resultnum==0)
                   {
                       for(int i=0;i!=num-1;++i)
                       {
                           //û�жԣ���һ��2�����ҶԷ����Ʊ�Q��ʱ����2��
                            if(int2Value(anaCards[i])==int2Value(anaCards[i+1])&&int2Value(server.lPlayer[lastplayer].sendCards[0])>12
                              && int2Value(anaCards[i])>int2Value(server.lPlayer[lastplayer].sendCards[0]))
                            {
                               result[0] = anaCards[i];
                                result[1] = anaCards[i+1];
                                resultnum = 2;
                                break;
                            }
                       }
                   }
                   break;
               case 5:
                    if(threenum!=0)
                   {
                        for(int i =0;i!=18;++i)
                        {
                            if(int2Value(resultThree[i])>int2Value(server.lPlayer[lastplayer].sendCards[0]))
                            {
                                result[0] = resultThree[i];
                                result[1] = resultThree[i+1];
                                result[2] = resultThree[i+2];
                                resultnum = 3;
                                break;
                            }
                        }
                   }
                   if(resultnum==0)
                   {
                       for(int i=0;i<num-2;++i)
                       {
                           //û��3���ģ���3��2�����ҶԷ����Ʊ�Q��ʱ����2��
                            if(int2Value(anaCards[i])==int2Value(anaCards[i+1])&&int2Value(anaCards[i])==int2Value(anaCards[i+2])&&
                                    int2Value(server.lPlayer[lastplayer].sendCards[0])>12
                              && int2Value(anaCards[i])>int2Value(server.lPlayer[lastplayer].sendCards[0]))
                            {
                                result[0] = anaCards[i];
                                result[1] = anaCards[i+1];
                                result[2] = anaCards[i+2];
                                resultnum = 3;
                                break;
                            }
                       }
                   }
                   break;
               case 6://3��1��һ��
                    if(threenum!=0)
                   {
                        for(int i =0;i!=18;++i)
                        {
                            if(int2Value(resultThree[i])>rules1.is3and1())
                            {
                                result[0] = resultThree[i];
                                result[1] = resultThree[i+1];
                                result[2] = resultThree[i+2];
                                resultnum = 3;
                                break;
                            }
                        }
                       // if(resultnum==0)
                       //     break;
                   }
                   if(resultnum==0)
                   {
                       for(int i=0;i<num-2;++i)
                       {
                           //û��3���ģ���3��2�����ҶԷ����Ʊ�Q��ʱ����2��
                            if(int2Value(anaCards[i])==int2Value(anaCards[i+1])&&int2Value(anaCards[i])==int2Value(anaCards[i+2])&&
                                    rules1.is3and1()>12
                              && int2Value(anaCards[i])>rules1.is3and1())
                            {
                                result[0] = anaCards[i];
                                result[1] = anaCards[i+1];
                                result[2] = anaCards[i+2];
                                resultnum = 3;
                                break;
                            }
                       }
                       if(resultnum==0)
                            break;
                   }
                   if(server.lPlayer[lastplayer].sendnum==4&&singlenum!=0)
                   {
                       for(int i=0;i!=20;i++)
                           if(resultSingle[i]!=0)
                           {
                               result[3] = resultSingle[i];
                               break;
                           }
                       resultnum =4;
                   }
                   else if(server.lPlayer[lastplayer].sendnum==5&&doublenum!=0)
                   {
                        for(int i=0;i!=19;i++)
                           if(resultDouble[i]!=0)
                           {
                               result[3] = resultDouble[i];
                               result[4] = resultDouble[i+1];
                               break;
                           }
                       resultnum =5;
                   }
                   else
                       resultnum =0;
                   break;
               case 7://˳��
                   int p=0;
                   for(;p!=3;++p)
                       if(resultSqu[p][0]!=0)
                           break;
                  if(p==3)//û��˳��
                      break;
                  while(p<3)
                  {
                      int length=0;
                      for(;length<12&&resultSqu[p][length]!=0;++length);
                      if(length!=server.lPlayer[lastplayer].sendnum||int2Value(resultSqu[p][0])<=rules1.isOneSeq())
                      {
                          p++;
                          continue;
                      }
                      else
                      {
                          for(int i=0;i!=length;++i)
                              result[i] = resultSqu[p][i];
                          resultnum = length;
                          break;
                      }
                  }
                  break;
               case 8://����
                       int dp=0;
                       for(;dp!=3;++dp)
                           if(resultSquDouble[dp][0]!=0)
                               break;
                      if(dp==3)//û������
                          break;
                      while(dp<3)
                      {
                          int length=0;
                          for(;resultSquDouble[dp][length]!=0;++length);
                          if(length!=server.lPlayer[lastplayer].sendnum||int2Value(resultSquDouble[dp][0])<=rules1.isTwoSeq())
                          {
                              dp++;
                              continue;
                          }
                          else
                          {
                              for(int i=0;i!=length;++i)
                                  result[i] = resultSquDouble[dp][i];
                              resultnum = length;
                              break;
                          }
                      }
                      break;
               default:
                      break;




           }//end of switch
        }//end of right!=0
         if(resultnum==0)
                server.lPlayer[player].pass=true;
         else if(player!=1)
         {
               for(int i=0;i!=resultnum;++i)
                   server.lPlayer[player].sendCards[i]=result[resultnum-i-1];
               server.lPlayer[player].sendnum = resultnum;
         }
        if(player==1)
        {
           for(int i=0;i!=20;++i)
           {
              for(int j=0;j!=20;++j)
              {
                  if(server.lCanvas.lPlayer[1].Cards[i]==result[j])
                  {
                      server.lCanvas.state[i] = 1;
                      break;
                  }
              }
           }
        }
        System.out.println("choose over");
    }

     void cardAnalysis(int player,int num) {

        this.num = num;
        resultBomb = new int [20];
        resultPlane = new int [20];
        resultSquDouble = new int [3][20];
        resultSqu = new int [3][12];
        resultSingle = new int[20];
        resultDouble = new int[20];
        resultThree = new int[20];
        anaCards = new int [num];
        anaArray= new int [12];
        bombnum = 0;
        planenum = 0;
        singlenum =0;
        doublenum = 0;
        threenum = 0;

        for(int i=0;i!=num;++i)
        {
            anaCards[i] = server.lPlayer[player].Cards[num-1-i];
        }
         //ͳ���Ʒֲ�
        for(int i = 0; i!=num;++i)
        {
            if(anaCards[i]==0||int2Value(anaCards[i])>14)
                continue;
            else
                anaArray[int2Value(anaCards[i])-3]++;
        }       
        setBomb();
        setPlane();
        setAllOthers();    
    }

    private void addtoDouble(int i) {
         int p;
        for(p=0;p!=20;++p)
        {
            if(resultDouble[p]==0)
                break;
        }
        for(int k=0;k!=num-1;++k)
        {
            if(int2Value(anaCards[k])==i+3)
            {
                resultDouble[p] = anaCards[k];
                resultDouble[p+1] = anaCards[k+1];
                anaCards[k] =0;
                anaCards[k+1] =0;
                anaArray[i] -=2;
                doublenum++;
                return;
            }
        }
    }

    private void addtoSingle(int i) {
        int p;
        for(p=0;p!=20;++p)
        {
            if(resultSingle[p]==0)
                break;
        }
        for(int k=0;k!=num;++k)
        {
            if(int2Value(anaCards[k])==i+3)
            {
                resultSingle[p] = anaCards[k];
                anaCards[k] =0;
                anaArray[i] -=1;
                singlenum++;
                return;
            }
        }
    }

    private void addtoThree(int i) {
         int p;
        for(p=0;p!=20;++p)
        {
            if(resultThree[p]==0)
                break;
        }
        for(int k=0;k<num-2;++k)
        {
            if(int2Value(anaCards[k])==i+3)
            {
                resultThree[p] = anaCards[k];
                resultThree[p+1] = anaCards[k+1];
                resultThree[p+2] = anaCards[k+2];
                anaCards[k] =0;
                anaCards[k+1] =0;
                anaCards[k+2] =0;
                anaArray[i] -=3;
                threenum++;
                return;
            }
        }
    }
    //ѡ�������ú����е�ը��
    void setBomb() {
        System.out.println("set bomb ");
        if(num<4) return;
        int[] array = new int [4];
        GameRules rule = new GameRules();
        for(int i=0;i<num-3;++i)
        {
            for(int j=0;j!=4;++j)
                array[j] = anaCards[i+j];
            rule.setCards(array, 4);
            if(rule.isBomb()!=0)
            {
                //4��2����ʱ����ը��
                if(int2Value(array[0])>=15)
                    return;
                anaArray[int2Value(array[0])-3]=0;
                bombnum++;
                for(int k=0;k!=20;++k)
                 {
                     if(resultBomb[k]==0)
                     {
                         for(int j=0;j!=4;++j)
                         {
                             resultBomb[k+j] = array[j];
                             anaCards[i+j] = 0;
                         }
                         break;
                     }
                 }
            }
        } 
    }
    void setPlane()
    {
        System.out.println("set plane ");
        if(num<6) return;
        int[] array = new int [6];
       
        for(int i=0;i<num-5;++i)
        {
            for(int j=0;j!=6;++j)
                array[j] = int2Value(anaCards[i+j]);            
            if(array[0]==array[1]&&array[1]==array[2]&&array[2]+1==array[3]&&
               array[3]==array[4]&&array[4]==array[5])
            {
                anaArray[(array[0])-3]=0;
                anaArray[(array[3])-3]=0;
                planenum++;
                 for(int k=0;k!=20;++k)
                 {
                     if(resultPlane[k]==0)
                     {
                         for(int j=0;j!=6;++j)
                         {
                             resultPlane[k+j] = anaCards[i+j];
                             anaCards[i+j] = 0;
                         }
                         break;
                     }
                 }
            }
        }
    }
    void setAllOthers() {
        System.out.println("set all others");
        int start=0,end=0;
        while(start<12)//ֱ�������ƶ�������
        {            
            if(anaArray[start]==0)
            {
                start++;
                end= start;
                continue;
            }
           while(end<12&&anaArray[end]!=0)
               end++;            
            int typediff =0, cardnum=0;
            typediff =end-start;
            switch(typediff)
            {
                case 1://���Ż��߶�
                case 2:
                    for(int i=start;i<end;++i)
                    {
                        if(anaArray[i]==1)
                            addtoSingle(i);
                        else if(anaArray[i]==2)
                            addtoDouble(i);
                        else if(anaArray[i]==3)
                            addtoThree(i);
                    }
                    break;
                case 3://���ܳ�������Ŷ
                case 4:
                    if(!setSquDouble(start,end))//3��4����û�ҵ�����
                    {
                         for(int i=start;i<end;++i)
                        {
                            if(anaArray[i]==1)
                                addtoSingle(i);
                            else if(anaArray[i]==2)
                                addtoDouble(i);
                            else if(anaArray[i]==3)
                                addtoThree(i);
                         }
                    }
                    break;
                default://����5���������ˣ���Ҫ�ϸ��ӵĴ���
                    //�����������ȥ��ͷβ��3����������Ҫ
                    while(typediff>5)
                    {
                        if(anaArray[start]==3)
                        {
                            addtoThree(start);
                            start++;
                        }
                        else if(anaArray[end-1]==3)
                        {
                            addtoThree(end-1);
                            end--;
                        }
                        if(typediff !=end-start)
                            typediff =end-start;
                        else
                            break;
                    }
                    for(int i=start;i<end;++i)
                        cardnum += anaArray[i];
                    //����������С��3�ţ�ֱ��ȡ˳��
                    if(cardnum-typediff<3)
                        setSqu(start,end);
                    //�����ƽ϶࣬�ȿ�����û�����ӣ�û�еĻ�ȡ˳��
                    else if(cardnum-typediff<typediff)
                    {
                        if(!setSquDouble(start,end))
                            setSqu(start,end);
                    }
                    //������̫���ˣ��ȿ�����û�����ӣ�û�еĻ�ȡ3��ע�����������˸붴ԭ��
                    else
                       if(!setSquDouble(start,end))
                            for(int i=0;i<typediff;++i)
                                if(anaArray[i]==3)
                                    addtoThree(i);
                    break;
            }
            start =0;
            end =0;
        }
    }
    public boolean setSquDouble(int start, int end)
    {
                int dsStart = start;//������ʼ��
                int dsEnd = start;//���ӽ�����
                boolean hasSquDouble = false;
                for(;dsStart<end-2;dsStart++)
                {
                   if(anaArray[dsStart]<2)
                       continue;
                    for(dsEnd = dsStart;dsEnd<end;dsEnd++)
                    {
                        if(anaArray[dsEnd]<2)
                            break;
                    }                 
                   //�ҵ�����
                   if(dsEnd-dsStart>2)
                   {
                        int squid=0;
                        for(; squid!=3;++squid)
                            if(resultSquDouble[squid][0]==0)
                                break;
                       hasSquDouble = true;
                       for(int i = 0;i<dsEnd-dsStart;++i)
                       {
                           for(int j=0;j<num-1;++j)
                           {
                               if(int2Value(anaCards[j])==dsStart+i+3)
                               {
                                   resultSquDouble[squid][i*2]=anaCards[j];
                                   resultSquDouble[squid][i*2+1]=anaCards[j+1];
                                   anaCards[j] = 0;
                                   anaCards[j+1] = 0;
                                   anaArray[dsStart+i] -=2;
                                   break;
                               }
                           }
                       }
                   }
                }
                return hasSquDouble;
    }
    public void setSqu(int start , int end)
    {
                int squid=0;
                for(; squid!=3;++squid)
                    if(resultSqu[squid][0]==0)
                        break;
                int k=0;
                for(int i = start;i<end;i++)
                {
                    for(int j=0;j<num;++j)
                    {
                        if(int2Value(anaCards[j])==i+3)
                        {
                            resultSqu[squid][k++]=anaCards[j];
                            anaCards[j]=0;
                            anaArray[i]--;
                            break;
                        }
                    }
                }
    }
    public int int2Value(int x)
	{
		if(x==0) return -1;
        if(x==53)return 16;
		if(x==54)return 17;
		int val=0;
		if(x%4==0)
			val=x/4+2;
		else
			val=x/4+3;
		return val;
	}
}
