package com.electronia.mElsmart;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompatSideChannelService;
//import androidx.appcompat.app.ActionBarActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

public class RegistrationActivity extends Fragment {
    public static final String TAG = "Elsmart";
    private TextView txtImei, txttitle, txtdept, txtcardvalidfrom, txtcardiisuedfrom, txtcardexpired,edtURL,edtMessage;
    private Button btnregistration, btnRedirect;
    private Button btnunregistered;
    private TextView txtunregistered;
    private RadioGroup radioSystem;
    private RadioButton Eacs, Elsmart;
    private static final String arabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";
    private static int Vibrationmode = 0;
    Context context;
    String msg = "";
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private static final String FileMacAddress = "Mac.txt";
    private static final String Datafile = "mytextfile.txt";
    final String model = Build.MODEL;
    final String serial = Build.SERIAL;
    static final int READ_BLOCK_SIZE = 100;
    private static String EMPLOYEE_SERVICE_UR2 = "http://212.12.167.242:6002";
    Controllerdb db;// = new Controllerdb(this);
    SQLiteDatabase database;
    private static int BLE = 0;
    private static int QRCode = 0;
    private static int Geofence = 0;
    private static View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_registration, container, false);
        return view;
    }
    public static RegistrationActivity newInstance() {
        RegistrationActivity fragment = new RegistrationActivity();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        db = new Controllerdb(getContext());

        btnunregistered = (Button) view.findViewById(R.id.btnunregistered);
        edtMessage = (TextView) view.findViewById(R.id.editImei);
        txtImei = (TextView) view.findViewById(R.id.txtimei);
        txttitle = (TextView) view.findViewById(R.id.txttitle);
        txtdept = (TextView) view.findViewById(R.id.txtdept);
        txtcardvalidfrom = (TextView) view.findViewById(R.id.txtcardvalidfrom);
        txtcardiisuedfrom = (TextView) view.findViewById(R.id.txtcardiisuedfrom);
        txtcardexpired = (TextView) view.findViewById(R.id.txtcardexpired);
        edtURL = (TextView) view.findViewById(R.id.txturl);
        txtunregistered = (TextView) view.findViewById(R.id.txtunregistered);
        radioSystem = (RadioGroup) view.findViewById(R.id.radioSystem);
        DispalyData();

        btnunregistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (Vibrationmode == 1)
                        shakeIt(1, MainActivity.myVirator.sclick);

                    if (checkCondition()) {
                        return;
                    }
                    File Registrationfile = new File(getActivity().getFilesDir() + File.separator + fileRegistrationVerify);
                    File Valuefile = new File(getActivity().getFilesDir() + File.separator + Datafile);
                    if (!(Registrationfile.exists())) {

                        Toast.makeText(getActivity(), getResources().getString(R.string.Registration_does_not_exist),
                                Toast.LENGTH_LONG).show();
                        PAlertDialog(getResources().getString(R.string.Error), getResources().getString(R.string.No_registration_information_found_on_the_device));
                        return;
                    }


                    boolean deleted = Registrationfile.delete();
                    boolean deleted1 = Valuefile.delete();
                    if (DeleteRegistration() == 1) {
                        DeleteGeo();
                        DeleteQrCode();
                        DeleteLiveTracking();
                        DeleteTasks_Operation();
                        DeleteTasks();
                        Deletesystem_setting();
                        DeleteRegisteredTerminals();
                        DeleteLogs();
                        DeleteTasks_TimeOut();
                        if (isMyServiceRunning(LocationMonitoringService.class)) {
                            getActivity().stopService(new Intent(getActivity(), LocationMonitoringService.class));
                        }
                        if (isMyServiceRunningLocationUpdtae(LocationUpdateservice.class)) {
                            getActivity().stopService(new Intent(getActivity(), LocationUpdateservice.class));
                        }
                        btnunregistered.setEnabled(false);
                        txtunregistered.setText(R.string.UnRegistered);
                        txtunregistered.setBackgroundColor(Color.RED);

                        PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_deleted_Successfully));
                        Intent intent = new Intent(getActivity(), SignUp.class);
                        startActivity(intent);

                    } else {
//                        Toast.makeText(RegistrationActivity.this, "Your Registration are not  deleted.",
//                                Toast.LENGTH_LONG).show();
                        PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.Registration_not_deleted));

                    }
                } catch (Exception e) {
                    String error = "";
                    error = e.getMessage();
//                    Toast.makeText(RegistrationActivity.this, e.getMessage(),
//                            Toast.LENGTH_LONG).show();
                    PAlertDialog(getResources().getString(R.string.Error), e.getMessage());

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
            Toast.makeText(getActivity(), " Network Connection Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }
        return false;
    }




    public boolean checkCondition() {
        String str = edtMessage.getText().toString();


        try {



            if (str == null || str.isEmpty() || str.equals("")) {
//                Toast.makeText(RegistrationActivity.this, "Badge No should not be blank",
//                        Toast.LENGTH_SHORT).show();
                PAlertDialog( getResources().getString(R.string.Information),  "Badge Number is not Found" );
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

        if (getActivity().getSystemService(VIBRATOR_SERVICE) != null) {
            long[] pattern = {0, duration};
            ((Vibrator) getActivity().getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(pattern, repeat);

        }
    }
    //Use the following alertdialog function to display information which require user aciton (pressing of a button)
    //Only one button case - no specific action required
    private void PAlertDialog(String title, String msgvalue)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            Toast.makeText(getActivity(), "Error in display Data",
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
            File Registrationfile = new File(getActivity().getFilesDir() + File.separator + fileRegistrationVerify);

            File Valuefile = new File(getActivity().getFilesDir() + File.separator + Datafile);
            if ((Registrationfile.exists())) {
                s = regfileValue();
                String[] urldata = s.split("~");
                edtMessage.setText(urldata[0]);/////changed
                txttitle.setText(urldata[7]);
                txtdept.setText(urldata[6]);
                edtURL.setText(urldata[2]);
                txtImei.setText(urldata[1]);
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




                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
             else {
                txtunregistered.setText( getResources().getString(R.string.UnRegistered));
                txtunregistered.setBackgroundColor(Color.RED);


                edtURL.setText(EMPLOYEE_SERVICE_UR2);
            }

        }
        catch(Exception e){
            Toast.makeText(getActivity(), "Registration data Error", Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }

    }


    public boolean Filecheck() {
        try {
            File file = new File(getActivity().getFilesDir() + File.separator + fileRegistrationVerify);
            if (file.exists()) {
                FileInputStream fileIn = getActivity().openFileInput(fileRegistrationVerify);
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
        }
        return true;
    }


    public String regfileValue() {
        String s = "";
        FileInputStream fileIn = null;
        try {
            fileIn = getActivity().openFileInput(fileRegistrationVerify);
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

    public Integer DeleteTasks_TimeOut()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Connection_Time_Out" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
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
    public void onStart() {
        super.onStart();
        Log.d(this.getClass().getSimpleName() , "onStart()");
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
        Log.d(TAG, "onDestroy");
    }







    public boolean checkLocationUpdateserviceRunning(){
        ActivityManager manager = (ActivityManager)
                getActivity().getSystemService(ACTIVITY_SERVICE);
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


    public boolean checkLocationMonitoringServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (com.electronia.mElsmart.LocationMonitoringService.class
                    .equals(service.service.getClassName())) {
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

    private boolean isMyServiceRunningLocationUpdtae(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}




