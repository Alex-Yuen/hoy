using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows;
using Ws.Hoyland.CSharp;

namespace Ws.Hoyland.XAM
{
    static class Program
    {
        /// <summary>
        /// 应用程序的主入口点。
        /// </summary>
        [STAThread]
        static void Main()
        {
            AppDomain.CurrentDomain.UnhandledException += new System.UnhandledExceptionEventHandler(ExceptionHandler);

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            Downloader downloader = new Downloader();
            downloader.Text = "X-Asset-Manager";
            downloader.URL = "http://www.hoyland.ws/x-asset-manager";
            downloader.CoreName = "x-asset-manager-core";
            downloader.Namespace = "Ws.Hoyland.XAM";
            Application.Run(downloader);
        }

        static void ExceptionHandler(object sender, System.UnhandledExceptionEventArgs e)
        {
            MessageBox.Show("我们很抱歉，当前应用程序遇到一些问题，该操作已经终止，请进行重试，如果问题继续存在，请联系开发商.", "意外的操作", MessageBoxButtons.OK, MessageBoxIcon.Information);//这里通常需要给用户一些较为友好的提示，并且后续可能的操作
            Application.Exit();
        }
    }
}
