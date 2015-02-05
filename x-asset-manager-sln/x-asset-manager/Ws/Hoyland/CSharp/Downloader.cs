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
using System.Diagnostics;

namespace Ws.Hoyland.CSharp
{
    public partial class Downloader : DelegatedForm
    {
        private string version = null;
        private WebClient client = null;
        private bool cf = true;//是否已经进入关闭模式
        private string md5 = null;
        private Form form = null;
        private string xpath = null;

        private string url = null;

        public string URL
        {
            get { return url; }
            set { url = value; }
        }

        private string core_name = null;

        public string CoreName
        {
            get { return core_name; }
            set { core_name = value; }
        }

        private string ns = null;

        public string Namespace
        {
            get { return ns; }
            set { ns = value; }
        }


        public Downloader()
        {
            InitializeComponent();
        }

        private void CheckingUpdate()
        {
            try
            {
                //check update
                SetProgress(0);

                string rv = null;
                // string version = null;
                string u = url + "/update";

                WebRequest req = WebRequest.Create(u);
                req.Method = "GET";
                WebResponse res = req.GetResponse();
                
                Encoding resEncoding = Encoding.GetEncoding("utf-8");
                StreamReader reader = new StreamReader(res.GetResponseStream(), resEncoding);

                rv = reader.ReadLine();
                
                reader.Close();
                res.Close();
                
                SetProgress(50);
                //Console.WriteLine(">>>" + String.Compare(rv, version));
                if (rv != null && version != null && String.Compare(rv, version)>0)
                {
                    //download
                    u = url + "/update/"+core_name+".dll";
                    string filename = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) + "\\" + core_name + ".dll";
                    //Console.WriteLine("...."+filename);

                    //this.label1.Text = "0%";
                                        
                    client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(ProgressChanged);
                    client.DownloadFileCompleted += new AsyncCompletedEventHandler(Completed);
                    client.DownloadFileAsync(new Uri(u), filename);
                }
                else
                {
                    SetProgress(100);
                    CloseOnceTime();
                }
            }
            catch (WebException)
            {
                //Console.WriteLine(e.Message);
                //Console.WriteLine(e.StackTrace);
                CloseOnceTime();
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
                //Console.WriteLine(e.Message);
                //Console.WriteLine(e.StackTrace);
            }
        }

        private void ProgressChanged(object sender, DownloadProgressChangedEventArgs e)
        {
            SetProgress(50 + e.ProgressPercentage / 2);
        }

        private void Completed(object sender, AsyncCompletedEventArgs e)
        {
            SetProgress(100);
            CloseOnceTime();
        }

        private void DownloadSplash()
        {
            string splash = xpath + "//tmp//" + "splash.png";
            //File fisplash = new File(splash);
            bool dl = false;
            try
            {
                if (!File.Exists(splash))
                {
                    dl = true;
                }
                else
                {
                    //DateTime dt = File.GetLastWriteTime(splash);
                    string rmd = null;
                    //string curtime = dt.GetDateTimeFormats('s')[0].ToString().Replace("T", " ");

                    //curtime = md5;

                    string u = url+"/logo";

                    WebRequest req = WebRequest.Create(u);
                    req.Method = "GET";
                    WebResponse res = req.GetResponse();

                    Encoding resEncoding = Encoding.GetEncoding("utf-8");
                    StreamReader reader = new StreamReader(res.GetResponseStream(), resEncoding);

                    rmd = reader.ReadLine();

                    reader.Close();
                    res.Close();

                    if (!md5.Equals(rmd))
                    {
                        dl = true;
                    }
                    else
                    {
                        dl = false;
                    }
                }

                if (dl) //need dl
                {
                    string splashtmp = xpath + "//tmp//splash.tmp";
                    if (File.Exists(splashtmp))
                    {
                        File.Delete(splashtmp);
                    }

                    string u = url + "/logo/splash.png";
                    //string filename = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) + "\\splash.tmp";
                    //Console.WriteLine("...."+filename);

                    //this.label1.Text = "0%";
                    //this.client = new WebClient();
                    //client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(ProgressChanged);
                    this.client.DownloadFileCompleted += new AsyncCompletedEventHandler(SCompleted);
                    this.client.DownloadFileAsync(new Uri(u), splashtmp);
                }
            }
            catch (Exception e)
            {
                //MessageBox.Show(e.Message);
            }
        }

        private void SCompleted(object sender, AsyncCompletedEventArgs e)
        {
            string splashtmp = xpath + "//tmp//splash.tmp";
            string splashnew = xpath + "//tmp//splash.new";
            if (File.Exists(splashnew))
            {
                File.Delete(splashnew);
            }
            File.Move(splashtmp, splashnew);
        }


