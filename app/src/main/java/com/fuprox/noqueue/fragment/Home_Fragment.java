package com.fuprox.noqueue.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.fuprox.noqueue.MainActivity;
import com.fuprox.noqueue.activities.LatLong;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.adapters.services_adapter;
import com.fuprox.noqueue.adapters.suggestion_adapter;
import com.fuprox.noqueue.model.JSONParser;
import com.fuprox.noqueue.model.RecyclerTouchListener;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.new_app.BranchesActivity;
import com.fuprox.noqueue.new_app.HomeActivity;
import com.fuprox.noqueue.new_app.adapter.InstitutionAdapter;
import com.fuprox.noqueue.new_app.adapter.category_adapter;
import com.fuprox.noqueue.utils.institution_details;
import com.fuprox.noqueue.utils.service_details;
import com.fuprox.noqueue.utils.suggestion_details;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.Task;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressLint("ValidFragment")
public class Home_Fragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

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
    LatLong my_latlong;
    View tolocation_view;
    SpinKitView loadingDots;
    LinearLayout layout_loading;
    TextView txterror_title,error_desc;
    Button error_retry;

    private InstitutionAdapter institution_adapater;
    private category_adapter category_adapter;
    RecyclerView categoryrecyclerView;
    ShimmerRecyclerView companyrecyclerView;

    ArrayList<institution_details> institution_list = new ArrayList<>();
    String error_title = "";

    boolean list_status_nolist;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String SHARED_PREFS_FILE = "category_list";

    private boolean mLocationPermissionGranted;
    String gotenpostparams=" ";

    public Home_Fragment(Activity activity, ArrayList<service_details> servicelist){
        this.activity=activity;
        this.servicelist = servicelist;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.onbackpress="exit";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        servicelist.clear();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mainview = inflater.inflate(R.layout.layout_services, container, false);
        tolocation_view=inflater.inflate(R.layout.activity_home, container, false);
        loadingDots = tolocation_view.findViewById(R.id.spin_kit);
        layout_loading = tolocation_view.findViewById(R.id.layout_loading);

        error_linearlayout= tolocation_view.findViewById(R.id.layout_error_disp);

        layout_loading = tolocation_view.findViewById(R.id.layout_loading);

        companyrecyclerView = tolocation_view.findViewById(R.id.companyrecyclerview);
        categoryrecyclerView = tolocation_view.findViewById(R.id.categoryrecyclerview);
        txterror_title = tolocation_view.findViewById(R.id.error_title);
        error_desc = tolocation_view.findViewById(R.id.error_description);
        error_retry = tolocation_view.findViewById(R.id.retry_btn);

//        new services_get(tolocation_view).execute();

//        getdevicelocation();
        if (servicelist.isEmpty()){
            new services_get(tolocation_view).execute();
        }
        else {
            setupAdapter(tolocation_view);
            new institution_get(activity,tolocation_view,servicelist.get(0).getId()).execute();

        }
        tolocation_view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_left));

        return tolocation_view;
    }
    private void setupAdapter(View view) {
        if (activity==null){
            activity=getActivity();
        }
        LinearLayoutManager mLayoutManager;
        error_linearlayout= view.findViewById(R.id.layout_error_disp);
        layout_loading = view.findViewById(R.id.layout_loading);
        companyrecyclerView = view.findViewById(R.id.companyrecyclerview);
        categoryrecyclerView = view.findViewById(R.id.categoryrecyclerview);

        category_adapter = new category_adapter(activity,servicelist);

        mLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryrecyclerView.setLayoutManager(mLayoutManager);
        categoryrecyclerView.setItemAnimator(new DefaultItemAnimator());
        categoryrecyclerView.setAdapter(category_adapter);
        categoryrecyclerView.addOnItemTouchListener(new RecyclerTouchListener(activity, categoryrecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position){
                institution_list.clear();
                new institution_get(getActivity(),view,servicelist.get(position).getId()).execute();
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }
    private void setup_InstitutionAdapter(View view){
        institution_adapater = new InstitutionAdapter(activity,institution_list);
        int numberOfColumns = 2;
        companyrecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        companyrecyclerView.setItemAnimator(new DefaultItemAnimator());
        companyrecyclerView.setAdapter(institution_adapater);
        companyrecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), companyrecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position){
                Intent intent1 = new Intent(getActivity(), BranchesActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("institution_id", institution_list.get(position).getId());
                bundle.putString("institution_name",institution_list.get(position).getTitle());
                bundle.putString("imgurl",institution_list.get(position).getIcon_url());

                intent1.putExtras(bundle);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
            companyrecyclerView.showShimmerAdapter();

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
                            int i = 0;
                            while (i < jsonarray.length()) {
                                service_details service_details = new service_details();
                                obj = new JSONObject(jsonarray.getString(i));

                                service_details.setTitle(obj.getString("name"));
                                service_details.setId(obj.getString("id"));
//                                Log.d(TAG, "get all services " + obj.getString("name"));

                                servicelist.add(service_details);

//                                Log.e(TAG, "doInBackground: services obj "+i+": "+servicelist.get(i).getTitle());
                                i++;
                            }
                            if (servicelist.size()==0) {
                                error="No Services added yet!";
                                error_title="No services!";
//                                network_error(view, error_title,"Try again later and find services provided!");
                            }
//                            Log.d(TAG, "get all services " + jsonarray);


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
            setupAdapter(view);
            if (error_title.isEmpty()){
                categoryrecyclerView.setVisibility(View.VISIBLE);
                if (!institution_list.isEmpty()){
                    institution_list.clear();
                }
                new institution_get(activity,view,servicelist.get(0).getId()).execute();
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
                network_error(view,error_title,error);
            }

            Log.d(TAG, "onPostExecute: services  ");
        }

        }
    private class institution_get extends AsyncTask<String, String, String> {
        View view;
        String service_id;
        String error_title="";
        Context activity;
        public institution_get(Context activity, View view, String service_id){
            this.view = view;
            this.activity = activity;
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
            error_linearlayout.setVisibility(View.GONE);
            companyrecyclerView.setVisibility(View.VISIBLE);
            companyrecyclerView.showShimmerAdapter();
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/company/by/service");
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
                            institution_list.clear();

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
            companyrecyclerView.hideShimmerAdapter();
//            pDialog.dismiss();
            setup_InstitutionAdapter(view);
            if (error_title.isEmpty()){
                categoryrecyclerView.setVisibility(View.VISIBLE);
                error_linearlayout.setVisibility(View.GONE);
            }
            else {
                network_errorInst(view,service_id,error_title,error);
            }
            Log.d(TAG, "onPostExecute: Institutions  ");
        }
    }

    public void network_error(View view,String error,String error_description){
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {error};
//        List<String> list = Arrays.asList(strings_);
//        ArrayList<branches_details> servicelist = new ArrayList<>();
        error_linearlayout.setVisibility(View.VISIBLE);
        categoryrecyclerView.setVisibility(View.GONE);
        companyrecyclerView.setVisibility(View.GONE);
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

    public void network_errorInst(View view,String id,String error,String error_description){
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {error};
        List<String> list= Arrays.asList(strings);
//                ArrayList<branches_details> branch_list = new ArrayList<>();

//        error_linearlayout = view.findViewById(R.id.layout_error_disp);
        error_linearlayout.setVisibility(View.VISIBLE);
        companyrecyclerView.setVisibility(View.GONE);

        txterror_title.setText(error);
        error_desc.setText(error_description);

        error_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_title = "";
                error_linearlayout.setVisibility(View.INVISIBLE);

                new institution_get(activity,view,id).execute();
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

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
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
