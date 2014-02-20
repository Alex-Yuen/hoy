using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using ws.hoyland.sszs;
using System.IO;
using ws.hoyland.util;

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
            //Application.Run(new SSZS());

            CheckResult cr = new CheckResult();
            Engine.getInstance().addObserver(cr);

            EngineMessage message = new EngineMessage();
            message.setType(EngineMessageType.IM_CHECKEXP);
            message.setData(null);

            Engine.getInstance().fire(message);
        }

        static void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            string strException = string.Format("{0}发生系统异常。\r\n{1}\r\n\r\n\r\n", DateTime.Now, e.ExceptionObject.ToString());
            File.AppendAllText(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "error.log"), strException);
        }
    }

    class CheckResult : Observer
    {
        public void update(object sender, EventArgs e)
        {
            EngineMessage msg = (EngineMessage)e;
            int type = msg.getType();

            switch (type)
            {
                case EngineMessageType.OM_CHECKEXP:

                    int expire = (Int32)msg.getData();
                    if (expire <= 0)
                    {
                        new Expire().ShowDialog();//Show("此机器授权已经过期:" + byteArrayToHexString(mc).ToUpper());
                        Application.Exit();
                    }
                    else
                    {
                        //Console.WriteLine(byteArrayToHexString(mc).ToUpper());
                        Application.Run(new Declare(expire));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
