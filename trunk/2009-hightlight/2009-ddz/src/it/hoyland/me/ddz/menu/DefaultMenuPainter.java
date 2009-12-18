
package it.hoyland.me.ddz.menu;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import javax.microedition.lcdui.Graphics;

public class DefaultMenuPainter implements MenuPainter
{

  protected Font titleFont =
    Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
  protected Font itemFont =
    Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
  protected int titleColor = 0xff0000;
  protected int titleBackColor = 0x880000;
  protected int itemColor = 0x00ffff;
  protected int itemColorDisabled = 0x888888;
  protected int selItemColor = 0xffffff;
  protected int imgPadding = 4;
  public void paintMenu(Graphics g, MenuPage menu,
      int x, int y, int width, int height)
  {
    paintMenu(g, menu, x, y, width, height, false, false);
  }

  protected void paintMenu(Graphics g, MenuPage page,
      int x, int y, int width, int height, boolean to, boolean from)
  {
    if (page == null)
      return;
    int tHeight = titleFont.getHeight();
    int iHeight = itemFont.getHeight();
    paintTitle(g, page, x + width / 2, y, width);
    paintItems(g, page, x, y + getTitleHeight(page) + 8, width, height,
        to, from);
  }

  public void paintTransition(Graphics g, MenuPage fromMenu, MenuPage toMenu,
      int x, int y, int width, int height, int frame, int frames, boolean back)
  {
    double percent = (double) frame / (double) frames;
    int dx = (int) (width * Math.sin(Math.PI / 2d * percent));
    int sign = back ? -1 : 1;
    paintMenu(g, fromMenu, -dx * sign, y, width, height, 
        false, true);
    paintMenu(g, toMenu, sign * (width - dx), y, width, height, 
        true, false);
  }

  protected void paintTitle(Graphics g, MenuPage page, int x, int y, int w)
  {
    String title = page.getTitle();
    Image img = page.getTitleImage();
    int imgW = 0;
    int imgH = 0;
    int txtW = 0;
    int txtH = itemFont.getHeight();
    int layout = page.getLayout();
    boolean imgLeft = layout == MenuPage.LAYOUT_LEFT;

    if (title != null)
    {
      txtW = itemFont.stringWidth(title);
    }
    if (img != null)
    {
      imgW = img.getWidth();
      imgH = img.getHeight();
    }
    
    int totW = imgW + txtW + ((imgW == 0 || txtW == 0) ? 0 : imgPadding);
    int totH = Math.max(imgH, txtH);
    
    if (img != null)
    {
      int dx = imgLeft ? (-totW/2 - imgW) : (txtW/2);
      g.drawImage(img, x + dx, y + (totH - imgH) / 2,
          Graphics.TOP | Graphics.LEFT);
      imgW += imgPadding;
    }
    
    if (title != null)
    {
      int dx = imgLeft ? (imgW/2) : (-imgW/2);
      g.setFont(titleFont);
      g.setColor(titleBackColor);
      g.drawString(title,
          x + dx + 1, y + (totH - txtH) / 2 + 1,
          Graphics.TOP | Graphics.HCENTER);
      g.setColor(titleColor);
      g.drawString(title,
          x + dx, y + (totH - txtH) / 2,
          Graphics.TOP | Graphics.HCENTER);
    }
  }
  