        private void Downloader_Load(object sender, EventArgs e)
        {
            try
            {
                xpath = AppDomain.CurrentDomain.BaseDirectory;
                string splash = xpath + "//tmp//splash.png";
                string splashnew = xpath + "//tmp//splash.new";
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
                MessageBox.Show(ex.Message);
                //Console.WriteLine(ex.Message);
                //Console.WriteLine(ex.StackTrace);
            }

            try
            {
                this.client = new WebClient();
                AppDomain od = AppDomain.CreateDomain("TMP-DOMAIN");
                //od.SetData("srv", srv);
                SRPath srp = new SRPath();
                srp.CN = core_name;
                srp.XPath = xpath;

                od.SetData("srp", srp);
                //Console.WriteLine(">>> 之前");
                //CheckAssemblies(AppDomain.CurrentDomain);
                //CheckAssemblies(od);

                od.DoCallBack(() =>
                {
                    try
                    {
                        SRPath sx = AppDomain.CurrentDomain.GetData("srp") as SRPath;
                        SRVersion srv = new SRVersion();

                        if (File.Exists(sx.XPath + "//" + sx.CN + ".dll"))
                        {
                            FileVersionInfo info = FileVersionInfo.GetVersionInfo(sx.XPath + "//" + sx.CN + ".dll");
                            
                            //Assembly assembly = Assembly.Load(sx.CN);
                            //srv.Version = assembly.FullName.Split(',')[1].Substring(9);
                            srv.Version = info.FileVersion;

                            //version = assembly.FullName;
                            //SRVersion s = AppDomain.CurrentDomain.GetData("svr") as SRVersion;
                            //assembly.FullName.Split(',')[1].Substring(9);
                            
                            //Console.WriteLine("V1:" + AppDomain.CurrentDomain.FriendlyName.ToString());
                            //AppDomain.CurrentDomain.SetData("srv", srv);
                        }
                        else
                        {
                            srv.Version = "0.0.0.0";
                        }
                        //SRVersion s = AppDomain.CurrentDomain.GetData("svr") as SRVersion;
                        //assembly.FullName.Split(',')[1].Substring(9);
                        //od.SetData("srv", srv);
                        //Console.WriteLine("V0:" + od.FriendlyName.ToString());
                        //Console.WriteLine("V1:"+AppDomain.CurrentDomain.FriendlyName.ToString());
                        AppDomain.CurrentDomain.SetData("srv", srv);
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show(ex.Message);
                        //Application.Exit();
                    }
                    //srv.version = assembly.FullName.Split(',')[1].Substring(9);
                    //version = assembly.FullName.Split(',')[1].Substring(9);
                });

                //Console.WriteLine(">>> 之后");
                //CheckAssemblies(AppDomain.CurrentDomain);
                //CheckAssemblies(od);
                //Console.WriteLine("V2:" + od.FriendlyName.ToString());
                //Console.WriteLine("V3:" + AppDomain.CurrentDomain.FriendlyName.ToString());
                SRVersion s = od.GetData("srv") as SRVersion;
                version = s.Version;

                //version = od.GetData("version") as string;
                //od.SetData("srv", srv);
                //version = s.version;
                //Console.WriteLine(s.version);

                AppDomain.Unload(od);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
                Application.Exit();
                //Console.WriteLine(ex.Message);
                //Console.WriteLine(ex.StackTrace);
                //Console.WriteLine(ex.Source);
            }

            Thread th = new Thread(CheckingUpdate);
            th.Start();
        }
        
        private void CloseOnceTime()
        {
            if (cf)
            {
                //Console.WriteLine("EEE");
                cf = false;
                ManualCloseForm();
            }
        }


        public void SetProgress(int i)
        {
            dlg = delegate()
            {
                this.label1.Text = i.ToString() + "%";
            };
            this.BeginInvoke(dlg);
        }

        private void ManualCloseForm()
        {
            try
            {
                Assembly DllAssembly = Assembly.LoadFrom(core_name+".dll");
                Type[] DllTypes = DllAssembly.GetTypes();

                foreach (Type DllType in DllTypes)
                {
                    //Console.WriteLine(">>" + Namespace.Substring(Namespace.LastIndexOf(".")));
                    if (DllType.Namespace == Namespace && DllType.Name == Namespace.Substring(Namespace.LastIndexOf(".")+1))
                    {
                        form = (Form)(Activator.CreateInstance(DllType, new object[] { this }));
                        //form = (Form)(Activator.CreateInstance(DllType, new object[]{this, this.md5}));
                        break;
                    }
                }

                if (form != null)
                {
                    dlg = delegate()
                    {                        
                        for (int i = 0; i < 100; i++)
                        {
                            this.Opacity -= 0.01;
                            Thread.Sleep(8);
                        }
                        this.Hide();
                        form.Show();
                    };
                    this.BeginInvoke(dlg);

                    new Thread(DownloadSplash).Start();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error");
                dlg = delegate()
                {
                    if (form != null)
                    {
                        form.Close();
                    }
                    //this.Close();
                };
                this.BeginInvoke(dlg);
                Application.Exit();
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
                CloseOnceTime();
            }
            //base.OnFormClosing(e); 
            //return;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void button1_MouseEnter(object sender, EventArgs e)
        {
            button1.FlatAppearance.BorderColor = Color.Beige;
            //button1.FlatAppearance.BorderSize = 1;
            //button1.FlatStyle = FlatStyle.Popup;
        }

        private void button1_MouseLeave(object sender, EventArgs e)
        {
            button1.FlatAppearance.BorderColor = Color.Gray;
            //button1.FlatAppearance.BorderSize = 0;
            //button1.FlatStyle = FlatStyle.Flat;
        }
    }
}
