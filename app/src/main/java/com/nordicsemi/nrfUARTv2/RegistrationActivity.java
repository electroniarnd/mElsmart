package com.nordicsemi.nrfUARTv2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

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

public class RegistrationActivity extends ActionBarActivity {

    private EditText edtMessage;
    private EditText edtURL;
    private TextView txtImei,txttitle,txtdept,txtcardvalidfrom,txtcardiisuedfrom,txtcardexpired;
    private Button btnregistration;
    private Button btnunregistered;
    private TextView txtunregistered;
    private RadioGroup radioSystem;
    private RadioButton Eacs, Elsmart;
    private static final String arabic = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";
    private static  int Vibrationmode = 0;
    Context context;
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private static final String Datafile = "mytextfile.txt";
    final String model = Build.MODEL;
    final String serial = Build.SERIAL;
    static final int READ_BLOCK_SIZE = 100;

    private static String EMPLOYEE_SERVICE_URI;// = "http://93.95.24.25/EmployeService/Service1.svc/GetEmployee/?key=1";
    private static String EMPLOYEE_SERVICE_UR2 = "http://212.12.167.242:6003/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1;

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


        edtURL = (EditText) findViewById(R.id.txturl);
        txtunregistered = (TextView) findViewById(R.id.txtunregistered);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        txtImei.setText(telephonyManager.getDeviceId().toString());
         //EMPLOYEE_SERVICE_URI = "http://93.95.24.25/Emobile/Service1.svc";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        try {
            String s = "";
            StrictMode.setThreadPolicy(policy);
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


                try {
                    shakeIt(1, MainActivity.myVirator.sclick);
                    if (checkCondition()) {
                        return;
                    }

                    File file = new File(getFilesDir() + File.separator + fileRegistrationVerify);


                    if (Filecheck()) {

                        try {
                            //   Toast.makeText(this, "", Toast.LENGTH_LONG).show();


                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                            builder.setTitle( getResources().getString(R.string.Confirm));
                            builder.setMessage( getResources().getString(R.string.Are_you_sure_to_do_Registration_Again));
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        if (connectionurl()  && connectionurlSecData()  ) {
                                            shakeIt(1, MainActivity.myVirator.sclick);



                                   ///////////  FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
                                            // OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);


                                         //////////  OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
                                     ///////////  String BadgeIMEI = edtMessage.getText().toString() + "," + txtImei.getText().toString() + "," + EMPLOYEE_SERVICE_URI + "/GetCardholderData/" + "/'" + edtMessage.getText().toString() + "'/'" + txtImei.getText().toString() + "'/'" + model + "'/'" + serial + "'," + txtImei.getText().toString() + "," + model + "," + serial;
                                        ////////////////  outputStreamWriter.write(BadgeIMEI);
                                      /////////////////  outputStreamWriter.close();



                                            DispalyData();

                                            btnunregistered.setEnabled(true);
                                            edtMessage.setEnabled(false);

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

                            if (connectionurl() && connectionurlSecData() ) {

                            //////////////////   FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
                                // OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);


                              /////////////  OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
                               //////////////////// String BadgeIMEI = edtMessage.getText().toString() + "," + txtImei.getText().toString() + "," + EMPLOYEE_SERVICE_URI + "/GetCardholderData/" + "/'" + edtMessage.getText().toString() + "'/'" + txtImei.getText().toString() + "'";

                            //////////////  outputStreamWriter.write(BadgeIMEI);
                             ////////////////  outputStreamWriter.close();

                                DispalyData();
                                btnunregistered.setEnabled(true);
                            }

                        } catch (Exception e) {
                            Log.e("Exception", "File write failed: " + e.toString());
                            PAlertDialog( getResources().getString(R.string.Error), e.getMessage() );
                        }
                    }
                } catch (Exception e) {
                    String error = "";
                    error = e.getMessage();
//                    Toast.makeText(RegistrationActivity.this, e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
                    PAlertDialog( getResources().getString(R.string.Error), e.getMessage() );

                }
            }
        });


        btnunregistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    shakeIt(1, MainActivity.myVirator.sclick);

                    if (checkCondition()) {
                        return;
                    }

                   // String number = arabicToDecimal("۴۲"); // number = 42;
                    //Toast.makeText(RegistrationActivity.this, number, Toast.LENGTH_LONG).show();
                    File Registrationfile = new File(getFilesDir() + File.separator + fileRegistrationVerify);

                    File Valuefile = new File(getFilesDir() + File.separator + Datafile);
                    if (!(Registrationfile.exists())) {

                        Toast.makeText(RegistrationActivity.this,  getResources().getString(R.string.Registration_does_not_exist),
                                Toast.LENGTH_LONG).show();
                        PAlertDialog( getResources().getString(R.string.Error),  getResources().getString(R.string.No_registration_information_found_on_the_device));
                        return;
                    }
                    if (!(Valuefile.exists())) {

//                        Toast.makeText(RegistrationActivity.this, "Registration Data file does not exist",
//                                Toast.LENGTH_LONG).show();
                        PAlertDialog( getResources().getString(R.string.Error), getResources().getString(R.string.Registration_does_not_exist) );

                        return;
                    }


                    boolean deleted = Registrationfile.delete();
                    boolean deleted1 = Valuefile.delete();
                    if (deleted && deleted1) {
//                        Toast.makeText(RegistrationActivity.this, "Your Registration  are deleted Successfully",
//                                Toast.LENGTH_LONG).show();
                        btnunregistered.setEnabled(false);
                        edtMessage.setEnabled(true);
                        PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.Registration_deleted_Successfully));

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


    public boolean connectionurl() {

        String msg = "", s="", lang="",number="",urlar="";
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
                 s = urlar + "/GetCardholderData/" + "/'" + number + "'/'" + imei + "'/'" + model + "'/'" + serial + "'";
            }
            else
                 s = urlar + "/GetCardholderDataElsmart/" + "/'" + number + "'/'" + imei + "'/'" + model + "'/'" + serial + "'";

            EMPLOYEE_SERVICE_URI = s.replace(' ','-');

            URL url = new URL(EMPLOYEE_SERVICE_URI);

            URLConnection conexion = url.openConnection();
            conexion.connect();

          //  int lenghtOfFile = conexion.getContentLength();



            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            InputStream input = new BufferedInputStream(url.openStream());


          //  byte data[] = new byte[lenghtOfFile];



      int b=-1;
         while ((b = input.read()) != -1)
           buffer.write(b);

           // int count = -1;

          // while ((count = input.read(data)) != -1) {
             //   buffer.write(data, 0, count);
          //  }

            input.close();

            String json = new String(buffer.toString());
            Log.d( getResources().getString(R.string.download), getResources().getString(R.string.Lenght_of_file) + json.length());

            if (json.contains("`")) {
                String getstring = json;
                int iend = getstring.indexOf("`");

                if (iend != -1)
                    getstring = json.substring(iend, json.length()); //this will give abc


//                Toast.makeText(getBaseContext(), getstring,
//                        Toast.LENGTH_SHORT).show();
                PAlertDialog( getResources().getString(R.string.Error), getstring);

                return false;
            }


            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);

            int len = json1.length();
            byte[] data1 = new byte[len / 2];

            for (int i = 0; i < len; i += 2) {
                data1[i / 2] = (byte) ((Character.digit(json1.charAt(i), 16) << 4) + Character.digit(json1.charAt(i + 1), 16));
            }


            FileOutputStream fileout = openFileOutput(Datafile, MODE_PRIVATE);
            // OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);


            fileout.write(data1);
            fileout.close();
            //outputWriter.close();

            //display file saved message
            //rsd
