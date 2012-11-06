using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;

using System.Runtime.InteropServices;
using System.IO;
using System.Drawing.Imaging;
using DirectShowLib;
using DirectShowLib.DES;
using System.Reflection;
using System.Configuration;
using System.Management;
using System.Security.Cryptography;
using System.Text;
using System.Threading;
using System.Net;
using System.Globalization;

namespace xplayer
{
    /// <summary>
    ///
    /// </summary>
    public class XPlayer : System.Windows.Forms.Form
    {
        private System.Windows.Forms.MainMenu mainMenu1;
        private System.Windows.Forms.MenuItem menuItem1;
        private System.Windows.Forms.MenuItem menuItem4;
        private System.Windows.Forms.ImageList imageList1;
        private System.Windows.Forms.StatusBar statusBar1;
        private System.Windows.Forms.Timer timer1;
        private System.Windows.Forms.StatusBarPanel statusBarPanel1;
        private System.Windows.Forms.StatusBarPanel statusBarPanel2;
        private System.Windows.Forms.StatusBarPanel statusBarPanel3;
        private System.ComponentModel.IContainer components;

        private const int WM_APP = 0x8000;
        //private const int WM_GRAPHNOTIFY = WM_APP + 1;
        private const int WM_GRAPHNOTIFY = 0x0400 + 13;

        private IGraphBuilder m_objGraphBuilder = null;
        private IBasicAudio m_objBasicAudio = null;
        private IBasicVideo m_objBasicVideo = null;
        private IVideoWindow m_objVideoWindow = null;
        //private IMediaEvent m_objMediaEvent = null;
        private IMediaEventEx m_objMediaEventEx = null;
        private IMediaPosition m_objMediaPosition = null;
        private IMediaControl m_objMediaControl = null;
        private MenuItem menuItem5;
        private MenuItem menuItem6;
        private Screen screen;
        private ImageList imageList2;
        private ToolBar toolBar1;
        private ToolBar toolBar2;
        private ToolBarButton toolBarButton1;
        private ToolBarButton toolBarButton2;
        private ToolBarButton toolBarButton3;
        private ToolBarButton toolBarButton4;
        private ToolBarButton toolBarButton5;
        private ToolBarButton toolBarButton6;
        private ToolBarButton toolBarButton7;
        private ToolBarButton toolBarButton8;
        private Panel panel1;
        private Panel panel4;
        private Panel panel3;
        private TrackBar trackBar1;
        private ToolBarButton toolBarButton9;
        private Panel panel5;
        private ListView listView1;
        private ColumnHeader columnHeader1;
        enum MediaStatus { None, Stopped, Paused, Running };

        private MediaStatus m_CurrentStatus = MediaStatus.None;
        //private float[] factor;
        //private ArrayList items;
        private const string EXTS = "#.jpeg#.jpg#.mpg#.mpeg#.png#.bmp#";
        private OpenFileDialog openFileDialog;
        private SaveFileDialog saveFileDialog;
        private MenuItem menuItem2;
        private string currentFile = null;
        private string currentMedia = null;
        private MenuItem menuItem3;
        private ImageList imageList3;
        private MenuItem menuItem7;
        private MenuItem menuItem9;
        private MenuItem menuItem10;
        private string title = "X-Player";
        private ToolBarButton toolBarButton10;
        private MenuItem menuItem11;
        private ToolBarButton toolBarButton11;
        private ToolBarButton toolBarButton12;
        private Form pf = null;
        private MenuItem menuItem12;
        private PropertyGrid propertyGrid1;
        private ToolBarButton toolBarButton13;
        private ToolBarButton toolBarButton14;
        private ToolBarButton toolBarButton15;
        private MediaItem mi = null;
        private MenuItem menuItem13;
        private ListViewItem currentLVI = null;
        private bool registered = false;
        private string hardcode = null;
        private string license = null;
        private WebClient client = null;
        private MenuItem menuItem14;
        private MenuItem menuItem8;
        private MenuItem menuItem15;
        private MenuItem menuItem16;
        private MenuItem menuItem17;
        private string md5 = null;

        public XPlayer(Form pf, string md5)
        {
            this.pf = pf;
            this.md5 = md5;
            //Console.WriteLine("F1");
            try
            {
                InitializeComponent();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine(e.StackTrace);
            }
            
            //int count = this.Controls.Count * 2 + 2;
            //factor = new float[count];
            //int i = 0;
            //factor[i++] = Size.Width;
            //factor[i++] = Size.Height;
            //foreach (Control ctrl in this.Controls)
            //{
            //    factor[i++] = ctrl.Location.X / (float)Size.Width;
            //    factor[i++] = ctrl.Location.Y / (float)Size.Height;
            //    ctrl.Tag = ctrl.Size;
            //}
            //Tag = factor;

            //
            // TODO: F黦en Sie den Konstruktorcode nach dem Aufruf von InitializeComponent hinzu
            //
            //this.items = new ArrayList();
            //Console.WriteLine("F2");
            //ShowScrollBar((int)this.listView1.Handle, SB_VERT, 1);
            try
            {
                this.mi = new MediaItem();
                //Console.WriteLine("F3");
                this.title += " " + Assembly.GetExecutingAssembly().GetName().Version.ToString();
                this.Text = title;
                listView1.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);
                //Console.WriteLine("F4");
                this.toolBarButton6.Enabled = false;
                this.toolBarButton8.Enabled = false;
                this.toolBarButton9.Enabled = false;
                this.toolBarButton10.Enabled = false;
                //Console.WriteLine("F5");
                screen = new Screen(this);
                //Console.WriteLine("F6");
                UpdateStatusBar();
                UpdateToolBar();
                //Console.WriteLine("F7");
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
            //Console.WriteLine("F8");
        }

        protected override void Dispose(bool disposing)
        {
            imageList2.Images.Clear();
            listView1.Clear();
            //imageList2.Dispose();
            //listView1.Dispose();
            //GC.Collect();

            CleanUp();
            if (disposing)
            {
                if (components != null)
                {
                    components.Dispose();
                }
            }
            
            base.Dispose(disposing);

        }

