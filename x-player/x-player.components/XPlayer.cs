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
        private MenuItem menuItem8;
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
        public XPlayer(Form pf)
        {
            this.pf = pf;
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
                //Console.WriteLine("F3");
                this.title += " " + Assembly.GetExecutingAssembly().GetName().Version.ToString();
                this.Text = title;
                listView1.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);
                //Console.WriteLine("F4");
                this.toolBarButton6.Enabled = false;
                this.toolBarButton8.Enabled = false;
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
            this.menuItem8 = new System.Windows.Forms.MenuItem();
            this.menuItem2 = new System.Windows.Forms.MenuItem();
            this.menuItem11 = new System.Windows.Forms.MenuItem();
            this.menuItem3 = new System.Windows.Forms.MenuItem();
            this.menuItem9 = new System.Windows.Forms.MenuItem();
            this.menuItem10 = new System.Windows.Forms.MenuItem();
            this.menuItem6 = new System.Windows.Forms.MenuItem();
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
            this.panel1 = new System.Windows.Forms.Panel();
            this.panel4 = new System.Windows.Forms.Panel();
            this.listView1 = new System.Windows.Forms.ListView();
            this.columnHeader1 = ((System.Windows.Forms.ColumnHeader)(new System.Windows.Forms.ColumnHeader()));
            this.panel5 = new System.Windows.Forms.Panel();
            this.panel3 = new System.Windows.Forms.Panel();
            this.trackBar1 = new System.Windows.Forms.TrackBar();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel2)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel3)).BeginInit();
            this.panel1.SuspendLayout();
            this.panel4.SuspendLayout();
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
            // 
            // menuItem1
            // 
            this.menuItem1.Index = 0;
            this.menuItem1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem4});
            this.menuItem1.Text = "&File";
            // 
            // menuItem4
            // 
            this.menuItem4.Index = 0;
            this.menuItem4.Text = "E&xit";
            this.menuItem4.Click += new System.EventHandler(this.menuItem4_Click);
            // 
            // menuItem5
            // 
            this.menuItem5.Index = 1;
            this.menuItem5.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem8,
            this.menuItem2,
            this.menuItem11,
            this.menuItem3,
            this.menuItem9,
            this.menuItem10});
            this.menuItem5.Text = "&Tool";
            this.menuItem5.Visible = false;
            // 
            // menuItem8
            // 
            this.menuItem8.Index = 0;
            this.menuItem8.Text = "&Option";
            // 
            // menuItem2
            // 
            this.menuItem2.Index = 1;
            this.menuItem2.Shortcut = System.Windows.Forms.Shortcut.CtrlH;
            this.menuItem2.Text = "Show/Hide Screen";
            this.menuItem2.Visible = false;
            this.menuItem2.Click += new System.EventHandler(this.menuItem2_Click);
            // 
            // menuItem11
            // 
            this.menuItem11.Index = 2;
            this.menuItem11.Shortcut = System.Windows.Forms.Shortcut.CtrlO;
            this.menuItem11.Text = "Option";
            this.menuItem11.Click += new System.EventHandler(this.menuItem11_Click);
            // 
            // menuItem3
            // 
            this.menuItem3.Index = 3;
            this.menuItem3.Shortcut = System.Windows.Forms.Shortcut.CtrlF12;
            this.menuItem3.Text = "Full/Part Screen";
            this.menuItem3.Visible = false;
            this.menuItem3.Click += new System.EventHandler(this.menuItem3_Click);
            // 
            // menuItem9
            // 
            this.menuItem9.Index = 4;
            this.menuItem9.Shortcut = System.Windows.Forms.Shortcut.Del;
            this.menuItem9.Text = "Delete Item";
            this.menuItem9.Visible = false;
            this.menuItem9.Click += new System.EventHandler(this.menuItem9_Click);
            // 
            // menuItem10
            // 
            this.menuItem10.Index = 5;
            this.menuItem10.Shortcut = System.Windows.Forms.Shortcut.Ins;
            this.menuItem10.Text = "Insert Item";
            this.menuItem10.Visible = false;
            this.menuItem10.Click += new System.EventHandler(this.menuItem10_Click);
            // 
            // menuItem6
            // 
            this.menuItem6.Index = 2;
            this.menuItem6.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
            this.menuItem7});
            this.menuItem6.Text = "&Help";
            // 
            // menuItem7
            // 
            this.menuItem7.Index = 0;
            this.menuItem7.Text = "&About";
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
            // 
            // statusBar1
            // 
            this.statusBar1.Location = new System.Drawing.Point(0, 394);
            this.statusBar1.Name = "statusBar1";
            this.statusBar1.Panels.AddRange(new System.Windows.Forms.StatusBarPanel[] {
            this.statusBarPanel1,
            this.statusBarPanel2,
            this.statusBarPanel3});
            this.statusBar1.ShowPanels = true;
            this.statusBar1.Size = new System.Drawing.Size(679, 20);
            this.statusBar1.TabIndex = 2;
            // 
            // statusBarPanel1
            // 
            this.statusBarPanel1.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Spring;
            this.statusBarPanel1.BorderStyle = System.Windows.Forms.StatusBarPanelBorderStyle.None;
            this.statusBarPanel1.Name = "statusBarPanel1";
            this.statusBarPanel1.Text = "Ready";
            this.statusBarPanel1.Width = 534;
            // 
            // statusBarPanel2
            // 
            this.statusBarPanel2.Alignment = System.Windows.Forms.HorizontalAlignment.Center;
            this.statusBarPanel2.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Contents;
            this.statusBarPanel2.Name = "statusBarPanel2";
            this.statusBarPanel2.Text = "00:00:00";
            this.statusBarPanel2.Width = 64;
            // 
            // statusBarPanel3
            // 
            this.statusBarPanel3.Alignment = System.Windows.Forms.HorizontalAlignment.Center;
            this.statusBarPanel3.AutoSize = System.Windows.Forms.StatusBarPanelAutoSize.Contents;
            this.statusBarPanel3.Name = "statusBarPanel3";
            this.statusBarPanel3.Text = "00:00:00";
            this.statusBarPanel3.Width = 64;
            // 
            // timer1
            // 
            this.timer1.Enabled = true;
            this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
            // 
            // imageList2
            // 
            this.imageList2.ColorDepth = System.Windows.Forms.ColorDepth.Depth32Bit;
            this.imageList2.ImageSize = new System.Drawing.Size(70, 45);
            this.imageList2.TransparentColor = System.Drawing.Color.Transparent;
            // 
            // toolBar1
            // 
            this.toolBar1.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
            this.toolBar1.AutoSize = false;
            this.toolBar1.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
            this.toolBarButton11,
            this.toolBarButton1,
            this.toolBarButton2,
            this.toolBarButton3,
            this.toolBarButton12});
            this.toolBar1.ButtonSize = new System.Drawing.Size(46, 46);
            this.toolBar1.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.toolBar1.DropDownArrows = true;
            this.toolBar1.ImageList = this.imageList3;
            this.toolBar1.Location = new System.Drawing.Point(0, 321);
            this.toolBar1.Name = "toolBar1";
            this.toolBar1.ShowToolTips = true;
            this.toolBar1.Size = new System.Drawing.Size(679, 73);
            this.toolBar1.TabIndex = 9;
            this.toolBar1.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.toolBar1_ButtonClick);
            // 
            // toolBarButton11
            // 
            this.toolBarButton11.ImageIndex = 3;
            this.toolBarButton11.Name = "toolBarButton11";
            this.toolBarButton11.ToolTipText = "Prev";
            // 
            // toolBarButton1
            // 
            this.toolBarButton1.ImageIndex = 0;
            this.toolBarButton1.Name = "toolBarButton1";
            this.toolBarButton1.ToolTipText = "Play";
            // 
            // toolBarButton2
            // 
            this.toolBarButton2.ImageIndex = 1;
            this.toolBarButton2.Name = "toolBarButton2";
            this.toolBarButton2.ToolTipText = "Pause";
            // 
            // toolBarButton3
            // 
            this.toolBarButton3.ImageIndex = 2;
            this.toolBarButton3.Name = "toolBarButton3";
            this.toolBarButton3.ToolTipText = "Stop";
            // 
            // toolBarButton12
            // 
            this.toolBarButton12.ImageIndex = 4;
            this.toolBarButton12.Name = "toolBarButton12";
            this.toolBarButton12.ToolTipText = "Next";
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
            this.toolBar2.Appearance = System.Windows.Forms.ToolBarAppearance.Flat;
            this.toolBar2.AutoSize = false;
            this.toolBar2.Buttons.AddRange(new System.Windows.Forms.ToolBarButton[] {
            this.toolBarButton4,
            this.toolBarButton5,
            this.toolBarButton6,
            this.toolBarButton7,
            this.toolBarButton8,
            this.toolBarButton9,
            this.toolBarButton10});
            this.toolBar2.ButtonSize = new System.Drawing.Size(46, 46);
            this.toolBar2.DropDownArrows = true;
            this.toolBar2.ImageList = this.imageList1;
            this.toolBar2.Location = new System.Drawing.Point(0, 0);
            this.toolBar2.Name = "toolBar2";
            this.toolBar2.ShowToolTips = true;
            this.toolBar2.Size = new System.Drawing.Size(679, 46);
            this.toolBar2.TabIndex = 13;
            this.toolBar2.ButtonClick += new System.Windows.Forms.ToolBarButtonClickEventHandler(this.toolBar2_ButtonClick);
            // 
            // toolBarButton4
            // 
            this.toolBarButton4.ImageIndex = 0;
            this.toolBarButton4.Name = "toolBarButton4";
            this.toolBarButton4.ToolTipText = "New X-Player List";
            // 
            // toolBarButton5
            // 
            this.toolBarButton5.ImageIndex = 1;
            this.toolBarButton5.Name = "toolBarButton5";
            this.toolBarButton5.ToolTipText = "Open X-Player List";
            // 
            // toolBarButton6
            // 
            this.toolBarButton6.ImageIndex = 2;
            this.toolBarButton6.Name = "toolBarButton6";
            this.toolBarButton6.ToolTipText = "Save X-Player List";
            // 
            // toolBarButton7
            // 
            this.toolBarButton7.ImageIndex = 3;
            this.toolBarButton7.Name = "toolBarButton7";
            this.toolBarButton7.ToolTipText = "Add File to List (Ins)";
            // 
            // toolBarButton8
            // 
            this.toolBarButton8.ImageIndex = 4;
            this.toolBarButton8.Name = "toolBarButton8";
            this.toolBarButton8.ToolTipText = "Remove File from List (Del)";
            // 
            // toolBarButton9
            // 
            this.toolBarButton9.ImageIndex = 5;
            this.toolBarButton9.Name = "toolBarButton9";
            this.toolBarButton9.Style = System.Windows.Forms.ToolBarButtonStyle.ToggleButton;
            this.toolBarButton9.ToolTipText = "Screen (Ctrl+H)";
            // 
            // toolBarButton10
            // 
            this.toolBarButton10.ImageIndex = 6;
            this.toolBarButton10.Name = "toolBarButton10";
            this.toolBarButton10.ToolTipText = "Option (Ctrl+O)";
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.panel4);
            this.panel1.Controls.Add(this.panel3);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(0, 46);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(679, 275);
            this.panel1.TabIndex = 15;
            // 
            // panel4
            // 
            this.panel4.Controls.Add(this.listView1);
            this.panel4.Controls.Add(this.panel5);
            this.panel4.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel4.Location = new System.Drawing.Point(0, 0);
            this.panel4.Name = "panel4";
            this.panel4.Size = new System.Drawing.Size(679, 236);
            this.panel4.TabIndex = 20;
            // 
            // listView1
            // 
            this.listView1.Alignment = System.Windows.Forms.ListViewAlignment.Default;
            this.listView1.AllowDrop = true;
            this.listView1.BackColor = System.Drawing.SystemColors.Info;
            this.listView1.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1});
            this.listView1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.listView1.FullRowSelect = true;
            this.listView1.GridLines = true;
            this.listView1.HeaderStyle = System.Windows.Forms.ColumnHeaderStyle.None;
            this.listView1.Location = new System.Drawing.Point(243, 0);
            this.listView1.Margin = new System.Windows.Forms.Padding(0);
            this.listView1.Name = "listView1";
            this.listView1.ShowItemToolTips = true;
            this.listView1.Size = new System.Drawing.Size(436, 236);
            this.listView1.SmallImageList = this.imageList2;
            this.listView1.TabIndex = 19;
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
            this.columnHeader1.Text = "Overview";
            this.columnHeader1.Width = 429;
            // 
            // panel5
            // 
            this.panel5.BackColor = System.Drawing.SystemColors.ScrollBar;
            this.panel5.Dock = System.Windows.Forms.DockStyle.Left;
            this.panel5.Location = new System.Drawing.Point(0, 0);
            this.panel5.Name = "panel5";
            this.panel5.Size = new System.Drawing.Size(243, 236);
            this.panel5.TabIndex = 18;
            // 
            // panel3
            // 
            this.panel3.Controls.Add(this.trackBar1);
            this.panel3.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.panel3.Location = new System.Drawing.Point(0, 236);
            this.panel3.Name = "panel3";
            this.panel3.Size = new System.Drawing.Size(679, 39);
            this.panel3.TabIndex = 19;
            // 
            // trackBar1
            // 
            this.trackBar1.AutoSize = false;
            this.trackBar1.BackColor = System.Drawing.SystemColors.Control;
            this.trackBar1.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.trackBar1.Location = new System.Drawing.Point(0, 8);
            this.trackBar1.Maximum = 200;
            this.trackBar1.Name = "trackBar1";
            this.trackBar1.Size = new System.Drawing.Size(679, 31);
            this.trackBar1.TabIndex = 12;
            this.trackBar1.TabStop = false;
            this.trackBar1.TickStyle = System.Windows.Forms.TickStyle.None;
            this.trackBar1.Scroll += new System.EventHandler(this.trackBar1_Scroll);
            // 
            // XPlayer
            // 
            this.AutoScaleBaseSize = new System.Drawing.Size(6, 14);
            this.ClientSize = new System.Drawing.Size(679, 414);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.toolBar2);
            this.Controls.Add(this.toolBar1);
            this.Controls.Add(this.statusBar1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Menu = this.mainMenu1;
            this.Name = "XPlayer";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "X-Player";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.XPlayer_FormClosing);
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel2)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.statusBarPanel3)).EndInit();
            this.panel1.ResumeLayout(false);
            this.panel4.ResumeLayout(false);
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
            }
            //Console.WriteLine("U2");
        }

        private void toolBar1_ButtonClick(object sender, System.Windows.Forms.ToolBarButtonClickEventArgs e)
        {
            switch (toolBar1.Buttons.IndexOf(e.Button))
            {
                case 0:
                    //prev
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
                                catch (Exception ex)
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
            switch (m_CurrentStatus)
            {
                case MediaStatus.None: statusBarPanel1.Text = "Stopped"; break;
                case MediaStatus.Paused: statusBarPanel1.Text = "Paused "; break;
                case MediaStatus.Running: statusBarPanel1.Text = "Running"; break;
                case MediaStatus.Stopped: statusBarPanel1.Text = "Stopped"; break;
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

        public void reserveScreen()
        {
            if (screen.Visible == true)
            {
                this.toolBarButton9.Pushed = false;
                screen.Hide();
            }
            else
            {
                this.toolBarButton9.Pushed = true;
                screen.Show();
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
                int idx = item.StateImageIndex;
                this.imageList2.Images.RemoveAt(idx);
                //item 后的所有StateImageIndex都要-1
                foreach (ListViewItem itx in this.listView1.Items)
                {
                    if (itx.StateImageIndex > idx)
                    {
                        itx.StateImageIndex = itx.StateImageIndex - 1;
                    }
                }
                if (this.currentFile == null && this.listView1.Items.Count == 0)
                {
                    this.toolBarButton6.Enabled = false;
                }
                else
                {
                    this.toolBarButton6.Enabled = true;
                }
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
                    reserveScreen();
                    break;
                case 6:
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

        private void addItem(string fn)
        {
            if (!File.Exists(fn))
            {
                return;
            }
            //string tmp = null;
            Bitmap bitmap = null;
            if (fn.ToLower().EndsWith(".mpeg") || fn.ToLower().EndsWith(".mpg"))
            {
                try
                {
                    //tmp = Environment.CurrentDirectory + "\\temp\\tb-" + UnixStamp() + ".tmp";

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
            }

            Image img = null;
            if (bitmap != null)
            {
                img = bitmap;
                //File.Delete(tmp);
            }
            else
            {
                img = Image.FromFile(fn);
            }

            imageList2.Images.Add(img);
            img.Dispose();

            ListViewItem item = new ListViewItem(fn);
            //item.
            item.ImageIndex = imageList2.Images.Count - 1;
            item.ToolTipText = fn;
            //item.SubItems.Add("IMAGE");
            //item.SubItems.Add(s);
            listView1.Items.Add(item);
            listView1.AutoResizeColumns(ColumnHeaderAutoResizeStyle.HeaderSize);
            listView1.Items[listView1.Items.Count - 1].EnsureVisible();

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
            }
            else
            {
                this.toolBarButton8.Enabled = false;
            }
        }

        private void listView1_DoubleClick(object sender, EventArgs e)
        {
            if (screen.Visible == false)
            {
                reserveScreen();
            }

            if (isImage(this.listView1.SelectedItems[0].Text))
            {
                CleanUp();
                Image img = new Bitmap(this.listView1.SelectedItems[0].Text);
                this.screen.BackgroundImage = img;

                this.trackBar1.Enabled = false;
                UpdateStatusBar();
                //UpdateToolBar();
            }
            else
            {
                this.screen.BackgroundImage = null;
                if (this.listView1.SelectedItems.Count != 0)
                {
                    CleanUp();

                    m_objGraphBuilder = (IGraphBuilder)new FilterGraph();
                    m_objGraphBuilder.RenderFile(this.listView1.SelectedItems[0].Text, null);

                    m_objBasicAudio = m_objGraphBuilder as IBasicAudio;
                    m_objBasicVideo = m_objGraphBuilder as IBasicVideo;
                    m_objMediaEventEx = m_objGraphBuilder as IMediaEventEx;
                    m_objVideoWindow = m_objGraphBuilder as IVideoWindow;
                    m_objMediaPosition = m_objGraphBuilder as IMediaPosition;
                    m_objMediaControl = m_objGraphBuilder as IMediaControl;

                    try
                    {
                        m_objMediaEventEx.SetNotifyWindow(this.Handle, WM_GRAPHNOTIFY, IntPtr.Zero);

                        m_objVideoWindow.put_Owner(screen.Handle);
                        m_objVideoWindow.put_WindowStyle(WindowStyle.Child | WindowStyle.ClipChildren);
                        //m_objVideoWindow.WindowStyle = WS_CHILD;
                        //m_objVideoWindow.FullScreenMode = 1;
                        m_objVideoWindow.SetWindowPosition(screen.ClientRectangle.Left,
                            screen.ClientRectangle.Top,
                            screen.ClientRectangle.Width,
                            screen.ClientRectangle.Height);

                        //m_objVideoWindow.put_MessageDrain(screen.Handle);
                        //m_objVideoWindow.
                        // m_objBasicVideo.SetDestinationPosition(screen.ClientRectangle.Left-400,
                        //     screen.ClientRectangle.Top,
                        //     screen.ClientRectangle.Width+400,
                        //    screen.ClientRectangle.Height);
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
    }
}
