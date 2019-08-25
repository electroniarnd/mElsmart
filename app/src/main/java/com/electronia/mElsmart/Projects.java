package com.electronia.mElsmart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.electronia.mElsmart.Common.UrlConnection;
import com.electronia.mElsmart.adapters.RecyclerViewDataAdapter;
import com.electronia.mElsmart.models.SectionDataModel;
import com.electronia.mElsmart.models.SingleItemModel;

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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.electronia.mElsmart.MainActivity.TAG;


public class Projects extends Fragment {
    private Context mContext;
  //  private static String EMPLOYEE_SERVICE_URI1 = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI2 = "",EmpID="";
    private TextView mTextViewInfo;
    private TextView mTextViewPercentage,txtproject,txtdate;
    private ProgressBar mProgressBar;
    Controllerdb db;// = new Controllerdb(this);
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
    private static View view;
    UrlConnection urlconnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_projects, container, false);
        return view;
    }
    public static Projects newInstance() {
        Projects fragment = new Projects();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new Controllerdb(getContext());
        urlconnection = new UrlConnection(getActivity().getApplicationContext());


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String s = "";
        allSampleData = new ArrayList<SectionDataModel>();
        my_recycler_view = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        my_recycler_view.setHasFixedSize(true);
        adapter = new RecyclerViewDataAdapter(getActivity(), allSampleData);
        my_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());


        if(GetServiceURL()==1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(getActivity(), getResources().getString(R.string.Registration_does_not_exist), Toast.LENGTH_LONG).show();
                return;
            }
        }
        createDummyData();
        my_recycler_view.setAdapter(adapter);
        Projects.AsyncTaskRunner runner = new Projects.AsyncTaskRunner();
        runner.execute();


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
            Cursor cursor = database.rawQuery("SELECT Project_Id,Project_Name,Task_No,Task_Name,Task_Id,OpenTask,FinishedTask FROM  Tasks  order by Project_Id ASC", null);
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
                        if(Integer.valueOf(TaskID) >0)
                            singleItem.add(new SingleItemModel(cursor.getString(cursor.getColumnIndex("Task_Name")), TaskID+","+EmpID));
                        count++;
                        Tasks++;
                    } else {
                        count++;
                        Tasks++;
                        TaskID = cursor.getString(cursor.getColumnIndex("Task_Id"));
                       if(Integer.valueOf(TaskID) >0)
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        String msg = "", urlar = "",json="";
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
            s = ServiceURL+"/ElguardianService/Service1.svc/" + "/TaskList/" + "/" + EmpID + "/" + "en";
            EMPLOYEE_SERVICE_URI2 = s.replace(' ', '-');

            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI2);

            if (json.contains("~") || json.contains("^")) {
              String getstring = json;
              int iend = getstring.indexOf("`");

            if (iend != -1)
            getstring = json.substring(1, json.length()-1); //this will give abc
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
        Integer result=0,EmployeeId=0,Task_id=0;
        String Date_start="",Date_end="",Project_Date_start="",Project_Date_end="";


        String rest = "Success";
        result=Deletedata();
        if(result==1) {
            String[] urldata = json1.split(";");


            try {

                for (int i = 0; i < urldata.length; i++) {
                    String[] urlValue = urldata[i].split("`");
                    if (urlValue.length > 0) {
                        Date_start=urlValue[5].replaceAll("[^' ':/\\w\\[\\]]", "");
                        Date_end=urlValue[6].replaceAll("[^' ':/\\w\\[\\]]", "");
                        Project_Date_start=urlValue[14].replaceAll("[^' ':/\\w\\[\\]]", "");
                        Project_Date_end=urlValue[15].replaceAll("[^' ':/\\w\\[\\]]", "");
                        if(urlValue[1] != null && !urlValue[1].isEmpty())
                        {
                            EmployeeId=Integer.valueOf(urlValue[1]);
                        }
                        else
                        {
                            EmployeeId=0;
                        }
                        if(urlValue[13] != null && !urlValue[13].isEmpty())
                        {
                              Task_id=Integer.valueOf(urlValue[13]);
                        }
                        else
                        {
                            Task_id=0;
                        }
                        database = db.getWritableDatabase();
                        database.execSQL("INSERT INTO Tasks(Project_Id,Employee_Id,BadgeNo,FullName,Task_Name,Date_Expected_Start,Date_Expected_End,Status1,Descriptions,Plan1,Project_Name,Project_No,Task_No,Task_Id,Project_Date_Actual_Start,Project_Date_Actual_End)" +
                                "VALUES(" + urlValue[0] + "," + EmployeeId + ",'" + urlValue[2] + "','" + urlValue[3] + "','" + urlValue[4] + "','" + Date_start + "','" +Date_end + "','" + urlValue[7] + "','" + urlValue[8] + "','" + urlValue[9] + "','" + urlValue[10] + "','" + urlValue[11] + "','" + urlValue[12] + "'," + Task_id + ",'" +Project_Date_start+"','" + Project_Date_end+ "')");

                    }
                }
            } catch (Exception ex) {
                rest=ex.getMessage();
                Log.d(TAG, ex.getMessage());
            }
        }
        return rest;
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
            database.execSQL("Delete from Tasks");
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
                    if (urlconnection.checkConnection()) {

                        connectionurlSecData();
                        createDummyData();

                    } else {
                        createDummyData();
                        resp=" Network Connection Failed. Data is fetching  from local database";
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                if(!resp.equals("Success"))
                                    Toast.makeText(getActivity(),resp, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception ex) {
                    createDummyData();
                    Log.d(TAG, ex.getMessage());
                    resp=" Network Connection Failed. Data is fetching  from local database";
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if(!resp.equals("Success"))
                                Toast.makeText(getActivity(),resp, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if(!resp.equals("Success"))
                            Toast.makeText(getActivity(),resp, Toast.LENGTH_SHORT).show();
                    }
                });
            }




            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            my_recycler_view.setAdapter(adapter);
            progressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),
                    "Loading...",
                    "Please Wait");
            // progressDialog.getWindow().setLayout(200,50);
            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {
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
                    EmpID=cursor.getString(cursor.getColumnIndex("Employee_Id"));
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