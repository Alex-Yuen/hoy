package it.hoyland.sclottery;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

public class MessageForm extends Form implements CommandListener {
	
	private LotteryMIDlet midlet;
	private TextField sms;
	private StringItem item;
	
	private Command cmdBack;
	private Command cmdPrint;
	private Command cmdSMS;

	public MessageForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		this.sms = new TextField(this.midlet.prop("L58"), null, 30, 3);
		this.item = new StringItem("", this.midlet.prop("L59"));
		this.item.setFont(Font.getFont(0, 0, 16));
		
		this.append(this.sms);
		this.append(this.item);
		
		this.cmdBack =  new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdPrint =  new Command(this.midlet.prop("L60"), 2, 1);
		this.cmdSMS =  new Command(this.midlet.prop("L61"), 2, 1);
		
		addCommand(cmdBack);
		addCommand(cmdPrint);
		addCommand(cmdSMS);
		
		setCommandListener(this);
	}

	public void commandAction(Command cmd, Displayable dsp) {
		if(cmd==cmdBack){
//			switch(b_char_fld)
//            {
//            case 90: // 'Z'
//                getDisplay().setCurrent(get_frmLogin());
//                c_javax_microedition_lcdui_TextField_fld.setString("");
//                return;
//
//            case 48: // '0'
//                getDisplay().setCurrent(get_lstMain());
//                return;
//
//            case 49: // '1'
//                getDisplay().setCurrent(get_frmNameInput());
//                return;
//
//            case 50: // '2'
//                getDisplay().setCurrent(get_frmBet());
//                return;
//
//            case 51: // '3'
//                getDisplay().setCurrent(get_frmReprint());
//                return;
//
//            case 52: // '4'
//                getDisplay().setCurrent(get_frmBalance());
//                return;
//
//            case 53: // '5'
//                getDisplay().setCurrent(get_frmBetHist());
//                return;
//
//            case 54: // '6'
//                getDisplay().setCurrent(get_frmTStakes());
//                return;
//
//            case 55: // '7'
//                getDisplay().setCurrent(get_frmResult());
//                return;
//
//            case 56: // '8'
//                getDisplay().setCurrent(get_frmWinning());
//                return;
//
//            case 57: // '9'
//                getDisplay().setCurrent(get_frmPayment());
//                return;
//
//            case 65: // 'A'
//                getDisplay().setCurrent(get_frmPassword());
//                return;
//
//            case 66: // 'B'
//                getDisplay().setCurrent(get_frmNumMeaning());
//                return;
//
//            case 67: // 'C'
//                getDisplay().setCurrent(get_frmAddPrinter());
//                // fall through
//
//            case 58: // ':'
//            case 59: // ';'
//            case 60: // '<'
//            case 61: // '='
//            case 62: // '>'
//            case 63: // '?'
//            case 64: // '@'
//            case 68: // 'D'
//            case 69: // 'E'
//            case 70: // 'F'
//            case 71: // 'G'
//            case 72: // 'H'
//            case 73: // 'I'
//            case 74: // 'J'
//            case 75: // 'K'
//            case 76: // 'L'
//            case 77: // 'M'
//            case 78: // 'N'
//            case 79: // 'O'
//            case 80: // 'P'
//            case 81: // 'Q'
//            case 82: // 'R'
//            case 83: // 'S'
//            case 84: // 'T'
//            case 85: // 'U'
//            case 86: // 'V'
//            case 87: // 'W'
//            case 88: // 'X'
//            case 89: // 'Y'
//            default:
//                getDisplay().setCurrent(get_lstMain());
//                return;
//            }
		}else if(cmd==cmdPrint){ // 蓝牙
//	        try
//	        {
//	            StreamConnection streamconnection = (StreamConnection)Connector.open(g_java_lang_String_fld, 2);
//	            Thread.sleep(1000L);
//	            DataOutputStream dataoutputstream = streamconnection.openDataOutputStream();
//	            Thread.sleep(1000L);
//	            dataoutputstream.flush();
//	            byte abyte0[] = {
//	                27, 64
//	            };
//	            dataoutputstream.write(abyte0);
//	            abyte0 = (new byte[] {
//	                29, 33, 1
//	            });
//	            dataoutputstream.write(abyte0);
//	            String s1;
//	            int i1 = (s1 = a_javax_microedition_lcdui_StringItem_fld.getText()).length();
//	            for(int j1 = 0; j1 < i1; j1 += 512)
//	            {
//	                if(i1 - j1 >= 512)
//	                {
//	                    dataoutputstream.write(s1.getBytes(), j1, 512);
//	                } else
//	                {
//	                    dataoutputstream.write(s1.getBytes(), j1, i1 - j1);
//	                }
//	                dataoutputstream.flush();
//	                Thread.sleep(500L);
//	            }
//
//	            byte abyte1[] = {
//	                10, 10, 10
//	            };
//	            dataoutputstream.write(abyte1);
//	            dataoutputstream.flush();
//	            if(dataoutputstream != null)
//	            {
//	                dataoutputstream.close();
//	            }
//	            if(streamconnection != null)
//	            {
//	                streamconnection.close();
//	            }
//	            return;
//	        }
//	        catch(Throwable throwable)
//	        {
//	            DisplayErrorMsg("IOException: " + throwable.getMessage());
//	        }
		}else if(cmd==cmdSMS){ // 发送短信
//			if((displayable = ((D88) (command = this)).d_javax_microedition_lcdui_TextField_fld.getString().trim()).length() != 0)
//            {
//                if(displayable.startsWith("0"))
//                {
//                    displayable = "+6" + displayable;
//                } else
//                if(!displayable.startsWith("+"))
//                {
//                    displayable = "+" + displayable;
//                }
//                displayable = "sms://" + displayable;
//                try
//                {
//                    int i1;
//                    String s1;
//                    if((i1 = (s1 = ((D88) (command)).a_javax_microedition_lcdui_StringItem_fld.getText().trim()).trim().length()) <= 160)
//                    {
//                        TextMessage textmessage;
//                        (textmessage = (TextMessage)(i1 = (MessageConnection)Connector.open(displayable)).newMessage("text")).setAddress(displayable);
//                        textmessage.setPayloadText(s1);
//                        i1.send(textmessage);
//                        if(i1 != null)
//                        {
//                            try
//                            {
//                                i1.close();
//                            }
//                            catch(IOException ioexception1)
//                            {
//                                System.out.println("Closing connection caught: ");
//                                command.DisplayErrorMsg(ioexception1.toString());
//                                ioexception1.printStackTrace();
//                            }
//                        }
//                    } else
//                    {
//                        Vector vector = new Vector();
//                        int j1 = 0;
//                        do
//                        {
//                            if(i1 - j1 > 160)
//                            {
//                                String s2 = s1.substring(j1, j1 + 160);
//                                vector.addElement(s2);
//                                j1 += 160;
//                                continue;
//                            }
//                            vector.addElement(s1.substring(j1));
//                            break;
//                        } while(j1 != i1);
//                        if(vector.size() != 0)
//                        {
//                            for(int k1 = 0; k1 < vector.size(); k1++)
//                            {
//                                MessageConnection messageconnection;
//                                TextMessage textmessage1;
//                                (textmessage1 = (TextMessage)(messageconnection = (MessageConnection)Connector.open(displayable)).newMessage("text")).setAddress(displayable);
//                                textmessage1.setPayloadText(vector.elementAt(k1).toString());
//                                messageconnection.send(textmessage1);
//                                if(messageconnection != null)
//                                {
//                                    try
//                                    {
//                                        messageconnection.close();
//                                    }
//                                    catch(IOException ioexception)
//                                    {
//                                        System.out.println("Closing connection caught: ");
//                                        command.DisplayErrorMsg(ioexception.toString());
//                                        ioexception.printStackTrace();
//                                    }
//                                }
//                                Thread.sleep(3000L);
//                            }
//
//                        }
//                    }
//                    command.getDisplay().setCurrent(command.get_alertSMS());
//                    ((D88) (command)).b_javax_microedition_lcdui_Alert_fld.setTimeout(2000);
//                    break label0;
//                }
//                catch(Throwable throwable)
//                {
//                    System.out.println("Send caught: ");
//                    command.DisplayErrorMsg(throwable.toString());
//                    throwable.printStackTrace();
//                }
//            }
//            return;
		}
		
	}
	
//    public Alert get_alertSMS()
//    {
//        if(b_javax_microedition_lcdui_Alert_fld == null)
//        {
//            b_javax_microedition_lcdui_Alert_fld = new Alert(D88.b.a("L19"), D88.b.a("L19"), get_imgAlert(), null);
//            b_javax_microedition_lcdui_Alert_fld.setTimeout(-2);
//        }
//        return b_javax_microedition_lcdui_Alert_fld;
//    }

	public StringItem getItem(){
		return this.item;
	}
}
