using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Runtime.InteropServices;

namespace xplayer
{
    public partial class Screen : Form
    {
        private Size size;

        public Size getSize()
        {
            return this.size;
        }

        public void setSize(Size size)
        {
            this.size = size;
        }
//        private Screen()
//        {
//           InitializeComponent();
//        }
        public Screen(XPlayer xPlayer)
        {
            // TODO: Complete member initialization
            this.xPlayer = xPlayer;
            InitializeComponent();
        }

        /**
        protected override CreateParams CreateParams
        {
            get
            {
                CreateParams cp = base.CreateParams;
                cp.Style |= 0xC0000 + 0x20000;
                return cp;
            }
        }
        **/

        [DllImport("user32.dll")]
        public static extern bool ReleaseCapture();

        [DllImport("user32.dll")]
        public static extern bool SendMessage(IntPtr hwnd, int wMsg, int wParam, int lParam);

        public const int WM_SYSCOMMAND = 0x0112;//0x0112
        public const int WMSZ_SC_MOVE = 0xF010;
        public const int WMSZ_HTCAPTION = 0x0002;
        public const int WMSZ_LEFT = 0xF001;
        public const int WMSZ_RIGHT = 0xF002;
        public const int WMSZ_TOP = 0xF003;
        public const int WMSZ_TOPLEFT = 0xF004;
        public const int WMSZ_TOPRIGHT = 0xF005;
        public const int WMSZ_BOTTOM = 0xF006;
        public const int WMSZ_BOTTOMLEFT = 0xF007;
        public const int WMSZ_BOTTOMRIGHT = 0xF008;

        public const int MARGIN = 20;
        private XPlayer xPlayer;
        
        private void menuItem2_Click(object sender, EventArgs e)
        {
            this.xPlayer.reserveScreen();
        }

        private void menuItem1_Click(object sender, EventArgs e)
        {
            if (this.WindowState == FormWindowState.Normal&&this.Visible==true)
            {
                size = this.Size;
                this.WindowState = FormWindowState.Maximized;
                this.Cursor = System.Windows.Forms.Cursors.SizeAll;
            }
            else if (this.WindowState == FormWindowState.Maximized && this.Visible == true)
            {
                //this.WindowState = FormWindowState.Minimized;
                this.WindowState = FormWindowState.Normal;
                this.Size = size;
                //this.ClientSize = this.getSize();
            }
        }
        
        private void Screen_MouseDown(object sender, MouseEventArgs e)
        {
            if (this.WindowState == FormWindowState.Maximized) return;
            ReleaseCapture();
            /**
            int PX0 = this.Location.X - this.Width / 2;
            int PX1 = this.Location.X + this.Width / 2;
            int PY0 = this.Location.Y - this.Height / 2;
            int PY1 = this.Location.Y + this.Height / 2;
            **/
            //Console.WriteLine(e.X+":"+e.Y);
            //Console.WriteLine(this.Location.X+"->"+this.Location.Y);
            if ((e.X < MARGIN) && (e.Y < MARGIN))
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_TOPLEFT, 0);
            }
            else if ((e.X < MARGIN) && (e.Y > this.Height - MARGIN))
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_BOTTOMLEFT, 0);
            }
            else if ((e.X > this.Width - MARGIN) && (e.Y < MARGIN))
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_TOPRIGHT, 0);
            }
            else if ((e.X > this.Width - MARGIN) && (e.Y > this.Height - MARGIN))
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_BOTTOMRIGHT, 0);
            }
            else if (e.X < MARGIN)
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_LEFT, 0);
            }
            else if (e.Y < MARGIN)
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_TOP, 0);
            }
            else if (e.X > this.Width - MARGIN)
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_RIGHT, 0);
            }
            else if (e.Y > this.Height - MARGIN)
            {
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_BOTTOM, 0);
            }
            else
            {
                //Console.WriteLine("EEE:"+e.X+"!"+e.Y);
                this.xPlayer.ts();
                //Console.WriteLine("1");
                SendMessage(this.Handle, WM_SYSCOMMAND, WMSZ_SC_MOVE | WMSZ_HTCAPTION , 0);
                //Console.WriteLine("2");
                //SendMessage(this.Handle, 514, 0, 0);
            }
        }

        private void Screen_MouseMove(object sender, MouseEventArgs e)
        {
            //Console.WriteLine("EEEE");
            //this.Cursor = System.Windows.Forms.Cursors.PanNW;
            if (this.WindowState == FormWindowState.Maximized) return;
            if ((e.X < MARGIN) && (e.Y < MARGIN))
            {
                this.Cursor = System.Windows.Forms.Cursors.PanNW;
            }
            else if ((e.X < MARGIN) && (e.Y > this.Height - MARGIN))
            {
                this.Cursor = System.Windows.Forms.Cursors.PanSW;
            }
            else if ((e.X > this.Width - MARGIN) && (e.Y < MARGIN))
            {
                this.Cursor = System.Windows.Forms.Cursors.PanNE;
            }
            else if ((e.X > this.Width - MARGIN) && (e.Y > this.Height - MARGIN))
            {
                this.Cursor = System.Windows.Forms.Cursors.PanSE;
            }
            else if (e.X < MARGIN)
            {
                this.Cursor = System.Windows.Forms.Cursors.PanWest;
            }
            else if (e.Y < MARGIN)
            {
                this.Cursor = System.Windows.Forms.Cursors.PanNorth;
            }
            else if (e.X > this.Width - MARGIN)
            {
                this.Cursor = System.Windows.Forms.Cursors.PanEast;
            }
            else if (e.Y > this.Height - MARGIN)
            {
                this.Cursor = System.Windows.Forms.Cursors.PanSouth;
            }
            else
            {
                this.Cursor = System.Windows.Forms.Cursors.SizeAll;
                //this.Cursor = PanAll;
            }
        }

        private void Screen_SizeChanged(object sender, EventArgs e)
        {
            this.xPlayer.sizeChanged(sender, e);
        }
    }
}
