package ws.hoyland.qqonline;

import java.util.Enumeration;
import java.util.Random;
import java.util.zip.CRC32;

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
		
		//09  CD FE F2
		//9CDFEF
		String d = "PPSM";
		byte[] bs = d.getBytes();
		for(int i=0;i<bs.length;i++){
			System.out.println(bs[i]);
		}
		System.out.println(Converts.bytesToHexString(d.getBytes()));
		System.out.println(Integer.toHexString(Integer.valueOf(164495090)).toUpperCase());
		//byte[] xx = Converts.hexStringToByte("9cdfef2".toUpperCase());
		System.out.println(Converts.bytesToHexString(Converts.hexStringToByte(Integer.toHexString(Integer.valueOf(164495090)).toUpperCase())));
		/**
		CRC32 crc = new CRC32();
		crc.reset();
		crc.update(new byte[]{
				0x62, (byte)0x98, 0x45, 0x7F, 0x7E, 0x2C, 0x0D, (byte)0xD8, (byte)0x9E, (byte)0x8B, (byte)0xF7, 0x4B, (byte)0xF9, (byte)0x9F, (byte)0x93, 0x31
		});
		
		System.out.println(Long.toHexString(crc.getValue()).toUpperCase());
		crc.update(new byte[]{
				0x65, (byte)0x94, 0x0E, 0x7E, (byte)0xC9, (byte)0x83, 0x36, 0x69, 0x1E, (byte)0x87, 0x63, 0x77, 0x73, 0x41, 0x43, (byte)0xEB
		});
		System.out.println(Long.toHexString(crc.getValue()).toUpperCase());
		
		crc.reset();
		crc.update(new byte[]{
				0x65, (byte)0x94, 0x0E, 0x7E, (byte)0xC9, (byte)0x83, 0x36, 0x69, 0x1E, (byte)0x87, 0x63, 0x77, 0x73, 0x41, 0x43, (byte)0xEB
		});
		System.out.println(Long.toHexString(crc.getValue()).toUpperCase());
		**/
		long time = System.currentTimeMillis();
		//System.out.println(Integer.toHexString((int)(time&0xFF00)));
		System.out.println(Long.toHexString(time/1000).toUpperCase());
		System.out.println((time));
		
		Random rnd = new Random();
		System.out.println((short)rnd.nextInt(0xFFFF));
		
	}

}
