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
		
		String module = "w5pR+xIC918OIPaRyONwvPp80rdf1YjK2sVJrfHwPP2qzLn7pdchnKSj5A+TJBIUdL6FNVzxeODTvQcZ7fhZ1g0kh0sQX6xz7wZ97pYvXRLH25gwObpe4Bg0eZIxdIhqLEWs/VRBwbL8wgg5UgFsZmMYhFJ1hf9Ea7xPdWBu+Hs=";
//		private String exponentString = "AQAB";
		String delement = "cdMGq9zyXvMwrJvvgABiZYY6RwCwwvkEWsR9uLxWeZd/4fzEZOBIzfe864Tosg/XWYxYxhHc7uOeM5zDSQjBdVjkJKJN8H1JISm9qTWqmZATL03xgItf5glVxupsMBqBXr3FdYJe8PjOmIYXpREBSWvkMfqwcpuaU+zRuOu+FSk=";

		
		byte[] expBytes = base64.decode(delement);
		byte[] modBytes = base64.decode(module);
		
		System.out.println(Converts.bytesToHexString(expBytes));
		System.out.println(Converts.bytesToHexString(modBytes));
		
	}

}
