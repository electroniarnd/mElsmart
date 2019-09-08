package com.electronia.mElsmart;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Task  extends Activity {
    Controllerdb controllerdb = new Controllerdb(this);
    HashMap<Integer,String> OtprationName= new HashMap<Integer,String>();
    SQLiteDatabase db;
    Spinner spinner;
    Button start;
    double lat;
    double longt;
    String Selected="";
    int counter=0,EmpID=0;
   // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
   private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;
    private static String EMPLOYEE_SERVICE_URI1 = "";
    public static final String TAG = "ElSmart";
    String[] TaskValue;
    FusedLocationProviderClient mFusedLocationClient;
    private TextView txttasks,txtstartdate,txtenddate,txtplan,editfedback,txtcomment;
    List<String> list = new ArrayList<String>();
    String TaskId="";
    UrlConnection urlconnection;
    String[] colors=new String[8];;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtprationName.put( 41,getResources().getString(R.string.Initiated));
       OtprationName.put( 42,getResources().getString(R.string.Assigned));
        OtprationName.put( 43,getResources().getString(R.string.Started));
        OtprationName.put( 44,getResources().getString(R.string.Progress));
        OtprationName.put( 45,getResources().getString(R.string.Suspended));
        OtprationName.put( 46,getResources().getString(R.string.Resumed));
        OtprationName.put( 47,getResources().getString(R.string.Transfered));
        OtprationName.put( 48,getResources().getString(R.string.Finished));
        setContentView(R.layout.activity_task);
        start=(Button) findViewById(R.id.btnstart);
        spinner = (Spinner) findViewById(R.id.spinner);
        txttasks=(TextView) findViewById(R.id.txttasks);
        txtstartdate=(TextView) findViewById(R.id.txtstartdate);
        txtenddate=(TextView) findViewById(R.id.txtenddate);
        txtplan=(TextView) findViewById(R.id.txtplan);
        editfedback=(EditText) findViewById(R.id.editfedback);
        txtcomment=(EditText) findViewById(R.id.txtcomment);
        urlconnection = new UrlConnection(getApplicationContext());

                colors[0]= getResources().getString(R.string.Initiated);
                colors[1]= getResources().getString(R.string.Assigned);
                colors[2]= getResources().getString(R.string.Started);
                colors[3]= getResources().getString(R.string.Progress);
                colors[4]= getResources().getString(R.string.Suspended);
                colors[5]= getResources().getString(R.string.Resumed);
                colors[6]=  getResources().getString(R.string.Transfered);
                colors[7]= getResources().getString(R.string.Finished);

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


         requestpermission();
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);


        Intent intent = getIntent();
        TaskId = intent.getStringExtra("TaskID1");
        TaskValue = TaskId.split(",");
        displayData(TaskValue);



        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.getItemAtPosition(position);
                Selected=list.get(position);
              //  ((TextView) parent.getChildAt(position)).setTextSize(14);
              //  Toast.makeText(getApplicationContext(), "You have selected " + list.get(position), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat=0;
                longt=0;
                if(ActivityCompat.checkSelfPermission(Task.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(Task.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                       if(location != null)
                       {
                            lat=location.getLatitude();
                            longt=location.getLongitude();
                           SaveTasks();
                       }
                       else
                       {
                           SaveTasks();
                       }
                    }


                }) .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SaveTasks();
                    }
                });




            }
        });

        Task.AsyncTaskRunner runner = new Task.AsyncTaskRunner();
        runner.execute();
}



    private Integer displayData(String[] taskid) {
       int  res=0;
       Integer Status=0;
        String Value="";
        String Fromdate="";
        Date date1 = null;
      //  SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        if(taskid.length>1) {
            try {
                db = controllerdb.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM  Tasks where Task_Id= " + taskid[0] , null);
                SimpleDateFormat format ;// = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
                String formattedDate = "";//df.format(dt.getTime());



                SimpleDateFormat formatOld = new SimpleDateFormat( "dd/MM/yyyy hh:mm:ss");
                Date newDate;// = formatOld.parse(formattedDate);

                format = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");



               // String date = format.format(Date.parse("Your date string"));
                if (cursor.moveToFirst()) {
                    do {
                        txttasks.setText(cursor.getString(cursor.getColumnIndex("Task_Name")));
                         formattedDate =cursor.getString(cursor.getColumnIndex("Date_Expected_Start")).replaceAll("[^' ':/\\w\\[\\]]", "");
                        newDate = formatOld.parse(formattedDate);
                        Fromdate= format.format(newDate);
                        txtstartdate.setText(Fromdate);
                        formattedDate =cursor.getString(cursor.getColumnIndex("Date_Expected_End")).replaceAll("[^' ':/\\w\\[\\]]", "");
                        newDate = formatOld.parse(formattedDate);
                        Fromdate= format.format(newDate);
                        txtenddate.setText(Fromdate);
                        txtplan.setText(cursor.getString(cursor.getColumnIndex("Plan1")));
                        Status = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Status1")));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                res = 1;
                Value = OtprationName.get(Status);
                RetrieveWeather(Value);
            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
                Toast.makeText(this,getResources().getString(R.string.Error),Toast.LENGTH_LONG).show();
            }
        }
        return res;
    }

    public void RetrieveWeather(String a)
    {
        String weather="";
        if(!a.equals(null))
         weather = a;
        list.add(weather);
        for(String s:colors){
            if(!list.contains(s)){
                list.add(s);
            }
        }
        Selected=list.get(0);
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.spinner, list);
        spinner.setAdapter(adapter);
    }
    public  void requestpermission()
    {
   ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }






    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Task.this);
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



    private void PAlertDialog1(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Task.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //////String empid=TaskValue[1];
                ////////Bundle sendBundle = new Bundle();
               ////////// sendBundle.putString("EmpID",empid);
               //////////// Intent intent = new Intent(Task.this, Projects.class);
               ////////// intent.putExtras(sendBundle);;
               ///////////// startActivity(intent);
               ///////////// finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
       // alert.getWindow().setLayout(600, 370);


    }





    public boolean SaveTasks() {

        String Comments, Feedback,s="",lang="",json="";
        int  empid=3;
        Comments=txtcomment.getText().toString();
        Feedback=editfedback.getText().toString();
        int keyvalue=  getKey(OtprationName, Selected);
        Date dt =new Date();
       SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd kk.mm.ss");
        String formattedDate = df.format(dt.getTime());


        try {
            if(counter>1) {
                Thread.sleep(1000);
                counter++;
            }

            lang= Locale.getDefault().getDisplayLanguage();
            if(!lang.equals("English"))
            {
                formattedDate =urlconnection.arabicToDecimal(formattedDate);

            }
            if (!(urlconnection.checkConnection())) {
                PAlertDialog(getResources().getString(R.string.Error),getResources().getString(R.string.Data_Saved_locally));
                WriteLogData( Integer.valueOf(TaskValue[1]), formattedDate,Comments,Feedback,Integer.valueOf(TaskValue[0]),0);
                return false;
            }

            s ="";

            s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/TaskOperation" + "/'" + Comments+"'" +"/'"+formattedDate+"'/'"+ Feedback +"'/"+TaskValue[0]+"/"+TaskValue[1]+"/"+lat+"/"+longt+"/"+keyvalue;



           // EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            json = urlconnection.ServerConnection(s);
            if (json.contains("`") || json.contains("^"))
            {
                String getstring = json;
                int iend = getstring.indexOf("`");

                if (iend != -1)
                    getstring = json.substring(iend, json.length()); //this will give abc
                PAlertDialog( getResources().getString(R.string.Error), getstring);
                WriteLogData( Integer.valueOf(TaskValue[1]), formattedDate,Comments,Feedback,Integer.valueOf(TaskValue[0]),0);
                return false;
            }

            else
            {
                int ret=  WriteLogData( Integer.valueOf(TaskValue[1]), formattedDate,Comments,Feedback,Integer.valueOf(TaskValue[0]),1);
                if(ret==1) {
                    PAlertDialog1( getResources().getString(R.string.Logs),  getResources().getString(R.string.Save_Successfully));

                }
                else
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.Log_did_not_save_offline), Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            WriteLogData( Integer.valueOf(TaskValue[1]), formattedDate,Comments,Feedback,Integer.valueOf(TaskValue[0]),0);
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public String sendTasksToDB(int Task_Id,int Employee_Id,int Operation_No,String Comments,String Customer_Feedback,String Datetime,String Longitude,String Latitude) {


       String s="",Error="",lang="",json="";


     //   Date dt =new Date();
      //  SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd kk.mm.ss");
      //  String formattedDate = df.format(Datetime);
        try {
            if(counter>1) {
                Thread.sleep(1000);
                counter++;
            }
            if (!(urlconnection.checkConnection())) {
                Error=getResources().getString(R.string.No_Internet_Connection_Found);
                return Error;
            }
            lang= Locale.getDefault().getDisplayLanguage();
            if(!lang.equals("English"))
            {
                Datetime =urlconnection.arabicToDecimal(Datetime);

            }
            s ="";
            s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/TaskOperation" + "/'" + Comments+"'" +"/'"+Datetime+"'/'"+ Customer_Feedback +"'/"+Task_Id+"/"+Employee_Id+"/"+Latitude+"/"+Longitude+"/"+Operation_No;
            EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);
            if (json.contains("`") || json.contains("^"))
            {
                return ErrorValue(json);
            }

            else
            {
                int ret= UpdateLogData(Task_Id);
                  //  PAlertDialog1("Task Logs",  "send Successfully");
            }
        } catch (Exception e) {
            PAlertDialog( getResources().getString(R.string.Error), e.getMessage());
            Error= e.getMessage();
            return Error;
        }
        Error="Success";
        return Error;

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
            Toast.makeText(Task.this, getResources().getString(R.string.Data_Saved_locally),
                    Toast.LENGTH_SHORT).show();
            Log.e("", e.getMessage());
        }
        return false;
    }



    public Integer WriteLogData(int  Emipid,String dt,String comments,String Feedback,int Taskid,int sent)//////CHANGE INTO COMMON FUNCTION LATTER
    {


        int res =0;

      int keyvalue=  getKey(OtprationName, Selected);
        try {
            db=controllerdb.getWritableDatabase();
            db.execSQL("INSERT INTO Tasks_Operation(Datetime,Comments,Customer_Feedback,Longitude,Latitude,Task_Id,Employee_Id,Operation_No,send)VALUES('"+dt+"','"+comments+"','"+Feedback+"',"+longt+","+lat+","+Taskid+","+Emipid+","+keyvalue+","+sent+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }



    @Override
    public void onBackPressed() {


        finish();

    }
    public String ReadSavedTask()//////CHANGE INTO COMMON FUNCTION LATTER
    {

        int res = 0,Task_Id=0,Employee_Id=0,Operation_No=0;
        String Comments,Customer_Feedback,Datetime,Longitude,Latitude,error="";
        try {
            db = controllerdb.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM  Tasks_Operation where send=0 LIMIT 5", null);
            if (cursor.moveToFirst()) {
                do {
                    Task_Id =   Integer.valueOf(cursor.getString(cursor.getColumnIndex("Task_Id")));
                    Employee_Id= Integer.valueOf( cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    Operation_No= Integer.valueOf( cursor.getString(cursor.getColumnIndex("Operation_No")));
                    Comments =   String.valueOf(cursor.getString(cursor.getColumnIndex("Comments")));
                    Customer_Feedback= String.valueOf( cursor.getString(cursor.getColumnIndex("Customer_Feedback")));
                    Datetime= String.valueOf( cursor.getString(cursor.getColumnIndex("Datetime")));
                    Longitude =   String.valueOf(cursor.getString(cursor.getColumnIndex("Longitude")));
                    Latitude= String.valueOf( cursor.getString(cursor.getColumnIndex("Latitude")));
                  error= sendTasksToDB(Task_Id,Employee_Id,Operation_No,Comments,Customer_Feedback,Datetime,Longitude,Latitude);
                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 1;
        } catch (Exception ex) {
            error = "Error";
            Log.d(TAG, ex.getMessage());
            return error;
        }

        return "Success";
    }



    public Integer UpdateLogData(int Task_Id)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            db=controllerdb.getWritableDatabase();
            db.execSQL("update  Tasks_Operation SET send=1 WHERE Task_Id="+Task_Id);
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }
    public Integer GetServiceURL()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue="";
        int res =1;
        try {
            db = controllerdb.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM  Registration", null);
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                resp=   ReadSavedTask();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(!resp.equals("Success"))
                            Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage() ;
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            String  res="";

            progressDialog.dismiss();

        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Task.this,
                    getResources().getString(R.string.Sending_Pending_Task),
                    getResources().getString(R.string.Please_Wait));

            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

}