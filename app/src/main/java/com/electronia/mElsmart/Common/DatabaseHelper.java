package com.electronia.mElsmart.Common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.util.Log;

import com.electronia.mElsmart.Controllerdb;

import static com.electronia.mElsmart.MainActivity.TAG;

public class DatabaseHelper {
    Controllerdb db ;//= new Controllerdb(this);
    SQLiteDatabase database;
    Context mContext;
    public DatabaseHelper(Context context){
        this.mContext = context;
        db = new Controllerdb(mContext);
    }

    public byte[] GetEmpPhoto(int ID)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        byte[] data = new byte[8096];
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Emp_Photo where Employee_Id = " + ID, null);
            if (cursor.moveToFirst()) {
                do {
                    data = cursor.getBlob(cursor.getColumnIndex("pic"));
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



    public Integer dbDeleteGeofence()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Geofence");
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }


    public Integer dbDeleteQR()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from QRCode_Permission");
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }





}
