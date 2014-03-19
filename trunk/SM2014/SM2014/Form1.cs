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
using Ws.Hoyland.Util;
using System.Text.RegularExpressions;
using System.Threading;
using System.Configuration;
using System.Globalization;
using Ws.Hoyland.CSharp.XThread;

namespace SM2014
{
    public partial class Form1 : Form
    {
        private ThreadManager manager = null;

        private StreamWriter[] output = new StreamWriter[4]; //成功，失败，未运行
        private String[] fns = new String[] { "密码正确", "密码错误", "帐号冻结", "未识别" };
        private String xpath = AppDomain.CurrentDomain.BaseDirectory;
        private List<String> accounts;
        private int visited = 0;
        private int finish = 0;
        private Configuration cfa = null;
        private int idx = 0;
        private bool run = false;
        private Object obj = new Object();
        private Object pobj = new Object();
        private int[] stat = new int[3];

        public int Idx
        {
            get { return idx; }
            set { idx = value; }
        }

        public List<String> Accounts
        {
            get { return accounts; }
            set { accounts = value; }
        }

        //private int pidx = 0; //当前代理索引

        private static Form1 FORM;

        public static Form1 GetInstance()
        {
            return FORM;
        }

        public Form1()
        {
            InitializeComponent();
            FORM = this;
            //MessageBox.Show(System.Net.ServicePointManager.DefaultConnectionLimit.ToString());
            //System.Net.ServicePointManager.DefaultConnectionLimit = 1024;
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
            this.BeginInvoke(new Action(() =>
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
            }));
        }

        private void ready()
        {
            this.BeginInvoke(new Action(() =>
            {
                if (accounts != null && accounts.Count > 0 && Engine.GetInstance().Proxies != null && Engine.GetInstance().Proxies.Count > 0)
                {
                    button1.Enabled = true;
                }
                else
                {
                    button1.Enabled = false;
                }
            }));
        }

        private void 导入代理PToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.BeginInvoke(new Action(() =>
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
                            Engine.GetInstance().Proxies = new List<String>();

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

                                    Engine.GetInstance().Proxies.Add(line);
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

                            label2.Text = "代理: " + Engine.GetInstance().Proxies.Count;
                            ready();
                        }
                        catch (Exception ex)
                        {
                            throw ex;
                        }
                    }
                }
            }));
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.BeginInvoke(new Action(() =>
            {
                if (button1.Text.Equals("开始"))
                {
                    Info("");
                    Info("开始运行");
                    Info("==================");
                    Info("");
                    String tm = DateTime.Now.ToString("yyyy年MM月dd日 HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo);
                    for (int i = 0; i < output.Length; i++)
                    {
                        try
                        {
                            output[i] = File.AppendText(xpath + fns[i] + "-" + tm + ".txt");
                        }
                        catch (Exception ex)
                        {
                            throw ex;
                        }
                    }

                    cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

                    if (cfa == null)
                    {
                        MessageBox.Show("加载配置文件失败!");
                    }

                    ConfigurationManager.RefreshSection("appSettings");

                    finish = 0;
                    visited = 0;

                    for (int i = 0; i < stat.Length; i++)
                    {
                        stat[i] = 0;
                    }

                    toolStripStatusLabel1.Text = "正在运行";

                    int tc = Int32.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value);

                    manager = new ThreadManager(tc);

                    //ThreadPool.SetMinThreads(1, 0);
                    //ThreadPool.SetMaxThreads(tc, 0);

                    for (int i = 0; i < accounts.Count; i++)
                    {
                        //N 个任务
                        //Task task = new Task(this, accounts[i]);
                        //ThreadPool.QueueUserWorkItem(new WaitCallback(task.Run));
                        manager.Queue(new Task(accounts[i]));
                    }

                    button1.Text = "停止";

                    run = true;
                    manager.Execute();
                }
                else
                {
                    Shutdown();
                    Info("");
                    Info("==================");
                    Info("运行结束");
                    Info("");
                }
            }));
        }

        public void Queue(String line)
        {
            if (run)
            {
                //not need to add
                //lock (accounts)
                //{
                //    accounts.Add(line);
                //}
                manager.Queue(new Task(line));
            }
        }

        private void Shutdown()
        {
            if (run)
            {
                run = false;

                //暂停处理代码
                toolStripStatusLabel1.Text = "未运行";
                button1.Text = "开始";
                
                if (manager != null)
                {
                    this.manager.Shutdown();
                }

                for (int i = 0; i < output.Length; i++)
                {
                    try
                    {
                        if (output[i] != null)
                        {
                            output[i].Close();
                            output[i] = null;
                        }
                    }
                    catch (Exception)
                    {
                        //throw ex;
                    }
                }
            }
        }

        public void UpdateProxy(int count)
        {
            if (!this.IsDisposed)
            {
                this.BeginInvoke(new Action(() =>
                {
                    lock (pobj)
                    {
                        label2.Text = "代理: " + count;
                    }
                }));
            }
        }

        public void Finish()
        {
            //GC.Collect();
            lock (obj)
            {
                /**
                int tc = Int32.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value);

                int workerThreads = 0;
                int completionPortThreads = 0;
                ThreadPool.GetAvailableThreads(out workerThreads, out completionPortThreads);

                if (workerThreads > tc * 0.2) //到达一半时候，自动添加任务 , 90%时候自动增加
                {
                    for (int i=0; idx < accounts.Count && i < tc; idx++, i++)
                    {
                        //N 个任务
                        Task task = new Task(this, accounts[idx]);
                        //new WaitCallback(task.Run).
                        ThreadPool.QueueUserWorkItem(new WaitCallback(task.Run));
                    }
                }
                **/
                finish++;

                if (finish % 100 == 0)
                {
                    if (!this.IsDisposed)
                    {
                        this.BeginInvoke(new Action(() =>
                        {
                            lock (this.textBox1)
                            {
                                if (this.textBox1.TextLength > 10000)
                                {
                                    this.textBox1.ResetText();
                                }
                            }
                        }));
                    }
                }

                /**
                if (finish == Accounts.Count) //自动产生的，目前不太可能执行到
                {
                    dlg = delegate()
                    {
                        toolStripStatusLabel1.Text = "运行结束";
                        button1.Text = "开始";
                    };
                    if (!this.IsDisposed)
                    {
                        this.BeginInvoke(dlg);
                    }
                }**/
            }

        }

        public void Info(String message)
        {
            this.BeginInvoke(new Action(() =>
            {
                lock (this.textBox1)
                {
                    this.textBox1.AppendText(DateTime.Now.ToString("[yyyy/MM/dd HH:mm:ss] "));
                    this.textBox1.AppendText(message + "\r\n");
                }
            }));
        }

        public void Log(int type, String line)
        {           
            this.BeginInvoke(new Action(() =>
            {
                visited++;
                stat[type]++;

                label3.Text = visited + " / " + accounts.Count + " = " + (100 * visited / accounts.Count).ToString("0.00") + "%";
                label4.Text = stat[0] + " / " + stat[1] + " / " + stat[2];

                //TODO, 加入日志文件
                lock (this.textBox1)
                {
                    this.textBox1.AppendText(DateTime.Now.ToString("[yyyy/MM/dd HH:mm:ss] "));
                    this.textBox1.AppendText("DETECTED: " + line + "=" + type.ToString() + "\r\n");
                }

                try
                {
                    output[type].WriteLine(line);
                    output[type].Flush();
                }
                catch (Exception)
                {
                    //throw e;
                };
            }));
        }

        private void 选项OToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new Option().ShowDialog();
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            Shutdown();
        }
    }
}
