package com.example.shhapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.internal.utility.GMailUtil;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ShhActivity extends
OrmLiteBaseActivity<com.internal.utility.DatabaseHelper> implements
OnItemSelectedListener {
  EditText messageTxt;
  Button encryptBtn;
  List<String> smsArray = new ArrayList<String>();
  List<String> messageBody = new ArrayList<String>();
  Spinner spinnerCategory;
  Spinner spinnerReadMail;

  /**
   * On Activity Creation initialize all UI elements.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    doSampleDatabaseStuff("onCreate");
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
        messageUtil.getRecieverMailIdAndSendMail(messageTxt.getText()
            .toString(), ShhActivity.this);
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
        messageUtil.readEmail("a");
        List<String> from = GMailUtil.from;
        messageBody = GMailUtil.messageBody;
        spinnerReadMail = (Spinner) findViewById(R.id.Spinner01);
        ArrayAdapter<String> mailAdapter = new ArrayAdapter<String>(
            ShhActivity.this, android.R.layout.simple_spinner_item, from);
        spinnerReadMail.setAdapter(mailAdapter);
        spinnerReadMail.setOnItemSelectedListener(ShhActivity.this);
      }
    });
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

    switch (parent.getId()) {
    case R.id.Spinner01:
      messageTxt.setText(messageBody.get(pos));
      break;
    case R.id.spinner:
      messageTxt.setText(textSms);
      break;
    default:
      messageTxt.setText("");
      break;
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {

  }

  /**
   * Do our sample database stuff.
   */
  private void doSampleDatabaseStuff(String action) {
    // get our dao
    RuntimeExceptionDao<User, Integer> simpleDao = getHelper()
    .getSimpleDataDao();
    // query for all of the data objects in the database
    List<User> list = simpleDao.queryForAll();
    // our string builder for building the content-view
    StringBuilder sb = new StringBuilder();
    sb.append("got ").append(list.size()).append(" entries in ").append(action)
    .append("\n");

    // if we already have items in the database
    int simpleC = 0;
    for (User simple : list) {
      sb.append("------------------------------------------\n");
      sb.append("[").append(simpleC).append("] = ").append(simple).append("\n");
      simpleC++;
    }
    sb.append("------------------------------------------\n");
    for (User simple : list) {
      simpleDao.delete(simple);
      sb.append("deleted id ").append(simple.getId()).append("\n");
      Log.i("User", "deleting simple(" + simple.getId() + ")");
      simpleC++;
    }
    int createNum;
    do {
      createNum = new Random().nextInt(3) + 1;
    } while (createNum == list.size());
    for (int i = 0; i < createNum; i++) {
      // create a new simple object
      long millis = System.currentTimeMillis();
      User simple = new User();
      // store it in the database
      simpleDao.create(simple);
      Log.i("User", "created simple(" + millis + ")");
      // output it
      sb.append("------------------------------------------\n");
      sb.append("created new entry #").append(i + 1).append(":\n");
      sb.append(simple).append("\n");
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        // ignore
      }
    }
    Log.i("User", "Done with page at " + System.currentTimeMillis());
  }
}