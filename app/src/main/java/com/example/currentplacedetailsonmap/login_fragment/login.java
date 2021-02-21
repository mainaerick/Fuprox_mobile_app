package com.example.currentplacedetailsonmap.login_fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.currentplacedetailsonmap.MainActivity;
import com.example.currentplacedetailsonmap.R;
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

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class login extends Fragment {

    Activity activity;
    ProgressDialog pDialog;

    Context context;
    public login(Activity _activity){
        activity=_activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        context=getActivity();
        view=inflater.inflate(R.layout.layout_signin, container, false);

        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_to_left));
        sign_in(view);
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
    public void sign_in(final View view) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText text_email =view.findViewById(R.id.signin_email);
        editTextHashs.put(R.string.email, text_email);
        text_email.setText(retrive_pending_user());
        final EditText text_password = view.findViewById(R.id.signin_password);
        editTextHashs.put(R.string.password, text_password);

        TextView create_acc=view.findViewById(R.id.create_account_from_dialog);

        final TextView save = view.findViewById(R.id.confirm_signin);

        TextView forgotpass=view.findViewById(R.id.forgotpass);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                check user from external db

//
//                sendJson_login(activity,text_email.getText().toString(),text_password.getText().toString(),fragmentActivity);
//
////                new Dbhelper(activity).insert_user("",text_email.getText().toString(),text_password.getText().toString());
//                dialog.dismiss();
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
                    new sendJson_login(activity,text_email.getText().toString(),text_password.getText().toString()).execute();

//                new Dbhelper(activity).insert_user("",text_email.getText().toString(),text_password.getText().toString());
                }



            }
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View alertLayout = activity.getLayoutInflater().inflate(R.layout.layout_signup, null);

//                startActivity(new Intent(activity,SignupActivity.class));
//                sign_up(activity,alertLayout,fragmentActivity);
                rloadfragment(new signup(activity));
            }
        });


        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rloadfragment(new forgot_pass(activity));
            }
        });

    }

    String message="";
    private class sendJson_login  extends AsyncTask<String, String, String> {
        Activity activity;
        String email;
        String password;


        public sendJson_login(final Activity _activity, final String _email, final String _password){
            activity=_activity;
            email=_email;
            password=_password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Login in.....");
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(getContext())+"/user/login");
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
                        try {
                            obj= new JSONObject(result);

                            Log.d(TAG, "run: " + obj.getString("msg"));
                            message =obj.getString("msg");
//                            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText(obj.getString("msg"))
//                                    .setContentText("click ok to exit")
//                                    .show();
//                                Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                            pDialog.dismiss();

                        } catch (Throwable t) {
                            Log.d(TAG, "run: " + obj.getJSONObject("user_data").getString("email"));

                            new Dbhelper(activity).insert_user("",email,obj.getJSONObject("user_data").getString("password"),obj.getJSONObject("user_data").getString("id"));
                            message ="Login Successfull";

                            clear_sharedpref(); //clear pending signup
                            activity.finish();
                            startActivity(new Intent(activity, MainActivity.class));
//
                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());

                        }


                        Log.d(TAG, "run: string "+result);

                    } catch (Exception e) {
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
            if (message.equals("Login Successfull")){

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

                if (message.equals("User is not active. Please check email for code to activate account.")){
                    rloadfragment(new activate_acc(activity));
                }
            }


        }
    }

    private String retrive_pending_user(){
        SharedPreferences prefs = activity.getSharedPreferences("pending_signup", MODE_PRIVATE);
        String email = prefs.getString("email", "");//"No name defined" is the default value.
        if (prefs.getAll().size()==0){
            email="";
        }
        return email;
    }
    private void clear_sharedpref(){
        SharedPreferences.Editor editor = activity.getSharedPreferences("pending_signup", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoginActivity.checkfragment ="login";
    }
}
