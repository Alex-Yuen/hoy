using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using System.Net;
using System.IO;
using System.Reflection;
using System.Security.Cryptography;

namespace xplayer
{
    public partial class Downloader : Form
    {
        private string version = null;
        private WebClient client = null;
        private bool gsf = true;//是否已经进入关闭模式
        private string md5 = null;

        public Downloader()
        {
            InitializeComponent();
        }

        private void download()
        {
            try
            {
                //check update
                if (label1.InvokeRequired)
                {
                    label1.Invoke(new DLDelegater(SetText), 0);
                }
                string update = null;
                // string version = null;
                string url = "http://www.hoyland.ws/x-player/update";

                WebRequest req = WebRequest.Create(url);
                req.Method = "GET";
                WebResponse res = req.GetResponse();
                
                Encoding resEncoding = Encoding.GetEncoding("utf-8");
                StreamReader reader = new StreamReader(res.GetResponseStream(), resEncoding);
                
                update = reader.ReadLine();
                
                reader.Close();
                res.Close();
                
                //AppDomainSetup setup = new AppDomainSetup();
                //setup.ApplicationBase = AppDomain.CurrentDomain.BaseDirectory;
                //AppDomain od = AppDomain.CreateDomain("otherDomain");

                //Assembly DllAssembly = ad.Load(AssemblyName.GetAssemblyName("x-player.dll"));
                // Only loads assembly in one application domain.
                //Assembly assembly = null;
                //od.DoCallBack(new CrossAppDomainDelegate(MyCallBack));
                //od.DoCallBack(() => {
                //    Assembly assembly = Assembly.Load(AssemblyName.GetAssemblyName("x-player.dll"));
                //});
                //ad.DoCallBack(() =>
                //{
                //    assembly = AppDomain.CurrentDomain.Load(AssemblyName.GetAssemblyName("x-player.dll")); 
                //version = assembly.FullName;
                //version = version.Split(',')[1].Substring(9);
                //});

                //AssemblyName assemblyName = DllAssembly.GetName();
                //assembly.
                //version = assembly.FullName;
                //version = version.Split(',')[1].Substring(9);

                //AppDomain.Unload(od);

                if (label1.InvokeRequired)
                {
                    label1.Invoke(new DLDelegater(SetText), 50);
                }

                if (update != null && version != null && !update.Equals(version))
                {
                    //download
                    url = "http://www.hoyland.ws/x-player/update/x-player-core.dll";
                    string filename = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) + "\\x-player-core.dll";
                    //Console.WriteLine("...."+filename);

                    //this.label1.Text = "0%";
                                        
                    client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(ProgressChanged);
                    client.DownloadFileCompleted += new AsyncCompletedEventHandler(Completed);
                    client.DownloadFileAsync(new Uri(url), filename);
                }
                else
                {
                    if (label1.InvokeRequired)
                    {
                        label1.Invoke(new DLDelegater(SetText), 100);
                    }
                    gs();
                }
            }
            catch (WebException e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine(e.StackTrace);
                gs();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine(e.StackTrace);
            }
        }

        private void ProgressChanged(object sender, DownloadProgressChangedEventArgs e)
        {
            if (label1.InvokeRequired)
            {
                label1.Invoke(new DLDelegater(SetText), 50 + e.ProgressPercentage/2);
            }
        }

        private void Completed(object sender, AsyncCompletedEventArgs e)
        {
            gs();
        }

