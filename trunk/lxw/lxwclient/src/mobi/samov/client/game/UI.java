package mobi.samov.client.game;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class UI 
{
	
	public int FontH;
	public Font ft;
	public int SW,SH;
	
	public int ParMenuLenth;//下拉字符个数
	public int ParMenuIndex;//下拉窗口选择
	public Image img_icon;  //下拉窗口箭头
	
	public int row;
	public int STRY;
	public int RollLen;
	public UI(Font ft,int SW,int SH)
	{
		this.ft = ft;
		FontH = ft.getHeight();
		this.SW = SW;
		this.SH = SH;
	}
	/**
	 * 
	 * @param g
	 * @param str
	 * @param x
	 * @param y
	 * @param Open   //是否打开下拉选项
	 * @param rectH  矩型宽
	 * @param index  
	 */
	public void drawRect(Graphics g,String str[],int x,int y ,boolean Open,int rectH,int index,boolean bool)
	{
//		if(!Open)
//			index = 0;
		ParMenuLenth = str.length;
		int RectW = img_icon.getWidth()+img_icon.getWidth()/2;
		int RectH = rectH;
		g.setColor(255,255,255);
		g.fillRect(x-1, y-1, RectW+ft.stringWidth(str[0]+"一一")+3, RectH+3);
		g.setColor(255,255,255);
		g.fillRect(x, y, RectW, RectH);//第一个矩型框
		g.setColor(0,0,0);
		g.drawRect(x, y, RectW, RectH);//第一个矩型框

		g.setColor(0xfffaac3);
		g.fillRect(x+2, y+2, RectW-3, RectH-3);//第一个框底色
		g.drawImage(img_icon, x+(RectW - img_icon.getWidth())/2, y+(RectH-img_icon.getHeight())/2, 0);
		x+=RectW;
		

		RectW = ft.stringWidth(str[0]+"一一");
		int tempH = RectH;
		if(Open)//画打开下拉框底色
		{
			tempH = RectH*str.length;
			g.setColor(255,255,255);
			g.fillRect(x, y, RectW, tempH);
		}
		if(bool)
			g.setColor(0xfffaac3);
		else
			g.setColor(255,255,255);
	//	g.setColor(0xf4794fc);//画选择框底色
		if(Open)
		{
			g.fillRect(x, y+index*RectH, RectW, RectH);//画选择框
			
		}
		else
			g.fillRect(x, y, RectW, RectH);//画选择框

		g.setColor(0,0,0);
		g.drawRect(x, y, RectW, tempH);//画打开下拉框
		
		if(Open)
		{
			for (int i = 0; i < str.length; i++) 
			{
				
				g.drawString(str[i], x+(RectW-ft.stringWidth(str[i]))/2, 
						y+i*RectH+(RectH - FontH)/2, 0);
			}
		}
		else
		{
			g.drawString(str[index], x+(RectW-ft.stringWidth(str[index]))/2, 
					y+(RectH - FontH)/2, 0);
		}
	}
	/**
	 * 画右下角选项
	 */
	public void DrawOption(Graphics g,int OptionIndex,String str[])
	{
		int StrW = ft.stringWidth("一一一一");
		int W = StrW+ft.stringWidth("一一");//框的宽
		int H = str.length*(FontH+2);//框的高  2==间隔
		int y = SH-Platform.H2-H;//框的Y坐标
		int H2 = H/str.length;//每行字高
//		OptionNum = str.length-1;
		g.setColor(255,255,255);  
	//	g.fillRoundRect(1, y, W, H,5,5);
		g.fillRect(1, y-1, W, H+3);
		g.setColor(0xffd7c9a);
		g.fillRect(4, y+OptionIndex*(H/str.length)+2, W-6, H2-3);//+2 -3修整选项框宽度
		g.setColor(255,168,188);
	//	g.drawRoundRect(2, y+1, W-3, H-3,5,5);
		g.drawRect(2, y, W-3, H);
		
		for (int i = 0; i < str.length; i++) 
		{
			if(OptionIndex==i)
			{
				g.setColor(255,255,255);
			}
			else
				g.setColor(0,0,0); 
			
			g.drawString(str[i], (W-StrW)/2, y+i*H2+(H2-FontH)/2+1, 0);
		}
	}
	/**
	 * 画右边滚动条
	 * RectH 滚动条高
	 * len   相当于小RECTH
	 * 
	 */
	public void DrawRoll(Graphics g,int y,int RectH,int len,int index,int SW,int SH)
	{
		int RectW = 6;
		g.setColor(255,255,255);
		g.fillRect(SW-RectW, y, RectW, RectH);
		g.setColor(0xffaac3);
		g.fillRect(SW-RectW+1, y+index*(RectH/len), RectW-2, RectH/len);
	}
	/**
	 * 5 RectW   要在多宽的框画
	 * 6 RectH   要在多高的框画
	 * 7 limitW   限定画多宽就换行
	 * 8 YH       每行间隔
	 * 9 HelepIndex  偏差
	 * 10 bool   是否画白色底
	 */
	public void DrawStr(Graphics g,String str,int x,int y,int RectW,int RectH,int limitW,int YH,int HelepIndex,boolean bool)
	{
		row = 1;
		g.setColor(0,0,0);
		String temp = "";
		int FontW=0;
		int tempX = (RectW-limitW)/2;//将一行字符居中
		int StrY = y;
		boolean tempBool = false;
		RectH+=y;
		if(bool)
			for (int i = 0; i < str.length(); i++) 
			{
				temp = str.substring(i,i+1);
				FontW = ft.stringWidth(temp);
				tempX+=FontW;
				if(tempX>limitW || temp.equals("\n"))
				{
					StrY+=YH;
					tempX = (RectW-limitW)/2;
					row++;
				}
			}
		
		RectH*=row;
	//	temp = "";
		FontW=0;
		tempX = (RectW-limitW)/2;
		StrY = y;
		//-------------------------
		if(bool)//是否画白色底
			for (int i = 0; i < row; i++)//画白色背景
			{
				g.setColor(255,255,255);
				g.fillRect(x, y+i*YH, RectW+1, YH);
				g.setColor(0,0,0);
			}
		RollLen = 0;
		for (int i = 0; i < str.length(); i++) 
		{
			temp = str.substring(i,i+1);				
			tempX+=FontW;
			FontW = ft.stringWidth(temp);
			if(tempX>limitW || temp.equals("\n"))
			{
				StrY+=YH;
				tempX = (RectW-limitW)/2;
				RollLen++;
				tempBool = true;
				if(i >= str.length()-10 && !bool)
				{
					STRY = StrY;
				}
			}
			if(StrY+HelepIndex<=RectH && StrY+HelepIndex>=y)
			{
				g.drawString(temp, x+tempX, StrY+HelepIndex+2, 0);
			}
		}
	}
	
	public void DrawRect(Graphics g,int x,int y,int W,int H,int color1,int color2,boolean bool)
	{
		g.setColor(255,255,255);
		g.fillRect(x, y, W, H);
		g.setColor(0,0,0);
		g.drawRect(x, y, W, H);
		if(bool)
		{
			g.setColor(0xfffa8bc);
			g.fillRect(x+2, y+2, W-3, H-3);
		}
		
	}
}


