package com.example.shhapp;

import java.math.BigInteger;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;

public class MainActivity extends Activity {
  BigInteger encrypt = null;
  BigInteger decrypt = null;
  EditText messageTxt;
  RSA key;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    messageTxt = (EditText) findViewById(R.id.smsTextView);
    Button sendSMSButton = (Button) findViewById(R.id.btnSendSms);
    Button encryptBtn = (Button) findViewById(R.id.btnEncrptSms);
    Button decryptBtn = (Button) findViewById(R.id.btnDecrypt);
    Button email = (Button) findViewById(R.id.btnEmail);
    Button readSms = (Button) findViewById(R.id.btnReadSms);
    final EncryptionUtility encryptionUtil = new EncryptionUtility();
    encryptBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        encryptSMS();
      }
    });
    decryptBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        decryptSMS();
      }
    });
    sendSMSButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        invokeSMSApp();
      }
    });
    email.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        encryptionUtil.sendEmail(messageTxt.getText().toString());
      }
    });
    readSms.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        SMSRead();
      }
    });
  }

  public void invokeSMSApp() {
    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
    smsIntent.putExtra("sms_body", messageTxt.getText().toString());
    smsIntent.putExtra("address", "");
    smsIntent.setType("vnd.android-dir/mms-sms");
    startActivity(smsIntent);
  }

  public void SMSRead() {
    Uri uriSMSURI = Uri.parse("content://sms/inbox");
    Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
    String sms = "";

    while (cur.moveToNext()) {
      sms += "From :" + cur.getString(2) + " : " + cur.getString(11) + "\n";
    }
    messageTxt.setText(sms);
    Log.i("sms", sms);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  private void decryptSMS() {
    if (key != null) {
      decrypt = key.decrypt(encrypt);
      messageTxt.setText(new String(decrypt.toByteArray()));
      System.out.println("decrypted = " + decrypt);
      System.out.println("after decrypt the message is "
          + new String(decrypt.toByteArray()));
    }
  }

  private void encryptSMS() {
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
