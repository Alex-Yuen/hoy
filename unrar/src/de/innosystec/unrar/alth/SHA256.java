package de.innosystec.unrar.alth;

/*@author  liulang
 *@version  09.3.22
 *@主要是有String实现
 *@从文件“msg.txt"中读入消息
 *@在将加密的细节写入到"SHA_256.txt"文件中
 */
import java.io.*;

public class SHA256 {
	static String msg_binary = new String();// 消息用二进制表示
	static StringBuffer str2 = new StringBuffer();// 可变字符串
	static String H0 = "6a09e667";// 初始值
	static String H1 = "bb67ae85";
	static String H2 = "3c6ef372";
	static String H3 = "a54ff53a";
	static String H4 = "510e527f";
	static String H5 = "9b05688c";
	static String H6 = "1f83d9ab";
	static String H7 = "5be0cd19";
	static String A, B, C, D, E, F, G, H;
	static PrintWriter out; // 输入到文件
	static File file; // 文件名
	static FileReader fls;
	static BufferedReader in; // 文件输入流
	static long begin; // 程序运行前的时间
	static long end; // 程序运行后的时间
	static String[] k = new String[64];
	static String[] K = { "428a2f98", "71374491", "b5c0fbcf", "e9b5dba5",
			"3956c25b", "59f111f1", "923f82a4", "ab1c5ed5", "d807aa98",
			"12835b01", "243185be", "550c7dc3", "72be5d74", "80deb1fe",
			"9bdc06a7", "c19bf174", "e49b69c1", "efbe4786", "0fc19dc6",
			"240ca1cc", "2de92c6f", "4a7484aa", "5cb0a9dc", "76f988da",
			"983e5152", "a831c66d", "b00327c8", "bf597fc7", "c6e00bf3",
			"d5a79147", "06ca6351", "14292967", "27b70a85", "2e1b2138",
			"4d2c6dfc", "53380d13", "650a7354", "766a0abb", "81c2c92e",
			"92722c85", "a2bfe8a1", "a81a664b", "c24b8b70", "c76c51a3",
			"d192e819", "d6990624", "f40e3585", "106aa070", "19a4c116",
			"1e376c08", "2748774c", "34b0bcb5", "391c0cb3", "4ed8aa4a",
			"5b9cca4f", "682e6ff3", "748f82ee", "78a5636f", "84c87814",
			"8cc70208", "90befffa", "a4506ceb", "bef9a3f7", "c67178f2" };
	static String[] w = new String[80];
	static int group_num = 1;// 组数
	static int mod = 0;// 最后一组的原始长度

	public static void main(String[] args) throws IOException {
		try {
			file = new File("msg.txt");
			boolean createok;
			if (!file.exists()) // 判断文件是否存在
				createok = file.createNewFile(); // 如果文件不存在，则在当前目录创建文件
			fls = new FileReader(file);
			in = new BufferedReader(fls);
			String msg = new String();
			msg = in.readLine();

			begin = System.currentTimeMillis();
			out = new PrintWriter(new FileWriter("SHA_256.txt"));

			for (int i = 0; i < 64; i++)
				k[i] = hexToBinary(K[i]);
			msg_binary = stringToBinary(msg);
			final int SIZE = msg_binary.length();// 消息变成二进制后的长度
			out.println(msg.length());

			mod = SIZE % 512;

			out.println(msg_binary);
			out.println(SIZE);
			if (SIZE < 448)
				group_num = 1;
			else if (SIZE >= 448 && SIZE <= 512)
				group_num = 2;
			else {
				if (mod < 448)
					group_num = SIZE / 512 + 1;
				else
					group_num = SIZE / 512 + 2;
			}
			char[] cw = new char[512 * group_num];// /liulang

			for (int i = 0; i < SIZE; i++) {// 填充位数
				cw[i] = msg_binary.charAt(i);
			}

			String str1 = new String(Integer.toBinaryString(SIZE));
			if (SIZE < 448) {
				cw[SIZE] = '1';
				for (int i = SIZE + 1; i < 512 * group_num - str1.length(); i++) {
					cw[i] = '0';
				}
				for (int i = 512 * group_num - str1.length(); i < 512 * group_num; i++) {
					cw[i] = str1.charAt(i - 512 * group_num + str1.length());
				}
			}
			if (SIZE >= 448 && SIZE <= 512) {
				cw[SIZE] = '1';
				for (int i = SIZE + 1; i < 512 * group_num - str1.length(); i++) {
					cw[i] = '0';
				}
				for (int i = 512 * group_num - str1.length(); i < 512 * group_num; i++) {
					cw[i] = str1.charAt(i - 512 * group_num + str1.length());
				}
			}
			if (SIZE > 512) {
				cw[SIZE] = '1';
				for (int i = SIZE + 1; i < 512 * group_num - str1.length(); i++) {
					cw[i] = '0';
				}
				for (int i = 512 * group_num - str1.length(); i < 512 * group_num; i++) {
					cw[i] = str1.charAt(i - 512 * group_num + str1.length());
				}
			}

			str2 = str2.delete(0, str2.length());// delete str2=null;
			for (int i = 0; i < 512 * group_num; i++) {
				// System.out.print(cw[i]);
				// if((i+1)%8==0)
				// System.out.print(" ");
				str2 = str2.append(cw[i]);
			}
			out.println();
			for (int n = 0; n < group_num; n++) {
				out.println("第" + (n + 1) + "组:");
				// w[0] to w[80]
				String str3 = new String();// 存放每一个分组的512位
				str3 = str2.substring(n * 512, (n + 1) * 512).toString();// 取每一组的512为
				for (int i = 0; i < 16; i++) {
					w[i] = str3.substring(i * 32, (i + 1) * 32);
					out.println("w" + i + "  " + binaryToHex(w[i]));
				}
				for (int i = 16; i < 64; i++) {// liulang
					w[i] = binaryplus(
							binaryplus(small_sigma_one(w[i - 2]), w[i - 7]),
							binaryplus(small_sigma_zero(w[i - 15]), w[i - 16]));
					out.println("w" + i + "  " + binaryToHex(w[i]));
				}

				A = new String(hexToBinary(H0));
				B = new String(hexToBinary(H1));
				C = new String(hexToBinary(H2));
				D = new String(hexToBinary(H3));
				E = new String(hexToBinary(H4));
				F = new String(hexToBinary(H5));
				G = new String(hexToBinary(H6));
				H = new String(hexToBinary(H7));

				calculate_sha_256(A, B, C, D, E, F, G, H);
			}

		} catch (FileNotFoundException fe) {
			System.out.println("the file don't exist");
		} catch (NullPointerException fe) {
			System.out
					.println("the file is null,please inputting the context to the file");
		}

		end = System.currentTimeMillis();
		System.out.println(H0 + H1 + H2 + H3 + H4);
		System.out.println("运行时间" + (end - begin) + "ms");
		out.close();
		in.close();
	}

