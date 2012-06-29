package rabbit.filter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rabbit.html.HtmlBlock;
import rabbit.html.Tag;
import rabbit.html.TagType;
import rabbit.html.Token;
import rabbit.html.TokenType;
import rabbit.html.TokenType;
import rabbit.http.HttpHeader;
import rabbit.proxy.Connection;
import rabbit.proxy.HttpProxy;
import rabbit.util.Config;

/** This class switches advertising images into another image.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class AdFilter extends HtmlFilter {
    /** the image we replace ads with */
    protected static final String ADREPLACER = 
    "http://$proxy/FileSender/public/NoAd.gif";

    /** the actual imagelink. */
    private String adreplacer = null;

    /** The pattern. */
    private Pattern adPattern;

    // For the factory.
    public AdFilter () {
    }

    /** Create a new AdFilter for the given request, response pair.
     * @param request the actual request made.
     * @param response the actual response being sent.
     */
    public AdFilter (Connection con, HttpHeader request, HttpHeader response) {
	super (con, request, response);
	int idx = -1;	
	HttpProxy proxy = con.getProxy ();
	adreplacer = proxy.getConfig ().getProperty (getClass ().getName (), 
						     "adreplacer", 
						     ADREPLACER);
	while ((idx = adreplacer.indexOf ("$proxy")) > -1) {
	    adreplacer = adreplacer.substring(0,idx) + 
		proxy.getHost ().getHostName () + ":" + proxy.getPort () +
		adreplacer.substring (idx + "$proxy".length ());
	}
    }

    @Override public HtmlFilter newFilter (Connection con, 
					   HttpHeader request, 
					   HttpHeader response) {
	return new AdFilter (con, request, response);
    }
    
    /** Removes advertising from the given block.
     * @param block the part of the html page we are filtering.
     */
    public void filterHtml (HtmlBlock block) {
	int astart = -1;
	
	List<Token> tokens = block.getTokens ();
	int tsize = tokens.size ();
	for (int i = 0; i < tsize; i++) {
	    Token t = tokens.get (i);
	    if (t.getType () == TokenType.TAG) {
		Tag tag = t.getTag ();
		TagType tagtype = tag.getTagType ();
		if (tagtype == TagType.A) {
		    astart = i;
		    Tag atag = tag;
		    int ttsize = tokens.size ();
		    for (; i < ttsize; i++) {
			Token tk2 = tokens.get (i);
			if (tk2.getType () == TokenType.TAG) {
			    Tag tag2 = tk2.getTag ();
			    TagType t2tt = tag2.getTagType ();
			    if (t2tt != null && 
				t2tt == TagType.SA)
				break;
			    else if (t2tt != null &&
				     t2tt == TagType.IMG) {
				if (isEvil (atag.getAttribute ("href")))
				    tag2.setAttribute ("src", adreplacer);
			    }
			}
		    }
		    if (i == tsize && astart < i) {
			block.setRest ((tokens.get (astart)).getStartIndex ());
		    }
		} else if (tagtype == TagType.LAYER 
			   || tagtype == TagType.SCRIPT) {
		    String src = tag.getAttribute ("src");
		    if (isEvil (src))
			tag.setAttribute ("src", adreplacer);
		}
	    }	    
	}
    }
    
    /** Check if a string is evil (that is its probably advertising).
     * @param str the String to check.
     */
    public boolean isEvil (String str) {
	if (str == null)
	    return false;
	if (adPattern == null) {
	    Config conf = con.getProxy ().getConfig ();
	    String adLinks = conf.getProperty (getClass ().getName (),
					       "adlinks",
					       "[/.]ad[/.]");
	    adPattern = Pattern.compile (adLinks);
	}
	Matcher m = adPattern.matcher (str);
	return (m.find ());
    }
}

