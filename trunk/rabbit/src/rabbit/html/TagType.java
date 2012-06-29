package rabbit.html;

import java.util.HashMap;
import java.util.Map;

/** This is a class that holds common tagtypes.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class TagType {
    private static Map<String, TagType> types;
    private String type;
    
    public static final TagType A = new TagType ("a");
    public static final TagType SA = new TagType ("/a");
    public static final TagType IMG = new TagType ("img");
    public static final TagType SIMG = new TagType ("/img");  
    public static final TagType LAYER = new TagType ("layer");
    public static final TagType SLAYER = new TagType ("/layer");
    public static final TagType SCRIPT = new TagType ("script");
    public static final TagType SSCRIPT = new TagType ("/script");
    public static final TagType STYLE = new TagType ("style");
    public static final TagType SSTYLE = new TagType ("/style");
    public static final TagType BODY = new TagType ("body");
    public static final TagType SBODY = new TagType ("/body");
    public static final TagType TABLE = new TagType ("table");
    public static final TagType STABLE = new TagType ("/table");
    public static final TagType TR = new TagType ("tr");
    public static final TagType STR = new TagType ("/tr");
    public static final TagType TD = new TagType ("td");
    public static final TagType STD = new TagType ("/td");
    public static final TagType BLINK = new TagType ("blink");
    public static final TagType SBLINK = new TagType ("/blink");
    public static final TagType DOCTYPE = new TagType ("!doctype");
    public static final TagType HTML = new TagType ("html");
    public static final TagType SHTML = new TagType ("/html");
    public static final TagType HEAD = new TagType ("head");
    public static final TagType SHEAD = new TagType ("/head");
    public static final TagType BR = new TagType ("br");
    public static final TagType FONT = new TagType ("font");
    public static final TagType SFONT = new TagType ("/font");
    public static final TagType LI = new TagType ("li");
    public static final TagType SLI = new TagType ("/li");
    public static final TagType B = new TagType ("b");
    public static final TagType SB = new TagType ("/b");
    public static final TagType P = new TagType ("p");
    public static final TagType SP = new TagType ("/p");
    public static final TagType TT = new TagType ("tt");
    public static final TagType STT = new TagType ("/tt");
    public static final TagType SPAN = new TagType ("span");
    public static final TagType SSPAN = new TagType ("/span");
    public static final TagType DIV = new TagType ("div");
    public static final TagType SDIV = new TagType ("/div");
    public static final TagType FORM = new TagType ("form");
    public static final TagType SFORM = new TagType ("/form");
    public static final TagType INPUT = new TagType ("input");
    public static final TagType META = new TagType ("meta");
    public static final TagType SMETA = new TagType ("/meta");
    public static final TagType TITLE = new TagType ("title");
    public static final TagType STITLE = new TagType ("/title");

    
    static {
	types = new HashMap<String, TagType> ();
	types.put (A.toString (), A);
	types.put (SA.toString (), SA);
	types.put (IMG.toString (), IMG);
	types.put (SIMG.toString (), SIMG);
	types.put (LAYER.toString (), LAYER);
	types.put (SLAYER.toString (), SLAYER);
	types.put (SCRIPT.toString (), SCRIPT);
	types.put (SSCRIPT.toString (), SSCRIPT);
	types.put (STYLE.toString (), STYLE);
	types.put (SSTYLE.toString (), SSTYLE);
	types.put (BODY.toString (), BODY);
	types.put (SBODY.toString (), SBODY);
	types.put (TABLE.toString (), TABLE);
	types.put (STABLE.toString (), STABLE);
	types.put (TR.toString (), TR);
	types.put (STR.toString (), STR);
	types.put (TD.toString (), TD);
	types.put (STD.toString (), STD);
	types.put (BLINK.toString (), BLINK);
	types.put (SBLINK.toString (), SBLINK);
	types.put (DOCTYPE.toString (), DOCTYPE);
	types.put (HTML.toString (), HTML);
	types.put (SHTML.toString (), SHTML);
	types.put (HEAD.toString (), HEAD);
	types.put (SHEAD.toString (), SHEAD);
	types.put (BR.toString (), BR);
	types.put (FONT.toString (), FONT);
	types.put (SFONT.toString (), SFONT);
	types.put (LI.toString (), LI);
	types.put (SLI.toString (), SLI);
	types.put (B.toString (), B);
	types.put (SB.toString (), SB);
	types.put (P.toString (), P);
	types.put (SP.toString (), SP);
	types.put (TT.toString (), TT);
	types.put (STT.toString (), STT);
	types.put (SPAN.toString (), SPAN);
	types.put (SSPAN.toString (), SSPAN);
	types.put (DIV.toString (), DIV);
	types.put (SDIV.toString (), SDIV);
	types.put (FORM.toString (), FORM);
	types.put (SFORM.toString (), SFORM);
	types.put (INPUT.toString (), INPUT);
	types.put (META.toString (), META);
	types.put (SMETA.toString (), SMETA);
	types.put (TITLE.toString (), TITLE);
	types.put (STITLE.toString (), STITLE);
    }

    private TagType (String type) {
	this.type = type;
    }
    
    public static TagType getTagType (String type) {
	TagType t = types.get (type);
	return t;
    }

    public String toString () {
	return type;
    }
}
