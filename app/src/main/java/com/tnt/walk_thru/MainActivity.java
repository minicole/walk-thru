package com.tnt.walk_thru;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Nearable;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    String appId = "secret";
    String appToken = "secret";

    private BeaconManager beaconManager;
    private String scanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  App ID & App Token can be taken from App section of Estimote Cloud.
        EstimoteSDK.initialize(this.getApplicationContext(), appId, appToken);
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

        beaconManager = new BeaconManager(getApplicationContext());


        // Should be invoked in #onCreate.
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> nearables) {
                Log.d(this.getClass().getSimpleName(), "Discovered nearables: " + nearables);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
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


}
