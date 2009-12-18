

package it.hoyland.me.ddz.menu;

import java.util.Stack;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class Menu implements Runnable
{

  protected MenuPainter painter;
  protected MenuPage startPage;
  protected MenuPage curPage;
  protected boolean inTransition;
  protected boolean transitionBack;
  protected MenuPage transPage;
  protected int frame;
  protected int m_frames;
  protected long frameDelay = 500;
  protected MenuListener listener;
  protected final Object ANIM_LOCK = new Object(); 
  protected volatile boolean running = true;
  protected Canvas canvas; 
  protected Stack history = new Stack();
  protected int width = 0;
  protected int height = 0;
  protected int x = 0;
  protected int y = 0;
  public Menu(MenuPage startPage, Canvas canvas, MenuPainter painter)
  {
    this.startPage = startPage;
    setCanvas(canvas);
    setPainter(painter);
  }
  public void start()
  {
    new Thread(this, "Menu thread").start();
    gotoPage(startPage);
  }
  public void paint(Graphics g)
  {
    if (width == 0 && height == 0 && canvas != null)
    {
      width = canvas.getWidth() - x;
      height = canvas.getHeight() - y;
    }
    if (inTransition && m_frames > 0)
    {
      painter.paintTransition(g, curPage, transPage,
          x, 45, width, height, frame, m_frames, transitionBack);
    }
    else
    {
      painter.paintMenu(g, curPage, x, 45, width, height);
    }
  }
  public void keyPressed(int keyCode)
  {
    MenuPage curPage = getCurrentPage();
    if (curPage != null && canvas != null)
    {
      if (keyCode == -11)
      {
        goBack();
        return;
      }
      int gameCode = canvas.getGameAction(keyCode);
      switch (gameCode)
      {
      	case Canvas.LEFT:
      	  goBack();
      	break;
      	case Canvas.FIRE:
      	case Canvas.RIGHT:
      	  PageItem item = getSelectedItem();
      	  if (gameCode == Canvas.FIRE && item != null && item.getAction() != null)
      	  {
      	    doAction(item.getAction(), curPage, item);
      	  }
      	  if (item != null && item.getSubPage() != null)
      	  {
      	    gotoPage(item.getSubPage());
      	  }
      	break;
      	case Canvas.UP:
      	  setSelectedItemIndex(getSelectedItemIndex()-1);
      	break;
      	case Canvas.DOWN:
      	  setSelectedItemIndex(getSelectedItemIndex()+1);
      	break;
      }
    }
  }
  public void gotoPage(MenuPage newPage)
  {
    MenuPage curPage = null;
    synchronized(ANIM_LOCK)
    {
      curPage = getCurrentPage();
      if (curPage != null)
      {
        history.push(curPage);
      }
    }
    if (listener != null)
    {
      listener.newPage(curPage, newPage, false); 
      listener.itemSelected(newPage, null,
          newPage == null ? null : (newPage.itemAt(newPage.getSelectedIndex()))
      );
    }
    startTransition(curPage, newPage, false);
  }
  public void goBack()
  {
    MenuPage curPage = null;
    MenuPage backPage = null;
    synchronized(ANIM_LOCK)
    {
      curPage = getCurrentPage();
      if (history.size() > 0)
      {
        backPage = (MenuPage)history.pop();
      }
    }
    if (backPage != null)
    {
      if (listener != null)
      {
        listener.newPage(curPage, backPage, true); 
        listener.itemSelected(backPage, null,
            backPage == null ? null : (backPage.itemAt(backPage.getSelectedIndex()))
        );
      }
      startTransition(curPage, backPage, true);
    }
  }
  protected void doAction(final ItemAction action, final MenuPage page, final PageItem item)
  {
    new Thread(new Runnable() {
      public void run()
      {
        try
        {
	        action.itemAction(page, item);
	       //}
	        if (canvas != null)
	        {
	          canvas.repaint();
	        }
        }
        catch (Throwable t)
        {
          System.out.println("Exception in ItemAction");
          t.printStackTrace();
        }
      }
    },"ItemAction runner").start();
  }
  public MenuPage getCurrentPage()
  {
    MenuPage curPage = null;
    synchronized(ANIM_LOCK)
    {
      if (inTransition)
      {
        curPage = transPage;
      }
      else
      {
        curPage = this.curPage;
      }
    }
    return curPage;
  }
  public PageItem getSelectedItem()
  {
    MenuPage curPage = getCurrentPage();
    if (curPage != null)
    {
      return curPage.itemAt(curPage.getSelectedIndex());
    }
    else
    {
      return null;
    }
  }
  public int getSelectedItemIndex()
  {
    MenuPage curPage = getCurrentPage();
    if (curPage != null)
    {
      return curPage.getSelectedIndex();
    }
    else
    {
      return -1;
    }
  }
  public void setSelectedItemIndex(int itemIndex)
  {
    MenuPage curPage = getCurrentPage();
    if (curPage != null)
    {
      PageItem oldItem = curPage.itemAt(curPage.getSelectedIndex());
      curPage.setSelectedIndex(itemIndex);
      PageItem newItem = curPage.itemAt(curPage.getSelectedIndex());
      if (listener != null)
      {
        listener.itemSelected(curPage, oldItem, newItem);
      }
      if (canvas != null)
      {
        canvas.repaint();
      }
    }
  }
  public Canvas getCanvas()
  {
    return canvas;
  }
  public void setCanvas(Canvas canvas)
  {
    this.canvas = canvas;
  }
  public MenuListener getListener()
  {
    return listener;
  }
  public void setListener(MenuListener listener)
  {
    this.listener = listener;
  }
  public MenuPainter getPainter()
  {
    return painter;
  }
  public void setPainter(MenuPainter painter)
  {
    this.painter = painter;
  }
  public long getFrameDelay()
  {
    return frameDelay;
  }
  public int getFrames()
  {
    return m_frames;
  }
  public int getHeight()
  {
    return height;
  }
  public MenuPage getStartPage()
  {
    return startPage;
  }
  public int getWidth()
  {
    return width;
  }
  public int getX()
  {
    return x;
  }
  public int getY()
  {
    return y;
  }
  public void setLocation(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
  public void setDimensions(int width, int height)
  {
    this.width = width;
    this.height = height;
  }
  public void setFrameData(int nbrOfFrames, long frameDelay)
  {
    m_frames = nbrOfFrames;
    this.frameDelay = frameDelay;
    
  }
  protected void startTransition(MenuPage fromPage, MenuPage toPage, boolean back)
  {
    synchronized(ANIM_LOCK)
    {
      curPage = fromPage;
      transPage = toPage;

      if (inTransition)
      {
        ANIM_LOCK.notifyAll();        
      }
      while (inTransition)
      {
        try
        {
          ANIM_LOCK.wait();
        }
        catch (InterruptedException e) {}
      }
      
      transitionBack = back;
      
      inTransition  = true;
      ANIM_LOCK.notifyAll();
    }
  }

  public void run()
  {
    while(running)
    {
      synchronized(ANIM_LOCK)
      {
        while (!inTransition)
        {
          try
          {
            ANIM_LOCK.wait();
          } catch (InterruptedException e) {}
        }
        
        inTransition = true;
        int frames = m_frames;
        long delay = frameDelay;
        MenuPage fromPage = curPage;
        MenuPage toPage = transPage;
        boolean backFlag = transitionBack;
        for (; frame < frames; frame++)
        {
          try
          {
            ANIM_LOCK.wait(delay);
          } catch (InterruptedException e1) {}
          if (canvas != null)
          {
            canvas.repaint();
          }
          if (curPage != fromPage || transPage != toPage)
          {
            break;
          }
        }
        frame = 0;
        curPage = toPage;
        if (canvas != null) canvas.repaint();
        inTransition = false;
        if (canvas != null)
        {
          canvas.repaint();
        }
        ANIM_LOCK.notifyAll();
      }
    }
  }
}
