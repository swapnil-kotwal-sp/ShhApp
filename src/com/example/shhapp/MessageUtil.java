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

public class MessageUtil {
  private ShhActivity shhActivity;
  private RSAAsynckTask rsaTask = null;
  List<String> smsList = new ArrayList<String>();
  List<String> from = new ArrayList<String>();
  List<String> messageBody = new ArrayList<String>();

  public MessageUtil(ShhActivity shhActivity) {
    this.shhActivity = shhActivity;
  }

  public void invokeSMSApp(String smsBody) {
    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
    smsIntent.putExtra("address", "");
    smsIntent.putExtra("sms_body", smsBody);
    smsIntent.setData(Uri.parse("smsto:" + ""));
    // smsIntent.setType("vnd.android-dir/mms-sms");
    shhActivity.startActivity(smsIntent);
  }

  public void readEmail(String message) {
    GmailReciever gMailSenderAsynTask = new GmailReciever(
        "user@gmail.com", "password", shhActivity);
    gMailSenderAsynTask.execute(message);
    try {
      if (!gMailSenderAsynTask.get(8000, TimeUnit.MILLISECONDS)) {
        Log.e("email", "password may not be correct");
      }
    } catch (Exception e) {
      if (e != null) {
        e.printStackTrace();
      }
    }
  }

  public void sendEmail(String message) {
    GMailSenderAsynTask gMailSenderAsynTask = new GMailSenderAsynTask(
        "user@gmail.com", "password",
        "user@gmail.com", shhActivity);
    gMailSenderAsynTask.execute(message);
    try {
      if (!gMailSenderAsynTask.get(8000, TimeUnit.MILLISECONDS)) {
        Log.e("email", "password may not be correct");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    gMailSenderAsynTask.cancel(true);
  }

  public List<String> SMSRead() {
    Uri uriSMSURI = Uri.parse("content://sms/inbox");
    Cursor cur = shhActivity.getContentResolver().query(uriSMSURI, null, null,
        null, null);
    try {
      for (boolean hasData = cur.moveToFirst(); hasData; hasData = cur
          .moveToNext()) {
        final String address = cur.getString(cur.getColumnIndex("address"));
        final String body = cur.getString(cur.getColumnIndexOrThrow("body"));
        smsList.add("Number: " + address + " .Message: " + body);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return smsList;
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
