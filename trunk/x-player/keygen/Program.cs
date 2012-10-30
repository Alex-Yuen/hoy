using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace keygen
{
    class Program
    {
        static void Main(string[] args)
        {
            int[] intCode = new int[127];//用于存密钥
            int[] intNumber = new int[25];//用于存机器码的Ascii值
            char[] Charcode = new char[25];//存储机器码字

            for (int i = 1; i < intCode.Length; i++)
            {
                //intCode[i] = ra.Next(0, 9);
                intCode[i] = i % 9;
            }

            //string hardcode = "B6C0F0D228B9135EE8DFE13E";
            string hardcode = args[0];
            string regcode = "";
            if (hardcode != "")
            {
                //把机器码存入数组中
                for (int i = 1; i < Charcode.Length; i++)//把机器码存入数组中
                {
                    Charcode[i] = Convert.ToChar(hardcode.Substring(i - 1, 1));
                }//
                for (int j = 1; j < intNumber.Length; j++)//把字符的ASCII值存入一个整数组中。
                {
                    intNumber[j] = intCode[Convert.ToInt32(Charcode[j])] + Convert.ToInt32(Charcode[j]);
                }

                string strAsciiName = null;//用于存储机器码
                for (int j = 1; j < intNumber.Length; j++)
                {
                    //MessageBox.Show((Convert.ToChar(intNumber[j])).ToString());
                    if (intNumber[j] >= 48 && intNumber[j] <= 57)//判断字符ASCII值是否0－9之间
                    {
                        strAsciiName += Convert.ToChar(intNumber[j]).ToString();
                    }
                    else if (intNumber[j] >= 65 && intNumber[j] <= 90)//判断字符ASCII值是否A－Z之间
                    {
                        strAsciiName += Convert.ToChar(intNumber[j]).ToString();
                    }
                    else if (intNumber[j] >= 97 && intNumber[j] <= 122)//判断字符ASCII值是否a－z之间
                    {
                        strAsciiName += Convert.ToChar(intNumber[j]).ToString();
                    }
                    else//判断字符ASCII值不在以上范围内
                    {
                        if (intNumber[j] > 122)//判断字符ASCII值是否大于z
                        {
                            strAsciiName += Convert.ToChar(intNumber[j] - 10).ToString();
                        }
                        else
                        {
                            strAsciiName += Convert.ToChar(intNumber[j] - 9).ToString();
                        }
                    }

                    regcode = strAsciiName;//得到注册码
                }
            }

            Console.WriteLine(regcode);
        }
    }
}
