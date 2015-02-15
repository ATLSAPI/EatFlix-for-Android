package com.melvin.apps.materialtests;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    ArrayList<Integer> reviews_id = new ArrayList<Integer>();
    private NavigationDrawerFragment mNavigationDrawerFragment;
    ListView lv1;
    ArrayAdapter<String> adapter;

    private static final String DEBUG_TAG = "HttpExample";
    private EditText urlText;
    private TextView textView;
    public final static String apiURL = "http://www.cheesejedi.com/rest_services/get_big_cheese.php?puzzle=1";
    public static final String TAG = MainActivity.class.getSimpleName();
    ListAdapter mAdapter;
    ReviewAdapter reviewAdapter;
    ListView listView;
    TextView error_tv, netfail;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressBar;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_topdrawer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        reviewAdapter = new ReviewAdapter(this);
        listView = (ListView) findViewById(R.id.reviewList);
        error_tv = (TextView) findViewById(R.id.error_tv);
        netfail = (TextView) findViewById(R.id.net_fail);
        progressBar = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("Auth",MODE_PRIVATE);
        //initialiseCache();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

            @Override
            public void onRefresh() {
                //listView.setAdapter(reviewAdapter);

                LongRunningGetIO longRunningGetIO = new LongRunningGetIO();
                longRunningGetIO.execute("reviews_get", "get");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        /**
         * Check internet connection first
         */
        boolean check =  isNetworkConnected();

        if (!isNetworkConnected())
        {

            new AlertDialog.Builder(this)
                    .setTitle("No network connection detected")
                    .setMessage("Retry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                LongRunningGetIO longRunningGetIO = new LongRunningGetIO();
                                longRunningGetIO.execute("reviews_get", "get");
                                AdapterView.OnItemClickListener t1 = new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        //Toast.makeText(MainActivity.this, "AM here" + "" + reviews_id.get(i), Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
//                        intent.putExtra("selected_restaurants_id",reviews_id.get(i));
//                        startActivity(intent);
                                    }
                                };
                                listView.setOnItemClickListener(t1);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                            }
                            netfail.setText("No network detected. Pull down to refresh");
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            try {
                progressBar.setTitle("Loading");
                progressBar.setMessage("Wait while loading...");
                progressBar.show();
                LongRunningGetIO longRunningGetIO = new LongRunningGetIO();
                longRunningGetIO.execute("reviews_get", "get");
                AdapterView.OnItemClickListener t1 = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(MainActivity.this, "AM here" + "" + reviews_id.get(i), Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
//                        intent.putExtra("selected_restaurants_id",reviews_id.get(i));
//                        startActivity(intent);
                    }
                };
                netfail.setVisibility(View.INVISIBLE);
                listView.setOnItemClickListener(t1);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error occured", Toast.LENGTH_LONG).show();
            }
        }
        //listView.setAdapter(reviewAdapter);
        /**
         * Nav drawer implement
         */
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        //SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        /**
         * Get and display
         */
    }
    public void initialiseCache()
    {
        File httpCacheDir = getExternalCacheDir();

        // Cache Size of 5MB
        long httpCacheSize = 5 * 1024 * 1024;
        try {
            // Install the custom Cache Implementation
            com.integralblue.httpresponsecache.HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        com.integralblue.httpresponsecache.HttpResponseCache cache = com.integralblue.httpresponsecache.HttpResponseCache.getInstalled();
        if(cache != null) {
            //   If cache is present, flush it to the filesystem.
            //   Will be used when activity starts again.
            cache.flush();
        }
    }
    private class emailVerificationResult {
        public String statusNbr;
        public String hygieneResult;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
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

    private class LongRunningGetIO extends AsyncTask <String, Integer, String> {
        Map<String, String> myMap = new HashMap<String, String>();
        ArrayList<SingleRow> list = new ArrayList<SingleRow>();
        @Override
        protected void onPreExecute() {
            myMap.put("review_get","http://timothysnw.co.uk/v1/reviews");
            myMap.put("review_post","http://timothysnw.co.uk/v1/reviews");
            super.onPreExecute();
        }

         @Override
        protected String doInBackground(String... params) {
            String result="bbs";
            if(params[1].equals("get")) {
               result = getData("http://timothysnw.co.uk/v1/reviews");
                try {
                  JSONArray jsonArray = new JSONArray(result);
                    JSONObject tempObject;
                    String tempString;
                    if (jsonArray != null) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            //list.add(jsonArray.get(i).toString());
                            tempString = jsonArray.get(i).toString();
                            tempObject = new JSONObject(tempString);
                            list.add(new SingleRow(
                                    tempObject.getString("restaurant"),
                                    tempObject.getString("cuisine"),
                                    tempObject.getString("type"),
                                    tempObject.getString("created"),
                                    tempObject.getString("modified"),
                                    tempObject.getString("description"),
                                    //tempObject.getString("image"),
                                    tempObject.getString("rating")));
                            reviews_id.add(tempObject.getInt("id"));
                       }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
           }
            else if(params[1].equals("post")){
                result =postData("http://timothysnw.co.uk/v1/reviews");
            }
            return params[1];
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            if (s.equals("get")) {
                reviewAdapter.setItemList(list);
                listView.setAdapter(reviewAdapter);
                reviewAdapter.notifyDataSetChanged();
                progressBar.dismiss();
                //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                //error_tv.setText(s);
            }
            else{
                error_tv.setText(s);
            }
        }

        public String getData(String url) {
            try {
                // Create a new HttpClient and Post Header
//                String userCredentials = "email@example.com"+":"+"password";
//                String ret="Basic "+Base64.encodeToString(userCredentials.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                //httpget.setHeader("Authorization",ret);
                HttpResponse response = httpclient.execute(httpget);

                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    return data;
              }
                else {
                    httpclient.getConnectionManager().shutdown();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "still on it";
        }

        public String postData(String url){
            InputStream inputStream = null;
            String result = "";
            try{
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                String json = "";
                // 3. build jsonObject

                json = prepareData();

                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                //Set authentication header here
                HttpResponse response= httpClient.execute(httpPost);


                inputStream = response.getEntity().getContent();

                String status = " "+response.getStatusLine();
                httpClient.getConnectionManager().shutdown();
                if(inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                }
                else {
                    result = status+json;
                }
                return result;
            } catch(Exception e){
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return null;
        }

        public String prepareData(){
            String json="";
//            String review = review_et.getText().toString();
//            String rating = rating_et.getText().toString();
//            int rate = Integer.parseInt(rating);
//            Date date = new Date();
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String today = dateFormat.format(date);
//            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//            String thisMoment = timeFormat.format(date);
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("review", review);
//                jsonObject.accumulate("rating", rate);
//                jsonObject.accumulate("review_date", today);
//                jsonObject.accumulate("review_time", thisMoment);
//                jsonObject.accumulate("restaurants_id", restaurantsId);
//                json = jsonObject.toString();
//                return json;
//            }
//            catch (JSONException e){
//                e.printStackTrace();
//            }
           return json;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

    }

    class SingleRow
    {
        String restaurant;
        String type;
        String postcode;
        String cuisine;
        String created;
        String description;
        String rating;
        //String image;


        SingleRow(String restaurant, String type, String postcode, String cuisine, String created, String description, String rating)
        {
            this.restaurant = restaurant;
            this.type = type;
            this.postcode = postcode;
            this.cuisine = cuisine;
            this.created = created;
            this.rating = rating;
            this.description = description;
            //this.image = image;
        }

    }
    class ReviewAdapter extends BaseAdapter
    {
        ArrayList<SingleRow> list;
        Context context;

        ReviewAdapter(Context c)
        {
            context = c;
            //setItemList();
        }
        public void setItemList(ArrayList myList){
            list = new ArrayList<SingleRow>();
            list = myList;
//            for (int i=0; i<str.length; i++)
//            {
//                list.add(new SingleRow(str[i]));
//            }
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        class MyViewHolder {
            TextView tv1, tv2, tv3, tv4, tv5, tv6;
            LinearLayout l1;
            RatingBar r1;
            MyViewHolder(View view)
            {
                ///Instantiate views here....
                //tv1 = (TextView) view.findViewById(R.id.header);
                tv2 = (TextView) view.findViewById(R.id.restaurant);
                tv3 = (TextView) view.findViewById(R.id.type);
                tv4 = (TextView) view.findViewById(R.id.created);
                tv5 = (TextView) view.findViewById(R.id.description);
                tv6 = (TextView) view.findViewById(R.id.cuisine);
                r1 = (RatingBar) view.findViewById(R.id.rating);
                //l1 = (LinearLayout) findViewById(R.id.background);
                r1.isInEditMode();
            }
        }
        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View row = convertView;
            MyViewHolder holder = null;
            if (row == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.review_row, parent, false);
                holder = new MyViewHolder(row);
                row.setTag(holder);
                Log.d("Test", "Creating");
            }
            else{
                holder = (MyViewHolder) row.getTag();
                Log.d("Test", "Creating");
            }



            ///Set here....
            SingleRow temp = list.get(i);

            //holder.tv1.setText(temp.restaurant);
            holder.tv2.setText(temp.restaurant);
            holder.tv3.setText(temp.type);
            holder.tv4.setText(temp.created);
            holder.tv5.setText(temp.description);
            holder.tv6.setText(temp.cuisine);
            holder.r1.setRating(Integer.parseInt(temp.rating));

//            try {
//                File imagefile = new File(temp.image);
//                FileInputStream fis = new FileInputStream(imagefile);
//                Bitmap bmImg = BitmapFactory.decodeStream(fis);
//                BitmapDrawable background = new BitmapDrawable(bmImg);
//                //holder.l1.setBackgroundDrawable(background);
//            } catch (FileNotFoundException e) {
//                //e.printStackTrace();
//            }
            return row;
        }
    }

}
