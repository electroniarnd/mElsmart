package com.nordicsemi.nrfUARTv2;

/**
 * Created by Pradeepn on 6/21/2018.
 */
import  android.content.Context;
import  android.database.sqlite.SQLiteDatabase;
import  android.database.sqlite.SQLiteOpenHelper;
public class Controllerdb extends SQLiteOpenHelper {


    private static final String DATABASE_NAME="DBElSmart";
    public Controllerdb(Context applicationcontext) {
        super(applicationcontext, DATABASE_NAME, null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table to insert data
        String query;
        query = "CREATE TABLE IF NOT EXISTS tblLogs(Id INTEGER PRIMARY KEY AUTOINCREMENT,date DateTime,time DateTime,BadgeNo VARCHAR,Name VARCHAR,Ter VARCHAR,empid INTEGER,direction VARCHAR );";

        db.execSQL(query);
        db.execSQL("Create table  IF NOT EXISTS Device (ID INTEGER PRIMARY KEY AUTOINCREMENT,MacId TEXT,Name TEXT)");
        db.execSQL("Create table  IF NOT EXISTS system_setting (ID INTEGER PRIMARY KEY AUTOINCREMENT,Vibration INTEGER DEFAULT 0,Debug INTEGER DEFAULT 0,AutoPunch INTEGER DEFAULT 0,Biometric INTEGER DEFAULT 0 )");
        db.execSQL("Create table  IF NOT EXISTS QRCode_Permission (ID INTEGER PRIMARY KEY AUTOINCREMENT,geoID INTEGER DEFAULT 0,Qrcode TEXT )");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query ;
        query = "DROP TABLE IF EXISTS tblLogs";
        db.execSQL(query);
        db.execSQL("DROP TABLE IF EXISTS Device" );
        db.execSQL("DROP TABLE IF EXISTS system_setting");
        db.execSQL("DROP TABLE IF EXISTS QRCode_Permission");
        onCreate(db);
    }
}