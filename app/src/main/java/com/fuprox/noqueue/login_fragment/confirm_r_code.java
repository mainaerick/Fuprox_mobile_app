package com.fuprox.noqueue.login_fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.model.strings_;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class confirm_r_code extends Fragment {

    Activity activity;
    ProgressDialog pDialog;
    String email;

    public confirm_r_code(Activity _activity,String email){
        activity=_activity;
        this.email=email;
    }

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        context=getActivity();
        view=inflater.inflate(R.layout.layout_confirmrcode, container, false);


        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left));

        confirmrcode(view,email);
        return view;
    }
    private void rloadfragment(Fragment fragment){
        FragmentManager fm = ((FragmentActivity) context)
                .getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void confirmrcode(View view,String email){
        TextView retrycode=view.findViewById(R.id.retrycode);
        TextView verify_confirm = view.findViewById(R.id.verify_resetcode);
        EditText code_text = view.findViewById(R.id.resetcode);
        verify_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new confirm_request_code(activity,code_text.getText().toString(),email).execute();
            }
        });


        retrycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rloadfragment(new forgot_pass(activity));
            }
        });
    }


    String message="";
    private class confirm_request_code  extends AsyncTask<String, String, String> {
        Activity activity;
        String security_code,email;
        String status="";

        public confirm_request_code(final Activity _activity, final String security_code,String email){
            activity=_activity;
            this.security_code=security_code;
            this.email=email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Verifying security code.....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(getContext())+"/password/forgot/code");
                json.put("code", security_code);
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
                        try {
                            obj= new JSONObject(result);

                            boolean code=obj.getBoolean("code");
                            if (code){
                                status="true";
                            }
                            else {
                                message="Code is invalid";
                            }
                        } catch (Throwable t) {
                            message ="Code is invalid";
                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());

                        }
                        Log.d(TAG, "run: string "+result);

                    } catch (Exception e) {
                        pDialog.dismiss();
//                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
                        message ="Couldn't communicate with the server";
                        Log.e("log_tag", "Error converting result login" + e.toString());
                        Log.d(TAG, "run: error loging result "+result);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
                message ="Cannot Estabilish Connection";
                Log.e("log_tag", "Error converting result login" + e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if (status.equals("true")){
                rloadfragment(new change_password(activity,email,security_code));
            }
            else{
                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog
                        .setTitleText(message)
                        .setContentText("click ok to exit")
                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
            }
        }
    }







}

