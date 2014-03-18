package com.example.shhapp;

/*
 * AsyncTask for instantiating GMailSender object
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.internal.utility.GMailUtil;

public class GMailSenderAsynTask extends AsyncTask<String, Boolean, Boolean> {
  private String sender;
  private String reciever;
  private String senderPassWord;
  private ProgressDialog progress;
  private Activity shhActivity;
  
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
  public GMailSenderAsynTask(final String sender, final String senderPassWord,
      final String reciver, Activity activity) {
    super();
    this.sender = sender;
    this.senderPassWord = senderPassWord;
    this.reciever = reciver;
    this.shhActivity = activity;
  }

  protected final Boolean doInBackground(final String... logArray) {
    GMailUtil gMailUtil = new GMailUtil(sender, senderPassWord);
    try {
      gMailUtil.sendMail("Shh.. you message!", logArray[0].toString(), reciever,
          reciever);
      publishProgress(true);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  protected void onPostExecute(final Boolean success) {
    if (progress.isShowing()) {
      progress.dismiss();
    }
    if(success){
      Toast.makeText(shhActivity, "Email has been sent", Toast.LENGTH_LONG).show();
    }else
      Toast.makeText(shhActivity, "Email not sent", Toast.LENGTH_LONG).show();
  }
}