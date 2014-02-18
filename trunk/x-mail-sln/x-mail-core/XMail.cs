using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Ws.Hoyland.CSharp;
using System.Reflection;
using System.Diagnostics;

namespace Ws.Hoyland.XMail
{
    public partial class XMail : Form
    {
        private Form pf;
        public XMail(Form pf)
        {
            this.pf = pf;
            InitializeComponent();
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void XMail_FormClosing(object sender, FormClosingEventArgs e)
        {
            this.pf.Close();
        }

        private void 关于AToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new AboutBox().ShowDialog();
        }

        private void XMail_Load(object sender, EventArgs e)
        {
            Version ApplicationVersion = new Version(Application.ProductVersion);
            string version = ApplicationVersion.Major.ToString();//获取主版本号  

            FileVersionInfo info = FileVersionInfo.GetVersionInfo(AppDomain.CurrentDomain.BaseDirectory + "//x-mail-core.dll");

            this.Text = "X-Mail " + info.FileVersion;// Assembly.GetExecutingAssembly().GetName().Version.ToString();
        }
    }
}
