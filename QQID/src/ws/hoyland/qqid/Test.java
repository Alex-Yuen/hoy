package ws.hoyland.qqid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("test");
//		for(int i=0;i<10;i++){
//			System.out.println(Names.getInstance().getName());
//		}
		
//		String t = "Province=new Array;City=new Array;I=0;Province[I]='北京';City[I]=new Array('北京');I++;Province[I]='上海';City[I]=new Array('上海');I++;Province[I]='天津';City[I]=new Array('天津');I++;Province[I]='重庆';City[I]=new Array('重庆');I++;Province[I]='河北';City[I]=new Array('【请选择城市】','忘记了','石家庄','唐山','秦皇岛','邯郸','邢台','保定','张家口','承德','沧州','廊坊','衡水','其他');I++;Province[I]='山西';City[I]=new Array('【请选择城市】','忘记了','太原','大同','阳泉','长治','晋城','朔州','晋中','运城','忻州','临汾','吕梁');I++;Province[I]='内蒙古';City[I]=new Array('【请选择城市】','忘记了','呼和浩特','包头','乌海','赤峰','通辽','鄂尔多斯','呼伦贝尔','乌兰察布盟','锡林郭勒盟','巴彦淖尔盟','阿拉善盟','兴安盟');I++;Province[I]='辽宁';City[I]=new Array('【请选择城市】','忘记了','沈阳','大连','鞍山','抚顺','本溪','丹东','锦州','葫芦岛','营口','盘锦','阜新','辽阳','铁岭','朝阳');I++;Province[I]='吉林';City[I]=new Array('【请选择城市】','忘记了','长春','吉林市','四平','辽源','通化','白山','松原','白城','延边朝鲜族自治州');I++;Province[I]='黑龙江';City[I]=new Array('【请选择城市】','忘记了','哈尔滨','齐齐哈尔','鹤岗','双鸭山','鸡西','大庆','伊春','牡丹江','佳木斯','七台河','黑河','绥化','大兴安岭');I++;Province[I]='江苏';City[I]=new Array('【请选择城市】','忘记了','南京','无锡','徐州','常州','苏州','南通','连云港','淮安','盐城','扬州','镇江','泰州','宿迁','昆山');I++;Province[I]='浙江';City[I]=new Array('【请选择城市】','忘记了','杭州','宁波','温州','嘉兴','湖州','绍兴','金华','衢州舟山','台州','丽水');I++;Province[I]='安徽';City[I]=new Array('【请选择城市】','忘记了','合肥','芜湖','蚌埠','淮南','马鞍山','淮北','铜陵','安庆','黄山','滁州','阜阳','宿州','巢湖','六安','亳州','池州','宣城');I++;Province[I]='福建';City[I]=new Array('【请选择城市】','忘记了','福州','厦门','莆田','三明','泉州','漳州','南平','龙岩','宁德');I++;Province[I]='江西';City[I]=new Array('【请选择城市】','忘记了','南昌','景德镇','萍乡','新余','九江','鹰潭','赣州','吉安','宜春','抚州','上饶');I++;Province[I]='山东';City[I]=new Array('【请选择城市】','忘记了','济南','青岛','淄博','枣庄','东营','潍坊','烟台','威海','济宁','泰安','日照','莱芜','德州','临沂','聊城','滨州','菏泽');I++;Province[I]='河南';City[I]=new Array('【请选择城市】','忘记了','郑州','开封','洛阳','平顶山','焦作','鹤壁','新乡','安阳','濮阳','许昌','漯河','三门峡','南阳','商丘','信阳','周口','驻马店','济源');I++;Province[I]='湖北';City[I]=new Array('【请选择城市】','忘记了','武汉','黄石','襄樊','十堰','荆州','宜昌','荆门','鄂州','孝感','黄冈','咸宁','随州','仙桃','天门','潜江','神农架','恩施土家族苗族自治州');I++;Province[I]='湖南';City[I]=new Array('【请选择城市】','忘记了','长沙','株洲','永州','湘潭','衡阳','邵阳','岳阳','常德','张家界','益阳','郴州','怀化','娄底','湘西土家族苗族自治州');I++;Province[I]='广东';City[I]=new Array('【请选择城市】','忘记了','广州','深圳','珠海','汕头','韶关','佛山','江门','湛江','茂名','肇庆','惠州','梅州','汕尾','河源','阳江','清远','东莞','中山','潮州','揭阳','云浮');I++;Province[I]='广西';City[I]=new Array('【请选择城市】','忘记了','南宁','柳州','桂林','梧州','北海','防城港','钦州','贵港','玉林','百色','贺州','河池','来宾','崇左');I++;Province[I]='海南';City[I]=new Array('【请选择城市】','忘记了','海口','三亚','五指山','琼海','儋州','文昌','万宁','东方','澄迈','定安','屯昌','临高','白沙黎族自治县昌','江黎族自治县','乐东黎族自治县','陵水黎族自治县','保亭黎族苗族自治县','琼中黎族苗族自治县');I++;Province[I]='四川';City[I]=new Array('【请选择城市】','忘记了','成都','自贡','攀枝花','泸州','德阳','绵阳','广元','遂宁','内江','乐山','南充','宜宾','广安','达州','眉山','雅安','巴中','资阳','阿坝藏族羌族自治州','甘孜藏族自治州','凉山彝族自治州');I++;Province[I]='贵州';City[I]=new Array('【请选择城市】','忘记了','贵阳','六盘水','遵义','安顺','铜仁','毕节','黔西南布依族苗族自治州','黔东南苗族侗族自治州','黔南布依族苗族自治州');I++;Province[I]='云南';City[I]=new Array('【请选择城市】','忘记了','昆明','曲靖','玉溪','保山','昭通','丽江','思茅','临沧','文山壮族苗族自治州','红河哈尼族彝族自治州','西双版纳傣族自治州','楚雄彝族自治州','大理白族自治州','德宏傣族景颇族自治州 ','怒江傈傈族自治州','迪庆藏族自治州');I++;Province[I]='西藏';City[I]=new Array('【请选择城市】','忘记了','拉萨','那曲','昌都','山南','日喀则','阿里','林芝');I++;Province[I]='陕西';City[I]=new Array('【请选择城市】','忘记了','西安','铜川','宝鸡','咸阳','渭南','延安','汉中','榆林','安康','商洛');I++;Province[I]='甘肃';City[I]=new Array('【请选择城市】','忘记了','兰州','金昌','白银','天水','嘉峪关','武威','张掖','平凉','酒泉','庆阳','定西','陇南','临夏回族自治州','甘南藏族自治州');I++;Province[I]='青海';City[I]=new Array('【请选择城市】','忘记了','西宁','海东','海北藏族自治州','黄南藏族自治州','海南藏族自治州','果洛藏族自治州','玉树藏族自治州','海西蒙古族藏族自治州');I++;Province[I]='宁夏';City[I]=new Array('【请选择城市】','忘记了','银川','石嘴山','吴忠','固原');I++;Province[I]='新疆';City[I]=new Array('【请选择城市】','忘记了','乌鲁木齐','克拉玛依','石河子','阿拉尔','图木舒克','五家渠','吐鲁番','哈密','和田','阿克苏','喀什','克孜勒苏柯尔克孜自治州','巴音郭楞蒙古自治州','昌吉回族自治州','博尔塔拉蒙古自治州','伊犁哈萨克自治州');I++;Province[I]='香港';City[I]=new Array('香港');I++;Province[I]='澳门';City[I]=new Array('澳门');I++;Province[I]='台湾';City[I]=new Array('【请选择城市】','忘记了','台北','高雄','基隆','台中','台南','新竹','嘉义','台北县','宜兰县','新竹县','桃园县','苗栗县','台中县','彰化县','南投县','嘉义县','云林县','台南县','高雄县','屏东县','台东县','花莲县','澎湖县');";
//		
//		int i = t.indexOf("City[I]='")+19;
//		int j = t.indexOf("');I++;")+10;
//		while(i!=-1){
//			System.out.println(t.substring(i, j).replaceAll("\'", "\""));
//			t = t.substring(j+7);
//			
//			i = t.indexOf("City[I]=''")+19;
//			j = t.indexOf("');I++;")+10;
//		}
//		
		
