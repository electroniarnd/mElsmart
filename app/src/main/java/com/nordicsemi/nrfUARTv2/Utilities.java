package com.nordicsemi.nrfUARTv2;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;


import static android.content.Context.VIBRATOR_SERVICE;


/**
 * Created by Pradeepn on 11/27/2017.
 */

public class Utilities {

     public static void shakeIt(Context mContext, int repeat, int duration) {

        long[] pattern = {0,duration,200};
//        if (Build.VERSION.SDK_INT >= 26) {
//            ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, mContext.VibrationEffect.DEFAULT_AMPLITUDE));
 //       } else {
            ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(pattern, repeat);
 //       }
    }
}

