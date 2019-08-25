package com.electronia.mElsmart;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
public class TaskOnMap  extends AppCompatActivity implements OnMapReadyCallback{
    public static final String TAG = "TaskOnMap";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private GoogleMap map;
    private MapView myMapView;

    private Context mContext;
 //   ArrayList<  LatLng > locationArrayList = new ArrayList<>();
      ArrayList<latlong> taskmap = new ArrayList<latlong>();

    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    String[] TaskValue;
    String TaskId="";
    String TaskID="";
    String EmpID="";
    private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;
    UrlConnection urlconnection;

  //  private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_on_map);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        urlconnection = new UrlConnection(getApplicationContext());
        Intent intent = getIntent();
        TaskId = intent.getStringExtra("TaskID1");
        TaskValue = TaskId.split(",");
        TaskID=TaskValue[0];
        EmpID=TaskValue[1];
        mContext = this;

        TaskValue = TaskId.split(",");
        TaskID=TaskValue[0];
        EmpID=TaskValue[1];


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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, mapFragment).commit();
        }
        //  setupViewModel();
        //     checkLocationPermission();





    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        TaskOnMap.AsyncTaskRunner runner = new TaskOnMap.AsyncTaskRunner();
        runner.execute();


    }

    private String plotMarkers() {
 String res="",Update="";
 int count=0;
        if(map != null) {
            ArrayList<LatLng> pnts = new ArrayList<LatLng>();
            LatLng Value=null;
            if (taskmap.size() > 0) {
                for (latlong person : taskmap) {

                     Value = person.getLatlng();
                    Update= person.getUpdate();
                    if (count == 0) {
                        map.addMarker(new MarkerOptions().position(Value).title(Update).icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                        map.moveCamera(CameraUpdateFactory.newLatLng(Value));
                        map.animateCamera(zoom);
                        pnts.add(Value);
                        count++;
                    } else {
                        pnts.add(Value);
                        map.addMarker(new MarkerOptions().position(Value).title(Update));
                    }

                }
                map.addPolyline(new PolylineOptions().
                        addAll(pnts)
                        .width(8)
                        .color(Color.BLUE)
                        .zIndex(30));
            }
            else
            {
                res=   getResources().getString(R.string.No_Record);
            }
        }
        return res;
    }




    public String WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        Integer result=0;
        String rest="Success";
      //  locationArrayList.clear();
        taskmap.clear();
          if(json1.length()>0) {
        String[] urldata = json1.split(";");
        try {


            for (int i = 0; i < urldata.length; i++) {
                String[] urlValue = urldata[i].split("~");
                if (urlValue.length > 0) {
                    if( (urlValue[1] != null && !urlValue[1].isEmpty() && !urlValue[1].equals("null") && !urlValue[1].equals("0")) && (urlValue[0] != null && !urlValue[0].isEmpty() && !urlValue[0].equals("null")  && !urlValue[0].equals("0")))
                    {
                        taskmap.add(new latlong( urlValue[2],new LatLng(Double.valueOf(urlValue[1]), Double.valueOf(urlValue[0]))));
                    }

                }
            }

        } catch (Exception ex) {
            rest=ex.getMessage();
            Log.d(TAG, ex.getMessage());
        }
         }
         else
          {
              rest= getResources().getString(R.string.No_Record);//"Success"
          }
        return rest;
    }





    public String SaveTasks() {

        String s = "", lang = "", lang1 = "",rest="",json="";
        int ret = 0, res = 0;
        if (EmpID != null && !EmpID.isEmpty() && !EmpID.equals("null"))
        {
            try {

                if (!(checkConnection())) {
                 //   PAlertDialog(getResources().getString(R.string.Error), "Internet Connection not found");
                    rest="Internet Connection not found";
                    return rest;
                }

                s = "";
                s = ServiceURL+"/ElguardianService/Service1.svc/" + "/TaskHistory/"+TaskID;
                EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
                json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);
                if (json.contains("`") || json.contains("^"))
                {
                    return ErrorValue(json);
                }  else {

                    int lnth = json.length();
                    String json1 = json.substring(1, lnth - 1);
                    rest=  WriteMACAddress(json1);
                }
            } catch (Exception e) {
                rest=e.getMessage();
              //  Toast.makeText(getBaseContext(), "Error:Connection with Server", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

                return rest;
            }


        }
        else {
            rest=getResources().getString(R.string.Error_in_reading_local_database);
            //  Toast.makeText(getBaseContext(), "Error:Employee ID did not read Successfully", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(TaskOnMap.this, getResources().getString(R.string.Network_Connection_Error), Toast.LENGTH_SHORT).show();
            Log.e("", e.getMessage());
        }
        return false;
    }


    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskOnMap.this);
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
             resp=   SaveTasks();
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
            res= plotMarkers();
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    String res="";
                    res=  plotMarkers();
                    if(!res.equals(""))
                        Toast.makeText(getBaseContext(),res, Toast.LENGTH_SHORT).show();
                }
            });
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(TaskOnMap.this,
                    "Loading...",
                    "Please Wait");

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

