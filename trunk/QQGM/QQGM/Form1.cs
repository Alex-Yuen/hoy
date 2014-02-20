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
using System.Configuration;
using System.Globalization;
using System.Runtime.CompilerServices;
using System.Collections;
using System.Diagnostics;
using System.Runtime.InteropServices;
using FileEncoding;
using System.Management;
using System.Security.Cryptography;
using RedQ;

namespace QQGM
{
    public partial class Form1 : Form
    {
        private Form pf;
        private delegate void Delegate();
        private Delegate dlg;

        private bool isLogin = false;
        private bool running = false;
        private DataTable table = new DataTable();
        private int type = 0;
        private int cptype = -1;
        private Configuration cfa = null;
        //private bool ns = false;
        private int[] statis = new int[6];
        private StreamWriter[] output = new StreamWriter[9];//
        private int frec = 0;
        private HashSet<Task> tasks = new HashSet<Task>();
        private Hashtable ips = new Hashtable();
        //System.Collections.Hashtable
        private Form1()
        {
            InitializeComponent();

            table.Columns.Add("ID", Type.GetType("System.String"));
            table.Columns.Add("帐号", Type.GetType("System.String"));
            table.Columns.Add("密码", Type.GetType("System.String"));
            table.Columns.Add("状态", Type.GetType("System.String"));
            table.Columns.Add("问题1", Type.GetType("System.String"));
            table.Columns.Add("答案1", Type.GetType("System.String"));
            table.Columns.Add("问题2", Type.GetType("System.String"));
            table.Columns.Add("答案2", Type.GetType("System.String"));
            table.Columns.Add("问题3", Type.GetType("System.String"));
            table.Columns.Add("答案3", Type.GetType("System.String"));

            dataGridView1.DataSource = table;
        }

        public Form1(Form pf, string p)
        {
            // TODO: Complete member initialization
            this.pf = pf;
            this.p = p;

            InitializeComponent();

            table.Columns.Add("ID", Type.GetType("System.String"));
            table.Columns.Add("帐号", Type.GetType("System.String"));
            table.Columns.Add("密码", Type.GetType("System.String"));
            table.Columns.Add("状态", Type.GetType("System.String"));
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
            this.Text += this.p;
            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            if (cfa == null)
            {
                MessageBox.Show("加载配置文件失败!");
            }

            comboBox1.SelectedIndex = 0;
            ConfigurationManager.RefreshSection("appSettings");
            try
            {
                comboBox2.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["CPT_TYPE"].Value);
                comboBox1.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["OPT_TYPE"].Value);

                textBox2.Text = cfa.AppSettings.Settings["ACCOUNT"].Value;
                if ("True".Equals(cfa.AppSettings.Settings["REM_PASSWORD"].Value))
                {
                    checkBox1.Checked = true;
                    textBox3.Text = cfa.AppSettings.Settings["PASSWORD"].Value;
                }

                if ("True".Equals(cfa.AppSettings.Settings["AUTO_LOGIN"].Value))
                {
                    checkBox2.Checked = true;
                    //TODO, autologin
                }

                setlogin();

                if (checkBox2.Checked)
                {//自动登录 
                    //Thread lt;
                    //lt = new Thread(login);
                    //lt.Start();

                    System.Timers.Timer t = new System.Timers.Timer(1000);
                    //实例化Timer类，设置间隔时间为10000毫秒；   
                    t.Elapsed +=
                    new System.Timers.ElapsedEventHandler(login);
                    //到达时间的时候执行事件；   
                    t.AutoReset = false;
                    //设置是执行一次（false）还是一直执行(true)；   
                    t.Enabled = true;
                    //是否执行System.Timers.Timer.Elapsed事件；  
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }

