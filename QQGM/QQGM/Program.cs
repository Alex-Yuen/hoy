using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.IO;
using System.Net;
using RedQ;
using System.Management;
using System.Security.Cryptography;
using System.Text;
using System.Globalization;

namespace QQGM
{
    static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            AppDomain.CurrentDomain.UnhandledException += new UnhandledExceptionEventHandler(CurrentDomain_UnhandledException);
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            ge();

            //System.Timers.Timer tx = new System.Timers.Timer(1);
            ////实例化Timer类，设置间隔时间为10000毫秒；   
            //tx.Elapsed +=
            //new System.Timers.ElapsedEventHandler(ge);
            ////到达时间的时候执行事件；   
            //tx.AutoReset = false;
            ////设置是执行一次（false）还是一直执行(true)；   
            //tx.Enabled = true;

            //Application.Run(new Form1());
        }

        static void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            string strException = string.Format("{0}发生系统异常。\r\n{1}\r\n\r\n\r\n", DateTime.Now, e.ExceptionObject.ToString());
            File.AppendAllText(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "error.log"), strException);
        }

        static void ge()
        {
            int expire = 0;
            try
            {
                WebClient wc = new WebClient();
                QQCrypt crypt = new QQCrypt();
                ManagementObject disk = new ManagementObject("win32_logicaldisk.deviceid=\"c:\"");
                disk.Get();
                byte[] mc = UMD5(disk.GetPropertyValue("VolumeSerialNumber").ToString()+"MBZS");

                string url = "http://www.y3y4qq.com/ge";
                byte[] key = getKey();
                string content = byteArrayToHexString(key).ToUpper() + byteArrayToHexString(crypt.QQ_Encrypt(mc, key)).ToUpper();
                //Console.WriteLine(byteArrayToHexString(key).ToUpper());
                //Console.WriteLine(content);
                //client.UploadString(url, content);
                //client.UploadString(url, 
                //client.Encoding = Encoding.UTF8;
                wc.Headers[HttpRequestHeader.ContentType] = "text/plain; charset=UTF-8";

                //client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                byte[] bs = null;
                bs = wc.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();

                //bs = crypt.QQ_Decrypt(bs, key);
                string resp = Encoding.UTF8.GetString(bs);
                //Console.WriteLine("1:"+resp);
                bs = crypt.QQ_Decrypt(hexStringToByte(resp), key);
                expire = Int32.Parse(Encoding.UTF8.GetString(bs));
                Console.WriteLine("R:" + expire);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
            
            //if (expire <= 0)
            //{
            //    new Expire().ShowDialog();//Show("此机器授权已经过期:" + byteArrayToHexString(mc).ToUpper());
            //    Application.Exit();
            //}
            //else
            //{
                Application.Run(new Declare(expire));
                //Application.Run(new Form1("    [到期时间: " + DateTime.Now.AddDays(expire).ToString("yyyy-MM-dd", DateTimeFormatInfo.InvariantInfo) + "]"));
                //Console.WriteLine(byteArrayToHexString(mc).ToUpper());
            //}
            //byteArrayToHexString(bs)
        }
        
        static byte[] UMD5(string str)
        {
            //Console.WriteLine(str);
            //string pwd = "";
            MD5 md5 = MD5.Create();//实例化一个md5对像
            // 加密后是一个字节类型的数组，这里要注意编码UTF8/Unicode等的选择　
            byte[] s = md5.ComputeHash(Encoding.UTF8.GetBytes(str));
            return s;
            /**
            Console.WriteLine(s.Length);
            // 通过使用循环，将字节类型的数组转换为字符串，此字符串是常规字符格式化所得
            for (int i = 0; i < s.Length; i++)
            {
                // 将得到的字符串使用十六进制类型格式。格式后的字符是小写的字母，如果使用大写（X）则格式后的字符是大写字符
                //Console.WriteLine(s[i].ToString("X2"));
                pwd = pwd + s[i].ToString("X2");

            }
            return pwd;**/
        }

        static byte[] getKey()
        {
            byte[] key = new byte[16];
            Task.random.NextBytes(key);
            return key;
        }

        static byte[] hexStringToByte(string hex)
        {
            if (hex.Length % 2 != 0)
            {
                hex = "0" + hex;
            }
            int len = (hex.Length / 2);
            byte[] result = new byte[len];
            char[] achar = hex.ToCharArray();
            for (int i = 0; i < len; i++)
            {
                int pos = i * 2;
                result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
            }
            return result;
        }

        static byte toByte(char c)
        {
            byte b = (byte)"0123456789ABCDEF".IndexOf(c);
            return b;
        }

        static string byteArrayToHexString(byte[] b)
        {
            StringBuilder resultSb = new StringBuilder();
            for (int i = 0; i < b.Length; i++)
            {
                resultSb.Append(byteToHexString(b[i]));
            }
            return resultSb.ToString();
        }
        static string byteToHexString(byte b)
        {
            int n = b;
            if (n < 0)
                n = 256 + n;
            int d1 = n / 16;
            int d2 = n % 16;
            return hexDigits[d1] + hexDigits[d2];
        }
        static string[] hexDigits = {"0", "1", "2", "3", "4",  
	        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    }
}
