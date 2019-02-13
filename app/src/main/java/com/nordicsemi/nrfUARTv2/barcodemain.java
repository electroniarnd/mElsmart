package com.nordicsemi.nrfUARTv2;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.nordicsemi.nrfUARTv2.barcode.barcodecaptureactivity;

public class barcodemain extends Activity {
    private TextView scanResult;
    private Button scanButton;
    private String Electronia="",Company_Name="",Site_Name="",Terminal_Name="";
    private int site_no=0,terminal_no=0,cumtomerid=0;

    private static final int BARCODE_READER_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodemain);
        scanResult = (TextView) findViewById(R.id.result_textview);

        scanButton = (Button) findViewById(R.id.scan_barcode_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), barcodecaptureactivity.class);
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


      try {
          if (requestCode == BARCODE_READER_REQUEST_CODE) {
              if (resultCode == CommonStatusCodes.SUCCESS) {
                  if (data != null) {
                      Barcode barcode = data.getParcelableExtra(barcodecaptureactivity.BarcodeObject);
                      Point[] p = barcode.cornerPoints;
                      scanResult.setText(barcode.displayValue);

                      String scan = barcode.displayValue;
                      int lenth = 0;
                      String[] sc = scan.split(",");

                      if (sc.length == 7) {
                          Electronia = sc[0];
                          Company_Name = sc[1];
                          Site_Name = sc[3];
                          Terminal_Name = sc[4];
                          site_no = Integer.valueOf(sc[5]);
                          terminal_no = Integer.valueOf(sc[6]);
                          cumtomerid = Integer.valueOf(sc[2]);
                      }

                  } else {
                      scanResult.setText("Not Scanning properly");
                  }
              }
          } else {
              super.onActivityResult(requestCode, resultCode, data);
          }
      }
        catch(Exception ex)
          {

          }
    }
}
