using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;


namespace ws.hoyland.sszs
{
    /// <summary>
    /// QQ Msg En/DeCrypt Class
    /// Writen By Red_angelX On 2006.9.13
    /// </summary>
    public class Crypter
    {
        //QQ TEA-16 Encrypt/Decrypt Class 
        // 
        // 
        //And also LumaQQ//s source code 
        //  CopyRight:No CopyRight^_^ 
        //  Author : Red_angelX     
        //  NetWork is Free,Tencent is ****!
        // 
        //Class Begin 
        //AD:Find Job!!,If you Want Give me a Job,Content Me!!

        //Copied & translated from LumaQQ//s source code          `From LumaQQ///s source code: 
        private byte[] Plain;                                   //指向当前的明文块 
        private byte[] prePlain;                               //指向前面一个明文块 
        private byte[] Out;                                     //输出的密文或者明文 
        private long Crypt, preCrypt;                           //当前加密的密文位置和上一次加密的密文块位置，他们相差8 
        private long Pos;                                       //当前处理的加密解密块的位置 
        private long padding;                                   //填充数 
        private byte[] Key = new byte[16];                      //密钥 
        private bool Header;                                    //用于加密时，表示当前是否是第一个8字节块，因为加密算法 
        //是反馈的，但是最开始的8个字节没有反馈可用，所有需要标 
        //明这种情况 
        private long contextStart;                              //这个表示当前解密开始的位置，之所以要这么一个变量是为了 
        //避免当解密到最后时后面已经没有数据，这时候就会出错，这 
        //个变量就是用来判断这种情况免得出错 
        public Crypter()
        {
            //
            // TODO: 在此处添加构造函数逻辑
            //
        }


        //Push 数据
        byte[] CopyMemory(byte[] arr, int arr_index, long input)  //lenth = 4
        {
            if (arr_index + 4 > arr.Length)
            {
                // 不能执行
                return arr;
            }

            arr[arr_index + 3] = (byte)((input & 0xff000000) >> 24);
            arr[arr_index + 2] = (byte)((input & 0x00ff0000) >> 16);
            arr[arr_index + 1] = (byte)((input & 0x0000ff00) >> 8);
            arr[arr_index] = (byte)(input & 0x000000ff);

            arr[arr_index] &= 0xff;
            arr[arr_index + 1] &= 0xff;
            arr[arr_index + 2] &= 0xff;
            arr[arr_index + 3] &= 0xff;

            return arr;
        }

        long CopyMemory(long Out, byte[] arr, int arr_index)
        {
            if (arr_index + 4 > arr.Length)
            {
                return Out;
                //不能执行
            }

            long x1 = arr[arr_index + 3] << 24;
            long x2 = arr[arr_index + 2] << 16;
            long x3 = arr[arr_index + 1] << 8;
            long x4 = arr[arr_index];

            long o = x1 | x2 | x3 | x4;
            o &= 0xffffffff;
            return o;
        }

        long getUnsignedInt(byte[] arrayIn, int offset, int len /*Default is 4*/)
        {

            long ret = 0;
            int end = 0;
            if (len > 8)
                end = offset + 8;
            else
                end = offset + len;
            for (int i = offset; i < end; i++)
            {
                ret <<= 8;
                ret |= arrayIn[i] & (long)0xff;
            }
            return (ret & 0xffffffff) | (ret >> 32);
        }

        long Rand()
        {
            Random rd = new Random();
            long ret;
            ret = rd.Next() + (rd.Next() % 1024);
            return ret;
        }

