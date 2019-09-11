package com.electronia.mElsmart;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.electronia.mElsmart.Common.DatabaseHelper;
import com.electronia.mElsmart.Common.UrlConnection;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUP";
    private EditText edtBadgeNo, edtURL;
    private Button btnregistration;
    private String IMEI = "000000000000000";
    UrlConnection urlconnection;
    DatabaseHelper dbHelper;
    private RadioGroup radioSystem;
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private static final int PERMISSION_REQUEST_CODE = 1;
    TelephonyManager telephonyManager;
    private static String EMPLOYEE_SERVICE_URI;
    private static final int PERMISSION_REQUEST_CODE_Storage = 1;
    private static final int PERMISSION_REQUEST_CODE_Phone_Read = 3;
    private static final int PERMISSION_REQUEST_CODE_Read_Phone = 2;
    final String model = Build.MODEL;
    final String serial = Build.SERIAL;
    private static int BLE = 0;
    private static int QRCode = 0;
    private static int Geofence = 0, Employee_Id = 0, GeoQR = 0;
    private static String msg = "", BadgeNo = "", url = "";
    private static int Vibrationmode = 0, Count = 0;
    static final int READ_BLOCK_SIZE = 100;
    public RelativeLayout mLayout;
    public int RegisteredValue = 0;
    private static final String Datafile = "mytextfile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnregistration = (Button) findViewById(R.id.btnSignup);
        edtBadgeNo = (EditText) findViewById(R.id.edtBadgeId);
        edtURL = (EditText) findViewById(R.id.edtURL);
        radioSystem = (RadioGroup) findViewById(R.id.radioSystem);
        urlconnection = new UrlConnection(getApplicationContext());
        dbHelper = new DatabaseHelper(getApplicationContext());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        btnregistration.setEnabled(true);

        if (!checkPermission()) {
            requestPermission();
        }
        ///   IMEI = Settings.Secure.getString(this.getContentResolver(),
        ///    Settings.Secure.ANDROID_ID);
        addListenerOnButton();
        mLayout = (RelativeLayout) findViewById(R.id.SignUp_layout);


        // Set a click listener for CoordinatorLayout
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the input method manager
                InputMethodManager inputMethodManager = (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Hide the soft keyboard
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        Count = 0;
        // EditText.TEXT_DIRECTION_RTL
        ///  if( ReadSystemValue() == 1) {
        ///  if (Count > 0) {
        ///    edtBadgeNo.setText(BadgeNo);
        ///  edtURL.setText(url);
        ///  btnregistration.setEnabled(false);
        /// SignUp.AsyncTaskRunner runner = new SignUp.AsyncTaskRunner();
        ///runner.execute();
        /// }
        /// }
        /// else
        /// {
        //   Toast.makeText(this,"Local Database Error",Toast.LENGTH_LONG).show();
        /// }

        if (!AutoUpdationActivity()) {

//   startService(new Intent(this, TestService.class));

        }
        btnregistration.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String result = "", DisplayResult = "", UpdateValue = "", ErrorValue = "", result1 = "", msg1 = "";
                int res = 0;

                RegisteredValue = 0;
                try {

                    ErrorValue = edtBadgeNo.getText().toString();

                    if (ErrorValue == null || ErrorValue.isEmpty() || ErrorValue.equals("null")) {
                        PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.Enter_BadgeNo));
                        return;
                    }
                    ErrorValue = edtURL.getText().toString();

                    if (ErrorValue == null || ErrorValue.isEmpty() || ErrorValue.equals("null")) {
                        PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.Enter_URL));
                        return;
                    }

                    if (!urlconnection.checkConnection()) {
                        PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.No_Internet_Connection_Found));
                        return;
                    }
                    result = connectionurlSecData();
                    if (result.equals("Success")) {

                        dbHelper.dbDeleteGeofence();
                        dbHelper.dbDeleteQR();
                        if (ReadSystemValue() == 1) {
                            if (BLE == 1) {
                                result = connectionurl();
                                if (!result.equals("Success"))
                                    DisplayResult = getResources().getString(R.string.Ble_Registration_Failed) + "\n" + result + "\n";// PAlertDialog( getResources().getString(R.string.Error),   "Ble Registration Failed: "+result);
                            }
                            msg = "";
                            if (Geofence == 1) {

                                result = connectionurlGeo();
                                if (!result.equals("Success")) {
                                    if (result.contains("no_geofence_record_found")) {
                                        msg1 = msg + "\n" + getResources().getString(R.string.no_geofence_record_found);
                                    } else
                                        DisplayResult += getResources().getString(R.string.Geofence_Registration_Failed) + "\n" + result + "\n";
                                }
                                // PAlertDialog(getResources().getString(R.string.Error), "Geofence RegisTration Failed");
                            }
                            if (QRCode == 1) {
                                result = connectionurlQR();
                                if (!result.equals("Success")) {
                                    if (result.contains("no_QR_record_found")) {
                                        if (msg1.contains("IMEI"))
                                            msg1 += "\n" + getResources().getString(R.string.no_QR_record_found);
                                        else
                                            msg1 += msg + "\n" + getResources().getString(R.string.no_QR_record_found);

                                    } else
                                        DisplayResult += getResources().getString(R.string.QR_Code_Registration_failed) + "\n" + result;
                                }
                                //  PAlertDialog(getResources().getString(R.string.Error), "QR Code RegisTration Failed");
                            }

                            result = "";
                            if (GeoQR == 1) {

                                if (QRCode == 0)
                                    result = connectionurlQR();
                                else
                                    result = "hadqr";
                                if (result.equals("Success") || result.equals("hadqr")) {
                                    result = "";
                                    if (Geofence == 0)
                                        result = connectionurlGeo();
                                    else
                                        result = "hadgeo";

                                    if (result.equals("Success") || result.equals("hadgeo")) {
                                    } else {
                                        if (result.contains("no_geofence_record_found")) {

                                            msg1 = msg + "\n" + getResources().getString(R.string.no_geofence_record_found) + "\n";
                                        } else
                                            DisplayResult += getResources().getString(R.string.QR__Code_Geo_Registration_failed) + "\n" + result + "\n";
                                    }
                                } else {
                                    if (result.contains("no_QR_record_found")) {
                                        if (msg1.contains("IMEI"))
                                            msg1 += "\n" + getResources().getString(R.string.no_QR_record_found) + "\n";
                                        else
                                            msg1 += msg + "\n" + getResources().getString(R.string.no_QR_record_found) + "\n";
                                    } else
                                        DisplayResult += getResources().getString(R.string.QR__Code_Geo_Registration_failed) + "\n" + result;
                                }
                            }

                            result = GetEmpPic(Employee_Id);

                            if (!result.equals("Success")) {
                                msg1 += getResources().getString(R.string.Profile_Photo_Error) + "\n" + result + "\n";
                            }

                            result = LeaveDesc();
                            if (!result.equals("Success")) {

                                msg1 += getResources().getString(R.string.Leave_Description_Error) + "\n" + result + "\n";
                            }

                            if (!DisplayResult.equals("") && !DisplayResult.equals(null))
                                PAlertDialog(getResources().getString(R.string.Error), DisplayResult);
                        } else {
                            DisplayResult = getResources().getString(R.string.Error_in_reading_local_database);
                            PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.Local_Database_Error));
                            return;
                        }


                        if (DisplayResult.equals("") || DisplayResult.equals(null)) {
                            if (!msg1.contains("IMEI"))
                                PAlertDialog1(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_successfully) + "\n" + msg + "\n" + msg1);
                            else
                                PAlertDialog1(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_successfully) + "\n" + msg1);
                            UpdateValue = UpdateStatus(31);
                            if (!UpdateValue.equals("Success")) {
                                Toast.makeText(SignUp.this, getResources().getString(R.string.Server_database_update_failed), Toast.LENGTH_LONG).show();
                            }
                        } else
                            PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_Failed) + "\n" + DisplayResult + "\n" + msg1);

                    } else {
                        if (result.contains("Please enter Correct Badge Number")) {
                            PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.Registration_Failed) + ": " + "\n" + getResources().getString(R.string.No_Record_Found));
                        } else {
                            result = result.replace("`", "");
                            result = result.replace("'", "");
                            PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.Registration_Failed) + ": " + "\n" + result + "\n" + DisplayResult + "\n" + msg1);
                        }
                    }

                } catch (Exception e) {
                    Log.e("Exception", getResources().getString(R.string.Registration_Failed) + "\n" + e.toString());
                    PAlertDialog(getResources().getString(R.string.Error), e.getMessage());
                }
            }
        });
    }

    public boolean AutoUpdationActivity() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.electronia.mElsmart.Services.AutoRegistration"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private String Auto_Registration() {
        String result = "", DisplayResult = "";
        RegisteredValue = 1;
        int res = 0;

        try {
            if (!urlconnection.checkConnection()) {
                return "\n" + getResources().getString(R.string.title_alert_no_intenet);
            }
            result = connectionurlSecData();
            if (result.equals("Success")) {
                if (ReadSystemValue() == 1) {
                    if (BLE == 1) {
                        result = connectionurl();
                        if (!result.equals("Success"))
                            DisplayResult = "\n" + getResources().getString(R.string.Ble_Registration_Failed) + result + "\n";// PAlertDialog( getResources().getString(R.string.Error),   "Ble Registration Failed: "+result);
                    }
                    if (Geofence == 1) {
                        result = connectionurlGeo();
                        if (!result.equals("Success")) {
                            if (result.contains("no_geofence_record_found")) {
                                DisplayResult += "\n" + getResources().getString(R.string.no_geofence_record_found) + "\n";
                            } else
                                DisplayResult += getResources().getString(R.string.Geofence_Registration_Failed) + result + "\n"; // PAlertDialog(getResources().getString(R.string.Error), "Geofence RegisTration Failed");
                        }
                    }
                    if (QRCode == 1) {
                        result = connectionurlQR();
                        if (!result.equals("Success")) {
                            if (result.contains("no_QR_record_found")) {
                                DisplayResult += "\n" + getResources().getString(R.string.no_QR_record_found) + "\n";
                            } else
                                DisplayResult += "\n" + getResources().getString(R.string.QR_Code_Registration_failed) + result + "\n";
                        }
                    }

                    result = GetEmpPic(Employee_Id);
                    if (!result.equals("Success")) {
                        DisplayResult += "\n" + getResources().getString(R.string.Profile_Photo_Error) + result + "\n";
                        ;
                    }

                    result = LeaveDesc();
                    if (!result.equals("Success")) {
                        DisplayResult += "\n" + getResources().getString(R.string.Leave_Description_Error) + result + "\n";
                        ;
                    }

                } else {
                    DisplayResult = "\n" + getResources().getString(R.string.Error_in_reading_local_database);// "Error in reading local database";
                    return DisplayResult;
                }
                if (DisplayResult.equals("") || DisplayResult.equals(null))
                    return "";
                else {
                    return DisplayResult;
                }

            } else {
                if (result.equals("Already_Updated")) {
                    DisplayResult = "Already_Updated";
                } else
                    DisplayResult = getResources().getString(R.string.Updating_Failed) + "\n: " + result + "\n";
            }

        } catch (Exception e) {
            Log.e("Exception", getResources().getString(R.string.Registration_Failed) + e.toString());
            DisplayResult = getResources().getString(R.string.Updating_failed_with_some_error);
        }
        return DisplayResult;
    }

    private void PAlertDialog1(String title, String msgvalue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle(title);
        builder.setMessage(msgvalue);
        builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent mainpage = new Intent(SignUp.this, Main2Activity.class);
                startActivity(mainpage);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void PAlertDialog2(String title, String msgvalue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle(title);
        builder.setMessage(msgvalue);
        builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent mainpage = new Intent(SignUp.this, Main2Activity.class);
                startActivity(mainpage);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public String connectionurlQR() {

        String s = "", lang = "", number = "", urlar = "", result = "", json = "";
        try {
            String badgeno = edtBadgeNo.getText().toString();

            msg = (getResources().getString(R.string.BadgeNo) + ": " + badgeno + "\n" + getResources().getString(R.string.IMEI) + ": " + IMEI);
            lang = Locale.getDefault().getDisplayLanguage();

            if (lang.equals("English")) {
                lang = "en";
                number = badgeno;
                urlar = urlconnection.arabicToDecimal(edtURL.getText().toString());
            } else {
                lang = "ar";
                urlar = urlconnection.arabicToDecimal(edtURL.getText().toString());
                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
            }
            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            //  int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            lang = Locale.getDefault().getDisplayLanguage();

            if (lang.equals("English")) {

                number = badgeno;
                urlar = edtURL.getText().toString();
            } else {

                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
                urlar = urlconnection.arabicToDecimal(edtURL.getText().toString());
            }

            if (checkedRadioButtonId == R.id.Eacs) {
                s = urlar + "/ElguardianService/Service1.svc/GetGeoDataQR/" + "/'" + number + "'" + "/'" + lang;
            } else
                s = urlar + "/ElguardianService/Service1.svc/GetGeoDataQR/" + "/'" + number + "'" + "/'" + lang;
            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');

            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^")) {
                return ErrorValue(json);
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            if (WriteGeofanceDataQR(json1) != 1) {
                result = getResources().getString(R.string.Error_in_inserting_local_database);
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            //  PAlertDialog("ERROR", e.getMessage());
            result = e.getMessage();
            return result;
        }
        return "Success";

    }


    public String LeaveDesc() {

        String s = "", lang = "", number = "", urlar = "", result = "", json = "";
        try {
            String badgeno = edtBadgeNo.getText().toString();
            number = badgeno;
            urlar = urlconnection.arabicToDecimal(edtURL.getText().toString());

            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            number = badgeno;
            urlar = edtURL.getText().toString();

            if (checkedRadioButtonId == R.id.Eacs) {
                s = urlar + "/ElguardianService/Service1.svc/LeaveDesc";
            } else
                s = urlar + "/ElguardianService/Service1.svc/LeaveDesc";
            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');

            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^")) {
                return ErrorValue(json);
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            if (WriteLeaveDescription(json1) != 1) {
                result = getResources().getString(R.string.Leave_Description_Error);
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            //  PAlertDialog("ERROR", e.getMessage());
            result = e.getMessage();
            return result;
        }
        return "Success";

    }


    public Integer WriteGeofanceDataQR(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            String[] Val = Value.split(";");
            database = db.getWritableDatabase();
            database.execSQL("delete from  QRCode_Permission");
            for (int i = 0; i < Val.length; i++) {
                String[] Val1 = Val[i].split("~");
                database = db.getWritableDatabase();
                database.execSQL("INSERT INTO QRCode_Permission(GeoID,CustID,Qrcode,BadgeNo,TerID,QRId,QRCode_Name)VALUES(" + Val1[0] + "," + Val1[1] + ",'" + Val1[2] + "'," + Val1[3] + "," + Val1[4] + "," + Val1[5] + ",'" + Val1[6] + "')");
            }

            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return res;
    }

    public Integer WriteLeaveDescription(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            String[] Val = Value.split(";");
            database = db.getWritableDatabase();
            database.execSQL("delete from  LeaveDesc");
            for (int i = 0; i < Val.length; i++) {
                String[] Val1 = Val[i].split("~");
                database = db.getWritableDatabase();
                database.execSQL("INSERT INTO LeaveDesc(LeaveDescID,Description,LeaveCode,OfficialDuty,Description_Ar,LeaveCode_Ar)VALUES(" + Val1[0] + "," + Val1[1] + "," + Val1[2] + "," + Val1[3] + "," + Val1[4] + "," + Val1[5] + ")");
            }

            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return res;
    }


    public String connectionurlGeo() {

        String s = "", lang = "", number = "", urlar = "", res = "", json = "", json1 = "";
        int lnth = 0;
        try {
            String badgeno = edtBadgeNo.getText().toString();//.getText().toString();
            msg = (getResources().getString(R.string.BadgeNo) + ": " + badgeno + "\n" + getResources().getString(R.string.IMEI) + ": " + IMEI);
            lang = Locale.getDefault().getDisplayLanguage();

            if (lang.equals("English")) {
                lang = "en";
                number = badgeno;
                urlar = edtURL.getText().toString();
            } else {
                lang = "ar";
                urlar = urlconnection.arabicToDecimal(edtURL.getText().toString());
                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
            }
            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            lang = Locale.getDefault().getDisplayLanguage();
            if (checkedRadioButtonId == R.id.Eacs) {
                s = urlar + "/ElguardianService/Service1.svc/GetGeoData/" + "/'" + number + "'" + "/'" + lang;
            } else
                s = urlar + "/ElguardianService/Service1.svc/GetGeoData/" + "/'" + number + "'" + "/'" + lang;

            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^")) {

                return ErrorValue(json);
            }
            lnth = json.length();
            json1 = json.substring(1, lnth - 1);
            if (WriteGeofanceData(json1) != 1) {
                res = getResources().getString(R.string.Error_in_inserting_local_database);//"Error in inserting in Local Database";
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = e.getMessage();
            return res;
        }
        return "Success";

    }


    public String ErrorValue(String json) {
        String getstring = "";
        if (json.contains("`")) {
            getstring = json;
            int iend = getstring.indexOf("`");
            if (iend != -1)
                getstring = json.substring(1, json.length() - 1); //this will give abc

        }
        if (json.contains("^")) {
            if (json.contains("Server Offline")) {
                getstring = getResources().getString(R.string.Server_Offline);
            }
            if (json.contains("^within_Time_Out")) {
                getstring = getResources().getString(R.string.Please_wait);
            } else
                getstring = json.substring(1, json.length());


        }
        return getstring;
    }

    public Integer WriteGeofanceData(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            String[] Val = Value.split(";");
            database = db.getWritableDatabase();
            database.execSQL("delete from  Geofence");
            for (int i = 0; i < Val.length; i++) {
                String[] Val1 = Val[i].split("~");
                database = db.getWritableDatabase();
                database.execSQL("INSERT INTO Geofence(GeoID,Lat,Long,Radius,GeoName,KeyName,Badgeno,Shape_Name,Group_Name,Zoom_value)VALUES(" + Val1[0] + "," + Val1[1] + "," + Val1[2] + "," + Val1[3] + ",'" + Val1[4] + "','" + Val1[4] + "','" + Val1[5] + "','" + Val1[6] + "','" + Val1[7] + "','" + Val1[8] + "')");
            }

            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return res;
    }


    public String connectionurl() {

        String s = "", lang = "", number = "", urlar = "", res = "", json = "";
        try {


            String badgeno = edtBadgeNo.getText().toString();

            msg = (getResources().getString(R.string.BadgeNo) + ": " + badgeno + "\n" + getResources().getString(R.string.IMEI) + ": " + IMEI);
            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            //  int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            lang = Locale.getDefault().getDisplayLanguage();

            if (lang.equals("English")) {

                number = badgeno;
                urlar = edtURL.getText().toString();
            } else {

                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
                urlar = urlconnection.arabicToDecimal(edtURL.getText().toString());
            }

            if (checkedRadioButtonId == R.id.Eacs) {
                s = urlar + "/ElguardianService/Service1.svc/GetCardholderData/" + "/'" + number + "'/'" + IMEI + "'/'" + model + "'/'" + serial + "'";
            } else
                s = urlar + "/ElguardianService/Service1.svc/GetCardholderDataElsmart/" + "/'" + number + "'/'" + IMEI + "'/'" + model + "'/'" + serial + "'";

            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^")) {
                return ErrorValue(json);
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
            res = e.getMessage();
            return res;
        }
        return "Success";
    }

    private void PAlertDialog(String title, String msgvalue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
        builder.setTitle(title);
        builder.setMessage(msgvalue);
        builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public boolean Filecheck(String fileRegistrationVerify) {
        try {
            File file = new File(getFilesDir() + File.separator + fileRegistrationVerify);
            if (file.exists()) {
                FileInputStream fileIn = openFileInput(fileRegistrationVerify);
                if (fileIn != null) {
                    return true;
                } else
                    return false;

            } else {
                return false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, (e.getMessage() + "File Error"),
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public String connectionurlSecData() {
        String msg = "", urlar = "", result = "", json = "";
        try {

            String badgeno = edtBadgeNo.getText().toString();
            String lang = "", sys = "", s = "", lang1 = "";
            lang = Locale.getDefault().getDisplayLanguage();
            int checkedRadioButtonId = radioSystem.getCheckedRadioButtonId();
            //int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            String number = "";
            if (lang.equals("English")) {
                lang1 = "en";
                number = badgeno;
                urlar = edtURL.getText().toString();
            } else {
                lang1 = "ar";
                urlar = urlconnection.arabicToDecimal(edtURL.getText().toString());
                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
            }

            msg = (getResources().getString(R.string.BadgeNo) + ":" + badgeno + "\n" + getResources().getString(R.string.IMEI) + ": " + IMEI);
            if (checkedRadioButtonId == R.id.Eacs) {
                sys = "Eacs";
                s = urlar + "/ElguardianService/Service1.svc/GetAdditionalData/" + "/'" + number + "'/" + lang1 + "/" + RegisteredValue;
            } else {
                sys = "Elsmart";
                s = urlar + "/ElguardianService/Service1.svc/GetAdditionalDataElsmart/" + "/'" + number + "'/" + lang1 + "/" + RegisteredValue;
            }
            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');
            URL url = new URL(EMPLOYEE_SERVICE_URI);


            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^")) {
                if (json.contains("Already_Updated"))
                    return "Already_Updated";//getResources().getString(R.string.Already_Updated);
                //   int lnth = json.length();
                //   json = json.substring(1, lnth - 1);
                return ErrorValue(json);
            }

            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            WriteMACAddress(json1);
            FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
            String BadgeIMEI = badgeno + "~" + IMEI + "~" + edtURL.getText().toString() + "~" + model + "~" + serial + "~" + json1 + "~" + EMPLOYEE_SERVICE_URI + "~" + sys;
            outputStreamWriter.write(BadgeIMEI);
            outputStreamWriter.close();
            // PAlertDialog( getResources().getString(R.string.Information), "Secondary Data added successfully" + "\n\n" + msg);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Success";
    }

    public String UpdateStatus(Integer OpeartionStatus) {
        String msg = "", urlar = "", result = "", json = "", s = "";
        try {
            urlar = url;
            s = urlar + "/ElguardianService/Service1.svc/UpdateOperationNo/" + "/'" + BadgeNo + "'/'" + IMEI + "'/'" + model + "'/'" + serial + "'/" + OpeartionStatus;
            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');
            URL url = new URL(EMPLOYEE_SERVICE_URI);
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^")) {
                return ErrorValue(json);
            }
            int lnth = json.length();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Success";
    }

    public String GetEmpPic(int Empid) {

        String urlar = "", result = "", json = "";
        String s = "", res = "";
        InputStream in = null;
        BufferedOutputStream out = null;
        final int IO_BUFFER_SIZE = 64;
        final URL urlObject;
        final URLConnection myConn;
        byte[] data = new byte[8096];
        try {
            urlar = edtURL.getText().toString();
            s = urlar + "/ElguardianService/Service1.svc/" + "/GetImage" + "/" + Empid;
            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');
            data = urlconnection.ServerConnection_Pic(EMPLOYEE_SERVICE_URI);
            if (data.length > 0) {
                if (data[0] == -100)//contains("`") || json.contains("^"))
                {
                    res = getResources().getString(R.string.Please_wait);//"Please wait for defined time and try again for server";
                    return res;
                }
                if (data[0] == -101)//contains("`") || json.contains("^"))
                {
                    res = getResources().getString(R.string.Server_Offline);//"Server Offline";
                    return res;
                }
                if (data[0] == -102)//contains("`") || json.contains("^"))
                {
                    res = getResources().getString(R.string.Other_server_connection_error);//"Other Server Connection Error";
                    return res;
                }
            }
            UpdateLogData(Empid, data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.Network_Connection_Error), Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return res = "Success";

    }

    public Integer UpdateLogData(int ID, byte[] img)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("delete from  Profile_Photo");
            database = db.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("pic", img);

// updating row
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Profile_Photo where Employee_Id = " + ID, null);
            if (cursor.moveToNext()) {
                database.update("Profile_Photo", values, "Employee_Id" + " = ?", new String[]{String.valueOf(ID)});
            } else {
                values.put("Employee_Id", ID);
                database.insert("Profile_Photo", null, values);
                // database.execSQL("update Tasks set pic="+cv+ " where Employee_Id="+ID );
            }
            cursor.close();

            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }

    private void copyCompletely(InputStream input, OutputStream output) throws IOException {

        if ((output instanceof FileOutputStream) && (input instanceof FileInputStream)) {

            try {
                FileChannel target = ((FileOutputStream) output).getChannel();
                FileChannel source = ((FileInputStream) input).getChannel();
                source.transferTo(0, Integer.MAX_VALUE, target);
                source.close();
                target.close();
                return;
            } catch (Exception e) { /* failover to byte stream version */}
        }

        byte[] buf = new byte[8192];

        while (true) {
            int length = input.read(buf);
            if (length < 0) {
                break;
            }
            output.write(buf, 0, length);
        }

        try {
            input.close();
        } catch (IOException ignore) {
        }

        try {
            output.close();
        } catch (IOException ignore) {
        }

    }


    public Integer WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        Integer result = 0;
        int res = 0;
        result = Deletedata();
        String[] urlValue = json1.split("~");
        if (result == 1) {

            try {
                if (urlValue.length > 0) {
                    database = db.getWritableDatabase();
                    database.execSQL("INSERT INTO Registration(BadgeNo,DeptName,Title,IssuedDate,ValidFromDate,ExpiryDate,FullName,Employee_Id,Tracking_Type_Id,Company_Id,Tracking,QRCode,Geofence,BLE,UserRole,Interval,MinDist,Cust_Id,Customer_Virtual_Id,GeoQR,url,IMEI)" +
                            "VALUES('" + urlValue[0] + "','" + urlValue[1] + "','" + urlValue[2] + "','" + urlValue[3].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[4].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[5].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[6] + "'," + urlValue[7] + "," + urlValue[8] + "," + urlValue[9] + "," + urlValue[10] + "," + urlValue[11] + "," + urlValue[12] + "," + urlValue[13] + "," + urlValue[14] + "," + urlValue[15] + "," + urlValue[16] + "," + urlValue[17] + "," + urlValue[18] + "," + urlValue[19] + ",'" + edtURL.getText() + "','" + IMEI + "')");
                    res = 1;

                }
            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
            }
        }
        return res;
    }

    public Integer Deletedata() {
        Integer res = 0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Registration");// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }




    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        if (result1 == PackageManager.PERMISSION_GRANTED) {
            IMEI = IMEI = telephonyManager.getDeviceId();
        }

        return result == PackageManager.PERMISSION_GRANTED;//&& result1 == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean PhoneReadAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StorageAccepted && PhoneReadAccepted) {
                        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                        IMEI = telephonyManager.getDeviceId();
                        Toast.makeText(this,getResources().getString(R.string.Permission_Granted) , Toast.LENGTH_SHORT).show();//.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        if (!StorageAccepted) {
                            Toast.makeText(this, getResources().getString(R.string.Permission_denied_to_read_your_External_storage), Toast.LENGTH_LONG).show();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                    showMessageOKCancel("You need to allow access to Storage  permissions",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_PHONE_STATE},
                                                                PERMISSION_REQUEST_CODE);
                                                    }
                                                }
                                            });
                                    return;
                                }
                            }

                        }

                        if (PhoneReadAccepted) {
                            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                            IMEI = telephonyManager.getDeviceId();


                        }
                        else
                            Toast.makeText(this, getResources().getString(R.string.Permission_Denied_You_cannot_access_Phone_Read), Toast.LENGTH_LONG).show();

                    }
                }


                break;
        }
    }




    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SignUp.this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.OK), okListener)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create()
                .show();
    }


    public Integer InsertTime_Out(int TimeOut,int Connection_Time_Out )//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            database    =db.getWritableDatabase();
            database.execSQL("INSERT OR REPLACE INTO URL_Time_Out( ID,timestamp,time_out,status,Connection_Time_Out) VALUES (1,"+System.currentTimeMillis()+", "+TimeOut +",1,"+Connection_Time_Out+")") ;
       //// URL_Time_Out SET timestamp="+System.currentTimeMillis()+",status="+status);
            res=1;
        }
        catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
           res=0;
        }
        return res;
    }

    public Integer ReadSystemValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode,Employee_Id,BadgeNo,url,GeoQR  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {
                    Count++;
                    BLE = Integer.valueOf(cursor.getString(cursor.getColumnIndex("BLE")));
                    Geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
                    QRCode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRCode")));
                    Employee_Id= Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    BadgeNo = cursor.getString(cursor.getColumnIndex("BadgeNo"));
                    url  =   cursor.getString(cursor.getColumnIndex("url"));
                    GeoQR= Integer.valueOf(cursor.getString(cursor.getColumnIndex("GeoQR")));
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


    public boolean checkLocationUpdateserviceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (com.electronia.mElsmart.LocationUpdateservice.class
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
            if (com.electronia.mElsmart.LocationMonitoringService.class
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
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


                            finishAffinity();//  moveTaskToBack(true); // exist app

                                finish();
                        }
                    })
                    .setNegativeButton(R.string.popup_no, null)
                    .show();

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



    public void addListenerOnButton() {
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

                                    PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.Re_Register_for_Eacs_System));
                                // Toast.makeText(RegistrationActivity.this,
                                ////    "Need to be Reregistration for Elsmart", Toast.LENGTH_SHORT).show();

                            } else if (i == R.id.Elsmart) {
                                if (urldata[26].equals("Eacs"))
                                    PAlertDialog(getResources().getString(R.string.Information),  getResources().getString(R.string.Re_Register_for_Elsmart_System));
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
            Toast.makeText(SignUp.this, getResources().getString(R.string.Registration_Error),
                    Toast.LENGTH_SHORT).show();
        }
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
                            DisplayResult = getResources().getString(R.string.Ble_Registration_Failed) + result + "\n";// PAlertDialog( getResources().getString(R.string.Error),   "Ble Registration Failed: "+result);
                    }
                    if (Geofence == 1) {
                        result = connectionurlGeo();
                        if (!result.equals("Success"))
                            DisplayResult += getResources().getString(R.string.Geofence_Registration_Failed) + result + "\n"; // PAlertDialog(getResources().getString(R.string.Error), "Geofence RegisTration Failed");
                    }
                    if (QRCode == 1) {
                        result = connectionurlQR();
                        if (!result.equals("Success"))
                            DisplayResult += getResources().getString(R.string.QR_Code_Registration_failed) + result;
                        //  PAlertDialog(getResources().getString(R.string.Error), "QR Code RegisTration Failed");
                    }
                    // if (!DisplayResult.equals("") && !DisplayResult.equals(null))
                    //  DisplayResult=   PAlertDialog(getResources().getString(R.string.Error), DisplayResult);
                } else {
                    DisplayResult =getResources().getString(R.string.Error_in_reading_local_database);// " Error in reading local database";
                    // PAlertDialog(getResources().getString(R.string.Error), "Local Database Error");

                }
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()

                try  {
                        resp = Auto_Registration();
                     }
                catch (Exception ex) {
                    Log.d(SignUp.TAG, ex.getMessage());
                    resp = ex.getMessage();
                    runOnUiThread(new Runnable() {
                        public void run() {
                                Toast.makeText(getBaseContext(),resp, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
                 String res=result;
                    if(res.equals("") || res.equals(null) || res.contains("Already_Updated") ) {
                        if(res.contains("Already_Updated"))
                        Toast.makeText(SignUp.this,getResources().getString(R.string.Already_Updated),Toast.LENGTH_LONG).show();
                        else {
                            Toast.makeText(SignUp.this,getResources().getString(R.string.Updated_Successfully) + res, Toast.LENGTH_LONG).show();
                            UpdateStatus(38);
                        }
                        Intent mainpage = new Intent(SignUp.this, Main2Activity.class);
                        startActivity(mainpage);
                    }
                    else
                    {
                        Toast.makeText(SignUp.this,"Updating Failed"+res,Toast.LENGTH_LONG).show();
                        Intent mainpage = new Intent(SignUp.this, Main2Activity.class);
                        startActivity(mainpage);
                    }
                         progressDialog.dismiss();
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SignUp.this,
                    getResources().getString(R.string.Updating_Registration),
                    getResources().getString(R.string.Please_Wait1));
            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
}
