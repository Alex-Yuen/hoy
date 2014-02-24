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
        private bool operating = false;
        private bool changed = false;
        private Font sf = new Font("宋体", 9, FontStyle.Underline);
        private Font nf = new Font("宋体", 9, FontStyle.Regular);

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

            tabPage2.Parent = null;
            tabPage3.Parent = null;
        }

        private void toolStripButton5_Click(object sender, EventArgs e)
        {
            关于AToolStripMenuItem.PerformClick();
        }
        
        private void 选项OToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show("Under Building...");
            //http://www.jb51.net/article/35248.htm
        }

        private void tabControl1_Selecting(object sender, TabControlCancelEventArgs e)
        {
            //e.TabPageIndex
            //if (!operating)
            //{
            //    e.Cancel = true;
            //}
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            if (tabPage2.Parent == null)
            {
                tabControl1.TabPages.Add(tabPage2);
                changed = true;
                SetSaveBtnStatus();
            }
            tabControl1.SelectedIndex = 1;
        }

        private void button3_Click(object sender, EventArgs e)
        {
            if (tabPage3.Parent == null)
            {
                tabControl1.TabPages.Add(tabPage3);
            }
            tabControl1.SelectedIndex = 2;
        }

        private void SetSaveBtnStatus()
        {
            if (changed)
            {
                toolStripButton3.Enabled = true;
            }
            else
            {
                toolStripButton3.Enabled = false;
            }
        }

        private void listView1_ItemSelectionChanged(object sender, ListViewItemSelectionChangedEventArgs e)
        {
            if (e.IsSelected)
            {
                e.Item.Selected = false;
            }
        }

        private void listView2_ItemSelectionChanged(object sender, ListViewItemSelectionChangedEventArgs e)
        {
            if (e.IsSelected)
            {
                foreach (ListViewItem it in listView2.Items)
                {
                    if (it != e.Item)
                    {
                        it.BackColor = Color.White;
                    }
                }
                e.Item.BackColor = Color.Gray;
                e.Item.Selected = false;
            }
        }

        private void listView1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (tabPage2.Parent == null)
            {
                tabPage2.Parent = tabControl1;
            }
            tabControl1.SelectedIndex = 1;
            //tabPage2.Select();
        }


    }
}
