package com.example.shhapp;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectSms extends Activity {
  List<String> smsArray = new ArrayList<String>();
  private ListView lv;
  // Search EditText
  EditText inputSearch;
  ArrayAdapter<String> arrayAdapter;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_sms);
    smsArray.add("hi");
    smsArray.add("sdk");
    smsArray.add("pune");
    inputSearch = (EditText) findViewById(R.id.inputSearch);
    lv = (ListView) findViewById(R.id.listView1);
    arrayAdapter = new ArrayAdapter<String>(SelectSms.this,
        R.layout.list_example_entry, smsArray);
    arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
    lv.setAdapter(arrayAdapter);
    /**
     * Enabling Search Filter
     * */
    inputSearch.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence cs, int arg1, int arg2,
          int arg3) {
        // When user changed the Text
        SelectSms.this.arrayAdapter.getFilter().filter(cs);
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
          int arg3) {

      }

      @Override
      public void afterTextChanged(Editable arg0) {
      }
    });
    lv.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View arg1,
          int position, long arg3) {
        Object a = parent.getAdapter().getItem(position);
        Log.e("a  t ", a.toString());
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_select_sms, menu);
    return true;
  }

}
