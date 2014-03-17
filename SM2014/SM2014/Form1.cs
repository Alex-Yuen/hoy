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
using System.Threading;

namespace SM2014
{
    public partial class Form1 : Form
    {
        private delegate void Delegate();
        private Delegate dlg;

        private StreamWriter[] output = new StreamWriter[5]; //成功，失败，未运行
        private String[] fns = new String[] { "密码正确", "密码错误", "帐号冻结", "未识别" };
        private String xpath = AppDomain.CurrentDomain.BaseDirectory;
        private List<String> accounts;

        public List<String> Accounts
        {
            get { return accounts; }
            set { accounts = value; }
        }
        private List<String> proxies;

        public List<String> Proxies
        {
            get { return proxies; }
            set { proxies = value; }
        }

        private int pidx = 0; //当前代理索引

        public Form1()
        {
            InitializeComponent();
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
                if (accounts != null && accounts.Count > 0 && proxies != null && proxies.Count > 0)
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
                            proxies = new List<String>();

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
                                    //line = i + "----" + line;
                                    //string[] lns = Regex.Split(line, "----");
                                    //List<string> listArr = new List<string>();
                                    //listArr.Add(lns[0]);
                                    //listArr.Add(lns[1]);
                                    //listArr.Add(lns[2]);
                                    //listArr.Add("初始化");
                                    ////listArr.AddRange(lns);
                                    ////listArr.Insert(3, "初始化");
                                    //lns = listArr.ToArray();

                                    proxies.Add(line);
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

                            label2.Text = "代理: " + proxies.Count;
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

        private void button1_Click(object sender, EventArgs e)
        {
            dlg = delegate()
            {
                ThreadPool.SetMinThreads(1, 0);
                ThreadPool.SetMaxThreads(20, 0);

                //Task task = new Task(accounts[i]);
                //tasks.Add(task);
                //ThreadPool.QueueUserWorkItem(new WaitCallback(task.run));//, task

                Task task = new Task(this, 0);
                task.Start();

                //for (int i = 0; i < accounts.Count; i++)
                //{
                //    //1000 个任务
                //    Task task = new Task(accounts[i]);
                //    ThreadPool.QueueUserWorkItem(new WaitCallback(task.run));
                //}
            };
            this.BeginInvoke(dlg);
        }

        public void info(String message)
        {
            dlg = delegate()
            {
                this.textBox1.AppendText(DateTime.Now.ToString("[yyyy/MM/dd HH:mm:ss] "));
                this.textBox1.AppendText(message + "\r\n");
            };
            this.BeginInvoke(dlg);
        }

        public void log(int type, int id)
        {
            dlg = delegate()
            {
                this.textBox1.AppendText(DateTime.Now.ToString("[yyyy/MM/dd HH:mm:ss] "));
                this.textBox1.AppendText("LOG: "+type.ToString()+"="+id.ToString() + "\r\n");
            };
            this.BeginInvoke(dlg);
        }

        public string getProxy()
        {
            String proxy = null;
            lock (this)
            {
                proxy = this.Proxies[pidx];

                //Console.WriteLine(pidx + "=" + proxy);

                if (pidx == this.Proxies.Count - 1)
                {
                    pidx = 0;
                }
                else
                {
                   pidx++;
                }
            }

            return proxy;
        }
    }
}
