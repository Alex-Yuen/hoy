using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Ws.Hoyland.CSharp.XThread
{
    public interface Runnable
    {
        //void RunX(Object info);
        void Run();
        void Abort();
    }
}
