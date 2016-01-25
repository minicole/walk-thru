package com.tnt.walk_thru;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yelp.parcelgen.JsonUtil;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private String scanId;
    private List<Restaurant> restoList;
    private List<Beacon> beaconList;
    private List<String> beaconUuidList;
    private CustomAdapter listViewAdapter;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private Yelp mYelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restoList = new ArrayList();
        listViewAdapter = new CustomAdapter(this, restoList);

        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Restaurant resto = (Restaurant) parent.getItemAtPosition(position);
                Toast.makeText(parent.getContext(), resto.business.getSnippetText(), Toast.LENGTH_LONG).show();
            }
        });

        mainListView.setAdapter(listViewAdapter);

        mYelp = new Yelp(getString(R.string.consumer_key), getString(R.string.consumer_secret),
                getString(R.string.api_token), getString(R.string.api_token_secret));

        beaconList = new ArrayList();
        beaconUuidList = new ArrayList();

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                for (int i = 0; i < list.size(); i++) {
                    Restaurant resto = null;
                    if (!beaconList.contains(list.get(i))) {
                        beaconList.add(list.get(i));
                        Log.d("Main", "Beacon Discovered: " + list.get(i).toString());
                        String yelp_id = parseQueryForBeacon(list.get(i));
                        Log.d("Main", "Yelp ID: " + yelp_id);
                        resto = new Restaurant(yelp_id, null, null, list.get(i), null);
                        new searchYelp().execute(yelp_id, "Montreal");
                        listViewAdapter.add(resto);
                    }

                    Log.d("Main", list.get(i).getMacAddress().toString() + " distance: " + String.valueOf(Utils.computeAccuracy(list.get(i))));

                    if (resto == null) {
                        for (int j = 0; j < beaconList.size(); j++) {
                            //Log.d("Main", "Compare: " + list.get(i).getMacAddress().toString() + ", " + restoList.get(j).beacon.getMacAddress().toString());
                            if (list.get(i).getMacAddress().toString().equals(restoList.get(j).beacon.getMacAddress().toString())) {
                                resto = restoList.get(j);
                                //Log.d("Main", "RESTO SET" + resto.distance);
                                break;
                            }
                        }
                    }
                    if (resto != null) {
                        resto.distance = String.format("%.2f", Utils.computeAccuracy(list.get(i)));
                        listViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public String parseQueryForBeacon(Beacon beacon) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
            try {
                ParseObject result = query.whereEqualTo("beacon_UUID", beacon.getMacAddress().toString()).getFirst();
                String yelp_id = result.getString("yelp_id");
                Log.d("Main", "Restaurant Found: " + yelp_id);
                return yelp_id;
            } catch (Exception e) {
                Log.e("Main", e.toString());
            }
        return "";
    }

    public class searchYelp extends AsyncTask<String, Void, ArrayList<Business>> {

        private String yelp_id;

        @Override
            protected ArrayList<Business> doInBackground(String... params) {
                yelp_id = params[0];
                String result = mYelp.search(params[0], params[1]);
                try {
                    JSONObject response = new JSONObject(result);
                    if (response.has("businesses")) {
                        return JsonUtil.parseJsonList(
                                response.getJSONArray("businesses"), Business.CREATOR);
                    }
                } catch (JSONException e) {
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Business> businesses) {
                onSuccess(businesses);
            }

        public void onSuccess(ArrayList<Business> businesses) {
            // Launch BusinessesActivity with an intent that includes the received businesses
            //dismissDialog(DIALOG_PROGRESS);
            if (businesses != null) {
    //            Intent intent = new Intent(SearchActivity.this, BusinessesActivity.class);
    //            intent.putParcelableArrayListExtra(BusinessesActivity.EXTRA_BUSINESSES, businesses);
    //            startActivity(intent);
                Log.d("Main", "Business: " + businesses.get(0).getName() + ", " + businesses.get(0).getLocation().getDisplayAddress());
                Restaurant resto = null;
                for (int i = 0; i < restoList.size(); i++) {
                    if (yelp_id == restoList.get(i).name) {
                        resto = restoList.get(i);
                        break;
                    }
                }
                resto.business = businesses.get(0);
                listViewAdapter.notifyDataSetChanged();
                new LoadImage(resto).execute(businesses.get(0).getImageUrl());
            }
        }
    };


    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        Restaurant resto;

        public LoadImage(Restaurant resto) {
            this.resto = resto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(MainActivity.this);
//            pDialog.setMessage("Loading Image ....");
//            pDialog.show();

        }

        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                resto.logo = image;
                listViewAdapter.notifyDataSetChanged();
                //pDialog.dismiss();

            }else{

                //pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
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
