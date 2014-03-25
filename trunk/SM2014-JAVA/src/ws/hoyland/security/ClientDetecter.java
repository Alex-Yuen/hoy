package ws.hoyland.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import ws.hoyland.util.Converts;

public class ClientDetecter {

	private static String MID = null;
	public ClientDetecter() {
		// TODO Auto-generated constructor stub
	}

	public static String getMachineID(String applicationName) {
		//String machineID = null;
		String line = null;

		try {
			if(MID==null){
				Process proces = Runtime.getRuntime().exec("cmd /c dir c:");// 获取命令行参数
				BufferedReader buffreader = new BufferedReader(
						new InputStreamReader(proces.getInputStream(), "GB2312"));
				while ((line = buffreader.readLine()) != null) {
					//System.out.println(line);
					if (line.indexOf("卷的序列号是 ") != -1) { // 读取参数并获取硬盘序列号
						MID = line.substring(line.indexOf("卷的序列号是 ")
								+ "卷的序列号是 ".length(), line.length());
						break;
						// System.out.println(HdSerial);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Converts.MD5EncodeToHex(MID+applicationName);
	}
}
