package com.example.shhapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
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
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

/**
 * 
 * @author Swapnil
 */

public class ReadGmails extends AsyncTask<String, Integer, Boolean> {

  private String userName;
  private String password;
  private Folder inbox;
  List<String> messageBody = new ArrayList<String>();
  List<String> from = new ArrayList<String>();
  public void setAccountDetails(String userName, String password) {

    this.userName = userName;
    this.password = password;

  }

  @Override
  protected Boolean doInBackground(String... params) {
    /* Set the mail properties */

    Properties props = System.getProperties();
    props.setProperty("mail.store.protocol", "imaps");
    try {
      /* Create the session and get the store for read the mail. */
      Session session = Session.getDefaultInstance(props, null);
      Store store = session.getStore("imaps");
      store.connect("imap.gmail.com", userName, password);

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
        printAllMessages(messages);
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
    return null;
  }

  public void printAllMessages(Message[] msgs) throws Exception {
    for (int i = 0; i < msgs.length; i++) {
      System.out.println(msgs.length + "MESSAGE #" + (i + 1) + ":");
      getMessageSubject(msgs[i]);
      getMessageBody(msgs[i]);
    }
  }

  public List<String> getMessageBody(Message msg) throws IOException,
  MessagingException {
    Multipart multipart = (Multipart) msg.getContent();
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
    return  messageBody;
  }

  public String stripHtml(String html) {
    return Html.fromHtml(html).toString();
  }

  /* Print the envelope(FromAddress,ReceivedDate,Subject) */
  public  List<String> getMessageSubject(Message message) throws Exception {
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