	// 计算A,B,C,D,E,F,G,H函数
	public static void calculate_sha_256(String A, String B, String C,
			String D, String E, String F, String G, String H) {
		String temp1 = new String();
		String temp2 = new String();
		for (int i = 0; i < 64; i++) {
			out.print("i=" + i + "  ");
			temp1 = T1(H, E, ch(E, F, G), w[i], k[i]);
			temp2 = binaryplus(temp1, T2(A, maj(A, B, C)));
			H = G;
			G = F;
			F = E;
			E = binaryplus(D, temp1);
			D = C;
			C = B;
			B = A;
			A = temp2;
			display(A, B, C, D, E, F, G, H);
		}
		H0 = binaryToHex(binaryplus(A, hexToBinary(H0)));
		H1 = binaryToHex(binaryplus(B, hexToBinary(H1)));
		H2 = binaryToHex(binaryplus(C, hexToBinary(H2)));
		H3 = binaryToHex(binaryplus(D, hexToBinary(H3)));
		H4 = binaryToHex(binaryplus(E, hexToBinary(H4)));
		H5 = binaryToHex(binaryplus(F, hexToBinary(H5)));
		H6 = binaryToHex(binaryplus(G, hexToBinary(H6)));
		H7 = binaryToHex(binaryplus(H, hexToBinary(H7)));
		out.println(H0 + " " + H1 + " " + H2 + " " + H3 + " " + H4 + " " + H5
				+ " " + H6 + " " + H7);
		// System.out.println(H0+H1+H2+H3+H4+H5+H6+H7);
	}

