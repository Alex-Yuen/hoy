package it.hoyland.sclottery;

import java.util.Vector;

import it.hoyland.sclottery.util.StringUtil;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;

public class AddPrinterForm extends Form implements CommandListener,
		DiscoveryListener {

	private LotteryMIDlet midlet;
	private StringItem progress;
	private ChoiceGroup cgBTDiscover;

	private Command cmdBTBack;
	private Command cmdBTExit;
	private Command cmdBTSearch;
	private Command cmdBTSave;

	private LocalDevice localDevice;
	private DiscoveryAgent discoveryAgent;

	public AddPrinterForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;

		this.progress = new StringItem(this.midlet.prop("L21"), this.midlet.prop("L22"));
		this.cgBTDiscover = new ChoiceGroup(this.midlet.prop("L23"), 1, new String[0], new Image[0]);
		this.cgBTDiscover.setSelectedFlags(new boolean[0]);
		this.cgBTDiscover.setFitPolicy(1);

		append(this.progress);
		append(this.cgBTDiscover);

		this.cmdBTBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdBTExit = new Command(this.midlet.prop("L17"), 7, 1);
		this.cmdBTSearch = new Command(this.midlet.prop("L25"), 1, 0);
		this.cmdBTSave = new Command(this.midlet.prop("L26"), 1, 1);

		addCommand(this.cmdBTBack);
		addCommand(this.cmdBTExit);
		addCommand(this.cmdBTSearch);
		addCommand(this.cmdBTSave);

		setCommandListener(this);

	}

	public void commandAction(Command cmd, Displayable dsp) {
		if (cmd == this.cmdBTBack) {
			this.midlet.getDisplay().setCurrent(this.midlet.getMainList());
		} else if (cmd == this.cmdBTExit) {
			this.midlet.exit();
		} else if (cmd == this.cmdBTSearch) { // 搜索

			this.cgBTDiscover.deleteAll();
			try {
				this.localDevice = LocalDevice.getLocalDevice();
				this.discoveryAgent = this.localDevice.getDiscoveryAgent();
				
				Thread.sleep(2000L);

				if (this.discoveryAgent.startInquiry(0x9e8b33, this)) {
					this.progress.setText(this.midlet.prop("L27"));
				} else {
					this.progress.setText(this.midlet.prop("L28"));
				}
			} catch (Exception e) {
				this.midlet.getDisplay().setCurrent(this.midlet.getMessageForm());
				this.midlet.getMessageForm().getItem().setText(e.toString());
			}
		} else if (cmd == this.cmdBTSave) { // 保存
			String item = this.cgBTDiscover.getString(this.cgBTDiscover.getSelectedIndex());
            Vector v = StringUtil.split(item, '=');
            if(!v.isEmpty())
            {
                this.midlet.getRmsUtil().setString("BT", v.elementAt(1).toString().trim());
                try
                {
                	this.midlet.getRmsUtil().save();
                    this.midlet.setBtUrl("btspp://" + this.midlet.getRmsUtil().getString("BT") + ":1;authenticate=false;encrypt=false;master=false");
                }
                catch(Exception e) { 
                	e.printStackTrace();
                }
            }
		}

	}

	public void deviceDiscovered(RemoteDevice device, DeviceClass clazz) {
		  try {
			  (new Thread(new BlueToothRegister(device, this))).start();
		  } catch(Exception e) {
			  this.midlet.getDisplay().setCurrent(this.midlet.getMessageForm());
			  this.midlet.getMessageForm().getItem().setText(e.toString());
		  }

	}

	public void inquiryCompleted(int arg0) {
		this.progress.setText(this.midlet.prop("L29"));

	}

	public void serviceSearchCompleted(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
		// TODO Auto-generated method stub

	}

	public StringItem getProgress(){
		return this.progress;
		
	}
	
	public ChoiceGroup getCgBTDiscover(){
		return this.cgBTDiscover;
	}
}
