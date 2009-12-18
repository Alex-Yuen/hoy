

package it.hoyland.me.ddz.popup;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class Popup implements Runnable
{

  public static final char[][] ALT_OK;
  public static final char[][] ALT_CANCEL;
  public static final char[][] ALT_YES_NO;
  public static final char[][] ALT_OK_CANCEL;
  public static final char[][] ALT_YES_NO_CANCEL;

  static
  {
    ALT_OK = new char[1][];
    ALT_OK[0] = "Ok".toCharArray();
    ALT_CANCEL = new char[1][];
    ALT_CANCEL[0] = "Cancel".toCharArray();
    ALT_YES_NO = new char[2][];
    ALT_YES_NO[0] = "YES".toCharArray();
    ALT_YES_NO[1] = "NO".toCharArray();
    ALT_OK_CANCEL = new char[2][];
    ALT_OK_CANCEL[0] = ALT_OK[0];
    ALT_OK_CANCEL[1] = ALT_CANCEL[0];
    ALT_YES_NO_CANCEL = new char[3][];
    ALT_YES_NO_CANCEL[0] = ALT_YES_NO[0];
    ALT_YES_NO_CANCEL[1] = ALT_YES_NO[1];
    ALT_YES_NO_CANCEL[2] = ALT_CANCEL[0];
  }

  protected char[] text;
  protected byte alternatives;
  protected char[][] altTexts;
  protected byte timeOut;
  protected byte timeOutAlt;
  protected byte curAlt;
  protected PopupListener listener;
  protected volatile boolean active = false;
  protected int w;
  protected int h;
  protected int[][] breakTextData;
  protected int visibleLines;
  protected int curLine;
  protected int maxLine;
  protected int yoffset;
  protected long endTime;
  protected Font font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD,
      Font.SIZE_LARGE);
  protected int fontHeight = font.getHeight();
  protected int borderColor = 0xdd0000;
  protected int backgroundColor = 0xcc440000;
  protected int textColor = 0xffffff;
  protected int alternativeColor = 0xff2200;
  protected int selectedAlternativeColor = 0xffffff;
  protected static int[] rgbData;
  protected static final int OFFSET_POPUP = 8;
  protected static final int OFFSET_TEXT = 2;
  protected static final int OFFSET_ALT = 4;
  protected static final int SB_WIDTH = 5;
  protected static final char[] TEXTBREAKS =
    { ' ', '?', ';', ',', '.', '!',
      ':', '-', '=', '(', ')', '[', ']' };
  protected static final char NEWLINE = '\n';
  public Popup() {}
  public void init(char[] text, char[][] altTexts, byte timeOut,
      byte defaultAlt, byte timeOutAlt, PopupListener listener, int width,
      int height)
  {
    this.text = text;
    this.altTexts = altTexts;
    if (altTexts != null)
    {
      alternatives = (byte) altTexts.length;
    }
    else
    {
      alternatives = 0;
    }
    this.timeOut = timeOut;
    this.timeOutAlt = timeOutAlt;
    this.listener = listener;
    curAlt = defaultAlt;
    w = width - (OFFSET_POPUP << 1);
    h = height - (OFFSET_POPUP << 1);
    active = true;
    if (timeOut > 0)
    {
      endTime = System.currentTimeMillis() + (timeOut * 1000);
    }
    else if (alternatives > 0)
    {
      endTime = 0;
    }
    else
    {
      endTime = System.currentTimeMillis();
    }

    visibleLines =
      Math.max(1, ((h - (OFFSET_TEXT << 1)) / fontHeight) - 1);
    int w = this.w - (OFFSET_TEXT << 1) - (SB_WIDTH << 1);
    curLine = 0;

    breakTextData = breakString(text, w);

    yoffset = 0;
    maxLine = breakTextData.length - visibleLines + 1;
    if (breakTextData.length < visibleLines)
    {
      int newH = breakTextData.length * fontHeight;
      if (alternatives > 0)
        newH += fontHeight;
      newH += OFFSET_TEXT + OFFSET_ALT;
      yoffset = (h - newH) >> 1;
      h = newH;
    }

    if (rgbData == null || rgbData.length != this.w * 8)
    {
      rgbData = new int[this.w * 8];
      for (int i = 0; i < rgbData.length; i++)
      {
        rgbData[i] = backgroundColor;
      }
    }

    new Thread(this, "PopupPoll").start();
  }
  protected int[][] breakString(char[] text, int width)
  {
    int offset = 0;
    int lines = 0;
    int newOffset;

    while (offset < text.length)
    {
      newOffset = 
        findNextBreak(text, offset, text.length - offset, width, font);
      offset = newOffset;
      lines++;
    }

    int[][] indices = new int[lines][2];

    lines = 0;
    offset = 0;
    while (offset < text.length)
    {
      newOffset =
        findNextBreak(text, offset, text.length - offset, width, font);
      indices[lines][0] = offset;
      indices[lines][1] = newOffset - offset;
      lines++;
      offset = newOffset;
    }

    return indices;
  }

  public int findNextBreak(char[] text, int offset, int len, int w, Font f)
  {
    int breakOffset = offset;
    int textW = 0;
    int niceB = -1;
    char c;
    charLoop: while (breakOffset <= offset + len && textW < w)
    {
      if (breakOffset == offset + len)
        c = TEXTBREAKS[0]; 
      else
        c = text[breakOffset];
      if (c == NEWLINE)
      {
        niceB = breakOffset;
        break charLoop;
      }

      breakCharLoop:
      for (int i = TEXTBREAKS.length - 1; i >= 0; i--)
      {
        if (c == TEXTBREAKS[i])
        {
          niceB = breakOffset;
          break breakCharLoop;
        }
      }
      if (breakOffset == offset + len - 1)
      {
        niceB = breakOffset + 1;
      }
      breakOffset++;
      textW += f.charWidth(c);
    }
    if (niceB > offset && niceB < offset + len - 2 && (text[niceB + 1] == ' '))
      return niceB + 2;        
    else if (niceB > offset && niceB < offset + len)
      return niceB + 1;        
    else if (breakOffset > offset + 1)
      return breakOffset - 1; 
    else if (breakOffset == offset)
      return breakOffset + 1; 
    else
      return breakOffset;     
  }
  public void paint(Graphics g)
  {
    if (active)
    {
      for (int y = OFFSET_POPUP + yoffset; y < OFFSET_POPUP + yoffset + h; y += 8)
      {
        g.drawRGB(rgbData, 0, w, OFFSET_POPUP, y, w, Math.min(8,
            OFFSET_POPUP + yoffset + h - y), true);
      }
      g.setColor(borderColor);
      g.drawRect(OFFSET_POPUP, OFFSET_POPUP + yoffset, w, h);
      g.setColor(textColor);
      g.setFont(font);
      int y = OFFSET_POPUP + OFFSET_TEXT + yoffset;
      int maxLine =
        Math.min(curLine + visibleLines, breakTextData.length);
      for (int i = curLine; i < maxLine; i++)
      {
        int offset = breakTextData[i][0];
        int len = breakTextData[i][1];
        if (len == 1 && text[offset] == NEWLINE)
        {
          y += fontHeight;
        }
        else
        {
          if (text[offset + len - 1] == NEWLINE)
          {
            len--;
          }
          g.drawChars(text, offset, len, OFFSET_POPUP + OFFSET_TEXT
              + (w >> 1), y, Graphics.TOP | Graphics.HCENTER);
          y += fontHeight;
        }
      }

      if (visibleLines < breakTextData.length)
      {
        int sbh = visibleLines * fontHeight;   // Scrollbar max height
        int sbstep = ((sbh - 4) << 8) / maxLine; // Scrollbar height * 256
        int sbX =
          OFFSET_POPUP + w - SB_WIDTH - (SB_WIDTH >> 1); // Scrollbar x-coordinate
        g.setColor(textColor);
        g.fillRect(sbX, OFFSET_POPUP + OFFSET_TEXT + ((curLine * sbstep) >> 8),
          SB_WIDTH, 4 + (sbstep >> 8));
      }

      if (alternatives > 0)
      {
        y =
          OFFSET_POPUP + OFFSET_TEXT + h + yoffset - OFFSET_TEXT - fontHeight;
        int dx = (w / (alternatives + 1));
        int x = OFFSET_POPUP + OFFSET_TEXT;
        for (int i = 0; i < alternatives; i++)
        {
          char[] t = altTexts[i];
          x += dx;
          int xx = x - (font.charsWidth(t, 0, t.length) >> 1);
          if (curAlt != i)
          {
            g.setColor(alternativeColor);
            g.drawChars(t, 0, t.length, xx, y, Graphics.TOP | Graphics.LEFT);
          } 
          else
          {
            g.setColor(alternativeColor);
            g.drawChars(t, 0, t.length, xx + 1, y + 1,
                Graphics.TOP | Graphics.LEFT);
            g.setColor(selectedAlternativeColor);
            g.drawChars(t, 0, t.length, xx, y, Graphics.TOP | Graphics.LEFT);
          }
        }
      }
    }
  }

  public void keyPressed(int keyCode, int gameCode)
  {
    if (active)
    {
      if (alternatives < 1)
      {
        active = false;
        if (listener != null)
          listener.selectedChoice(curAlt, false);
      }
      else
      {
        switch (gameCode)
        {
        case Canvas.DOWN:
        {
          curLine++;
          if (curLine >= maxLine)
            curLine = 0;
          break;
        }
        case Canvas.UP:
        {
          if (maxLine > 0)
            curLine--;
          if (curLine < 0)
            curLine = maxLine - 1;
          break;
        }
        case Canvas.RIGHT:
        {
          curAlt++;
          if (curAlt >= alternatives)
            curAlt = 0;
          break;
        }
        case Canvas.LEFT:
        {
          curAlt--;
          if (curAlt < 0)
            curAlt = (byte) (alternatives - 1);
          break;
        }
        case Canvas.FIRE:
        {

          if (curAlt >= 0)
          {
            active = false;
            if (listener != null)
              listener.selectedChoice(curAlt, false);
          }
          break;
        }
        case -11:
        {
          active = false;
          if (listener != null)
            listener.selectedChoice(timeOutAlt, false);
          break;
        }
        }
      }
    }
  }

  /**
   * Disposes all resources held by this popup and closes it.
   */
  public void dispose()
  {
    active = false;
    text = null;
    altTexts = null;
    listener = null;
    breakTextData = null;
    System.gc();
  }

  /**
   * Returns whether this popup is active or not.
   * 
   * @return true if active, false otherwise.
   */
  public boolean isActive()
  {
    return active;
  }

  /**
   * Returns alternative index on timeout
   * 
   * @return timeout alternative
   */
  public byte getTimeOutChoice()
  {
    return timeOutAlt;
  }

  /**
   * Called by framework to check if popup reached its' timeout.
   * 
   * @return true if timeout, false otherwise.
   */
  protected boolean pollTimeout()
  {
    if (active)
    {
      if (endTime > 0 && System.currentTimeMillis() > endTime)
      {
        active = false;
        if (listener != null)
        {
          listener.selectedChoice(timeOutAlt, true);
          return true;
        }
      }
    }
    return false;
  }

  // Runnable impl to poll this popup
  public void run()
  {
    while (isActive())
    {
      // Poll popup timeout
      try
      {
        Thread.sleep(1000);
        pollTimeout();
      } catch (InterruptedException e) {}
    }
  }
}