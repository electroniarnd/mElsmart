package com.electronia.mElsmart;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import androidx.appcompat.app.ActionBarActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class recordLogs extends AppCompatActivity {
    Controllerdb controllerdb = new Controllerdb(this);
    SQLiteDatabase db;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
 //   private ArrayList<String> BadgeNo = new ArrayList<String>();
    private ArrayList<String> Ter = new ArrayList<String>();
    private ArrayList<String> direction = new ArrayList<String>();
    private ArrayList<String> time = new ArrayList<String>();
    private ArrayList<String> name = new ArrayList<String>();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_logs);
        lv = (ListView) findViewById(R.id.lstvw);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header, lv, false);
       // lv.addHeaderView(headerView);
        lv.setSmoothScrollbarEnabled(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    @Override
    protected void onResume() {
        displayData();
        super.onResume();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayData() {
        db = controllerdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  tblLogs  order by Id desc LIMIT 120",null);
        Id.clear();
        date.clear();
       // BadgeNo.clear();
     //   Name.clear();
        Ter.clear();
        time.clear();
        name.clear();
      //  empid.clear();
        direction.clear();
        if (cursor.moveToFirst()) {
            do {
                Id.add(cursor.getString(cursor.getColumnIndex("Id")));
                date.add(cursor.getString(cursor.getColumnIndex("date")));
                time.add(cursor.getString(cursor.getColumnIndex("time")));

              //  Name.add(cursor.getString(cursor.getColumnIndex("Name")));
                Ter.add(cursor.getString(cursor.getColumnIndex("PunchType")));
                //empid.add(cursor.getString(cursor.getColumnIndex("empid")));
                if(cursor.getString(cursor.getColumnIndex("direction")).equals("1"))
                direction.add("IN");
                else
                    direction.add("OUT");
              //  BadgeNo.add(cursor.getString(cursor.getColumnIndex("BadgeNo")));
                name.add(cursor.getString(cursor.getColumnIndex("Ter")));

            } while (cursor.moveToNext());
        }
        CustomAdapter ca = new CustomAdapter(recordLogs.this,Id, date,time,Ter,direction,name);
        lv.setAdapter(ca);
        //code to set adapter to populate list
        cursor.close();
    }

}
