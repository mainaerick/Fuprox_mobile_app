package com.fuprox.noqueue.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuprox.noqueue.MainActivity;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.login_fragment.activate_acc;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.Dbhelper;

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

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ContactActivity extends AppCompatActivity {

    EditText ed_name,ed_email,ed_massage;
//    TextView btn_sendmessage;
    CircularProgressButton btn_sendmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ed_name = findViewById(R.id.ed_name);
        ed_email = findViewById(R.id.ed_email);
        ed_email.setText(new Dbhelper(this).get_user_email());
        ed_massage = findViewById(R.id.ed_message);
        btn_sendmessage = findViewById(R.id.send_message);
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();

        btn_sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(ed_name.getText()) || TextUtils.isEmpty(ed_email.getText()) || TextUtils.isEmpty(ed_massage.getText())) {
                    Toast.makeText(ContactActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
                        if(TextUtils.isEmpty(entry.getValue().getText())) {
                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
                            entry.getValue().requestFocus();
                        }
                    }
                }
                else {
                    new send_mail(ed_name.getText().toString(),ed_email.getText().toString(),ed_massage.getText().toString()).execute();
                }
            }
        });
        ImageView backbtn=findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    String message="";
    private class send_mail extends AsyncTask<String, String, String> {
        String subject;
        String email;
        String body;

        public send_mail(String subject,String email, String body){
            this.subject=subject;
            this.email=email;
            this.body=body;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(ContactActivity.this, "Sending Message...", Toast.LENGTH_SHORT).show();
            btn_sendmessage.startAnimation();
            btn_sendmessage.setActivated(false);
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
                HttpPost post = new HttpPost("http://159.65.144.235:3000/email");
                json.put("subject", subject);
                json.put("email", email);
                json.put("body", body);
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
                            message ="Message Sent";

                            Log.d(TAG, "doInBackground: email contact send "+obj.toString());
                        } catch (Throwable t) {
                            message ="Couldn't send message \n\n There was a problem communicating with the servers. \n\n Try again later.";
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
//                    createDialog("Error", "Cannot Estabilish Connection");
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(ContactActivity.this, message, Toast.LENGTH_SHORT).show();
            if (message.equals("Message Sent")){
                finish();
            }
            btn_sendmessage.stopAnimation();
//            btn_sendmessage.setActivated(true);
        }
    }
}