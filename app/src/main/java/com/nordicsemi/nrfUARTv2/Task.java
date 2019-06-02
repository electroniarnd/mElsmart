package com.nordicsemi.nrfUARTv2;

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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.nordicsemi.nrfUARTv2.MainActivity.TAG;

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
    String[] colors = {
            "Initiated",
            "Assigned",
            "Started",
            "Progress",
            "Suspended",
            "Resumed",
            "Transfered",
            "Finished"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OtprationName.put( 41,"Initiated");
       OtprationName.put( 42,"Assigned");
        OtprationName.put( 43,"Started");
        OtprationName.put( 44,"Progress");
        OtprationName.put( 45,"Suspended");
        OtprationName.put( 46,"Resumed");
        OtprationName.put( 47,"Transfered");
        OtprationName.put( 48,"Finished");
        setContentView(R.layout.activity_task);
        start=(Button) findViewById(R.id.btnstart);
        spinner = (Spinner) findViewById(R.id.spinner);
        txttasks=(TextView) findViewById(R.id.txttasks);
        txtstartdate=(TextView) findViewById(R.id.txtstartdate);
        txtenddate=(TextView) findViewById(R.id.txtenddate);
        txtplan=(TextView) findViewById(R.id.txtplan);
        editfedback=(EditText) findViewById(R.id.editfedback);
        txtcomment=(EditText) findViewById(R.id.txtcomment);


        if(GetServiceURL()==1) {
            Toast.makeText(this, "Error in reading local Database", Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(this, "Registration not found", Toast.LENGTH_LONG).show();
                return;
            }
        }


         requestpermission();
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);


        Intent intent = getIntent();
        TaskId = intent.getStringExtra("TaskID");
        TaskValue = TaskId.split(",");
        displayData(TaskValue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empid=TaskValue[1];
                Bundle sendBundle = new Bundle();
                sendBundle.putString("TaskID",TaskId);
                Intent intent = new Intent(Task.this, Task_Details.class);
                intent.putExtras(sendBundle);;
                startActivity(intent);
                finish();
            }
        });

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
        Date date1 = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        if(taskid.length>1) {
            try {
                db = controllerdb.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM  Tasks where Task_Id= " + taskid[0] + " and Employee_Id=" + taskid[1], null);

                if (cursor.moveToFirst()) {
                    do {
                        txttasks.setText(cursor.getString(cursor.getColumnIndex("Task_Name")));
                        txtstartdate.setText(cursor.getString(cursor.getColumnIndex("Date_Expected_Start")).replaceAll("[^' ':/\\w\\[\\]]", ""));
                        txtenddate.setText(cursor.getString(cursor.getColumnIndex("Date_Expected_End")).replaceAll("[^' ':/\\w\\[\\]]", ""));
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
                String empid=TaskValue[1];
                Bundle sendBundle = new Bundle();
                sendBundle.putString("EmpID",empid);
                Intent intent = new Intent(Task.this, Projects.class);
                intent.putExtras(sendBundle);;
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
       // alert.getWindow().setLayout(600, 370);


    }





    public boolean SaveTasks() {

        String Comments, Feedback,s="";
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
            if (!(checkConnection())) {
                PAlertDialog("Error","No Internet Connection Found.Saved Data locally");
                WriteLogData( Integer.valueOf(TaskValue[1]), formattedDate,Comments,Feedback,Integer.valueOf(TaskValue[0]),0);
                return false;
            }

            s ="";

            s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/TaskOperation" + "/'" + Comments+"'" +"/'"+formattedDate+"'/'"+ Feedback +"'/"+TaskValue[0]+"/"+TaskValue[1]+"/"+lat+"/"+longt+"/"+keyvalue;



           // EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            URL url = new URL(s);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            InputStream input = new BufferedInputStream(url.openStream());


            int b=-1;
            while ((b = input.read()) != -1)
                buffer.write(b);
            input.close();

            String json = new String(buffer.toString());
            Log.d( getResources().getString(R.string.download), getResources().getString(R.string.Lenght_of_file) + json.length());

            if (json.contains("`")) {
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
                    PAlertDialog1("Task Logs",  "Save Successfully");

                }
                else
                    Toast.makeText(getBaseContext(), "Error:Log did not save Offline", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            WriteLogData( Integer.valueOf(TaskValue[1]), formattedDate,Comments,Feedback,Integer.valueOf(TaskValue[0]),0);
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public String sendTasksToDB(int Task_Id,int Employee_Id,int Operation_No,String Comments,String Customer_Feedback,String Datetime,String Longitude,String Latitude) {


       String s="",Error="";


     //   Date dt =new Date();
      //  SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd kk.mm.ss");
      //  String formattedDate = df.format(Datetime);
        try {
            if(counter>1) {
                Thread.sleep(1000);
                counter++;
            }
            if (!(checkConnection())) {
                Error="No Internet Connection Found";
                return Error;
            }
            s ="";
            s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/TaskOperation" + "/'" + Comments+"'" +"/'"+Datetime+"'/'"+ Customer_Feedback +"'/"+Task_Id+"/"+Employee_Id+"/"+Latitude+"/"+Longitude+"/"+Operation_No;
            EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            URL url = new URL(EMPLOYEE_SERVICE_URI1);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            InputStream input = new BufferedInputStream(url.openStream());


            int b=-1;
            while ((b = input.read()) != -1)
                buffer.write(b);
            input.close();

            String json = new String(buffer.toString());
            Log.d( getResources().getString(R.string.download), getResources().getString(R.string.Lenght_of_file) + json.length());

            if (json.contains("`")) {
                String getstring = json;
                int iend = getstring.indexOf("`");

                if (iend != -1)
                    getstring = json.substring(iend, json.length()); //this will give abc
                Error=getstring;
                return Error;

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
            Toast.makeText(Task.this, " Network Connection Error Data is saving  in the local database",
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

        String empid=TaskValue[1];
        Bundle sendBundle = new Bundle();
        sendBundle.putString("TaskID",TaskId);
        Intent intent = new Intent(Task.this, Task_Details.class);
        intent.putExtras(sendBundle);;
        startActivity(intent);
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
                resp = e.getMessage();
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
                    "Sending Pending Task...",
                    "Please Wait");

            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

}