package mobi.samov.client.game;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;

/**
 * ������
 */

public class NumImgTools {

  //--- ������� ----------------------------------------------------------------
//  private Graphics g; // ͼ�����
  public Sprite sprite; // ͼ�ζ���
  private int width; // ÿ���ַ��Ŀ��

  //--- ������ ----------------------------------------------------------------
  public NumImgTools(Image img, int width, int height) {
    sprite = new Sprite(img, width, height);
    this.width = width;
  }

  //--- �����ַ� ----------------------------------------------------------------
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
  //--- �����ַ��� --------------------------------------------------------------
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
