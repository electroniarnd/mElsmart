package com.electronia.mElsmart;

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
        query = "CREATE TABLE IF NOT EXISTS tblLogs(Id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,time TEXT,datetosent TEXT,timetosent TEXT,BadgeNo VARCHAR,Name VARCHAR,Ter VARCHAR,empid INTEGER,direction INTEGER DEFAULT  0,GeoID INTEGER DEFAULT  0,QRId INTEGER DEFAULT  0,PunchType TEXT,sent INTEGER DEFAULT  0);";
        db.execSQL(query);
        db.execSQL("Create table  IF NOT EXISTS Device (ID INTEGER PRIMARY KEY AUTOINCREMENT,MacId TEXT,Name TEXT)");
        db.execSQL("Create table  IF NOT EXISTS system_setting (ID INTEGER PRIMARY KEY AUTOINCREMENT,Vibration INTEGER DEFAULT 0,Debug INTEGER DEFAULT 0,AutoPunch INTEGER DEFAULT 0,Biometric INTEGER DEFAULT 0,AutoPunchGeo INTEGER DEFAULT 0,AutoPunchQR INTEGER DEFAULT 0 )");
        db.execSQL("Create table  IF NOT EXISTS QRCode_Permission (ID INTEGER PRIMARY KEY AUTOINCREMENT,geoID INTEGER DEFAULT 0,Qrcode TEXT,CustID INTEGER DEFAULT 0,BadgeNo TEXT,TerID INTEGER DEFAULT 0,QRId INTEGER DEFAULT 0,QRCode_Name TEXT)");
        db.execSQL("Create table  IF NOT EXISTS Tasks (ID INTEGER PRIMARY KEY AUTOINCREMENT,Project_Id INTEGER DEFAULT 0,Employee_Id INTEGER DEFAULT 0,Project_No TEXT,Task_No TEXT,BadgeNo TEXT,FullName TEXT,Task_Name TEXT,Date_Expected_Start TEXT,Date_Expected_End TEXT,Status1 INTEGER DEFAULT 0,Descriptions TEXT,Plan1 TEXT,Project_Name TEXT,Task_Id INTEGER DEFAULT 0,OpenTask INTEGER DEFAULT 0,FinishedTask INTEGER DEFAULT 0,pic blob,Project_Date_Actual_Start TEXT,Project_Date_Actual_End TEXT)");
        db.execSQL("Create table  IF NOT EXISTS Tasks_Operation(ID INTEGER PRIMARY KEY AUTOINCREMENT,Employee_Id INTEGER DEFAULT 0,Operation_No INTEGER DEFAULT 0,Comments TEXT,Task_Id INTEGER DEFAULT 0,Customer_Feedback TEXT,send INTEGER DEFAULT 0,Longitude REAL ,Latitude REAL,Datetime TEXT)");
        db.execSQL("Create table  IF NOT EXISTS Registration(ID INTEGER PRIMARY KEY AUTOINCREMENT,Employee_Id INTEGER DEFAULT 0,UserRole INTEGER DEFAULT 0,QRCode INTEGER DEFAULT 0,Geofence INTEGER DEFAULT 0,Tracking INTEGER DEFAULT 0,Tracking_Type_Id INTEGER DEFAULT 0,BLE INTEGER DEFAULT 0,FullName TEXT,IMEI TEXT,ModelNo TEXT,ValidFromDate TEXT,IssuedDate TEXT,ExpiryDate TEXT ,Title TEXT ,DeptName TEXT,BadgeNo TEXT,Company_Id INTEGER DEFAULT 0,Interval INTEGER DEFAULT 0,MinDist INTEGER DEFAULT 0,Cust_Id INTEGER DEFAULT 0,Customer_Virtual_Id INTEGER DEFAULT 0,url TEXT,GeoQR INTEGER DEFAULT 0)");
        db.execSQL("Create table  IF NOT EXISTS LiveTracking(ID INTEGER PRIMARY KEY AUTOINCREMENT,Employee_Id INTEGER DEFAULT 0,Longitude REAL ,Latitude REAL,Datetime1 TEXT,Sent INTEGER DEFAULT 0,Speed INTEGER DEFAULT 0,Heading INTEGER DEFAULT 0)");
        db.execSQL("Create table  IF NOT EXISTS Geofence (ID INTEGER PRIMARY KEY AUTOINCREMENT,GeoID INTEGER DEFAULT 0, KeyName TEXT,GeoName TEXT,Lat REAL,Long REAL,Radius INTEGER,Badgeno TEXT,Shape_Name TEXT,Group_Name TEXT,Zoom_value TEXT)");
        db.execSQL("Create table  IF NOT EXISTS LogGeo (ID INTEGER PRIMARY KEY AUTOINCREMENT,BadgeNo TEXT,date TEXT,time TEXT ,sent INTEGER,GeoID INTEGER DEFAULT  0, PunchType INTEGER DEFAULT  0,QRId INTEGER DEFAULT  0)");
        db.execSQL("Create table  IF NOT EXISTS RegiGeo (ID INTEGER PRIMARY KEY AUTOINCREMENT,BadgeNo TEXT,Departmentname TEXT,Title TEXT,CardIssuedDate DateTime, CardValidFromDate DateTime,CardExpiryDate DateTime,FullName TEXT)");
        db.execSQL("Create table  IF NOT EXISTS Emp_Photo (ID INTEGER PRIMARY KEY AUTOINCREMENT,INTEGER DEFAULT 0,Employee_Id INTEGER DEFAULT 0,pic blob)");
        db.execSQL("Create table  IF NOT EXISTS URL_Time_Out (ID INTEGER PRIMARY KEY AUTOINCREMENT,INTEGER DEFAULT 0,timestamp TEXT,time_out INTEGER DEFAULT 0,status INTEGER DEFAULT 1,Connection_Time_Out INTEGER DEFAULT 3)");
        db.execSQL("Create table  IF NOT EXISTS Profile_Photo (ID INTEGER PRIMARY KEY AUTOINCREMENT,INTEGER DEFAULT 0,Employee_Id INTEGER DEFAULT 0,pic blob)");

        db.execSQL("Create table  IF NOT EXISTS LeaveDesc (ID INTEGER PRIMARY KEY AUTOINCREMENT,LeaveDescID TEXT,Description TEXT,LeaveCode TEXT,OfficialDuty INTEGER DEFAULT 0, Description_Ar TEXT,LeaveCode_Ar TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query ;
        query = "DROP TABLE IF EXISTS tblLogs";
        db.execSQL(query);
        db.execSQL("DROP TABLE IF EXISTS Device" );
        db.execSQL("DROP TABLE IF EXISTS system_setting");
        db.execSQL("DROP TABLE IF EXISTS QRCode_Permission");
        db.execSQL("DROP TABLE IF EXISTS Tasks");
        db.execSQL("DROP TABLE IF EXISTS Tasks_Operation");
        db.execSQL("DROP TABLE IF EXISTS Registration");
        db.execSQL("DROP TABLE IF EXISTS LiveTracking");
        db.execSQL("DROP TABLE IF EXISTS Geofence");
        db.execSQL("DROP TABLE IF EXISTS LogGeo");
        db.execSQL("DROP TABLE IF EXISTS RegiGeo");
        db.execSQL("DROP TABLE IF EXISTS Emp_Photo");
        db.execSQL("DROP TABLE IF EXISTS URL_Time_Out");
        db.execSQL("DROP TABLE IF EXISTS Profile_Photo");
        db.execSQL("DROP TABLE IF EXISTS LeaveDesc");
        onCreate(db);
    }
}