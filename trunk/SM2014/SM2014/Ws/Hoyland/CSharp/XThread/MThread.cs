using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Ws.Hoyland.CSharp.XThread
{
    internal class MThread
    {
        private Thread t;
        private Queue<Runnable> queue;
        private bool flag = false;

        public Thread T
        {
            get { return t; }
            set { t = value; }
        }

        private Runnable task;

        public Runnable Task
        {
            get { return task; }
            set { task = value; }
        }

        public MThread(Queue<Runnable> queue)
        {
            this.queue = queue;
        }

        public void Abort()
        {
            flag = false;
            //if (t.ThreadState == ThreadState.WaitSleepJoin)
            //{
            //    lock (this)
            //    {
            //        Monitor.PulseAll(this);
            //    }
            //}

            if (task != null)
            {
                task.Abort();
            }
            //Console.WriteLine(t.ThreadState);
            //if (t.ThreadState == ThreadState.Running)
            //{
            //    t.Abort();
            //}
        }

        public void Start()
        {
            //if(task!=null)
            //{
            //    if (t == null)
            //    {
            flag = true;
            t = new Thread(new ThreadStart(this.ThreadProc));
            t.IsBackground = true;
            t.Name = "MThread's T";
            t.Start();
            //    }
            /**
        else
        {
            if (t.ThreadState == ThreadState.WaitSleepJoin)
            {
                lock (this)
                {
                    Monitor.PulseAll(this);
                }
            }
        }**/
            //}
        }

        private void ThreadProc()
        {
            //bool run = false; //标志本轮是否有运行

            while (flag)
            {
                //run = false;

                //wc = null;
                //wc = new WaitCallback(task.RunX);
                //wc(null);

                //if (!flag)
                //{
                //    continue;
                //}
                //lock (this)
                //{

                try
                {
                    if (task != null)
                    {
                        task.Run();
                        task = null;
                        //run = true;
                    }
                }
                catch (Exception)
                {
                    //
                }
                //}

                try
                {
                    if (task == null)
                    {
                        lock (queue)
                        {
                            if (queue.Count > 0)
                            {
                                task = queue.Dequeue();
                            }
                        }
                    }
                }
                catch (Exception)
                {
                    //Console.WriteLine(">>>>>>>>>>>>>>>>>>>>>>>>>1");
                }


                try
                {
                    //if (run)
                    //{
                        Thread.Sleep(200);
                    //}
                }
                catch (Exception)
                {
                    //Console.WriteLine(">>>>>>>>>>>>>>>>>>>>>>>>>2");
                }
                /**
                if (flag)
                {
                    lock (this)
                    {
                        Monitor.Wait(this);
                    }
                }**/
            }
        }
    }
}
