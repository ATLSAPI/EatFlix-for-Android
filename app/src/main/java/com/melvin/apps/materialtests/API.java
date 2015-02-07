//package com.melvin.apps.materialtests;
//
//import android.os.AsyncTask;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpDelete;
//import org.apache.http.client.methods.HttpHead;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpPut;
//import org.apache.http.entity.ByteArrayEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.client.HttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.client.methods.HttpGet;
//import android.os.AsyncTask;
//import android.widget.ListView;
//
//
//import org.apache.http.HttpEntity;
//import org.apache.http.protocol.BasicHttpContext;
//import java.io.IOException;
//import java.io.InputStream;
//import org.apache.http.HttpResponse;
//import org.apache.http.util.EntityUtils;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
///**
// * Created by Melvin on 2/6/2015.
// */
//public class API {
//
//
//    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
//        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
//            InputStream in = entity.getContent();
//
//
//            StringBuffer out = new StringBuffer();
//            int n = 1;
//            while (n > 0) {
//                byte[] b = new byte[4096];
//                n = in.read(b);
//
//
//                if (n > 0) out.append(new String(b, 0, n));
//            }
//
//
//            return out.toString();
//        }
//
//
//        @Override
//        protected String doInBackground(Void... params) {
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpContext localContext = new BasicHttpContext();
//            HttpGet httpGet = new HttpGet("http://sjp284.edu.csesalford.com/odunmama/restaurants");
//
//
//            String text = null;
//            try {
//                HttpResponse response = httpClient.execute(httpGet, localContext);
//
//
//                HttpEntity entity = response.getEntity();
//
//
//                text = getASCIIContentFromEntity(entity);
//
//
//            } catch (Exception e) {
//                return e.getLocalizedMessage();
//            }
//
//
//            return text;
//        }
//
//
//        protected void onPostExecute(String results) {
//            if (results != null) {
//
//
//                listview = (ListView) rootView.findViewById(R.id.listview);
//                JSONParser parse = new JSONParser();
//
//
//                String[] values = new String[]{"Android", "" + parse.jsonparsetostring(results)};
//                adapter = new ArrayAdapter<String>(getActivity(),
//                        android.R.layout.simple_list_item_1, values);
//                listview.setAdapter(adapter);
//
//
//                listview.setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view,
//                                            int position, long id) {
//
//
//                        // PostCalls sendData = new PostCalls();
//                        // String jsontoSend = "{\"title\":\"Madhuban Restaurant\",\"description\":\"Serve excellent fish cuuries\",\"city\":\"bolton\",\"postcode\":\"bl14dn\",\"additional_info\":\"Welcome Hotel Rama International, Opposite High Court, CIDCO, Aurangabad\"}";
//                        String url = "http://sjp284.edu.csesalford.com/odunmama/restaurants";
//                        new PostCalls().execute(url);
//                        //String  pos =  sendData.examplePost(jsontoSend);
//
//
//                      /*  Intent i = new Intent(getActivity(), LoginActivity.class);
//                        startActivity(i);*/
//                    }
//
//
//                });
//            }
//
//
//        }
//
//
//    }
//
//
//    /* Posting Data  to odunmama api**/
//    public class PostCalls extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... url) {
//            HttpClient httpClient = new DefaultHttpClient();
//
//
//            try {
//                HttpPost httppost = new HttpPost(url[0]);
//                // serialization of data into json
//              /*  Gson gson = new GsonBuilder().serializeNulls().create();
//                String json = gson.toJson(data);
//                httppost.addHeader("content-type", "application/json");
//
//
//                // creating the entity to send
//                ByteArrayEntity toSend = new ByteArrayEntity(json.getBytes());
//                httppost.setEntity(toSend);*/
//                List<NameValuePair> name_value = new ArrayList<NameValuePair>();
//                name_value.add(new BasicNameValuePair("title", "eba re"));
//                name_value.add(new BasicNameValuePair("description", "Hi"));
//                name_value.add(new BasicNameValuePair("city", "Liverpool"));
//                name_value.add(new BasicNameValuePair("postcode", "bl7get"));
//                name_value.add(new BasicNameValuePair("additional_info", "Hi"));
//                httppost.setEntity(new UrlEncodedFormEntity(name_value));
//                HttpResponse response = httpClient.execute(httppost);
//
//
//                String status = "" + response.getStatusLine();
//                return "" + status;
//           /* HttpEntity entity = response.getEntity();
//
//
//            InputStream input = entity.getContent();
//            StringWriter writer = new StringWriter();
//            IOUtils.copy(input, writer, "UTF8");
//            String content = writer.toString();
//            // do something useful with the content
//            System.out.println(content);
//            writer.close();
//            EntityUtils.consume(entity);*/
//            } catch (Exception e) {
//                return e.toString();
//            } finally {
//                httpClient.getConnectionManager().shutdown();
//            }
//        }
//
//
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                Toast message = Toast.makeText(getActivity(), result, Toast.LENGTH_LONG);
//                //Toast.makeText(getActivity(), pos, Toast.LENGTH_LONG );
//                message.show();
//            }
//        }
//
//
//    }
//
//
//
//    /*Put in odunmama api*/
//
//
//    public class PutCalls extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... url) {
//            HttpClient httpClient = new DefaultHttpClient();
//
//
//            try {
//                HttpPut httpPut = new HttpPut(url[0]);
//                List<NameValuePair> name_value = new ArrayList<NameValuePair>();
//                name_value.add(new BasicNameValuePair("title", "eba re"));
//                name_value.add(new BasicNameValuePair("description", "Hi"));
//                name_value.add(new BasicNameValuePair("city", "Liverpool"));
//                name_value.add(new BasicNameValuePair("postcode", "bl7get"));
//                name_value.add(new BasicNameValuePair("additional_info", "Hi"));
//                httpPut.setEntity(new UrlEncodedFormEntity(name_value));
//                HttpResponse response = httpClient.execute(httpPut);
//                String status = "" + response.getStatusLine();
//                return "" + status;
//            } catch (Exception e) {
//                return e.toString();
//            } finally {
//                httpClient.getConnectionManager().shutdown();
//            }
//        }
//
//
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                Toast message = Toast.makeText(getActivity(), result, Toast.LENGTH_LONG);
//                message.show();
//            }
//        }
//
//
//    }
//
//
//
//  /*Put in odunmama api*/
//
//
//    public class DeleteCalls extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... url) {
//            HttpClient httpClient = new DefaultHttpClient();
//
//
//            try {
//                HttpDelete httpDelete = new HttpDelete(url[0]);
//                HttpResponse response = httpClient.execute(httpDelete);
//                String status = "" + response.getStatusLine();
//                return "" + status;
//            } catch (Exception e) {
//                return e.toString();
//            } finally {
//                httpClient.getConnectionManager().shutdown();
//            }
//        }
//
//
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                Toast message = Toast.makeText(getActivity(), result, Toast.LENGTH_LONG);
//                message.show();
//            }
//        }
//
//
//    }
//}
