package rabbit.proxy;

import java.net.URL;
import rabbit.http.HttpHeader;

/** An interface describing the methods for http header generation.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public interface HttpGenerator {
    /** Get a new HttpHeader. This is the same as 
     * getHeader ("HTTP/1.0 200 OK");
     * @return a new HttpHeader.
     */
    HttpHeader getHeader ();

    /** Get a new HttpHeader initialized with some data.
     * @param statusLine the statusline of the response.
     * @return a new HttpHeader.
     */
    HttpHeader getHeader (String statusLine);

    /** Get a 200 Ok header
     * @return a 200 HttpHeader .
     */
    HttpHeader get200 ();

    /** Get a 206 Partial Content header. 
     * @param ifRange if the request is a range request.
     * @param header the current HttpHeader.
     */
    HttpHeader get206 (String ifRange, HttpHeader header);

    /** Get a 304 Not Modified header for the given old header
     * @param oldresp the cached header.
     * @return a 304 HttpHeader .
     */
    HttpHeader get304 (HttpHeader oldresp);

    /** Get a 400 Bad Request header for the given exception.
     * @param exception the Exception handled.
     * @return a HttpHeader for the exception.
     */
    HttpHeader get400 (Exception exception);

    /** Get a 403 Forbidden header.
     * @return a HttpHeader.
     */
    HttpHeader get403 ();

    /** Get a 404 File not found header.
     * @return a HttpHeader.
     */
    HttpHeader get404 (String file);

    /** Get a 407 Proxy Authentication Required for the given realm and url.
     * @param realm the realm that requires auth.
     * @param url the URL of the request made.
     * @return a suitable HttpHeader.
     */
    HttpHeader get407 (String realm, URL url);

    /** Get a 412 Precondition Failed header.
     * @return a suitable HttpHeader.
     */
    HttpHeader get412 ();

    /** Get a 414 Request-URI Too Long header.
     * @return a suitable HttpHeader.
     */
    HttpHeader get414 ();

    /** Get a Requested Range Not Satisfiable for the given exception.
     * @param exception the Exception made.
     * @return a suitable HttpHeader.
     */
    HttpHeader get416 (Throwable exception);

    /** Get a 417 Expectation Failed header.
     * @param expectation the expectation that failed.
     * @return a suitable HttpHeader.
     */
    HttpHeader get417 (String expectation);
    
    /** Get a 500 Internal Server Error header for the given exception.
     * @param exception the Exception made.
     * @return a suitable HttpHeader.
     */
    HttpHeader get500 (Throwable exception);

    /** Get a 504 Gateway Timeout for the given exception.
     * @param exception the Exception made.
     * @return a suitable HttpHeader.
     */
    HttpHeader get504 (Throwable exception, String requestLine);
}
