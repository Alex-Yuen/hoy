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
        private int fidx = -1;
        private int lidx = -1;
        private string version = null;
        private string loginsig = null;
        private string line = null;
        private string vcode = null;
        private string salt = null;

        private int nCaptchaId = 0;
        private bool changepwd = false;
        private string pwd = null;

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
                    while (isrun)//直接改密
                    {
                        gm0();
                    }
                    break;
                case 2://有保改密，自动判断是否有保
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


        private void gm0()//直接改密
        {
            switch (idx)
            {
                case 0:
                    form.info(id, "正在登录");
                    url = "https://ui.ptlogin2.qq.com/cgi-bin/login?appid=2001601&no_verifyimg=1&f_url=loginerroralert&lang=0&target=top&hide_title_bar=1&s_url=http%3A//aq.qq.com/cn2/index&qlogin_jumpname=aqjump&qlogin_param=aqdest%3Dhttp%253A//aq.qq.com/cn2/index&css=https%3A//aq.qq.com/v2/css/login.css";
                    data = client.OpenRead(url);
                    reader = new StreamReader(data);
                    line = reader.ReadToEnd();
                    //Console.WriteLine(s);

                    loginsig = line.Substring(line.IndexOf("var g_login_sig=encodeURIComponent") + 36).Substring(0, 64);

                    data.Close();
                    reader.Close();

                    idx++;
                    //isrun = false;
                    break;
                case 1:
                    form.info(id, "获取版本号");
                    url = "https://ui.ptlogin2.qq.com/ptui_ver.js?v=" + random.NextDouble();
                    data = client.OpenRead(url);
                    reader = new StreamReader(data);
                    line = reader.ReadToEnd();

                    version = line.Substring(line.IndexOf("ptuiV(\"") + 7, 5);//line.IndexOf("\");")

                    //Console.WriteLine(s);
                    data.Close();
                    reader.Close();

                    idx++;
                    //isrun = false;
                    break;
                case 2:
                    form.info(id, "检查帐号");
                    url = "https://ssl.ptlogin2.qq.com/check?uin=" + account + "&appid=2001601&js_ver=" + version + "&js_type=0&login_sig=" + loginsig + "&u1=http%3A%2F%2Faq.qq.com%2Fcn2%2Findex&r=" + random.NextDouble();
                    data = client.OpenRead(url);
                    reader = new StreamReader(data);
                    line = reader.ReadToEnd();

                    bool nvc = line.ToCharArray()[14] == '1' ? true : false;
                    //没有做RSAKEY检查，默认是应该有KEY，用getEncryption；否则用getRSAEncryption

                    fidx = line.IndexOf(",");
                    lidx = line.LastIndexOf(",");

                    vcode = line.Substring(fidx + 2, 4);
                    salt = line.Substring(lidx + 2, 32);

                    if (nvc)
                    {
                        //Encryption.getRSAEncryption(K, G)
                        idx++; //进入下一步验证码
                    }
                    else
                    {
                        idx += 5;
                    }
                    //Console.WriteLine(s);
                    data.Close();
                    reader.Close();

                    //isrun = false;
                    break;
                case 3:
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
                case 4:
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
                    int nCodeType;
                    pCodeResult = new StringBuilder("0000000000"); // 分配30个字节存放识别结果


                    // 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
                    nCodeType = 1004;

                    // 返回验证码ID，大于零为识别成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                    if (form.getCptType() == 0)
                    {
                        nCaptchaId = YDMWrapper.YDM_DecodeByBytes(bytes, size, nCodeType, pCodeResult);
                    }
                    else
                    {
                        nCaptchaId = UUWrapper.uu_recognizeByCodeTypeAndBytes(bytes, size, nCodeType, pCodeResult);
                    }

                    Console.WriteLine("R:" + pCodeResult.ToString());
                    //nCaptchaId.ToString();

                    //isrun = false;
                    idx++;
                    break;
                case 5:
                    form.info(id, "提交验证码");
                    if (!changepwd)
                    {
                        url = "https://aq.qq.com/cn2/ajax/check_verifycode?session_type=on_rand&verify_code=" + pCodeResult.ToString();

                    }
                    else
                    {
                        url = "https://aq.qq.com/cn2/ajax/check_verifycode?verify_code=" + pCodeResult.ToString();
                    }
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
                            vcode = pCodeResult.ToString();
                            if (!changepwd)
                            {
                                idx += 2;
                            }
                            else//第二次
                            {
                                idx = 12;
                            }
                        }
                        else
                        {
                            form.info(id, "验证码错误");
                            id++;
                        }
                        //Console.WriteLine(jtr.Value);
                    }
                    //.WriteLine(jtr.ReadAsString());
                    reader.Close();
                    data.Close();
                    break;
                case 6:
                    form.info(id, "报告验证码错误");
                    int reportErrorResult = -1;
                    if (form.getCptType() == 0)
                    {
                        reportErrorResult = YDMWrapper.YDM_Report(nCaptchaId, false);
                    }
                    else
                    {
                        reportErrorResult = UUWrapper.uu_reportError(nCaptchaId);
                    }
                    Console.WriteLine("REPORT:"+reportErrorResult);
                    idx = 3;
                    break;
                case 7:
                    form.info(id, "提交登录请求");
                    //计算ECP
                    System.Security.Cryptography.MD5CryptoServiceProvider md5CSP = new System.Security.Cryptography.MD5CryptoServiceProvider();

                    byte[] results = md5CSP.ComputeHash(Encoding.UTF8.GetBytes(this.password));

                    int psz = results.Length;
                    byte[] rs = new byte[psz + 8];
                    for (int i = 0; i < psz; i++)
                    {
                        rs[i] = results[i];
                    }

                    string[] salts = Regex.Split(this.salt.Substring(2), "\\\\x");

                    //string[] salts = this.salt.Substring(2).split("\\\\x");
                    //System.out.println(salts.length);
                    for (int i = 0; i < salts.Length; i++)
                    {
                        rs[psz + i] = (byte)Int32.Parse(salts[i], System.Globalization.NumberStyles.HexNumber);
                    }

                    results = md5CSP.ComputeHash(rs);
                    string resultString = byteArrayToHexString(results).ToUpper();

                    //vcode = "!RQM";
                    results = md5CSP.ComputeHash(Encoding.UTF8.GetBytes(resultString + vcode.ToUpper()));
                    resultString = byteArrayToHexString(results).ToUpper();
                    //System.out.println(resultString);
                    string ecp = resultString;

                    url = "https://ssl.ptlogin2.qq.com/login?u=" + account + "&p=" + ecp + "&verifycode=" + vcode + "&aid=2001601&u1=http%3A%2F%2Faq.qq.com%2Fcn2%2Findex&h=1&ptredirect=1&ptlang=2052&from_ui=1&dumy=&fp=loginerroralert&action=4-14-" + currentTimeMillis() + "&mibao_css=&t=1&g=1&js_type=0&js_ver=" + version + "&login_sig=" + loginsig;
                    data = client.OpenRead(url);

                    idx++;
                    break;
                case 8:
                    form.info(id, "打开修改密码页面");
                    url = "http://aq.qq.com/cn2/change_psw/change_psw_index";
                    data = client.OpenRead(url);
                    //reader = new StreamReader(data);
                    //line = reader.ReadToEnd();
                    //Console.WriteLine(s);
                    
                    data.Close();
                    reader.Close();

                    idx++;
                    //isrun = false;
                    break;
                case 9:
                    form.info(id, "跳转");
                    url = "https://aq.qq.com/cn2/change_psw/pc/pc_change_pwd_way";
                    data = client.OpenRead(url);
                    //reader = new StreamReader(data);
                    //line = reader.ReadToEnd();
                    //Console.WriteLine(s);
                    
                    data.Close();
                    reader.Close();

                    idx++;
                    //isrun = false;
                    break;
                     
                case 10:
                    form.info(id, "提交页面操作记录");
                    url = "https://aq.qq.com/cn2/ajax/page_optlog?logid=180&page_name=change_pwd&element_name=input_old_pwd_page_count&uin=" + account;
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;
                    break;
                case 11:
                    form.info(id, "分析密码");
                    url = "https://aq.qq.com/cn2/change_psw/pc/pc_change_pwd_analysis_psw_ajax";
                    //data = client.OpenRead(url);
                    pwd = GetPassWord();
                    Console.WriteLine("PWD:" + pwd);
                    content = "new_psw="+pwd;
                    client.UploadString(url, content);

                    data.Close();
                    changepwd = true;
                    idx = 3;//下载第二次验证码
                    break;
                case 12://第二次验证通过
                    form.info(id, "获取密码强度");
                    url = "https://aq.qq.com/cn2/ajax/get_psw_sgh?psw="+pwd;
                    data = client.OpenRead(url);
                    
                    data.Close();
                    changepwd = true;
                    idx++;
                    break;
                case 13:
                    form.info(id, "提交新密码");
                    url = "https://aq.qq.com/cn2/change_psw/pc/pc_change_pwd_result";
                    
                    
                    content = "psw_old="+password+"&psw="+pwd+"&psw_ack="+pwd+"&verifycode="+vcode+"&method=2&sub_method=0";
                    //client.UploadString(url, content);
                    bs = Encoding.GetEncoding("GB2312").GetBytes(client.UploadString(url, content));
                    resp = Encoding.UTF8.GetString(bs);
                    //Console.WriteLine(resp);
                    //Console.WriteLine(resp.IndexOf("修改成功"));
                    if (resp.IndexOf("same_psw") != -1)
                    {
                        form.info(id, "密码相同");
                        //Console.WriteLine("");
                    }
                    else if (resp.IndexOf("修改成功") != -1)
                    {
                        form.info(id, "修改成功");
                        //Console.WriteLine("修改成功");
                    }
                    else if (resp.IndexOf("操作非法或者超时") != -1)
                    {
                        form.info(id, "操作非法或者超时");
                    }
                    //Console.WriteLine(resp);
                    idx++;
                    isrun = false;
                    break;
                default:
                    break;
            }
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
                    int nCodeType;
                    pCodeResult = new StringBuilder("0000000000"); // 分配30个字节存放识别结果


                    // 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型 http://www.yundama.com/price.html
                    nCodeType = 1004;

                    // 返回验证码ID，大于零为识别成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                    if (form.getCptType() == 0)
                    {
                        nCaptchaId = YDMWrapper.YDM_DecodeByBytes(bytes, size, nCodeType, pCodeResult);
                    }
                    else
                    {
                        nCaptchaId = UUWrapper.uu_recognizeByCodeTypeAndBytes(bytes, size, nCodeType, pCodeResult);
                    }

                    Console.WriteLine("R:" + pCodeResult.ToString());
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
                            idx += 2;
                        }
                        else
                        {
                            form.info(id, "验证码错误");
                            idx++;
                        }
                        //Console.WriteLine(jtr.Value);
                    }
                    //.WriteLine(jtr.ReadAsString());
                    reader.Close();
                    data.Close();
                    break;
                case 5:
                    form.info(id, "报告验证码错误");
                    int reportErrorResult = -1;
                    if (form.getCptType() == 0)
                    {
                        reportErrorResult = YDMWrapper.YDM_Report(nCaptchaId, false);
                    }
                    else
                    {
                        reportErrorResult = UUWrapper.uu_reportError(nCaptchaId);
                    }
                    Console.WriteLine("REPORT:" + reportErrorResult);
                    idx = 2;
                    break;
                case 6:
                    form.info(id, "选择方式");
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_way";
                    content = "input_find_qq=" + account + "&pw_type=1&verifycode=" + pCodeResult.ToString();
                    client.UploadString(url, content);
                    idx++;
                    break;
                case 7:
                    form.info(id, "提交页面操作记录");
                    url = "https://aq.qq.com/cn2/ajax/page_optlog?page_name=pc_find_pwd_way&element_name=" + account;
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;
                    break;
                case 8:
                    form.info(id, "跳转");
                    url = "https://aq.qq.com/cn2/unionverify/unionverify_jump?jumpname=pc_find_pwd&session_context=3&PTime=" + random.NextDouble();
                    //Console.WriteLine(client.Headers);
                    data = client.OpenRead(url);

                    reader = new StreamReader(data);
                    resp = reader.ReadToEnd();
                    //QuerId:[4,1,7],
                    if (resp.IndexOf("QuerId") == -1)
                    {
                        form.info(id, "需要短信验证");
                        isrun = false;
                    }
                    else
                    {
                        resp = resp.Substring(resp.IndexOf("QuerId"));
                        fidx = resp.IndexOf("[") + 1;
                        lidx = resp.IndexOf("]");

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
                    }
                    reader.Close();
                    data.Close();

                    idx++;
                    //isrun = false;
                    break;
                case 9:
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
                case 10:
                    form.info(id, "成功，继续跳转");
                    url = unionverify;
                    data = client.OpenRead(url);
                    data.Close();
                    idx++;
                    break;
                case 11:
                    form.info(id, "提交新密码");
                    url = "https://aq.qq.com/cn2/findpsw/pc/pc_find_pwd_result";
                    string pwd = GetPassWord();
                    Console.WriteLine("PWD:" + pwd);
                    content = "psw=" + pwd + "&psw_ack=" + pwd + "&method=1&sub_method=0";
                    //client.UploadString(url, content);
                    bs = Encoding.GetEncoding("GB2312").GetBytes(client.UploadString(url, content));
                    resp = Encoding.UTF8.GetString(bs);
                    //Console.WriteLine(resp.IndexOf("修改成功"));
                    if (resp.IndexOf("same_psw") != -1)
                    {
                        form.info(id, "密码相同");
                        //Console.WriteLine("");
                    }
                    else if (resp.IndexOf("修改成功") != -1)
                    {
                        form.info(id, "修改成功");
                        //Console.WriteLine("修改成功");
                    }
                    else if (resp.IndexOf("操作非法或者超时") != -1)
                    {
                        form.info(id, "操作非法或者超时");
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

        private long currentTimeMillis()
        {
            DateTime Jan1st1970 = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            return (long)((DateTime.UtcNow - Jan1st1970).TotalMilliseconds);
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
    }
}
