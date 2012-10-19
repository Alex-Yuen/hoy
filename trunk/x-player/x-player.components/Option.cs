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
    public partial class Option : Form
    {
        private Screen screen;

        public Option(Screen screen)
        {
            InitializeComponent();
            this.screen = screen;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            Configuration config = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
            config.AppSettings.Settings["SP"].Value = this.radioButton2.Checked.ToString();
            config.AppSettings.Settings["PATH"].Value = this.textBox1.Text;
            config.Save(ConfigurationSaveMode.Modified);
            ConfigurationManager.RefreshSection("appSettings");

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

            this.Close();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void Option_Load(object sender, EventArgs e)
        {
            bool sp = false;
            string ssp = ConfigurationManager.AppSettings["SP"];
            if (ssp != null)
            {
                sp = Boolean.Parse(ssp);
            }

            string path = null;
            if (sp)
            {
                this.radioButton1.Checked = false;
                this.radioButton2.Checked = true;
                path = ConfigurationManager.AppSettings["PATH"];
                this.textBox1.Text = path;
                //load image
                try
                {
                    Image bi = Image.FromFile(path);
                    this.panel1.BackgroundImage = bi;
                }
                catch (Exception ex)
                {
                    this.panel1.BackColor = Color.Black;
                }
            }
            else
            {
                this.radioButton1.Checked = true;
                this.radioButton2.Checked = false;
                this.panel1.BackColor = Color.Black;
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            openFileDialog.Multiselect = false;
            openFileDialog.Filter = "Image Files (jpeg, bmp, png)|*.jpg;*.jpeg;*.bmp;*.png";

            if (DialogResult.OK == openFileDialog.ShowDialog())
            {
                this.textBox1.Text = openFileDialog.FileName;
                try
                {
                    Image bi = Image.FromFile(this.textBox1.Text);
                    this.panel1.BackgroundImage = bi;
                }
                catch (Exception ex)
                {
                    this.panel1.BackColor = Color.Black;
                }
            }
        }

        private void radioButton1_CheckedChanged(object sender, EventArgs e)
        {
            if (this.radioButton1.Checked)
            {
                this.textBox1.Enabled = false;
                this.button3.Enabled = false;
                this.panel1.BackColor = Color.Black;
            }
        }

        private void radioButton2_CheckedChanged(object sender, EventArgs e)
        {
            if (this.radioButton2.Checked)
            {
                this.textBox1.Enabled = true;
                this.button3.Enabled = true;
                try
                {
                    Image bi = Image.FromFile(this.textBox1.Text);
                    this.panel1.BackgroundImage = bi;
                }
                catch (Exception ex)
                {
                    this.panel1.BackColor = Color.Black;
                }
            }
        }
    }
}
