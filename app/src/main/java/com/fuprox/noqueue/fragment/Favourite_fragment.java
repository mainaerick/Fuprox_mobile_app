package com.fuprox.noqueue.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.fuprox.noqueue.MainActivity;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.activities.activity_booking;
import com.fuprox.noqueue.adapters.favourite_adapter;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.Dbhelper;
import com.fuprox.noqueue.utils.booking_details;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import java.nio.charset.StandardCharsets;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Favourite_fragment extends Fragment {
    Activity activity;
    ProgressDialog pDialog;
    public BottomNavigationView navView;

    public Favourite_fragment(Activity _activity){
        activity=_activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        ImageView backbtn;
        if (new Dbhelper(getActivity()).get_fav().isEmpty()){
            view=inflater.inflate(R.layout.layout_favourite_empty, container, false);
            backbtn = view.findViewById(R.id.backbtn);
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navView= activity.findViewById(R.id.nav_view);
                    navView.setSelectedItemId(R.id.navigation_queue);
                }
            });
        }
        else {
            view=inflater.inflate(R.layout.favourite_layout, container, false);

            ListView listView=view.findViewById(R.id.listview_fav);
            favourite_adapter favourite_adapter = new favourite_adapter(getActivity(),listView,R.layout.list_favourite,new Dbhelper(getActivity()).get_fav());
            listView.setAdapter(favourite_adapter);


            backbtn = view.findViewById(R.id.backbtn);

            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navView= activity.findViewById(R.id.nav_view);
                    navView.setSelectedItemId(R.id.navigation_queue);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(activity, ""+favourite_adapter.getItem(position).getId(), Toast.LENGTH_SHORT).show();
                    TextView fav_b_id=view.findViewById(R.id.tvfav_id);
                    new branch_get(fav_b_id.getText().toString()).execute();

//                    new branch_get(favourite_adapter.getItem(position).getBranchid()).execute();

                }
            });
        }

        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left));

        return view;
    }

    private class branch_get extends AsyncTask<String, String, String> {

        String branch_id;

        String company_name,
        company_id,
        branch_name,
        branch_close,
        branch_open,ismedical,latitude,longitude;;

        public branch_get(String branch_id){
            this.branch_id=branch_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Getting details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/branch/get/single");
                json.put("branch_id", branch_id);
                StringEntity se = new StringEntity( json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                if(response!=null){
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                is, StandardCharsets.ISO_8859_1), 8);
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
                        try {
                            obj= new JSONObject(result);
                            company_id=obj.getString("company");
                            branch_id= String.valueOf(obj.getInt("id"));
                            branch_name=obj.getString("name");
                            branch_close=obj.getString("closes");
                            branch_open=obj.getString("opens");
                            boolean isMedical=obj.getBoolean("is_medical");
                            String longi=obj.getString("longitude");
                            String latti=obj.getString("latitude");

                            this.latitude = latti;
                            this.longitude = longi;

                            if (isMedical){
                                ismedical="1";
                            }
                            else {
                                ismedical="0";
                            }

                            Log.d(TAG, "run: getting branch id "+ obj);

                        } catch (Throwable t) {
                            Log.d(TAG, "run: getting branch id company "+ result);

                            Log.e("My App", "get branches for booking  " +t.getMessage());

//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                        }


//                            Log.d(TAG, "run: string" + result);

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }


                }

            } catch(Exception e) {
                e.printStackTrace();
                //                    createDialog("Error", "Cannot Estabilish Connection");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            new getcompany_name(company_id,branch_id,branch_name,branch_close,branch_open,"book",ismedical,latitude,longitude).execute();
        }
    }
    private class getcompany_name extends AsyncTask<String, String, String>{
        String company_id;
        String branch_id;
        String branch_name;
        String branch_close;
        String branch_open;
        String company_name=" ";
        String action;
        String gcnamerroe=" ";
        String ismedical;
        String longitude;
        String latitude;

        public getcompany_name(String company_id,String branch_id,String branch_name,String branch_close,String branch_open,String action,String ismedical,String lat,String longitude){
            this.ismedical=ismedical;
            this.company_id=company_id;
            this.branch_id=branch_id;
            this.branch_name=branch_name;
            this.branch_close=branch_close;
            this.branch_open=branch_open;
            this.action=action;
            this.latitude = lat;
            this.longitude = longitude;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action.equals("book")){
                pDialog = new ProgressDialog(activity, AlertDialog.THEME_HOLO_LIGHT);
                pDialog.setMessage("Getting Services. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/company/by/id");
                json.put("id", company_id);

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
                                is, StandardCharsets.ISO_8859_1), 8);
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
                            if (obj.length()==0){
                                gcnamerroe="empty company";
                            }
//                              jsonarray = new JSONArray(result);
                            company_name=obj.getString("name");


                            Log.d(TAG, "company details by id"+obj);

                        } catch (Throwable t) {

                            Log.e("My App", "Could not parse malformed JSON: getcomany name \"" + obj + "\""+t.getMessage());
                            Log.d(TAG, "doInBackground: company-d   "+company_id );
                            gcnamerroe="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";

                        }

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        gcnamerroe="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
                Log.d(TAG, "run: company get connection error" );
                gcnamerroe="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (gcnamerroe.equals(" ")) {
                if (action.equals("book")) {
//                    bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment(activity, branch_id,
//                            company_name,
//                            "",
//                            branch_name,
//                            branch_close,
//                            branch_open, ismedical
//                    );
//                    bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());

                    Intent intent1 = new Intent(activity, activity_booking.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("branch_id", branch_id);
                    bundle.putString("opens", branch_open);
                    bundle.putString("closes", branch_close);
                    bundle.putString("branch_name", branch_name);
                    bundle.putString("company", company_name);
                    bundle.putString("ismedical", ismedical);
                    bundle.putString("longitude", longitude);
                    bundle.putString("latitude", latitude);

                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
            }
//            pDialog.dismiss();

        }
    }

}
