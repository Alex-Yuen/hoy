using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Configuration;
using Microsoft.Win32;
using System.Runtime.InteropServices;

namespace QQGM
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
            try
            {
                //MessageBox.Show("1");
                if (!check())
                {
                    return;
                }
                //MessageBox.Show("2");
                cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

                if (cfa == null)
                {
                    MessageBox.Show("加载配置文件失败!");
                }
                //MessageBox.Show("3");
                ConfigurationManager.RefreshSection("appSettings");
                //MessageBox.Show("4.1:"+cfa);
                //MessageBox.Show("4.2:" + cfa.AppSettings);
                //MessageBox.Show("4.3:" + cfa.AppSettings.Settings);
                //MessageBox.Show("4.4:" + cfa.AppSettings.Settings["THREAD_COUNT"]);
                //Configuration cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
                cfa.AppSettings.Settings["THREAD_COUNT"].Value = numericUpDown1.Value.ToString();
                //MessageBox.Show("4.5");
                cfa.AppSettings.Settings["FIX_PWD"].Value = radioButton1.Checked.ToString();
                //MessageBox.Show("5");
                cfa.AppSettings.Settings["RND_PWD"].Value = radioButton2.Checked.ToString();
                //MessageBox.Show("6");
                cfa.AppSettings.Settings["FIX_PWD_VALUE"].Value = textBox1.Text;
                //MessageBox.Show("7");
                cfa.AppSettings.Settings["RND_PWD_LEN"].Value = numericUpDown2.Value.ToString();
                //MessageBox.Show("8");
                cfa.AppSettings.Settings["RND_PWD_F1"].Value = checkBox1.Checked.ToString();
                //MessageBox.Show("9");
                cfa.AppSettings.Settings["RND_PWD_F2"].Value = checkBox2.Checked.ToString();
                cfa.AppSettings.Settings["RND_PWD_F3"].Value = checkBox3.Checked.ToString();
                //MessageBox.Show("10");
                cfa.AppSettings.Settings["STOP_FLAG"].Value = checkBox4.Checked.ToString();
                cfa.AppSettings.Settings["STOP_FLAG_F1"].Value = numericUpDown3.Value.ToString();
                cfa.AppSettings.Settings["STOP_FLAG_F2"].Value = numericUpDown4.Value.ToString();
                //MessageBox.Show("11");
                cfa.AppSettings.Settings["REC_FLAG"].Value = checkBox5.Checked.ToString();
                //MessageBox.Show("11.1");
                cfa.AppSettings.Settings["REC_FLAG_F1"].Value = numericUpDown5.Value.ToString();
                //MessageBox.Show("11.2");
                cfa.AppSettings.Settings["REC_FLAG_F2"].Value = numericUpDown6.Value.ToString();
                //MessageBox.Show("11.3");
                cfa.AppSettings.Settings["REC_FLAG_F3"].Value = checkBox6.Checked.ToString();
                //MessageBox.Show("11.4");
                cfa.AppSettings.Settings["REC_FLAG_F4"].Value = checkBox7.Checked.ToString();
                //MessageBox.Show("11.5");
                if (comboBox1.SelectedItem != null)
                {
                    cfa.AppSettings.Settings["REC_FLAG_F5"].Value = comboBox1.SelectedItem.ToString();
                }
                else
                {
                    cfa.AppSettings.Settings["REC_FLAG_F5"].Value = "";
                }
                //MessageBox.Show("11.6");
                cfa.AppSettings.Settings["REC_FLAG_F6"].Value = textBox2.Text;
                //MessageBox.Show("11.7");
                cfa.AppSettings.Settings["REC_FLAG_F7"].Value = textBox3.Text;

                cfa.AppSettings.Settings["DNA_Q1"].Value = comboBox2.SelectedIndex.ToString();
                cfa.AppSettings.Settings["DNA_Q2"].Value = comboBox3.SelectedIndex.ToString();
                cfa.AppSettings.Settings["DNA_Q3"].Value = comboBox4.SelectedIndex.ToString();

                cfa.AppSettings.Settings["DNA_A1"].Value = textBox4.Text;
                cfa.AppSettings.Settings["DNA_A2"].Value = textBox5.Text;
                cfa.AppSettings.Settings["DNA_A3"].Value = textBox6.Text;

                cfa.AppSettings.Settings["DNA_F1"].Value = checkBox8.Checked.ToString();
                cfa.AppSettings.Settings["DNA_F2"].Value = checkBox9.Checked.ToString();
                cfa.AppSettings.Settings["DNA_F3"].Value = checkBox10.Checked.ToString();
                //MessageBox.Show("12");
                cfa.Save();
                //MessageBox.Show("13");
                this.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
                MessageBox.Show(ex.StackTrace);
            }
            //MessageBox.Show("14");
            //ConfigurationManager.RefreshSection("appSettings");
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

                //高级
                //ADSL
                List<string> adls = GetAllAdslName();
                int i = 0;
                int idx = -1;
                foreach(string ad in adls)
                {
                    if (ad.Equals(cfa.AppSettings.Settings["REC_FLAG_F5"].Value))
                    {
                        idx = i;
                    }
                    comboBox1.Items.Add(ad);
                    i++;
                }
                if (idx != -1)
                {
                    comboBox1.SelectedIndex = idx;
                }
                textBox2.Text = cfa.AppSettings.Settings["REC_FLAG_F6"].Value;
                textBox3.Text = cfa.AppSettings.Settings["REC_FLAG_F7"].Value;

                //STOP_FLAG
                if ("True".Equals(cfa.AppSettings.Settings["STOP_FLAG"].Value))
                {
                    checkBox4.Checked = true;
                }
                else
                {
                    checkBox4.Checked = false;
                }
                numericUpDown3.Value = Decimal.Parse(cfa.AppSettings.Settings["STOP_FLAG_F1"].Value);
                numericUpDown4.Value = Decimal.Parse(cfa.AppSettings.Settings["STOP_FLAG_F2"].Value);

                //REC_FLAG
                if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG"].Value))
                {
                    checkBox5.Checked = true;
                }
                else
                {
                    checkBox5.Checked = false;
                }
                numericUpDown5.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F1"].Value);
                numericUpDown6.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F2"].Value);

                if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG_F3"].Value))
                {
                    checkBox6.Checked = true;
                }
                else
                {
                    checkBox6.Checked = false;
                }

                if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG_F4"].Value))
                {
                    checkBox7.Checked = true;
                }
                else
                {
                    checkBox7.Checked = false;
                }


                if ("True".Equals(cfa.AppSettings.Settings["DNA_F1"].Value))
                {
                    checkBox8.Checked = true;
                }
                else
                {
                    checkBox8.Checked = false;
                }

                if ("True".Equals(cfa.AppSettings.Settings["DNA_F2"].Value))
                {
                    checkBox9.Checked = true;
                }
                else
                {
                    checkBox9.Checked = false;
                }

                if ("True".Equals(cfa.AppSettings.Settings["DNA_F3"].Value))
                {
                    checkBox10.Checked = true;
                }
                else
                {
                    checkBox10.Checked = false;
                }
                comboBox2.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["DNA_Q1"].Value);
                comboBox3.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["DNA_Q2"].Value);
                comboBox4.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["DNA_Q3"].Value);

                textBox4.Text = cfa.AppSettings.Settings["DNA_A1"].Value;
                textBox5.Text = cfa.AppSettings.Settings["DNA_A2"].Value;
                textBox6.Text = cfa.AppSettings.Settings["DNA_A3"].Value;
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
            //Console.WriteLine("CHANGE");
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
            if ((checkBox1.Checked && !checkBox2.Checked && !checkBox3.Checked) && numericUpDown2.Value < 9)
            {
                MessageBox.Show("密码不能是9位以下纯数字");
                return false;
            }

            if (comboBox2.SelectedIndex == comboBox3.SelectedIndex || comboBox2.SelectedIndex == comboBox4.SelectedIndex || comboBox3.SelectedIndex == comboBox4.SelectedIndex)
            {
                MessageBox.Show("密保问题不能一样");
                return false;
            }

            if (textBox4.Text.Equals(textBox5.Text) || textBox4.Text.Equals(textBox6.Text) || textBox5.Text.Equals(textBox6.Text))
            {
                MessageBox.Show("密保答案不能一样");
                return false;
            }
            
            return true;
            
        }

        private void checkBox4_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox4.Checked)
            {
                numericUpDown3.Enabled = true;
                numericUpDown4.Enabled = true;
            }
            else
            {
                numericUpDown3.Enabled = false;
                numericUpDown4.Enabled = false;
            }

        }

        private void checkBox5_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox5.Checked)
            {
                numericUpDown5.Enabled = true;
                numericUpDown6.Enabled = true;
                checkBox6.Enabled = true;
                checkBox7.Enabled = true;
                comboBox1.Enabled = true;
                textBox2.Enabled = true;
                textBox3.Enabled = true;
            }
            else
            {
                numericUpDown5.Enabled = false;
                numericUpDown6.Enabled = false;
                checkBox6.Enabled = false;
                checkBox7.Enabled = false;
                comboBox1.Enabled = false;
                textBox2.Enabled = false;
                textBox3.Enabled = false;
            }
        }

        // #region 获取adsl所有宽带连接名称

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
        public struct RasEntryName      //define the struct to receive the entry name
        {
            public int dwSize;
            [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256 + 1)]
            public string szEntryName;
