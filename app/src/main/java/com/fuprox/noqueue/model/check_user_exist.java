package com.fuprox.noqueue.model;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fuprox.noqueue.utils.Dbhelper;
import com.fuprox.noqueue.utils.booking_details;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class check_user_exist extends AsyncTask<String, String, String> {

    Context context;
    String user_id,error_serv= " ";
    boolean exists;
    public check_user_exist(Context context,String user_id){
        this.context = context;
        this.user_id = user_id;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... strings) {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        JSONObject json = new JSONObject();
        StringBuilder sb;
        InputStream is = null;
        String result = null;
        try {
            HttpPost post = new HttpPost(new strings_().get_ipaddress(context)+"/user/exists");
            json.put("user_id", user_id);
            StringEntity se = new StringEntity( json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            /*Checking response */
            if(response!=null){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    sb = new StringBuilder();
                    sb.append(reader.readLine()).append("\n");
                    String line = "0";

                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }

                    is.close();
                    result = sb.toString();
//                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
                    JSONObject obj = null;
                    JSONArray jsonarray;
                    booking_details bookingDetails=new booking_details();
                    try {
                        obj= new JSONObject(result);
                        exists = obj.getBoolean("exists");
//                            infront = 1;
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: services offered \"" + result + "\""+t.getMessage());
                        error_serv="There was a problem communicating with the servers. \n\n Try again later.";
                    }
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                    error_serv="There was a problem communicating with the servers. \n\n Try again later.";
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            Log.e("TAG", "run: check User exist connection error"+e.toString() );
            error_serv="There was a problem communicating with the servers. \n\n Try again later.";
        }
        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (error_serv.equals(" ") ){
            if (!exists){
                new Dbhelper(context).delete_user();
            }
        }
    }
}
