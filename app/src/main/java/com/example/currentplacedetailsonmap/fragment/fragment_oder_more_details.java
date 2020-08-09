package com.example.currentplacedetailsonmap.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.currentplacedetailsonmap.model.strings_;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.R;


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

import cn.pedant.SweetAlert.SweetAlertDialog;


@SuppressLint("ValidFragment")
public class fragment_oder_more_details extends BottomSheetDialogFragment {

    public  static String TAG="";

    String sname,saddress,user_id,stime,sid;
    TextView ticket_number,ticket_status,ticket_service;
    ProgressDialog pDialog;
    TextView confirm;
    ProgressBar progressBar;


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
        progressBar = contentView.findViewById(R.id.progress_circular);
        confirm=contentView.findViewById(R.id.confirmticket);
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
        String t_number,t_s_name;
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
                    HttpPost post = new HttpPost(new strings_().url()+"/book/get");
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

                                t_status=ticket_status_bol;
                                t_number=ticket_numvber;
                                t_s_name=service_name;

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
                if (t_status){
                    ticket_status.setText("Not active");
                }
                else {
                    ticket_status.setText("Active");
                }

                ticket_number.setText(t_number);
                ticket_service.setText(t_s_name);
                confirm.setVisibility(View.VISIBLE);
            }

            else {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(tickt_error)
                        .setContentText("click ok to exit")
                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dismiss();
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
            }

            imageView.setVisibility(View.INVISIBLE);
//            pDialog.dismiss();

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
