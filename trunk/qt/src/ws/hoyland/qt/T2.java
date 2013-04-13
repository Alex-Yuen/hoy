package ws.hoyland.qt;

import java.math.BigInteger;

public class T2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger root = new BigInteger("2");
		BigInteger e = new BigInteger("-1");
		BigInteger c = new BigInteger("36B024FC30E4215E6E6F7E1CF8F3", 16);
		String os = root.modPow(e, c).toString(32).toUpperCase();
		
		//int i = os.length();
		if(os.length()<22){
			for(int i=0;i<22-os.length();i++){
				os = "0" + os;
			}
		}
		
		//System.out.println(os);
		char[] osc = ("27"+os).toCharArray();

		if(osc[0]=='I'){
			osc[0] = (char)89;
		}
		
		for(int i=1;i<osc.length;i++){
			if(osc[0]=='O'){
				osc[0] = (char)88;
			}
		}
		
		os = String.valueOf(osc);
		System.out.println(os);
		
		byte[] out = Converts.MD5Encode(os);
		
		
//		 byte[] arrayOfByte2 = localb2.b(arrayOfByte1);
//		    byte[] arrayOfByte3 = localb1.b(arrayOfByte2);
//		    byte[] arrayOfByte4 = new byte[arrayOfByte3.length * 2];
//		    int i2 = 0;
//		    while (true)
//		    {
//		      int i3 = arrayOfByte3.length;
//		      if (i2 >= i3)
//		        break;
//		      int i4 = i2 * 2;
//		      int i5 = (byte)((arrayOfByte3[i2] & 0xFF) >>> 4);
//		      arrayOfByte4[i4] = i5;
//		      int i6 = i2 * 2 + 1;
//		      int i7 = (byte)(arrayOfByte3[i2] & 0xF);
//		      arrayOfByte4[i6] = i7;
//		      i2 += 1;
//		    }
//		    int i1 = 0;
//		    while (i1 < 16)
//		    {
//		      i2 = 0;
//		      int i8 = 0;
//		      while (i2 < 4)
//		      {
//		        int i9 = i2 * 16 + i1;
//		        int i10 = arrayOfByte4[i9];
//		        i8 += i10;
//		        i2 += 1;
//		      }
//		      int i11 = i8 % 10;
//		      arrayOfInt[i1] = i11;
//		      i1 += 1;
//		    }
//		    if (arrayOfInt[0] == 0)
//		      arrayOfInt[0] = 1;
//		    return arrayOfInt;
	}

}
