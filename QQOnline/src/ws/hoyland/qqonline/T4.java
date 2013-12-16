package ws.hoyland.qqonline;

import java.util.Enumeration;

import org.bouncycastle.asn1.x9.ECNamedCurveTable;

public class T4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Enumeration<?> names = ECNamedCurveTable.getNames();
		while(names.hasMoreElements()){
			System.out.println(names.nextElement());
		}
	}

}
