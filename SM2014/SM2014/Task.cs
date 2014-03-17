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
        private int idx;

        public static String UAG = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 734; Maxthon; .NET CLR 2.0.50727; .NET4.0C; .NET4.0E)";
        public static Random random = new Random();
        private int total = 0;

        public int Total
        {
            get { return total; }
            set { total = value; }
        }
        private int visited = 0;

        public int Visited
        {
            get { return visited; }
            set { visited = value; }
        }

        private String[] details = null;

        private String url = null;


        public Task(Form1 form, int idx)
        {
            this.form = form;
            this.idx = idx;
        }

        public void Start()
        {
            details = Regex.Split(form.Accounts[idx], "----");
            url = "http://pt.3g.qq.com/login?act=json&format=2&bid_code=house_touch&r=#" + "&qq=" + details[1] + "&pmd5=" + Util.UMD5X(details[2]) + "&go_url=http%3A%2F%2Fhouse60.3g.qq.com%2Ftouch%2Findex.jsp%3Fsid%3DAd_JZ1k2ZviFLkV2nvFt7005%26g_ut%3D3%26g_f%3D15124";

            form.info("开始查询帐号:" + details[1]);
            total = form.Proxies.Count;
            for(int i=0;i<form.Proxies.Count;i++)
            {
                MiniTask mt = new MiniTask(form, idx, this, details, url);
                ThreadPool.QueueUserWorkItem(new WaitCallback(mt.Run), i);
            }
        }
    }

    class MiniTask
    {
        private Form1 form;
        private int idx;
        private String[] details = null;
        private String url = null;
        private Task task;

        private bool finish = false;

        public MiniTask(Form1 form, int idx, Task task, string[] details, string url)
        {
            this.form = form;
            this.idx = idx;
            this.task = task;
            this.details = details;
            this.url = url;
        }

        public void Run(Object stateInfo)
        {
            if (finish)
            {
                return;
            }

            //http请求
            HttpClient client = new HttpClient();
            client.Headers.Add("User-Agent", Task.UAG);

            //取proxy
            String proxy = form.getProxy();

            WebProxy wp = new WebProxy(proxy);
            client.Proxy = wp;
            client.Encoding = Encoding.Default;

            try
            {
                url = url.Replace("#", Task.random.NextDouble().ToString());

                Stream data = client.OpenRead(url);
                StreamReader reader = new StreamReader(data);
                String line = reader.ReadToEnd();


                if (line.IndexOf("pt.handleLoginResult") == -1)
                {
                    //form.Proxies.Remove(proxy);
                }
                else
                {
                    bool ok = true;
                    if (line.IndexOf(",0,") != -1)
                    {
                        form.log(0, Int32.Parse(details[0]));
                    }
                    else if (line.IndexOf(",40010,") != -1)
                    {
                        form.log(1, Int32.Parse(details[0]));
                    }
                    else if (line.IndexOf(",40026,") != -1)
                    {
                        form.log(2, Int32.Parse(details[0]));
                    }
                    //else if (line.IndexOf("40001") != -1)
                    //{
                    //}
                    else
                    {
                        ok = false;
                    }

                    if (ok && !this.finish)
                    {
                        this.finish = true;
                        if (this.idx < form.Accounts.Count - 1)
                        {
                            Task taskx = new Task(form, this.idx + 1);//查询下一个帐号
                            taskx.Start();
                        }
                        return;
                    }
                }
            }
            catch (Exception)
            {
                //
            }

            lock (this.task)
            {
                form.info(details[1] + " -> " + proxy);
                task.Visited++;
                //Console.WriteLine(task.Total + "->" + task.Visited);
                if (task.Visited == task.Total) //已经完成所有线程，开启下一任务
                {
                    if (this.idx < form.Accounts.Count - 1)
                    {
                        Task taskx = new Task(form, this.idx + 1);//查询下一个帐号
                        taskx.Start();
                    }
                }
            }
        }
    }
}
