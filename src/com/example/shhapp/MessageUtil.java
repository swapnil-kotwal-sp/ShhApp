package com.example.shhapp;

import java.math.BigInteger;
import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;

public class MessageUtil {
  private ShhActivity shhActivity;
  private EditText messageTxt;
  private BigInteger encrypt = null;
  private BigInteger decrypt = null;
  private RSA key;

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
  
  public void SMSRead() {
    Uri uriSMSURI = Uri.parse("content://sms/inbox");
    Cursor cur = shhActivity.getContentResolver().query(uriSMSURI, null, null, null, null);
    String sms = "";

    while (cur.moveToNext()) {
      sms += "From :" + cur.getString(2) + " : " + cur.getString(11) + "\n";
    }
    messageTxt.setText(sms);
    Log.i("sms", sms);
  }
  public void readMail(){
    Intent intent = shhActivity.getPackageManager().getLaunchIntentForPackage("com.android.email");
    shhActivity.startActivity(intent);
  }

 public void encryptSMS() {
    if (key == null) {
      key = new RSA(4000);
    } 
    System.out.println(key);
    // create random message, encrypt.
    String s = messageTxt.getText().toString();
    BigInteger message = new BigInteger(s.getBytes());
    encrypt = key.encrypt(message);
    System.out.println("message   = " + message);
    System.out.println("hexa dicimal form of message " + message.toString(16));
    System.out.println("encrpyted = " + encrypt);
    messageTxt.setText(encrypt + "");
  }
  
  public void decryptSMS() {
    if (key != null) {
      decrypt = key.decrypt(encrypt);
      messageTxt.setText(new String(decrypt.toByteArray()));
      System.out.println("decrypted = " + decrypt);
      System.out.println("after decrypt the message is "
          + new String(decrypt.toByteArray()));
    }
  }
}