//		String account = "12345678";
//		System.out.println(account.substring(account.length()-1));
//		try{
//			Properties props = new Properties();
//	//		props.setProperty("mail.store.protocol", "pop3");
//	//		props.setProperty("mail.pop3.host", "pop3.163.com");
//			props.put("mail.imap.host", "imap.163.com");	            
//	        props.put("mail.imap.auth.plain.disable", "true");
//	         
//			Session session = Session.getDefaultInstance(props);
//			session.setDebug(false); 
//			IMAPStore store = (IMAPStore)session.getStore("imap");
//			store.connect("bingfan105310@163.com", "bianyxw0461");
//			IMAPFolder folder = (IMAPFolder)store.getFolder("INBOX");
//			folder.open(Folder.READ_WRITE);
//	
//			// 全部邮件
//			Message[] messages = folder.getMessages();
//			
//			boolean seen = true;
//			//System.err.println(messages.length);
//			
//			
//			for (int i = messages.length-1; i >=0; i--) {
//				seen = true;
//				Message message = messages[i];
//				// 删除邮件
//				// message.setFlag(Flags.Flag.DELETED,true);
//				message.getAllHeaders();
//	
//				Flags flags = message.getFlags();    
//				if (flags.contains(Flags.Flag.SEEN)){
//					//info("A1");
//					seen = true;    
//				} else {
//					//info("A2");
//					seen = false;    
//				}
//	
//				if(!seen){
//					System.out.println(String.valueOf(seen));
//					//info(message.get)
//					System.out.println(message.getSubject());
//					System.out.println((String)message.getContent());
//					flags = message.getFlags();    
//					if (flags.contains(Flags.Flag.SEEN)){
//						//info("A1");
//						seen = true;    
//					} else {
//						//info("A2");
//						seen = false;    
//					}
//					System.out.println(String.valueOf(seen));
//					//message.setFlag(Flags.Flag.SEEN, false);
//
//					try{
//						Thread.sleep(5000);
//					}
//					catch(Exception e){
//						e.printStackTrace();
//						
//					}
//					message.setFlag(Flags.Flag.SEEN, false);
//					
//				}
//				if(!seen){
//					//message.setFlag(Flags.Flag.SEEN, false);
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		try{
//			InetAddress addr = InetAddress.getLocalHost();
//			String ip=addr.getHostAddress().toString();//获得本机IP
//			System.out.println(ip);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
//		try{
//		String result = new Test().execute("ipconfig");
//		//result = result.substring(result.indexOf("宽带连接"));
//		if(result.indexOf("IP Address")!=-1){
//			result = result.substring(result.indexOf("IP Address"));
//		}
//		if(result.indexOf("IPv4 地址")!=-1){
//			result = result.substring(result.indexOf("IPv4 地址"));
//		}
//		//System.out.println(result);
//		result = result.substring(result.indexOf(":")+2);
//		//System.out.println(result);
//		result = result.substring(0, result.indexOf("\n "));
//		System.out.println(result);
//		System.out.println("*");
//		//System.out.println(result+"*");
//		System.out.println(result.substring(0, result.lastIndexOf("."))+"*");
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
//		
//		try{
//			String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
//			
//			HttpClient client = new DefaultHttpClient();
//			client.getParams().setParameter(
//					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
//			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
//			
//			HttpHost proxy = new HttpHost("222.79.136.76", 18186);
//			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
//			 
//			 KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//		      InputStream instream = Test.class.getResourceAsStream("/my.truststore");
//		      //密匙库的密码
//		      trustStore.load(instream, "Hoy133".toCharArray());
//		      //注册密匙库
//		      SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
//		      //不校验域名
//		      socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		      Scheme sch = new Scheme("https", 443, socketFactory);
//		      client.getConnectionManager().getSchemeRegistry().register(sch);
//		      //获得HttpGet对象
//		      HttpGet httpGet = null;
//		      httpGet = new HttpGet("https://ynote.youdao.com/login/acc/reg/query?app=client&product=YNOTE&ClientVer=30500000000&GUID=PCacef4b7bf9ee6a1d3&LoginFormABTest=LoginFormATest&client_ver=30500000000&device_id=PCacef4b7bf9ee6a1d3&device_name=ZHU-PC&device_type=PC&os=Windows&os_ver=Windows%207&vendor=null");
//		      //发送请求
//		      HttpResponse response = client.execute(httpGet);
//		      //输出返回值
//		      InputStream is = response.getEntity().getContent();
//		      BufferedReader br = new BufferedReader(new InputStreamReader(is));
//		      String line = "";
//		      while((line = br.readLine())!=null){
//		          System.out.println(line);
//		      }
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
//		double i = 2200;
//		double k = 2309;
//		DecimalFormat df2  = new DecimalFormat("0.00");  
//		System.out.println(i*100/k);
//		System.out.println(df2.format(i*100/k));
		//BB6FC290E67EBADD7ADE4A5C2C0AA7C4
		try{
			
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			byte[] results = md.digest("uefnqosrwa".getBytes()); 
			String resultString = byteArrayToHexString(results);
			resultString = resultString.toUpperCase();
			//System.out.println(resultString.toUpperCase());
			
//			StringBuffer sb = new StringBuffer();
			
//			for(int i=0;i<resultString.length();i+=2){
//				char c = (char)Integer.valueOf(String.valueOf(resultString.charAt(i)+resultString.charAt(i+1)), 16).intValue();
//				sb.append(c);
//			}
			// \x5A\x06\x35\x0C\x92\x2D\x08\xB5\xA1\x76\x66\xE0\xF9\x82\xD0\x05
			
			//685D590A89F352D10644DD985DA2A9A1
//			byte bb = (byte) 0x92;
//			System.out.println(bb);
//			1001 0010
//			byte[] bbs = new byte[1];
//			bbs[0] = bb;
//			
//			char c= 0x92;
//			System.out.println(c);
//			sb.append('’');
////			c= 0x06;
////			sb.append(c);
//			
			
//			sb.append((char)(byte)0x5A);
//			sb.append((char)(byte)0x06);
//			sb.append((char)(byte)0x35);
//			sb.append((char)(byte)0x0C);
//			
//			sb.append((byte)0x92);
//			sb.append((char)(byte)0x2D);
//			sb.append((char)(byte)0x08);
//			sb.append((char)(byte)0xB5);
			
//			sb.append((char)(byte)0xA1);
//			sb.append((char)(byte)0x76);
//			sb.append((char)(byte)0x66);
//			sb.append((char)(byte)0xE0);
//			sb.append((char)(byte)0xF9);
//			sb.append((char)(byte)0x82);
//			sb.append((char)(byte)0xD0);
//			sb.append((char)(byte)0x05);
//			
//			sb.append((char)(byte)0x00);
//			sb.append((char)(byte)0x00);
//			sb.append((char)(byte)0x00);
//			sb.append((char)(byte)0x00);
//			sb.append((char)(byte)0x08);
//			sb.append((char)(byte)0x23);
//			sb.append((char)(byte)0x9b);
//			sb.append((char)(byte)0x86);
			
//			System.out.println(sb.toString().length());
//			
//			//System.out.println((int)'’');
//			//char c = 0x5A;
//			//System.out.println(c+".");
//			results = md.digest(sb.toString().getBytes()); 
//			resultString = byteArrayToHexString(results);
//			resultString = resultString.toUpperCase();
//			System.out.println(resultString);
				
				
//			System.out.println(sb.toString());
//			sb.append("\\x00\\x00\\x00\\x00\\x08\\x23\\x9b\\x86");
//			System.out.println(sb.toString());
			
//			char c = 0x9b;
//			System.out.println(c);
//			
			int idx = results.length;
			byte[] rs = new byte[idx+8];
			for(int i=0;i<idx;i++){
				rs[i] = results[i];
			}
//			
			rs[idx+0] = (byte)0x00;
			rs[idx+1] = (byte)0x00;
			rs[idx+2] = (byte)0x00;
			rs[idx+3] = (byte)0x00;
			rs[idx+4] = (byte)0x3a;
			rs[idx+5] = (byte)0x1d;
			rs[idx+6] = (byte)0xc0;
			rs[idx+7] = (byte)0x6b;
			
//			System.out.println(rs.length);
//			
//			int chrsz = 8;
//			int mask = (1 << chrsz) - 1;
////			System.out.println(rs.length);
//			
//			int[] bin = new int[((rs.length*chrsz-1)>>5)+1];
//			//System.out.println((24*8-1)>>5);
//			
//			for (int i = 0; i < rs.length * chrsz; i += chrsz) {
//				//System.out.println((rs[i / chrsz] & mask) << (i % 32));
//				bin[i >> 5] |= ((rs[i / chrsz] & mask) << (i % 32));
//				//System.out.println((i>>5)+":"+bin[i>>5]);
//			}
			
//			bin[0] = 90;
//			System.out.println(bin[0]);
//			bin[0] = (byte) (bin[0]|1536);
//			System.out.println(bin[0]);
//			
			results = md.digest(rs); 
			resultString = byteArrayToHexString(results).toUpperCase();
			//System.out.println(resultString);
			
//			byte[] vb = "!RQM".toUpperCase().getBytes();
//			
//			idx = results.length;
//			rs = new byte[idx+vb.length];
//			for(int i=0;i<idx;i++){
//				rs[i] = results[i];
//			}
//			for(int i=0;i<vb.length;i++){
//				rs[idx+i] = vb[i]; 
//			}
//			System.out.println(rs.length);
			results = md.digest((resultString+"mmpa".toUpperCase()).getBytes()); 
			
			resultString = byteArrayToHexString(results).toUpperCase();
			System.out.println(resultString);
//			
//			1386680630213
//			1386751012007
//			System.out.println(System.currentTimeMillis());
//			results = md.digest((resultString+"!RQM").getBytes()); 
//			resultString = byteArrayToHexString(results).toUpperCase();
//			
//			System.out.println(resultString);
			
			
			System.out.println(encryptSkey("@FB38DDedE"));//@tMVppQ7kx
			
			String v = "12434-234234-";
			v = v.substring(0, v.length()-1);
			System.out.println(v);
			
			long t = System.currentTimeMillis()|0XFFFFFFFF00000000L;
			System.out.println(t);
			
			System.out.println(URLEncoder.encode("-", "UTF-8"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	  public static String byteArrayToHexString(byte[] b){  
	        StringBuffer resultSb = new StringBuffer();  
	        for (int i = 0; i < b.length; i++){  
	            resultSb.append(byteToHexString(b[i]));  
	        }  
	        return resultSb.toString();  
	    }  
	  public static String byteToHexString(byte b){  
	        int n = b;  
	        if (n < 0)  
	            n = 256 + n;  
	        int d1 = n / 16;  
	        int d2 = n % 16;  
	        return hexDigits[d1] + hexDigits[d2];  
	    }  
	   private final static String[] hexDigits = {"0", "1", "2", "3", "4",  
	        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};  
	   
	public String execute(String cmd) throws Exception {
		Process p = Runtime.getRuntime().exec("cmd /c " + cmd);
		StringBuilder result = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream(), "GB2312"));
		String line;
		while ((line = br.readLine()) != null) {
			result.append(line + "\n");
		}
		return result.toString();
	}
	
	//2045554247
	public static long encryptSkey(String sKey){
		   long i = 5381;
		   int j = 0;
		   int k = sKey.length();
//		   System.out.println(6190419136L&0XFFFFFFFFL);
		   while(j<k){
			   long n = i<<5;
			   if((n&0x80000000L)==0x80000000L){//最高位为1
				   n = n | 0xFFFFFFFF00000000L;
			   }else {
				   n = n & 0XFFFFFFFFL;
			   }
//			   System.out.println(i);
//			   System.out.println(n);
//			   System.out.println((byte)sKey.charAt(j));
			   i = i + (n + (byte)sKey.charAt(j));
			   //System.out.println((byte)sKey.charAt(j));
//			   System.out.println("------->"+i);
			   j++;
		   }
		   return i & 2147483647L;
	   }
}
