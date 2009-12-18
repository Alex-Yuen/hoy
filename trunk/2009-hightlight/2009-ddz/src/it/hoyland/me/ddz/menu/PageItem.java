
package it.hoyland.me.ddz.menu;

import java.util.Hashtable;

import javax.microedition.lcdui.Image;

public class PageItem
{

  public static final int LAYOUT_CENTERED_LEFT = 0;
  public static final int LAYOUT_CENTERED_RIGHT = 1;
  public static final int LAYOUT_ALIGN_LEFT = 2;
  public static final int LAYOUT_ALIGN_RIGHT = 3;
  
  protected String label;
  protected ItemAction m_action;
  protected MenuPage m_subPage;
  protected Image m_image;
  protected int m_layout = LAYOUT_CENTERED_LEFT;
  protected boolean m_enabled = true;
  protected Hashtable m_props = null;
  protected int m_id = Integer.MIN_VALUE;
  public PageItem(String label, Image image, ItemAction action, MenuPage subPage)
  {
    setLabel(label);
    setImage(image);
    setAction(action);
    setSubPage(subPage);
  }
  public PageItem(String label, Image image, ItemAction action, MenuPage subPage, int id)
  {
    this(label, image, action, subPage);
    m_id = id;
  }
 
  public int getId()
  {
    return m_id;
  }
   public void addedToPage() {}
  
  public String getLabel()
  {
    return label;
  }
  public void setLabel(String label)
  {
    this.label = label;
  }
  public MenuPage getSubPage()
  {
    return m_subPage;
  }
  public void setSubPage(MenuPage page)
  {
    m_subPage = page;
  }
  public ItemAction getAction()
  {
    return m_action;
  }
  public void setAction(ItemAction action)
  {
    m_action = action;
  }
  public boolean isEnabled()
  {
    return m_enabled;
  }
  public void setEnabled(boolean enabled)
  {
    m_enabled = enabled;
  }
  public Image getImage()
  {
    return m_image;
  }
  public void setImage(Image image)
  {
    m_image = image;
  }
  public void setLayout(int layout)
  {
    m_layout = layout;
  }
  public int getLayout()
  {
    return m_layout;
  }
  public Object getProperty(int key)
  {
    if (m_props == null)
      return null;
    else
      return m_props.get(new Integer(key));
  }
  public void setProperty(int key, Object value)
  {
    if (m_props == null)
    {
      m_props = new Hashtable();
    }
    if (value == null)
    {
      m_props.remove(new Integer(key));
    }
    else
    {
      m_props.put(new Integer(key), value);
    }
  }
}
