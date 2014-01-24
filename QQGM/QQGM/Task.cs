using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using ws.hoyland;
using System.Text.RegularExpressions;
using System.Web;
using Newtonsoft.Json;
using System.Configuration;

namespace QQGM
{
    class Task
    {
        private int idx;
        private HttpClient client = null;
        private Random random = null;

        private string url = null;
        private Stream data = null;
        private StreamReader reader = null;
        private StringBuilder pCodeResult = null;
        private int size = -1;
        private byte[] bytes = new byte[4096];
        private string content = null;
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

        private string[] qs = null;
        private string[] ans = null;
        private string resp = null;
        private byte[] bs = null;
        private string unionverify = null;
        private Form1 form;
        private Configuration cfa = null;

        public Task()
        {
            client = new HttpClient();
            client.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            random = new Random();
            isrun = true;

            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
        }

        public int IDX
        {
            get { return idx; }
            set { idx = value; }
        }

        private bool isrun;

        public bool Isrun
        {
            get { return isrun; }
            set { isrun = value; }
        }

        private int id;

        public int ID
        {
            get { return id; }
            set { id = value; }
        }

        private bool isdna;

        public bool Isdna
        {
            get { return isdna; }
            set { isdna = value; }
        }

        private string account;

        public string Account
        {
            get { return account; }
            set { account = value; }
        }
        private string password;

        public string Password
        {
            get { return password; }
            set { password = value; }
        }

        private string q1;

        public string Q1
        {
            get { return q1; }
            set { q1 = value; }
        }
        private string a1;

        public string A1
        {
            get { return a1; }
            set { a1 = value; }
        }

        private string q2;

        public string Q2
        {
            get { return q2; }
            set { q2 = value; }
        }
        private string a2;

        public string A2
        {
            get { return a2; }
            set { a2 = value; }
        }

        private string q3;

        public string Q3
        {
            get { return q3; }
            set { q3 = value; }
        }
        private string a3;

        public string A3
        {
            get { return a3; }
            set { a3 = value; }
        }

        public void process(Form1 form, int type)
        {
            this.form = form;
            switch (type)
            {
                case 1:
                    while (isrun)//无保
                    {
                        gm0();
                    }
                    break;
                case 2://改密，自动判断是否有保
                    if (isdna)//有保
                    {
                        while (isrun)
                        {
                            gm1();
                        }
                    }
                    break;
                default:
                    break;
            }
        }


        private void gm0()//无保改密
        {
            //TODO
        }

