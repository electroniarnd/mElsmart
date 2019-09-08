package com.electronia.mElsmart;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.electronia.mElsmart.Common.UrlConnection;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class LocationUpdateservice extends IntentService {
    private static final String TAG ="Location Update Service" ;
    int counter=0;
   // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    private static String ServiceURL="";
    UrlConnection urlconnection;
    public LocationUpdateservice() {

            super("LocationUpdateservice");


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        urlconnection = new UrlConnection(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        GetServiceURL();
        SendSavedData();
        stopSelf();
    }

    @Override
    public void onCreate() {
       //Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();
        super.onCreate();

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
       // Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }


    public Integer SendSavedData()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0,count=0,Employee_Id,ID,Speed,Heading;
        Double Longitude=0.0,Latitude=0.0;
        String Datetime1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,Longitude,Latitude,Datetime1,ID,Speed,Heading  FROM  LiveTracking where sent=0", null);
            if (cursor.moveToFirst()) {
                do {
                    Employee_Id=Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    Longitude=Double.valueOf(cursor.getString(cursor.getColumnIndex("Longitude")));
                    Latitude=Double.valueOf(cursor.getString(cursor.getColumnIndex("Latitude")));
                    Datetime1=cursor.getString(cursor.getColumnIndex("Datetime1"));
                    ID=Integer.valueOf(cursor.getString(cursor.getColumnIndex("ID")));
                    Speed=Integer.valueOf(cursor.getString(cursor.getColumnIndex("Speed")));
                    Heading=Integer.valueOf(cursor.getString(cursor.getColumnIndex("Heading")));
                    sendsavedlog(Latitude, Longitude, Datetime1, ID, Employee_Id,Speed,Heading);
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


    public boolean sendsavedlog(Double Lat,Double Long,String datetime,int Id,int Empid,int Speed,int heading) {
        String s="",json="", lang="";
        try {
            if(counter>1) {
                Thread.sleep(1000);
                counter++;
            }
           if (!urlconnection.checkConnection()) {

                return false;
            }
            lang= Locale.getDefault().getDisplayLanguage();
            if(!lang.equals("English")) {
                // EmpID=urlconnection.arabicToDecimal(EmpID);
                datetime= urlconnection.arabicToDecimal(datetime);
            }
            s ="";
            s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/LiveValue" + "/" + Empid +"/"+Lat+"/"+ Long +"/'"+datetime+"'/"+Speed+"/"+heading;
            EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);


            if (json.contains("`") || json.contains("^"))
            {
                String getstring = json;
                int iend = getstring.indexOf("`");

                if (iend != -1)
                    getstring = json.substring(iend, json.length());//this will give abc
                   Toast.makeText(this, getstring, Toast.LENGTH_LONG).show();
                return false;
            }
            else
            {
                UpdateLogData(Id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UpdateLogData(Id);
            Toast.makeText(this, getResources().getString(R.string.Error), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

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
            Toast.makeText(this, " Network Connection Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("", e.getMessage());
        }
        return false;
    }

    public Integer UpdateLogData(int ID)//////CHANGE INTO COMMON FUNCTION LATTER
    {


        int res =0;


        try {
            database = db.getReadableDatabase();

            database.execSQL("update LiveTracking set sent=1 where ID="+ID );
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
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    ServiceURL=cursor.getString(cursor.getColumnIndex("url"));
                    //  Registration++;
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
