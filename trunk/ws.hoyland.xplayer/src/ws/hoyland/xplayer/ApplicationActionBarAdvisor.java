package ws.hoyland.xplayer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	// 基础数据菜单：公共参数设置、教学单位情况

	private Action publicData, teachUnitData;

	// 测算数据菜单：历史数据、图形对比

	private Action historyData, graphicsData;

	// 系统管理菜单：获取数据、用户管理、数据备份、远程数据源配置、新增年度

	private Action getServerData, userManag, dataBak, remoteDataSourceConfig,

	addNewYear;

	// 帮助菜单：欢迎画面、帮助信息

	private IWorkbenchAction welcomeAction, helpInfo;

	// 帮助菜单：关于系统

	private Action aboutSystem;

	// 工具栏：用户注销、在线升级

	private Action logoff, update;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		welcomeAction = ActionFactory.INTRO.create(window);

		welcomeAction.setText("欢迎使用");

		welcomeAction.setAccelerator(SWT.ALT + 87); // 设置快捷键为ALT+W

		register(welcomeAction);

		helpInfo = ActionFactory.HELP_CONTENTS.create(window);

		helpInfo.setText("系统帮助@ALT+H");

		helpInfo.setToolTipText("系统帮助");

		register(helpInfo);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
//		MenuManager baseDataMenu = new MenuManager("基础数据[&B]", "baseDataMenu");
//
//		menuBar.add(baseDataMenu);
//
//		baseDataMenu.add(publicData);
//
//		baseDataMenu.add(teachUnitData);
//
//		MenuManager calcDataMenu = new MenuManager("测算数据[&C]", "calcDataMenu");
//
//		menuBar.add(calcDataMenu);
//
//		calcDataMenu.add(historyData);
//
//		calcDataMenu.add(graphicsData);
//
//		MenuManager sysManagMenu = new MenuManager("系统管理[&M]", "sysManagMenu");
//
//		menuBar.add(sysManagMenu);
//
//		sysManagMenu.add(getServerData);
//
//		sysManagMenu.add(userManag);
//
//		sysManagMenu.add(dataBak);
//
//		sysManagMenu.add(remoteDataSourceConfig);
//
//		sysManagMenu.add(addNewYear);
//
//		sysManagMenu.add(update);

		MenuManager helpMenu = new MenuManager("帮助[&H]", "helpManagMenu");

		menuBar.add(helpMenu);

		helpMenu.add(welcomeAction);

		helpMenu.add(helpInfo);

		helpMenu.add(aboutSystem);
	}

}
