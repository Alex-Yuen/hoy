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
    String name;//姓名
    int ID;//座位
    int Cards[] = new int [20];//牌
    int call=-1;//叫分
    int left;//剩余牌数
    int score,callscore=-1;
    boolean iscall;
    boolean isLord;//是否地主
    int sendCards[] = new int[20];
    int sendnum;
    int sendright =-1; //0:随便出，1：大过上家，2：大过上上家（下家）
 
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
                name = "林靖";
               
                break;
            case 1:
                name = "张加佳";
                break;
            case 2:
                name = "许欣欣";
               
                break;
            default:
                name = "张加佳";
        }

    }
    public void chooseCall()//根据手里的牌确定叫分
    {
        Random rnd=new Random(System.currentTimeMillis());
        this.callscore =  Math.abs(rnd.nextInt())%4;

    }
    



}
