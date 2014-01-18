using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using ws.hoyland;

namespace QQGM
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            comboBox1.SelectedIndex = 0;
            comboBox2.SelectedIndex = 0;
        }

        private void 退出XToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void 关于AToolStripMenuItem_Click(object sender, EventArgs e)
        {
            AboutBox1 about = new AboutBox1();
            about.Show();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (comboBox1.SelectedIndex == 0)
            {
                int nAppId;         // 软件ＩＤ，开发者分成必要参数。登录开发者后台【我的软件】获得！
                string lpAppKey;    // 软件密钥，开发者分成必要参数。登录开发者后台【我的软件】获得！

                nAppId = 175;
                lpAppKey = "8c31eb9cf312478eda8301b87232e731";

                YDMWrapper.YDM_SetAppInfo(nAppId, lpAppKey);

                //login
                toolStripStatusLabel1.Text = "正在登录...";
                bool isLogin = false;
                string username, password;
                int ret;

                username = textBox1.Text;
                password = textBox2.Text;

                // 返回云打码用户UID，大于零为登录成功，返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
                ret = YDMWrapper.YDM_Login(username, password);

                if (ret > 0)
                {
                    isLogin = true;
                    toolStripStatusLabel1.Text = "登陆成功，ID=" + ret.ToString();
                    
                }
                else
                {
                    isLogin = false;
                    toolStripStatusLabel1.Text = "登陆失败，错误代码：" + ret.ToString();
                }
            }
        }

        private void 导入帐号LToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }
    }
}
