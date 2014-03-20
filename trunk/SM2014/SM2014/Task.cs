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

        public String Line
        {
            get { return line; }
            set { line = value; }
        }

        private AutoResetEvent evt;

        public AutoResetEvent AutoResetEvent
        {
            get { return evt; }
            set { evt = value; }
        }

        private String proxy;

        public String Proxy
        {
            get { return proxy; }
            set { proxy = value; }
        }

        //private bool flag = true;
        //private HttpClient client = null;

        //        private static String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
        private static String UAG = "Opera/9.25 (Windows NT 6.0; U; en)";
        private static Configuration cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
        //        private static HttpClient client = new HttpClient();
        private static AsyncCallback callback = new AsyncCallback(Engine.OnResponseX);
        //private static Object infoobj = new Object();

        public Task(String line)
        {
            this.line = line;
            this.evt = new AutoResetEvent(false);
            //cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            //if (cfa == null)
            //{
            //    MessageBox.Show("加载配置文件失败!");
            //}
        }

        public void Abort()
        {
            //
            //this.flag = false;
            //Console.WriteLine("SET............");
            evt.Set();
        }

        public void Run()
        {
            //long start = DateTime.Now.Ticks;
            proxy = Engine.GetInstance().GetProxy();
            if (proxy == null)
            {
                //flag = false;
                return;
            }

            String[] details = Regex.Split(line, "----");
            String url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=#" + "&qq=" + details[1] + "&pmd5=" + Util.UMD5X(details[2]) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";

            //lock (infoobj)
            //{
            Form1.GetInstance().Info("开始查询帐号:" + details[1]);
            //}
            //Console.WriteLine("开始查询帐号:" + details[1]);

            HttpWebRequest request = null;
            WebProxy wp = null;

            //int times = 0;

            //http请求
            
            //timeout = 1;
            //timeout = 1;

            //while (flag && times < ts)
            {
                //Console.WriteLine("DDDDDDDDD");
                //ConfigurationManager.RefreshSection("appSettings");

                //client.Headers.Add("User-Agent", Task.UAG);
                //proxy = Engine.GetInstance().GetProxy();
                //proxy = "127.0.0.1:8888";
                //proxy = "1.62.18.106:8088";

                //if (proxy == null)
                //{
                //    //flag = false;
                //    return;
                //}

                wp = new WebProxy(proxy);
                Form1.GetInstance().Info(details[1] + " -> " + proxy);
                //Console.WriteLine(details[1] + " -> " + proxy);
                //client.Proxy = wp;
                //client.Encoding = Encoding.Default;

                try
                {
                    url = url.Replace("#", Engine.RANDOM.NextDouble().ToString());
                    request = (HttpWebRequest)WebRequest.Create(url);
                    //request.Timeout = 1000 * timeout;
                    //request.ReadWriteTimeout = 1000 * timeout;
                    //request.Method = "GET";
                    request.Proxy = wp;
                    request.KeepAlive = false;

                    request.Method = "GET";
                    request.UserAgent = UAG;

                    //request.ServicePoint.Expect100Continue = false;
                    request.ServicePoint.UseNagleAlgorithm = false;
                    //request.ServicePoint.ConnectionLimit = 65500;
                    request.AllowWriteStreamBuffering = false;

                    //MessageBox.Show(request.ServicePoint.ConnectionLimit.ToString());

                    //request.Headers.Add(HttpRequestHeader.AcceptEncoding, "gzip,deflate");

                    //Thread.Sleep(2000);
                    //Engine.GetInstance().Put(request);


                    request.BeginGetResponse(callback, new Tuple<HttpWebRequest, Task>(request, this));//new Tuple<HttpWebRequest, Task>(request, this)

                    evt.WaitOne(Engine.TIMEOUT);
                }
                catch (Exception)
                {
                    //代理异常
                    Engine.GetInstance().RemoveProxy(proxy);
                }
                finally
                {
                    wp = null;
                    //proxy = null;

                    //request = null;
                    if (request != null)
                    {
                        request.Abort();
                        request = null;
                    }
                }
                
                //times++;
                //allDone.WaitOne();
            }
        }
    }
}
