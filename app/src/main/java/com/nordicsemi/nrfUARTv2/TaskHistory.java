package com.nordicsemi.nrfUARTv2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nordicsemi.nrfUARTv2.models.*;
import com.nordicsemi.nrfUARTv2.models.CustomAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.nordicsemi.nrfUARTv2.MainActivity.TAG;

public class TaskHistory extends Activity

    {
        ListView messages_list;
        ArrayList<DataModelTaskHistory> dataModels;
        ListView listView;
        private static com.nordicsemi.nrfUARTv2.models.CustomAdapterTaskHistory adapter;
        Controllerdb db =new Controllerdb(this);
        SQLiteDatabase database;
        public static final String TAG = "MapTracking";
       // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
        private static String EMPLOYEE_SERVICE_URI1 = "";
        String[] TaskValue;
        String TaskId="";
        String TaskID="";
        String EmpID="",Status="",BadgeNo="";
        private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
        private static int Registration=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        listView=(ListView)findViewById(R.id.list);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        TaskId = intent.getStringExtra("TaskID");
        TaskValue = TaskId.split(",");
        TaskID=TaskValue[0];
        EmpID=TaskValue[1];

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle sendBundle = new Bundle();
                sendBundle.putString("TaskID",TaskId);
                Intent intent = new Intent(TaskHistory.this, Task_Details.class);
                intent.putExtras(sendBundle);;
                startActivity(intent);
                finish();
            }
        });


        if(GetServiceURL()==1) {
            Toast.makeText(this, "Error in reading local Database", Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            if(Registration==0)
            {
                Toast.makeText(this, "Registration not found", Toast.LENGTH_LONG).show();
                return;
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModelTaskHistory dataModel= dataModels.get(position);

                Snackbar.make(view, dataModel.getPlace()+"\n"+dataModel.getstatus()+"\n"+dataModel.getdatetime(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
      //  SaveTasks();


        TaskHistory.AsyncTaskRunner runner = new TaskHistory.AsyncTaskRunner();
        runner.execute();
    }





        public String SaveTasks() {

            String s = "", lang = "", lang1 = "",rest="";
            int ret = 0, res = 0;
            if (EmpID != null && !EmpID.isEmpty() && !EmpID.equals("null"))
            {
                try {

                    if (!(checkConnection())) {

                       // PAlertDialog(getResources().getString(R.string.Error), "Internet Connection not found");
                        rest="Internet Connection not found";
                        return rest;
                    }

                    s = "";
                    s = ServiceURL+"/ElguardianService/Service1.svc/" + "/TaskHistory/"+TaskID;
                    EMPLOYEE_SERVICE_URI1 = s.replace(' ', '-');
                    URL url = new URL(EMPLOYEE_SERVICE_URI1);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    InputStream input = new BufferedInputStream(url.openStream());


                    int b = -1;
                    while ((b = input.read()) != -1)
                        buffer.write(b);
                    input.close();

                    String json = new String(buffer.toString());
                    Log.d(getResources().getString(R.string.download), getResources().getString(R.string.Lenght_of_file) + json.length());

                    if (json.contains("`")) {
                        String getstring = json;
                        rest= getstring;//PAlertDialog(getResources().getString(R.string.Error), getstring);
                        return rest;
                    } else {

                        int lnth = json.length();
                        String json1 = json.substring(1, lnth - 1);
                        rest=    WriteMACAddress(json1);

                    }
                } catch (Exception e) {
                 //   Toast.makeText(getBaseContext(), "Error:Connection with Server", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    rest="Error:Connection with Server";
                    return rest;
                }


            }
            else {
                Toast.makeText(getBaseContext(), "Error:Employee ID did not read Successfully", Toast.LENGTH_SHORT).show();
                rest="Error:Employee ID did not read Successfully";
            }
            return rest;
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
                Toast.makeText(TaskHistory.this, " Network Connection Error Data is saving  in the local database",
                        Toast.LENGTH_SHORT).show();
                Log.e("", e.getMessage());
            }
            return false;
        }


        private void PAlertDialog(String title, String msg) {

            AlertDialog.Builder builder = new AlertDialog.Builder(TaskHistory.this);
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







        public String WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
        {
            Integer result=0;

            String Place="",rest="Success";
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.ENGLISH);
        //    SimpleDateFormat format = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                //    Locale.ENGLISH);
            Date date1 = null;
            dataModels= new ArrayList<>();
            //  result=Deletedata();

            if(json1.length()>0) {
            String[] urldata = json1.split(";");
            try {


                for (int i = 0; i < urldata.length; i++) {
                    String[] urlValue = urldata[i].split("~");

                   // date1 = format.parse(urlValue[3].replaceAll("[^' ':/\\w\\[\\]]", ""));
                    String ss = urlValue[3].replaceAll("[^' ':/\\w\\[\\]]", "");
                    date1 = format.parse(ss);
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                    String requiredDate = df.format(date1).toString();


                    if (urlValue.length > 0) {
                        if ((urlValue[1] != null && !urlValue[1].isEmpty() && !urlValue[1].equals("null") && !urlValue[1].equals("0")) && (urlValue[0] != null && !urlValue[0].isEmpty() && !urlValue[0].equals("null") && !urlValue[0].equals("0"))) {
                            Place = geo(Double.valueOf(urlValue[1]), Double.valueOf(urlValue[0]));
                            Status = urlValue[2];
                            BadgeNo = urlValue[4]+"-"+urlValue[5];
                            dataModels.add(new DataModelTaskHistory(Place, Status, requiredDate, BadgeNo));
                        } else {
                            Status = urlValue[2];
                            BadgeNo = urlValue[4];
                            dataModels.add(new DataModelTaskHistory("--", Status, requiredDate, BadgeNo));
                        }
                    }
                }

                adapter= new CustomAdapterTaskHistory(dataModels,getApplicationContext());

            } catch (Exception ex) {
                rest=ex.getMessage();
                Log.d(TAG, ex.getMessage());
            }
            }
            return rest;
        }


        protected String geo(  double  latitude,double longitude) {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String errorMessage = "";
            List<Address> addresses = null;
            String addressvalue="";




            // double  latitude =26.2934941;;
            //  double longitude =50.2073916 ;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException ioException) {
                errorMessage = "Service Not Available";
                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = "Invalid Latitude or Longitude Used";
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latitude + ", Longitude = " +
                        longitude, illegalArgumentException);
            }



            if (addresses == null || addresses.size()  == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "Not Found";
                    Log.e(TAG, errorMessage);
                    return  addressvalue;
                }

            } //else {
            ///   for(Address address : addresses) {
            // String outputAddress = "";
            // for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            //     outputAddress += " --- " + address.getAddressLine(i);
            // }
            // Log.e(TAG, outputAddress);
            //  }
            Address address = addresses.get(0);
            addressvalue=  address.getAddressLine(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            //     addressFragments.add(address.getAddressLine(i));
            //}
            Log.i(TAG, "Address Found");

            //}

            return addressvalue;
        }


        @Override
        public void onBackPressed() {
            String empid=TaskValue[1];
            Bundle sendBundle = new Bundle();
            sendBundle.putString("TaskID",TaskId);
            Intent intent = new Intent(TaskHistory.this, Task_Details.class);
            intent.putExtras(sendBundle);;
            startActivity(intent);
            finish();

        }






        private class AsyncTaskRunner extends AsyncTask<String, String, String> {

            private String resp;
            ProgressDialog progressDialog;

            @Override
            protected String doInBackground(String... params) {
                publishProgress("Sleeping..."); // Calls onProgressUpdate()
                try {
                    resp=  SaveTasks();
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
                listView.setAdapter(adapter);
                progressDialog.dismiss();
            }


            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(TaskHistory.this,
                        "Loading...",
                        "Please Wait");
                // progressDialog.getWindow().setLayout(200,50);
                progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
            }


            @Override
            protected void onProgressUpdate(String... text) {
                ///////////////////// finalResult.setText(text[0]);
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

    }
