using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using ws.hoyland.sszs;
using System.IO;

namespace ws.hoyland.sszs
{
    static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            AppDomain.CurrentDomain.UnhandledException += new UnhandledExceptionEventHandler(CurrentDomain_UnhandledException);
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new SSZS());
        }

        static void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            string strException = string.Format("{0}发生系统异常。\r\n{1}\r\n\r\n\r\n", DateTime.Now, e.ExceptionObject.ToString());
            File.AppendAllText(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "error.log"), strException);
        }
    }
}
