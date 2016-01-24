package com.tnt.walk_thru;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private String scanId;
    private List<Restaurant> restoList;
    private List<Beacon> beaconList;
    private List<String> beaconUuidList;
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
        beaconUuidList = new ArrayList();

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (!beaconList.contains(list.get(i))) {
                        beaconList.add(list.get(i));
                        beaconUuidList.add(list.get(i).getMacAddress().toString());
                        Log.d("Main", "Beacon Discovered: " + list.get(i).toString());
                        parseQueryForBeacon(list.get(i));
                    }
                    Log.d("Main", list.get(i).getMacAddress().toString() + " distance: " + String.valueOf(Utils.computeAccuracy(list.get(i))));
                }
            }
        });
    }

    public void parseQueryForBeacon(Beacon beacon) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
            try {
                ParseObject result = query.whereEqualTo("beacon_UUID", beacon.getMacAddress().toString()).getFirst();
                String yelp_id = result.getString("yelp_id");
                Restaurant resto = new Restaurant(yelp_id, null, null, beacon);
                this.addRestaurant(resto);
                Log.d("Main", "Restaurant Found: " + yelp_id);
            } catch (Exception e) {
                Log.e("Main", e.toString());
            }
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
