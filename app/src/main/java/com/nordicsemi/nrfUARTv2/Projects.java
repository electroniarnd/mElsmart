package com.nordicsemi.nrfUARTv2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nordicsemi.nrfUARTv2.adapters.RecyclerViewDataAdapter;
import com.nordicsemi.nrfUARTv2.models.SectionDataModel;
import com.nordicsemi.nrfUARTv2.models.SingleItemModel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;


import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.nordicsemi.nrfUARTv2.MainActivity.TAG;


public class Projects extends AppCompatActivity {
    private Context mContext;
  //  private static String EMPLOYEE_SERVICE_URI1 = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI2 = "",EmpID="";
    private TextView mTextViewInfo;
    private TextView mTextViewPercentage,txtproject,txtdate;
    private ProgressBar mProgressBar;
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;
    ArrayList<SectionDataModel> allSampleData;
    private static int Projects=0;
    private static int Tasks=0;
    private static int OpenTasks=0;
    private static int Finishedasks=0;
    RecyclerView my_recycler_view;
    RecyclerViewDataAdapter adapter;
    ProgressBar progressBar;
    private int mProgressStatus = 0;
    private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        Intent intent = getIntent();
        EmpID =  intent.getStringExtra("EmpID");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String s = "";
        txtproject=(TextView) findViewById(R.id.txttasks);
     //   txttask=(TextView) findViewById(R.id.txttask);
        txtdate=(TextView) findViewById(R.id.txtdate);
        progressBar=(ProgressBar) findViewById(R.id.progress_login);
        allSampleData = new ArrayList<SectionDataModel>();
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        //  mTextViewInfo = (TextView) findViewById(R.id.tv_info);
        mTextViewPercentage = (TextView) findViewById(R.id.tv_percentage);
         my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);
        my_recycler_view.setHasFixedSize(true);
        adapter = new RecyclerViewDataAdapter(this, allSampleData);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        txtdate.setText(currentDateandTime);

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


        createDummyData();
        my_recycler_view.setAdapter(adapter);
        // execution of result of Long time consuming operation












        Projects.AsyncTaskRunner runner = new Projects.AsyncTaskRunner();


            runner.execute();



        //createDummyData();

    }



    public void createDummyData() {


        HashMap<Integer, String> Project = new HashMap<Integer, String>();
        Project.clear();
        allSampleData.clear();
        int res = 0;
        String TaskID="";
        int count = 0;
        Projects=0;
        Tasks=0;
        String ProjectName = "";
        ArrayList<SingleItemModel> singleItem = new ArrayList<SingleItemModel>();

        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Project_Id,Project_Name,Task_No,Task_Name,Task_Id,OpenTask,FinishedTask FROM  Tasks where Employee_Id="+EmpID+" order by Project_Id ASC", null);
            if (cursor.moveToFirst()) {
                do {
                    if (!Project.containsKey(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Project_Id"))))) {
                        if (count > 0) {
                            SectionDataModel dm = new SectionDataModel();
                            dm.setHeaderTitle(ProjectName);
                            dm.setAllItemsInSection(singleItem);
                            allSampleData.add(dm);
                            count = 0;
                            Projects++;
                        }
                        Project.put(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Project_Id"))),cursor.getString(cursor.getColumnIndex("Project_Name")));
                        //  singleItem.clear();
                        singleItem = new ArrayList<SingleItemModel>();
                        ProjectName = (cursor.getString(cursor.getColumnIndex("Project_Name")));
                        TaskID = cursor.getString(cursor.getColumnIndex("Task_Id"));
                        OpenTasks= Integer.valueOf( cursor.getString(cursor.getColumnIndex("OpenTask")));
                        Finishedasks= Integer.valueOf( cursor.getString(cursor.getColumnIndex("FinishedTask")));
                        singleItem.add(new SingleItemModel(cursor.getString(cursor.getColumnIndex("Task_Name")), TaskID+","+EmpID));
                        count++;
                        Tasks++;
                    } else {
                        count++;
                        Tasks++;
                        TaskID = cursor.getString(cursor.getColumnIndex("Task_Id"));
                        singleItem.add(new SingleItemModel(cursor.getString(cursor.getColumnIndex("Task_Name")), TaskID+","+EmpID));
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 1;
            if (count > 0) {
                SectionDataModel dm = new SectionDataModel();
                dm.setHeaderTitle(ProjectName);
                dm.setAllItemsInSection(singleItem);
                allSampleData.add(dm);
                Projects++;
                count = 0;
            }



        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
    }

    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Projects.this);
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


    public String connectionurlSecData() {

        String msg = "", urlar = "";
        String lang = "", sys = "", s = "", lang1 = "",rest="";

        String number = "";
        try {
            lang = Locale.getDefault().getDisplayLanguage();
            if (lang.equals("English")) {
                lang1 = "en";

            } else {
                lang1 = "ar";

            }

            s="";
            s = ServiceURL+"/ElguardianService/Service1.svc/" + "/TaskListEmp/" + "/" + EmpID + "/" + "en";
            EMPLOYEE_SERVICE_URI2 = s.replace(' ', '-');
            URL url = new URL(EMPLOYEE_SERVICE_URI2);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            Log.d(getResources().getString(R.string.download), getResources().getString(R.string.Lenght_of_file) + lenghtOfFile);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            InputStream input = new BufferedInputStream(url.openStream());
            byte data[] = new byte[lenghtOfFile];
            int count = -1;
            while ((count = input.read(data)) != -1) {
                buffer.write(data, 0, count);
            }
            input.close();
            String json = new String(buffer.toString());


            if (json.contains("~")) {
              String getstring = json;
         int iend = getstring.indexOf("`");

            if (iend != -1)
            getstring = json.substring(iend, json.length()); //this will give abc
                rest=getstring;

         return rest;
          }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
         rest=   WriteMACAddress(json1);


            // PAlertDialog(getResources().getString(R.string.Information), "Secondary Data added successfully" + "\n\n" + msg);

        } catch (Exception e) {
            e.printStackTrace();
           rest=e.getMessage();
            return rest;
        }
        return rest;

    }


    public String WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        Integer result=0;

        String rest = "Success";
        result=Deletedata();
        if(result==1) {
            String[] urldata = json1.split(";");


            try {

                for (int i = 0; i < urldata.length; i++) {
                    String[] urlValue = urldata[i].split("`");
                    if (urlValue.length > 0) {
                        database = db.getWritableDatabase();
                        database.execSQL("INSERT INTO Tasks(Project_Id,Employee_Id,BadgeNo,FullName,Task_Name,Date_Expected_Start,Date_Expected_End,Status1,Descriptions,Plan1,Project_Name,Project_No,Task_No,Task_Id,OpenTask,FinishedTask)" +
                                "VALUES(" + urlValue[0] + "," + urlValue[1] + ",'" + urlValue[2] + "','" + urlValue[3] + "','" + urlValue[4] + "','" + urlValue[5] + "','" + urlValue[6] + "','" + urlValue[7] + "','" + urlValue[8] + "','" + urlValue[9] + "','" + urlValue[10] + "','" + urlValue[11] + "','" + urlValue[12] + "'," + urlValue[13] + "," + urlValue[14]+"," + urlValue[15]+ ")");

                    }
                }
            } catch (Exception ex) {
                rest=ex.getMessage();
                Log.d(TAG, ex.getMessage());
            }
        }
        return rest;
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
            Toast.makeText(Projects.this, " Network Connection Error Data is fetching  from local database",
                    Toast.LENGTH_SHORT).show();
            Log.e("", e.getMessage());
        }
        return false;
    }


    public Integer ReadSysSetting()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  system_setting", null);
            if (cursor.moveToFirst()) {
                do {
                    //   VisibleMode =   Integer.valueOf(cursor.getString(cursor.getColumnIndex("Debug")));
                    //  AutoPunch= Integer.valueOf( cursor.getString(cursor.getColumnIndex("AutoPunch")));
                    //  Biometric= Integer.valueOf( cursor.getString(cursor.getColumnIndex("Biometric")));
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


    public Integer Deletedata()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Tasks where Employee_Id="+EmpID );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
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
                try {
                    if (checkConnection()) {

                        connectionurlSecData();
                        createDummyData();

                    } else {
                        createDummyData();
                        resp=" Network Connection Failed. Data is fetching  from local database";
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if(!resp.equals("Success"))
                                    Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception ex) {
                    createDummyData();
                    Log.d(TAG, ex.getMessage());
                    resp=" Network Connection Failed. Data is fetching  from local database";
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(!resp.equals("Success"))
                                Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(!resp.equals("Success"))
                            Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                    }
                });
            }




            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            my_recycler_view.setAdapter(adapter);
            // execution of result of Long time consuming operation




            txtproject.setText("OnGoing Projects/Tasks: "+Projects+"/"+Tasks);
           // txttask.setText("OnGoing Tasks: "+Tasks);
            int level = Finishedasks;
            int scale =Finishedasks+OpenTasks;//Tasks;
            float percentage = level / (float) scale;
            mProgressStatus = (int) ((percentage) * 100);
            mTextViewPercentage.setText("\u0020" + "\u0020" + "\u0020" + "\u0020" + "\u0020" + "\u0020" + "\u0020" + "\u0020" + "\u0020" + level + "/" + scale + "\nJob Completed");
            mProgressBar.setProgress(mProgressStatus);

            progressDialog.dismiss();
            /////////////// finalResult.setText(result);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Projects.this,
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