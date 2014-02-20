using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using ws.hoyland.util;
using System.Configuration;
using System.Globalization;
using System.Threading;
using System.IO;

namespace ws.hoyland.sszs
{

    public partial class SSZS : Form, Observer
    {
        private delegate void Delegate();
        private Delegate dlg;

        private DataTable table1 = new DataTable();
        private DataTable table2 = new DataTable();
        private Configuration cfa = null;

        private int first = -1;
        private int last = -1;
        private int mfirst = -1;
        private Declare declare;
        private string p;
                
        private SSZS()
        {
            InitializeComponent();
            table1.Columns.Add("ID", Type.GetType("System.String"));
            table1.Columns.Add("帐号", Type.GetType("System.String"));
            table1.Columns.Add("密码", Type.GetType("System.String"));
            table1.Columns.Add("状态", Type.GetType("System.String"));
            dataGridView1.DataSource = table1;


            table2.Columns.Add("ID", Type.GetType("System.String"));
            table2.Columns.Add("帐号", Type.GetType("System.String"));
            table2.Columns.Add("密码", Type.GetType("System.String"));
            table2.Columns.Add("次数", Type.GetType("System.String"));
            dataGridView2.DataSource = table2;
        }

        public SSZS(Declare declare, string p)
        {
            // TODO: Complete member initialization
            this.declare = declare;
            this.p = p;
            
            InitializeComponent();
            table1.Columns.Add("ID", Type.GetType("System.String"));
            table1.Columns.Add("帐号", Type.GetType("System.String"));
            table1.Columns.Add("密码", Type.GetType("System.String"));
            table1.Columns.Add("状态", Type.GetType("System.String"));
            dataGridView1.DataSource = table1;


            table2.Columns.Add("ID", Type.GetType("System.String"));
            table2.Columns.Add("帐号", Type.GetType("System.String"));
            table2.Columns.Add("密码", Type.GetType("System.String"));
            table2.Columns.Add("次数", Type.GetType("System.String"));
            dataGridView2.DataSource = table2;
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void SSZS_Load(object sender, EventArgs e)
        {
            this.Text += this.p;

            Engine.getInstance().addObserver(this);

            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            if (cfa == null)
            {
                MessageBox.Show("加载配置文件失败!");
            }
            ConfigurationManager.RefreshSection("appSettings");

            try
            {
                comboBox2.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["CPT_TYPE"].Value);

                textBox2.Text = cfa.AppSettings.Settings["ACCOUNT"].Value;
                if ("True".Equals(cfa.AppSettings.Settings["REM_PASSWORD"].Value))
                {
                    checkBox1.Checked = true;
                    textBox3.Text = cfa.AppSettings.Settings["PASSWORD"].Value;
                }

                if ("True".Equals(cfa.AppSettings.Settings["AUTO_LOGIN"].Value))
                {
                    checkBox2.Checked = true;
                    //TODO, autologin
                }

                setLoginPanel();

                //自动登录 
                if (checkBox2.Checked)
                {
                    button1.PerformClick();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        private void 关于AToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new AboutBox().ShowDialog();
        }

        private void 选项OToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new Option().ShowDialog();
        }

        private void 导入帐号LToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }

        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {

        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {

        }

        private void button1_Click(object sender, EventArgs e)
        {

        }

        private void textBox3_Enter(object sender, EventArgs e)
        {

        }

        private void textBox3_KeyUp(object sender, KeyEventArgs e)
        {

        }

        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void button2_Click(object sender, EventArgs e)
        {

        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void 常规导入SToolStripMenuItem_Click(object sender, EventArgs e)
        {
            dlg = delegate()
            {
                OpenFileDialog dialog = new OpenFileDialog();
                //dialog.
                dialog.Title = "导入帐号-常规";
                dialog.Filter = "所有文件(*.*)|*.*";

                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    string fn = dialog.FileName;
                    if (fn != null)
                    {
                        EngineMessage message = new EngineMessage();
                        message.setType(EngineMessageType.IM_LOAD_ACCOUNT);
                        message.setData("S|" + fn);
                        Engine.getInstance().fire(message);
                    }
                }
            };
            this.BeginInvoke(dlg);
        }

        private void 带历史密码导入LToolStripMenuItem_Click(object sender, EventArgs e)
        {
            dlg = delegate()
            {
                OpenFileDialog dialog = new OpenFileDialog();
                //dialog.
                dialog.Title = "导入帐号-带历史密码";
                dialog.Filter = "所有文件(*.*)|*.*";

                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    string fn = dialog.FileName;
                    if (fn != null)
                    {
                        EngineMessage message = new EngineMessage();
                        message.setType(EngineMessageType.IM_LOAD_ACCOUNT);
                        message.setData("H|" + fn);
                        Engine.getInstance().fire(message);
                    }
                }
            };
            this.BeginInvoke(dlg);
        }

