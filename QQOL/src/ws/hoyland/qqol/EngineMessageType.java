package ws.hoyland.qqol;

public class EngineMessageType {
	//incoming message
	public static final int IM_CONFIG_UPDATED = 0x1010; //配置更新
	public static final int IM_USERLOGIN = 0x1011; //UU用户登录
	public static final int IM_UL_STATUS = 0x1012; //登录结果
	public static final int IM_LOAD_ACCOUNT = 0x1013; //加载帐号文件
	public static final int IM_LOAD_MAIL = 0x1014; //加载邮件列表
	public static final int IM_CAPTCHA_TYPE = 0x1015;
	public static final int IM_PROCESS = 0x1016;

	public static final int IM_IMAGE_DATA = 0x1017;
	public static final int IM_REQUIRE_MAIL = 0x1018;
	public static final int IM_INFO = 0x1019;

	public static final int IM_NO_EMAILS = 0x1020;
	
	public static final int IM_FINISH = 0x1021;
	public static final int IM_START = 0x1022;
	
	public static final int IM_EXIT = 0x1023;
	public static final int IM_PAUSE = 0x1024;
	public static final int IM_FREQ = 0x1025; //申诉频繁

	public static final int IM_PAUSE_COUNT = 0x1026;
	public static final int IM_INFOACT = 0x1027;

	public static final int IM_NICK = 0x1028;
	public static final int IM_PROFILE = 0x1029;
	public static final int IM_TF = 0x1030;

	public static final int IM_RELOGIN = 0x1031;
	//outgoing message
	public static final int OM_LOGINING = 0x2010;
	public static final int OM_LOGINED = 0x2011;
	public static final int OM_CLEAR_ACC_TBL = 0x2012;
	public static final int OM_ADD_ACC_TBIT = 0x2013;
	public static final int OM_ACCOUNT_LOADED = 0x2014;
	
	public static final int OM_CLEAR_MAIL_TBL = 0x2015;
	public static final int OM_ADD_MAIL_TBIT = 0x2016;
	public static final int OM_MAIL_LOADED = 0x2017;
	
	public static final int OM_READY = 0x2018;
	public static final int OM_UNREADY = 0x2019;
	public static final int OM_LOGIN_ERROR = 0x2020;
	public static final int OM_RUNNING = 0x2021;
	public static final int OM_IMAGE_DATA = 0x2022;
	public static final int OM_REQUIRE_MAIL = 0x2023;
	public static final int OM_INFO = 0x2024;
	public static final int OM_RECONN = 0x2025;
	public static final int OM_PAUSE = 0x2026;
	public static final int OM_INFOACT = 0x2027;
	public static final int OM_BEAT = 0x2028;
	public static final int OM_STOP = 0x2029;
	public static final int OM_NICK = 0x2030;
	public static final int OM_PROFILE = 0x2031;
	public static final int OM_TF = 0x2032;
}
