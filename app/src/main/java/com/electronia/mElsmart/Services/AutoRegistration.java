package com.electronia.mElsmart.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.electronia.mElsmart.Common.UrlConnection;
import com.electronia.mElsmart.Controllerdb;
import com.electronia.mElsmart.R;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class AutoRegistration extends Service {

    private static final String TAG ="AutoRegistrationService" ;
    int counter=0;
    // private static String EMPLOYEE_SERVICE_URI = "http://212.12.167.242:6002/Service1.svc";
    private static String EMPLOYEE_SERVICE_URI1 = "";
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    static String ServiceURL="";
    UrlConnection urlconnection;
    private static int BLE = 0;
    private static int QRCode = 0;
    private static int Geofence = 0,Employee_Id=0,GeoQR=0,Count=0;
    private static String msg = "",BadgeNo="",url="";
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private String IMEI;
    private String sys="";
    final String model = Build.MODEL;
    final String serial = Build.SERIAL;
    private static final String Datafile = "mytextfile.txt";
    public AutoRegistration() {
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
        urlconnection = new UrlConnection(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        IMEI  = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Count=0;
        if( ReadSystemValue()==1)
            Toast.makeText(this,"Error in reading local Database",Toast.LENGTH_LONG).show();
     //  Auto_Registration();
        stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }


    public Integer ReadTrackingValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue="";
        int res =1;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    ServiceURL=cursor.getString(cursor.getColumnIndex("url"));

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



    private String Auto_Registration()
    {
        String result = "", DisplayResult = "";
        int res = 0;
        try {
            if (!urlconnection.checkConnection()) {
                return   "\n"+getResources().getString(R.string.title_alert_no_intenet);
            }
            result = connectionurlSecData();
            if (result.equals("Success")) {
                if (ReadSystemValue() == 1) {
                    if (BLE == 1) {
                        result = connectionurl();
                        if (!result.equals("Success"))
                            DisplayResult = "\n"+getResources().getString(R.string.Ble_Registration_Failed) + result + "\n";// PAlertDialog( getResources().getString(R.string.Error),   "Ble Registration Failed: "+result);
                    }
                    if (Geofence == 1) {
                        result = connectionurlGeo();
                        if (!result.equals("Success")) {
                            if(result.contains("no_geofence_record_found"))
                            {
                                DisplayResult+= "\n"+getResources().getString(R.string.no_geofence_record_found)  + "\n";
                            }
                            else
                                DisplayResult += getResources().getString(R.string.Geofence_Registration_Failed) + result + "\n"; // PAlertDialog(getResources().getString(R.string.Error), "Geofence RegisTration Failed");
                        }
                    }
                    if (QRCode == 1) {
                        result = connectionurlQR();
                        if (!result.equals("Success")) {
                            if(result.contains("no_QR_record_found"))
                            {
                                DisplayResult+= "\n"+ getResources().getString(R.string.no_QR_record_found)  + "\n";
                            }
                            else
                                DisplayResult += "\n"+ getResources().getString(R.string.QR_Code_Registration_failed) + result+ "\n";
                        }
                    }

                    if (GeoQR == 1) {
                        if(QRCode == 0)
                            result = connectionurlQR();
                        else
                            result="hadqr";
                        if (result.equals("Success") || result.equals("hadqr"))
                        {
                            result="";
                            if(Geofence == 0)
                                result = connectionurlGeo();
                            else
                                result="hadgeo";

                            if (result.equals("Success") || result.equals("hadgeo"))
                            {
                            }
                            else
                            {
                                if(result.contains("no_geofence_record_found"))
                                {
                                    msg= getResources().getString(R.string.no_geofence_record_found)  + "\n";
                                }
                                else
                                    DisplayResult +=getResources().getString(R.string.QR__Code_Geo_Registration_failed)+"\n" + result + "\n";
                            }
                        }

                        else
                        {
                            if(result.contains("no_QR_record_found"))
                            {
                                msg+= getResources().getString(R.string.no_QR_record_found)  + "\n";
                            }
                            else
                                DisplayResult += getResources().getString(R.string.QR__Code_Geo_Registration_failed) + "\n" + result;
                        }
                    }


                    result=  GetEmpPic(Employee_Id);
                    if (!result.equals("Success"))
                    {
                        DisplayResult += "\n"+getResources().getString(R.string.Profile_Photo_Error)+result+ "\n";;
                    }

                    result=  LeaveDesc();
                    if (!result.equals("Success"))
                    {
                        DisplayResult += "\n"+getResources().getString(R.string.Leave_Description_Error) +result+ "\n";;
                    }

                } else {
                    DisplayResult = "\n"+getResources().getString(R.string.Error_in_reading_local_database);// "Error in reading local database";
                    return DisplayResult;
                }
                if (DisplayResult.equals("") || DisplayResult.equals(null))
                    return "";
                else {
                    return DisplayResult;
                }

            } else {
                if (result.equals("Already_Updated")) {
                    DisplayResult= "Already_Updated";
                }
                else
                    DisplayResult=getResources().getString(R.string.Updating_Failed)  + "\n: " + result + "\n";
            }

        } catch (Exception e) {
            Log.e("Exception", getResources().getString(R.string.Registration_Failed) + e.toString());
            DisplayResult=getResources().getString(R.string.Updating_failed_with_some_error) ;
        }
        return  DisplayResult;
    }

    public String connectionurlSecData() {
        String msg = "", urlar = "", result = "", json="",EMPLOYEE_SERVICE_URI="";
        try {


            String lang = "", sys = "", s = "", lang1 = "";
            sys="";
            lang = Locale.getDefault().getDisplayLanguage();
            String number = "";
            if (lang.equals("English")) {
                lang1 = "en";
                number = BadgeNo;
                urlar= url;
            } else {
                lang1 = "ar";
                urlar =  url;
                number =  BadgeNo;// number = 42;
            }

            msg = (getResources().getString(R.string.BadgeNo) + ":" + BadgeNo + "\n" + getResources().getString(R.string.IMEI) + ": " + IMEI);

               if(sys.contains("Eacs"))// = "Eacs";) sys = ;
                s = urlar + "/ElguardianService/Service1.svc/GetAdditionalData/" + "/'" + number + "'/" + lang1+ "/" +1;
            else {
                sys = "Elsmart";
                s = urlar + "/ElguardianService/Service1.svc/GetAdditionalDataElsmart/" + "/'" + number + "'/" + lang1+ "/" +1;
            }
            EMPLOYEE_SERVICE_URI = s.replace(' ', '-');
            URL url = new URL(EMPLOYEE_SERVICE_URI);



            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^"))
            {
                if (json.contains("Already_Updated"))
                    return   "Already_Updated";//getResources().getString(R.string.Already_Updated);
                //   int lnth = json.length();
                //   json = json.substring(1, lnth - 1);
                return ErrorValue(json);
            }

            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            WriteMACAddress(json1);
            FileOutputStream fileout = openFileOutput(fileRegistrationVerify, MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileout);
            String BadgeIMEI = BadgeNo + "~" + IMEI + "~" + url+ "~" + model + "~" + serial + "~" + json1 + "~" + EMPLOYEE_SERVICE_URI + "~" + sys;
            outputStreamWriter.write(BadgeIMEI);
            outputStreamWriter.close();
            // PAlertDialog( getResources().getString(R.string.Information), "Secondary Data added successfully" + "\n\n" + msg);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Success";
    }

    public Integer WriteMACAddress(String json1)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        Integer result=0;
        int res = 0;
        result=Deletedata();
        String[] urlValue = json1.split("~");
        if(result==1) {

            try {
                if (urlValue.length > 0) {
                    database = db.getWritableDatabase();
                    database.execSQL("INSERT INTO Registration(BadgeNo,DeptName,Title,IssuedDate,ValidFromDate,ExpiryDate,FullName,Employee_Id,Tracking_Type_Id,Company_Id,Tracking,QRCode,Geofence,BLE,UserRole,Interval,MinDist,Cust_Id,Customer_Virtual_Id,GeoQR,url,SysType)" +
                            "VALUES('" + urlValue[0] + "','" + urlValue[1] + "','" + urlValue[2] + "','" + urlValue[3].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[4].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[5].replaceAll("[^' ':/\\w\\[\\]]", "") + "','" + urlValue[6] + "'," + urlValue[7] + "," + urlValue[8] + "," + urlValue[9] + "," + urlValue[10] + "," + urlValue[11] + "," + urlValue[12] + "," + urlValue[13]+ "," + urlValue[14]+ ","+urlValue[15]+ ","+urlValue[16]+","+urlValue[17]+","+urlValue[18]+","+urlValue[19]+",'"+url+ "','"+sys+"')");
                    res = 1;

                }
            } catch (Exception ex) {
                res = 0;
                Log.d(TAG, ex.getMessage());
            }
        }
        return res;
    }
    public Integer Deletedata()
    {
        Integer res=0;
        try {
            database = db.getWritableDatabase();
            database.execSQL("Delete from Registration" );// (date,BadgeNo,Name,Ter,direction,empid)VALUES('"+txtdatetime.getText()+"','"+badgeno+"','"+name+"' ,'"+compara.termo+"','"+s+"' ,"+empID+")" );
            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
        }
        return res;

    }
    public Integer ReadSystemValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode,Employee_Id,BadgeNo,url,GeoQR  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {
                    Count++;
                    BLE = Integer.valueOf(cursor.getString(cursor.getColumnIndex("BLE")));
                    Geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
                    QRCode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRCode")));
                    Employee_Id= Integer.valueOf(cursor.getString(cursor.getColumnIndex("Employee_Id")));
                    BadgeNo = cursor.getString(cursor.getColumnIndex("BadgeNo"));
                    url  =   cursor.getString(cursor.getColumnIndex("url"));
                    GeoQR= Integer.valueOf(cursor.getString(cursor.getColumnIndex("GeoQR")));
                    sys= cursor.getString(cursor.getColumnIndex("SysType"));
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


    public String connectionurl() {

        String  s="", lang="",number="",urlar="",res="",json="",EMPLOYEE_SERVICE_URI="";
        try {


            String badgeno = BadgeNo;

            msg = ( getResources().getString(R.string.BadgeNo)+ ": " + badgeno+ "\n" + getResources().getString(R.string.IMEI)+": "  + IMEI);

            //  int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {

                number=badgeno;
                urlar= url;
            }
            else {

                number =  urlconnection.arabicToDecimal(badgeno); // number = 42;
                urlar=  urlconnection.arabicToDecimal(url);
            }

            if(sys.contains("Eacs"))
            {
                s = urlar + "/ElguardianService/Service1.svc/GetCardholderData/" + "/'" + number + "'/'" + IMEI + "'/'" + model + "'/'" + serial + "'";
            }
            else
                s = urlar + "/ElguardianService/Service1.svc/GetCardholderDataElsmart/" + "/'" + number + "'/'" + IMEI + "'/'" + model + "'/'" + serial + "'";

            EMPLOYEE_SERVICE_URI = s.replace(' ','-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^"))
            {
                return ErrorValue(json);
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            int len = json1.length();
            byte[] data1 = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data1[i / 2] = (byte) ((Character.digit(json1.charAt(i), 16) << 4) + Character.digit(json1.charAt(i + 1), 16));
            }
            FileOutputStream fileout = openFileOutput(Datafile, MODE_PRIVATE);
            fileout.write(data1);
            fileout.close();
        } catch (Exception e) {
            e.printStackTrace();
            res=e.getMessage();
            return res;
        }
        return "Success";
    }

    public String connectionurlGeo() {

        String  s="", lang="",number="",urlar="",res="",json="",json1="",EMPLOYEE_SERVICE_URI="";
        int lnth=0;
        try {
            String badgeno = BadgeNo;//.getText().toString();
            msg = ( getResources().getString(R.string.BadgeNo)+ ": " + badgeno+ "\n" + getResources().getString(R.string.IMEI)+": "  + IMEI);
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {
                lang = "en";
                number=badgeno;
                urlar= url;
            }
            else {
                lang = "ar";
                urlar= urlconnection.arabicToDecimal(url);
                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
            }

            lang= Locale.getDefault().getDisplayLanguage();
            if(sys.contains("Eacs"))
            {
                s = urlar + "/ElguardianService/Service1.svc/GetGeoData/" + "/'" + number+"'"+ "/'" +lang;
            }
            else
                s = urlar + "/ElguardianService/Service1.svc/GetGeoData/" + "/'" + number+"'"+ "/'" +lang;

            EMPLOYEE_SERVICE_URI = s.replace(' ','-');
            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^"))
            {

                return ErrorValue(json);
            }
            lnth = json.length();
            json1 = json.substring(1, lnth - 1);
            if(WriteGeofanceData(json1)!=1) {
                res= getResources().getString(R.string.Error_in_inserting_local_database);//"Error in inserting in Local Database";
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res= e.getMessage();
            return res;
        }
        return "Success";

    }


    public Integer WriteGeofanceData(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            String[] Val=Value.split(";");
            database=db.getWritableDatabase();
            database.execSQL("delete from  Geofence");
            for(int i=0;i<Val.length;i++)
            {
                String[] Val1=Val[i].split("~");
                database=db.getWritableDatabase();
                database.execSQL("INSERT INTO Geofence(GeoID,Lat,Long,Radius,GeoName,KeyName,Badgeno,Shape_Name,Group_Name,Zoom_value)VALUES("+Val1[0]+","+Val1[1]+","+Val1[2]+","+Val1[3]+",'"+Val1[4]+"','"+Val1[4]+"','"+Val1[5]+"','"+Val1[6]+"','"+Val1[7]+"','"+Val1[8]+"')" );
            }

            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
            Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG ).show();
        }
        return res;
    }

    public Integer WriteGeofanceDataQR(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            String[] Val=Value.split(";");
            database=db.getWritableDatabase();
            database.execSQL("delete from  QRCode_Permission");
            for(int i=0;i<Val.length;i++)
            {
                String[] Val1=Val[i].split("~");
                database=db.getWritableDatabase();
                database.execSQL("INSERT INTO QRCode_Permission(GeoID,CustID,Qrcode,BadgeNo,TerID,QRId,QRCode_Name)VALUES("+Val1[0]+","+Val1[1]+",'"+Val1[2]+"',"+Val1[3]+","+Val1[4]+","+Val1[5]+",'"+Val1[6]+"')" );
            }

            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
            Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG ).show();
        }
        return res;
    }

    public String connectionurlQR() {

        String  s="", lang="",number="",urlar="",result="",json="",EMPLOYEE_SERVICE_URI="";
        try {
            String badgeno = BadgeNo;

            msg = ( getResources().getString(R.string.BadgeNo)+ ": " + badgeno+ "\n" + getResources().getString(R.string.IMEI)+": "  + IMEI);
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {
                lang = "en";
                number=badgeno;
                urlar= urlconnection.arabicToDecimal(url);
            }
            else {
                lang = "ar";
                urlar= urlconnection.arabicToDecimal(url);
                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
            }

            //  int index = radioSystem.indexOfChild(findViewById(radioSystem.getCheckedRadioButtonId()));
            lang= Locale.getDefault().getDisplayLanguage();

            if(lang.equals("English")) {

                number=badgeno;
                urlar= url;
            }
            else {

                number = urlconnection.arabicToDecimal(badgeno); // number = 42;
                urlar= urlconnection.arabicToDecimal(url);
            }

            if(sys.contains("Eacs"))
            {
                s = urlar + "/ElguardianService/Service1.svc/GetGeoDataQR/" + "/'" + number+"'"+ "/'" +lang;
            }
            else
                s = urlar + "/ElguardianService/Service1.svc/GetGeoDataQR/" + "/'" + number+"'"+ "/'" +lang;
            EMPLOYEE_SERVICE_URI = s.replace(' ','-');

            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^"))
            {
                return ErrorValue(json);
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            if(WriteGeofanceDataQR(json1)!=1) {
                result=getResources().getString(R.string.Error_in_inserting_local_database);
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            //  PAlertDialog("ERROR", e.getMessage());
            result=e.getMessage();
            return result;
        }
        return "Success";

    }



    public String ErrorValue(String json)
    {
        String getstring="";
        if (json.contains("`")) {
            getstring = json;
            int iend = getstring.indexOf("`");
            if (iend != -1)
                getstring = json.substring(1, json.length()-1); //this will give abc

        }
        if (json.contains("^")) {
            if(json.contains("Server Offline") )
            {
                getstring=getResources().getString(R.string.Server_Offline);
            }
            if(json.contains("^within_Time_Out"))
            {
                getstring=getResources().getString(R.string.Please_wait);
            }
            else
                getstring=json.substring(1,json.length());



        }
        return getstring;
    }


    public String GetEmpPic(int Empid) {

        String  urlar = "", result = "", json="",EMPLOYEE_SERVICE_URI="";
        String s="",res="";
        InputStream in = null;
        BufferedOutputStream out = null;
        final int IO_BUFFER_SIZE = 64;
        final URL urlObject;
        final URLConnection myConn;
        byte[] data=new byte[8096];
        try {
            urlar= url;
            s = urlar+"/ElguardianService/Service1.svc/"  + "/GetImage" + "/" + Empid;
            EMPLOYEE_SERVICE_URI = s.replace(' ','-');
            data= urlconnection.ServerConnection_Pic(EMPLOYEE_SERVICE_URI);

            if (data[0]==-100)//contains("`") || json.contains("^"))
            {
                res= getResources().getString(R.string.Please_wait) ;//"Please wait for defined time and try again for server";
                return res;
            }
            if (data[0]==-101)//contains("`") || json.contains("^"))
            {
                res=getResources().getString(R.string.Server_Offline);//"Server Offline";
                return res;
            }
            if (data[0]==-102)//contains("`") || json.contains("^"))
            {
                res=getResources().getString(R.string.Other_server_connection_error);//"Other Server Connection Error";
                return res;
            }
            UpdateLogData(Empid,data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,getResources().getString(R.string.Network_Connection_Error), Toast.LENGTH_LONG).show();
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
        return res="Success";

    }


    public Integer UpdateLogData(int ID,byte[] img)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            database=db.getWritableDatabase();
            database.execSQL("delete from  Profile_Photo");
            database = db.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("pic", img);

// updating row
            Cursor cursor = database.rawQuery("SELECT Employee_Id,pic FROM  Profile_Photo where Employee_Id = " + ID, null);
            if (cursor.moveToNext()) {
                database.update("Profile_Photo", values, "Employee_Id" + " = ?", new String[]{String.valueOf(ID)});
            } else {
                values.put("Employee_Id", ID);
                database.insert("Profile_Photo", null, values);
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


    public String LeaveDesc() {

        String  s="", lang="",number="",urlar="",result="",json="",EMPLOYEE_SERVICE_URI="";
        try {
            String badgeno = url;
            number=badgeno;
            urlar= urlconnection.arabicToDecimal(url);


            number=badgeno;
            urlar= url;

            if(sys.contains("Eacs"))
            {
                s = urlar + "/ElguardianService/Service1.svc/LeaveDesc";
            }
            else
                s = urlar + "/ElguardianService/Service1.svc/LeaveDesc";
            EMPLOYEE_SERVICE_URI = s.replace(' ','-');

            json = urlconnection.ServerConnection(EMPLOYEE_SERVICE_URI);
            if (json.contains("`") || json.contains("^"))
            {
                return ErrorValue(json);
            }
            int lnth = json.length();
            String json1 = json.substring(1, lnth - 1);
            if(WriteLeaveDescription(json1)!=1) {
                result=getResources().getString(R.string.Leave_Description_Error);
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            //  PAlertDialog("ERROR", e.getMessage());
            result=e.getMessage();
            return result;
        }
        return "Success";

    }

    public Integer WriteLeaveDescription(String Value)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res =0;
        try {
            String[] Val=Value.split(";");
            database=db.getWritableDatabase();
            database.execSQL("delete from  LeaveDesc");
            for(int i=0;i<Val.length;i++)
            {
                String[] Val1=Val[i].split("~");
                database=db.getWritableDatabase();
                database.execSQL("INSERT INTO LeaveDesc(LeaveDescID,Description,LeaveCode,OfficialDuty,Description_Ar,LeaveCode_Ar)VALUES("+Val1[0]+","+Val1[1]+","+Val1[2]+","+Val1[3]+","+Val1[4]+","+Val1[5]+")" );
            }

            res=1;
        }
        catch (Exception ex) {
            res=0;
            Log.d(TAG, ex.getMessage());
            Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG ).show();
        }
        return res;
    }



}

