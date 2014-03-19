using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Threading;
using SM2014;

namespace Ws.Hoyland.CSharp.XThread
{
    public class ThreadManager
    {
        private int core = 2;
        private ArrayList list = new ArrayList(); //线程列表
        private Queue<Runnable> queue = new Queue<Runnable>();
        private Thread starter = null;

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
            //flag = true;

            //开启初始化线程
            starter = new Thread(new ThreadStart(() =>
            {
                //初始化
                foreach (MThread xt in list)
                {
                    //lock (xt)
                    //{
                        xt.Start();
                    //}
                    Thread.Sleep(50);
                }
            }));
            starter.Name = "Starter";
            starter.Start();

            //开启检测线程
            //checker = new Thread(new ThreadStart(() =>
            //{
            //    while (flag)
            //    {
            //        foreach (MThread xt in list)
            //        {
            //            if (flag)
            //            {
            //                //lock (xt)
            //                //{
            //                    if (xt.Task == null)
            //                    {
            //                        lock (queue)
            //                        {
            //                            if (queue.Count > 0)
            //                            {
            //                                xt.Task = queue.Dequeue();
            //                            }
            //                        }
            //                    }
            //                //}
            //            }
            //            else
            //            {
            //                break;
            //            }
            //            Thread.Sleep(50);
            //        }

            //        //Thread.Sleep(500);
            //    }
            //}));
            //checker.Name = "Checker";
            //checker.Start();
        }

        public void Shutdown()
        {
            //flag = false;

            lock (queue)
            {
                queue.Clear();
            }

            foreach (MThread xt in list)
            {
                //if (xt.T != null)
                //{
                    xt.Abort();
                //}
                //Thread.Sleep(5);
            }
        }

        //private void Start()
        //{
        //    //初始化
        //    foreach (MThread xt in list)
        //    {
        //        xt.Start();
        //        Thread.Sleep(5);
        //        //if (xt.T == null && xt.Task != null)
        //        //{
        //        //    xt.Init();
        //        //}

        //        //if (xt.Task == null)
        //        //{
        //        //    lock (queue)
        //        //    {
        //        //        if (queue.Count > 0)
        //        //        {
        //        //            xt.Task = queue.Dequeue();
        //        //        }
        //        //    }
        //        //}
        //    }

        //}

        //private void Check()
        //{

        //    while (flag)
        //    {
        //        //执行任务
        //        foreach (MThread xt in list)
        //        {
        //            //if (xt.T != null &&xt.W != null)
        //            //if(xt.T==null || xt.T.ThreadState == ThreadState.WaitSleepJoin)
        //            {
        //                //if (Form1.RANDOM.Next(100) < 10)
        //                //{
        //                if (flag)
        //                {
        //                    if (xt.Task == null)
        //                    {
        //                        lock (queue)
        //                        {
        //                            if (queue.Count > 0)
        //                            {
        //                                xt.Task = queue.Dequeue();
        //                            }
        //                        }
        //                    }
        //                }
        //                else
        //                {
        //                    break;
        //                }
        //                Thread.Sleep(50);
        //                //}
        //            }
        //        }

        //        Thread.Sleep(2000);
        //    }
        //}
    }
}
