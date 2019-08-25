package com.electronia.mElsmart.Common;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.electronia.mElsmart.Controllerdb;
import com.electronia.mElsmart.R;
import com.electronia.mElsmart.RegistrationActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.electronia.mElsmart.RegistrationActivity.TAG;


public class UrlConnection  {
    Context mContext;
    long timeDifference;//=System.currentTimeMillis();
    Integer TimeOut =1;
    Controllerdb db ;//= new Controllerdb(this);
    SQLiteDatabase database;
    Integer status=1,Connection_Time_Out=5;
    // constructor
    public UrlConnection(Context context){
        this.mContext = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        db = new Controllerdb(mContext);
    }

    public  boolean checkConnection() {

        try {
            ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
            Toast.makeText(mContext, " Network Connection Error",
                    Toast.LENGTH_SHORT).show();
            Log.e("",e.getMessage());
        }
        return false;
    }


    public   String arabicToDecimal(String number) {
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

    public   String ServerConnection(String URL) throws Exception {
        String json="";
        int res=0;
        try {
            long time;// = System.currentTimeMillis();
            long timediffence=0;

            ReadTimeOutValue();
            if(status==0) {
                time = System.currentTimeMillis();
                timediffence = time - timeDifference;
                if (((timediffence / (1000 * 60)) < TimeOut)) {
                    return "^within_Time_Out";
                }
            }
            java.net.URL url = new URL(URL);
            URLConnection conexion = url.openConnection();
            conexion.setConnectTimeout(Connection_Time_Out * 1000);
            conexion.connect();
            int lenghtOfFile = conexion.getContentLength();
            InputStream input = new BufferedInputStream(conexion.getInputStream());
           // InputStream input = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();





            byte data[] = new byte[lenghtOfFile];
            int count = -1;

            while ((count = input.read(data)) != -1) {
                buffer.write(data, 0, count);
            }
            input.close();
             json = new String(buffer.toString());

             if(status==0)
             {
                 UpdateTime_Out(1);
             }
        }
        catch (Exception ex)
        {

            if(ex.getMessage().contains("connect timed out"))
            {
                timeDifference=System.currentTimeMillis();
                UpdateTime_Out(0);
                json="^Server Offline";
            }
            else {
                json = "^" + ex.getMessage();
            }
              return     json;

        }
        return  json ;
    }





    public   byte[] ServerConnection_Pic(String URL) throws Exception {
        String json="";
        String  urlar = "", result = "";
        String s="";
        InputStream in = null;
        BufferedOutputStream out = null;
        final int IO_BUFFER_SIZE = 64;
        final URL urlObject;
        final URLConnection myConn;
        byte[] data=new byte[8096];
        int res=0;
        long time;// = System.currentTimeMillis();
        long timediffence=0;
        try {


            ReadTimeOutValue();
            if(status==0) {
                time = System.currentTimeMillis();
                timediffence = time - timeDifference;
                if (((timediffence / (1000 * 60)) < TimeOut)) {
                    data[0]=-100;
                    return  data;
                }
            }
            urlObject = new URL(URL);
            myConn = urlObject.openConnection();
            myConn.setConnectTimeout(Connection_Time_Out*1000);
            in = myConn.getInputStream();
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copyCompletely(in, out);
            data = dataStream.toByteArray();
            if(status==0)
            {
                UpdateTime_Out(1);
            }
        }
        catch (Exception ex)
        {
            if(ex.getMessage().contains("connect timed out"))
            {
                timeDifference=System.currentTimeMillis();
                UpdateTime_Out(0);
                data[0]=-101;
                return  data;
            }
            else {
                data[0]=-102;;
            }
             return     data;

        }
        return  data ;
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


    public Integer UpdateTime_Out(int statusstatus) throws Exception//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            database    =db.getWritableDatabase();
            database.execSQL("INSERT OR REPLACE INTO URL_Time_Out( ID,timestamp,time_out,status,Connection_Time_Out) VALUES (1,"+System.currentTimeMillis()+", "+TimeOut +","+statusstatus +","+Connection_Time_Out+ ")") ;
            res=1;
        }
        catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            throw new Exception();
        }
        return res;
    }







    public Integer ReadTimeOutValue() throws Exception//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT time_out,timestamp,status,Connection_Time_Out  From   URL_Time_Out", null);
            if (cursor.moveToFirst()) {
                do {
                    TimeOut = Integer.valueOf(cursor.getString(cursor.getColumnIndex("time_out")));
                    timeDifference = Long.valueOf(cursor.getString(cursor.getColumnIndex("timestamp")));
                    status = Integer.valueOf(cursor.getString(cursor.getColumnIndex("status")));
                    Connection_Time_Out = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Connection_Time_Out")));

                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 1;
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            throw new Exception();
        }
        return res;
    }


}