        private void Downloader_Load(object sender, EventArgs e)
        {
            try
            {                
                string splash = AppDomain.CurrentDomain.BaseDirectory + "//splash.png";
                string splashnew = AppDomain.CurrentDomain.BaseDirectory + "//splash.new";
                if (File.Exists(splashnew))
                {
                    File.Delete(splash);
                    File.Move(splashnew, splash);
                }

                if (File.Exists(splash))
                {
                    FileStream file = new FileStream(splash, FileMode.Open);
                    MD5 md5s = new MD5CryptoServiceProvider();
                    byte[] retVal = md5s.ComputeHash(file);
                    file.Close();
                    //Console.WriteLine(this.md5);

                    Image imsp = Image.FromFile(splash);
                    if (imsp != null)
                    {
                        this.BackgroundImage = imsp;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < retVal.Length; i++)
                    {
                        sb.Append(retVal[i].ToString("x2"));
                    }
                    this.md5 = sb.ToString();
                }
            }
            catch (Exception ex)
            {
                //MessageBox.Show(ex.Message);
                Console.WriteLine(ex.Message);
                Console.WriteLine(ex.StackTrace);
            }

            try
            {
                this.client = new WebClient();
                AppDomain od = AppDomain.CreateDomain("new");
                //SRVersion srv = new SRVersion();
                //od.SetData("srv", srv);

                //Console.WriteLine(">>> 之前");
                //CheckAssemblies(AppDomain.CurrentDomain);
                //CheckAssemblies(od);

                od.DoCallBack(() =>
                {
                    Assembly assembly = Assembly.Load("x-player-core");
                    //version = assembly.FullName;
                    SRVersion srv = new SRVersion();
                    //SRVersion s = AppDomain.CurrentDomain.GetData("svr") as SRVersion;
                    //assembly.FullName.Split(',')[1].Substring(9);
                    srv.version = assembly.FullName.Split(',')[1].Substring(9);
                    AppDomain.CurrentDomain.SetData("svr", srv);
                    //srv.version = assembly.FullName.Split(',')[1].Substring(9);
                    //version = assembly.FullName.Split(',')[1].Substring(9);
                });

                //Console.WriteLine(">>> 之后");
                //CheckAssemblies(AppDomain.CurrentDomain);
                //CheckAssemblies(od);

                SRVersion s = od.GetData("svr") as SRVersion;
                //od.SetData("srv", srv);
                version = s.version;
                //Console.WriteLine(s.version);

                AppDomain.Unload(od);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                Console.WriteLine(ex.StackTrace);
                //Console.WriteLine(ex.Source);
            }

            Thread th = new Thread(new ThreadStart(download));
            th.Start();
        }

        //输出程序集
        /**
        static void CheckAssemblies(AppDomain ad)
        {

            var assList = ad.GetAssemblies();

            Console.WriteLine("{0}: {1}", ad.FriendlyName, assList.Length);

            foreach (var ass in assList)

                Console.WriteLine("  " + ass.GetName().Name);

        }**/

        private void gs()
        {
            if (gsf)
            {
                //Console.WriteLine("EEE");
                gsf = false;
                this.Invoke(new DLDelegater(SetClose), 0);
            }
        }

        public delegate void DLDelegater(int text);

        public void SetText(int i)
        {
            this.label1.Text = i.ToString()+"%";
        }

        public void SetClose(int i)
        {
            try
            {
                Form form = null;

                Assembly DllAssembly = Assembly.LoadFrom("x-player-core.dll");
                Type[] DllTypes = DllAssembly.GetTypes();

                foreach (Type DllType in DllTypes)
                {
                    if (DllType.Namespace == "xplayer" && DllType.Name == "XPlayer")
                    {
                        form = (Form)(Activator.CreateInstance(DllType, new object[]{this, this.md5}));
                        break;
                    }
                }

                if (form != null)
                {
                    form.Show();
                    this.Hide();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error");
            }
        }

        private void Downloader_FormClosing(object sender, FormClosingEventArgs e)
        {
            //Console.WriteLine(sender);
            if (this.Visible)
            {
                e.Cancel = true;
                //gs();
                if (this.client != null)
                {
                    if (this.client.IsBusy)
                    {
                        this.client.CancelAsync();
                    }
                    this.client.Dispose();
                }
                gs();
            }
            //base.OnFormClosing(e); 
            //return;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void button1_Click_1(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
