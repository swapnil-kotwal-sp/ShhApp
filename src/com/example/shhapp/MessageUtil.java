package com.example.shhapp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageUtil {
  private ShhActivity shhActivity;
  private RSAAsynckTask rsaTask = null;
  List<String> smsList = new ArrayList<String>();
  List<String> from = new ArrayList<String>();
  List<String> messageBody = new ArrayList<String>();
  public static String userEmailID;

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
    GmailReciever gMailSenderAsynTask = new GmailReciever("user@gmail.com",
        "password", shhActivity);
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

  public void sendEmail(String reciverMailId, String message) {
    GMailSenderAsynTask gMailSenderAsynTask = new GMailSenderAsynTask(
        "user@gmail.com", "pwd",
        reciverMailId, shhActivity);
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

  /**
   * Dialog for getting user's name and email_id.
   * 
   * @param message
   */
  public void getRecieverMailIdAndSendMail(final String message, Activity activity) {
    final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
    final AlertDialog dialog = alert.create();
    LayoutInflater inflater = activity.getLayoutInflater();
    final View v = inflater.inflate(R.layout.dialog_signin, null);
    dialog.setView(v);
    dialog.setCancelable(false);
    dialog.show();
    Button button = (Button) dialog.findViewById(R.id.Button01);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(final View v1) {
        EditText usernameEditext = (EditText) v.findViewById(R.id.username);
        if (usernameEditext != null) {
          String emailUserName = usernameEditext.getText().toString();
          Boolean flag = true;
          if (emailUserName.equals("")) {
            flag = false;
            usernameEditext.setError("email_id is null");
          }
          if (flag && !validEmailAddress(emailUserName)) {
            flag = false;
            usernameEditext.setError("invalid email_id");
          }
          if (flag) {
            dialog.dismiss();
            sendEmail(emailUserName, message);
          }
        }
      }
    });
  }

  // Java mail API to validate email address.
  public static boolean validEmailAddress(final String email) {
    boolean result = true;
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
    } catch (AddressException ex) {
      result = false;
    }
    return result;
  }
}
