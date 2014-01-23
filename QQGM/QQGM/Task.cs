using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using ws.hoyland;

namespace QQGM
{
    class Task
    {
        private int idx;
        private WebClient client = null;
        private Random random = null;

        public Task()
        {
            client = new WebClient();
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
            string url = null;
            Stream data = null;
            StreamReader reader = null;
            StringBuilder pCodeResult = null;
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

                    int size = -1;

                    byte[] bytes = new byte[4096];
                    size = data.Read(bytes, 0, bytes.Length);

                    Console.WriteLine("SIZE:"+size);
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
                    pCodeResult = new StringBuilder("0000000"); // 分配30个字节存放识别结果
                              

                    // 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
                    nCodeType = 1004;

                    // 返回验证码ID，大于零为识别成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                    nCaptchaId = YDMWrapper.YDM_DecodeByBytes(bytes, size, nCodeType, pCodeResult);

                    Console.WriteLine("R:"+pCodeResult.ToString());
                    //nCaptchaId.ToString();

                    //isrun = false;
                    idx++;
                    break;
                case 3:
                    url = "https://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code=" + pCodeResult.ToString();
                    data = client.OpenRead(url);
                    data.Close();
                    break;
                default:
                    break;
            }
        }
    }
}
