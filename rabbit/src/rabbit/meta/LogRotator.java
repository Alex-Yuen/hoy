package rabbit.meta;

/** An admin page that rotates the logs.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class LogRotator extends BaseMetaHandler {

    protected String getPageHeader () {
	return "Status";
    }

    /** Add the page information */
    protected PageCompletion addPageInformation (StringBuilder sb) {
	con.getLogger ().rotateLogs ();
	sb.append ("Logs rotated");
	return PageCompletion.PAGE_DONE;
    }
}
