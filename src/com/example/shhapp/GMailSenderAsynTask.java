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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.internal.utility.GMailUtil;

/**
 * 
 * AsyncTask for sending mail
 * 
 */
public class GMailSenderAsynTask extends AsyncTask<String, Boolean, Boolean> {
  private String sender;
  private String reciever;
  private String senderPassWord;
  private ProgressDialog progress;
  private Activity shhActivity;

  protected final void onPreExecute() {
    // Show progress Dialog here
    super.onPreExecute();

    // create ProgressDialog here ...
    progress = new ProgressDialog(shhActivity);
    progress.setTitle("Shh.. ");
    progress.setMessage("Shh.. keep you voice down!");
    // set other progressbar attributes
    progress.setIndeterminate(true);
    progress.setCancelable(false);
    progress.show();

  }

  /**
   * Constructor for initializing GMailSender AsyncTask.
   * 
   * @param sender
   *          sender's mail Id.
   * @param senderPassWord
   *          sender's mail password.
   * @param reciver
   *          reciver's mail Id.
   * @param activity
   *          initializing activity.
   */
  public GMailSenderAsynTask(final String sender, final String senderPassWord,
      final String reciver, final Activity activity) {
    super();
    this.sender = sender;
    this.senderPassWord = senderPassWord;
    this.reciever = reciver;
    this.shhActivity = activity;
  }

  /**
   * background thread.
   * @return boolean
   */
  protected final Boolean doInBackground(final String... logArray) {
    GMailUtil gMailUtil = new GMailUtil(sender, senderPassWord);
    try {
      gMailUtil.sendMail("Shh.. you message!", logArray[0].toString(),
          reciever, reciever);
      publishProgress(true);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  protected final void onPostExecute(final Boolean success) {
    if (progress.isShowing()) {
      progress.dismiss();
    }
    if (success) {
      Toast.makeText(shhActivity, "Email has been sent", Toast.LENGTH_LONG)
      .show();
    } else {
      Toast.makeText(shhActivity, "Email not sent", Toast.LENGTH_LONG).show();
    }
  }
}