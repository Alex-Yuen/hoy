package test;

//import it.hoyland.sclottery.util.Base64;

import game4d.classic4d.mobile.CommandMessage;
import game4d.classic4d.mobile.info.BaseInfo;
import game4d.classic4d.mobile.info.PaymentInfo;
import util.RetObj;
//import cn.edu.bit82.j2me.J2meNetUtil;

/**
 * 测试Hessian客户端
 * @author Administrator
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RetObj r;
		try {
//			BaseInfo info = new PaymentInfo();
//			info.setCommandLine(CommandMessage.PAYMENT);
//			info.setCustomerId("myr000");
//			String sessionId = "1234";
//			
//			r = (RetObj) J2meNetUtil.invoke(
//					"http://218.16.120.102:8080/AppServer/service/game/classic4d/GameManager",
//					"mobileBet", 
//					new Object[]{sessionId, info});
//			
//			System.out.println(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		String a = "eedfsasdffsdf";
//		System.out.println(Base64.convert(a.getBytes()));
	}

}
