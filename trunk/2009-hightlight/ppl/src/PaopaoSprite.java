import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
/**
* ������
*/
class PaopaoSprite extends Sprite
{
	/**
	* ��ʼ������ͼƬ�����òο���
	*/
	public PaopaoSprite(Image image, int paopaoWidth, int paopaoHeight, int width, int height) 
	{
		super(image, paopaoWidth, paopaoHeight);
		defineReferencePixel(width,height);
	}
}