package com.tnt.walk_thru;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private String scanId;
    private List<Restaurant> restoList;
    private List<Beacon> beaconList;
    private CustomAdapter listViewAdapter;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restoList = new ArrayList();
        listViewAdapter = new CustomAdapter(this, restoList);
        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mainListView.setAdapter(listViewAdapter);

        beaconList = new ArrayList();

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (!beaconList.contains(list.get(i))) {
                        beaconList.add(list.get(i));
                        Log.d("Main", "Beacon Discovered: " + list.get(i).toString());
                    }
                }
            }
        });
    }

    public void addRestaurant(Restaurant resto) {
        listViewAdapter.add(resto);
    }

    @Override
    protected void onStart() {
        super.onStart();

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
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
