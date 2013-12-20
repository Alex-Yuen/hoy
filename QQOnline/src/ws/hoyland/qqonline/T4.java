package ws.hoyland.qqonline;

import java.util.Enumeration;

import org.bouncycastle.asn1.x9.ECNamedCurveTable;

import ws.hoyland.util.Converts;

public class T4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Enumeration<?> names = ECNamedCurveTable.getNames();
//		while(names.hasMoreElements()){
//			System.out.println(names.nextElement());
//		}
		String d = "PPSM";
		byte[] bs = d.getBytes();
		for(int i=0;i<bs.length;i++){
			System.out.println(bs[i]);
		}
		System.out.println(Converts.bytesToHexString(d.getBytes()));
	}

}
