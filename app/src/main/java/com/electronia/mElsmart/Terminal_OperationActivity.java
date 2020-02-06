package com.electronia.mElsmart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.electronia.mElsmart.Common.UrlConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Terminal_OperationActivity extends AppCompatActivity {
    Controllerdb controllerdb = new Controllerdb(this);
    SQLiteDatabase db;
    public static final String TAG = "Terminal_Operation";

    private ArrayList<String> TerminalID = new ArrayList<String>();
    private ArrayList<String> Site_No = new ArrayList<String>();
    private ArrayList<String> TerNo = new ArrayList<String>();
    private ArrayList<String> Terminal_Name = new ArrayList<String>();
    private ArrayList<String> IPAddress = new ArrayList<String>();
    CustomAdopterTerOpr ca;
    ListView lv;
    private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;
    UrlConnection urlconnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal__operation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        urlconnection = new UrlConnection(getApplicationContext());
        lv = (ListView) findViewById(R.id.lstvw);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header, lv, false);
        // lv.addHeaderView(headerView);
        lv.setSmoothScrollbarEnabled(true);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if(GetServiceURL()==1) {
            Toast.makeText(this, getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(this, getResources().getString(R.string.Registration_does_not_exist), Toast.LENGTH_LONG).show();
                return;
            }
        }


        Terminal_OperationActivity.AsyncTaskRunner runner = new Terminal_OperationActivity.AsyncTaskRunner();
        runner.execute();
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

    }
public String GetTerminalList() {

        String s = "", lang = "", lang1 = "",rest="",json="",EMPLOYEE_SERVICE_URI1="";
        int ret = 0, res = 0;

        try {
        if (!(urlconnection.checkConnection())) {
        rest=getResources().getString(R.string.No_Internet_Connection_Found);
        return rest;
        }
            lang = Locale.getDefault().getDisplayLanguage();
            if (lang.equals("English")) {
                lang1 = "en";

            } else {
                lang1 = "ar";

            }
        s = "";
        s = ServiceURL+"/ElguardianService/Service1.svc/getterminals/"+lang1;
        EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
        json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);
        if (json.contains("`") || json.contains("^"))
        {
        return ErrorValue(json);
        }
        else {

        int lnth = json.length();
        String json1 = json.substring(1, lnth - 1);
        rest=   WriteMACAddress(json1);

        }
        } catch (Exception e) {
        //   Toast.makeText(getBaseContext(), "Error:Connection with Server", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
        rest=getResources().getString(R.string.Error_in_reading_local_database);
        return rest;
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


private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Terminal_OperationActivity.this);
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
public String WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
        {
            String rest="";
            TerminalID.clear();
            Site_No.clear();
            TerNo.clear();
            Terminal_Name.clear();
            IPAddress.clear();


            //code to set adapter to populate list
        if(json1.length()>0) {
        String[] urldata = json1.split(";");
        try {
        for (int i = 0; i < urldata.length; i++) {
        String[] urlValue = urldata[i].split("~");
        if (urlValue.length > 0) {
            TerminalID.add( urlValue[0]);
            Site_No.add( urlValue[1]);
            TerNo.add( urlValue[2]);
            Terminal_Name.add( urlValue[3]);
            IPAddress.add( urlValue[4]);
        }
        }

             ca = new CustomAdopterTerOpr(Terminal_OperationActivity.this,TerminalID, Site_No,TerNo,Terminal_Name,IPAddress);

        } catch (Exception ex) {
        rest=ex.getMessage();
        Log.d(TAG, ex.getMessage());
        }
        }
        return rest;
        }




@Override
public void onBackPressed() {

        finish();

        }






private class AsyncTaskRunner extends AsyncTask<String, String, String> {

    private String resp;
    ProgressDialog progressDialog;

    @Override
    protected String doInBackground(String... params) {
        publishProgress("Sleeping..."); // Calls onProgressUpdate()
        try {
            resp=  GetTerminalList();
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
        lv.setAdapter(ca);
        progressDialog.dismiss();
    }


    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(Terminal_OperationActivity.this,
                "Loading...",
                "Please Wait");
        // progressDialog.getWindow().setLayout(200,50);
        progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
    }


    @Override
    protected void onProgressUpdate(String... text) {
        ///////////////////// finalResult.setText(text[0]);
        // // Things to be done while execution of long running operation is in
        // progress. For example updating ProgessDialog


    }
}


    public Integer GetServiceURL()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue="";
        int res =1;
        try {
            Controllerdb controllerdb = new Controllerdb(this);
            SQLiteDatabase db;
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

}