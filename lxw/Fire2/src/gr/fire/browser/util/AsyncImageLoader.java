package gr.fire.browser.util;

import gr.fire.browser.Browser;
import gr.fire.ui.ImageComponent;
import gr.fire.util.Log;

import java.util.Vector;

import javax.microedition.lcdui.Image;

/**
 * A utility class to asynchronously load images after a page is rendered.
 * @author padeler
 *
 */
public class AsyncImageLoader extends Thread
{
	private Page page=null;
	private Browser browser=null;
	private String pageUrl;
	private boolean completed;
	public AsyncImageLoader(Browser b, Page p)
	{
		if(b==null || p==null) 
			throw new NullPointerException("Browser and page instance cannot be null");
		this.page=p;
		this.browser = b;
		this.completed=false;
		pageUrl = browser.getHttpClient().getCurrentURL();
	}

	
	public void run()
	{
		Vector asyncImageLoadList = page.getAsyncImageLoadList();
		if(asyncImageLoadList!=null)
		{
			try{
				HttpClient httpClient = browser.getHttpClient();
				for(int i=0;i<asyncImageLoadList.size() && !completed;++i)
				{
					Object[] pair = (Object[])asyncImageLoadList.elementAt(i);
					ImageComponent cmp = (ImageComponent)pair[0];
					String url = (String)pair[1];
					Image img = page.getCachedImage(url);
					if(img==null)
						img = browser.loadImage(url);
					
					if(img!=null)
					{
						page.cacheImage(url,img);
						cmp.setImage(img);
					}
				//	Log.logDebug("AsyncImageLoad["+pageUrl+"]=> "+httpClient.getCurrentURL());
					if(pageUrl!=null && pageUrl.equals(httpClient.getCurrentURL())==false) 
					{// browser changed page. stop loading images for this page.
						Log.logInfo("Stoping asynchronous loading of page images.");
						break;
					}
				}
			}catch (OutOfMemoryError e){
				Log.logWarn("Out of memory error!", e);
				System.gc();
			}finally{
				completed=true;
				page=null;
			}
		}
	}
	
	public void cancel()
	{
		completed=true;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
}
