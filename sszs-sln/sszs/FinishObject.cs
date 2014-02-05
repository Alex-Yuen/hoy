using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ws.hoyland.sszs
{
    public class FinishObject
    {
        private static FinishObject instance;

        private FinishObject()
        {

        }

        public static FinishObject getInstance()
        {
            if (instance == null)
            {
                instance = new FinishObject();
            }
            return instance;
        }
    }
}
