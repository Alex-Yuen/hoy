package rabbit.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import rabbit.html.HtmlBlock;
import rabbit.html.HtmlParseException;
import rabbit.html.HtmlParser;
import rabbit.html.Tag;
import rabbit.html.Token;
import rabbit.html.TokenType;

/** This class tests the html parser
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class TestHtmlParser {
    private String file;
    
    public static void main (String[] args) {
	for (String s : args) {
	    try {
		TestHtmlParser thp = new TestHtmlParser (s);
		thp.parse ();
	    } catch (Exception e) {
		e.printStackTrace ();
	    }
	}
    }

    public TestHtmlParser (String file) {
	this.file = file;
    }

    private void parse () throws IOException, HtmlParseException {
	File f = new File (file);
	long size = f.length ();
	FileInputStream fis = new FileInputStream (f);
	DataInputStream dis = new DataInputStream (fis);
	byte[] buf = new byte[(int)size];
	dis.readFully (buf);
	HtmlParser parser = new HtmlParser ();
	parser.setText (buf);
	HtmlBlock block = parser.parse ();
	for (Token t : block.getTokens ()) {
	    System.out.print ("t.type: " + t.getType ());
	    if (t.getType () == TokenType.TAG)
		System.out.print (", tag: " + t.getTag ().getType ());
	    System.out.println ();
	}
    }
}
