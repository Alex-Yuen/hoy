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
public class LordPlayer {
    String name;//����
    int ID;//��λ
    int Cards[] = new int [20];//��
    int call=-1;//�з�
    int left;//ʣ������
    int score,callscore=-1;
    boolean iscall;
    boolean isLord;//�Ƿ����
    int sendCards[] = new int[20];
    int sendnum;
    int sendright =-1; //0:������1������ϼң�2��������ϼң��¼ң�
 
    boolean pass = false;
    public LordPlayer()
    {
        name = new String("");
        ID = 0;
       call = 0;
       left = 0;
       isLord = false;
       score = 0;
       iscall = false;
       callscore = -1;

     }
    public void init(int i)
    {
        ID = i;
        switch(ID)
        {
            case 0:
                name = "�־�";
               
                break;
            case 1:
                name = "�żӼ�";
                break;
            case 2:
                name = "������";
               
                break;
            default:
                name = "�żӼ�";
        }

    }
    public void chooseCall()//�����������ȷ���з�
    {
        Random rnd=new Random(System.currentTimeMillis());
        this.callscore =  Math.abs(rnd.nextInt())%4;

    }
    



}
