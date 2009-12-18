

package it.hoyland.me.ddz.menu;

import java.util.Vector;

import javax.microedition.lcdui.Image;
public class MenuPage
{
  public static final int LAYOUT_LEFT = 0;
  public static final int LAYOUT_RIGHT = 1;

  protected String title;
  protected Image titleImage;
  protected int layout = LAYOUT_LEFT;
  protected Vector items = new Vector();
  protected int m_currentIndex = -1;
  
  public MenuPage(String title, Image titleImage)
  {
    setTitle(title);
    setTitleImage(titleImage);
  }
  
  public void setLayout(int layout)
  {
    this.layout = layout;
  }
  
  public int getLayout()
  {
    return layout;
  }
  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public Image getTitleImage()
  {
    return titleImage;
  }
  public void setTitleImage(Image titleImage)
  {
    titleImage = titleImage;
  }
  public synchronized void addItem(PageItem item)
  {
    items.addElement(item);
    item.addedToPage();
    if (m_currentIndex == -1)
    {
      m_currentIndex = 0;
    }
  }
  public synchronized void insertItemAt(PageItem item,int index)
  {
    items.insertElementAt(item,index) ;
    m_currentIndex = 0;
  }
  public synchronized void removeItemAt(int index)
  {
    items.removeElementAt(index) ;
    m_currentIndex = 0;
  }  
  public synchronized void removeItem(PageItem item)
  {
    items.removeElement(item);
    if (size() == 0)
    {
      m_currentIndex = -1;
    }
  }
  public synchronized void removeItem(int index)
  {
    items.removeElementAt(index);
  }
  public synchronized int size()
  {
    return items.size();
  }
  public synchronized int getSelectedIndex()
  {
    PageItem item = itemAt(m_currentIndex);
    if (item != null && !item.isEnabled())
    {
      setSelectedIndex(m_currentIndex+1);
    }
    return m_currentIndex;
  }
  public synchronized void setSelectedIndex(int index)
  {
    int size = size();
    boolean dirDown = index - m_currentIndex > 0;
    boolean allDisabled = true;
    for (int i = 0; allDisabled && i < size; i++)
    {
      allDisabled = !itemAt(i).isEnabled();
    } 
    if (size == 0 || allDisabled)
    {
      index = -1;
    }
    else
    {
      boolean enabled = true;
      do
      {  
        if (index >= size)
        {
          index = 0;
        }
        else if (index < 0)
        {
          index = size - 1;
        }
        enabled = itemAt(index).isEnabled();
        if (!enabled)
        {
          if (dirDown)
          {
            index++;
          }
          else
          {
            index--;
          }
        }
      } while (!enabled);
    }
    m_currentIndex = index;
  }
  public synchronized PageItem itemAt(int index)
  {
    if (index < 0 || index >= size())
    {
      return null;
    }
    else
    {
      return (PageItem)items.elementAt(index);
    }
  }
  public synchronized int getIndex(PageItem item)
  {
    return items.indexOf(item);
  }
  public synchronized void removeAllItems()
  {
    items.removeAllElements();
  }
}
