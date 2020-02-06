package com.electronia.mElsmart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Terminal_Control extends AppCompatActivity {


    String[] TerminalValue;
    String Terminal="",SwSRelaySts1="0",SwSRelaySts2="0",SwSRelaySts3="0";
    String TerminalID="";
    String SiteNo="",TerNo="",IPAddress="";
    Switch SwRelay1, SwRelay2,SwRelay3;
    public static final String TAG = "Terminal_Control";
    private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;
    RadioGroup radRelay;
    private ArrayAdapter<String> listAdapter;
    UrlConnection urlconnection;
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    Button Exicute,btnRefresh;
    String[] CmdValue;
    private ListView messageListView;
    TextView txtTerStus,txtdttime,txtLogcount,txttername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal__control);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Exicute=(Button)findViewById(R.id.btnExecute);
        SwRelay1 = (Switch) findViewById(R.id.SwRelay1);
        SwRelay2 = (Switch) findViewById(R.id.SwRelay2);
        SwRelay3 = (Switch) findViewById(R.id.SwRelay3);
        radRelay = (RadioGroup)findViewById(R.id.radRelay);

        btnRefresh=(Button)findViewById(R.id.btnRefresh);
        txtTerStus=(TextView) findViewById(R.id.txtTerStus);
        txtdttime=(TextView) findViewById(R.id.txtdttime);
        txtLogcount=(TextView) findViewById(R.id.txtLgCnt);
        txttername=(TextView) findViewById(R.id.txttername);

     //   messageListView = (ListView) findViewById(R.id.lstCmdView);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_details);
    //    messageListView.setAdapter(listAdapter);
      //  messageListView.setDivider(null);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        urlconnection = new UrlConnection(getApplicationContext());
        Intent intent = getIntent();
        Terminal = intent.getStringExtra("Terminal");
        TerminalValue = Terminal.split(",");
        TerminalID=TerminalValue[0];
        SiteNo=TerminalValue[1];
        TerNo=TerminalValue[2];
        IPAddress=TerminalValue[3];

        if(SwRelay1.isChecked()){
            /////  AutoPunch=1; //edit here
            SwRelay1.setText(R.string.ON);
            SwSRelaySts1="1";
        }else{
            SwRelay1.setText(R.string.OFF);
            SwSRelaySts1="0";
        }

        if(SwRelay2.isChecked()){
            SwSRelaySts2="1";
            SwRelay2.setText(R.string.ON);
        }else{
            SwRelay2.setText(R.string.OFF);
            SwSRelaySts2="0";

        }


        if(SwRelay3.isChecked()){
            SwSRelaySts3="1";
            SwRelay3.setText(R.string.ON);
        }else{
            SwRelay3.setText(R.string.OFF);
            SwSRelaySts3="0";

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

        SwRelay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                                                           if(isChecked){
                                                                      /////  AutoPunch=1; //edit here
                                                               SwRelay1.setText(R.string.ON);
                                                               SwSRelaySts1="1";
                                                           }else{
                                                             SwRelay1.setText(R.string.OFF);
                                                               SwSRelaySts1="0";
                                                           }

                                                       }

                                                   }

        );

        SwRelay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    /////  AutoPunch=1; //edit here
                    SwRelay2.setText(R.string.ON);
                    SwSRelaySts2="1";
                }else{
                    SwRelay2.setText(R.string.OFF);
                    SwSRelaySts2="0";

                }


            }
        });


        SwRelay3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    /////  AutoPunch=1; //edit here
                    SwRelay3.setText(R.string.ON);
                    SwSRelaySts3="1";
                }else{
                    SwRelay3.setText(R.string.OFF);
                    SwSRelaySts3="0";

                }


            }
        });


        Exicute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTasks();
            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Terminal_Control.AsyncTaskRunner runner = new Terminal_Control.AsyncTaskRunner();
                runner.execute();
            }
        });


        Terminal_Control.AsyncTaskRunner runner = new Terminal_Control.AsyncTaskRunner();
        runner.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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







    public String SaveTasks() {

        String s = "", lang = "", lang1 = "",rest="",json="",EMPLOYEE_SERVICE_URI1="",message="",json1="";
        int ret = 0, res = 0;
        int checkedRadioButtonId=0;
        listAdapter.clear();
        try {

                if (!(urlconnection.checkConnection())) {
                    rest=getResources().getString(R.string.No_Internet_Connection_Found);
                    return rest;
                }
                 checkedRadioButtonId = radRelay.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.rdRelay1) {

                    s = ServiceURL+"/ElguardianService/Service1.svc/" + "/Command/"+IPAddress+"/R1/"+SwSRelaySts1+"/"+SiteNo+"/"+TerNo;
                }
                else if (checkedRadioButtonId == R.id.rdRelay2) {
                    s = ServiceURL+"/ElguardianService/Service1.svc/" + "/Command/"+IPAddress+"/R2/"+SwSRelaySts2+"/"+SiteNo+"/"+TerNo;

                    }


                else if (checkedRadioButtonId == R.id.rdRelay3) {
                    s = ServiceURL+"/ElguardianService/Service1.svc/" + "/Command/"+IPAddress+"/R3/"+SwSRelaySts3+"/"+SiteNo+"/"+TerNo;

                   }

                else if (checkedRadioButtonId == R.id.radReset) {
                    s = ServiceURL+"/ElguardianService/Service1.svc/" + "/Command/"+IPAddress+"/RST"+"/0"+"/"+SiteNo+"/"+TerNo;

                }



              //  s = ServiceURL+"/ElguardianService/Service1.svc/" + "/Command/172.18.202.61/"+"/RST"+"/0";
                EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
                json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);
                if (json.contains("`") || json.contains("^"))
                {
                    return ErrorValue(json);
                }
                else {

                    int lnth = json.length();
                     json1 = json.substring(1, lnth - 1);
                //////////    rest=    WriteMACAddress(json1);

                }

            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
            CmdValue = json1.split("~");
              if(CmdValue.length==3) {
                  listAdapter.add("["+currentDateTimeString+"]:"+CmdValue[0] );
                  listAdapter.add("["+currentDateTimeString+"]:"+CmdValue[1] );
                  listAdapter.add("["+currentDateTimeString+"]:"+CmdValue[2] );
              }
              else
                  listAdapter.add("["+currentDateTimeString+"]:"+json1 );



          //  messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);

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
            int iend = getstring.indexOf("Unable");
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

        AlertDialog.Builder builder = new AlertDialog.Builder(Terminal_Control.this);
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

                resp=   ReadTerminal();
                //runOnUiThread(new Runnable() {
                // public void run() {
                // if(resp.contains("`"))
                //  Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                // }
                //  });
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage() ;
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            if(!result.contains("Unable")) {
                String[] res = result.split("~");
                txtdttime.setText(res[0]);
                txtLogcount.setText(res[1].replace('\\',' '));
                txtTerStus.setText("Connected");
            }
            else
            {
                // Toast.makeText(getBaseContext(),result, Toast.LENGTH_SHORT).show();
                PAlertDialog1("Error",result);

            }
            progressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Terminal_Control.this,
                    getResources().getString(R.string.Getting_Terminal_Data),
                    getResources().getString(R.string.Please_Wait));
            txtTerStus.setText("Connecting..");

            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }

    }


    private void PAlertDialog1(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Terminal_Control.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }




    public String ReadTerminal() {

        String s = "", lang = "", lang1 = "",rest="",json="",EMPLOYEE_SERVICE_URI1="";
        int ret = 0, res = 0;

        try {
            if (!(urlconnection.checkConnection())) {
                rest=getResources().getString(R.string.No_Internet_Connection_Found);
                return rest;
            }
            lang = Locale.getDefault().getDisplayLanguage();

            s = "";
            s = ServiceURL+"/ElguardianService/Service1.svc/commandread/"+IPAddress+"/"+SiteNo+"/"+TerNo;;
            EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);
            if (json.contains("Unable") || json.contains("^"))
            {
                return ErrorValue(json);
            }
            else {

                int lnth = json.length();
                String json1 = json.substring(1, lnth - 1);
                rest=   json1;//(json1);

            }
        } catch (Exception e) {
            //   Toast.makeText(getBaseContext(), "Error:Connection with Server", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            rest=getResources().getString(R.string.Error_in_reading_local_database);
            return rest;
        }

        return rest;
    }






}