        private void gm1()//有保改密
        {
            switch (idx)
            {
                case 0:
                    form.info(id, "正在开始");
                    url = "https://aq.qq.com/cn2/findpsw/findpsw_index";
                    data = client.OpenRead(url);
                    //StreamReader reader = new StreamReader(data);
                    //string s = reader.ReadToEnd();
                    //Console.WriteLine(s);
                    data.Close();
                    //reader.Close();

                    idx++;
//                    isrun = false;
                    break;
                case 1:
                    form.info(id, "输入帐号");
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_input_account?pw_type=0&aquin=";
                    data = client.OpenRead(url);
                    data.Close();

                    idx++;
                    break;
                case 2:
                    form.info(id, "下载验证码");
                    url = "https://ssl.captcha.qq.com/getimage?aid=2001601&" + random.NextDouble() + "&uin=" + account;
                    //Console.WriteLine(url);
                    data = client.OpenRead(url);

                    bytes = new byte[4096];
                    size = data.Read(bytes, 0, bytes.Length);

                    this.form.setImage(bytes, size);
                    
                    //Console.WriteLine("SIZE:"+size);
                    idx++;
                    break;
                case 3:
                    //MemoryStream ms = new MemoryStream();

                    //reader = new StreamReader(data);
                    //char[] buffer = new char[1024];

                    //int size = -1;
                    //while((size=reader.ReadBlock(buffer, 0, buffer.Length))!=-1)
                    //{
                    //    ms.Write(buffer, 0, size);
                    //}
                    //string s = reader.ReadToEnd();
                    //Console.WriteLine(s);
                    form.info(id, "识别验证码");
                    int nCodeType, nCaptchaId;
                    pCodeResult = new StringBuilder("0000000000"); // 分配30个字节存放识别结果
                              

                    // 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
                    nCodeType = 1004;

                    // 返回验证码ID，大于零为识别成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                    nCaptchaId = YDMWrapper.YDM_DecodeByBytes(bytes, size, nCodeType, pCodeResult);

                    Console.WriteLine("R:"+pCodeResult.ToString());
                    //nCaptchaId.ToString();

                    //isrun = false;
                    idx++;
                    break;
                case 4:
                    form.info(id, "提交验证码");
                    url = "https://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code=" + pCodeResult.ToString();
                    data = client.OpenRead(url);

                    reader = new StreamReader(data);
                    string s = reader.ReadToEnd();
                    Console.WriteLine(s);
                    JsonTextReader jtr = new JsonTextReader(new StringReader(s));
                    jtr.Read();
                    jtr.Read();
                    jtr.Read();// && (jtr.TokenType!=JsonToken.StartObject)
                    {
                        if (jtr.Value.ToString().Equals("0"))
                        {
                            form.info(id, "验证码正确");
                            idx++;
                        }
                        else
                        {
                            form.info(id, "验证码错误");
                            idx = 2;
                        }
                        //Console.WriteLine(jtr.Value);
                    }
                    //.WriteLine(jtr.ReadAsString());
                    reader.Close();
                    data.Close();
                    break;
                case 5:
                    form.info(id, "选择方式");
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_way";
                    content = "input_find_qq=" + account + "&pw_type=1&verifycode=" + pCodeResult.ToString();
                    client.UploadString(url, content);
                    idx++;
                    break;
                case 6:
                    form.info(id, "提交页面操作记录");
                    url = "https://aq.qq.com/cn2/ajax/page_optlog?page_name=pc_find_pwd_way&element_name="+account;
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;
                    break;
                case 7:
                    form.info(id, "跳转");
                    url = "https://aq.qq.com/cn2/unionverify/unionverify_jump?jumpname=pc_find_pwd&session_context=3&PTime=" + random.NextDouble();
                    //Console.WriteLine(client.Headers);
                    data = client.OpenRead(url);

                    reader = new StreamReader(data);
                    resp = reader.ReadToEnd();
                    //QuerId:[4,1,7],
                    resp = resp.Substring(resp.IndexOf("QuerId"));
                    int fidx = resp.IndexOf("[") + 1;
                    int lidx = resp.IndexOf("]");

                    resp = resp.Substring(fidx, lidx - fidx);
                    qs = Regex.Split(resp, ",");
                    ans = new string[3];
                    string question = null;
                    for (int i = 0; i < qs.Length; i++)
                    {
                        question = questions[Int32.Parse(qs[i]) - 1];
                        if (Q1.Equals(question))
                        {
                            ans[i] = A1;
                        }
                        else if (Q2.Equals(question))
                        {
                            ans[i] = A2;
                        }
                        else if (Q3.Equals(question))
                        {
                            ans[i] = A3;
                        }
                    }

                    Console.WriteLine(resp);
                    
                    reader.Close();
                    data.Close();
                    idx++;

                    //isrun = false;
                    break;
                case 8:
                    form.info(id, "提交密保答案");
                    url = "https://aq.qq.com/cn2/unionverify/pc/pc_uv_verify";
                    content = "type=1&dnaAnswer1=" + ans[0] + "&dnaAnswer2=" + ans[1] + "&dnaAnswer3=" + ans[2] + "&order=0";//&dnaAnswerHex1=&dnaAnswerHex2=&dnaAnswerHex3=
                    Console.WriteLine(content);
                    //client.UploadString(url, content);
                    //client.UploadString(url, 
                    //client.Encoding = Encoding.UTF8;
                    client.Headers[HttpRequestHeader.ContentType] = "text/plain; charset=UTF-8";

                    //client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                    bs = client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                    //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();
                    resp = Encoding.UTF8.GetString(bs);

                    resp = resp.Substring(resp.IndexOf("window.location.href="));
                    fidx = resp.IndexOf("'") + 1;
                    lidx = resp.IndexOf(";");

                    resp = resp.Substring(fidx, lidx - fidx - 1);

                    //Console.WriteLine(Encoding.UTF8.GetString(bs));
                    unionverify = resp;
                    Console.WriteLine(resp);
                    //client.Headers.Remove(HttpRequestHeader.ContentType);
                    //isrun = false;
                    idx++;
                    break;
                case 9:
                    form.info(id, "成功，继续跳转");
                    url = unionverify;
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;            
                    break;
                case 10:
                    form.info(id, "提交新密码");
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_result";
                    string pwd = GetPassWord();
                    Console.WriteLine("PWD:" + pwd);
                    content = "psw="+pwd+"&psw_ack="+pwd+"&method=1&sub_method=0";
                    //client.UploadString(url, content);
                    bs = Encoding.GetEncoding("GB2312").GetBytes(client.UploadString(url, content));
                    resp = Encoding.UTF8.GetString(bs);
                    Console.WriteLine(resp.IndexOf("修改成功"));
                    if (resp.IndexOf("same_psw")!=-1)
                    {
                        form.info(id, "密码相同");
                        //Console.WriteLine("");
                    }
                    else if (resp.IndexOf("修改成功") != -1)
                    {
                        form.info(id, "修改成功");
                        //Console.WriteLine("修改成功");
                    }
                    //Console.WriteLine(resp);
                    idx++;
                    isrun = false;
                    break;
                default:
                    break;
            }
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
                
                if ("True".Equals(cfa.AppSettings.Settings["FIX_PWD_F1"].Value))
                {
                    sb.Append("0123456789");
                }

                if ("True".Equals(cfa.AppSettings.Settings["FIX_PWD_F2"].Value))
                {
                    sb.Append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
                }

                if ("True".Equals(cfa.AppSettings.Settings["FIX_PWD_F3"].Value))
                {
                    sb.Append("!\"#$%&'()*+,-./:;<=>?@[\\]^_`{}|~");
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
                
        /**
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
        } **/
    }
}
