using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using ws.hoyland;
using System.Text.RegularExpressions;
using System.Web;

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

        public Task()
        {
            client = new HttpClient();
            client.Headers.Add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            random = new Random();
            isrun = true;
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

        public void process(int type)
        {            ;
            switch (type)
            {
                case 0://改密，自动判断是否有保
                    if (isdna)//有保
                    {
                        while (isrun)
                        {
                            gm1();
                        }
                    }
                    else//无保
                    {
                        gm0();
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
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_input_account?pw_type=0&aquin=";
                    data = client.OpenRead(url);
                    data.Close();

                    idx++;
                    break;
                case 2:
                    url = "https://ssl.captcha.qq.com/getimage?aid=2001601&" + random.NextDouble() + "&uin=" + account;
                    Console.WriteLine(url);
                    data = client.OpenRead(url);

                    bytes = new byte[4096];
                    size = data.Read(bytes, 0, bytes.Length);

                    Console.WriteLine("SIZE:"+size);
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
                    url = "https://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code=" + pCodeResult.ToString();
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;
                    break;
                case 5:
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_way";
                    content = "input_find_qq=" + account + "&pw_type=1&verifycode=" + pCodeResult.ToString();
                    client.UploadString(url, content);
                    idx++;
                    break;
                case 6:
                    url = "https://aq.qq.com/cn2/ajax/page_optlog?page_name=pc_find_pwd_way&element_name="+account;
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;
                    break;
                case 7:
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
                    url = unionverify;
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;                   
                    break;
                case 10:
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_result";
                    content = "psw=qwer1234&psw_ack=qwer1234&method=1&sub_method=0";
                    client.UploadString(url, content);
                    //bs = Encoding.GetEncoding("GB2312").GetBytes(client.UploadString(url, content));
                    //Console.WriteLine(Encoding.UTF8.GetString(bs));
                    idx++;
                    isrun = false;
                    break;
                default:
                    break;
            }
        }

        
        public static string UrlEncode(string strCode)
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
    }
}
