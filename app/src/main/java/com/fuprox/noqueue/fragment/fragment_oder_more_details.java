package com.fuprox.noqueue.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fuprox.noqueue.Receiver.FloatingWidgetService;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.booking_details;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fuprox.noqueue.R;


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

import cn.pedant.SweetAlert.SweetAlertDialog;


@SuppressLint("ValidFragment")
public class fragment_oder_more_details extends BottomSheetDialogFragment {

    public  static String TAG="";

    String sname,saddress,user_id,stime,sid;
    TextView ticket_number,ticket_status,ticket_service, ticket_verification,txtqueuesize,txttellers,tvqueuesizeerror,tvtellerslabel;
    ProgressDialog pDialog;
    TextView confirm;
    ProgressBar progressBar;
    LinearLayout layout_active,layout_served;

    public fragment_oder_more_details(String id,String user_id) {
        this.user_id=user_id;
        sid=id;
    }
    
    String invisi="invisible";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (slideOffset>0.5){

            }

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.activity_order_details, null);
        dialog.setContentView(contentView);
        ticket_number=contentView.findViewById(R.id.ticket_number);
        ticket_service=contentView.findViewById(R.id.ticket_service_name);
        ticket_status=contentView.findViewById(R.id.ticket_status);
        ticket_verification = contentView.findViewById(R.id.ticket_verification);
        progressBar = contentView.findViewById(R.id.progress_circular);
        confirm=contentView.findViewById(R.id.confirmticket);
        txtqueuesize = contentView.findViewById(R.id.queuesize);
        layout_active = contentView.findViewById(R.id.ticket_active);
        layout_served = contentView.findViewById(R.id.layout_served);
        txttellers = contentView.findViewById(R.id.tvtellers);
        tvqueuesizeerror = contentView.findViewById(R.id.queuesize_error);
        tvtellerslabel = contentView.findViewById(R.id.tellerslabel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Printing Ticket", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        new ticket_get(contentView).execute();

    }
    public void rorate_Clockwise(View view) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 180f, 0f);
