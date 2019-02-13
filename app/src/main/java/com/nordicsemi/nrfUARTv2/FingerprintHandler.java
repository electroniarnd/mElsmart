package com.nordicsemi.nrfUARTv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;

import android.annotation.TargetApi;

import android.os.Build;




@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private TextView tv;
    Integer res=0;
    private Context context;


    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        //  tv.setText("Auth error");
        this.update("Fingerprint Authentication error\n" + errString);

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        this.update("Fingerprint Authentication help\n" + helpString);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        // tv.setText("ok");
        // res=1;
        //  tv.setTextColor(tv.getContext().getResources().getColor(android.R.color.holo_green_light));




        ///////// ((Activity) context).finish();
        ///////////   Intent intent = new Intent(context, MainActivity.class);
        ///////////  context.startActivity(intent);






        Intent result1 = new Intent();
        Bundle b = new Bundle();
        b.putString("messge","1");
        result1.putExtras(b);
        ((Activity) context).setResult(RESULT_OK, result1);
        ((Activity) context).finish();



    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        this.update("Fingerprint Authentication failed.");
    }

    public void doAuth(FingerprintManager manager, FingerprintManager.CryptoObject obj) {
        CancellationSignal signal = new CancellationSignal();

        try {

            manager.authenticate(obj, signal, 0, this, null);


        }
        catch(SecurityException sce) {}
    }


    private void update(String e){
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.errorText);
        textView.setText(e);
    }
}