            //comboBox2.SelectedIndex = 0;
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void 关于AToolStripMenuItem_Click(object sender, EventArgs e)
        {
            AboutBox1 about = new AboutBox1();
            about.ShowDialog();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (comboBox2.Enabled == true)
            {
                //button1.Enabled = false;
                System.Timers.Timer t = new System.Timers.Timer(1);
                //实例化Timer类，设置间隔时间为10000毫秒；   
                t.Elapsed +=
                new System.Timers.ElapsedEventHandler(login);
                //到达时间的时候执行事件；   
                t.AutoReset = false;
                //设置是执行一次（false）还是一直执行(true)；   
                t.Enabled = true;
                /**
                Thread lt;
                lt = new Thread(login, null, null);
                lt.Start();**/
            }
            else
            {
                label10.Text = "密码:";
                button1.Text = "登录";
                comboBox2.Enabled = true;
                checkBox1.Visible = true;
                checkBox2.Visible = true;

                textBox2.Visible = true;
                textBox3.Visible = true;

                label15.Text = "";
                label15.Visible = false;

                label16.Text = "";
                label16.Visible = false;

            }
        }

        private byte[] UMD5(string str)
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

        private byte[] getKey()
        {
            byte[] key = new byte[16];
            Task.random.NextBytes(key);
            return key;
        }

        public byte[] hexStringToByte(string hex)
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

        private byte toByte(char c)
        {
            byte b = (byte)"0123456789ABCDEF".IndexOf(c);
            return b;
        }

