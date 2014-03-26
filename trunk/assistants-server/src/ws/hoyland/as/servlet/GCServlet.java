package ws.hoyland.as.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
//import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Random;

import javax.crypto.Cipher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.sql.DataSource;

//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.ClassWriter;
//import org.objectweb.asm.FieldVisitor;
//import org.objectweb.asm.Opcodes;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Crypter;

public class GCServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7633331277396719497L;
	//private static Random RND = new Random();
	private boolean isByte = false;
	private BufferedWriter[] outputs = new BufferedWriter[3];

	
	//private String module = "w5pR+xIC918OIPaRyONwvPp80rdf1YjK2sVJrfHwPP2qzLn7pdchnKSj5A+TJBIUdL6FNVzxeODTvQcZ7fhZ1g0kh0sQX6xz7wZ97pYvXRLH25gwObpe4Bg0eZIxdIhqLEWs/VRBwbL8wgg5UgFsZmMYhFJ1hf9Ea7xPdWBu+Hs=";
	
//	private String exponentString = "AQAB";, pbkexp
	
	//private String delement = "cdMGq9zyXvMwrJvvgABiZYY6RwCwwvkEWsR9uLxWeZd/4fzEZOBIzfe864Tosg/XWYxYxhHc7uOeM5zDSQjBdVjkJKJN8H1JISm9qTWqmZATL03xgItf5glVxupsMBqBXr3FdYJe8PjOmIYXpREBSWvkMfqwcpuaU+zRuOu+FSk=";, prvexp

	private String expBytes = "71D306ABDCF25EF330AC9BEF80006265863A4700B0C2F9045AC47DB8BC5679977FE1FCC464E048CDF7BCEB84E8B20FD7598C58C611DCEEE39E339CC34908C17558E424A24DF07D492129BDA935AA9990132F4DF1808B5FE60955C6EA6C301A815EBDC575825EF0F8CE988617A51101496BE431FAB0729B9A53ECD1B8EBBE1529";
	private String modBytes = "C39A51FB1202F75F0E20F691C8E370BCFA7CD2B75FD588CADAC549ADF1F03CFDAACCB9FBA5D7219CA4A3E40F9324121474BE85355CF178E0D3BD0719EDF859D60D24874B105FAC73EF067DEE962F5D12C7DB983039BA5EE0183479923174886A2C45ACFD5441C1B2FCC2083952016C66631884527585FF446BBC4F75606EF87B";
	
	public GCServlet() {
		try{
			long time = System.currentTimeMillis();
			String[] sn = new String[]{"C://正确-"+time+".txt", "C://错误-"+time+".txt", "C://冻结-"+time+".txt"};
			for(int i=0;i<sn.length;i++){
				if(i!=1){
					File file = new File(sn[i]);
					if(!file.exists()){
						file.createNewFile();
					}
					outputs[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.getOutputStream().println("OK");
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return;
		// RequestDispatcher reqDispatcher =
		// req.getRequestDispatcher("/WEB-INF/view/index.jsp");
		// reqDispatcher.forward(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		byte[] key = null;
		Crypter crypter = new Crypter();
		//Base64 base64 = new Base64();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String result = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
		int aid = 0;
		String resultString = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
		byte[] bstobeoutput = null;

		Connection conn = null;
		Statement stmt = null;
		
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			Cipher cipher = Cipher.getInstance("RSA");
			
			BufferedReader sis = req.getReader();
			char[] buf = new char[1024];
			int len = 0;
			StringBuffer sb = new StringBuffer();
			while ((len = sis.read(buf)) != -1) {
				sb.append(buf, 0, len);
			}
			// System.out.println("A");
			// System.out.println(sb.toString());
			// 解密

			boolean valid = false;
			key = Converts.hexStringToByte(sb.toString().substring(0, 32));
			byte[] fmc = Converts.hexStringToByte(sb.toString().substring(32,
					96)); // System.out.println("B");
			String lmc = Converts.bytesToHexString(crypter.decrypt(fmc, key));// 机器码
			System.out.println(lmc+" post to /gc");
			String JNDINAME = "java:comp/env/jdbc/assistants";
			ResultSet rs = null;

			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(JNDINAME);
			conn = ds.getConnection();
			stmt = conn.createStatement();

			rs = stmt
					.executeQuery("select * from t_machine where machine_code = '"
							+ lmc + "'");
			// System.out.println("select * from t_qqgm where machine_code = '"+lmc+"'");
			// sdf.format(Calendar.getInstance().getTime())
			
			if (rs.next()) {
				// System.out.println("1");
				String expire = rs.getString("expire");
				aid = rs.getInt("aid");
				//System.out.println(expire);
				long itv = sdf.parse(expire).getTime()
						- Calendar.getInstance().getTime().getTime();
//				System.out.println(itv / (24 * 60 * 60 * 1000));
//				System.out.println(itv % (24 * 60 * 60 * 1000));
//				long day = (itv / (24 * 60 * 60 * 1000))
//						+ (((itv % (24 * 60 * 60 * 1000)) > 0) ? 1 : 0);
				if (itv > 0) {
					valid = true;
				} else {
					valid = false;
				}
				// System.out.println("2");
			}else{
				valid = false;
			}
			// System.out.println("3");
			// sdf.parse(result)

			rs.close();

			if(valid){
				//System.out.println("RSA:" + sb.toString().substring(96));
				byte[] rsa = Converts.hexStringToByte(sb.toString().substring(96));
	
//				byte[] expBytes = base64.decode(delement);
//				byte[] modBytes = base64.decode(module);
//	
//				BigInteger modules = new BigInteger(1, modBytes);
//				BigInteger exponent = new BigInteger(1, expBytes);
	
				BigInteger modules = new BigInteger(modBytes, 16);
				BigInteger exponent = new BigInteger(expBytes, 16);
				
				RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(modules,
						exponent);
				PrivateKey privKey = factory.generatePrivate(privSpec);
				cipher.init(Cipher.DECRYPT_MODE, privKey);
				byte[] decrypted = cipher.doFinal(rsa);
	
				String content = new String(decrypted);
	
				if(aid==1){//密保助手
					String[] cts = content.split("&");
					String password = cts[0].split("=")[1];
					String salt = cts[1].split("=")[1];
					String vcode = cts[2].split("=")[1];
		
					byte[] rsx = Converts.MD5Encode(password.getBytes());
					int psz = rsx.length;
		
					byte[] rsb = new byte[psz + 8];
					for (int i = 0; i < psz; i++) {
						rsb[i] = rsx[i];
					}
		
					salt = salt.substring(2);
		
					String[] salts = salt.split("\\\\x");
		
					for (int i = 0; i < salts.length; i++) {
						rsb[psz + i] = (byte) Integer.parseInt(salts[i], 16);
					}
		
					rsx = Converts.MD5Encode(rsb);
					resultString = Converts.bytesToHexString(rsx).toUpperCase();
					rsx = Converts.MD5Encode((resultString + vcode.toUpperCase())
							.getBytes());
		
					resultString = Converts.bytesToHexString(rsx).toUpperCase();
				}else if(aid==2){//申诉助手
					String[] cts = content.split("&");
					String password = cts[0].split("=")[1];
					String ts = cts[1].split("=")[1];
										
					RSAPublicKey pbk = null;
					KeyFactory keyFac = null;
					try {
						keyFac = KeyFactory.getInstance("RSA");
					} catch (NoSuchAlgorithmException ex) {
						throw new Exception(ex.getMessage());
					}

					RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
							new BigInteger("CF87D7B4C864F4842F1D337491A48FFF54B73A17300E8E42FA365420393AC0346AE55D8AFAD975DFA175FAF0106CBA81AF1DDE4ACEC284DAC6ED9A0D8FEB1CC070733C58213EFFED46529C54CEA06D774E3CC7E073346AEBD6C66FC973F299EB74738E400B22B1E7CDC54E71AED059D228DFEB5B29C530FF341502AE56DDCFE9", 16), 
							new BigInteger("10001", 16));
					
					pbk = (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
					cipher.init(Cipher.ENCRYPT_MODE, pbk);					
					byte[] ecrypted = cipher.doFinal((password+"\n"+ts+"\n").getBytes());
					
					resultString = Converts.bytesToHexString(ecrypted);
				}else if(aid==3){//QQ在线
					resultString = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF3";
				}else if(aid==4){//晒密助手
					String[] cts = content.split("&");
					String action = cts[0].split("=")[1];
					String type = cts[1].split("=")[1];
					String ct = cts[2].split("=")[1];
					
					if("1".equals(action)){
						if("0".equals(type)||"2".equals(type)){
							//写文件
							int it = Integer.parseInt(type);
							String[] line = ct.split("----");
							//System.err.println("ct="+ct);
							outputs[it].write(line[0]+"----"+line[1]+"\r\n");
							outputs[it].flush();
//							stmt
//							.executeUpdate("INSERT INTO t_upload (account, pwd, type) VALUES ('"+line[0]+"', '"+line[1]+"', "+type+")");
						}
						
						resultString = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF14";
					}
					
					//resultString = Converts.MD5EncodeToHex(password);
					
					/**
					ClassWriter cw = new ClassWriter(0);
					cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC,
							 "ws.hoyland.sm/Dynamicer", null, "java/lang/Object", null);
					 
					FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, "URL", "Ljava/lang/String;", null, null);
					fv
				    fv.visitEnd();
				    
					//动态生成类
					InputStream input = this.getClass().getResourceAsStream("/Dynamicer");
					bstobeoutput = new byte[input.available()];
					input.read(bstobeoutput);
					isByte = true;
					**/
					
					//Dynamicer
					else{
						resultString = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFF04";
					}
//					String url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=" + String.valueOf(RND.nextDouble()) + "&qq=" + account + "&pmd5=" + Converts.bytesToHexString(Converts.MD5Encode(password)) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";					
//					resultString = Converts.bytesToHexString(crypter.encrypt(url.getBytes(), key));
				}else{
					resultString = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0";
				}
				result = resultString;
			}else{
				result = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";	
			}
		} catch (Exception e) {
			e.printStackTrace();
			// resp.getOutputStream().println("OK");
		}finally{
			if(stmt!=null){
				try{
					stmt.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try{
					conn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		// result = Converts.bytesToHexString(crypter.encrypt(result.getBytes(),
		// key));

		resp.setContentType("text/html;charset=UTF-8");
		if(isByte){
			resp.getOutputStream().write(bstobeoutput);
		}else{
			resp.getOutputStream().print(result);
		}
		resp.getOutputStream().flush();
		resp.getOutputStream().close();
		return;
	}
}
