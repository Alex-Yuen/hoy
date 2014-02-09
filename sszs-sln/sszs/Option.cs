using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.Runtime.InteropServices;
using System.Configuration;
using ws.hoyland.util;

namespace ws.hoyland.sszs
{
    public partial class Option : Form
    {
        private Configuration cfa = null;
        private String[][] cities = {
			new String[]{"城市"},
			new String[]{"北京"},
			new String[]{"上海"},
			new String[]{"天津"},
			new String[]{"重庆"},
			new String[]{"请选择城市","忘记了","石家庄","唐山","秦皇岛","邯郸","邢台","保定","张家口","承德","沧州","廊坊","衡水","其他"},
			new String[]{"请选择城市","忘记了","太原","大同","阳泉","长治","晋城","朔州","晋中","运城","忻州","临汾","吕梁"},
			new String[]{"请选择城市","忘记了","呼和浩特","包头","乌海","赤峰","通辽","鄂尔多斯","呼伦贝尔","乌兰察布盟","锡林郭勒盟","巴彦淖尔盟","阿拉善盟","兴安盟"},
			new String[]{"请选择城市","忘记了","沈阳","大连","鞍山","抚顺","本溪","丹东","锦州","葫芦岛","营口","盘锦","阜新","辽阳","铁岭","朝阳"},
			new String[]{"请选择城市","忘记了","长春","吉林市","四平","辽源","通化","白山","松原","白城","延边朝鲜族自治州"},
			new String[]{"请选择城市","忘记了","哈尔滨","齐齐哈尔","鹤岗","双鸭山","鸡西","大庆","伊春","牡丹江","佳木斯","七台河","黑河","绥化","大兴安岭"},
			new String[]{"请选择城市","忘记了","南京","无锡","徐州","常州","苏州","南通","连云港","淮安","盐城","扬州","镇江","泰州","宿迁","昆山"},
			new String[]{"请选择城市","忘记了","杭州","宁波","温州","嘉兴","湖州","绍兴","金华","衢州舟山","台州","丽水"},
			new String[]{"请选择城市","忘记了","合肥","芜湖","蚌埠","淮南","马鞍山","淮北","铜陵","安庆","黄山","滁州","阜阳","宿州","巢湖","六安","亳州","池州","宣城"},
			new String[]{"请选择城市","忘记了","福州","厦门","莆田","三明","泉州","漳州","南平","龙岩","宁德"},
			new String[]{"请选择城市","忘记了","南昌","景德镇","萍乡","新余","九江","鹰潭","赣州","吉安","宜春","抚州","上饶"},
			new String[]{"请选择城市","忘记了","济南","青岛","淄博","枣庄","东营","潍坊","烟台","威海","济宁","泰安","日照","莱芜","德州","临沂","聊城","滨州","菏泽"},
			new String[]{"请选择城市","忘记了","郑州","开封","洛阳","平顶山","焦作","鹤壁","新乡","安阳","濮阳","许昌","漯河","三门峡","南阳","商丘","信阳","周口","驻马店","济源"},
			new String[]{"请选择城市","忘记了","武汉","黄石","襄樊","十堰","荆州","宜昌","荆门","鄂州","孝感","黄冈","咸宁","随州","仙桃","天门","潜江","神农架","恩施土家族苗族自治州"},
			new String[]{"请选择城市","忘记了","长沙","株洲","永州","湘潭","衡阳","邵阳","岳阳","常德","张家界","益阳","郴州","怀化","娄底","湘西土家族苗族自治州"},
			new String[]{"请选择城市","忘记了","广州","深圳","珠海","汕头","韶关","佛山","江门","湛江","茂名","肇庆","惠州","梅州","汕尾","河源","阳江","清远","东莞","中山","潮州","揭阳","云浮"},
			new String[]{"请选择城市","忘记了","南宁","柳州","桂林","梧州","北海","防城港","钦州","贵港","玉林","百色","贺州","河池","来宾","崇左"},
			new String[]{"请选择城市","忘记了","海口","三亚","五指山","琼海","儋州","文昌","万宁","东方","澄迈","定安","屯昌","临高","白沙黎族自治县昌","江黎族自治县","乐东黎族自治县","陵水黎族自治县","保亭黎族苗族自治县","琼中黎族苗族自治县"},
			new String[]{"请选择城市","忘记了","成都","自贡","攀枝花","泸州","德阳","绵阳","广元","遂宁","内江","乐山","南充","宜宾","广安","达州","眉山","雅安","巴中","资阳","阿坝藏族羌族自治州","甘孜藏族自治州","凉山彝族自治州"},
			new String[]{"请选择城市","忘记了","贵阳","六盘水","遵义","安顺","铜仁","毕节","黔西南布依族苗族自治州","黔东南苗族侗族自治州","黔南布依族苗族自治州"},
			new String[]{"请选择城市","忘记了","昆明","曲靖","玉溪","保山","昭通","丽江","思茅","临沧","文山壮族苗族自治州","红河哈尼族彝族自治州","西双版纳傣族自治州","楚雄彝族自治州","大理白族自治州","德宏傣族景颇族自治州 ","怒江傈傈族自治州","迪庆藏族自治州"},
			new String[]{"请选择城市","忘记了","拉萨","那曲","昌都","山南","日喀则","阿里","林芝"},
			new String[]{"请选择城市","忘记了","西安","铜川","宝鸡","咸阳","渭南","延安","汉中","榆林","安康","商洛"},
			new String[]{"请选择城市","忘记了","兰州","金昌","白银","天水","嘉峪关","武威","张掖","平凉","酒泉","庆阳","定西","陇南","临夏回族自治州","甘南藏族自治州"},
			new String[]{"请选择城市","忘记了","西宁","海东","海北藏族自治州","黄南藏族自治州","海南藏族自治州","果洛藏族自治州","玉树藏族自治州","海西蒙古族藏族自治州"},
			new String[]{"请选择城市","忘记了","银川","石嘴山","吴忠","固原"},
			new String[]{"请选择城市","忘记了","乌鲁木齐","克拉玛依","石河子","阿拉尔","图木舒克","五家渠","吐鲁番","哈密","和田","阿克苏","喀什","克孜勒苏柯尔克孜自治州","巴音郭楞蒙古自治州","昌吉回族自治州","博尔塔拉蒙古自治州","伊犁哈萨克自治州"},
			new String[]{"香港"},
			new String[]{"澳门"},
			new String[]{"请选择城市","忘记了","台北","高雄","基隆","台中","台南","新竹","嘉义","台北县","宜兰县","新竹县","桃园县","苗栗县","台中县","彰化县","南投县","嘉义县","云林县","台南县","高雄县","屏东县","台东县","花莲县","澎湖县"}
	};

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
                
