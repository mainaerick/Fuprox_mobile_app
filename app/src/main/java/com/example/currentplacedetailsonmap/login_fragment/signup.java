package com.example.currentplacedetailsonmap.login_fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.currentplacedetailsonmap.LoginActivity;
import com.example.currentplacedetailsonmap.R;
import com.example.currentplacedetailsonmap.fragment.ordersfragment;
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

import static android.content.Context.MODE_PRIVATE;

public class signup extends Fragment {

    Activity activity;
    ProgressDialog pDialog;

    public signup(Activity _activity){
        activity=_activity;
    }


    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=getActivity();

        View view;
        view=inflater.inflate(R.layout.layout_signup, container, false);

        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));
        sign_up(view);
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

    public void sign_up(final View view ) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();

        final EditText text_password = view.findViewById(R.id.signup_password);
        editTextHashs.put(R.string.password, text_password);
        final EditText text_email = view.findViewById(R.id.signup_email);
        editTextHashs.put(R.string.email, text_email);

        final TextView save = view.findViewById(R.id.confirm_signup);
        final TextView login=view.findViewById(R.id.login);
        TextView activateacc= view.findViewById(R.id.activate_acc);


        activateacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rloadfragment(new activate_acc(activity));
            }
        });
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
                else if (text_password.getText().toString().length()<4){
                    Toast.makeText(activity, "Password too short, minimum 4", Toast.LENGTH_SHORT).show();
                }
                else {
//                    new validate_email(text_email.getText().toString(),text_password.getText().toString()).execute();
                    new sendJson_signup(activity,text_email.getText().toString(),text_password.getText().toString()).execute();
//                    validateemail(text_email.getText().toString(),text_password.getText().toString());

                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View alertLayout = activity.getLayoutInflater().inflate(R.layout.layout_signup, null);

//                startActivity(new Intent(activity,LoginActivity.class));
                rloadfragment(new login(activity));
//                sign_up(activity,alertLayout,fragmentActivity);
            }
        });
    }

    String message="";
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
            pDialog.setMessage("Sign up...");
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
                HttpPost post = new HttpPost(new strings_().url()+"/user/signup");
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
                            if (obj.length()>2){
//                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                                pDialog.dismiss();
                                Log.d(TAG, "run: " + obj);
                            }
                            else {
                                message ="Email already exists";
                            }
                        } catch (Throwable t) {
                            message ="Couldn't sign up \n\n There was a problem communicating with the servers. \n\n Try again later.";
                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                        }
                        Log.d(TAG, "run: string "+result);
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
                message ="No Internet Connection";
//                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
//                    createDialog("Error", "Cannot Estabilish Connection");
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            if(!message.isEmpty()){
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
            else {
                Toast.makeText(activity, "Activation Code Sent To Email!", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = activity.getSharedPreferences("pending_signup", MODE_PRIVATE).edit();
                editor.putString("email", email);
                editor.apply();
                rloadfragment(new activate_acc(activity));
            }


        }
    }

}
