package com.tnt.walk_thru;

import android.app.Application;
import android.content.Context;

import com.estimote.sdk.EstimoteSDK;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

public class app extends Application{

    private static app instance = new app();

    String appId = "walk-thru-30b";
    String appToken = "2c5ffc3c2fc7baf417473dcd2f7c0d81";

    /**
     * Parse Application ID
     */
    private static String APPLICATION_ID = "yq5eb8V0j6dj80kFR00pTb7q661hXJnC8MKJeEZU";

    /**
     * Parse Client Key
     */
    private static String CLIENT_KEY = "otDAIX0vlj9d7pSmG6Jq7EgmzrUSVKDyMNOBhonu";

    // Update tokens here from Yelp developers site, Manage API access.
    String yelp_consumer_key = "Qm3iXRtMtERyrLw23jnHOw";
    String yelp_consumer_secret = "_fZ9EMRxdurSIByMXlkqOZ-0CHs";
    String yelp_token = "7nQmSbbcaoQWLcJ9O9sqnOQiatj_GUCc";
    String yelp_token_secret = "diSaVmTcZV_t7j4efsywEWwj5o0";

    static private Yelp yelp;

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

        yelp = new Yelp(yelp_consumer_key, yelp_consumer_secret, yelp_token, yelp_token_secret);

    }

    static public Yelp getYelp(){
        return yelp;
    }
}