	// 从字符串(ASCII)到二进制
	public static String stringToBinary(String str) {
		StringBuffer str2 = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			str2 = str2.append(fill_zero(
					Integer.toBinaryString(Integer.valueOf(str.charAt(i))), 8));
		}
		return str2.toString();
	}

	// 按位填充函数
	public static String fill_zero(String str, int n) {
		String str2 = new String();
		StringBuffer str1 = new StringBuffer();

		if (str.length() < n)
			for (int i = 0; i < n - str.length(); i++) {
				str2 = str1.append('0').toString();
			}
		return str2 + str;// Integer.toHexString(Integer.valueOf(str2+s,2))+" ";//换成十六进制;
	}

	// 按位异或
	public static String bit_df_or(String str1, String str2) {
		String str = new String();
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < str1.length(); i++) {
			if (str1.charAt(i) == str2.charAt(i))
				str = s.append('0').toString();
			else
				str = s.append('1').toString();
		}
		return str;
	}

	// 按位同或
	public static String bit_sa_or(String str1, String str2) {
		String str = new String();
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < str1.length(); i++) {
			if (str1.charAt(i) == str2.charAt(i))
				str = s.append('1').toString();
			else
				str = s.append('0').toString();
		}
		return str;
	}

	// 按位与
	public static String bit_and(String str1, String str2) {
		String str = new String();
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < str1.length(); i++) {
			if (str1.charAt(i) == '0' || str2.charAt(i) == '0')
				str = s.append('0').toString();
			else
				str = s.append('1').toString();
		}
		return str;
	}

	// 按位或
	public static String bit_or(String str1, String str2) {
		String str = new String();
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < str1.length(); i++) {
			if (str1.charAt(i) == '1' || str2.charAt(i) == '1')
				str = s.append('1').toString();
			else
				str = s.append('0').toString();
		}
		return str;
	}

	// 按位非
	public static String bit_non(String str1) {
		String str = new String();
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < str1.length(); i++) {
			if (str1.charAt(i) == '0')
				str = s.append('1').toString();
			else
				str = s.append('0').toString();
		}
		return str;
	}

	// 按位循环左移n位
	public static String rotl(String str, int n) {
		String str1 = str.substring(0, n);
		String str2 = str.substring(n);
		return str2 + str1;
	}

	// 按位循环右移n位
	public static String rotr(String str, int n) {
		String str1 = str.substring(str.length() - n);
		String str2 = str.substring(0, str.length() - n);
		return str1 + str2;
	}

	// 按位右移n位
	public static String shr(String str, int n) {
		char[] fillZero = new char[n];
		java.util.Arrays.fill(fillZero, '0');
		String str1 = str.substring(0, str.length() - n);
		return new String(fillZero) + str1;
	}

	public static String ch(String str1, String str2, String str3) {
		return bit_df_or(bit_and(str1, str2), bit_and(bit_non(str1), str3));
	}

	public static String maj(String str1, String str2, String str3) {
		return bit_df_or(bit_df_or(bit_and(str1, str2), bit_and(str1, str3)),
				bit_and(str2, str3));
	}

	public static String big_sigma_zero(String str1) {
		return bit_df_or(bit_df_or(rotr(str1, 2), rotr(str1, 13)),
				rotr(str1, 22));
	}

	public static String big_sigma_one(String str1) {
		return bit_df_or(bit_df_or(rotr(str1, 6), rotr(str1, 11)),
				rotr(str1, 25));
	}

	public static String small_sigma_zero(String str1) {
		return bit_df_or(bit_df_or(rotr(str1, 7), rotr(str1, 18)), shr(str1, 3));
	}

	public static String small_sigma_one(String str1) {
		return bit_df_or(bit_df_or(rotr(str1, 17), rotr(str1, 19)),
				shr(str1, 10));
	}

	// 按位相加
	public static String binaryplus(String str1, String str2) {
		char[] cstr = new char[32];
		int flag = 0;
		for (int i = str1.length() - 1; i >= 0; i--) {
			cstr[i] = (char) (((str1.charAt(i) - '0')
					+ ((str2.charAt(i) - '0')) + flag) % 2 + '0');
			if (((str1.charAt(i) - '0') + (str2.charAt(i) - '0') + flag) >= 2)
				flag = 1;
			else
				flag = 0;
		}
		return new String(cstr);
	}

	// 二进制到十六进制
	public static String binaryToHex(String str) {
		int temp = 0;
		// String str1=new String();
		StringBuffer st = new StringBuffer();
		for (int i = 0; i < str.length() / 4; i++) {
			temp = Integer.valueOf(str.substring(i * 4, (i + 1) * 4), 2);
			st = st.append(Integer.toHexString(temp));
		}
		return st.toString();
	}

	// 从十六进制到二进制
	public static String hexToBinary(String str) {
		StringBuffer st1 = new StringBuffer();
		String st = new String();
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case '0':
				st = "0000";
				break;
			case '1':
				st = "0001";
				break;
			case '2':
				st = "0010";
				break;
			case '3':
				st = "0011";
				break;
			case '4':
				st = "0100";
				break;
			case '5':
				st = "0101";
				break;
			case '6':
				st = "0110";
				break;
			case '7':
				st = "0111";
				break;
			case '8':
				st = "1000";
				break;
			case '9':
				st = "1001";
				break;
			case 'a':
				st = "1010";
				break;
			case 'b':
				st = "1011";
				break;
			case 'c':
				st = "1100";
				break;
			case 'd':
				st = "1101";
				break;
			case 'e':
				st = "1110";
				break;
			case 'f':
				st = "1111";
				break;
			default:
				break;

			}
			st1 = st1.append(st);
		}
		return st1.toString();
	}

	// 计算T1
	public static String T1(String str_h, String str_e, String str_ch,
			String str_w, String str_k) {
		return binaryplus(
				binaryplus(binaryplus(str_h, big_sigma_one(str_e)),
						binaryplus(str_ch, str_w)), str_k);
	}

	// 计算T2
	public static String T2(String str_a, String str_maj) {
		return binaryplus(big_sigma_zero(str_a), str_maj);
	}

	// 打印出A,B,C,D,E,F,G,H
	public static void display(String A, String B, String C, String D,
			String E, String F, String G, String H) {
		out.print("A=" + binaryToHex(A) + "  ");
		out.print("B=" + binaryToHex(B) + "  ");
		out.print("C=" + binaryToHex(C) + "  ");
		out.print("D=" + binaryToHex(D) + "  ");
		out.print("E=" + binaryToHex(E) + "  ");
		out.print("F=" + binaryToHex(F) + "  ");
		out.print("G=" + binaryToHex(G) + "  ");
		out.print("H=" + binaryToHex(H) + "  ");
		out.println();
	}
}