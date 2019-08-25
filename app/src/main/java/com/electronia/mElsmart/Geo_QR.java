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
import android.graphics.Point;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.electronia.mElsmart.barcode.barcodecaptureactivity;

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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public class Geo_QR extends Fragment
{
    private static final String TAG ="markattendanceActivity" ;
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    GoogleMap map;

    //  LatLng portLatLong,portLatLong1,portLatLong2,portLatLong3;
    Spinner spdLocations;
    HashMap<LatLng, String> locations = new HashMap<>();
    int  QrCodeId=0;
    int Geoid=0;
    String  building="",bestProvider;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int shadeColor = 0x44ff0000, GeoID=0,count =0,counter=0;
    LocationManager locationManager;
    Circle circle, circle1;
    ProgressDialog dialog;
    private Location lastLocation;
    Location location;
    LatLng latLong;
    float dis=0;
    MediaPlayer in = null;
    MediaPlayer out = null;
    MediaPlayer qrcode = null;
    MediaPlayer error = null;
    ArrayList<String> locationArray = new ArrayList<>();
    Controllerdb db;// =new Controllerdb(this);
    SQLiteDatabase database;
    TextView txtBadgeNo,txtPunchDate,txtPunchTime,txtLastPunch,txtLogStatus,txtGeoname,QR_CodeName;
    //private static String EMPLOYEE_SERVICE_URI1="http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1="";
    private String BadgeNoReg="";
    Button check_in_button;
    private static int Tracking = 0;
    private boolean isGPS = false;
    static int   countGeofence=0;
    private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;

    private static int countQR=0;
    private static int countResult=0;
    String BadgeNo="",QRCODE="",QRCodeName="";
    private Context mContext;
    private Activity mActivity;
    private RelativeLayout mRelativeLayout;
    private Button mButton;
    private PopupWindow mPopupWindow;
    private static View view;
    private static FragmentManager fragmentManager;



    public static  int BLE = 0;
    public static  int geofence =0; //Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
    public static  int QRCode=0;
    private static int startvalue = 0;
    private static int AutoPunch = 0;
    private static int Biometric = 0;
    private static final int  REQUEST_ENABLE_FT=3;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private MapView mapView;
    public static LatLng Geolocation;
    int GeoCount = 0;
    UrlConnection urlconnection;
    public static Geo_QR newInstance() {
        Geo_QR fragment = new Geo_QR();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_barcodemain, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        check_in_button = (Button) view.findViewById(R.id.scan_barcode_button);
        urlconnection = new UrlConnection(getActivity().getApplicationContext());
        db =new Controllerdb(getContext());
        if(GetServiceURL()==1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(getActivity(), getResources().getString(R.string.Registration_does_not_exist), Toast.LENGTH_LONG).show();
                Intent Registration = new Intent(getActivity(), SignUp.class);
                startActivity(Registration);
                return;
            }
        }


        if(Geofence()==1)
        {
            if(countGeofence==0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.goefence_not_found_for_this_employee), Toast.LENGTH_LONG);
                check_in_button.setEnabled(false);
            }
        }
        else
        {
            Toast.makeText(getActivity(),getResources().getString(R.string.Error_in_reading_local_database),Toast.LENGTH_LONG);
        }
        if(ReadSysSetting()==0) {//check System Setting
            Toast.makeText(getActivity(),getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();//check_Setting();
            return;
        }
        qrcode=MediaPlayer.create(getActivity(), R.raw.qrcode);
        if( RaadGeoFence() !=1)
            Toast.makeText(getActivity(),getResources().getString(R.string.Error_in_reading_local_database),Toast.LENGTH_LONG).show();

        mContext = getActivity().getApplicationContext();
        // Get the activity
        mActivity = getActivity();


        check_in_button.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(getActivity().getApplicationContext(), barcodecaptureactivity.class);
                    startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);


            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu1, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    private void setSupportActionBar(android.widget.Toolbar toolbar) {
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

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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

            case BARCODE_READER_REQUEST_CODE:


                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Barcode barcode = data.getParcelableExtra(barcodecaptureactivity.BarcodeObject);
                        Point[] p = barcode.cornerPoints;
                        // scanResult.setText(barcode.displayValue);
                        ReadQRCode(barcode.displayValue);
                        if (countResult == 1) {
                            Intent intent = new Intent(getActivity(), Geo_QR_MapActivity.class);
                          //  intent.putExtras(sendBundle);;
                            startActivity(intent);
                            countResult = 0;
                        } else

                        {
                            Toast.makeText(getActivity(),getResources().getString(R.string.QR_Code_not_matching),Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.No_Result_Found),Toast.LENGTH_LONG).show();
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }

                break;

            case REQUEST_ENABLE_FT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), barcodecaptureactivity.class);
                        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                }
                break;

            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    public Integer ReadQRCode(String QRCodeValue)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        Date dt;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  QRCode_Permission  order by ID Desc", null);
            if (cursor.moveToFirst()) {
                do {
                    QRCODE= ( cursor.getString(cursor.getColumnIndex("Qrcode")));
                    if(QRCodeValue.equals(QRCODE)) {
                        BadgeNo = (cursor.getString(cursor.getColumnIndex("BadgeNo")));
                        QrCodeId = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRId")));
                        Geoid = Integer.valueOf(cursor.getString(cursor.getColumnIndex("geoID")));
                        QRCodeName = cursor.getString(cursor.getColumnIndex("QRCode_Name"));
                        countResult=1;
                        break;

                    }
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
}

