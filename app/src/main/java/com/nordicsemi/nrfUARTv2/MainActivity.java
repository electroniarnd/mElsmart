
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




import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.app.Activity;
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
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.format;
import static com.nordicsemi.nrfUARTv2.MainActivity.FRAMERXINGSTATE.WAITFrameProcess;
import static com.nordicsemi.nrfUARTv2.MainActivity.FRAMERXINGSTATE.WAITSTX1;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;
    private static int a = 0;
    private static  int VisibleMode = 0;
    private  static int bytcount=0;
    private  static int framesize=0;
    private  static int autostartvalue=1;



    private static final int IN_OUT_Punch=			0x01;
    private static final int InvalidDoor=			0x02;
    private static final int	SystemLocked=			0x04;
    private static final int	ExitEntryMissed	=		0x08;
    private static final int	ExpiredCard=				0x10;
    private static final int CardType1=				0x20;
    private static final int CardType2=				0x40;
    private static final int	BlackListedCard		=	0x80;
    private static final int	READER_ERROR_SET=		0x22;
    private static final int	READER_ERROR_CLEAR	=	0x23;
    private static final int	DOOR_KEPT_OPEN_ALARM=		0x24;
    private static final int	DOOR_FORCED_OPEN_ALARM	=	0x25;
    private static final int	EPB_ALARM	=		0x26;
    private static final int	EMBU_ALARM	=		0x27;
    private static final int	DOOR_OPENED_BY_COMMAND	=	0x28;
    private static final int	BIOMETRIC_ERROR_SET	=	0x29;
    private static final int	BIOMETRIC_ERROR_CLEAR=		0x2A;
    private static final int	DATE_TIME_ALARM_SET=		0x2B;
    private static final int	DATE_TIME_ALARM_CLEAR	=	0x2C;





    private static final int OK=0;
    public static final int BLACKLISTED=1;
    public static final int ACCESSDENIED=2;
    public static final int CARDEXPIRED=3;
    public static final int ANTIPASSBKERROR=4;
    public static final int ACCLOCK=5;
    public static final int ACCHOLIDAY=6;
    public static final int  ACCLEAVE=7;
    public static final int   PINFAIL=8;
    public static final int BIOFAIL=9;
    public static final int  DURESSCODE=10;
    public static final int  ACCSCHEDULE=11;
    public static final int   FREE12=12;
    public static final int   FREE13=13;
    public static final int  ACCESCORT=14;
    public static final int  CARDTWING=15;





    public static final int MAX_BUFF = 8096;      //includes tcpip buffer
    public static final int MAXRXFRAMESize = 4096;
    byte[] rxBuff = new byte[MAX_BUFF];
    byte[] tmpBuff = new byte[MAX_BUFF];

    public static final int STX1 = 0xAA;
    public static final int STX2 = 0x55;
    public static final int ETX = 0xF0;
    //Frame Receive State

    public static final int PollSourceID = 0x83;
    static final int READ_BLOCK_SIZE = 4096;

    //Command list
    public static final byte cmdWhoAmI = (byte) 0x88;
    public static final int cmdGetDateTime = 0x51;
    public static final int cmdSetTerID = 0x52;



    public int rxByteCnt = 0;
    public int rxFrameSize = 0;
    private short rxprIndex = 0;
    int count=0;
   public byte[] TXvalue1;
    public byte[] tmpBuff1;
    public  enum FRAMERXINGSTATE {
        NONE,
        WAITSTX1,
        WAITSTX2,
        WAITDATA,
        WAITFrameProcess,
        ReadyForNextCmd;



    }


  // public enum EACS_AlarmNo {OK, BLACKLISTED,ACCESSDENIED,CARDEXPIRED,ANTIPASSBKERROR,ACCLOCK,ACCHOLIDAY, ACCLEAVE,PINFAIL,BIOFAIL, DURESSCODE,ACCSCHEDULE,FREE12,FREE13,ACCESCORT,CARDTWING };




    private FRAMERXINGSTATE frameProcessState= WAITSTX1;






    private TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState = UART_PROFILE_DISCONNECTED;
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;
    private ListView messageListView;
    private ArrayAdapter<String> listAdapter;
    private Button btnConnectDisconnect, btnSend;
    private EditText edtMessage;
    private TextView txttick;
    private TextView  txtdatetime;
    private TextView txtname;
    private static final String fileRegistrationVerify = "BadgeIMEI.txt";
    private static final String Datafile = "mytextfile.txt";
    TextView  txtmessagecode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        count = telephonyManager.getDeviceId().length();
        TXvalue1 = telephonyManager.getDeviceId().getBytes();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        messageListView.setAdapter(listAdapter);
        messageListView.setDivider(null);
        btnConnectDisconnect = (Button) findViewById(R.id.btn_select);
        btnSend = (Button) findViewById(R.id.sendButton);
        edtMessage = (EditText) findViewById(R.id.sendText);
        txttick = (TextView) findViewById(R.id.txttick);

        txtdatetime = (TextView) findViewById(R.id.txtdatetime);
        txtname = (TextView) findViewById(R.id.txtname);

        txtmessagecode = (TextView) findViewById(R.id.txtmessagecode);
        edtMessage.setVisibility(View.GONE);
        //btnSend.setVisibility(View.GONE);
        messageListView.setVisibility(View.GONE);
        btnConnectDisconnect.setBackgroundColor(Color.parseColor("#ABBD48"));


        service_init();


        // Handle Disconnect & Connect button
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtAdapter.isEnabled()) {
                    Log.i(TAG, "onClick - BT not enabled yet");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                } else {
                    if (btnConnectDisconnect.getText().equals("Connect")) {

                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices

                        Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);

                        autostartvalue=0;


                    } else {
                        //Disconnect button pressed
                        if (mDevice != null) {
                            mService.disconnect();

                        }
                    }
                }
            }
        });
        // Handle Send button

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.sendText);
                String message = editText.getText().toString();
                byte[] value;
                try {


                    //send data to service
                    value = message.getBytes("UTF-8");
                    sendCommandToTerminal((byte) 0x88);
                    //  mService.writeRXCharacteristic(value);
                    //Update the log with time stamp
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    //listAdapter.add("[" + currentDateTimeString + "] TX: " + message);
                    //messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                    edtMessage.setText("");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
      //  if (autostartvalue == 0) {
       //     btnSend.performClick();
       //     autostartvalue =1;

      //  }





        // Set initial UI state

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.Registration_id:
                startActivity(new Intent(this,RegistrationActivity.class));
                break;
            case R.id.About_id:
                startActivity(new Intent(this,AboutActivity.class));
                break;
             case R.id.Debug_id:
                if(VisibleMode==0) {
                    messageListView.setVisibility(View.VISIBLE);
                   VisibleMode=1;
               }
                else
                    if(VisibleMode==1) {
                        messageListView.setVisibility(View.GONE);
                       VisibleMode=0;
                   }


                break;

            default:  return super.onOptionsItemSelected(item);

        }

        return super.onOptionsItemSelected(item);
    }








    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
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
                runOnUiThread(new Runnable() {
                    public void run() {

                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        btnConnectDisconnect.setText("Disconnect");
                        edtMessage.setEnabled(true);
                        btnSend.setEnabled(true);
                        btnSend.setBackgroundColor(Color.GREEN);
                        btnConnectDisconnect.setBackgroundColor(Color.GREEN);


                        ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + " - ready");
                        listAdapter.add("[" + currentDateTimeString + "] Connected to: " + mDevice.getName());
                        messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);

                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        btnConnectDisconnect.setText("Connect");
                        edtMessage.setEnabled(false);
                        btnSend.setEnabled(false);
                        btnConnectDisconnect.setBackgroundColor(Color.parseColor("#ABBD48"));
                        btnSend.setBackgroundColor(Color.LTGRAY);
                        ((TextView) findViewById(R.id.deviceName)).setText("Not Connected");
                        listAdapter.add("[" + currentDateTimeString + "] Disconnected to: " + mDevice.getName());
                        mState = UART_PROFILE_DISCONNECTED;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mService.close();
                        //setUiState();

                    }
                });
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
               // try {
                 //   Thread.sleep(1000);
              //  } catch (InterruptedException e) {
               // //    e.printStackTrace();
                //}
               //if(autostartvalue==0) {
              //  sendCommandToTerminal((byte) 0x88);
               //   autostartvalue=1;
                //}
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

               final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
                // final  int[] txValue = intent.getIntArrayExtra(UartService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                          //  String text = new String(txValue, "UTF-8");

                          //  String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                          //  listAdapter.add("[" + currentDateTimeString + "] RX: " + text);
                           // messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);


                            int byteRead= txValue.length;

                            processRxPacket(txValue,  byteRead);

                            if(bytcount==0 && txValue.length>9)
                                framesize=txValue[9]&0xff;
                           if (framesize == (byte) 0x53) {
                               bytcount++;
                               if(bytcount==4)
                               {
                         if (mDevice != null) {

                             bytcount=0;
                             mService.disconnect();
                         }
                            }
                               }

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
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
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
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService = null;

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

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

                File Registrationfile = new File(getFilesDir() + File.separator + fileRegistrationVerify);

                File Valuefile = new File(getFilesDir() + File.separator + Datafile);
                if ((!(Registrationfile.exists())) && data != null) {
                    popup("Registration  does not exist");


                    return;
                }
                if ((!(Valuefile.exists())) && data != null) {

                    popup("Registration data file does not exist");

                    return;
                }


                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                    ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + " - connecting");
                    mService.connect(deviceAddress);


                }


                txtmessagecode.setText("");
                txtdatetime.setText("");
                txtname.setText("");
                txttick.setText("");
                txtmessagecode.setBackgroundColor(Color.TRANSPARENT);

                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }


    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            showMessage("nRFUART's running in background.\n             Disconnect to exit");
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.popup_title)
                    .setMessage(R.string.popup_message)
                    .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.popup_no, null)
                    .show();
        }
    }


    int sessionid = 0x12345678;
    byte terno = 1;
    byte pollsourceid =(byte) 0x84;
    int customerID = 1;

    public class Comparameters
    {
        public byte termo=1;
        public byte siteno;
        public long sessionidrx;
        public Date dtrx= new Date();
        public short custid;
        public  byte [] mac = new byte[6];
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
                    txBuff[i++] = (byte) (sessionid >>16);
                    txBuff[i++] = (byte) (sessionid >> 24);

                    break;
                case (byte) 0x4F: //DirectMobileCmd
                    int i1 = tmpBuff[0] &0xff;
                    i1 |= ((tmpBuff[1]& 0xff)<<8);
                    for(int k = 2;k<i1;k++)
                    {
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
                checkSum += txBuff[i]&0xff;

            i = (short) (byteCount - 3);
            txBuff[2] = (byte) i;
            txBuff[3] = (byte) (i >> 8);
            txBuff[byteCount - 3] = (byte) ((checkSum & 0xffff)>>8);
            txBuff[byteCount - 2] = (byte) ((checkSum &0xff));

            //string str = "";
            //for (int x = 0; x < byteCount; x++)
            //    str += string.Format("{0:X}", txBuff[x]) + " ";

            //System.Windows.Forms.MessageBox.Show(str);

            frameProcessState =  WAITSTX1;

            byte [] arr = new byte[byteCount];
            System.arraycopy(txBuff, 0, arr, 0, byteCount);


            mService.writeRXCharacteristic(arr);

            displaytxCommandMsg(cmdid,tmpBuff[2]);

        } catch (Exception ex) {
//                ter.displayErrorMsg(string.Format("***///???cmd:{0} : tmpl_out_ptr = {1}", ter.td.biocmdtx, ter.td.tmpl_out_ptr));
            showMessage(ex.getMessage());
        }


    }


    void displaytxCommandMsg() {



    }

    void displaytxCommandMsg(byte cmdid,byte comid1)
    {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());

       if((cmdid&0xff)==0x88)
        {
            listAdapter.add("[" + currentDateTimeString + "] TX: " + cmdid+"  Connection message has sent");
        }
        else  if((cmdid&0xff)==0x4F)
       {

           if((comid1&0xff)==0x51)
           {
               listAdapter.add("[" + currentDateTimeString + "] TX: " + comid1+"  IMEI  has sent");
           }
           if((comid1&0xff)==0x52)
           {
               listAdapter.add("[" + currentDateTimeString + "] TX: " + comid1+"  file  has sent");
           }

           if((comid1&0xff)==0x53)
           {
               listAdapter.add("[" + currentDateTimeString + "] TX: " + comid1+" Opearation Status has sent and mobile disconnected ");
           }

       }



        messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);

    }


    void displaytxCommandMsgRX(byte cmdid)
    {
        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
        if((cmdid&0xff)==0x51) {
            listAdapter.add("[" + currentDateTimeString + "] RX: " + cmdid+" Request for IMEI frame has recieved");
        }
        if((cmdid&0xff)==0x52) {
            listAdapter.add("[" + currentDateTimeString + "] RX: " + cmdid+" Request for File frame has recieved");
        }
        if((cmdid&0xff)==0x53) {
            listAdapter.add("[" + currentDateTimeString + "] RX: " + cmdid+" Request for Opeartion status frame has recieved");
        }




        messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);

    }






    public boolean processRxPacket(byte[] buffer, int byteRead)
    {
        int k;
        int i = 0;
        boolean bRet = false;
        while (i < byteRead)
        {
            k = buffer[i++];
            k= (int) (k&0xFF);
            switch (frameProcessState)
            {
                case WAITSTX1:
                    if (k == STX1)
                    {
                        rxByteCnt = 0;
                        rxFrameSize = (int)MAXRXFRAMESize;
                        rxBuff[rxByteCnt++] =(byte) k;
                        frameProcessState = FRAMERXINGSTATE.WAITSTX2;
                    }
                    break;
                case WAITSTX2:
                    if (k == STX2)
                    {
                        frameProcessState = FRAMERXINGSTATE.WAITDATA;
                        rxBuff[rxByteCnt++] = (byte)k;
                    }
                    else
                    {
                        frameProcessState = WAITSTX1;
                    }
                    break;
                case WAITDATA:
                    rxBuff[rxByteCnt++] =(byte) k;
                    if (rxByteCnt == 4)
                    {
                        rxFrameSize = rxBuff[2] & 0xff;
                        rxFrameSize |= (k << 8) & 0xff;
                    }
                    if (k == ETX && rxByteCnt == rxFrameSize + 3)
                    {
                        if (validateRxFrame() == 0)
                        {
                              //   Toast.makeText(this,rxFrameSize,Toast.LENGTH_LONG).show();
                                frameProcessState = WAITFrameProcess;
                                processAndActRxFrame();
                               /// ter.displayMsg("Frame Received");///change with toast
                                frameProcessState = WAITSTX1;
                                bRet = true;

                        }
                        else
                        {
                          //  ter.displayMsg(":Checksum Error");//change with toast
                            frameProcessState = WAITSTX1;
                        }
                    }
                    if (rxByteCnt > rxFrameSize+3 || rxByteCnt >= MAXRXFRAMESize)
                    {
                        frameProcessState = WAITSTX1;
                    }
                    break;
            }
        }
        return bRet;
    }
    public byte validateRxFrame()
    {
        byte bRet = 1;
        short nSum, nSum1, i, j;

        // calculate csum
        nSum = 0;
        j = (short) (rxBuff[2] | (rxBuff[3] << 8));
        for (i = 4; i < j; i++)
        {
            nSum += rxBuff[i] & 0xff;
        }

        nSum = (short)(nSum & 0xFFFF);
        nSum1 =(short)((rxBuff[i++]&0xff) << 8);
        nSum1 |= (short) (rxBuff[i]&0xff);

        if (nSum != nSum1)
            bRet = 2;
        else
            bRet = 0;
        return bRet;
    }




    private void processAndActRxFrame()
    {
        rxprIndex =8;
        try
        {
            if (rxBuff[rxprIndex++] != 0xEE)    //Reply OK
            {
                if (rxBuff[rxprIndex - 1] == (int)cmdWhoAmI) //-1 because incremented above
                {
                }
                else if (rxBuff[rxprIndex-1] == 0x4F)
                {
                    long aa=0;
                    rxprIndex++;
                    compara.termo= (byte)(rxBuff[rxprIndex++]& 0xff);
                    compara.siteno=(byte)(rxBuff[rxprIndex++]& 0xff);



                   // int abba=0,x,y,z,l;



                   // abba = rxBuff[rxprIndex++]& 0xff;
                   //x= rxBuff[rxprIndex++]& 0xff;
                   // abba= abba| (x<<8);
                   /// x=rxBuff[rxprIndex++]& 0xff;
                  //  abba |=  x;
                   /// abba|= (x<<16);
                    ///x=rxBuff[rxprIndex++]& 0xff;
                   /// abba |= (x<<24);




                    compara.sessionidrx = rxBuff[rxprIndex++]& 0xff;
                    compara.sessionidrx |= (long)((rxBuff[rxprIndex++]& 0xff)<<8);
                    compara.sessionidrx |= (long)((rxBuff[rxprIndex++]& 0xff)<<16);
                    compara.sessionidrx |= (long)((rxBuff[rxprIndex++]& 0xff)<<24);

                    if(sessionid == compara.sessionidrx)  // replyframe if sessionid matches
                    {
                        ProcessBLEFrame();
                    }
                }
                else
                {
                    //Invalid command
                }
               //////////////////// ter.pollCmdReplyState = 0;      //Reset the command reply state
            }
            else //Reply error
            {


               //////////// displayRxFrameError(rxBuff[rxprIndex++]);
               ///////////// ter.pollCmdReplyState = 2;      //set command reply state to indicate waiting loop command replied with error and should be reset from there
            }
        }
        catch (Exception ex)
        {
            showMessage(ex.getMessage());
        }
        frameProcessState = WAITSTX1;
    }
    private void    ProcessBLEFrame()
    {
        int rxb = 9;
        int name=40;
        int date=16;
        int i = 0;
        int j = 0;
        int l=0;
        int m=0;
        String FullName="";

        char chr = ' ';
        String filename="";
        FileChannel ch = null;
        filename="mytextfile.txt";

        displaytxCommandMsgRX(rxBuff[rxb]);

        switch(rxBuff[rxb])
        {
            case 0x51: //get IMEI
                i++;
                i++;
                tmpBuff[i++] = rxBuff[rxb];
                tmpBuff[i++] = compara.termo;
                tmpBuff[i++] = compara.siteno;
                //sessionid++;
                tmpBuff[i++] = (byte)(sessionid>>0);
                tmpBuff[i++] = (byte)(sessionid>>8);
                tmpBuff[i++] = (byte)(sessionid>>16);
                tmpBuff[i++] = (byte)(sessionid>>24);
                tmpBuff[i++] = 0;//////good response;

                   // count = TXvalue1.length;
                    j = tmpBuff[i++] = (byte) count;
                    for (int k = 0; k < j; k++) {
                   tmpBuff[i++] = TXvalue1[k];
                    }


                tmpBuff[0] = (byte)i;
                tmpBuff[1] = (byte)(i>>8);
             //   tmpBuff1 = new byte[i];
              //  System.arraycopy(tmpBuff, 0, tmpBuff1, 0, i);
                sendCommandToTerminal(rxBuff[rxb-1]);
                break;
            case 0x52: //Send file
                i++;
                i++;
                tmpBuff[i++] = rxBuff[rxb];
                tmpBuff[i++] = compara.termo;
                tmpBuff[i++] = compara.siteno;
                sessionid++;
                tmpBuff[i++] = (byte)(sessionid>>0);
                tmpBuff[i++] = (byte)(sessionid>>8);
                tmpBuff[i++] = (byte)(sessionid>>16);
                tmpBuff[i++] = (byte)(sessionid>>24);
                tmpBuff[i++] = 0;//////good response;
                //filename="mytextfile.txt";
                try {
                    FileInputStream fileIn=openFileInput(filename);
                    //InputStreamReader InputRead= new InputStreamReader(fileIn);

                   /// char[] inputBuffer= new char[READ_BLOCK_SIZE];
                   /// String s="";
                  ///  int charRead;



                    byte[] b = filename.getBytes();
                    ch = fileIn.getChannel();

                    int size = (int) ch.size();
                    MappedByteBuffer buf = ch.map(FileChannel.MapMode.READ_ONLY, 0, size);
                    byte[] bytes = new byte[size];
                    int lnth=0;
                    lnth=bytes.length;
                    buf.get(bytes);

                    while(m<16) {


                        if(m>=b.length)
                            tmpBuff[i++]=0;
                        else
                            tmpBuff[i++]=b[m];
                        m++;

                    }

                    tmpBuff[i++] = 0;
                    tmpBuff[i++] =0;
                    tmpBuff[i++] = 0;
                    tmpBuff[i++] = 0;

                    tmpBuff[i++] = 0;

                    tmpBuff[i++] = (byte)lnth;
                    tmpBuff[i++] = (byte)(lnth>>8);

                 while(l<size)//size
                 {
                     tmpBuff[i++]=bytes[l];
                     l++;

                 }



                 //   while ((charRead=InputRead.read(inputBuffer))>0) {
                        // char to string conversion
                     ///   tmpBuff[i++]=(byte)(charRead);
                     ///   String readstring=String.copyValueOf(inputBuffer,0,charRead);
                     ///   s +=readstring;
                  ///  }
                   // InputRead.close();



                } catch (Exception e) {
                    e.printStackTrace();
                }

                finally {
                    try {

                        if (ch != null) {
                            ch.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                tmpBuff[0] = (byte)i;
                tmpBuff[1] = (byte)(i>>8);
                sendCommandToTerminal(rxBuff[rxb-1]);
                break;

            case 0x53: //Send file



                String  strdate = (rxBuff[date++])+2000+"/"+rxBuff[date++] +"/"+rxBuff[date++]+" " +
                        ""+ rxBuff[date++]+":"+rxBuff[date++]+":"+ rxBuff[date++];





                 SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    Date date1 = format.parse(strdate);
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                    String requiredDate = df.format(date1).toString();
                    txtdatetime.setText(requiredDate.toString());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                txtdatetime.setTextColor(Color.BLUE);



                for (int k = 0; k < 16; k++)
                {
                    if ((rxBuff[name+k]&0xff)== 0)
                    break;

                    chr = (char)( rxBuff[name+k]&0xff);
                    FullName += chr;
                }

                FullName =FullName.trim();
                txtname.setText(FullName);
                txtname.setTextColor(Color.BLUE);

                i++;
                i++;
                tmpBuff[i++] = rxBuff[rxb];
                tmpBuff[i++] = compara.termo;
                tmpBuff[i++] = compara.siteno;
                tmpBuff[i++] = (byte)(sessionid>>0);
                tmpBuff[i++] = (byte)(sessionid>>8);
                tmpBuff[i++] = (byte)(sessionid>>16);
                tmpBuff[i++] = (byte)(sessionid>>24);

                try
                {

                    txtmessagecode.setText("");
                    txttick.setText("");

                    switch(rxBuff[rxb+13]&0xff) {





                        case IN_OUT_Punch:
                            txtmessagecode.setText("Invalid IN/Out");//  Toast.makeText(this,"Invalid IN/Out",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case InvalidDoor:
                            txtmessagecode.setText("Invalid Door");// Toast.makeText(this,"Invalid Door",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case SystemLocked:
                            txtmessagecode.setText("System Locked");// Toast.makeText(this,"System Locked",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ExitEntryMissed:
                            txtmessagecode.setText("Exit Entry Missed");//  Toast.makeText(this,"Exit Entry Missed",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ExpiredCard:
                            txtmessagecode.setText("Card Expired");// Toast.makeText(this,"Card Expired",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;

                        case CardType1:
                            txtmessagecode.setText("CardType1");// Toast.makeText(this,"CardType1",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case CardType2:
                            txtmessagecode.setText("CardType2");//  Toast.makeText(this,"CardType2",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case BlackListedCard:
                            txtmessagecode.setText("BlackListed Card");//   Toast.makeText(this,"BlackListed Card",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case READER_ERROR_SET:
                            txtmessagecode.setText("READER ERROR SET");//  Toast.makeText(this,"READER ERROR SET",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case READER_ERROR_CLEAR:
                            txtmessagecode.setText("READER ERROR CLEAR");//  Toast.makeText(this,"READER ERROR CLEAR",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case DOOR_KEPT_OPEN_ALARM:
                            txtmessagecode.setText("DOOR KEPT OPEN ALARM");//  Toast.makeText(this,"DOOR KEPT OPEN ALARM",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case DOOR_FORCED_OPEN_ALARM:
                            txtmessagecode.setText("DOOR FORCED OPEN ALARM");//  Toast.makeText(this,"DOOR FORCED OPEN ALARM",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case EPB_ALARM:
                            txtmessagecode.setText("EPB ALARM");//   Toast.makeText(this,"EPB ALARM",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case EMBU_ALARM:
                            txtmessagecode.setText("EMBU ALARM");//   Toast.makeText(this,"EMBU ALARM",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case DOOR_OPENED_BY_COMMAND:
                            txtmessagecode.setText("DOOR OPENED BY COMMAND");//  Toast.makeText(this,"DOOR OPENED BY COMMAND",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case BIOMETRIC_ERROR_SET:
                            txtmessagecode.setText("BIOMETRIC ERROR SET");//  Toast.makeText(this,"BIOMETRIC ERROR SET",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case BIOMETRIC_ERROR_CLEAR:
                            txtmessagecode.setText("BIOMETRIC ERROR CLEAR");//  Toast.makeText(this,"BIOMETRIC ERROR CLEAR",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case DATE_TIME_ALARM_SET:
                            txtmessagecode.setText("DATE TIME ALARM SET");// Toast.makeText(this,"DATE TIME ALARM SET",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case DATE_TIME_ALARM_CLEAR:
                            txtmessagecode.setText("DATE TIME ALARM CLEAR");//  Toast.makeText(this,"DATE TIME ALARM CLEAR",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;




                        default:

                    }



                    txtmessagecode.setText("");
                    txttick.setText("");

                    switch(rxBuff[rxb+14]&0xff) {





                        case OK:
                            txtmessagecode.setText("Punched Successfully");//   Toast.makeText(this,"Punched Successfully",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.CYAN);
                            txttick.setText("\u2713");
                            break;
                        case BLACKLISTED:
                            txtmessagecode.setText("CARD BLACKLISTED");// Toast.makeText(this,"CARD BLACKLISTED",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ACCESSDENIED:
                            txtmessagecode.setText("ACCESS DENIED");// Toast.makeText(this,"ACCESS DENIED",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case CARDEXPIRED:
                            txtmessagecode.setText("CARD EXPIRED");//   Toast.makeText(this,"CARD EXPIRED",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ANTIPASSBKERROR:
                            txtmessagecode.setText("ANTIPASS BACK ERROR");// Toast.makeText(this,"ANTIPASS BACK ERROR",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;

                        case ACCLOCK:
                            txtmessagecode.setText("ACCESS DURING LOCK");// Toast.makeText(this,"ACCESS DURING LOCK",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ACCHOLIDAY:
                            txtmessagecode.setText("ACCESS DURING HOLIDAY");//  Toast.makeText(this,"ACCESS DURING HOLIDAY",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ACCLEAVE:
                            txtmessagecode.setText("ACCESS DURING LEAVE");//  Toast.makeText(this,"ACCESS DURING LEAVE",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case PINFAIL:
                            txtmessagecode.setText("PIN FAIL");//  Toast.makeText(this,"PIN FAIL",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case BIOFAIL:
                            txtmessagecode.setText("BIO FAIL");// Toast.makeText(this,"BIO FAIL",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case DURESSCODE:
                            txtmessagecode.setText("DURESS CODE");// Toast.makeText(this,"DURESS CODE",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ACCSCHEDULE:
                            txtmessagecode.setText("ACCESS SCHEDULE");//  Toast.makeText(this,"ACCESS SCHEDULE",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case FREE12:
                            txtmessagecode.setText("FREE12");//  Toast.makeText(this,"FREE12",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case FREE13:
                            txtmessagecode.setText("FREE13");//  Toast.makeText(this,"FREE13",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case ACCESCORT:
                            txtmessagecode.setText("ACCESS SCORT");//  Toast.makeText(this,"ACCESS SCORT",Toast.LENGTH_LONG).show();
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");
                            break;
                        case CARDTWING:
                            txtmessagecode.setText("CARDTWING");
                            txtmessagecode.setBackgroundColor(Color.RED);
                            txttick.setText("\u2715");

                            //  Toast.makeText(this,"CARDTWING",Toast.LENGTH_LONG).show();
                            break;
                        default:

                    }












                       tmpBuff[i++]=0;



                }
                catch(Exception e) {
                    tmpBuff[i++] = 1;
                }
                   String files="";
              //  int kk=22;
                //    while(kk<(rxb+30))
                  //  {
                      //  files+=rxBuff[rxb+13];
                  //  }


                   //  if(files.equals(filename) && (rxBuff[rxb+13]==0) )
                   //  {

                     //    tmpBuff[i++]=0;
                   //  }
                   //  else
                   //  {
                   //      tmpBuff[i++]=1;
                    // }

                byte[] c = filename.getBytes();

                m=0;

                if (c.length==0) {

                    Toast.makeText(MainActivity.this, "Registration data  does not exist",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                while(m<16) {


                    if(m>=c.length)
                        tmpBuff[i++]=0;
                    else
                        tmpBuff[i++]=c[m];
                    m++;

                }






                tmpBuff[0] = (byte)i;
                tmpBuff[1] = (byte)(i>>8);
                sendCommandToTerminal(rxBuff[rxb-1]);


                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

public void popup(String message)
{

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setMessage(message)
        .setCancelable(false)
       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    });
    AlertDialog alert = builder.create();
alert.show();

}
}
