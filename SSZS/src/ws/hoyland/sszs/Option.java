package ws.hoyland.sszs;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Group;

public class Option extends Dialog implements Observer {

	protected Object result;
	protected Shell shell;
	private Text text;
	private Text text_1;
	private Configuration configuration = Configuration.getInstance();
	private Combo combo;
	private Combo combo_1;
	private Combo combo_3;
	private Combo combo_5;
	private Combo combo_2;
	private Combo combo_4;
	private Combo combo_6;
	
	private String[][] cities = {
			{"城市"},
			{"北京"},
			{"上海"},
			{"天津"},
			{"重庆"},
			{"请选择城市","忘记了","石家庄","唐山","秦皇岛","邯郸","邢台","保定","张家口","承德","沧州","廊坊","衡水","其他"},
			{"请选择城市","忘记了","太原","大同","阳泉","长治","晋城","朔州","晋中","运城","忻州","临汾","吕梁"},
			{"请选择城市","忘记了","呼和浩特","包头","乌海","赤峰","通辽","鄂尔多斯","呼伦贝尔","乌兰察布盟","锡林郭勒盟","巴彦淖尔盟","阿拉善盟","兴安盟"},
			{"请选择城市","忘记了","沈阳","大连","鞍山","抚顺","本溪","丹东","锦州","葫芦岛","营口","盘锦","阜新","辽阳","铁岭","朝阳"},
			{"请选择城市","忘记了","长春","吉林市","四平","辽源","通化","白山","松原","白城","延边朝鲜族自治州"},
			{"请选择城市","忘记了","哈尔滨","齐齐哈尔","鹤岗","双鸭山","鸡西","大庆","伊春","牡丹江","佳木斯","七台河","黑河","绥化","大兴安岭"},
			{"请选择城市","忘记了","南京","无锡","徐州","常州","苏州","南通","连云港","淮安","盐城","扬州","镇江","泰州","宿迁","昆山"},
			{"请选择城市","忘记了","杭州","宁波","温州","嘉兴","湖州","绍兴","金华","衢州舟山","台州","丽水"},
			{"请选择城市","忘记了","合肥","芜湖","蚌埠","淮南","马鞍山","淮北","铜陵","安庆","黄山","滁州","阜阳","宿州","巢湖","六安","亳州","池州","宣城"},
			{"请选择城市","忘记了","福州","厦门","莆田","三明","泉州","漳州","南平","龙岩","宁德"},
			{"请选择城市","忘记了","南昌","景德镇","萍乡","新余","九江","鹰潭","赣州","吉安","宜春","抚州","上饶"},
			{"请选择城市","忘记了","济南","青岛","淄博","枣庄","东营","潍坊","烟台","威海","济宁","泰安","日照","莱芜","德州","临沂","聊城","滨州","菏泽"},
			{"请选择城市","忘记了","郑州","开封","洛阳","平顶山","焦作","鹤壁","新乡","安阳","濮阳","许昌","漯河","三门峡","南阳","商丘","信阳","周口","驻马店","济源"},
			{"请选择城市","忘记了","武汉","黄石","襄樊","十堰","荆州","宜昌","荆门","鄂州","孝感","黄冈","咸宁","随州","仙桃","天门","潜江","神农架","恩施土家族苗族自治州"},
			{"请选择城市","忘记了","长沙","株洲","永州","湘潭","衡阳","邵阳","岳阳","常德","张家界","益阳","郴州","怀化","娄底","湘西土家族苗族自治州"},
			{"请选择城市","忘记了","广州","深圳","珠海","汕头","韶关","佛山","江门","湛江","茂名","肇庆","惠州","梅州","汕尾","河源","阳江","清远","东莞","中山","潮州","揭阳","云浮"},
			{"请选择城市","忘记了","南宁","柳州","桂林","梧州","北海","防城港","钦州","贵港","玉林","百色","贺州","河池","来宾","崇左"},
			{"请选择城市","忘记了","海口","三亚","五指山","琼海","儋州","文昌","万宁","东方","澄迈","定安","屯昌","临高","白沙黎族自治县昌","江黎族自治县","乐东黎族自治县","陵水黎族自治县","保亭黎族苗族自治县","琼中黎族苗族自治县"},
			{"请选择城市","忘记了","成都","自贡","攀枝花","泸州","德阳","绵阳","广元","遂宁","内江","乐山","南充","宜宾","广安","达州","眉山","雅安","巴中","资阳","阿坝藏族羌族自治州","甘孜藏族自治州","凉山彝族自治州"},
			{"请选择城市","忘记了","贵阳","六盘水","遵义","安顺","铜仁","毕节","黔西南布依族苗族自治州","黔东南苗族侗族自治州","黔南布依族苗族自治州"},
			{"请选择城市","忘记了","昆明","曲靖","玉溪","保山","昭通","丽江","思茅","临沧","文山壮族苗族自治州","红河哈尼族彝族自治州","西双版纳傣族自治州","楚雄彝族自治州","大理白族自治州","德宏傣族景颇族自治州 ","怒江傈傈族自治州","迪庆藏族自治州"},
			{"请选择城市","忘记了","拉萨","那曲","昌都","山南","日喀则","阿里","林芝"},
			{"请选择城市","忘记了","西安","铜川","宝鸡","咸阳","渭南","延安","汉中","榆林","安康","商洛"},
			{"请选择城市","忘记了","兰州","金昌","白银","天水","嘉峪关","武威","张掖","平凉","酒泉","庆阳","定西","陇南","临夏回族自治州","甘南藏族自治州"},
			{"请选择城市","忘记了","西宁","海东","海北藏族自治州","黄南藏族自治州","海南藏族自治州","果洛藏族自治州","玉树藏族自治州","海西蒙古族藏族自治州"},
			{"请选择城市","忘记了","银川","石嘴山","吴忠","固原"},
			{"请选择城市","忘记了","乌鲁木齐","克拉玛依","石河子","阿拉尔","图木舒克","五家渠","吐鲁番","哈密","和田","阿克苏","喀什","克孜勒苏柯尔克孜自治州","巴音郭楞蒙古自治州","昌吉回族自治州","博尔塔拉蒙古自治州","伊犁哈萨克自治州"},
			{"香港"},
			{"澳门"},
			{"请选择城市","忘记了","台北","高雄","基隆","台中","台南","新竹","嘉义","台北县","宜兰县","新竹县","桃园县","苗栗县","台中县","彰化县","南投县","嘉义县","云林县","台南县","高雄县","屏东县","台东县","花莲县","澎湖县"}
	};
	private Spinner spinner;
	private Spinner spinner_1;
	private Spinner spinner_2;
	private Spinner spinner_3;
	private Button btnCheckButton;
	private Spinner spinner_4;
	private Spinner spinner_5;
	private Button btnIp;
	private Button button;
	private Spinner spinner_6;
	private Spinner spinner_7;
	private Button btnCheckButton_1;
	private Spinner spinner_9;
	private Spinner spinner_10;
	private Spinner spinner_11;
	private Spinner spinner_8;
	private Spinner spinner_12;
	private Spinner spinner_13;
	private Spinner spinner_14;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Option(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		Engine.getInstance().addObserver(this);
	}
	
