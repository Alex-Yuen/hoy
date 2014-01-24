using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Configuration;

namespace QQGM
{
    public partial class Option : Form
    {
        private Configuration cfa = null;

        public Option()
        {
            InitializeComponent();
            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void button1_Click(object sender, EventArgs e)
        {

            if (!check())
            {
                MessageBox.Show("密码不能是9位以下纯数字");
                return;
            }
            ConfigurationManager.RefreshSection("appSettings");

            //Configuration cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
            cfa.AppSettings.Settings["THREAD_COUNT"].Value = numericUpDown1.Value.ToString();
            cfa.AppSettings.Settings["FIX_PWD"].Value = radioButton1.Checked.ToString();
            cfa.AppSettings.Settings["RND_PWD"].Value = radioButton2.Checked.ToString();
            cfa.AppSettings.Settings["FIX_PWD_VALUE"].Value = textBox1.Text;

            cfa.AppSettings.Settings["RND_PWD_LEN"].Value = numericUpDown2.Value.ToString();
            cfa.AppSettings.Settings["RND_PWD_F1"].Value = checkBox1.Checked.ToString();
            cfa.AppSettings.Settings["RND_PWD_F2"].Value = checkBox2.Checked.ToString();
            cfa.AppSettings.Settings["RND_PWD_F3"].Value = checkBox3.Checked.ToString();
            
            cfa.Save();
            this.Close();
            //ConfigurationManager.RefreshSection("appSettings");
        }

        private void Option_Load(object sender, EventArgs e)
        {
            ConfigurationManager.RefreshSection("appSettings");
            try
            {
                numericUpDown1.Value = Decimal.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value);
                if ("True".Equals(cfa.AppSettings.Settings["FIX_PWD"].Value))
                {
                    radioButton1.Checked = true;
                    textBox1.Enabled = true;
                }
                else
                {
                    radioButton1.Checked = false;
                    textBox1.Enabled = false;
                }

                textBox1.Text = cfa.AppSettings.Settings["FIX_PWD_VALUE"].Value;

                if ("True".Equals(cfa.AppSettings.Settings["RND_PWD"].Value))
                {
                    radioButton2.Checked = true;
                    checkBox1.Enabled = true;
                    checkBox2.Enabled = true;
                    checkBox3.Enabled = true;
                }
                else
                {
                    radioButton2.Checked = false;
                    checkBox1.Enabled = false;
                    checkBox2.Enabled = false;
                    checkBox3.Enabled = false;
                }

                numericUpDown2.Value = Decimal.Parse(cfa.AppSettings.Settings["RND_PWD_LEN"].Value);

                if ("True".Equals(cfa.AppSettings.Settings["RND_PWD_F1"].Value))
                {
                    checkBox1.Checked = true;
                }
                else
                {
                    checkBox1.Checked = false;
                }

                if ("True".Equals(cfa.AppSettings.Settings["RND_PWD_F2"].Value))
                {
                    checkBox2.Checked = true;
                }
                else
                {
                    checkBox2.Checked = false;
                }

                if ("True".Equals(cfa.AppSettings.Settings["RND_PWD_F3"].Value))
                {
                    checkBox3.Checked = true;
                }
                else
                {
                    checkBox3.Checked = false;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        private void Option_KeyUp(object sender, KeyEventArgs e)
        {
            if (e.KeyValue == 27)
            {
                this.Close();
            }
            //Console.WriteLine(e.KeyValue);
        }

        private void radioButton1_CheckedChanged(object sender, EventArgs e)
        {
            if (radioButton1.Checked)
            {
                numericUpDown2.Enabled = false;
                checkBox1.Enabled = false;
                checkBox2.Enabled = false;
                checkBox3.Enabled = false;

                textBox1.Enabled = true;
            }
            else
            {
                numericUpDown2.Enabled = true;
                checkBox1.Enabled = true;
                checkBox2.Enabled = true;
                checkBox3.Enabled = true;

                textBox1.Enabled = false;
            }
        }
        
        private bool check()
        {
            if ((checkBox1.Checked && !checkBox2.Checked && !checkBox3.Checked) && numericUpDown2.Value<9)
            {
                return false;
            }else{
                return true;
            }
        }
    }
}
