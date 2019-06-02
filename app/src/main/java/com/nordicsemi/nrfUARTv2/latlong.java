package com.nordicsemi.nrfUARTv2;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pradeepn on 4/10/2019.
 */

public class latlong {
    private String Update;
    private LatLng latlng;

    public  latlong(String Update ,LatLng latlng) {
        this.latlng = latlng;
        this.Update=Update;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public String getUpdate() {
        return Update;
    }
}
