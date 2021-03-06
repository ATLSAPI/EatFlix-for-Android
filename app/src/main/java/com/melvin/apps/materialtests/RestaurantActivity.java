package com.melvin.apps.materialtests;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
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
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.app.PendingIntent.getActivity;


public class RestaurantActivity extends ActionBarActivity implements NavigationDrawerCallbacks {
    private  Toolbar mToolbar;
    Button b1;
    FloatingActionButton floatingActionButton;
    View.OnClickListener add_restaurant;
    Intent intent;
    ListView listView;
    RecyclerView recyclerView;
    ViewRestaurantAdapter restaurantAdapter;
    ArrayList<Integer> restaurant_id = new ArrayList<Integer>();
    ProgressDialog progressDialog, progressDialog2;
    ImageView img;
    Bitmap bitmap;
    View convertView;
    ProgressDialog pDialog;
    ArrayList<Restaurant> mItems;
    SharedPreferences sharedPreferences;
    TextView error_tv, netfail;
    private AlertDialog.Builder alertDialog, alertDialog2;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int color = 0;
    private boolean added = false;
    AlertDialogManager alertDialogManager = new AlertDialogManager();
    OnItemTouchListener itemTouchListener;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private HashMap<String, List<String>> map;
    private HashMap<String, List<String>> arrayList;
    private DrawerLayout drawerLayout;
    private Menu actionBarMenu;
    private String type;
    private String get_url = "http://timothysnw.co.uk/v1/restaurants";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
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
        sharedPreferences = getSharedPreferences("Auth",MODE_PRIVATE);
        netfail = (TextView) findViewById(R.id.net_fail);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_restaurant_swipe_refresh_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        //
        map = new HashMap<String, List<String>>();
        arrayList = new HashMap<String, List<String>>();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);