  protected void paintItems(Graphics g, MenuPage page, int x, int y, int w, int h,
      boolean to, boolean from)
  {
    int selectedIndex = page.getSelectedIndex();
    PageItem selectedItem = page.itemAt(selectedIndex);
    PageItem itemBelow = page.itemAt(selectedIndex + 1);
    itemBelow = itemBelow == null ? selectedItem : itemBelow;
    
    int startIndex = selectedIndex < 0 ? 0 : selectedIndex;
    int size = page.size();
    if (selectedItem != null)
    {
      int topIndex = selectedIndex;
      int endIndex = selectedIndex;
      int yLeft = h - y - getItemHeight(selectedItem) - getItemHeight(itemBelow);
      do
      {
        if (endIndex < size - 1)
        {
          endIndex++;
          yLeft -= getItemHeight(page.itemAt(endIndex));
        }
        if (yLeft > 0 && topIndex > 0)
        {
          topIndex--;
          yLeft -= getItemHeight(page.itemAt(topIndex));
        }
      } while (yLeft > 0 && (topIndex > 0 || endIndex < size - 1));

      startIndex = topIndex;
    }
    
    int iMaxW = 0;
    for (int i = startIndex < 0 ? 0 : startIndex; i < size; i++)
    {
      iMaxW = Math.max(iMaxW, getItemWidth(page.itemAt(i)));
    }
    for (int i = startIndex < 0 ? 0 : startIndex; i < size; i++)
    {
      boolean selected = i == selectedIndex;
      PageItem item = page.itemAt(i); 
      paintItem(g, item, selected, x + w / 2, y, w, iMaxW, to, from);
      y += getItemHeight(item);
      if (y >= h)
      {
        break;
      }
    }
  }

  protected void paintItem(Graphics g, PageItem item, boolean selected, int x,
      int y, int w, int iMaxW, boolean to, boolean from)
  {
    String label = item.getLabel();
    boolean enabled = item.isEnabled();
    Image img = item.getImage();
    int imgW = 0;
    int imgH = 0;
    int txtW = 0;
    int txtH = itemFont.getHeight();
    int layout = item.getLayout();
    boolean align =
      layout == PageItem.LAYOUT_ALIGN_LEFT | layout == PageItem.LAYOUT_ALIGN_RIGHT;
    boolean imgLeft =
      layout == PageItem.LAYOUT_CENTERED_LEFT | layout == PageItem.LAYOUT_ALIGN_LEFT;

    if (label != null)
    {
      txtW = itemFont.stringWidth(label);
    }
    if (img != null)
    {
      imgW = img.getWidth();
      imgH = img.getHeight();
    }
    
    int totW = imgW + txtW + ((imgW == 0 || txtW == 0) ? 0 : imgPadding);
    int totH = Math.max(imgH, txtH);
    
    if (img != null)
    {
      int dx = 0;
      if (align)
      {
        dx = imgLeft ? (-iMaxW/2) : (iMaxW/2 - imgW);
      }
      else
      {
        dx = imgLeft ? (-totW/2 - imgW) : (txtW/2);
      }
      g.drawImage(img, x + dx, y + (totH - imgH) / 2,
          Graphics.TOP | Graphics.LEFT);
      imgW += imgPadding;
    }
    
    if (label != null)
    {
      if (!selected)
      {
        g.setColor(enabled ? itemColor : itemColorDisabled);
      }
      else
      {
        g.setColor(selItemColor);
      }
      int dx = 0;
      if (align)
      {
        dx = imgLeft ? ((iMaxW - txtW)/2) : (-(iMaxW - txtW)/2);
      }
      else
      {
        dx = imgLeft ? (imgW/2) : (-imgW/2);
      }
      g.setFont(itemFont);
      g.drawString(label,
          x + dx, y + (totH - txtH) / 2,
          Graphics.TOP | Graphics.HCENTER);
    }
  }
  
  protected int getTitleHeight(MenuPage menu)
  {
    return titleFont.getHeight();
  }

  protected int getItemHeight(PageItem item)
  {
    int fHeight = itemFont.getHeight();
    Image image = item.getImage();
    int iHeight = (image == null ? 0 : image.getHeight());
    return Math.max(fHeight, iHeight);
  }
  protected int getItemWidth(PageItem item)
  {
    Image image = item.getImage();
    String text = item.getLabel();
    int w = (image == null ? 0 : image.getWidth());
    w += (text == null ? 0 : itemFont.stringWidth(text));
    w += (image == null || text == null) ? 0 : imgPadding;
    return w;
  }
}
