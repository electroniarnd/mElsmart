package com.nordicsemi.nrfUARTv2;

import android.app.ActionBar;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.ArrayList;

public class recordLogs extends AppCompatActivity {
    Controllerdb controllerdb = new Controllerdb(this);
    SQLiteDatabase db;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> BadgeNo = new ArrayList<String>();
    private ArrayList<String> Ter = new ArrayList<String>();
    private ArrayList<String> direction = new ArrayList<String>();
    private ArrayList<String> time = new ArrayList<String>();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_logs);
        lv = (ListView) findViewById(R.id.lstvw);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header, lv, false);
       // lv.addHeaderView(headerView);
        lv.setSmoothScrollbarEnabled(true);

    }
    @Override
    protected void onResume() {
        displayData();
        super.onResume();
    }


    private void displayData() {
        db = controllerdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  tblLogs  order by Id desc LIMIT 120",null);
        Id.clear();
        date.clear();
        BadgeNo.clear();
     //   Name.clear();
        Ter.clear();
        time.clear();

      //  empid.clear();
        direction.clear();
        if (cursor.moveToFirst()) {
            do {
                Id.add(cursor.getString(cursor.getColumnIndex("Id")));
                date.add(cursor.getString(cursor.getColumnIndex("date")));
                time.add(cursor.getString(cursor.getColumnIndex("time")));

              //  Name.add(cursor.getString(cursor.getColumnIndex("Name")));
                Ter.add(cursor.getString(cursor.getColumnIndex("Ter")));
                //empid.add(cursor.getString(cursor.getColumnIndex("empid")));
                direction.add(cursor.getString(cursor.getColumnIndex("direction")));
                BadgeNo.add(cursor.getString(cursor.getColumnIndex("BadgeNo")));


            } while (cursor.moveToNext());
        }
        CustomAdapter ca = new CustomAdapter(recordLogs.this,Id, date,time,Ter,direction,BadgeNo);
        lv.setAdapter(ca);
        //code to set adapter to populate list
        cursor.close();
    }

}
