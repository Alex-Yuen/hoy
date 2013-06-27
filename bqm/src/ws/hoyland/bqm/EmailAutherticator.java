package ws.hoyland.bqm;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 用来进行服务器对用户的认证
 */
public class EmailAutherticator extends Authenticator {
	private String username;
	private String password;

	public EmailAutherticator() {
		super();
	}

	public EmailAutherticator(String user, String pwd) {
		super();
		username = user;
		password = pwd;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}
}