#if WINVER5
     public int dwFlags;
     [MarshalAs(UnmanagedType.ByValTStr,SizeConst=260+1)]
     public string szPhonebookPath;
#endif
        }

        [DllImport("rasapi32.dll", CharSet = CharSet.Auto)]

        public extern static uint RasEnumEntries(
            string reserved,              // reserved, must be NULL
            string lpszPhonebook,         // pointer to full path and file name of phone-book file
            [In, Out]RasEntryName[] lprasentryname, // buffer to receive phone-book entries
            ref int lpcb,                  // size in bytes of buffer
            out int lpcEntries             // number of entries written to buffer
        );

        public List<string> GetAllAdslName()
        {
            List<string> list = new List<string>();
            int lpNames = 1;
            int entryNameSize = 0;
            int lpSize = 0;
            RasEntryName[] names = null;
            entryNameSize = Marshal.SizeOf(typeof(RasEntryName));
            lpSize = lpNames * entryNameSize;
            names = new RasEntryName[lpNames];
            names[0].dwSize = entryNameSize;
            uint retval = RasEnumEntries(null, null, names, ref lpSize, out lpNames);

            //if we have more than one connection, we need to do it again
            if (lpNames > 1)
            {
                names = new RasEntryName[lpNames];
                for (int i = 0; i < names.Length; i++)
                {
                    names[i].dwSize = entryNameSize;
                }
                retval = RasEnumEntries(null, null, names, ref lpSize, out lpNames);
            }

            if (lpNames > 0)
            {
                for (int i = 0; i < names.Length; i++)
                {
                    list.Add(names[i].szEntryName);
                }
            }
            return list;
        }

        private void checkBox8_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox8.Checked)
            {
                textBox4.Enabled = true;
            }
            else
            {
                textBox4.Enabled = false;
            }
        }

        private void checkBox9_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox9.Checked)
            {
                textBox5.Enabled = true;
            }
            else
            {
                textBox5.Enabled = false;
            }
        }

        private void checkBox10_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox10.Checked)
            {
                textBox6.Enabled = true;
            }
            else
            {
                textBox6.Enabled = false;
            }
        }


        // #endregion
    }
}
