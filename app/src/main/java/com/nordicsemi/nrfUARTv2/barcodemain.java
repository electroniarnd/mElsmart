package com.nordicsemi.nrfUARTv2;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.nordicsemi.nrfUARTv2.barcode.barcodecaptureactivity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class barcodemain extends AppCompatActivity {
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private TextView scanResult;
    private Button scanButton;
    public static final String TAG = "ElsmarQR:MainActivity";
    MediaPlayer in = null,out = null, qrcode=null, error = null,Access_Denied = null,Card_Expired = null,Blacklisted = null,AntiPassBackError = null;
    ArrayList<String> locationArray = new ArrayList<>();
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    TextView txtBadgeNo,txtPunchDate,txtPunchTime,txtLastPunch,txtLogStatus,txtGeoname,QR_CodeName;
    private static String EMPLOYEE_SERVICE_URI="http://212.12.167.242:6002/Service1.svc";
    String building="";
    private Context mContext;
    private Activity mActivity;
    int counter=0, QrCodeId=0;
    int Geoid=0;
    private RelativeLayout mRelativeLayout;
    private Button mButton;
    private static int Tracking = 0;
    private PopupWindow mPopupWindow;
    private static int countQR=0;
    private static int countResult=0;
    String BadgeNo="",QRCODE="",QRCodeName="";
    static int Registration=0;
    static String ServiceURL;
    public static  int BLE = 0;
    public static  int geofence =0; //Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
    public static  int QRCode=0;
    private static int startvalue = 0;
    private static int AutoPunch = 0;
    private static int Biometric = 0;
    private static final int  REQUEST_ENABLE_FT=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodemain);
        scanResult = (TextView) findViewById(R.id.result_textview);

        scanResult = (TextView) findViewById(R.id.result_textview);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        scanButton = (Button) findViewById(R.id.scan_barcode_button);
        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);
        error = MediaPlayer.create(this, R.raw.error);
        qrcode= MediaPlayer.create(this, R.raw.qrcode);
        Card_Expired = MediaPlayer.create(this,R.raw.cardexpired);
        Blacklisted = MediaPlayer.create(this,R.raw.blacklisted);
        AntiPassBackError = MediaPlayer.create(this,R.raw.antipassback);
        Access_Denied = MediaPlayer.create(this, R.raw.accessdenied);
        if (!(ReadTrackingValue() == 0)) {
            Toast.makeText(this,"Error in reading local database" ,Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(this,"Registration not found" ,Toast.LENGTH_LONG).show();
                Intent Registration = new Intent(barcodemain.this, RegistrationPage.class);
                startActivity(Registration);
                return;
            }
        }
        if(QrCode()==1)
        {
            if(countQR==0)
            {
                Toast.makeText(this, "QR Code not found", Toast.LENGTH_LONG).show();
               return;
            }
        }
        else
        {
            Toast.makeText(this,"Error in reading local database",Toast.LENGTH_LONG);
            return;
        }
        if(ReadSysSetting()==0) {//check System Setting
            Toast.makeText(this, "Error in Reading system_setting", Toast.LENGTH_LONG).show();//check_Setting();
            return;
        }

        if ((startvalue == 0) && AutoPunch == 1 && Biometric==1) {

            Intent newIntent = new Intent(barcodemain.this, biometric.class);
            startActivityForResult(newIntent, REQUEST_ENABLE_FT);
        }
        if ((startvalue == 0) && AutoPunch == 1 && Biometric==0) {

            Intent intent = new Intent(getApplicationContext(), barcodecaptureactivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }
        startvalue=1;



        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), barcodecaptureactivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mContext = getApplicationContext();
        // Get the activity
        mActivity = barcodemain.this;
        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
        scanResult.setTextColor(Color.BLACK);


        if(!checkServiceRunningGeoLog()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Intent startServiceIntent = new Intent(barcodemain.this, GeofenceLogService.class);
                    startService(startServiceIntent);
                }
            };
            thread.start();
        }

        if(!checkServiceRunning()) {




            Thread thread = new Thread() {
                @Override
                public void run() {
                    Intent startServiceIntent = new Intent(barcodemain.this, LocationUpdateservice.class);
                    startService(startServiceIntent);
                }
            };
            thread.start();






        }






    }
    private void setSupportActionBar(Toolbar toolbar) {
    }

    public static class myVirator {
        public static int sclick = 200;
        public static int longclick = 500;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case BARCODE_READER_REQUEST_CODE:


                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Barcode barcode = data.getParcelableExtra(barcodecaptureactivity.BarcodeObject);
                        Point[] p = barcode.cornerPoints;
                        // scanResult.setText(barcode.displayValue);
                        ReadQRCode(barcode.displayValue);

                        if (countResult == 1) {
                            int punchtype = ReadLastPunch();
                            if (punchtype == 0)
                                punchtype = 1;
                            else
                                punchtype = 0;

                            scanResult.setText("Punched Successfully");
                            scanResult.setTextColor(Color.GREEN);
                            countResult = 0;
                            barcodemain.AsyncTaskRunner runner1 = new barcodemain.AsyncTaskRunner();
                            runner1.execute(BadgeNo,String.valueOf(punchtype),String.valueOf(Geoid)+"~"+String.valueOf(QrCodeId) );
                           // SaveLog(BadgeNo, punchtype, Geoid, QrCodeId);
                        } else

                        {
                            scanResult.setText("QR Code not Matching");
                            scanResult.setTextColor(Color.RED);//  Toast.makeText(this,"QR Code not Matching",Toast.LENGTH_LONG).show();
                            SoundIt(2);
                        }
                    } else {
                        scanResult.setText("No Result Found");
                        scanResult.setTextColor(Color.RED);
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }

                break;
            case REQUEST_ENABLE_FT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    if (resultCode == CommonStatusCodes.SUCCESS) {
                        if (data != null) {

                            Intent newIntent = new Intent(barcodemain.this, DeviceListActivity.class);
                            startActivityForResult(newIntent, BARCODE_READER_REQUEST_CODE);
                        }
                    }

                }
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.Registration_id:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
            case R.id.Setting_id:
                startActivity(new Intent(this, setting_QR.class));
                break;
            case R.id.About_id:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.Record_id:
                startActivity(new Intent(this, recordLogs.class));
                //  getActivity().onBackPressed();
                break;
            case R.id.Tracking_id:
                    if (Tracking == 1)
                        startActivity(new Intent(this, Tracking.class));
                    else
                        Toast.makeText(this, "not authorized", Toast.LENGTH_LONG).show();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }



        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu1, menu);
        return true;
    }


    public void popup(String BadgeNo,String formattedDate,String formattedDate2, int punchtype,String building,String LogType,String QrCodeName)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.custom_layout_qr,null);
        txtBadgeNo =(TextView) customView.findViewById(R.id.txtBadgeNo);
        txtPunchDate = (TextView) customView.findViewById(R.id.txtPunchDate);
        txtPunchTime = (TextView)customView.findViewById(R.id.txtPunchTime);
        txtLastPunch = (TextView)customView.findViewById(R.id.txtLastPunch);
        txtLogStatus =(TextView)customView.findViewById(R.id.txtLogStatus);
        txtGeoname= (TextView)customView.findViewById(R.id.txtGeoname);
        QR_CodeName=(TextView)customView.findViewById(R.id.txtQRCodeName);
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                android.support.v7.widget.Toolbar.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button

        txtBadgeNo.setText(BadgeNo);
        txtPunchDate.setText(formattedDate);
        txtPunchTime.setText(formattedDate2);
        if(punchtype==0) {
            txtLastPunch.setText("IN");
            if(!isMyServiceRunning(LocationMonitoringService.class)) {
                Intent intent = new Intent(barcodemain.this, LocationMonitoringService.class);
                startService(intent);
            }
        }
        else {
            txtLastPunch.setText("OUT");
            stopService(new Intent(this, LocationMonitoringService.class));
        }
        txtLogStatus.setText(LogType);
        txtGeoname.setText(building);
        QR_CodeName.setText(QrCodeName);
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
        //mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
        mRelativeLayout.post(new Runnable() {
            public void run() {
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
            }
        });

        SoundIt(punchtype);



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

    public void  ClearData()
    {
        txtBadgeNo.setText("");
        txtPunchDate.setText("");
        txtPunchTime.setText("");

        txtLastPunch.setText("");

        txtLogStatus.setText("");
        txtGeoname.setText("");
    }

    private void SoundIt(int index) {
        switch (index) {
            case 1:
                out.start();
                break;
            case 0:
                in.start();
                break;
            case 2:
                qrcode.start();
                break;

        }

    }



    private void PAlertDialog(String title, String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(barcodemain.this);
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



    public Integer WriteLogData(String Badgeno,String dt, String time,int punchtype,int geofenceid,int QrCodeID, int sent)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String Type="QR";
        int res =0;
        try {
            database=db.getWritableDatabase();
            database.execSQL("INSERT INTO tblLogs(BadgeNo,date,time,direction,GeoID,sent,QRId,PunchType,Ter)VALUES('"+BadgeNo+"','"+dt+"','"+time+"',"+punchtype+","+geofenceid+","+sent+","+QrCodeID+",'"+Type+"','"+QRCodeName+"')" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }



    public String SaveLog(String BadgeNo,int punchtype,int geoid,int QRId) {

        String msg = "", s="", lang="",number="",urlar="",rest="Success";;
        Date dt =new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat df1 = new SimpleDateFormat("HH.mm.ss");

        String formattedDate = df.format(dt.getTime());
        String formattedDate1 = df1.format(dt.getTime());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss aa");
        String formattedDate2 = time.format(dt.getTime());

        SimpleDateFormat dfDate = new SimpleDateFormat(" dd MMM yyyy");
        String formatDate = dfDate.format(dt.getTime());

        try {

            if (!(checkConnection())) {
                WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid, QRId,0);

                rest="Internet Connection not found";
               // popup( BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE",QRCodeName);
                return rest+"~"+BadgeNo+"~"+formattedDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~OFFLINE~"+QRCodeName;

            }
            urlar="";
            urlar= ServiceURL+"/ElguardianService/Service1.svc/";
            s ="";
            s = urlar + "/LogSaveQR" + "/'" + BadgeNo+"'" +"/"+formattedDate+"/"+ formattedDate1 +"/"+punchtype+"/"+"/"+geoid+"/"+QRId;
            String  EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
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
              //  PAlertDialog( getResources().getString(R.string.Error), getstring);
                WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid,QRId,0);
                rest=getstring+"~"+BadgeNo+"~"+formattedDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~OFFLINE~"+QRCodeName;
                return rest;
            }
            else
            {
                int ret= WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid,QRId,1);
                if(ret==1) {
                 //   popup(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"ONLINE",QRCodeName);
                }
                else
                    rest="Log did not save Offline"+"~"+BadgeNo+"~"+formattedDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~ONLINE~"+QRCodeName;// Toast.makeText(getBaseContext(), "Error:Log did not save Offline", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getBaseContext(), "Error:Log did not save Offline", Toast.LENGTH_SHORT).show();

            }
            //  int lnth = json.length();
            //  String json1 = json.substring(1, lnth - 1);
        } catch (Exception e) {
            WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid,QRId,0);
           // popup(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE",QRCodeName);
           // e.printStackTrace();
            rest="Error in catch";
            return rest+"~"+BadgeNo+"~"+formattedDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~OFFLINE~"+QRCodeName;
        }
        return rest+"~"+BadgeNo+"~"+formattedDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~ONLINE~"+QRCodeName;

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
        }
        catch(Exception e){
            Toast.makeText(barcodemain.this, " Network Connection Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }
        return false;
    }



    public Integer ReadLastPunch()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0,punchtype=1;
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
            if(punchtype>1)
                punchtype=1;

        }
        catch (Exception ex) {
            punchtype=3;
            Log.d(TAG, ex.getMessage());
        }
        return punchtype;
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
                    Tracking =  Integer.valueOf(cursor.getString(cursor.getColumnIndex("Tracking")));
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
    public Integer QrCode()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue="";
        int res =0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  QRCode_Permission", null);
            if (cursor.moveToFirst()) {
                do {
                    countQR++;

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
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                String value=params[2];
                String[] GetValue =value.split("~");
                resp=  SaveLog(params[0],Integer.valueOf(params[1]) ,Integer.valueOf(GetValue[0]),Integer.valueOf(GetValue[1]));
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(!resp.contains("Success")) {
                            if(resp.contains("Internet Connection not found"))
                                Toast.makeText(getBaseContext(), "Log Saved OffLine", Toast.LENGTH_SHORT).show();
                        }
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
            /// String result=text[0];
            String[] resultvalue=result.split("~");
            if(resultvalue.length>0) {
                if (resultvalue[0].equals("Success")) {
                    popup(resultvalue[1], resultvalue[2], resultvalue[3],Integer.valueOf(resultvalue[4]), resultvalue[5], "ONLINE",resultvalue[7]);
                }
                else if(resultvalue[0].equals("Log did not save Offline"))
                {
                    popup(resultvalue[1], resultvalue[2], resultvalue[3],Integer.valueOf(resultvalue[4]), resultvalue[5], "ONLINE",resultvalue[7]);
                }

                else
                    popup(resultvalue[1], resultvalue[2], resultvalue[3],Integer.valueOf(resultvalue[4]), resultvalue[5], "OFFLINE",resultvalue[7]);

            }

            progressDialog.dismiss();
        }





        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(barcodemain.this,
                    "Punching...",
                    "Please Wait");

            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    @Override
    public void onBackPressed() {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_message)
                    .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (ReadSystemValue() == 1) {
                                if (BLE == 0 && QRCode == 1 && geofence == 0) {

                                    barcodemain.this.moveTaskToBack(true);
                                }
                                else
                                    finish();
                            }
                         //
                        }
                    })
                    .setNegativeButton(R.string.popup_no, null)
                    .show();
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

                    AutoPunch= Integer.valueOf( cursor.getString(cursor.getColumnIndex("AutoPunchQR")));
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

    public boolean checkServiceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("com.nordicsemi.nrfUARTv2.LocationUpdateservice"
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
            if ("com.nordicsemi.nrfUARTv2.LocationMonitoringService"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
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


    public boolean checkServiceRunningGeoLog(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("com.nordicsemi.nrfUARTv2.GeofenceLogService"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

}

