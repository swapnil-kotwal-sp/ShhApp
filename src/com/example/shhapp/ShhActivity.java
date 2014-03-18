package com.example.shhapp;

/**  **/
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.internal.utility.GMailUtil;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ShhActivity extends OrmLiteBaseActivity<DatabaseHelper> implements
OnItemSelectedListener {
  EditText messageTxt;
  Button encryptBtn;
  Button decryptBtn;
  BigInteger encrypt;
  BigInteger decrypt;
  List<String> smsArray = new ArrayList<String>();
  List<String> messageBody = new ArrayList<String>();
  Spinner spinnerCategory;
  Spinner spinnerReadMail;
  private List<String> creadentials = new ArrayList<String>();
  private RuntimeExceptionDao<Contact, Integer> dao;

  private RSA key = null;

  /**
   * On Activity Creation initialize all UI elements.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // Initialize all UI elements
    messageTxt = (EditText) findViewById(R.id.viewSmsText);
    Button sendSMSButton = (Button) findViewById(R.id.btnSendSms);
    encryptBtn = (Button) findViewById(R.id.btnEncrptSms);
    encryptBtn.setEnabled(false);
    decryptBtn = (Button) findViewById(R.id.btnDecrypt);
    decryptBtn.setEnabled(false);
    Button sendEmail = (Button) findViewById(R.id.btnEmail);
    Button readSms = (Button) findViewById(R.id.btnReadSms);
    Button readMails = (Button) findViewById(R.id.btnReadEmail);

    // Initialize encryption object
    key = new RSA();

    dao = getHelper().getRuntimeExceptionDao(Contact.class);
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
        if (key != null) {
          try {
            String s = messageTxt.getText().toString();
            BigInteger message = new BigInteger(s.getBytes());
            encrypt = key.encrypt(message);
            messageTxt.setText(encrypt + "");
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    });

    decryptBtn.setOnClickListener(new OnClickListener() {
      // this section needs to be handled through arrays and not from edittex
      // box.
      @Override
      public void onClick(View arg0) {
        if (key != null) {
          if (encrypt != null) {
            decrypt = key.decrypt(encrypt);
            messageTxt.setText(new String(decrypt.toByteArray()));
          } else if (messageTxt.getText().length() > 0
              && !messageTxt.getText().equals("")) {
            String encryptedMessage = messageTxt.getText().toString();
            encryptedMessage = encryptedMessage.replace(" ", "");
            encryptedMessage = encryptedMessage.trim();
            Log.i("encryptedMessage", encryptedMessage);
            try {
              decrypt = (BigInteger) key.decrypt(new BigInteger(
                  encryptedMessage));
              messageTxt.setText(new String(decrypt.toByteArray()));
            } catch (Exception e) {
              e.printStackTrace();
              Toast.makeText(ShhActivity.this, "Your text cannot be decrypted",
                  Toast.LENGTH_SHORT).show();
            }
          } else
            Toast.makeText(ShhActivity.this, "Your text cannot be decrypted",
                Toast.LENGTH_SHORT).show();
        }
      }
    });
    sendSMSButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        messageUtil.invokeSMSApp(messageTxt.getText().toString());
      }
    });
    sendEmail.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        creadentials = ShhActivity.this.isRegisteredUser();
        if (creadentials.size() > 0) {
          messageUtil.getRecieverMailIdAndSendMail(creadentials.get(0),
              creadentials.get(1), messageTxt.getText().toString(),
              ShhActivity.this);
        } else
          messageUtil.registerUserMailId();
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
        creadentials = ShhActivity.this.isRegisteredUser();
        if (creadentials.size() > 0) {
          messageUtil.readEmail(creadentials.get(0), creadentials.get(1));
          List<String> from = GMailUtil.from;
          messageBody = GMailUtil.messageBody;
          if (from != null) {
            spinnerReadMail = (Spinner) findViewById(R.id.Spinner01);
            ArrayAdapter<String> mailAdapter = new ArrayAdapter<String>(
                ShhActivity.this, android.R.layout.simple_spinner_item, from);
            spinnerReadMail.setAdapter(mailAdapter);
            spinnerReadMail.setOnItemSelectedListener(ShhActivity.this);
          }
        } else
          messageUtil.registerUserMailId();
      }
    });
  }

  protected void enableSubmitIfReady() {
    if (messageTxt.getText().toString().trim().length() > 0) {
      if (key != null) {
        encryptBtn.setEnabled(true);
      }
      if (key != null) {
        decryptBtn.setEnabled(true);
      }
    } else {
      encryptBtn.setEnabled(false);
      decryptBtn.setEnabled(false);
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long id) {
    String textSms = parent.getItemAtPosition(pos).toString();

    switch (parent.getId()) {
    case R.id.Spinner01:
      if (messageBody.size() > pos) {
        String message = messageBody.get(pos);
        message = message.replace(" ", "");
        message = message.trim();
        Log.i("message body ", message + " position " + pos);
        messageTxt.setText(message);
      }
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
   * Query for all the regs
   */
  private List<String> queryForAll() {
    // query for all of the data objects in the database
    List<Contact> list = dao.queryForAll();
    List<String> creadentials = new ArrayList<String>();
    for (Contact contact : list) {
      creadentials.add(contact.email);
      creadentials.add(contact.password);
    }
    return creadentials;
  }

  private void addContact(String Email, String password)
  throws NoSuchAlgorithmException, UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder();
    sb.append("Added contact " + Email);
    dao.create(new Contact(Email, password));
    Log.i("results", sb.toString());
  }

  public List<String> isRegisteredUser() {
    return queryForAll();
  }

  public void saveUserInfo(String userName, String password)
  throws NoSuchAlgorithmException, UnsupportedEncodingException {
    addContact(userName, password);
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
