package com.melvin.apps.materialtests;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NewRestaurant extends ActionBarActivity {
    private Toolbar mToolbar;
    Button b1, b2;
    private String[] mFileList;
    private File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
    private String mChosenFile;
    private static final String FTYPE = ".txt";
    private static final int DIALOG_LOAD_FILE = 1000;
    public static final String TAG = NewRestaurant.class.getSimpleName();
    FileDialog fileDialog;
    private static int RESULT_LOAD_IMAGE = 1;
    ProgressDialog progress, progress2;
    String path;
    ArrayList<Integer> restaurant_id = new ArrayList<Integer>();
    RestaurantAdapter restaurantAdapter;
    public static String image = "";
    ListView listView;
    EditText et1,et2,et3,et4,et5,et6,et7;
    Spinner sp1, sp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        b1 = (Button) findViewById(R.id.image_btn);
        progress = new ProgressDialog(this);
        progress2 = new ProgressDialog(this);
        et1 = (EditText) findViewById(R.id.name_edit_text);
        et2 = (EditText) findViewById(R.id.email_edit_text);
        et3 = (EditText) findViewById(R.id.telephone_edit_text);
        et4 = (EditText) findViewById(R.id.postcode_edit_text);
        et5 = (EditText) findViewById(R.id.address_edit_text);
        et6 = (EditText) findViewById(R.id.description);
        et7 = (EditText) findViewById(R.id.town);
        sp1 = (Spinner) findViewById(R.id.type_spinner);
        sp2 = (Spinner) findViewById(R.id.type_cuisine);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LongRunningGetIO longRunningGetIO = new LongRunningGetIO();
                longRunningGetIO.execute("review_post","post");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_restaurant) {
            return true;
        }
        else if(id == R.id.submit_restaurant) {
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.show();
            LongRunningGetIO longRunningGetIO = new LongRunningGetIO();
            longRunningGetIO.execute("review_post","post");
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //try{

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();
                //selectedImage.
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.show();
                image = picturePath;
                //path = picturePath;
                 //new LoadImg().execute(picturePath);
                File imagefile = new File(picturePath);
                long length = imagefile.length()/1024;
                if (length<200) {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(imagefile);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Bitmap bm = BitmapFactory.decodeStream(fis);

                    String imgString = Base64.encodeToString(getBytesFromBitmap(bm), Base64.NO_WRAP);
                    //Toast.makeText(NewRestaurant.this, imgString, Toast.LENGTH_LONG).show();
                    //Toast.makeText(NewRestaurant.this, picturePath, Toast.LENGTH_LONG).show();
                    byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ImageView img = (ImageView) findViewById(R.id.image_view);
                    img.setImageBitmap(decodedByte);
                    // get the base 64 string
                    progress.dismiss();
                    //Toast.makeText(NewRestaurant.this, "Length is " + length + "KB", Toast.LENGTH_LONG).show();
                }
                else
                {
                    progress.dismiss();
                    Toast.makeText(NewRestaurant.this, "File is too big. Max 200KB", Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
            catch (Exception e)
            {
                Toast.makeText(NewRestaurant.this, "Failed!! Select another image"+e.getMessage(), Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
            ///
        }

    }
//    private class LoadImg extends AsyncTask<String, String, String>{
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            try {
//                File imagefile = new File(path);
//                FileInputStream fis = null;
//                try {
//                    fis = new FileInputStream(imagefile);
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                Bitmap bm = BitmapFactory.decodeStream(fis);
//                String imgString = Base64.encodeToString(getBytesFromBitmap(bm), Base64.NO_WRAP);
//                //Toast.makeText(NewRestaurant.this, imgString, Toast.LENGTH_LONG).show();
//                //Toast.makeText(NewRestaurant.this, picturePath, Toast.LENGTH_LONG).show();
//                byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//                ImageView img = (ImageView) findViewById(R.id.image_view);
//                img.setImageBitmap(decodedByte);
//
//            } catch (Exception e) {
//                progress.dismiss();
//                //e.printStackTrace();
//                Log.d(TAG, e.getMessage());
//            }
//            return "ok";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            progress.dismiss();
//        }
//    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
    class SingleRow
    {
        String name;
        String address;
        String town;
        String image;
        int type_id;
        String postcode;
        int cuisine_id;
        String description;


        SingleRow(String name, String address,String town,String image,int type_id,String postcode,int cuisine_id,String description)
        {
            this.name = name;
            this.type_id = type_id;
            this.postcode = postcode;
            this.cuisine_id = cuisine_id;
            this.address = address;
            this.town = town;
            this.description = description;
            this.image = image;
        }

    }
    private class LongRunningGetIO extends AsyncTask <String, Integer, String> {
        Map<String, String> myMap = new HashMap<String, String>();
        ArrayList<SingleRow> list = new ArrayList<SingleRow>();
        @Override
        protected void onPreExecute() {
            myMap.put("review_get","http://eatflix.x10.mx/v1/reviews");
            myMap.put("review_post","http://eatflix.x10.mx/v1/reviews");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result="bbs";
            if(params[1].equals("get")) {
                result = getData("http://eatflix.x10.mx/v1/restaurants");
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
                                    tempObject.getString("name"),
                                    tempObject.getString("address"),
                                    tempObject.getString("town"),
                                    tempObject.getString("image"),
                                    tempObject.getInt("type_id"),
                                    tempObject.getString("postcode"),
                                    tempObject.getInt("cuisine_id"),
                                    tempObject.getString("description")));
                            restaurant_id.add(tempObject.getInt("id"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(params[1].equals("post")){
                result = postData("http://eatflix.x10.mx/v1/restaurants");
            }
            return params[1];
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("get")) {
                restaurantAdapter.setItemList(list);
                listView.setAdapter(restaurantAdapter);
                restaurantAdapter.notifyDataSetChanged();
                //error_tv.setText(s);
            }
            else{
                progress2.dismiss();
                //error_tv.setText(s);
            }
        }

        public String getData(String url) {
            try {
                // Create a new HttpClient and Post Header
                String userCredentials = "email@example.com"+":"+"password";
                String ret="Basic "+Base64.encodeToString(userCredentials.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
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
                Toast.makeText(NewRestaurant.this, "Building json", Toast.LENGTH_LONG).show();
                String json = "";
                // 3. build jsonObject

                json = prepareData();

                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);

                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                //Set authentication header here
                String userCredentials = "email@example.com"+":"+"password";
                String ret="Basic "+Base64.encodeToString(userCredentials.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);

                httpPost.setHeader("Authorization",ret);
                HttpResponse response= httpClient.execute(httpPost);


                inputStream = response.getEntity().getContent();

                String status = " "+response.getStatusLine();

                if(inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                }
                else {
                    result = status+json;
                }
                return result;
            } catch(Exception e){
                Log.d("InputStream", e.getLocalizedMessage());
                Toast.makeText(NewRestaurant.this, "Failed!! "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        public String prepareData(){
            String json="";
            String name = et1.getText().toString();
            String description = et6.getText().toString();
            String address = et5.getText().toString();
            String town = et6.getText().toString();
            String postcode = et6.getText().toString();
            String imagey = image;
            int cuisine_id = (int) sp2.getSelectedItem();
            int type_id = (int) sp2.getSelectedItem();
            cuisine_id = 1;
            type_id =1;
//            int rate = Integer.parseInt(rating);
//            Date date = new Date();
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String today = dateFormat.format(date);
//            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            //String thisMoment = timeFormat.format(date);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("name", name);
                jsonObject.accumulate("description", description);
                jsonObject.accumulate("address", address);
                jsonObject.accumulate("town", town);
                jsonObject.accumulate("postcode", postcode);
                jsonObject.accumulate("image", imagey);
                jsonObject.accumulate("cuisine_id", cuisine_id);
                jsonObject.accumulate("type_id", type_id);
                json = jsonObject.toString();
                return json;
            }
            catch (JSONException e){
                e.printStackTrace();
            }
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
    class RestaurantAdapter extends BaseAdapter
    {
        ArrayList<SingleRow> list;
        Context context;

        RestaurantAdapter(Context c)
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
            RatingBar r1;
            MyViewHolder(View view)
            {
                ///Instantiate views here....
                //tv1 = (TextView) view.findViewById(R.id.header);
//                tv2 = (TextView) view.findViewById(R.id.restaurant);
//                tv3 = (TextView) view.findViewById(R.id.type);
//                tv4 = (TextView) view.findViewById(R.id.created);
//                tv5 = (TextView) view.findViewById(R.id.description);
//                tv6 = (TextView) view.findViewById(R.id.cuisine);
//                r1 = (RatingBar) view.findViewById(R.id.rating);
//                r1.isInEditMode();
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
//            holder.tv2.setText(temp.restaurant);
//            holder.tv3.setText(temp.type);
//            holder.tv4.setText(temp.created);
//            holder.tv5.setText(temp.description);
//            holder.tv6.setText(temp.cuisine);
//            holder.r1.setRating(Integer.parseInt(temp.rating));
            return row;
        }
    }


}
