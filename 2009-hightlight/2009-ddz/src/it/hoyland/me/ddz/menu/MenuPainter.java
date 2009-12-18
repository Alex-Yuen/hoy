

package it.hoyland.me.ddz.menu;

import javax.microedition.lcdui.Graphics;

public interface MenuPainter
{

  public void paintMenu(Graphics g, MenuPage menu,
      int x, int y, int width, int height);

  public void paintTransition(Graphics g, MenuPage fromMenu, MenuPage toMenu,
      int x, int y, int width, int height, int frame, int frames, boolean back);
}
