package ws.hoyland.as;

import org.apache.commons.codec.binary.Base64;

import ws.hoyland.util.Converts;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "yxZ6rhY0ykpm0IVUH0cJhBOYWcDwodSWjdgJb99h7GVaxxAkzuuTJqydUgF8V6gPI2KZM+/gyQ1tOvSzo0WDwsm2J6gfOdIGJ/Nzlg5chhDtQuKmwEhJ0f9TcqaWJ2eAWodLTh2ELhMu4DFEjbTH5vL63uoFr4xTyK6PsD2pw2A=";
		Base64 base64 = new Base64();
		System.out.println(Converts.bytesToHexString(base64.decode(s)));
	}

}
