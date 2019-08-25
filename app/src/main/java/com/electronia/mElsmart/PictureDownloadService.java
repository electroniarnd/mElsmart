package com.electronia.mElsmart;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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

public class PictureDownloadService extends Service {
    private static final String TAG ="Location Update Service" ;
    private static String EMPLOYEE_SERVICE_URI1 = "";
    int counter=0;
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    private static String ServiceURL="";
    public PictureDownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        //Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        GetServiceURL();
        SendSavedData();
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }



    public Integer SendSavedData()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0,count=0,Employee_Id,ID;

        Double Longitude=0.0,Latitude=0.0;
        String Datetime1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT DISTINCT Employee_Id,pic FROM  Tasks ", null);
            if (cursor.moveToFirst()) {
                do {
                    Employee_Id=Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    byte[] imgByte=cursor.getBlob(cursor.getColumnIndex("pic"));
                    if (imgByte == null && imgByte.length == 0)
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
            Toast.makeText(this, " Network Connection Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("", e.getMessage());
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
                    //  Registration++;
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