        private string byteArrayToHexString(byte[] b)
        {
            StringBuilder resultSb = new StringBuilder();
            for (int i = 0; i < b.Length; i++)
            {
                resultSb.Append(byteToHexString(b[i]));
            }
            return resultSb.ToString();
        }
        private string byteToHexString(byte b)
        {
            int n = b;
            if (n < 0)
                n = 256 + n;
            int d1 = n / 16;
            int d2 = n % 16;
            return hexDigits[d1] + hexDigits[d2];
        }
        private static string[] hexDigits = {"0", "1", "2", "3", "4",  
	        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        private string p;
        
        private void login(object source, System.Timers.ElapsedEventArgs e)
        {
            string username = null;
            string password = null;

            int ret = -1;
            int score = 0;

            dlg = delegate()
            {
                button1.Enabled = false;
                cptype = comboBox2.SelectedIndex;
                toolStripStatusLabel1.Text = "正在登录...";
                username = textBox2.Text;
                password = textBox3.Text;
            };
            this.Invoke(dlg);

            //dlg = delegate()
            {
                int nAppId;         // 软件ＩＤ，开发者分成必要参数。登录开发者后台【我的软件】获得！
                string lpAppKey;    // 软件密钥，开发者分成必要参数。登录开发者后台【我的软件】获得！
                              
                //login


                if (cptype == 0)
                {
                    nAppId = 175;
                    lpAppKey = "8c31eb9cf312478eda8301b87232e731";

                    YDMWrapper.YDM_SetAppInfo(nAppId, lpAppKey);
                    ret = YDMWrapper.YDM_Login(username, password);
                }
                else
                {
                    nAppId = 95099;
                    lpAppKey = "08573f0698dc43b4b35761c9ab64f014";
                    ret = UUWrapper.uu_login(username, password);
                }

                if (ret > 0)
                {
                    isLogin = true;

                    if (cptype == 0)
                    {
                        //获取题分
                        score = YDMWrapper.YDM_GetBalance(username, password);
                    }
                    else
                    {
                        score = UUWrapper.uu_getScore(username, password);
                    }
                    //toolStripStatusLabel1.Text = "1";
                    
                    //toolStripStatusLabel1.Text = "5";
                }
                else
                {
                    isLogin = false;
                }

            }

            dlg = delegate()
            {
                if (isLogin)
                {
                    toolStripStatusLabel1.Text = "登录成功，ID=" + ret.ToString();

                    cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

                    if (cfa == null)
                    {
                        MessageBox.Show("加载配置文件失败!");
                    }

                    ConfigurationManager.RefreshSection("appSettings");
                    //toolStripStatusLabel1.Text = "2:" + cfa.AppSettings + "/" + cfa.AppSettings.Settings["ACCOUNT"] + "/" + textBox2;
                    //保存登录参数
                    cfa.AppSettings.Settings["ACCOUNT"].Value = textBox2.Text;
                    //toolStripStatusLabel1.Text = "2.1";
                    if (checkBox1.Checked)
                    {
                        //toolStripStatusLabel1.Text = "2.2";
                        cfa.AppSettings.Settings["PASSWORD"].Value = textBox3.Text;
                        //toolStripStatusLabel1.Text = "2.3";
                    }
                    else
                    {
                        //toolStripStatusLabel1.Text = "2.4";
                        cfa.AppSettings.Settings["PASSWORD"].Value = "";
                        //toolStripStatusLabel1.Text = "2.5";
                    }
                    //toolStripStatusLabel1.Text = "2.6";
                    cfa.AppSettings.Settings["REM_PASSWORD"].Value = checkBox1.Checked.ToString();
                    //toolStripStatusLabel1.Text = "2.7";
                    cfa.AppSettings.Settings["AUTO_LOGIN"].Value = checkBox2.Checked.ToString();
                    //toolStripStatusLabel1.Text = "3";
                    cfa.Save();
                    //toolStripStatusLabel1.Text = "4";

                    label10.Text = "题分:";
                    button1.Text = "切换帐号";
                    comboBox2.Enabled = false;
                    checkBox1.Visible = false;
                    checkBox2.Visible = false;

                    textBox2.Visible = false;
                    textBox3.Visible = false;

                    label15.Text = textBox2.Text;
                    label15.Visible = true;

                    label16.Text = score.ToString();
                    label16.Visible = true;
                }
                else
                {

                    toolStripStatusLabel1.Text = "登陆失败，错误代码：" + ret.ToString();
                }

                button1.Enabled = true;
                //toolStripStatusLabel1.Text = "6";
                ready();
            };

            this.Invoke(dlg);
            //toolStripStatusLabel1.Text = "7";
            //this.BeginInvoke(dlg);
            //toolStripStatusLabel1.Text = "8";
        }

        private void ready()
        {
            if (isLogin && table.Rows.Count > 0 && comboBox1.SelectedIndex != 0)
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
                    table.Clear();

                    string fn = dialog.FileName;

                    Encoding ecdtype = EncodingType.GetType(fn);
                    FileStream fs = new FileStream(fn, FileMode.Open);
                    StreamReader m_streamReader = new StreamReader(fs, ecdtype);
                    m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);
                    int i = 0;
                    string line = null;
                    while ((line = m_streamReader.ReadLine()) != null)
                    {
                        if (!line.Equals(""))
                        {
                            line = (++i) + "----" + line;
                            string[] lns = Regex.Split(line, "----");
                            List<string> listArr = new List<string>();
                            listArr.AddRange(lns);
                            listArr.Insert(3, "初始化");
                            lns = listArr.ToArray();

                            DataRow row = table.NewRow();
                            //row[0] = ++i;
                            if (lns.Length < 9)
                            {
                                for (int m = 0; m < 4; m++)
                                {
                                    row[m] = lns[m];
                                }
                            }
                            else
                            {
                                for (int m = 0; m < table.Columns.Count && m < lns.Length; m++)
                                {
                                    row[m] = lns[m];
                                }
                            }
                            //row[1] = lns[0];
                            table.Rows.Add(row);

                            dataGridView1.DataSource = table;
                        }

                    }
                    statis[0] = table.Rows.Count;
                    label4.Text = statis[0].ToString();
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
                type = comboBox1.SelectedIndex;
                toolStripProgressBar1.Value = 0;
                toolStripProgressBar1.Visible = true;
                tasks.Clear();

                for (int i = 1; i < statis.Length; i++)//0 not need init
                {
                    statis[i] = 0;
                    //label4.Text = statis[0].ToString();
                }

                label5.Text = statis[1].ToString();
                label11.Text = statis[2].ToString();
                label12.Text = statis[3].ToString();
                label13.Text = statis[4].ToString();
                label14.Text = statis[5].ToString();

                if (type == 0)
                {
                    MessageBox.Show("请选择操作类型");
                    return;
                }

                DateTime dt = DateTime.Now;

                if (type == 1 || type == 2 || type == 4)
                {
                    output[0] = File.AppendText(Application.StartupPath + "\\改密成功-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                    output[1] = File.AppendText(Application.StartupPath + "\\改密失败-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                }

                if (type == 3 || type == 4)
                {
                    output[2] = File.AppendText(Application.StartupPath + "\\改保成功-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                    output[3] = File.AppendText(Application.StartupPath + "\\改保失败-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                }

                output[4] = File.AppendText(Application.StartupPath + "\\帐号冻结-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                output[5] = File.AppendText(Application.StartupPath + "\\密码错误-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                output[6] = File.AppendText(Application.StartupPath + "\\短信验证-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                output[7] = File.AppendText(Application.StartupPath + "\\激活靓号-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");
                output[8] = File.AppendText(Application.StartupPath + "\\密保异常-" + dt.ToString("yyyy年MM月dd日HH时mm分ss秒", DateTimeFormatInfo.InvariantInfo) + ".txt");

                cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

                if (cfa == null)
                {
                    MessageBox.Show("加载配置文件失败!");
                }

                ConfigurationManager.RefreshSection("appSettings");
                //保存操作类型参数
                cfa.AppSettings.Settings["OPT_TYPE"].Value = comboBox1.SelectedIndex.ToString();
                cfa.Save();

                //ConfigurationManager.RefreshSection("appSettings");
                int mt = Int32.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value);
                //Console.WriteLine("MT:" + mt);
                ThreadPool.SetMinThreads(1, 0);
                ThreadPool.SetMaxThreads(mt, 0);
                Task task = null;
                for (int i = 0; i < table.Rows.Count; i++)
                {
                    task = new Task();
                    task.ID = Int32.Parse((string)table.Rows[i]["ID"]);
                    task.Account = (string)table.Rows[i][1];
                    task.Password = (string)table.Rows[i][2];

                    if (table.Rows[i][9] != null && !table.Rows[i][9].ToString().Equals(""))
                    {
                        task.Isdna = true;
                        task.Q1 = (string)table.Rows[i][4];
                        task.A1 = (string)table.Rows[i][5];
                        task.Q2 = (string)table.Rows[i][6];
                        task.A2 = (string)table.Rows[i][7];
                        task.Q3 = (string)table.Rows[i][8];
                        task.A3 = (string)table.Rows[i][9];

                        task.Original = (string)table.Rows[i][1] + "----" + (string)table.Rows[i][2] + "----" + (string)table.Rows[i][4] + "----" + (string)table.Rows[i][5] + "----" + (string)table.Rows[i][6] + "----" + (string)table.Rows[i][7] + "----" + (string)table.Rows[i][8] + "----" + (string)table.Rows[i][9];
                    }
                    else
                    {
                        task.Isdna = false;
                        task.Original = (string)table.Rows[i][1] + "----" + (string)table.Rows[i][2];
                    }
                    tasks.Add(task);
                    ThreadPool.QueueUserWorkItem(new WaitCallback(process), task);
                }
                button2.Text = "结束";
            }
            else
            {
                //toolStripProgressBar1.Visible = false;
                button2.Text = "开始";
                shutdown();
            }
            running = !running;
        }

        private void process(Object stateInfo)
        {
            Task task = (Task)stateInfo;
            task.process(this, this.type);
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

        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox2.Checked)
            {
                checkBox1.Checked = true;
            }
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if (!checkBox1.Checked)
            {
                checkBox2.Checked = false;
            }
        }

        private void textBox3_Enter(object sender, EventArgs e)
        {
            textBox3.SelectAll();
            //           ns = true;
        }

        /**
        private void textBox3_MouseDown(object sender, MouseEventArgs e)
        {
            if (ns)
            {
                textBox3.SelectAll();
                ns = false;
            }
        }**/

        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            setlogin();
        }

