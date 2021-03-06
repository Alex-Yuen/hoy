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

/**
 * 
 */
package gr.fire.browser;

import gr.fire.browser.util.AsyncImageLoader;
import gr.fire.browser.util.HttpClient;
import gr.fire.browser.util.Page;
import gr.fire.browser.util.PageListener;
import gr.fire.browser.util.Request;
import gr.fire.core.CommandListener;
import gr.fire.core.Component;
import gr.fire.core.Container;
import gr.fire.core.FireScreen;
import gr.fire.core.Panel;
import gr.fire.core.Theme;
import gr.fire.ui.Alert;
import gr.fire.ui.ProgressbarAnimation;
import gr.fire.ui.TransitionAnimation;
import gr.fire.util.FireConnector;
import gr.fire.util.Lang;
import gr.fire.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import org.kxml2.io.KXmlParser;

/**
 * The Browser parses XHTML from a given stream and renders it.
 * It has some basic rendering rules that are choosen to improve the 
 * readability and usability of a page when rendered for a small screen. <br/>
 * 
 * * The default width of the browser is the width of the screen.<br/>
 *    
 * * TextPrimitive width is set as the width of the Browser unless noted otherwise 
 *   by the style of the tag.<br/>
 *   
 * * TextPrimitive height is the height of the text calculated using the width 
 *   (according to the rules above) and the font of the text.  <br/>
 *   
 * * ImagePrimitive width is the width of the image unless noted otherwise by the style of the tag. <br/>
 * 
 * @author padeler
 */
public class Browser implements CommandListener, PageListener
{
	
	/**
	 * Flag for the imageLoadingPolicy field. 
	 * No images are loaded.
	 */
	public static final byte NO_IMAGES=0x00; 
	/**
	 * Flag for the imageLoadingPolicy field. 
	 * Load images imediatelly
	 */
	public static final byte LOAD_IMAGES=0x01; 
	
	/**
	 * Flag for the imageLoadingPolicy field. This is the default browser behaivior.
	 * Load images in a seperate thread after the full parsing of the page. 
	 * The browser will return the Page object and start a thread to load the rest of the images.
	 * This will apply only for images that the width and height attributes are set.
	 * Images that do not have their width and height attributes set, will be loaded imediatelly.
	 */
	public static final byte LOAD_IMAGES_ASYNC=0x02;

	/**
	 * The listener that will receive the events generated by the page rendered by this browser (i.e. link clicks etc) 
	 * The listener will also receive the submit events generated by forms in the rendered pages. 
	 * If null, then the form handles the submit, with the default way (calls browser.displayPage()) 
	 */
	CommandListener listener = this; 
	PageListener pageListener = this;
	
	/**
	 * If this field is set to false, then the browser will not request the images inside each page
	 */
	byte imageLoadingPolicy = LOAD_IMAGES_ASYNC;  
	
	/**
	 * The HttpClient used to request resources.
	 */
	HttpClient httpClient;
	
	private Hashtable knownTags = new Hashtable();
	
	private int viewportWidth=-1; 
	
	private boolean showLoadingGauge=true;
	
	/* ****** HTML Parsing support variables ******* */  
	private Vector tagStack = new Vector();
	private ProgressbarAnimation gauge=null;
	private AsyncImageLoader asyncImageLoader=null;
	private boolean rendering=false;
	
	/**
	 * Constructor of a Browser instance with the default HttpClient.
	 * The CommandListener and PageListener are set to this new Browser instance.
	 * @see HttpClient
	 * @see PageListener
	 * @see CommandListener
	 */
	public Browser()
	{
		this(new HttpClient(new FireConnector()));
	}
	
