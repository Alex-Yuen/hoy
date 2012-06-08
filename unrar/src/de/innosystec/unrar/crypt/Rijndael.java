/*
 * Copyright (c) 2007 innoSysTec (R) GmbH, Germany. All rights reserved.
 * Original author: Edmund Wagner
 * Creation date: 31.05.2007
 *
 * the unrar licence applies to all junrar source and binary distributions 
 * you are not allowed to use this source to re-create the RAR compression algorithm
 * Source: $HeadURL$
 * Last changed: $LastChangedDate$
 * 
 * Here some html entities which can be used for escaping javadoc tags:
 * "&":  "&#038;" or "&amp;"
 * "<":  "&#060;" or "&lt;"
 * ">":  "&#062;" or "&gt;"
 * "@":  "&#064;" 
 */
package de.innosystec.unrar.crypt;

/**
 * DOCUMENT ME
 * 
 * @author $LastChangedBy$
 * @version $LastChangedRevision$
 */
public class Rijndael {
	private final int uKeyLenInBytes = 16;
	private final int m_uRounds = 10;
	private final int ff_poly = 0x011b;
	private final int ff_hi = 0x80;

	static byte[] S = new byte[256];
	static byte[] S5 = new byte[256];
	static byte[] rcon = new byte[30];
	static byte[][] T1 = new byte[256][4];
	static byte[][] T2 = new byte[256][4];
	static byte[][] T3 = new byte[256][4];
	static byte[][] T4 = new byte[256][4];
	static byte[][] T5 = new byte[256][4];
	static byte[][] T6 = new byte[256][4];
	static byte[][] T7 = new byte[256][4];
	static byte[][] T8 = new byte[256][4];

	static byte[][] U1 = new byte[256][4];
	static byte[][] U2 = new byte[256][4];
	static byte[][] U3 = new byte[256][4];
	static byte[][] U4 = new byte[256][4];

	private byte[] mInitVector = new byte[16];
	private byte[][][] mExpandedKey = new byte[14 + 1][4][4];

