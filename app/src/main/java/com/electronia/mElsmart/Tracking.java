package com.electronia.mElsmart;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.electronia.mElsmart.Common.UrlConnection;
import com.electronia.mElsmart.models.SingleItemModel;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.electronia.mElsmart.MainActivity.TAG;



    public class Tracking extends AppCompatActivity {
        private ListView listView;
        private ListAdapter listAdapter;
        Button btnPlaceOrder;
        String EmpID = "", EmpID1 = "";
        private static int BLE = 0;
        private static int QRCode = 0;
        private static int geofence = 0;
        private static int Tracking1 = 0;
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Product> productOrders = new ArrayList<>();
        ArrayList<String> lOrderItems = new ArrayList<String>();
        Controllerdb db = new Controllerdb(this);
        SQLiteDatabase database;
        static int counter = 0;
        // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
        private static String EMPLOYEE_SERVICE_URI1 = "";
        private static String ServiceURL = "";//+"/ElguardianService/Service1.svc/" +
        private static int Registration = 0;
        UrlConnection urlconnection;
        String[] TaskValue;
        String TaskId="";
        String TaskID_Value="";
        String EmployeeID="";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_tracking);
            Intent intent = getIntent();
            TaskId = intent.getStringExtra("TaskID1");
            TaskValue = TaskId.split(",");
            TaskID_Value=TaskValue[0];
            EmployeeID=TaskValue[1];

            urlconnection = new UrlConnection(getApplicationContext());
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            listView = (ListView) findViewById(R.id.customListView);
            listAdapter = new ListAdapter(this, products);


            if (GetServiceURL() == 1) {
                Toast.makeText(this, "Error in reading local Database", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (Registration == 0) {
                    Toast.makeText(this, "Registration not found", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            ReadLogData();
            listView.setAdapter(listAdapter);
           // AsyncTaskRunner runner = new AsyncTaskRunner();
           // runner.execute();
        }



        public Integer ReadEmpID()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res = 0;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT Employee_Id FROM  Registration", null);
                if (cursor.moveToFirst()) {
                    do {
                        EmpID = cursor.getString(cursor.getColumnIndex("Employee_Id"));
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












        public Integer ReadLogData() {
            Integer res = 0;

            HashMap<Integer, String> Project = new HashMap<Integer, String>();
            Project.clear();
            String TaskID = "";
            int count = 0;
            products.clear();
            String ProjectName = "";
            byte[] image;
            ArrayList<SingleItemModel> singleItem = new ArrayList<SingleItemModel>();
            ;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT Employee_Id,Project_Id,Project_Name,BadgeNo,FullName FROM  Tasks where Task_Id="+TaskID_Value+" order by Project_Id ASC", null);
                if (cursor.moveToFirst()) {
                    do {
                        if (!Project.containsKey(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id"))))) {
                            Project.put(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id"))), cursor.getString(cursor.getColumnIndex("Project_Name")));
                            image = GetEmpPhoto(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id"))));
                            products.add(new Product((cursor.getString(cursor.getColumnIndex("BadgeNo"))) + "-" + (cursor.getString(cursor.getColumnIndex("FullName"))), Integer.valueOf((cursor.getString(cursor.getColumnIndex("Employee_Id")))), image));
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                if (!checkServiceRunning()) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            Intent startServiceIntent = new Intent(Tracking.this, PictureDownloadService.class);
                            startService(startServiceIntent);
                        }
                    };
                    thread.start();
                }

                res = 1;
            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
            }
            return res;

        }


        public boolean checkServiceRunning() {
            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.nordicsemi.nrfUARTv2.PictureDownloadService"
                        .equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }


        public Integer GetServiceURL()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            String MacValue = "";
            int res = 1;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
                if (cursor.moveToFirst()) {
                    do {

                        ServiceURL = cursor.getString(cursor.getColumnIndex("url"));
                        Registration++;
                    } while (cursor.moveToNext());
                }
                cursor.close();
                res = 0;
            } catch (Exception ex) {
                res = -1;
                Log.d(TAG, ex.getMessage());
            }
            return res;
        }





        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_tracking, menu);
            return true;
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

                default:
                    return super.onOptionsItemSelected(item);

            }

            return super.onOptionsItemSelected(item);
        }


        public Integer SendSavedData()//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res = 0, count = 0, Employee_Id, ID;

            Double Longitude = 0.0, Latitude = 0.0;
            String Datetime1;
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Tasks ", null);
                if (cursor.moveToFirst()) {
                    do {
                        Employee_Id = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                        byte[] imgByte = cursor.getBlob(cursor.getColumnIndex("pic"));
                        if (imgByte == null || imgByte.length == 0)
                            sendsavedlog(Employee_Id);
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


        public byte[] sendsavedlog(int Empid) {


            String s = "";
            Bitmap bitmap = null;
            InputStream in = null;
            BufferedOutputStream out = null;
            final int IO_BUFFER_SIZE = 64;
            final URL urlObject;
            final URLConnection myConn;
            byte[] data = new byte[8096];
            try {
                if (counter > 1) {
                    Thread.sleep(1000);
                    counter++;
                }

                if (!(urlconnection.checkConnection())) {
                    return null;
                }

                s = "";
                s = ServiceURL + "/ElguardianService/Service1.svc/" + "/GetImage" + "/" + Empid;
                EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
                data=    urlconnection.ServerConnection_Pic(EMPLOYEE_SERVICE_URI1);
                UpdateLogData(Empid, data);
                // BitmapFactory.Options options = new BitmapFactory.Options();
                // bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

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
            return data;

        }


        public Integer UpdateLogData(int ID, byte[] img)//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res = 0;
            try {
                database = db.getReadableDatabase();
                ContentValues values = new ContentValues();
                values.put("pic", img);

// updating row
                Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Emp_Photo where Employee_Id = " + ID, null);
                if (cursor.moveToNext()) {
                    database.update("Emp_Photo", values, "Employee_Id" + " = ?", new String[]{String.valueOf(ID)});
                } else {
                    values.put("Employee_Id", ID);
                    database.insert("Emp_Photo", null, values);
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

        public byte[] GetEmpPhoto(int ID)//////CHANGE INTO COMMON FUNCTION LATTER
        {
            int res = 0;

            Double Longitude = 0.0, Latitude = 0.0;
            String Datetime1;
            byte[] data = new byte[8096];
            try {
                database = db.getReadableDatabase();
                Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Emp_Photo where Employee_Id = " + ID, null);
                if (cursor.moveToFirst()) {
                    do {

                        data = cursor.getBlob(cursor.getColumnIndex("pic"));
                        // if (imgByte == null && imgByte.length == 0);
                        //sendsavedlog(Employee_Id);
                    } while (cursor.moveToNext());


                }
                cursor.close();
                res = 1;


            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
            }
            return data;
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

        public String convertToBase64(Bitmap bitmap) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] byteArray = os.toByteArray();
            return Base64.encodeToString(byteArray, 0);
        }

        public Bitmap convertToBitmap(String base64String) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return bitmapResult;
        }
    }
