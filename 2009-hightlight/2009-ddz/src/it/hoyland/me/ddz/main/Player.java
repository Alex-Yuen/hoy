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
public class Player {

    String name;//����
    int ID;//��λ
    int Cards[] = new int [20];//��
    int call;//�з�
    int left;//ʣ������
    int score,callscore;
    boolean iscall;
    boolean isLord;//�Ƿ����
    public Player()
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
