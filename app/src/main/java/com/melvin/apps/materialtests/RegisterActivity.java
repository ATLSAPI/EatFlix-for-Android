package com.melvin.apps.materialtests;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private ImageView imageView;
    private TextView first_name, last_name, email, password, confirm_password;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int CAMERA_REQUEST = 0;
    private String imagePath, fileName;
    private ProgressDialog progress, progress2;
    private String path;
    private Bitmap bitmap;
    private Password objPassword;
    private SharedPreferences sharedPreferences;


    public static String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#A40300")));
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        //Initialise views
        imageView = (ImageView) findViewById(R.id.profile_image);
        progress = new ProgressDialog(this);
        first_name = (TextView) findViewById(R.id.first_name);
        last_name = (TextView) findViewById(R.id.last_name_text);
        email = (TextView) findViewById(R.id.email_edit_text);
        password = (TextView) findViewById(R.id.password);
        confirm_password = (TextView) findViewById(R.id.confirm_password);
        objPassword = new Password();
        sharedPreferences = getSharedPreferences("Auth",MODE_PRIVATE);
        progress2 = new ProgressDialog(this);
        imagePath = "";
        //validate next

//        RegisterActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
//                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
//        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//        params.verticalMargin = 140;
//        params.horizontalMargin = 10;
//        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
        else if(id == R.id.attach_image) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        else if(id == R.id.take_photo)
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
        else if(id == R.id.submit_register)
        {
            boolean valid = true;
            final String email_text = email.getText().toString();
            if (!isValidEmail(email_text)) {
                email.setError("Invalid Email");
                valid = false;
            }
            final String password_text = password.getText().toString();
            final String confirm_password_text = confirm_password.getText().toString();
            if (!password_text.equals(confirm_password_text)) {
                confirm_password.setError("Passwords do not match");
                valid = false;
            }
            else if (objPassword.calculateStrength(password_text) < 3) {
                valid = false;
                password.setError("Invalid Password. Minimum 6 characters and at least one number and one upper case or special chars");
            }
            final String first_name_text = first_name.getText().toString();
            final String last_name_text = last_name.getText().toString();
            if (first_name_text.trim().isEmpty()) {
                first_name.setError("Enter first name");
                valid = false;
            }
            if (last_name_text.trim().isEmpty()) {
                last_name.setError("Enter last name");
                valid = false;
            }
            if (imagePath.trim().isEmpty())
            {
                valid = false;
                new AlertDialog.Builder(this)
                        .setTitle("Profile Image")
                        .setMessage("Please select an image")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                .show();
            }
            if (valid == true)
            {
                progress2.setTitle("Register");
                progress2.setMessage("Registering your details");
                progress2.show();
                new RegisterData().execute();
            }
            else {
                //Toast.makeText(RegisterActivity.this, "Valid" + valid, Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(RegisterActivity.this, "Valid"+valid, Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }
    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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

                    ImageView img = (ImageView) findViewById(R.id.profile_image);
                    img.setImageBitmap(decodedByte);
                    // get the base 64 string
                    progress.dismiss();
                    //Toast.makeText(NewRestaurant.this, "Length is " + length + "KB", Toast.LENGTH_LONG).show();
                }
                else
                {
                    progress.dismiss();
                    Toast.makeText(RegisterActivity.this, "File is too big. Max 200KB", Toast.LENGTH_LONG).show();
                }
                cursor.close();
            }
            catch (Exception e)
            {
                Toast.makeText(RegisterActivity.this, "Failed!! Select another image"+e.getMessage(), Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
            ///
        }
        else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.show();
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);

                progress.dismiss();
                Uri tempUri = getImageUri(getApplicationContext(), photo);
                File file = new File(getRealPathFromURI(tempUri));
                imagePath = getRealPathFromURI(tempUri);
                Toast.makeText(RegisterActivity.this, getRealPathFromURI(tempUri)+"", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    private class RegisterData extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... strings) {
            MultipartEntity multipartEntity = new MultipartEntity();
            String first_name_text = first_name.getText().toString();
            String email_text = email.getText().toString();
            String last_name_text = last_name.getText().toString();
            String password_text = password.getText().toString();
            String url = "http://timothysnw.co.uk/v1/users";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            try {
                multipartEntity.addPart("first_name", new StringBody(first_name_text));
                multipartEntity.addPart("last_name",new StringBody(last_name_text));
                multipartEntity.addPart("email",new StringBody(email_text));
                multipartEntity.addPart("password",new StringBody(password_text));
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
            int status = 0;
            //if (response == null) throw new AssertionError();
            status = response.getStatusLine().getStatusCode();

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
                    data = status+"";
                    //fail
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("OK"))
            {
                progress2.dismiss();
                finish();
                //Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                //NavUtils.navigateUpFromSameTask(RegisterActivity.this);
                //Success

                //startActivity(intent);
                Toast.makeText(RegisterActivity.this, "Registration successful, now log in", Toast.LENGTH_SHORT).show();
            }
            else {
                progress2.dismiss();
                Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                ///Failed
            }
        }
    }
}
