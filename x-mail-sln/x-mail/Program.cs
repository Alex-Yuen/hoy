using System;
using System.Windows.Forms;
using System.Net;
using System.Text;
using System.IO;
using System.Reflection;
using System.Threading;
using System.Diagnostics;
using System.Security.Permissions;

namespace Ws.Hoyland.Xmail
{
    static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        [SecurityPermission(SecurityAction.Demand, Flags = SecurityPermissionFlag.ControlAppDomain)]
        static void Main()
        {
            AppDomain.CurrentDomain.UnhandledException += new System.UnhandledExceptionEventHandler(ExceptionHandler);
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new Downloader());

        }

        static void ExceptionHandler(object sender, System.UnhandledExceptionEventArgs e)
        {
            try
            {
                Exception ex = (Exception)e.ExceptionObject;
                string errorMsg = "An application error occurred. Please contact the adminstrator " +
                    "with the following information:/n/n";

                if (!EventLog.SourceExists("ThreadException"))
                {
                    EventLog.CreateEventSource("ThreadException", "X-MAIL");
                }

                EventLog myLog = new EventLog();
                myLog.Source = "ThreadException";
                myLog.WriteEntry(errorMsg + ex.Message + "/n/nStack Trace:/n" + ex.StackTrace);
            }
            catch (Exception exc)
            {
                try
                {
                    MessageBox.Show("Fatal Non-UI Error",
                        "Fatal Non-UI Error. Could not write the error to the event log. Reason: "
                        + exc.Message, MessageBoxButtons.OK, MessageBoxIcon.Stop);
                }
                finally
                {
                    Application.Exit();
                }
            }
            /**
            if (e.ExceptionObject != null)
            {
                Exception ex = e.ExceptionObject as Exception;
                new System.Windows.Forms.ThreadExceptionDialog(ex).ShowDialog();
            }**/
        }
    }
}
