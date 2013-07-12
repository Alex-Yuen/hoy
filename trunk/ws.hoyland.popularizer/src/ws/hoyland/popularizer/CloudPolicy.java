package ws.hoyland.popularizer;

import org.eclipse.equinox.p2.ui.Policy;

public class CloudPolicy extends Policy {
	public CloudPolicy() {
		// XXX User has no visibility for repos
		setRepositoriesVisible(false);
	}
	

	public void updateForPreferences() {
//		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
//		setRepositoriesVisible(prefs
//				.getBoolean(PreferenceConstants.REPOSITORIES_VISIBLE));
//		setRestartPolicy(prefs.getInt(PreferenceConstants.RESTART_POLICY));
//		setShowLatestVersionsOnly(prefs
//				.getBoolean(PreferenceConstants.SHOW_LATEST_VERSION_ONLY));
//		setGroupByCategory(prefs
//				.getBoolean(PreferenceConstants.AVAILABLE_GROUP_BY_CATEGORY));
//		setShowDrilldownRequirements(prefs
//				.getBoolean(PreferenceConstants.SHOW_DRILLDOWN_REQUIREMENTS));
//		if (prefs.getBoolean(PreferenceConstants.AVAILABLE_SHOW_ALL_BUNDLES))
//			setVisibleAvailableIUQuery(QueryUtil.ALL_UNITS);
//		else
//			setVisibleAvailableIUQuery(QueryUtil.createIUGroupQuery());
//		if (prefs.getBoolean(PreferenceConstants.INSTALLED_SHOW_ALL_BUNDLES))
//			setVisibleInstalledIUQuery(QueryUtil.ALL_UNITS);
//		else
//			setVisibleInstalledIUQuery(new UserVisibleRootQuery());

	}
}