//            Toast.makeText(getBaseContext(), "Registered successfully!",
//                    Toast.LENGTH_SHORT).show();
            PAlertDialog( getResources().getString(R.string.Information),  getResources().getString(R.string.Registration_successfully) + "\n\n" + msg);

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getBaseContext(), e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
            PAlertDialog("ERROR", e.getMessage());
            return false;
        }
        return true;

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
    private void PAlertDialog(String title, String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
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







    public boolean connectionurlSecData() {

        String msg = "",urlar="";
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
                 s = urlar + "/GetAdditionalData/" + "/'" + number + "'/" +lang1;
            }
            else {
                sys = "Elsmart";
                 s = urlar + "/GetAdditionalDataElsmart/" + "/'" + number + "'/" +lang1;
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

                PAlertDialog("Error", getstring);
                return false;
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            InsertValue(json1);
            FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
            String BadgeIMEI = edtMessage.getText().toString() + "~" + txtImei.getText().toString() + "~" +edtURL.getText().toString() + "~"  + model + "~" + serial+"~"+json1+"~"+EMPLOYEE_SERVICE_URI1+"~"+ sys;
            outputStreamWriter.write(BadgeIMEI);
            outputStreamWriter.close();


            PAlertDialog( getResources().getString(R.string.Information), "Secondary Data added successfully" + "\n\n" + msg);

        } catch (Exception e) {
            e.printStackTrace();
            PAlertDialog( getResources().getString(R.string.Information), e.getMessage());
            return false;
        }
        return true;

    }


    public void InsertValue(String json1) {

        try {
        String[] urldata = json1.split("~");


        txttitle.setText(urldata[2]);
        txtdept.setText(urldata[1]);
       // txtcardvalidfrom.setText(urldata[3]);
       // txtcardiisuedfrom.setText(urldata[4]);
      //  txtcardexpired.setText(urldata[5]);



        Date date1 = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

            date1 = format.parse(urldata[3].replaceAll("[^' ':/\\w\\[\\]]", ""));

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

                                if (urldata[13].equals("Elsmart"))

                                    PAlertDialog("Information", "Need to be Re-Register for Eacs System");
                                // Toast.makeText(RegistrationActivity.this,
                                ////    "Need to be Reregistration for Elsmart", Toast.LENGTH_SHORT).show();

                            } else if (i == R.id.Elsmart) {
                                if (urldata[13].equals("Eacs"))
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

                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
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


                    if (urldata[13].equals("Elsmart")) {
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
            Toast.makeText(RegistrationActivity.this, "Registration data Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }

    }


    public boolean Filecheck() {


        try {
            File file = new File(getFilesDir() + File.separator + fileRegistrationVerify);


            if (file.exists()) {

                FileInputStream fileIn = openFileInput(fileRegistrationVerify);


                if (fileIn != null) {
                 /////////   FileChannel ch = null;

                  ///////////////  ch = fileIn.getChannel();

                   //////////////////// int size = (int) ch.size();

                   ////////////////////// MappedByteBuffer buf = ch.map(FileChannel.MapMode.READ_ONLY, 0, size);
                   ////////////////////// byte[] bytes = new byte[size];
                   ////////////////////////// int lnth = 0;
                  /////////////////////////  lnth = bytes.length;
                  /////////////////////////  buf.get(bytes);
                    ///////////////////////String s = new String(bytes);
                   ///////////////////// fileIn.close();
                    /////////////////////////String[] separated = s.split("~");
                  //////////////////////////////////  String badgeno = separated[0]; // this will contain "Fruit"
                  ///////////////////////  edtMessage.setText(badgeno);/////changed
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

//                    Toast.makeText(RegistrationActivity.this, "Empty File",
//                            Toast.LENGTH_SHORT).show();

            PAlertDialog( getResources().getString(R.string.Information), getResources().getString(R.string.File_Empty) );


        }
        return  s;
    }

}




