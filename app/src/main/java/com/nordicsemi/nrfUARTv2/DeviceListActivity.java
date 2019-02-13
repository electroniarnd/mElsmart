
/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.nordicsemi.nrfUARTv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceListActivity extends Activity {


    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
   // private ScanCallback mScanCallback;


    private BluetoothAdapter mBluetoothAdapter;

   // private BluetoothAdapter mBtAdapter;
    private TextView mEmptyList;
    public static final String TAG = "DeviceListActivity";

    List<BluetoothDevice> deviceList;
    private DeviceAdapter deviceAdapter;
    private ServiceConnection onService = null;
    Map<String, Integer> devRssiValues;
    private static final long SCAN_PERIOD = 10000; //scanning for 10 seconds
    private Handler mHandler;
    private boolean mScanning;
    private static final String  FileMacAddress="Mac.txt";
    static final int READ_BLOCK_SIZE = 500;
    private static final String  macid= "00:12:A1:00:03:06";
    private  int AutoPuch=0;
    Controllerdb db =new Controllerdb(this);
    SQLiteDatabase database;
    String s="";
    int cnt=0;
    public static UUID RX_SERVICE_UUID = UUID.fromString("0000E0FF-3C17-D293-8E48-14FE2E4DA212");
    public static UUID RX_SERVICE_UUIDESP32=UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if(ReadAutoPuchValue()==1)
        {
         if(AutoPuch==1)
              if(ReadMACValue()==1) {
             //    if(cnt>0)
              //    setTheme(R.style.AppBaseTheme1);
              }
             else
                 Toast.makeText(this,"Error in read MacAddress value from database",Toast.LENGTH_LONG).show();

          // android:theme="@android:style/Theme.NoDisplay" ;

          //      android:theme="@android:style/Theme.Dialog";
      }
      else
          Toast.makeText(this,"Error in Reading AutoPunch Value",Toast.LENGTH_LONG).show();

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
           setContentView(R.layout.device_list);
        android.view.WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.gravity=Gravity.TOP;
        layoutParams.y = 200;
        mHandler = new Handler();


        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }



        populateList();
        mEmptyList = (TextView) findViewById(R.id.empty);


      //  try {
          //  Thread.sleep(10000);
      //  } catch (InterruptedException e) {
        //    e.printStackTrace();
      //  }
      //  if (deviceList.size()==1)
     //   {
           // Bundle b = new Bundle();
           // b.putString(BluetoothDevice.EXTRA_DEVICE, deviceList.get(0).getAddress());

       //     Intent result = new Intent();
        ///    result.putExtras(b);
        //    setResult(Activity.RESULT_OK, result);
       //     finish();
      //  }

        Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	if (mScanning==false) scanLeDevice(true);
            	else {


                    Intent result = new Intent();
                    Bundle b = new Bundle();
                    b.putString("name", "cancel");
                    result.putExtras(b);

                    setResult(Activity.RESULT_OK, result);
                    finish();


                }
            }
        });

    }

    private void populateList() {
        /* Initialize device list container */
        Log.d(TAG, "populateList");
        deviceList = new ArrayList<BluetoothDevice>();
        deviceAdapter = new DeviceAdapter(this, deviceList);
        devRssiValues = new HashMap<String, Integer>();

        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(deviceAdapter);

        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

           scanLeDevice(true);

    }
    
    private void scanLeDevice(final boolean enable) {

        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        final Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        if (enable) {



            if (Build.VERSION.SDK_INT >= 21) {
                Log.d(TAG, "Preparing for scan...");

                // set up v21 scanner

                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<>();

             //filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(RX_SERVICE_UUIDESP32)).build());
              filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(RX_SERVICE_UUID)).build());
             //   filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid( UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E"))).build());
              //  filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid( UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E"))).build());
                filters.add( new ScanFilter.Builder().setDeviceAddress("24:0A:C4:AF:6B:FA").build());
            }




            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
					mScanning = false;
                    if ((Build.VERSION.SDK_INT < 21) ) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(leScanCallback);

                    }



                    cancelButton.setText(R.string.scan);
                    if(AutoPuch==1)
                        finish();

                }
            }, SCAN_PERIOD);

            mScanning = true;

            if ((Build.VERSION.SDK_INT < 21) ) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters,settings,leScanCallback);
            }
            cancelButton.setText(R.string.cancel);
        } else {
            mScanning = false;
            if ((Build.VERSION.SDK_INT < 21) ) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(leScanCallback);

            }
            cancelButton.setText(R.string.scan);
        }

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	
                              addDevice(device,rssi);
                }
            });
        }
    };
    
    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }


        if(AutoPuch==1)
        {
            String address1 = device.getAddress(), macid = "";
            boolean bool = false;
            String[] macdata = s.split("~");
            for (int i = 0; i < macdata.length; i++) {
                if (!macdata[i].equals("")) {
                    macid = macdata[i];
                    if (device.getAddress().equals(macid)) {
                        bool = true;

                        break;
                    }
                }

            }

            if (bool) {
                Intent result = new Intent();
                Bundle b = new Bundle();
                b.putString(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
                result.putExtras(b);
                setResult(Activity.RESULT_OK, result);
                scanLeDevice(false);
                finish();
            }

        }
        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
        	deviceList.add(device);
            mEmptyList.setVisibility(View.GONE);



            
            deviceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
       
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        setVisible(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        scanLeDevice(false);

    
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanLeDevice(false);
        
    }

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
    	
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = deviceList.get(position);

            scanLeDevice(false);
            Bundle b = new Bundle();
            b.putString(BluetoothDevice.EXTRA_DEVICE, deviceList.get(position).getAddress());

            Intent result = new Intent();
            result.putExtras(b);
            setResult(Activity.RESULT_OK, result);
            finish();
        	
        }
    };


    
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
    }
    
    class DeviceAdapter extends BaseAdapter {
        Context context;
        List<BluetoothDevice> devices;
        LayoutInflater inflater;

        public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;

            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.device_element, null);
            }

            BluetoothDevice device = devices.get(position);
            final TextView tvadd = ((TextView) vg.findViewById(R.id.address));
            final TextView tvname = ((TextView) vg.findViewById(R.id.name));
            final TextView tvpaired = (TextView) vg.findViewById(R.id.paired);
            final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);

            tvrssi.setVisibility(View.VISIBLE);
            byte rssival = (byte) devRssiValues.get(device.getAddress()).intValue();
            if (rssival != 0) {
                tvrssi.setText("Rssi = " + String.valueOf(rssival));
            }

            tvname.setText(device.getName());
            tvadd.setText(device.getAddress());
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.i(TAG, "device::"+device.getName());
                tvname.setTextColor(Color.WHITE);
                tvadd.setTextColor(Color.WHITE);
                tvpaired.setTextColor(Color.GRAY);
                tvpaired.setVisibility(View.VISIBLE);
                tvpaired.setText(R.string.paired);
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.WHITE);
                
            } else {
                tvname.setTextColor(Color.WHITE);
                tvadd.setTextColor(Color.WHITE);
                tvpaired.setVisibility(View.GONE);
                tvrssi.setVisibility(View.VISIBLE);
                tvrssi.setTextColor(Color.WHITE);
            }
            return vg;
        }
    }
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



 //   public String MacValue() {
      //  String s = "";
      //  File FileMacAddrss = new File(getFilesDir() + File.separator + FileMacAddress);
      //  if ((FileMacAddrss.exists())) {
         //   FileInputStream fileIn = null;
         //   try {
          //      fileIn = openFileInput(FileMacAddress);
          //  } catch (FileNotFoundException e) {
          //      e.printStackTrace();
          //  }
          //  if (fileIn != null) {
           //     InputStreamReader InputRead;
            //    InputRead = new InputStreamReader(fileIn);

             //   char[] inputBuffer = new char[READ_BLOCK_SIZE];

              //  int charRead;

              //  try {
                //    while ((charRead = InputRead.read(inputBuffer)) > 0) {
                       // // char to string conversion
                   //     String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                     //   s += readstring;
                   // }
                   // InputRead.close();

                //} catch (IOException e) {
               //     e.printStackTrace();
               // }
          //  } else {

