import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 * Title: ���ַ�ҳ��
 * Description: ��������ֻ���Ļ�ϻ���һ��ָ����С�����������������ʵ�ַ�ҳ�Ĺ���
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
     * ���캯��
     * @param Str �����е��ַ���
     * @param LayoutX ���ֶ���X
     * @param LayoutY ���ֶ���Y
     * @param LayoutWidth ���ֿ��
     * @param LayoutHeight ���ָ߶�
     * @param Gap �о�
     * @param font ����
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
            { // layoutWidth-3�е�3Ϊƫ��ֵ
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
     * �����ַ���
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
     * ������
     */
    public void next()
    {
        if (currLine < lineCount - 1)
        {
            currLine++;
        }
    }
    
    /**
     * ��ǰ����
     */
    public void prev()
    {
        if (currLine > 0)
        {
            currLine--;
        }
    }

}