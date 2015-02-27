package com.melvin.apps.materialtests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        HashMap<String, List<String>> drawMarker =
                (HashMap<String, List<String>>) getIntent().getSerializableExtra("lat");
        Iterator it = drawMarker.entrySet().iterator();
        String data = "bOO";

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
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
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,10));
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        // Move the camera instantly to hamburg with a zoom of 15.
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(UK, 5));


        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
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
        if (id == R.id.action_settings) {
            return true;
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