        private void setlogin()
        {
            if (comboBox2.SelectedIndex == 2)
            {
                textBox2.Enabled = false;
                textBox3.Enabled = false;
                checkBox1.Enabled = false;
                checkBox2.Enabled = false;
                button1.Enabled = false;
            }
            else
            {
                textBox2.Enabled = true;
                textBox3.Enabled = true;
                checkBox1.Enabled = true;
                checkBox2.Enabled = true;
                button1.Enabled = true;
            }
        }

        public void setImage(byte[] bs, int size)
        {
            dlg = delegate()
            {
                MemoryStream ms = new MemoryStream();
                ms.Write(bs, 0, size);
                pictureBox1.Image = Image.FromStream(ms);
                ms.Close();
            };

            this.BeginInvoke(dlg);
        }

        public void info(int tid, string info)
        {
            dlg = delegate()
            {
                this.table.Rows[tid - 1][3] = info;
            };
            this.BeginInvoke(dlg);
        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            ready();
        }

        public int getCptType()
        {
            return cptype;
        }

        public void log(int type, string info)
        {
            output[type].WriteLine(info);
            output[type].Flush();
        }

        /**
        public void saveNewDNA(string account, string[] dna)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dna.Length; i++)
            {
                sb.Append("----" + dna[i]);
            }
            output[1].WriteLine(account + sb.ToString());
            output[1].Flush();
        }**/

