package com.tnt.walk_thru;

import android.app.Application;
import android.content.Context;

import com.estimote.sdk.EstimoteSDK;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class app extends Application{

    private static app instance = new app();

    String appId = "secret";
    String appToken = "secret";


    /**
     * Parse Application ID
     */
    private static String APPLICATION_ID = "secret";

    /**
     * Parse Client Key
     */
    private static String CLIENT_KEY = "secret";

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

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(this);
    }
}
