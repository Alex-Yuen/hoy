package it.hoyland.hessian.server;

import util.RetObj;

import com.caucho.hessian.server.HessianServlet;
import game4d.classic4d.mobile.*;
import game4d.classic4d.mobile.info.*;

public class ServiceServlet extends HessianServlet implements MobileGameManager {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1829352022006443287L;

	@Override
	public RetObj mobileBet(String sessionId, BaseInfo betInfo) {
		//System.out.println(betInfo.getClass().getName());
		System.out.println(((BalanceInfo)betInfo).getCustomerId());
		RetObj retObj = new RetObj(true);
		BaseInfo info = new PassWordInfo();
		info.setCustomerId("pwicid");
		retObj.put("pwi", info);
		return retObj;
	}

}
