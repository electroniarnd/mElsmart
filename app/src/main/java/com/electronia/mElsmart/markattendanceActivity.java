package com.electronia.mElsmart;

import android.Manifest;
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
import android.app.Activity;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
//import android.Manifest;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.electronia.mElsmart.Common.UrlConnection;
import com.electronia.mElsmart.Services.AutoRegistration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
//import org.apache.commons.net.time.TimeTCPClient;


public class markattendanceActivity extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "markattendanceActivity";
    private static final String TODO ="" ;
    GoogleMap map;
    SupportMapFragment mapFragment;
    //  LatLng portLatLong,portLatLong1,portLatLong2,portLatLong3;
    Spinner spdLocations;
    HashMap<LatLng, String> locations = new HashMap<>();
    String building = "", bestProvider, BadgeNo = "";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int shadeColor = 0x44ff0000, GeoID = 0, count = 0, counter = 0;
    LocationManager locationManager;
    Circle circle, circle1;
    ProgressDialog dialog;
    private Location lastLocation;
    public static Location location;
    public static LatLng Geolocation;
    LatLng latLong;
    float dis = 0;
    MediaPlayer in = null;
    MediaPlayer out = null;
    MediaPlayer Geo = null;
    MediaPlayer error = null;
    ArrayList<String> locationArray = new ArrayList<>();
    Controllerdb db; //= new Controllerdb(getContext());
    SQLiteDatabase database;
    TextView txtBadgeNo, txtPunchDate, txtPunchTime, txtLastPunch, txtLogStatus, txtGeoname;
    //private static String EMPLOYEE_SERVICE_URI1="http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    private String BadgeNoReg = "";
    Button check_in_button;
    private static int Tracking = 0;
    private boolean isGPS = false;
    static int countGeofence = 0;
    private static String ServiceURL = "";//+"/ElguardianService/Service1.svc/" +
    private static int Registration = 0;

    private Context mContext;
    private Activity mActivity;

    private RelativeLayout mRelativeLayout;
    private Button mButton;

    private PopupWindow mPopupWindow;

    public static int BLE = 0;
    public static int geofence = 0; //Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
    public static int QRCode = 0;
    private static int startvalue = 0;
    private static int AutoPunch = 0;
    private static int Biometric = 0;
    private static final int REQUEST_ENABLE_FT = 3;
    private static View view;
    private static FragmentManager fragmentManager;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    Criteria criteria;
    private MapView mapView;
    UrlConnection urlconnection;
    public static int REQUEST_ID_MULTIPLE_PERMISSIONS = 21;


    public static markattendanceActivity newInstance() {
        markattendanceActivity fragment = new markattendanceActivity();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_markattendance, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        check_in_button = (Button) view.findViewById(R.id.check_in_button);
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        db = new Controllerdb(getContext());
        checkAndRequestPermissions();


        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        //  toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        check_in_button.setEnabled(true);

        if (GetServiceURL() == 1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();
            return;
        } else {
            if (Registration == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.Registration_does_not_exist), Toast.LENGTH_LONG).show();
                Intent Registration = new Intent(getActivity(), SignUp.class);
                startActivity(Registration);
                return;
            }
        }


        if (Geofence() == 1) {
            if (countGeofence == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.goefence_not_found_for_this_employee), Toast.LENGTH_LONG);
                check_in_button.setEnabled(false);
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG);
        }
        if (ReadSysSetting() == 0) {//check System Setting
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();//check_Setting();
            return;
        }

        if (RaadGeoFence() != 1)
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();

        in = MediaPlayer.create(getActivity(), R.raw.in);
        out = MediaPlayer.create(getActivity(), R.raw.out);
        Geo = MediaPlayer.create(getActivity(), R.raw.geo);
        // error = MediaPlayer.create(this, R.raw.error);
        urlconnection = new UrlConnection(getActivity().getApplicationContext());


        ButterKnife.bind(getActivity());
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        dialog = new ProgressDialog(getActivity());
        criteria = new Criteria();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ////////// GetGeofenceFromServer();
        /// mapFragment.getMapAsync(this);
        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();// markattendanceActivity.this;
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl);


        check_in_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validateLocation(1);

                if (count > 0) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.you_are_inside) + building, Toast.LENGTH_SHORT).show();

                } else {
                    if (Geolocation != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Geolocation, 17.0f));
                        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                    }
                    Toast.makeText(getActivity(), getResources().getString(R.string.you_are_outside), Toast.LENGTH_SHORT).show();
                    //  SoundIt(2);

                }
            }
        });
        if (!checkServiceRunningGeoLog()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    getActivity().startService(new Intent(getActivity(), GeofenceLogService.class));
                }
            };
            thread.start();
        }
        markattendanceActivity.AsyncTaskRunner runner = new markattendanceActivity.AsyncTaskRunner();
        runner.execute();
        if (!checkServiceRunning()) {

            Thread thread = new Thread() {
                @Override
                public void run() {
                    getActivity().startService(new Intent(getActivity(), LocationUpdateservice.class));
                }
            };
            thread.start();
        }


        if (!AutoUpdationActivity()) {

            // getActivity().startService(new Intent(getActivity(), AutoRegistration.class));

        }
    }


    public boolean AutoUpdationActivity() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.electronia.mElsmart.Services.AutoRegistration"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private boolean checkAndRequestPermissions() {
        int location_permission = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);

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
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {


            if (grantResults.length > 0) {
                if (permissions != null) {
                    for (int i = 0; i < permissions.length; i++) {


                        if (permissions[i].equals(ACCESS_FINE_LOCATION)) {
                            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                Log.e("msg", "location granted");
                                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                                if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Map_InitValue(googleMap);

        if ((startvalue == 0) && AutoPunch == 1 && Biometric == 1) {

            Intent newIntent = new Intent(getActivity(), biometric.class);
            startActivityForResult(newIntent, REQUEST_ENABLE_FT);
        }
        if ((startvalue == 0) && AutoPunch == 1 && Biometric == 0) {

            validateLocation(0);
            if (count > 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.you_are_inside) + building, Toast.LENGTH_SHORT).show();

            } else {
                if (Geolocation != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(Geolocation, 17.0f));
                    map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                }
                Toast.makeText(getActivity(), getResources().getString(R.string.you_are_outside), Toast.LENGTH_SHORT).show();
                //  SoundIt(2);

            }
        }
        startvalue = 1;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu1, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private MarkerOptions generateMarker(LatLng latLng, String title) {
        TextView text = new TextView(
                getActivity().getApplicationContext());
        text.setText(title);
        text.setTextColor(Color.parseColor("#ffffff"));
        text.setPadding(5, 5, 5, 5);
        return new MarkerOptions().position(latLng).title("Building");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    void validateLocation(int StValue) {
        if (StValue > 0)
            Map_InitValue(map);
        int lastpunch = -1;
        int punchtye = 0;
        //   ClearData();
        try {
            if (count > 0) {
                lastpunch = ReadLastPunch();
                if (lastpunch == 0 || lastpunch == 1) {
                    if (lastpunch == 0) {

                        punchtye = 1;
                        //  SaveLog(BadgeNo, 1, GeoID);
                        //  SoundIt(1);

                    } else {
                        punchtye = 0;
                        //  SaveLog(BadgeNo, 0, GeoID);
                        // SoundIt(0);
                    }

                    markattendanceActivity.AsyncTaskRunnerSecond runner1 = new markattendanceActivity.AsyncTaskRunnerSecond();
                    runner1.execute(BadgeNo, String.valueOf(punchtye), String.valueOf(GeoID));


                } else {
                    if (lastpunch == 2) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.Last_punch_not_found), Toast.LENGTH_SHORT).show();
                        //SaveLog(BadgeNo, 1, GeoID);

                        markattendanceActivity.AsyncTaskRunnerSecond runner1 = new markattendanceActivity.AsyncTaskRunnerSecond();
                        runner1.execute(BadgeNo, "1", String.valueOf(GeoID));
                    } else
                        Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


//

    @Override
    public void onLocationChanged(Location Currentlocations) {
        lastLocation = Currentlocations;
        location = Currentlocations;

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
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Geofence", null);
            if (cursor.moveToFirst()) {
                do {
                    locations.put(new LatLng(Float.valueOf(cursor.getString(cursor.getColumnIndex("Lat"))), Float.valueOf(cursor.getString(cursor.getColumnIndex("Long")))), String.valueOf(cursor.getString(cursor.getColumnIndex("KeyName"))) + "~" + String.valueOf(cursor.getString(cursor.getColumnIndex("Radius"))) + "~" + String.valueOf(cursor.getString(cursor.getColumnIndex("Badgeno"))) + "~" + String.valueOf(cursor.getString(cursor.getColumnIndex("GeoID"))));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public String SaveLog(String BadgeNo, int punchtype, int geoid) {

        String msg = "", s = "", lang = "", number = "", urlar = "", rest = "Success", json = "";
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
            lang = Locale.getDefault().getDisplayLanguage();
            Criteria criteria = new Criteria();
            bestProvider = locationManager.getBestProvider(criteria, false);
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


                resp = SaveLog(params[0], Integer.valueOf(params[1]), Integer.valueOf(params[2]));
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (!resp.contains("Success")) {
                            if (resp.contains("internet_Connection_not_found"))
                                Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.Log_Saved_OffLine), Toast.LENGTH_SHORT).show();
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
            String res = "";
            /// String result=text[0];
            String[] resultvalue = result.split("~");
            if (resultvalue.length > 0) {
                if (resultvalue[0].equals("Success")) {
                    popup(resultvalue[1], resultvalue[2], resultvalue[3], Integer.valueOf(resultvalue[4]), resultvalue[5], getResources().getString(R.string.ONLINE));
                } else if (resultvalue[0].equals("log_did_not_save_offline")) {
                    popup(resultvalue[1], resultvalue[2], resultvalue[3], Integer.valueOf(resultvalue[4]), resultvalue[5],  getResources().getString(R.string.ONLINE));
                } else
                    popup(resultvalue[1], resultvalue[2], resultvalue[3], Integer.valueOf(resultvalue[4]), resultvalue[5],  getResources().getString(R.string.OFFLINE));

            }

            progressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(),
                    getResources().getString(R.string.Processing),
                    getResources().getString(R.string.Please_Wait));
            // progressDialog.getWindow().setLayout(200,50);
            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);


        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }


    public boolean checkConnection() {

        try {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
            if (activeNetworkInfo != null) { // connected to the internet
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Network_Connection_Error),
                    Toast.LENGTH_SHORT).show();
            Log.e("", e.getMessage());
        }
        return false;
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


    public Integer WriteLogData(String Badgeno, String dt, String time,String formattedDate,String formattedDate1, int punchtype, int geofenceid, int sent)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        String Punchtpe = "Geo";
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
    public void Map_InitValue(GoogleMap googleMap) {
        googleMap.clear();
        count = 0;
        int GeoCount = 0;
        GeoID = 0;
        dis = 0;
        Geolocation = null;
        BadgeNo = "";
        map = googleMap;
        // Get an iterator
        Set set = locations.entrySet();
        Iterator i = set.iterator();
        String str = "";
        String[] getredius;


        try {

            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            int duration = Toast.LENGTH_SHORT;
            if (!gps_enabled && !network_enabled) {
                Context context = getActivity().getApplicationContext();
                Toast toast = Toast.makeText(context, "nothing is enabled", duration);
                toast.show();
                return;
            }
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
           // map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
          //  map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
            // Get a set of the entries
            if (locations.size() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.assigned_any_geofence), Toast.LENGTH_LONG).show();
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
                //    map.addMarker(generateMarker(latLng, locations.get(latLng)));

                CircleOptions circleOptions = new CircleOptions().center(latLng).radius(Integer.valueOf(getredius[1])).fillColor(shadeColor).strokeColor(Color.BLUE);
              //  map.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(getredius[0]));
                circle = googleMap.addCircle(circleOptions);


                float[] distance = new float[2];
                if (location == null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.your_location_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (location.getLatitude() > 0 && location.getLongitude() > 0) {
                    Location.distanceBetween(location.getLatitude(),
                            location.getLongitude(), circle.getCenter().latitude,
                            circle.getCenter().longitude, distance);


                    if (distance[0] > circle.getRadius()) {
                        //    map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                        //    map.moveCamera(CameraUpdateFactory.newLatLngZoom(portLatLong, 17.0f));
                        //    Toast.makeText(this, "You are outsid", Toast.LENGTH_SHORT).show();
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
                        // dialog.setMessage("Fetching data");
                        //  dialog.setCanceledOnTouchOutside(false);
                        // dialog.show();
                        //     markAttendence();
                        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                     //   googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
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
            Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_LONG).show();
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
        View customView = inflater.inflate(R.layout.custom_layout_attendance, null);

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
            if(!checkLocationMonitoringServiceRunning()) {
                getActivity().startService(new Intent(getActivity(), LocationMonitoringService.class));
            }
        }
        else {
            SoundIt(0);
            txtLastPunch.setText(getResources().getString(R.string.OUT));
            getActivity().stopService(new Intent(getActivity(), LocationMonitoringService.class));
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

                mPopupWindow.dismiss();
            }
        });
        mRelativeLayout.post(new Runnable() {
            public void run() {
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
            }
        });

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void  ClearData()
    {
        txtBadgeNo.setText("");
        txtPunchDate.setText("");
        txtPunchTime.setText("");

        txtLastPunch.setText("");

        txtLogStatus.setText("");
        txtGeoname.setText("");
    }




    public boolean GetGeofenceFromServer() {

        String msg = "", s="", lang="",number="",urlar="";
        try {

            if (!checkConnection()) {

                PAlertDialog( getResources().getString(R.string.Error), "Internet not found");
                return false;
            }
            Badgeno();
            if (BadgeNoReg == null && BadgeNoReg.isEmpty() && BadgeNoReg.equals("null"))
            {
                PAlertDialog( getResources().getString(R.string.Error), "Registration not found");
                return false;
            }
                urlar =ServiceURL+"/ElguardianService/Service1.svc/";


            lang= Locale.getDefault().getDisplayLanguage();
                s = urlar + "/GetGeoData/" + "/'" + BadgeNoReg+"'"+ "/'" +lang;

            EMPLOYEE_SERVICE_URI1="";
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


                PAlertDialog( getResources().getString(R.string.Error), getstring);

                return false;
            }


            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            if(WriteGeofanceData(json1)!=1) {

                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
            PAlertDialog(getResources().getString(R.string.Error), e.getMessage());
            return false;
        }
        return true;

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
            Toast.makeText(getActivity(), ex.getMessage(),Toast.LENGTH_LONG ).show();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.Registration_id:
                startActivity(new Intent(getActivity(), RegistrationActivity.class));
                break;
            case R.id.Setting_id:
                startActivity(new Intent(getActivity(), settingGeo.class));
                break;
            case R.id.About_id:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.Record_id:
                startActivity(new Intent(getActivity(),recordLogs.class));
                break;
            case R.id.Tracking_id:
                if(ReadTrackingValue()==0) {
                    if(Tracking==1 )
                        startActivity(new Intent(getActivity(), Tracking.class));
                    else
                        Toast.makeText(getActivity(),"not authorized",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getActivity(),"Local Database Error",Toast.LENGTH_LONG).show();
                break;

            default:
                return super.onOptionsItemSelected(item);

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
                new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
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
    public void onStart() {
        super.onStart();
      Log.d(this.getClass().getSimpleName() , "onStart()");
    }



// Fragment is active




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,  "onDestroyView()");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }
    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
         locationManager.removeUpdates(this);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkServiceRunningGeoLog()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    getActivity().startService(new Intent(getActivity(), GeofenceLogService.class));
                }
            };
            thread.start();
        }

        Log.d(TAG, "onResume");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    public Integer GetServiceURL()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        Registration=0;
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
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
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
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("com.electronia.mElsmart.GeofenceLogService"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }


    public boolean checkLocationMonitoringServiceRunning(){
        ActivityManager manager = (ActivityManager)  getActivity().getSystemService(ACTIVITY_SERVICE);
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
                startActivity(new Intent(getContext(), LocationMonitoringService.class));

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


