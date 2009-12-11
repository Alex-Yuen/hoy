package it.hoyland.test;

import java.net.MalformedURLException;

import util.RetObj;
import game4d.classic4d.mobile.*;
import game4d.classic4d.mobile.info.*;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * Hessian客户端, J2SE下
 * @author Administrator
 *
 */
public class Tester {

	private static String URL = "http://localhost/hessian/service";
	
	public Tester() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
	        HessianProxyFactory factory = new HessianProxyFactory();
	        MobileGameManager manager;
			manager = (MobileGameManager) factory.create(MobileGameManager.class, URL);
	        BaseInfo info = new BalanceInfo();
	        info.setCustomerId("bicid");
	        RetObj ret = manager.mobileBet("1234", info);
	        //System.out.println(ret.get("pwi").getClass().getName());
	        System.out.println(((PassWordInfo)ret.get("pwi")).getCustomerId());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