        public void stat(int type)
        {
            //Console.WriteLine(">>:" + type);
            dlg = delegate()
            {
                statis[type]++;
                //label4.Text = statis[0].ToString();
                label5.Text = statis[1].ToString();
                label11.Text = statis[2].ToString();
                label12.Text = statis[3].ToString();
                label13.Text = statis[4].ToString();
                label14.Text = statis[5].ToString();

                lock (this)
                {
                    if (type == 1)
                    {
                        toolStripProgressBar1.Value = (statis[1] * 100) / statis[0];
                        frec++;
                        //是否启动重拨功能
                        ConfigurationManager.RefreshSection("appSettings");
                        if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG"].Value) && Int32.Parse(cfa.AppSettings.Settings["REC_FLAG_F1"].Value) == frec)
                        {
                            frec = 0;

                            //通知阻塞
                            foreach (Task task in tasks)
                            {
                                task.Pause();
                            }

                            try
                            {
                                //Thread.Sleep(Int32.Parse(cfa.AppSettings.Settings["REC_FLAG_F2"].Value));
                                //重拨
                                System.Timers.Timer t = new System.Timers.Timer(Int32.Parse(cfa.AppSettings.Settings["REC_FLAG_F2"].Value) * 1000);
                                //实例化Timer类，设置间隔时间为10000毫秒；   
                                t.Elapsed +=
                                new System.Timers.ElapsedEventHandler(Recon);
                                //到达时间的时候执行事件；   
                                t.AutoReset = false;
                                //设置是执行一次（false）还是一直执行(true)；   
                                t.Enabled = true;
                            }
                            catch (Exception e)
                            {
                                MessageBox.Show(e.Message);
                            }
                        }
                    }
                }
            };
            this.BeginInvoke(dlg);
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            this.pf.Close();
            shutdown();
        }

        private void shutdown()
        {
            for (int i = 0; i < output.Length; i++)
            {
                if (output[i] != null)
                {
                    output[i].Close();
                }
            }
        }

