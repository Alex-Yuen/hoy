using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Ws.Hoyland.CSharp
{
    public class DelegatedForm : Form
    {
        protected delegate void Delegater();
        protected Delegater dlg;

        private void InitializeComponent()
        {
            this.SuspendLayout();
            // 
            // DelegatedForm
            // 
            this.ClientSize = new System.Drawing.Size(384, 212);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Name = "DelegatedForm";
            this.Text = "Delegated Form";
            this.ResumeLayout(false);

        }
    }
}
