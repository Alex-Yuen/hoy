using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using ws.hoyland.util;
using System.IO;
using System.Text.RegularExpressions;
using System.Threading;
using System.Configuration;
using System.Net;
using System.Globalization;

namespace ws.hoyland.sszs
{
    public partial class MBForm : Form
    {
        private delegate void Delegate();
        private Delegate dlg;

        private bool runx = false;
        private DataTable table1 = new DataTable();
        private List<String> accounts = null;
        private String xpath = AppDomain.CurrentDomain.BaseDirectory;

        private StreamWriter[] output = new StreamWriter[2]; //成功，失败，未运行
        private String[] fns = new String[] { "设置成功", "设置失败"};

        public MBForm()
        {
            InitializeComponent();
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void 导入LToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();
            //dialog.
            dialog.Title = "导入需上密上保帐号";
            dialog.Filter = "所有文件(*.*)|*.*";

            if (dialog.ShowDialog() == DialogResult.OK)
            {
                string fn = dialog.FileName;
                if (fn != null)
                {
                    table1.Clear();
                    
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
                            string[] lns = Regex.Split(line, "----");
                            List<string> listArr = new List<string>();
                            listArr.Add(lns[0]);
                            listArr.Add(lns[1]);
                            listArr.Add(lns[2]);
                            listArr.Add("初始化");
                            //listArr.AddRange(lns);
                            //listArr.Insert(3, "初始化");
                            lns = listArr.ToArray();

                            accounts.Add(line);
                            //							if (lns.size() == 3) {
                            //								lns.add("0");
                            //								lns.add("初始化");
                            //								//line += "----0----初始化";
                            //							} else {
                            //								//line += "----初始化";
                            //								lns.add("初始化");
                            //							}

                            String[] items = lns.ToArray();

                            DataRow row = table1.NewRow();
                            //String[] dt = (String[])msg.getData();
                            for (int k = 0; k < items.Length; k++)
                            {
                                row[k] = items[k];
                            }
                            table1.Rows.Add(row);
                            dataGridView1.DataSource = table1;
                            dataGridView1.FirstDisplayedScrollingRowIndex = dataGridView1.Rows.Count - 1;
                        }
                        i++;
                    }

                    m_streamReader.Close();
                    m_streamReader.Dispose();
                    fs.Close();
                    fs.Dispose();


                    if (accounts.Count > 0)
                    {
                        //List<String> ls = (List<String>)msg.getData();
                        //label4.Text = ls[0];
                        dataGridView1.FirstDisplayedScrollingRowIndex = 0;
                    }

                    if (accounts != null && accounts.Count > 0)
                    {
                        button1.Enabled = true;
                    }
                    else
                    {
                        button1.Enabled = false;
                    }
                }
            }
        }

        public void log(int type, string info)
        {
            output[type].WriteLine(info);
            output[type].Flush();
        }

        private void MBForm_Load(object sender, EventArgs e)
        {
            table1.Columns.Add("ID", Type.GetType("System.String"));
            table1.Columns.Add("帐号", Type.GetType("System.String"));
            table1.Columns.Add("连接", Type.GetType("System.String"));
            table1.Columns.Add("状态", Type.GetType("System.String"));
            dataGridView1.DataSource = table1;

        }

        private void button1_Click(object sender, EventArgs e)
        {
            runx = !runx;

            if (runx)
            {
                String tm = DateTime.Now.ToString("yyyy年MM月dd日 hh时mm分ss秒", DateTimeFormatInfo.InvariantInfo);

                try
                {
                    for (int i = 0; i < fns.Length; i++)
                    {
                        output[i] = File.AppendText(xpath + fns[i] + "-" + tm + ".txt");
                    }
                }
                catch (Exception ex)
                {
                    throw ex;
                }

                button1.Text = "结束";
                ThreadPool.SetMinThreads(1, 0);
                ThreadPool.SetMaxThreads(3, 0);

                for (int i = 0; i < accounts.Count; i++)
                {
                    //Thread t = new Thread(new ThreadStart(() =>{

                    //    })
                    //);
                    MBTask task = new MBTask(this, accounts[i]);
                    ThreadPool.QueueUserWorkItem(new WaitCallback(task.run));//, task
                }
            }
            else
            {
                button1.Text = "开始";
                //shutdown;
                {
                    for (int i = 0; i < output.Length; i++)
                    {
                        if (output[i] != null)
                        {
                            output[i].Close();
                        }
                    }
                }
            }
        }

        public void info(int id, String message)
        {
            dlg = delegate()
            {
                table1.Rows[id - 1][3] = message;

                dataGridView1.FirstDisplayedScrollingRowIndex = id - 1;

            };
            this.BeginInvoke(dlg);
        }
    }


    class MBTask
    {
        private bool runx = true;
        private int idx = 0;
        private HttpClient client;
        private string url = null;
        //private HttpGet get = null;

        private Stream data = null;
        private StreamReader reader = null;
        // private HttpUriRequest request = null;
        private string content = null;
        private byte[] bs = null;
        private string resp = null;
        private MBForm form = null;
        private int id = -1;
        private String account = null;
        private String link = null;
        private Configuration cfa = null;
        private int fidx = -1;
        private int lidx = -1;
        private string[] tqs = new string[3];//问题
        private string[] tas = new string[3];//答案
        private string[] qtag = new string[3]{
                        "问题一", 
                        "问题二", 
                        "问题三"
                    };

        private string[] questions = new string[]{
            "您父亲的姓名是？",
            "您父亲的生日是？",
            "您父亲的职业是？",
            "您母亲的姓名是？",
            "您母亲的生日是？",
            "您母亲的职业是？",
            "您配偶的姓名是？",
            "您配偶的生日是？",
            "您配偶的职业是？",
            "您小学班主任的名字是？",
            "您初中班主任的名字是？",
            "您高中班主任的名字是？",
            "您的学号（或工号）是？",
            "您的出生地是？",
            "您的小学校名是？",
            "您最熟悉的童年好友名字是？",
            "您最熟悉的学校宿舍室友名字是？",
            "对您影响最大的人名字是？"
        };

        private StringBuilder sb = null;
        private string line;
        private string pwd = null;
        public static Random random = new Random();
        

        public MBTask(MBForm form, String line)
        {
            this.form = form;
            this.line = line;

            String[] ls = Regex.Split(line, "----");
            this.id = Int32.Parse(ls[0]);
            this.account = ls[1];
            this.link = ls[2];

            client = new HttpClient();
            client.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
        }

        public void run(Object stateInfo)
        {
            ConfigurationManager.RefreshSection("appSettings");
            while (runx)
            {
                process(idx);

                try
                {
                    if (data != null)
                    {
                        data.Close();
                    }
                    if (reader != null)
                    {
                        reader.Close();
                    }
                }
                catch (Exception e)
                {
                    throw e;
                }
            }
        }
        
        private void process(int index)
        {
            switch (idx)
            {
                case 0:
                    form.info(id, "开始设置");
                    try
                    {
                        url =
                                link;

                        data = client.OpenRead(url);
                        // System.err.println(sig);

                        idx++;
                    }
                    catch (Exception e)
                    {
                        runx = false;
                        throw e;
                    }
                    break;

                case 1:
                    form.info(id, "填写帐号");
                    try
                    {
                        url =
                                "https://aq.qq.com/cn2/appeal/appeal_check_assist_account?UserAccount="+account;

                        data = client.OpenRead(url);
                        // System.err.println(sig);

                        idx++;
                    }
                    catch (Exception e)
                    {
                        runx = false;
                        throw e;
                    }
                    break;
                case 2:
                    form.info(id, "重置");
                    try
                    {
                        url =
                                "https://aq.qq.com/cn2/appeal/appeal_reset_jump";

                        content = "uin=" + this.account;

                        client.Headers[HttpRequestHeader.ContentType] = "application/x-www-form-urlencoded";

                        bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                        //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                        //resp = Encoding.UTF8.GetString(bs);


                        // System.err.println(sig);

                        idx++;
                    }
                    catch (Exception e)
                    {
                        runx = false;
                        throw e;
                    }
                    break;
                case 3:
                    form.info(id, "设置密保");
                    try
                    {
                        url =
                                "https://aq.qq.com/cn2/appeal/appeal_reset_checkquestion";

                        string[] tobeuploadans = new string[3];
                        if ("False".Equals(cfa.AppSettings.Settings["DNA_F1"].Value))
                        {
                            tobeuploadans[0] = genAns();
                        }
                        else
                        {
                            tobeuploadans[0] = cfa.AppSettings.Settings["DNA_A1"].Value;
                        }

                        if ("False".Equals(cfa.AppSettings.Settings["DNA_F2"].Value))
                        {
                            tobeuploadans[1] = genAns();
                        }
                        else
                        {
                            tobeuploadans[1] = cfa.AppSettings.Settings["DNA_A2"].Value;
                        }

                        if ("False".Equals(cfa.AppSettings.Settings["DNA_F3"].Value))
                        {
                            tobeuploadans[2] = genAns();
                        }
                        else
                        {
                            tobeuploadans[2] = cfa.AppSettings.Settings["DNA_A3"].Value;
                        }

                        content = "dna_ques_1=" + (Int32.Parse(cfa.AppSettings.Settings["DNA_Q1"].Value) + 1) + "&dna_answer_1=" + tobeuploadans[0] + "&dna_ques_2=" + (Int32.Parse(cfa.AppSettings.Settings["DNA_Q2"].Value) + 1) + "&dna_answer_2=" + tobeuploadans[1] + "&dna_ques_3=" + (Int32.Parse(cfa.AppSettings.Settings["DNA_Q3"].Value) + 1) + "&dna_answer_3=" + tobeuploadans[2] + "&mb_flow_type=dna&mb_up_from=";
                        

                        //content = "dna_ques_1=1&dna_answer_1=zhang&dna_ques_2=4&dna_answer_2=huang&dna_ques_3=11&dna_answer_3=cai";

                        client.Headers[HttpRequestHeader.ContentType] = "application/x-www-form-urlencoded";

                        bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                        //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();

                        resp = Encoding.UTF8.GetString(bs);

                        for (int i = 0; i < tqs.Length; i++)
                        {
                            fidx = resp.IndexOf(qtag[i]) + 39;
                            resp = resp.Substring(fidx);
                            lidx = resp.IndexOf("<");
                            tqs[i] = resp.Substring(0, lidx);//问题中文
                            if (tqs[i].Equals("您初中班主任的名字？"))
                            {
                                tqs[i] = "您初中班主任的名字是？";
                            }
                            for (int m = 0; m < 3; m++)
                            {
                                if (tqs[i].Equals(questions[(Int32.Parse(cfa.AppSettings.Settings["DNA_Q" + (m + 1)].Value))]))
                                //Console.WriteLine(questions[(Int32.Parse(cfa.AppSettings.Settings["DNA_Q" + (m + 1)].Value))]);
                                //Console.WriteLine(tqs[i]);

                                //if(questions[(Int32.Parse(cfa.AppSettings.Settings["DNA_Q"+(m+1)].Value))].IndexOf(tqs[i])!=-1)
                                {
                                    tas[i] = cfa.AppSettings.Settings["DNA_A" + (m + 1)].Value;
                                    break;
                                }
                            }
                        }



                        // System.err.println(sig);

                        idx++;
                    }
                    catch (Exception e)
                    {
                        runx = false;
                        throw e;
                    }
                    break;
                case 4:
                    form.info(id, "确认问题和答案");
                    sb = new StringBuilder();
                    for (int i = 0; i < tas.Length; i++)
                    {
                        sb.Append("a" + (i + 1) + "=" + UrlEncode(tas[i]) + "&");
                    }
                    url = "https://aq.qq.com/cn2/appeal/appeal_reset_mbajax?" + sb.ToString();

                    data = client.OpenRead(url);
                    //{ret: [0, 0, 0, 0]}
                    data.Close();
                    break;
                case 5:
                    form.info(id, "设置密保手机");
                    url = "https://aq.qq.com/cn2/appeal/appeal_reset_mobile?";
                    sb = new StringBuilder();
                    for(int i=0;i<tas.Length;i++){
                        sb.Append("dna_answer_" + (i + 1) + "=" +  UrlEncode(tas[i]) + "&");
                    }

                    data = client.OpenRead(url+sb.ToString());
                    data.Close();
                    break;
                case 6:
                    form.info(id, "跳过，填写密码");
                    url = "https://aq.qq.com/cn2/appeal/appeal_reset_passwd";

                    content = "mb1="+UrlEncode("请填写您的常用手机")+"&mb2="+UrlEncode("请再次填写手机号码");
                    Console.WriteLine(content);
                    client.Headers[HttpRequestHeader.ContentType] = "application/x-www-form-urlencoded";

                    bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                    //resp = Encoding.UTF8.GetString(bs);

                    idx++;
                    break;
                case 7:
                    form.info(id, "分析密码");
                    pwd = GetPassWord();
                    url = "https://aq.qq.com/cn2/appeal/appeal_reset_check_psw?psw="+pwd+"&pwd2="+pwd;
                    //data = client.OpenRead(url);
                    Console.WriteLine("PWD:" + pwd);
                    data = client.OpenRead(url);
                    data.Close();

                    idx++;
                    break;
                case 8://第二次验证通过
                    form.info(id, "获取密码强度");
                    url = "https://aq.qq.com/cn2/ajax/get_psw_sgh?psw=" + pwd;
                    data = client.OpenRead(url);

                    data.Close();
                    idx++;
                    break;
                case 9:
                    form.info(id, "提交新密码");
                    url = "https://aq.qq.com/cn2/appeal/appeal_reset_end";

                    content = "psw=" + UrlEncode(pwd) + "&psw2=" + UrlEncode(pwd);
                    //client.UploadString(url, content);
                    bs = Encoding.GetEncoding("GB2312").GetBytes(client.UploadString(url, content));
                    resp = Encoding.UTF8.GetString(bs);
                    if (resp.IndexOf("资料设置成功") != -1)
                    {
                        form.log(0, account + "----" + pwd + "----" + tqs[0] + "----" + tas[0] + "----" + tqs[1] + "----" + tas[1] + "----" + tqs[2] + "----" + tas[2]);
                        form.info(id, "设置成功");
                        //form.stat(3);
                        //Console.WriteLine("");
                    }
                    else
                    {                        ;
                        form.log(1, line.Substring(line.IndexOf("----") + 4));
                        form.info(id, "设置失败");
                    }
                    runx = false;//结束
                    
                    break;
                default:
                    break;
            }
        }

        private string genAns()
        {
            StringBuilder sb = new StringBuilder();
            int area, code;//汉字由区位和码位组成(都为0-94,其中区位16-55为一级汉字区,56-87为二级汉字区,1-9为特殊字符区)
            string chara;
            Random rand = new Random();
            for (int i = 0; i < 3; i++)
            {
                area = rand.Next(16, 88);
                if (area == 55)//第55区只有89个字符
                {
                    code = rand.Next(1, 90);
                }
                else
                {
                    code = rand.Next(1, 94);
                }
                chara = Encoding.GetEncoding("GB2312").GetString(new byte[] { Convert.ToByte(area + 160), Convert.ToByte(code + 160) });
                sb.Append(chara);
            }
            return sb.ToString();
        }

        private string UrlEncode(string strCode)
        {
            StringBuilder sb = new StringBuilder();
            byte[] byStr = System.Text.Encoding.UTF8.GetBytes(strCode); //默认是System.Text.Encoding.Default.GetBytes(str)  
            System.Text.RegularExpressions.Regex regKey = new System.Text.RegularExpressions.Regex("^[A-Za-z0-9]+$");
            for (int i = 0; i < byStr.Length; i++)
            {
                string strBy = Convert.ToChar(byStr[i]).ToString();
                if (regKey.IsMatch(strBy))
                {
                    //是字母或者数字则不进行转换    
                    sb.Append(strBy);
                }
                else
                {
                    sb.Append(@"%" + Convert.ToString(byStr[i], 16));
                }
            }
            return (sb.ToString());
        }

        private string GetPassWord()
        {
            ConfigurationManager.RefreshSection("appSettings");

            if ("True".Equals(cfa.AppSettings.Settings["FIX_PWD"].Value))
            {
                return cfa.AppSettings.Settings["FIX_PWD_VALUE"].Value;
            }
            else
            {
                int len = Int32.Parse(cfa.AppSettings.Settings["RND_PWD_LEN"].Value);
                StringBuilder sb = new StringBuilder();

                if ("True".Equals(cfa.AppSettings.Settings["RND_PWD_F1"].Value))
                {
                    sb.Append("0123456789");
                }

                if ("True".Equals(cfa.AppSettings.Settings["RND_PWD_F2"].Value))
                {
                    sb.Append("abcdefghijklmnopqrstuvwxyz");
                }

                if ("True".Equals(cfa.AppSettings.Settings["RND_PWD_F3"].Value))
                {
                    sb.Append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                    //sb.Append("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{}|~");
                }

                char[] ss = sb.ToString().ToCharArray();
                StringBuilder rr = new StringBuilder();

                for (int i = 0; i < len; i++)
                {
                    rr.Append(ss[random.Next(ss.Length)]);
                }
                return rr.ToString();
            }
        }
    }
}
