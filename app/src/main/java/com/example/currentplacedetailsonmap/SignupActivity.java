package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.model.strings_;
import com.example.currentplacedetailsonmap.utils.Dbhelper;

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


public class SignupActivity extends AppCompatActivity {
    static ProgressDialog pDialog;
    private static String signup_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadwithanimation();
        sign_up(this);

    }

    private void loadwithanimation(){
        LayoutInflater inflator=getLayoutInflater();
        View view=inflator.inflate(R.layout.layout_signup, null, false);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_to_left));
        setContentView(view);

    }

    public void sign_up(final Activity activity ) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();

        final EditText text_password = findViewById(R.id.signup_password);
        editTextHashs.put(R.string.password, text_password);
        final EditText text_email = findViewById(R.id.signup_email);
        editTextHashs.put(R.string.email, text_email);

        final TextView save = findViewById(R.id.confirm_signup);
        final TextView login=findViewById(R.id.login);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(text_email.getText()) || TextUtils.isEmpty(text_password.getText())) {
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if(TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                }
                else {
                    new sendJson_signup(activity,text_email.getText().toString(),text_password.getText().toString()).execute();

                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View alertLayout = activity.getLayoutInflater().inflate(R.layout.layout_signup, null);

                startActivity(new Intent(activity,LoginActivity.class));
//                sign_up(activity,alertLayout,fragmentActivity);
            }
        });

    }

    private class sendJson_signup extends AsyncTask<String, String, String> {

        Activity activity;
        String email;
        String password;
        public sendJson_signup(final Activity _activity, final String _email, final String _password){
            password=_password;
            activity=_activity;
            email=_email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Signing up......");
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/user/signup");
                json.put("email", email);
                json.put("password",password);
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
                        String TAG="";
                        try {
                            obj= new JSONObject(result);

                            if (obj.get("msg")!=null){
                                Log.d("My App ob ", obj.getString("msg"));
                                signup_message="Email already exists";
//                                Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
//                                pDialog.dismiss();
                                Log.d(TAG, "run: " + obj);
                            }


                        } catch (Throwable t) {
                            if (t.getMessage().equals("No value for msg")){
                                new Dbhelper(activity).insert_user("",email,obj.getString("password"),obj.getString("id"));
//                                signup_message="Account Created";
//                                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
//                                        .setTitleText("Account Created")
//                                        .setContentText("click ok to exit")
//                                        .show();
//                                Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(activity,MainActivity.class));
                                pDialog.dismiss();
                            }
                            else{
                                signup_message="Couldn't sign up \n\n There was a problem communicating with the servers. \n\n Try again later.";

                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                            }
//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                        }


                        Log.d(TAG, "run: string "+result);

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }


                }

            } catch(Exception e) {
                e.printStackTrace();
                signup_message="No Internet Connection";
//                Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
//                    createDialog("Error", "Cannot Estabilish Connection");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if(!signup_message.isEmpty()){
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(signup_message)
                        .setContentText("click ok to exit")
                        .show();
            }


        }
    }
//
//    private void sendJson_signup(final Activity activity, final String email, final String password) {
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
//                pDialog = new ProgressDialog(activity);
//                pDialog.setMessage("Creating account. Please wait...");
//                pDialog.setIndeterminate(false);
//                pDialog.setCancelable(false);
//                pDialog.show();
//
//                try {
//                    HttpPost post = new HttpPost(new strings_().url()+"/user/signup");
//                    json.put("email", email);
//                    json.put("password",password);
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
//                            try {
//                                obj= new JSONObject(result);
//
//                                if (obj.get("msg")!=null){
//                                    Log.d("My App ob ", obj.getString("msg"));
//                                    signup_message="Email already exists";
//                                    Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
//                                    pDialog.dismiss();
//                                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                            .setTitleText("Email already exists")
//                                            .setContentText("click ok to exit")
//                                            .show();
//                                    Log.d(TAG, "run: " + obj);
//
//                                }
//
//
//                            } catch (Throwable t) {
//                                if (t.getMessage().equals("No value for msg")){
//                                    new Dbhelper(activity).insert_user("",email,obj.getString("password"),obj.getString("id"));
//                                    signup_message="Account Created";
//                                    new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
//                                            .setTitleText("Account Created")
//                                            .setContentText("click ok to exit")
//                                            .show();
//                                    Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
//                                    finish();
//                                    startActivity(new Intent(activity,MainActivity.class));
//                                    pDialog.dismiss();
//                                }
//                                else{
//                                    Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                                }
////                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                            }
//
//
//                            Log.d(TAG, "run: string "+result);
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
//                    signup_message="Cannot Estabilish Connection";
//                    Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
//                    pDialog.dismiss();
////                    createDialog("Error", "Cannot Estabilish Connection");
//                }
//
//                Looper.loop(); //Loop in the message queue
//            }
//        };
//
//        t.start();
//    }

}