//        mToolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new GetLatLngFromAddress().execute();
//            }
//        });
        try {
            type = bundle.getString("type").toLowerCase();

            switch (type)
            {
                case "bar":
                    get_url = "http://timothysnw.co.uk/v1/bars";
                    setTitle("Bars");
                    break;
                case "pub":
                    get_url = "http://timothysnw.co.uk/v1/pubs";
                    setTitle("Pubs");
                    break;
                default:
                    break;
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected()) {
                    alertDialog2.show();
                    swipeRefreshLayout.setRefreshing(false);
                    netfail.setVisibility(View.VISIBLE);
                    netfail.setText("No network detected. Pull down to refresh");
                }
                else {
                    getRests hh = new getRests();
                    hh.execute();
                    swipeRefreshLayout.setRefreshing(false);
                    netfail.setVisibility(View.INVISIBLE);
                }
            }
        });


        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(recyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipe(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                int swiped_id = 0;
                                for (int position : reverseSortedPositions) {
                                    mItems.remove(position);
                                    //Toolbar toolbar = (Toolbar)recyclerView.findViewHolderForPosition(position).;
                                    swiped_id = position;
                                    //swipeRefreshLayout.setRefreshing(true);
                                    restaurantAdapter.notifyItemRemoved(position);
                                }
                                restaurantAdapter.notifyDataSetChanged();

                                Toast.makeText(RestaurantActivity.this, "Position: "+restaurant_id.get(swiped_id), Toast.LENGTH_LONG).show();
                                //Delete here
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                int swiped_id = 0;
                                for (int position : reverseSortedPositions) {
                                    mItems.remove(position);
                                    swiped_id = position;
                                    restaurantAdapter.notifyItemRemoved(position);
                                }
                                restaurantAdapter.notifyDataSetChanged();

                                Toast.makeText(RestaurantActivity.this, "Position: "+restaurant_id.get(swiped_id), Toast.LENGTH_LONG).show();
                                //Delete here
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);



        //Initialise Http cache
        //initialiseCache();

        //AlertDialog
        //AlertDialog
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("You need to be logged in")
                .setMessage("Login?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent_add = new Intent(RestaurantActivity.this, LoginActivity.class);
                        //intent_add.putExtra("selected_restaurants_id",reviews_id.get(i));
                        startActivity(intent_add);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog2 = new AlertDialog.Builder(this)
                .setTitle("No network connection detected")
                .setMessage("Retry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!isNetworkConnected()) {
                            alertDialog2.show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        netfail.setText("No network detected. Pull down to refresh");
                    }
                });
        try {
            if (!isNetworkConnected()) {
                alertDialog2.show();
            }
            else {
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Loading restaurants...");
                progressDialog.show();
                getRests objAsync = new getRests();
                objAsync.execute();
            }

        } catch (Exception e) {
            Toast.makeText(RestaurantActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        itemTouchListener = new OnItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                Intent intent = new Intent(RestaurantActivity.this, NewReviewActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id", restaurant_id.get(position)+"");
                startActivity(intent);
                //Toast.makeText(RestaurantActivity.this, "Tapped " + restaurant_id.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToolbarClick(View view, int position) {
                Toast.makeText(RestaurantActivity.this, "Clicked Toolbar in " + mItems.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToolbarMenuItemClick(View view, int position) {
                Toast.makeText(RestaurantActivity.this, "Clicked ToolbarMenuItem in " + mItems.get(position), Toast.LENGTH_SHORT).show();
            }
        };
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }
    public interface OnItemTouchListener {
        /**
         * Callback invoked when the user Taps one of the RecyclerView items
         *
         * @param view     the CardView touched
         * @param position the index of the item touched in the RecyclerView
         */
        public void onCardViewTap(View view, int position);

        /**
         * Callback invoked when the Button1 of an item is touched
         *
         * @param view     the Button touched
         * @param position the index of the item touched in the RecyclerView
         */
        public void onToolbarClick(View view, int position);

        /**
         * Callback invoked when the Button2 of an item is touched
         *
         * @param view     the Button touched
         * @param position the index of the item touched in the RecyclerView
         */

        public void onToolbarMenuItemClick(View view, int position);
    }
    /*
    Cache for Http connections
     */
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
    public boolean isNetworkConnected() {
        NetworkInfo ni = null;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            ni = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
    private class getRests extends AsyncTask<String, String, String> {
        ArrayList<Restaurant> list = new ArrayList<>();
        @Override
        protected String doInBackground(String... strings) {
            String result = getData(get_url);

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
                                tempObject.getString("cuisine"),
                                tempObject.getString("reviewed"),
                                tempObject.getString("average"),
                                tempObject.getString("latitude"),
                               tempObject.getString("longitude")));
                        restaurant_id.add(tempObject.getInt("id"));
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
                restaurantAdapter = new ViewRestaurantAdapter(list, itemTouchListener);
                recyclerView.setAdapter(restaurantAdapter);
                restaurantAdapter.notifyDataSetChanged();
                if (restaurantAdapter.getItemCount()>0)
                {
                    added = true;
                }
                actionBarMenu.getItem(0).setEnabled(true);
//                invalidateOptionsMenu();
                progressDialog.dismiss();

            } catch (Exception e) {
                Toast.makeText(RestaurantActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public void AddRestaurant(View view) {
        if (sharedPreferences.contains("token") && sharedPreferences.contains("id") && sharedPreferences.contains("device_id")) {
            //User is logged in
            intent = new Intent(this, NewRestaurant.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else
        {
            alertDialog.show();
            //Not Logged in
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        actionBarMenu = menu;
        menu.getItem(0).setEnabled(false);
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
        else if (id == R.id.near)
        {
            Intent intent = new Intent(RestaurantActivity.this, NearPlacesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        else if(id == R.id.map_launcher) {

//            progressDialog2.setTitle("Loading");
//            progressDialog2.setMessage("Loading map coordinates...");
//            progressDialog2.show();
            if (added) {
                Intent intent1 = new Intent(RestaurantActivity.this, MapActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //arrayList.add(map);
                //TODO: Implement arrayList HashMap here
                intent1.putExtra("lat", map);
                //progressDialog2.dismiss();
                startActivity(intent1);
            }
            else
            {
                alertDialogManager.showAlertDialog(RestaurantActivity.this, "Map View",
                        "No "+getTitle()+" to show",
                        false);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    class Restaurant implements Serializable
    {
        String id;
        String restaurant;
        String address;
        String town;
        String type;
        String postcode;
        String cuisine;
        String reviewed;
        String average;
        String latitude;
        String longitude;
                //String description;


        Restaurant(String restaurant, String address,String postcode,String town,String type,String id,String cuisine, String reviewed, String average, String latitude, String longitude)
        {
            this.restaurant = restaurant;
            this.type = type;
            this.postcode = postcode;
            this.cuisine = cuisine;
            this.address = address;
            this.town = town;
            this.id = id;
            this.reviewed = reviewed;
            this.average = average;
            this.latitude =  latitude;
            this.longitude =  longitude;
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
        private OnItemTouchListener onItemTouchListener;

        public ViewRestaurantAdapter(List<Restaurant> contactList, OnItemTouchListener onItemTouchListener) {
            this.contactList = contactList;
            this.onItemTouchListener = onItemTouchListener;
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        @Override
        public void onBindViewHolder(RestaurantViewHolder contactViewHolder, int i) {

            Restaurant ci = contactList.get(i);
            contactViewHolder.vName.setText(ci.cuisine+" cuisine");

            Uri uri = Uri.parse("http://timothysnw.co.uk/v1/restaurants/" + ci.id + "/image");
            /*
            */
//            new Shuffle().execute("http://timothysnw.co.uk/v1/restaurants/" + ci.id + "/image");
//            contactViewHolder.toolbar.setBackgroundColor(color);
            /*
             */
            //final String full_address = ci.address.replace(" ","+")+ci.town.replace(" ","+")+ci.postcode.replace(" ","+")+"+UK";
            final String full_address = ci.address+" "+ci.town+" "+ci.postcode;
            try {
//                Picasso.with(contactViewHolder.imageView.getContext()).load(R.drawable.restaurant_default)
//                        .into(contactViewHolder.imageView);
//                contactViewHolder.imageView.setImageResource(R.drawable.restaurant_default);
                final String review_num = ci.reviewed;
                final String average = String.valueOf(Float.parseFloat(ci.average));
                final String name = ci.restaurant;
                final int percentage = Math.round(Float.parseFloat(ci.average) / 5 * 100);
                Context context = contactViewHolder.imageView.getContext();
                Picasso.with(contactViewHolder.imageView.getContext()).load(uri)
                        .into(contactViewHolder.imageView);
                contactViewHolder.toolbar.setTitle(ci.restaurant+" "+ci.town);
                //contactViewHolder.toolbar.setBackgroundColor(getResources().getColor(R.color.myPrimaryColor));
                contactViewHolder.toolbar.setTitleTextColor(getResources().getColor(R.color.ControlText));
                if (contactViewHolder.toolbar != null) {
                    // inflate your menu
                    contactViewHolder.toolbar.getMenu().clear();
                    contactViewHolder.toolbar.inflateMenu(R.menu.menu_cardview);
                    contactViewHolder.toolbar.getMenu().findItem(R.id.action_share).setTitle(ci.id);
                    contactViewHolder.toolbar.getMenu().findItem(R.id.action_add).setTitle(ci.id);
                    contactViewHolder.toolbar.getMenu().findItem(R.id.action_delete).setTitle(ci.id);
                    contactViewHolder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            if (menuItem.getItemId() == R.id.action_share) {
                                //menuItem
                                Toast.makeText(RestaurantActivity.this, menuItem.getTitle() + "", Toast.LENGTH_LONG).show();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT,
                                        Html.fromHtml("<b>Name: <b/>")+name+ Html.fromHtml("<br>")+
                                        Html.fromHtml("<b>Address: <b/>")+full_address+ Html.fromHtml("<br>")+
                                        Html.fromHtml("<b>Number of Reviews: <b/>")+review_num+ Html.fromHtml("<br>")+
                                        Html.fromHtml("<b>Average Reviews: <b/>")+average+ Html.fromHtml("<br>")+
                                        Html.fromHtml("<b>Percentage: <b/>")+percentage+"%");
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.sendto)));
                            } else if (menuItem.getItemId() == R.id.action_delete) {

                            } else if (menuItem.getItemId() == R.id.action_add) {
                                Intent intent = new Intent(RestaurantActivity.this, NewReviewActivity.class);
                                intent.putExtra("id", menuItem.getTitle());
                                startActivity(intent);
                            }
                            return true;
                        }

                    });

                }


                //new LoadImage(contactViewHolder.imageView).execute("http://timothysnw.co.uk/v1/restaurants/" + ci.id + "/image");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            float percent = Float.parseFloat(ci.average)/5 * 100;
            contactViewHolder.average.setText(Math.round(percent)+"%");
            contactViewHolder.number.setText(ci.reviewed+" reviews");
            //contactViewHolder.vTitle.setText(ci.name + " " + ci.surname);
            //
            //String full_address = ci.address.replace(" ","+")+ci.town.replace(" ","+")+ci.postcode.replace(" ","+")+"+UK";
            //Load List
            String output = "";
            ci.address.replace(" ","+");
            //TODO: Cut out here
//            String address_this = ci.address.replace(" ","+")+ci.town.replace(" ","+")+ci.postcode.replace(" ","+")+"+UK";
//            try {
//                output = new GetLatLngFromAddress().execute(ci.address.replace(" ","+")+ci.town.replace(" ","+")+ci.postcode.replace(" ","+")+"+UK").get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            if(!output.trim().isEmpty()) {
//                //contactViewHolder.coordinates.setText(output);
//                List<String> restaurants = new ArrayList<>();
//                restaurants.add(0,ci.restaurant);
//                String[] lat_lng =output.split(",");
//                restaurants.add(1, lat_lng[0]);
//                restaurants.add(2, lat_lng[1]);
//
//                map.put(ci.id,restaurants);
//            }
//            List<String> info = new ArrayList<>();
//            info.add(0,ci.restaurant);
//            info.add(1, full_address);
//            arrayList.put(ci.id, info);
            //Inserted...

//            List<Restaurant> restaurants = new ArrayList<>();
//            restaurants.add(ci);
//            map.put(ci.id+","+output,restaurants);
            if(!ci.longitude.equals("none")) {
                List<String> restaurants = new ArrayList<>();
                restaurants.add(0, ci.restaurant);
                restaurants.add(1, ci.latitude);
                restaurants.add(2, ci.longitude);
                map.put(ci.id, restaurants);

            }

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
            protected Toolbar toolbar;
            protected TextView average;
            protected TextView number, coordinates;

            public RestaurantViewHolder(View v) {
                super(v);
                vName =  (TextView) v.findViewById(R.id.restaurant_name);
                imageView = (ImageView) v.findViewById(R.id.restaurant_image);
                //imageView.setAlpha(65);
                toolbar = (Toolbar) v.findViewById(R.id.card_toolbar);
                coordinates = (TextView) v.findViewById(R.id.coordinates);
                average = (TextView)  v.findViewById(R.id.average_reviews);
                number = (TextView)  v.findViewById(R.id.reviews_num);
                //vTitle = (TextView) v.findViewById(R.id.title);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemTouchListener.onCardViewTap(view, getPosition());
                    }
                });


            }
        }
    }
    private class Shuffle extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            Bitmap bitmap1;
            Uri uri = Uri.parse(strings[0]);
            int jb =0;
            try {
                bitmap1 = Picasso.with(getApplicationContext()).load(uri)
                        .get();
                Bitmap bitmap = bitmap1; //assign your bitmap here
                int redColors = 0;
                int greenColors = 0;
                int blueColors = 0;
                int pixelCount = 0;

                for (int y = 0; y < bitmap.getHeight(); y++)
                {
                    for (int x = 0; x < bitmap.getWidth(); x++)
                    {
                        int c = bitmap.getPixel(x, y);
                        pixelCount++;
                        redColors += Color.red(c);
                        greenColors += Color.green(c);
                        blueColors += Color.blue(c);
                    }
                }
                // calculate average of bitmap r,g,b values
                int red = (redColors/pixelCount);
                int green = (greenColors/pixelCount);
                int blue = (blueColors/pixelCount);
                jb = Color.rgb(red, green, blue);
                color = jb;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return jb+"";
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
    private class GetLatLngFromAddress extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
//            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyCbsof3EMi0eHl8nZuobes6Mj_W768iSec";
//            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=47+The+Linkway+Horwich,BL66JAUK&key=AIzaSyCbsof3EMi0eHl8nZuobes6Mj_W768iSec";
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+strings[0]+"&key=AIzaSyCbsof3EMi0eHl8nZuobes6Mj_W768iSec";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = null;
            try {

                response = httpclient.execute(httpGet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String data = null;
            int status = 0;
            if (response != null) {
                status = response.getStatusLine().getStatusCode();
            }

            if (status == 200) {
                //Login success
                HttpEntity entity = response.getEntity();
                try {
                    data = EntityUtils.toString(entity);
                    try {
                        JSONObject jsonObject= new JSONObject(data);
                        JSONArray results = jsonObject.getJSONArray("results");
                        JSONObject location = (JSONObject) results.get(0);
                        JSONObject geometry = location.getJSONObject("geometry");
                        JSONObject locationJSONObject = geometry.getJSONObject("location");
                        double latitude = locationJSONObject.getDouble("lat");
                        double longitude = locationJSONObject.getDouble("lng");
                        data = latitude+","+longitude;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        data = "";
                    }
                } catch (IOException e) {
                    data = "";
                }
            }
            return data;
        }

//        @Override
//        protected void onPostExecute(String s) {
//            //Toast.makeText(RestaurantActivity.this, s, Toast.LENGTH_LONG).show();
//            super.onPostExecute(s);
//
//        }
    }


}
