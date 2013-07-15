package ws.hoyland.popularizer;

import java.util.prefs.Preferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ws.hoyland.popularizer"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		// XXX register the p2 UI policy

		String key = "WS_HOYLAND_POPULARIZER_PID";//写入的键
		Preferences pre = Preferences.systemRoot();//得到跟节点
		String value = null;
		
		//读注册表
		value = pre.get(key, "");
		
		if(value==null||"".equals(value)){
			//写注册表
			value = Util.PID();
			//System.out.println("!"+value);
			pre.put(key, value);
		}else{
			//pre.put(key, value);
			//System.out.println("#"+value);
		}
//		CloudPolicy policy = new CloudPolicy();
//		policy.updateForPreferences();
//		registration = context.registerService(Policy.class.getName(), policy, null);
		
		//getPreferenceStore().addPropertyChangeListener(getPreferenceListener());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
//		context.ungetService(registration.getReference());
//		registration = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
}
