package com.electronia.mElsmart;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.electronia.mElsmart.models.DataModel;
import com.electronia.mElsmart.models.CustomAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationHistory extends AppCompatActivity

    {
        ListView messages_list;
        ArrayList<DataModel> dataModels;
        ListView listView;
        private static CustomAdapter adapter;
        Controllerdb db =new Controllerdb(this);
        SQLiteDatabase database;
        String EmpID="";
        public static final String TAG = "MapTracking";
      //  private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
        private static String EMPLOYEE_SERVICE_URI1 = "";
        private static String ServiceURL="";
        private static int Registration=0;
        private EditText edtfrom,edtTo;
        private Button btnGetLocation;//=(Button)findViewById(R.id.button1);
        DatePickerDialog picker;
        private static String FromDate="",ToDate="";
        UrlConnection urlconnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        edtfrom=(EditText) findViewById(R.id.edtFromDate);
        edtTo=(EditText) findViewById(R.id.edtToDate);
        edtfrom.setInputType(InputType.TYPE_NULL);
        edtTo.setInputType(InputType.TYPE_NULL);
        listView=(ListView)findViewById(R.id.list);
        btnGetLocation=   (Button)findViewById(R.id.btnGetLocation);
        Intent intent = getIntent();
        EmpID =  intent.getStringExtra("EmpID");
        urlconnection = new UrlConnection(getApplicationContext());


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataModel dataModel= dataModels.get(position);
                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
        if(GetServiceURL()==1) {
            Toast.makeText(this, getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(this,  getResources().getString(R.string.Registration_does_not_exist), Toast.LENGTH_LONG).show();
                return;
            }
        }


       // try {
       ////     Thread.sleep(2000);
      //  } catch (InterruptedException e) {
      //      e.printStackTrace();
      //  }
       // LocationHistory.AsyncTaskRunner runner1 = new LocationHistory.AsyncTaskRunner();
      //  runner1.execute("1","1","1");
      //  SaveTasks();


        edtfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(LocationHistory.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtfrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                FromDate=year +"."+ ((monthOfYear + 1)<10?("0"+(monthOfYear + 1)):(monthOfYear + 1)) + "." + ((dayOfMonth<10)? ("0"+(dayOfMonth)):dayOfMonth);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
            }
        });


        edtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(LocationHistory.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                ToDate= year +"."+((monthOfYear + 1)<10?("0"+(monthOfYear + 1)):(monthOfYear + 1))  + "." + ((dayOfMonth<10)? ("0"+(dayOfMonth)):dayOfMonth);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
            }
        });


        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    tvw.setText("Selected Date: "+ eText.getText());
                LocationHistory.AsyncTaskRunner runner = new LocationHistory.AsyncTaskRunner();
                runner.execute("1",FromDate,ToDate);
            }
        });


    }




        public String SaveTasks(String datavalue,String FrmDate,String Todate ) {

            String s = "", lang = "", lang1 = "",rest="",json="";
            int ret = 0, res = 0;
            if (EmpID != null && !EmpID.isEmpty() && !EmpID.equals("null"))
            {
                try {

                    if (!(urlconnection.checkConnection())) {
                        rest=getResources().getString(R.string.Internet_Connection_not_found);
                       // PAlertDialog(getResources().getString(R.string.Error), "Internet Connection not found");
                        return rest;
                    }
                    if (FrmDate == null || FrmDate.isEmpty() || FrmDate.equals("null")) {
                        return getResources().getString(R.string.Please_enter_from_date);
                    }
                    if (Todate == null || Todate.isEmpty() || Todate.equals("null")) {
                        return getResources().getString(R.string.Please_Select_To_Date);
                    }
                    lang= Locale.getDefault().getDisplayLanguage();
                    if(!lang.equals("English"))
                    {
                        FrmDate =urlconnection.arabicToDecimal(FrmDate);
                        Todate=urlconnection.arabicToDecimal(Todate);
                    }
                    s = "";
                    s = ServiceURL+"/ElguardianService/Service1.svc/" + "/LiveMap/" + "/" + EmpID+"/"+datavalue+"/"+FrmDate+"/"+Todate;
                   String EMPLOYEE_SERVICE_URI = s.replace(' ','-');
                    json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
                    if (json.contains("`") || json.contains("^"))
                    {
                        return ErrorValue(json);
                    }  else {

                        int lnth = json.length();
                        String json1 = json.substring(1, lnth - 1);
                        rest=  WriteMACAddress(json1);
                    }
                } catch (Exception e) {
                  //  ReadLogData();
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.Connection_with_Server), Toast.LENGTH_SHORT).show();

                    rest=getResources().getString(R.string.Connection_with_Server);
                    return rest;
                }
            }
            else {
                rest=getResources().getString(R.string.Error_in_reading_local_database);
               // Toast.makeText(getBaseContext(), "Error:Employee ID did not read Successfully", Toast.LENGTH_SHORT).show();
            }
            return rest;
        }
        public String ErrorValue(String json)
        {
            String getstring="";
            if (json.contains("`")) {
                getstring = json;
                int iend = getstring.indexOf("`");
                if (iend != -1)
                    getstring = json.substring(1, json.length()-1); //this will give abc

            }
            if (json.contains("^")) {
                if(json.contains("Server Offline") )
                {
                    getstring=getResources().getString(R.string.Server_Offline);
                }
                if(json.contains("^within_Time_Out"))
                {
                    getstring=getResources().getString(R.string.Please_wait);
                }
                else
                    getstring=json.substring(1,json.length());



            }
            return getstring;
        }


        public boolean checkConnection() {

            try {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

                if (activeNetworkInfo != null) { // connected to the internet
                    //Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

                    if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        return true;
                    } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        // connected to the mobile provider's data plan
                        return true;
                    }
                }
            } catch (Exception e) {
                Toast.makeText(LocationHistory.this, getResources().getString(R.string.Local_Database_Error),
                        Toast.LENGTH_SHORT).show();
                Log.e("", e.getMessage());
            }
            return false;
        }


        private void PAlertDialog(String title, String msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(LocationHistory.this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }






        public Integer  ReadLogData()
        {
            Integer res=0;
            //  locationArrayList.clear();
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT Employee_Id,Longitude,Latitude FROM  LiveTracking where Employee_Id="+EmpID+ " order by Project_Id ASC", null);

                if (cursor.moveToFirst()) {
                    do {
                        //    locationArrayList.add(new LatLng( Double.valueOf(cursor.getColumnIndex("Latitude")),Double.valueOf(cursor.getColumnIndex("Longitude"))));
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

        public String WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
        {
            Integer result=0;
            int res = 0;
            String Place="",rest="Success";
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date date1 = null;
            dataModels= new ArrayList<>();
            //  result=Deletedata();

             if(json1.length()>0) {

            String[] urldata = json1.split(";");
            try {
                for (int i = 0; i < urldata.length; i++) {
                    String[] urlValue = urldata[i].split("~");

                  //  date1 = format.parse(urlValue[2].replaceAll("[^' ':/\\w\\[\\]]", ""));
                    String ss = urlValue[2].replaceAll("[^' ':/\\w\\[\\]]", "");
                    date1 = format.parse(ss);
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                    String requiredDate = df.format(date1).toString();
                    if (urlValue.length > 0) {
                        Place=  geo(Double.valueOf(urlValue[1]),Double.valueOf(urlValue[0]));

                        dataModels.add(new DataModel(Place,requiredDate));
                        res = 1;
                    }
                }
              //  adapter.clear();
                adapter= new CustomAdapter(dataModels,getApplicationContext());

            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
                rest=     "Error"  + ex.getMessage();

            }
             }
             else
             {
               //  Toast.makeText(LocationHistory.this, "Data is not found",
                       //  Toast.LENGTH_SHORT).show();
                 rest="Data  not found";
             }
            return rest;
        }


        protected String geo(  double  latitude,double longitude) {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String errorMessage = "";
            List<Address> addresses = null;
            String addressvalue="";
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException ioException) {
                errorMessage =getResources().getString(R.string.Service_Not_Available);
                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = getResources().getString(R.string.Invalid_Latitude_or_Longitude_Used);
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latitude + ", Longitude = " +
                        longitude, illegalArgumentException);
            }
            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "--";
                    Log.e(TAG, errorMessage);
                    return  addressvalue;
                }

            } //else {
            ///   for(Address address : addresses) {
            // String outputAddress = "";
            // for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            //     outputAddress += " --- " + address.getAddressLine(i);
            // }
            // Log.e(TAG, outputAddress);
            //  }
            Address address = addresses.get(0);
             addressvalue=  address.getAddressLine(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            //     addressFragments.add(address.getAddressLine(i));
            //}
            Log.i(TAG, "Address Found");

            //}

            return addressvalue;
        }



        private class AsyncTaskRunner extends AsyncTask<String, String, String> {

            private String resp;
            ProgressDialog progressDialog;

            @Override
            protected String doInBackground(String... params) {
                publishProgress("Sleeping...");
                String datavalue = params[0];
                String FormDate=params[1];// Calls onProgressUpdate()
                String ToDate=params[2];
                try {
                    resp=   SaveTasks(datavalue,FormDate,ToDate);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(!resp.equals("Success"))
                                Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    resp = e.getMessage();
                }
                return resp;
            }


            @Override
            protected void onPostExecute(String result) {
                listView.setAdapter(adapter);
               // progressDialog.dismiss();
            }


            @Override
            protected void onPreExecute() {
               // progressDialog = ProgressDialog.show(LocationHistory.this,
                  //      "Loading...",
                    //    "Please Wait");
               // progressDialog.getWindow().setLayout(200,50);
             //   progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
            }


            @Override
            protected void onProgressUpdate(String... text) {
              // finalResult.setText(text[0]);
                // // Things to be done while execution of long running operation is in
                // progress. For example updating ProgessDialog


            }
        }

        public Integer GetServiceURL()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            String MacValue="";
            int res =1;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
                if (cursor.moveToFirst()) {
                    do {

                        ServiceURL=cursor.getString(cursor.getColumnIndex("url"));
                        Registration++;
                    } while (cursor.moveToNext());
                }
                cursor.close();
                res =0;
            }
            catch (Exception ex) {
                res=-1;
                Log.d(TAG, ex.getMessage());
            }
            return res;
        }


    }
