package com.example.shhapp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;

public class MessageUtil {
  private ShhActivity shhActivity;
  private EditText messageTxt;
  private RSAAsynckTask rsaTask = null;
  List<String> smsArray = new ArrayList<String>();

  public MessageUtil(ShhActivity shhActivity) {
    this.shhActivity = shhActivity;
    messageTxt = shhActivity.messageTxt;
  }

  public void sendLongSMS() {
    String phoneNumber = "";
    String message = "Hello World! Now we are going to demonstrate "
      + "how to send a message with more than 160 characters from your Android application.";

    SmsManager smsManager = SmsManager.getDefault();
    ArrayList<String> parts = smsManager.divideMessage(message);
    smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
  }

  public void invokeSMSApp() {
    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
    smsIntent.putExtra("sms_body", messageTxt.getText().toString());
    smsIntent.putExtra("address", "");
    smsIntent.setType("vnd.android-dir/mms-sms");
    shhActivity.startActivity(smsIntent);
  }

  public void sendEmail(String message) {
    GMailSenderAsynTask gMailSenderAsynTask = new GMailSenderAsynTask(
        "ooyalatester@vertisinfotech.com", "!password*",
        "swapnil@vertisinfotech.com", shhActivity);
    gMailSenderAsynTask.execute(message);
    try {
      if (!gMailSenderAsynTask.get(8000, TimeUnit.MILLISECONDS)) {
        Log.e("email", "password may not be correct");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<String> SMSRead() {
    Uri uriSMSURI = Uri.parse("content://sms/inbox");
    Cursor cur = shhActivity.getContentResolver().query(uriSMSURI, null, null,
        null, null);
    String sms = "";
    int i = 0;
    while (cur.moveToNext()) {
      sms += "From :" + cur.getString(2) + " : " + cur.getString(11) + "\n";
      smsArray.add(cur.getString(11));
    }
    messageTxt.setText(smsArray.get(0));
    return smsArray;
  }

  public void readMail() {
    Intent intent = shhActivity.getPackageManager().getLaunchIntentForPackage(
    "com.android.email");
    shhActivity.startActivity(intent);
  }

  public void encryptSMS() throws InterruptedException, ExecutionException,
  TimeoutException {
    rsaTask = new RSAAsynckTask(shhActivity);
    rsaTask.execute(5000);
    rsaTask.get(8000, TimeUnit.MILLISECONDS);
  }

  public void decryptSMS() {
    if (rsaTask != null) {
      rsaTask.decryptSMS();
    }
  }
}
