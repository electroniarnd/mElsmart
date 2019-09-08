package com.electronia.mElsmart;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Device extends AppCompatActivity {
    Controllerdb controllerdb = new Controllerdb(this);
    SQLiteDatabase db;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> MacAddress = new ArrayList<String>();
    private ArrayList<String> DeviceName = new ArrayList<String>();
    private ArrayList<String> Delete = new ArrayList<String>();
    ListView lv;
    Button deletebtn;
    public static final String TAG = "Elsmart";
    public static final String Table_Name="Device";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        lv = (ListView) findViewById(R.id.lstvw);
        deletebtn= (Button) findViewById(R.id.btn_deleteTer);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header_mac, lv, false);
        // lv.addHeaderView(headerView);
        lv.setSmoothScrollbarEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i=  Deletedata();

                if(i>0)
                {
                    Toast.makeText(Device.this, getResources().getString(R.string.Data_deleted), Toast.LENGTH_SHORT).show();
                    displayData();
                }
                else
                {
                    Toast.makeText(Device.this,
                            getResources().getString(R.string.Fail_to_Delete), Toast.LENGTH_SHORT).show();
                }
            }
        });





        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                Log.i("List View Clicked", "**********");
                displayData();
               // Toast.makeText(Device.this, getResources().getString(R.string.List_View_Clicked) + position, Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        displayData();
        super.onResume();
    }

    private void displayData() {
        db = controllerdb.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  Device",null);
        Id.clear();
        MacAddress.clear();
        DeviceName.clear();
        Delete.clear();;
        if (cursor.moveToFirst()) {
            do {
                Id.add(cursor.getString(cursor.getColumnIndex("ID")));
                MacAddress.add(cursor.getString(cursor.getColumnIndex("MacId")));
                DeviceName.add(cursor.getString(cursor.getColumnIndex("Name")));
                Delete.add(cursor.getString(cursor.getColumnIndex("ID")));
            } while (cursor.moveToNext());
        }
        CustomAdapter_Mac ca = new CustomAdapter_Mac(Device.this,Id, MacAddress,DeviceName,Delete);
        lv.setAdapter(ca);
        //code to set adapter to populate list
        cursor.close();
    }






    public Integer Deletedata()
    {
        Integer res=0;
        try {
            db = controllerdb.getWritableDatabase();
            db.execSQL("Delete from Device" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    public void delete() {



    }




}
