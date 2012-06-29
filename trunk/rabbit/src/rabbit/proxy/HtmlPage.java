package rabbit.proxy;

import rabbit.util.SProperties;

/** This class is intended to be used as a template for metapages.
 *  It provides methods to get different part of the HTML-page so 
 *  we can get a consistent interface.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class HtmlPage {

    private static String BASICLOGO = 
	"http://www.khelekore.org/rabbit/images/smallRabbIT3.png";

    private static SProperties config = setup ();
    
    // no dont instanciate this.
    private HtmlPage () {
    }

    /** return a simple HTMLheader.
     * @return a HTMLHeader.
     */
    public static String getPageHeader () {
	return ("<html><head><title>?</title></head>\n" +
		"<body bgcolor=\"" + config.getProperty ("bodybgcolor") + 
		"\" text=\"" + config.getProperty ("bodytext") +
		"\" link=\"" + config.getProperty ("bodylink") +
		"\" alink=\"" + config.getProperty ("bodyalink") +
		"\" vlink=\"" + config.getProperty ("bodyvlink") + "\">\n"); 
    }

    /** return a HTMLheader.
     * @param title the title of this page.
     * @return a HTMLHeader.
     */
    public static String getPageHeader (Connection con, String title) {
	int idx = -1;	
	HttpProxy proxy = con.getProxy ();
	String basiclogo = 
	    proxy.getConfig ().getProperty (proxy.getClass ().getName (), 
							   "logo", 
							   BASICLOGO);
	while ((idx = basiclogo.indexOf ("$proxy")) > -1) {
	    basiclogo = basiclogo.substring(0,idx) + 
		proxy.getHost ().getHostName () + ":" + proxy.getPort () +
		basiclogo.substring (idx + "$proxy".length ());
	}

	
	return ("<html><head><title>" + title + "</title></head>\n" +
		"<body bgcolor=\"" + config.getProperty ("bodybgcolor") + 
		"\" text=\"" + config.getProperty ("bodytext") +
		"\" link=\"" + config.getProperty ("bodylink") +
		"\" alink=\"" + config.getProperty ("bodyalink") +
		"\" vlink=\"" + config.getProperty ("bodyvlink") + "\">\n" +
		"<img src=\"" + basiclogo + 
		"\" alt=\"RabbIT logo\" align=\"right\">\n" +
		"<h1>" + title + "</h1>\n");
    }
  

    /** return a table header with given width (int %) and given borderwidth.
     * @param width the width of the table
     * @param border the width of the border in pixels
     */
    public static String getTableHeader (int width, int border) {
	return ("<table border=\"" + border + "\" " + 
		"width=\"" + width + "%\" " + 
		"bgcolor=\"" + config.getProperty ("tablebgcolor") + "\">\n");
    }   
  
    /** return a tabletopic row 
     */
    public static String getTableTopicRow () {
	return "<tr bgcolor=\"" + config.getProperty ("tabletopicrow") + "\">";
    }
  
    /** setup this class for usage
     */
    public static SProperties setup () {
	config = new SProperties ();
	config.put ("bodybgcolor", "WHITE");
	config.put ("bodytext", "BLACK");
	config.put ("bodylink", "BLUE");
	config.put ("bodyalink", "RED");
	config.put ("bodyvlink", "#AA00AA");
	config.put ("tablebgcolor", "#DDDDFF");
	config.put ("tabletopicrow", "#DD6666");	
	return config;
    }
    

    /** setup this class for usage 
     * @param props the properties to read from 
     */
    public static SProperties setup (SProperties props) {
	String param = props.getProperty ("bodybgcolor");
	if (param != null)
	    config.put ("bodybgcolor", param);
	
	param = props.getProperty ("bodytext");
	if (param != null)
	    config.put ("bodytext", param);
	
	param = props.getProperty ("bodylink");
	if (param != null)
	    config.put ("bodylink", param);
	
	param = props.getProperty ("bodyalink");
	if (param != null)
	    config.put ("bodyalink", param);

	param = props.getProperty ("bodyvlink");
	if (param != null)
	    config.put ("bodyvlink", param);
	
	param = props.getProperty ("tablebgcolor");
	if (param != null)
	    config.put ("tablebgcolor", param);

	param = props.getProperty ("tabletopicrow");
	if (param != null)
	    config.put ("tabletopicrow", param);
	return config;
    }
  
    /** return the properties this class uses
     */
    public static SProperties getProperties () {
	return config;
    }  
}

