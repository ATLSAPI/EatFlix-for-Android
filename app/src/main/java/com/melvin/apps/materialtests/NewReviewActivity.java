package com.melvin.apps.materialtests;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class NewReviewActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private TextView restaurant_name, address, cuisine, postcode, average, reviewed, town, desc, rate;
    private ImageView image;
    private EditText description;
    private RatingBar rating;
    private SharedPreferences sharedPreferences;
    ProgressDialog progressDialog, progressDialog2;
    private JSONObject json;
    private Button button;
    private String restaurant_id;
    private String rating_value= "";
    String newString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
        //Set Custom toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A40300")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Hide Keyboard on load view
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //UI Views
        restaurant_name = (TextView) findViewById(R.id.restaurant_name);
        cuisine = (TextView) findViewById(R.id.cuisine);
        address = (TextView) findViewById(R.id.restaurant_address);
        postcode = (TextView) findViewById(R.id.restaurant_postcode);
        town = (TextView) findViewById(R.id.restaurant_town);
        desc = (TextView) findViewById(R.id.desc);
        rate = (TextView) findViewById(R.id.rate);
        reviewed = (TextView) findViewById(R.id.reviews_num);
        average = (TextView) findViewById(R.id.average_reviews);
        image = (ImageView) findViewById(R.id.restaurant_image);
        description = (EditText) findViewById(R.id.description);
        sharedPreferences = getSharedPreferences("Auth",MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);
        rating = (RatingBar) findViewById(R.id.rating);
        rating.setRating(5);
        final LayerDrawable stars = (LayerDrawable) rating.getProgressDrawable();
        stars.getDrawable(1).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                //int rating = Math.round(v);

                if (ratingBar.getRating()<2.5) {
                    stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                }
                if (ratingBar.getRating()>2.5 && ratingBar.getRating()<=3.5) {
                    stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                }
                if(ratingBar.getRating()>=4){
                    stars.getDrawable(2).setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                }
                if(ratingBar.getRating()==5){
                    stars.getDrawable(2).setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                }

            }
        });

        button = (Button) findViewById(R.id.submit_review);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  if(description.getText().toString().trim().isEmpty())
                  {
                      description.setError("Enter your review");

                  }
                else {
//                      progressDialog2.setTitle("Loading");
//                      progressDialog2.setMessage("Loading restaurant...");
//                      progressDialog2.show();
                      rating_value = rating.getRating()+"";
                       new PostReviewData().execute(description.getText().toString(), newString, rating_value);
                  }
            }
        });



        //String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("id");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("id");
            restaurant_id = newString;
        }
        Uri uri = Uri.parse("http://timothysnw.co.uk/v1/restaurants/" + newString + "/image");
        Picasso.with(image.getContext()).load(uri)
                .into(image);
        try {
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Loading restaurant...");
            progressDialog.show();
            new GetRestaurantData().execute("http://timothysnw.co.uk/v1/restaurants/" + newString);
        }
        catch (Exception e)
        {

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_review, menu);
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
    public void SetData()
    {

        JSONObject jsonObject = json;
        try {
            setTitle(jsonObject.getString("restaurant") +" " + jsonObject.getString("town"));
            restaurant_name.setText(jsonObject.getString("restaurant"));
            cuisine.setText(jsonObject.getString("cuisine"));
            address.setText(jsonObject.getString("address"));
            postcode.setText(jsonObject.getString("postcode"));
            average.setText((int) Math.round(Double.parseDouble(jsonObject.getString("average")))+"");
            reviewed.setText(jsonObject.getString("reviewed"));
            desc.setText(jsonObject.getString("description"));
            town.setText(jsonObject.getString("town"));
            rate.setText("Rate "+jsonObject.getString("restaurant")+"!!!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //restaurant_name.setText(jsonObject.getString("restaurant"));
    }
    private class PostReviewData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            String url = "http://timothysnw.co.uk/v1/reviews";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> name_value = new ArrayList<NameValuePair>();
            name_value.add(new BasicNameValuePair("description",strings[0]));
            name_value.add(new BasicNameValuePair("restaurant_id",strings[1]));
            name_value.add(new BasicNameValuePair("rating",strings[2]));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(name_value));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String token = sharedPreferences.getString("token", "");
            httpPost.setHeader("Token", token);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String data = null;
            int status = 0;
            if (response != null) {
                status = response.getStatusLine().getStatusCode();
            }

            if (status == 201) {
                //Retrieval success
                HttpEntity entity = response.getEntity();
                try {
                    data = EntityUtils.toString(entity);
                    data = "OK";
                    //Success
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Retrieval Failed
                HttpEntity entity = response.getEntity();
                try {
                    data = EntityUtils.toString(entity);
                    //data = status+"";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpclient.getConnectionManager().shutdown();
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("OK")) {
                SetData();
                //progressDialog2.dismiss();
//                Intent intent = new Intent(NewReviewActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                Toast.makeText(NewReviewActivity.this, "Review added!!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(NewReviewActivity.this, "Review failed!!"+s, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
    private class GetRestaurantData extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(strings[0]);
            HttpResponse response = null;
            try {

                response = httpclient.execute(httpGet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String data = null;
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                //Retrieval success
                HttpEntity entity = response.getEntity();
                try {
                    data = EntityUtils.toString(entity);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        json = jsonObject;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    data = "OK";
                    //Success
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Retrieval Failed
                HttpEntity entity = response.getEntity();
                try {
                    data = EntityUtils.toString(entity);
                    data = status+"";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpclient.getConnectionManager().shutdown();
            return data;
        }


        @Override
        protected void onPostExecute(String s) {
            if (s.equals("OK")) {
                SetData();
                progressDialog.dismiss();
//                Toast.makeText(NewReviewActivity.this, "Review added!!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(NewReviewActivity.this, "Failed to load restaurant!!"+s, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
