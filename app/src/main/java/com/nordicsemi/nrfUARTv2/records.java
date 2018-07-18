package com.nordicsemi.nrfUARTv2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class records extends Activity {
    Controllerdb controllerdb = new Controllerdb(this);
    SQLiteDatabase db;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> Name = new ArrayList<String>();
    private ArrayList<String> MailId = new ArrayList<String>();
    private ArrayList<String> Age = new ArrayList<String>();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        lv = (ListView) findViewById(R.id.lstvw);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header, lv, false);
        lv.addHeaderView(headerView);
        lv.setSmoothScrollbarEnabled(true);
    }
    @Override
    protected void onResume() {
        displayData();
        super.onResume();
    }
    private void displayData() {
        db = controllerdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  EmployeeDetails",null);
        Id.clear();
        Name.clear();
        MailId.clear();
        Age.clear();
        if (cursor.moveToFirst()) {
            do {
                Id.add(cursor.getString(cursor.getColumnIndex("Id")));
                Name.add(cursor.getString(cursor.getColumnIndex("Username")));
                MailId.add(cursor.getString(cursor.getColumnIndex("Mailid")));
                Age.add(cursor.getString(cursor.getColumnIndex("Age")));
            } while (cursor.moveToNext());
        }
        CustomAdapter ca = new CustomAdapter(records.this,Id, Name,MailId,Age);
        lv.setAdapter(ca);
        //code to set adapter to populate list
        cursor.close();
    }

}
