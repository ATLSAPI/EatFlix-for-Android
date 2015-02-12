package com.melvin.apps.materialtests;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.integralblue.httpresponsecache.HttpResponseCache;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;


public class RestaurantActivity extends BaseActivity {
    private  Toolbar mToolbar;
    Button b1;
    FloatingActionButton floatingActionButton;
    View.OnClickListener add_restaurant;
    Intent intent;
    ListView listView;
    RecyclerView recyclerView;
    ViewRestaurantAdapter restaurantAdapter;
    ArrayList<Integer> restaurant_id = new ArrayList<Integer>();
    ProgressDialog progressDialog;
    ImageView img;
    Bitmap bitmap;
    View convertView;
    ProgressDialog pDialog;
    ArrayList<Restaurant> mItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mItems.remove(position);
                                    restaurantAdapter.notifyItemRemoved(position);
                                }
                                restaurantAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mItems.remove(position);
                                    restaurantAdapter.notifyItemRemoved(position);
                                }
                                restaurantAdapter.notifyDataSetChanged();
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);


        initialiseCache();
        try {
            getRests hh = new getRests();
            hh.execute("jjh","jhjh","hhi");
            Toast.makeText(RestaurantActivity.this, "AM here1", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(RestaurantActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public void initialiseCache()
    {
        File httpCacheDir = getExternalCacheDir();

        // Cache Size of 5MB
        long httpCacheSize = 5 * 1024 * 1024;
        try {
            // Install the custom Cache Implementation
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if(cache != null) {
            //   If cache is present, flush it to the filesystem.
            //   Will be used when activity starts again.
            cache.flush();
        }
    }
    private class getRests extends AsyncTask<String, String, String> {
        ArrayList<Restaurant> list = new ArrayList<Restaurant>();
        @Override
        protected String doInBackground(String... strings) {
            String result = getData("http://timothysnw.co.uk/v1/restaurants");
            try {
                JSONArray jsonArray = new JSONArray(result);
                JSONObject tempObject;
                String tempString;
                if (jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        //list.add(jsonArray.get(i).toString());
                        tempString = jsonArray.get(i).toString();
                        tempObject = new JSONObject(tempString);
                        list.add(new Restaurant(
                                tempObject.getString("restaurant"),
                                tempObject.getString("address"),
                                tempObject.getString("postcode"),
                                tempObject.getString("town"),
                                tempObject.getString("type"),
                                tempObject.getString("id"),
                                tempObject.getString("cuisine")));
                        //restaurant_id.add(tempObject.getInt("id"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                mItems = list;
                restaurantAdapter = new ViewRestaurantAdapter(list);
                recyclerView.setAdapter(restaurantAdapter);
                restaurantAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                Toast.makeText(RestaurantActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public void AddRestaurant(View view) {
        intent = new Intent(this, NewRestaurant.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
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
        else if(id == android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        else if(id == R.id.add_restaurant)
        {

        }
        return super.onOptionsItemSelected(item);
    }
    class Restaurant
    {
        String id;
        String restaurant;
        String address;
        String town;
        String type;
        String postcode;
        String cuisine;
                //String description;


         Restaurant(String restaurant, String address,String postcode,String town,String type,String id,String cuisine)
        {
            this.restaurant = restaurant;
            this.type = type;
            this.postcode = postcode;
            this.cuisine = cuisine;
            this.address = address;
            this.town = town;
            this.id = id;
        }

    }
    public String getData(String url) {
        String resp="";
        try {
            // Create a new HttpClient and Post Header
            String userCredentials = "email@example.com"+":"+"password";
            String ret="Basic "+ Base64.encodeToString(userCredentials.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("Authorization",ret);
            HttpResponse response = httpclient.execute(httpget);

            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                return data;
            }
            else if(status == 404){
                   //resp = response+"";
            }
            else if(status == 400)
            {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class ViewRestaurantAdapter extends RecyclerView.Adapter<ViewRestaurantAdapter.RestaurantViewHolder> {

        private List<Restaurant> contactList;

        public ViewRestaurantAdapter(List<Restaurant> contactList) {
            this.contactList = contactList;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(RestaurantViewHolder contactViewHolder, int i) {

            Restaurant ci = contactList.get(i);
            contactViewHolder.vName.setText(ci.restaurant);

            Uri uri = Uri.parse("http://timothysnw.co.uk/v1/restaurants/" + ci.id + "/image");

            try {
                contactViewHolder.imageView.setImageBitmap(null);
                Context context = contactViewHolder.imageView.getContext();
                Picasso.with(contactViewHolder.imageView.getContext()).load(uri)
                        .into(contactViewHolder.imageView);

                //new LoadImage(contactViewHolder.imageView).execute("http://timothysnw.co.uk/v1/restaurants/" + ci.id + "/image");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
//            contactViewHolder.vSurname.setText(ci.surname);
//            contactViewHolder.vEmail.setText(ci.email);
//            contactViewHolder.vTitle.setText(ci.name + " " + ci.surname);

        }

        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.restaurant_row, viewGroup, false);

            return new RestaurantViewHolder(itemView);
        }

        public class RestaurantViewHolder extends RecyclerView.ViewHolder {
            protected TextView vName;
            protected ImageView imageView;
//            protected TextView vEmail;
//            protected TextView vTitle;

            public RestaurantViewHolder(View v) {
                super(v);
                vName =  (TextView) v.findViewById(R.id.restaurant_name);
                imageView = (ImageView) v.findViewById(R.id.restaurant_image);
//                vSurname = (TextView)  v.findViewById(R.id.txtSurname);
//                vEmail = (TextView)  v.findViewById(R.id.txtEmail);
//                vTitle = (TextView) v.findViewById(R.id.title);


            }
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        private final WeakReference imageViewReference;
        public LoadImage(ImageView imageView)
        {
            imageViewReference = new WeakReference(imageView);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(RestaurantActivity.this);
//            pDialog.setMessage("Loading Image ....");
//            pDialog.show();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            img = (ImageView) imageViewReference.get();//(ImageView) findViewById(R.id.restaurant_image);
            if(image != null){
                img.setImageBitmap(image);
                //pDialog.dismiss();
            }else{
                //pDialog.dismiss();
                //Toast.makeText(RestaurantActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }









}
