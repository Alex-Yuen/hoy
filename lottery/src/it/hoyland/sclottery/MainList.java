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
		setSelectedFlags(new boolean[] {false, false, false, false, false, false,
				false, false, false, false, false, false});
		setFitPolicy(1);
		
		this.cmdExit = new Command(this.midlet.prop("L17"), 7, 1);
		addCommand(cmdExit);
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		if(cmd==List.SELECT_COMMAND){
			switch(getSelectedIndex()){ // 根据选择进入不同窗口
            case 0: // '\0'
                this.midlet.setStatus('1');
                switch(this.midlet.getBint())
                {
                case 4: // '\004'
                	NameInputForm nameInputForm = this.midlet.getNameInputForm();
                    this.midlet.getDisplay().setCurrent(nameInputForm);
                    nameInputForm.getAgentSearch().setString("");
                    nameInputForm.setAgentSearchString("");
                    nameInputForm.setIndexString("");
                    if(nameInputForm.getCgAgents().size() > 0)
                    {
                    	nameInputForm.getCgAgents().setSelectedIndex(0, true);
                    }
                    break;
                }
                break;
											
			case 1: // '\001'
                switch(this.midlet.getBint())
                {
                default:
                    break;

                case 4: // '\004'
                	this.midlet.setStatus('1');
                	NameInputForm nameInputForm = this.midlet.getNameInputForm();
                    this.midlet.getDisplay().setCurrent(nameInputForm);
                    nameInputForm.getAgentSearch().setString("");
                    nameInputForm.setAgentSearchString("");
                    nameInputForm.setIndexString("");
                    if(nameInputForm.getCgAgents().size() > 0)
                    {
                    	nameInputForm.getCgAgents().setSelectedIndex(0, true);
                    }
                    break;

                case 5: // '\005'
                	this.midlet.setStatus('2');
                	this.midlet.getDisplay().setCurrent(this.midlet.getBetForm());
                    break;
                }
                break;

            case 2: // '\002'
            	this.midlet.setStatus('3');
            	this.midlet.getDisplay().setCurrent(this.midlet.getReprintForm());
                break;

            case 3: // '\003'
            	this.midlet.setStatus('4');
            	this.midlet.getDisplay().setCurrent(this.midlet.getBalanceForm());
                break;

            case 4: // '\004'
            	this.midlet.setStatus('5');
            	this.midlet.getDisplay().setCurrent(this.midlet.getBetHistoryForm());
                break;
                

            case 5: // '\005'
            	this.midlet.setStatus('6');
            	this.midlet.getDisplay().setCurrent(this.midlet.getTStakesForm());
                break;


            case 6: // '\006'
            	this.midlet.setStatus('7');
            	this.midlet.getDisplay().setCurrent(this.midlet.getResultForm());
                break;

            case 7: // '\007'
            	this.midlet.setStatus('8');
            	this.midlet.getDisplay().setCurrent(this.midlet.getWinningForm());
                break;

            case 8: // '\b'
            	this.midlet.setStatus('9');
                switch(this.midlet.getBint())
                {
                case 4: // '\004'
                	this.midlet.getDisplay().setCurrent(this.midlet.getPaymentForm());
                    break;
                }
                break;

            case 9: // '\t'
            	this.midlet.setStatus('A');
            	this.midlet.getDisplay().setCurrent(this.midlet.getPasswordForm());
                break;


            case 10: // '\n'
            	this.midlet.setStatus('B');
            	this.midlet.getDisplay().setCurrent(this.midlet.getNumMeaningForm());
                break;
                
            case 11: // '\013'
            	this.midlet.setStatus('C');
            	this.midlet.getDisplay().setCurrent(this.midlet.getAddPrinterForm());
                break;

			default:
				break;
			}
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
