import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
/**
* 精灵类
*/
class PaopaoSprite extends Sprite
{
	/**
	* 初始化精灵图片并设置参考点
	*/
	public PaopaoSprite(Image image, int paopaoWidth, int paopaoHeight, int width, int height) 
	{
		super(image, paopaoWidth, paopaoHeight);
		defineReferencePixel(width,height);
	}
}