//        rotate.setRepeatCount(10);
        rotate.setDuration(500);
        rotate.start();
    }
    private class ticket_get extends AsyncTask<String, String, String>{
        View contentView;
        String tickt_error=" ";
        String t_number,t_s_name,t_verify,t_branchid;
        Boolean t_status;
        TextView imageView;


        public ticket_get(View view){
            contentView=view;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView=contentView.findViewById(R.id.loading_ordermorededatils);
            progressBar.setIndeterminate(true);
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
                    HttpPost post = new HttpPost(new strings_().get_ipaddress(getActivity())+"/book/get");
                    json.put   ("booking_id", sid);
                    json.put   ("user_id", user_id);
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    /*Checking response */
                    if(response!=null){
//                        InputStream in = response.getEntity().getContent(); //Get the data in the entity

//                        decode response to string

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
                                obj= new JSONObject(result);
                                JSONObject jsonobject = obj.getJSONObject("booking_data");
                                Boolean ticket_status_bol=jsonobject.getBoolean("serviced");
                                String ticket_numvber=jsonobject.getString("code");
                                String service_name=jsonobject.getString("service_name");
                                String verification_number = jsonobject.getString("unique");
                                String branch_id = jsonobject.getString("branch_id");

                                t_status=ticket_status_bol;
                                t_number=ticket_numvber;
                                t_s_name=service_name;
                                t_verify = verification_number;
                                t_branchid = branch_id;

                                Log.d(TAG, "booking more details: object "+ jsonobject);


                            } catch (Throwable t) {
                                Log.e("My App", "Could not parse malformed JSON: \"" + obj + "\""+t.getMessage());
                                tickt_error="Couldn't get ticket \n an error occured communicating with the servers";
//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                            }

//                            Log.d(TAG, "run: string" + result+new Dbhelper(activity).get_user_id());

                        } catch (Exception e) {
                            Log.e("log_tag", "Error converting result " + e.toString());
                            tickt_error="Couldn't get ticket \n an error occured communicating with the servers";

                        }

                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    tickt_error="Couldn't get ticket \n an error occured communicating with the servers";

                    //                    createDialog("Error", "Cannot Estabilish Connection");
                }
//
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if (tickt_error.equals(" ")){
                ticket_number.setText(t_number);
                ticket_service.setText(t_s_name);
                tvtellerslabel.setText("Teller name/number offering "+ t_s_name +" service");

                ticket_verification.setText(t_verify);
                confirm.setVisibility(View.VISIBLE);
                if (t_status){
                    ticket_status.setText("Not active");
                    layout_served.setVisibility(View.VISIBLE);
                }
                else {
                    layout_active.setVisibility(View.VISIBLE);
                    ticket_status.setText("Active");
                    new Checkinfront(sid,Integer.parseInt(t_branchid),ticket_service.getText().toString()).execute();

                }


            }

            else {
                tvqueuesizeerror.setVisibility(View.VISIBLE);

//                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText(tickt_error)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                dismiss();
//                                sweetAlertDialog.dismiss();
//                            }
//                        })
//                        .show();
            }

            imageView.setVisibility(View.INVISIBLE);
//            pDialog.dismiss();

        }
    }

    public class Checkinfront extends AsyncTask<String, String, String> {
        //        Context context;
        int infront;
        String people;
        String booking_id;
        String msg="";
        String error_serv=" ",servicename;
        int branch_id;
        public Checkinfront(String booking_id, int branch_id,String servicename){
            this.booking_id=booking_id;
            this.branch_id = branch_id;
            this.servicename = servicename;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(getContext())+"/ahead/of/you/id");
                json.put("booking_id", booking_id);
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
                            infront=obj.getInt("msg");
//                            Log.e(TAG, "doInBackground: counter "+obj );

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
                Log.d(TAG, "run: check infront connection error"+e.toString() );
                error_serv="There was a problem communicating with the servers. \n\n Try again later.";
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if (error_serv.equals(" ")){
                Log.d(TAG, "onPostExecute: check infront"+infront);
                if (infront==0){
                    txtqueuesize.setTextSize(13);
                    txtqueuesize.setText("Currently being served");

                }
                else if(infront==1){
                    txtqueuesize.setText(infront+"");
                }
                else{
                    txtqueuesize.setText(infront+"");
                }

                new tellers_get(branch_id,servicename).execute();
            }
            else {
                Log.d(TAG, "onPostExecute: check infront"+error_serv);
                tvqueuesizeerror.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "onPostExecute: "+error_serv);
        }
    }


    public class tellers_get extends AsyncTask<String, String, String> {
        Context context;
        int id;
        int infront;
        String people;
        String content,branchname,servicename,booking_id;
        String msg="";
        String error_serv=" ";
        ArrayList<String> tellers = new ArrayList<>();

        public tellers_get(int id, String servicename){
            this.context=context;
            this.id=id;
            this.content=content;
            this.branchname=branchname;
            this.servicename=servicename;
            this.booking_id=booking_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
//            tvnumberpeople.setText("Checking queue size...");
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(getContext())+"/ahead/of/you");
                json.put("branch_id", id);
                json.put("service_name",servicename);
                Log.e(TAG, "doInBackground: "+id+" "+servicename );
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
                            infront=obj.getInt("infront");
//                            infront = 1;
                            JSONArray teller = obj.getJSONArray("tellers");
                            JSONObject tellersobj= null;
                            for (int i=0;i<teller.length();i++){
                                tellersobj = teller.getJSONObject(i);
                                tellers.add(tellersobj.getString("number"));
                                Log.e(TAG, "doInBackground: tellers receipt "+ tellersobj);
                            }
//                            Log.d(TAG, "doInBackground: infront="+infront+"branc_id="+id+" object="+tellers.get(0));
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: services offered check infront \"" + result + "\""+t.getMessage());
                            error_serv="There was a problem communicating with the servers. \n\n Try again later.";
                        }

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        error_serv="There was a problem communicating with the servers. \n\n Try again later.";
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
                Log.d(TAG, "run: check infront connection error"+e.toString() );
                error_serv="There was a problem communicating with the servers. \n\n Try again later.";
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if (error_serv.equals(" ")){
                StringBuilder strlellerlist = new StringBuilder();
                Log.e(TAG, "onPostExecute: rteller number" + tellers.get(0) );
                if (tellers.size()==1){
                    strlellerlist.append(tellers.get(0)+"");
                }
                else {
                    for (int i=0; i<tellers.size(); i++){
                        strlellerlist.append(tellers.get(i)+",");
                    }
                }
                Log.d(TAG, "onPostExecute: check infront"+infront);
                if (infront==0){
                    txttellers.setText(strlellerlist.toString());

                }
                else if(infront==1){
                    txttellers.setText(strlellerlist.toString());
                }
                else{
                    txttellers.setText(strlellerlist.toString());
                }

            }
            else {
                Log.d(TAG, "onPostExecute: check infront"+error_serv);
                tvqueuesizeerror.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "onPostExecute: "+error_serv);
        }
    }


//    private void sendJson_getbookings(final Activity activity) { // Returns branch id
//        final Thread t = new Thread() {
//
//            public void run() {
//                Looper.prepare(); //For Preparing Message Pool for the child Thread
//                HttpClient client = new DefaultHttpClient();
//                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
//                HttpResponse response;
//                JSONObject json = new JSONObject();
//                StringBuilder sb;
//                InputStream is = null;
//                String result = null;
//
//                try {
//                    HttpPost post = new HttpPost(new strings_().url()+"/book/get");
//                    json.put   ("booking_hash", sid);
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
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                            JSONObject obj = null;
//                            JSONArray jsonarray;
//                            try {
//                                obj= new JSONObject(result);
////                                jsonarray = new JSONArray(result);
////                                for(int i=0; i < jsonarray.length(); i++) {
////                                    JSONObject jsonobject = jsonarray.getJSONObject(i);
////                                    String id       = jsonobject.getString("id");
////                                    String branch_id    = jsonobject.getString("branch");
////                                    String institution  = jsonobject.getString("institution");
////                                    String timestamp = jsonobject.getString("start");
////                                    String userid = jsonobject.getString("user_id");
////                                    Log.d(TAG, "run: view all bokings id="+"branch_id="+branch_id);
//
////
////                                }
//                                Log.d(TAG, "booking more details: object "+ obj);
//
//
//
//                            } catch (Throwable t) {
//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//
////                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                            }
//
//
////                            Log.d(TAG, "run: string" + result+new Dbhelper(activity).get_user_id());
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

//    private Bitmap bitmap(){
//        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//                Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
//                return bmp;
//    }


}
