import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 * Title: 文字翻页类
 * Description: 本类会在手机屏幕上绘制一个指定大小的区域，在这个区域内实现翻页的功能
 * Copyright: Copyright (c) 2004
 * Company: Bluecell
 * 
 * @author not attributable
 * @version 1.0
 */

public class StringLayout
{
    public int layoutWidth, layoutHeight, layoutX, layoutY;
    private int lineGap;
    private String text;
    private int fontHeight;
    private int lineCount;
    private Vector line;
    private Font font = null;
    private int currLine = 0;

    /**
     * 构造函数
     * @param Str 布局中的字符串
     * @param LayoutX 布局顶点X
     * @param LayoutY 布局顶点Y
     * @param LayoutWidth 布局宽度
     * @param LayoutHeight 布局高度
     * @param Gap 行距
     * @param font 字体
     */
    public StringLayout(String Str, int LayoutX, int LayoutY, int LayoutWidth, int LayoutHeight, int Gap, Font font)
    {
        text = Str;
        layoutX = LayoutX;
        layoutY = LayoutY;
        layoutWidth = LayoutWidth;
        layoutHeight = LayoutHeight;
        lineGap = Gap;
        this.font = font;
		
        int begin = 0;
        fontHeight = font.getHeight();
        lineCount = 0;
        line = new Vector(5, 2);
        for (int i = 0; i < text.length(); i++)
        {
            char ch = text.charAt(i);
			
            if (font.stringWidth(text.substring(begin, i + 1)) >= layoutWidth || i == text.length() - 1 || ch == '\n')
            { // layoutWidth-3中的3为偏移值
                if(i == text.length() - 1)
                    i++;
                line.addElement(text.substring(begin, i));
                if (ch == '\n')
                    begin = i + 1;
                else
                    begin = i;
                lineCount++;
            }
        }
    }

    /**
     * 画出字符串
     */
    public void draw(Graphics g, int x, int y)
    {
        int i1 = g.getClipX();
        int j1 = g.getClipY();
        int k1 = g.getClipWidth();
        int l1 = g.getClipHeight();

        g.setClip(layoutX, layoutY, layoutWidth, layoutHeight);
        g.setFont(font);
        for (int i = currLine; i < line.size(); i++)
        {
            String s = (String) line.elementAt(i);
            g.drawString(s, x, y + (i - currLine) * (fontHeight + lineGap), Graphics.TOP | Graphics.LEFT);
        }
		
        g.setClip(i1, j1, k1, l1);
    }

    /**
     * 向后滚行
     */
    public void next()
    {
        if (currLine < lineCount - 1)
        {
            currLine++;
        }
    }
    
    /**
     * 向前滚行
     */
    public void prev()
    {
        if (currLine > 0)
        {
            currLine--;
        }
    }

}