package com.fuprox.noqueue.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.fuprox.noqueue.MainActivity;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.adapters.institution_adapater;
import com.fuprox.noqueue.adapters.suggestion_adapter;
import com.fuprox.noqueue.model.JSONParser;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.institution_details;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




@SuppressLint("ValidFragment")
public class Institution_fragment extends Fragment implements MaterialSearchBar.OnSearchActionListener  {
    public  static String TAG="";
    public static final String KEY_FRIDAY_FRAGMENT = "Friday";
    public static String error=" ";
    final JSONParser jParser = new JSONParser();
    static ProgressDialog pDialog;
    ArrayList<institution_details> institution_list = new ArrayList<>();;
    String service_id;
    MaterialSearchBar searchBar;
    suggestion_adapter suggestion_adapter;
    static List <suggestion_details> post_params = new ArrayList<>();
    boolean list_status_nolist=false;

    LinearLayout error_linearlayout;
    ListView listView;
    String error_title="";
    LinearLayout layout_loading;


    Activity activity;
    @Override
    public void onResume() {
        super.onResume();
        MainActivity.onbackpress="_";
    }

    public Institution_fragment(Activity activity,String identifier){

        this.service_id=identifier;
        this.activity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View mainview=inflater.inflate(R.layout.layout_institution, container, false);

        layout_loading = mainview.findViewById(R.id.layout_loading);

        if (institution_list.isEmpty()){
            new institution_get(mainview).execute();
        }
        else {
            setupAdapter(mainview);
        }
//        setupAdapter(mainview);
        return mainview;
    }

    private void setupAdapter(View view) {

//        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {"Equity Bank", "KCB Bank","National Bank","Cooperative Bank"};
//        ArrayList<institution_details> institutionlist = new ArrayList<>();
        List<String> list= Arrays.asList(strings);


//        String name = null;
//        for (int i = 0; i < strings_.length; i++) {
//            institution_details institution_details = new institution_details();
//
//            institution_details.setTitle(list.get(i));
//            institutionlist.add(institution_details);
//
//        }




        view.findViewById(R.id.institution_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadFragment(new Services_fragment());
            }
        });


        listView = view.findViewById(R.id.institution_listview);
        error_linearlayout=view.findViewById(R.id.layout_error_disp);

//        adapter = new orderadapter(getActivity(), listView, R.layout.listorders, new Dbhelper(getContext()).get_booking());

        institution_adapater adapter = new institution_adapater(getActivity(), listView, R.layout.list_institution, institution_list);
        listView.setAdapter(adapter);

        if (!list_status_nolist){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView=view.findViewById(R.id.institution_title);

                    TextView textViewid=view.findViewById(R.id.txtinstitution_id);
                    ImageView iconimageview=view.findViewById(R.id.service_image);

                    loadFragment(new Branches_fragment(activity,textViewid.getText().toString(),textView.getText().toString(),service_id,iconimageview.getDrawable()));

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
                    loadFragment(new Institution_fragment(activity,service_id));


                }
            });


        }




//
//        LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
//        suggestion_adapter=new suggestion_adapter(inflater);
//        sendJson_getbranch_details(" ");
//        searchBar=view.findViewById(R.id.searchBar_institution);
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
//



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


    private class institution_get extends AsyncTask<String, String, String> {

        View view;
        public institution_get(View view){
            this.view=view;
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

                new institution_get(view).execute();
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
//                    HttpPost post = new HttpPost(new strings_().get_ipaddress()+"/app/search/");
//                    json.put("term", " ");
//                    StringEntity se = new StringEntity( json.toString());
//                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                    post.setEntity(se);
//                    response = client.execute(post);
//                    HttpEntity entity = response.getEntity();
//                    is = entity.getContent();
//
//
//
//                    /*Checking response */
//                    if(response!=null){
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
//                                jsonarray=new JSONArray(result);
////                                if (!post_params.isEmpty()){
////                                    for (int j=0;j<post_params.size();j++){
////                                        ArrayList<suggestion_details> arr = new ArrayList<>();
////                                        arr.add(post_params.get(j));
////                                        post_params.removeAll(arr);
////                                    }
////                                }
//                                if (jsonarray.length()>4){
//                                    for (int j=0;j<4;j++){
//                                        obj=new JSONObject(jsonarray.getString(j));
////
//                                        String company=obj.getString("company");
//                                        String branch_name=obj.getString("name");
//                                        suggestion_details suggestion_details=new suggestion_details();
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
//                                }
//
//                                else{
//                                    for (int i=0;i<jsonarray.length();i++){
//                                        obj=new JSONObject(jsonarray.getString(i));
////
//                                        String company=obj.getString("company");
//                                        String branch_name=obj.getString("name");
//                                        suggestion_details suggestion_details=new suggestion_details();
//                                        suggestion_details.setTitle(branch_name);
//                                        suggestion_details.setLatitude(obj.getString("longitude"));
//                                        suggestion_details.setLongitude(obj.getString("latitude"));
//                                        suggestion_details.setOpens(obj.getString("opens"));
//                                        suggestion_details.setCloses(obj.getString("closes"));
//                                        suggestion_details.setId(obj.getString("id"));
//                                        suggestion_details.setCompany(company);
////                                    post_params.add(company+ " " +branch_name);
//                                        post_params.add(suggestion_details);
//                                        Log.d(TAG, "run: search results "+ obj.getString("company")+" "+obj.getString("name"));
//
//                                    }
//                                }
//
//
//
//                                Log.d(TAG, "run: search odj "+result);
//
//
//
//
//
//
//
////                                PUT DATA TO THE LOCAL DATABASE
//
//
//
//
//                            } catch (Throwable t) {
//                                Log.d(TAG, "run: search result error "+ searchtext);
//
//                                Log.e("My App", "search result error  " +t.getMessage());
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
//                } catch(Exception e) {
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
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }

}
