package it.hoyland.sclottery;

import javax.bluetooth.RemoteDevice;

public class BlueToothRegister implements Runnable {

	private RemoteDevice device;
	private AddPrinterForm form;
	private String friendlyName;

	public BlueToothRegister(RemoteDevice device, AddPrinterForm form) {
		this.device = device;
		this.form = form;

	}

	public void run() {
		try {
			this.friendlyName = this.device.getFriendlyName(false);
			this.form.getCgBTDiscover().append(this.friendlyName + "=" + this.device.getBluetoothAddress(), null);
		} catch (Exception e) {
			this.form.getProgress().setText("* Ex: when getFriendlyName : " + e);
		}

	}

}
