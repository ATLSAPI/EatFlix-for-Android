package com.melvin.apps.materialtests;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewRestaurant extends ActionBarActivity {
    private Toolbar mToolbar;
    String auth;
    Intent intent;
    String imagePath, fileName;
    public static final String TAG = NewRestaurant.class.getSimpleName();
    private static int RESULT_LOAD_IMAGE = 1;
    ProgressDialog progress, progress2;
    String path;
    Bitmap bitmap;
    private SharedPreferences sharedPreferences;


    public static String image = "";
    EditText et1,et2,et3,et4,et5,et6,et7;
    Spinner sp1, sp2;

    //AlertDialog initialise


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        b1 = (Button) findViewById(R.id.image_btn);
//        b2 = (Button) findViewById(R.id.sub_btn);
        progress = new ProgressDialog(this);
        progress2 = new ProgressDialog(this);
        et1 = (EditText) findViewById(R.id.name_edit_text);
        //et2 = (EditText) findViewById(R.id.email_edit_text);
        //et3 = (EditText) findViewById(R.id.telephone_edit_text);
        et4 = (EditText) findViewById(R.id.postcode_edit_text);
        et5 = (EditText) findViewById(R.id.address_edit_text);
        et6 = (EditText) findViewById(R.id.description);
        et7 = (EditText) findViewById(R.id.town);
        sp1 = (Spinner) findViewById(R.id.type_spinner);
        sp2 = (Spinner) findViewById(R.id.type_cuisine);
        sharedPreferences = getSharedPreferences("Auth", MODE_PRIVATE);
        imagePath = "";


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_restaurant, menu);
        return true;
    }

    public String PostData(String username, String password) {
        InputStream inputStream;
        byte[] bytes;
        MultipartEntity multipartEntity = new MultipartEntity();

        String name = et1.getText().toString();
//        String email = et2.getText().toString();
//        String telephone = et3.getText().toString();
        String postcode = et4.getText().toString();
        String address = et5.getText().toString();
        String description = et6.getText().toString();
        String town = et7.getText().toString();
        String imagey = image;
//        String cuisine_id = "1";
//        String type_id ="1";
        String cuisine_id = sp2.getSelectedItemPosition()+"";
        String type_id = sp1.getSelectedItemPosition()+"";
        String token = sharedPreferences.getString("token", "");
        String url = "http://timothysnw.co.uk/v1/restaurants";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Token", token);
        try {
            multipartEntity.addPart("name", new StringBody(name));
            multipartEntity.addPart("description",new StringBody(description));
            multipartEntity.addPart("address",new StringBody(address));
            multipartEntity.addPart("postcode",new StringBody( postcode));
            multipartEntity.addPart("town",new StringBody(town));
            multipartEntity.addPart("cuisine_id",new StringBody(cuisine_id));
            multipartEntity.addPart("type_id",new StringBody(type_id));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            multipartEntity.addPart("file", new FileBody(new File(imagePath)));
            httpPost.setEntity(multipartEntity);



        HttpResponse response = null;
        try {

            response = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = null;
        int status = response.getStatusLine().getStatusCode();

        if (status == 201) {
            HttpEntity entity = response.getEntity();
            try {
                data = EntityUtils.toString(entity);
                data = "OK";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            HttpEntity entity = response.getEntity();
            try {
                data = EntityUtils.toString(entity);
                //data = status+"";
                //fail
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
    private class PostAsync extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            auth = PostData("hhjh","jhhjhj");
            return "";

        }

        @Override
        protected void onPostExecute(String s) {
            progress2.dismiss();
            if (auth == "OK")
            {
                Toast.makeText(NewRestaurant.this, "Restaurant added!", Toast.LENGTH_LONG).show();
                intent = new Intent(NewRestaurant.this, RestaurantActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(NewRestaurant.this, s, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_restaurant) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        else if(id == R.id.submit_restaurant) {


            //Validate()
            /*
            et1 = (EditText) findViewById(R.id.name_edit_text);
        et2 = (EditText) findViewById(R.id.email_edit_text);
        et3 = (EditText) findViewById(R.id.telephone_edit_text);
        et4 = (EditText) findViewById(R.id.postcode_edit_text);
        et5 = (EditText) findViewById(R.id.address_edit_text);
        et6 = (EditText) findViewById(R.id.description);
        et7 = (EditText) findViewById(R.id.town);
        sp1 = (Spinner) findViewById(R.id.type_spinner);
        sp2 = (Spinner) findViewById(R.id.type_cuisine);
             */

            boolean valid = true;

            final String name_edit_text = et1.getText().toString();
            if (name_edit_text.trim().isEmpty()) {

                et1.setError("Enter restaurant name");
                valid = false;
            }
            String regex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(et4.getText().toString().trim());
            if (!matcher.matches()) {
                et4.setError("Postcode not valid");
                valid = false;
            }
            final String address_edit_text = et5.getText().toString();
            if (name_edit_text.trim().isEmpty()) {
                et5.setError("Enter address");
                valid = false;
            }
            final String description = et6.getText().toString();
            if (name_edit_text.trim().isEmpty()) {
                et6.setError("Enter description");
                valid = false;
            }
            final String town = et7.getText().toString();
            if (name_edit_text.trim().isEmpty()) {
                et7.setError("Enter town");
                valid = false;
            }
            final int type_id = sp1.getSelectedItemPosition();
            if (type_id<1) {
                Toast.makeText(NewRestaurant.this,"Select a restaurant type", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            final int cuisine_id = sp2.getSelectedItemPosition();
            if (cuisine_id<1) {
                if (valid) {
                    Toast.makeText(NewRestaurant.this,"Select cuisine type", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
            }
            if (imagePath.trim().isEmpty())
            {
                valid = false;
                new AlertDialog.Builder(this)
                        .setTitle("Restaurant Image")
                        .setMessage("Please select an image")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }


            //Toast.makeText(NewRestaurant.this, "File is too big. Max 200KB", Toast.LENGTH_LONG).show();
            if (valid) {
                progress2.setTitle("Loading");
                progress2.setMessage("Wait while loading...");
                progress2.show();
                try {
                    new PostAsync().execute("Post");
                } catch (Exception e) {
                    Toast.makeText(NewRestaurant.this, "Error!!!"+e.toString(), Toast.LENGTH_LONG).show();
                }
            }

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
                fileName = selectedImage.getLastPathSegment();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.show();

                //path = picturePath;
                 //new LoadImg().execute(picturePath);
                imagePath = picturePath;
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
                    bitmap = bm;
                    String imgString = Base64.encodeToString(getBytesFromBitmap(bm), Base64.NO_WRAP);
                    image = imgString;
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
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }


}
