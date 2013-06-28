package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.FolderClosedIOException;
import com.sun.mail.util.LineOutputStream;
import com.sun.mail.util.MessageRemovedIOException;
import com.sun.mail.util.MimeUtil;
import com.sun.mail.util.PropUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.util.SharedByteArrayInputStream;

public class MimeMessage extends Message
  implements MimePart
{
  protected DataHandler dh;
  protected byte[] content;
  protected InputStream contentStream;
  protected InternetHeaders headers;
  protected Flags flags;
  protected boolean modified = false;

  protected boolean saved = false;
  protected Object cachedContent;
  private static final MailDateFormat mailDateFormat = new MailDateFormat();

  private boolean strict = true;

  private static final Flags answeredFlag = new Flags(Flags.Flag.ANSWERED);

  public MimeMessage(Session session)
  {
    super(session);
    this.modified = true;
    this.headers = new InternetHeaders();
    this.flags = new Flags();
    initStrict();
  }

  public MimeMessage(Session session, InputStream is)
    throws MessagingException
  {
    super(session);
    this.flags = new Flags();
    initStrict();
    parse(is);
    this.saved = true;
  }

  public MimeMessage(MimeMessage source)
    throws MessagingException
  {
    super(source.session);
    this.flags = source.getFlags();
    if (this.flags == null) {
      this.flags = new Flags();
    }
    int size = source.getSize();
    ByteArrayOutputStream bos;
   // ByteArrayOutputStream bos;
    if (size > 0)
      bos = new ByteArrayOutputStream(size);
    else
      bos = new ByteArrayOutputStream();
    try {
      this.strict = source.strict;
      source.writeTo(bos);
      bos.close();
      SharedByteArrayInputStream bis = new SharedByteArrayInputStream(bos.toByteArray());

      parse(bis);
      bis.close();
      this.saved = true;
    }
    catch (IOException ex) {
      throw new MessagingException("IOException while copying message", ex);
    }
  }

  protected MimeMessage(Folder folder, int msgnum)
  {
    super(folder, msgnum);
    this.flags = new Flags();
    this.saved = true;
    initStrict();
  }

  protected MimeMessage(Folder folder, InputStream is, int msgnum)
    throws MessagingException
  {
    this(folder, msgnum);
    initStrict();
    parse(is);
  }

  protected MimeMessage(Folder folder, InternetHeaders headers, byte[] content, int msgnum)
    throws MessagingException
  {
    this(folder, msgnum);
    this.headers = headers;
    this.content = content;
    initStrict();
  }

  private void initStrict()
  {
    if (this.session != null)
      this.strict = PropUtil.getBooleanSessionProperty(this.session, "mail.mime.address.strict", true);
  }

  protected void parse(InputStream is)
    throws MessagingException
  {
    if ((!(is instanceof ByteArrayInputStream)) && (!(is instanceof BufferedInputStream)) && (!(is instanceof SharedInputStream)))
    {
      is = new BufferedInputStream(is);
    }
    this.headers = createInternetHeaders(is);

    if ((is instanceof SharedInputStream)) {
      SharedInputStream sis = (SharedInputStream)is;
      this.contentStream = sis.newStream(sis.getPosition(), -1L);
    } else {
      try {
        this.content = ASCIIUtility.getBytes(is);
      } catch (IOException ioex) {
        throw new MessagingException("IOException", ioex);
      }
    }

    this.modified = false;
  }

  public Address[] getFrom()
    throws MessagingException
  {
    Address[] a = getAddressHeader("From");
    if (a == null) {
      a = getAddressHeader("Sender");
    }
    return a;
  }

  public void setFrom(Address address)
    throws MessagingException
  {
    if (address == null)
      removeHeader("From");
    else
      setHeader("From", address.toString());
  }

  public void setFrom(String address)
    throws MessagingException
  {
    if (address == null)
      removeHeader("From");
    else
      setAddressHeader("From", InternetAddress.parse(address));
  }

  public void setFrom()
    throws MessagingException
  {
    InternetAddress me = null;
    try {
      me = InternetAddress._getLocalAddress(this.session);
    }
    catch (Exception ex)
    {
      throw new MessagingException("No From address", ex);
    }
    if (me != null)
      setFrom(me);
    else
      throw new MessagingException("No From address");
  }

  public void addFrom(Address[] addresses)
    throws MessagingException
  {
    addAddressHeader("From", addresses);
  }

  public Address getSender()
    throws MessagingException
  {
    Address[] a = getAddressHeader("Sender");
    if ((a == null) || (a.length == 0))
      return null;
    return a[0];
  }

  public void setSender(Address address)
    throws MessagingException
  {
    if (address == null)
      removeHeader("Sender");
    else
      setHeader("Sender", address.toString());
  }

  public Address[] getRecipients(Message.RecipientType type)
    throws MessagingException
  {
    if (type == RecipientType.NEWSGROUPS) {
      String s = getHeader("Newsgroups", ",");
      return s == null ? null : NewsAddress.parse(s);
    }
    return getAddressHeader(getHeaderName(type));
  }

  public Address[] getAllRecipients()
    throws MessagingException
  {
    Address[] all = super.getAllRecipients();
    Address[] ng = getRecipients(RecipientType.NEWSGROUPS);

    if (ng == null)
      return all;
    if (all == null) {
      return ng;
    }
    Address[] addresses = new Address[all.length + ng.length];
    System.arraycopy(all, 0, addresses, 0, all.length);
    System.arraycopy(ng, 0, addresses, all.length, ng.length);
    return addresses;
  }

  public void setRecipients(Message.RecipientType type, Address[] addresses)
    throws MessagingException
  {
    if (type == RecipientType.NEWSGROUPS) {
      if ((addresses == null) || (addresses.length == 0))
        removeHeader("Newsgroups");
      else
        setHeader("Newsgroups", NewsAddress.toString(addresses));
    }
    else setAddressHeader(getHeaderName(type), addresses);
  }

  public void setRecipients(Message.RecipientType type, String addresses)
    throws MessagingException
  {
    if (type == RecipientType.NEWSGROUPS) {
      if ((addresses == null) || (addresses.length() == 0))
        removeHeader("Newsgroups");
      else
        setHeader("Newsgroups", addresses);
    }
    else setAddressHeader(getHeaderName(type), addresses == null ? null : InternetAddress.parse(addresses));
  }

  public void addRecipients(Message.RecipientType type, Address[] addresses)
    throws MessagingException
  {
    if (type == RecipientType.NEWSGROUPS) {
      String s = NewsAddress.toString(addresses);
      if (s != null)
        addHeader("Newsgroups", s);
    } else {
      addAddressHeader(getHeaderName(type), addresses);
    }
  }

  public void addRecipients(Message.RecipientType type, String addresses)
    throws MessagingException
  {
    if (type == RecipientType.NEWSGROUPS) {
      if ((addresses != null) && (addresses.length() != 0))
        addHeader("Newsgroups", addresses);
    }
    else addAddressHeader(getHeaderName(type), InternetAddress.parse(addresses));
  }

  public Address[] getReplyTo()
    throws MessagingException
  {
    Address[] a = getAddressHeader("Reply-To");
    if ((a == null) || (a.length == 0))
      a = getFrom();
    return a;
  }

  public void setReplyTo(Address[] addresses)
    throws MessagingException
  {
    setAddressHeader("Reply-To", addresses);
  }

  private Address[] getAddressHeader(String name)
    throws MessagingException
  {
    String s = getHeader(name, ",");
    return s == null ? null : InternetAddress.parseHeader(s, this.strict);
  }

  private void setAddressHeader(String name, Address[] addresses)
    throws MessagingException
  {
    String s = InternetAddress.toString(addresses);
    if (s == null)
      removeHeader(name);
    else
      setHeader(name, s);
  }

  private void addAddressHeader(String name, Address[] addresses) throws MessagingException
  {
    if ((addresses == null) || (addresses.length == 0))
      return;
    Address[] a = getAddressHeader(name);
    Address[] anew;
   // Address[] anew;
    if ((a == null) || (a.length == 0)) {
      anew = addresses;
    } else {
      anew = new Address[a.length + addresses.length];
      System.arraycopy(a, 0, anew, 0, a.length);
      System.arraycopy(addresses, 0, anew, a.length, addresses.length);
    }
    String s = InternetAddress.toString(anew);
    if (s == null)
      return;
    setHeader(name, s);
  }

  public String getSubject()
    throws MessagingException
  {
    String rawvalue = getHeader("Subject", null);

    if (rawvalue == null)
      return null;
    try
    {
      return MimeUtility.decodeText(MimeUtility.unfold(rawvalue)); } catch (UnsupportedEncodingException ex) {
    }
    return rawvalue;
  }

  public void setSubject(String subject)
    throws MessagingException
  {
    setSubject(subject, null);
  }

  public void setSubject(String subject, String charset)
    throws MessagingException
  {
    if (subject == null)
      removeHeader("Subject");
    else
      try {
        setHeader("Subject", MimeUtility.fold(9, MimeUtility.encodeText(subject, charset, null)));
      }
      catch (UnsupportedEncodingException uex) {
        throw new MessagingException("Encoding error", uex);
      }
  }

  public Date getSentDate()
    throws MessagingException
  {
    String s = getHeader("Date", null);
    if (s != null) {
      try {
        synchronized (mailDateFormat) {
          return mailDateFormat.parse(s);
        }
      } catch (ParseException pex) {
        return null;
      }
    }

    return null;
  }

  public void setSentDate(Date d)
    throws MessagingException
  {
    if (d == null)
      removeHeader("Date");
    else
      synchronized (mailDateFormat) {
        setHeader("Date", mailDateFormat.format(d));
      }
  }

  public Date getReceivedDate()
    throws MessagingException
  {
    return null;
  }

  public int getSize()
    throws MessagingException
  {
    if (this.content != null)
      return this.content.length;
    if (this.contentStream != null)
      try {
        int size = this.contentStream.available();

        if (size > 0)
          return size;
      }
      catch (IOException ex)
      {
      }
    return -1;
  }

  public int getLineCount()
    throws MessagingException
  {
    return -1;
  }

  public String getContentType()
    throws MessagingException
  {
    String s = getHeader("Content-Type", null);
    s = MimeUtil.cleanContentType(this, s);
    if (s == null)
      return "text/plain";
    return s;
  }

  public boolean isMimeType(String mimeType)
    throws MessagingException
  {
    return MimeBodyPart.isMimeType(this, mimeType);
  }

  public String getDisposition()
    throws MessagingException
  {
    return MimeBodyPart.getDisposition(this);
  }

  public void setDisposition(String disposition)
    throws MessagingException
  {
    MimeBodyPart.setDisposition(this, disposition);
  }

  public String getEncoding()
    throws MessagingException
  {
    return MimeBodyPart.getEncoding(this);
  }

  public String getContentID()
    throws MessagingException
  {
    return getHeader("Content-Id", null);
  }

  public void setContentID(String cid)
    throws MessagingException
  {
    if (cid == null)
      removeHeader("Content-ID");
    else
      setHeader("Content-ID", cid);
  }

  public String getContentMD5()
    throws MessagingException
  {
    return getHeader("Content-MD5", null);
  }

  public void setContentMD5(String md5)
    throws MessagingException
  {
    setHeader("Content-MD5", md5);
  }

  public String getDescription()
    throws MessagingException
  {
    return MimeBodyPart.getDescription(this);
  }

  public void setDescription(String description)
    throws MessagingException
  {
    setDescription(description, null);
  }

  public void setDescription(String description, String charset)
    throws MessagingException
  {
    MimeBodyPart.setDescription(this, description, charset);
  }

  public String[] getContentLanguage()
    throws MessagingException
  {
    return MimeBodyPart.getContentLanguage(this);
  }

  public void setContentLanguage(String[] languages)
    throws MessagingException
  {
    MimeBodyPart.setContentLanguage(this, languages);
  }

  public String getMessageID()
    throws MessagingException
  {
    return getHeader("Message-ID", null);
  }

  public String getFileName()
    throws MessagingException
  {
    return MimeBodyPart.getFileName(this);
  }

  public void setFileName(String filename)
    throws MessagingException
  {
    MimeBodyPart.setFileName(this, filename);
  }

  private String getHeaderName(Message.RecipientType type)
    throws MessagingException
  {
    String headerName;
    if (type == Message.RecipientType.TO) {
      headerName = "To";
    }
    else
    {
     // String headerName;
      if (type == Message.RecipientType.CC) {
        headerName = "Cc";
      }
      else
      {
       // String headerName;
        if (type == Message.RecipientType.BCC) {
          headerName = "Bcc";
        }
        else
        {
         // String headerName;
          if (type == RecipientType.NEWSGROUPS)
            headerName = "Newsgroups";
          else
            throw new MessagingException("Invalid Recipient Type");
        }
      }
    }
    //String headerName;
    return headerName;
  }

  public InputStream getInputStream()
    throws IOException, MessagingException
  {
    return getDataHandler().getInputStream();
  }

  protected InputStream getContentStream()
    throws MessagingException
  {
    if (this.contentStream != null)
      return ((SharedInputStream)this.contentStream).newStream(0L, -1L);
    if (this.content != null) {
      return new SharedByteArrayInputStream(this.content);
    }
    throw new MessagingException("No MimeMessage content");
  }

  public InputStream getRawInputStream()
    throws MessagingException
  {
    return getContentStream();
  }

  public synchronized DataHandler getDataHandler()
    throws MessagingException
  {
    if (this.dh == null) {
      this.dh = new MimeBodyPart.MimePartDataHandler(new MimePartDataSource(this));
    }
    return this.dh;
  }

  public Object getContent()
    throws IOException, MessagingException
  {
    if (this.cachedContent != null)
      return this.cachedContent;
    Object c;
    try {
      c = getDataHandler().getContent();
    } catch (FolderClosedIOException fex) {
      throw new FolderClosedException(fex.getFolder(), fex.getMessage());
    } catch (MessageRemovedIOException mex) {
      throw new MessageRemovedException(mex.getMessage());
    }
    if ((MimeBodyPart.cacheMultipart) && (((c instanceof Multipart)) || ((c instanceof Message))) && ((this.content != null) || (this.contentStream != null)))
    {
      this.cachedContent = c;

      if ((c instanceof MimeMultipart))
        ((MimeMultipart)c).parse();
    }
    return c;
  }

  public synchronized void setDataHandler(DataHandler dh)
    throws MessagingException
  {
    this.dh = dh;
    this.cachedContent = null;
    MimeBodyPart.invalidateContentHeaders(this);
  }

  public void setContent(Object o, String type)
    throws MessagingException
  {
    if ((o instanceof Multipart))
      setContent((Multipart)o);
    else
      setDataHandler(new DataHandler(o, type));
  }

  public void setText(String text)
    throws MessagingException
  {
    setText(text, null);
  }

  public void setText(String text, String charset)
    throws MessagingException
  {
    MimeBodyPart.setText(this, text, charset, "plain");
  }

  public void setText(String text, String charset, String subtype)
    throws MessagingException
  {
    MimeBodyPart.setText(this, text, charset, subtype);
  }

  public void setContent(Multipart mp)
    throws MessagingException
  {
    setDataHandler(new DataHandler(mp, mp.getContentType()));
    mp.setParent(this);
  }

  public Message reply(boolean replyToAll)
    throws MessagingException
  {
    return reply(replyToAll, true);
  }

  public Message reply(boolean replyToAll, boolean setAnswered)
    throws MessagingException
  {
    MimeMessage reply = createMimeMessage(this.session);

    String subject = getHeader("Subject", null);
    if (subject != null) {
      if (!subject.regionMatches(true, 0, "Re: ", 0, 4))
        subject = "Re: " + subject;
      reply.setHeader("Subject", subject);
    }
    Address[] a = getReplyTo();
    reply.setRecipients(Message.RecipientType.TO, a);
    if (replyToAll) {
      Vector v = new Vector();

      InternetAddress me = InternetAddress.getLocalAddress(this.session);
      if (me != null) {
        v.addElement(me);
      }
      String alternates = null;
      if (this.session != null)
        alternates = this.session.getProperty("mail.alternates");
      if (alternates != null) {
        eliminateDuplicates(v, InternetAddress.parse(alternates, false));
      }

      String replyallccStr = null;
      boolean replyallcc = false;
      if (this.session != null) {
        replyallcc = PropUtil.getBooleanSessionProperty(this.session, "mail.replyallcc", false);
      }

      eliminateDuplicates(v, a);
      a = getRecipients(Message.RecipientType.TO);
      a = eliminateDuplicates(v, a);
      if ((a != null) && (a.length > 0)) {
        if (replyallcc)
          reply.addRecipients(Message.RecipientType.CC, a);
        else
          reply.addRecipients(Message.RecipientType.TO, a);
      }
      a = getRecipients(Message.RecipientType.CC);
      a = eliminateDuplicates(v, a);
      if ((a != null) && (a.length > 0)) {
        reply.addRecipients(Message.RecipientType.CC, a);
      }
      a = getRecipients(RecipientType.NEWSGROUPS);
      if ((a != null) && (a.length > 0)) {
        reply.setRecipients(RecipientType.NEWSGROUPS, a);
      }
    }
    String msgId = getHeader("Message-Id", null);
    if (msgId != null) {
      reply.setHeader("In-Reply-To", msgId);
    }

    String refs = getHeader("References", " ");
    if (refs == null)
    {
      refs = getHeader("In-Reply-To", " ");
    }
    if (msgId != null) {
      if (refs != null)
        refs = MimeUtility.unfold(refs) + " " + msgId;
      else
        refs = msgId;
    }
    if (refs != null) {
      reply.setHeader("References", MimeUtility.fold(12, refs));
    }
    if (setAnswered)
      try {
        setFlags(answeredFlag, true);
      }
      catch (MessagingException mex)
      {
      }
    return reply;
  }

  private Address[] eliminateDuplicates(Vector v, Address[] addrs)
  {
    if (addrs == null)
      return null;
    int gone = 0;
    for (int i = 0; i < addrs.length; i++) {
      boolean found = false;

      for (int j = 0; j < v.size(); j++) {
        if (((InternetAddress)v.elementAt(j)).equals(addrs[i]))
        {
          found = true;
          gone++;
          addrs[i] = null;
          break;
        }
      }
      if (!found) {
        v.addElement(addrs[i]);
      }
    }
    if (gone != 0)
    {
      Address[] a;
     // Address[] a;
      if ((addrs instanceof InternetAddress[]))
        a = new InternetAddress[addrs.length - gone];
      else
        a = new Address[addrs.length - gone];
      int i = 0; for (int j = 0; i < addrs.length; i++)
        if (addrs[i] != null)
          a[(j++)] = addrs[i];
      addrs = a;
    }
    return addrs;
  }

  public void writeTo(OutputStream os)
    throws IOException, MessagingException
  {
    writeTo(os, null);
  }

  public void writeTo(OutputStream os, String[] ignoreList)
    throws IOException, MessagingException
  {
    if (!this.saved) {
      saveChanges();
    }
    if (this.modified) {
      MimeBodyPart.writeTo(this, os, ignoreList);
      return;
    }

    Enumeration hdrLines = getNonMatchingHeaderLines(ignoreList);
    LineOutputStream los = new LineOutputStream(os);
    while (hdrLines.hasMoreElements()) {
      los.writeln((String)hdrLines.nextElement());
    }

    los.writeln();

    if (this.content == null)
    {
      InputStream is = null;
      byte[] buf = new byte[8192];
      try {
        is = getContentStream();
        int len;
        while ((len = is.read(buf)) > 0)
          os.write(buf, 0, len);
      } finally {
        if (is != null)
          is.close();
        buf = null;
      }
    } else {
      os.write(this.content);
    }
    os.flush();
  }

  public String[] getHeader(String name)
    throws MessagingException
  {
    return this.headers.getHeader(name);
  }

  public String getHeader(String name, String delimiter)
    throws MessagingException
  {
    return this.headers.getHeader(name, delimiter);
  }

  public void setHeader(String name, String value)
    throws MessagingException
  {
    this.headers.setHeader(name, value);
  }

  public void addHeader(String name, String value)
    throws MessagingException
  {
    this.headers.addHeader(name, value);
  }

  public void removeHeader(String name)
    throws MessagingException
  {
    this.headers.removeHeader(name);
  }

  public Enumeration getAllHeaders()
    throws MessagingException
  {
    return this.headers.getAllHeaders();
  }

  public Enumeration getMatchingHeaders(String[] names)
    throws MessagingException
  {
    return this.headers.getMatchingHeaders(names);
  }

  public Enumeration getNonMatchingHeaders(String[] names)
    throws MessagingException
  {
    return this.headers.getNonMatchingHeaders(names);
  }

  public void addHeaderLine(String line)
    throws MessagingException
  {
    this.headers.addHeaderLine(line);
  }

  public Enumeration getAllHeaderLines()
    throws MessagingException
  {
    return this.headers.getAllHeaderLines();
  }

  public Enumeration getMatchingHeaderLines(String[] names)
    throws MessagingException
  {
    return this.headers.getMatchingHeaderLines(names);
  }

  public Enumeration getNonMatchingHeaderLines(String[] names)
    throws MessagingException
  {
    return this.headers.getNonMatchingHeaderLines(names);
  }

  public synchronized Flags getFlags()
    throws MessagingException
  {
    return (Flags)this.flags.clone();
  }

  public synchronized boolean isSet(Flags.Flag flag)
    throws MessagingException
  {
    return this.flags.contains(flag);
  }

  public synchronized void setFlags(Flags flag, boolean set)
    throws MessagingException
  {
    if (set)
      this.flags.add(flag);
    else
      this.flags.remove(flag);
  }

  public void saveChanges()
    throws MessagingException
  {
    this.modified = true;
    this.saved = true;
    updateHeaders();
  }

  protected void updateMessageID()
    throws MessagingException
  {
    //setHeader("Message-ID", "<" + UniqueValue.getUniqueMessageIDValue(this.session) + ">");
  }

  protected synchronized void updateHeaders()
    throws MessagingException
  {
    MimeBodyPart.updateHeaders(this);
    setHeader("MIME-Version", "1.0");
    updateMessageID();

    if (this.cachedContent != null) {
      this.dh = new DataHandler(this.cachedContent, getContentType());
      this.cachedContent = null;
      this.content = null;
      if (this.contentStream != null)
        try {
          this.contentStream.close();
        } catch (IOException ioex) {
        }
      this.contentStream = null;
    }
  }

  protected InternetHeaders createInternetHeaders(InputStream is)
    throws MessagingException
  {
    return new InternetHeaders(is);
  }

  protected MimeMessage createMimeMessage(Session session)
    throws MessagingException
  {
    return new MimeMessage(session);
  }

  public static class RecipientType extends Message.RecipientType
  {
    private static final long serialVersionUID = -5468290701714395543L;
    public static final RecipientType NEWSGROUPS = new RecipientType("Newsgroups");

    protected RecipientType(String type) {
      super(type);
    }

    protected Object readResolve() throws ObjectStreamException {
      if (this.type.equals("Newsgroups")) {
        return NEWSGROUPS;
      }
      return super.readResolve();
    }
  }
}