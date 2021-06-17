package com.fuprox.noqueue.new_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.adapters.institution_adapater;
import com.fuprox.noqueue.adapters.services_adapter;
import com.fuprox.noqueue.fragment.Institution_fragment;
import com.fuprox.noqueue.fragment.Services_fragment;
import com.fuprox.noqueue.model.RecyclerTouch;
import com.fuprox.noqueue.model.RecyclerTouchListener;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.new_app.adapter.InstitutionAdapter;
import com.fuprox.noqueue.new_app.adapter.category_adapter;
import com.fuprox.noqueue.utils.institution_details;
import com.fuprox.noqueue.utils.service_details;

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
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private InstitutionAdapter institution_adapater;
    private category_adapter category_adapter;
    RecyclerView categoryrecyclerView;
    ShimmerRecyclerView companyrecyclerView;
    LinearLayout error_linearlayout;
    LinearLayout layout_loading;

    ArrayList<service_details> servicelist= new ArrayList<>();
    ArrayList<institution_details> institution_list = new ArrayList<>();

    public static String error = " ";

    String error_title = "";

    boolean list_status_nolist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        error_linearlayout= findViewById(R.id.layout_error_disp);

        layout_loading = findViewById(R.id.layout_loading);

        companyrecyclerView = findViewById(R.id.companyrecyclerview);
        categoryrecyclerView = findViewById(R.id.categoryrecyclerview);

        new services_get().execute();


    }
    private void setupAdapter(){
        LinearLayoutManager mLayoutManager;

        category_adapter = new category_adapter(this,servicelist);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryrecyclerView.setLayoutManager(mLayoutManager);
        categoryrecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoryrecyclerView.setAdapter(category_adapter);
        categoryrecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), categoryrecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position){
                institution_list.clear();
                new institution_get(servicelist.get(position).getId()).execute();
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void setup_InstitutionAdapter(){
        institution_adapater = new InstitutionAdapter(this,institution_list);
        int numberOfColumns = 2;
        companyrecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        companyrecyclerView.setItemAnimator(new DefaultItemAnimator());
        companyrecyclerView.setAdapter(institution_adapater);
        companyrecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), companyrecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position){
                Intent intent1 = new Intent(HomeActivity.this, BranchesActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("institution_id", institution_list.get(position).getId());
                bundle.putString("institution_name",institution_list.get(position).getTitle());
                bundle.putString("imgurl",institution_list.get(position).getIcon_url());

                intent1.putExtras(bundle);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }
    public class services_get extends AsyncTask<String, String, String> {
        String error_title="";
        public services_get() {

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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(HomeActivity.this) + "/service/get");
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

            setupAdapter();
            if (error_title.isEmpty()){
                categoryrecyclerView.setVisibility(View.VISIBLE);
                if (!institution_list.isEmpty()){
                    institution_list.clear();
                }
                new institution_get(servicelist.get(0).getId()).execute();
                companyrecyclerView.setVisibility(View.VISIBLE);

                companyrecyclerView.showShimmerAdapter();
                service_details service_details = new service_details();

//                for (int i=0; i<2;i++){
//                    service_details.setTitle("Hospital");
//                    service_details.setId("Garage");
//
//                    servicelist.add(service_details);
//                }
                error_linearlayout.setVisibility(View.GONE);
            }
            else {
                network_error(error_title,error);
            }

            Log.d(TAG, "onPostExecute: services  ");
        }
    }


    public void network_error(String error,String error_description){
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {error};
//        List<String> list = Arrays.asList(strings_);
//        ArrayList<branches_details> servicelist = new ArrayList<>();
        error_linearlayout.setVisibility(View.VISIBLE);
        categoryrecyclerView.setVisibility(View.GONE);
        companyrecyclerView.setVisibility(View.GONE);
        TextView error_title=findViewById(R.id.error_title);
        TextView error_desc=findViewById(R.id.error_description);
        error_title.setText(error);
        error_desc.setText(error_description);

        Button error_retry=findViewById(R.id.retry_btn);

        error_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_linearlayout.setVisibility(View.INVISIBLE);
                new services_get().execute();
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



    private class institution_get extends AsyncTask<String, String, String> {
        View view;
        String service_id;
        String error_title="";
        public institution_get(String service_id){
            this.view = view;
            this.service_id = service_id;
        }
        @Override
        protected void onPreExecute() {
//            layout_loading.setVisibility(View.VISIBLE);
//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Loading. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {


            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();
            StringBuilder sb;
            InputStream is = null;
            String result = null;

            try {
                HttpPost post = new HttpPost(new strings_().get_ipaddress(HomeActivity.this)+"/company/by/service");
                json.put("service", service_id);

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
                        try {
//                            obj= new JSONObject(result);

                            jsonarray = new JSONArray(result);



                            String name = null;


                            int i=0;
                            while (i<jsonarray.length()){


                                institution_details institution_details = new institution_details();
                                obj=new JSONObject(jsonarray.getString(i));

                                institution_details.setTitle(obj.getString("name"));
                                institution_details.setId(obj.getString("id"));
                                institution_details.setIcon_url(obj.getString("icon"));

                                Log.d(TAG, "get all services "+ obj.getString("name"));

                                institution_list.add(institution_details);
//
                                i++;
                            }

                            if (institution_list.size()==0){
                                error_title="No Institutions!";
                                error="No Institutions added yet!";
                            }
                            Log.d(TAG, "get all institution "+ result);

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: services\"" + obj + "\""+t.getMessage());
                            error="Oops.. \nThere was a problem communicating with the servers.";
                            error_title="Server error!";

                        }
                        Log.d(TAG, "run: string" + result);

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        error="Oops.. \nThere was a problem communicating with the servers. \n\n Tap to retry.";
                        error_title="Server error!";
                    }

                }

            } catch(Exception e) {
                e.printStackTrace();
//                    Toast.makeText(getActivity(), "Problem Establishing Connection", Toast.LENGTH_SHORT).show();
//                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("There was a problem communicating with the servers.")
//                            .setContentText("Try again later.")
//                            .show();
                Log.d(TAG, "run: services get" );
                error = "Oops.. \nThere was a problem establishing internet connection.";
                error_title = "Internet connection error!";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            pDialog.dismiss();
            layout_loading.setVisibility(View.GONE);
//            pDialog.dismiss();
            setup_InstitutionAdapter();
            if (error_title.isEmpty()){
                categoryrecyclerView.setVisibility(View.VISIBLE);
                error_linearlayout.setVisibility(View.GONE);

            }
            else {
                network_errorInst(service_id,error_title,error);
            }

            Log.d(TAG, "onPostExecute: Institutions  ");

        }
    }

    public void network_errorInst(String id,String error,String error_description){
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {error};
        List<String> list= Arrays.asList(strings);
//                ArrayList<branches_details> branch_list = new ArrayList<>();

        error_linearlayout=findViewById(R.id.layout_error_disp);
        error_linearlayout.setVisibility(View.VISIBLE);
        companyrecyclerView.setVisibility(View.GONE);
        TextView txterror_title=findViewById(R.id.error_title);
        TextView error_desc=findViewById(R.id.error_description);
        txterror_title.setText(error);
        error_desc.setText(error_description);

        Button error_retry=findViewById(R.id.retry_btn);
        error_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_title = "";
                error_linearlayout.setVisibility(View.INVISIBLE);

                new institution_get(id).execute();
            }
        });
//        String name = null;
//        institution_list=new ArrayList<>();
//        for (int i = 0; i < strings_.length; i++) {
//            institution_details institution_details = new institution_details();
//
//            institution_details.setTitle(list.get(i));
//            institution_list.add(institution_details);
//
//        }
        list_status_nolist=true;
    }
    public static void hideactivityitems(Activity activity) {
        activity.findViewById(R.id.borderedsearchlay).setVisibility(View.VISIBLE);
//        activity_order_details.findViewById(R.id.message).setVisibility(View.VISIBLE);
//        activity.findViewById(R.id.active_ticket_numb).setVisibility(View.VISIBLE);
//        activity.findViewById(R.id.searchBar_q).setVisibility(View.VISIBLE);


    }
}