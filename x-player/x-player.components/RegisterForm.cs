using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Configuration;

namespace xplayer
{
    public partial class RegisterForm : Form
    {
        private string hardcode;
        private string regcode;
        private bool registered;

        public RegisterForm(string hardcode, string regcode, bool registered)
        {
            InitializeComponent();
            this.hardcode = hardcode;
            this.regcode = regcode;
            this.registered = registered;
        }

        private void RegisterForm_Load(object sender, EventArgs e)
        {
            if (this.registered)
            {
                this.textBox2.Text = regcode;
                this.textBox2.Enabled = false;
            }
            this.textBox1.Text = hardcode;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Configuration config = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
            config.AppSettings.Settings["LICENSE"].Value = this.textBox2.Text;
            config.Save(ConfigurationSaveMode.Modified);
            ConfigurationManager.RefreshSection("appSettings");
            Environment.Exit(0);
        }
    }
}
