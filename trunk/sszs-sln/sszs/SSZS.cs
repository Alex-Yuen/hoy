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

namespace ws.hoyland.sszs
{

    public partial class SSZS : Form, Observer
    {
        private delegate void Delegate();
        private Delegate dlg;

        private DataTable table1 = new DataTable();
        private DataTable table2 = new DataTable();
        private Configuration cfa = null;

        public SSZS()
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

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void SSZS_Load(object sender, EventArgs e)
        {
            Engine.getInstance().addObserver(this);

            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

            comboBox2.SelectedIndex = 0;
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
						
						lblNewLabel.setText((String)objs[2]);
						label_9.setText(String.valueOf(((Integer)objs[1]).intValue()));

                        button1.Enabled = true;
						lblNewLabel.setVisible(true);
						label_9.setVisible(true);
						button.setVisible(true);
						label_6.setVisible(true);
						
						//btnUu.setVisible(false);
						combo.setEnabled(false);
						button_1.setVisible(false);
						button_3.setVisible(false);
						text_1.setVisible(false);
						text_3.setVisible(false);
						label_3.setVisible(false);
                        button1.setVisible(false);
						 toolStripStatusLabel1.Text = "登录成功: ID="+((Int32)objs[0]);
					};
                this.BeginInvoke(dlg);
				break;
			case EngineMessageType.OM_LOGIN_ERROR:
				dlg = delegate
                    {
						Object[] objs = (Object[])msg.getData();
                        button1.Enabled = true;
						toolStripStatusLabel1.Text =  "登录失败: ERR="+((Int32)objs[0]);
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

        private void button1_Click_1(object sender, EventArgs e)
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
    }
}
