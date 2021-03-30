package com.fuprox.noqueue;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fuprox.noqueue.login_fragment.activate_acc;
import com.fuprox.noqueue.login_fragment.login;
import com.fuprox.noqueue.model.SocketInstance;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Stack;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "";
    static ProgressDialog pDialog;
    private static String message ="";
    private int which_layout=0;
    public static String checkfragment="";

    Stack<Integer> layoutHistoryStack = new Stack<>();
    private String retrive_pending_user(){
        SharedPreferences prefs = getSharedPreferences("pending_signup", MODE_PRIVATE);
        String email = prefs.getString("email", "");//"No name defined" is the default value.
        if (prefs.getAll().size()==0){
            email="";
        }
        return email;
    }
    Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (retrive_pending_user().equals("")){
            loadFragment(new login(this));
//            SocketInstance instance = (SocketInstance)getApplication();
//            mSocket = instance.getSocketInstance();
//            mSocket.connect();
//            if (mSocket.connected()){
//                Toast.makeText(this, "Socket Connected!!",Toast.LENGTH_SHORT).show();
//                attemptSend();
//            }

        }
        else {
            loadFragment(new activate_acc(this));
        }
        Log.e(TAG, "onCreate: ", null);
    }
    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    String onbackpress="";

    @Override
    public void onBackPressed() {
//        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
////            Toast.makeText(this, checkfragment, Toast.LENGTH_SHORT).show();
//            if (checkfragment.equals("login")){
//                finish();
//            }
//            else {
//                getSupportFragmentManager().popBackStack();
//            }
//        }
//        else{
//            super.onBackPressed();
//        }
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
//
//    private void loadwithanimation(String layout){
//        if (layout.equals("signup")){
//            LayoutInflater inflator=getLayoutInflater();
//            View view=inflator.inflate(R.layout.layout_signup, null, false);
//            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_to_right));
//            setContentView(view);
//            sign_up(LoginActivity.this);
//        }
//        else if (layout.equals("login")){
//            LayoutInflater inflator=getLayoutInflater();
//            View view=inflator.inflate(R.layout.layout_signin, null, false);
//            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_to_left));
//            setContentView(view);
//        }
//
//        else if (layout.equals("fpass")){
//            LayoutInflater inflator=getLayoutInflater();
//            View view=inflator.inflate(R.layout.layoutforgotpass, null, false);
//            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.up_from_bottom));
//            setContentView(view);
//        }
//        else if (layout.equals("change_pass")){
//            LayoutInflater inflator=getLayoutInflater();
//            View view=inflator.inflate(R.layout.layout_password_change, null, false);
//            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.right_to_left));
//            setContentView(view);
//        }
//        else if (layout.equals("activate_acc")){
//            LayoutInflater inflator=getLayoutInflater();
//            View view=inflator.inflate(R.layout.signup_verification, null, false);
//            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.left_to_right));
//            setContentView(view);
//        }
//        else {
//            LayoutInflater inflator=getLayoutInflater();
//            View view=inflator.inflate(R.layout.layout_confirmrcode, null, false);
//            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.up_from_bottom));
//            setContentView(view);
//        }
//    }
//    public void sign_in(final Activity activity) {
//        which_layout=1;
//        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
//        final EditText text_email =findViewById(R.id.signin_email);
//        editTextHashs.put(R.string.email, text_email);
//        final EditText text_password = findViewById(R.id.signin_password);
//        editTextHashs.put(R.string.password, text_password);
//
//        TextView create_acc=findViewById(R.id.create_account_from_dialog);
//
//        final TextView save = findViewById(R.id.confirm_signin);
//
//        TextView forgotpass=findViewById(R.id.forgotpass);
//
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
////                check user from external db
//
////
////                sendJson_login(activity,text_email.getText().toString(),text_password.getText().toString(),fragmentActivity);
////
//////                new Dbhelper(activity).insert_user("",text_email.getText().toString(),text_password.getText().toString());
////                dialog.dismiss();
//                if(TextUtils.isEmpty(text_email.getText()) || TextUtils.isEmpty(text_password.getText())) {
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
//                            entry.getValue().requestFocus();
//                        }
//                    }
//                }
//                else if (text_password.getText().toString().length()<4){
//                    Toast.makeText(activity, "Password too short, minimum 4", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    new sendJson_login(activity,text_email.getText().toString(),text_password.getText().toString()).execute();
//
////                new Dbhelper(activity).insert_user("",text_email.getText().toString(),text_password.getText().toString());
//                }
//
//
//
//            }
//        });
//
//        create_acc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final View alertLayout = activity.getLayoutInflater().inflate(R.layout.layout_signup, null);
//
////                startActivity(new Intent(activity,SignupActivity.class));
////                sign_up(activity,alertLayout,fragmentActivity);
//                loadwithanimation("signup");
//                sign_up(activity);
//            }
//        });
//
//
//        forgotpass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadwithanimation("fpass");
//                forgotpass();
//            }
//        });
//
//    }
//
//
//    public void sign_up(final Activity activity ) {
//        which_layout=2;
//        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
//
//        final EditText text_password = findViewById(R.id.signup_password);
//        editTextHashs.put(R.string.password, text_password);
//        final EditText text_email = findViewById(R.id.signup_email);
//        editTextHashs.put(R.string.email, text_email);
//
//        final TextView save = findViewById(R.id.confirm_signup);
//        final TextView login=findViewById(R.id.login);
//        TextView activateacc= findViewById(R.id.activate_acc);
//
//
//        activateacc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadwithanimation("activate_acc");
//                activate_account();
//            }
//        });
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(TextUtils.isEmpty(text_email.getText()) || TextUtils.isEmpty(text_password.getText())) {
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(activity.getResources().getString(entry.getKey()) + " " + activity.getResources().getString(R.string.field_error));
//                            entry.getValue().requestFocus();
//                        }
//                    }
//                }
//                else if (text_password.getText().toString().length()<4){
//                    Toast.makeText(activity, "Password too short, minimum 4", Toast.LENGTH_SHORT).show();
//                }
//                else {
////                    new validate_email(text_email.getText().toString(),text_password.getText().toString()).execute();
//                    new sendJson_signup(activity,text_email.getText().toString(),text_password.getText().toString()).execute();
////                    validateemail(text_email.getText().toString(),text_password.getText().toString());
//
//                }
//            }
//        });
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final View alertLayout = activity.getLayoutInflater().inflate(R.layout.layout_signup, null);
//
////                startActivity(new Intent(activity,LoginActivity.class));
//                loadwithanimation("login");
//                sign_in(activity);
////                sign_up(activity,alertLayout,fragmentActivity);
//            }
//        });
//    }
//
//    private void forgotpass(){
//        which_layout=3;
//        TextView request_code=findViewById(R.id.requestcode);
//        TextView have_code=findViewById(R.id.havecode);
//        EditText email_tv=findViewById(R.id.forgotpass_email);
//        have_code.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
//                editTextHashs.put(R.string.email, email_tv);
//                if(TextUtils.isEmpty(email_tv.getText())) {
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
//                            entry.getValue().requestFocus();
//                        }
//                    }
//                }
//                else {
//                    loadwithanimation("confirmrcode");
//                    confirmrcode(email_tv.getText().toString());                }
//
//            }
//        });
//
//
//        request_code.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
//                editTextHashs.put(R.string.email, email_tv);
//                if(TextUtils.isEmpty(email_tv.getText())) {
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
//                            entry.getValue().requestFocus();
//                        }
//                    }
//                }
//                else {
//                    new request_code(LoginActivity.this,email_tv.getText().toString()).execute();
//                }
//            }
//        });
//    }
//
//
//    private void confirmrcode(String email){
//        which_layout=4;
//        TextView retrycode=findViewById(R.id.retrycode);
//        TextView verify_confirm = findViewById(R.id.verify_resetcode);
//        EditText code_text = findViewById(R.id.resetcode);
//        verify_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new confirm_request_code(LoginActivity.this,code_text.getText().toString(),email).execute();
//            }
//        });
//
//
//        retrycode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadwithanimation("fpass");
//                forgotpass();
//            }
//        });
//    }
//
//
//    private void change_password(String email,String code){
//        which_layout=5;
//        TextView confirm = findViewById(R.id.confirm_password_change);
//        TextView login = findViewById(R.id.login);
//        EditText new_password = findViewById(R.id.new_password);
//        EditText confirm_pass_text = findViewById(R.id.confirm_password);
//
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
//                editTextHashs.put(R.string.password, new_password);
//                editTextHashs.put(R.string.confirm_password, confirm_pass_text);
//                if(TextUtils.isEmpty(new_password.getText()) || TextUtils.isEmpty(confirm_pass_text.getText())) {
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
//                            entry.getValue().requestFocus();
//                        }
//                    }
//                }
//                else if (!new_password.getText().toString().equals(confirm_pass_text.getText().toString())){
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(getResources().getString(R.string.password_match_error));
//                            entry.getValue().requestFocus();
//                        }
//                    }
//                }
//                else {
//                    new change_password_request(LoginActivity.this,code,email,new_password.getText().toString()).execute();
//                }
//            }
//        });
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadwithanimation("login");
//                sign_in(LoginActivity.this);
//            }
//        });
//    }
//
//
//    private void activate_account(){
//        which_layout=6;
//        EditText email=findViewById(R.id.tvactivationemail);
//        EditText code= findViewById(R.id.tvactivation_code);
//        TextView activate= findViewById(R.id.activate_acc);
//        TextView create_acc= findViewById(R.id.create_account);
//        CardView layout_code = findViewById(R.id.layout_code);
//        TextView resetacc = findViewById(R.id.resetaccount);
//        resetacc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
//                editTextHashs.put(R.string.email, email);
//                if(TextUtils.isEmpty(email.getText())) {
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
//                            entry.getValue().requestFocus();
//                            Toast.makeText(LoginActivity.this, "Enter the email address to reset code", Toast.LENGTH_SHORT).show();
//                            layout_code.setVisibility(View.GONE);
//                        }
//                    }
//                }
//                else {
//                    new reset_account(LoginActivity.this,email.getText().toString()).execute();
//                }
//            }
//        });
//        create_acc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadwithanimation("signup");
//                sign_up(LoginActivity.this);
//            }
//        });
//
//        activate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                layout_code.setVisibility(View.VISIBLE);
//
//                final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
//                editTextHashs.put(R.string.email, email);
//                editTextHashs.put(R.string.activation_code, code);
//                if(TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(code.getText())) {
//                    for (Map.Entry<Integer, EditText> entry : editTextHashs.entrySet()) {
//                        if(TextUtils.isEmpty(entry.getValue().getText())) {
//                            entry.getValue().setError(getResources().getString(entry.getKey()) + " " + getResources().getString(R.string.field_error));
//                            entry.getValue().requestFocus();
//                        }
//                    }
//                }
//                else {
//                    new activate_account(LoginActivity.this,code.getText().toString(),email.getText().toString()).execute();
//                }
//            }
//        });
//    }
//
//
//    private class sendJson_login  extends AsyncTask<String, String, String>{
//        Activity activity;
//        String email;
//        String password;
//
//
//        public sendJson_login(final Activity _activity, final String _email, final String _password){
//            activity=_activity;
//            email=_email;
//            password=_password;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Login in.....");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//            try {
//                HttpPost post = new HttpPost(new strings_().url()+"/user/login");
//                json.put("email", email);
//                json.put("password",password);
//                StringEntity se = new StringEntity( json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//
//
//                /*Checking response */
//                if(response!=null){
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
////                        decode response to string
//
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, StandardCharsets.ISO_8859_1), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        try {
//                            obj= new JSONObject(result);
//
//                            Log.d(TAG, "run: " + obj.getString("msg"));
//                            message =obj.getString("msg");
////                            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
////                                    .setTitleText(obj.getString("msg"))
////                                    .setContentText("click ok to exit")
////                                    .show();
////                                Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show();
//
//                            pDialog.dismiss();
//
//                        } catch (Throwable t) {
//                            Log.d(TAG, "run: " + obj.getJSONObject("user_data").getString("email"));
//
//                            new Dbhelper(activity).insert_user("",email,obj.getJSONObject("user_data").getString("password"),obj.getJSONObject("user_data").getString("id"));
//                            message ="Login Successfull";
//                            finish();
//                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
////
//
//
//                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//
//                        }
//
//
//                        Log.d(TAG, "run: string "+result);
//
//                    } catch (Exception e) {
////                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
//                        message ="Couldn't communicate with the server";
//                        Log.e("log_tag", "Error converting result login" + e.toString());
//                        Log.d(TAG, "run: error loging result "+result);
//                    }
//
//
//                }
//
//            } catch(Exception e) {
//                e.printStackTrace();
//                message ="Cannot Estabilish Connection";
//                Log.e("log_tag", "Error converting result login" + e.toString());
//
////                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
////                    createDialog("Error", "Cannot Estabilish Connection");
//            }
//            return null;
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//            if (message.equals("Login Successfull")){
//
//            }
//            else{
//                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
//                sweetAlertDialog
//                        .setTitleText(message)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                            }
//                        })
//                        .show();
//                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
//            }
//
//
//        }
//    }
//
//
//    private class sendJson_signup extends AsyncTask<String, String, String> {
//
//        Activity activity;
//        String email;
//        String password;
//        public sendJson_signup(final Activity _activity, final String _email, final String _password){
//            password=_password;
//            activity=_activity;
//            email=_email;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(activity);
//            pDialog.setMessage("Sign up...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//
//            try {
//                HttpPost post = new HttpPost(new strings_().url()+"/user/signup");
//                json.put("email", email);
//                json.put("password",password);
//                StringEntity se = new StringEntity( json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//                /*Checking response */
//                if(response!=null){
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
////                        decode response to string
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, StandardCharsets.ISO_8859_1), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        String TAG="";
//                        try {
//                            obj= new JSONObject(result);
//                            if (obj.length()>2){
////                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
////                                pDialog.dismiss();
//                                Log.d(TAG, "run: " + obj);
//                            }
//                            else {
//                                message ="Email already exists";
//                            }
//                        } catch (Throwable t) {
//                            message ="Couldn't sign up \n\n There was a problem communicating with the servers. \n\n Try again later.";
//                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
////                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                        }
//                        Log.d(TAG, "run: string "+result);
//                    } catch (Exception e) {
//                        Log.e("log_tag", "Error converting result " + e.toString());
//                    }
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//                message ="No Internet Connection";
////                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
////                    createDialog("Error", "Cannot Estabilish Connection");
//            }
//
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//            if(!message.isEmpty()){
//                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
//                sweetAlertDialog
//                        .setTitleText(message)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                            }
//                        })
//                        .show();
//                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
//            }
//            else {
//                Toast.makeText(activity, "Activation Code Sent To Email!", Toast.LENGTH_LONG).show();
//                loadwithanimation("activate_acc");
//                activate_account();
//            }
//
//
//        }
//    }
//
//    private class request_code  extends AsyncTask<String, String, String>{
//        Activity activity;
//        String email;
//        String status="";
//
//        public request_code(final Activity _activity, final String _email){
//            activity=_activity;
//            email=_email;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Requesting code.....");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//            try {
//                HttpPost post = new HttpPost(new strings_().url()+"/password/forgot/email");
//                json.put("email", email);
//                StringEntity se = new StringEntity( json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//                /*Checking response */
//                if(response!=null){
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
////                        decode response to string
//
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, StandardCharsets.ISO_8859_1), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        try {
//                            obj= new JSONObject(result);
//
//                            boolean user=obj.getBoolean("user");
//                            if (user){
//                                status="true";
//                            }
//                            else {
//                                message="Email does not exist!";
//                            }
//                        } catch (Throwable t) {
//                            message ="Couldn't communicate with the server";
//                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//
//                        }
//
//                        Log.d(TAG, "run: string "+result);
//
//                    } catch (Exception e) {
//                        pDialog.dismiss();
////                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
//                        message ="Couldn't communicate with the server";
//                        Log.e("log_tag", "Error converting result login" + e.toString());
//                        Log.d(TAG, "run: error loging result "+result);
//                    }
//
//
//                }
//
//            } catch(Exception e) {
//                e.printStackTrace();
//                message ="Cannot Estabilish Connection";
//                Log.e("log_tag", "Error converting result login" + e.toString());
//
//            }
//
//
//            return null;
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//            if (status.equals("true")){
//                loadwithanimation("confirmrcode");
//                confirmrcode(email);
//            }
//            else{
//                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
//                sweetAlertDialog
//                        .setTitleText(message)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                            }
//                        })
//                        .show();
//                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
//            }
//
//
//        }
//    }
//
//
//    private class confirm_request_code  extends AsyncTask<String, String, String>{
//        Activity activity;
//        String security_code,email;
//        String status="";
//
//        public confirm_request_code(final Activity _activity, final String security_code,String email){
//            activity=_activity;
//            this.security_code=security_code;
//            this.email=email;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Verifying security code.....");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//            try {
//                HttpPost post = new HttpPost(new strings_().url()+"/password/forgot/code");
//                json.put("code", security_code);
//                StringEntity se = new StringEntity( json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//                /*Checking response */
//                if(response!=null){
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
////                        decode response to string
//
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, StandardCharsets.ISO_8859_1), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        try {
//                            obj= new JSONObject(result);
//
//                            boolean code=obj.getBoolean("code");
//                            if (code){
//                                status="true";
//                            }
//                            else {
//                                message="Code is invalid";
//                            }
//                        } catch (Throwable t) {
//                            message ="Code is invalid";
//                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//
//                        }
//                        Log.d(TAG, "run: string "+result);
//
//                    } catch (Exception e) {
//                        pDialog.dismiss();
////                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
//                        message ="Couldn't communicate with the server";
//                        Log.e("log_tag", "Error converting result login" + e.toString());
//                        Log.d(TAG, "run: error loging result "+result);
//                    }
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//                message ="Cannot Estabilish Connection";
//                Log.e("log_tag", "Error converting result login" + e.toString());
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//            if (status.equals("true")){
//                loadwithanimation("change_pass");
//                change_password(email,security_code);
//            }
//            else{
//                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
//                sweetAlertDialog
//                        .setTitleText(message)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                            }
//                        })
//                        .show();
//                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
//            }
//        }
//    }
//
//
//    private class reset_account extends AsyncTask<String, String, String>{
//        Activity activity;
//        String email;
//        String status="";
//
//        public reset_account(final Activity _activity,final String email){
//            activity=_activity;
//            this.email=email;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Resetting activation.....");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//            try {
//                HttpPost post = new HttpPost(new strings_().url()+"/user/dev/reset");
//                json.put("email", email);
//
//                StringEntity se = new StringEntity( json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//                /*Checking response */
//                if(response!=null){
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
////                        decode response to string
//
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, StandardCharsets.ISO_8859_1), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        try {
//                            obj= new JSONObject(result);
//
//                            if(obj.length()>2){
//                                status="true";
//                            }
//
//                        } catch (Throwable t) {
//                            message ="Couldn't communicate with the server";
//                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//
//                        }
//                        Log.d(TAG, "run: string "+result);
//
//                    } catch (Exception e) {
////                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
//                        message ="Couldn't communicate with the server";
//                        Log.e("log_tag", "Error converting result login" + e.toString());
//                        Log.d(TAG, "run: error loging result "+result);
//                    }
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//                message ="Cannot Estabilish Connection";
//                Log.e("log_tag", "Error converting result login" + e.toString());
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//            if (status.equals("true")){
//                loadwithanimation("signup");
//                sign_up(LoginActivity.this);
//            }
//            else{
//                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
//                sweetAlertDialog
//                        .setTitleText(message)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                            }
//                        })
//                        .show();
//                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
//            }
//        }
//    }
//
//    private class activate_account extends AsyncTask<String, String, String>{
//        Activity activity;
//        String security_code,email;
//        String status="";
//
//        public activate_account(final Activity _activity, final String security_code,final String email){
//            activity=_activity;
//            this.security_code=security_code;
//            this.email=email;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Verifying security code.....");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//            try {
//                HttpPost post = new HttpPost(new strings_().url()+"/user/account/activate");
//                json.put("code", security_code);
//                json.put("email", email);
//
//                StringEntity se = new StringEntity( json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//                /*Checking response */
//                if(response!=null){
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
////                        decode response to string
//
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, StandardCharsets.ISO_8859_1), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        try {
//                            obj= new JSONObject(result);
//
//                            message = obj.getString("msg");
//
//                        } catch (Throwable t) {
//                            message ="Couldn't communicate with the server";
//                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//
//                        }
//                        Log.d(TAG, "run: string "+result);
//
//                    } catch (Exception e) {
//                        pDialog.dismiss();
////                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
//                        message ="Couldn't communicate with the server";
//                        Log.e("log_tag", "Error converting result login" + e.toString());
//                        Log.d(TAG, "run: error loging result "+result);
//                    }
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//                message ="Cannot Estabilish Connection";
//                Log.e("log_tag", "Error converting result login" + e.toString());
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
////            if (status.equals("true")){
//                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.NORMAL_TYPE);
//                sweetAlertDialog
//                        .setTitleText(message)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("Login ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                                loadwithanimation("login");
//                                sign_in(LoginActivity.this);
//                            }
//                        })
//                        .show();
//                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
////            }
//////            else{
////                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
////                sweetAlertDialog
////                        .setTitleText(message)
////                        .setContentText("click ok to exit")
////                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
////                            @Override
////                            public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                sweetAlertDialog.dismiss();
////                            }
////                        })
////                        .show();
////                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
////            }
//        }
//    }
//
//
//
//
//    private class change_password_request extends AsyncTask<String, String, String>{
//        Activity activity;
//        String security_code,email,password;
//        String status="";
//
//        public change_password_request(final Activity _activity, final String security_code,final String email, final String password){
//            activity=_activity;
//            this.security_code=security_code;
//            this.email=email;
//            this.password=password;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setMessage("Verifying security code.....");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//            try {
//                HttpPost post = new HttpPost(new strings_().url()+"/password/forgot/change");
//                json.put("code", security_code);
//                json.put("email", email);
//                json.put("password", password);
//
//                StringEntity se = new StringEntity( json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//                /*Checking response */
//                if(response!=null){
////                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
////                        decode response to string
//
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, StandardCharsets.ISO_8859_1), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        try {
//                            obj= new JSONObject(result);
//
//                            if(obj.length()>2){
//                                status="true";
//                            }
//                            else {
//                                message="Security Code is Invalid!";
//                            }
//
//                        } catch (Throwable t) {
//                            message ="Couldn't communicate with the server";
//                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//
//                        }
//                        Log.d(TAG, "run: string "+result);
//
//                    } catch (Exception e) {
//                        pDialog.dismiss();
////                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
//                        message ="Couldn't communicate with the server";
//                        Log.e("log_tag", "Error converting result login" + e.toString());
//                        Log.d(TAG, "run: error loging result "+result);
//                    }
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//                message ="Cannot Estabilish Connection";
//                Log.e("log_tag", "Error converting result login" + e.toString());
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//            if (status.equals("true")){
//                loadwithanimation("login");
//                sign_in(LoginActivity.this);
//            }
//            else{
//                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
//                sweetAlertDialog
//                        .setTitleText(message)
//                        .setContentText("click ok to exit")
//                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismiss();
//                            }
//                        })
//                        .show();
//                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
//            }
//        }
//    }

}