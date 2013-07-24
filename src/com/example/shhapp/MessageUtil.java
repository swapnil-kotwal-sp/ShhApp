/*
 * Copyright 2013 Swapnil Kotwal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.example.shhapp;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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

/**
 * 
 * Utility object for messages.
 * 
 */
public class MessageUtil {
  private ShhActivity shhActivity;
  List<String> smsList = new ArrayList<String>();
  List<String> from = new ArrayList<String>();
  List<String> messageBody = new ArrayList<String>();

  public static String userEmailID;

  /**
   * Constructor for initializing MessageUtil object.
   * 
   * @param shhActivity
   *          initializing activity.
   */
  public MessageUtil(final ShhActivity shhActivity) {
    this.shhActivity = shhActivity;
  }

  /**
   * 
   * @param smsBody
   *          sms text.
   */
  public final void invokeSMSApp(final String smsBody) {
    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
    smsIntent.putExtra("address", "");
    smsIntent.putExtra("sms_body", smsBody);
    smsIntent.setData(Uri.parse("smsto:" + ""));
    // smsIntent.setType("vnd.android-dir/mms-sms");
    shhActivity.startActivity(smsIntent);
  }

  /**
   * read registered users emails.
   * 
   * @param emailId
   *          users email Id.
   * @param password
   *          users email password.
   */
  public final void readEmail(final String emailId, final String password) {
    GmailReciever gMailSenderAsynTask = new GmailReciever(emailId, password,
        shhActivity);
    gMailSenderAsynTask.execute("a");
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

  /**
   * Send mail to registered Gmail user id.
   * 
   * @param emailId
   *          users email Id.
   * @param password
   *          users email password.
   * @param reciverMailId
   *          reciver's email Id.
   * @param message
   *          mail body.
   */
  public final void sendEmail(final String emailId, final String password,
      final String reciverMailId, final String message) {
    GMailSenderAsynTask gMailSenderAsynTask = new GMailSenderAsynTask(emailId,
        password, reciverMailId, shhActivity);
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

  /**
   * Reads inbox SMS.
   * 
   * @return List<String> messages.
   */
  public final List<String> SMSRead() {
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

  /**
   * read registered user's mails.
   */
  public final void readMail() {
    Intent intent = shhActivity.getPackageManager().getLaunchIntentForPackage(
    "com.android.email");
    shhActivity.startActivity(intent);
  }

  /**
   * encrypt given message.
   * 
   * @param rsaTask
   *          rsaAsyncktask object.
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws TimeoutException
   */
  public final void encryptSMS(final RSAAsynckTask rsaTask)
  throws InterruptedException, ExecutionException, TimeoutException {
    if (rsaTask != null) {

    }
  }

  /**
   * decrypt's message.
   * 
   * @param rsaTask
   *          rsaAsyncktask object.
   */
  public final void decryptSMS(final RSAAsynckTask rsaTask) {
    if (rsaTask != null) {
      rsaTask.decryptSMS();
    }
  }

  /**
   * Dialog for getting user's name and email_id.
   * 
   * @param emailId
   *          user mail Id.
   * @param password
   *          user mail password.
   * @param message
   *          message to be delivered.
   * @param activity
   *          initializing activity.
   */
  public final void getRecieverMailIdAndSendMail(final String emailId,
      final String password, final String message, final Activity activity) {
    final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
    final AlertDialog dialog = alert.create();
    LayoutInflater inflater = activity.getLayoutInflater();
    final View v = inflater.inflate(R.layout.dialog_reciver, null);
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
            sendEmail(emailId, password, emailUserName, message);
          }
        }
      }
    });
  }

  /**
   * Java mail API to validate email address.
   * 
   * @param email
   *          email address.
   * @return boolean
   */
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

  /**
   * Register user email and password.
   */
  public final void registerUserMailId() {
    final AlertDialog.Builder alert = new AlertDialog.Builder(shhActivity);
    final AlertDialog dialog = alert.create();
    LayoutInflater inflater = shhActivity.getLayoutInflater();
    final View v = inflater.inflate(R.layout.dialog_signin, null);
    dialog.setView(v);
    dialog.setCancelable(false);
    dialog.show();
    Button button = (Button) dialog.findViewById(R.id.Button01);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(final View v1) {
        EditText usernameEditext = (EditText) v.findViewById(R.id.username);
        EditText passwordEdittextBox = (EditText) v.findViewById(R.id.password);
        if (passwordEdittextBox != null && usernameEditext != null) {
          String userName = usernameEditext.getText().toString();
          if (userName.equals("")) {
            usernameEditext.setError("username empty");
          }
          String password = passwordEdittextBox.getText().toString();
          Boolean flag = true;
          if (password.equals("")) {
            flag = false;
            passwordEdittextBox.setError("password is null");
          }
          if (flag && !validEmailAddress(userName)) {
            flag = false;
            usernameEditext.setError("invalid email_id");
          }
          if (flag && !userName.equals("")) {
            dialog.dismiss();
            try {
              shhActivity.saveUserInfo(userName, password);
            } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });
  }
}
