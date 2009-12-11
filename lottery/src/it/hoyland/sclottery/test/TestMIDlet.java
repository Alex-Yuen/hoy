package it.hoyland.sclottery.test;

import game4d.classic4d.mobile.info.BalanceInfo;
import game4d.classic4d.mobile.info.BaseInfo;

import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.caucho.hessian.micro.MicroHessianInput;
import com.caucho.hessian.micro.MicroHessianOutput;

public class TestMIDlet extends MIDlet {

	public TestMIDlet() {
		// TODO Auto-generated constructor stub
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		System.out.println("Ready to test...");
		try {
			String url = "http://localhost/hessian/service";

			HttpConnection c = (HttpConnection) Connector.open(url,
					Connector.READ_WRITE, true);

			c.setRequestMethod(HttpConnection.POST);

			OutputStream os = c.openOutputStream();
			MicroHessianOutput out = new MicroHessianOutput(os);

			out.startCall("mobileBet");
			BaseInfo info = new BalanceInfo();
	        info.setCustomerId("bicid");
	        
			Object[] args = {"1234", info};
			if (args != null) {
				for (int i = 0; i < args.length; i++)
					out.writeObject(args[i]);
			}
			out.completeCall();
			os.close();

			InputStream is = c.openInputStream();

			MicroHessianInput in = new MicroHessianInput(is);
			in.startReply();
			Object ret = in.readObject(null);

			in.completeReply();
			c.close();

			System.out.println(ret.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
