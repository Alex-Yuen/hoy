package it.hoyland.sclottery;

import it.hoyland.sclottery.util.Properties;
import it.hoyland.sclottery.util.RMSUtil;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class LotteryMIDlet extends MIDlet {

	private boolean inited;
	private Properties prop;
	private Display display;
	private char status;
	private char subStatus;
	
	private int bint;
	private int aint;
	private RMSUtil rmsUtil;
	private String btUrl;
	
	////////////////////////////////////////////////////////
	private DefaultImageCanvas dicOfLogin;	// login
	private DefaultImageCanvas dicOfPlaceBet; // place bet
	
	////////////////////////////////////////////////////////
	private LangList langList;
	private LoginForm loginForm;
	private MainList mainList;
	private MessageForm messageForm;
	
	private BetForm betForm;
	private BetHistoryForm betHistoryForm;
	private ReprintForm reprintForm;
	private BalanceForm balanceForm;
	private NumMeaningForm numMeaningForm;
	private ResultForm resultForm;
	private PasswordForm passwordForm;
	private PaymentForm paymentForm;
	private TStakesForm tStakesForm;
	private WinningForm winningForm;
	private NameInputForm nameInputForm;
	private AddPrinterForm addPrinterForm;
	

	public LotteryMIDlet() {
		// TODO Auto-generated constructor stub
		this.inited = false;
		this.status = 'Z';
		this.subStatus = '\0';
		try{
			this.rmsUtil = new RMSUtil("preferences");
			setBtUrl("btspp://" + this.rmsUtil.getString("BT") + ":1;authenticate=false;encrypt=false;master=false");
		}catch(Exception e){
			e.printStackTrace();
			this.exit();
		}

	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		if (!this.inited) { // 初始化操作
			try {
				this.display = Display.getDisplay(this);
	
				this.langList = new LangList(this, "Y!", List.IMPLICIT);
				this.display.setCurrent(this.langList);
				this.inited = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void init(){
		try{
			Image image = Image.createImage("/sandglass.png");
			TaskExecutor te = new TaskExecutor();
			te.setTask(new LoginTask(this));
	
			this.dicOfLogin = new DefaultImageCanvas(this, this.display);
			this.dicOfLogin.setTitle(prop("L30"));
			this.dicOfLogin.setContent(prop("L53"));
			this.dicOfLogin.setImage(image);
			this.dicOfLogin.setExecutor(te);
	
			te = new TaskExecutor();
			te.setTask(new PlaceBetTask(this));
			this.dicOfPlaceBet = new DefaultImageCanvas(this, this.display);
			this.dicOfPlaceBet.setTitle(prop("L30"));
			this.dicOfPlaceBet.setContent(prop("L73"));
			this.dicOfPlaceBet.setImage(image);
			this.dicOfPlaceBet.setExecutor(te);
			
			// 初始化窗口
			this.loginForm = new LoginForm(this, prop("L13"));
			this.mainList = new MainList(this, prop("L0"), List.IMPLICIT);
			this.messageForm = new MessageForm(this, "");
			
			this.betForm = new BetForm(this, "");
			this.betHistoryForm = new BetHistoryForm(this, prop("L5"));
			this.reprintForm = new ReprintForm(this, prop("L3"));
			this.balanceForm = new BalanceForm(this, prop("L4"));
			this.numMeaningForm = new NumMeaningForm(this, prop("L11"));
			this.resultForm = new ResultForm(this, prop("L7"));
			this.passwordForm = new PasswordForm(this, prop("L10"));
			this.paymentForm = new PaymentForm(this, prop("L19"));
			this.tStakesForm = new TStakesForm(this, prop("L6"));
			this.winningForm = new WinningForm(this, prop("L8"));
			this.nameInputForm = new NameInputForm(this, prop("L1"));
			this.addPrinterForm = new AddPrinterForm(this, prop("L20"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	                   
	public void setProp(Properties prop) {
		this.prop = prop;
	}

	public Display getDisplay() {
		return this.display;
	}

	public String prop(String key) {
		return (String) this.prop.get(key);
	}

	public void exit() {
		try {
			this.display.setCurrent(null);
			destroyApp(true);
			notifyDestroyed();
		} catch (MIDletStateChangeException e) {
			e.printStackTrace();
		}
	}

	public char getStatus() {
		// TODO Auto-generated method stub
		return this.status;
	}

	public char getSubStatus() {
		return this.subStatus;
	}
	
	public int getBint() {
		return bint;
	}

	public int getAint() {
		return aint;
	}

	public void setStatus(char status) {
		this.status = status;
	}
	
	public void setBint(int bint){
		this.bint = bint;
	}
	
	public DefaultImageCanvas getDicOfLogin() {
		return this.dicOfLogin;
	}
	
	public DefaultImageCanvas getDicOfPlaceBet() {
		return this.dicOfPlaceBet;
	}
	
	public LoginForm getLoginForm(){
		return this.loginForm;
	}
	
	public MainList getMainList(){
		return this.mainList;
	}
	
	public MessageForm getMessageForm(){
		return this.messageForm;
	}

	public BetForm getBetForm() {
		return betForm;
	}

	public BetHistoryForm getBetHistoryForm() {
		return betHistoryForm;
	}

	public ReprintForm getReprintForm() {
		return reprintForm;
	}

	public BalanceForm getBalanceForm() {
		return balanceForm;
	}

	public NumMeaningForm getNumMeaningForm() {
		return numMeaningForm;
	}

	public ResultForm getResultForm() {
		return resultForm;
	}

	public PasswordForm getPasswordForm() {
		return passwordForm;
	}

	public PaymentForm getPaymentForm() {
		return paymentForm;
	}

	public TStakesForm getTStakesForm() {
		return tStakesForm;
	}

	public WinningForm getWinningForm() {
		return winningForm;
	}

	public NameInputForm getNameInputForm() {
		return nameInputForm;
	}

	public AddPrinterForm getAddPrinterForm() {
		return addPrinterForm;
	}

	public RMSUtil getRmsUtil() {
		return rmsUtil;
	}

	public String getBtUrl() {
		return btUrl;
	}

	public void setBtUrl(String btUrl) {
		this.btUrl = btUrl;
	}
	
}
