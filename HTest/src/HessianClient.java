import game4d.classic4d.mobile.CommandMessage;
import game4d.classic4d.mobile.MobileGameManager;
import game4d.classic4d.mobile.info.BaseInfo;
import game4d.classic4d.mobile.info.PaymentInfo;
import com.caucho.hessian.client.HessianProxyFactory;

public class HessianClient {
	// 提供服务servlet的url地址
	private static String serviceUrl = "http://218.16.120.102:8080/AppServer/service/game/classic4d/GameManager";

	public static void main(String[] args) {
		try {
			BaseInfo info = new PaymentInfo();
			info.setCommandLine(CommandMessage.PAYMENT);
			info.setCustomerId("myr000");
			String sessionId = "1234";

			HessianProxyFactory factory = new HessianProxyFactory();
			MobileGameManager manager = (MobileGameManager) factory.create(
					MobileGameManager.class, serviceUrl);
			System.out.println("结果：" + manager.mobileBet(sessionId, info));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}