package com.internal.utility;

/*
 * @author Swapnil
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import android.text.Html;
import android.util.Log;

public class GMailUtil extends javax.mail.Authenticator {
  private String mailhost = "smtp.gmail.com";
  private String user;
  private String password;
  private Session session;
  private Folder inbox;
  public static List<String> messageBody;
  public static List<String> from;
  static {
    Security.addProvider(new JSSEProvider());
  }

  public GMailUtil(final String user, final String password) {
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

  public void readMails() {
    /* Set the mail properties */

    Properties props = System.getProperties();
    props.setProperty("mail.store.protocol", "imaps");
    try {
      Store store = session.getStore("imaps");
      store.connect("imap.gmail.com", user, password);

      /* Mention the folder name which you want to read. */
      inbox = store.getFolder("Inbox");
      System.out.println("No of Unread Messages : "
          + inbox.getUnreadMessageCount());
      /* Open the inbox using store. */
      inbox.open(Folder.READ_WRITE);

      /* Get the messages which is unread in the Inbox */
      Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.RECENT),
          false));
      /* Use a suitable FetchProfile */
      FetchProfile fp = new FetchProfile();
      fp.add(FetchProfile.Item.ENVELOPE);
      fp.add(FetchProfile.Item.CONTENT_INFO);
      inbox.fetch(messages, fp);

      try {
        if (messages.length > 0) {
          printAllMessages(messages);
        }
        inbox.close(true);
        store.close();
      } catch (Exception ex) {
        System.out.println("Exception arise at the time of read mail");
        ex.printStackTrace();
      }
    } catch (NoSuchProviderException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (MessagingException e) {
      e.printStackTrace();
      System.exit(2);
    }
    return;
  }

  public void printAllMessages(Message[] msgs) throws Exception {
    from = new ArrayList<String>();
    messageBody = new ArrayList<String>();
    for (int i = 0; i < msgs.length; i++) {// msgs.length
      from = getMessageSubject(msgs[i]);
      messageBody = getMessageBody(msgs[i]);
      System.out.println(msgs.length + "MESSAGE from   #" + from.get(i)
          + " message " + messageBody.get(i));
    }
  }

  public List<String> getMessageBody(Message msg) throws IOException,
  MessagingException {
    Object multipartObj = msg.getContent();
    if (multipartObj instanceof Multipart) {
      Multipart multipart = (Multipart) multipartObj;
      for (int x = 0; x < multipart.getCount(); x++) {
        BodyPart bodyPart = multipart.getBodyPart(x);
        messageBody.add(stripHtml(bodyPart.getContent().toString()));
        String disposition = bodyPart.getDisposition();

        if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT))) {
          System.out.println("Mail have some attachment : ");
          DataHandler handler = bodyPart.getDataHandler();
          System.out.println("file name : " + handler.getName());
        } else {
          Log.i("BOdyy >>>>>>> ", stripHtml(bodyPart.getContent().toString()));
        }
      }
    }
    return messageBody;
  }

  public String stripHtml(String html) {
    return Html.fromHtml(html).toString();
  }

  /* Print the envelope(FromAddress,ReceivedDate,Subject) */
  public List<String> getMessageSubject(Message message) throws Exception {
    Address[] a;
    // FROM
    if ((a = message.getFrom()) != null) {
      for (int j = 0; j < a.length; j++) {
        System.out.println("FROM: " + a[j].toString());
        from.add(a[j].toString());
      }
    }
    return from;
  }
}