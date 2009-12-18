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

    String name;//姓名
    int ID;//座位
    int Cards[] = new int [20];//牌
    int call;//叫分
    int left;//剩余牌数
    int score,callscore;
    boolean iscall;
    boolean isLord;//是否地主
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
