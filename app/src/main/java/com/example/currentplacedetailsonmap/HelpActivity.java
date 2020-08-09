package com.example.currentplacedetailsonmap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.adapters.CustomExpandableListAdapter;
import com.example.currentplacedetailsonmap.utils.service_details;

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
import java.util.HashMap;
import java.util.List;
import com.example.currentplacedetailsonmap.model.strings_;


public class HelpActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail=new HashMap<>();
    LinearLayout error_linearlayout;
    ProgressDialog pDialog;
    HashMap<String, List<String>> ExpandableListDataPump=new HashMap<>() ;
    List<String> answer = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        ImageView backbtn=findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new faq_get().execute();
    }

    private void setupAdapter(){
        error_linearlayout= findViewById(R.id.layout_error_disp);
        expandableListView = findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump;
        expandableListTitle = new ArrayList<String>(ExpandableListDataPump.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getApplicationContext(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//                ).show();
                return false;
            }
        });
    }

    private class faq_get extends AsyncTask<String, String, String> {
        View view;
        String error_title="";
        String error="";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(HelpActivity.this);
            pDialog.setMessage("FAQs loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
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
                HttpPost post = new HttpPost(new strings_().url() + "/help/feed");
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

                                answer.add(obj.getString("solution"));
                                ExpandableListDataPump.put(obj.getString("title"),answer);

                                i++;
                            }
                            if (answer.size()==0) {
                                error="No Questions added yet!";
                                error_title="No Questions!";
//                                network_error(view, error_title,"Try again later and find services provided!");
                            }
                            else {

                            }
                            Log.d("TAG", "get all services " + jsonarray);


                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: QUESTIONS\"" + obj + "\"" + t.getMessage());
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
                Log.d("", "run: services get");
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
            pDialog.dismiss();
            setupAdapter();

            if (error_title.isEmpty()){
                expandableListView.setVisibility(View.VISIBLE);
                error_linearlayout.setVisibility(View.GONE);
            }
            else {

                network_error(view,error_title,error);
            }
        }
    }

    public void network_error(View view,String error,String error_description){
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {error};
//        List<String> list = Arrays.asList(strings_);
//        ArrayList<branches_details> servicelist = new ArrayList<>();
        error_linearlayout.setVisibility(View.VISIBLE);
        expandableListView.setVisibility(View.GONE);
        TextView error_title=view.findViewById(R.id.error_title);
        TextView error_desc=view.findViewById(R.id.error_description);
        error_title.setText(error);
        error_desc.setText(error_description);

        Button error_retry=view.findViewById(R.id.retry_btn);

        error_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new faq_get().execute();
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
//        list_status_nolist=true;

    }
}
