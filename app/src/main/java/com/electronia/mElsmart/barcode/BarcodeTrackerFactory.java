package com.electronia.mElsmart.barcode;

/**
 * Created by Pradeepn on 8/16/2018.
 */
import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
private Context mContext;

        BarcodeTrackerFactory(Context context) {
        mContext = context;
        }

@Override
public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeTracker(mContext);
        }
        }