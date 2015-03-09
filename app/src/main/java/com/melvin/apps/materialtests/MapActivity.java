package com.melvin.apps.materialtests;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MapActivity extends ActionBarActivity {
    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    static  final LatLng UK = new LatLng(51.5000, 0.1167);
    private GoogleMap map;
    private Toolbar mToolbar;
    private ProgressDialog progressDialog;
    LatLngBounds mapBounds = null;
    HashMap<String, List<String>> drawMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading map coordinates...");
        progressDialog.show();
        drawMarker =
                (HashMap<String, List<String>>) getIntent().getSerializableExtra("lat");
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        //new LoadMap().execute();


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                Iterator it = drawMarker.entrySet().iterator();
                String data = "bOO";


                ArrayList<Marker> places = new ArrayList<Marker>();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                while (it.hasNext())
                {
                    HashMap.Entry<String, List<String>> pair = (HashMap.Entry<String, List<String>>) it.next();
                    List<String> stringList = pair.getValue();
                    String title = stringList.get(0);
                    double lat = Double.parseDouble(stringList.get(1));
                    double lng = Double.parseDouble(stringList.get(2));
                    data+=""+lat+lng;
                    Bitmap image = null;

                    try {
                        image = new LoadImage().execute(pair.getKey()).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    //map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(title));
                    map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(title).icon(BitmapDescriptorFactory.fromBitmap(image)));
                    builder.include(new LatLng(lat,lng));
                }
                //Toast.makeText(MapActivity.this, data, Toast.LENGTH_SHORT).show();
                Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
                        .title("Hamburg"));
                Marker kiel = map.addMarker(new MarkerOptions()
                        .position(KIEL)
                        .title("Kiel")
                        .snippet("Kiel is cool")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_launcher)));


                final LatLngBounds bounds = builder.build();
                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,15));
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        progressDialog.dismiss();
                    }
                });

                // Move the camera instantly to hamburg with a zoom of 15.
                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(UK, 5));


                // Zoom in, animating the camera.
                map.animateCamera(CameraUpdateFactory.zoomTo(2), 2000, null);
//            }
//        }, 1000);
        //new Thread(runnable).start();


    }
    private class LoadMap extends AsyncTask<String, String, LatLngBounds>
    {

        @Override
        protected LatLngBounds doInBackground(String... strings) {


            Iterator it = drawMarker.entrySet().iterator();
            String data = "bOO";


            ArrayList<Marker> places = new ArrayList<Marker>();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            while (it.hasNext())
            {
                HashMap.Entry<String, List<String>> pair = (HashMap.Entry<String, List<String>>) it.next();
                List<String> stringList = pair.getValue();
                String title = stringList.get(0);
                double lat = Double.parseDouble(stringList.get(1));
                double lng = Double.parseDouble(stringList.get(2));
                data+=""+lat+lng;
                Bitmap image = null;
                try {
                    image = new LoadImage().execute(pair.getKey()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(title));
                //map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(title).icon(BitmapDescriptorFactory.fromBitmap(image)));
                builder.include(new LatLng(lat,lng));
            }
            //Toast.makeText(MapActivity.this, data, Toast.LENGTH_SHORT).show();
            Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
                    .title("Hamburg"));
            Marker kiel = map.addMarker(new MarkerOptions()
                    .position(KIEL)
                    .title("Kiel")
                    .snippet("Kiel is cool")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_launcher)));


            final LatLngBounds bounds = builder.build();
            //mapBounds = bounds;



            return bounds;
        }

        @Override
        protected void onPostExecute(final LatLngBounds latLngBounds) {
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,15));
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    progressDialog.dismiss();
                }
            });

            // Move the camera instantly to hamburg with a zoom of 15.
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(UK, 5));


            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(2), 2000, null);
            ////}
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.list_view) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        else if (id == R.id.near)
        {
            Intent intent = new Intent(MapActivity.this, NearPlacesActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
    private class LoadImage extends AsyncTask<String,String,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url;
            Bitmap image = null;
            try {

                url = new URL("http://timothysnw.co.uk/v1/restaurants/"+strings[0]+"/image");
                try {
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap resized = null;
            try {
                resized = image.createScaledBitmap(image,(int)(image.getHeight()*0.4) ,(int)(image.getHeight()*0.4),true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resized;
        }
    }
}
