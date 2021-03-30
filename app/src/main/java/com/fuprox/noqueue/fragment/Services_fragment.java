package com.fuprox.noqueue.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fuprox.noqueue.MainActivity;
import com.fuprox.noqueue.activities.LatLong;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.adapters.services_adapter;
import com.fuprox.noqueue.adapters.suggestion_adapter;
import com.fuprox.noqueue.model.JSONParser;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.service_details;
import com.fuprox.noqueue.utils.suggestion_details;
import com.github.ybq.android.spinkit.SpinKitView;
import com.mancj.materialsearchbar.MaterialSearchBar;

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
import java.util.ArrayList;
import java.util.List;


@SuppressLint("ValidFragment")
public class Services_fragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    public static final String KEY_FRIDAY_FRAGMENT = "Friday";
    public static String error = " ";
    final JSONParser jParser = new JSONParser();
    static ProgressDialog pDialog;
    ArrayList<service_details> servicelist= new ArrayList<>();
    private MaterialSearchBar searchBar;
    Activity activity;
    suggestion_adapter suggestion_adapter;
    static List<suggestion_details> post_params = new ArrayList<>();
    public  static String TAG="";
    LinearLayout error_linearlayout;
    ListView listView;
    LatLong my_latlong;
    View tolocation_view;
    SpinKitView loadingDots;
    LinearLayout layout_loading;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean mLocationPermissionGranted;
    boolean list_status_nolist;
    String gotenpostparams=" ";

    public Services_fragment(Activity activity){
        this.activity=activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.onbackpress="exit";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mainview = inflater.inflate(R.layout.layout_services, container, false);
        tolocation_view=inflater.inflate(R.layout.layout_services, container, false);
        loadingDots = tolocation_view.findViewById(R.id.spin_kit);
        layout_loading = tolocation_view.findViewById(R.id.layout_loading);

//        getdevicelocation();
        if (servicelist.isEmpty()){
            new services_get(tolocation_view).execute();
        }
        else {
            setupAdapter(tolocation_view);
        }
        return tolocation_view;
    }

    private void setupAdapter(View view) {
        if (activity==null){
            activity=getActivity();
        }

        listView = view.findViewById(R.id.service_listview);
        error_linearlayout=view.findViewById(R.id.layout_error_disp);

//        adapter = new orderadapter(getActivity(), listView, R.layout.listorders, new Dbhelper(getContext()).get_booking());

        services_adapter adapter = new services_adapter(activity, listView, R.layout.list_services, servicelist);
        listView.setAdapter(adapter);

        if (!list_status_nolist) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = view.findViewById(R.id.service_title);
                    TextView textViewid = view.findViewById(R.id.serviceid);
                    loadFragment(new Institution_fragment(activity,textViewid.getText().toString()));
                }
            });
        }
        else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = view.findViewById(R.id.service_title);
                    TextView textViewid = view.findViewById(R.id.serviceid);
                    loadFragment(new Services_fragment(activity));
                }
            });
        }
    }
    @Override
    public void onSearchStateChanged(boolean enabled) {
    }
    @Override
    public void onSearchConfirmed(CharSequence text) {
    }
    @Override
    public void onButtonClicked(int buttonCode) {
    }
    private class services_get extends AsyncTask<String, String, String> {
        View view;
        String error_title="";
        public services_get(View view) {
            this.view = view;
        }
        @Override
        protected void onPreExecute() {
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Services loading. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
            layout_loading.setVisibility(View.VISIBLE);
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity) + "/service/get");
//                json.put("branch", "");

                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                /*Checking response */
                if (response != null) {

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
                        try {
//                            obj= new JSONObject(result);

                            jsonarray = new JSONArray(result);


                            String name = null;
                            int i = 0;
                            while (i < jsonarray.length()) {


                                service_details service_details = new service_details();
                                obj = new JSONObject(jsonarray.getString(i));

                                service_details.setTitle(obj.getString("name"));
                                service_details.setId(obj.getString("id"));
                                Log.d(TAG, "get all services " + obj.getString("name"));


                                servicelist.add(service_details);

                                i++;
                            }
                            if (servicelist.size()==0) {
                                error="No Services added yet!";
                                error_title="No services!";
//                                network_error(view, error_title,"Try again later and find services provided!");
                            }
                            else {

                            }
                            Log.d(TAG, "get all services " + jsonarray);


                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: services\"" + obj + "\"" + t.getMessage());
                            error = "Oops.. \n There was a problem communicating with the servers.";
                            error_title="Server error";
//                            network_error(view,"Server error",error);
                        }
