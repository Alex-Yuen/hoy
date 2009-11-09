package it.hoyland.sclottery;

//import it.hoyland.sclottery.util.Properties;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class MainList extends List implements CommandListener {

	private Command cmdExit;
	private LotteryMIDlet midlet;
	
	public MainList(LotteryMIDlet lotteryMIDlet, String title, int listType) {
		super(title, listType);
		this.midlet = lotteryMIDlet;
		for(int i=0; i<12; i++){
			this.append(this.midlet.prop("L"+String.valueOf(i+1)), null);
		}
		setSelectedFlags(new boolean[] {false, false, false, false, false, false
				, false, false, false, false, false, false});
		setFitPolicy(1);
		
		this.cmdExit = new Command(this.midlet.prop("L17"), 7, 1);
		addCommand(cmdExit);
		setCommandListener(this);
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		if(cmd==List.SELECT_COMMAND){
//			switch(getSelectedIndex()){ // 根据选择进入不同窗口
//            case 0: // '\0'
//                this.midlet.setStatus('1');
//                switch(this.midlet.getBint())
//                {
//                case 4: // '\004'
//                    this.midlet.getDisplay().setCurrent(this.midlet.getNameInputForm());
//                    G_javax_microedition_lcdui_TextField_fld.setString("");
//                    f_java_lang_String_fld = "";
//                    e_java_lang_String_fld = "";
//                    if(a_javax_microedition_lcdui_ChoiceGroup_fld.size() > 0)
//                    {
//                        a_javax_microedition_lcdui_ChoiceGroup_fld.setSelectedIndex(0, true);
//                    }
//                    break;
//                }
//                break;
//											
//			case 1: // '\001'
//                switch(this.midlet.getBint())
//                {
//                default:
//                    break label0;
//
//                case 4: // '\004'
//                    b_char_fld = '1';
//                    getDisplay().setCurrent(get_frmNameInput());
//                    G_javax_microedition_lcdui_TextField_fld.setString("");
//                    f_java_lang_String_fld = "";
//                    e_java_lang_String_fld = "";
//                    if(a_javax_microedition_lcdui_ChoiceGroup_fld.size() > 0)
//                    {
//                        a_javax_microedition_lcdui_ChoiceGroup_fld.setSelectedIndex(0, true);
//                    }
//                    break;
//
//                case 5: // '\005'
//                    b_char_fld = '2';
//                    getDisplay().setCurrent(get_frmBet());
//                    break label0;
//                }
//                break;
//
//            case 2: // '\002'
//                b_char_fld = '3';
//                getDisplay().setCurrent(get_frmReprint());
//                break label0;
//
//            case 3: // '\003'
//                b_char_fld = '4';
//                getDisplay().setCurrent(get_frmBalance());
//                break label0;
//
//            case 4: // '\004'
//                b_char_fld = '5';
//                getDisplay().setCurrent(get_frmBetHist());
//                break label0;
//                
//
//            case 5: // '\005'
//                b_char_fld = '6';
//                getDisplay().setCurrent(get_frmTStakes());
//                break label0;
//
//
//            case 6: // '\006'
//                b_char_fld = '7';
//                getDisplay().setCurrent(get_frmResult());
//                break label0;
//
//            case 7: // '\007'
//                b_char_fld = '8';
//                getDisplay().setCurrent(get_frmWinning());
//                break label0;
//
//            case 8: // '\b'
//                b_char_fld = '9';
//                switch(b_int_fld)
//                {
//                case 4: // '\004'
//                    getDisplay().setCurrent(get_frmPayment());
//                    break;
//                }
//                break label0;
//
//            case 9: // '\t'
//                b_char_fld = 'A';
//                getDisplay().setCurrent(get_frmPassword());
//                break label0;
//
//
//            case 10: // '\n'
//                b_char_fld = 'B';
//                getDisplay().setCurrent(get_frmNumMeaning());
//                break label0;
//                
//            case 11: // '\013'
//                b_char_fld = 'C';
//                getDisplay().setCurrent(get_frmAddPrinter());
//                break label0;
//
//			default:
//				break;
//			}
		}
		else if(cmd==cmdExit){
			this.midlet.exit();
		}
		//Display.getDisplay(this.midlet).setCurrent(new LoginForm(this.midlet));
	}

	public LotteryMIDlet getMidlet() {
		return midlet;
	}
	
}
