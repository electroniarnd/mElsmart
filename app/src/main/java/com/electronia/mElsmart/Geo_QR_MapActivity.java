package com.electronia.mElsmart;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.electronia.mElsmart.Common.UrlConnection;
import com.electronia.mElsmart.Services.LogUpdateService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Geo_QR_MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener

    {
        private static final String TAG ="Geo_QR_MapActivity" ;
        private static final String TODO ="" ;
        GoogleMap map;
        SupportMapFragment mapFragment;

        //  LatLng portLatLong,portLatLong1,portLatLong2,portLatLong3;
        Spinner spdLocations;
        HashMap<LatLng, String> locations = new HashMap<>();
        String  building="",bestProvider,BadgeNo="";
        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        int shadeColor = 0x44ff0000, GeoID=0,count =0,counter=0;
        LocationManager locationManager;
        Circle circle, circle1;
        ProgressDialog dialog;
        private Location lastLocation;
        public static  Location location;
        public static  LatLng Geolocation;
        LatLng latLong;
        float dis=0;
        MediaPlayer in = null;
        MediaPlayer out = null;
        MediaPlayer Geo = null;
        MediaPlayer error = null;
        ArrayList<String> locationArray = new ArrayList<>();
        Controllerdb db =new Controllerdb(this);
        SQLiteDatabase database;
        TextView txtBadgeNo,txtPunchDate,txtPunchTime,txtLastPunch,txtLogStatus,txtGeoname;
        //private static String EMPLOYEE_SERVICE_URI1="http://212.12.167.242:6002/Service1.svc";
        private static String EMPLOYEE_SERVICE_URI1="";
        private String BadgeNoReg="";
        private static int Tracking = 0;
        private boolean isGPS = false;
        static int   countGeofence=0;
        private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
        private static int Registration=0;

        private Context mContext;
        private Activity mActivity;

        private RelativeLayout mRelativeLayout;
        private Button mButton;

        private PopupWindow mPopupWindow;

        public static  int BLE = 0;
        public static  int geofence =0; //Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
        public static  int QRCode=0;
        private static int startvalue = 0;
        private static int AutoPunch = 0;
        private static int Biometric = 0;
        private static final int  REQUEST_ENABLE_FT=3;
        UrlConnection urlconnection;

        boolean gps_enabled = false;
        boolean network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo__qr__map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (GetServiceURL() == 1) {
            Toast.makeText(this, "Error in reading local Database", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (Registration == 0) {
                Toast.makeText(this, "Registration not found", Toast.LENGTH_LONG).show();
                Intent Registration = new Intent(Geo_QR_MapActivity.this, SignUp.class);
                startActivity(Registration);
                return;
            }
        }
        if (Geofence() == 1) {
            if (countGeofence == 0) {
                Toast.makeText(this, "Goefence not found for this employee", Toast.LENGTH_LONG);

            }
        } else {
            Toast.makeText(this, "Error in reading local database", Toast.LENGTH_LONG);
        }
        if (ReadSysSetting() == 0) {//check System Setting
            Toast.makeText(this, "Error in Reading system_setting", Toast.LENGTH_LONG).show();//check_Setting();
            return;
        }
        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);
        Geo = MediaPlayer.create(this, R.raw.geo);
        urlconnection = new UrlConnection(getApplicationContext());

        // Geo_QR_MapActivity.AsyncTaskRunner runner = new Geo_QR_MapActivity.AsyncTaskRunner();
        //  runner.execute();


        ButterKnife.bind(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frame);
        dialog = new ProgressDialog(this);


        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input method manager
                InputMethodManager inputMethodManager = (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


        if (RaadGeoFence() != 1)
            Toast.makeText(this, "Problem in Reading Grofence", Toast.LENGTH_LONG).show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mapFragment.getMapAsync(this);
        mContext = getApplicationContext();
        mActivity = Geo_QR_MapActivity.this;

        // Get the widgets reference from XML layout


        if (urlconnection.checkConnection()) {

            if (!checkServiceRunningGeoLog()) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Intent startServiceIntent = new Intent(Geo_QR_MapActivity.this, LogUpdateService.class);
                        startService(startServiceIntent);
                    }
                };
                thread.start();
            }

            if (!checkServiceRunning()) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Intent startServiceIntent = new Intent(Geo_QR_MapActivity.this, LocationUpdateservice.class);
                        startService(startServiceIntent);
                    }
                };
                thread.start();
            }
        }
    }




        private void setupToolbar() {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Mark Attendance");
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Map_InitValue(googleMap);


            validateLocation(0);
        }




        private MarkerOptions generateMarker(LatLng latLng, String title) {
            TextView text = new TextView(getApplicationContext());
            text.setText(title);
            text.setTextColor(Color.parseColor("#ffffff"));
            text.setPadding(5, 5, 5, 5);
            return new MarkerOptions().position(latLng).title("Building");
        }






        void validateLocation(int StValue) {
            int lastpunch=-1;
            int punchtye= 0;
            try {
                if (count > 0) {
                    lastpunch = ReadLastPunch();
                    if (lastpunch == 0 || lastpunch == 1) {
                        if (lastpunch == 0) {

                            punchtye=1;
                        } else {
                            punchtye=0;
                        }

                        Geo_QR_MapActivity.AsyncTaskRunnerSecond runner1 = new Geo_QR_MapActivity.AsyncTaskRunnerSecond();
                        runner1.execute(BadgeNo,String.valueOf(punchtye) ,String.valueOf(GeoID) );


                    } else {
                        if (lastpunch == 2) {
                            Toast.makeText(this, "Last Punch Not found", Toast.LENGTH_SHORT).show();
                            Geo_QR_MapActivity.AsyncTaskRunnerSecond runner1 = new Geo_QR_MapActivity.AsyncTaskRunnerSecond();
                            runner1.execute(BadgeNo, "1", String.valueOf(GeoID));
                        } else
                            Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            catch(Exception ex)
            {
                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }


//

        @Override
        public void onLocationChanged(Location Currentlocations) {
            lastLocation=Currentlocations;
            location=Currentlocations;

            //  map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("It's You!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person)));

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }


        public Integer RaadGeoFence()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res =0;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM  Geofence", null);
                if (cursor.moveToFirst()) {
                    do {
                        locations.put(new LatLng(Float.valueOf(cursor.getString(cursor.getColumnIndex("Lat"))), Float.valueOf(cursor.getString(cursor.getColumnIndex("Long")))), String.valueOf(cursor.getString(cursor.getColumnIndex("KeyName")))+"~"+String.valueOf(cursor.getString(cursor.getColumnIndex("Radius")))+"~"+String.valueOf(cursor.getString(cursor.getColumnIndex("Badgeno")))+"~"+String.valueOf(cursor.getString(cursor.getColumnIndex("GeoID"))));
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



        @RequiresApi(api = Build.VERSION_CODES.M)
        public String SaveLog(String BadgeNo, int punchtype, int geoid) {

            String msg = "", s = "", lang = "", number = "", urlar = "", rest = "Success",json="";
            Date dt = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
            SimpleDateFormat df1 = new SimpleDateFormat("HH.mm.ss");
            SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss aa");
            SimpleDateFormat dfDate = new SimpleDateFormat(" dd MMM yyyy");

            String formattedDate = "";// = df.format(dt.getTime());
            String formattedDate1 = "";// = df1.format(dt.getTime());
            String formattedDate2 = "";// = time.format(dt.getTime());
            String formatDate = "";// = dfDate.format(dt.getTime());
            try {
                lang= Locale.getDefault().getDisplayLanguage();
                Criteria criteria = new Criteria();
                bestProvider = locationManager.getBestProvider(criteria, false);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return TODO;
                }
                location = locationManager.getLastKnownLocation(bestProvider);
                //  String time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(location.getTime());
                if (location.getProvider().equals(android.location.LocationManager.GPS_PROVIDER)) {
                    formattedDate = df.format(location.getTime());
                    formattedDate1 = df1.format(location.getTime());
                    formattedDate2 = time.format(location.getTime());
                    formatDate = dfDate.format(location.getTime());
                    Log.d("Location", "Time GPS: " + time); // This is what we want!
                }
                // if( location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
                //{
                else {

                    formattedDate = df.format(location.getTime());
                    formattedDate1 = df1.format(location.getTime());
                    formattedDate2 = time.format(location.getTime());
                    formatDate = dfDate.format(location.getTime());


                    if(!lang.equals("English"))
                    {
                        formatDate =urlconnection.arabicToDecimal(formatDate);
                        formattedDate2=urlconnection.arabicToDecimal(formattedDate2);
                    }

                    Log.d("Location", "Time Device (" + location.getProvider() + "): " + time);
                    // }
                }
                if (counter > 1) {
                    Thread.sleep(1000);
                    counter++;
                }
                if (!(urlconnection.checkConnection())) {
                    WriteLogData(BadgeNo, formatDate, formattedDate2,formattedDate,formattedDate1, punchtype, geoid, 0);
                    rest = "internet_Connection_not_found";
                    return rest + "~" + BadgeNo + "~" + formatDate + "~" + formattedDate2 + "~" + punchtype + "~" + building + "~OFFLINE";
                    //popup( BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE");

                }
                urlar = "";
                urlar = ServiceURL + "/ElguardianService/Service1.svc/";
                s = "";
                s = urlar + "/LogSaveQR" + "/'" + BadgeNo + "'" + "/" + formattedDate + "/" + formattedDate1 + "/" + punchtype + "/" + geoid+ "/" +0;
                String EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
                json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);


                if (json.contains("`") || json.contains("^"))
                {

                    String getstring = json;
                    int iend = getstring.indexOf("`");

                    if (iend != -1)
                        getstring = json.substring(iend, json.length()); //this will give abc
                    //  PAlertDialog( getResources().getString(R.string.Error), getstring);
                    WriteLogData(BadgeNo, formatDate, formattedDate2,formattedDate,formattedDate1, punchtype, geoid, 0);
                    rest = getstring + "~" + BadgeNo + "~" + formatDate + "~" + formattedDate2 + "~" + punchtype + "~" + building + "~OFFLINE";
                    ;
                    return rest;


                } else {
                    int ret = WriteLogData(BadgeNo, formatDate, formattedDate2,formattedDate,formattedDate1, punchtype, geoid, 1);
                    if (ret == 1) {

                        // popup(BadgeNo, formattedDate, formattedDate2, punchtype, building, "ONLINE");
                    } else
                        rest = "log_did_not_save_offline" + "~" + BadgeNo + "~" + formatDate + "~" + formattedDate2 + "~" + punchtype + "~" + building + "~ONLINE";// Toast.makeText(getBaseContext(), "Error:Log did not save Offline", Toast.LENGTH_SHORT).show();

                }
                //  int lnth = json.length();
                //  String json1 = json.substring(1, lnth - 1);
            } catch (Exception e) {
                WriteLogData(BadgeNo, formatDate, formattedDate2,formattedDate,formattedDate1, punchtype, geoid, 0);
                //fillData(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE");
                /// popup(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE");
                e.printStackTrace();
                rest = "Error in catch";
                return rest + "~" + BadgeNo + "~" + formatDate + "~" + formattedDate2 + "~" + punchtype + "~" + building + "~"+getResources().getString(R.string.OFFLINE);
            }
            return rest + "~" + BadgeNo + "~" + formatDate + "~" + formattedDate2 + "~" + punchtype + "~" + building + "~"+getResources().getString(R.string.ONLINE);

        }




        private class AsyncTaskRunnerSecond extends AsyncTask<String, String, String> {

            private String resp;
            ProgressDialog progressDialog;

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected String doInBackground(String... params) {
                publishProgress("Sleeping..."); // Calls onProgressUpdate()
                try {


                    resp=  SaveLog(params[0],Integer.valueOf(params[1]) ,Integer.valueOf(params[2]));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(!resp.contains("Success")) {
                                if(resp.contains("Internet Connection not found"))
                                    Toast.makeText(getBaseContext(), "Log Saved OffLine", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //startStep1();

                } catch (Exception e) {
                    e.printStackTrace();
                    resp = e.getMessage();
                }
                return resp;
            }


            @Override
            protected void onPostExecute(String result) {
                String  res="";
                /// String result=text[0];
                String[] resultvalue=result.split("~");
                if(resultvalue.length>0) {
                    if (resultvalue[0].equals("Success")) {
                        popup(resultvalue[1], resultvalue[2], resultvalue[3],Integer.valueOf(resultvalue[4]), resultvalue[5], "ONLINE");
                    }
                    else if(resultvalue[0].equals("Log did not save Offline"))
                    {
                        popup(resultvalue[1], resultvalue[2], resultvalue[3],Integer.valueOf(resultvalue[4]), resultvalue[5], "ONLINE");
                    }

                    else
                        popup(resultvalue[1], resultvalue[2], resultvalue[3],Integer.valueOf(resultvalue[4]), resultvalue[5], "OFFLINE");

                }

                progressDialog.dismiss();
            }


            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(Geo_QR_MapActivity.this,
                        "Punching...",
                        "Please Wait");
                // progressDialog.getWindow().setLayout(200,50);
                progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);


            }


            @Override
            protected void onProgressUpdate(String... text) {


            }
        }





        private void PAlertDialog(String title, String msg)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(Geo_QR_MapActivity.this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton( getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }



        public Integer WriteLogData(String Badgeno, String dt, String time,String formattedDate,String formattedDate1, int punchtype, int geofenceid, int sent)//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res = 0;
            String Punchtpe = getResources().getString(R.string.QR_Geo);
            try {
                database = db.getWritableDatabase();
                database.execSQL("INSERT INTO tblLogs(BadgeNo,date,time,direction,GeoID,PunchType,sent,Ter,datetosent,timetosent)VALUES('" + BadgeNo + "','" + dt + "','" + time + "'," + punchtype + "," + geofenceid + ",'" + Punchtpe + "'," + sent + ",'" + building + "','" + formattedDate + "','" + formattedDate1 + "')");
                res = 1;
            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
            }
            return res;
        }






        @RequiresApi(api = Build.VERSION_CODES.M)
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {


                if (grantResults.length > 0) {
                    if(permissions != null) {
                        for (int i = 0; i < permissions.length; i++) {


                            if (permissions[i].equals(ACCESS_FINE_LOCATION)) {
                                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                    Log.e("msg", "location granted");
                                    gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                    network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    Activity#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for Activity#requestPermissions for more details.
                                        return;
                                    }
                                    if (gps_enabled)

                                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                                    else if (network_enabled)
                                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);

                                    Criteria criteria = new Criteria();
                                    bestProvider = locationManager.getBestProvider(criteria, false);
                                    location = locationManager.getLastKnownLocation(bestProvider);
                                }
                            }


                        }
                    }

                }


            }
        }

        private boolean checkAndRequestPermissions() {
            int location_permission = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);

            List<String> listPermissionsNeeded = new ArrayList<>();

            if (location_permission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(ACCESS_FINE_LOCATION);
            } else {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (gps_enabled)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                else if (network_enabled)
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);

                Criteria criteria = new Criteria();
                bestProvider = locationManager.getBestProvider(criteria, false);
                location = locationManager.getLastKnownLocation(bestProvider);

            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                return false;
            }
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void Map_InitValue(GoogleMap googleMap )
        {
            googleMap.clear();
            count =0;
            int GeoCount=0;
            GeoID=0;
            dis=0;
            Geolocation=null;
            BadgeNo="";
            map = googleMap;
            // Get an iterator
            Set set = locations.entrySet();
            Iterator i = set.iterator();
            String str="";
            String[]  getredius;



            try {

                checkAndRequestPermissions();
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                int duration = Toast.LENGTH_SHORT;
                if (!gps_enabled && !network_enabled) {  Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "nothing is enabled", duration);
                    toast.show();
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                // Get a set of the entries
                if (locations.size() == 0) {
                    Toast.makeText(this, "You are not assigned any Geofence", Toast.LENGTH_LONG).show();
                    if(location != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
                        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                    }
                    return;
                }
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    System.out.print(me.getKey() + ": ");
                    System.out.println(me.getValue());

                    LatLng latLng = (LatLng) me.getKey();
                    str = (String) me.getValue();
                    getredius = str.split("~");
                    CircleOptions circleOptions = new CircleOptions().center(latLng).radius(Integer.valueOf(getredius[1])).fillColor(shadeColor).strokeColor(Color.BLUE);
                    circle = googleMap.addCircle(circleOptions);
                    float[] distance = new float[2];
                    if (location == null) {
                        Toast.makeText(this, getResources().getString(R.string.your_location_not_found), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (location.getLatitude() > 0 && location.getLongitude() > 0) {
                        Location.distanceBetween(location.getLatitude(),
                                location.getLongitude(), circle.getCenter().latitude,
                                circle.getCenter().longitude, distance);


                        if (distance[0] > circle.getRadius()) {
                            if(GeoCount==0)
                            {
                                dis=distance[0];
                                Geolocation=latLng;
                                GeoCount++;
                            }
                            if (dis > distance[0]) {
                                dis = distance[0];
                                Geolocation=latLng;
                            }

                        } else if (distance[0] < circle.getRadius()) {
                            latLong = latLng;
                            circle1 = circle;
                            building = getredius[0];
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                            map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                            BadgeNo = getredius[2];
                            GeoID = Integer.valueOf(getredius[3]);
                            count++;
                        }
                    }
                }
            }
            catch(Exception ex)
            {
                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }


        public Integer ReadLastPunch()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res =0,punchtype=2;
            String BadgeNo="";
            Date dt;//=new Date();
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM  tblLogs  order by ID Desc LiMIT 1", null);
                if (cursor.moveToFirst()) {
                    do {
                        punchtype =  Integer.valueOf( cursor.getString(cursor.getColumnIndex("direction")));

                    } while (cursor.moveToNext());
                }
                cursor.close();

            }
            catch (Exception ex) {
                punchtype=3;
                Log.d(TAG, ex.getMessage());
            }
            return punchtype;
        }


        private void SoundIt(int index) {
            switch (index) {
                case 0:
                    out.start();
                    break;
                case 1:
                    in.start();
                    break;
                case 2:
                    Geo.start();
                    break;

            }

        }
        public void popup(String BadgeNo,String formattedDate,String formattedDate2, int punchtype,String building,String LogType) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View customView = inflater.inflate(R.layout.custom_layout_attendance, null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
            txtBadgeNo = (TextView) customView.findViewById(R.id.txtBadgeNo);
            txtPunchDate = (TextView) customView.findViewById(R.id.txtPunchDate);
            txtPunchTime = (TextView) customView.findViewById(R.id.txtPunchTime);
            txtLastPunch = (TextView) customView.findViewById(R.id.txtLastPunch);
            txtLogStatus = (TextView) customView.findViewById(R.id.txtLogStatus);
            txtGeoname = (TextView) customView.findViewById(R.id.txtGeoname);
            ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
            // Initialize a new instance of popup window
            mPopupWindow = new PopupWindow(
                    customView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.WRAP_CONTENT
            );

            // Set an elevation value for popup window
            // Call requires API level 21
            if (Build.VERSION.SDK_INT >= 21) {
                mPopupWindow.setElevation(5.0f);
            }

            // Get a reference for the custom view close button

            txtBadgeNo.setText(BadgeNo);
            txtPunchDate.setText(formattedDate);
            txtPunchTime.setText(formattedDate2);
            if (punchtype == 1) {
                SoundIt(1);
                txtLastPunch.setText(getResources().getString(R.string.IN));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        if (urlconnection.checkConnection()) {
                            if (!checkLocationMonitoringServiceRunning()) {
                                Geo_QR_MapActivity.AsyncTaskRunnerLocMonitoring runner = new Geo_QR_MapActivity.AsyncTaskRunnerLocMonitoring();
                                runner.execute();
                            }
                        }

                    }
                }, 5000);
            }
            else {
                SoundIt(0);
                txtLastPunch.setText(getResources().getString(R.string.OUT));
                stopService(new Intent(this, LocationMonitoringService.class));
            }
            txtLogStatus.setText(LogType);
            txtGeoname.setText(building);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setFocusable(true);
            // Set a click listener for the popup window close button
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Dismiss the popup window
                    txtBadgeNo.setText("");
                    txtPunchDate.setText("");
                    txtPunchTime.setText("");

                    txtLastPunch.setText("");

                    txtLogStatus.setText("");
                    txtGeoname.setText("");
                    finish();
                    mPopupWindow.dismiss();
                }
            });

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
            // Finally, show the popup window at the center location of root relative layout
            //     mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
            mRelativeLayout.post(new Runnable() {
                public void run() {
                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
                }
            });

        }
        private boolean isMyServiceRunning(Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }






        public Integer WriteGeofanceData(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res =0;
            try {
                String[] Val=Value.split(";");
                database=db.getWritableDatabase();
                database.execSQL("delete from  Geofence");
                for(int i=0;i<Val.length;i++)
                {
                    String[] Val1=Val[i].split("~");
                    database=db.getWritableDatabase();
                    database.execSQL("INSERT INTO Geofence(GeoID,Lat,Long,Radius,GeoName,KeyName,Badgeno,Shape_Name,Group_Name,Zoom_value)VALUES("+Val1[0]+","+Val1[1]+","+Val1[2]+","+Val1[3]+",'"+Val1[4]+"','"+Val1[4]+"','"+Val1[5]+"','"+Val1[6]+"','"+Val1[7]+"','"+Val1[8]+"')" );
                }

                res=1;
            }
            catch (Exception ex) {
                res=0;
                Log.d(TAG, ex.getMessage());
                Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG ).show();
            }
            return res;
        }


        public Integer Badgeno()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res=0;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT BadgeNo FROM  Registration", null);
                if (cursor.moveToFirst()) {
                    do {
                        BadgeNoReg = cursor.getString(cursor.getColumnIndex("BadgeNo"));

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

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void setSupportActionBar(android.widget.Toolbar toolbar) {
        }

        public Integer ReadTrackingValue()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            String MacValue="";
            int res =0;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
                if (cursor.moveToFirst()) {
                    do {
                        Tracking =  Integer.valueOf(cursor.getString(cursor.getColumnIndex("Tracking")));

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


        public Integer Geofence()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            String MacValue="";
            int res =0;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM  Geofence", null);
                if (cursor.moveToFirst()) {
                    do {
                        countGeofence++;

                    } while (cursor.moveToNext());
                }
                cursor.close();
                res =1;
            }
            catch (Exception ex) {
                res=-0;
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
                    new GpsUtils(Geo_QR_MapActivity.this).turnGPSOn(new GpsUtils.onGpsListener() {
                        @Override
                        public void gpsStatus(boolean isGPSEnable) {
                            // turn on GPS
                            isGPS = isGPSEnable;
                        }
                    });

                    //startStep1();

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




        @Override
        protected void onStop() {
            Log.d(TAG, "onStop");
            super.onStop();
            locationManager.removeUpdates(this);

        }

        @Override
        protected void onPause() {
            Log.d(TAG, "onPause");
            super.onPause();
            locationManager.removeUpdates(this);

        }

        @Override
        protected void onRestart() {
            super.onRestart();

            Log.d(TAG, "onRestart");
        }




        @Override
        public void onResume() {
            super.onResume();

            Log.d(TAG, "onResume");


        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            locationManager.removeUpdates(this);
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
        public Integer ReadSystemValue()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            String MacValue = "";
            int res = 0;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode  From   Registration", null);
                if (cursor.moveToFirst()) {
                    do {

                        BLE = Integer.valueOf(cursor.getString(cursor.getColumnIndex("BLE")));
                        geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
                        QRCode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRCode")));
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

        @Override
        public void onBackPressed() {
            finish();
        }





        public Integer ReadSysSetting()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res =0;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM  system_setting", null);
                if (cursor.moveToFirst()) {
                    do {

                        AutoPunch= Integer.valueOf( cursor.getString(cursor.getColumnIndex("AutoPunchGeo")));
                        Biometric= Integer.valueOf( cursor.getString(cursor.getColumnIndex("Biometric")));
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



        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {



                case REQUEST_ENABLE_FT:
                    // When the request to enable Bluetooth returns
                    if (resultCode == Activity.RESULT_OK) {

                        validateLocation(0);
                    }
                    break;

                default:
                    Log.e(TAG, "wrong request code");
                    break;
            }
        }
        public boolean checkServiceRunning(){
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            {
                if ("com.electronia.mElsmart.LocationUpdateservice"
                        .equals(service.service.getClassName()))
                {
                    return true;
                }
            }
            return false;
        }


        public boolean checkServiceRunningGeoLog(){
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            {
                if ("com.electronia.mElsmart.Services.LogUpdateService"
                        .equals(service.service.getClassName()))
                {
                    return true;
                }
            }
            return false;
        }


        public boolean checkLocationMonitoringServiceRunning(){
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            {
                if ("com.electronia.mElsmart.LocationMonitoringService"
                        .equals(service.service.getClassName()))
                {
                    return true;
                }
            }
            return false;
        }






        private class AsyncTaskRunnerLocMonitoring extends AsyncTask<String, String, String> {

            private String resp;


            @Override
            protected String doInBackground(String... params) {
                publishProgress("Sleeping..."); // Calls onProgressUpdate()
                try {
                    Intent intent = new Intent(Geo_QR_MapActivity.this, LocationMonitoringService.class);
                    startService(intent);

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
