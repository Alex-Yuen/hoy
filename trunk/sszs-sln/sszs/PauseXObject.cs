using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ws.hoyland.sszs
{
    public class PauseXObject
    {
        private static PauseXObject instance;

        private PauseXObject()
        {

        }

        public static PauseXObject getInstance()
        {
            if (instance == null)
            {
                instance = new PauseXObject();
            }
            return instance;
        }
    }
}