        private void Recon(object source, System.Timers.ElapsedEventArgs e)
        {
            //dlg = delegate()
            {
                //MessageBox.Show("正在重拨");

                ConfigurationManager.RefreshSection("appSettings");
                string adsl = cfa.AppSettings.Settings["REC_FLAG_F5"].Value;
                if (adsl == null || "".Equals(adsl))
                {
                    MessageBox.Show("请指定宽带连接名称");
                    return;
                }

                List<string> adls = GetAllAdslName();
                bool hasadls = false ;
                foreach (string ad in adls)
                {
                    if (ad.Equals(adsl))
                    {
                        hasadls = true;
                        break;
                    }
                }

                if (!hasadls)
                {
                    MessageBox.Show("请指定宽带连接名称");
                    return;
                }

                string account = cfa.AppSettings.Settings["REC_FLAG_F6"].Value;
                string password = cfa.AppSettings.Settings["REC_FLAG_F7"].Value;

                bool st = false;
                string cut = "rasdial \"" + adsl + "\" /disconnect";
                string link = "rasdial \"" + adsl + "\" "
                                        + account
                                        + " "
                                        + password;
                try
                {
                    bool fo = true;
                    bool fi = true;

                    int tfo = 0;
                    int tfi = 0;

                    while (fo && tfo < 4)
                    {
                        string result = Execute(cut);
                        Console.WriteLine("CUT:" + result);
                        if (result.IndexOf("没有连接") == -1)
                        {
                            //System.err.println("CUT1");
                            fo = false; // 断线成功，将跳出外循环
                            fi = true;

                            tfi = 0;
                            //System.err.println("CUT2:" + fi + "/" + configuration.getProperty("AWCONN") + "/" + tfi);
                            while (fi && ("True".Equals(cfa.AppSettings.Settings["REC_FLAG_F3"].Value) || ("False".Equals(cfa.AppSettings.Settings["REC_FLAG_F3"].Value) && tfi < 4)))
                            {
                                //System.err.println("CUT3");
                                result = Execute(link);
                                //System.err.println("LINK:" + result);
                                if (result
                                        .IndexOf("已连接") > 0 || result
                                        .IndexOf("已经连接") > 0)
                                {
                                    //System.err.println("CUT4");

                                    result = Execute("ipconfig");
                                    result = result.Substring(result.IndexOf(adsl));
                                    if (result.IndexOf("IP Address") != -1)
                                    {
                                        result = result.Substring(result.IndexOf("IP Address"));
                                    }
                                    if (result.IndexOf("IPv4 地址") != -1)
                                    {
                                        result = result.Substring(result.IndexOf("IPv4 地址"));
                                    }

                                    result = result.Substring(result.IndexOf(":") + 2);
                                    result = result.Substring(0, result.IndexOf("\n "));
                                    //String ip = result;
                                    string rip = result;
                                    string ip = rip;
                                    /**
                                     * IP段重复的判断
                                    if ("true".equals(recflag))
                                    {
                                        ip = result.substring(0, result.lastIndexOf("."));
                                    }**/

                                    //System.err.println("ip=" + ip);
                                    if (ips.ContainsKey(ip) && "True".Equals(cfa.AppSettings.Settings["REC_FLAG_F4"].Value)) //判断是否重复
                                    {
                                        long time = (long)ips[ip];
                                        if (currentTimeMillis() - time >= 1 * 60 * 60 * 1000)
                                        {
                                            //System.err.println("IP重复，但超过1小时，拨号成功:" + ip);
                                            //cip = rip;
                                            ips[ip] = currentTimeMillis();
                                            fi = false;//跳出内循环
                                            st = true;
                                            //break;
                                        }
                                        else
                                        {
                                            //System.err.println("IP重复，未超过1小时，重新拨号:" + ip);
                                            fo = true;
                                            fi = false;
                                            tfo = 0;
                                            st = false;
                                            //continue;
                                        }
                                    }
                                    else
                                    {
                                        //System.err.println("IP不重复，拨号成功:" + ip);
                                        //cip = rip;
                                        ips[ip] = currentTimeMillis();
                                        fi = false;
                                        st = true;
                                        //break;
                                    }
                                }
                                else
                                {
                                    //System.err.println("CUT5");
                                    //System.err.println("连接失败(" + tfi + ")");
                                    if (tfi < 3)
                                    {
                                        try
                                        {
                                            Thread.Sleep(1000 * 30);
                                        }
                                        catch (Exception ex)
                                        {
                                            MessageBox.Show(ex.Message);
                                        }
                                    }
                                    tfi++;//允许3次循环
                                    //break;
                                }
                            }//while in
                        }
                        else
                        {
                            //System.err.println("CUT6");
                            Console.WriteLine("没有连接(" + tfo + ")");
                            if (tfo < 3)
                            {
                                try
                                {
                                    Thread.Sleep(1000 * 30);
                                }
                                catch (Exception ex)
                                {
                                    MessageBox.Show(ex.Message);
                                }
                            }
                            tfo++; //允许3次循环
                            //break;
                        }
                    }//while out
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }

                if (st)
                {
                    //唤醒
                    foreach (Task task in tasks)
                    {
                        task.Pause();//避免下次继续pause
                    }
                    //notify
                    Monitor.PulseAll(this);

                    //MessageBox.Show("重拨结束");
                }
                else
                {
                    //MessageBox.Show("重拨失败");
                }

                //MessageBox.Show("重拨结束");
            }
            //this.BeginInvoke(dlg);
        }

