

package it.hoyland.me.ddz.menu;

public interface MenuListener
{

  public void newPage(MenuPage fromPage, MenuPage toPage, boolean back);

  public void itemSelected(MenuPage page, PageItem oldItem, PageItem newItem);

}
