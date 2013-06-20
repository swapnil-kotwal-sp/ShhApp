package com.example.shhapp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.analytics.tracking.android.EasyTracker;

public class ShhActivity extends Activity implements OnItemSelectedListener {
  EditText messageTxt;
  Button encryptBtn;
  List<String> smsArray = new ArrayList<String>();
  List<String> messageBody;
  Spinner spinnerCategory;
  Spinner spinnerReadMail;
/**
 * On Activity Creation initialize all UI elements.
 */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    messageTxt = (EditText) findViewById(R.id.viewSmsText);
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
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
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
        }
      }
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
        messageUtil.invokeSMSApp(messageTxt.getText().toString());
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
        smsArray = messageUtil.SMSRead();
        spinnerCategory = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
            ShhActivity.this, android.R.layout.simple_spinner_item, smsArray);
        spinnerCategory.setAdapter(categoriesAdapter);
        spinnerCategory.setOnItemSelectedListener(ShhActivity.this);
      }
    });
    readMails.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        ReadGmails newGmailClient = new ReadGmails();
        newGmailClient.setAccountDetails("nehakumar001988@gmail.com",
            "goodsamaritan");
        newGmailClient.execute("a");
        try {
          newGmailClient.get();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        } catch (ExecutionException e1) {
          e1.printStackTrace();
        }
        List<String> from = newGmailClient.from;
       messageBody = newGmailClient.messageBody;
        spinnerReadMail = (Spinner) findViewById(R.id.Spinner01);
        ArrayAdapter<String> mailAdapter = new ArrayAdapter<String>(
            ShhActivity.this, android.R.layout.simple_spinner_item, from);
        spinnerReadMail.setAdapter(mailAdapter);
        spinnerReadMail.setOnItemSelectedListener(ShhActivity.this);
      }
    });
  }

  @SuppressWarnings("deprecation")
  private Cursor getContacts() {
    // Run query
    Uri uri = ContactsContract.Contacts.CONTENT_URI;
    String[] projection = new String[] { ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME };
    String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
        + ("1") + "'";
    String[] selectionArgs = null;
    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
        + " COLLATE LOCALIZED ASC";

    return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
  }

  protected void enableSubmitIfReady() {
    if (messageTxt.getText().toString().trim().length() > 0) {
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

  @Override
  public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long id) {
    String textSms = parent.getItemAtPosition(pos).toString();
    messageTxt.setText(textSms);
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
    // TODO Auto-generated method stub

  }
}
