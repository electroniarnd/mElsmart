
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

package com.electronia.mElsmart;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.electronia.mElsmart.Common.UrlConnection;
import com.electronia.mElsmart.Services.TestService;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.electronia.mElsmart.circularbuttom.CircularProgressButton;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.electronia.mElsmart.MainActivity.FRAMERXINGSTATE.WAITFrameProcess;
import static com.electronia.mElsmart.MainActivity.FRAMERXINGSTATE.WAITSTX1;
import static com.electronia.mElsmart.MainActivity.TAG;


public class MainActivity extends Fragment {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private TextView mMsgView;
    private boolean isGPS = false;


    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_ENABLE_FT = 3;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;
    private static int a = 0;
    private static int VisibleMode = 0;
    private static int Tracking = 0;
    private static int AutoPunch = 0;
    private static int Biometric = 0;
    private int connection_value = -1;
    private static int bytcount = 0;
    private static int framesize = 0;
    private static int autostartvalue = 0;
    private static int startvalue = 0;
    boolean isRunning = false;
    CountDownTimer cTimer = null;
    private boolean mAlreadyStartedService = false;

    //Elsmart
    private static final int elsmartIN_OUT_Punch = 0x01;
    private static final int elsmartInvalidDoor = 0x02;
    private static final int elsmartSystemLocked = 0x04;
    private static final int elsmartExitEntryMissed = 0x08;
    private static final int elsmartExpiredCard = 0x10;
    private static final int elsmartCardType1 = 0x20;
    private static final int elsmartCardType2 = 0x40;
    private static final int elsmartBlackListedCard = 0x80;
    //eacs
    private static final int eacsREADER_ERROR_SET = 0x22;
    private static final int eacsREADER_ERROR_CLEAR = 0x23;
    private static final int eacsDOOR_KEPT_OPEN_ALARM = 0x24;
    private static final int eacsDOOR_FORCED_OPEN_ALARM = 0x25;
    private static final int eacsEPB_ALARM = 0x26;
    private static final int eacsEMBU_ALARM = 0x27;
    private static final int eacsDOOR_OPENED_BY_COMMAND = 0x28;
    private static final int eacsBIOMETRIC_ERROR_SET = 0x29;
    private static final int eacsBIOMETRIC_ERROR_CLEAR = 0x2A;
    private static final int eacsDATE_TIME_ALARM_SET = 0x2B;
    private static final int eacsDATE_TIME_ALARM_CLEAR = 0x2C;


    private static final int OK = 0;
    public static final int eacsBLACKLISTED = 1;
    public static final int eacsACCESSDENIED = 2;
    public static final int eacsCARDEXPIRED = 3;
    public static final int eacsANTIPASSBKERROR = 4;
    public static final int eacsACCLOCK = 5;
    public static final int eacsACCHOLIDAY = 6;
    public static final int eacsACCLEAVE = 7;
    public static final int eacsPINFAIL = 8;
    public static final int eacsBIOFAIL = 9;
    public static final int eacsDURESSCODE = 10;
    public static final int eacsACCSCHEDULE = 11;
    public static final int FLASH_WRITE_FAIL = 12;
    public static final int BIO_DEVICE_ERR = 13;
    public static final int eacsACCESCORT = 14;
    public static final int eacsCARDTWING = 15;
    public static final int SYSTEM_HALTED = 16;
    public static final int FPTIMEOUTERROR = 17;
    public static final int NOTSUPPORTED = 18;
    public static final int NOTENROLLED = 19;
    public static final int NOFINGERDETECT = 20;
    public static final int READER_ERROR = 21;
    public static final int Cutomer_Mismatch = 22;
    public static final int General_Error = 23;

    public static int BLE = 0;
    public static int geofence = 0; //Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
    public static int QRCode = 0;

    public static final int MAX_BUFF = 8096;      //includes tcpip buffer
    public static final int MAXRXFRAMESize = 4096;
    byte[] rxBuff = new byte[MAX_BUFF];
    byte[] tmpBuff = new byte[MAX_BUFF];

    public static final int STX1 = 0xAA;
    public static final int STX2 = 0x55;
    public static final int ETX = 0xF0;
    //Frame Receive State
    String requiredDate = "", requiredTime = "";
    public static final int PollSourceID = 0x83;
    static final int READ_BLOCK_SIZE = 4096;

    //Command list
    public static final byte cmdWhoAmI = (byte) 0x88;
    public static final int cmdGetDateTime = 0x51;
    public static final int cmdSetTerID = 0x52;

    public int rxByteCnt = 0;
    public int rxFrameSize = 0;
    private short rxprIndex = 0;
    int count = 0;
    public byte[] TXvalue1=new byte[15];
    public byte[] TXvalue2=new byte[15];
    public byte[] tmpBuff1;
    TelephonyManager telephonyManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    UrlConnection urlconnection;
    private BackReciver backReciver;
    public static MainActivity newInstance() {
        MainActivity fragment = new MainActivity();
        return fragment;
    }


    public enum FRAMERXINGSTATE {
        NONE,
        WAITSTX1,
        WAITSTX2,
        WAITDATA,
        WAITFrameProcess,
        ReadyForNextCmd;
    }

    public static class myVirator {

        public static int sclick = 200;
        public static int longclick = 500;
    }

    // public enum EACS_AlarmNo {OK, BLACKLISTED,ACCESSDENIED,CARDEXPIRED,ANTIPASSBKERROR,ACCLOCK,ACCHOLIDAY, ACCLEAVE,PINFAIL,BIOFAIL, DURESSCODE,ACCSCHEDULE,FREE12,FREE13,ACCESCORT,CARDTWING };
    private FRAMERXINGSTATE frameProcessState = WAITSTX1;
    private TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState = UART_PROFILE_DISCONNECTED;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;


    //////////////// private Button  btnSend;

    private TextView txtregistered, txtbadgeno1, txtfullname, txtdate, ErrorMessage, deviceLabel;
    private TextView txtdatetime, lblname, lbldate, lblpunchtype, txttime, punchTime, txtname, txtmessagecode;
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private static final String Datafile = "mytextfile.txt";
    private static final String DirectionFile = "direction.txt";
    MediaPlayer in = null, out = null, error = null, Access_Denied = null, Card_Expired = null, Blacklisted = null, AntiPassBackError = null;
    private String sysvalue = "";
    Controllerdb db;
    SQLiteDatabase database;
    CircularProgressButton btnConnectDisconnect;
    private Context mContext;
    private Activity mActivity;
    private LinearLayout mRelativeLayout;
    private Button mButton;
    private PopupWindow mPopupWindow;
    private static View view;
    private static FragmentManager fragmentManager;

