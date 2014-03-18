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
            lock (this)
            {
                Monitor.PulseAll(this);
            }

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

        public void Execute()
        {
            flag = true;

            if (task != null)
            {
                if (t == null)
                {
                    t = new Thread(new ThreadStart(this.ThreadProc));
                    t.Start();
                }
                else
                {
                    //Console.WriteLine(t.ThreadState);
                    /**
                    lock (queue)
                    {
                        if (queue.Count > 0)
                        {
                            task = null;
                            GC.Collect();
                            task = queue.Dequeue();
                        }
                    }**/

                    lock (this)
                    {
                        Monitor.PulseAll(this);
                    }
                }
            }
        }

        private void ThreadProc()
        {
            while (flag)
            {
                task.Run();
                //Thread.Sleep(500);
                if (flag)
                {
                    lock (this)
                    {
                        Monitor.Wait(this);
                    }
                }
            }
        }
    }
}
