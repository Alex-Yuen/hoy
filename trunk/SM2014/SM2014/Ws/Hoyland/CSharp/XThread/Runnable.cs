using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Ws.Hoyland.CSharp.XThread
{
    public interface Runnable
    {
        void Run();
        void Abort();
    }
}
