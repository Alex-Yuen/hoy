using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace Ws.Hoyland.CSharp.XThread
{
    public class MThread
    {
        private Thread t;
        private ThreadManager manager = null;
        private AutoResetEvent evt = null;
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

        public MThread(ThreadManager manager)
        {
            this.manager = manager;
        }

        public void Abort()
        {
            flag = false;
            if (evt != null)
            {
                evt.Set();
            }
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

        public void Init()
        {
            flag = true;
            evt = new AutoResetEvent(false);
            t = new Thread(new ThreadStart(this.ThreadProc));
            t.IsBackground = true;
            t.Name = "MThread's Thread";
            t.Start();
        }

        public void Execute()
        {
            //new Thread(new ThreadStart(() =>
            //{
            //    while (!((t.ThreadState | ThreadState.WaitSleepJoin) == t.ThreadState))
            //    {
            //        Thread.Sleep(50);
            //    }
            //    evt.Set();
            //})).Start();
            evt.Set();
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

                //if (task != null)
                //{
                try
                {
                    manager.QueueThread(this); //标志为可用
                    evt.WaitOne();
                }
                catch (Exception)
                {
                    //
                }

                //try
                //{
                //    Thread.Sleep(500);
                //}
                //catch (Exception)
                //{
                //    //
                //}

                try
                {
                    if (task != null)
                    {
                        task.Run();
                        task = null;
                    }
                    //run = true;
                }
                catch (Exception)
                {
                    //
                }
                //}
                //else
                //{
                //    try
                //    {
                //        lock (queue)
                //        {
                //            if (queue.Count > 0)
                //            {
                //                task = queue.Dequeue();
                //            }
                //        }
                //    }
                //    catch (Exception)
                //    {
                //        //
                //    }

                //    try
                //    {
                //        Thread.Sleep(500);
                //    }
                //    catch (Exception)
                //    {
                //        //
                //    }
                //}

                //}

                //try
                //{
                //    //if (run)
                //    //{
                //        Thread.Sleep(2000);
                //    //}
                //}
                //catch (Exception)
                //{
                //    //Console.WriteLine(">>>>>>>>>>>>>>>>>>>>>>>>>2");
                //}

                //try
                //{
                //    if (task == null)
                //    {
                //        lock (queue)
                //        {
                //            if (queue.Count > 0)
                //            {
                //                task = queue.Dequeue();
                //            }
                //        }
                //    }
                //}
                //catch (Exception)
                //{
                //    //Console.WriteLine(">>>>>>>>>>>>>>>>>>>>>>>>>1");
                //}


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