//                            Log.d(TAG, "run: string" + result+new Dbhelper(activity).get_user_id());
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        error = "Oops.. \n There was a problem communicating with the servers.";
                        error_title="Server error";
//                        network_error(view,error_title,error);
//                            pDialog.dismiss();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
//                    Toast.makeText(getActivity(), "Problem Establishing Connection", Toast.LENGTH_SHORT).show();
//                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("There was a problem communicating with the servers.")
//                            .setContentText("Try again later.")
//                            .show();
                Log.d(TAG, "run: services get");
                error = "Oops.. \nThere was a problem establishing internet connection.";
                error_title="Internet connection error!";

//                network_error(view,error_title,error);
//                    pDialog.cancel();
//                    createDialog("Error", "Cannot Estabilish Connection");
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            layout_loading.setVisibility(View.GONE);
//            pDialog.dismiss();
            setupAdapter(view);
            if (error_title.isEmpty()){
                listView.setVisibility(View.VISIBLE);
                error_linearlayout.setVisibility(View.GONE);
            }
            else {


                network_error(view,error_title,error);
            }

            Log.d(TAG, "onPostExecute: services  ");
        }
    }

    public void network_error(View view,String error,String error_description){
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {error};
//        List<String> list = Arrays.asList(strings_);
//        ArrayList<branches_details> servicelist = new ArrayList<>();
        error_linearlayout.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        TextView error_title=view.findViewById(R.id.error_title);
        TextView error_desc=view.findViewById(R.id.error_description);
        error_title.setText(error);
        error_desc.setText(error_description);

        Button error_retry=view.findViewById(R.id.retry_btn);

        error_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_linearlayout.setVisibility(View.INVISIBLE);
                new services_get(view).execute();
            }
        });

//        String name = null;
//        servicelist = new ArrayList<>();
//
//        for (int i = 0; i < strings_.length; i++) {
//            service_details service_details = new service_details();
//            service_details.setTitle(strings_[i]);
//            servicelist.add(service_details);
//        }
        list_status_nolist=true;

    }
