package com.electronia.mElsmart;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import com.electronia.mElsmart.Common.UrlConnection;
import com.electronia.mElsmart.CustomEditText.TimeEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class LeaveRequest extends AppCompatActivity {
    private static final String TAG ="Leave Request" ;
    private EditText edtFromDate,edtToDate,txtcomment;
    DatePickerDialog picker;
    public TimeEditText edtTime;
    TimePickerDialog Timepicker;
    private static String FromDate="",ToDate="";
    private static ConstraintLayout cLayout;
    InputFilter timeFilter;
    Button btnSend;
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;
    private static int Count=0,Employee_Id=0,LeaveDescCount=0,OneDay=0;
    private static String BadgeNo="",url="";
    private static String ErrorValue ="",LeaveType="0",Hourly="0";
    private static String NewDate="",Comments="";
    Spinner spnLeaveDesv;
    CheckBox HourTime;
    UrlConnection urlconnection;
    HashMap<Integer,String> LeaveDesc= new HashMap<Integer,String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        cLayout= findViewById(R.id.cLayoutLR);
        urlconnection = new UrlConnection(getApplicationContext());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        edtFromDate=(EditText) findViewById(R.id.edtFromDate);
        edtToDate=(EditText) findViewById(R.id.edtToDate);
        txtcomment=(EditText) findViewById(R.id.txtcomment);
        btnSend=(Button) findViewById(R.id.btnSend);
        HourTime=(CheckBox) findViewById(R.id.chkHrs);
        spnLeaveDesv=(Spinner) findViewById(R.id.spLeaveType);
        edtTime=(TimeEditText) findViewById(R.id.edtTime);
        edtFromDate.setFocusable(false);
        edtToDate.setFocusable(false);
        LeaveDescCount=0;
        if(setData() !=1)
        {

            Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Error_in_Leave_Description_database),Toast.LENGTH_LONG).show();
            return;
        }

        spnLeaveDesv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                LeaveCode leave = (LeaveCode) parent.getSelectedItem();
                LeaveType=leave.getId();

             //   Toast.makeText(LeaveRequest.this, "Country ID: "+leave.getId()+",  Country Name : "+leave.getName(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String time_format="";
                    Hourly="0";
                    OneDay=0;
                    Count=0;
                    if (ReadSystemValue() == 1) {
                        if (Count > 0) {
                            ErrorValue = edtFromDate.getText().toString();
                            if (ErrorValue == null || ErrorValue.isEmpty() || ErrorValue.equals("null")) {
                                Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Please_enter_from_date), Toast.LENGTH_LONG).show();
                                return;
                            }


                            if (LeaveDescCount == 0) {
                                Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Error_in_Leave_Description_database), Toast.LENGTH_LONG).show();
                                return;
                            }


                            ErrorValue = edtToDate.getText().toString();
                            if (ErrorValue == null || ErrorValue.isEmpty() || ErrorValue.equals("null")) {
                                Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Please_enter_To_date), Toast.LENGTH_LONG).show();
                                return;
                            }




                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                            Date dtFromDate = sdf.parse(edtFromDate.getText().toString());
                            Date stToDate = sdf.parse(edtToDate.getText().toString());


                            if (dtFromDate.after(stToDate)) {
                                Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Please_enter_valid_Date), Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (HourTime.isChecked()) {
                                ErrorValue = edtTime.getText().toString();
                                if (ErrorValue.equals("00:00") ) {
                                    Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Please_enter_valid_Time), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Hourly = "1";


                                 if(dtFromDate.after(stToDate) || dtFromDate.before(stToDate) )
                                {
                                    Toast.makeText(LeaveRequest.this, getResources().getString(R.string.From_Date_and_To_Date_shouldbe_same), Toast.LENGTH_LONG).show();
                                    return;
                                }

                            }

                             if(dtFromDate.after(stToDate) || dtFromDate.before(stToDate) )
                            {
                                OneDay=0;
                            }
                            else
                                OneDay=1;

                            // Make sure user insert date into edittext in this format.
                            Comments = txtcomment.getText().toString();

                        } else {
                            Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Badge_No_not_found), Toast.LENGTH_LONG).show();
                            return;
                        }
                    } else {
                        Toast.makeText(LeaveRequest.this, getResources().getString(R.string.Local_Database_Error), Toast.LENGTH_LONG).show();
                        return;
                    }
                    time_format=edtTime.getText().toString();
                    time_format=time_format.replace(":",".");
                       LeaveRequest.AsyncTaskRunner runner = new LeaveRequest.AsyncTaskRunner();
                    runner.execute(FromDate, ToDate,  time_format);


                }
                catch(Exception ex)
                {
                    Toast.makeText(LeaveRequest.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
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


        timeFilter  = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                boolean allowEdit = true;
                if (source != null) {
                    if (source.length() == 0) {
                        return null;// deleting, keep original editing
                    }
                    String result = "";
                    result += dest.toString().substring(0, dstart);
                    result += source.toString().substring(start, end);
                    result += dest.toString().substring(dend, dest.length());
                    if (result != null) {
                        if (result.length() > 5) {
                            return "";// do not allow this edit
                        }

                        char c;
                        if (result.length() > 0) {
                            c = result.charAt(0);
                            allowEdit &= (c >= '0' && c <= '2');
                        }
                        if (result.length() > 1) {
                            c = result.charAt(1);
                            if (result.charAt(0) == '0' || result.charAt(0) == '1')
                                allowEdit &= (c >= '0' && c <= '9');
                            else
                                allowEdit &= (c >= '0' && c <= '3');
                        }
                        if (result.length() > 2) {
                            c = result.charAt(2);
                            allowEdit &= (c == ':');
                        }
                        if (result.length() > 3) {
                            c = result.charAt(3);
                            allowEdit &= (c >= '0' && c <= '5');
                        }
                        if (result.length() > 4) {
                            c = result.charAt(4);
                            allowEdit &= (c >= '0' && c <= '9');
                        }
                        return allowEdit ? null : "";
                    }
                }
                return allowEdit ? null : "";
            }
        };
        //edtTime=(EditText) findViewById(R.id.edtTime);
       // edtTime.setFilters(new InputFilter[]{timeFilter});


        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                final String Date_Time="";
                // date picker dialog
                picker = new DatePickerDialog(LeaveRequest.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                              // edtFromDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                                edtFromDate.setText(formatDate(year,monthOfYear,dayOfMonth));
                                FromDate=year +"."+ ((monthOfYear + 1)<10?("0"+(monthOfYear + 1)):(monthOfYear + 1)) + "." + ((dayOfMonth<10)? ("0"+(dayOfMonth)):dayOfMonth);
                            }
                        }, year, month, day);
              //  picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
            }
        });

        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(LeaveRequest.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                               // edtToDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                edtToDate.setText(formatDate(year,monthOfYear,dayOfMonth));
                                ToDate=year +"."+ ((monthOfYear + 1)<10?("0"+(monthOfYear + 1)):(monthOfYear + 1)) + "." + ((dayOfMonth<10)? ("0"+(dayOfMonth)):dayOfMonth);
                            }
                        }, year, month, day);
               // picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    public class LeaveCode {

        private String leave_code;
        private String leave_name;

        public LeaveCode(String id, String name) {
            this.leave_code = id;
            this.leave_name = name;
        }


        public String getId() {
            return leave_code;
        }

        public void setId(String id) {
            this.leave_code = id;
        }

        public String getName() {
            return leave_name;
        }

        public void setName(String name) {
            this.leave_name = name;
        }


        //to display object as a string in spinner
        @Override
        public String toString() {
            return leave_name;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof LeaveCode){
                LeaveCode c = (LeaveCode )obj;
                if(c.getName().equals(leave_name) && c.getId()==leave_code ) return true;
            }
            return false;
        }

    }


    private Integer setData() {

       String lang="";
        Cursor cursor;
        ArrayList<LeaveCode> Leave = new ArrayList<>();
        int res = 0;
        try {
            lang= Locale.getDefault().getDisplayLanguage();
            database = db.getReadableDatabase();
            if(lang.equals("English")) {
                cursor = database.rawQuery("SELECT LeaveDescID,Description  From   LeaveDesc", null);
            }
            else
            {
                cursor = database.rawQuery("SELECT LeaveDescID,Description_Ar AS Description From   LeaveDesc", null);
            }
            if (cursor.moveToFirst()) {
                do {
                    LeaveDescCount++;
                    Leave.add(new LeaveCode(cursor.getString(cursor.getColumnIndex("LeaveDescID")), cursor.getString(cursor.getColumnIndex("Description"))));
                } while (cursor.moveToNext());
            }
            cursor.close();
            ArrayAdapter<LeaveCode> adapter = new ArrayAdapter<LeaveCode>(this, R.layout.spinner, Leave);
            spnLeaveDesv.setAdapter(adapter);
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }



    public void Clear()
    {
        edtFromDate.setText("");
        edtToDate.setText("");
        edtTime.setHour(00);
        edtTime.setMinutes(00);
        txtcomment.setText("");
    }


    public String SaveLeave(String FromDate,String ToDate,String ToTime ) {

        String s = "",rest="",json="",EMPLOYEE_SERVICE_URI1,lang="";
        try {
            if (!(urlconnection.checkConnection())) {
                rest= getResources().getString(R.string.Internet_Connection_not_found);//Internet Connection not found";
                return rest;
            }

            lang= Locale.getDefault().getDisplayLanguage();
            if(!lang.equals("English"))
            {
                FromDate =urlconnection.arabicToDecimal(FromDate);
                ToDate=urlconnection.arabicToDecimal(ToDate);
                ToTime=urlconnection.arabicToDecimal(ToTime);
            }

            s = "";
            s = url+"/ElguardianService/Service1.svc" + "/Leave" + "/" + BadgeNo+"/"+LeaveType+"/"+FromDate+"/"+ToDate+"/"+ToTime+"/"+Hourly+"/'"+Comments+"'/"+OneDay;
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
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping...");
            String FromDate = params[0];
            String ToDate=params[1];// Calls onProgressUpdate()
            String Time=params[2];
            try {
                resp=   SaveLeave(FromDate,ToDate,Time);


            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {



            if(resp.equals("Updated_Successfully")) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.Updated_Successfully), Toast.LENGTH_SHORT).show();

            }
               else if (resp.contains("user_not_authorized"))
                {
                    Toast.makeText(getBaseContext(),getResources().getString(R.string.user_not_authorized_for_leave), Toast.LENGTH_SHORT).show();
                }
               else if (resp.contains("Leave_already_exist"))
                {
                Toast.makeText(getBaseContext(),getResources().getString(R.string.Leave_already_exist), Toast.LENGTH_SHORT).show();
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
            progressDialog = ProgressDialog.show(LeaveRequest.this, getResources().getString(R.string.Loading),
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

}