        public void update(object sender, EventArgs e)
        {
            EngineMessage msg = (EngineMessage)e;
            int type = msg.getType();

            switch (type)
            {
                case EngineMessageType.OM_LOGINING:
                    dlg = delegate
                        {
                            button1.Enabled = false;
                            toolStripStatusLabel1.Text = "正在登录";
                        };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_LOGINED:
                    dlg = delegate
                        {
                            Object[] objs = (Object[])msg.getData();

                            label17.Text = (String)objs[2];
                            label17.Visible = true;
                            label18.Text = ((Int32)objs[1]).ToString();
                            label18.Visible = true;

                            button1.Text = "切换帐号";
                            button1.Enabled = true;
                            label10.Text = "题分:";

                            comboBox2.Enabled = false;
                            checkBox1.Visible = false;
                            checkBox2.Visible = false;
                            textBox2.Visible = false;
                            textBox3.Text = "";
                            textBox3.Visible = false;

                            toolStripStatusLabel1.Text = "登录成功: ID=" + ((Int32)objs[0]);
                        };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_USERCHANGE:
                    dlg = delegate
                    {
                        label17.Text = "";
                        label17.Visible = false;
                        label18.Text = "";
                        label18.Visible = false;

                        button1.Text = "登录";
                        //button1.Enabled = true;
                        label10.Text = "密码:";

                        comboBox2.Enabled = true;
                        checkBox1.Visible = true;
                        checkBox2.Visible = true;
                        textBox2.Visible = true;
                        textBox3.Visible = true;

                        toolStripStatusLabel1.Text = "未登录";
                    };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_LOGIN_ERROR:
                    dlg = delegate
                        {
                            Object[] objs = (Object[])msg.getData();
                            button1.Enabled = true;
                            toolStripStatusLabel1.Text = "登录失败: ERR=" + ((Int32)objs[0]);
                        };
                    this.BeginInvoke(dlg);
                    break;

                case EngineMessageType.OM_CLEAR_ACC_TBL:
                    dlg = delegate
                    {
                        table1.Clear();
                    };
                    this.BeginInvoke(dlg);

                    break;
                case EngineMessageType.OM_ADD_ACC_TBIT:
                    dlg = delegate
                    {
                        DataRow row = table1.NewRow();
                        String[] dt = (String[])msg.getData();
                        for (int i = 0; i < dt.Length; i++)
                        {
                            row[i] = dt[i];
                        }
                        table1.Rows.Add(row);
                        dataGridView1.DataSource = table1;
                        dataGridView1.FirstDisplayedScrollingRowIndex = dataGridView1.Rows.Count - 1;
                        //dataGridView1.Rows[dataGridView1.Rows.Count - 1].Selected = true;
                    };
                    this.BeginInvoke(dlg);

                    break;
                case EngineMessageType.OM_CLEAR_MAIL_TBL:
                    dlg = delegate
                        {
                            table2.Clear();
                        };
                    this.BeginInvoke(dlg);

                    break;

                case EngineMessageType.OM_ADD_MAIL_TBIT:
                    dlg = delegate
                        {
                            DataRow row = table2.NewRow();
                            String[] dt = (String[])msg.getData();
                            for (int i = 0; i < dt.Length; i++)
                            {
                                row[i] = dt[i];
                            }
                            table2.Rows.Add(row);
                            dataGridView2.DataSource = table2;
                            dataGridView2.FirstDisplayedScrollingRowIndex = dataGridView2.Rows.Count - 1;
                            //dataGridView1.Rows[dataGridView1.Rows.Count - 1].Selected = true;
                        };
                    this.BeginInvoke(dlg);

                    break;

                case EngineMessageType.OM_ACCOUNT_LOADED:
                    dlg = delegate
                            {
                                List<String> ls = (List<String>)msg.getData();
                                label4.Text = ls[0];
                                dataGridView1.FirstDisplayedScrollingRowIndex = 0;
                            };

                    this.BeginInvoke(dlg);

                    break;
                case EngineMessageType.OM_MAIL_LOADED:
                    dlg = delegate
                            {
                                List<String> ls = (List<String>)msg.getData();
                                label5.Text = ls[0];
                                dataGridView2.FirstDisplayedScrollingRowIndex = 0;
                            };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_READY:
                    dlg = delegate
                               {
                                   button2.Enabled = true;
                               };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_UNREADY:
                    dlg = delegate
                               {
                                   button2.Enabled = false;
                               };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_RUNNING:
                    dlg = delegate()
                    {
                        if ((Boolean)msg.getData())
                        {
                            toolStripStatusLabel1.Text = "正在运行...";
                            button2.Text = "停止";
                            button3.Enabled = true;
                        }
                        else
                        {
                            first = -1;
                            last = -1;
                            mfirst = -1;
                            toolStripStatusLabel1.Text = "运行停止";
                            button2.Text = "开始";
                            button3.Enabled = false;
                        }
                    };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_IMAGE_DATA:
                    dlg = delegate()
                    {
                        MemoryStream ms = (MemoryStream)msg.getData();
                        pictureBox1.Image = Image.FromStream(ms);
                        ms.Close();
                    };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_INFO:
                    dlg = delegate()
                    {
                        table1.Rows[msg.getTid() - 1][3] = (String)msg.getData();

                        dataGridView1.FirstDisplayedScrollingRowIndex = msg.getTid() - 1;

                    };
                    this.BeginInvoke(dlg);
                    break;
                case EngineMessageType.OM_REQUIRE_MAIL:
                    dlg = delegate()
                    {
                        int mid = Int32.Parse(((String[])msg.getData())[0]);
                        int mc = Int32.Parse(table2.Rows[msg.getTid() - 1][3].ToString()) + 1;
                        table2.Rows[msg.getTid() - 1][3] = mc.ToString();
                        dataGridView2.FirstDisplayedScrollingRowIndex = mid - 1;
                    };
                    this.BeginInvoke(dlg);
                    break;
                default:
                    break;
            }

        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            常规导入SToolStripMenuItem.PerformClick();
        }

