using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using ws.hoyland.util;
using System.Text.RegularExpressions;
using System.Globalization;
using System.Threading;
using System.Configuration;
using System.Net;
using System.Management;
using System.Security.Cryptography;
using ws.hoyland.sszs.Properties;

namespace ws.hoyland.sszs
{
    public partial class RSForm : Form
    {
        private delegate void Delegate();
        private Delegate dlg;

        private bool runx = false;
        private DataTable table1 = new DataTable();
        private List<String> accounts = null;
        private String xpath = AppDomain.CurrentDomain.BaseDirectory;

        private StreamWriter[] output = new StreamWriter[2]; //成功，失败，未运行
        private String[] fns = new String[] { "申诉结果", "申诉成功-IP" };

        private int suc = 0;
        private int total = 0;

        public RSForm()
        {
            InitializeComponent();
        }

        private void RSForm_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Escape)
            {
                this.Close();
            }
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void 导入LToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }

        private void RSForm_Load(object sender, EventArgs e)
        {
            table1.Columns.Add("ID", Type.GetType("System.String"));
            table1.Columns.Add("帐号", Type.GetType("System.String"));
            table1.Columns.Add("密码", Type.GetType("System.String"));
            table1.Columns.Add("回执编号", Type.GetType("System.String"));
            table1.Columns.Add("邮箱帐号", Type.GetType("System.String"));
            table1.Columns.Add("邮箱密码", Type.GetType("System.String"));
            table1.Columns.Add("成功", Type.GetType("System.String"));
            table1.Columns.Add("失败", Type.GetType("System.String"));
            dataGridView1.DataSource = table1;
        }

        public void log(int type, string info)
        {
            output[type].WriteLine(info);
            output[type].Flush();
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

        private void 导入LToolStripMenuItem_Click_1(object sender, EventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();
            //dialog.
            dialog.Title = "导入邮件列表";
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
                            listArr.AddRange(lns);
                            listArr.RemoveAt(lns.Length - 1);
                            listArr.Add("0");
                            listArr.Add("0");
                            //listArr.Add(lns[0]);
                            //listArr.Add(lns[1]);
                            //listArr.Add(lns[2]);
                            //listArr.Add("初始化");
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
                        total = accounts.Count;
                        Stat();
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
                    RSTask task = new RSTask(this, accounts[i]);
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

        public void SetColor(int id, Color color)
        {
            dlg = delegate()
            {
                dataGridView1.Rows[id].DefaultCellStyle.BackColor = color;
            };
            this.BeginInvoke(dlg);
        }


        public void succ(int id)
        {
            dlg = delegate()
            {
                table1.Rows[id - 1][6] = Int32.Parse(table1.Rows[id - 1][6].ToString()) + 1;
                dataGridView1.FirstDisplayedScrollingRowIndex = id - 1;
            };
            this.BeginInvoke(dlg);
        }

        public void fail(int id)
        {
            dlg = delegate()
            {
                table1.Rows[id - 1][7] = Int32.Parse(table1.Rows[id - 1][7].ToString()) + 1;
                dataGridView1.FirstDisplayedScrollingRowIndex = id - 1;
            };
            this.BeginInvoke(dlg);
        }

        public void inc()
        {
            lock (this)
            {
                suc++;
            }
        }

        public void Stat()
        {
            dlg = delegate()
            {
                label1.Text = suc + " / " + total + " = " + ((double)(100 * suc) / (double)total).ToString("F2") + "%";
            };
            this.BeginInvoke(dlg);
        }
    }

    class RSTask
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
        private RSForm form = null;
        private int id = -1;
        private Configuration cfa = null;
        private int fidx = -1;
        private int lidx = -1;
        
        private string original = null;
        private string line;
        public static Random random = new Random();
        private string[] ms = null;
        private bool uploadvcode = false;
        private string ts = null;
        private string vurl = null;
        private string vurlx = null;
        private string vuin = null;
        private string vid = null;
        private string result = null;
        private byte[] bytes = null;
        private int size = -1;

        private EngineMessage message = null;
        private StringBuilder pCodeResult = null;
        private int nCaptchaId = -1;
        private string mlist = null;

        private int sc = 0;
        private int fc = 0;

        public RSTask(RSForm form, String original)
        {
            this.form = form;
            this.original = original;

            ms = Regex.Split(original, "----");
            this.id = Int32.Parse(ms[0]);
            //this.account = ms[1];
            //this.link = ms[2];

            client = new HttpClient();

            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
        }

        public void run(Object stateInfo)
        {
            ConfigurationManager.RefreshSection("appSettings");
            try
            {
                form.SetColor(id, Color.Blue);

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
                    catch (Exception)
                    {
                        //throw e;
                    }
                }

                form.Stat();
                form.SetColor(id, Color.Black);
            }
            catch (Exception)
            {
                form.SetColor(id, Color.Red);
            }
        }


        private void process(int index)
        {
            client.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            switch (idx)
            {
                case 0: // 收邮件 
                    try
                    {
                        if (!uploadvcode)
                        {
                            form.info(id, "打开邮箱");
                            url =
                                    "https://w.mail.qq.com/";
                        }
                        else
                        {
                            form.info(id, "提交验证码");
                        }

                        data = client.OpenRead(url);
                        reader = new StreamReader(data);
                        line = reader.ReadToEnd();

                        if (line.IndexOf("name=\"ts\"") != -1)
                        {
                            ts = line.Substring(line.IndexOf("name=\"ts\"") - 12, 10);
                        }
                        else
                        {
                            form.info(id, "无法打开邮箱，任务结束");
                            runx = false;
                        }
                        idx++;
                    }
                    catch (Exception)
                    {
                        form.info(id, "无法打开邮箱，任务结束");
                        runx = false;
                        //throw e;
                    }
                    break;
                case 1:
                    form.info(id, "登录邮箱");
                    try
                    {
                        if (uploadvcode)
                        {
                            content = "device=&ts=" + ts + "&p=&f=xhtml&delegate_url=&action=&https=true&tfcont=22%2520serialization%3A%3Aarchive%25205%25200%25200%252010%25200%25200%25200%25208%2520authtype%25201%25208%25209%2520clientuin%25209%2520" + ms[4] + "%25209%2520aliastype%25207%2520%40qq.com%25206%2520domain%25206%2520qq.com%25202%2520ts%252010%25201392345223%25201%2520f%25205%2520xhtml%25205%2520https%25204%2520true%25203%2520uin%25209%2520" + ms[4] + "%25203%2520mss%25201%25201%25207%2520btlogin%25206%2520%2520%E7%99%BB%E5%BD%95%2520&verifycode=" + result + "&vid=" + vid + "&vuin=" + vuin + "&vurl=" + Util.UrlEncode(vurlx).Replace("%2e", ".") + "&mss=1&btlogin=+%E7%99%BB%E5%BD%95+";
                            uploadvcode = false;
                        }
                        else
                        {
                            string ecp = Convert.ToBase64String(Util.hexStringToByte(getECP()));
                            content = "device=&ts=" + ts + "&p=" + Util.UrlEncode(ecp) + "&f=xhtml&delegate_url=&action=&https=true&tfcont=&uin=" + ms[4] + "&aliastype=%40qq.com&pwd=&mss=1&btlogin=+%E7%99%BB%E5%BD%95+";
                        }

                        url = "https://w.mail.qq.com/cgi-bin/login?sid=";

                        bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                        //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                        line = Encoding.UTF8.GetString(bs);

                        if (line.IndexOf("errtype=1") != -1)
                        {
                            form.info(id, "邮箱密码错误");
                            runx = false;
                        }
                        else if (line.IndexOf("autoactivation") != -1)
                        {
                            form.info(id, "未开通邮箱");
                            runx = false;
                        }
                        else if (line.IndexOf("errtype=8") != -1)
                        {
                            form.info(id, "暂时无法登录邮箱");
                            runx = false;
                        }
                        else if (line.IndexOf("errtype=3") != -1)
                        {
                            form.info(id, "需要验证码");
                            line = line.Substring(line.IndexOf("url=https:") + 4);
                            line = line.Substring(0, line.IndexOf("\"/>"));
                            url = line;

                            vurl = line.Substring(line.IndexOf("vurl=") + 5);
                            vurl = vurl.Substring(0, vurl.IndexOf("&vid"));
                            vurlx = vurl;
                            vid = vurl.Substring(20, 32);
                            vuin = url.Substring(url.IndexOf("vuin=") + 5);
                            vuin = vuin.Substring(0, vuin.IndexOf("&"));

                            vurl = vurl.EndsWith("gif") ? vurl : vurl + ".gif";

                            Console.WriteLine(url);
                            Console.WriteLine(vurl);
                            Console.WriteLine(vid);
                            Console.WriteLine(vuin);
                            idx++;
                        }
                        else if (line.IndexOf("errtype=") == -1 && (line.IndexOf("today") != -1))//|| line.IndexOf("mobile") != -1
                        {
                            form.info(id, "登录成功");
                            line = line.Substring(line.IndexOf("url=http") + 4);
                            line = line.Substring(0, line.IndexOf("\"/>"));
                            url = line;
                            Console.WriteLine(url);
                            idx = 14;//TODO
                        }
                        else // 验证码错误，报告验证码错误
                        {
                            form.info(id, "登录邮箱, 未知错误");
                            runx = false;
                        }
                    }
                    catch (Exception)
                    {
                        form.info(id, "无法打开邮箱，任务结束");
                        runx = false;
                        ////throw e;
                    }
                    break;

                case 2:
                    form.info(id, "请求验证码");
                    try
                    {
                        data = client.OpenRead(vurl);
                        reader = new StreamReader(data);

                        bytes = new byte[4096];
                        size = data.Read(bytes, 0, bytes.Length);

                        MemoryStream ms = new MemoryStream();
                        ms.Write(bytes, 0, size);

                        message = new EngineMessage();
                        message.setType(EngineMessageType.IM_IMAGE_DATA);
                        message.setData(ms);

                        Engine.getInstance().fire(message);

                        idx++;
                    }
                    catch (Exception)
                    {
                        runx = false;
                        //throw e;
                    }
                    break;
                case 3:
                    // 根据情况，阻塞或者提交验证码到UU
                    form.info(id, "正在识别验证码");
                    try
                    {
                        int nCodeType;
                        pCodeResult = new StringBuilder("0000000000"); // 分配30个字节存放识别结果


                        // 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
                        nCodeType = 1004;

                        // 返回验证码ID，大于零为识别成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                        if (Engine.getInstance().getCptType() == 0)
                        {
                            nCaptchaId = YDMWrapper.YDM_DecodeByBytes(bytes, size, nCodeType, pCodeResult);
                        }
                        else
                        {
                            nCaptchaId = UUWrapper.uu_recognizeByCodeTypeAndBytes(bytes, size, nCodeType, pCodeResult);
                        }

                        result = pCodeResult.ToString();


                        //result = rsb.toString();
                        //System.out.println("---"+result);

                        idx = 9;
                        uploadvcode = true;
                    }
                    catch (Exception)
                    {
                        runx = false;
                        //throw e;
                    }
                    break;
                case 4:
                    form.info(id, "验证码错误，报告异常");
                    try
                    {
                        //
                        int reportErrorResult = -1;
                        if (Engine.getInstance().getCptType() == 0)
                        {
                            reportErrorResult = YDMWrapper.YDM_Report(nCaptchaId, false);
                        }
                        else
                        {
                            reportErrorResult = UUWrapper.uu_reportError(nCaptchaId);
                        }

                        //idx = 0; // 重新开始
                        idx = 9;//重新开始接收邮件
                    }
                    catch (Exception)
                    {
                        runx = false;
                        //throw e;
                    }
                    break;
                case 5:
                    form.info(id, "获取邮件列表地址");
                    try
                    {
                        data = client.OpenRead(url);
                        reader = new StreamReader(data);
                        line = reader.ReadToEnd();

                        line = line.Substring(line.IndexOf("mail_list") - 9);
                        url = line.Substring(0, line.IndexOf("\">"));
                        mlist = url;
                        idx++;
                    }
                    catch (Exception)
                    {
                        runx = false;
                        //throw e;
                    }
                    break;
                case 6:
                    form.info(id, "读取邮件列表");
                    try
                    {
                        data = client.OpenRead("http://w.mail.qq.com" + mlist);
                        reader = new StreamReader(data);
                        line = reader.ReadToEnd();

                        int formidx = -1;
                        int qqidx = -1;
                        if ((formidx = line.IndexOf("<form")) != -1 && (qqidx = line.IndexOf("申诉结果")) != -1 && line.Substring(formidx, qqidx).IndexOf("mui_font_bold") != -1)
                        {
                            //Console.WriteLine(line.Substring(formidx, qqidx - formidx));
                            line = line.Substring(formidx, qqidx - formidx);
                            line = line.Substring(line.LastIndexOf("/cgi-bin/readmail?"));
                            url = line.Substring(0, line.IndexOf("\">"));

                            form.info(id, " 读取邮件内容");
                            client.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                            data = client.OpenRead("http://w.mail.qq.com" + url);
                            reader = new StreamReader(data);
                            line = reader.ReadToEnd();

                            string rcl = line.Substring(line.IndexOf("回执编号[") + 5, 10);
                            if (ms[3].Equals(rcl))
                            {
                                if (line.IndexOf("申诉成功") != -1)
                                {
                                    form.inc();
                                    sc++;

                                    form.succ(id);

                                    fidx = line.IndexOf("<a style=\"color:red\" href=\"") + 27;
                                    lidx = line.IndexOf("\" target=\"_blank\"><span>点此重新设置密码");
                                    String link = line.Substring(fidx, lidx - fidx);
                                    //System.err.println(rcl);
                                    //System.err.println(link);
                                    // 写文件
                                    form.log(0, ms[1] + "----" + link);
                                    form.log(1, ms[1] + "----" + ms[6]);

                                }
                                else if (line.IndexOf("申诉未能通过审核") != -1)
                                {
                                    fc++;
                                    form.fail(id);
                                }
                            }

                            //line = line.Substring(line.IndexOf("mail_list") - 9);
                            //url = line.Substring(0, line.IndexOf("\">"));

                        }
                        runx = false;
                        //
                    }
                    catch (Exception)
                    {
                        runx = false;

                        //System.err.print("读取工具 : 收取邮件出错，新建任务运行.[");
                        //for (int i = 0; i < msx.length; i++)
                        //{
                        //    System.err.print(msx[i] + "----");
                        //}
                        ///System.err.println();
                        RSTask task = new RSTask(form, this.original);
                        ThreadPool.QueueUserWorkItem(new WaitCallback(task.run));
                        //throw e;
                    }
                    break;
                default:
                    break;
            }
        }

        private string getECP()
        {
            string ecp = null;
            try
            {
                Crypter crypt = new Crypter();
                ManagementObject disk = new ManagementObject("win32_logicaldisk.deviceid=\"c:\"");
                disk.Get();
                byte[] mc = Util.UMD5(disk.GetPropertyValue("VolumeSerialNumber").ToString() + "SSZS");

                url = "http://www.y3y4qq.com/gc";
                byte[] key = Util.getKey();
                string content = Util.byteArrayToHexString(key).ToUpper() + Util.byteArrayToHexString(crypt.QQ_Encrypt(mc, key)).ToUpper();
                Console.WriteLine(content);
                string ct = "password=" + ms[5] + "&ts=" + this.ts;
                Console.WriteLine(ct);
                RSACryptoServiceProvider provider = new RSACryptoServiceProvider();

                provider.FromXmlString(Resources.pbkey);

                bs = Encoding.UTF8.GetBytes(ct);
                //Console.WriteLine("LEN:"+bs.Length);
                byte[] ciphertext = provider.Encrypt(bs, false);

                /**
                StringBuilder sb = new StringBuilder();
                foreach (byte b in ciphertext)
                {
                    sb.AppendFormat("{0:X2}", b);
                }
                Console.WriteLine(sb.ToString());
                **/

                content += Util.byteArrayToHexString(ciphertext).ToUpper();
                Console.WriteLine(content);

                //Console.WriteLine(byteArrayToHexString(key).ToUpper());
                //Console.WriteLine(content);
                //client.UploadString(url, content);
                //client.UploadString(url, 
                //client.Encoding = Encoding.UTF8;
                client.Headers[HttpRequestHeader.ContentType] = "text/plain; charset=UTF-8";

                //client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();

                //bs = crypt.QQ_Decrypt(bs, key);
                ecp = Encoding.UTF8.GetString(bs);
                Console.WriteLine("R:" + ecp);
                //bs = crypt.QQ_Decrypt(hexStringToByte(resp), key);
                //expire = Int32.Parse(Encoding.UTF8.GetString(bs));
                //Console.WriteLine("2:" + expire);
            }
            catch (Exception ex)
            {
                ecp = null;
                Console.WriteLine(ex.Message);
                //MessageBox.Show(ex.Message);
            }
            return ecp;
        }

        private string genAns()
        {
            StringBuilder sb = new StringBuilder();
            int area, code;//汉字由区位和码位组成(都为0-94,其中区位16-55为一级汉字区,56-87为二级汉字区,1-9为特殊字符区)
            string chara;
            for (int i = 0; i < 3; i++)
            {
                area = random.Next(16, 88);
                if (area == 55)//第55区只有89个字符
                {
                    code = random.Next(1, 90);
                }
                else
                {
                    code = random.Next(1, 94);
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
