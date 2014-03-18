using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Threading;

namespace Ws.Hoyland.CSharp.XThread
{
    public class ThreadManager
    {
        private int core = 2;        
        private ArrayList list = new ArrayList(); //线程列表
        private Queue<Runnable> queue = new Queue<Runnable>();
        private Thread checker = null;
        private bool flag = false;

        //不允许创建实例
        public ThreadManager()
        {
            Init();
        }

        public ThreadManager(int core)
        {
            this.core = core;
            Init();
        }
        
        private void Init()
        {
            for (int i = 0; i < core; i++)
            {
                MThread t = new MThread(queue);
                list.Add(t);
            }
        }

        public void Queue(Runnable task)
        {
            lock (queue)
            {
                queue.Enqueue(task);
            }
        }

        public void Execute()
        {
            flag = true;
            //开启检测线程
            checker = new Thread(new ThreadStart(this.Check));
            checker.Start();
        }

        public void Shutdown()
        {
            flag = false;
        }
        private void Check()
        {
            //填充
            foreach (MThread xt in list)
            {
                lock (queue)
                {
                    if (queue.Count > 0)
                    {
                        xt.Task = queue.Dequeue();
                    }
                    else
                    {
                        break;
                    }
                }
            }

            while (flag)
            {
                Thread.Sleep(500);

                //填充队列
                foreach (MThread xt in list)
                {
                    //if (xt.T != null &&xt.W != null)
                    if(xt.T==null || xt.T.ThreadState == ThreadState.WaitSleepJoin)
                    {
                        xt.Execute();
                    }
                }
            }

            lock (queue)
            {
                queue.Clear();
            }

            foreach (MThread xt in list)
            {
                if (xt.T != null)
                {
                    xt.Abort();
                }
            }
        }
    }
}
