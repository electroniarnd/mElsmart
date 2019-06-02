package com.nordicsemi.nrfUARTv2;

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
import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

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

import com.nordicsemi.nrfUARTv2.R;
import com.nordicsemi.nrfUARTv2.models.SectionDataModel;
import com.nordicsemi.nrfUARTv2.models.SingleItemModel;

import static com.nordicsemi.nrfUARTv2.MainActivity.TAG;

public class Tracking extends AppCompatActivity {
    private ListView listView;
    private ListAdapter listAdapter;
    Button btnPlaceOrder;
    String EmpID="",EmpID1="";
    private static int BLE = 0;
    private static int QRCode = 0;
    private static int geofence = 0;
    private static int Tracking1=0;
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Product> productOrders = new ArrayList<>();
    ArrayList<String> lOrderItems = new ArrayList<String>();
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;
    static int counter=0;
   // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    private  static String ServiceURL="";//+"/ElguardianService/Service1.svc/" +
    private static int Registration=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        listView = (ListView) findViewById(R.id.customListView);
        listAdapter = new ListAdapter(this,products);


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

        ReadLogData();
        listView.setAdapter(listAdapter);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }


    public void openSummary(String orderItems)
    {
        Intent summaryIntent = new Intent(this,Summary.class);
        summaryIntent.putExtra("orderItems",orderItems);
        startActivity(summaryIntent);
    }

    public void showMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    public Integer ReadEmpID()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id FROM  Registration", null);
            if (cursor.moveToFirst()) {
                do {
                    EmpID=cursor.getString(cursor.getColumnIndex("Employee_Id"));
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

    public String SaveTasks() {

        String s = "", lang = "", lang1 = "",rest="";
        int ret = 0, res = 0;
        res = ReadEmpID();
        if (res == 1) {
            lang = Locale.getDefault().getDisplayLanguage();
            String number = "";
            if (lang.equals("English")) {
                lang1 = "en";

            } else {
                lang1 = "ar";

            }

            Date dt = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd kk.mm.ss");
            String formattedDate = df.format(dt.getTime());


            try {

                if (!(checkConnection())) {
                    ReadLogData();
                    rest="Internet Connection not found";
                    return rest;
                }

                s = "";
                s = ServiceURL+"/ElguardianService/Service1.svc/" + "/TaskList/" + "/" + EmpID + "/" + "en";
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

                if (json.contains("~")) {
                    String getstring = json;
                   // PAlertDialog(getResources().getString(R.string.Error), getstring);
                    ReadLogData();
                    rest=getstring;
                    return rest;
                } else {

                    int lnth = json.length();
                    String json1 = json.substring(1, lnth - 1);
                    rest=  WriteMACAddress(json1);
                    ret =   ReadLogData();


                    if (ret != 1) {
                       // Toast.makeText(getBaseContext(), "Error:Task did not read Offline", Toast.LENGTH_SHORT).show();

                        rest="Error:Task did not read Offline";
                    }
                }
            } catch (Exception e) {
                ReadLogData();
                e.printStackTrace();
                rest= e.getMessage();
                return rest;
            }
            return rest;

        }
        else {
        //    Toast.makeText(getBaseContext(), "Error:Task did not read Offline", Toast.LENGTH_SHORT).show();
            rest="Error:Task did not read Offline";
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
            Toast.makeText(Tracking.this, " Network Connection Error Data is saving  in the local database",
                    Toast.LENGTH_SHORT).show();
            Log.e("", e.getMessage());
        }
        return false;
    }


    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Tracking.this);
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
        String rest="Success";
        int res = 0;
        result=Deletedata();
        String[] urldata;
        if(result==1) {
            if(json1.length()>0) {
                urldata = json1.split(";");


                try {

                    for (int i = 0; i < urldata.length; i++) {
                        String[] urlValue = urldata[i].split("`");
                        if (urlValue.length > 0) {
                            database = db.getWritableDatabase();
                            database.execSQL("INSERT INTO Tasks(Project_Id,Employee_Id,BadgeNo,FullName,Task_Name,Date_Expected_Start,Date_Expected_End,Status1,Descriptions,Plan1,Project_Name,Project_No,Task_No,Task_Id)" +
                                    "VALUES(" + urlValue[0] + "," + urlValue[1] + ",'" + urlValue[2] + "','" + urlValue[3] + "','" + urlValue[4] + "','" + urlValue[5] + "','" + urlValue[6] + "','" + urlValue[7] + "','" + urlValue[8] + "','" + urlValue[9] + "','" + urlValue[10] + "','" + urlValue[11] + "','" + urlValue[12] + "'," + urlValue[13] + ")");

                        }
                    }
                } catch (Exception ex) {
                    rest = ex.getMessage();
                    Log.d(TAG, ex.getMessage());
                }
            }
            else {
                rest="data is not found";// Toast.makeText(getBaseContext(), "data is not found", Toast.LENGTH_SHORT).show();
            }
        }
        return rest;
    }


    public Integer Deletedata()
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

    public Integer  ReadLogData()
    {
        Integer res=0;

        HashMap<Integer, String> Project = new HashMap<Integer, String>();
        Project.clear();
        String TaskID="";
        int count = 0;
        products.clear();
        String ProjectName = "";
        byte[] image;
        ArrayList<SingleItemModel> singleItem = new ArrayList<SingleItemModel>();
        ;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,Project_Id,Project_Name,BadgeNo,FullName FROM  Tasks order by Project_Id ASC", null);
            if (cursor.moveToFirst()) {
                do {
                    if (!Project.containsKey(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id"))))) {
                        Project.put(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id"))),cursor.getString(cursor.getColumnIndex("Project_Name")));
                        image=   GetEmpPhoto(Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id"))));
                        products.add(new Product((cursor.getString(cursor.getColumnIndex("BadgeNo")))+"-"+(cursor.getString(cursor.getColumnIndex("FullName"))),Integer.valueOf((cursor.getString(cursor.getColumnIndex("Employee_Id")))),image));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            if(!checkServiceRunning()) {
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





    public boolean checkServiceRunning(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("com.nordicsemi.nrfUARTv2.PictureDownloadService"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
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






















    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                resp=   SaveTasks();
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
            listView.setAdapter(listAdapter);
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
           /////////////// finalResult.setText(result);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Tracking.this,
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

    @Override
    public void onBackPressed() {
                        if (ReadSystemValue() == 1) {
                            if (BLE == 0 && QRCode == 0 && geofence == 0 && Tracking1==1) {

                                Tracking.this.moveTaskToBack(true);
                            }
                            else
                                finish();
                        }
                    }

    public Integer ReadSystemValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode,Tracking  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    BLE = Integer.valueOf(cursor.getString(cursor.getColumnIndex("BLE")));
                    geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
                    QRCode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRCode")));
                    Tracking1 = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Tracking")));
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
        int res = 0,count=0,Employee_Id,ID;

        Double Longitude=0.0,Latitude=0.0;
        String Datetime1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Tasks ", null);
            if (cursor.moveToFirst()) {
                do {
                    Employee_Id=Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    byte[] imgByte=cursor.getBlob(cursor.getColumnIndex("pic"));
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


        String s="";
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        final int IO_BUFFER_SIZE = 64;
        final URL urlObject;
        final URLConnection myConn;
        byte[] data=new byte[8096];
        try {
            if(counter>1) {
                Thread.sleep(1000);
                counter++;
            }

            if (!(checkConnection())) {
                return null;
            }

            s ="";
            s = ServiceURL+"/ElguardianService/Service1.svc/"  + "/GetImage" + "/" + Empid;
            EMPLOYEE_SERVICE_URI1 = s.replace(' ','-');
            URL url = new URL(EMPLOYEE_SERVICE_URI1);

            urlObject = new URL(EMPLOYEE_SERVICE_URI1);
            myConn = urlObject.openConnection();
            in = myConn.getInputStream();
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copyCompletely(in, out);
            data = dataStream.toByteArray();
                UpdateLogData(Empid,data);
            // BitmapFactory.Options options = new BitmapFactory.Options();
            // bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Network Connection Error", Toast.LENGTH_LONG).show();

        }
        finally {

            try {
                if(in != null) {in.close();}
            }
            catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            try {
                if(out != null) {out.close();}
            }
            catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return data;

    }



    public Integer UpdateLogData(int ID,byte[] img)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
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

            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }

    public byte[] GetEmpPhoto(int  ID)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;

        Double Longitude=0.0,Latitude=0.0;
        String Datetime1;
        byte[] data=new byte[8096];
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Emp_Photo where Employee_Id = " + ID, null);
            if (cursor.moveToFirst()) {
                do {

                    data=cursor.getBlob(cursor.getColumnIndex("pic"));
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

        if((output instanceof FileOutputStream) && (input instanceof FileInputStream)) {

            try {
                FileChannel target = ((FileOutputStream) output).getChannel();
                FileChannel source = ((FileInputStream) input).getChannel();
                source.transferTo(0, Integer.MAX_VALUE, target);
                source.close();
                target.close();
                return;
            }
            catch (Exception e) { /* failover to byte stream version */}
        }

        byte[] buf = new byte[8192];

        while (true) {
            int length = input.read(buf);
            if(length < 0) {
                break;
            }
            output.write(buf, 0, length);
        }

        try {
            input.close();
        }
        catch (IOException ignore) {}

        try {
            output.close();
        }
        catch (IOException ignore) {}

    }

    public String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,os);
        byte[] byteArray = os.toByteArray();
        return Base64.encodeToString(byteArray, 0);
    }

    public Bitmap convertToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmapResult;
    }
}
