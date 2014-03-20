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
        private Queue<Runnable> queue = new Queue<Runnable>(); //task queue
        private Queue<MThread> threadQueue = new Queue<MThread>(); //MThread Queue

        public Queue<MThread> ThreadQueue
        {
            get { return threadQueue; }
            set { threadQueue = value; }
        }

        public Queue<Runnable> Queue
        {
            get { return queue; }
            set { queue = value; }
        }

        private Thread dispatcher = null;
        private AutoResetEvent evt = null;
        private AutoResetEvent evtx = null;
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
                MThread mt = new MThread(this);
                list.Add(mt);
            }
        }

        public void QueueTask(Runnable task)
        {
            lock (queue)
            {
                queue.Enqueue(task);
            }

            if (flag)
            {
                evt.Set();
            }
        }

        public void QueueThread(MThread mt)
        {
            lock (threadQueue)
            {
                threadQueue.Enqueue(mt);
            }

            if (flag)
            {
                evtx.Set();
            }
        }

        public void Execute()
        {
            flag = true;
            evt = new AutoResetEvent(false);
            evtx = new AutoResetEvent(false);

            //开启任务分配
            dispatcher = new Thread(new ThreadStart(() =>
            {
                while (flag)
                {
                    Runnable task = null;
                    lock (queue)
                    {
                        if (queue.Count > 0)
                        {
                            task = queue.Dequeue();
                        }
                    }

                    if (task != null)
                    {
                        while (flag)
                        {
                            MThread mt = null;
                            lock (threadQueue)
                            {
                                if (threadQueue.Count > 0)
                                {
                                    mt = threadQueue.Dequeue();
                                }
                            }
                            
                            if (mt != null)
                            {
                                if (!((mt.T.ThreadState | ThreadState.WaitSleepJoin) == mt.T.ThreadState))
                                {//取到的mt未准备好
                                    this.QueueThread(mt);
                                    mt = null;
                                    Thread.Sleep(100);//5
                                    //continue;
                                }
                                else
                                {
                                    mt.Task = task;
                                    mt.Execute();
                                    break;
                                }
                            }
                            else
                            {
                                evtx.WaitOne(100);
                                Thread.Sleep(10);// 稍等，再继续取MT，避免MT未进入WaitSleepJoin状态
                            }
                        }
                        Thread.Sleep(100);//5
                    }
                    else
                    {
                        evt.WaitOne(100); //等待
                    }
                }
            }));
            dispatcher.Name = "Dispatcher";
            dispatcher.IsBackground = true;
            
            new Thread(new ThreadStart(() =>
            {
                //初始化
                foreach (MThread xt in list)
                {
                    xt.Init();
                    Thread.Sleep(5);
                }
                dispatcher.Start();
            })).Start();

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
            flag = false;
            if (evtx != null)
            {
                evtx.Set();
            }

            if (evt != null)
            {
                evt.Set();
            }

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
