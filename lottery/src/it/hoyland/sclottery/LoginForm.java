package it.hoyland.sclottery;

import java.io.InputStream;
import java.io.OutputStream;

//import game4d.classic4d.mobile.CommandMessage;
//import game4d.classic4d.mobile.info.BaseInfo;
//import game4d.classic4d.mobile.info.PaymentInfo;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;

import org.json.me.JSONObject;


import com.caucho.hessian.micro.MicroHessianInput;
import com.caucho.hessian.micro.MicroHessianOutput;

//import test.Basic;
import util.RetObj;
//import vo.Hoy;
import vo.Hoy;

public class LoginForm extends Form implements CommandListener {

	private LotteryMIDlet midlet;
	private TextField imie;
	private TextField loginId;
	private TextField loginPass;
	private TextField address;

	private Command cmdLogin;
	private Command cmdExit;

	public LoginForm(LotteryMIDlet midlet, String title) {
		super(title);
		this.midlet = midlet;
		this.imie = new TextField("IMEI", "", 120, 0xc0000);
		this.loginId = new TextField(this.midlet.prop("L14"), "", 120, 0xc0000);
		this.loginPass = new TextField(this.midlet.prop("L10"), "", 50, 0xd0000);
		this.address = new TextField(this.midlet.prop("L15"), "", 120, 0xc0004);

		append(this.imie);
		append(this.loginId);
		append(this.loginPass);
		append(this.address);

		this.cmdLogin = new Command(this.midlet.prop("L16"), 1, 1);
		this.cmdExit = new Command(this.midlet.prop("L17"), 7, 1);

		addCommand(cmdExit);
		addCommand(cmdLogin);
		setCommandListener(this);

	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		// 登录窗口
		if (dsp == this) {
			if (cmd == cmdExit) {
				this.midlet.exit();
			} else if (cmd == cmdLogin) {
//				DefaultImageCanvas dic = this.midlet.getDicOfLogin();
//				dic.setCommandListener(this);
//				this.midlet.getDisplay().setCurrent(dic);
//				RetObj r = null;
				try {
//					BaseInfo info = new PaymentInfo();
//					info.setCommandLine(CommandMessage.PAYMENT);
//					info.setCustomerId("myr000");
//					String sessionId = "1234";
					
//					r = (RetObj) J2meNetUtil.invoke(
//							"http://218.16.120.102:8080/AppServer/service/game/classic4d/GameManager",
//							"mobileBet", 
//							new Object[]{sessionId, info});				
//					System.out.println(r);
//					Hoy hoy = null;
//					String nax = (String)J2meNetUtil.invoke(
//							"http://127.0.0.1/hessian/basicservice", 
//							"getUserName", 
//							new Object[]{});
//					System.out.println(nax);
					
					//MicroHessianInput in = new MicroHessianInput();

					String url = "http://127.0.0.1/hessian/basicservice";

					HttpConnection c = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);

					c.setRequestMethod(HttpConnection.POST);

					OutputStream os = c.openOutputStream();
					MicroHessianOutput out = new MicroHessianOutput(os);
					
					//out.startCall("getUserName");
					out.startCall("getHoy2");
					 Hoy hoy = new Hoy();
			            hoy.setId(1234l);
			            hoy.setName("abcd");
			            System.out.println(hoy.getId()+":"+hoy.getName());
			            JSONObject jsonObj = new JSONObject();
			            jsonObj.put("id", hoy.getId());
			            jsonObj.put("name", hoy.getName());
			            out.writeString(jsonObj.toString());
//				    if (args != null)
//				    {
//				      for (int i = 0; i < args.length; i++)
//				        out.writeObject(args[i]);
//				    }
					out.completeCall();
					//os.flush();
					os.close();

					InputStream is = c.openInputStream();

					MicroHessianInput in = new MicroHessianInput(is);
					in.startReply();
					String ret = in.readString();

					in.completeReply();
					c.close();

					//System.out.println(ret);
					
					 jsonObj = new JSONObject(ret);
						Hoy hoy2 = new Hoy();
						hoy2.setId(jsonObj.getLong("id"));
						hoy2.setName(jsonObj.getString("name"));
						System.out.println(hoy2.getId()+":"+hoy2.getName());
						
					//is.close();
//					HessianProxyFactory factory = new HessianProxyFactory();
//			        Basic basic = (Basic) factory.create(Basic.class, "http://127.0.0.1/hessian/basicservice");
//			        System.out.println("从servlet返回的用户名："+basic.getUserName());
//			        System.out.println("hoy:"+basic.getHoy().getId()+":"+basic.getHoy().getName());
//					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// 等待窗口
		if (dsp == this.midlet.getDicOfLogin()) {
			try {
				if (cmd == DefaultImageCanvas.cmdFailure && cmd != DefaultImageCanvas.cmdSuccess) { // 登录失败
					Image image = Image.createImage("/D88/alert.png");
					Alert alert = new Alert(this.midlet.prop("L18"), "", image, AlertType.WARNING);
					this.midlet.getDisplay().setCurrent(alert, this);
				} // 成功此处不做处理？
//				else{
//					this.midlet.getDisplay().setCurrent(new ReprintForm(this.midlet, "EF"));
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