	private void load(){
		//load & show
		try{
//			if(!flag){
//				InputStream is = Option.class.getResourceAsStream("/qm.ini");
//				this.configuration.load(is);
//				is.close();
//			}			
			if(this.configuration.size()>0){
				combo_1.select(Integer.parseInt(this.configuration.getProperty("P1")));
				combo_2.setItems(cities[combo_1.getSelectionIndex()]);
				combo_2.select(Integer.parseInt(this.configuration.getProperty("C1")));
				
				combo_3.select(Integer.parseInt(this.configuration.getProperty("P2")));
				combo_4.setItems(cities[combo_3.getSelectionIndex()]);
				combo_4.select(Integer.parseInt(this.configuration.getProperty("C2")));
				
				combo_5.select(Integer.parseInt(this.configuration.getProperty("P3")));
				combo_6.setItems(cities[combo_5.getSelectionIndex()]);
				combo_6.select(Integer.parseInt(this.configuration.getProperty("C3")));
				
				spinner.setSelection(Integer.parseInt(this.configuration.getProperty("EMAIL_TIMES")));
				spinner_12.setSelection(Integer.parseInt(this.configuration.getProperty("TRY_TIMES")));		
				spinner_13.setSelection(Integer.parseInt(this.configuration.getProperty("APPEAL_DELAY_MIN")));
				spinner_14.setSelection(Integer.parseInt(this.configuration.getProperty("APPEAL_DELAY_MAX")));
				
				//spinner_1.setSelection(Integer.parseInt(this.configuration.getProperty("TOKEN_QUANTITY")));
//				if(Integer.parseInt(this.configuration.getProperty("RECONN_GROUP_QUANTITY_FLAG"))==1){
//					btnCheckButton.setSelection(true);
//					spinner_2.setEnabled(true);
//					spinner_2.setSelection(Integer.parseInt(this.configuration.getProperty("RECONN_GROUP_QUANTITY")));
//				}else{
//					btnCheckButton.setSelection(false);
//					spinner_2.setEnabled(false);
//				}
				
//				if(Integer.parseInt(this.configuration.getProperty("RECONN_ACCOUNT_QUANTITY_FLAG"))==1){
//					btnCheckButton_1.setSelection(true);
//					spinner_3.setEnabled(true);
//					spinner_3.setSelection(Integer.parseInt(this.configuration.getProperty("RECONN_ACCOUNT_QUANTITY")));
//				}else{
//					btnCheckButton_1.setSelection(false);
//					spinner_3.setEnabled(false);
//				}
				
				text.setText(this.configuration.getProperty("ADSL_ACCOUNT"));
				text_1.setText(this.configuration.getProperty("ADSL_PASSWORD"));
				
				spinner_1.setSelection(Integer.parseInt(this.configuration.getProperty("THREAD_COUNT")));
				
				spinner_2.setSelection(Integer.parseInt(this.configuration.getProperty("AUTO_RECON")));
				spinner_3.setSelection(Integer.parseInt(this.configuration.getProperty("RECON_DELAY")));
				
				spinner_4.setSelection(Integer.parseInt(this.configuration.getProperty("READ_TC")));
				spinner_5.setSelection(Integer.parseInt(this.configuration.getProperty("MAIL_ITV")));
				
				if("true".equals(configuration.getProperty("AWCONN"))){
					btnCheckButton.setSelection(true);
				}else{
					btnCheckButton.setSelection(false);
				}
				
				if("true".equals(configuration.getProperty("REC_TYPE"))){
					btnIp.setSelection(true);
				}else{
					btnIp.setSelection(false);
				}
				if("true".equals(configuration.getProperty("ACC_ITV_FLAG"))){
					button.setSelection(true);
					spinner_6.setEnabled(true);
					spinner_7.setEnabled(true);
				}else{
					button.setSelection(false);
					spinner_6.setEnabled(false);
					spinner_7.setEnabled(false);
				}
				spinner_6.setSelection(Integer.parseInt(this.configuration.getProperty("ACC_ITV_COUNT")));
				spinner_7.setSelection(Integer.parseInt(this.configuration.getProperty("ACC_ITV_PERIOD")));
				//btnCheckButton.setSelection(Boolean.parseBoolean(this.configuration.getProperty("AWCONN")));
				
				spinner_8.setSelection(Integer.parseInt(this.configuration.getProperty("REC_ITV")));
				
				spinner_9.setSelection(Integer.parseInt(this.configuration.getProperty("IP3_1")));
				spinner_10.setSelection(Integer.parseInt(this.configuration.getProperty("IP3_2")));
				spinner_11.setSelection(Integer.parseInt(this.configuration.getProperty("IP3_3")));
				
				if("true".equals(configuration.getProperty("IP3FLAG"))){
					btnCheckButton_1.setSelection(true);
					spinner_9.setEnabled(true);
					spinner_10.setEnabled(true);
					spinner_11.setEnabled(true);
				}else{
					btnCheckButton_1.setSelection(false);
					spinner_9.setEnabled(false);
					spinner_10.setEnabled(false);
					spinner_11.setEnabled(false);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void save(){
		this.configuration.put("EMAIL_TIMES", spinner.getText());
		this.configuration.put("TRY_TIMES", spinner_12.getText());
		
		this.configuration.put("APPEAL_DELAY_MIN", spinner_13.getText());
		this.configuration.put("APPEAL_DELAY_MAX", spinner_14.getText());
		
//		spinner_13.setSelection(Integer.parseInt(this.configuration.getProperty("")));
//		spinner_14.setSelection(Integer.parseInt(this.configuration.getProperty("")));
		
//		this.configuration.put("RECONN_GROUP_QUANTITY_FLAG", btnCheckButton.getSelection()?"1":"0");
//		this.configuration.put("RECONN_GROUP_QUANTITY", spinner_2.getText());
//		this.configuration.put("RECONN_ACCOUNT_QUANTITY_FLAG", btnCheckButton_1.getSelection()?"1":"0");
//		this.configuration.put("RECONN_ACCOUNT_QUANTITY", spinner_3.getText());
		this.configuration.put("ADSL_ACCOUNT", text.getText());
		this.configuration.put("ADSL_PASSWORD", text_1.getText());
		this.configuration.put("THREAD_COUNT", spinner_1.getText());
		this.configuration.put("READ_TC", spinner_4.getText());
		this.configuration.put("MAIL_ITV", spinner_5.getText());
		
		this.configuration.put("P1", String.valueOf(combo_1.getSelectionIndex()));
		this.configuration.put("C1", String.valueOf(combo_2.getSelectionIndex()));
		this.configuration.put("P2", String.valueOf(combo_3.getSelectionIndex()));
		this.configuration.put("C2", String.valueOf(combo_4.getSelectionIndex()));
		this.configuration.put("P3", String.valueOf(combo_5.getSelectionIndex()));
		this.configuration.put("C3", String.valueOf(combo_6.getSelectionIndex()));
		
		this.configuration.put("AUTO_RECON", spinner_2.getText());
		this.configuration.put("RECON_DELAY", spinner_3.getText());
		this.configuration.put("AWCONN", String.valueOf(btnCheckButton.getSelection()));
		this.configuration.put("REC_TYPE", String.valueOf(btnIp.getSelection()));
		
		this.configuration.put("ACC_ITV_FLAG", String.valueOf(button.getSelection()));		
		this.configuration.put("ACC_ITV_COUNT", spinner_6.getText());
		this.configuration.put("ACC_ITV_PERIOD", spinner_7.getText());
				
		this.configuration.put("IP3FLAG", String.valueOf(btnCheckButton_1.getSelection()));		
		
		this.configuration.put("REC_ITV", spinner_8.getText());
		this.configuration.put("IP3_1", spinner_9.getText());
		this.configuration.put("IP3_2", spinner_10.getText());
		this.configuration.put("IP3_3", spinner_11.getText());
		
		this.configuration.save();
	}


	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		load();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

//	public void close(){
//		this.shell.setVisible(false);
//	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				Engine.getInstance().deleteObserver(Option.this);
			}
		});
//		shell.addShellListener(new ShellAdapter() {
//			@Override
//			public void shellClosed(ShellEvent e) {
//				shell.setVisible(false);
//				e.doit = false;
//			}
//		});
		
		shell.setSize(426, 280);
		shell.setText("设置");
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(-1, 2, 424, 215);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("常规");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		
		Group group = new Group(composite, SWT.NONE);
		group.setText("常用QQ的地点");
		group.setBounds(0, 67, 416, 118);
		
		Label label = new Label(group, SWT.NONE);
		label.setText("2014年");
		label.setBounds(9, 25, 46, 17);
		
		combo_1 = new Combo(group, SWT.NONE);
		combo_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				combo_2.removeAll();
				combo_2.setItems(cities[combo_1.getSelectionIndex()]);
				combo_2.select(0);
			}
		});
		combo_1.setItems(new String[] {"省份", "北京", "上海", "天津", "重庆", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南", "广东", "广西", "海南", "四川", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆", "香港", "澳门", "台湾"});
		combo_1.setBounds(61, 21, 69, 25);
		combo_1.select(0);
		
		combo_2 = new Combo(group, SWT.NONE);
		combo_2.setItems(new String[] {"城市"});
		combo_2.setBounds(136, 21, 174, 25);
		combo_2.select(0);
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setText("2013年");
		label_4.setBounds(9, 57, 46, 17);
		
		combo_3 = new Combo(group, SWT.NONE);
		combo_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				combo_4.removeAll();
				combo_4.setItems(cities[combo_3.getSelectionIndex()]);
				combo_4.select(0);
			}
		});
		combo_3.setItems(new String[] {"省份", "北京", "上海", "天津", "重庆", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南", "广东", "广西", "海南", "四川", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆", "香港", "澳门", "台湾"});
		combo_3.setBounds(61, 53, 69, 25);
		combo_3.select(0);
		
		combo_4 = new Combo(group, SWT.NONE);
		combo_4.setItems(new String[] {"城市"});
		combo_4.setBounds(136, 53, 174, 25);
		combo_4.select(0);
		
		Label label_6 = new Label(group, SWT.NONE);
		label_6.setText("2012年");
		label_6.setBounds(9, 88, 46, 17);
		
		combo_5 = new Combo(group, SWT.NONE);
		combo_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				combo_6.removeAll();
				combo_6.setItems(cities[combo_5.getSelectionIndex()]);
				combo_6.select(0);
			}
		});
		combo_5.setItems(new String[] {"省份", "北京", "上海", "天津", "重庆", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南", "广东", "广西", "海南", "四川", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆", "香港", "澳门", "台湾"});
		combo_5.setBounds(61, 84, 69, 25);
		combo_5.select(0);
		
		combo_6 = new Combo(group, SWT.NONE);
		combo_6.setItems(new String[] {"城市"});
		combo_6.setBounds(136, 84, 174, 25);
		combo_6.select(0);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setText("邮箱复用:");
		label_2.setBounds(10, 10, 61, 17);
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setText("线程数量:");
		label_3.setBounds(10, 38, 61, 17);
		
		spinner = new Spinner(composite, SWT.BORDER);
		spinner.setMaximum(10);
		spinner.setMinimum(1);
		spinner.setSelection(2);
		spinner.setBounds(77, 10, 47, 20);
		
		spinner_1 = new Spinner(composite, SWT.BORDER);
		spinner_1.setMinimum(1);
		spinner_1.setSelection(1);
		spinner_1.setBounds(77, 36, 47, 20);
		
		spinner_5 = new Spinner(composite, SWT.BORDER);
		spinner_5.setMaximum(50);
		spinner_5.setSelection(3);
		spinner_5.setBounds(238, 7, 47, 23);
		
		Label label_10 = new Label(composite, SWT.NONE);
		label_10.setText("邮箱间隔(秒):");
		label_10.setBounds(153, 10, 79, 17);
		
		spinner_6 = new Spinner(composite, SWT.BORDER);
		spinner_6.setEnabled(false);
		spinner_6.setMaximum(999);
		spinner_6.setMinimum(1);
		spinner_6.setSelection(5);
		spinner_6.setBounds(191, 35, 47, 23);
		
		Label label_12 = new Label(composite, SWT.NONE);
		label_12.setText("个帐号，暂停");
		label_12.setBounds(244, 38, 80, 17);
		
		spinner_7 = new Spinner(composite, SWT.BORDER);
		spinner_7.setEnabled(false);
		spinner_7.setMaximum(2000);
		spinner_7.setMinimum(1);
		spinner_7.setSelection(1);
		spinner_7.setBounds(330, 35, 47, 23);
		
		Label label_13 = new Label(composite, SWT.NONE);
		label_13.setText("分钟");
		label_13.setBounds(383, 38, 29, 17);
		
		button = new Button(composite, SWT.CHECK);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(button.getSelection()){
					spinner_6.setEnabled(true);
					spinner_7.setEnabled(true);
				}else{
					spinner_6.setEnabled(false);
					spinner_7.setEnabled(false);
				}
			}
		});
		button.setBounds(153, 38, 33, 17);
		button.setText("每");
		
		Label label_15 = new Label(composite, SWT.NONE);
		label_15.setText("重试:");
		label_15.setBounds(291, 10, 33, 17);
		
		spinner_12 = new Spinner(composite, SWT.BORDER);
		spinner_12.setMaximum(2000);
		spinner_12.setMinimum(1);
		spinner_12.setSelection(1);
		spinner_12.setBounds(330, 7, 47, 23);
		
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("高级");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(composite_1);
		
		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setEnabled(false);
		lblNewLabel_1.setBounds(10, 69, 61, 17);
		lblNewLabel_1.setText("宽带连接:");
		
		combo = new Combo(composite_1, SWT.NONE);
		combo.setEnabled(false);
		combo.setBounds(77, 69, 88, 23);
		combo.setText("宽带连接");
		
		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setBounds(10, 99, 61, 17);
		lblNewLabel_2.setText("宽带帐号:");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setBounds(77, 99, 139, 20);
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setText("宽带密码:");
		label_1.setBounds(10, 125, 61, 17);
		
		text_1 = new Text(composite_1, SWT.BORDER | SWT.PASSWORD);
		text_1.setBounds(77, 125, 139, 20);
		
		Label label_5 = new Label(composite_1, SWT.NONE);
		label_5.setText("自动拨号:");
		label_5.setBounds(10, 14, 61, 17);
		
		spinner_2 = new Spinner(composite_1, SWT.BORDER);
		spinner_2.setMaximum(99);
		spinner_2.setBounds(77, 11, 45, 23);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBounds(128, 14, 37, 17);
		lblNewLabel.setText("个号码");
		
		Label label_7 = new Label(composite_1, SWT.NONE);
		label_7.setText("拨号延时:");
		label_7.setBounds(10, 43, 61, 17);
		
		spinner_3 = new Spinner(composite_1, SWT.BORDER);
		spinner_3.setMaximum(99);
		spinner_3.setBounds(77, 40, 45, 23);
		
		Label label_8 = new Label(composite_1, SWT.NONE);
		label_8.setText("秒");
		label_8.setBounds(128, 43, 37, 17);
		
		btnCheckButton = new Button(composite_1, SWT.CHECK);
		btnCheckButton.setBounds(235, 14, 120, 17);
		btnCheckButton.setText("无限重拨(默认3次)");
		
		spinner_4 = new Spinner(composite_1, SWT.BORDER);
		spinner_4.setMaximum(10);
		spinner_4.setMinimum(1);
		spinner_4.setSelection(2);
		spinner_4.setBounds(77, 152, 45, 23);
		
		Label label_9 = new Label(composite_1, SWT.NONE);
		label_9.setText("读取线程:");
		label_9.setBounds(10, 155, 61, 17);
		
		btnIp = new Button(composite_1, SWT.CHECK);
		btnIp.setText("IP类似重拨");
		btnIp.setBounds(235, 43, 120, 17);
		
		spinner_8 = new Spinner(composite_1, SWT.BORDER);
		spinner_8.setMaximum(48);
		spinner_8.setMinimum(1);
		spinner_8.setSelection(2);
		spinner_8.setBounds(235, 122, 45, 23);
		
		Label lblNewLabel_3 = new Label(composite_1, SWT.NONE);
		lblNewLabel_3.setBounds(286, 125, 100, 17);
		lblNewLabel_3.setText("小时内IP重复重拨");
		
		btnCheckButton_1 = new Button(composite_1, SWT.CHECK);
		btnCheckButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnCheckButton_1.getSelection()){
					spinner_9.setEnabled(true);
					spinner_10.setEnabled(true);
					spinner_11.setEnabled(true);
				}else{
					spinner_9.setEnabled(false);
					spinner_10.setEnabled(false);
					spinner_11.setEnabled(false);
				}
			}
		});
		btnCheckButton_1.setBounds(235, 69, 139, 17);
		btnCheckButton_1.setText("前三段非以下IP重拨:");
		
		spinner_9 = new Spinner(composite_1, SWT.BORDER);
		spinner_9.setMaximum(255);
		spinner_9.setSelection(2);
		spinner_9.setBounds(235, 92, 45, 23);
		
		spinner_10 = new Spinner(composite_1, SWT.BORDER);
		spinner_10.setMaximum(255);
		spinner_10.setSelection(2);
		spinner_10.setBounds(296, 92, 45, 23);
		
		spinner_11 = new Spinner(composite_1, SWT.BORDER);
		spinner_11.setMaximum(255);
		spinner_11.setSelection(2);
		spinner_11.setBounds(361, 92, 45, 23);
		
		Label label_11 = new Label(composite_1, SWT.NONE);
		label_11.setText(".");
		label_11.setBounds(282, 99, 8, 17);
		
		Label label_14 = new Label(composite_1, SWT.NONE);
		label_14.setText(".");
		label_14.setBounds(347, 99, 8, 17);
		
		Label label_16 = new Label(composite_1, SWT.NONE);
		label_16.setText("提交延迟:");
		label_16.setBounds(235, 155, 61, 17);
		
		spinner_13 = new Spinner(composite_1, SWT.BORDER);
		spinner_13.setMaximum(600);
		spinner_13.setSelection(21);
		spinner_13.setBounds(296, 152, 37, 23);
		
		Label label_17 = new Label(composite_1, SWT.NONE);
		label_17.setBounds(339, 155, 10, 17);
		label_17.setText("-");
		
		spinner_14 = new Spinner(composite_1, SWT.BORDER);
		spinner_14.setMaximum(600);
		spinner_14.setSelection(30);
		spinner_14.setBounds(349, 152, 37, 23);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//save configuration
				save();
				Option.this.shell.dispose();
			}
		});
		btnNewButton.setBounds(236, 221, 80, 27);
		btnNewButton.setText("确定(&O)");
		
		Button btnc = new Button(shell, SWT.NONE);
		btnc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Option.this.shell.dispose();
			}
		});
		btnc.setText("取消(&C)");
		btnc.setBounds(338, 221, 80, 27);
	}

	@Override
	public void update(Observable obj, Object arg) {
		// TODO Auto-generated method stub
		// 接收来自Engine的消息
	}
}
