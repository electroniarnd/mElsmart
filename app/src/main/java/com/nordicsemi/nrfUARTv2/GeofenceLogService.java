package com.nordicsemi.nrfUARTv2;

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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class GeofenceLogService extends Service {
    private static final String TAG ="Location Update Service" ;
    int counter=0;
   // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    static String ServiceURL="";
    public GeofenceLogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if(ReadTrackingValue()==1)
            Toast.makeText(this,"Error in reading local Database",Toast.LENGTH_LONG).show();
        ReadLog();
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }

    public Integer ReadLog()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0,geoid=0,logid=0,punchtype=0;
        String BadgeNo="";
        String  dt="",Time="";//=new Date();
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  LogGeo where sent=0 ", null);
            if (cursor.moveToFirst()) {
                do {
                    logid =  Integer.valueOf( cursor.getString(cursor.getColumnIndex("ID")));
                    punchtype =  Integer.valueOf( cursor.getString(cursor.getColumnIndex("PunchType")));
                    geoid =  Integer.valueOf( cursor.getString(cursor.getColumnIndex("GeoID")));
                    dt = cursor.getString(cursor.getColumnIndex("date"));
                    Time=cursor.getString(cursor.getColumnIndex("time"));
                    BadgeNo=cursor.getString(cursor.getColumnIndex("BadgeNo"));
                    sendLog(BadgeNo,dt,Time,punchtype,geoid,logid);
                } while (cursor.moveToNext());
            }
            cursor.close();
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }



    public boolean sendLog(String BadgeNo,String dt,String time,int punchtype,int geoid,int logid) {

        String msg = "", s="", lang="",number="",urlar="";
        try {
            if (!checkConnection()) {
                Toast.makeText(this,"Internet not found",Toast.LENGTH_LONG).show();
                return false;
            }
            urlar="";
            urlar= ServiceURL+"/ElguardianService/Service1.svc/";;
            s = urlar + "/LogSave" + "/'" + BadgeNo+"'" +"/"+dt+"/"+ time +"/"+punchtype+"/"+geoid;

            String EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
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
                //PAlertDialog( getResources().getString(R.string.Error), getstring);
                Toast.makeText(this,getstring,Toast.LENGTH_LONG).show();
                return false;
            }
            else
            {
                UpdateLogData(logid);

            }

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "ERROR:"+e.getMessage(),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }




    public Integer UpdateLogData(int logid)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            database=db.getWritableDatabase();
            database.execSQL("update  LogGeo SET sent=1 WHERE ID="+logid);
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }

    public boolean checkConnection() {

        try {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
            if (activeNetworkInfo != null) { // connected to the internet
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }
        }
        catch(Exception e){
            Toast.makeText(this, " Network Connection Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }
        return false;
    }

    public Integer ReadTrackingValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue="";
        int res =1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    ServiceURL=cursor.getString(cursor.getColumnIndex("url"));

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
