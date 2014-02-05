using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ws.hoyland.util
{

    public class Observable
    {
        public delegate void UpdateEventHandler(object sender, EventArgs e);

        public event UpdateEventHandler update;

        public void notifyObservers(EventArgs e)
        {
            if (update != null)
            {                      //这里表示subjects状态有变化，需要通知observers.
                update(this, e);
            }
        }

        public void addObserver(Observer obsever)
        {
            this.update += new UpdateEventHandler(obsever.update);
        }


        public void removeObserver(Observer obsever)
        {
            this.update -= new UpdateEventHandler(obsever.update);
        }
    }
}
