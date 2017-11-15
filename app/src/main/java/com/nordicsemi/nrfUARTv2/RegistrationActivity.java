package com.nordicsemi.nrfUARTv2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Date;
import java.util.HashMap;

public class RegistrationActivity extends ActionBarActivity {

    private EditText edtMessage;
    private EditText edtURL;
    private TextView txtImei;
    private Button btnregistration;
    private Button btnunregistered;
    private TextView txtunregistered;
    Context context;
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private static final String Datafile = "mytextfile.txt";
    final String model = Build.MODEL;
    final String serial = Build.SERIAL;

    private static String EMPLOYEE_SERVICE_URI;// = "http://93.95.24.25/EmployeService/Service1.svc/GetEmployee/?key=1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        btnregistration = (Button) findViewById(R.id.btnregistration);
        btnunregistered = (Button) findViewById(R.id.btnunregistered);
        edtMessage = (EditText) findViewById(R.id.editImei);
        txtImei = (TextView) findViewById(R.id.txtimei);
        edtURL = (EditText) findViewById(R.id.txturl);
        txtunregistered = (TextView) findViewById(R.id.txtunregistered);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        txtImei.setText(telephonyManager.getDeviceId().toString());
        EMPLOYEE_SERVICE_URI = "http://93.95.24.25/Emobile/Service1.svc";
        edtURL.setText(EMPLOYEE_SERVICE_URI);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();





        StrictMode.setThreadPolicy(policy);
        txtunregistered.setText("");


        try {



            if (Filecheck()) {
                txtunregistered.setText("Registered");
                txtunregistered.setBackgroundColor(Color.GREEN);
                btnunregistered.setEnabled(true);
                edtMessage.setEnabled(false);


            } else {
                txtunregistered.setText("UnRegistered");
                txtunregistered.setBackgroundColor(Color.RED);
                btnunregistered.setEnabled(false);
                edtMessage.setEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        btnregistration.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {


                try {
                    if (checkCondition()) {
                        return;
                    }

                    File file = new File(getFilesDir() + File.separator + fileRegistrationVerify);


                    if (Filecheck()) {

                        try {
                            //   Toast.makeText(this, "", Toast.LENGTH_LONG).show();


                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                            builder.setTitle("Confirm");
                            builder.setMessage("Are you sure to do Registration Again?");
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        if (connectionurl()) {
                                            FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
                                            // OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);


                                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
                                            String BadgeIMEI = edtMessage.getText().toString() + "," + txtImei.getText().toString() + "," + EMPLOYEE_SERVICE_URI + "/GetCardholderData/" + "/'" + edtMessage.getText().toString() + "'/'" + txtImei.getText().toString() + "'/'" +model + "'/'" +serial+"',"+txtImei.getText().toString()+","+model+","+serial;
                                            outputStreamWriter.write(BadgeIMEI);
                                            outputStreamWriter.close();
                                            btnunregistered.setEnabled(true);
                                        }

                                    } catch (IOException e) {
                                        Log.e("Exception", "File write failed: " + e.toString());
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

                            if (connectionurl()) {

                                FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
                                // OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);


                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
                                String BadgeIMEI = edtMessage.getText().toString() + "," + txtImei.getText().toString() + "," + EMPLOYEE_SERVICE_URI + "/GetCardholderData/" + "/'" + edtMessage.getText().toString() + "'/'" + txtImei.getText().toString() + "'";

                                outputStreamWriter.write(BadgeIMEI);
                                outputStreamWriter.close();


                                btnunregistered.setEnabled(true);
                            }

                        } catch (IOException e) {
                            Log.e("Exception", "File write failed: " + e.toString());
                        }
                    }
                } catch (Exception e) {
                    String error = "";
                    error = e.getMessage();
                    Toast.makeText(RegistrationActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnunregistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (checkCondition()) {
                        return;
                    }
                    File Registrationfile = new File(getFilesDir() + File.separator + fileRegistrationVerify);

                    File Valuefile = new File(getFilesDir() + File.separator + Datafile);
                    if (!(Registrationfile.exists())) {

                        Toast.makeText(RegistrationActivity.this, "Registration  does not exist",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!(Valuefile.exists())) {

                        Toast.makeText(RegistrationActivity.this, "Registration Data file does not exist",
                                Toast.LENGTH_LONG).show();
                        return;
                    }


                    boolean deleted = Registrationfile.delete();
                    boolean deleted1 = Valuefile.delete();
                    if (deleted && deleted1) {
                        Toast.makeText(RegistrationActivity.this, "Your Registration  are deleted Successfully",
                                Toast.LENGTH_LONG).show();
                        btnunregistered.setEnabled(false);
                        edtMessage.setEnabled(true);

                    } else {
                        Toast.makeText(RegistrationActivity.this, "Your Registration are not  deleted.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    String error = "";
                    error = e.getMessage();
                    Toast.makeText(RegistrationActivity.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    public byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0)
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1)
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);

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
        return false;
    }


    public boolean connectionurl() {

        try {


            String badgeno = edtMessage.getText().toString();
            String imei = txtImei.getText().toString();


            EMPLOYEE_SERVICE_URI = edtURL.getText().toString() + "/GetCardholderData/" + "/'" + badgeno + "'/'" + imei + "'/'" + model + "'/'" + serial + "'";

            URL url = new URL(EMPLOYEE_SERVICE_URI);

            URLConnection conexion = url.openConnection();
            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();
            Log.d("download", "Lenght of file: " + lenghtOfFile);


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


                Toast.makeText(getBaseContext(), getstring,
                        Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getBaseContext(), "Registered successfully!",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


    public boolean checkCondition() {
        String str = edtMessage.getText().toString();


        try {

            if (!(checkConnection())) {
                Toast.makeText(RegistrationActivity.this, "No Internet Connection Found",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            if (str == null || str.isEmpty() || str.equals("")) {
                Toast.makeText(RegistrationActivity.this, "Badge No should not be blank",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            str = "";
            str = edtURL.getText().toString();

            if (str == null || str.isEmpty() || str.equals("")) {
                Toast.makeText(RegistrationActivity.this, "Service URL should not be blank",
                        Toast.LENGTH_SHORT).show();
                return true;

            }

        } catch (Exception e) {
            String error = "";
            error = e.getMessage();
            return true;
        }
        return false;
    }


    public boolean Filecheck() {


        try {
            File file = new File(getFilesDir() + File.separator + fileRegistrationVerify);


            if (file.exists()) {

                FileInputStream fileIn = openFileInput(fileRegistrationVerify);


                if (fileIn != null) {
                    FileChannel ch = null;

                    ch = fileIn.getChannel();

                    int size = (int) ch.size();

                    MappedByteBuffer buf = ch.map(FileChannel.MapMode.READ_ONLY, 0, size);
                    byte[] bytes = new byte[size];
                    int lnth = 0;
                    lnth = bytes.length;
                    buf.get(bytes);
                    String s = new String(bytes);
                    fileIn.close();
                    String[] separated = s.split(",");
                    String badgeno = separated[0]; // this will contain "Fruit"
                    edtMessage.setText(badgeno);

                }
                else {

                    Toast.makeText(RegistrationActivity.this, "Empty File",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
               return true;
    }
}
