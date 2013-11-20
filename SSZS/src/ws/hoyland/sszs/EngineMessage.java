package ws.hoyland.sszs;

public class EngineMessage {
	//incoming message
	public static final int IM_CONFIG_UPDATED = 0x0010; //配置更新
	public static final int IM_USERLOGIN = 0x0011; //UU用户登录
	public static final int IM_UL_STATUS = 0x0012; //登录结果
	
	//outgoing message

	public static final int OM_LOGINING = 0x2010;
	public static final int OM_LOGINED = 0x2011;
}
