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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.internal.utility.GMailUtil;

/**
 * 
 * AsyncTask for reading inbox mails.
 */
public class GmailReciever extends AsyncTask<String, Boolean, Boolean> {
  private String sender;
  private String senderPassWord;
  private ProgressDialog progress;
  private Activity shhActivity;
  List<String> from = new ArrayList<String>();
  List<String> messageBody = new ArrayList<String>();
/**
 * this block Execute before AsyncTask background thread start.
 */
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
 * Constructor for initializing GmailReciever object.
 * @param sender sender's email id.
 * @param senderPassWord sender's email password.
 * @param activity activity that initialize the task.
 */
  public GmailReciever(final String sender, final String senderPassWord,
      final Activity activity) {
    super();
    this.sender = sender;
    this.senderPassWord = senderPassWord;
    this.shhActivity = activity;
  }
/**
 * AsyncTask background thread.
 */
 
  protected final Boolean doInBackground(final String... agrs) {
    GMailUtil gMailUtil = new GMailUtil(sender, senderPassWord);
    try {
      gMailUtil.readMails();
      publishProgress(true);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      from = GMailUtil.from;
      messageBody = GMailUtil.messageBody;
      return false;
    }
  }

  @Override
  protected final void onPostExecute(final Boolean success) {
    if (progress.isShowing()) {
      progress.dismiss();
    }
    if (success) {
      Toast.makeText(shhActivity, "All Email has been read", Toast.LENGTH_LONG)
      .show();
    } else {
      Toast.makeText(shhActivity, "Email not able to read", Toast.LENGTH_LONG)
      .show();
    }
  }
}
