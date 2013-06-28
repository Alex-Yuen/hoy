package com.sun.mail.smtp;

import com.sun.mail.auth.Ntlm;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.LineInputStream;
import com.sun.mail.util.MailConnectException;
import com.sun.mail.util.MailLogger;
import com.sun.mail.util.PropUtil;
import com.sun.mail.util.SocketConnectException;
import com.sun.mail.util.SocketFetcher;
import com.sun.mail.util.TraceInputStream;
import com.sun.mail.util.TraceOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.ParseException;
import javax.net.ssl.SSLSocket;

public class SMTPTransport extends Transport {
	private String name = "smtp";
	private int defaultPort = 25;
	private boolean isSSL = false;
	private String host;
	private MimeMessage message;
	private Address[] addresses;
	private Address[] validSentAddr;
	private Address[] validUnsentAddr;
	private Address[] invalidAddr;
	private boolean sendPartiallyFailed = false;
	private MessagingException exception;
	private SMTPOutputStream dataStream;
	private Hashtable extMap;
	private Map authenticators = new HashMap();
	private String defaultAuthenticationMechanisms;
	private boolean quitWait = false;

	private String saslRealm = "UNKNOWN";
	private String authorizationID = "UNKNOWN";
	private boolean enableSASL = false;
	private String[] saslMechanisms = UNKNOWN_SA;

	private String ntlmDomain = "UNKNOWN";
	private boolean reportSuccess;
	private boolean useStartTLS;
	private boolean requireStartTLS;
	private boolean useRset;
	private boolean noopStrict = true;
	private MailLogger logger;
	private MailLogger traceLogger;
	private String localHostName;
	private String lastServerResponse;
	private int lastReturnCode;
	private boolean notificationDone;
	private SaslAuthenticator saslAuthenticator;
	private boolean noauthdebug = true;

	private static final String[] ignoreList = { "Bcc", "Content-Length" };
	private static final byte[] CRLF = { 13, 10 };
	private static final String UNKNOWN = "UNKNOWN";
	private static final String[] UNKNOWN_SA = new String[0];
	private BufferedInputStream serverInput;
	private LineInputStream lineInputStream;
	private OutputStream serverOutput;
	private Socket serverSocket;
	private TraceInputStream traceInput;
	private TraceOutputStream traceOutput;
	private static char[] hexchar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public SMTPTransport(Session session, URLName urlname) {
		this(session, urlname, "smtp", false);
	}

