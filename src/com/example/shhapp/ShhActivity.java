package com.example.shhapp;

import android.R.bool;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;

public class ShhActivity extends Activity {
  EditText messageTxt;
  Button encryptBtn;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    messageTxt = (EditText) findViewById(R.id.smsTextView);
    Button sendSMSButton = (Button) findViewById(R.id.btnSendSms);
    encryptBtn = (Button) findViewById(R.id.btnEncrptSms);
    encryptBtn.setEnabled(false);
    Button decryptBtn = (Button) findViewById(R.id.btnDecrypt);
    Button email = (Button) findViewById(R.id.btnEmail);
    Button readSms = (Button) findViewById(R.id.btnReadSms);
    Button readMails = (Button) findViewById(R.id.btnReadEmail);
    final MessageUtil messageUtil = new MessageUtil(this);
    messageTxt.addTextChangedListener(new TextWatcher() {
      @Override
      public void afterTextChanged(Editable arg0) {
         enableSubmitIfReady();
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });
    encryptBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        try {
          messageUtil.encryptSMS();
        } catch (Exception e) {
          e.printStackTrace();
      }}
    });
    decryptBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        messageUtil.decryptSMS();
      }
    });
    sendSMSButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        messageUtil.invokeSMSApp();
      }
    });
    email.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        messageUtil.sendEmail(messageTxt.getText().toString());
      }
    });
    readSms.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        messageUtil.SMSRead();
      }
    });
    readMails.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        messageUtil.readMail();
      }
    });
  }


  protected void enableSubmitIfReady() {
    if(messageTxt.getText().toString().trim().length() > 0){
      encryptBtn.setEnabled(true);
   } else {
     encryptBtn.setEnabled(false);
    }    
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }


  @Override
  public void onStart() {
    super.onStart();
    // Initialise google analytics for this app.
    EasyTracker.getInstance().activityStart(this); // Add this method.
  }

  @Override
  public void onStop() {
    super.onStop();
    // The rest of your onStop() code.
    EasyTracker.getInstance().activityStop(this); // Add this method.
  }
}
