package ws.hoyland.cc;

import java.math.BigInteger;

public class T4 {

	public T4() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//测试g值是否有改变。
		
		BigInteger b1 = new BigInteger("2");
		//e
		BigInteger b2 = null;
		//d
		BigInteger b3 = new BigInteger("B8008767A628A4F53BCB84C13C961A55BF87607DAA5BE0BA3AC2E0CB778E494579BD444F699885F4968CD9028BB3FC6FA657D532F1718F581669BDC333F83DC3", 16);
		
		//exchange's pub key
		BigInteger b4 = new BigInteger("82C49C3C3F122047F38F06DC38E05A967C573D446E1E428828942BD7248EC301B79A3110C3D1F1357C9A1922C79C424F12CC521585D8E2B31B8DF57AF31C3B32", 16);
		
//		2^e mod d = crypt
//		g^e mod d = key
//		
//		收到pubkey之后，g=new BigInteger(pubkey, 16)
//		
//		2^b2 mod b3 = b4
//		
//		(-1)^b2 mod b3 = kk
//		
//				key = md5(hexStringToByte(kk.toString(16)))	//121, 114开头.
//		
//			32 mod 9 = 5
	}
//	
//		2^b2 = b3*i + b4
//	    (-1)^b2 = b3*j + kk
//	    
//	    1.如果b2是偶数
//	    
//	    2^b2 = b3*i + b4
//	    1=b3*j + kk
//
//	    mbtoken3_exchange_key_v2 什么时候上来的？
//	    
//	    
//	    
//	    生成e
//	    
//	  2^e mod d 作为发送的参数过去 c  (求得dd)
//	 c^dd mod d = 2
//	 
//	 key^dd mod d = pukey
//	  
//	  收到pubkey
//	 
//	 g=pubkey>>>>>>>>>>>>>>>>>>>g解密为key，用于加密
//	 i.b = g^e mod d
	 
}
