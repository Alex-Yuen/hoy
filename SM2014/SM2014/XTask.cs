using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ws.Hoyland.CSharp.XThread;

namespace SM2014
{
    public class XTask :Runnable
    {
        private String line;
        public XTask(String line)
        {
            this.line = line;
        }

        //public void RunX(Object info)
        //{
        //    Run();
        //}

        public void Run()
        {
            Console.WriteLine("EFFFFFFFFFFFFFFFF");
        }

        public void Abort()
        {
        }
    }
}
