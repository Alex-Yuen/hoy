package test;

import it.hoyland.sclottery.util.Base64;

//import java.io.IOException;
//
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
//		String username;
//		try {
//			username = (String) J2meNetUtil.invoke(
//					"http://127.0.0.1:5555/hessian/basicservice",
//					"getUserName", null);
//			System.out.println(username);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String a = "eedfsasdffsdf";
		System.out.println(Base64.convert(a.getBytes()));
	}

}