                //MessageBox.Show("4.5");
                //MessageBox.Show("6");
                cfa.AppSettings.Settings["EMAIL_TIMES"].Value = numericUpDown4.Value.ToString();
                //MessageBox.Show("8");
                cfa.AppSettings.Settings["MAIL_ITV"].Value = numericUpDown2.Value.ToString();

                cfa.AppSettings.Settings["P1"].Value = comboBox2.SelectedIndex.ToString();
                cfa.AppSettings.Settings["C1"].Value = comboBox5.SelectedIndex.ToString();
                cfa.AppSettings.Settings["P2"].Value = comboBox3.SelectedIndex.ToString();
                cfa.AppSettings.Settings["C2"].Value = comboBox6.SelectedIndex.ToString();
                cfa.AppSettings.Settings["P3"].Value = comboBox4.SelectedIndex.ToString();
                cfa.AppSettings.Settings["C3"].Value = comboBox7.SelectedIndex.ToString();

                cfa.AppSettings.Settings["THREAD_COUNT"].Value = numericUpDown1.Value.ToString();
                cfa.AppSettings.Settings["TOOL_COUNT"].Value = numericUpDown3.Value.ToString();

                cfa.AppSettings.Settings["REC_FLAG"].Value = checkBox5.Checked.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F1"].Value = numericUpDown5.Value.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F2"].Value = numericUpDown6.Value.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F3"].Value = checkBox6.Checked.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F4"].Value = checkBox4.Checked.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F5"].Value = checkBox7.Checked.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F6"].Value = numericUpDown10.Value.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F7"].Value = checkBox1.Checked.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F8"].Value = numericUpDown7.Value.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F9"].Value = numericUpDown8.Value.ToString();
                cfa.AppSettings.Settings["REC_FLAG_F10"].Value = numericUpDown9.Value.ToString();
                
                if (comboBox1.SelectedItem != null)
                {
                    cfa.AppSettings.Settings["REC_FLAG_F11"].Value = comboBox1.SelectedItem.ToString();
                }
                else
                {
                    cfa.AppSettings.Settings["REC_FLAG_F11"].Value = "";
                }
                cfa.AppSettings.Settings["REC_FLAG_F12"].Value = textBox2.Text;
                cfa.AppSettings.Settings["REC_FLAG_F13"].Value = textBox3.Text;
                
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
                numericUpDown4.Value = Decimal.Parse(cfa.AppSettings.Settings["EMAIL_TIMES"].Value);
                numericUpDown2.Value = Decimal.Parse(cfa.AppSettings.Settings["MAIL_ITV"].Value);

                comboBox2.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["P1"].Value);
                comboBox5.Items.AddRange(cities[comboBox2.SelectedIndex]);
                comboBox5.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["C1"].Value);
                comboBox3.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["P2"].Value);
                comboBox6.Items.AddRange(cities[comboBox3.SelectedIndex]);
                comboBox6.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["C2"].Value);
                comboBox4.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["P3"].Value);
                comboBox7.Items.AddRange(cities[comboBox4.SelectedIndex]);
                comboBox7.SelectedIndex = Int32.Parse(cfa.AppSettings.Settings["C3"].Value);

                numericUpDown1.Value = Decimal.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value);
                numericUpDown3.Value = Decimal.Parse(cfa.AppSettings.Settings["TOOL_COUNT"].Value);
                
                //高级
                //ADSL
                List<string> adls = Util.GetAllAdslName();
                int i = 0;
                int idx = -1;
                foreach(string ad in adls)
                {
                    if (ad.Equals(cfa.AppSettings.Settings["REC_FLAG_F11"].Value))
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
                textBox2.Text = cfa.AppSettings.Settings["REC_FLAG_F12"].Value;
                textBox3.Text = cfa.AppSettings.Settings["REC_FLAG_F13"].Value;

                //REC_FLAG
                if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG"].Value))
                {
                    checkBox5.Checked = true;
                }
                else
                {
                    checkBox5.Checked = false;
                }

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
                    checkBox4.Checked = true;
                }
                else
                {
                    checkBox4.Checked = false;
                }

                if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG_F5"].Value))
                {
                    checkBox7.Checked = true;
                }
                else
                {
                    checkBox7.Checked = false;
                }

                if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG_F7"].Value))
                {
                    checkBox1.Checked = true;
                }
                else
                {
                    checkBox1.Checked = false;
                }

                numericUpDown5.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F1"].Value);
                numericUpDown6.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F2"].Value);
                numericUpDown10.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F6"].Value);
                numericUpDown7.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F8"].Value);
                numericUpDown8.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F9"].Value);
                numericUpDown9.Value = Decimal.Parse(cfa.AppSettings.Settings["REC_FLAG_F10"].Value);
                
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
                checkBox1.Enabled = true;
               
                checkBox4.Enabled = true;
                checkBox6.Enabled = true;
                checkBox7.Enabled = true;
                comboBox1.Enabled = true;
                textBox2.Enabled = true;
                textBox3.Enabled = true;

                if (checkBox1.Checked)
                {
                    numericUpDown7.Enabled = true;
                    numericUpDown8.Enabled = true;
                    numericUpDown9.Enabled = true;
                }
                else
                {
                    numericUpDown7.Enabled = false;
                    numericUpDown8.Enabled = false;
                    numericUpDown9.Enabled = false;
                }

                if (checkBox7.Checked)
                {
                    numericUpDown10.Enabled = true;
                }
                else
                {
                    numericUpDown10.Enabled = false;
                }
            }
            else
            {
                numericUpDown5.Enabled = false;
                numericUpDown6.Enabled = false;
                     numericUpDown7.Enabled = false;
                numericUpDown8.Enabled = false;
                 numericUpDown9.Enabled = false;
                numericUpDown10.Enabled = false;
                checkBox1.Enabled = false;
                checkBox4.Enabled = false;
                checkBox6.Enabled = false;
                checkBox7.Enabled = false;
                comboBox1.Enabled = false;
                textBox2.Enabled = false;
                textBox3.Enabled = false;
            }
        }
        

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox1.Checked)
            {
                numericUpDown7.Enabled = true;
                numericUpDown8.Enabled = true;
                numericUpDown9.Enabled = true;
            }
            else
            {
                numericUpDown7.Enabled = false;
                numericUpDown8.Enabled = false;
                numericUpDown9.Enabled = false;
            }
        }

        private void checkBox7_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox7.Checked)
            {
                numericUpDown10.Enabled = true;
            }
            else
            {
                numericUpDown10.Enabled = false;
            }
        }

        private void comboBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            comboBox5.Items.Clear();
            comboBox5.Items.AddRange(cities[comboBox2.SelectedIndex]);
            comboBox5.SelectedIndex = 0;
        }

        private void comboBox3_SelectedIndexChanged(object sender, EventArgs e)
        {
            comboBox6.Items.Clear();
            comboBox6.Items.AddRange(cities[comboBox3.SelectedIndex]);
            comboBox6.SelectedIndex = 0;
        }

        private void comboBox4_SelectedIndexChanged(object sender, EventArgs e)
        {
            comboBox7.Items.Clear();
            comboBox7.Items.AddRange(cities[comboBox4.SelectedIndex]);
            comboBox7.SelectedIndex = 0;
        }

        // #endregion
    }
}
