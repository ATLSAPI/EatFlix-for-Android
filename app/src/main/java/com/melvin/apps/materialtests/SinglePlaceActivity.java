package com.melvin.apps.materialtests;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


public class SinglePlaceActivity extends ActionBarActivity {
    //Action Bar
    private Toolbar mToolbar;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Google Places
    GooglePlaces googlePlaces;

    // Place Details
    PlaceDetails placeDetails;

    // Progress dialog
    ProgressDialog pDialog;

    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place

    //Set Map bounds
    static  final LatLng UK = new LatLng(51.5000, 0.1167);

    //Google Map
    private GoogleMap map;

    private Button addPlaces;

    String PlacesName;
    String PlacesAddress;
    String PlacesLatitude;
    String PlacesLongitude;

    GPSTracker gpsTracker;

    AlertDialogManager alertDialogManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_place);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        // Place reference id
        String reference = i.getStringExtra(KEY_REFERENCE);

        // Calling a Async Background thread
        new LoadSinglePlaceDetails().execute(reference);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        // creating GPS Class object
        gpsTracker = new GPSTracker(this);

        // check if GPS location can get
        if (gpsTracker.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gpsTracker.getLatitude() + ", longitude: " + gpsTracker.getLongitude());
        } else {
            // Can't get user's current location
            alertDialogManager.showAlertDialog(SinglePlaceActivity.this, "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return;
        }

        addPlaces = (Button) findViewById(R.id.add_places);
        addPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SinglePlaceActivity.this, NewRestaurant.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("name", PlacesName);
                i.putExtra("address",PlacesAddress);
                i.putExtra("latitude",PlacesLatitude);
                i.putExtra("longitude",PlacesLongitude);
                startActivity(i);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Background Async Task to Load Google places
     * */
    /**
     * Background Async Task to Load Google places
     * */
    class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SinglePlaceActivity.this);
            pDialog.setMessage("Loading profile ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... args) {
            String reference = args[0];

            // creating Places class object
            googlePlaces = new GooglePlaces();

            // Check if used is connected to Internet
            try {
                placeDetails = googlePlaces.getPlaceDetails(reference);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    if(placeDetails != null){
                        String status = placeDetails.status;

                        // check place deatils status
                        // Check for all possible status
                        if(status.equals("OK")){
                            if (placeDetails.result != null) {
                                String name = placeDetails.result.name;
                                String address = placeDetails.result.formatted_address;
                                String phone = placeDetails.result.formatted_phone_number;
                                String latitude = Double.toString(placeDetails.result.geometry.location.lat);
                                String longitude = Double.toString(placeDetails.result.geometry.location.lng);
                                PlacesName = name;
                                PlacesAddress = address;
                                PlacesLatitude = latitude;
                                PlacesLongitude = longitude;
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                map.addMarker(new MarkerOptions().position(new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude())).title("You are here")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location_grey600_18dp)));
                                map.addMarker(new MarkerOptions().position(new LatLng(placeDetails.result.geometry.location.lat, placeDetails.result.geometry.location.lng)).title(name));
                                builder.include(new LatLng(placeDetails.result.geometry.location.lat, placeDetails.result.geometry.location.lng));
                                builder.include(new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude()));
                                final LatLngBounds bounds = builder.build();
                                setTitle(name);
                                //map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 3));
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placeDetails.result.geometry.location.lat, placeDetails.result.geometry.location.lng), 3));
                                map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                Log.d("Place ", name + address + phone + latitude + longitude);

                                // Displaying all the details in the view
                                // single_place.xml
                                TextView lbl_name = (TextView) findViewById(R.id.name);
                                TextView lbl_address = (TextView) findViewById(R.id.address);
                                TextView lbl_phone = (TextView) findViewById(R.id.phone);
                                TextView lbl_location = (TextView) findViewById(R.id.location);

                                // Check for null data from google
                                // Sometimes place details might missing
                                name = name == null ? "Not present" : name; // if name is null display as "Not present"
                                address = address == null ? "Not present" : address;
                                phone = phone == null ? "Not present" : phone;
                                latitude = latitude == null ? "Not present" : latitude;
                                longitude = longitude == null ? "Not present" : longitude;

                                lbl_name.setText(name);
                                lbl_address.setText(address);
                                lbl_phone.setText(Html.fromHtml("<b>Phone:</b> " + phone));
                                lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + latitude + ", <b>Longitude:</b> " + longitude));
                            }
                        }
                        else if(status.equals("ZERO_RESULTS")){
                            alert.showAlertDialog(SinglePlaceActivity.this, "Near Places",
                                    "Sorry no place found.",
                                    false);
                        }
                        else if(status.equals("UNKNOWN_ERROR"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry unknown error occured.",
                                    false);
                        }
                        else if(status.equals("OVER_QUERY_LIMIT"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                        }
                        else if(status.equals("REQUEST_DENIED"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Request is denied",
                                    false);
                        }
                        else if(status.equals("INVALID_REQUEST"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Invalid Request",
                                    false);
                        }
                        else
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured.",
                                    false);
                        }
                    }else{
                        alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }


                }
            });

        }

    }
}
