package com.example.currentplacedetailsonmap.login_fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.currentplacedetailsonmap.R;
import com.example.currentplacedetailsonmap.model.strings_;

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
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class forgot_pass extends Fragment {

    Activity activity;
    ProgressDialog pDialog;

    Context context;
    public forgot_pass(Activity _activity){
        activity=_activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=getActivity();
        View view;
        view=inflater.inflate(R.layout.layoutforgotpass, container, false);


        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left));

        forgotpass(view);
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
    private void forgotpass(View view){
        TextView request_code=view.findViewById(R.id.requestcode);
        TextView have_code=view.findViewById(R.id.havecode);
        EditText email_tv=view.findViewById(R.id.forgotpass_email);
        have_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
                editTextHashs.put(R.string.email, email_tv);
                if(TextUtils.isEmpty(email_tv.getText())) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if(TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                }
                else {
                    rloadfragment(new confirm_r_code(activity,email_tv.getText().toString()));
                }

            }
        });


        request_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
                editTextHashs.put(R.string.email, email_tv);
                if(TextUtils.isEmpty(email_tv.getText())) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if(TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                }
                else {
                    new request_code(activity,email_tv.getText().toString()).execute();
                }
            }
        });
    }

    String message="";
    private class request_code  extends AsyncTask<String, String, String> {
        Activity activity;
        String email;
        String status="";

        public request_code(final Activity _activity, final String _email){
            activity=_activity;
            email=_email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Requesting code.....");
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(getContext())+"/password/forgot/email");
                json.put("email", email);
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

                            boolean user=obj.getBoolean("user");
                            if (user){
                                status="true";
                            }
                            else {
                                message="Email does not exist!";
                            }
                        } catch (Throwable t) {
                            message ="Couldn't communicate with the server";
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
                rloadfragment(new confirm_r_code(activity,email));
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
