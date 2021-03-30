package com.fuprox.noqueue.model;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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



public class Checkinfront extends AsyncTask<String, String, String> {
    public  static String TAG="";

    View view;
    String number;
    String msg="";
    String error_serv=" ";
    public Checkinfront(View view,String number){
        this.view=view;
        this.number=number;
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
            HttpPost post = new HttpPost(new strings_().desktopurl()+"/service/pay");
            json.put("phone", number);
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

                        Log.d(TAG, "doInBackground: msg="+obj.getString("msg"));
                        msg=obj.getString("msg");
//                            jsonarray = new JSONArray(result);

//                            String name = null;
//                            servicesOfferedDetailsArrayList = new ArrayList<>();
//                            list_sername=new ArrayList<String>();
//                            int i=0;
//                            while (i<jsonarray.length()) {
//                                services_offered_details services_offered_details = new services_offered_details();
//                                obj = new JSONObject(jsonarray.getString(i));
//                                services_offered_details.setService_code(obj.getString("code"));
//                                services_offered_details.setService_id(obj.getString("id"));
//                                services_offered_details.setService_name(obj.getString("name"));
//                                services_offered_details.setService_teller(obj.getString("teller"));
//                                servicesOfferedDetailsArrayList.add(services_offered_details);
//                                list_sername.add(obj.getString("name"));
//                                i++;
//                            }
//                            if (list_sername.size()==0){
//                                no_items="No Services Available";
//                            }
//                            else{
//                                no_items="Select a service";
//                            }

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
            Log.d(TAG, "run: book connection error" );
            error_serv="There was a problem communicating with the servers. \n\n Try again later.";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }
}
