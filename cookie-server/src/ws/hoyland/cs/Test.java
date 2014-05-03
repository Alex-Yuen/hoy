package ws.hoyland.cs;

//import java.lang.reflect.Proxy;
//import java.util.Collection;
//import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//import ws.hoyland.util.JNALoader;
//import ws.hoyland.util.YDM;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//        System.out.println(Proxy.class.getName());
//        Class clazz=Proxy.class;
//        Class clazz1=Proxy.getProxyClass(Collection.class.getClassLoader(), Collection.class);
//        System.out.println(clazz);
//        System.out.println(clazz1);
//		YDM y = (YDM)JNALoader.load("/yundamaAPI.dll", YDM.class);
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("1", "11");
		map.put("2", "12");
		map.put("3", "13");
		map.put("4", "14");
		
		String[] x = map.keySet().toArray(new String[0]);
		for(int i=0;i<x.length;i++){
			System.out.println(x[i]);
		}
		
		String resp = "ptuiCB('0','0','https://ssl.ptlogin2.mail.qq.com/check_sig?pttype=1&uin=1824975360&service=login&nodirect=0&ptsig=63yEXd4xiLQDFKpilV*cPgVF5i8ruySuaDOWR*3dfvk_&s_url=https%3A%2F%2Fmail.qq.com%2Fcgi-bin%2Flogin%3Fvt%3Dpassport%26vm%3Dwpt%26ft%3Dptlogin%26ss%3D%26validcnt%3D%26clientaddr%3D1824975360%40qq.com&f_url=&ptlang=2052&ptredirect=101&aid=522005705&daid=4&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=1&pt_aid=0&pt_aaid=0&pt_light=0&ptlogin_token=e082d7d48762fdabfd15a254eeaa324b01adef65f04bab18','1','登录成功！', 'soa');";
		String checksigUrl = resp.substring(resp.indexOf("http"),
				resp.indexOf("','1','"));
		System.out.println(checksigUrl);
		
		System.out.println(System.currentTimeMillis());
	}

}