	private void xor128(byte[] dest, final byte[] arg1, final byte[] arg2) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = (byte) (arg1[i] ^ arg2[i]);
		}
	}

	private void xor128(byte[] dest, final byte[] arg1, final byte[] arg2,
			final byte[] arg3, final byte[] arg4) {
		for (int i = 0; i < dest.length; i++) {
			dest[i] = (byte) (arg1[i] ^ arg2[i] ^ arg3[i] ^ arg4[i]);
		}
	}

	private void copy128(byte[] dest, final byte[] src) {
		for (int i = 0; i < src.length; i++) {
			dest[i] = src[i];
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// API
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Rijndael() {
		if (S[0] == 0)
			generateTables();
	}

	public void init(boolean direction, final byte[] key, byte[] initVector) {

		byte[][] keyMatrix = new byte[256 / 32][4];

		for (int i = 0; i < uKeyLenInBytes; i++) {
			keyMatrix[i >> 2][i & 3] = key[i];
		}

		for (int i = 0; i < 16; i++) {
			mInitVector[i] = initVector[i];
		}

		keySched(keyMatrix);

		if (!direction) // decrypt
			keyEncToDec();
	}

	public int blockDecrypt(final byte[] input, int length, byte[] outBuffer) {
		if (input == null || length <= 0)
			return 0;

		byte[] block = new byte[16];
		byte[] iv = new byte[16];
		byte[] ip = new byte[16];
		byte[] op = new byte[16];

		// for(int i=0;i<iv.length;i++){
		// iv[i] = mInitVector[i];
		// }
		int idx = 0;
		int odx = 0;

		copy128(iv, mInitVector);

		int numBlocks = length / 16;
		for (int i = numBlocks; i > 0; i--) {
			for (int m = 0; m < ip.length; m++) {
				ip[m] = input[m + idx];
			}

			decrypt(ip, block);
			xor128(block, block, iv);

			copy128(iv, ip);
			copy128(op, block);

			for (int m = 0; m < op.length; m++) {
				outBuffer[m + odx] = op[m];
			}

			idx += 16;
			odx += 16;
		}

		copy128(mInitVector, iv);

		return 16 * numBlocks;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ALGORITHM
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void keySched(byte[][] key)// [256/32][4]
	{
		int j, rconpointer = 0;

		// Calculate the necessary round keys
		// The number of calculations depends on keyBits and blockBits
		int uKeyColumns = m_uRounds - 6;

		byte[][] tempKey = new byte[256 / 32][4];

		// Copy the input key to the temporary key matrix
		for (int m = 0; m < tempKey.length; m++) {
			for (int n = 0; n < tempKey[m].length; n++) {
				tempKey[m][n] = key[m][n];
			}
		}

		int r = 0;
		int t = 0;

		// copy values into round key array
		for (j = 0; (j < uKeyColumns) && (r <= m_uRounds);) {
			for (; (j < uKeyColumns) && (t < 4); j++, t++)
				for (int k = 0; k < 4; k++)
					mExpandedKey[r][t][k] = tempKey[j][k];

			if (t == 4) {
				r++;
				t = 0;
			}
		}

		while (r <= m_uRounds) {
			tempKey[0][0] ^= S[tempKey[uKeyColumns - 1][1]];
			tempKey[0][1] ^= S[tempKey[uKeyColumns - 1][2]];
			tempKey[0][2] ^= S[tempKey[uKeyColumns - 1][3]];
			tempKey[0][3] ^= S[tempKey[uKeyColumns - 1][0]];
			tempKey[0][0] ^= rcon[rconpointer++];

			if (uKeyColumns != 8)
				for (j = 1; j < uKeyColumns; j++)
					for (int k = 0; k < 4; k++)
						tempKey[j][k] ^= tempKey[j - 1][k];
			else {
				for (j = 1; j < uKeyColumns / 2; j++)
					for (int k = 0; k < 4; k++)
						tempKey[j][k] ^= tempKey[j - 1][k];

				tempKey[uKeyColumns / 2][0] ^= S[tempKey[uKeyColumns / 2 - 1][0]];
				tempKey[uKeyColumns / 2][1] ^= S[tempKey[uKeyColumns / 2 - 1][1]];
				tempKey[uKeyColumns / 2][2] ^= S[tempKey[uKeyColumns / 2 - 1][2]];
				tempKey[uKeyColumns / 2][3] ^= S[tempKey[uKeyColumns / 2 - 1][3]];
				for (j = uKeyColumns / 2 + 1; j < uKeyColumns; j++)
					for (int k = 0; k < 4; k++)
						tempKey[j][k] ^= tempKey[j - 1][k];
			}
			for (j = 0; (j < uKeyColumns) && (r <= m_uRounds);) {
				for (; (j < uKeyColumns) && (t < 4); j++, t++)
					for (int k = 0; k < 4; k++)
						mExpandedKey[r][t][k] = tempKey[j][k];
				if (t == 4) {
					r++;
					t = 0;
				}
			}
		}
	}

	private void keyEncToDec() {
		for (int r = 1; r < m_uRounds; r++) {
			byte[][] n_expandedKey = new byte[4][4];
			byte[] w = new byte[4];
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					w = mExpandedKey[r][j];
					n_expandedKey[j][i] = (byte) (U1[w[0]][i] ^ U2[w[1]][i]
							^ U3[w[2]][i] ^ U4[w[3]][i]);
				}
			}

			for (int i = 0; i < n_expandedKey.length; i++) {
				for (int j = 0; j < n_expandedKey[i].length; j++) {
					mExpandedKey[r][i][j] = n_expandedKey[i][j];
				}
			}
		}
	}

	private void decrypt(final byte[] a, final byte[] b) // [16], [16]
	{
		int r;
		byte[][] temp = new byte[4][4];
		byte[] t = new byte[16];
		byte[] m = new byte[16];
		int k = 0;

		for (int i = 0; i < mExpandedKey[m_uRounds].length; i++) {
			for (int j = 0; j < mExpandedKey[m_uRounds][i].length; j++) {
				m[k++] = mExpandedKey[m_uRounds][i][j];
			}
		}

		xor128(t, a, m);

		k = 0;
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				temp[i][j] = t[k++];
			}
		}

		byte[] bx = new byte[4];
		int bdx = 0;

		xor128(bx, T5[temp[0][0]], T6[temp[3][1]], T7[temp[2][2]],
				T8[temp[1][3]]);
		for (int i = 0; i < 4; i++) {
			b[i + bdx] = bx[i];
		}
		bdx += 4;

		xor128(bx, T5[temp[1][0]], T6[temp[0][1]], T7[temp[3][2]],
				T8[temp[2][3]]);
		for (int i = 0; i < 4; i++) {
			b[i + bdx] = bx[i];
		}
		bdx += 4;

		xor128(bx, T5[temp[2][0]], T6[temp[1][1]], T7[temp[0][2]],
				T8[temp[3][3]]);
		for (int i = 0; i < 4; i++) {
			b[i + bdx] = bx[i];
		}
		bdx += 4;

		xor128(bx, T5[temp[3][0]], T6[temp[2][1]], T7[temp[1][2]],
				T8[temp[0][3]]);
		for (int i = 0; i < 4; i++) {
			b[i + bdx] = bx[i];
		}

		for (r = m_uRounds - 1; r > 1; r--) {
			k = 0;

			for (int i = 0; i < mExpandedKey[r].length; i++) {
				for (int j = 0; j < mExpandedKey[r][i].length; j++) {
					m[k++] = mExpandedKey[r][i][j];
				}
			}
			xor128(t, b, m);

			k = 0;
			for (int i = 0; i < temp.length; i++) {
				for (int j = 0; j < temp[i].length; j++) {
					temp[i][j] = t[k++];
				}
			}
			bdx = 0;

			xor128(bx, T5[temp[0][0]], T6[temp[3][1]], T7[temp[2][2]],
					T8[temp[1][3]]);
			for (int i = 0; i < 4; i++) {
				b[i + bdx] = bx[i];
			}
			bdx += 4;

			xor128(bx, T5[temp[1][0]], T6[temp[0][1]], T7[temp[3][2]],
					T8[temp[2][3]]);
			for (int i = 0; i < 4; i++) {
				b[i + bdx] = bx[i];
			}
			bdx += 4;

			xor128(bx, T5[temp[2][0]], T6[temp[1][1]], T7[temp[0][2]],
					T8[temp[3][3]]);
			for (int i = 0; i < 4; i++) {
				b[i + bdx] = bx[i];
			}
			bdx += 4;

			xor128(bx, T5[temp[3][0]], T6[temp[2][1]], T7[temp[1][2]],
					T8[temp[0][3]]);
			for (int i = 0; i < 4; i++) {
				b[i + bdx] = bx[i];
			}
		}

		k = 0;

		for (int i = 0; i < mExpandedKey[1].length; i++) {
			for (int j = 0; j < mExpandedKey[1][i].length; j++) {
				m[k++] = mExpandedKey[1][i][j];
			}
		}

		xor128(t, b, m);

		k = 0;
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				temp[i][j] = t[k++];
			}
		}

		b[0] = S5[temp[0][0]];
		b[1] = S5[temp[3][1]];
		b[2] = S5[temp[2][2]];
		b[3] = S5[temp[1][3]];
		b[4] = S5[temp[1][0]];
		b[5] = S5[temp[0][1]];
		b[6] = S5[temp[3][2]];
		b[7] = S5[temp[2][3]];
		b[8] = S5[temp[2][0]];
		b[9] = S5[temp[1][1]];
		b[10] = S5[temp[0][2]];
		b[11] = S5[temp[3][3]];
		b[12] = S5[temp[3][0]];
		b[13] = S5[temp[2][1]];
		b[14] = S5[temp[1][2]];
		b[15] = S5[temp[0][3]];

		k = 0;

		for (int i = 0; i < mExpandedKey[0].length; i++) {
			for (int j = 0; j < mExpandedKey[0][i].length; j++) {
				m[k++] = mExpandedKey[0][i][j];
			}
		}

		xor128(b, b, m);
	}

	private void generateTables() {
		int[] pow = new int[512];
		int[] log = new int[256];
		int i = 0, w = 1;
		do {
			pow[i] = ((byte) w<0)?256+(byte)w:(byte)w;
			pow[i + 255] =  ((byte) w<0)?256+(byte)w:(byte)w;
			log[w] = ((byte) i<0)?256+(byte)i:(byte)i;
			i++;
			w ^= (w << 1) ^ ((w & ff_hi) != 0 ? ff_poly : 0);
		} while (w != 1);

		for (i = 0, w = 1; i < rcon.length; i++) {
			rcon[i] = (byte) w;
			w = (w << 1) ^ ((w & ff_hi) != 0 ? ff_poly : 0);
		}
		for (i = 0; i < 256; ++i) {
			w = (((byte) i != 0) ? pow[255 - log[(byte) i]] : 0);
			w ^= (w << 1) ^ (w << 2) ^ (w << 3) ^ (w << 4);
			byte b = S[i] = (byte) (0x63 ^ (w ^ (w >> 8)));

			T1[i][1] = T1[i][2] = T2[i][2] = T2[i][3] = T3[i][0] = T3[i][3] = T4[i][0] = T4[i][1] = b;
			T1[i][0] = T2[i][1] = T3[i][2] = T4[i][3] = (byte)(b != 0 ? pow[log[b] + 0x19]
					: 0);
			T1[i][3] = T2[i][0] = T3[i][1] = T4[i][2] = (byte)(b != 0 ? pow[log[b] + 0x01]
					: 0);

			w = (byte) i;
			w = (w << 1) ^ (w << 3) ^ (w << 6);
			w = (byte) (0x05 ^ (w ^ (w >> 8)));
			//System.out.println(i);
			S5[i] = b = (byte)(((byte) w != 0) ? pow[255 - log[(byte) w]] : 0);

			U1[b][3] = U2[b][0] = U3[b][1] = U4[b][2] = T5[i][3] = T6[i][0] = T7[i][1] = T8[i][2] = (byte)(b != 0 ? pow[log[b] + 0x68]
					: 0);
			U1[b][1] = U2[b][2] = U3[b][3] = U4[b][0] = T5[i][1] = T6[i][2] = T7[i][3] = T8[i][0] = (byte)(b != 0 ? pow[log[b] + 0xc7]
					: 0);
			U1[b][2] = U2[b][3] = U3[b][0] = U4[b][1] = T5[i][2] = T6[i][3] = T7[i][0] = T8[i][1] = (byte)(b != 0 ? pow[log[b] + 0xee]
					: 0);
			U1[b][0] = U2[b][1] = U3[b][2] = U4[b][3] = T5[i][0] = T6[i][1] = T7[i][2] = T8[i][3] = (byte)(b != 0 ? pow[log[b] + 0xdf]
					: 0);
		}
	}

}
