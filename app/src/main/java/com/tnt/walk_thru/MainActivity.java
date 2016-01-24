package com.tnt.walk_thru;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private String scanId;

    /*
 * Update OAuth credentials below from the Yelp Developers API site:
 * http://www.yelp.com/developers/getting_started/api_access
 */
    private static final String CONSUMER_KEY = "Qm3iXRtMtERyrLw23jnHOw";
    private static final String CONSUMER_SECRET = "_fZ9EMRxdurSIByMXlkqOZ-0CHs";
    private static final String TOKEN = "7nQmSbbcaoQWLcJ9O9sqnOQiatj_GUCc";
    private static final String TOKEN_SECRET = "diSaVmTcZV_t7j4efsywEWwj5o0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = new BeaconManager(getApplicationContext());

        // Should be invoked in #onCreate.
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {
                Log.d(this.getClass().getSimpleName(), "Discovered nearables: " + nearables);
            }
        });
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

            }
        });

//        Log.d("YELP", app.getYelp().search("burritos", 30.361471, -87.164326));

        run.run();
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {

            YelpAPI yelpAPI = YelpAPI.start();


            Log.d("YELP", yelpAPI.searchForBusinessesByLocation("Italian", "Montreal"));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();


        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                scanId = beaconManager.startNearableDiscovery();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Should be invoked in #onStop.
        beaconManager.stopNearableDiscovery(scanId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }

    /**
     * Button launches an intent to switch to the activity
     * @param view Current activity
     */
    public void switchToStartActivity(View view){
        final Intent intent = new Intent(this, BaseActivity.class);
        startActivity(intent);
    }



}
