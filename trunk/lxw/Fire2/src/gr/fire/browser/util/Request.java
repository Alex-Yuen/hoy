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
package gr.fire.browser.util;

import gr.fire.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.HttpConnection;


/**
 * 
 * This method wraps a request made with the HttpClient. It can handle both requests to local files (in recordstore or the jar file) 
 * as well as requests to external resource via http.
 * 
 * @author padeler
 *
 */
public class Request
{
	public static final String defaultEncoding="UTF-8";

	private String host;
	private String protocol;
	private String query;
	private String file;
	private int port;
	
	private String encoding;
	

	private HttpConnection connection=null;
	
	private InputStream in=null;
	

	public Request(String host, String protocol, String file, String encoding,InputStream in)
	{
		this.host = host;
		this.protocol = protocol;
		this.file =  file;
		this.encoding = encoding;
		this.in=in;
	}

	public Request(HttpConnection conn) throws IOException
	{
		protocol = conn.getProtocol();
		host = conn.getHost();
		port = conn.getPort();
		query = conn.getQuery();
		file = conn.getFile();
		this.connection=conn;

		encoding = conn.getEncoding();
		if(encoding==null) 
		{
			// else check the content-type field
			String type=null;
			try
			{
				type = conn.getHeaderField("content-type");
			} catch (IOException e)
			{
				Log.logError("Failed to read header field content-type",e);
			}
			if(type!=null) 
			{
				int idx = type.indexOf("charset=");
				if(idx>-1){
					encoding = type.substring(idx+8);
					idx = encoding.indexOf(";");
					if(idx>-1) // cut everything after the semicolon.
						encoding = encoding.substring(0,idx);
				}
				else encoding = defaultEncoding;
			}
			else encoding = defaultEncoding;
		}
		else 
		{
			String enc = encoding.toUpperCase();
			if(enc.equals("UTF_8") || enc.equals("UTF8"))
			{// UTF_8 with underscore and UTF8 is not supported by all phones but is sent by some http servers.
				encoding = defaultEncoding; // set it to UTF-8 (replace underscore with minus)				
			}
		}
	}
	
	public String getBaseURL()
	{
		if("file".equals(protocol)) return protocol +"://";
		if(port==80)
			return protocol+"://"+host;
		else
			return protocol+"://"+host+":"+port;
	}
	
	public String getURL()
	{
		if(connection!=null) return connection.getURL();
		return getBaseURL()+getFile();
	}


	public String getHost()
	{
		return host;
	}
	
	public String getFile()
	{
		return file;
	}
	


	public String getProtocol()
	{
		return protocol;
	}


	public String getQuery()
	{
		return query;
	}


	public int getPort()
	{
		return port;
	}


	public String getEncoding()
	{
		return (encoding!=null)?encoding:defaultEncoding;
	}

	public InputStream getInputStream() throws IOException
	{
		if(in==null && connection!=null)
		{
			in = connection.openInputStream();
		}
		return in;
	}
	
	public void setInputStream(InputStream in){
		this.in = in;
	}

	public HttpConnection getConnection()
	{
		return connection;
	}
	
	public void close() throws IOException
	{
		try{
			if(in!=null) in.close();
			if(connection!=null) connection.close();
		}catch(IOException e){
			Log.logWarn("Failed to close connection "+getBaseURL());
			throw e;
		}
	}	
}
