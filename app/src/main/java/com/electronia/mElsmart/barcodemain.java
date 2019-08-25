package com.electronia.mElsmart;

import android.app.ActivityManager;
import android.app.AlertDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.gms.common.api.CommonStatusCodes;
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
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class barcodemain extends Fragment {
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private TextView scanResult;
    private Button scanButton;
    public static final String TAG = "ElsmarQR:MainActivity";
    MediaPlayer in = null,out = null, qrcode=null, error = null,Access_Denied = null,Card_Expired = null,Blacklisted = null,AntiPassBackError = null;
    ArrayList<String> locationArray = new ArrayList<>();
    Controllerdb db;
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
    private static View view;
    private static FragmentManager fragmentManager;
    UrlConnection urlconnection;

    public static barcodemain newInstance() {
        barcodemain fragment = new barcodemain();
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


        scanResult = (TextView) view.findViewById(R.id.result_textview);

        scanResult = (TextView) view.findViewById(R.id.result_textview);
        db = new Controllerdb(getContext());
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        scanButton = (Button) view.findViewById(R.id.scan_barcode_button);
        in = MediaPlayer.create(getActivity(), R.raw.in);
        out = MediaPlayer.create(getActivity(), R.raw.out);
        error = MediaPlayer.create(getActivity(), R.raw.error);
        qrcode = MediaPlayer.create(getActivity(), R.raw.qrcode);
        Card_Expired = MediaPlayer.create(getActivity(), R.raw.cardexpired);
        Blacklisted = MediaPlayer.create(getActivity(), R.raw.blacklisted);
        AntiPassBackError = MediaPlayer.create(getActivity(), R.raw.antipassback);
        Access_Denied = MediaPlayer.create(getActivity(), R.raw.accessdenied);
        urlconnection = new UrlConnection(getActivity().getApplicationContext());
        if (!(ReadTrackingValue() == 0)) {
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
        if (QrCode() == 1) {
            if (countQR == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.QR_Code_not_found), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG);
            return;
        }
        if (ReadSysSetting() == 0) {//check System Setting
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();//check_Setting();
            return;
        }

        if ((startvalue == 0) && AutoPunch == 1 && Biometric == 1) {

            Intent newIntent = new Intent(getActivity(), biometric.class);
            startActivityForResult(newIntent, REQUEST_ENABLE_FT);
        }
        if ((startvalue == 0) && AutoPunch == 1 && Biometric == 0) {

            Intent intent = new Intent(getActivity().getApplicationContext(), barcodecaptureactivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }
        startvalue = 1;


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), barcodecaptureactivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mContext = getActivity().getApplicationContext();
        // Get the activity
        mActivity = getActivity();
        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rl);
        scanResult.setTextColor(Color.BLACK);


        if (!checkServiceRunningGeoLog()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    getActivity().startService(new Intent(getActivity(), GeofenceLogService.class));
                }
            };
            thread.start();
        }

        if (!checkServiceRunning()) {


            Thread thread = new Thread() {
                @Override
                public void run() {
                    getActivity().startService(new Intent(getActivity(), LocationUpdateservice.class));
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
                            int punchtype = ReadLastPunch();
                            if (punchtype == 0)
                                punchtype = 1;
                            else
                                punchtype = 0;

                            scanResult.setText( getResources().getString(R.string.Punched_Successfully));
                            scanResult.setTextColor(Color.GREEN);
                            countResult = 0;
                            barcodemain.AsyncTaskRunner runner1 = new barcodemain.AsyncTaskRunner();
                            runner1.execute(BadgeNo,String.valueOf(punchtype),String.valueOf(Geoid)+"~"+String.valueOf(QrCodeId) );
                           // SaveLog(BadgeNo, punchtype, Geoid, QrCodeId);
                        } else

                        {
                            scanResult.setText(getResources().getString(R.string.QR_Code_not_matching));
                            scanResult.setTextColor(Color.RED);//  Toast.makeText(this,"QR Code not Matching",Toast.LENGTH_LONG).show();
                            SoundIt(2);
                        }
                    } else {
                        scanResult.setText(getResources().getString(R.string.No_Result_Found));//"No Result Found");
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

                            Intent newIntent = new Intent(getActivity(), DeviceListActivity.class);
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
                startActivity(new Intent(getActivity(), RegistrationActivity.class));
                break;
            case R.id.Setting_id:
                startActivity(new Intent(getActivity(), setting_QR.class));
                break;
            case R.id.About_id:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.Record_id:
                startActivity(new Intent(getActivity(), recordLogs.class));
                //  getActivity().onBackPressed();
                break;
            case R.id.Tracking_id:
                    if (Tracking == 1)
                        startActivity(new Intent(getActivity(), Tracking.class));
                    else
                        Toast.makeText(getActivity(), "not authorized", Toast.LENGTH_LONG).show();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu1, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT
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
        if(punchtype==1) {
            txtLastPunch.setText(getResources().getString(R.string.IN));
            if(!isMyServiceRunning(LocationMonitoringService.class)) {
                getActivity().startService(new Intent(getActivity(), LocationMonitoringService.class));
            }
        }
        else {
            txtLastPunch.setText(getResources().getString(R.string.OUT));
            getActivity().stopService(new Intent(getActivity(), LocationMonitoringService.class));
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
            case 0:
                out.start();
                break;
            case 1:
                in.start();
                break;
            case 2:
                qrcode.start();
                break;

        }

    }



    private void PAlertDialog(String title, String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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



    public Integer WriteLogData(String Badgeno,String dt, String time,int punchtype,int geofenceid,int QrCodeID, int sent,String formattedDate,String formattedDate1)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String Type="QR";
        int res =0;
        try {
            database=db.getWritableDatabase();
            database.execSQL("INSERT INTO tblLogs(BadgeNo,date,time,direction,GeoID,sent,QRId,PunchType,Ter,datetosent,timetosent)VALUES('"+BadgeNo+"','"+dt+"','"+time+"',"+punchtype+","+geofenceid+","+sent+","+QrCodeID+",'"+Type+"','"+QRCodeName+"','"+formattedDate+"','"+formattedDate1+"')" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }



    public String SaveLog(String BadgeNo,int punchtype,int geoid,int QRId) {

        String msg = "", s="", lang="",number="",urlar="",rest="Success",json="";
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
            lang= Locale.getDefault().getDisplayLanguage();
            if(!lang.equals("English"))
            {
                formatDate =urlconnection.arabicToDecimal(formatDate);
                formattedDate2=urlconnection.arabicToDecimal(formattedDate2);
            }
            if (!(urlconnection.checkConnection())) {
                WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid, QRId,0,formattedDate,formattedDate1);

                rest="Internet_connection_not_found";
               // popup( BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE",QRCodeName);
                return rest+"~"+BadgeNo+"~"+formatDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~OFFLINE~"+QRCodeName;

            }
            urlar="";
            urlar= ServiceURL+"/ElguardianService/Service1.svc/";
            s ="";
            s = urlar + "/LogSaveQR" + "/'" + BadgeNo+"'" +"/"+formattedDate+"/"+ formattedDate1 +"/"+punchtype+"/"+"/"+geoid+"/"+QRId;
            String  EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI1);


            if (json.contains("`") || json.contains("^"))
             {
                String getstring = json;
                int iend = getstring.indexOf("`");

                if (iend != -1)
                    getstring = json.substring(iend, json.length()); //this will give abc
              //  PAlertDialog( getResources().getString(R.string.Error), getstring);
                WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid,QRId,0,formattedDate,formattedDate1);
                rest=getstring+"~"+BadgeNo+"~"+formatDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~OFFLINE~"+QRCodeName;
                return rest;
            }
            else
            {
                int ret= WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid,QRId,1,formattedDate,formattedDate1);
                if(ret==1) {
                 //   popup(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"ONLINE",QRCodeName);
                }
                else
                    rest="Log_did_not_save_offline"+"~"+BadgeNo+"~"+formatDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~"+getResources().getString(R.string.ONLINE)+"~"+QRCodeName;// Toast.makeText(getBaseContext(), "Error:", Toast.LENGTH_SHORT).show();


            }
            //  int lnth = json.length();
            //  String json1 = json.substring(1, lnth - 1);
        } catch (Exception e) {
            WriteLogData( BadgeNo, formatDate,formattedDate2, punchtype, geoid,QRId,0,formattedDate,formattedDate1);
           // popup(BadgeNo, formattedDate, formattedDate2,  punchtype, building,"OFFLINE",QRCodeName);
           // e.printStackTrace();
            rest=e.getMessage();
            return rest+"~"+BadgeNo+"~"+formatDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~"+getResources().getString(R.string.OFFLINE)+"~"+QRCodeName;
        }
        return rest+"~"+BadgeNo+"~"+formatDate+"~"+formattedDate2+"~"+punchtype+"~"+building+"~"+getResources().getString(R.string.ONLINE)+"~"+QRCodeName;

    }


    public boolean checkConnection() {

        try {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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
            Toast.makeText(getActivity(), getResources().getString(R.string.Network_Connection_Error),
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
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if(!resp.contains("Success")) {
                            if(resp.contains("Internet_connection_not_found"))
                                Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.Log_Saved_OffLine), Toast.LENGTH_SHORT).show();
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
                else if(resultvalue[0].equals("Log_did_not_save_offline"))
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
            progressDialog = ProgressDialog.show(getActivity(),
                    "Punching...",
                    "Please Wait");

            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
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
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
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
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
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
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public boolean checkServiceRunningGeoLog(){
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
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

