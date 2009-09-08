/**
 * 
 */
package gr.fire.browser.util;


import gr.fire.browser.Browser;

import java.util.Hashtable;

/**
 * 
 * A PageListener registered to a Browser instance will be notified upon completion of requests to Browser.loadPageAsync.
 * @see Browser#loadPageAsync(String, String, Hashtable, byte[])
 * @author padeler
 *
 */
public interface PageListener
{
	
	public static final byte PAGE_LOAD_START=0x10;
	public static final byte PAGE_LOAD_LOADING_DATA=0x20;
	public static final byte PAGE_LOAD_PARSING_DATA=0x30;
	public static final byte PAGE_LOAD_END=0x70;
	
	
	/**
	 * Only called when pageLoadAsync is used to load a page.
	 * The rendering of a page was completed. The result is in the Page instance.
	 * @param url The usr that was used when the request was made. This may not be the same with the url in the Page parameter since there may have been redirects.
	 * @param method The method that was used for the Http request
	 * @param requestParams The parameters that where send with the request. 
	 * @param page The result of the request.
	 */
	public void pageLoadCompleted(String url,String method,Hashtable requestParams, Page page);
	
	/**
	 * Only called when pageLoadAsync is used to load a page.
	 * The loadPageAsync failed with an exception. A callback to the PageListener is made with the error.
	 * @param url
	 * @param method
	 * @param requestParams
	 * @param error
	 */
	public void pageLoadFailed(String url,String method,Hashtable requestParams, Throwable error);	
	
	/**
	 * This method is called during the loading and rendering of a page to notify about progress. 
	 * 
	 * The pageLoadProgress method will be called at least 2 times during the progress of the loading.
	 * Once on the loading start, and once on the page completion or abort. 
	 * 
	 * @param message A short message. Can be the url or a default status message
	 * @param state A value describing the current status of the process. The listener will receive atleast one
	 * method call with state PAGE_LOAD_START and one with PAGE_LOAD_END ({@link #PAGE_LOAD_START}, {@value #PAGE_LOAD_END}).
	 * @param percent The percentage of the page loaded. This is just an estimated value.
	 */
	public void pageLoadProgress(String message,byte state,int percent);
	
}
