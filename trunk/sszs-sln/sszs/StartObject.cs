using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ws.hoyland.sszs
{
    public class StartObject
    {
        private static StartObject instance;

        private StartObject()
        {

        }

        public static StartObject getInstance()
        {
            if (instance == null)
            {
                instance = new StartObject();
            }
            return instance;
        }
    }
}
