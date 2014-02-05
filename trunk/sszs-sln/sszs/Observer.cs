using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ws.hoyland.util
{
    public interface Observer
    {
        public void update(object sender, EventArgs e);
    }
}
