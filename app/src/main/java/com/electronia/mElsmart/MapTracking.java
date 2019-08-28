package com.electronia.mElsmart;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompatSideChannelService;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.electronia.mElsmart.models.LatLngInterpolator;
import com.electronia.mElsmart.models.MarkerAnimationHelper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MapTracking  extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "MapTracking";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private GoogleMap map;
    private MapView myMapView;
     private String rest="";
    private Context mContext;
    ArrayList<  LatLng > locationArrayList = new ArrayList<>();
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    String EmpID="";
    private EditText edtfrom,edtTo;
    private Button btnGetLocation;//=(Button)findViewById(R.id.button1);
    private static String FromDate="",ToDate="";
    DatePickerDialog picker;
    private Marker currentPositionMarker = null;
   // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;
    UrlConnection urlconnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);
        edtfrom=(EditText) findViewById(R.id.edtFromDate);
        edtTo=(EditText) findViewById(R.id.edtToDate);
        edtfrom.setInputType(InputType.TYPE_NULL);
        edtTo.setInputType(InputType.TYPE_NULL);
        btnGetLocation=   (Button)findViewById(R.id.btnGetLocation);
        urlconnection = new UrlConnection(getApplicationContext());
      //  locationArrayList.add(new LatLng(-08.8265861,13.2274667));
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if(GetServiceURL()==1) {
            Toast.makeText(this, getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(this,getResources().getString(R.string.Registration_does_not_exist), Toast.LENGTH_LONG).show();
                return;
            }
        }

        Intent intent = getIntent();
        EmpID =  intent.getStringExtra("EmpID");
        mContext = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, mapFragment).commit();
        }
      //  setupViewModel();
   //     checkLocationPermission();

        edtfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MapTracking.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtfrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                FromDate=year +"."+ ((monthOfYear + 1)<10?("0"+(monthOfYear + 1)):(monthOfYear + 1)) + "." +((dayOfMonth<10)? ("0"+(dayOfMonth)):dayOfMonth); ;
                            }
                        }, year, month, day);
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
            }
        });


        edtTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MapTracking.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                edtTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                ToDate= year +"."+((monthOfYear + 1)<10?("0"+(monthOfYear + 1)):(monthOfYear + 1))  + "." + ((dayOfMonth<10)? ("0"+(dayOfMonth)):dayOfMonth);;




                            }
                        }, year, month, day);
                picker.getDatePicker().setMaxDate(System.currentTimeMillis());
                picker.show();
            }
        });


        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FromDate == null || FromDate.isEmpty() || FromDate.equals("null")) {

                    Toast.makeText(MapTracking.this,getResources().getString(R.string.Please_enter_from_date),Toast.LENGTH_LONG).show();
                    return ;

                }
                if (ToDate == null || ToDate.isEmpty() || ToDate.equals("null")) {

                    Toast.makeText(MapTracking.this,getResources().getString(R.string.Please_Select_To_Date),Toast.LENGTH_LONG).show();
                    return ;
                }
                //    tvw.setText("Selected Date: "+ eText.getText());
                MapTracking.AsyncTaskRunner runner = new MapTracking.AsyncTaskRunner();
                runner.execute("1",FromDate,ToDate);
            }
        });

        String CurrentDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        MapTracking.AsyncTaskRunner runner = new MapTracking.AsyncTaskRunner();
        runner.execute("0",CurrentDate,CurrentDate);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

     //   MapTracking.AsyncTaskRunner runner = new MapTracking.AsyncTaskRunner();
      //  runner.execute("0","0","0");;


     //   MapTracking.AsyncTaskRunner runner1 = new MapTracking.AsyncTaskRunner();
      //  runner1.execute("1","1","1");
    }

    private String plotMarkers() {
         String res="";
        if (map != null) {
            if (locationArrayList.size() > 0) {
                LatLng india = new LatLng(locationArrayList.get(0).latitude, locationArrayList.get(0).longitude);
                map.addMarker(new MarkerOptions().position(india).title("Start Point").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                map.moveCamera(CameraUpdateFactory.newLatLng(india));
                map.animateCamera(zoom);

                int arrowColor = Color.RED; // change this if you want another color (Color.BLUE)
                int lineColor = Color.RED;
                LatLngInterpolator aa = new LatLngInterpolator.Spherical();
              //  locationArrayList.add(india);
                Log.d("Location list", "plotMarkers: " + locationArrayList.size());
                BitmapDescriptor endCapIcon = getEndCapIcon(arrowColor);
                //Draw Line
                LatLng singleLatLong = null;
                ArrayList<LatLng> pnts = new ArrayList<LatLng>();
                if (locationArrayList != null) {
                    for (int i = 1; i < locationArrayList.size(); i++) {
                        double routePoint1Lat = locationArrayList.get(i).latitude;
                        double routePoint2Long = locationArrayList.get(i).longitude;
                        singleLatLong = new LatLng(routePoint1Lat,
                                routePoint2Long);
                        pnts.add(singleLatLong);

                        currentPositionMarker = map.addMarker(new MarkerOptions().position( new LatLng(locationArrayList.get(i-1).latitude, locationArrayList.get(i-1).longitude)).icon((BitmapDescriptorFactory.fromResource(R.drawable.mapicon))));//.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                           //.title("Start"));//.rotation( HeadingRotation.floatValue()).icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)) );
                        if(i==locationArrayList.size()-1)
                           map.addMarker(new MarkerOptions().position(new LatLng(locationArrayList.get(i).latitude, locationArrayList.get(i).longitude)).title("End").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                       else
                        // map.addMarker(new MarkerOptions().position( new LatLng(locationArrayList.get(i).latitude, locationArrayList.get(i).longitude)).icon((BitmapDescriptorFactory.fromResource(R.drawable.mapicon))));//;.HUE_VIOLET
                            MarkerAnimationHelper.animateMarkerToGB(currentPositionMarker, new LatLng(locationArrayList.get(i).latitude, locationArrayList.get(i).longitude), aa);
                    }
                }
                map.addPolyline(new PolylineOptions().
                        addAll(pnts)
                        .width(8)
                        // .startCap(new RoundCap())
                        // .endCap(new CustomCap(endCapIcon, 8))
                        //  .jointType(JointType.ROUND)
                        .color(Color.BLUE)
                        .zIndex(30));
            }
            else
            {
                res=getResources().getString(R.string.Tracking_data_is_not_found);
            }
        }
       return res;

    }




    public Polyline drawPolylineWithArrowEndcap(Context context,
                                                GoogleMap googleMap,
                                                LatLng fromLatLng,
                                                LatLng toLatLng) {

        int arrowColor = Color.RED; // change this if you want another color (Color.BLUE)
        int lineColor = Color.RED;

        BitmapDescriptor endCapIcon = getEndCapIcon(arrowColor);

        // have googleMap create the line with the arrow endcap
        // NOTE:  the API will rotate the arrow image in the direction of the line
        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .geodesic(true)
                .color(lineColor)
                .width(8)
              //  .startCap(new RoundCap())
              //  .endCap(new CustomCap(endCapIcon,8))
              //  .jointType(JointType.ROUND)
                .add(fromLatLng, toLatLng));

        return polyline;
    }


    public BitmapDescriptor getEndCapIcon(  int color) {

        // mipmap icon - white arrow, pointing up, with point at center of image
        // you will want to create:  mdpi=24x24, hdpi=36x36, xhdpi=48x48, xxhdpi=72x72, xxxhdpi=96x96
        Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.arrow);

        // set the bounds to the whole image (may not be necessary ...)
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        // overlay (multiply) your color over the white icon
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        // create a bitmap from the drawable
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // render the bitmap on a blank canvas
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        // create a BitmapDescriptor from the new bitmap
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




    public String WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        Integer result=0;
        String res = "Success";
      //  result=Deletedata();
        locationArrayList.clear();
   if(json1.length()>1) {
        String[] urldata = json1.split(";");
            try {
                for (int i = 0; i < urldata.length; i++) {
                    String[] urlValue = urldata[i].split("~");
                if (urlValue.length > 0) {
                    locationArrayList.add(new LatLng(Double.valueOf(urlValue[1]), Double.valueOf(urlValue[0])));
                }
                }
            } catch (Exception ex) {
                res=ex.getMessage();
                Log.d(TAG, ex.getMessage());
                res=     "Error"  + ex.getMessage();
            }
        }

        else
       {
           res=getResources().getString(R.string.Tracking_data_is_not_found);
          // Toast.makeText(getBaseContext(), "Tracking data is not found", Toast.LENGTH_SHORT).show();
       }

        return res;
    }





    public String SaveTasks(String datavalue,String FrmDate,String Todate) {

        String s = "", lang = "", lang1 = "", rest="",json="";
        int ret = 0, res = 0;
        boolean result=false;
        if (EmpID != null && !EmpID.isEmpty() && !EmpID.equals("null"))
        {
            try {

                if (!(urlconnection.checkConnection())) {
                //    PAlertDialog(getResources().getString(R.string.Error), "Internet Connection not found");
                    rest=getResources().getString(R.string.Internet_Connection_not_found);
                    return getResources().getString(R.string.Internet_Connection_not_found);
                }

                lang= Locale.getDefault().getDisplayLanguage();

                if(!lang.equals("English"))
                {
                    FrmDate =urlconnection.arabicToDecimal(FrmDate);
                    Todate=urlconnection.arabicToDecimal(Todate);
                }

                s = "";
                s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/LiveMap/" + "/" + EmpID+"/"+datavalue+"/"+FrmDate+"/"+Todate;
                EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
                json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);
                if (json.contains("`") || json.contains("^"))
                {
                    return ErrorValue(json);
                } else {

                    int lnth = json.length();
                    String json1 = json.substring(1, lnth - 1);
                    rest=   WriteMACAddress(json1);

                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.Connection_with_Server), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                rest= getResources().getString(R.string.Connection_with_Server);
                return  getResources().getString(R.string.Connection_with_Server);
            }


        }
        else {
            Toast.makeText(getBaseContext(),  getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_SHORT).show();
           rest=  getResources().getString(R.string.Connection_with_Server);//"Error:Employee ID did not read Successfully";
            return  getResources().getString(R.string.Connection_with_Server);//"Error:Employee ID did not read Successfully";
        }
       // rest="Success";
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
          //  PAlertDialog(getResources().getString(R.string.Error), "Internet Connection Problem");
            Log.e("", e.getMessage());
        }
        return false;
    }


    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapTracking.this);
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











    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                String datavalue = params[0];
                String FormDate=params[1];// Calls onProgressUpdate()
                String ToDate=params[2];
                resp=   SaveTasks(datavalue,FormDate,ToDate);

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

         //   progressDialog.dismiss();
            rest=result;
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
          //  progressDialog = ProgressDialog.show(MapTracking.this,
            //        "Loading...",
              //      "Please Wait");
            // progressDialog.getWindow().setLayout(200,50);
          //  progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {

          ////   finalResult.setText(text[0]);
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


    //private void showMarker(@NonNull Location currentLocation,GoogleMap googleMap) {
       // LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
       // if (currentLocationMarker == null)
       //     currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng));
       // else
      //      MarkerAnimationHelper.animateMarkerToGB(currentLocationMarker, latLng, LatLngInterpolator.Spherical.class);
    //}

}
