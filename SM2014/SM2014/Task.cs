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
 //       private Form1 form;
        private String line;
        private bool flag = true;
        //private HttpClient client = null;

//        private static String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
        private static Configuration cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
//        private static HttpClient client = new HttpClient();

        public Task(String line)
        {
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
            this.flag = false;
        }

        public void Run()
        {
            //long start = DateTime.Now.Ticks;

            String[] details = Regex.Split(line, "----");
            String url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=#" + "&qq=" + details[1] + "&pmd5=" + Util.UMD5X(details[2]) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";
            
            Form1.GetInstance().Info("开始查询帐号:" + details[1]);

            WebProxy wp = null;

            HttpWebRequest request = null;
            HttpWebResponse response = null;

            Stream stream = null;
            StreamReader reader = null;

            String resp = null;

            int times = 0;

            //http请求

            int ts = Int32.Parse(cfa.AppSettings.Settings["TASK_TIMES"].Value);
            int timeout = Int32.Parse(cfa.AppSettings.Settings["TIMEOUT"].Value);

            while (flag && times < ts)
            {
                //Console.WriteLine("DDDDDDDDD");
                //ConfigurationManager.RefreshSection("appSettings");

                resp = null;
                //client.Headers.Add("User-Agent", Task.UAG);
                String proxy = Form1.GetInstance().GetProxy();
                //String proxy = "127.0.0.1:8888";

                if (proxy == null)
                {
                    flag = false;
                    continue;
                }

                wp = new WebProxy(proxy);
                Form1.GetInstance().Info(details[1] + " -> " + proxy);
                //client.Proxy = wp;
                //client.Encoding = Encoding.Default;

                try
                {
                    //url = url.Replace("#", Form1.RANDOM.NextDouble().ToString());

                    request = (HttpWebRequest)WebRequest.Create(url);
                    request.Timeout = 1000 * timeout;
                    request.ReadWriteTimeout = 1000 * timeout;
                    request.Proxy = wp;

                    //Thread.Sleep(2000);

                    response = (HttpWebResponse)request.GetResponse();

                    //Console.WriteLine(Form1.RANDOM.Next(100) + Form1.RANDOM.Next(100));
                    stream = response.GetResponseStream();

                    if (stream != null)
                    {
                        reader = new StreamReader(stream, System.Text.Encoding.UTF8);
                        resp = reader.ReadToEnd();
                    }
                    else
                    {
                        throw new Exception();
                    }

                    wp = null;

                    //resp = "123";

                    if (resp.IndexOf("pt.handleLoginResult") == -1)//代理异常
                    {
                        Form1.GetInstance().RemoveProxy(proxy);
                    }
                    else
                    {
                        //bool ok = false;
                        if (resp.IndexOf("," + details[1] + ",0,") != -1)
                        {
                            //Form1.GetInstance().Log(0, details[1] + "----" + details[2]);//details[1] + " / " + proxy
                            flag = false;
                        }
                        else if (resp.IndexOf(",0,40010,") != -1)
                        {
                            //Form1.GetInstance().Log(1, details[1] + "----" + details[2]);
                            flag = false;
                        }
                        else if (resp.IndexOf(",0,40026,") != -1)
                        {
                            //Form1.GetInstance().Log(2, details[1] + "----" + details[2]);
                            flag = false;
                        }
                        else if (resp.IndexOf("," + details[1] + ",0,") != -1)//验证码
                        {
                            Form1.GetInstance().Queue(this.line);

                            //不离开当前任务
                            //Thread.Sleep(1000 * Int32.Parse(cfa.AppSettings.Settings["P_ITV"].Value));//N秒后继续
                        }
                        else //代理异常
                        {
                            Form1.GetInstance().RemoveProxy(proxy);
                        }
                    }
                }
                catch (Exception e)
                {
                    //Console.WriteLine(e.Message);
                    //代理异常
                    Form1.GetInstance().RemoveProxy(proxy);
                }
                finally
                {
                    if (reader != null)
                    {
                        reader.Close();
                        reader.Dispose();
                    }

                    if (stream != null)
                    {
                        stream.Close(); 
                        stream.Dispose();
                    }

                    if (response != null)
                    {
                        response.Close();
                    }
                }

                times++;
            }

            //Console.WriteLine(Thread.CurrentThread.GetHashCode() + ">>>>:" + (DateTime.Now.Ticks - start));
            //client.Dispose();

            Form1.GetInstance().Finish();

            //Thread.CurrentThread.Abort();
        }
    }
}
