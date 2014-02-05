using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace ws.hoyland.util
{
    public class Basket
    {
        private byte count = 0;

        public void push()
        {
            lock (this)
            {
                try
                {
                    while (count == 1)
                    {
                        Monitor.Wait(this);
                    }
                    Monitor.PulseAll(this);
                }
                catch (Exception e)
                {
                    throw e;
                }
                count = 1;
            }
        }

        public void pop()
        {
            lock (this)
            {
                try
                {
                    while (count == 0)
                    {
                        Monitor.Wait(this);
                    }
                    Monitor.PulseAll(this);
                }
                catch (Exception e)
                {
                    throw e;
                }
                count = 0;
            }
        }
    }
}