	/**
	 * Constructor of a Browser instance with the supplied httpclient. All other parameters are set to the default
	 * @see #Browser()
	 * @param httpClient
	 */
	public Browser(HttpClient httpClient)
	{
		this.httpClient = httpClient; 
		
		{
			Class ie = new InlineTag().getClass();
			
			registerTag(InlineTag.TAG_A,ie);
			registerTag(InlineTag.TAG_B,ie);
			registerTag(InlineTag.TAG_BR,ie);
			registerTag(InlineTag.TAG_EM,ie);
			registerTag(InlineTag.TAG_I,ie);
			registerTag(InlineTag.TAG_IMG,ie);
			registerTag(InlineTag.TAG_SPAN,ie);
			registerTag(InlineTag.TAG_STRONG,ie);
			registerTag(InlineTag.TAG_BIG,ie);
			registerTag(InlineTag.TAG_SMALL,ie);
			registerTag(InlineTag.TAG_TT,ie);
			registerTag(InlineTag.TAG_U,ie);
			registerTag(InlineTag.TAG_TD,ie);
			registerTag(InlineTag.TAG_INPUT,ie);
			registerTag(InlineTag.TAG_BUTTON,ie);
			registerTag(InlineTag.TAG_TEXTAREA,ie);
			registerTag(InlineTag.TAG_CENTER,ie);
			registerTag(InlineTag.TAG_LABEL,ie);
			registerTag(InlineTag.TAG_OPTION,ie);
			registerTag(InlineTag.TAG_SELECT,ie);
		}		
		
		{
			Class be = new BlockTag().getClass();
		
			registerTag(BlockTag.TAG_P,be);		
			registerTag(BlockTag.TAG_BODY,be);		
			registerTag(BlockTag.TAG_TABLE,be);	
			registerTag(BlockTag.TAG_TR,be);
			registerTag(BlockTag.TAG_DIV,be);		
			registerTag(BlockTag.TAG_TITLE,be);		
			registerTag(BlockTag.TAG_META,be);		
			registerTag(BlockTag.TAG_STYLE,be);
			registerTag(BlockTag.TAG_SCRIPT,be);
			registerTag(BlockTag.TAG_H1,be);		
			registerTag(BlockTag.TAG_H2,be);		
			registerTag(BlockTag.TAG_H3,be);		
			registerTag(BlockTag.TAG_H4,be);		
			registerTag(BlockTag.TAG_H5,be);		
			registerTag(BlockTag.TAG_H6,be);
			registerTag(BlockTag.TAG_HR,be);
			registerTag(BlockTag.TAG_FORM,be);
		}		
		
		{
			Class le = new ListBlockTag().getClass();
			registerTag(ListBlockTag.TAG_UL,le);		
			registerTag(ListBlockTag.TAG_LI,le);		
			registerTag(ListBlockTag.TAG_OL,le);		
			registerTag(ListBlockTag.TAG_DL,le);		
			registerTag(ListBlockTag.TAG_DT,le);		
			registerTag(ListBlockTag.TAG_DD,le);		
		}
	}
	
	/**
	 * Registers an XML tag to be handled by an instance of the given class. The class MUST be a subclass of Tag
	 * @see HtmlUtil
	 * @param name name of the tag
	 * @param cl the class that will handle the action
	 */
	public void registerTag(String name,Class cl) 
	{
		if(name!=null && cl!=null)
		{
			Tag t = null;
			try
			{
				t = (Tag)cl.newInstance();
			} catch (Exception e)
			{
				Log.logError("Failed to register class for tag "+name+". ",e);
				throw new IllegalArgumentException("Class must be an instantiable subclass of Tag. "+e.getMessage());
			}
			if(t!=null){
				knownTags.put(name,cl);
			}else{
				throw new NullPointerException("Tag name and class cannot be null");
			}
		}
		else throw new NullPointerException("Tag name and class cannot be null");
	}

	/**
	 * Loads the page from the given URL using the supplied method and reqest parameters and data. 
	 * This method will use the supplied HttpClient {@link #httpClient} to make the request and then render the page.<br/> 
	 * 
	 * The resulting will be added to added to a Page instance and returned to the caller.
	 * It will not handle "meta" tag information, but will return them inside the Page instance. <br/>
	 * 
	 * This method is <b>synchronized</b> on the browser instance.
	 * 
	 * @param url 
	 * @param method The method can be HttpConnection.GET or HttpConnection.POST
	 * @param requestParameters params for the http request header 
	 * @param data if the method is HttpConnection.POST the post data if any must be in this byte array 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	public synchronized Page loadPage(String url,String method,Hashtable requestParameters,byte []data) throws UnsupportedEncodingException,IOException,Exception
	{
		rendering=true; // rendering started

		if(url.startsWith("ttp://") || url.startsWith("ttps://"))
		{
			url = "h"+url;
			Log.logWarn("Malformed url resolved to: "+url);
		}
		
		Page page = new Page(url);

		/* ********** Notify listener *************** */
		pageListener.pageLoadProgress(url,PAGE_LOAD_START,10); // must make a call with PAGE_LOAD_START
		pageListener.pageLoadProgress(url,PAGE_LOAD_LOADING_DATA,20);
		
