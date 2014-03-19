using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SM2014
{
    public class Engine
    {
        private List<String> proxies;

        public static Random RANDOM = new Random();

        public List<String> Proxies
        {
            get { return proxies; }
            set { proxies = value; }
        }

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

        public static void OnResponse(IAsyncResult ar)
        {
            //Console.WriteLine("BACK");
        }

    }
}