        private byte[] Decipher(byte[] arrayIn, byte[] arrayKey, long offset)
        {
            //long Y,z,a,b,c,d;
            long sum, delta;
            //Y=z=a=b=c=d=0;
            byte[] tmpArray = new byte[24];
            byte[] tmpOut = new byte[8];
            if (arrayIn.Length < 8)
            {
                // Error:return
                return tmpOut;
            }
            if (arrayKey.Length < 16)
            {
                // Error:return
                return tmpOut;
            }
            sum = 0xE3779B90;
            sum = sum & 0xFFFFFFFF;
            delta = 0x9E3779B9;
            delta = delta & 0xFFFFFFFF;
            /*tmpArray[3] = arrayIn[offset]; 
            tmpArray[2] = arrayIn[offset + 1]; 
            tmpArray[1] = arrayIn[offset + 2]; 
            tmpArray[0] = arrayIn[offset + 3]; 
            tmpArray[7] = arrayIn[offset + 4]; 
            tmpArray[6] = arrayIn[offset + 5]; 
            tmpArray[5] = arrayIn[offset + 6]; 
            tmpArray[4] = arrayIn[offset + 7]; 
            tmpArray[11] = arrayKey[0]; 
            tmpArray[10] = arrayKey[1]; 
            tmpArray[9] = arrayKey[2]; 
            tmpArray[8] = arrayKey[3]; 
            tmpArray[15] = arrayKey[4]; 
            tmpArray[14] = arrayKey[5]; 
            tmpArray[13] = arrayKey[6]; 
            tmpArray[12] = arrayKey[7]; 
            tmpArray[19] = arrayKey[8]; 
            tmpArray[18] = arrayKey[9]; 
            tmpArray[17] = arrayKey[10]; 
            tmpArray[16] = arrayKey[11]; 
            tmpArray[23] = arrayKey[12]; 
            tmpArray[22] = arrayKey[13]; 
            tmpArray[21] = arrayKey[14]; 
            tmpArray[20] = arrayKey[15]; 
            Y=CopyMemory(Y,tmpArray,0);    
            z=CopyMemory(z,tmpArray,4);
            a=CopyMemory(a,tmpArray,8);
            b=CopyMemory(b,tmpArray,12);
            c=CopyMemory(c,tmpArray,16);
            d=CopyMemory(d,tmpArray,20);*/
            long Y = getUnsignedInt(arrayIn, (int)offset, 4);
            long z = getUnsignedInt(arrayIn, (int)offset + 4, 4);
            long a = getUnsignedInt(arrayKey, 0, 4);
            long b = getUnsignedInt(arrayKey, 4, 4);
            long c = getUnsignedInt(arrayKey, 8, 4);
            long d = getUnsignedInt(arrayKey, 12, 4);
            for (int i = 1; i <= 16; i++)
            {
                z -= ((Y << 4) + c) ^ (Y + sum) ^ ((Y >> 5) + d);
                z &= 0xFFFFFFFF;
                Y -= ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);
                Y &= 0xFFFFFFFF;
                sum -= delta;
                sum &= 0xFFFFFFFF;
            }

            tmpArray = CopyMemory(tmpArray, 0, Y);
            tmpArray = CopyMemory(tmpArray, 4, z);
            tmpOut[0] = tmpArray[3];
            tmpOut[1] = tmpArray[2];
            tmpOut[2] = tmpArray[1];
            tmpOut[3] = tmpArray[0];
            tmpOut[4] = tmpArray[7];
            tmpOut[5] = tmpArray[6];
            tmpOut[6] = tmpArray[5];
            tmpOut[7] = tmpArray[4];

            return tmpOut;
        }

        private byte[] Decipher(byte[] arrayIn, byte[] arrayKey)
        {
            return Decipher(arrayIn, arrayKey, 0);
        }

        private byte[] Encipher(byte[] arrayIn, byte[] arrayKey, long offset)
        {
            byte[] tmpOut = new byte[8];
            byte[] tmpArray = new byte[24];
            //long Y,z,a,b,c,d;
            //Y=z=a=b=c=d=0;
            long sum, delta;
            if (arrayIn.Length < 8)
            {
                // Error:
                return tmpOut;
            }
            if (arrayKey.Length < 16)
            {
                // Error:
                return tmpOut;
            }
            sum = 0;
            delta = 0x9E3779B9;
            delta &= 0xFFFFFFFF;

            /*tmpArray[3] = arrayIn[offset]; 
            tmpArray[2] = arrayIn[offset + 1]; 
            tmpArray[1] = arrayIn[offset + 2]; 
            tmpArray[0] = arrayIn[offset + 3]; 
            tmpArray[7] = arrayIn[offset + 4]; 
            tmpArray[6] = arrayIn[offset + 5]; 
            tmpArray[5] = arrayIn[offset + 6]; 
            tmpArray[4] = arrayIn[offset + 7]; 
            tmpArray[11] = arrayKey[0]; 
            tmpArray[10] = arrayKey[1]; 
            tmpArray[9] = arrayKey[2]; 
            tmpArray[8] = arrayKey[3]; 
            tmpArray[15] = arrayKey[4]; 
            tmpArray[14] = arrayKey[5]; 
            tmpArray[13] = arrayKey[6]; 
            tmpArray[12] = arrayKey[7]; 
            tmpArray[19] = arrayKey[8];
            tmpArray[18] = arrayKey[9]; 
            tmpArray[17] = arrayKey[10]; 
            tmpArray[16] = arrayKey[11]; 
            tmpArray[23] = arrayKey[12]; 
            tmpArray[22] = arrayKey[13]; 
            tmpArray[21] = arrayKey[14]; 
            tmpArray[20] = arrayKey[15]; 

            Y=CopyMemory(Y,tmpArray,0);
            z=CopyMemory(z,tmpArray,4);
            a=CopyMemory(a,tmpArray,8);
            b=CopyMemory(b,tmpArray,12);
            c=CopyMemory(c,tmpArray,16);
            d=CopyMemory(d,tmpArray,20);*/

            long Y = getUnsignedInt(arrayIn, (int)offset, 4);
            long z = getUnsignedInt(arrayIn, (int)offset + 4, 4);
            long a = getUnsignedInt(arrayKey, 0, 4);
            long b = getUnsignedInt(arrayKey, 4, 4);
            long c = getUnsignedInt(arrayKey, 8, 4);
            long d = getUnsignedInt(arrayKey, 12, 4);

            for (int i = 1; i <= 16; i++)
            {
                sum += delta;
                sum &= 0xFFFFFFFF;
                Y += ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);
                Y &= 0xFFFFFFFF;
                z += ((Y << 4) + c) ^ (Y + sum) ^ ((Y >> 5) + d);
                z &= 0xFFFFFFFF;
            }