        private string Execute(string cmd)
        {
            string output = "";
            try
            {
                Process p = new Process();
                p.StartInfo.FileName = "cmd.exe";
                p.StartInfo.Arguments = "/c " + cmd;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.RedirectStandardInput = true;
                p.StartInfo.RedirectStandardOutput = true;

                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;

                p.Start();

                //Console.WriteLine(output);
                p.WaitForExit();
                output = p.StandardOutput.ReadToEnd();
                p.Close();
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
            }
            return output;

            /**
            Runtime.getRuntime().exec("cmd /c " + cmd);
            StringBuilder result = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream(), "GB2312"));
            String line;
            while ((line = br.readLine()) != null)
            {
                result.append(line + "\n");
            }
            return result.toString();**/
        }

        private long currentTimeMillis()
        {
            DateTime Jan1st1970 = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            return (long)((DateTime.UtcNow - Jan1st1970).TotalMilliseconds);
        }

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
        public struct RasEntryName      //define the struct to receive the entry name
        {
            public int dwSize;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256 + 1)]
            public string szEntryName;
#if WINVER5
     public int dwFlags;
     [MarshalAs(UnmanagedType.ByValTStr,SizeConst=260+1)]
     public string szPhonebookPath;
#endif
        }

        [DllImport("rasapi32.dll", CharSet = CharSet.Auto)]

        public extern static uint RasEnumEntries(
            string reserved,              // reserved, must be NULL
            string lpszPhonebook,         // pointer to full path and file name of phone-book file
            [In, Out]RasEntryName[] lprasentryname, // buffer to receive phone-book entries
            ref int lpcb,                  // size in bytes of buffer
            out int lpcEntries             // number of entries written to buffer
        );

        public List<string> GetAllAdslName()
        {
            List<string> list = new List<string>();
            int lpNames = 1;
            int entryNameSize = 0;
            int lpSize = 0;
            RasEntryName[] names = null;
            entryNameSize = Marshal.SizeOf(typeof(RasEntryName));
            lpSize = lpNames * entryNameSize;
            names = new RasEntryName[lpNames];
            names[0].dwSize = entryNameSize;
            uint retval = RasEnumEntries(null, null, names, ref lpSize, out lpNames);

            //if we have more than one connection, we need to do it again
            if (lpNames > 1)
            {
                names = new RasEntryName[lpNames];
                for (int i = 0; i < names.Length; i++)
                {
                    names[i].dwSize = entryNameSize;
                }
                retval = RasEnumEntries(null, null, names, ref lpSize, out lpNames);
            }

            if (lpNames > 0)
            {
                for (int i = 0; i < names.Length; i++)
                {
                    list.Add(names[i].szEntryName);
                }
            }
            return list;
        }
    }
}
