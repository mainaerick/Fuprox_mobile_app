package com.fuprox.noqueue.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.fuprox.noqueue.activities.Direction_activity;
import com.fuprox.noqueue.activities.LatLong;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.activities.Receipt_Activity;
import com.fuprox.noqueue.activities.activity_booking;
import com.fuprox.noqueue.adapters.branches_adapter;
import com.fuprox.noqueue.adapters.suggestion_adapter;
import com.fuprox.noqueue.model.JSONParser;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.branches_details;
import com.fuprox.noqueue.utils.suggestion_details;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


@SuppressWarnings("ALL")
@SuppressLint("ValidFragment")
public class Branches_fragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{
    public  static String TAG="";

    public static final String KEY_FRIDAY_FRAGMENT = "Friday";
    public static String error=" ";
    final JSONParser jParser = new JSONParser();
    String institution_id,institution_name,service_id;
    static ProgressDialog pDialog;
    ArrayList<branches_details> branches_list = new ArrayList<>();
    MaterialSearchBar searchBar;
    suggestion_adapter suggestion_adapter;
    static List <suggestion_details> post_params = new ArrayList<>();
    LatLong my_latlong;
    View tolocation_view;
    private boolean mLocationPermissionGranted;

    String no_branche=" ";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    boolean list_status_nolist=false;
    Activity activity;

    LinearLayout error_linearlayout;
    ListView listView;
    String error_title="";
    Drawable drawable;

    LinearLayout layout_loading;

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.onbackpress="_";
    }

    public Branches_fragment(Activity activity, String institution_id, String institution_name, String service_id, Drawable drawable){
        this.institution_name=institution_name;
        this.institution_id=institution_id;
        this.service_id=service_id;
        this.activity=activity;
        this.drawable=drawable;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mainview=inflater.inflate(R.layout.layout_branches, container, false);
        tolocation_view=inflater.inflate(R.layout.layout_branches, container, false);
        layout_loading = tolocation_view.findViewById(R.id.layout_loading);

        try {
            if (branches_list.isEmpty()){
                new branch_get(mainview).execute();
            }
            else {
                setupAdapter(mainview);
            }
        }catch (Exception e){
            new SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE).setContentText(e.getMessage()).setTitleText("error").show();
        }


        return mainview;
    }

    private void setupAdapter(View view) {

//        ArrayList<String> listNamesOfFiles = new ArrayList<>();
//        String[] strings_ = {"Kenyatta university branch", "Unicity Branch"};
//        List<String> list= Arrays.asList(strings_);
//        ArrayList<branches_details> branch_list = new ArrayList<>();
//
//
//        String name = null;
//        for (int i = 0; i < strings_.length; i++) {
//            branches_details branches_details = new branches_details();
//
//            branches_details.setTitle(list.get(i));
//            branch_list.add(branches_details);
//
//        }

        listView = view.findViewById(R.id.branches_listview);
        error_linearlayout=view.findViewById(R.id.layout_error_disp);

        Collections.sort(branches_list, new Comparator<branches_details>() {
            @Override
            public int compare(branches_details o1, branches_details o2) {
                return o1.getLattitude().compareTo(o2.getLattitude());
            }
        });

        branches_adapter adapter = new branches_adapter(getActivity(), listView, R.layout.list_branches, branches_list,drawable);

        listView.setAdapter(adapter);

        if (!list_status_nolist){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView=view.findViewById(R.id.branch_title);
                    TextView textViewid=view.findViewById(R.id.txtbranchid);
                    bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment(getActivity(),textViewid.getText().toString(),
                            institution_name,"1",
                            textView.getText().toString(),
                            branches_list.get(position).getClose_time(),
                            branches_list.get(position).getOpen_time(),branches_list.get(position).getIsmedical());
                    assert getFragmentManager() != null;
                    bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());

//                    loadFragment(new queue(branches_list.get(position).getLongitude(),branches_list.get(position).getLattitude()));
//                    Intent intent1 = new Intent(getActivity(), activity_booking.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("branch_id", textViewid.getText().toString());
//                    bundle.putString("opens", branches_list.get(position).getOpen_time());
//                    bundle.putString("closes", branches_list.get(position).getClose_time());
//                    bundle.putString("branch_name", textView.getText().toString());
//                    bundle.putString("company", institution_name);
//                    bundle.putString("ismedical", branches_list.get(position).getIsmedical());
//                    bundle.putString("longitude", branches_list.get(position).getLongitude());
//                    bundle.putString("latitude", branches_list.get(position).getLattitude());
//
//                    intent1.putExtras(bundle);
//                    startActivity(intent1);
                }
            });
        }

        else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        TextView textView = view.findViewById(R.id.service_title);
//
//                        TextView textViewid = view.findViewById(R.id.serviceid);
                    loadFragment(new Branches_fragment(activity,institution_id,institution_name,service_id,drawable));

                }
            });
        }
        view.findViewById(R.id.branches_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadFragment(new Institution_fragment(service_id));
            }
        });


