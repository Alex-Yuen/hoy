
package it.hoyland.me.ddz.menu;

import javax.microedition.lcdui.Image;


public abstract class BinaryPageItem extends PageItem
	implements ItemAction
{
  protected Image imgTrue;
  protected Image imgFalse;
  protected ItemAction dispatchAction;
  public BinaryPageItem(String label, Image imageTrue, Image imageFalse,
      MenuPage subPage)
  {
    super(label, null, null, subPage);
    this.imgTrue = imageTrue;
    this.imgFalse = imageFalse;
    setAction(this);
    setLayout(LAYOUT_ALIGN_RIGHT);
  }
  public BinaryPageItem(String label, Image imageTrue, Image imageFalse,
      MenuPage subPage, ItemAction dispatchAction, int id)
  {
    super(label, null, null, subPage, id);
    this.imgTrue = imageTrue;
    this.imgFalse = imageFalse;
    dispatchAction = dispatchAction;
    setAction(this);
    setLayout(LAYOUT_ALIGN_RIGHT);
  }
  public void addedToPage()
  {
  	
    setImage(getBoolean() ? imgTrue: imgFalse);
  }
  public void itemAction(MenuPage page, PageItem item)
  {
    boolean b = !getBoolean();
    setBoolean(b);
    setImage(b ? imgTrue: imgFalse);
    if (dispatchAction != null)
    {
      dispatchAction.itemAction(page, item);
    }
  }
  public abstract boolean getBoolean();
  public abstract void setBoolean(boolean value);
 
}
