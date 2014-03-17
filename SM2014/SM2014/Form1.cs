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
using System.Configuration;

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
        private int visited = 0;
        private int finish = 0;
        private Configuration cfa = null;

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

        //private int pidx = 0; //当前代理索引

        public static Random RANDOM = new Random();

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
                            label3.Text = "0 / " + accounts.Count + " = 0%";
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
                if (button1.Text.Equals("开始"))
                {
                    ConfigurationManager.RefreshSection("appSettings");

                    finish = 0;
                    visited = 0;

                    toolStripStatusLabel1.Text = "正在运行";

                    ThreadPool.SetMinThreads(1, 0);
                    ThreadPool.SetMaxThreads(Int32.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value), 0);

                    //Task task = new Task(accounts[i]);
                    //tasks.Add(task);
                    //ThreadPool.QueueUserWorkItem(new WaitCallback(task.run));//, task

                    //Task task = new Task(this, 0);
                    //task.Start();

                    for (int i = 0; i < accounts.Count; i++)
                    {
                        //N 个任务
                        Task task = new Task(this, accounts[i]);
                        ThreadPool.QueueUserWorkItem(new WaitCallback(task.Run));
                    }

                    button1.Text = "暂停";
                }
                else
                {
                    //暂停处理代码
                    toolStripStatusLabel1.Text = "未运行";
                    button1.Text = "开始";
                }
            };
            this.BeginInvoke(dlg);
        }


        public String GetProxy()
        {
            lock (this)
            {
                if (proxies.Count == 0)
                {
                    return null;
                }
                else
                {
                    return proxies[RANDOM.Next(proxies.Count)];
                }
            }
        }

        public void RemoveProxy(String proxy)
        {
            lock (this)
            {
                proxies.Remove(proxy);
            }

            dlg = delegate(){
                label2.Text = "代理: " + proxies.Count;
            };
            this.BeginInvoke(dlg);
        }

        public void Finish()
        {
            lock (this)
            {
                finish++;

                if (finish == Accounts.Count)
                {
                     dlg = delegate(){
                        toolStripStatusLabel1.Text = "运行结束";
                        button1.Text = "开始";
                     };
                     this.BeginInvoke(dlg);
                }
            }

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

        public void log(int type, String line)
        {
            dlg = delegate()
            {
                visited++;
                label3.Text = visited+" / " + accounts.Count + " = "+(100*visited/accounts.Count).ToString("0.00")+"%";

                //TODO, 加入日志文件
                this.textBox1.AppendText(DateTime.Now.ToString("[yyyy/MM/dd HH:mm:ss] "));
                this.textBox1.AppendText("DETECTED: " + line + "=" + type.ToString() + "\r\n");
            };
            this.BeginInvoke(dlg);
        }

        private void 选项OToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new Option().ShowDialog();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            if (cfa == null)
            {
                MessageBox.Show("加载配置文件失败!");
            }
        }
    }
}