            tmpArray = CopyMemory(tmpArray, 0, Y);
            tmpArray = CopyMemory(tmpArray, 4, z);

            tmpOut[0] = tmpArray[3];
            tmpOut[1] = tmpArray[2];
            tmpOut[2] = tmpArray[1];
            tmpOut[3] = tmpArray[0];
            tmpOut[4] = tmpArray[7];
            tmpOut[5] = tmpArray[6];
            tmpOut[6] = tmpArray[5];
            tmpOut[7] = tmpArray[4];

            return tmpOut;
        }


        private byte[] Encipher(byte[] arrayIn, byte[] arrayKey)
        {
            return Encipher(arrayIn, arrayKey, 0);
        }

        private void Encrypt8Bytes()
        {
            byte[] Crypted;
            for (Pos = 0; Pos <= 7; Pos++)
            {
                if (this.Header == true)
                {
                    Plain[Pos] = (byte)(Plain[Pos] ^ prePlain[Pos]);
                }
                else
                {
                    Plain[Pos] = (byte)(Plain[Pos] ^ Out[preCrypt + Pos]);
                }
            }
            Crypted = Encipher(Plain, Key);

            for (int i = 0; i <= 7; i++)
            {
                Out[Crypt + i] = (byte)Crypted[i];
            }

            for (Pos = 0; Pos <= 7; Pos++)
            {
                Out[Crypt + Pos] = (byte)(Out[Crypt + Pos] ^ prePlain[Pos]);
            }
            Plain.CopyTo(prePlain, 0);
            preCrypt = Crypt;
            Crypt = Crypt + 8;
            Pos = 0;
            Header = false;
        }

        private bool Decrypt8Bytes(byte[] arrayIn, long offset)
        {
            long lngTemp;
            for (Pos = 0; Pos <= 7; Pos++)
            {
                if (this.contextStart + Pos > arrayIn.Length - 1)
                {
                    return true;
                }
                prePlain[Pos] = (byte)(prePlain[Pos] ^ arrayIn[offset + Crypt + Pos]);
            }
            try
            {
                prePlain = this.Decipher(prePlain, Key);
            }
            catch
            {
                return false;
            }
            lngTemp = prePlain.Length - 1;
            contextStart += 8;
            Crypt += 8;
            Pos = 0;
            return true;
        }

        private bool Decrypt8Bytes(byte[] arrayIn)
        {
            return Decrypt8Bytes(arrayIn, 0);
        }


        #region Public Methods!

        /// <summary>
        /// QQ TEA 加密函数
        /// </summary>
        /// <param name="arrayIn">要加密的字串</param>
        /// <param name="arrayKey">密钥</param>
        /// <param name="offset">偏移</param>
        /// <returns></returns>
        public byte[] QQ_Encrypt(byte[] arrayIn, byte[] arrayKey, long offset)
        {
            Plain = new byte[8];
            prePlain = new byte[8];
            long l;
            Pos = 1;
            padding = 0;
            Crypt = preCrypt = 0;
            arrayKey.CopyTo(Key, 0);    // Key Must Be 16 Length!
            Header = true;
            Pos = 2;
            //计算头部填充字节数
            Pos = (arrayIn.Length + 10) % 8;
            if (Pos != 0)
                Pos = 8 - Pos;
            //输出长度
            Out = new byte[arrayIn.Length + Pos + 10];
            //把POS存到PLAIN的第一个字节
            //0xf8后面3位是空的，正好给Pos
            Plain[0] = (byte)((Rand() & 0xf8) | Pos);
            //用随机数填充1到Pos的内容
            for (int i = 1; i <= Pos; i++)
            {
                Plain[i] = (byte)(Rand() & 0xff);
            }
            Pos++;
            padding = 1;

            //继续填充两个字节随机数，满8字节就加密
            while (padding < 3)
            {
                if (Pos < 8)
                {
                    Plain[Pos] = (byte)(Rand() & 0xff);
                    padding++;
                    Pos++;
                }
                else if (Pos == 8)
                {
                    this.Encrypt8Bytes();
                }
            }

            int I = (int)offset;
            l = 0;
            //明文内容，满8字节加密到读完
            l = arrayIn.Length;
            while (l > 0)
            {
                if (Pos < 8)
                {
                    Plain[Pos] = arrayIn[I];
                    I++;
                    Pos++;
                    l--;
                }
                else if (Pos == 8)
                {
                    this.Encrypt8Bytes();
                }
            }

            //末尾填充0，保证是8的倍数
            padding = 1;
            while (padding < 9)
            {
                if (Pos < 8)
                {
                    Plain[Pos] = 0;
                    Pos++;
                    padding++;
                }
                else if (Pos == 8)
                {
                    this.Encrypt8Bytes();
                }
            }

            return Out;
        }


        public byte[] QQ_Encrypt(byte[] arrayIn, byte[] arrayKey)
        {
            return QQ_Encrypt(arrayIn, arrayKey, 0);
        }

        /// <summary>
        ///  QQ TEA 解密函数
        /// </summary>
        /// <param name="arrayIn">要解密字串</param>
        /// <param name="arrayKey">密钥</param>
        /// <param name="offset">偏移</param>
        /// <returns></returns>
        public byte[] QQ_Decrypt(byte[] arrayIn, byte[] arrayKey, long offset)
        {
            byte[] error = new byte[0];
            //检查是否是8的倍数至少16字节
            if (arrayIn.Length < 16 || (arrayIn.Length % 8 != 0))
            {
                //Return What?
                return error;
            }
            if (arrayKey.Length != 16)
            {
                //Return What?
                return error;
            }
            byte[] m;
            long I, Count;
            m = new byte[offset + 8];
            arrayKey.CopyTo(Key, 0);
            Crypt = preCrypt = 0;
            //计算消息头部，明文开始的偏移，解密第一字节和7相与得到
            prePlain = this.Decipher(arrayIn, arrayKey, offset);
            Pos = prePlain[0] & 7;
            //计算明文长度
            Count = arrayIn.Length - Pos - 10;
            if (Count <= 0)
            {
                //Return What?
                return error;
            }
            Out = new byte[Count];
            preCrypt = 0;
            Crypt = 8;
            this.contextStart = 8;
            Pos++;
            padding = 1;
            //跳过头部
            while (padding < 3)
            {
                if (Pos < 8)
                {
                    Pos++;
                    padding++;
                }
                else if (Pos == 8)
                {
                    for (int i = 0; i < m.Length; i++)
                        m[i] = arrayIn[i];
                    if (this.Decrypt8Bytes(arrayIn, offset) == false)
                    {
                        //Return What?
                        return error;
                    }
                }
            }

            //解密明文
            I = 0;
            while (Count != 0)
            {
                if (Pos < 8)
                {
                    Out[I] = (byte)(m[offset + preCrypt + Pos] ^ prePlain[Pos]);
                    I++;
                    Count--;
                    Pos++;
                }
                else if (Pos == 8)
                {
                    m = arrayIn;
                    preCrypt = Crypt - 8;
                    if (this.Decrypt8Bytes(arrayIn, offset) == false)
                    {
                        //Return What?
                        return error;
                    }
                }
            }

            //最后的解密部分，检查尾部是不是0
            for (padding = 1; padding <= 7; padding++)
            {
                if (Pos < 8)
                {
                    if ((m[offset + preCrypt + Pos] ^ prePlain[Pos]) != 0)
                    {
                        //Return What?
                        return error;
                    }
                    Pos++;
                }
                else if (Pos == 8)
                {
                    for (int i = 0; i < m.Length; i++)
                        m[i] = arrayIn[i];
                    preCrypt = Crypt;
                    if (this.Decrypt8Bytes(arrayIn, offset) == false)
                    {
                        //Return What?
                        return error;
                    }
                }
            }
            return Out;
        }

        public byte[] QQ_Decrypt(byte[] arrayIn, byte[] arrayKey)
        {
            return QQ_Decrypt(arrayIn, arrayKey, 0);
        }
        #endregion
    }
}