//        LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
//
//        suggestion_adapter=new suggestion_adapter(inflater);
//        sendJson_getbranch_details(" ");
//        searchBar=view.findViewById(R.id.searchBar_branch);
//        searchBar.setOnSearchActionListener(this);
////        searchBar.inflateMenu(R.menu.bottom_nav_menu);
//        searchBar.setMaxSuggestionCount(5);
//        searchBar.setText("");
//        suggestion_adapter.setSuggestions(post_params);
//        searchBar.setCustomSuggestionAdapter(suggestion_adapter);
//        Log.d("LOG_TAG", getClass().getSimpleName() + ": text " + searchBar.getText());
//        searchBar.setCardViewElevation(10);
//        searchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
////                searchBar.clearSuggestions();
//                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
//
//
//                suggestion_adapter.getFilter().filter(searchBar.getText());
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
////                searchBar.showSuggestionsList();
//
//            }
//
//        });
//
//        RecyclerView recyclerView=view.findViewById(R.id.mt_recycler);
//        recyclerView.addOnItemTouchListener(
//                new RecyclerTouch(getContext().getApplicationContext(), recyclerView ,new RecyclerTouch.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//
////                        Toast.makeText(MainActivity.this, "position"+position, Toast.LENGTH_SHORT).show();
//
//                        TextView textView=view.findViewById(R.id.suggestion_title);
////                        searchBar.setText(textView.getText().toString());
//
//                        bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment(post_params.get(position).getId(),
//                                post_params.get(position).getCompany(),
//                                "",
//                                textView.getText().toString(),
//                                post_params.get(position).getCloses(),
//                                post_params.get(position).getOpens()
//
//                        );
//                        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
//                        // do whatever
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );




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


    private class branch_get extends AsyncTask<String, String, String> {

        View view;
        public branch_get(View view){
            this.view=view;
        }

        @Override
        protected void onPreExecute() {
            layout_loading.setVisibility(View.VISIBLE);
//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Getting Branches. Please wait...");
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/branch/by/company");
                json.put("company", institution_id);

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
                        try {
//                            obj= new JSONObject(result);

                            jsonarray = new JSONArray(result);



                            String name = null;
                            int i=0;
                            while (i<jsonarray.length()){
                                branches_details branches_details = new branches_details();
                                obj=new JSONObject(jsonarray.getString(i));
                                branches_details.setTitle(obj.getString("name"));
                                branches_details.setId(obj.getString("id"));
                                branches_details.setOpen_time(obj.getString("opens"));
                                branches_details.setClose_time(obj.getString("closes"));
                                branches_details.setLongitude(obj.getString("longitude"));
                                branches_details.setLattitude(obj.getString("latitude"));
                                branches_details.setIcon_url(obj.getString("icon"));
                                boolean ismedical=obj.getBoolean("is_medical");

                                if (ismedical){
                                    branches_details.setIsmedical("1");
                                }
                                else {
                                    branches_details.setIsmedical("0");
                                }
                                Log.d(TAG, "get all services "+ obj.getString("name"));

                                branches_list.add(branches_details);
                                i++;
                            }
                            if (branches_list.size()==0){
                                error_title="No Branches";
                                error="No Branches added yet!";
                            }
////
                            Log.d(TAG, "get all branches "+ result);


                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: branches\"" + obj + "\""+t.getMessage());
                            error= "Oops.. \nThere was a problem communicating with the servers.";
                            error_title="Server error!";
                        }
                        Log.d(TAG, "run: string" + result);

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        error= "Oops.. \nThere was a problem communicating with the servers.";
                        error_title="Server error!";

//                            pDialog.dismiss();

                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
//                    Toast.makeText(getActivity(), "Problem Establishing Connection", Toast.LENGTH_SHORT).show();
//                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("There was a problem communicating with the servers.")
//                            .setContentText("Try again later.")
//                            .show();
                Log.d(TAG, "run: branches get" );
                error = "Oops.. \nThere was a problem establishing internet connection.";
                error_title = "Internet connection error!";
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
        List<String> list= Arrays.asList(strings);
//                ArrayList<branches_details> branch_list = new ArrayList<>();

        error_linearlayout=view.findViewById(R.id.layout_error_disp);
        error_linearlayout.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        TextView txterror_title=view.findViewById(R.id.error_title);
        TextView error_desc=view.findViewById(R.id.error_description);
        txterror_title.setText(error);
        error_desc.setText(error_description);

        Button error_retry=view.findViewById(R.id.retry_btn);

        error_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_title = "";
                error_linearlayout.setVisibility(View.INVISIBLE);

                new branch_get(view).execute();
            }
        });
//        String name = null;
//        branches_list=new ArrayList<>();
//        for (int i = 0; i < strings_.length; i++) {
//            branches_details branches_details = new branches_details();
//
//            branches_details.setTitle(list.get(i));
//            branches_list.add(branches_details);
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
//                    .addToBackStack(null)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }




    }
