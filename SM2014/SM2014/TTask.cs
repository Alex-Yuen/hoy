using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Ws.Hoyland.CSharp.XThread;

namespace SM2014
{
    public class TTask :Runnable
    {
        private String line;
        public TTask(String line)
        {
            this.line = line;
        }

        public void Run()
        {
            Console.WriteLine("EFFFFFFFFFFFFFFFF");
        }

        public void Abort()
        {
        }
    }
}
