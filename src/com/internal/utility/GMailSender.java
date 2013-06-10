package com.internal.utility;

/*
 * @author xyz
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailSender extends javax.mail.Authenticator {
  private String mailhost = "smtp.gmail.com";
  private String user;
  private String password;
  private Session session;

  static {
    Security.addProvider(new JSSEProvider());
  }

  public GMailSender(final String user, final String password) {
    this.user = user;
    this.password = password;

    Properties props = new Properties();
    props.setProperty("mail.transport.protocol", "smtp");
    props.setProperty("mail.host", mailhost);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "465");
    props.put("mail.smtp.socketFactory.port", "465");
    props
    .put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.quitwait", "false");

    session = Session.getDefaultInstance(props, this);
  }

  protected final PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(user, password);
  }

  public final synchronized void sendMail(final String subject,
      final String body, final String sender, final String recipients)
  throws Exception {
    MimeMessage message = new MimeMessage(session);
    DataHandler handler = new DataHandler(new ByteArrayDataSource(
        body.getBytes(), "text/plain"));
    message.setSender(new InternetAddress(sender));
    message.setSubject(subject);
    message.setDataHandler(handler);
    if (recipients.indexOf(',') > 0) {
      message.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(recipients));
    } else {
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(
          recipients));
    }
    Transport.send(message);

  }

  public class ByteArrayDataSource implements DataSource {
    private byte[] data;
    private String type;

    public ByteArrayDataSource(final byte[] data, final String type) {
      super();
      this.data = data;
      this.type = type;
    }

    public ByteArrayDataSource(final byte[] data) {
      super();
      this.data = data;
    }

    public final void setType(final String type) {
      this.type = type;
    }

    public final String getContentType() {
      if (type == null) {
        return "application/octet-stream";
      } else {
        return type;
      }
    }

    public final InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(data);
    }

    public final String getName() {
      return "ByteArrayDataSource";
    }

    public final OutputStream getOutputStream() throws IOException {
      throw new IOException("Not Supported");
    }
  }
}