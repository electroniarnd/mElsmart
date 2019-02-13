package com.nordicsemi.nrfUARTv2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i=  Deletedata();

                if(i>0)
                {
                    Toast.makeText(Device.this, "Data deleted", Toast.LENGTH_SHORT).show();
                    displayData();
                }
                else
                {
                    Toast.makeText(Device.this,
                            "Fail to Delete", Toast.LENGTH_SHORT).show();
                }
            }
        });





        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                Log.i("List View Clicked", "**********");
                Toast.makeText(Device.this, "List View Clicked:" + position, Toast.LENGTH_LONG).show();
            }
        });


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
