package com.example.shhapp;

/*
 * AsyncTask for instantiating GMailSender object
 */
import android.os.AsyncTask;

import com.internal.utility.GMailSender;

public class GMailSenderAsynTask extends AsyncTask<String, Void, Boolean> {
  private String sender;
  private String reciever;
  private String senderPassWord;

  public GMailSenderAsynTask(final String sender, final String senderPassWord,
      final String reciver) {
    super();
    this.sender = sender;
    this.senderPassWord = senderPassWord;
    this.reciever = reciver;
  }

  protected final Boolean doInBackground(final String... logArray) {
    GMailSender gsender = new GMailSender(sender, senderPassWord);
    try {
      gsender.sendMail("Shh.. you message!",
          logArray[0].toString(), reciever, reciever);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
