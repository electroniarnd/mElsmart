package com.electronia.mElsmart;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompatSideChannelService;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MPRequest extends AppCompatActivity {
    private static final String TAG ="Manual Request" ;
    private EditText edtdate,edtFromTime,edtToTime,txtcomment;
    DatePickerDialog picker;
    TimePickerDialog Timepicker;
    private static String FromDate="",IN_Time="",Out_Time="";
    private static ConstraintLayout cLayout;
    Button btnsend;
    private static int Count=0,Employee_Id=0;
    private static String BadgeNo="",url="";
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;
    UrlConnection urlconnection;
    private static String ErrorValue ="",PunchType="";
    CheckBox InTime, OutTime;
    private static String NewDate="",Comments="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mprequest);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        cLayout= findViewById(R.id.cLayout);
        edtdate=(EditText) findViewById(R.id.edtFromDate);
        urlconnection = new UrlConnection(getApplicationContext());
        edtFromTime=(EditText) findViewById(R.id.editFromTime);
        edtToTime=(EditText) findViewById(R.id.editToTime);
        btnsend=(Button) findViewById(R.id.btnSend);
        InTime=(CheckBox)findViewById(R.id.chkFromTime);
        OutTime=(CheckBox)findViewById(R.id.chkToTime);
        txtcomment=(EditText) findViewById(R.id.txtcomment);
        edtdate.setFocusable(false);
        edtFromTime.setFocusable(false);
        edtToTime.setFocusable(false);
        edtFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                Timepicker = new TimePickerDialog(MPRequest.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                IN_Time=((sHour)<10?("0"+(sHour)):(sHour)) + "." + ((sMinute<10)? ("0"+(sMinute)):sMinute);
                                edtFromTime.setText(((sHour)<10?("0"+(sHour)):(sHour)) + ":" + ((sMinute<10)? ("0"+(sMinute)):sMinute));
                            }
                        }, hour, minutes, false);

                Timepicker.show();

            }
        });




                edtToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                Timepicker = new TimePickerDialog(MPRequest.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                Out_Time=((sHour)<10?("0"+(sHour)):(sHour)) + "." + ((sMinute<10)? ("0"+(sMinute)):sMinute);
                                edtToTime.setText(((sHour)<10?("0"+(sHour)):(sHour)) + ":" + ((sMinute<10)? ("0"+(sMinute)):sMinute));
                            }
                        }, hour, minutes, false);
                Timepicker.show();
            }
        });


        // Set a click listener for CoordinatorLayout
        cLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input method manager
                InputMethodManager inputMethodManager = (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });


        edtdate.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MPRequest.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtdate.setText(formatDate(year,monthOfYear,dayOfMonth));
                               // edtdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                FromDate=year +"."+ ((monthOfYear + 1)<10?("0"+(monthOfYear + 1)):(monthOfYear + 1)) + "." + ((dayOfMonth<10)? ("0"+(dayOfMonth)):dayOfMonth);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
            }
        });



        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    tvw.setText("Selected Date: "+ eText.getText());
                if( ReadSystemValue() == 1) {
                    if (Count > 0) {
                        if(!(InTime.isChecked() || OutTime.isChecked()) ) {

                            Toast.makeText(MPRequest.this, getResources().getString(R.string.Please_check_IN_or_OUT_Checkbox


                            ),Toast.LENGTH_LONG).show();
                            return ;
                        }
                        ErrorValue= edtdate.getText().toString();
                        if (ErrorValue == null || ErrorValue.isEmpty() || ErrorValue.equals("null")) {
                            Toast.makeText(MPRequest.this, getResources().getString(R.string.Please_enter_Date),Toast.LENGTH_LONG).show();
                            return ;
                        }
                        if(InTime.isChecked())
                        {
                            PunchType="1";
                        ErrorValue= edtFromTime.getText().toString();
                        if (ErrorValue == null || ErrorValue.isEmpty() || ErrorValue.equals("null")) {
                            Toast.makeText(MPRequest.this, getResources().getString(R.string.Please_enter_IN_Time), Toast.LENGTH_LONG).show();
                            return;
                        }
                        }
                        if(OutTime.isChecked()) {
                            PunchType="0";
                            ErrorValue = edtToTime.getText().toString();
                            if (ErrorValue == null || ErrorValue.isEmpty() || ErrorValue.equals("null")) {
                                Toast.makeText(MPRequest.this, getResources().getString(R.string.Please_enter_OUT_Time), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        if(InTime.isChecked() & OutTime.isChecked())
                        {
                            PunchType="2";
                        }
                       // Make sure user insert date into edittext in this format.
                        Comments=txtcomment.getText().toString();
                        MPRequest.AsyncTaskRunner runner = new MPRequest.AsyncTaskRunner();
                        runner.execute(FromDate, IN_Time, Out_Time);
                    }
                    else
                    {
                        Toast.makeText(MPRequest.this, getResources().getString(R.string.Badge_No_not_found),Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(MPRequest.this, getResources().getString(R.string.Local_Database_Error),Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });
    }

    private static String formatDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.YEAR, year);
        Date myDate = cal.getTime();

        String date=new SimpleDateFormat("dd-MMM-yyyy").format(myDate);


        return date;
    }


    public void Clear()
    {
        edtdate.setText("");
        edtFromTime.setText("");
        edtToTime.setText("");
        txtcomment.setText("");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    public Integer ReadSystemValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode,Employee_Id,BadgeNo,url  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {
                    Count++;

                    Employee_Id= Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    BadgeNo = cursor.getString(cursor.getColumnIndex("BadgeNo"));
                    url  =   cursor.getString(cursor.getColumnIndex("url"));

                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }





    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping...");
            String FromDate = params[0];
            String Formtime=params[1];// Calls onProgressUpdate()
            String ToTime=params[2];
            try {
                resp=   SaveTasks(FromDate,Formtime,ToTime);


            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
                    if(resp.equals("Updated_Successfully"))
                        Toast.makeText(getBaseContext(), getResources().getString(R.string.Updated_Successfully), Toast.LENGTH_SHORT).show();
                    else  if (resp.contains("user_not_authorized"))
                    {
                      Toast.makeText(getBaseContext(),getResources().getString(R.string.user_not_authorized_for_manual_punch), Toast.LENGTH_SHORT).show();
                    }
                    else
                   {
                      Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                   }

            Clear();
             progressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
             progressDialog = ProgressDialog.show(MPRequest.this, getResources().getString(R.string.Loading),
                     getResources().getString(R.string.Please_Wait));

             progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog


        }
    }


    public String SaveTasks(String Date,String FromTime,String ToTime ) {

        String s = "",rest="",json="",EMPLOYEE_SERVICE_URI1,lang="";
        try {
            if (!(urlconnection.checkConnection())) {
                    rest= getResources().getString(R.string.Internet_Connection_not_found);//Internet Connection not found";
                    return rest;
                }
            lang= Locale.getDefault().getDisplayLanguage();
            if(!lang.equals("English"))
            {
                Date =urlconnection.arabicToDecimal(Date);
                FromTime=urlconnection.arabicToDecimal(FromTime);
                ToTime=urlconnection.arabicToDecimal(ToTime);
            }

                s = "";
                s = url+"/ElguardianService/Service1.svc/" + "/ManualPunch" + "/" + BadgeNo+"/"+PunchType+"/"+Date+"/"+FromTime+"/"+ToTime+"/'"+Comments+"'";
                EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
                  json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);
            if (json.contains("`") || json.contains("^"))
            {
                String getstring = json;
                rest=getstring;
                return rest;
            }
                    int lnth = json.length();
                     rest = json.substring(1, lnth - 1);
            } catch (Exception e) {
                e.printStackTrace();
                rest=getResources().getString(R.string.Error_Connection_with_Server);
                return rest;
            }


        return rest;
    }


}
