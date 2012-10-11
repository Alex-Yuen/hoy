package ws.hoyland.tea;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Tea
{
	private int[] DEFAULT_KEY = null;

	public Tea()
	{
		DEFAULT_KEY = new int[] { 0x789f5645, 0xf68bd5a4, 0x81963ffa, 0x458fac58 };
	}

	public Tea(int[] KEY)
	{
		this.DEFAULT_KEY = KEY;
	}

	public byte[] encrypt(byte[] content, int offset, int[] key, int times)
	{
		int[] tempInt = byteToInt(content, offset);
		int y = tempInt[0], z = tempInt[1], sum = 0, i;
		int delta = 0x9e3779b9; // 这是算法标准给的值
		int a = key[0], b = key[1], c = key[2], d = key[3];

		for(i = 0; i < times; i++)
		{
			sum += delta;
			y += ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);
			z += ((y << 4) + c) ^ (y + sum) ^ ((y >> 5) + d);
		}
		tempInt[0] = y;
		tempInt[1] = z;
		return intToByte(tempInt, 0);
	}

	public byte[] decrypt(byte[] encryptContent, int offset, int[] key, int times)
	{
		int[] tempInt = byteToInt(encryptContent, offset);
		int y = tempInt[0], z = tempInt[1], sum = 0xC6EF3720, i;
		int delta = 0x9e3779b9; // 这是算法标准给的值
		int a = key[0], b = key[1], c = key[2], d = key[3];

		for(i = 0; i < times; i++)
		{
			z -= ((y << 4) + c) ^ (y + sum) ^ ((y >> 5) + d);
			y -= ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);
			sum -= delta;
		}
		tempInt[0] = y;
		tempInt[1] = z;

		return intToByte(tempInt, 0);
	}

	private int[] byteToInt(byte[] content, int offset)
	{

		int[] result = new int[content.length >> 2];// 除以2的n次方 == 右移n位 即 content.length / 4 == content.length >> 2
		for(int i = 0, j = offset; j < content.length; i++, j += 4)
		{
			result[i] = transform(content[j + 3]) | transform(content[j + 2]) << 8 | transform(content[j + 1]) << 16
					| (int)content[j] << 24;
		}
		return result;

	}

	private byte[] intToByte(int[] content, int offset)
	{
		byte[] result = new byte[content.length << 2];// 乘以2的n次方 == 左移n位 即 content.length * 4 == content.length << 2
		for(int i = 0, j = offset; j < result.length; i++, j += 4)
		{
			result[j + 3] = (byte)(content[i] & 0xff);
			result[j + 2] = (byte)((content[i] >> 8) & 0xff);
			result[j + 1] = (byte)((content[i] >> 16) & 0xff);
			result[j] = (byte)((content[i] >> 24) & 0xff);
		}
		return result;
	}

	private static int transform(byte temp)
	{
		int tempInt = (int)temp;
		if( tempInt < 0 )
		{
			tempInt += 256;
		}
		return tempInt;
	}

	public String encryptByTea(String info)
	{
		return encryptByTea(info, DEFAULT_KEY);
	}

	public String encryptByTea(String info, int[] KEY)
	{
		byte[] temp = info.getBytes();
		int n = 8 - temp.length % 8;// 若temp的位数不足8的倍数,需要填充的位数
		byte[] encryptStr = new byte[temp.length + n];
		encryptStr[0] = (byte)n;
		System.arraycopy(temp, 0, encryptStr, n, temp.length);
		byte[] result = new byte[encryptStr.length];
		for(int offset = 0; offset < result.length; offset += 8)
		{
			//long start_time = System.currentTimeMillis();
			byte[] tempEncrpt = encrypt(encryptStr, offset, KEY, 32);
			//long en_time = System.currentTimeMillis();
			//System.out.println("加密时间=" + (en_time - start_time));
			System.arraycopy(tempEncrpt, 0, result, offset, 8);
		}
		return new String(Hex.encodeHex(result));
	}

	public String decryptByTea(String str)
	{
		byte[] secretInfo;
		try
		{
			secretInfo = Hex.decodeHex(str.toCharArray());
		}
		catch(DecoderException e)
		{
			e.printStackTrace(System.out);
			return null;
		}
		return decryptByTea(secretInfo, DEFAULT_KEY);
	}

	public String decryptByTea(byte[] secretInfo, int[] KEY)
	{
		byte[] decryptStr = null;
		byte[] tempDecrypt = new byte[secretInfo.length];
		for(int offset = 0; offset < secretInfo.length; offset += 8)
		{
			decryptStr = decrypt(secretInfo, offset, KEY, 32);
			System.arraycopy(decryptStr, 0, tempDecrypt, offset, 8);
		}

		int n = tempDecrypt[0];
		return new String(tempDecrypt, n, decryptStr.length - n);
	}

	public static void main(String[] args)
	{
		boolean show_info=true;
		Tea tea = new Tea();

		String info = "[www/拷斯蒂芬第三方|->|orangehf]";
		if(show_info) System.out.println("原数据：" + info);
		
		long start_time = System.currentTimeMillis();
		String encryptInfo = tea.encryptByTea(info);
		long en_time = System.currentTimeMillis();
		System.out.print("加密时间=[" + (en_time - start_time) + "]，加密后的数据：" + new String(encryptInfo));
		
		System.out.println();
		
		start_time = System.currentTimeMillis();
		String decryptInfo = tea.decryptByTea("ba4fc3ff2b22c15d216e8c2c61eec4228edee0b5e9c01bcf");
		en_time = System.currentTimeMillis();
		System.out.print("解密时间=[" + (en_time - start_time) + "]，解密后的数据：[");
		if(show_info) System.out.println(decryptInfo + "]");
	}
}
