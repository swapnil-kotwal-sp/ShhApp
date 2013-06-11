package com.example.shhapp;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

public class EncryptionUtility {

  public void sendLongSMS() {
    String phoneNumber = "";
    String message = "Hello World! Now we are going to demonstrate "
        + "how to send a message with more than 160 characters from your Android application.";

    SmsManager smsManager = SmsManager.getDefault();
    ArrayList<String> parts = smsManager.divideMessage(message);
    smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
  }
  public void sendEmail(String message) {
    AsyncTask<String, Void, Boolean> gMailSenderAsynTask = new GMailSenderAsynTask(
        "ooyalatester@vertisinfotech.com", "!password*",
        "swapnil@vertisinfotech.com");
    gMailSenderAsynTask.execute(message);
    try {
      if (!gMailSenderAsynTask.get()) {
        Log.e("email", "password may not be correct");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
