package game4d.classic4d.mobile.info;

import java.io.Serializable;

public class BaseInfo implements Serializable{
	
	private String customerId;//用户ID
	private String commandLine;//操作指令

	public String getCommandLine() {
		return commandLine;
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


}
