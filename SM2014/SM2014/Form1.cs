using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Ws.Hoyland.CSharp;
using System.IO;
using ws.hoyland.util;
using System.Text.RegularExpressions;

namespace SM2014
{
    public partial class Form1 : Form
    {
        private delegate void Delegate();
        private Delegate dlg;

        private StreamWriter[] output = new StreamWriter[5]; //成功，失败，未运行
        private String[] fns = new String[] { "密码正确", "密码错误", "帐号冻结", "未识别"};
        private String xpath = AppDomain.CurrentDomain.BaseDirectory;
        private List<String> accounts;
        private List<String> ps;

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            //http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=0.1331082413429372&qq=1803720730&pmd5=78A18D3736CE9BF47D809CF900B5F1A1&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void 关于AToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new AboutBox().ShowDialog();
        }

        private void 导入帐号AToolStripMenuItem_Click(object sender, EventArgs e)
        {
            dlg = delegate()
            {
                OpenFileDialog dialog = new OpenFileDialog();
                //dialog.
                dialog.Title = "导入帐号";
                dialog.Filter = "所有文件(*.*)|*.*";

                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    string fn = dialog.FileName;
                    if (fn != null)
                    {
                        try
                        {
                            accounts = new List<String>();

                            Encoding ecdtype = EncodingType.GetType(fn);
                            FileStream fs = new FileStream(fn, FileMode.Open);
                            StreamReader m_streamReader = new StreamReader(fs, ecdtype);
                            m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);

                            String line = null;
                            int i = 1;
                            while ((line = m_streamReader.ReadLine()) != null)
                            {
                                if (!line.Equals(""))
                                {
                                    line = i + "----" + line;
                                    //string[] lns = Regex.Split(line, "----");
                                    //List<string> listArr = new List<string>();
                                    //listArr.Add(lns[0]);
                                    //listArr.Add(lns[1]);
                                    //listArr.Add(lns[2]);
                                    //listArr.Add("初始化");
                                    ////listArr.AddRange(lns);
                                    ////listArr.Insert(3, "初始化");
                                    //lns = listArr.ToArray();

                                    accounts.Add(line);
                                    //							if (lns.size() == 3) {
                                    //								lns.add("0");
                                    //								lns.add("初始化");
                                    //								//line += "----0----初始化";
                                    //							} else {
                                    //								//line += "----初始化";
                                    //								lns.add("初始化");
                                    //							}
                                }
                                i++;
                            }

                            m_streamReader.Close();
                            m_streamReader.Dispose();
                            fs.Close();
                            fs.Dispose();

                            label1.Text = "帐号: " + accounts.Count;
                            ready();
                        }
                        catch (Exception ex)
                        {
                            throw ex;
                        }
                    }
                }
            };
            this.BeginInvoke(dlg);
        }

        private void ready()
        {
            dlg = delegate()
            {
                if (accounts != null && accounts.Count > 0 && ps != null && ps.Count > 0)
                {
                    button1.Enabled = true;
                }
                else
                {
                    button1.Enabled = false;
                }
            };
            this.BeginInvoke(dlg);
        }

        private void 导入代理PToolStripMenuItem_Click(object sender, EventArgs e)
        {
            dlg = delegate()
            {
                OpenFileDialog dialog = new OpenFileDialog();
                //dialog.
                dialog.Title = "导入代理";
                dialog.Filter = "所有文件(*.*)|*.*";

                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    string fn = dialog.FileName;
                    if (fn != null)
                    {
                        try
                        {
                            ps = new List<String>();

                            Encoding ecdtype = EncodingType.GetType(fn);
                            FileStream fs = new FileStream(fn, FileMode.Open);
                            StreamReader m_streamReader = new StreamReader(fs, ecdtype);
                            m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);

                            String line = null;
                            int i = 1;
                            while ((line = m_streamReader.ReadLine()) != null)
                            {
                                if (!line.Equals(""))
                                {
                                    line = i + "----" + line;
                                    //string[] lns = Regex.Split(line, "----");
                                    //List<string> listArr = new List<string>();
                                    //listArr.Add(lns[0]);
                                    //listArr.Add(lns[1]);
                                    //listArr.Add(lns[2]);
                                    //listArr.Add("初始化");
                                    ////listArr.AddRange(lns);
                                    ////listArr.Insert(3, "初始化");
                                    //lns = listArr.ToArray();

                                    ps.Add(line);
                                    //							if (lns.size() == 3) {
                                    //								lns.add("0");
                                    //								lns.add("初始化");
                                    //								//line += "----0----初始化";
                                    //							} else {
                                    //								//line += "----初始化";
                                    //								lns.add("初始化");
                                    //							}
                                }
                                i++;
                            }

                            m_streamReader.Close();
                            m_streamReader.Dispose();
                            fs.Close();
                            fs.Dispose();

                            label2.Text = "代理: " + ps.Count;
                            ready();
                        }
                        catch (Exception ex)
                        {
                            throw ex;
                        }
                    }
                }
            };
            this.BeginInvoke(dlg);
        }
    }

    
}
