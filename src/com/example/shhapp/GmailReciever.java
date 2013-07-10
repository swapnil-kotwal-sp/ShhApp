package com.example.shhapp;

/*
 * AsyncTask for instantiating GMailSender object
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.internal.utility.GMailUtil;

public class GmailReciever extends AsyncTask<String, Boolean, Boolean> {
  private String sender;
  private String senderPassWord;
  private ProgressDialog progress;
  private Activity shhActivity;
  List<String> from = new ArrayList<String>();
  List<String> messageBody = new ArrayList<String>();

  protected void onPreExecute() {
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

  public GmailReciever(final String sender, final String senderPassWord,
      Activity activity) {
    super();
    this.sender = sender;
    this.senderPassWord = senderPassWord;
    this.shhActivity = activity;
  }

  protected final Boolean doInBackground(String... agrs) {
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
  protected void onPostExecute(final Boolean success) {
    if (progress.isShowing()) {
      progress.dismiss();
    }
    if (success) {
      Toast.makeText(shhActivity, "All Email has been read", Toast.LENGTH_LONG)
      .show();
    } else
      Toast.makeText(shhActivity, "Email not able to read", Toast.LENGTH_LONG)
      .show();
  }
}
