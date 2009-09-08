package mobi.samov.client.game;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;

/**
 * 字体类
 */

public class NumImgTools {

  //--- 定义变量 ----------------------------------------------------------------
//  private Graphics g; // 图象对象
  public Sprite sprite; // 图形对象
  private int width; // 每个字符的宽度

  //--- 构造器 ----------------------------------------------------------------
  public NumImgTools(Image img, int width, int height) {
    sprite = new Sprite(img, width, height);
    this.width = width;
  }

  //--- 绘制字符 ----------------------------------------------------------------
  public void drawNum(Graphics g,int num, int x, int y) {
    sprite.setPosition(x, y);
    sprite.setFrame(num);
    sprite.paint(g);
  }
  public int  getW()
  {
	  return sprite.getWidth();
  }
  public int getH()
  {
	  return sprite.getHeight();
  }
  //--- 绘制字符串 --------------------------------------------------------------
  public void drawNums(Graphics g,int num, int x, int y, int bit){
    String StrNum = String.valueOf(num);
    //System.out.println(StrNum);
    while (StrNum.length() < bit) {
      StrNum = "0" + StrNum;
     // System.out.println("StrNum in while:"+StrNum);
    }
    for (int i = 0; i < StrNum.length(); i++) {
      drawNum(g,Integer.parseInt(String.valueOf(StrNum.charAt(i))), x + (width+2) * i,
              y);
    }
  }
}