		/* ************************** Request the resource **************************** */
		Request currentRequest = null;
		try{
			currentRequest = httpClient.requestResource(url,method,requestParameters,data,true);

//			StringBuffer sb = new StringBuffer();
//			byte[] buffer = new byte[512];
//			byte[] tmpBuffer = null;
//			int b = 0;
//			while(true){   
//				b = currentRequest.getInputStream().read(buffer);
//				if(b<=0){   
//			          break;
//				}
//				tmpBuffer = new byte[b];
//				for(int i=0;i<b;i++){
//					tmpBuffer[i] = buffer[i];
//				}
//				sb.append(new String(tmpBuffer, "UTF-8"));
//			}
			
//			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(currentRequest.getInputStream()));
//			
//			byte[] bx = new byte[currentRequest.getInputStream().available()];
			byte[] bx = new byte[1024*10];
			currentRequest.getInputStream().read(bx);
//			System.out.println(new String(b, currentRequest.getEncoding()));
			System.gc();
			//String s = sb.toString();
			String s = new String(bx, "UTF-8");
			System.gc();
			String[][] symbols = {{"<wml", "<html"}, {"</wml", "</html"}, {"<card", "<body"}, {"</card", "</body"}};
			for(int i=0; i<symbols.length; i++){
				while(s.toLowerCase().indexOf(symbols[i][0])!=-1){
					s = s.substring(0, s.toLowerCase().indexOf(symbols[i][0])) + symbols[i][1] + s.substring(s.toLowerCase().indexOf(symbols[i][0])+symbols[i][0].length());  
				}
			}
			
			//System.out.println(s);
			System.gc();
			//String k = new String(s.getBytes("UTF-8"), "UTF-8");
			//System.out.println(s);
//			String sx = "<html><body>你好</body></html>";
			//System.out.println(s);
			//System.out.println(new String(s.getBytes("UTF-8"), "GBK"));
			InputStream is = new ByteArrayInputStream(s.getBytes("UTF-8"));
			currentRequest.setInputStream(is);
//			
			
			//currentRequest = httpClient.requestResource(url,method,requestParameters,data,true);
			
			if(currentRequest==null) {
				Log.logWarn("Failed to get resource from "+url);
				return null;
			}
			page.setAbsolutUrl(currentRequest.getURL());
			InputStream in = currentRequest.getInputStream();
			
			if (in == null)
			{
				Log.logWarn("Failed to read data from "+url);
				return null;
			}
			
			Log.logDebug("Base URL is: "+currentRequest.getBaseURL());
			String encoding= currentRequest.getEncoding();
	
			return loadPageFromStream(page,in,encoding);
		}catch(Exception e){
			Log.logError("Failed to request page "+url+".",e);
			throw e;
		}finally{
			if(currentRequest!=null){
				try{
					currentRequest.close();
				}catch(IOException ex){
					Log.logWarn("Connection not closed!", ex);
				}
				currentRequest=null;
			}
			pageListener.pageLoadProgress(url,PAGE_LOAD_END,100);
		}
	}
	
	/**
	 * Loads a page from the given InputStream using the given encoding.
	 * 
	 * This method is <b>synchronized</b> on the browser instance.
	 * 
	 * @param in
	 * @param encoding
	 * @return A Page instance containing the result of the request
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws Exception
	 */
	public synchronized Page loadPage(InputStream in,String encoding) throws UnsupportedEncodingException,IOException,Exception
	{
		rendering=true; // rendering started

		Page page = new Page();
		/* ********** Notify the page listener *************** */
		pageListener.pageLoadProgress("Loading...",PAGE_LOAD_START,0); // need to make a call with PAGE_LOAD_START
		pageListener.pageLoadProgress("Loading...",PAGE_LOAD_LOADING_DATA,10);
		
		try{
			return loadPageFromStream(page,in,encoding);
		}catch(Exception e){
			Log.logError("Failed to request page from stream.",e);
			throw e;
		}finally{
			pageListener.pageLoadProgress("Done...",PAGE_LOAD_END,100);
		}
	}
	
	/*
	 * The main loop of the Browser module. This uses the XmlPullParser to parse the xml from the inputstream and 
	 * then iterates through the tags of the document. Each known tag is handled by the class that is registered to handle it
	 * using the registerTag method.
	 */
	private Page loadPageFromStream(Page page,InputStream in,String encoding) throws UnsupportedEncodingException,IOException,Exception
	{
		/* ******************************** clean old page stuff here **************************** */
		tagStack.removeAllElements();
		if(asyncImageLoader!=null)
		{
			asyncImageLoader.cancel();// stop current page images from loading.
			asyncImageLoader=null;
		}
		
		pageListener.pageLoadProgress(page.getUrl(),PAGE_LOAD_PARSING_DATA,50);
		
		InputStreamReader reader=null;
		try{
			Log.logDebug("Using Encoding: "+encoding);
			reader = new InputStreamReader(in, encoding);
			
			//byte[] buf = new byte[2048];
			//in.mark(0);
			//in.read(buf);
			//reader.reset();
			//String s = new String(buf, encoding);
			//System.out.println(s);
			
			KXmlParser parser = new KXmlParser();
			parser.setInput(reader);
			parser.setFeature(org.xmlpull.v1.XmlPullParser.FEATURE_RELAXED,true);

			int type=-1,oldType=-1;
		
			Theme th = FireScreen.getTheme();
			Tag rootTag = new InlineTag();
			rootTag.setForegroundColor(th.getIntProperty("xhtml.fg.color"));
			rootTag.setBackgroundColor(th.getIntProperty("xhtml.bg.color"));
			rootTag.setFont(th.getFontProperty("xhtml.font"));
			
			/* ********** Main XML parsing loop **************** */ 
			while (rendering)
			{
				if(type==oldType) type= parser.next(); // only progress parser if a tag didnt already call parser.next()
				oldType = type; // some tags call parser.next(), old type helps keep track if parser.next() was called.
				
				if(type==KXmlParser.START_TAG) /* **** Handle Opening TAGs ***** */
				{
					String name = parser.getName().toLowerCase();;
					Class tagClass = (Class)knownTags.get(name);
					if(tagClass!=null)
					{
						try
						{
							Tag t = (Tag)tagClass.newInstance();
							
							if(tagStack.size()==0)
								t.inheritStyle(rootTag); // inherit basic style information.
							
							t.handleTagStart(this,page,parser);
							pushTag(t);
						}catch(InstantiationException e)
						{
							Log.logError("Failed to instantiate a Tag class for tag name "+name+".",e);
						} catch (Exception e)
						{
							Log.logError("Exception while handling tag start "+name,e);
						}
					}
					else Log.logWarn("Unknown Opening TAG "+name);
				}
				else if(type==KXmlParser.END_TAG) /* **** Handle Closing TAGs ***** */
				{
					String name = parser.getName().toLowerCase();;
					Tag t = (Tag)topTag();
					if(t!=null && name.equals(t.getName()))
					{
						t.handleTagEnd(this,page,parser);
						popTag();
					}
					else Log.logWarn("Unknown Closing TAG "+name+" expected "+(t==null?"none":t.getName()));
				}
				else if(type==KXmlParser.TEXT) /* **** Handle Text inside a TAG ***** */
				{
					Tag top = (Tag)topTag();
					
					String txt = parser.getText();
					if(top!=null && txt.length()>0)
					{
						top.handleText(top,txt);
					}
				}
				else if(type==KXmlParser.END_DOCUMENT)
				{
					Log.logDebug("=>End Of Document<=");
					break; // parsing completed.
				}
				else /* **** Default action, just log the unknown type and continue **** */
				{ 
					Log.logWarn("Unknown tag "+parser.getName() +" type " + type);
				}
				
				type  = parser.getEventType(); // get type again since some tags call parser.next() 
			}
			
			pageListener.pageLoadProgress(page.getUrl(),PAGE_LOAD_PARSING_DATA,100);			
			
			if(rendering && imageLoadingPolicy==LOAD_IMAGES_ASYNC)
			{ // start async loading only if rendering was not stoped using Browser.stop()
				asyncImageLoader = new AsyncImageLoader(this,page);
				asyncImageLoader.start();
			}
			
		}finally{
			rendering=false;
			try{
				if(reader!=null) reader.close();
			}catch(Throwable e) {}	
		}

		return page;
	}
	
	/**
	 * When a Tag is pushed it is considered to be inside the last one pushed. 
	 * The Tag implementation is responsible for doing so.
	 *  
	 * @param node
	 */
	private void pushTag(Tag node)
	{
		tagStack.addElement(node);
	}
	
	public Tag topTag()
	{
		if(tagStack.size()>0)
			return (Tag)tagStack.lastElement();
		// else
		return null;
	}

	
	private Tag popTag()
	{
		int size = tagStack.size();
		if(size>0)
		{
			Tag tc = (Tag)tagStack.lastElement();
			tagStack.removeElementAt(size-1);
			
			if(size<=3)
				pageListener.pageLoadProgress(null,PAGE_LOAD_PARSING_DATA,-1); // easy (but not so aquarate) method to show progress relative the parsing of the page... 
			
			return tc;
		}
		return null;
	}
		
	/**
	 * The Browser instance is the default CommandListener for any rendered page. Use setListener method to use your
	 * own custom listener.
	 * @see #setListener(CommandListener)
	 * @see gr.fire.core.CommandListener#commandAction(javax.microedition.lcdui.Command, gr.fire.core.Component)
	 */
	public void commandAction(Command command, Component c)
	{
		if(command instanceof gr.fire.browser.util.Command)
		{ // only handle known command types
			gr.fire.browser.util.Command cmd = (gr.fire.browser.util.Command)command;
			String url = cmd.getUrl();
			loadPageAsync(url,HttpConnection.GET,null,null);
		}
	}
	/**
	 * The Browser instance is the default PageListener for loadPageAsync requests. Use the setPageListener method to
	 * use your own custom pageListener
	 * @see PageListener
	 * @see #setPageListener(PageListener)
	 */
	public void pageLoadCompleted(String url,String method,Hashtable requestParams, Page page)
	{		
		if(page!=null)
		{
			Container cnt = page.getPageContainer();
			String title= page.getPageTitle();
			Log.logInfo("Loaded Page ["+url+"]["+title+"]");

			if(cnt!=null)
			{
				Panel panel = new Panel(cnt,Panel.VERTICAL_SCROLLBAR|Panel.HORIZONTAL_SCROLLBAR,true);
				panel.setLabel(title);
				
				Component current = FireScreen.getScreen().getCurrent();
				Command left=null,right=null;
				if(current!=null)
				{
					left = current.getLeftSoftKeyCommand();
					right = current.getRightSoftKeyCommand();
				}
				
				panel.setLeftSoftKeyCommand(left);
				panel.setRightSoftKeyCommand(right);
				FireScreen screen = FireScreen.getScreen();
				Component last = screen.getCurrent();
				
				if(last!=null) // show a transition animation
				{
					panel.setAnimation(new TransitionAnimation(last,panel,TransitionAnimation.TRANSITION_SCROLL|TransitionAnimation.TRANSITION_RIGHT));
				}
				
				FireScreen.getScreen().setCurrent(panel);
				panel.setCommandListener(listener);
				panel.setDragScroll(true);
				return;
			}
			else // if cnt is null then the action was canceled by the user (using Browser.cancel()).
			{
				return;
			}
		}
		// Error case. Alert user
		String t=url;
		if(url.length()>15) t = url.substring(0,15)+"...";  
		FireScreen.getScreen().showAlert(Lang.get("Failed to load page")+": "+t,Alert.TYPE_ERROR,Alert.USER_SELECTED_OK,null,null);
	}
	
	/**
	 * This is the asynchronous version of the loadPage{@link #loadPage(String, String, Hashtable, byte[])} method.
	 * It will start a new thread to handle the request and it will send the result Page to the registered PageListener 
	 * instead of returning it to the caller.
	 * 
	 * @see #loadPage(InputStream, String)
	 * 
	 * @param url
	 * @param method
	 * @param requestParameters
	 * @param data
	 */
	public void loadPageAsync(final String url,final String method,final Hashtable requestParameters,final byte []data)
	{
		Thread th = new Thread()
		{
			public void run()
			{
				try
				{
					Page pageMeta  =loadPage(url,method,requestParameters,data);
					pageListener.pageLoadCompleted(url,method,requestParameters,pageMeta);
					
					if(pageMeta!=null)
					{
						if(pageMeta.getRefresh()!=null)
						{
							int seconds = pageMeta.getRefreshSeconds();
							
							if(seconds>0) try{Thread.sleep(seconds*1000);}catch(InterruptedException e){}
							Component current = FireScreen.getScreen().getCurrent();
							if((current instanceof Panel && ((Panel)current).getComponent(0)==pageMeta.getPageContainer()) || current==pageMeta.getPageContainer()) // only execute refresh, if the user is still on the same page
							{
								loadPageAsync(pageMeta.getRefresh(),HttpConnection.GET,null,null);
							}
							else 
							{
								Log.logWarn("Ignoring refresh to "+pageMeta.getRefresh() );
							}
						}
					}
				} catch (Throwable er)
				{
					Log.logWarn("Async load failed",er);
					pageListener.pageLoadFailed(url,method,requestParameters,er);
				}
			}
		};
		th.start();
	}
	
	/**
	 * Utility method to easily load an image using the httpclient of the browser instance.
	 * If an out of memory occures during loadImage, the method 
	 * will disable the FireScreen animations.
	 * @param url
	 * @return the image on the given url.
	 */
	public Image loadImage(String url)
	{
		Request r=null;
		try{
			r = httpClient.requestResource(url,HttpConnection.GET,null,null,false);
			if(r!=null && (r.getConnection()==null || r.getConnection().getResponseCode()==HttpConnection.HTTP_OK))
			{
				Image img = Image.createImage(r.getInputStream());
				return img;
			}
		}catch(OutOfMemoryError e){
			System.gc();
			FireScreen.getScreen().setAnimationsEnabled(false);
			Log.logWarn("Out-of-Memory on load image from "+url,e);
		}catch(Throwable e){
			Log.logWarn("Failed to load image from: "+url,e);
		}finally{
			try{
				if(r!=null) r.close();
			}catch(Exception e){
				Log.logWarn("Failed to close request in Browser.loadImage.",e);
			}
		}
		return null;
	}
	
	
	public void commandAction(Command cmd, Displayable d)
	{
	}

	/**
	 * The Browser renders each page based on a set width and unbounded height. The viewportWidth is 
	 * the width of a rendered page. If the page contains elements that do not fit in the viewpoerWidth
	 * It will increase the width of the resulting page ignoring the viewportWidth.
	 *  
	 * @return
	 */
	public int getViewportWidth()
	{
		if(viewportWidth<=0) return FireScreen.getScreen().getWidth();
		
		return viewportWidth;
	}
	

	/**
	 * Sets the width of the screen that the browser will use to render properly each Page.
	 * @param viewportWidth
	 */
	public void setViewportWidth(int viewportWidth)
	{
		this.viewportWidth = viewportWidth;
	}

	/**
	 * Returns the CommandListener that is set to handle the Browser requests
	 * @return
	 */
	public CommandListener getListener()
	{
		return listener;
	}

	/**
	 * Overides the default listener for link and form events for all rendered pages of this Browser intance.
	 * The default listener is the Browser instance itself.
	 * 
	 * @param listener The CommandListener for link and form events. If null then the Browser instance if used (default)
	 */
	public void setListener(CommandListener listener)
	{
		if(listener==null) listener=this;
		this.listener = listener;
	}

	/**
	 * Returns the HttpClient instance that this Browser instance will use to make Http Requests to http servers. 
	 * @return
	 */
	public HttpClient getHttpClient()
	{
		return httpClient;
	}

	/**
	 * Sets the HttpClient of this Browser instance.
	 * @see #getHttpClient()
	 * @param httpClient
	 */
	public void setHttpClient(HttpClient httpClient)
	{
		this.httpClient = httpClient;
	}

	/**
	 * There are different policies on loading the images of a Page.<br/> 
	 * - Browser.NO_IMAGES <br/>
	 * - Browser.LOAD_IMAGES<br/>
	 * - Browser.LOAD_IMAGES_ASYNC  (default) <br/>
	 * 
	 * The load LOAD_IMAGES_ASYNC will skip images with preset width and height (as img tag properties) and will
	 * try to load them after the Page is done loading. This greatly speeds up Page loading. 
	 * 
	 * @return
	 */
	public byte getImageLoadingPolicy()
	{
		return imageLoadingPolicy;
	}

	/**
	 * @see #getImageLoadingPolicy()
	 * @param imageLoadingPolicy
	 */
	public void setImageLoadingPolicy(byte imageLoadingPolicy)
	{
		this.imageLoadingPolicy = imageLoadingPolicy;
	}

	/**
	 * Returns the PageListener for loadPageAsync requests. The default pagelistener is the Browser.
	 * @see  #pageLoadCompleted(String, String, Hashtable, Page)
	 * @see #pageLoadFailed(String, String, Hashtable, Throwable)
	 * @return
	 */
	public PageListener getPageListener()
	{
		return pageListener;
	}

	/**
	 * @see #getPageListener()
	 * @param pageListener
	 */
	public void setPageListener(PageListener pageListener)
	{
		if(pageListener==null) pageListener=this;
		this.pageListener = pageListener;
	}

	public void pageLoadFailed(String url, String method, Hashtable requestParams, Throwable error)
	{
		if(error instanceof OutOfMemoryError)
		{
			System.gc();
			try
			{
				Log.logError("Out of Memory! Request to URL ["+url+"] failed",error);
				FireScreen.getScreen().showAlert(Lang.get("Could not load page. Out of memory!"),Alert.TYPE_ERROR,Alert.USER_SELECTED_OK,null,null);
			}catch (OutOfMemoryError e)
			{
				System.gc();
			}			
		}
		else 
		{
			Log.logError("Request to URL ["+url+"] failed",error);
			FireScreen.getScreen().showAlert(Lang.get("Error loading page. ")+" "+error.getMessage(),Alert.TYPE_ERROR,Alert.USER_SELECTED_OK,null,null);			
		}
	}

	public boolean isShowLoadingGauge()
	{
		return showLoadingGauge;
	}

	public void setShowLoadingGauge(boolean showLoadingGauge)
	{
		this.showLoadingGauge = showLoadingGauge;
	}
	
	/**
	 * If there is a rendering process on this Browser instance this method will cancel it.
	 * This method will not close any connections or interrupt i/o operations. 
	 * It will only prevent the data from beeing rendered.
	 * 
	 * If images are beeing loaded asynchronously a call to this method will also stop
	 * the image loading thread.
	 * 
	 */
	public void cancel()
	{
		if(rendering)
		{ // force stop rendering is any.
			rendering=false;
			pageListener.pageLoadProgress(Lang.get("Canceled"),PAGE_LOAD_END,100);
		}
		
		if(asyncImageLoader!=null)
		{
			asyncImageLoader.cancel();
			asyncImageLoader=null;
		}
		
	}

	/**
	 * The default implamentation of this method.
	 * @see PageListener#pageLoadProgress(String, byte, int)
	 */
	public void pageLoadProgress(String message, byte state, int percent)
	{
		if(!showLoadingGauge) return;
		
		switch(state)
		{
		case PAGE_LOAD_START:
			if(gauge!=null){
				FireScreen.getScreen().removeComponent(gauge);
				gauge=null;
			}
			gauge = new ProgressbarAnimation(message);
			
			Font font = FireScreen.getTheme().getFontProperty("titlebar.font");
			FireScreen screen = FireScreen.getScreen();
			int sw = screen.getWidth();
			//int mw = font.stringWidth(message);
			gauge.setWidth(sw);
			gauge.setHeight(font.getHeight());
			gauge.setPosition(0,0); // top left corner of the fire screen.
			screen.addComponent(gauge,6);
			break;
		case PAGE_LOAD_END:
			if(gauge!=null){
				FireScreen.getScreen().removeComponent(gauge);
				gauge=null;
			}
		case PAGE_LOAD_LOADING_DATA:
			if(gauge!=null)
			{
				gauge.setMessage(Lang.get("Loading")+"...");
				if(percent>0) gauge.progress(percent);
				else gauge.progress();
			}
			break;
		case PAGE_LOAD_PARSING_DATA:
			if(gauge!=null)
			{
				gauge.setMessage(Lang.get("Rendering")+"...");
				if(percent>0) gauge.progress(percent);
				else gauge.progress();
			}
			break;
		}
	}
}