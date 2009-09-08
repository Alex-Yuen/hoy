/*
 * Fire (Flexible Interface Rendering Engine) is a set of graphics widgets for creating GUIs for j2me applications. 
 * Copyright (C) 2006-2008 Bluevibe (www.bluevibe.net)
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */

package gr.fire.browser.util;

import gr.fire.browser.Browser;
import gr.fire.core.Container;
import gr.fire.ui.ImageComponent;
import gr.fire.util.Log;
import gr.fire.util.StringUtil;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import org.kxml2.io.KXmlParser;

/**
 * A Page is the result of a request on a URL containing an html document. 
 * The Page is produced by the Browser instance and contains the Container with the rendered document
 * as well as metadata about the document such as info on the meta and title tags. 
 * 
 * For all the images in the document the Page instance keeps a cache. For every image that appears in the document, 
 * the Browser will first look in the Page cache for it, before requesting it from its url.  
 * 
 * If the Browser has image loading policy #{@link gr.fire.browser.Browser#LOAD_IMAGES_ASYNC} then the Page will also hold 
 * the vector with the pending images.
 *   
 * @see Browser#loadPage(java.io.InputStream, String)
 * 
 * @author padeler
 *
 */
public class Page
{
	private String url;  // holds the requested url (may be relative or absolut)
	private String absolutUrl; // holds the absolut url of the request.
	
	private String refresh;
	private int refreshSeconds;
	private String pageTitle=null;
	private Container pageContainer;
	
	private Form openForm=null;
	private Vector asyncImageLoadList;
	
	Hashtable imageCache=new Hashtable(); // cache images loaded while rendering a page. Images are often used multiple times in each page

	
	public Page()
	{}
	
	public void registerAsyncImageRequest(ImageComponent cmp,String url)
	{
		if(asyncImageLoadList==null) asyncImageLoadList=new Vector();
		asyncImageLoadList.addElement(new Object[]{cmp,url});
	}
	
	public Page(String url)
	{
		Log.logInfo("Page["+url+"]");
		this.url=url;
	}
	
	public Image getCachedImage(String id)
	{
		return (Image)imageCache.get(id);
	}
	
	public void cacheImage(String id,Image img)
	{
		imageCache.put(id,img);
	}
	
	public void clearImageCache()
	{
		imageCache.clear();
	}
	
	public void parseMetaTag(KXmlParser parser)
	{
		 //http-equiv="refresh" content="5;url=http://example.com/"
		String type = parser.getAttributeValue(null,"http-equiv");
		if(type==null) return;
		type = type.toLowerCase();
		if(type.equals("refresh"))
		{
			String content = parser.getAttributeValue(null,"content");
			if(content==null){
				refreshSeconds=0;
				refresh = url;
				return;
			}
			String urlStr = null,delayStr=null;
			int idx =content.indexOf(";");
			if(idx==-1) delayStr = content;
			else {
				delayStr = content.substring(0,idx).trim();
				urlStr = content.substring(idx+1).trim();
			}
			
			if(urlStr!=null && urlStr.startsWith("url="))
			{
				refresh = StringUtil.proccessUrl(urlStr.substring(4),false);
				if(refresh.startsWith("'")) refresh= refresh.substring(1,refresh.length()-1); // remove single quotes "'"
			}
			
			if(delayStr!=null)try{
				refreshSeconds = Integer.parseInt(delayStr);
			}catch(NumberFormatException e){}
			
			Log.logInfo("MetaRefresh in "+refreshSeconds+" seconds to "+refresh);
		}
	}

	public String getRefresh()
	{
		return refresh;
	}

	public void setRefresh(String refresh)
	{
		this.refresh = refresh;
	}

	public int getRefreshSeconds()
	{
		return refreshSeconds;
	}

	public void setRefreshSeconds(int refreshSeconds)
	{
		this.refreshSeconds = refreshSeconds;
	}

	public String getPageTitle()
	{
		return pageTitle;
	}

	public void setPageTitle(String pageTitle)
	{
		this.pageTitle = pageTitle;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Container getPageContainer()
	{
		return pageContainer;
	}

	public void setPageContainer(Container pageContainer)
	{
		this.pageContainer = pageContainer;
	}

	public Form getOpenForm()
	{
		return openForm;
	}

	public void setOpenForm(Form openForm)
	{
		this.openForm = openForm;
	}

	public String getAbsolutUrl()
	{
		return absolutUrl;
	}

	public void setAbsolutUrl(String absolutUrl)
	{
		this.absolutUrl = absolutUrl;
	}

	public Vector getAsyncImageLoadList()
	{
		return asyncImageLoadList;
	}
}
