using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Management;
using System.Security.Cryptography;

namespace ws.hoyland.util
{
    public partial class Expire : Form
    {
        public Expire()
        {
            InitializeComponent();


        }

        private void Expire_Load(object sender, EventArgs e)
        {
            ManagementObject disk = new ManagementObject("win32_logicaldisk.deviceid=\"c:\"");
            disk.Get();
            string mc = UMD5(disk.GetPropertyValue("VolumeSerialNumber").ToString());

            textBox1.Text = mc;
        }

        private string UMD5(string str)
        {
            //Console.WriteLine(str);
            string pwd = "";
            MD5 md5 = MD5.Create();//实例化一个md5对像
            // 加密后是一个字节类型的数组，这里要注意编码UTF8/Unicode等的选择　
            byte[] s = md5.ComputeHash(Encoding.UTF8.GetBytes(str));
            //return s;
            
            //Console.WriteLine(s.Length);
            // 通过使用循环，将字节类型的数组转换为字符串，此字符串是常规字符格式化所得
            for (int i = 0; i < s.Length; i++)
            {
                // 将得到的字符串使用十六进制类型格式。格式后的字符是小写的字母，如果使用大写（X）则格式后的字符是大写字符
                //Console.WriteLine(s[i].ToString("X2"));
                pwd = pwd + s[i].ToString("X2");

            }
            return pwd;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
