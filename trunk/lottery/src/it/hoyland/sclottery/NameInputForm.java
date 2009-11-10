package it.hoyland.sclottery;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.TextField;

public class NameInputForm extends Form implements ItemCommandListener, CommandListener {

	private LotteryMIDlet midlet;
	private TextField agentSearch;
	private ChoiceGroup cgAgents;
	
	private String agentSearchString;
	private String indexString;
	
	private Command cmdAgentClick;
	
	private Command cmdNameInputBack;
	private Command cmdNameInputExit;
	private Command cmdNameInputCon;
	private Command cmdNameInputSearch;
	
	public NameInputForm(LotteryMIDlet lotteryMIDlet, String title) {
		super(title);
		this.midlet = lotteryMIDlet;
		
		this.agentSearch = new TextField(this.midlet.prop("L55"), null, 25, 0xc0000);
		
		this.cgAgents = new ChoiceGroup(this.midlet.prop("L1"), 1, new String[0], new Image[0]);
		this.cmdAgentClick = new Command(this.midlet.prop("L63"), 1, 1);
		this.cgAgents.addCommand(this.cmdAgentClick);
		this.cgAgents.setItemCommandListener(this);
		this.cgAgents.setSelectedFlags(new boolean[0]);
		this.cgAgents.setFitPolicy(1);
		
		append(this.agentSearch);
		append(this.cgAgents);
		
		this.cmdNameInputBack = new Command(this.midlet.prop("L24"), 2, 1);
		this.cmdNameInputExit = new Command(this.midlet.prop("L17"), 1, 4);
		this.cmdNameInputCon = new Command(this.midlet.prop("L62"), 1, 2);
		this.cmdNameInputSearch = new Command(this.midlet.prop("L25"), 1, 0);
		
		addCommand(this.cmdNameInputBack);
		addCommand(this.cmdNameInputExit);
		addCommand(this.cmdNameInputCon);
		addCommand(this.cmdNameInputSearch);
		
		setCommandListener(this);
		
	}

	public void commandAction(Command cmd, Item item) {
		// TODO Auto-generated method stub
		
	}

	public void commandAction(Command cmd, Displayable dsp) {
		// TODO Auto-generated method stub
		
	}
	
	public TextField getAgentSearch(){
		return this.agentSearch;
	}

	public String getAgentSearchString() {
		return agentSearchString;
	}

	public void setAgentSearchString(String agentSearchString) {
		this.agentSearchString = agentSearchString;
	}

	public String getIndexString() {
		return indexString;
	}

	public void setIndexString(String indexString) {
		this.indexString = indexString;
	}

	public ChoiceGroup getCgAgents() {
		return cgAgents;
	}

	public void setCgAgents(ChoiceGroup cgAgents) {
		this.cgAgents = cgAgents;
	}
		
}