    public MainActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main, container, false);
        // initViews();


        // btnConnectDisconnect.setOnClickListener(this);
        //  setListeners();
        return view;
    }

    private void registebackReceiver(){
        backReciver = new BackReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TestService.BACK_INFO);

        getActivity().registerReceiver(backReciver, intentFilter);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        db = new Controllerdb(getContext());
        btnConnectDisconnect = (CircularProgressButton) view.findViewById(R.id.btn_select);
        btnConnectDisconnect.setIndeterminateProgressMode(true);
        txtfullname = (TextView) view.findViewById(R.id.txtfullname);
        txtdate = (TextView) view.findViewById(R.id.txtdate);
        deviceLabel = (TextView) view.findViewById(R.id.deviceLabel);
        ErrorMessage = (TextView) view.findViewById(R.id.ErrorMessage);
        mRelativeLayout = (LinearLayout) view.findViewById(R.id.linearLayout2);
        txtregistered = (TextView) view.findViewById(R.id.txtregistered);
        checkAndRequestPermissions();//  requestPermissions();
        urlconnection = new UrlConnection(getActivity().getApplicationContext());
       // count = telephonyManager.getDeviceId().length();
       // TXvalue1 = telephonyManager.getDeviceId().getBytes();

        try{
            registebackReceiver();
        } catch (Exception e){
            // already registered
        }



        count =  Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID).length();
        TXvalue2 = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID).getBytes();
       if(TXvalue2.length>=15)
        TXvalue1 = Arrays.copyOfRange(TXvalue2,0,15);
       else
       {
           int lenth=0;
           lenth=TXvalue1.length;
           lenth=lenth-1;
          for (int i = TXvalue2.length-1; i >=0; i--) {
               TXvalue1[lenth] = TXvalue2[i];
              lenth--;
           }
       }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to get IMEI - requesting it");
                String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getActivity(),getResources().getString(R.string.ble_not_available), Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }
        if (!mBtAdapter.isEnabled()) {
            try {
                mBtAdapter.enable();
                Thread.sleep(1000);
            } catch (Exception ex) {
                Toast.makeText(getActivity(), getResources().getString(R.string.ble_enable_problem), Toast.LENGTH_LONG).show();
            }

        }

        File Registrationfile = new File(getActivity().getFilesDir() + File.separator + fileRegistrationVerify);
        File Valuefile = new File(getActivity().getFilesDir() + File.separator + Datafile);
        if ((Registrationfile.exists())) {
            FileInputStream fileIn = null;
            try {
                fileIn = getActivity().openFileInput(fileRegistrationVerify);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader InputRead;
            InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";
            int charRead;

            try {
                while ((charRead = InputRead.read(inputBuffer)) > 0) {
                    // char to string conversion
                    String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                    s += readstring;
                }
                String[] urldata = s.split("~");
                txtregistered.setText(getResources().getString(R.string.Registered));
                sysvalue = urldata[13].toLowerCase().toString();
                InputRead.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("", e.getMessage());
            } catch (Exception e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.Error),
                        Toast.LENGTH_SHORT).show();
                Log.e("", e.getMessage());
            }

        } else {
            txtregistered.setText(getResources().getString(R.string.UnRegistered));
            popup( getResources().getString(R.string.Registration_does_not_exist));
            Intent Registration = new Intent(getActivity(), SignUp.class);
            startActivity(Registration);
            // return;
        }

        if ((!(Valuefile.exists()))) {
            popup(getResources().getString(R.string.BLE_registration_data_file_does_not_exist));//"BLE Registration data file does not exist");
            return;
        }

        service_init();
        // Handle Disconnect & Connect button
        if (ReadSysSetting() == 0)//check System Setting
            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_local_database), Toast.LENGTH_LONG).show();//check_Setting();


        if ((startvalue == 0) && AutoPunch == 1 && Biometric == 1) {
            autostartvalue = 0;
            Intent newIntent = new Intent(getActivity(), biometric.class);
            startActivityForResult(newIntent, REQUEST_ENABLE_FT);
        }
        if (mBtAdapter.isEnabled()) {
            if ((startvalue == 0) && AutoPunch == 1 && Biometric == 0) {
                autostartvalue = 0;
                Intent newIntent2 = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(newIntent2, REQUEST_SELECT_DEVICE);
                btnConnectDisconnect.setProgress(50);
                if (isRunning == false) {
                    cTimer = new CountDownTimer(8000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            isRunning = true;
                        }

                        public void onFinish() {
                            isRunning = false;
                            if (mDevice != null) {
                                mService.disconnect();
                                // if(mService != null)
                                //  mService.close();
                                deviceLabel.setTextColor(0Xff0099cc);
                                ((TextView) view.findViewById(R.id.deviceName)).setText(getResources().getString(R.string.Not_Connected));
                            }
                            btnConnectDisconnect.setProgress(0);
                        }
                    }.start();
                }
            }
            startvalue = 1;

        }
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtAdapter.isEnabled()) {
                    try {
                        mBtAdapter.enable();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                if (btnConnectDisconnect.getProgress() == 100 || btnConnectDisconnect.getProgress() == -1) {
                    btnConnectDisconnect.setProgress(0);
                    ErrorMessage.setBackgroundColor(Color.TRANSPARENT);
                    txtfullname.setText("");
                    txtdate.setText("");
                    ErrorMessage.setText("");
                } else {
                    if ( btnConnectDisconnect.getProgress()==0) {
                        if (isRunning == false) {
                            cTimer = new CountDownTimer(8000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    isRunning = true;
                                }

                                public void onFinish() {
                                    isRunning = false;
                                    if (mDevice != null) {
                                        mService.disconnect();
                                        deviceLabel.setTextColor(0Xff0099cc);
                                        //  if(mService != null)
                                        //  mService.close();

                                        ((TextView) view.findViewById(R.id.deviceName)).setText(getResources().getString(R.string.Not_Connected));
                                    }
                                    btnConnectDisconnect.setProgress(0);

                                }
                            }.start();
                        }
                        btnConnectDisconnect.setProgress(50);
                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices

                        if (Biometric == 1) {
                            Intent newIntent = new Intent(getActivity(), biometric.class);
                            startActivityForResult(newIntent, REQUEST_ENABLE_FT);
                        }
                        if (Biometric == 0) {
                            Intent newIntent = new Intent(getActivity(), DeviceListActivity.class);
                            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                        }
                        autostartvalue = 0;

                    } else {
                        //Disconnect button pressed
                        if (mDevice != null) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mService.disconnect();
                            // if(mService != null)
                            //  mService.close();
                            btnConnectDisconnect.setProgress(0);
                        }
                    }
                }

            }
        });


        // Set initial UI state
        in = MediaPlayer.create(getActivity(), R.raw.in);
        out = MediaPlayer.create(getActivity(), R.raw.out);
        error = MediaPlayer.create(getActivity(), R.raw.error);
        Card_Expired = MediaPlayer.create(getActivity(), R.raw.cardexpired);
        Blacklisted = MediaPlayer.create(getActivity(), R.raw.blacklisted);
        AntiPassBackError = MediaPlayer.create(getActivity(), R.raw.antipassback);
        Access_Denied = MediaPlayer.create(getActivity(), R.raw.accessdenied);

        if (!checkServiceRunning()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    getActivity().startService(new Intent(getActivity(), LocationUpdateservice.class));
                }
            };
            thread.start();


        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.Registration_id:

                startActivity(new Intent(getContext(), RegistrationActivity.class));

                break;
            case R.id.Setting_id:
                startActivity(new Intent(getContext(), setting.class));
                break;
            case R.id.About_id:
                startActivity(new Intent(getContext(), AboutActivity.class));
                break;
            case R.id.Record_id:
                startActivity(new Intent(getContext(), recordLogs.class));
                //  getActivity().onBackPressed();
                break;
            case R.id.Device_id:
                startActivity(new Intent(getContext(), Device.class));
                //  getActivity().onBackPressed();
                break;
            case R.id.Tracking_id:
                if (ReadTrackingValue() == 0) {
                    if (Tracking == 1)
                        startActivity(new Intent(getContext(), Tracking.class));
                    else
                        Toast.makeText(getActivity(), getResources().getString(R.string.not_Authorized), Toast.LENGTH_LONG).show();

                } else
                    Toast.makeText(getActivity(), getResources().getString(R.string.Local_Database_Error), Toast.LENGTH_LONG).show();
                //  getActivity().onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return super.onOptionsItemSelected(item);
    }



    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {

            try {


                mService = ((UartService.LocalBinder) rawBinder).getService();
                Log.d(TAG, "onServiceConnected mService= " + mService);
                if (!mService.initialize()) {
                    Log.e(TAG, "Unable to initialize BLE");
                    getActivity().finish();
                    int v = 0;
                }
            } catch (Exception ex) {
                Log.e(TAG, "Unable to initialize Service");
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };

    private Handler mHandler = new Handler() {
        @Override

        //Handler events that received from UART service 
        public void handleMessage(Message msg) {

        }
    };

    public final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//


            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        //////////////////   btnConnectDisconnect.setText("Punching");//pradeeep
                        ((TextView) view.findViewById(R.id.deviceName)).setText(mDevice.getName() + (getResources().getString(R.string.Connected)));
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                getActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        //  btnConnectDisconnect.setText("Punch");

                        deviceLabel.setTextColor(0Xff0099cc);


                        ((TextView) view.findViewById(R.id.deviceName)).setText(getResources().getString(R.string.Not_Connected));

                        mState = UART_PROFILE_DISCONNECTED;
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (cTimer != null) {
                            cTimer.cancel();
                            //  cTimer.();
                            cTimer = null;
                            isRunning = false;
                            btnConnectDisconnect.setProgress(0);
                        }


                        mService.close();
                        //setUiState();

                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //    e.printStackTrace();
                }
                if (autostartvalue == 0) {
                    sendCommandToTerminal((byte) 0x88);
                    autostartvalue = 1;
                }
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                // final  int[] txValue = intent.getIntArrayExtra(UartService.EXTRA_DATA);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            int byteRead = txValue.length;
                            processRxPacket(txValue, byteRead);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                showMessage("Device doesn't support UART. Disconnecting");
                mService.disconnect();
            }


        }
    };

    private void service_init() {
        Intent bindIntent = new Intent(getContext(), UartService.class);
        getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        getActivity().unbindService(mServiceConnection);
        mService.stopSelf();
        mService = null;

        //  if (mBtAdapter.isEnabled()) {
        //   mBtAdapter.disable();
        // }

       // getActivity().unregisterReceiver(backReciver);
    }

    @Override
    public void onStop() {
        super.onStop();

        try{
            getActivity().unregisterReceiver(backReciver);

          //  in.release();
          //  out.release();// = MediaPlayer.create(getActivity(), R.raw.out);
          //  error.release(); ///= MediaPlayer.create(getActivity(), R.raw.error);
           // Card_Expired.release();/// = MediaPlayer.create(getActivity(), R.raw.cardexpired);
           // Blacklisted.release();/// = MediaPlayer.create(getActivity(), R.raw.blacklisted);
           // AntiPassBackError.release();/// = MediaPlayer.create(getActivity(), R.raw.antipassback);
           // Access_Denied.release();// = MediaPlayer.create(getActivity(), R.raw.accessdenied);
        } catch (Exception e){
            // already unregistered
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        //  Log.d(TAG, "onResume");
        //// if (!mBtAdapter.isEnabled()) {
        ///      Log.i(TAG, "onResume - BT not enabled yet");
        ///      Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ///      startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        ///  }

        try{
            registebackReceiver();
        } catch (Exception e){
            // already registered
        }
    }
    @Override
    public void onPause() {


        //called after unregistering
        super.onPause();

     // getActivity().unregisterReceiver(backReciver);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String actionName = data.getStringExtra("name");
                    if ((actionName != null && !actionName.isEmpty()) && actionName.equals("cancel")) {
                        if (cTimer != null) {
                            cTimer.cancel();
                            //  cTimer.();
                            cTimer = null;
                            isRunning = false;
                            if (mDevice != null) {
                                mService.disconnect();
                                ((TextView) view.findViewById(R.id.deviceName)).setText(getResources().getString(R.string.Not_Connected));
                            }
                            btnConnectDisconnect.setProgress(0);
                            return;
                        }
                    }


                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    ((TextView) view.findViewById(R.id.deviceName)).setText(mDevice.getName() +"-"+getResources().getString( R.string.connecting) );
                    mService.connect(deviceAddress);
                }

                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getActivity(),getResources().getString( R.string.Bluetooth_has_turned_on), Toast.LENGTH_SHORT).show();


                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(),  getResources().getString( R.string.Problem_in_BLE_turning_ON), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;

            case REQUEST_ENABLE_FT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    //   Toast.makeText(this, "fingerprint on", Toast.LENGTH_SHORT).show();
                    Intent newIntent = new Intent(getContext(), DeviceListActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                } else {

                }
                break;

            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }


    private void showMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }


    int sessionid = 0x12345678;
    byte terno = 1;
    byte pollsourceid = (byte) 0x84;
    int customerID = 1;

    public class Comparameters {
        public byte termo = 1;
        public byte siteno;
        public long sessionidrx;
        public Date dtrx = new Date();
        public short custid;
        public byte[] mac = new byte[6];
        public byte language;
        public byte reserved;
    }

    public Comparameters compara = new Comparameters();

    public void sendCommandToTerminal(byte cmdid) {
        byte[] txBuff = new byte[MAX_BUFF];
        short addr = 0;
        byte bAuth = 0;
        byte bMode = 0;

        try {
            short i = 0, byteCount = 0;
            //Prepare header of packet
            txBuff[i++] = (byte) STX1;
            txBuff[i++] = STX2;
            txBuff[i++] = 0;    //Byte count lsb
            txBuff[i++] = 0;    //byte count msb
            txBuff[i++] = compara.termo;
            txBuff[i++] = pollsourceid;

            txBuff[i++] = (byte) (customerID / 0x100);
            txBuff[i++] = (byte) (customerID % 0x100);


            //Prepare command specific data
            // CmdResponseEventArgs ev1 = new CmdResponseEventArgs();
            txBuff[i++] = cmdid;
            //    ter.txcmdid = (byte) cmdid;
            switch (cmdid) {
                case (byte) 0x88: //who ami

                    txBuff[i++] = (byte) sessionid;
                    txBuff[i++] = (byte) (sessionid >> 8);
                    txBuff[i++] = (byte) (sessionid >> 16);
                    txBuff[i++] = (byte) (sessionid >> 24);

                    break;
                case (byte) 0x4F: //DirectMobileCmd
                    int i1 = tmpBuff[0] & 0xff;
                    i1 |= ((tmpBuff[1] & 0xff) << 8);
                    for (int k = 2; k < i1; k++) {
                        txBuff[i++] = tmpBuff[k];
                    }


                    break;


                default:

                    break;


            }

            i++;    //csum bytes
            i++;    //csum bytes
            txBuff[i++] = (byte) ETX;

            byteCount = i;

            int checkSum = 0;

            for (i = 4; i < byteCount - 3; i++)
                checkSum += txBuff[i] & 0xff;

            i = (short) (byteCount - 3);
            txBuff[2] = (byte) i;
            txBuff[3] = (byte) (i >> 8);
            txBuff[byteCount - 3] = (byte) ((checkSum & 0xffff) >> 8);
            txBuff[byteCount - 2] = (byte) ((checkSum & 0xff));

            //string str = "";
            //for (int x = 0; x < byteCount; x++)
            //    str += string.Format("{0:X}", txBuff[x]) + " ";

            //System.Windows.Forms.MessageBox.Show(str);

            frameProcessState = WAITSTX1;

            byte[] arr = new byte[byteCount];
            System.arraycopy(txBuff, 0, arr, 0, byteCount);


            mService.writeRXCharacteristic(arr);

            displaytxCommandMsg(cmdid, tmpBuff[2]);

        } catch (Exception ex) {
//                ter.displayErrorMsg(string.Format("***///???cmd:{0} : tmpl_out_ptr = {1}", ter.td.biocmdtx, ter.td.tmpl_out_ptr));
            showMessage(ex.getMessage());
        }


    }


    void displaytxCommandMsg() {


    }

    void displaytxCommandMsg(byte cmdid, byte comid1) {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

        if ((cmdid & 0xff) == 0x88) {

        } else if ((cmdid & 0xff) == 0x4F) {

            if ((comid1 & 0xff) == 0x51) {

            }
            if ((comid1 & 0xff) == 0x52) {

            }

            if ((comid1 & 0xff) == 0x53) {

            }

        }


    }


    void displaytxCommandMsgRX(byte cmdid) {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        if ((cmdid & 0xff) == 0x51) {

        }
        if ((cmdid & 0xff) == 0x52) {

        }
        if ((cmdid & 0xff) == 0x53) {

        }


    }


    public boolean processRxPacket(byte[] buffer, int byteRead) {
        int k;
        int i = 0;
        boolean bRet = false;
        while (i < byteRead) {
            k = buffer[i++];
            k = (int) (k & 0xFF);
            switch (frameProcessState) {
                case WAITSTX1:
                    if (k == STX1) {
                        rxByteCnt = 0;
                        rxFrameSize = (int) MAXRXFRAMESize;
                        rxBuff[rxByteCnt++] = (byte) k;
                        frameProcessState = FRAMERXINGSTATE.WAITSTX2;
                    }
                    break;
                case WAITSTX2:
                    if (k == STX2) {
                        frameProcessState = FRAMERXINGSTATE.WAITDATA;
                        rxBuff[rxByteCnt++] = (byte) k;
                    } else {
                        frameProcessState = WAITSTX1;
                    }
                    break;
                case WAITDATA:
                    rxBuff[rxByteCnt++] = (byte) k;
                    if (rxByteCnt == 4) {
                        rxFrameSize = rxBuff[2] & 0xff;
                        rxFrameSize |= (k << 8) & 0xff;
                    }
                    if (k == ETX && rxByteCnt == rxFrameSize + 3) {
                        if (validateRxFrame() == 0) {
                            //   Toast.makeText(this,rxFrameSize,Toast.LENGTH_LONG).show();
                            frameProcessState = WAITFrameProcess;
                            processAndActRxFrame();
                            /// ter.displayMsg("Frame Received");///change with toast
                            frameProcessState = WAITSTX1;
                            bRet = true;

                        } else {
                            //  ter.displayMsg(":Checksum Error");//change with toast
                            frameProcessState = WAITSTX1;
                        }
                    }
                    if (rxByteCnt > rxFrameSize + 3 || rxByteCnt >= MAXRXFRAMESize) {
                        frameProcessState = WAITSTX1;
                    }
                    break;
            }
        }
        return bRet;
    }

    public byte validateRxFrame() {
        byte bRet = 1;
        short nSum, nSum1, i, j;

        // calculate csum
        nSum = 0;
        j = (short) ((rxBuff[2] & 0xff) | ((rxBuff[3] & 0xff) << 8));
        for (i = 4; i < j; i++) {
            nSum += rxBuff[i] & 0xff;
        }

        nSum = (short) (nSum & 0xFFFF);
        nSum1 = (short) ((rxBuff[i++] & 0xff) << 8);
        nSum1 |= (short) (rxBuff[i] & 0xff);

        if (nSum != nSum1)
            bRet = 2;
        else
            bRet = 0;
        return bRet;
    }


    private void processAndActRxFrame() {
        rxprIndex = 8;
        try {
            if (rxBuff[rxprIndex++] != 0xEE)    //Reply OK
            {
                if (rxBuff[rxprIndex - 1] == (int) cmdWhoAmI) //-1 because incremented above
                {
                } else if (rxBuff[rxprIndex - 1] == 0x4F) {
                    long aa = 0;
                    rxprIndex++;
                    compara.termo = (byte) (rxBuff[rxprIndex++] & 0xff);
                    compara.siteno = (byte) (rxBuff[rxprIndex++] & 0xff);


                    // int abba=0,x,y,z,l;


                    // abba = rxBuff[rxprIndex++]& 0xff;
                    //x= rxBuff[rxprIndex++]& 0xff;
                    // abba= abba| (x<<8);
                    /// x=rxBuff[rxprIndex++]& 0xff;
                    //  abba |=  x;
                    /// abba|= (x<<16);
                    ///x=rxBuff[rxprIndex++]& 0xff;
                    /// abba |= (x<<24);


                    compara.sessionidrx = rxBuff[rxprIndex++] & 0xff;
                    compara.sessionidrx |= (long) ((rxBuff[rxprIndex++] & 0xff) << 8);
                    compara.sessionidrx |= (long) ((rxBuff[rxprIndex++] & 0xff) << 16);
                    compara.sessionidrx |= (long) ((rxBuff[rxprIndex++] & 0xff) << 24);

                    if (sessionid == compara.sessionidrx)  // replyframe if sessionid matches
                    {
                        ProcessBLEFrame();
                    }
                } else {
                    //Invalid command
                }
                //////////////////// ter.pollCmdReplyState = 0;      //Reset the command reply state
            } else //Reply error
            {


                //////////// displayRxFrameError(rxBuff[rxprIndex++]);
                ///////////// ter.pollCmdReplyState = 2;      //set command reply state to indicate waiting loop command replied with error and should be reset from there
            }
        } catch (Exception ex) {
            showMessage(ex.getMessage());
        }
        frameProcessState = WAITSTX1;
    }

    private void ProcessBLEFrame() {

        int empID = 0, dir = 0, rxb = 9, name = 40, Badgeindex = 58, fullnameindex = 74, empidindex = 113, date = 16, i = 0, j = 0, l = 0, m = 0;
        short res = 0;
        int direction = 0;
        String FullName = "", badgeno = "", NameDisplay = "", s = "", filename = "", lang = "", tickvalue = "\u2713";

        requiredDate = "";
        requiredTime = "";

        char chr = ' ';

        FileChannel ch = null, ch1 = null;

        filename = "mytextfile.txt";


        lang = Locale.getDefault().getDisplayLanguage();

        displaytxCommandMsgRX(rxBuff[rxb]);

        switch (rxBuff[rxb]) {
            case 0x51: //get IMEI
                i++;
                i++;
                tmpBuff[i++] = rxBuff[rxb];
                tmpBuff[i++] = compara.termo;
                tmpBuff[i++] = compara.siteno;
                //sessionid++;
                tmpBuff[i++] = (byte) (sessionid >> 0);
                tmpBuff[i++] = (byte) (sessionid >> 8);
                tmpBuff[i++] = (byte) (sessionid >> 16);
                tmpBuff[i++] = (byte) (sessionid >> 24);
                tmpBuff[i++] = 0;//////good response;

                // count = TXvalue1.length;
                j = tmpBuff[i++] = (byte) 15;
                for (int k = 0; k < j; k++) {
                    tmpBuff[i++] = TXvalue1[k];
                }


                tmpBuff[0] = (byte) i;
                tmpBuff[1] = (byte) (i >> 8);
                //   tmpBuff1 = new byte[i];
                //  System.arraycopy(tmpBuff, 0, tmpBuff1, 0, i);
                /////////////    btnSend.setBackgroundColor(Color.MAGENTA);

                deviceLabel.setTextColor(Color.GREEN);
                sendCommandToTerminal(rxBuff[rxb - 1]);
                break;
            case 0x52: //Send file
                i++;
                i++;
                tmpBuff[i++] = rxBuff[rxb];
                tmpBuff[i++] = compara.termo;
                tmpBuff[i++] = compara.siteno;
                sessionid++;
                tmpBuff[i++] = (byte) (sessionid >> 0);
                tmpBuff[i++] = (byte) (sessionid >> 8);
                tmpBuff[i++] = (byte) (sessionid >> 16);
                tmpBuff[i++] = (byte) (sessionid >> 24);
                tmpBuff[i++] = 0;//////good response;
                //filename="mytextfile.txt";
                try {
                    FileInputStream fileIn = getActivity().openFileInput(filename);

                    byte[] b = filename.getBytes();
                    ch = fileIn.getChannel();

                    int size = (int) ch.size();
                    MappedByteBuffer buf = ch.map(FileChannel.MapMode.READ_ONLY, 0, size);
                    byte[] bytes = new byte[size];
                    int lnth = 0;
                    lnth = bytes.length;
                    buf.get(bytes);

                    while (m < 16) {


                        if (m >= b.length)
                            tmpBuff[i++] = 0;
                        else
                            tmpBuff[i++] = b[m];
                        m++;

                    }

                    tmpBuff[i++] = 0;
                    tmpBuff[i++] = 0;
                    tmpBuff[i++] = 0;
                    tmpBuff[i++] = 0;

                    tmpBuff[i++] = 0;

                    tmpBuff[i++] = (byte) lnth;
                    tmpBuff[i++] = (byte) (lnth >> 8);

                    while (l < size)//size
                    {
                        tmpBuff[i++] = bytes[l];
                        l++;

                    }
                    File direction_file = new File(getActivity().getFilesDir() + File.separator + DirectionFile);
                    if ((direction_file.exists())) {
                        FileInputStream fileIn1 = getActivity().openFileInput(DirectionFile);
                        ch1 = fileIn1.getChannel();
                        int size1 = (int) ch1.size();
                        MappedByteBuffer buf1 = ch1.map(FileChannel.MapMode.READ_ONLY, 0, size1);
                        byte[] bytes1 = new byte[size1];
                        buf1.get(bytes1);

                        tmpBuff[i++] = bytes1[0];// s.getBytes();

                        int byt = bytes1[0];
                        //   String strIn = new String(bytes1, 0, 1);
                        //  String ss =buf1.toString();


                    } else
                        tmpBuff[i++] = 0;
                    tmpBuff[i++] = 0;


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {

                        if (ch != null) {
                            ch.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                tmpBuff[0] = (byte) i;
                tmpBuff[1] = (byte) (i >> 8);
                /////////////////////////    btnSend.setBackgroundColor(Color.BLUE);

                deviceLabel.setTextColor(Color.MAGENTA);
                //  btnConnectDisconnect.setProgress(50);
                sendCommandToTerminal(rxBuff[rxb - 1]);
                break;

            case 0x53: //Send file
                if (mDevice != null) {

                    mService.disconnect();
                    //   if(mService != null)
                    //    mService.close();
                }
                ///////////////////////////////   btnSend.setBackgroundColor(Color.GREEN);
                deviceLabel.setTextColor(Color.LTGRAY);
                String strdate = (rxBuff[date++]) + 2000 + "/" + rxBuff[date++] + "/" + rxBuff[date++] + " " +
                        "" + rxBuff[date++] + ":" + rxBuff[date++] + ":" + rxBuff[date++];

                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    Date date1 = format.parse(strdate);
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy");
                    requiredDate = df.format(date1).toString();
                    /////txtdatetime.setText(requiredDate.toString());


                    Date time1 = format.parse(strdate);
                    DateFormat tf = new SimpleDateFormat("hh:mm:ss a");
                    requiredTime = tf.format(time1).toString();
                    txtdate.setText(requiredDate + ", " + requiredTime);

                    /////  txttime.setText(requiredTime.toString());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //  lbldate.setText("Punch Date");
                /// punchTime.setText("Punch Time");

                ////  txtdatetime.setTextColor(Color.BLUE);
                //extract name
                for (int k = 0; k < 16; k++) {
                    if ((rxBuff[name + k] & 0xff) == 0)
                        break;

                    chr = (char) (rxBuff[name + k] & 0xff);
                    FullName += chr;
                }
                direction = rxBuff[name + 16];
                NameDisplay = FullName.trim();
                txtfullname.setText(NameDisplay);
                ///   txtname.setText(FullName);
                ///   txtname.setTextColor(Color.BLUE);
                //  lblname.setText("Name");
////          extract Badgeeno no
                for (int k = 0; k < 16; k++) {
                    if ((rxBuff[Badgeindex + k] & 0xff) == 0)
                        break;

                    chr = (char) (rxBuff[Badgeindex + k] & 0xff);
                    badgeno += chr;
                }

                // extract full name
                FullName = "";
                for (int k = 0; k < 16; k++) {
                    if ((rxBuff[fullnameindex + k] & 0xff) == 0)
                        break;

                    chr = (char) (rxBuff[fullnameindex + k] & 0xff);
                    FullName += chr;
                }

                // extract EmpID
                for (int k = 0; k < 16; k++) {
                    empID = (rxBuff[empidindex] & 0xff) + ((rxBuff[empidindex + 1] & 0xff) << 8) + ((rxBuff[empidindex + 1] & 0xff) << 16) + ((rxBuff[empidindex + 1] & 0xff) << 24);
                }


                i++;
                i++;
                tmpBuff[i++] = rxBuff[rxb];
                tmpBuff[i++] = compara.termo;
                tmpBuff[i++] = compara.siteno;
                tmpBuff[i++] = (byte) (sessionid >> 0);
                tmpBuff[i++] = (byte) (sessionid >> 8);
                tmpBuff[i++] = (byte) (sessionid >> 16);
                tmpBuff[i++] = (byte) (sessionid >> 24);
                res = (short) (rxBuff[rxb + 13] & 0xff);
                res |= (rxBuff[rxb + 14] << 8);
                //  if(sysvalue.equals("eacs")) {
                // res = (short) (rxBuff[rxb + 14] & 0xff);
                //  }

                // else {
                //   res = (short) (res & 0xFF9E);
                // }
                try {

                    // txtmessagecode.setText("");
                    // txttick.setText("");
                    if (res == 0) {

                        connection_value = 1;

                        int soundcode = 1;
                        s = "IN";
                        s = getResources().getString(R.string.IN);
                        dir = 1;
                        btnConnectDisconnect.setCompleteText(getResources().getString(R.string.IN)+" " + tickvalue);


                        if ((direction & 0x0C) == 0x08) ////////////////// if ((direction & 0x20) == 0x20)
                        {


                            //  Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                            //  intent.putExtra("enabled", false);
                            // sendBroadcast(intent);
                            dir = 0;
                            s = getResources().getString(R.string.OUT);
                            soundcode = 0;
                            btnConnectDisconnect.setCompleteText(getResources().getString(R.string.OUT)+" " + tickvalue);
                            getActivity().stopService(new Intent(getActivity(), LocationMonitoringService.class));
                            mAlreadyStartedService = false;
                        } else {


                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    if(urlconnection.checkConnection()) {
                                        if (!isMyServiceRunning(LocationMonitoringService.class)) {
                                            getActivity().startService(new Intent(getActivity(), LocationMonitoringService.class));
                                            mAlreadyStartedService = true;
                                        }
                                    }
                                }
                            }, 5000);





                        }

                        //String  s1 = s + getResources().getString( R.string.Punched_Successfully);

                        btnConnectDisconnect.setProgress(100);

                        SoundIt(soundcode);

                        if (sysvalue.equals("eacs")) {
                            //   popup( NameDisplay, requiredDate, requiredTime,  s,true,0);
                            btnConnectDisconnect.setCompleteText(tickvalue);
                        } else {
                            //   popup(NameDisplay, requiredDate, requiredTime, s, true, 1);
                        }

                        if (ReadMACValue(mDevice.getAddress()) == -1)
                            Toast.makeText(getActivity(), getResources().getString(R.string.Error_in_reading_Mac_value), Toast.LENGTH_LONG).show();
                        else if (ReadMACValue(mDevice.getAddress()) == 0) {
                            if (WriteMACAddress() == 0)
                                Toast.makeText(getActivity(),  getResources().getString(R.string.Error_in_writing_Mac_value), Toast.LENGTH_LONG).show();
                        }
                        if (WriteLog(badgeno, FullName, dir, empID) == 0)
                            Toast.makeText(getActivity(), getResources().getString(R.string.Log_Write_Error), Toast.LENGTH_LONG).show();


                    } else {
                        connection_value = 0;
                        btnConnectDisconnect.setProgress(-1);
                        if (sysvalue.equals("eacs"))
                            EACSdisplayResult(NameDisplay, requiredDate, requiredTime, res, 0);
                        else {
                            EACSdisplayResult(NameDisplay, requiredDate, requiredTime, res, 1);
                        }
                        tmpBuff[i++] = 0;


                    }


                    if (cTimer != null) {
                        cTimer.cancel();
                        //  cTimer.();
                        cTimer = null;
                        isRunning = false;
                    }


                    FileOutputStream file_out = getActivity().openFileOutput(DirectionFile, MODE_PRIVATE);
                    file_out.write((byte) direction);
                    file_out.close();


                } catch (Exception e) {
                    tmpBuff[i++] = 1;
                }
                startvalue = 1;
                break;
        }
    }

    public void popup(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void shakeIt(int repeat, int duration) {
        if (getActivity().getSystemService(VIBRATOR_SERVICE) != null) {
            long[] pattern = {0, duration};
            ((Vibrator) getActivity().getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(pattern, repeat);

        }
    }

    private void SoundIt(int index) {
        switch (index) {
            case 0:
                out.start();
                break;
            case 1:
                in.start();
                break;
            case 3:
                Access_Denied.start();
                break;
            case 4:
                Card_Expired.start();
                break;
            case 5:
                Blacklisted.start();
                break;
            case 6:
                AntiPassBackError.start();
            case 7:
                error.start();
                break;

        }

    }

    private void ElSmartdisplayResult(String NameDisplay, String requiredDate, String requiredTime, short res, int Elsmart) {
        String str = "", crossvalue = "\u2715";
        int soundcode = 0;
        switch (res) {

            case elsmartIN_OUT_Punch:
                str = getResources().getString(R.string.Invalid_IN_Out);//  Toast.makeText(this,"Invalid IN/Out",Toast.LENGTH_LONG).show();
                soundcode = 3;
                break;
            case elsmartInvalidDoor:
                str = getResources().getString(R.string.ACCESS_DENIED);
                soundcode = 3;
                break;
            case elsmartSystemLocked:
                str = getResources().getString(R.string.System_Locked);// Toast.makeText(this,"System Locked",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case elsmartExitEntryMissed:
                str = getResources().getString(R.string.Exit_Entry_Missed);//  Toast.makeText(this,"Exit Entry Missed",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case elsmartExpiredCard:
                str = getResources().getString(R.string.CARD_EXPIRED);// Toast.makeText(this,"Card Expired",Toast.LENGTH_LONG).show();
                soundcode = 4;
                break;
            case Cutomer_Mismatch:
                str = getResources().getString(R.string.Cust_Mismatch);
                soundcode = 7;
                break;

            case elsmartCardType1:
                str = getResources().getString(R.string.CardType1);// Toast.makeText(this,"CardType1",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case elsmartCardType2:
                str = getResources().getString(R.string.CardType2);//  Toast.makeText(this,"CardType2",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case elsmartBlackListedCard:
                str = getResources().getString(R.string.BlackListed_Card);//   Toast.makeText(this,"BlackListed Card",Toast.LENGTH_LONG).show();
                soundcode = 5;
                break;
            case General_Error:
                str = getResources().getString(R.string.General_Errr);
                soundcode = 7;
                break;
            default:
                str = getResources().getString(R.string.Error);
                soundcode = 7;//  Toast.makeText(this,"Invalid IN/Out",Toast.LENGTH_LONG).show();
        }
        if (mDevice != null) {
            mService.disconnect();
        }

        SoundIt(soundcode);
        ErrorMessage.setText(str);

        // popup( NameDisplay, requiredDate, requiredTime,  str, false,Elsmart);

    }


    private void EACSdisplayResult(String NameDisplay, String requiredDate, String requiredTime, short res, int Elsmart) {
        String str = "", crossvalue = "\u2715";
        int soundcode = 0;
        switch (res) {


              /*  case eacsREADER_ERROR_SET:
                    txtmessagecode.setText("READER ERROR SET");//  Toast.makeText(this,"READER ERROR SET",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsREADER_ERROR_CLEAR:
                    txtmessagecode.setText(R.string.READER_ERROR_CLEAR);//  Toast.makeText(this,"READER ERROR CLEAR",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsDOOR_KEPT_OPEN_ALARM:
                    txtmessagecode.setText(R.string.DOOR_KEPT_OPEN_ALARM);//  Toast.makeText(this,"DOOR KEPT OPEN ALARM",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsDOOR_FORCED_OPEN_ALARM:
                    txtmessagecode.setText(R.string.DOOR_FORCED_OPEN_ALARM);//  Toast.makeText(this,"DOOR FORCED OPEN ALARM",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsEPB_ALARM:
                    txtmessagecode.setText(R.string.EPB_ALARM);//   Toast.makeText(this,"EPB ALARM",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsEMBU_ALARM:
                    txtmessagecode.setText(R.string.EMBU_ALARM);//   Toast.makeText(this,"EMBU ALARM",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsDOOR_OPENED_BY_COMMAND:
                    txtmessagecode.setText(R.string.DOOR_OPENED_BY_COMMAND);//  Toast.makeText(this,"DOOR OPENED BY COMMAND",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsBIOMETRIC_ERROR_SET:
                    txtmessagecode.setText(R.string.BIOMETRIC_ERROR_SET);//  Toast.makeText(this,"BIOMETRIC ERROR SET",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsBIOMETRIC_ERROR_CLEAR:
                    txtmessagecode.setText(R.string.BIOMETRIC_ERROR_CLEAR);//  Toast.makeText(this,"BIOMETRIC ERROR CLEAR",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsDATE_TIME_ALARM_SET:
                    txtmessagecode.setText(R.string.DATE_TIME_ALARM_SET);// Toast.makeText(this,"DATE TIME ALARM SET",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;
                case eacsDATE_TIME_ALARM_CLEAR:
                    txtmessagecode.setText(R.string.DATE_TIME_ALARM_CLEAR);//  Toast.makeText(this,"DATE TIME ALARM CLEAR",Toast.LENGTH_LONG).show();
                    txtmessagecode.setBackgroundColor(Color.RED);
                    txttick.setText("\u2715");
                    break;*/
            case eacsBLACKLISTED:
                str = getResources().getString(R.string.CARD_BLACKLISTED);// Toast.makeText(this,"CARD BLACKLISTED",Toast.LENGTH_LONG).show();
                soundcode = 5;
                break;
            case eacsACCESSDENIED:
                str = getResources().getString(R.string.ACCESS_DENIED);// Toast.makeText(this,"ACCESS DENIED",Toast.LENGTH_LONG).show();
                soundcode = 3;
                break;
            case eacsCARDEXPIRED:
                str = getResources().getString(R.string.CARD_EXPIRED);//   Toast.makeText(this,"CARD EXPIRED",Toast.LENGTH_LONG).show();
                soundcode = 4;
                break;
            case eacsANTIPASSBKERROR:
                str = getResources().getString(R.string.ANTIPASS_BACK_ERROR);// Toast.makeText(this,"ANTIPASS BACK ERROR",Toast.LENGTH_LONG).show();
                soundcode = 6;
                break;

            case eacsACCLOCK:
                str = getResources().getString(R.string.ACCESS_DURING_LOCK);// Toast.makeText(this,"ACCESS DURING LOCK",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case eacsACCHOLIDAY:
                str = getResources().getString(R.string.ACCESS_DURING_HOLIDAY);//  Toast.makeText(this,"ACCESS DURING HOLIDAY",Toast.LENGTH_LONG).show();;
                soundcode = 7;
                break;
            case eacsACCLEAVE:
                str = getResources().getString(R.string.ACCESS_DURING_LEAVE);//  Toast.makeText(this,"ACCESS DURING LEAVE",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case eacsPINFAIL:
                str = getResources().getString(R.string.PIN_FAIL);//  Toast.makeText(this,"PIN FAIL",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case eacsBIOFAIL:
                str = getResources().getString(R.string.BIO_FAIL);// Toast.makeText(this,"BIO FAIL",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case eacsDURESSCODE:
                str = getResources().getString(R.string.DURESS_CODE);// Toast.makeText(this,"DURESS CODE",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case eacsACCSCHEDULE:
                str = getResources().getString(R.string.ACCESS_SCHEDULE);//  Toast.makeText(this,"ACCESS SCHEDULE",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case FLASH_WRITE_FAIL:
                str = getResources().getString(R.string.FLASH_WRITE_FAIL);//  Toast.makeText(this,"FREE12",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case BIO_DEVICE_ERR:
                str = getResources().getString(R.string.BIO_DEVICE_ERR);//  Toast.makeText(this,"FREE13",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case eacsACCESCORT:
                str = getResources().getString(R.string.ACCESS_SCORT);//  Toast.makeText(this,"ACCESS SCORT",Toast.LENGTH_LONG).show();
                soundcode = 7;
                break;
            case eacsCARDTWING:
                str = getResources().getString(R.string.CARDTWING);
                break;


            case SYSTEM_HALTED:
                str = getResources().getString(R.string.SYSTEM_HALTED);
                soundcode = 7;
                break;

            case FPTIMEOUTERROR:
                str = getResources().getString(R.string.FPTIMEOUTERROR);
                soundcode = 7;
                break;

            case NOTSUPPORTED:
                str = getResources().getString(R.string.NOTSUPPORTED);
                soundcode = 7;
                break;
            case Cutomer_Mismatch:
                str = getResources().getString(R.string.Cust_Mismatch);
                soundcode = 7;
                break;


            case NOTENROLLED:
                str = getResources().getString(R.string.NOTENROLLED);
                soundcode = 7;
                break;

            case NOFINGERDETECT:
                str = getResources().getString(R.string.NOFINGERDETECT);
                soundcode = 7;
                //  Toast.makeText(this,"CARDTWING",Toast.LENGTH_LONG).show();
                break;

            case READER_ERROR:
                str = getResources().getString(R.string.READER_ERROR);
                soundcode = 7;
                break;
            case General_Error:
                str = getResources().getString(R.string.General_Errr);
                soundcode = 7;
                break;
            default:
                str = getResources().getString(R.string.Error);// Toast.makeText(this,"ACCESS DENIED",Toast.LENGTH_LONG).show();
                // ErrorMessage.setText(str);
                // txttick.setText("\u2715");
                soundcode = 7;
                break;

        }


        SoundIt(soundcode);
        ErrorMessage.setText(str);

        //  popup( NameDisplay, requiredDate, requiredTime,  str, false,Elsmart);

    }


    public String regfileValue() {
        FileInputStream fileIn = null;
        try {
            fileIn = getActivity().openFileInput(DirectionFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader InputRead;
        InputRead = new InputStreamReader(fileIn);

        char[] inputBuffer = new char[READ_BLOCK_SIZE];
        String s = "";
        int charRead;

        try {
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }


    private void PAlertDialog(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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


    public Integer WriteLog(String badgeno, String name, Integer s, Integer empID)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String type = "BLE";
        int res = 0;
        try {

            database = db.getWritableDatabase();
            database.execSQL("INSERT INTO tblLogs(date,time,BadgeNo,Name,Ter,direction,empid,PunchType,sent)VALUES('" + requiredDate + "','" + requiredTime + "','" + badgeno + "','" + txtfullname.getText() + "' ,'" + compara.termo + "'," + s + " ," + empID + ",'" + type + "'," + 1 + ")");
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }

    public Integer WriteMACAddress()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {

            database = db.getWritableDatabase();
            database.execSQL("INSERT INTO Device(MacId,Name)VALUES('" + mDevice.getAddress() + "','" + mDevice.getName() + "')");
            res = 1;
        } catch (Exception ex) {
            res = 0;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


    public Integer ReadSysSetting()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  system_setting", null);
            if (cursor.moveToFirst()) {
                do {
                    VisibleMode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Debug")));
                    AutoPunch = Integer.valueOf(cursor.getString(cursor.getColumnIndex("AutoPunch")));
                    Biometric = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Biometric")));
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


    public Integer ReadMACValue(String MacID)//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Device", null);
            if (cursor.moveToFirst()) {
                do {
                    MacValue = cursor.getString(cursor.getColumnIndex("MacId"));
                    if (MacValue.equals(MacID))
                        res++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            res = -1;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


    public Integer ReadTrackingValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM  Registration", null);
            if (cursor.moveToFirst()) {
                do {
                    Tracking = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Tracking")));

                } while (cursor.moveToNext());
            }
            cursor.close();
            res = 0;
        } catch (Exception ex) {
            res = -1;
            Log.d(TAG, ex.getMessage());
        }
        return res;
    }


    public void popup(String name, String formattedDate, String formattedDate2, String punchtype, boolean value, int Elsmart) {
        ImageView iv;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.custom_layout, null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */

        txtname = (TextView) customView.findViewById(R.id.txtname1);
        txtdatetime = (TextView) customView.findViewById(R.id.txtdatetime);
        txttime = (TextView) customView.findViewById(R.id.txttime);
        txtmessagecode = (TextView) customView.findViewById(R.id.txtmessagecode);
        //    txttick =(TextView) customView.findViewById(R.id.txttick);
        iv = (ImageView) customView.findViewById(R.id.idimgvwin);


        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button

        txtname.setText(name);
        txtdatetime.setText(formattedDate);
        txttime.setText(formattedDate2);
        if (Elsmart == 1) {
            if (punchtype.equals("IN"))
                iv.setImageResource(R.drawable.in);
            else
                iv.setImageResource(R.drawable.out);
        } else
            iv.setImageResource(R.drawable.inn);
        txtmessagecode.setText(punchtype);
        txtdatetime.setTextColor(Color.BLUE);
        txttime.setTextColor(Color.BLUE);
        txtname.setTextColor(Color.BLUE);


        if (value) {

            txtmessagecode.setTextColor(Color.BLUE);
            //  txttick.setText("\u2713");
            // txttick.setTextColor(Color.GREEN);

            txtmessagecode.setText("Successfully");
        } else {
            // txttick.setText("\u2715");
            // txttick.setTextColor(Color.RED);
            txtmessagecode.setTextColor(Color.RED);
            iv.setImageResource(R.drawable.cross);
            txtmessagecode.setText(punchtype);
        }

        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                txtname.setText("");
                txtdatetime.setText("");
                txttime.setText("");

                txtmessagecode.setText("");

                //  txttick.setText("");

                mPopupWindow.dismiss();
            }
        });

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
        // Finally, show the popup window at the center location of root relative layout
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);


    }


    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getActivity(),getResources().getString(R.string.no_google_playservice_available)
                    , Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        NetworkInfo.State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//wifi
        NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        ////// if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
        ///////     try {
        ///////////      String mobile1=  mobile.name();

        //////// } catch (Exception e) {
        /////////////     e.printStackTrace();
        ////////// }

        //////////////////////  }

        ////////////  else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
        /////////////    String wifi1=  mobile.name();
        //////////////////      }

        //if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
        //  promptInternetConnect();
        //  return false;
        // }
        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            checkAndRequestPermissions();// requestPermissions();
        }
        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */


    private void promptInternetConnect() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                checkAndRequestPermissions();// requestPermissions();
                            }

                        }
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService) {

            //  mMsgView.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            if (!isMyServiceRunning(LocationMonitoringService.class)) {
                // Intent intent = new Intent(MainActivity.this, LocationMonitoringService.class);

             //   getActivity().startService(new Intent(getActivity(), LocationMonitoringService.class));
                // startService(intent);

                mAlreadyStartedService = true;
            }
            //Ends................................................
        }
    }

    private class BackReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String cbinfo = intent.getStringExtra("backinfo");
            //tv.setText(cbinfo);
            Toast.makeText(getActivity(),cbinfo,Toast.LENGTH_LONG).show();
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(getActivity(), status, 2404).show();
            }
            return false;
        }
        return true;
    }


    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */

    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private boolean checkAndRequestPermissions() {
        int location_permission = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (location_permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(ACCESS_FINE_LOCATION);
        } else {


        }
        if (!listPermissionsNeeded.isEmpty()) {
            Toast.makeText(this.getActivity(),getResources().getString(R.string.Location_permission_should_be_granted_for_BLE_scan),Toast.LENGTH_LONG).show();
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }



    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                view.findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        switch (requestCode) {
            case 1:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), getResources().getString(R.string.Permission_denied_to_read_your_External_storage), Toast.LENGTH_SHORT).show();
                }

            break;

            case 34:
                if (grantResults.length <= 0) {
                    // If img_user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                 //   startStep3();

                } else {
                    showSnackbar(R.string.permission_denied_explanation,
                            R.string.settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Build intent that displays the App settings screen.
                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                }
            }
        }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;


        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                new GpsUtils(getContext()).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        // turn on GPS
                        isGPS = isGPSEnable;
                    }
                });

                startStep1();

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            String  res="";



        }


        @Override
        protected void onPreExecute() {



        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }
    public Integer ReadSystemValue()//////CHANGE INTO COMMON FUNCTION LATTER
    {
        String MacValue = "";
        int res = 0;
        try {
            database = db.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT BLE,Geofence,QRCode  From   Registration", null);
            if (cursor.moveToFirst()) {
                do {

                    BLE = Integer.valueOf(cursor.getString(cursor.getColumnIndex("BLE")));
                    geofence = Integer.valueOf(cursor.getString(cursor.getColumnIndex("Geofence")));
                    QRCode = Integer.valueOf(cursor.getString(cursor.getColumnIndex("QRCode")));
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

    public boolean checkServiceRunning(){
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if ("com.nordicsemi.nrfUARTv2.LocationUpdateservice"
                    .equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }


}
