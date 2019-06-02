package com.nordicsemi.nrfUARTv2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.nordicsemi.nrfUARTv2.MainActivity.TAG;

public class RegistrationActivity extends AppCompatActivity {
    public static final String TAG = "Elsmart";
    private EditText edtMessage;
    private EditText edtURL;
    private TextView txtImei,txttitle,txtdept,txtcardvalidfrom,txtcardiisuedfrom,txtcardexpired;
    private Button btnregistration,btnRedirect;
    private Button btnunregistered;
    private TextView txtunregistered;
    private RadioGroup radioSystem;
    private RadioButton Eacs, Elsmart;
    private static final String arabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";
    private static  int Vibrationmode = 0;
    Context context;
    String msg="";
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private static final String  FileMacAddress="Mac.txt";
    private static final String Datafile = "mytextfile.txt";
    final String model = Build.MODEL;
    final String serial = Build.SERIAL;
    static final int READ_BLOCK_SIZE = 100;

    private static String EMPLOYEE_SERVICE_URI;// = "http://93.95.24.25/EmployeService/Service1.svc/GetEmployee/?key=1";
    private static String EMPLOYEE_SERVICE_UR2 = "http://212.12.167.242:6002";
  //  private static String EMPLOYEE_SERVICE_UR2 = "http://etac.electronia.com/Emobile/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1;
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    private static int BLE = 0;
    private static int QRCode = 0;
    private static int Geofence = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnregistration = (Button) findViewById(R.id.btnregistration);
        btnunregistered = (Button) findViewById(R.id.btnunregistered);
        edtMessage = (EditText) findViewById(R.id.editImei);
        txtImei = (TextView) findViewById(R.id.txtimei);
        txttitle = (TextView) findViewById(R.id.txttitle);
        txtdept = (TextView) findViewById(R.id.txtdept);
        txtcardvalidfrom = (TextView) findViewById(R.id.txtcardvalidfrom);
        txtcardiisuedfrom = (TextView) findViewById(R.id.txtcardiisuedfrom);
        txtcardexpired = (TextView) findViewById(R.id.txtcardexpired);
        String ResID="";
        edtURL = (EditText) findViewById(R.id.txturl);
        txtunregistered = (TextView) findViewById(R.id.txtunregistered);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        txtImei.setText(telephonyManager.getDeviceId().toString());
         //EMPLOYEE_SERVICE_URI = "http://93.95.24.25/Emobile/Service1.svc";
        btnRedirect=(Button) findViewById(R.id.btnRedirect);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        if(ReadSysSetting()==0)//check System Setting
            Toast.makeText(this,"Error in Reading setting Table" , Toast.LENGTH_LONG).show();//check_Setting();
        try {
            String s = "";
            StrictMode.setThreadPolicy(policy);
            Intent intent = getIntent();
            ResID = intent.getStringExtra("ResID");

            if (ResID != null && !ResID.isEmpty() && !ResID.equals("null") && ResID.equals("1"))
            {
                btnregistration.setEnabled(true);
            }
            else
            {
                btnregistration.setEnabled(false);
            }

            txtunregistered.setText("");
            addListenerOnButton();
            DispalyData();
        }
        catch(Exception e){
            Toast.makeText(RegistrationActivity.this, "Registration Display Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }


        btnregistration.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String result="",DisplayResult="";
                try {
                    if( Vibrationmode==1 )
                    shakeIt(1, MainActivity.myVirator.sclick);
                    if (checkCondition()) {
                        return;
                    }
                    File file = new File(getFilesDir() + File.separator + fileRegistrationVerify);
                    if (Filecheck()) {
                        try {
                          AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                            builder.setTitle( getResources().getString(R.string.Confirm));
                            builder.setMessage( getResources().getString(R.string.Are_you_sure_to_do_Registration_Again));
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    String res="",DisplayResult="";
                                    try {
                                        res=connectionurlSecData();
                                        if (res.equals("Success")) {
                                            if (ReadSystemValue() == 1) {
                                                if (BLE == 1) {
                                                    if (BLE == 1) {
                                                        res = connectionurl();
                                                        if (!res.equals("Success"))
                                                            DisplayResult = "Ble Registration Failed: " + res + "\n";// PAlertDialog( getResources().getString(R.string.Error),   "Ble Registration Failed: "+result);
                                                    }

                                                    if (Geofence == 1) {
                                                        res = connectionurlGeo();
                                                        if (!res.equals("Success"))
                                                            DisplayResult += "Geofence RegisTration Failed : " + res + "\n"; // PAlertDialog(getResources().getString(R.string.Error), "Geofence RegisTration Failed");
                                                    }

                                                    if (QRCode == 1) {
                                                        res=connectionurlQR();
                                                        if(!res.equals("Success"))
                                                            DisplayResult+="QR Code RegisTration Failed : "+res;
                                                        //  PAlertDialog(getResources().getString(R.string.Error), "QR Code RegisTration Failed");
                                                    }

                                                    if( !DisplayResult.equals("") && !DisplayResult.equals(null))
                                                        PAlertDialog(getResources().getString(R.string.Error), DisplayResult);
                                                }
                                                if (Vibrationmode == 1)
                                                    shakeIt(1, MainActivity.myVirator.sclick);
                                                DispalyData();
                                                btnunregistered.setEnabled(true);
                                                edtMessage.setEnabled(false);
                                                if( DisplayResult.equals("") || DisplayResult.equals(null)) {
                                                    PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_successfully) + "\n\n" + msg);
                                                }
                                                else
                                                    PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.Registration_successfully)+" Partially" + "\n\n" + msg);
                                                btnregistration.setEnabled(false);
                                            }
                                            else
                                            {
                                                PAlertDialog(getResources().getString(R.string.Information), "Error in reading local database");
                                            }
                                        }
                                        else
                                        {
                                            DisplayResult="Local Database Error";
                                            PAlertDialog(getResources().getString(R.string.Error), "Local Database Error: "+res);
                                            return;
                                        }


                                    } catch (Exception e) {
                                        Log.e("Exception",  getResources().getString(R.string.File_write_failed) + e.toString());
                                    }

                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                               result=connectionurlSecData();
                            if(result.equals("Success")) {
                                    if(ReadSystemValue()==1) {
                                        if(BLE==1) {
                                            result=connectionurl();
                                            if(!result.equals("Success"))
                                                DisplayResult="Ble Registration Failed: "+result+"\n";// PAlertDialog( getResources().getString(R.string.Error),   "Ble Registration Failed: "+result);
                                        }
                                        if(Geofence==1) {
                                            result=connectionurlGeo();
                                            if(!result.equals("Success"))
                                                DisplayResult+="Geofence RegisTration Failed : "+result+"\n"; // PAlertDialog(getResources().getString(R.string.Error), "Geofence RegisTration Failed");
                                        }
                                        if(QRCode==1) {
                                            result=connectionurlQR();
                                            if(!result.equals("Success"))
                                                DisplayResult+="QR Code RegisTration Failed : "+result;
                                          //  PAlertDialog(getResources().getString(R.string.Error), "QR Code RegisTration Failed");
                                        }
                                        if( !DisplayResult.equals("") && !DisplayResult.equals(null))
                                        PAlertDialog(getResources().getString(R.string.Error), DisplayResult);
                                    }
                                    else
                                    {
                                        DisplayResult=" Error in reading local database";
                                        PAlertDialog(getResources().getString(R.string.Error), "Local Database Error");
                                   return;
                                    }
                                if( Vibrationmode==1 )
                                    shakeIt(1, MainActivity.myVirator.sclick);
                                DispalyData();
                                btnunregistered.setEnabled(true);
                                edtMessage.setEnabled(false);
                              if( DisplayResult.equals("") || DisplayResult.equals(null))
                                PAlertDialog1( getResources().getString(R.string.Information),  getResources().getString(R.string.Registration_successfully) + "\n\n" + msg);
                              else
                                  PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.Registration_successfully)+" Partially" + "\n\n" + msg);
                                btnregistration.setEnabled(false);
                            }
                            else
                            {
                                PAlertDialog( getResources().getString(R.string.Error),  getResources().getString(R.string.Registration_Failed)+ ": "+result + "\n\n" + msg);
                            }

                        } catch (Exception e) {
                            Log.e("Exception", getResources().getString(R.string.Registration_Failed)  + e.toString());
                            PAlertDialog( getResources().getString(R.string.Error), e.getMessage() );
                        }
                    }
                } catch (Exception e) {
                    String error = "";
                    error = e.getMessage();
                    PAlertDialog( getResources().getString(R.string.Error), e.getMessage() );
                }
            }
        });


        btnunregistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if( Vibrationmode==1 )
                    shakeIt(1, MainActivity.myVirator.sclick);

                    if (checkCondition()) {
                        return;
                    }
                    File Registrationfile = new File(getFilesDir() + File.separator + fileRegistrationVerify);
                    File Valuefile = new File(getFilesDir() + File.separator + Datafile);
                    if (!(Registrationfile.exists())) {

                        Toast.makeText(RegistrationActivity.this,  getResources().getString(R.string.Registration_does_not_exist),
                                Toast.LENGTH_LONG).show();
                        PAlertDialog( getResources().getString(R.string.Error),  getResources().getString(R.string.No_registration_information_found_on_the_device));
                        return;
                    }
                ///    if (!(Valuefile.exists())) {
                     ///   PAlertDialog( getResources().getString(R.string.Error), getResources().getString(R.string.Registration_does_not_exist) );
                     //   return;
                   // }


                    boolean deleted = Registrationfile.delete();
                    boolean deleted1 = Valuefile.delete();
                    if (DeleteRegistration()==1) {
                        DeleteGeo();
                        DeleteQrCode();
                        DeleteLiveTracking();
                        DeleteTasks_Operation();
                        DeleteTasks();
                        Deletesystem_setting();
                        DeleteRegisteredTerminals();
                        DeleteLogs();
                        if(isMyServiceRunning(LocationMonitoringService.class))
                        {
                            stopService(new Intent(RegistrationActivity.this, LocationMonitoringService.class));
                        }
                        if(isMyServiceRunningLocationUpdtae(LocationUpdateservice.class))
                        {
                            stopService(new Intent(RegistrationActivity.this, LocationUpdateservice.class));
                        }
                        btnunregistered.setEnabled(false);
                        txtunregistered.setText(R.string.UnRegistered);
                        txtunregistered.setBackgroundColor(Color.RED);
                        edtMessage.setEnabled(true);
                        PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.Registration_deleted_Successfully));
                        Intent intent = new Intent(RegistrationActivity.this,RegistrationPage.class);
                        startActivity(intent);

                    } else {
//                        Toast.makeText(RegistrationActivity.this, "Your Registration are not  deleted.",
//                                Toast.LENGTH_LONG).show();
                        PAlertDialog( getResources().getString(R.string.Information), getResources().getString(R.string.Registration_not_deleted));

                    }
                } catch (Exception e) {
                    String error = "";
                    error = e.getMessage();
//                    Toast.makeText(RegistrationActivity.this, e.getMessage(),
//                            Toast.LENGTH_LONG).show();
                    PAlertDialog( getResources().getString(R.string.Error), e.getMessage());

                }
            }
        });



        btnRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent Registration = new Intent(RegistrationActivity.this, RegistrationPage.class);
                    startActivity(Registration);
                    }
                 catch (Exception e) {


                }
            }
        });


    }


    public byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0)
            throw new IllegalArgumentException( getResources().getString(R.string.hexBinary_needs_to_be_even_length) + s);

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1)
                throw new IllegalArgumentException( getResources().getString(R.string.contains_illegal_character_for_hexBinary) + s);

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }


    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') return ch - '0';
        if ('A' <= ch && ch <= 'F') return ch - 'A' + 10;
        if ('a' <= ch && ch <= 'f') return ch - 'a' + 10;
        return -1;
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
            Toast.makeText(RegistrationActivity.this, " Network Connection Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }
        return false;
    }


    public String connectionurl() {

        String  s="", lang="",number="",urlar="",res="";
        try {


            String badgeno = edtMessage.getText().toString();
            String imei = txtImei.getText().toString();
            msg = ( getResources().getString(R.string.BadgeNo)+ ": " + badgeno+ "\n" + getResources().getString(R.string.IMEI)+": "  + imei);
            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            //  int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {

                number=badgeno;
                urlar= edtURL.getText().toString();
            }
            else {

                number = arabicToDecimal(badgeno); // number = 42;
                urlar= arabicToDecimal(edtURL.getText().toString());
            }

            if(checkedRadioButtonId==R.id.Eacs)
            {
                 s = urlar + "/ElguardianService/Service1.svc/GetCardholderData/" + "/'" + number + "'/'" + imei + "'/'" + model + "'/'" + serial + "'";
            }
            else
                 s = urlar + "/ElguardianService/Service1.svc/GetCardholderDataElsmart/" + "/'" + number + "'/'" + imei + "'/'" + model + "'/'" + serial + "'";

            EMPLOYEE_SERVICE_URI = s.replace(' ','-');
            URL url = new URL(EMPLOYEE_SERVICE_URI);
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
                res =getstring;
                return  res;
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            int len = json1.length();
            byte[] data1 = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data1[i / 2] = (byte) ((Character.digit(json1.charAt(i), 16) << 4) + Character.digit(json1.charAt(i + 1), 16));
            }
            FileOutputStream fileout = openFileOutput(Datafile, MODE_PRIVATE);
            fileout.write(data1);
            fileout.close();
        } catch (Exception e) {
            e.printStackTrace();
            res=e.getMessage();
            return res;
        }
        return "Success";
    }


    public boolean checkCondition() {
        String str = edtMessage.getText().toString();


        try {

            if (!(checkConnection())) {
//                Toast.makeText(RegistrationActivity.this, "No Internet Connection Found",
//                        Toast.LENGTH_SHORT).show();
                PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.No_Internet_Connection_Found) );
                return true;
            }

            if (str == null || str.isEmpty() || str.equals("")) {
//                Toast.makeText(RegistrationActivity.this, "Badge No should not be blank",
//                        Toast.LENGTH_SHORT).show();
                PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.Please_enter_badge_number) );
                return true;
            }
            str = "";
            str = edtURL.getText().toString();

            if (str == null || str.isEmpty() || str.equals(""))
            {
//                Toast.makeText(RegistrationActivity.this, "Service URL should not be blank",
//                        Toast.LENGTH_SHORT).show();
                PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.Please_enter_service_url) );
                return true;

            }

        } catch (Exception e) {
            String error = "";
            error = e.getMessage();
            PAlertDialog( getResources().getString(R.string.Error), (e.getMessage() +  getResources().getString(R.string.Registration_Failed)));
            return true;
        }
        return false;
    }



    private void shakeIt(int repeat, int duration) {

        if (getSystemService(VIBRATOR_SERVICE) != null) {
            long[] pattern = {0, duration};
            ((Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(pattern, repeat);

        }
    }
    //Use the following alertdialog function to display information which require user aciton (pressing of a button)
    //Only one button case - no specific action required
    private void PAlertDialog(String title, String msgvalue)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setTitle(title);
        builder.setMessage(msgvalue);
        builder.setPositiveButton( getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void PAlertDialog1(String title, String msgvalue)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setTitle(title);
        builder.setMessage(msgvalue);
        builder.setPositiveButton( getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent mainpage = new Intent(RegistrationActivity.this, mainpage.class);
                startActivity(mainpage);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }


    public String connectionurlSecData() {
        String msg = "",urlar="",result="";
        try {
            String badgeno = edtMessage.getText().toString();
            String imei = txtImei.getText().toString();
            String lang="",sys="",   s="",lang1="";
            lang= Locale.getDefault().getDisplayLanguage();
            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            //int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            String number="";
            if(lang.equals("English")) {
                lang1 = "en";
                number=badgeno;
                urlar= arabicToDecimal(edtURL.getText().toString());
            }
            else {
                lang1 = "ar";
                urlar= arabicToDecimal(edtURL.getText().toString());
                 number = arabicToDecimal(badgeno); // number = 42;
            }

            msg = ( getResources().getString(R.string.BadgeNo) +":"+ badgeno +"\n" + getResources().getString(R.string.IMEI) +": " + imei);
            if(checkedRadioButtonId==R.id.Eacs)
            {
                sys="Eacs";
                 s = urlar + "/ElguardianService/Service1.svc/GetAdditionalData/" + "/'" + number + "'/" +lang1;
            }
            else {
                sys = "Elsmart";
                 s = urlar + "/ElguardianService/Service1.svc/GetAdditionalDataElsmart/" + "/'" + number + "'/" +lang1;
            }
            EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            URL url = new URL(EMPLOYEE_SERVICE_URI1);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            Log.d( getResources().getString(R.string.download),  getResources().getString(R.string.Lenght_of_file) + lenghtOfFile);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            InputStream input = new BufferedInputStream(url.openStream());
            byte data[] = new byte[lenghtOfFile];
            int count = -1;
            while ((count = input.read(data)) != -1) {
                buffer.write(data, 0, count);
            }
            input.close();
            String json = new String(buffer.toString());


            if (json.contains("`")) {
                String getstring = json;
                int iend = getstring.indexOf("`");
                if (iend != -1)
                    getstring = json.substring(iend, json.length()); //this will give abc
                return getstring;
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            InsertValue(json1);
            WriteMACAddress(json1);
            FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
            String BadgeIMEI = edtMessage.getText().toString() + "~" + txtImei.getText().toString() + "~" +edtURL.getText().toString() + "~"  + model + "~" + serial+"~"+json1+"~"+EMPLOYEE_SERVICE_URI1+"~"+ sys;
            outputStreamWriter.write(BadgeIMEI);
            outputStreamWriter.close();
           // PAlertDialog( getResources().getString(R.string.Information), "Secondary Data added successfully" + "\n\n" + msg);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Success";

    }


    public void InsertValue(String json1) {

        try {
        String[] urldata = json1.split("~");


        txttitle.setText(urldata[2]);
        txtdept.setText(urldata[1]);
       // txtcardvalidfrom.setText(urldata[3]);
       // txtcardiisuedfrom.setText(urldata[4]);
      //  txtcardexpired.setText(urldata[5]);

            String strvalidfrom="";

        Date date1 = null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            strvalidfrom=urldata[3].replaceAll("[^' ':/\\w\\[\\]]", "");
            date1 = format.parse(strvalidfrom);

            DateFormat df = new SimpleDateFormat("dd MMM yyyy");
            String requiredDate = df.format(date1).toString();
            txtcardvalidfrom.setText(requiredDate.toString());
            date1 = format.parse(urldata[4].replaceAll("[^' ':/\\w\\[\\]]", ""));
            requiredDate = df.format(date1).toString();
            txtcardiisuedfrom.setText(requiredDate.toString());
            date1 = format.parse(urldata[5].replaceAll("[^' ':/\\w\\[\\]]", ""));
            requiredDate = df.format(date1).toString();
            txtcardexpired.setText(requiredDate.toString());


        } catch (ParseException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            Toast.makeText(RegistrationActivity.this, "Error in display Data",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }
    }



    public void cleardata() {
      txttitle.setText("");
        txtdept.setText("");
        txtcardvalidfrom.setText("");
        txtcardiisuedfrom.setText("");
        txtcardexpired.setText("");

    }

    public void addListenerOnButton() {

        radioSystem = (RadioGroup) findViewById(R.id.radioSystem);

        try {
            radioSystem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    //   int selectedId = radioSystem.getCheckedRadioButtonId();
                    File Registrationfile = new File(getFilesDir() + File.separator + fileRegistrationVerify);
                    String[] urldata;

                    if ((Registrationfile.exists())) {
                        String s = regfileValue();

                        urldata = s.split("~");
                        if (urldata.length > 13) {
                            if (i == R.id.Eacs) {

                                if (urldata[26].equals("Elsmart"))

                                    PAlertDialog("Information", "Need to be Re-Register for Eacs System");
                                // Toast.makeText(RegistrationActivity.this,
                                ////    "Need to be Reregistration for Elsmart", Toast.LENGTH_SHORT).show();

                            } else if (i == R.id.Elsmart) {
                                if (urldata[26].equals("Eacs"))
                                    PAlertDialog("Information", "Need to be Re-Register for Elsmart System");
                                // Toast.makeText(RegistrationActivity.this,
                                // "Need to be Reregistration for Eacs", Toast.LENGTH_SHORT).show();
                                ;

                            }
                        }
                    }


                }
            });


        }
        catch(Exception e){

            Log.e("",e.getMessage());
            Toast.makeText(RegistrationActivity.this, "Error in Registration",
                    Toast.LENGTH_SHORT).show();
        }
    }






    private static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for(int i=0;i<number.length();i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }





    private  void DispalyData()
    {
        try {
            String s = "";
            File Registrationfile = new File(getFilesDir() + File.separator + fileRegistrationVerify);

            File Valuefile = new File(getFilesDir() + File.separator + Datafile);
            if ((Registrationfile.exists())) {
                s = regfileValue();
                String[] urldata = s.split("~");
                edtMessage.setText(urldata[0]);/////changed
                txttitle.setText(urldata[7]);
                txtdept.setText(urldata[6]);
                edtURL.setText(urldata[2]);

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = null;
                try {
                    date1 = format.parse(urldata[9].replaceAll("[^' ':/\\w\\[\\]]", ""));
                    String ss = urldata[9].replaceAll("[^' ':/\\w\\[\\]]", "");
                    date1 = format.parse(ss);
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                    String requiredDate = df.format(date1).toString();
                    txtcardvalidfrom.setText(requiredDate.toString());
                    date1 = format.parse(urldata[8].replaceAll("[^' ':/\\w\\[\\]]", ""));
                    requiredDate = df.format(date1).toString();
                    txtcardiisuedfrom.setText(requiredDate.toString());
                    date1 = format.parse(urldata[10].replaceAll("[^' ':/\\w\\[\\]]", ""));
                    requiredDate = df.format(date1).toString();
                    txtcardexpired.setText(requiredDate.toString());


                    if (urldata[26].equals("Elsmart")) {
                        radioSystem.check(R.id.Elsmart);
                    } else {
                        radioSystem.check(R.id.Eacs);
                    }

                    txtunregistered.setText( getResources().getString(R.string.Registered));
                    txtunregistered.setBackgroundColor(Color.GREEN);
                    btnunregistered.setEnabled(true);
                    edtMessage.setEnabled(false);////changed



                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
             else {
                txtunregistered.setText( getResources().getString(R.string.UnRegistered));
                txtunregistered.setBackgroundColor(Color.RED);
                btnunregistered.setEnabled(false);
                edtMessage.setEnabled(true);
                edtURL.setText(EMPLOYEE_SERVICE_UR2);
            }

        }
        catch(Exception e){
            Toast.makeText(RegistrationActivity.this, "Registration data Error", Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }

    }


    public boolean Filecheck() {
        try {
            File file = new File(getFilesDir() + File.separator + fileRegistrationVerify);
            if (file.exists()) {
                FileInputStream fileIn = openFileInput(fileRegistrationVerify);
                if (fileIn != null) {
                    return true;
                }
                else
                    return false;

            } else {
                return false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            PAlertDialog( getResources().getString(R.string.Error), (e.getMessage() + R.string.Registration_Failed));
        } catch (IOException e) {
            PAlertDialog( getResources().getString(R.string.Error), (e.getMessage() + R.string.Registration_Failed));
            e.printStackTrace();
        }
        return true;
    }


    public String regfileValue() {
        String s = "";
        FileInputStream fileIn = null;
        try {
            fileIn = openFileInput(fileRegistrationVerify);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fileIn != null) {
            InputStreamReader InputRead;
            InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];

            int charRead;

            try {
                while ((charRead = InputRead.read(inputBuffer)) > 0) {
                    // char to string conversion
                    String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                    s += readstring;
                }
                InputRead.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            PAlertDialog( getResources().getString(R.string.Information), getResources().getString(R.string.File_Empty) );
        }
        return  s;
    }


    public Integer ReadSysSetting()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  system_setting", null);
            if (cursor.moveToFirst()) {
                do {
                    Vibrationmode =  Integer.valueOf( cursor.getString(cursor.getColumnIndex("Vibration")));
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

    public Integer WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        Integer result=0;
        int res = 0;
        result=Deletedata();
        String[] urlValue = json1.split("~");
        if(result==1) {

            try {
                    if (urlValue.length > 0) {
                        database = db.getWritableDatabase();
                        database.execSQL("INSERT INTO Registration(BadgeNo,DeptName,Title,IssuedDate,ValidFromDate,ExpiryDate,FullName,Employee_Id,Tracking_Type_Id,Company_Id,Tracking,QRCode,Geofence,BLE,UserRole,Interval,MinDist,Cust_Id,Customer_Virtual_Id,GeoQR,url)" +
                                "VALUES('" + urlValue[0] + "','" + urlValue[1] + "','" + urlValue[2] + "','" + urlValue[3].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[4].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[5].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[6] + "'," + urlValue[7] + "," + urlValue[8] + "," + urlValue[9] + "," + urlValue[10] + "," + urlValue[11] + "," + urlValue[12] + "," + urlValue[13]+ "," + urlValue[14]+ ","+urlValue[15]+ ","+urlValue[16]+","+urlValue[17]+","+urlValue[18]+","+urlValue[19]+",'"+edtURL.getText()+ "')");
                        res = 1;

                }
            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
            }
        }
        return res;
    }


    public Integer Deletedata()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Registration" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    public Integer DeleteRegistration()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Registration" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }

    public Integer DeleteLogs()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from tblLogs" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    public Integer DeleteRegisteredTerminals()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Device" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }




    public Integer Deletesystem_setting()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from system_setting" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }
    public Integer DeleteTasks()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Tasks" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }

    public Integer DeleteTasks_Operation()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Tasks_Operation" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    public Integer DeleteLiveTracking()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from LiveTracking" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    public String connectionurlQR() {

        String  s="", lang="",number="",urlar="",result="";
        try {
            String badgeno = edtMessage.getText().toString();
            String imei = txtImei.getText().toString();
            msg = ( getResources().getString(R.string.BadgeNo)+ ": " + badgeno+ "\n" + getResources().getString(R.string.IMEI)+": "  + imei);
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {
                lang = "en";
                number=badgeno;
                urlar= arabicToDecimal(edtURL.getText().toString());
            }
            else {
                lang = "ar";
                urlar= arabicToDecimal(edtURL.getText().toString());
                number = arabicToDecimal(badgeno); // number = 42;
            }
           int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            //  int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {

                number=badgeno;
                urlar= edtURL.getText().toString();
            }
            else {

                number = arabicToDecimal(badgeno); // number = 42;
                urlar= arabicToDecimal(edtURL.getText().toString());
            }

            if(checkedRadioButtonId==R.id.Eacs)
            {
                s = urlar + "/ElguardianService/Service1.svc/GetGeoDataQR/" + "/'" + number+"'"+ "/'" +lang;
            }
            else
                s = urlar + "/ElguardianService/Service1.svc/GetGeoDataQR/" + "/'" + number+"'"+ "/'" +lang;
            EMPLOYEE_SERVICE_URI = s.replace(' ','-');
            URL url = new URL(EMPLOYEE_SERVICE_URI);
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
                result=getstring;
                return result;
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            if(WriteGeofanceDataQR(json1)!=1) {
                result="Error in inserting in Local Database";
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
          //  PAlertDialog("ERROR", e.getMessage());
            result=e.getMessage();
            return result;
        }
        return "Success";

    }


    public Integer WriteGeofanceDataQR(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            String[] Val=Value.split(";");
            database=db.getWritableDatabase();
            // database.execSQL("delete from  Geofence");
            for(int i=0;i<Val.length;i++)
            {
                String[] Val1=Val[i].split("~");
                database=db.getWritableDatabase();
                database.execSQL("INSERT INTO QRCode_Permission(GeoID,CustID,Qrcode,BadgeNo,TerID,QRId,QRCode_Name)VALUES("+Val1[0]+","+Val1[1]+",'"+Val1[2]+"',"+Val1[3]+","+Val1[4]+","+Val1[5]+",'"+Val1[6]+"')" );
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

    public String connectionurlGeo() {

        String  s="", lang="",number="",urlar="",res="";
        try {
            String badgeno = edtMessage.getText().toString();
            String imei = txtImei.getText().toString();
            msg = ( getResources().getString(R.string.BadgeNo)+ ": " + badgeno+ "\n" + getResources().getString(R.string.IMEI)+": "  + imei);

            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {
                lang = "en";
                number=badgeno;
                urlar= arabicToDecimal(edtURL.getText().toString());
            }
            else {
                lang = "ar";
                urlar= arabicToDecimal(edtURL.getText().toString());
                number = arabicToDecimal(badgeno); // number = 42;
            }
            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            //  int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {

                number=badgeno;
                urlar= edtURL.getText().toString();
            }
            else {

                number = arabicToDecimal(badgeno); // number = 42;
                urlar= arabicToDecimal(edtURL.getText().toString());
            }

            if(checkedRadioButtonId==R.id.Eacs)
            {
                s = urlar + "/ElguardianService/Service1.svc/GetGeoData/" + "/'" + number+"'"+ "/'" +lang;
            }
            else
                s = urlar + "/ElguardianService/Service1.svc/GetGeoData/" + "/'" + number+"'"+ "/'" +lang;

            EMPLOYEE_SERVICE_URI = s.replace(' ','-');
            URL url = new URL(EMPLOYEE_SERVICE_URI);
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
                res=getstring;
                return res;
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            if(WriteGeofanceData(json1)!=1) {
                res="Error in inserting in Local Database";
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
        //    PAlertDialog("ERROR", e.getMessage());
            res= e.getMessage();
            return res;
        }
        return "Success";

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
                    Geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
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


    public Integer DeleteGeo()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Geofence" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }

    public Integer DeleteQrCode()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from QRCode_Permission" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

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
        Log.d(TAG, "onDestroy()");
    }






    public String  ConnectionStatus()
    {
        String result="" ,DisplayResult="";
        try {
            result = connectionurlSecData();
            if (result.equals("Success")) {
                if (ReadSystemValue() == 1) {
                    if (BLE == 1) {
                        result = connectionurl();
                        if (!result.equals("Success"))
                            DisplayResult = "Ble Registration Failed: " + result + "\n";// PAlertDialog( getResources().getString(R.string.Error),   "Ble Registration Failed: "+result);
                    }
                    if (Geofence == 1) {
                        result = connectionurlGeo();
                        if (!result.equals("Success"))
                            DisplayResult += "Geofence RegisTration Failed : " + result + "\n"; // PAlertDialog(getResources().getString(R.string.Error), "Geofence RegisTration Failed");
                    }
                    if (QRCode == 1) {
                        result = connectionurlQR();
                        if (!result.equals("Success"))
                            DisplayResult += "QR Code RegisTration Failed : " + result;
                        //  PAlertDialog(getResources().getString(R.string.Error), "QR Code RegisTration Failed");
                    }
                   // if (!DisplayResult.equals("") && !DisplayResult.equals(null))
                      //  DisplayResult=   PAlertDialog(getResources().getString(R.string.Error), DisplayResult);
                } else {
                    DisplayResult = " Error in reading local database";
                   // PAlertDialog(getResources().getString(R.string.Error), "Local Database Error");

                }












                DispalyData();
                btnunregistered.setEnabled(true);
                edtMessage.setEnabled(false);
                if (DisplayResult.equals("") || DisplayResult.equals(null))
                    PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_successfully) + "\n\n" + msg);
                else
                    PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_successfully) + " Partially" + "\n\n" + msg);
                btnregistration.setEnabled(false);
            } else {
                DisplayResult = getResources().getString(R.string.Registration_Failed) + ": " + result + "\n\n" + msg;//  PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.Registration_Failed) + ": " + result + "\n\n" + msg);
            }

        } catch (Exception e) {
            Log.e("Exception", getResources().getString(R.string.Registration_Failed) + e.toString());
            PAlertDialog(getResources().getString(R.string.Error), e.getMessage());
        }

        return DisplayResult;
    }


    public boolean checkLocationUpdateserviceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (com.nordicsemi.nrfUARTv2.LocationUpdateservice.class
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
            if (com.nordicsemi.nrfUARTv2.LocationMonitoringService.class
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



    private boolean isMyServiceRunningLocationUpdtae(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}




