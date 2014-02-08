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

        public static string getMCX()
        {
            ManagementObject disk = new ManagementObject("win32_logicaldisk.deviceid=\"c:\"");
            disk.Get();
            string mc = Util.UMD5X(disk.GetPropertyValue("VolumeSerialNumber").ToString() + "SSZS");
            return mc;
        }

        public static byte[] getMC()
        {
            ManagementObject disk = new ManagementObject("win32_logicaldisk.deviceid=\"c:\"");
            disk.Get();
            byte[] mc = Util.UMD5(disk.GetPropertyValue("VolumeSerialNumber").ToString() + "SSZS");
            return mc;
        }


        private void Expire_Load(object sender, EventArgs e)
        {


            textBox1.Text = getMCX();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