        #region Windows Form Designer generated code
        /// <summary>
        /// Erforderliche Methode f黵 die Designerunterst黷zung. 
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor ge鋘dert werden.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(XPlayer));
            this.mainMenu1 = new System.Windows.Forms.MainMenu(this.components);
            this.menuItem1 = new System.Windows.Forms.MenuItem();
            this.menuItem4 = new System.Windows.Forms.MenuItem();
            this.menuItem5 = new System.Windows.Forms.MenuItem();
            this.menuItem14 = new System.Windows.Forms.MenuItem();
            this.menuItem15 = new System.Windows.Forms.MenuItem();
            this.menuItem16 = new System.Windows.Forms.MenuItem();
            this.menuItem17 = new System.Windows.Forms.MenuItem();
            this.menuItem8 = new System.Windows.Forms.MenuItem();
            this.menuItem11 = new System.Windows.Forms.MenuItem();
            this.menuItem2 = new System.Windows.Forms.MenuItem();
            this.menuItem3 = new System.Windows.Forms.MenuItem();
            this.menuItem9 = new System.Windows.Forms.MenuItem();
            this.menuItem10 = new System.Windows.Forms.MenuItem();
            this.menuItem12 = new System.Windows.Forms.MenuItem();
            this.menuItem6 = new System.Windows.Forms.MenuItem();
            this.menuItem13 = new System.Windows.Forms.MenuItem();
            this.menuItem7 = new System.Windows.Forms.MenuItem();
            this.imageList1 = new System.Windows.Forms.ImageList(this.components);
            this.statusBar1 = new System.Windows.Forms.StatusBar();
            this.statusBarPanel1 = new System.Windows.Forms.StatusBarPanel();
            this.statusBarPanel2 = new System.Windows.Forms.StatusBarPanel();
            this.statusBarPanel3 = new System.Windows.Forms.StatusBarPanel();
            this.timer1 = new System.Windows.Forms.Timer(this.components);
            this.imageList2 = new System.Windows.Forms.ImageList(this.components);
            this.toolBar1 = new System.Windows.Forms.ToolBar();
            this.toolBarButton11 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton1 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton2 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton3 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton12 = new System.Windows.Forms.ToolBarButton();
            this.imageList3 = new System.Windows.Forms.ImageList(this.components);
            this.toolBar2 = new System.Windows.Forms.ToolBar();
            this.toolBarButton4 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton5 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton6 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton7 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton8 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton9 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton10 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton13 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton14 = new System.Windows.Forms.ToolBarButton();
            this.toolBarButton15 = new System.Windows.Forms.ToolBarButton();
            this.panel1 = new System.Windows.Forms.Panel();
            this.panel4 = new System.Windows.Forms.Panel();
            this.listView1 = new System.Windows.Forms.ListView();
            this.columnHeader1 = ((System.Windows.Forms.ColumnHeader)(new System.Windows.Forms.ColumnHeader()));
            this.panel5 = new System.Windows.Forms.Panel();
            this.propertyGrid1 = new System.Windows.Forms.PropertyGrid();
            this.panel3 = new System.Windows.Forms.Panel();
            this.trackBar1 = new System.Windows.Forms.TrackBar();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel2)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel3)).BeginInit();
            this.panel1.SuspendLayout();
            this.panel4.SuspendLayout();
            this.panel5.SuspendLayout();
            this.panel3.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.trackBar1)).BeginInit();
            this.SuspendLayout();
            // 
            // mainMenu1
            // 
            this.mainMenu1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem1,
            this.menuItem5,
            this.menuItem6});
            resources.ApplyResources(this.mainMenu1, "mainMenu1");
            // 
            // menuItem1
            // 
            resources.ApplyResources(this.menuItem1, "menuItem1");
            this.menuItem1.Index = 0;
            this.menuItem1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem4});
            // 
            // menuItem4
            // 
            resources.ApplyResources(this.menuItem4, "menuItem4");
            this.menuItem4.Index = 0;
            this.menuItem4.Click += new System.EventHandler(this.menuItem4_Click);
            // 
            // menuItem5
            // 
            resources.ApplyResources(this.menuItem5, "menuItem5");
            this.menuItem5.Index = 1;
            this.menuItem5.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem14,
            this.menuItem8,
            this.menuItem11,
            this.menuItem2,
            this.menuItem3,
            this.menuItem9,
            this.menuItem10,
            this.menuItem12});
            // 
            // menuItem14
            // 
            resources.ApplyResources(this.menuItem14, "menuItem14");
            this.menuItem14.Index = 0;
            this.menuItem14.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem15,
            this.menuItem16,
            this.menuItem17});
            // 
            // menuItem15
            // 
            resources.ApplyResources(this.menuItem15, "menuItem15");
            this.menuItem15.Index = 0;
            this.menuItem15.Click += new System.EventHandler(this.menuItem15_Click);
            // 
            // menuItem16
            // 
            resources.ApplyResources(this.menuItem16, "menuItem16");
            this.menuItem16.Index = 1;
            this.menuItem16.Click += new System.EventHandler(this.menuItem16_Click);
            // 
            // menuItem17
            // 
            resources.ApplyResources(this.menuItem17, "menuItem17");
            this.menuItem17.Index = 2;
            this.menuItem17.Click += new System.EventHandler(this.menuItem17_Click);
            // 
            // menuItem8
            // 
            resources.ApplyResources(this.menuItem8, "menuItem8");
            this.menuItem8.Index = 1;
            // 
            // menuItem11
            // 
            resources.ApplyResources(this.menuItem11, "menuItem11");
            this.menuItem11.Index = 2;
            this.menuItem11.Click += new System.EventHandler(this.menuItem11_Click);
            // 
            // menuItem2
            // 
            resources.ApplyResources(this.menuItem2, "menuItem2");
            this.menuItem2.Index = 3;
            this.menuItem2.Click += new System.EventHandler(this.menuItem2_Click);
            // 
            // menuItem3
            // 
            resources.ApplyResources(this.menuItem3, "menuItem3");
            this.menuItem3.Index = 4;
            this.menuItem3.Click += new System.EventHandler(this.menuItem3_Click);
            // 
            // menuItem9
            // 
            resources.ApplyResources(this.menuItem9, "menuItem9");
            this.menuItem9.Index = 5;
            this.menuItem9.Click += new System.EventHandler(this.menuItem9_Click);
            // 
            // menuItem10
            // 
            resources.ApplyResources(this.menuItem10, "menuItem10");
            this.menuItem10.Index = 6;
            this.menuItem10.Click += new System.EventHandler(this.menuItem10_Click);
            // 
            // menuItem12
            // 
            resources.ApplyResources(this.menuItem12, "menuItem12");
            this.menuItem12.Index = 7;
            this.menuItem12.Click += new System.EventHandler(this.menuItem12_Click);
            // 
            // menuItem6
            // 
            resources.ApplyResources(this.menuItem6, "menuItem6");
            this.menuItem6.Index = 2;
            this.menuItem6.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem13,
            this.menuItem7});
            // 
            // menuItem13
            // 
            resources.ApplyResources(this.menuItem13, "menuItem13");
            this.menuItem13.Index = 0;
            this.menuItem13.Click += new System.EventHandler(this.menuItem13_Click);
            // 
            // menuItem7
            // 
            resources.ApplyResources(this.menuItem7, "menuItem7");
            this.menuItem7.Index = 1;
            this.menuItem7.Click += new System.EventHandler(this.menuItem7_Click);
            // 
            // imageList1
            // 
            this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
            this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
            this.imageList1.Images.SetKeyName(0, "list.png");
            this.imageList1.Images.SetKeyName(1, "open.png");
            this.imageList1.Images.SetKeyName(2, "save.png");
            this.imageList1.Images.SetKeyName(3, "insert.png");
            this.imageList1.Images.SetKeyName(4, "delete.png");
            this.imageList1.Images.SetKeyName(5, "screen.png");
            this.imageList1.Images.SetKeyName(6, "option.png");
            this.imageList1.Images.SetKeyName(7, "new.png");
            this.imageList1.Images.SetKeyName(8, "up-arrow-t.png");
            this.imageList1.Images.SetKeyName(9, "down-arrow-t.png");
            // 
            // statusBar1
            // 
            resources.ApplyResources(this.statusBar1, "statusBar1");
            this.statusBar1.Name = "statusBar1";
            this.statusBar1.Panels.AddRange(new System.Windows.Forms.StatusBarPanel[] {
            this.statusBarPanel1,
            this.statusBarPanel2,
            this.statusBarPanel3});
            this.statusBar1.ShowPanels = true;
            // 
            // statusBarPanel1
            // 
            resources.ApplyResources(this.statusBarPanel1, "statusBarPanel1");
            this.statusBarPanel1.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Spring;
            this.statusBarPanel1.BorderStyle = System.Windows.Forms.StatusBarPanelBorderStyle.None;
            // 
            // statusBarPanel2
            // 
            resources.ApplyResources(this.statusBarPanel2, "statusBarPanel2");
            this.statusBarPanel2.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Contents;
            // 
            // statusBarPanel3
            // 
            resources.ApplyResources(this.statusBarPanel3, "statusBarPanel3");
            this.statusBarPanel3.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Contents;
            // 
            // timer1
            // 
            this.timer1.Enabled = true;
            this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
            // 
            // imageList2
            // 
            this.imageList2.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
            resources.ApplyResources(this.imageList2, "imageList2");
            this.imageList2.TransparentColor = System.Drawing.Color.Transparent;
            // 
            // toolBar1
            // 
            resources.ApplyResources(this.toolBar1, "toolBar1");
            this.toolBar1.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
            this.toolBarButton11,
            this.toolBarButton1,
            this.toolBarButton2,
            this.toolBarButton3,
            this.toolBarButton12});
            this.toolBar1.ImageList = this.imageList3;
            this.toolBar1.Name = "toolBar1";
            this.toolBar1.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.toolBar1_ButtonClick);
            // 
            // toolBarButton11
            // 
            resources.ApplyResources(this.toolBarButton11, "toolBarButton11");
            this.toolBarButton11.Name = "toolBarButton11";
            // 
            // toolBarButton1
            // 
            resources.ApplyResources(this.toolBarButton1, "toolBarButton1");
            this.toolBarButton1.Name = "toolBarButton1";
            // 
            // toolBarButton2
            // 
            resources.ApplyResources(this.toolBarButton2, "toolBarButton2");
            this.toolBarButton2.Name = "toolBarButton2";
            // 
            // toolBarButton3
            // 
            resources.ApplyResources(this.toolBarButton3, "toolBarButton3");
            this.toolBarButton3.Name = "toolBarButton3";
            // 
            // toolBarButton12
            // 
            resources.ApplyResources(this.toolBarButton12, "toolBarButton12");
            this.toolBarButton12.Name = "toolBarButton12";
            // 
            // imageList3
            // 
            this.imageList3.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList3.ImageStream")));
            this.imageList3.TransparentColor = System.Drawing.Color.Transparent;
            this.imageList3.Images.SetKeyName(0, "play.png");
            this.imageList3.Images.SetKeyName(1, "pause.png");
            this.imageList3.Images.SetKeyName(2, "stop.png");
            this.imageList3.Images.SetKeyName(3, "prev.png");
            this.imageList3.Images.SetKeyName(4, "next.png");
            // 
            // toolBar2
            // 
            resources.ApplyResources(this.toolBar2, "toolBar2");
            this.toolBar2.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
            this.toolBarButton4,
            this.toolBarButton5,
            this.toolBarButton6,
            this.toolBarButton7,
            this.toolBarButton8,
            this.toolBarButton9,
            this.toolBarButton10,
            this.toolBarButton13,
            this.toolBarButton14,
            this.toolBarButton15});
            this.toolBar2.ImageList = this.imageList1;
            this.toolBar2.Name = "toolBar2";
            this.toolBar2.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.toolBar2_ButtonClick);
            // 
            // toolBarButton4
            // 
            resources.ApplyResources(this.toolBarButton4, "toolBarButton4");
            this.toolBarButton4.Name = "toolBarButton4";
            // 
            // toolBarButton5
            // 
            resources.ApplyResources(this.toolBarButton5, "toolBarButton5");
            this.toolBarButton5.Name = "toolBarButton5";
            // 
            // toolBarButton6
            // 
            resources.ApplyResources(this.toolBarButton6, "toolBarButton6");
            this.toolBarButton6.Name = "toolBarButton6";
            // 
            // toolBarButton7
            // 
            resources.ApplyResources(this.toolBarButton7, "toolBarButton7");
            this.toolBarButton7.Name = "toolBarButton7";
            // 
            // toolBarButton8
            // 
            resources.ApplyResources(this.toolBarButton8, "toolBarButton8");
            this.toolBarButton8.Name = "toolBarButton8";
            // 
            // toolBarButton9
            // 
            resources.ApplyResources(this.toolBarButton9, "toolBarButton9");
            this.toolBarButton9.Name = "toolBarButton9";
            // 
            // toolBarButton10
            // 
            resources.ApplyResources(this.toolBarButton10, "toolBarButton10");
            this.toolBarButton10.Name = "toolBarButton10";
            // 
            // toolBarButton13
            // 
            resources.ApplyResources(this.toolBarButton13, "toolBarButton13");
            this.toolBarButton13.Name = "toolBarButton13";
            this.toolBarButton13.Style = System.Windows.Forms.ToolBarButtonStyle.Separator;
            // 
            // toolBarButton14
            // 
            resources.ApplyResources(this.toolBarButton14, "toolBarButton14");
            this.toolBarButton14.Name = "toolBarButton14";
            this.toolBarButton14.Style = System.Windows.Forms.ToolBarButtonStyle.ToggleButton;
            // 
            // toolBarButton15
            // 
            resources.ApplyResources(this.toolBarButton15, "toolBarButton15");
            this.toolBarButton15.Name = "toolBarButton15";
            // 
            // panel1
            // 
            resources.ApplyResources(this.panel1, "panel1");
            this.panel1.Controls.Add(this.panel4);
            this.panel1.Controls.Add(this.panel3);
            this.panel1.Name = "panel1";
            // 
            // panel4
            // 
            resources.ApplyResources(this.panel4, "panel4");
            this.panel4.Controls.Add(this.listView1);
            this.panel4.Controls.Add(this.panel5);
            this.panel4.Name = "panel4";
            // 
            // listView1
            // 
            resources.ApplyResources(this.listView1, "listView1");
            this.listView1.AllowDrop = true;
            this.listView1.BackColor = System.Drawing.SystemColors.Info;
            this.listView1.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1});
            this.listView1.FullRowSelect = true;
            this.listView1.GridLines = true;
            this.listView1.HeaderStyle = System.Windows.Forms.ColumnHeaderStyle.None;
            this.listView1.Name = "listView1";
            this.listView1.ShowItemToolTips = true;
            this.listView1.SmallImageList = this.imageList2;
            this.listView1.UseCompatibleStateImageBehavior = false;
            this.listView1.View = System.Windows.Forms.View.Details;
            this.listView1.SelectedIndexChanged += new System.EventHandler(this.listView1_SelectedIndexChanged);
            this.listView1.DragDrop += new System.Windows.Forms.DragEventHandler(this.listView1_DragDrop);
            this.listView1.DragEnter += new System.Windows.Forms.DragEventHandler(this.listView1_DragEnter);
            this.listView1.DoubleClick += new System.EventHandler(this.listView1_DoubleClick);
            this.listView1.Resize += new System.EventHandler(this.listView1_Resize);
            // 
            // columnHeader1
            // 
            resources.ApplyResources(this.columnHeader1, "columnHeader1");
            // 
            // panel5
            // 
            resources.ApplyResources(this.panel5, "panel5");
            this.panel5.BackColor = System.Drawing.SystemColors.ScrollBar;
            this.panel5.Controls.Add(this.propertyGrid1);
            this.panel5.Name = "panel5";
            // 
            // propertyGrid1
            // 
            resources.ApplyResources(this.propertyGrid1, "propertyGrid1");
            this.propertyGrid1.Name = "propertyGrid1";
            // 
            // panel3
            // 
            resources.ApplyResources(this.panel3, "panel3");
            this.panel3.Controls.Add(this.trackBar1);
            this.panel3.Name = "panel3";
            // 
            // trackBar1
            // 
            resources.ApplyResources(this.trackBar1, "trackBar1");
            this.trackBar1.BackColor = System.Drawing.SystemColors.Control;
            this.trackBar1.Maximum = 200;
            this.trackBar1.Name = "trackBar1";
            this.trackBar1.TabStop = false;
            this.trackBar1.TickStyle = System.Windows.Forms.TickStyle.None;
            this.trackBar1.Scroll += new System.EventHandler(this.trackBar1_Scroll);
            // 
            // XPlayer
            // 
            resources.ApplyResources(this, "$this");
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.toolBar2);
            this.Controls.Add(this.toolBar1);
            this.Controls.Add(this.statusBar1);
            this.Menu = this.mainMenu1;
            this.Name = "XPlayer";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.XPlayer_FormClosing);
            this.Load += new System.EventHandler(this.XPlayer_Load);
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel2)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel3)).EndInit();
            this.panel1.ResumeLayout(false);
            this.panel4.ResumeLayout(false);
            this.panel5.ResumeLayout(false);
            this.panel3.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.trackBar1)).EndInit();
            this.ResumeLayout(false);

        }
        #endregion


        private void CleanUp()
        {
            if (m_objMediaControl != null)
                m_objMediaControl.Stop();

            m_CurrentStatus = MediaStatus.None;

            if (m_objMediaEventEx != null)
                m_objMediaEventEx.SetNotifyWindow(IntPtr.Zero, 0, IntPtr.Zero);

            if (m_objVideoWindow != null)
            {
                m_objVideoWindow.put_Visible(OABool.False); ;
                m_objVideoWindow.put_Owner(IntPtr.Zero);
                //m_objVideoWindow.put_MessageDrain(IntPtr.Zero);
            }

            if (m_objMediaControl != null) m_objMediaControl = null;
            if (m_objMediaPosition != null) m_objMediaPosition = null;
            if (m_objMediaEventEx != null) m_objMediaEventEx = null;
            //if (m_objMediaEvent != null) m_objMediaEvent = null;
            if (m_objVideoWindow != null) m_objVideoWindow = null;
            if (m_objBasicAudio != null) m_objBasicAudio = null;
            if (m_objGraphBuilder != null) m_objGraphBuilder = null;
            /**
            this.trackBar1.Value = 0;
            toolBarButton1.ImageIndex = 5;
            toolBarButton1.Enabled = false;
            toolBarButton2.ImageIndex = 6;
            toolBarButton2.Enabled = false;
            toolBarButton3.ImageIndex = 7;
            toolBarButton3.Enabled = false;
            toolBarButton11.ImageIndex = 8;
            toolBarButton11.Enabled = false;
            toolBarButton12.ImageIndex = 9;
            toolBarButton12.Enabled = false;**/
        }

        private void menuItem4_Click(object sender, System.EventArgs e)
        {
            this.Close();
        }

        public void sizeChanged(object sender, System.EventArgs e)
        {
            //int i = 2;
            //foreach (Control ctrl in this.Controls)
            //{
            //    ctrl.Left = (int)(Size.Width * factor[i++]);
            //    ctrl.Top = (int)(Size.Height * factor[i++]);
            //    ctrl.Width = (int)(Size.Width / (float)factor[0] * ((Size)ctrl.Tag).Width);
            //    ctrl.Height = (int)(Size.Height / (float)factor[1] * ((Size)ctrl.Tag).Height);
            //}
            //Console.WriteLine("U1");
            if (m_objVideoWindow != null)
            {
                m_objVideoWindow.SetWindowPosition(screen.ClientRectangle.Left,
                    screen.ClientRectangle.Top,
                    screen.ClientRectangle.Width,
                    screen.ClientRectangle.Height);

              //  m_objBasicVideo.SetDestinationPosition(0,
             //                0,
              //              100,
               //              100);
            }
            //Console.WriteLine("U2");
        }

        private void toolBar1_ButtonClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
        {
            switch (toolBar1.Buttons.IndexOf(e.Button))
            {
                case 0:
                    //prev
                    play(this.listView1.Items[this.currentLVI.Index - 1]);
                    break;

                case 1: m_objMediaControl.Run();
                    m_CurrentStatus = MediaStatus.Running;
                    break;

                case 2: m_objMediaControl.Pause();
                    m_CurrentStatus = MediaStatus.Paused;
                    break;

                case 3: m_objMediaControl.Stop();
                    m_objMediaPosition.put_CurrentPosition(0.0);
                    m_CurrentStatus = MediaStatus.Stopped;
                    //CleanUp();
                    break;

                case 4:
                    //next
                    play(this.listView1.Items[this.currentLVI.Index + 1]);
                    break;
            }

            UpdateStatusBar();
            UpdateToolBar();
        }

        protected override void WndProc(ref Message m)
        {
            //  MessageBox.Show(m.Msg.ToString("X"));  

            //Console.WriteLine("K1");
            switch (m.Msg)
            {
                case WM_GRAPHNOTIFY:
                    EventCode lEventCode;
                    IntPtr lParam1 = IntPtr.Zero;
                    IntPtr lParam2 = IntPtr.Zero;

                    int hr = 0;

                    // Make sure that we don't access the media event interface
                    // after it has already been released.
                    if (this.m_objMediaEventEx == null)
                        return;

                    // Process all queued events
                    while (this.m_objMediaEventEx!=null&&this.m_objMediaEventEx.GetEvent(out lEventCode, out lParam1, out lParam2, 0) == 0)
                    {
                        // Free memory associated with callback, since we're not using it
                        hr = this.m_objMediaEventEx.FreeEventParams(lEventCode, lParam1, lParam2);

                        // If this is the end of the clip, reset to beginning
                        if (lEventCode == EventCode.Complete)
                        {
                            CleanUp();
                            //m_objMediaControl.Stop();

                            //DsLong pos = new DsLong(0);
                            // Reset to first frame of movie
                            /**
                        hr = this.mediaSeeking.SetPositions(pos, AMSeekingSeekingFlags.AbsolutePositioning, 
                          null, AMSeekingSeekingFlags.NoPositioning);
                             * */
                            /**
                            hr = m_objMediaPosition.put_CurrentPosition(0.0);
                            if (hr < 0)
                            {
                                // Some custom filters (like the Windows CE MIDI filter)
                                // may not implement seeking interfaces (IMediaSeeking)
                                // to allow seeking to the start.  In that case, just stop
                                // and restart for the same effect.  This should not be
                                // necessary in most cases.
                                hr = this.m_objMediaControl.Stop();
                                hr = this.m_objMediaControl.Run();
                            }**/

                            //m_CurrentStatus = MediaStatus.None;

                            bool sp = false;
                            string ssp = ConfigurationManager.AppSettings["SP"];
                            if (ssp != null)
                            {
                                sp = Boolean.Parse(ssp);
                            }

                            string path = null;
                            if (sp)
                            {
                                path = ConfigurationManager.AppSettings["PATH"];
                                //load image
                                try
                                {
                                    Image bi = Image.FromFile(path);
                                    this.screen.BackgroundImage = bi;
                                }
                                catch (Exception)
                                {
                                    this.screen.BackColor = Color.Black;
                                }
                            }
                            else
                            {
                                this.screen.BackColor = Color.Black;
                            }
                            UpdateStatusBar();
                            UpdateToolBar();
                        }
                    }

                    if (this.m_objVideoWindow != null)
                    {
                        this.m_objVideoWindow.NotifyOwnerMessage(m.HWnd, m.Msg, m.WParam, m.LParam);
                    }

                    base.WndProc(ref m);
                    break;
                default:
                    base.WndProc(ref m);
                    break;
            }
            //Console.WriteLine("K2");
        }

        private void timer1_Tick(object sender, System.EventArgs e)
        {
            if (m_CurrentStatus == MediaStatus.Running)
            {
                UpdateStatusBar();
            }
        }

        private void UpdateStatusBar()
        {
            if (currentLVI == null)
            {
                statusBarPanel1.Text = "Stopped";
                statusBarPanel2.Text = "00:00:00";
                statusBarPanel3.Text = "00:00:00";
                return;
            }

            if (isImage(currentLVI.Text))
            {
                statusBarPanel1.Text = "Running - " + this.currentMedia;
            }
            else
            {
                switch (m_CurrentStatus)
                {
                    case MediaStatus.None: statusBarPanel1.Text = "Stopped"; break;
                    case MediaStatus.Paused: statusBarPanel1.Text = "Paused - " + this.currentMedia; break;
                    case MediaStatus.Running: statusBarPanel1.Text = "Running - " + this.currentMedia; break;
                    case MediaStatus.Stopped: statusBarPanel1.Text = "Stopped"; break;
                }
            }

            if (m_objMediaPosition != null)
            {
                double duration = 0.0;
                m_objMediaPosition.get_Duration(out duration);
                int s = (int)duration;
                int total = s;
                int h = s / 3600;
                int m = (s - (h * 3600)) / 60;
                s = s - (h * 3600 + m * 60);

                statusBarPanel3.Text = String.Format("{0:D2}:{1:D2}:{2:D2}", h, m, s);

                double cp = 0.0;
                m_objMediaPosition.get_CurrentPosition(out cp);
                s = (int)cp;
                int current = s;
                h = s / 3600;
                m = (s - (h * 3600)) / 60;
                s = s - (h * 3600 + m * 60);

                statusBarPanel2.Text = String.Format("{0:D2}:{1:D2}:{2:D2}", h, m, s);

                this.trackBar1.Value = current * 100 * 2 / total;
            }
            else
            {
                statusBarPanel2.Text = "00:00:00";
                statusBarPanel3.Text = "00:00:00";
            }
        }

        private void UpdateToolBar()
        {
            if (this.currentLVI == null)
            {
                toolBarButton1.Enabled = false;
                toolBarButton2.Enabled = false;
                toolBarButton3.Enabled = false;
                toolBarButton11.Enabled = false;
                toolBarButton12.Enabled = false;
                trackBar1.Value = 0;
                trackBar1.Enabled = false;
                return;
            }

            if (isImage(this.currentLVI.Text))
            {
                //Console.WriteLine("HERE");
                toolBarButton1.Enabled = false;
                toolBarButton2.Enabled = false;
                toolBarButton3.Enabled = false;
                trackBar1.Value = 0;
                trackBar1.Enabled = false;
                int idx = this.currentLVI.Index;

                if (idx == 0)
                {
                    this.toolBarButton11.Enabled = false;
                }
                else
                {
                    this.toolBarButton11.Enabled = true;
                }

                if (idx == this.listView1.Items.Count - 1)
                {
                    this.toolBarButton12.Enabled = false;
                }
                else
                {
                    this.toolBarButton12.Enabled = true;
                }
            }
            else
            {
                switch (m_CurrentStatus)
                {
                    case MediaStatus.None:
                        //toolBarButton1.ImageIndex = 5;
                        toolBarButton1.Enabled = false;
                        //toolBarButton2.ImageIndex = 6;
                        toolBarButton2.Enabled = false;
                        //toolBarButton3.ImageIndex = 7;
                        toolBarButton3.Enabled = false;
                        //toolBarButton11.ImageIndex = 8;
                        toolBarButton11.Enabled = false;
                        //toolBarButton12.ImageIndex = 9;
                        toolBarButton12.Enabled = false;
                        trackBar1.Value = 0;
                        trackBar1.Enabled = false;
                        break;

                    case MediaStatus.Paused:
                        //toolBarButton1.ImageIndex = 0;
                        toolBarButton1.Enabled = true;
                        //toolBarButton2.ImageIndex = 6;
                        toolBarButton2.Enabled = false;
                        //toolBarButton3.ImageIndex = 2;
                        toolBarButton3.Enabled = true;
                        trackBar1.Enabled = true;
                        break;

                    case MediaStatus.Running:
                        //toolBarButton1.ImageIndex = 5;
                        toolBarButton1.Enabled = false;
                        //toolBarButton2.ImageIndex = 1;
                        toolBarButton2.Enabled = true;
                        //toolBarButton3.ImageIndex = 2;
                        toolBarButton3.Enabled = true;
                        trackBar1.Enabled = true;
                        int idx = this.currentLVI.Index;

                        if (idx == 0)
                        {
                            this.toolBarButton11.Enabled = false;
                        }
                        else
                        {
                            this.toolBarButton11.Enabled = true;
                        }

                        if (idx == this.listView1.Items.Count - 1)
                        {
                            this.toolBarButton12.Enabled = false;
                        }
                        else
                        {
                            this.toolBarButton12.Enabled = true;
                        }
                        break;

                    case MediaStatus.Stopped:
                        //toolBarButton1.ImageIndex = 0;
                        toolBarButton1.Enabled = true;
                        //toolBarButton2.ImageIndex = 6;
                        toolBarButton2.Enabled = false;
                        //toolBarButton3.ImageIndex = 7;
                        toolBarButton3.Enabled = false;
                        trackBar1.Enabled = true;
                        break;
                }
            }
        }

        public void reserveScreen()
        {
            if (screen.Visible == true)
            {
                this.toolBarButton14.Pushed = false;
                screen.Hide();
            }
            else
            {
                this.toolBarButton14.Pushed = true;
                screen.Show();
                screen.BringToFront();
            }
        }

        private void insertItems()
        {
                openFileDialog = new OpenFileDialog();
                openFileDialog.Multiselect = true;
                openFileDialog.Filter = "Media & Image Files (jpeg, bmp, png, mpeg)|*.jpg;*.jpeg;*.bmp;*.png;*.mpg;*.mpeg";

                if (DialogResult.OK == openFileDialog.ShowDialog())
                {
                    foreach (String fn in openFileDialog.FileNames)
                    {
                        addItem(fn);
                        this.toolBarButton6.Enabled = true;
                    }
                }
        }

        private void deleteItems()
        {
            for (int i = this.listView1.SelectedItems.Count - 1; i >= 0; i--)
            {
                ListViewItem item = this.listView1.SelectedItems[i];
                this.listView1.Items.Remove(item);
                int idx = item.ImageIndex;
                this.imageList2.Images.RemoveAt(idx);

                /**
                //item 后的所有ImageIndex都要-1
                foreach (ListViewItem itx in this.listView1.Items)
                {
                    if (itx.ImageIndex > idx)
                    {
                        itx.ImageIndex = itx.ImageIndex - 1;
                    }
                }**/
                
                if (this.currentFile == null && this.listView1.Items.Count == 0)
                {
                    this.toolBarButton6.Enabled = false;
                }
                else
                {
                    this.toolBarButton6.Enabled = true;
                }
            }

            //refresh
            for (int i = 0; i < this.listView1.Items.Count; i++)
            {
                this.listView1.Items[i].ImageIndex = this.listView1.Items[i].ImageIndex;
            }
            //this.imageList2.Images.Clear();
        }

        private void toolBar2_ButtonClick(object sender, ToolBarButtonClickEventArgs e)
        {
            switch (toolBar2.Buttons.IndexOf(e.Button))
            {
                case 0:
                    if (this.toolBarButton6.Enabled != true || (this.toolBarButton6.Enabled == true && MessageBox.Show("Current List Changed, Give Up?", "Information ", MessageBoxButtons.OKCancel, MessageBoxIcon.Information, MessageBoxDefaultButton.Button2) == DialogResult.OK))
                    {
                        this.listView1.Items.Clear();
                        this.imageList2.Images.Clear();
                        this.currentFile = null;
                        this.toolBarButton6.Enabled = false;
                        break;
                    }
                    break;
                case 1:
                    if (this.toolBarButton6.Enabled != true || (this.toolBarButton6.Enabled == true && MessageBox.Show("Current List Changed, Give Up?", "Information ", MessageBoxButtons.OKCancel, MessageBoxIcon.Information, MessageBoxDefaultButton.Button2) == DialogResult.OK))
                    {
                        openFileDialog = new OpenFileDialog();
                        openFileDialog.Filter = "X-Player List|*.xpl";

                        if (DialogResult.OK == openFileDialog.ShowDialog())
                        {
                            this.listView1.Items.Clear();
                            this.imageList2.Images.Clear();
                            //
                            this.currentFile = openFileDialog.FileName;
                            StreamReader sr = File.OpenText(this.currentFile);
                            string s = "";
                            while ((s = sr.ReadLine()) != null)
                            {
                                addItem(s);
                            }
                            sr.Close();
                            this.toolBarButton6.Enabled = false;
                            this.Text = title + " - [" + this.currentFile + "]";
                        }
                    }
                    break;

                case 2:
                    if (this.currentFile == null)
                    {
                        saveFileDialog = new SaveFileDialog();
                        saveFileDialog.Filter = "X-Player List|*.xpl";

                        if (DialogResult.OK == saveFileDialog.ShowDialog())
                        {
                            this.currentFile = saveFileDialog.FileName;
                        }
                        else
                        {
                            break;
                        }
                    }

                    if (this.currentFile != null)
                    {
                        //save to file                        
                        StreamWriter sw = File.CreateText(this.currentFile);
                        foreach (ListViewItem lvi in listView1.Items)
                        {
                            sw.WriteLine(lvi.Text);
                        }
                        sw.Close();
                        this.toolBarButton6.Enabled = false;
                        this.Text = title + " - [" + this.currentFile + "]";
                    }
                    break;
                case 3:
                    insertItems();
                    break;
                case 4:
                    deleteItems();
                    break;
                case 5:
                    foreach (ListViewItem lvi in this.listView1.SelectedItems)
                    {
                        int idx = lvi.Index;
                        this.listView1.Items.RemoveAt(idx);
                        this.listView1.Items.Insert(idx-1, lvi);
                        //ListViewItem ulvi = this.listView1.Items[lvi.Index - 1];
                        //this.listView1.Items[lvi.Index - 1] = lvi;
                        //this.listView1.Items[lvi.Index + 1] = ulvi;
                    }
                    break;
                case 6:
                    foreach (ListViewItem lvi in this.listView1.SelectedItems)
                    {
                        int idx = lvi.Index;
                        this.listView1.Items.RemoveAt(idx);
                        this.listView1.Items.Insert(idx + 1, lvi);
                        //ListViewItem ulvi = this.listView1.Items[lvi.Index - 1];
                        //this.listView1.Items[lvi.Index - 1] = lvi;
                        //this.listView1.Items[lvi.Index + 1] = ulvi;
                    }
                    break;
                case 7:
                    break;
                case 8:
                    reserveScreen();
                    break;
                case 9:
                    (new Option(this.screen)).ShowDialog(this);
                    break;
            }
        }

        /**
        private UInt32 UnixStamp()
        {
            TimeSpan ts = DateTime.Now - TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1));
            return Convert.ToUInt32(ts.TotalSeconds);
        }**/

        private void ConfigureSampleGrabber(ISampleGrabber sampGrabber)
        {
            AMMediaType media;
            int hr;

            // Set the media type to Video/RBG24
            media = new AMMediaType();
            media.majorType = MediaType.Video;
            media.subType = MediaSubType.RGB24;
            media.formatType = FormatType.VideoInfo; //FormatType.VideoInfo2

            //Console.WriteLine(iPinOutSource.QueryAccept(media));

            hr = sampGrabber.SetMediaType(media);
            DsError.ThrowExceptionForHR(hr);

            DsUtils.FreeAMMediaType(media);
            media = null;

            //hr = sampGrabber.SetCallback(this.capture, 1);
            //DsError.ThrowExceptionForHR(hr);
        }
        
        private void addItem(string fn)
        {
            if (!this.registered && this.listView1.Items.Count == 5)
            {
                MessageBox.Show("Please register the application to add more items!", "Infomation");
                return;
            }

            if (!File.Exists(fn))
            {
                return;
            }
            //string tmp = null;
            //Bitmap bitmap = null;
            if (fn.ToLower().EndsWith(".mpeg") || fn.ToLower().EndsWith(".mpg"))
            {
                int hr;

                IBaseFilter ibfRenderer = null;
                ISampleGrabber sampGrabber = null;
                IBaseFilter capFilter = null;
                IPin iPinInFilter = null;
                IPin iPinOutFilter = null;
                IPin iPinInDest = null;

                try
                {
                    //tmp = Environment.CurrentDirectory + "\\temp\\tb-" + UnixStamp() + ".tmp";
                    Capture capture = new Capture(this, fn);

                    IFilterGraph2 m_FilterGraph = new FilterGraph() as IFilterGraph2;

                    sampGrabber = new SampleGrabber() as ISampleGrabber;
                    //IBaseFilter bf = (IBaseFilter) sb;

                    hr = m_FilterGraph.AddSourceFilter(fn, "Ds.NET FileFilter", out capFilter);
                    DsError.ThrowExceptionForHR(hr);

                    // Hopefully this will be the video pin
                    IPin iPinOutSource = DsFindPin.ByDirection(capFilter, PinDirection.Output, 0);

                    IBaseFilter baseGrabFlt = sampGrabber as IBaseFilter;
                    ConfigureSampleGrabber(sampGrabber);

                    iPinInFilter = DsFindPin.ByDirection(baseGrabFlt, PinDirection.Input, 0);
                    iPinOutFilter = DsFindPin.ByDirection(baseGrabFlt, PinDirection.Output, 0);

                    // Add the frame grabber to the graph
                    hr = m_FilterGraph.AddFilter(baseGrabFlt, "Ds.NET Grabber");
                    DsError.ThrowExceptionForHR(hr);

                    hr = m_FilterGraph.Connect(iPinOutSource, iPinInFilter);
                    DsError.ThrowExceptionForHR(hr);

                    // Get the default video renderer
                    ibfRenderer = (IBaseFilter)new NullRenderer();

                    // Add it to the graph
                    hr = m_FilterGraph.AddFilter(ibfRenderer, "Ds.NET VideoRendererDefault");
                    DsError.ThrowExceptionForHR(hr);
                    iPinInDest = DsFindPin.ByDirection(ibfRenderer, PinDirection.Input, 0);

                    // Connect the graph.  Many other filters automatically get added here                    
                    hr = m_FilterGraph.Connect(iPinOutFilter, iPinInDest);                    
                    DsError.ThrowExceptionForHR(hr);

                    capture.SaveSizeInfo(sampGrabber);

                    
                    //run to first frame

                    IMediaControl mediaCtrl2 = m_FilterGraph as IMediaControl;
                    
                    hr = mediaCtrl2.Run();
                    DsError.ThrowExceptionForHR(hr);

                    hr = mediaCtrl2.Pause();
                    DsError.ThrowExceptionForHR(hr);

                    
                    IMediaSeeking ims = m_FilterGraph as IMediaSeeking;
                    ims.SetTimeFormat(TimeFormat.MediaTime);
                    long dtr = 0;
                    ims.GetDuration(out dtr);
                    capture.setDtr(dtr);
                    //Console.WriteLine(frames);
                    
                    /**
                    Guid tf = new Guid();
                    IMediaSeeking ims = m_FilterGraph as IMediaSeeking;
                    ims.GetTimeFormat(out tf);
                    long dur = 0;
                    ims.SetTimeFormat(TimeFormat.MediaTime);
                    ims.GetDuration(out dur);
                    Console.WriteLine(">>>" + dur);
                    Console.WriteLine(tf);
                    if (tf == TimeFormat.MediaTime)
                    {
                        Console.WriteLine("111");
                    }
                    else
                    {
                        Console.WriteLine("222");
                    }**/

                    IMediaPosition m_objMediaPosition2 = m_FilterGraph as IMediaPosition;
                    //m_objMediaPosition2.
                    //double oo = 0.0;
                    //m_objMediaPosition2.get_Duration(out oo);
                    //int s = (int)oo;
                    //int total = s;
                    //int h = s / 3600;
                    //int m = (s - (h * 3600)) / 60;
                    //s = s - (h * 3600 + m * 60);

                    //Console.WriteLine(h+":"+m+":"+s);
                    hr = m_objMediaPosition2.put_CurrentPosition(0.0);
                    DsError.ThrowExceptionForHR(hr);

                    capture.FrameEvent += new Capture.ShowFrame(capture.CaptureDone);

                    //setcallback
                    sampGrabber.SetCallback(capture, 1);
                    //mediaCtrl2.Stop();
                    /**
                    IMediaDet imd = (IMediaDet)new MediaDet();
                    imd.put_Filename(fn);
                    imd.put_CurrentStream(0);
                    double length = 0;
                    imd.get_StreamLength(out length);
                    //imd.WriteBitmapBits(0, 70, 45, tmp);

                    int width = 70;
                    int height = 45;

                    IntPtr bufPtr = IntPtr.Zero;
                    int bufSize = 0;

                    int hr = imd.GetBitmapBits(0, out bufSize, bufPtr, width, height);
                    if (hr == 0)
                    {
                        IntPtr buffer = Marshal.AllocCoTaskMem(bufSize);
                        hr = imd.GetBitmapBits(0, out bufSize, buffer, width, height);

                        BitmapInfoHeader bitmapHeader = (BitmapInfoHeader)Marshal.PtrToStructure(buffer, typeof(BitmapInfoHeader));
                        IntPtr bitmapData;
                        //bitmapData.
                        if (IntPtr.Size == 4)
                            bitmapData = new IntPtr(buffer.ToInt32() + bitmapHeader.Size);
                        else
                            bitmapData = new IntPtr(buffer.ToInt64() + bitmapHeader.Size);
                        

                        bitmap = new Bitmap(bitmapHeader.Width, bitmapHeader.Height, PixelFormat.Format24bppRgb);
                        BitmapData bmpData = bitmap.LockBits(new Rectangle(0, 0, bitmapHeader.Width, bitmapHeader.Height), ImageLockMode.WriteOnly, PixelFormat.Format24bppRgb);
                        CopyMemory(bmpData.Scan0, bitmapData, width * height * 3);
                        bitmap.UnlockBits(bmpData);

                        if (buffer != IntPtr.Zero)
                            Marshal.FreeCoTaskMem(buffer);

                    }
                    imd = null;
                     **/
                    //loMD.
                    //Image loImg = Image.FromFile(tmp);
                    //loImg.Save(tmp+".jpg", ImageFormat.Jpeg);
                    //loImg.Dispose();
                    //File.Delete(tmp);
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                    // Means media not supported
                }
                finally
                {
                    if (capFilter != null)
                    {
                        Marshal.ReleaseComObject(capFilter);
                        capFilter = null;
                    }
                    if (sampGrabber != null)
                    {
                        Marshal.ReleaseComObject(sampGrabber);
                        sampGrabber = null;
                    }
                    if (ibfRenderer != null)
                    {
                        Marshal.ReleaseComObject(ibfRenderer);
                        ibfRenderer = null;
                    }
                    if (iPinInFilter != null)
                    {
                        Marshal.ReleaseComObject(iPinInFilter);
                        iPinInFilter = null;
                    }
                    if (iPinOutFilter != null)
                    {
                        Marshal.ReleaseComObject(iPinOutFilter);
                        iPinOutFilter = null;
                    }
                    if (iPinInDest != null)
                    {
                        Marshal.ReleaseComObject(iPinInDest);
                        iPinInDest = null;
                    }
                }
            }
            else
            {
                Image img = Image.FromFile(fn);
                AddImage(AddImageCB, 0, fn, img);
            }
        }

        public delegate void AddImageDelegate(long dtr, string fn, Image img);
        public void AddImageCB(long dtr, string fn, Image img)
        {
            //Console.WriteLine(avgtime);
            imageList2.Images.Add(img);
            img.Dispose();
            string simplefn = fn.Substring(fn.LastIndexOf("\\") + 1);
            ListViewItem item = new ListViewItem(simplefn);
            //item.
            item.Tag = dtr;
            item.ImageIndex = imageList2.Images.Count - 1;
            item.ToolTipText = fn;
            //item.Tag = avgtime;
            //item.SubItems.Add("IMAGE");
            //item.SubItems.Add(s);
            listView1.Items.Add(item);
            listView1.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);
            listView1.Items[listView1.Items.Count - 1].EnsureVisible();
        }

        public void AddImage(AddImageDelegate ai, long dtr, string fn, Image img)
        {
            if (this.InvokeRequired)
            {
                this.Invoke(ai, dtr, fn, img);
                return;
            }
            else
            {
                ai(dtr, fn, img);
                return;
            }
        }

        private void listView1_DragEnter(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop))
            {
                e.Effect = DragDropEffects.Link;
            }
            else
            {
                e.Effect = DragDropEffects.None;
            }
        }

        private void listView1_DragDrop(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop))
            {
                String[] files = (String[])e.Data.GetData(DataFormats.FileDrop);
                foreach (String s in files)
                {
                    FileInfo info = new FileInfo(s);
                    if (EXTS.Contains("#" + info.Extension.ToLower() + "#"))
                    {
                        addItem(s);
                        this.toolBarButton6.Enabled = true;
                    }
                    else
                    {
                        MessageBox.Show("Invalid File Type!");
                    }

                }
            }
        }
        private const int SB_HORZ = 0;
        private const int SB_VERT = 1;
        private const int SB_CTL = 2;
        private const int SB_BOTH = 3;

        [DllImport("user32.dll")]
        private static extern int ShowScrollBar(int hwnd, int wBar, int bShow);

        [DllImport("kernel32.dll", EntryPoint = "RtlMoveMemory")]
        private static extern void CopyMemory(IntPtr Destination, IntPtr Source, int Length);

        private void listView1_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (this.listView1.SelectedItems.Count > 0)
            {
                this.toolBarButton8.Enabled = true;
                if (this.listView1.SelectedItems[0].Index != 0)
                {
                    this.toolBarButton9.Enabled = true;
                }
                else
                {
                    this.toolBarButton9.Enabled = false;
                }

                if (this.listView1.SelectedItems[this.listView1.SelectedItems.Count - 1].Index != this.listView1.Items.Count-1)
                {
                    this.toolBarButton10.Enabled = true;
                }
                else
                {
                    this.toolBarButton10.Enabled = false;
                }

                ListViewItem lvi = this.listView1.SelectedItems[0];
                mi.Name = lvi.Text;
                mi.Path = lvi.ToolTipText;
                //float 
                long drt = ((long)lvi.Tag) / 10000000;
                int s = (int)drt;
                int total = s;
                int h = s / 3600;
                int m = (s - (h * 3600)) / 60;
                s = s - (h * 3600 + m * 60);
                //statusBarPanel3.Text = String.Format("{0:D2}:{1:D2}:{2:D2}", h, m, s);
                mi.Duration = String.Format("{0:D2}:{1:D2}:{2:D2}", h, m, s);
                mi.Type = lvi.Text.Substring(lvi.Text.LastIndexOf(".") + 1);
                this.propertyGrid1.SelectedObject = mi;
            }
            else
            {
                this.toolBarButton8.Enabled = false;
                this.toolBarButton9.Enabled = false;
                this.toolBarButton10.Enabled = false;
                this.propertyGrid1.SelectedObject = null;
            }
        }
        
        private void play(ListViewItem lvi)
        {
            if (screen.Visible == false)
            {
                reserveScreen();
            }
            //else
            //{
           //     this.screen.BringToFront();
            //}

            foreach (ListViewItem lvix in this.listView1.Items)
            {
                lvix.Selected = false;
            }

            /**
            if (this.currentLVI != null)
            {
                this.currentLVI.Selected = false;
            }**/

            lvi.Selected = true;
            lvi.EnsureVisible();

            if (isImage(lvi.Text))
            {
                CleanUp();
                UpdateStatusBar();
                UpdateToolBar();
                this.currentMedia = lvi.ToolTipText;
                this.currentLVI = lvi;
                Image img = new Bitmap(this.currentMedia);
                this.screen.BackgroundImage = img;

                statusBarPanel1.Text = "Running - " + this.currentMedia;
                UpdateStatusBar();
                UpdateToolBar();
                //this.trackBar1.Enabled = false;
            }
            else
            {
                this.screen.BackgroundImage = null;
                if (this.listView1.SelectedItems.Count != 0)
                {
                    CleanUp();
                    UpdateStatusBar();
                    UpdateToolBar();

                    m_objGraphBuilder = (IGraphBuilder)new FilterGraph();

                    //int cookie = 0;
                    //DsROT.AddGraphToRot(m_objGraphBuilder, out cookie);

                    this.currentMedia = lvi.ToolTipText;
                    this.currentLVI = lvi;
                    m_objGraphBuilder.RenderFile(this.currentMedia, null);

                    m_objBasicAudio = m_objGraphBuilder as IBasicAudio;
                    m_objBasicVideo = m_objGraphBuilder as IBasicVideo;
                    //IBasicVideo2 ivb2 = m_objGraphBuilder as IBasicVideo2;
                    m_objMediaEventEx = m_objGraphBuilder as IMediaEventEx;
                    m_objVideoWindow = m_objGraphBuilder as IVideoWindow;
                    m_objMediaPosition = m_objGraphBuilder as IMediaPosition;
                    m_objMediaControl = m_objGraphBuilder as IMediaControl;

                    try
                    {
                        m_objMediaEventEx.SetNotifyWindow(this.Handle, WM_GRAPHNOTIFY, IntPtr.Zero);

                        m_objVideoWindow.put_Owner(screen.Handle);
                        m_objVideoWindow.put_WindowStyle(WindowStyle.Child | WindowStyle.ClipChildren | WindowStyle.ClipSiblings);
                        //m_objVideoWindow.
                        //m_objVideoWindow.WindowStyle = WS_CHILD;
                        //m_objVideoWindow.put_AutoShow(OABool.False);
                        //m_objVideoWindow.
                        //m_objVideoWindow.SetWindowPosition(0, 0, screen.ClientRectangle.Right-300, screen.ClientRectangle.Bottom);
                        m_objVideoWindow.SetWindowPosition(screen.ClientRectangle.Left,
                            screen.ClientRectangle.Top,
                            screen.ClientRectangle.Width,
                            screen.ClientRectangle.Height);
                        //m_objVideoWindow.put_FullScreenMode(OABool.True);
                        //m_objVideoWindow.put_BorderColor(0x00FF00);
                        ///m_objBasicVideo.
                        //Console.WriteLine("KK:"+m_objBasicVideo.put_DestinationWidth(300));
                        //Console.WriteLine(m_objBasicVideo.SetSourcePosition(0, 0, 100, 100));
                        //m_objBasicVideo.SetDestinationPosition(0,
                         //      0,
                         //        screen.ClientRectangle.Width,
                         //        screen.ClientRectangle.Height);
                        //m_objBasicVideo.
                        //m_objBasicVideo.SetDestinationPosition(screen.ClientRectangle.Left, screen.ClientRectangle.Top - 60, 1950, 1040);
                        //m_objVideoWindow.put_FullScreenMode(OABool.True);
                        //m_objVideoWindow.put_MessageDrain(screen.Parent.Handle);
                        //m_objVideoWindow.
                        //m_objBasicVideo.SetDestinationPosition(0,
                        //     0,
                        //    100,
                        //     100);
                        //  m_objBasicVideo.SetDestinationPosition(screen.ClientRectangle.Left - 400,
                        //       screen.ClientRectangle.Top,
                        //         screen.ClientRectangle.Width + 400,
                        //         screen.ClientRectangle.Height);
                        //     m_objBasicVideo.SetDefaultDestinationPosition();
                        //m_objVideoWindow.FullScreenMode = 0;

                    }
                    catch (Exception)
                    {
                        m_objVideoWindow = null;
                    }

                    //m_objMediaEvent = m_objGraphBuilder as IMediaEvent;

                    if (this.currentFile != null)
                    {
                        this.Text = title + " - [" + this.currentFile + "]";
                    }
                    else
                    {
                        this.Text = title;
                    }

                    m_objMediaControl.Run();
                    m_CurrentStatus = MediaStatus.Running;

                    UpdateStatusBar();
                    UpdateToolBar();

                }
            }

            this.Focus();
        }

        private void listView1_DoubleClick(object sender, EventArgs e)
        {
            if (this.listView1.SelectedItems.Count > 0)
            {
                play(this.listView1.SelectedItems[0]);
            }
        }

        private void trackBar1_Scroll(object sender, EventArgs e)
        {
            double cp = 0.0;
            m_objMediaPosition.get_Duration(out cp);
            m_objMediaPosition.put_CurrentPosition(this.trackBar1.Value * cp / 200);
            UpdateStatusBar();
            UpdateToolBar();
        }

        private void menuItem2_Click(object sender, EventArgs e)
        {
            reserveScreen();
        }

        private void menuItem3_Click(object sender, EventArgs e)
        {
            if (screen.WindowState == FormWindowState.Normal && screen.Visible == true)
            {
                screen.setSize(screen.Size);
                screen.WindowState = FormWindowState.Maximized;
                screen.Cursor = System.Windows.Forms.Cursors.SizeAll;
            }
            else if (screen.WindowState == FormWindowState.Maximized && screen.Visible == true)
            {
                screen.WindowState = FormWindowState.Normal;
                screen.Size = screen.getSize();
            }
        }

        public void ts()
        {
            Console.WriteLine("ts");
            //this.m_objVideoWindow = null;
            //m_objMediaControl.Stop();
            //m_objMediaControl.Pause();
        }

        private bool isImage(string file)
        {
            string lfn = file.ToLower();
            if (lfn.EndsWith(".png") || lfn.EndsWith(".jpg") || lfn.EndsWith(".jpeg") || lfn.EndsWith(".bmp"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        private void menuItem7_Click(object sender, EventArgs e)
        {
            (new AboutBox()).ShowDialog(this);
        }

        private void menuItem9_Click(object sender, EventArgs e)
        {
            deleteItems();
        }

        private void menuItem10_Click(object sender, EventArgs e)
        {
            insertItems();
        }

        private void XPlayer_FormClosing(object sender, FormClosingEventArgs e)
        {
            /**
            if (this.client != null && this.client.IsBusy)
            {
                e.Cancel = true;
                MessageBox.Show("Please wait for operations completed!", "Information");
                return;
            }
             * **/
            
            if (this.client != null)
            {
                if (this.client.IsBusy)
                {
                    this.client.CancelAsync();
                }
                this.client.Dispose();
            }
            //this.pf.Tag = "X";
            this.pf.Close();
        }

        private void menuItem11_Click(object sender, EventArgs e)
        {
            (new Option(this.screen)).ShowDialog(this);
        }

        private void listView1_Resize(object sender, EventArgs e)
        {
            //listView1.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);
            //listView1.Items[listView1.Items.Count - 1].EnsureVisible();
        }

        private void menuItem12_Click(object sender, EventArgs e)
        {
            showBackground();
        }

        public void showBackground()
        {
            this.screen.BringToFront();

            CleanUp();

            bool sp = false;
            string ssp = ConfigurationManager.AppSettings["SP"];
            if (ssp != null)
            {
                sp = Boolean.Parse(ssp);
            }

            string path = null;
            if (sp)
            {
                path = ConfigurationManager.AppSettings["PATH"];
                //load image
                try
                {
                    Image bi = Image.FromFile(path);
                    this.screen.BackgroundImage = bi;
                }
                catch (Exception)
                {
                    this.screen.BackgroundImage = null;
                    this.screen.BackColor = Color.Black;
                }
            }
            else
            {
                this.screen.BackgroundImage = null;
                this.screen.BackColor = Color.Black;
            }

            UpdateStatusBar();
            UpdateToolBar();

            this.screen.Focus();
        }

        public Screen getScreen()
        {
            return this.screen;
        }

        private string UMD5(string str)
        {
            string pwd = "";
            MD5 md5 = MD5.Create();//实例化一个md5对像
            // 加密后是一个字节类型的数组，这里要注意编码UTF8/Unicode等的选择　
            byte[] s = md5.ComputeHash(Encoding.UTF8.GetBytes(str));
            // 通过使用循环，将字节类型的数组转换为字符串，此字符串是常规字符格式化所得
            for (int i = 0; i < s.Length; i++)
            {
                // 将得到的字符串使用十六进制类型格式。格式后的字符是小写的字母，如果使用大写（X）则格式后的字符是大写字符

                pwd = pwd + s[i].ToString("X");

            }
            return pwd;
        }

        private void XPlayer_Load(object sender, EventArgs e)
        {
            Thread th = new Thread(new ThreadStart(cpsplash));
            th.Start();

            license = ConfigurationManager.AppSettings["LICENSE"];
            if (license != null)
            {
                hardcode = getCpu(); //+ GetDiskVolumeSerialNumber();//获得24位Cpu和硬盘序列号
                hardcode = UMD5(hardcode);
                //Console.WriteLine(hardcode);
                string[] strid = new string[24];//
                for (int i = 0; i < 24; i++)//把字符赋给数组
                {
                    strid[i] = hardcode.Substring(i+4, 1);
                }

                hardcode = "";
                Random rdid = new Random();
                for (int i = 0; i < 24; i++)//从数组随机抽取24个字符组成新的字符生成机器三
                {
                    //hardcode += strid[rdid.Next(0, 24)];
                    hardcode += strid[i];
                }

                //regcode
                string regcode = "";
                if (hardcode != "")
                {
                    //把机器码存入数组中
                    setIntCode();//初始化127位数组
                    for (int i = 1; i < Charcode.Length; i++)//把机器码存入数组中
                    {
                        Charcode[i] = Convert.ToChar(hardcode.Substring(i - 1, 1));
                    }//
                    for (int j = 1; j < intNumber.Length; j++)//把字符的ASCII值存入一个整数组中。
                    {
                        intNumber[j] = intCode[Convert.ToInt32(Charcode[j])] + Convert.ToInt32(Charcode[j]);
                    }

                    string strAsciiName = null;//用于存储机器码
                    for (int j = 1; j < intNumber.Length; j++)
                    {
                        //MessageBox.Show((Convert.ToChar(intNumber[j])).ToString());
                        if (intNumber[j] >= 48 && intNumber[j] <= 57)//判断字符ASCII值是否0－9之间
                        {
                            strAsciiName += Convert.ToChar(intNumber[j]).ToString();
                        }
                        else if (intNumber[j] >= 65 && intNumber[j] <= 90)//判断字符ASCII值是否A－Z之间
                        {
                            strAsciiName += Convert.ToChar(intNumber[j]).ToString();
                        }
                        else if (intNumber[j] >= 97 && intNumber[j] <= 122)//判断字符ASCII值是否a－z之间
                        {
                            strAsciiName += Convert.ToChar(intNumber[j]).ToString();
                        }
                        else//判断字符ASCII值不在以上范围内
                        {
                            if (intNumber[j] > 122)//判断字符ASCII值是否大于z
                            { 
                                strAsciiName += Convert.ToChar(intNumber[j] - 10).ToString(); 
                            }
                            else
                            {
                                strAsciiName += Convert.ToChar(intNumber[j] - 9).ToString();
                            }
                        }

                        regcode = strAsciiName;//得到注册码
                    }
                }

                //Console.WriteLine(regcode);
                if (regcode != null && regcode.Equals(license))
                {
                    //ture
                    this.registered = true;
                    //Console.WriteLine("TTT");
                }
                else
                {
                    //false
                    this.registered = false;
                    //Console.WriteLine("FFF");
                }
            }
        }

        public int[] intCode = new int[127];//用于存密钥
        public void setIntCode()//给数组赋值个小于10的随机数
        {
            //Random ra = new Random();

            for (int i = 1; i < intCode.Length; i++)
            {
                //intCode[i] = ra.Next(0, 9);
                intCode[i] = i%9;
            }
        }

        public int[] intNumber = new int[25];//用于存机器码的Ascii值
        public char[] Charcode = new char[25];//存储机器码字
        
        private void menuItem13_Click(object sender, EventArgs e)
        {
            Form rf = new RegisterForm(this.hardcode, this.license, this.registered);
            rf.Visible = false;
            rf.ShowDialog();
        }

        private string getCpu()
        {
            string strCpu2 = null;
            ManagementClass myCpu = new ManagementClass("win32_Processor");
            ManagementObjectCollection myCpuConnection = myCpu.GetInstances();
            foreach (ManagementObject myObject in myCpuConnection)
            {
                //Console.WriteLine("KK:"+myObject.Properties["ProcessorId"].Value);
                if (myObject.Properties["Processorid"].Value == null)
                {
                    strCpu2 = myObject.Properties["name"].Value.ToString();
                }else{
                    strCpu2 = myObject.Properties["Processorid"].Value.ToString();
                }
                break;
            }
            //Console.WriteLine(strCpu);
            return strCpu2;
        }

        private string GetDiskVolumeSerialNumber()
        {
            ManagementClass mc = new ManagementClass("Win32_NetworkAdapterConfiguration");
            ManagementObject disk = new ManagementObject("win32_logicaldisk.deviceid=\"c:\"");
            disk.Get();
            return disk.GetPropertyValue("VolumeSerialNumber").ToString();
        }
        
        private void cpsplash() {
            string splash = AppDomain.CurrentDomain.BaseDirectory + "//" + "splash.png";
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
                    string remotetime = null;
                    string curtime = md5;
                    //string curtime = dt.GetDateTimeFormats('s')[0].ToString().Replace("T", " ");

                    //curtime = md5;

                    string url = "http://www.hoyland.ws/x-player/splash.php";

                    WebRequest req = WebRequest.Create(url);
                    req.Method = "GET";
                    WebResponse res = req.GetResponse();

                    Encoding resEncoding = Encoding.GetEncoding("utf-8");
                    StreamReader reader = new StreamReader(res.GetResponseStream(), resEncoding);

                    remotetime = reader.ReadLine();

                    reader.Close();
                    res.Close();

                    if (!curtime.Equals(remotetime))
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
                    string splashtmp = AppDomain.CurrentDomain.BaseDirectory + "//splash.tmp";
                    if (File.Exists(splashtmp))
                    {
                        File.Delete(splashtmp);
                    }

                    string url = "http://www.hoyland.ws/x-player/splash.png";
                    string filename = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetModules()[0].FullyQualifiedName) + "\\splash.tmp";
                    //Console.WriteLine("...."+filename);

                    //this.label1.Text = "0%";
                    this.client = new WebClient();
                    //client.DownloadProgressChanged += new DownloadProgressChangedEventHandler(ProgressChanged);
                    this.client.DownloadFileCompleted += new AsyncCompletedEventHandler(Completed);
                    this.client.DownloadFileAsync(new Uri(url), filename);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
                Console.WriteLine(e.StackTrace);
            }
        }

        private void Completed(object sender, AsyncCompletedEventArgs e)
        {
            string splashtmp = AppDomain.CurrentDomain.BaseDirectory + "//splash.tmp";
            string splashnew = AppDomain.CurrentDomain.BaseDirectory + "//splash.new";
            if (File.Exists(splashnew))
            {
                File.Delete(splashnew);
            }
            File.Move(splashtmp, splashnew);
        }

        private void menuItem15_Click(object sender, EventArgs e)
        {
            swt(0);
        }

        private void menuItem16_Click(object sender, EventArgs e)
        {
            swt(1);
        }

        private void menuItem17_Click(object sender, EventArgs e)
        {
            swt(2);
        }

        private void swt(int language)
        {
            string lanf = "";
            switch (language)
            {
                case 0:
                    break;
                case 1:
                    lanf = "zh-CN";
                    break;
                case 2:
                    lanf = "zh-TW";
                    break;
                default:
                    break;
            }

            CultureInfo UICulture = new CultureInfo(lanf);
            Thread.CurrentThread.CurrentUICulture = UICulture;

            menuItem1.Text = Properties.Resource.MENU_FILE;
            menuItem4.Text = Properties.Resource.MENU_EXIT;
            menuItem5.Text = Properties.Resource.MENU_TOOL;
            menuItem6.Text = Properties.Resource.MENU_HELP;
            menuItem7.Text = Properties.Resource.MENU_ABOUT;
            menuItem11.Text = Properties.Resource.MENU_OPTION;
            menuItem13.Text = Properties.Resource.MENU_REGISTER;
            menuItem14.Text = Properties.Resource.MENU_LANGUAGE;

            //string msg = Properties.Resource.MENU_HELP;

            //Console.WriteLine("msg=" + msg);
        }
    }
    
    internal class Capture : ISampleGrabberCB
    {
        private XPlayer xplayer;
        private string fn;

        private int m_videoWidth;
        private int m_videoHeight;
        private int m_stride;
        private int m_imageSize;
        //private long avgTimePerFrame;
        private bool flag = false;

        public Capture(XPlayer xplayer, String fn)
        {
            this.xplayer = xplayer;
            this.fn = fn;
        }

        public void SaveSizeInfo(ISampleGrabber sampGrabber)
        {
            int hr;

            // Get the media type from the SampleGrabber
            AMMediaType media = new AMMediaType();
            hr = sampGrabber.GetConnectedMediaType(media);
            DsError.ThrowExceptionForHR(hr);

            if ((media.formatType != FormatType.VideoInfo) || (media.formatPtr == IntPtr.Zero))
            {
                throw new NotSupportedException("Unknown Grabber Media Format");
            }

            // Grab the size info
            VideoInfoHeader videoInfoHeader = (VideoInfoHeader)Marshal.PtrToStructure(media.formatPtr, typeof(VideoInfoHeader));
            m_videoWidth = videoInfoHeader.BmiHeader.Width;
            m_videoHeight = videoInfoHeader.BmiHeader.Height;
            m_imageSize = videoInfoHeader.BmiHeader.ImageSize;
            m_stride = m_videoWidth * (videoInfoHeader.BmiHeader.BitCount / 8);
            //avgTimePerFrame = videoInfoHeader.AvgTimePerFrame;
            DsUtils.FreeAMMediaType(media);
            media = null;
        }

        [DllImport("kernel32.dll", EntryPoint = "RtlMoveMemory")]
        private static extern void CopyMemory(IntPtr Destination, IntPtr Source, int Length);

        /// <summary> sample callback, NOT USED. </summary>
        int ISampleGrabberCB.SampleCB(double SampleTime, IMediaSample pSample)
        {
            Marshal.ReleaseComObject(pSample);
            return 0;
        }

        /// <summary> buffer callback, COULD BE FROM FOREIGN THREAD. </summary>
        int ISampleGrabberCB.BufferCB(double SampleTime, IntPtr pBuffer, int BufferLen)
        {
            if (!flag){
               Bitmap b = new Bitmap(m_videoWidth, m_videoHeight, m_stride, PixelFormat.Format24bppRgb, pBuffer);
               BM = b;
               flag = true;
            }
            /**
            IntPtr buffer = Marshal.AllocCoTaskMem(BufferLen);
            CopyMemory(buffer, pBuffer, BufferLen);

            if (!flag)
            {
                BitmapInfoHeader bitmapHeader = (BitmapInfoHeader)Marshal.PtrToStructure(buffer, typeof(BitmapInfoHeader));
                IntPtr bitmapData;
                //bitmapData.
                if (IntPtr.Size == 4)
                    bitmapData = new IntPtr(buffer.ToInt32() + bitmapHeader.Size);
                else
                    bitmapData = new IntPtr(buffer.ToInt64() + bitmapHeader.Size);


                Bitmap bitmap = new Bitmap(bitmapHeader.Width, bitmapHeader.Height, PixelFormat.Format24bppRgb);
                BitmapData bmpData = bitmap.LockBits(new Rectangle(0, 0, bitmapHeader.Width, bitmapHeader.Height), ImageLockMode.WriteOnly, PixelFormat.Format24bppRgb);
                CopyMemory(bmpData.Scan0, bitmapData, m_videoWidth * m_videoHeight * 3);
                bitmap.UnlockBits(bmpData);
            **/
                //if (buffer != IntPtr.Zero)
                //    Marshal.FreeCoTaskMem(buffer);

                /**
                int bufferedSize = BufferLen;
                int stride = this.m_stride * 3;
                byte[] savedArray = new byte[this.m_imageSize + 64000];

                Marshal.Copy(pBuffer, savedArray, 0, BufferLen);

                GCHandle handle = GCHandle.Alloc(savedArray, GCHandleType.Pinned);
                int scan0 = (int)handle.AddrOfPinnedObject();
                scan0 += (this.m_videoHeight - 1) * stride;
                Bitmap b = new Bitmap(this.m_videoWidth, this.m_videoHeight, -stride,
                    System.Drawing.Imaging.PixelFormat.Format24bppRgb, (IntPtr)scan0);
                handle.Free();
                 * */
                /**
                Image img = b;
                if (img != null)
                {
                    try
                    {
                        img.RotateFlip(RotateFlipType.Rotate180FlipX);
                        this.xplayer.addImage(this.fn, img);
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine(ex.Message);
                        Console.WriteLine(ex.StackTrace);
                    }
                }
                **/
            //    flag = true;
            //}
            return 0;
        }

        public Bitmap BM
        {
            set
            {
                this.FrameEvent(value);
            }
        }

        /// <summary> Frame event </summary>
        public event ShowFrame FrameEvent;
        private long dtr;
        
        /// <summary> Interface frame event </summary>
        public delegate void ShowFrame(Bitmap e);

        public void CaptureDone(Bitmap e)
        {
            /**
            try
            {
                this.xplayer.getScreen().BackgroundImage = e;
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                Console.WriteLine(ex.StackTrace);
            }
            **/
            Image img = e;
            if (img != null)
            {
                try
                {
                    img.RotateFlip(RotateFlipType.Rotate180FlipX);
                    this.xplayer.AddImage(this.xplayer.AddImageCB, this.dtr, this.fn, img);
                    //this.xplayer.getScreen().BackgroundImage = img;
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.Message);
                    Console.WriteLine(ex.StackTrace);
                }
            }

            this.FrameEvent -= new ShowFrame(CaptureDone);
        }


        public void setDtr(long dtr)
        {
            this.dtr = dtr;
        }
    }
}
