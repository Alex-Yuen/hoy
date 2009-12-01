package game4d.classic4d.mobile;

import game4d.classic4d.mobile.info.BaseInfo;
import util.RetObj;

public interface MobileGameManager {
	/**
	 * 手机操作(指令请见CommandMessage枚举)
	 * @param sessionId
	 * @param commandLine
	 * @param betInfo
	 * @return
	 */
	
	RetObj mobileBet(String sessionId ,BaseInfo betInfo);
}
