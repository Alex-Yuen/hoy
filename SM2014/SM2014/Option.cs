using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Configuration;

namespace SM2014
{
    public partial class Option : Form
    {
        private Configuration cfa = null;

        public Option()
        {
            InitializeComponent();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            if (cfa == null)
            {
                MessageBox.Show("加载配置文件失败!");
            }

            ConfigurationManager.RefreshSection("appSettings");

            try
            {
                cfa.AppSettings.Settings["THREAD_COUNT"].Value = numericUpDown1.Value.ToString();
                cfa.AppSettings.Settings["TASK_TIMES"].Value = numericUpDown2.Value.ToString();
                cfa.AppSettings.Settings["TIMEOUT"].Value = numericUpDown3.Value.ToString();
                cfa.AppSettings.Settings["P_ITV"].Value = numericUpDown4.Value.ToString();                

                cfa.Save();
            }
            catch (Exception)
            {
                //
            }
            this.Close();
        }

        private void Option_Load(object sender, EventArgs e)
        {
            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            if (cfa == null)
            {
                MessageBox.Show("加载配置文件失败!");
            }

            ConfigurationManager.RefreshSection("appSettings");

            try
            {
                numericUpDown1.Value = Decimal.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value);
                numericUpDown2.Value = Decimal.Parse(cfa.AppSettings.Settings["TASK_TIMES"].Value);
                numericUpDown3.Value = Decimal.Parse(cfa.AppSettings.Settings["TIMEOUT"].Value);
                numericUpDown4.Value = Decimal.Parse(cfa.AppSettings.Settings["P_ITV"].Value);
            }
            catch (Exception)
            {
                //
            }
        }
    }
}
