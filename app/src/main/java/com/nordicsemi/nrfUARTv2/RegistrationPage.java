package com.nordicsemi.nrfUARTv2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class RegistrationPage extends Activity {
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    private  int count=0;
    Button btnRegister;
    private static final String TAG ="Registration Page" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        ReadTrackingValue();
if(count > 0)
{
  Intent mainpage = new Intent(this,mainpage.class);
  startActivity(mainpage);
}


        btnRegister.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle sendBundle = new Bundle();
                sendBundle.putString("ResID","1");
                Intent intent = new Intent(RegistrationPage.this,RegistrationActivity.class);
                intent.putExtras(sendBundle);;
                startActivity(intent);
                finish();
            }
        });
    }

    public Integer ReadTrackingValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue="";
        int res =0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT count(*) as cnt From   Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    count =  Integer.valueOf(cursor.getString(cursor.getColumnIndex("cnt")));

                } while (cursor.moveToNext());
            }
            cursor.close();
            res =1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }



}
