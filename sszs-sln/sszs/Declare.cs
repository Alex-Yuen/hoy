using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Globalization;

namespace ws.hoyland.sszs
{
    public partial class Declare : Form
    {
        private int expire;

        public Declare(int expire)
        {
            this.expire = expire;
            InitializeComponent();
        }

        private Declare()
        {
            InitializeComponent();
        }

        private void Declare_Load(object sender, EventArgs e)
        {
            textBox1.AppendText("\r\n\t一、任何使用本软件的用户均应仔细阅读本声明和协议，用户可\r\n" +
                        "选择不使用本软件，用户使用本软件的行为将被视为对本声明和协议\r\n" +
                        "全部内容的认可。\r\n" +
                        "\r\n" +
                      "\t二、本软件运行需要一定的软硬件环境支持，并且不保证没有错\r\n" +
                        "误。用户须提供软件运行所需软件硬件环境。\r\n" +
                        "\r\n" +
                      "\t三、购买壹份本软件后，用户只能在唯一一台经过合法注册的电\r\n" +
                        "脑使用。\r\n" +
"\r\n" +
                       "\t四、本软件不承诺永久使用期,不承诺免费升级。\r\n" +
                        "\r\n" +
                       "\t五、注册用户在遵守法律及本声明和协议的前提下可使用本软件\r\n" +
                        "。用户无权实施包括但不限于下列行为：\r\n" +
                      "\t1、修改本软件版权的信息、内容；\r\n" +
                      "\t2、对本软件进行反向工程、反向汇编、反向编译等；\r\n" +
                     "\t3、利用本“软件”查询、盗取、传播、储存侵害他人知识产权、\r\n" +
                        "商业秘密权、个人财产权、隐私权、公开权等合法权利。\r\n" +
                     "\t4、传送或散布以其他方式实现传送含有受到知识产权法律保护的\r\n" +
                       "图像、相片、软件或其他资料的文件，作为举例（但不限于此）：包\r\n" +
                       "括版权或商标法（或隐私权或公开权），除非您拥有或控制着相应的\r\n" +
                        "权利或已得到所有必要的认可。\r\n" +
                      "\t5、使用本“软件”必须遵守国家有关法律和政策等，并遵守本\r\n" +
                        "声明和协议。对于用户违法或违反本声明和协议的使用而引起的一切\r\n" +
                        "责任，由用户负全部负责，一概与本软件开发者及销售者无关。而且\r\n" +
                        "，软件开发者有权视用户的行为性质，在不事先通知用户的情况下，\r\n" +
                        "采取包括但不限于中断使用许可、停止提供服务、限制使用、法律追\r\n" +
                        "究等措施。\r\n" +
                      "\t6、用户使用第三方插件与本软件作者及销售无关。\r\n" +
                        "\r\n" +
                      "\t郑重声明：本软件只供管理个人号码使用！禁止用于非法活动！\r\n" +
                        "违者后果自负！\r\n" +
  "\r\n" +
  "\r\n" +
                       "\t\t\t同意请点击是(Y), 不同意请点击否(N)。\r\n" +
                        "");
            //textBox1.Select(0, 0);

            //this.textBox1.Fo
           
        }

        private void button2_Click(object sender, EventArgs e)
        {
            this.Close();
            Application.Exit();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            new SSZS(this, "    [到期时间: " + DateTime.Now.AddDays(expire).ToString("yyyy-MM-dd", DateTimeFormatInfo.InvariantInfo) + "]").Show();
            this.Visible = false;
        }
    }
}
