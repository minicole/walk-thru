package com.tnt.walk_thru;

import android.app.Application;
import android.content.Context;

import com.estimote.sdk.EstimoteSDK;

public class app extends Application{

    private static app instance = new app();

    String appId = "walk-thru-30b";
    String appToken = "2c5ffc3c2fc7baf417473dcd2f7c0d81";

    /**
     * default constructor that sets the instance
     */
    public app() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(this.getApplicationContext(), appId, appToken);
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);
    }
}
