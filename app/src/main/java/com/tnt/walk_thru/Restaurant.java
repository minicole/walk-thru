package com.tnt.walk_thru;

import android.graphics.Bitmap;

import com.estimote.sdk.Beacon;

/**
 * Created by EricTremblay on 16-01-23.
 */
public class Restaurant {
    public String name;
    public String distance;
    public Bitmap logo;
    public Beacon beacon;

    public Restaurant(String name, String distance, Bitmap logo, Beacon beacon) {
        this.name = name;
        this.distance = distance;
        this.logo = logo;
        this.beacon = beacon;
    }
}
