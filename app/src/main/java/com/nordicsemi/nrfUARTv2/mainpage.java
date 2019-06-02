package com.nordicsemi.nrfUARTv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;

public class mainpage extends Activity {
    private static final String TAG = "Main Page";
    Controllerdb db = new Controllerdb(this);
    SQLiteDatabase database;
    private static int BLE = 0;
    private static int QRCode = 0;
    private static int Geofence = 0;
    private static int Tracking=0;
    private static int GeoQR=0;
    private RadioGroup radioGruopPunchType;
    private RadioButton radBle,radQR,radGeo,radGeoQR;
    Button btn_Ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        btn_Ok=(Button) findViewById(R.id.btn_Ok);
        radioGruopPunchType = (RadioGroup) findViewById(R.id.radioPunchtype);
        radBle=(RadioButton) findViewById(R.id.radBle);
        radQR=(RadioButton) findViewById(R.id.radQR);
        radGeo=(RadioButton) findViewById(R.id.radGeo);
        radGeoQR=(RadioButton) findViewById(R.id.radGeoQR);
        btn_Ok.setEnabled(true);
        if (ReadTrackingValue() == 1) {
            if (BLE == 1 && QRCode == 0 && Geofence == 0 && GeoQR==0) {

            Intent ble = new Intent(mainpage.this, MainActivity.class);
            startActivity(ble);
        }
        if (BLE == 0 && QRCode == 0 && Geofence == 1 && GeoQR==0) {
            Intent ble = new Intent(mainpage.this, markattendanceActivity.class);
            startActivity(ble);
        }
        if (BLE == 0 && QRCode == 1 && Geofence == 0 && GeoQR==0) {
            Intent QR = new Intent(mainpage.this, barcodemain.class);
            startActivity(QR);
        }

            if (BLE == 0 && QRCode == 0 && Geofence == 0 && GeoQR==1) {
                Intent GeoQR = new Intent(mainpage.this, Geo_QR.class);
                startActivity(GeoQR);
            }

            if (BLE ==1 && QRCode == 1 && Geofence == 0) {
                radBle.setVisibility(View.VISIBLE);
                radQR.setVisibility(View.VISIBLE);
                radGeo.setVisibility(View.INVISIBLE);

            }

            if (BLE ==1 && QRCode == 0 && Geofence == 1) {
                radBle.setVisibility(View.VISIBLE);
                radQR.setVisibility(View.INVISIBLE);
                radGeo.setVisibility(View.VISIBLE);

            }


            if (BLE ==0 && QRCode == 1 && Geofence == 1) {
                radBle.setVisibility(View.INVISIBLE);
                radQR.setVisibility(View.VISIBLE);
                radGeo.setVisibility(View.VISIBLE);

            }
            if (BLE ==1 && QRCode ==1 && Geofence ==1) {
                radBle.setVisibility(View.VISIBLE);
                radQR.setVisibility(View.VISIBLE);
                radGeo.setVisibility(View.VISIBLE);


            }

            if (GeoQR==1) {
                radGeoQR.setVisibility(View.VISIBLE);
            }
            else
                radGeoQR.setVisibility(View.INVISIBLE);


            if (BLE ==0 && QRCode ==0 && Geofence ==0 && GeoQR==0) {
                radBle.setVisibility(View.INVISIBLE);
                radQR.setVisibility(View.INVISIBLE);
                radGeo.setVisibility(View.INVISIBLE);
                //  PAlertDialog( getResources().getString(R.string.Information),  "You are not register for any punch type");
                // Toast.makeText(this, "You are not register for any punch type", Toast.LENGTH_LONG).show();
                if (Tracking == 1) {
                    Intent Tracking = new Intent(mainpage.this, Tracking.class);
                    startActivity(Tracking);
                } else {
                    btn_Ok.setEnabled(false);
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.Information)
                            .setMessage("You are not register for any punch type \n Do you want to register again")
                            .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mainpage.this, RegistrationActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.popup_no, null)
                            .show();


                }
            }


        } else {
            Toast.makeText(this, "Error in Reading Data", Toast.LENGTH_LONG).show();
        }

      //  addListenerOnButton();

        btn_Ok.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                   int selectedId = radioGruopPunchType.getCheckedRadioButtonId();
                if (selectedId == R.id.radBle) {
                    Intent ble = new Intent(mainpage.this, MainActivity.class);
                    startActivity(ble);

                } else if (selectedId == R.id.radGeo)

                {
                    Intent ble = new Intent(mainpage.this, markattendanceActivity.class);
                    startActivity(ble);
                } else if (selectedId == R.id.radQR)
                {
                    Intent QR = new Intent(mainpage.this, barcodemain.class);
                    startActivity(QR);
                }
                else if (selectedId == R.id.radGeoQR)
                {
                    Intent QR = new Intent(mainpage.this, Geo_QR.class);
                    startActivity(QR);
                }

            }
        });
    }

    public Integer ReadTrackingValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode,Tracking,GeoQR  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    BLE = Integer.valueOf(cursor.getString(cursor.getColumnIndex("BLE")));
                    Geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
                    QRCode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRCode")));
                    Tracking = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Tracking")));
                    GeoQR = Integer.valueOf(cursor.getString(cursor.getColumnIndex("GeoQR")));
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


    public void addListenerOnButton() {



        try {
            radioGruopPunchType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    //   int selectedId = radioSystem.getCheckedRadioButtonId();

                    if (i == R.id.radBle) {
                        Intent ble = new Intent(mainpage.this, MainActivity.class);
                        startActivity(ble);

                    } else if (i == R.id.radGeo)

                    {
                        Intent ble = new Intent(mainpage.this, markattendanceActivity.class);
                        startActivity(ble);
                    } else if (i == R.id.radQR)
                    {
                        Intent QR = new Intent(mainpage.this, barcodemain.class);
                        startActivity(QR);
                    }
                    else if (i == R.id.radGeoQR)
                    {
                        Intent QR = new Intent(mainpage.this, Geo_QR.class);
                        startActivity(QR);
                    }

                }
            });


        } catch (Exception e) {

            Log.e("", e.getMessage());
            Toast.makeText(mainpage.this, "Error in this page",
                    Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed() {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_message)
                    .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainpage.this.moveTaskToBack(true);
                        }
                    })
                    .setNegativeButton(R.string.popup_no, null)
                    .show();

    }


    private void PAlertDialog(String title, String msg)
    {


        AlertDialog.Builder builder = new AlertDialog.Builder(mainpage.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton( getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

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


}