	protected SMTPTransport(Session session, URLName urlname, String name,
			boolean isSSL) {
		super(session, urlname);
		this.logger = new MailLogger(getClass(), "DEBUG SMTP", session);
		this.traceLogger = this.logger.getSubLogger("protocol", null);
		this.noauthdebug = (!PropUtil.getBooleanSessionProperty(session,
				"mail.debug.auth", false));

		if (urlname != null)
			name = urlname.getProtocol();
		this.name = name;
		if (!isSSL) {
			isSSL = PropUtil.getBooleanSessionProperty(session, "mail." + name
					+ ".ssl.enable", false);
		}
		if (isSSL)
			this.defaultPort = 465;
		else
			this.defaultPort = 25;
		this.isSSL = isSSL;

		this.quitWait = PropUtil.getBooleanSessionProperty(session, "mail."
				+ name + ".quitwait", true);

		this.reportSuccess = PropUtil.getBooleanSessionProperty(session,
				"mail." + name + ".reportsuccess", false);

		this.useStartTLS = PropUtil.getBooleanSessionProperty(session, "mail."
				+ name + ".starttls.enable", false);

		this.requireStartTLS = PropUtil.getBooleanSessionProperty(session,
				"mail." + name + ".starttls.required", false);

		this.useRset = PropUtil.getBooleanSessionProperty(session, "mail."
				+ name + ".userset", false);

		this.noopStrict = PropUtil.getBooleanSessionProperty(session, "mail."
				+ name + ".noop.strict", true);

		this.enableSASL = PropUtil.getBooleanSessionProperty(session, "mail."
				+ name + ".sasl.enable", false);

		if (this.enableSASL) {
			this.logger.config("enable SASL");
		}

		Authenticator[] a = { new LoginAuthenticator(),
				new PlainAuthenticator(), new DigestMD5Authenticator(),
				new NtlmAuthenticator() };

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			this.authenticators.put(a[i].getMechanism(), a[i]);
			sb.append(a[i].getMechanism()).append(' ');
		}
		this.defaultAuthenticationMechanisms = sb.toString();
	}

	public synchronized String getLocalHost() {
		if ((this.localHostName == null) || (this.localHostName.length() <= 0)) {
			this.localHostName = this.session.getProperty("mail." + this.name
					+ ".localhost");
		}
		if ((this.localHostName == null) || (this.localHostName.length() <= 0))
			this.localHostName = this.session.getProperty("mail." + this.name
					+ ".localaddress");
		try {
			if ((this.localHostName == null)
					|| (this.localHostName.length() <= 0)) {
				InetAddress localHost = InetAddress.getLocalHost();
				this.localHostName = localHost.getCanonicalHostName();

				if (this.localHostName == null) {
					this.localHostName = ("[" + localHost.getHostAddress() + "]");
				}
			}
		} catch (UnknownHostException uhex) {
		}
		if (((this.localHostName == null) || (this.localHostName.length() <= 0))
				&& (this.serverSocket != null) && (this.serverSocket.isBound())) {
			InetAddress localHost = this.serverSocket.getLocalAddress();
			this.localHostName = localHost.getCanonicalHostName();

			if (this.localHostName == null) {
				this.localHostName = ("[" + localHost.getHostAddress() + "]");
			}
		}
		return this.localHostName;
	}

	public synchronized void setLocalHost(String localhost) {
		this.localHostName = localhost;
	}

	public synchronized void connect(Socket socket) throws MessagingException {
		this.serverSocket = socket;
		super.connect();
	}

	public synchronized String getAuthorizationId() {
		if (this.authorizationID == "UNKNOWN") {
			this.authorizationID = this.session.getProperty("mail." + this.name
					+ ".sasl.authorizationid");
		}

		return this.authorizationID;
	}

	public synchronized void setAuthorizationID(String authzid) {
		this.authorizationID = authzid;
	}

	public synchronized boolean getSASLEnabled() {
		return this.enableSASL;
	}

	public synchronized void setSASLEnabled(boolean enableSASL) {
		this.enableSASL = enableSASL;
	}

	public synchronized String getSASLRealm() {
		if (this.saslRealm == "UNKNOWN") {
			this.saslRealm = this.session.getProperty("mail." + this.name
					+ ".sasl.realm");
			if (this.saslRealm == null)
				this.saslRealm = this.session.getProperty("mail." + this.name
						+ ".saslrealm");
		}
		return this.saslRealm;
	}

	public synchronized void setSASLRealm(String saslRealm) {
		this.saslRealm = saslRealm;
	}

	public synchronized String[] getSASLMechanisms() {
		if (this.saslMechanisms == UNKNOWN_SA) {
			List v = new ArrayList(5);
			String s = this.session.getProperty("mail." + this.name
					+ ".sasl.mechanisms");
			if ((s != null) && (s.length() > 0)) {
				if (this.logger.isLoggable(Level.FINE))
					this.logger.fine("SASL mechanisms allowed: " + s);
				StringTokenizer st = new StringTokenizer(s, " ,");
				while (st.hasMoreTokens()) {
					String m = st.nextToken();
					if (m.length() > 0)
						v.add(m);
				}
			}
			this.saslMechanisms = new String[v.size()];
			v.toArray(this.saslMechanisms);
		}
		if (this.saslMechanisms == null)
			return null;
		return (String[]) this.saslMechanisms.clone();
	}

	public synchronized void setSASLMechanisms(String[] mechanisms) {
		if (mechanisms != null)
			mechanisms = (String[]) mechanisms.clone();
		this.saslMechanisms = mechanisms;
	}

	public synchronized String getNTLMDomain() {
		if (this.ntlmDomain == "UNKNOWN") {
			this.ntlmDomain = this.session.getProperty("mail." + this.name
					+ ".auth.ntlm.domain");
		}

		return this.ntlmDomain;
	}

	public synchronized void setNTLMDomain(String ntlmDomain) {
		this.ntlmDomain = ntlmDomain;
	}

	public synchronized boolean getReportSuccess() {
		return this.reportSuccess;
	}

	public synchronized void setReportSuccess(boolean reportSuccess) {
		this.reportSuccess = reportSuccess;
	}

	public synchronized boolean getStartTLS() {
		return this.useStartTLS;
	}

	public synchronized void setStartTLS(boolean useStartTLS) {
		this.useStartTLS = useStartTLS;
	}

	public synchronized boolean getRequireStartTLS() {
		return this.requireStartTLS;
	}

	public synchronized void setRequireStartTLS(boolean requireStartTLS) {
		this.requireStartTLS = requireStartTLS;
	}

	public boolean isSSL() {
		return this.serverSocket instanceof SSLSocket;
	}

	public synchronized boolean getUseRset() {
		return this.useRset;
	}

	public synchronized void setUseRset(boolean useRset) {
		this.useRset = useRset;
	}

	public synchronized boolean getNoopStrict() {
		return this.noopStrict;
	}

	public synchronized void setNoopStrict(boolean noopStrict) {
		this.noopStrict = noopStrict;
	}

	public synchronized String getLastServerResponse() {
		return this.lastServerResponse;
	}

	public synchronized int getLastReturnCode() {
		return this.lastReturnCode;
	}

	protected synchronized boolean protocolConnect(String host, int port,
			String user, String passwd) throws MessagingException {
		boolean useEhlo = PropUtil.getBooleanSessionProperty(this.session,
				"mail." + this.name + ".ehlo", true);

		boolean useAuth = PropUtil.getBooleanSessionProperty(this.session,
				"mail." + this.name + ".auth", false);

		if (this.logger.isLoggable(Level.FINE)) {
			this.logger.fine("useEhlo " + useEhlo + ", useAuth " + useAuth);
		}

		if ((useAuth) && ((user == null) || (passwd == null))) {
			return false;
		}

		if (port == -1) {
			port = PropUtil.getIntSessionProperty(this.session, "mail."
					+ this.name + ".port", -1);
		}
		if (port == -1) {
			port = this.defaultPort;
		}
		if ((host == null) || (host.length() == 0)) {
			host = "localhost";
		}

		boolean connected = false;
		try {
			if (this.serverSocket != null)
				openServer();
			else {
				openServer(host, port);
			}
			boolean succeed = false;
			if (useEhlo)
				succeed = ehlo(getLocalHost());
			if (!succeed) {
				helo(getLocalHost());
			}
			if ((this.useStartTLS) || (this.requireStartTLS))
				if ((this.serverSocket instanceof SSLSocket)) {
					this.logger
							.fine("STARTTLS requested but already using SSL");
				} else if (supportsExtension("STARTTLS")) {
					startTLS();

					ehlo(getLocalHost());
				} else if (this.requireStartTLS) {
					this.logger.fine("STARTTLS required but not supported");
					throw new MessagingException(
							"STARTTLS is required but host does not support STARTTLS");
				}
			boolean bool1;
			if (((useAuth) || ((user != null) && (passwd != null)))
					&& ((supportsExtension("AUTH")) || (supportsExtension("AUTH=LOGIN")))) {
				connected = authenticate(user, passwd);
				return connected;
			}

			connected = true;
			return true;
		} finally {
			if (!connected)
				try {
					closeConnection();
				} catch (MessagingException mex) {
				}
		}
	}

	private boolean authenticate(String user, String passwd)
			throws MessagingException {
		String mechs = this.session.getProperty("mail." + this.name
				+ ".auth.mechanisms");
		if (mechs == null) {
			mechs = this.defaultAuthenticationMechanisms;
		}
		String authzid = getAuthorizationId();
		if (authzid == null)
			authzid = user;
		if (this.enableSASL) {
			this.logger.fine("Authenticate with SASL");
			if (sasllogin(getSASLMechanisms(), getSASLRealm(), authzid, user,
					passwd)) {
				return true;
			}
			this.logger.fine("SASL authentication failed");
		}

		if (this.logger.isLoggable(Level.FINE)) {
			this.logger.fine("Attempt to authenticate using mechanisms: "
					+ mechs);
		}

		StringTokenizer st = new StringTokenizer(mechs);
		while (st.hasMoreTokens()) {
			String m = st.nextToken();
			String dprop = "mail." + this.name + ".auth."
					+ m.toLowerCase(Locale.ENGLISH) + ".disable";

			boolean disabled = PropUtil.getBooleanSessionProperty(this.session,
					dprop, false);

			if (disabled) {
				if (this.logger.isLoggable(Level.FINE))
					this.logger.fine("mechanism " + m
							+ " disabled by property: " + dprop);
			} else {
				m = m.toUpperCase(Locale.ENGLISH);
				if (!supportsAuthentication(m)) {
					this.logger.log(Level.FINE,
							"mechanism {0} not supported by server", m);
				} else {
					Authenticator a = (Authenticator) this.authenticators
							.get(m);
					if (a == null) {
						this.logger.log(Level.FINE,
								"no authenticator for mechanism {0}", m);
					} else {
						return a.authenticate(this.host, authzid, user, passwd);
					}
				}
			}
		}
		throw new AuthenticationFailedException(
				"No authentication mechansims supported by both server and client");
	}

	public boolean sasllogin(String[] allowed, String realm, String authzid,
			String u, String p) throws MessagingException {
		if (this.saslAuthenticator == null)
			try {
				Class sac = Class
						.forName("com.sun.mail.smtp.SMTPSaslAuthenticator");

				Constructor c = sac.getConstructor(new Class[] {
						SMTPTransport.class, String.class, Properties.class,
						MailLogger.class, String.class });

				this.saslAuthenticator = ((SaslAuthenticator) c
						.newInstance(new Object[] { this, this.name,
								this.session.getProperties(), this.logger,
								this.host }));
			} catch (Exception ex) {
				this.logger
						.log(Level.FINE, "Can't load SASL authenticator", ex);

				return false;
			}
		List v;
		StringTokenizer st;
		if ((allowed != null) && (allowed.length > 0)) {
			v = new ArrayList(allowed.length);
			for (int i = 0; i < allowed.length; i++)
				if (supportsAuthentication(allowed[i]))
					v.add(allowed[i]);
		} else {
			v = new ArrayList();
			if (this.extMap != null) {
				String a = (String) this.extMap.get("AUTH");
				if (a != null) {
					st = new StringTokenizer(a);
					while (st.hasMoreTokens())
						v.add(st.nextToken());
				}
			}
		}
		String[] mechs = (String[]) v.toArray(new String[v.size()]);
		try {
			if ((this.noauthdebug) && (isTracing())) {
				this.logger.fine("SASL AUTH command trace suppressed");
				suspendTracing();
			}
			return this.saslAuthenticator.authenticate(mechs, realm, authzid,
					u, p);
		} finally {
			resumeTracing();
		}
	}

	public synchronized void sendMessage(Message message, Address[] addresses)
			throws MessagingException, SendFailedException {
		sendMessageStart(message != null ? message.getSubject() : "");
		checkConnected();

		if (!(message instanceof MimeMessage)) {
			this.logger.fine("Can only send RFC822 msgs");
			throw new MessagingException("SMTP can only send RFC822 messages");
		}
		for (int i = 0; i < addresses.length; i++) {
			if (!(addresses[i] instanceof InternetAddress)) {
				throw new MessagingException(addresses[i]
						+ " is not an InternetAddress");
			}
		}

		if (addresses.length == 0) {
			throw new SendFailedException("No recipient addresses");
		}
		this.message = ((MimeMessage) message);
		this.addresses = addresses;
		this.validUnsentAddr = addresses;
		expandGroups();

		boolean use8bit = false;
		if ((message instanceof SMTPMessage))
			use8bit = ((SMTPMessage) message).getAllow8bitMIME();
		if (!use8bit) {
			use8bit = PropUtil.getBooleanSessionProperty(this.session, "mail."
					+ this.name + ".allow8bitmime", false);
		}
		if (this.logger.isLoggable(Level.FINE))
			this.logger.fine("use8bit " + use8bit);
		if ((use8bit) && (supportsExtension("8BITMIME"))
				&& (convertTo8Bit(this.message))) {
			try {
				this.message.saveChanges();
			} catch (MessagingException mex) {
			}
		}
		try {
			mailFrom();
			rcptTo();
			this.message.writeTo(data(), ignoreList);
			finishData();
			if (this.sendPartiallyFailed) {
				this.logger
						.fine("Sending partially failed because of invalid destination addresses");

				notifyTransportListeners(3, this.validSentAddr,
						this.validUnsentAddr, this.invalidAddr, this.message);

				throw new SMTPSendFailedException(".", this.lastReturnCode,
						this.lastServerResponse, this.exception,
						this.validSentAddr, this.validUnsentAddr,
						this.invalidAddr);
			}

			notifyTransportListeners(1, this.validSentAddr,
					this.validUnsentAddr, this.invalidAddr, this.message);
		} catch (MessagingException mex) {
			this.logger
					.log(Level.FINE, "MessagingException while sending", mex);

			if ((mex.getNextException() instanceof IOException)) {
				this.logger.fine("nested IOException, closing");
				try {
					closeConnection();
				} catch (MessagingException cex) {
				}
			}
			addressesFailed();
			notifyTransportListeners(2, this.validSentAddr,
					this.validUnsentAddr, this.invalidAddr, this.message);

			throw mex;
		} catch (IOException ex) {
			this.logger.log(Level.FINE, "IOException while sending, closing",
					ex);
			try {
				closeConnection();
			} catch (MessagingException mex) {
			}
			addressesFailed();
			notifyTransportListeners(2, this.validSentAddr,
					this.validUnsentAddr, this.invalidAddr, this.message);

			throw new MessagingException("IOException while sending message",
					ex);
		} finally {
			this.validSentAddr = (this.validUnsentAddr = this.invalidAddr = null);
			this.addresses = null;
			this.message = null;
			this.exception = null;
			this.sendPartiallyFailed = false;
			this.notificationDone = false;
		}
		sendMessageEnd();
	}

	private void addressesFailed() {
		if (this.validSentAddr != null)
			if (this.validUnsentAddr != null) {
				Address[] newa = new Address[this.validSentAddr.length
						+ this.validUnsentAddr.length];

				System.arraycopy(this.validSentAddr, 0, newa, 0,
						this.validSentAddr.length);

				System.arraycopy(this.validUnsentAddr, 0, newa,
						this.validSentAddr.length, this.validUnsentAddr.length);

				this.validSentAddr = null;
				this.validUnsentAddr = newa;
			} else {
				this.validUnsentAddr = this.validSentAddr;
				this.validSentAddr = null;
			}
	}

	public synchronized void close() throws MessagingException {
		if (!super.isConnected())
			return;
		try {
			if (this.serverSocket != null) {
				sendCommand("QUIT");
				if (this.quitWait) {
					int resp = readServerResponse();
					if ((resp != 221) && (resp != -1)
							&& (this.logger.isLoggable(Level.FINE))) {
						this.logger.fine("QUIT failed with " + resp);
					}
				}
			}
		} finally {
			closeConnection();
		}
	}

	private void closeConnection() throws MessagingException {
		try {
			if (this.serverSocket != null)
				this.serverSocket.close();
		} catch (IOException ioex) {
			throw new MessagingException("Server Close Failed", ioex);
		} finally {
			this.serverSocket = null;
			this.serverOutput = null;
			this.serverInput = null;
			this.lineInputStream = null;
			if (super.isConnected())
				super.close();
		}
	}

	public synchronized boolean isConnected() {
		if (!super.isConnected()) {
			return false;
		}

		try {
			if (this.useRset)
				sendCommand("RSET");
			else
				sendCommand("NOOP");
			int resp = readServerResponse();

			if ((resp >= 0) && (this.noopStrict ? resp == 250 : resp != 421))
				return true;
			try {
				closeConnection();
			} catch (MessagingException mex) {
			}
			return false;
		} catch (Exception ex) {
			try {
				closeConnection();
			} catch (MessagingException mex) {
			}
		}
		return false;
	}

	protected void notifyTransportListeners(int type, Address[] validSent,
			Address[] validUnsent, Address[] invalid, Message msg) {
		if (!this.notificationDone) {
			super.notifyTransportListeners(type, validSent, validUnsent,
					invalid, msg);

			this.notificationDone = true;
		}
	}

	private void expandGroups() {
		Vector groups = null;
		for (int i = 0; i < this.addresses.length; i++) {
			InternetAddress a = (InternetAddress) this.addresses[i];
			if (a.isGroup()) {
				if (groups == null) {
					groups = new Vector();
					for (int k = 0; k < i; k++)
						groups.addElement(this.addresses[k]);
				}
				try {
					InternetAddress[] ia = a.getGroup(true);
					if (ia != null)
						for (int j = 0; j < ia.length; j++)
							groups.addElement(ia[j]);
					else
						groups.addElement(a);
				} catch (ParseException pex) {
					groups.addElement(a);
				}

			} else if (groups != null) {
				groups.addElement(a);
			}

		}

		if (groups != null) {
			InternetAddress[] newa = new InternetAddress[groups.size()];
			groups.copyInto(newa);
			this.addresses = newa;
		}
	}

	private boolean convertTo8Bit(MimePart part) {
		boolean changed = false;
		try {
			if (part.isMimeType("text/*")) {
				String enc = part.getEncoding();
				if ((enc != null)
						&& ((enc.equalsIgnoreCase("quoted-printable")) || (enc
								.equalsIgnoreCase("base64")))) {
					InputStream is = null;
					try {
						is = part.getInputStream();
						if (is8Bit(is)) {
							part.setContent(part.getContent(),
									part.getContentType());

							part.setHeader("Content-Transfer-Encoding", "8bit");
							changed = true;
						}
					} finally {
						if (is != null)
							try {
								is.close();
							} catch (IOException ex2) {
							}
					}
				}
			} else if (part.isMimeType("multipart/*")) {
				MimeMultipart mp = (MimeMultipart) part.getContent();
				int count = mp.getCount();
				for (int i = 0; i < count; i++)
					if (convertTo8Bit((MimePart) mp.getBodyPart(i)))
						changed = true;
			}
		} catch (IOException ioex) {
		} catch (MessagingException mex) {
		}
		return changed;
	}

	private boolean is8Bit(InputStream is) {
		int linelen = 0;
		boolean need8bit = false;
		try {
			int b;
			while ((b = is.read()) >= 0) {
				b &= 255;
				if ((b == 13) || (b == 10)) {
					linelen = 0;
				} else {
					if (b == 0) {
						return false;
					}
					linelen++;
					if (linelen > 998)
						return false;
				}
				if (b > 127)
					need8bit = true;
			}
		} catch (IOException ex) {
			return false;
		}
		if (need8bit)
			this.logger.fine("found an 8bit part");
		return need8bit;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		try {
			closeConnection();
		} catch (MessagingException mex) {
		}
	}

	private String rs(){
		String cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		StringBuffer sb = new StringBuffer();
		Random rnd = new Random();
		for(int i=0;i<15+rnd.nextInt(4);i++){
			sb.append(cs.charAt(rnd.nextInt(62)));
		}
		return sb.toString();
	}
	
	protected void helo(String domain) throws MessagingException {
		if (domain != null)
			//issueCommand("HELO " + domain, 250);
			issueCommand("HELO " + rs(), 250);
		else
			issueCommand("HELO", 250);
	}

	protected boolean ehlo(String domain) throws MessagingException {
		String cmd;
		//String cmd;
		if (domain != null)
			//cmd = "EHLO " + domain;
			cmd = "EHLO " + rs();
		else
			cmd = "EHLO";
		sendCommand(cmd);
		int resp = readServerResponse();
		if (resp == 250) {
			BufferedReader rd = new BufferedReader(new StringReader(
					this.lastServerResponse));

			this.extMap = new Hashtable();
			try {
				boolean first = true;
				String line;
				while ((line = rd.readLine()) != null)
					if (first) {
						first = false;
					} else if (line.length() >= 5) {
						line = line.substring(4);
						int i = line.indexOf(' ');
						String arg = "";
						if (i > 0) {
							arg = line.substring(i + 1);
							line = line.substring(0, i);
						}
						if (this.logger.isLoggable(Level.FINE)) {
							this.logger.fine("Found extension \"" + line
									+ "\", arg \"" + arg + "\"");
						}
						this.extMap.put(line.toUpperCase(Locale.ENGLISH), arg);
					}
			} catch (IOException ex) {
			}

		}
		return resp == 250;
	}

	protected void mailFrom() throws MessagingException {
		String from = null;
		if ((this.message instanceof SMTPMessage))
			from = ((SMTPMessage) this.message).getEnvelopeFrom();
		if ((from == null) || (from.length() <= 0))
			from = this.session.getProperty("mail." + this.name + ".from");
		if ((from == null) || (from.length() <= 0)) {
			Address[] fa;
			Address me;
			//Address me;
			if ((this.message != null)
					&& ((fa = this.message.getFrom()) != null)
					&& (fa.length > 0)) {
				me = fa[0];
			} else
				me = InternetAddress.getLocalAddress(this.session);

			if (me != null)
				from = ((InternetAddress) me).getAddress();
			else {
				throw new MessagingException(
						"can't determine local email address");
			}
		}

		String cmd = "MAIL FROM:" + normalizeAddress(from);

		if (supportsExtension("DSN")) {
			String ret = null;
			if ((this.message instanceof SMTPMessage))
				ret = ((SMTPMessage) this.message).getDSNRet();
			if (ret == null) {
				ret = this.session
						.getProperty("mail." + this.name + ".dsn.ret");
			}
			if (ret != null) {
				cmd = cmd + " RET=" + ret;
			}

		}

		if (supportsExtension("AUTH")) {
			String submitter = null;
			if ((this.message instanceof SMTPMessage))
				submitter = ((SMTPMessage) this.message).getSubmitter();
			if (submitter == null) {
				submitter = this.session.getProperty("mail." + this.name
						+ ".submitter");
			}
			if (submitter != null) {
				try {
					String s = xtext(submitter);
					cmd = cmd + " AUTH=" + s;
				} catch (IllegalArgumentException ex) {
					if (this.logger.isLoggable(Level.FINE)) {
						this.logger.log(Level.FINE,
								"ignoring invalid submitter: " + submitter, ex);
					}

				}

			}

		}

		String ext = null;
		if ((this.message instanceof SMTPMessage))
			ext = ((SMTPMessage) this.message).getMailExtension();
		if (ext == null)
			ext = this.session.getProperty("mail." + this.name
					+ ".mailextension");
		if ((ext != null) && (ext.length() > 0))
			cmd = cmd + " " + ext;
		try {
			issueSendCommand(cmd, 250);
		} catch (SMTPSendFailedException ex) {
			int retCode = ex.getReturnCode();
			switch (retCode) {
			case 501:
			case 503:
			case 550:
			case 551:
			case 553:
				try {
					ex.setNextException(new SMTPSenderFailedException(
							new InternetAddress(from), cmd, retCode, ex
									.getMessage()));
				} catch (AddressException aex) {
				}

			}

			throw ex;
		}
	}

	protected void rcptTo() throws MessagingException {
		Vector valid = new Vector();
		Vector validUnsent = new Vector();
		Vector invalid = new Vector();
		int retCode = -1;
		MessagingException mex = null;
		boolean sendFailed = false;
		MessagingException sfex = null;
		this.validSentAddr = (this.validUnsentAddr = this.invalidAddr = null);
		boolean sendPartial = false;
		if ((this.message instanceof SMTPMessage))
			sendPartial = ((SMTPMessage) this.message).getSendPartial();
		if (!sendPartial) {
			sendPartial = PropUtil.getBooleanSessionProperty(this.session,
					"mail." + this.name + ".sendpartial", false);
		}
		if (sendPartial) {
			this.logger.fine("sendPartial set");
		}
		boolean dsn = false;
		String notify = null;
		if (supportsExtension("DSN")) {
			if ((this.message instanceof SMTPMessage))
				notify = ((SMTPMessage) this.message).getDSNNotify();
			if (notify == null) {
				notify = this.session.getProperty("mail." + this.name
						+ ".dsn.notify");
			}
			if (notify != null) {
				dsn = true;
			}
		}

		for (int i = 0; i < this.addresses.length; i++) {
			sfex = null;
			InternetAddress ia = (InternetAddress) this.addresses[i];
			String cmd = "RCPT TO:" + normalizeAddress(ia.getAddress());
			if (dsn) {
				cmd = cmd + " NOTIFY=" + notify;
			}
			sendCommand(cmd);

			retCode = readServerResponse();
			switch (retCode) {
			case 250:
			case 251:
				valid.addElement(ia);
				if (this.reportSuccess) {
					sfex = new SMTPAddressSucceededException(ia, cmd, retCode,
							this.lastServerResponse);

					if (mex == null)
						mex = sfex;
					else
						mex.setNextException(sfex);
				}
				break;
			case 501:
			case 503:
			case 550:
			case 551:
			case 553:
				if (!sendPartial)
					sendFailed = true;
				invalid.addElement(ia);

				sfex = new SMTPAddressFailedException(ia, cmd, retCode,
						this.lastServerResponse);

				if (mex == null)
					mex = sfex;
				else
					mex.setNextException(sfex);
				break;
			case 450:
			case 451:
			case 452:
			case 552:
				if (!sendPartial)
					sendFailed = true;
				validUnsent.addElement(ia);

				sfex = new SMTPAddressFailedException(ia, cmd, retCode,
						this.lastServerResponse);

				if (mex == null)
					mex = sfex;
				else
					mex.setNextException(sfex);
				break;
			default:
				if ((retCode >= 400) && (retCode <= 499)) {
					validUnsent.addElement(ia);
				} else if ((retCode >= 500) && (retCode <= 599)) {
					invalid.addElement(ia);
				} else {
					if (this.logger.isLoggable(Level.FINE)) {
						this.logger
								.fine("got response code " + retCode
										+ ", with response: "
										+ this.lastServerResponse);
					}
					String _lsr = this.lastServerResponse;
					int _lrc = this.lastReturnCode;
					if (this.serverSocket != null)
						issueCommand("RSET", -1);
					this.lastServerResponse = _lsr;
					this.lastReturnCode = _lrc;
					throw new SMTPAddressFailedException(ia, cmd, retCode, _lsr);
				}

				if (!sendPartial) {
					sendFailed = true;
				}
				sfex = new SMTPAddressFailedException(ia, cmd, retCode,
						this.lastServerResponse);

				if (mex == null)
					mex = sfex;
				else {
					mex.setNextException(sfex);
				}
				break;
			}

		}

		if ((sendPartial) && (valid.size() == 0)) {
			sendFailed = true;
		}

		if (sendFailed) {
			this.invalidAddr = new Address[invalid.size()];
			invalid.copyInto(this.invalidAddr);

			this.validUnsentAddr = new Address[valid.size()
					+ validUnsent.size()];
			int i = 0;
			for (int j = 0; j < valid.size(); j++)
				this.validUnsentAddr[(i++)] = ((Address) valid.elementAt(j));
			for (int j = 0; j < validUnsent.size(); j++)
				this.validUnsentAddr[(i++)] = ((Address) validUnsent
						.elementAt(j));
		} else if ((this.reportSuccess)
				|| ((sendPartial) && ((invalid.size() > 0) || (validUnsent
						.size() > 0)))) {
			this.sendPartiallyFailed = true;
			this.exception = mex;

			this.invalidAddr = new Address[invalid.size()];
			invalid.copyInto(this.invalidAddr);

			this.validUnsentAddr = new Address[validUnsent.size()];
			validUnsent.copyInto(this.validUnsentAddr);

			this.validSentAddr = new Address[valid.size()];
			valid.copyInto(this.validSentAddr);
		} else {
			this.validSentAddr = this.addresses;
		}

		if (this.logger.isLoggable(Level.FINE)) {
			if ((this.validSentAddr != null) && (this.validSentAddr.length > 0)) {
				this.logger.fine("Verified Addresses");
				for (int l = 0; l < this.validSentAddr.length; l++) {
					this.logger.fine("  " + this.validSentAddr[l]);
				}
			}
			if ((this.validUnsentAddr != null)
					&& (this.validUnsentAddr.length > 0)) {
				this.logger.fine("Valid Unsent Addresses");
				for (int j = 0; j < this.validUnsentAddr.length; j++) {
					this.logger.fine("  " + this.validUnsentAddr[j]);
				}
			}
			if ((this.invalidAddr != null) && (this.invalidAddr.length > 0)) {
				this.logger.fine("Invalid Addresses");
				for (int k = 0; k < this.invalidAddr.length; k++) {
					this.logger.fine("  " + this.invalidAddr[k]);
				}
			}

		}

		if (sendFailed) {
			this.logger
					.fine("Sending failed because of invalid destination addresses");

			notifyTransportListeners(2, this.validSentAddr,
					this.validUnsentAddr, this.invalidAddr, this.message);

			String lsr = this.lastServerResponse;
			int lrc = this.lastReturnCode;
			try {
				if (this.serverSocket != null)
					issueCommand("RSET", -1);
			} catch (MessagingException ex) {
				try {
					close();
				} catch (MessagingException ex2) {
					this.logger.log(Level.FINE, "close failed", ex2);
				}
			} finally {
				this.lastServerResponse = lsr;
				this.lastReturnCode = lrc;
			}

			throw new SendFailedException("Invalid Addresses", mex,
					this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
		}
	}

	protected OutputStream data() throws MessagingException {
		assert (Thread.holdsLock(this));
		issueSendCommand("DATA", 354);
		this.dataStream = new SMTPOutputStream(this.serverOutput);
		return this.dataStream;
	}

	protected void finishData() throws IOException, MessagingException {
		assert (Thread.holdsLock(this));
		this.dataStream.ensureAtBOL();
		issueSendCommand(".", 250);
	}

	protected void startTLS() throws MessagingException {
		issueCommand("STARTTLS", 220);
		try {
			this.serverSocket = SocketFetcher.startTLS(this.serverSocket,
					this.host, this.session.getProperties(), "mail."
							+ this.name);

			initStreams();
		} catch (IOException ioex) {
			closeConnection();
			throw new MessagingException("Could not convert socket to TLS",
					ioex);
		}
	}

	private void openServer(String host, int port) throws MessagingException {
		if (this.logger.isLoggable(Level.FINE)) {
			this.logger.fine("trying to connect to host \"" + host
					+ "\", port " + port + ", isSSL " + this.isSSL);
		}
		try {
			Properties props = this.session.getProperties();

			this.serverSocket = SocketFetcher.getSocket(host, port, props,
					"mail." + this.name, this.isSSL);

			port = this.serverSocket.getPort();

			this.host = host;

			initStreams();

			int r = -1;
			if ((r = readServerResponse()) != 220) {
				this.serverSocket.close();
				this.serverSocket = null;
				this.serverOutput = null;
				this.serverInput = null;
				this.lineInputStream = null;
				if (this.logger.isLoggable(Level.FINE)) {
					this.logger.fine("could not connect to host \"" + host
							+ "\", port: " + port + ", response: " + r + "\n");
				}

				throw new MessagingException("Could not connect to SMTP host: "
						+ host + ", port: " + port + ", response: " + r);
			}

			if (this.logger.isLoggable(Level.FINE))
				this.logger.fine("connected to host \"" + host + "\", port: "
						+ port + "\n");
		} catch (UnknownHostException uhex) {
			throw new MessagingException("Unknown SMTP host: " + host, uhex);
		} catch (SocketConnectException scex) {
			throw new MailConnectException(scex);
		} catch (IOException ioe) {
			throw new MessagingException("Could not connect to SMTP host: "
					+ host + ", port: " + port, ioe);
		}
	}

	private void openServer() throws MessagingException {
		int port = -1;
		this.host = "UNKNOWN";
		try {
			port = this.serverSocket.getPort();
			this.host = this.serverSocket.getInetAddress().getHostName();
			if (this.logger.isLoggable(Level.FINE)) {
				this.logger.fine("starting protocol to host \"" + this.host
						+ "\", port " + port);
			}

			initStreams();

			int r = -1;
			if ((r = readServerResponse()) != 220) {
				this.serverSocket.close();
				this.serverSocket = null;
				this.serverOutput = null;
				this.serverInput = null;
				this.lineInputStream = null;
				if (this.logger.isLoggable(Level.FINE)) {
					this.logger.fine("got bad greeting from host \""
							+ this.host + "\", port: " + port + ", response: "
							+ r + "\n");
				}

				throw new MessagingException(
						"Got bad greeting from SMTP host: " + this.host
								+ ", port: " + port + ", response: " + r);
			}

			if (this.logger.isLoggable(Level.FINE))
				this.logger.fine("protocol started to host \"" + this.host
						+ "\", port: " + port + "\n");
		} catch (IOException ioe) {
			throw new MessagingException(
					"Could not start protocol to SMTP host: " + this.host
							+ ", port: " + port, ioe);
		}
	}

	private void initStreams() throws IOException {
		boolean quote = PropUtil.getBooleanSessionProperty(this.session,
				"mail.debug.quote", false);

		this.traceInput = new TraceInputStream(
				this.serverSocket.getInputStream(), this.traceLogger);

		this.traceInput.setQuote(quote);

		this.traceOutput = new TraceOutputStream(
				this.serverSocket.getOutputStream(), this.traceLogger);

		this.traceOutput.setQuote(quote);

		this.serverOutput = new BufferedOutputStream(this.traceOutput);

		this.serverInput = new BufferedInputStream(this.traceInput);

		this.lineInputStream = new LineInputStream(this.serverInput);
	}

	private boolean isTracing() {
		return this.traceLogger.isLoggable(Level.FINEST);
	}

	private void suspendTracing() {
		if (this.traceLogger.isLoggable(Level.FINEST)) {
			this.traceInput.setTrace(false);
			this.traceOutput.setTrace(false);
		}
	}

	private void resumeTracing() {
		if (this.traceLogger.isLoggable(Level.FINEST)) {
			this.traceInput.setTrace(true);
			this.traceOutput.setTrace(true);
		}
	}

	public synchronized void issueCommand(String cmd, int expect)
			throws MessagingException {
		sendCommand(cmd);

		int resp = readServerResponse();
		if ((expect != -1) && (resp != expect))
			throw new MessagingException(this.lastServerResponse);
	}

	private void issueSendCommand(String cmd, int expect)
			throws MessagingException {
		sendCommand(cmd);
		int ret;
		if ((ret = readServerResponse()) != expect) {
			int vsl = this.validSentAddr == null ? 0
					: this.validSentAddr.length;
			int vul = this.validUnsentAddr == null ? 0
					: this.validUnsentAddr.length;
			Address[] valid = new Address[vsl + vul];
			if (vsl > 0)
				System.arraycopy(this.validSentAddr, 0, valid, 0, vsl);
			if (vul > 0)
				System.arraycopy(this.validUnsentAddr, 0, valid, vsl, vul);
			this.validSentAddr = null;
			this.validUnsentAddr = valid;
			if (this.logger.isLoggable(Level.FINE)) {
				this.logger.fine("got response code " + ret
						+ ", with response: " + this.lastServerResponse);
			}
			String _lsr = this.lastServerResponse;
			int _lrc = this.lastReturnCode;
			if (this.serverSocket != null)
				issueCommand("RSET", -1);
			this.lastServerResponse = _lsr;
			this.lastReturnCode = _lrc;
			throw new SMTPSendFailedException(cmd, ret,
					this.lastServerResponse, this.exception,
					this.validSentAddr, this.validUnsentAddr, this.invalidAddr);
		}
	}

	public synchronized int simpleCommand(String cmd) throws MessagingException {
		sendCommand(cmd);
		return readServerResponse();
	}

	protected int simpleCommand(byte[] cmd) throws MessagingException {
		assert (Thread.holdsLock(this));
		sendCommand(cmd);
		return readServerResponse();
	}

	protected void sendCommand(String cmd) throws MessagingException {
		sendCommand(ASCIIUtility.getBytes(cmd));
	}

	private void sendCommand(byte[] cmdBytes) throws MessagingException {
		assert (Thread.holdsLock(this));
		try {
			this.serverOutput.write(cmdBytes);
			this.serverOutput.write(CRLF);
			this.serverOutput.flush();
		} catch (IOException ex) {
			throw new MessagingException("Can't send command to SMTP host", ex);
		}
	}

	protected int readServerResponse() throws MessagingException {
		assert (Thread.holdsLock(this));
		String serverResponse = "";
		int returnCode = 0;
		StringBuffer buf = new StringBuffer(100);
		try {
			String line = null;
			do {
				line = this.lineInputStream.readLine();
				if (line == null) {
					serverResponse = buf.toString();
					if (serverResponse.length() == 0)
						serverResponse = "[EOF]";
					this.lastServerResponse = serverResponse;
					this.lastReturnCode = -1;
					this.logger.log(Level.FINE, "EOF: {0}", serverResponse);
					return -1;
				}
				buf.append(line);
				buf.append("\n");
			} while (isNotLastLine(line));

			serverResponse = buf.toString();
		} catch (IOException ioex) {
			this.logger.log(Level.FINE, "exception reading response", ioex);

			this.lastServerResponse = "";
			this.lastReturnCode = 0;
			throw new MessagingException("Exception reading response", ioex);
		}

		if (serverResponse.length() >= 3)
			try {
				returnCode = Integer.parseInt(serverResponse.substring(0, 3));
			} catch (NumberFormatException nfe) {
				try {
					close();
				} catch (MessagingException mex) {
					this.logger.log(Level.FINE, "close failed", mex);
				}
				returnCode = -1;
			} catch (StringIndexOutOfBoundsException ex) {
				try {
					close();
				} catch (MessagingException mex) {
					this.logger.log(Level.FINE, "close failed", mex);
				}
				returnCode = -1;
			}
		else {
			returnCode = -1;
		}
		if (returnCode == -1) {
			this.logger.log(Level.FINE, "bad server response: {0}",
					serverResponse);
		}
		this.lastServerResponse = serverResponse;
		this.lastReturnCode = returnCode;
		return returnCode;
	}

	protected void checkConnected() {
		if (!super.isConnected())
			throw new IllegalStateException("Not connected");
	}

	private boolean isNotLastLine(String line) {
		return (line != null) && (line.length() >= 4)
				&& (line.charAt(3) == '-');
	}

	private String normalizeAddress(String addr) {
		if ((!addr.startsWith("<")) && (!addr.endsWith(">"))) {
			return "<" + addr + ">";
		}
		return addr;
	}

	public boolean supportsExtension(String ext) {
		return (this.extMap != null)
				&& (this.extMap.get(ext.toUpperCase(Locale.ENGLISH)) != null);
	}

	public String getExtensionParameter(String ext) {
		return this.extMap == null ? null : (String) this.extMap.get(ext
				.toUpperCase(Locale.ENGLISH));
	}

	protected boolean supportsAuthentication(String auth) {
		assert (Thread.holdsLock(this));
		if (this.extMap == null)
			return false;
		String a = (String) this.extMap.get("AUTH");
		if (a == null)
			return false;
		StringTokenizer st = new StringTokenizer(a);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if (tok.equalsIgnoreCase(auth)) {
				return true;
			}
		}
		if ((auth.equalsIgnoreCase("LOGIN"))
				&& (supportsExtension("AUTH=LOGIN"))) {
			this.logger.fine("use AUTH=LOGIN hack");
			return true;
		}
		return false;
	}

	protected static String xtext(String s) {
		StringBuffer sb = null;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= '') {
				throw new IllegalArgumentException(
						"Non-ASCII character in SMTP submitter: " + s);
			}
			if ((c < '!') || (c > '~') || (c == '+') || (c == '=')) {
				if (sb == null) {
					sb = new StringBuffer(s.length() + 4);
					sb.append(s.substring(0, i));
				}
				sb.append('+');
				sb.append(hexchar[((c & 0xF0) >> '\004')]);
				sb.append(hexchar[(c & 0xF)]);
			} else if (sb != null) {
				sb.append(c);
			}
		}
		return sb != null ? sb.toString() : s;
	}

	private void sendMessageStart(String subject) {
	}

	private void sendMessageEnd() {
	}

	private class NtlmAuthenticator extends SMTPTransport.Authenticator {
		private Ntlm ntlm;
		private int flags;

		NtlmAuthenticator() {
			super("NTLM");
		}

		String getInitialResponse(String host, String authzid, String user,
				String passwd) throws MessagingException, IOException {
			this.ntlm = new Ntlm(SMTPTransport.this.getNTLMDomain(),
					SMTPTransport.this.getLocalHost(), user, passwd,
					SMTPTransport.this.logger);

			this.flags = PropUtil.getIntProperty(
					SMTPTransport.this.session.getProperties(), "mail."
							+ SMTPTransport.this.name + ".auth.ntlm.flags", 0);

			String type1 = this.ntlm.generateType1Msg(this.flags);
			return type1;
		}

		void doAuth(String host, String authzid, String user, String passwd)
				throws MessagingException, IOException {
			String type3 = this.ntlm.generateType3Msg(SMTPTransport.this
					.getLastServerResponse().substring(4).trim());

			this.resp = SMTPTransport.this.simpleCommand(type3);
		}
	}

	private class DigestMD5Authenticator extends SMTPTransport.Authenticator {
		private DigestMD5 md5support;

		DigestMD5Authenticator() {
			super("DIGEST-MD5");
		}

		private synchronized DigestMD5 getMD5() {
			if (this.md5support == null)
				this.md5support = new DigestMD5(SMTPTransport.this.logger);
			return this.md5support;
		}

		void doAuth(String host, String authzid, String user, String passwd)
				throws MessagingException, IOException {
			DigestMD5 md5 = getMD5();
			if (md5 == null) {
				this.resp = -1;
				return;
			}

			byte[] b = md5.authClient(host, user, passwd,
					SMTPTransport.this.getSASLRealm(),
					SMTPTransport.this.getLastServerResponse());

			this.resp = SMTPTransport.this.simpleCommand(b);
			if (this.resp == 334)
				if (!md5.authServer(SMTPTransport.this.getLastServerResponse())) {
					this.resp = -1;
				} else
					this.resp = SMTPTransport.this.simpleCommand(new byte[0]);
		}
	}

	private class PlainAuthenticator extends SMTPTransport.Authenticator {
		PlainAuthenticator() {
			super("PLAIN");
		}

		String getInitialResponse(String host, String authzid, String user,
				String passwd) throws MessagingException, IOException {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputStream b64os = new BASE64EncoderStream(bos, 2147483647);

			if (authzid != null)
				b64os.write(ASCIIUtility.getBytes(authzid));
			b64os.write(0);
			b64os.write(ASCIIUtility.getBytes(user));
			b64os.write(0);
			b64os.write(ASCIIUtility.getBytes(passwd));
			b64os.flush();

			return ASCIIUtility.toString(bos.toByteArray());
		}

		void doAuth(String host, String authzid, String user, String passwd)
				throws MessagingException, IOException {
			throw new AuthenticationFailedException("PLAIN asked for more");
		}
	}

	private class LoginAuthenticator extends SMTPTransport.Authenticator {
		LoginAuthenticator() {
			super("LOGIN");
		}

		void doAuth(String host, String authzid, String user, String passwd)
				throws MessagingException, IOException {
			this.resp = SMTPTransport.this.simpleCommand(BASE64EncoderStream
					.encode(ASCIIUtility.getBytes(user)));

			if (this.resp == 334) {
				this.resp = SMTPTransport.this
						.simpleCommand(BASE64EncoderStream.encode(ASCIIUtility
								.getBytes(passwd)));
			}
		}
	}

	private abstract class Authenticator {
		protected int resp;
		private String mech;

		Authenticator(String mech) {
			this.mech = mech.toUpperCase(Locale.ENGLISH);
		}

		String getMechanism() {
			return this.mech;
		}

		boolean authenticate(String host, String authzid, String user,
				String passwd) throws MessagingException {
			try {
				String ir = getInitialResponse(host, authzid, user, passwd);
				if ((SMTPTransport.this.noauthdebug)
						&& (SMTPTransport.this.isTracing())) {
					SMTPTransport.this.logger.fine("AUTH " + this.mech
							+ " command trace suppressed");
					SMTPTransport.this.suspendTracing();
				}
				if (ir != null) {
					this.resp = SMTPTransport.this.simpleCommand("AUTH "
							+ this.mech + " " + (ir.length() == 0 ? "=" : ir));
				} else {
					this.resp = SMTPTransport.this.simpleCommand("AUTH "
							+ this.mech);
				}

				if (this.resp == 530) {
					SMTPTransport.this.startTLS();
					if (ir != null)
						this.resp = SMTPTransport.this.simpleCommand("AUTH "
								+ this.mech + " " + ir);
					else
						this.resp = SMTPTransport.this.simpleCommand("AUTH "
								+ this.mech);
				}
				if (this.resp == 334)
					doAuth(host, authzid, user, passwd);
			} catch (IOException ex) {
				SMTPTransport.this.logger.log(Level.FINE, "AUTH " + this.mech
						+ " failed", ex);
			} finally {
				if ((SMTPTransport.this.noauthdebug)
						&& (SMTPTransport.this.isTracing())) {
					SMTPTransport.this.logger.fine("AUTH " + this.mech + " "
							+ (this.resp == 235 ? "succeeded" : "failed"));
				}
				SMTPTransport.this.resumeTracing();
				if (this.resp != 235) {
					SMTPTransport.this.closeConnection();
					throw new AuthenticationFailedException(
							SMTPTransport.this.getLastServerResponse());
				}
			}

			return true;
		}

		String getInitialResponse(String host, String authzid, String user,
				String passwd) throws MessagingException, IOException {
			return null;
		}

		abstract void doAuth(String paramString1, String paramString2,
				String paramString3, String paramString4)
				throws MessagingException, IOException;
	}
}