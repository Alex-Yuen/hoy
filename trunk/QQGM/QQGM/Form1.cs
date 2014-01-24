using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using ws.hoyland;
using System.Threading;
using System.IO;
using System.Text.RegularExpressions;
using System.Net;

namespace QQGM
{
    public partial class Form1 : Form
    {
        private delegate void Delegate();
        private Delegate dlg;

        private bool isLogin = false;
        private bool running = false;
        private DataTable table = new DataTable();
        private int type = 0;

        public Form1()
        {
            InitializeComponent();

            table.Columns.Add("ID", Type.GetType("System.String"));
            table.Columns.Add("帐号", Type.GetType("System.String")); 
            table.Columns.Add("密码", Type.GetType("System.String"));
            table.Columns.Add("问题1", Type.GetType("System.String"));
            table.Columns.Add("答案1", Type.GetType("System.String"));
            table.Columns.Add("问题2", Type.GetType("System.String"));
            table.Columns.Add("答案2", Type.GetType("System.String"));
            table.Columns.Add("问题3", Type.GetType("System.String"));
            table.Columns.Add("答案3", Type.GetType("System.String"));

            dataGridView1.DataSource = table;
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            comboBox2.SelectedIndex = 0;
            comboBox2.SelectedIndex = 0;
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void 关于AToolStripMenuItem_Click(object sender, EventArgs e)
        {
            AboutBox1 about = new AboutBox1();
            about.Show();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            button1.Enabled = false;
            Thread lt;
            lt = new Thread(login);
            lt.Start();
        }

        public void login()
        {
            dlg = delegate()
            {
                if (comboBox2.SelectedIndex == 0)
                {
                    int nAppId;         // 软件ＩＤ，开发者分成必要参数。登录开发者后台【我的软件】获得！
                    string lpAppKey;    // 软件密钥，开发者分成必要参数。登录开发者后台【我的软件】获得！

                    nAppId = 175;
                    lpAppKey = "8c31eb9cf312478eda8301b87232e731";

                    YDMWrapper.YDM_SetAppInfo(nAppId, lpAppKey);

                    //login
                    toolStripStatusLabel1.Text = "正在登录...";

                    string username, password;
                    int ret;

                    username = textBox2.Text;
                    password = textBox3.Text;

                    // 返回云打码用户UID，大于零为登录成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                    ret = YDMWrapper.YDM_Login(username, password);

                    if (ret > 0)
                    {
                        isLogin = true;
                        toolStripStatusLabel1.Text = "登陆成功，ID=" + ret.ToString();

                    }
                    else
                    {
                        isLogin = false;
                        toolStripStatusLabel1.Text = "登陆失败，错误代码：" + ret.ToString();
                    }
                }
                button1.Enabled = true;
                ready();
            };
            this.Invoke(dlg);
        }

        private void ready()
        {
            if (isLogin && table.Rows.Count > 0)
            {
                button2.Enabled = true;
            }
            else
            {
                button2.Enabled = false;
            }
        }

        private void 导入帐号LToolStripMenuItem_Click(object sender, EventArgs e)
        {
            dlg = delegate()
            {
                OpenFileDialog dialog = new OpenFileDialog();
                dialog.Filter = "所有文件(*.*)|*.*";

                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    string fn = dialog.FileName;
                    FileStream fs = new FileStream(fn, FileMode.Open);
                    StreamReader m_streamReader = new StreamReader(fs);
                    m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);
                    int i = 0;
                    string line = null;
                    while ((line = m_streamReader.ReadLine()) != null)
                    {
                        if (!line.Equals(""))
                        {
                            line = (++i) + "----" + line;
                            string[] lns = Regex.Split(line, "----");
                            DataRow row = table.NewRow();
                            //row[0] = ++i;
                            for (int m = 0; m < table.Columns.Count; m++)
                            {
                                row[m] = lns[m];
                            }
                            //row[1] = lns[0];
                            table.Rows.Add(row);

                            dataGridView1.DataSource = table;
                        }

                    }
                    m_streamReader.Close();
                    m_streamReader.Dispose();
                    fs.Close();
                    fs.Dispose();

                    ready();
                }
            };
            this.BeginInvoke(dlg);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (!running)
            {
                type = comboBox2.SelectedIndex;
                ThreadPool.SetMinThreads(4, 4);
                ThreadPool.SetMaxThreads(8, 8);
                Task task = null;
                for (int i = 0; i < table.Rows.Count; i++)
                {
                    task = new Task();
                    task.ID = Int32.Parse((string)table.Rows[i]["ID"]);
                    task.Account = (string)table.Rows[i][1];
                    task.Password = (string)table.Rows[i][2];

                    if (table.Rows[i][3] != null && !table.Rows[i][1].Equals(""))
                    {
                        task.Isdna = true;
                        task.Q1 = (string)table.Rows[i][3];
                        task.A1 = (string)table.Rows[i][4];
                        task.Q2 = (string)table.Rows[i][5];
                        task.A2 = (string)table.Rows[i][6];
                        task.Q3 = (string)table.Rows[i][7];
                        task.A3 = (string)table.Rows[i][8];
                    }
                    else
                    {
                        task.Isdna = false;
                    }
                }
                ThreadPool.QueueUserWorkItem(new WaitCallback(process), task);
            }
            else
            {
                //ThreadPool.
                //stop
            }
            running = !running;
        }

        private void process(Object stateInfo)
        {
            Task task = (Task)stateInfo;
            task.process(this.type);
        }

        private void textBox3_KeyUp(object sender, KeyEventArgs e)
        {
            if (e.KeyValue == 13)
            {
                button1.Focus();
                button1.PerformClick();
            }
            //Console.WriteLine(e.KeyValue);
        }

        private void 选项OToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new Option().ShowDialog();
        }
    }
}