        private void toolStripButton5_Click(object sender, EventArgs e)
        {
            带历史密码导入LToolStripMenuItem.PerformClick();
        }

        private void 导入邮件MToolStripMenuItem_Click(object sender, EventArgs e)
        {
            dlg = delegate()
            {
                OpenFileDialog dialog = new OpenFileDialog();
                //dialog.
                dialog.Title = "导入邮箱";
                dialog.Filter = "所有文件(*.*)|*.*";

                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    string fn = dialog.FileName;
                    if (fn != null)
                    {
                        EngineMessage message = new EngineMessage();
                        message.setType(EngineMessageType.IM_LOAD_MAIL);
                        message.setData(fn);
                        Engine.getInstance().fire(message);
                    }
                }
            };
            this.BeginInvoke(dlg);
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            导入邮件MToolStripMenuItem.PerformClick();
        }

        private void setLoginPanel()
        {
            if (comboBox2.SelectedIndex == 2)
            {
                textBox2.Enabled = false;
                textBox3.Enabled = false;
                checkBox1.Enabled = false;
                checkBox2.Enabled = false;
                button1.Enabled = false;
            }
            else
            {
                textBox2.Enabled = true;
                textBox3.Enabled = true;
                checkBox1.Enabled = true;
                checkBox2.Enabled = true;
                button1.Enabled = true;
            }
        }

        private void button1_Click_1(object sender, EventArgs e)
        {
            if (comboBox2.Enabled)
            {
                List<String> param = new List<String>();
                param.Add(textBox2.Text);
                param.Add(textBox3.Text);
                param.Add(checkBox1.Checked.ToString());
                param.Add(checkBox2.Checked.ToString());
                param.Add(comboBox2.SelectedIndex.ToString());

                EngineMessage message = new EngineMessage();
                message.setType(EngineMessageType.IM_USERLOGIN);
                message.setData(param);

                Engine.getInstance().fire(message);
            }
            else
            {
                EngineMessage message = new EngineMessage();
                message.setType(EngineMessageType.IM_USERCHANGE);
                message.setData(null);

                Engine.getInstance().fire(message);
            }
        }

        private void comboBox2_SelectedIndexChanged_1(object sender, EventArgs e)
        {
            setLoginPanel();

            EngineMessage message = new EngineMessage();
            message.setType(EngineMessageType.IM_CAPTCHA_TYPE);
            message.setData(comboBox2.SelectedIndex);

            Engine.getInstance().fire(message);
        }

