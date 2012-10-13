using System;
using System.Windows.Forms;
using System.Net;
using System.Text;
using System.IO;
using System.Reflection;
using System.Threading;

namespace xplayer
{
    static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Downloader());
        }
    }
}
