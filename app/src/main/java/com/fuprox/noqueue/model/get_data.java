package com.fuprox.noqueue.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ALL")
class get_data {
    private String ip;
    Activity activity;
    public get_data(){
        new LoadAllProducts().execute();
    }
    String putdep;
    String putcourse;
    private String id;
    String title;


    static ProgressDialog pDialog;
    // products JSONArray
    private static JSONArray products = null;
    private static ArrayList<HashMap<String, String>> productsList;

    // Progress Dialog

    // Creating JSON Parser object
    private final JSONParser jParser = new JSONParser();



    // url to get all products list

    final String url_all_products = ip;

    // JSON Node names
    final String TAG_SUCCESS = "success";
    private final String TAG_PRODUCTS = "branches";
    private final String TAG_PID = "id";
    private final String TAG_company = "company";
    private final String Tag_latitude="latitude";
    private final String Tag_longitude="longitude";
    private final String Tag_branch_name="name";
    private final String Tag_address="address";

    private double longi=0;
    private double lat=0;
    private String straddress;
    private String strcompany_name;
    private String strbranch_name;
    private int list_size;



    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Loading lessons. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();


        }

        /**
         * getting All products from url
         * */

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
            post_params.add(new BasicNameValuePair("id", id));
//            post_params.add(new BasicNameValuePair("title",title));


            // getting JSON Object
            // Note that create product url accepts POST method
//            JSONObject json_post_course = jParser.makeHttpRequest(url_all_products,
//                    "POST", post_params);

            productsList = new ArrayList<HashMap<String, String>>();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest("http://192.168.12.63:4000/branch/get", "GET", params);
            JSONObject resultObject;
            JSONArray jsonArray = null;
            try {
                resultObject = new JSONObject(json.toString());
                jsonArray = resultObject.optJSONArray("branches");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonArray!=null){
                // Check your log cat for JSON reponse


                try {
                    // Checking for SUCCESS TAG
//                    int success = json.getInt(TAG_SUCCESS);

//                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        products = json.getJSONArray(TAG_PRODUCTS);
                    Log.d("All Products: ", json.toString());

                        // looping through All Products
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString(TAG_PID);
                            String name = c.getString(TAG_company);
                            String dblongitude=c.getString(Tag_longitude);
                            String dblatitude=c.getString(Tag_latitude);
                            String dbaddress=c.getString(Tag_address);
                            String dbbranch=c.getString(Tag_branch_name);

//                            Log.d("All Products: ", name);
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_PID, id);
                            map.put(TAG_company, name);
                            map.put(Tag_longitude,dblongitude);
                            map.put(Tag_latitude,dblatitude);
                            map.put(Tag_address,dbaddress);
                            map.put(Tag_branch_name,dbbranch);
                            // adding HashList to ArrayList
                            productsList.add(map);
                            list_size=productsList.size();



                            //to pass data
                            lat= Double.parseDouble(productsList.get(i).get(Tag_latitude));
                            longi= Double.parseDouble(productsList.get(i).get(Tag_longitude));
                            strcompany_name= productsList.get(i).get(TAG_company);
                            straddress=productsList.get(i).get(Tag_address);
                            strbranch_name=productsList.get(i).get(Tag_branch_name);

//                            Log.d("data size", "doInBackground: "+lat+"----"+longi);
//                            Toast.makeText(activity_order_details, productsList.get(1).get(Tag_longitude), Toast.LENGTH_SHORT).show();
                        }
//                    }
//                    else {
//                        // no products found
//
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }






//
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        int i;
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

//            pDialog.dismiss();
            // updating UI from Background Thread

//            new Thread() {
//                public void run() {
//                    while (i++ < 1000) {
//                        try {
//                            getActivity().runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//
//                                }
//                            });
//                            Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }.start();
//            activity_order_details.runOnUiThread(new Runnable() {
//                public void run() {
//
//
//
//                }});



//            final Handler handler = new Handler();
//            Runnable refresh = new Runnable() {
//                @Override
//                public void run() {
//
//                    listView.invalidateViews();
//                    handler.postDelayed(this, 60 * 1000);
//                }
//            };
//            handler.postDelayed(refresh, 1000);
        }



    }
    public double pass_longi(){

        return longi;
    }
    public double pass_lat(){
        Log.d("get_data", "pass_lat: " +lat);
        return lat;
    }
    public String branch_name(){

        return strbranch_name;
    }
    public String company_name(){

        return strcompany_name;
    }
    public String address_detail(){

        return straddress;
    }
    public int getProductsList() {


        return list_size;
    }


}
