using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Net;
using System.IO;

namespace SM2014
{
    public class Engine
    {
        private List<String> proxies;
        private Queue<HttpWebRequest> queue;

        public List<String> Proxies
        {
            get { return proxies; }
            set { proxies = value; }
        }

        public static Random RANDOM = new Random();
        private static AsyncCallback callback = new AsyncCallback(OnResponseX);


        private static Object iobj = new Object();
        private static Engine INSTANCE;
                
        public static Engine GetInstance()
        {
            lock (iobj)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new Engine();
                }
            }
            return INSTANCE;
        }

        private Engine()
        {
            this.queue = new Queue<HttpWebRequest>();
        }
        
        public String GetProxy()
        {
            lock (proxies)
            {
                if (proxies.Count == 0)
                {
                    return null;
                }
                else
                {
                    return proxies[RANDOM.Next(proxies.Count)];
                }
            }
        }

        public void RemoveProxy(String proxy)
        {
            lock (proxies)
            {
                proxies.Remove(proxy);
            }
            
            Form1.GetInstance().UpdateProxy(proxies.Count);
        }

        public void Put(HttpWebRequest request)
        {
            lock (this.queue)
            {
                queue.Enqueue(request);
            }
        }

        //request.BeginGetResponse(callback, new Tuple<HttpWebRequest, Task>(request, this));


        public static void OnResponseX(IAsyncResult ar)
        {
        }

        public static void OnResponse(IAsyncResult ar)
        {
            Tuple<HttpWebRequest, Task> tuple = null;
            String[] details = null;
            HttpWebRequest request = null;
            HttpWebResponse response = null;
            Stream stream = null;
            StreamReader reader = null;
            String resp = null;
            Task task = null;

            try
            {
                tuple = (Tuple<HttpWebRequest, Task>)ar.AsyncState;

                request = tuple.Item1;
                task = tuple.Item2;
                details = Regex.Split(task.Line, "----");

                response = (HttpWebResponse)request.EndGetResponse(ar);
                stream = response.GetResponseStream();

                if (stream != null)
                {
                    reader = new StreamReader(stream, Encoding.UTF8);
                    resp = reader.ReadToEnd();
                }
                else
                {
                    throw new Exception();
                }

                //wp = null;

                //resp = "123";

                if (resp.IndexOf("pt.handleLoginResult") == -1)//代理异常
                {
                    //Form1.GetInstance().RemoveProxy(proxy);
                }
                else
                {
                    //bool ok = false;
                    if (resp.IndexOf("," + details[1] + ",0,") != -1)
                    {
                        //Form1.GetInstance().Log(0, details[1] + "----" + details[2]);//details[1] + " / " + proxy
                        task.Abort();
                    }
                    else if (resp.IndexOf(",0,40010,") != -1)
                    {
                        //Form1.GetInstance().Log(1, details[1] + "----" + details[2]);
                        task.Abort();
                    }
                    else if (resp.IndexOf(",0,40026,") != -1)
                    {
                        //Form1.GetInstance().Log(2, details[1] + "----" + details[2]);
                        task.Abort();
                    }
                    else if (resp.IndexOf("," + details[1] + ",0,") != -1)//验证码
                    {
                        //Form1.GetInstance().Queue(this.line);

                        //不离开当前任务
                        //Thread.Sleep(1000 * Int32.Parse(cfa.AppSettings.Settings["P_ITV"].Value));//N秒后继续
                    }
                    else //代理异常
                    {
                        //Form1.GetInstance().RemoveProxy(proxy);
                    }
                }
            }
            catch (Exception)
            {
                //Console.WriteLine(e.Message);
                //代理异常
                //Form1.GetInstance().RemoveProxy(proxy);
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

            //Console.WriteLine(Thread.CurrentThread.GetHashCode() + ">>>>:" + (DateTime.Now.Ticks - start));
            //client.Dispose();

            //Form1.GetInstance().Finish();

            //Thread.CurrentThread.Abort();
        }

    }
}
