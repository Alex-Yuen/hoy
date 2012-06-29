package rabbit.proxy;

import java.util.HashMap;
import java.util.Map;
import rabbit.handler.HandlerFactory;
import rabbit.util.Config;
import rabbit.util.Logger;
import rabbit.util.SProperties;

/** A class to handle mime type handler factories.
 *
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
class HandlerFactoryHandler {
    private Map<String, HandlerFactory> handlers;
    private Map<String, HandlerFactory> cacheHandlers;
    
    public HandlerFactoryHandler (SProperties handlersProps, 
				  SProperties cacheHandlersProps, 
				  Config config,
				  Logger log) {
	handlers = loadHandlers (handlersProps, config, log);
	cacheHandlers = loadHandlers (cacheHandlersProps, config, log);
    }

    /** load a set of handlers.
     * @param section the section in the config file.
     * @param log the Logger to write errors/warnings to.
     * @return a Map with mimetypes as keys and Handlers as values.
     */
    protected Map<String, HandlerFactory> 
	loadHandlers (SProperties handlersProps, Config config, Logger log) {
	Map<String, HandlerFactory> hhandlers = 
	    new HashMap<String, HandlerFactory> ();
	if (handlersProps == null)
	    return hhandlers;
	String classname = "";
	for (String handler : handlersProps.keySet ()) {
	    try {
		classname = handlersProps.getProperty (handler).trim ();
		Class<? extends HandlerFactory> cls = 
		    Class.forName (classname).asSubclass (HandlerFactory.class);
		HandlerFactory hf = cls.newInstance ();
		hf.setup (log, config.getProperties (classname));
		hhandlers.put (handler, hf);
	    } catch (ClassNotFoundException ex) {
		log.logError ( "Could not load class: '" + classname + 
			       "' for handlerfactory '" + handler + "'");
	    } catch (InstantiationException ie) {
		log.logError ("Could not instanciate factory class: '" + 
			      classname + "' for handler '" + 
			      handler + "' :" + ie);
	    } catch (IllegalAccessException iae) {
		log.logError ("Could not instanciate factory class: '" + 
			      classname + "' for handler '" + 
			      handler + "' :" + iae);
	    }
	}
	return hhandlers;
    }

    HandlerFactory getHandlerFactory (String mime) {
	return handlers.get (mime);
    }

    HandlerFactory getCacheHandlerFactory (String mime) {
	return cacheHandlers.get (mime);
    }
}