        private void textBox3_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter)
            {
                button1.PerformClick();
            }
        }

        private void button2_Click_1(object sender, EventArgs e)
        {
            if (first == -1)
            {
                first = 0;
            }
            if (last == -1)
            {
                last = dataGridView1.RowCount - 2;//table.getItemCount() - 1;
            }

            Thread t = new Thread(new ThreadStart(() =>
            {
                Int32[] flidx = new Int32[3];
                flidx[0] = first;
                flidx[1] = last;
                flidx[2] = mfirst;
                EngineMessage message = new EngineMessage();
                message.setType(EngineMessageType.IM_PROCESS);
                message.setData(flidx);
                Engine.getInstance().fire(message);
            }));
            t.Start();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            if ("暂停".Equals(button3.Text))
            {
                button3.Text = "继续";
            }
            else
            {
                button3.Text = "暂停";
            }

            EngineMessage message = new EngineMessage();
            message.setType(EngineMessageType.IM_PAUSE);
            Engine.getInstance().fire(message);
        }

        private void 只执行选定行LToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count == 0)
            {
                return;
            }

            first =  dataGridView1.SelectedRows[0].Index;
            last = first;

            button2.PerformClick();
        }

        private void 从选定行开始执行ToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count == 0)
            {
                return;
            }

            first = dataGridView1.SelectedRows[0].Index;
            last = dataGridView1.RowCount - 2;

            button2.PerformClick();
        }

        private void 复制CToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count == 0)
            {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dataGridView1.SelectedRows.Count; i++)
            {
                sb.Append(table1.Rows[dataGridView1.SelectedRows[i].Index][0] + "----" + table1.Rows[dataGridView1.SelectedRows[i].Index][1] + "----" + table1.Rows[dataGridView1.SelectedRows[i].Index][2] + "----" + table1.Rows[dataGridView1.SelectedRows[i].Index][3] + "\r\n");
                //System.out.println("OK");
            }

            Clipboard.SetDataObject(sb.ToString()); 
        }

        private void dataGridView1_SelectionChanged(object sender, EventArgs e)
        {
            if (dataGridView1.SelectedRows.Count == 0)
            {
                复制CToolStripMenuItem.Enabled = false;
                只执行选定行LToolStripMenuItem.Enabled = false;
                从选定行开始执行ToolStripMenuItem.Enabled = false;
            }
            else
            {
                if (button2.Enabled)
                {
                    只执行选定行LToolStripMenuItem.Enabled = true;
                    从选定行开始执行ToolStripMenuItem.Enabled = true;
                }
                复制CToolStripMenuItem.Enabled = true;
            }
        }

        private void 复制CToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            if (dataGridView2.SelectedRows.Count == 0)
            {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dataGridView2.SelectedRows.Count; i++)
            {
                sb.Append(table2.Rows[dataGridView2.SelectedRows[i].Index][0] + "----" + table2.Rows[dataGridView2.SelectedRows[i].Index][1] + "----" + table2.Rows[dataGridView2.SelectedRows[i].Index][2] + "----" + table2.Rows[dataGridView2.SelectedRows[i].Index][3] + "\r\n");
                //System.out.println("OK");
            }

            Clipboard.SetDataObject(sb.ToString()); 

        }

        private void dataGridView2_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            if (dataGridView2.SelectedRows.Count == 0)
            {
                return;
            }
            int lidx = dataGridView2.SelectedRows[0].Index;
            for (int i = 0; i < lidx; i++)
            {
                dataGridView2.Rows[i].DefaultCellStyle.BackColor = Color.Gray;
            }
            for (int i = lidx; i < dataGridView2.RowCount; i++)
            {
                dataGridView2.Rows[i].DefaultCellStyle.BackColor = Color.White;
            }
            mfirst = lidx;
        }

        private void dataGridView2_SelectionChanged(object sender, EventArgs e)
        {
            if (dataGridView2.SelectedRows.Count == 0)
            {
                复制CToolStripMenuItem1.Enabled = false;
            }
            else
            {
                复制CToolStripMenuItem1.Enabled = true;
            }
        }

        private void 上密上保PToolStripMenuItem_Click(object sender, EventArgs e)
        {
            new MBForm().ShowDialog();
        }

        private void 申诉结果RToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            new RSForm().ShowDialog();
        }

        private void SSZS_FormClosing(object sender, FormClosingEventArgs e)
        {
            this.declare.Close();
        }
    }
}