//  //                  Toast.makeText(RegistrationActivity.this, "Empty File",
//  //                          Toast.LENGTH_SHORT).show();

               // PAlertDialog(getResources().getString(R.string.Information), getResources().getString(R.string.File_Empty));


            //}
        //}
      //  return  s;
  //  }

    private void PAlertDialog(String title, String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
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


    public Integer ReadAutoPuchValue()
    {
        int res =0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT AutoPunch FROM  system_setting", null);
            if (cursor.moveToFirst()) {
                do {
                    AutoPuch= Integer.valueOf( cursor.getString(cursor.getColumnIndex("AutoPunch")));
                } while (cursor.moveToNext());
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


    public Integer ReadMACValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        s="";
        int count=0;
        int res =0,i=0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Device", null);
            count=  cursor.getCount();
            if (cursor.moveToFirst()) {
                do {
                    i++;
                    cnt++;
                    if(i==count)
                    s +=   cursor.getString(cursor.getColumnIndex("MacId"));
                    else
                        s +=   cursor.getString(cursor.getColumnIndex("MacId"))+"~";
                } while (cursor.moveToNext());
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




    private void beginScan() {

        // all api versions
        scanLeDevice(true);
    }




        private ScanCallback leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                addDevice(result.getDevice(), result.getRssi());
            }


            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult sr : results) {
                    Log.i("ScanResult - Results", sr.toString());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "Scan Failed Error Code: " + errorCode);
                if (errorCode == 1) {
                    Log.e(TAG, "Already Scanning: "); // + isScanning);
                    //isScanning = true;
                } else if (errorCode == 2) {
                    // reset bluetooth?
                }
            }

        };




}
