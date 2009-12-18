package it.hoyland.me.ddz.main;





import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

public class SoftButtonControl
{
  protected Displayable displayable;
  protected Command leftCommand;
  protected Command rightCommand;
  protected Command backCommand;
  protected CommandListener listener;
  protected boolean leftCommandEnabled = true;
  protected boolean rightCommandEnabled = true;
  protected char[] left;
  protected char[] right;
  protected Font font;
  protected int maxWidth;
  
  protected static final int COL_BORDER = 0x88440000;
  protected static final int COL_BG = 0xbb440000;
  protected static final int COL_COMMAND = 0xffffff;
  protected static final int COL_DISABLED_COMMAND = 0x888888;
  protected int[] transpBuf;
  
  public void init(Displayable d, Font font, Command leftCommand, Command rightCommand)
  {
    displayable = d;
    this.font = font;
    setRightCommand(rightCommand);
    setLeftCommand(leftCommand);
  }
  
  public CommandListener getCommandListener()
  {
    return listener;
  }
  
  public void setCommandListener(CommandListener listener)
  {
    this.listener = listener;
    
  }
  
  public Command getLeftCommand()
  {
    return leftCommand;
  }
  
  public void setLeftCommand(Command c)
  {
    if (backCommand == leftCommand)
    {
      backCommand = null;
    }
    leftCommand = c;
    if (leftCommand != null)
    {
      left = leftCommand.getLabel().toCharArray();
      if (leftCommand.getCommandType() == Command.BACK)
      {
        backCommand = c;
      }
      enable(c, true);
    }
    calcWidth();
  }
  
  public Command getRightCommand()
  {
    return rightCommand;
  }
  
  public void setRightCommand(Command c)
  {
    if (backCommand == rightCommand)
    {
      backCommand = null;
    }
    rightCommand = c;
    if (rightCommand != null)
    {
      right = rightCommand.getLabel().toCharArray();
      if (rightCommand.getCommandType() == Command.BACK)
      {
        backCommand = c;
      }
      enable(c, true);
    }
    calcWidth();
  }
  
  public void enable(Command c, boolean enable)
  {
    if (c == leftCommand)
    {
      leftCommandEnabled = enable;
    }
    if (c == rightCommand)
    {
      rightCommandEnabled = enable;
    }
  }
  
  public void keyPressed(int keyCode)
  {
    if (keyCode == -6)
    {
      if (leftCommand != null && listener != null && leftCommandEnabled)
      {
        listener.commandAction(leftCommand, displayable);
      }
    }
    else if (keyCode == -7)
    {

      if (rightCommand != null && listener != null && rightCommandEnabled)
      {
        listener.commandAction(rightCommand, displayable);
      }
    }
    else if (keyCode == -11)
    {
      if (backCommand != null && listener != null && 
          (backCommand == leftCommand && leftCommandEnabled ||
           backCommand == rightCommand && rightCommandEnabled))
      {
        listener.commandAction(backCommand, displayable);
      }
    }
  }
  
  public void paint(Graphics g)
  {
    int w = displayable.getWidth();
    int h = displayable.getHeight();
    g.setFont(font);
    if (leftCommand != null)
    {
      paintCommand(g, left, w, h, leftCommandEnabled, false);
    }
    if (rightCommand != null)
    {
      paintCommand(g, right, w, h, rightCommandEnabled, true);
    }
  }
  
  protected void calcWidth()
  {
    int twl = left == null ? 0: font.charsWidth(left,0,left.length);
    int twr = right == null ? 0: font.charsWidth(right,0,right.length);
    int mw = Math.max(twr, twl);
    if (maxWidth != mw)
    {
      maxWidth = mw;
      recalcTransparantBuffer();
    }
    
  }
  
  protected void paintCommand(Graphics g, char[] text,
      int w, int h, boolean enabled, boolean rightAlign)
  {
    int textH = font.getHeight();
    int x = 0;
    if (rightAlign)
    {
      x = w - maxWidth - 2;
    }
    g.drawRGB(transpBuf, 0, maxWidth+2,
        x, h-textH-1, maxWidth+2, textH+1, true);
    g.setColor(enabled ? COL_COMMAND : COL_DISABLED_COMMAND);
    x += maxWidth / 2;
    x += rightAlign ? 2 : 1;
    g.drawChars(text,0,text.length, x, h, Graphics.BOTTOM | Graphics.HCENTER);
  }
  
  protected void recalcTransparantBuffer()
  {
    transpBuf = new int[(maxWidth+2) * (font.getHeight()+1)];
    for (int i = maxWidth+2; i < transpBuf.length; i++)
    {
      transpBuf[i] = COL_BG;
    }
    for (int i = 0; i < maxWidth+2; i++)
    {
      transpBuf[i] = COL_BORDER;
    }
    for (int i = 0; i < font.getHeight()+1; i++)
    {
      transpBuf[(i *  (maxWidth+2))] = COL_BORDER;
      transpBuf[(i *  (maxWidth+2)) +  maxWidth+1] = COL_BORDER;
    }
  }
}