//
//    private static void sendJson_getbranch_details(final String searchtext) {
//        final Thread t = new Thread() {
//
//            public void run() {
//                Looper.prepare(); //For Preparing Message Pool for the child Thread
//                HttpClient client = new DefaultHttpClient();
//                HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
//                HttpResponse response;
//                JSONObject json = new JSONObject();
//                StringBuilder sb;
//                InputStream is = null;
//                String result = null;
//
//                try {
//                    HttpPost post = new HttpPost(new strings_().get_ipaddress() + "/app/search/");
//                    json.put("term", " ");
//                    StringEntity se = new StringEntity(json.toString());
//                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                    post.setEntity(se);
//                    response = client.execute(post);
//                    HttpEntity entity = response.getEntity();
//                    is = entity.getContent();
//
//
//
//                    /*Checking response */
//                    if (response != null) {
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
////                        decode response to string
//
//                        try {
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                    is, "iso-8859-1"), 8);
//                            sb = new StringBuilder();
//                            sb.append(reader.readLine()).append("\n");
//                            String line = "0";
//
//                            while ((line = reader.readLine()) != null) {
//                                sb.append(line).append("\n");
//                            }
//
//                            is.close();
//                            result = sb.toString();
//
//
////                            CONVERT THE RESPONSE RESULT TO JSON FROM STRRING
//
//                            JSONObject obj = null;
//                            JSONArray jsonarray;
//                            try {
////                                obj= new JSONObject(result);
//                                jsonarray = new JSONArray(result);
////                                if (!post_params.isEmpty()){
////                                    for (int j=0;j<post_params.size();j++){
////                                        ArrayList<suggestion_details> arr = new ArrayList<>();
////                                        arr.add(post_params.get(j));
////                                        post_params.removeAll(arr);
////                                    }
////                                }
//                                if (jsonarray.length() > 4) {
//                                    for (int j = 0; j < 4; j++) {
//                                        obj = new JSONObject(jsonarray.getString(j));
////
//                                        String company = obj.getString("company");
//                                        String branch_name = obj.getString("name");
//                                        suggestion_details suggestion_details = new suggestion_details();
//                                        suggestion_details.setTitle(branch_name);
//                                        suggestion_details.setLatitude(obj.getString("longitude"));
//                                        suggestion_details.setLongitude(obj.getString("latitude"));
//                                        suggestion_details.setOpens(obj.getString("opens"));
//                                        suggestion_details.setCloses(obj.getString("closes"));
//                                        suggestion_details.setId(obj.getString("id"));
//                                        suggestion_details.setCompany(company);
////                                    post_params.add(company+ " " +branch_name);
//                                        post_params.add(suggestion_details);
//                                    }
//                                } else {
//                                    for (int i = 0; i < jsonarray.length(); i++) {
//                                        obj = new JSONObject(jsonarray.getString(i));
////
//                                        String company = obj.getString("company");
//                                        String branch_name = obj.getString("name");
//                                        suggestion_details suggestion_details = new suggestion_details();
//                                        suggestion_details.setTitle(branch_name);
//                                        suggestion_details.setLatitude(obj.getString("longitude"));
//                                        suggestion_details.setLongitude(obj.getString("latitude"));
//                                        suggestion_details.setOpens(obj.getString("opens"));
//                                        suggestion_details.setCloses(obj.getString("closes"));
//                                        suggestion_details.setId(obj.getString("id"));
//                                        suggestion_details.setCompany(company);
////                                    post_params.add(company+ " " +branch_name);
//                                        post_params.add(suggestion_details);
//                                        Log.d(TAG, "run: search results " + obj.getString("company") + " " + obj.getString("name"));
//
//                                    }
//                                }
//
//
//
//                                Log.d(TAG, "run: search odj " + result);
//
//
////                                PUT DATA TO THE LOCAL DATABASE
//
//
//                            } catch (Throwable t) {
//                                Log.d(TAG, "run: search result error " + searchtext);
//
//                                Log.e("My App", "search result error  " + t.getMessage());
//
////                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                            }
//
//
////                            Log.d(TAG, "run: string" + result);
//
//                        } catch (Exception e) {
//                            Log.e("log_tag", "Error converting result " + e.toString());
//                        }
//
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                    //                    createDialog("Error", "Cannot Estabilish Connection");
//                }
//
//                Looper.loop(); //Loop in the message queue
//            }
//        };
//
//        t.start();
//    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }

    public static void hideactivityitems(Activity activity) {
        activity.findViewById(R.id.borderedsearchlay).setVisibility(View.VISIBLE);
//        activity_order_details.findViewById(R.id.message).setVisibility(View.VISIBLE);
//        activity.findViewById(R.id.active_ticket_numb).setVisibility(View.VISIBLE);
//        activity.findViewById(R.id.searchBar_q).setVisibility(View.VISIBLE);


    }


    public void getdevicelocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
//
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{
//                            android.Manifest.permission.ACCESS_FINE_LOCATION
//                    },PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        else {
            mLocationPermissionGranted = true;


        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getdevicelocation();
                }
            }
        }
    }
}
