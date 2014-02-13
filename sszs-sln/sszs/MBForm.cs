using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace ws.hoyland.sszs
{
    public partial class MBForm : Form
    {
        private DataTable table1 = new DataTable();

        public MBForm()
        {
            InitializeComponent();
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void 导入LToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OpenFileDialog dialog = new OpenFileDialog();
            //dialog.
            dialog.Title = "导入需上密上保帐号";
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
        }

        private void MBForm_Load(object sender, EventArgs e)
        {
            table1.Columns.Add("ID", Type.GetType("System.String"));
            table1.Columns.Add("帐号", Type.GetType("System.String"));
            table1.Columns.Add("连接", Type.GetType("System.String"));
            table1.Columns.Add("状态", Type.GetType("System.String"));
            dataGridView1.DataSource = table1;

            accounts = new List<String>();

            Encoding ecdtype = EncodingType.GetType(paths[1]);
            FileStream fs = new FileStream(paths[1], FileMode.Open);
            StreamReader m_streamReader = new StreamReader(fs, ecdtype);
            m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);

            String line = null;
            int i = 1;
            while ((line = m_streamReader.ReadLine()) != null)
            {
                if (!line.Equals(""))
                {
                    line = i + "----" + line;
                    string[] lns = Regex.Split(line, "----");
                    List<string> listArr = new List<string>();
                    listArr.Add(lns[0]);
                    listArr.Add(lns[1]);
                    listArr.Add(lns[2]);
                    listArr.Add("初始化");
                    //listArr.AddRange(lns);
                    //listArr.Insert(3, "初始化");
                    lns = listArr.ToArray();

                    accounts.Add(paths[0] + "----" + line);
                    //							if (lns.size() == 3) {
                    //								lns.add("0");
                    //								lns.add("初始化");
                    //								//line += "----0----初始化";
                    //							} else {
                    //								//line += "----初始化";
                    //								lns.add("初始化");
                    //							}

                    String[] items = lns.ToArray();

                    msg = new EngineMessage();
                    msg.setType(EngineMessageType.OM_ADD_ACC_TBIT);
                    msg.setData(items);

                    this.notifyObservers(msg);
                }
                i++;
            }

            m_streamReader.Close();
            m_streamReader.Dispose();
            fs.Close();
            fs.Dispose();

        }
    }
}
