package com.nordicsemi.nrfUARTv2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.nordicsemi.nrfUARTv2.MainActivity.TAG;

/**
 * Created by Pradeepn on 3/31/2019.
 */

public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static  int LOCATION_INTERVAL = 20000;
    public static  int LOCATION_Distance = 300;
    public static  int FASTEST_LOCATION_INTERVAL = 10000;
    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    @SuppressLint("RestrictedApi")
    LocationRequest mLocationRequest = new LocationRequest();
    LocationManager locationManager;
    Location location;
    boolean isGPSEnable = false;
    int counter=0;
    boolean isNetworkEnable = false;
    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;

  //  private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    private static Integer EmpID = 0;
    private boolean isGPS = false;
    private static String ServiceURL="";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ReadEmpID();

        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_INTERVAL);
      // mLocationRequest.setSmallestDisplacement(100);//setMinimumDisplacement(minimumDistanceBetweenUpdates);
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
       // LocationMonitoringService.AsyncTaskRunner runner = new LocationMonitoringService.AsyncTaskRunner();
      //  runner.execute();
  //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Toast.makeText(this, "Nothing is  is Enabled in your device for Location Update", Toast.LENGTH_SHORT).show();
        } else {

            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

            } else if (isGPSEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

            }

        }
    }


    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }


    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");


        if (location != null) {

            location.getBearing();
            Log.d(TAG, "== location != null");
            String msg = "" +
                    "" +
                    " Location: " +
                    Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude());
            String newlatitude=String.valueOf(location.getLatitude());
            String newlongitude=String.valueOf(location.getLongitude());
          Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            //Send result to activities
         //   SaveLatLong(location.getLatitude(),location.getLongitude());
          //  SendSavedData();
            int speed=(int) ((location.getSpeed()*3600)/1000);
            int Bearing=(int) ( location.getBearing());
            location.getBearing();
            Toast.makeText(this, "Bearing: "+Integer.toString(Bearing)+" Angle" , Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Speed: "+Integer.toString(speed)+"KM/HR" , Toast.LENGTH_SHORT).show();
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            SaveLatLong(location.getLatitude(),location.getLongitude(),speed,Bearing);
        }

    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }



    @Override
    public void onDestroy() {
        Log.e(TAG, "Service is Destroying...");
        super.onDestroy();
        if (mLocationClient.isConnected()) {
            stopLocationUpdates();

        }

    }

    protected void stopLocationUpdates() {
        Log.d(TAG, "Location update stoping...");
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient, this);
    }




    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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








    public boolean SaveLatLong(Double Lat,Double Long,int Speed,int heading) {

        String Comments, Feedback,s="";
      //  ReadEmpID();
        Date dt =new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd kk.mm.ss");
        String formattedDate = df.format(dt.getTime());


        try {

            if (!(checkConnection())) {
                WriteLogData( EmpID, formattedDate,Lat,Long,0,Speed,heading);
                return false;
            }

            s ="";

            s = ServiceURL+"/ElguardianService/Service1.svc/" + "/LiveValue" + "/" + EmpID +"/"+Lat+"/"+ Long +"/'"+formattedDate+"'/"+Speed+"/"+heading;



            EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            URL url = new URL(EMPLOYEE_SERVICE_URI1);
            URLConnection conexion = url.openConnection();
            conexion.setConnectTimeout(3 * 1000);
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
                WriteLogData( EmpID, formattedDate,Lat,Long,0,Speed,heading);
                return false;
            }

        } catch (Exception e) {
            WriteLogData( EmpID, formattedDate,Lat,Long,0,Speed,heading);
            //fillData(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE");
            //////////////////////////// popup(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE");
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
            //  PAlertDialog("ERROR", e.getMessage());
            return false;
        }
        return true;

    }






    public boolean sendsavedlog(Double Lat,Double Long,String datetime,int Id,int Empid,int Speed,int heading) {


         String s="";


        try {
            if(counter>1) {
                Thread.sleep(1000);
                counter++;
            }

            if (!(checkConnection())) {

                return false;
            }

            s ="";
            s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/LiveValue" + "/" + Empid +"/"+Lat+"/"+ Long +"/'"+datetime+"'/"+Speed+"/"+heading;
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
               // PAlertDialog( getResources().getString(R.string.Error), getstring);
               // WriteLogData( EmpID, formattedDate,Lat,Long,0);
                return false;
            }
            else
            {

                UpdateLogData(Id);


            }
        } catch (Exception e) {
            e.printStackTrace();
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
            Toast.makeText(this, " Network Connection Error Data is saving  in the local database",
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

    public Integer WriteLogData(int  Emipid,String dt,Double Lat,Double Long,int sent,int speed,int bearing)//////CHANGE INTO COMMON FUNCTION LATTER
    {


        int res =0;


        try {
            database = db.getReadableDatabase();

            database.execSQL("INSERT INTO LiveTracking(Employee_Id,Datetime1,Longitude,Latitude,Sent,Speed,Heading)VALUES("+Emipid+",'"+dt+"',"+Long+","+Lat+","+sent+","+speed+","+bearing+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }



    public Integer ReadEmpID()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,MinDist,Interval,url FROM  Registration", null);
            if (cursor.moveToFirst()) {
                do {
                    EmpID=Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    LOCATION_INTERVAL=Integer.valueOf(cursor.getString(cursor.getColumnIndex("Interval")));
                    LOCATION_Distance=Integer.valueOf(cursor.getString(cursor.getColumnIndex("MinDist")));
                    ServiceURL=cursor.getString(cursor.getColumnIndex("url"));
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


    public Integer SendSavedData()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0,count=0,Employee_Id,ID,Speed,Heading;
        Double Longitude=0.0,Latitude=0.0;
        String Datetime1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,Longitude,Latitude,Datetime1,ID,Speed,Heading FROM  LiveTracking where sent=0", null);
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






    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
              SendSavedData();

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            String  res="";



        }


        @Override
        protected void onPreExecute() {



        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

}
