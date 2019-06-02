package com.nordicsemi.nrfUARTv2;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import android.hardware.fingerprint.FingerprintManager;




public class setting extends AppCompatActivity {
    Switch AutoPunchSwitch, VibrationSwitch,BiometricSwich;
    Button submit;
    Integer Vib=0,Debug=0,AutoPunch=0,Biometric=0;
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    Integer RecordValue=-2;
    private Integer status=0;
    public static final String TAG = "ElSmart";



    private FingerprintManager fingerprintManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        AutoPunchSwitch = (Switch) findViewById(R.id.AutoPunchSwitch);
        VibrationSwitch = (Switch) findViewById(R.id.VibrationSwitch);

        BiometricSwich = (Switch) findViewById(R.id.BiometricSwich);
        RecordValue=IsRecordExist();
        if(RecordValue==-1) {
            Toast.makeText(this,"Error in Reading for system_setting", Toast.LENGTH_LONG).show();
        }
        if(RecordValue==0) {
             if(InsertSysSetting()==0)
                 Toast.makeText(this,"Error in Inserting for system_setting", Toast.LENGTH_LONG).show();
        }
        if(RecordValue>0) {
            if(ReadSysSetting()==0)
                Toast.makeText(this,"Error in Reading system_setting Table" , Toast.LENGTH_LONG).show();
        }

        if(AutoPunch==1) {
            AutoPunchSwitch.setChecked(true);
            AutoPunchSwitch.setTextOn("ON");
        }
        else
        {
            AutoPunchSwitch.setChecked(false);
            AutoPunchSwitch.setTextOff("OFF");
        }


        if(Vib==1) {
            VibrationSwitch.setChecked(true);
            VibrationSwitch.setTextOn("ON");
        }
        else
        {
            VibrationSwitch.setChecked(false);
            VibrationSwitch.setTextOff("OFF");
        }

        if(Biometric==1) {
            BiometricSwich.setChecked(true);
            BiometricSwich.setTextOn("ON");
        }
        else
        {
            BiometricSwich.setChecked(false);
            BiometricSwich.setTextOff("OFF");
        }

       /// submit.setOnClickListener(new View.OnClickListener() {
        ///    @Override
         ///   public void onClick(View view) {
               //// String statusSwitch1, statusSwitch2;
             ////   if (simpleSwitch1.isChecked())
              ///      statusSwitch1 = simpleSwitch1.getTextOn().toString();
             ///   else
            ///        statusSwitch1 = simpleSwitch1.getTextOff().toString();
            ////    if (simpleSwitch2.isChecked())
            ///        statusSwitch2 = simpleSwitch2.getTextOn().toString();
           ///     else
           ///         statusSwitch2 = simpleSwitch2.getTextOff().toString();
         ///       Toast.makeText(getApplicationContext(), "Switch1 :" + statusSwitch1 + "\n" + "Switch2 :" + statusSwitch2, Toast.LENGTH_LONG).show(); // display the current state for switch's
         ///   }
     ////   });



        AutoPunchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    AutoPunch=1; //edit here
                }else{
                    AutoPunch=0; //edit here
                }
                if(UpdateAutoPunch(AutoPunch)==0) {
                    Toast.makeText(getApplicationContext(), "Error in Updation for AutoPunch", Toast.LENGTH_LONG).show();
                }
            }

        }

        );

        VibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    Vib=1; //edit here
                }else{
                    Vib=0; //edit here
                }
                if(UpdateVib(Vib)==0)
                    Toast.makeText(getApplicationContext(),"Error in Updation for Vibration" , Toast.LENGTH_LONG).show();


            }
        });



        BiometricSwich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    Biometric=1; //edit here
                }else{
                    Biometric=0; //edit here
                }
                if(UpdateBiometric(Biometric)==0)
                    Toast.makeText(getApplicationContext(),"Error in Updation for Biometric", Toast.LENGTH_LONG).show();
            }
        });
    }



    public Integer IsRecordExist()
    {
        int count =0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  system_setting", null);
            if (cursor.moveToFirst()) {
                do {
                    count++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        catch (Exception ex) {
            count=-1;
            Log.d(TAG,  ex.getMessage());
        }
        return count;
    }


    public Integer InsertSysSetting()
    {
        int res =0;
        try {
            database=db.getWritableDatabase();
            database.execSQL("INSERT INTO system_setting(Vibration,Debug,AutoPunch)VALUES("+Vib+","+Debug+","+AutoPunch+")" );
        res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG,  ex.getMessage());

        }
        return res;
    }

    public Integer UpdateSysSetting()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("UPDATE system_setting set Vibration=0,Debug=0,AutoPunch=0");// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
        res=1;
        }
        catch (Exception ex) {
           res=0;
            Log.d(TAG, ex.getMessage());
            Toast.makeText(this,"Error in Update for systemsetting table" , Toast.LENGTH_LONG).show();
        }
        return res;

    }


    public Integer UpdateVib(Integer Vib)
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("UPDATE system_setting set Vibration= "+Vib);// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }

    public Integer UpdateAutoPunch(Integer Autopunch)
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("UPDATE system_setting set AutoPunch= "+Autopunch);// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    public Integer UpdateBiometric(Integer Biometric)
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            if(Biometric==1)
            {
               if( checkFinger())
                   database.execSQL("UPDATE system_setting set Biometric= "+Biometric);
            }
           else
            database.execSQL("UPDATE system_setting set Biometric= "+Biometric);// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG,  ex.getMessage());
        }
        return res;
    }


    public Integer ReadSysSetting()
    {
        int res =0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  system_setting", null);
            if (cursor.moveToFirst()) {
                do {
                    Vib =  Integer.valueOf( cursor.getString(cursor.getColumnIndex("Vibration")));
                     Debug =   Integer.valueOf(cursor.getString(cursor.getColumnIndex("Debug")));
                  AutoPunch= Integer.valueOf( cursor.getString(cursor.getColumnIndex("AutoPunch")));
                    Biometric= Integer.valueOf( cursor.getString(cursor.getColumnIndex("Biometric")));
                } while (cursor.moveToNext());
            }
            cursor.close();
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkFinger() {
 String message="";
        // Keyguard Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        // Fingerprint Manager
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        try {
            // Check if the fingerprint sensor is present
            if (!fingerprintManager.isHardwareDetected()) {
                message="Fingerprint authentication not supported";
                PAlertDialog("Biometric",message);
                return false;
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                message="No fingerprint configured.";
                PAlertDialog("Biometric",message);
                return false;
            }




        }
        catch(SecurityException se) {
            se.printStackTrace();
        }


        return true;

    }

    private void PAlertDialog(String title, String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(setting.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton( getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }






}
