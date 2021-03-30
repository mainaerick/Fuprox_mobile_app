package com.fuprox.noqueue.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;

import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.utils.Dbhelper;
import com.fuprox.noqueue.utils.booking_details;
import com.fuprox.noqueue.utils.services_offered_details;

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
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;



public class alertdialoghelper {

    private static String signup_message,error_serv=" ";
    static ProgressDialog pDialog;
    ArrayList<services_offered_details> servicesOfferedDetailsArrayList;
    ArrayList<String> list_sername;

    public  static String TAG="";
    public static String getCountryZipCode(Activity activity){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=activity.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }


    public static void add_number(final Activity activity, final View alertLayout, final TextView shownumber){
        final Button rmbutton=alertLayout.findViewById(R.id.btn_remove_no);
        final EditText number = alertLayout.findViewById(R.id.editnumber);
        final TextView showcontrycode=alertLayout.findViewById(R.id.countrycode);
//        showcontrycode.setText(getCountryZipCode(activity));
        showcontrycode.setText("254");
        number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Phone Number");
        alert.setCancelable(true);
        final Button cancel = alertLayout.findViewById(R.id.btn_cancel);
        final Button save = alertLayout.findViewById(R.id.btn_save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

//        number.setText(showcontrycode.getText().toString());

        if (shownumber.getText().toString().length()>3){
            rmbutton.setVisibility(View.VISIBLE);
            number.setText(shownumber.getText().toString().substring(1));
            rmbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Dbhelper(activity).updateuser_no("0");
                    dialog.dismiss();
                    new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Number removed!")
                            .setContentText("click ok to exit")
                            .show();
                    shownumber.setText("");
                }
            });
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strnumber=number.getText().toString();

                if (number.getText().toString().length()==9)
                {
                    if (number.getText().toString().startsWith("0")){
                        Toast.makeText(activity, "Check your number and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        strnumber=showcontrycode.getText().toString()+number.getText().toString();

                        new Dbhelper(activity).updateuser_no(strnumber);

                        shownumber.setText(new Dbhelper(activity).getuser_no());


                        dialog.dismiss();
                    }
                }
                else if (number.getText().toString().length()<9){
                    Toast.makeText(activity, "Check your number and try again", Toast.LENGTH_SHORT).show();
                }




            }
        });
    }
    public static void number_input(final Activity activity, final View alertLayout, final TextView shownumber, final TextView showinfo){
        final EditText number = alertLayout.findViewById(R.id.editnumber);
        number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        final TextView showcontrycode=alertLayout.findViewById(R.id.countrycode);
        showcontrycode.setText("254");
//        showcontrycode.setText(getCountryZipCode(activity));
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Phone Number");
        alert.setCancelable(true);

        final Button cancel = alertLayout.findViewById(R.id.btn_cancel);
        final Button save = alertLayout.findViewById(R.id.btn_save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strnumber=number.getText().toString();

                if (number.getText().toString().length()==9)
                {
                    if (number.getText().toString().startsWith("0")){
                        Toast.makeText(activity, "Check your number and try again", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        shownumber.setText(showcontrycode.getText().toString()+strnumber);
                        Drawable img = activity.getResources().getDrawable(R.drawable.ic_close_black_24dp);
                        shownumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                        shownumber.setVisibility(View.VISIBLE);
//                        continue_book.setText("Confirm "+continue_book.getText().toString().toLowerCase().substring(0,continue_book.getText().toString().toLowerCase().indexOf(" ")));

//                        if (booktype.equals("normal")){
//                            showinfo.setText("Normal booking selected confirm to pay "+activity.getResources().getString(R.string.normal_amount));
//                        }
//                        else {
//                            showinfo.setText("Instant booking selected confirm to pay "+activity.getResources().getString(R.string.instant_amount));
//
//                        }
                        dialog.dismiss();
                    }
                }
                else if (number.getText().toString().length()<9){
                    Toast.makeText(activity, "Check your number and try again", Toast.LENGTH_SHORT).show();
                }


//                if (strnumber.startsWith("0")){
//                    strnumber.replaceFirst("0",showcontrycode.getText().toString());
//                    number.getText().replace(0,1,"");
//
//                }
//                else {
//                    strnumber=showcontrycode.getText().toString()+strnumber;
//                }
//                if (number.getText().toString().length()<9){
//                    Toast.makeText(activity, "Phone number is invalid", Toast.LENGTH_SHORT).show();
//                }else {
//                    shownumber.setText(showcontrycode.getText().toString()+strnumber);
//                    Drawable img = activity.getResources().getDrawable(R.drawable.ic_dialog_close_dark);
//                    shownumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
//                    continue_book.setText("Continue to "+continue_book.getText().toString().toLowerCase());
//
//                    dialog.dismiss();
//                }
//

            }
        });
    }
    public static void editDialog(final Activity activity, final View alertLayout,String get_text_name, String get_text_email, final TextView textView_name,final TextView textView_email) {

        final EditText text_name = alertLayout.findViewById(R.id.edittext_account_name);
        final EditText text_email = alertLayout.findViewById(R.id.edittext_account_email);
        text_name.setText(get_text_name);
        text_email.setText(get_text_email);


        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("EDIT");
        alert.setCancelable(true);
        final Button cancel = alertLayout.findViewById(R.id.btn_cancel);
        final Button save = alertLayout.findViewById(R.id.btn_save);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_name.setText(text_name.getText());
                textView_email.setText(text_email.getText());
                dialog.dismiss();

            }
        });


    }

    public void select_service(final Context activity, final View alertLayout, String bid) {

        new get_services_offered(alertLayout,activity,bid).execute();
        ListView listView=alertLayout.findViewById(R.id.listview_service_offered);


        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("EDIT");
        alert.setCancelable(true);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(activity, ""+position, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });



    }

    public static void sign_up(final Activity activity, final View alertLayout, final FragmentActivity fragmentActivity) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();

        final EditText text_password = alertLayout.findViewById(R.id.signup_password);
        editTextHashs.put(R.string.password, text_password);
        final EditText text_email = alertLayout.findViewById(R.id.signup_email);
        editTextHashs.put(R.string.email, text_email);


        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Create Account");
        alert.setCancelable(true);
        final TextView save = alertLayout.findViewById(R.id.confirm_signup);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

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
//                    sendJson_signup(activity,text_email.getText().toString(),text_password.getText().toString(),fragmentActivity);

                    dialog.dismiss();
                }




            }
        });


    }
    public static void sign_in(final Activity activity, final View alertLayout,final FragmentActivity fragmentActivity) {
        final HashMap<Integer, EditText> editTextHashs = new HashMap<>();
        final EditText text_email = alertLayout.findViewById(R.id.signin_email);
        editTextHashs.put(R.string.email, text_email);
        final EditText text_password = alertLayout.findViewById(R.id.signin_password);
        editTextHashs.put(R.string.password, text_password);

        TextView create_acc=alertLayout.findViewById(R.id.create_account_from_dialog);

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Sign In");

        alert.setCancelable(true);
        final TextView save = alertLayout.findViewById(R.id.confirm_signin);
        alert.setView(alertLayout);
        final AlertDialog dialog = alert.create();
        dialog.show();

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
                else {
                    sendJson_login(activity,text_email.getText().toString(),text_password.getText().toString(),fragmentActivity);

//                new Dbhelper(activity).insert_user("",text_email.getText().toString(),text_password.getText().toString());
                    dialog.dismiss();
                }



            }
        });

        create_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View alertLayout = activity.getLayoutInflater().inflate(R.layout.layout_signup, null);

                sign_up(activity,alertLayout,fragmentActivity);
                dialog.dismiss();
            }
        });



    }

//    private static void sendJson_signup(final Activity activity, final String email, final String password, final FragmentActivity fragmentActivity) {
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
//                                    loadFragment(fragmentActivity);
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

//    private static void loadFragment(FragmentActivity activity) {
//
//            activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, new account_fragment(activity))
//                    .commit();
//
//    }



    private static void sendJson_login(final Activity activity, final String email, final String password, final FragmentActivity fragmentActivity){
        final Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();
                StringBuilder sb;
                InputStream is = null;
                String result = null;
                pDialog = new ProgressDialog(activity);
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                try {
                    HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/user/login");
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
                                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(obj.getString("msg"))
                                        .setContentText("click ok to exit")
                                        .show();
//                                Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show();

                                pDialog.dismiss();

                            } catch (Throwable t) {
                                Log.d(TAG, "run: " + obj.getJSONObject("user_data").getString("email"));

                                new Dbhelper(activity).insert_user("",email,obj.getJSONObject("user_data").getString("password"),obj.getJSONObject("user_data").getString("id"));
                                signup_message="Login Successfull";
                                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Login successful")
                                        .setContentText("click ok to exit")
                                        .show();
                                Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
//                                loadFragment(fragmentActivity);
                                pDialog.dismiss();


                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());

//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                            }


                            Log.d(TAG, "run: string "+result);

                        } catch (Exception e) {
                            pDialog.dismiss();
//                            Toast.makeText(activity, "No users created yet", Toast.LENGTH_SHORT).show();
                            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Connection error")
                                    .setContentText("click ok to exit")
                                    .show();
                            Log.e("log_tag", "Error converting result login" + e.toString());
                            Log.d(TAG, "run: error loging result "+result);
                        }


                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    signup_message="Cannot Estabilish Connection";
                    Toast.makeText(activity, signup_message, Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
//                    createDialog("Error", "Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }




    private class get_services_offered extends AsyncTask<String, String, String> {
        View alertLayout;
        Context context;
        String b_id;

        public get_services_offered(View alertLayout,Context context,String b_id){
            this.context=context;
            this.b_id=b_id;
            this.alertLayout=alertLayout;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage(" ");
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(context)+"/services/get/all");
                json.put("branch_id", b_id);
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
//                            obj= new JSONObject(result);
                            jsonarray = new JSONArray(result);

//                                InputStream in = response.getEntity().getContent(); //Get the data in the entity
//                                new SimpleAlarmManager(activity)
//                                        .setup(1,booking_milis)
//                                        .register(position)
//                                        .start();


                            String name = null;
                            servicesOfferedDetailsArrayList = new ArrayList<>();
                            list_sername=new ArrayList<String>();
                            int i=0;
                            while (i<jsonarray.length()) {


                                services_offered_details services_offered_details = new services_offered_details();
                                obj = new JSONObject(jsonarray.getString(i));
                                services_offered_details.setService_code(obj.getString("code"));
                                services_offered_details.setService_id(obj.getString("id"));
                                services_offered_details.setService_name(obj.getString("name"));
                                services_offered_details.setService_teller(obj.getString("teller"));
                                servicesOfferedDetailsArrayList.add(services_offered_details);
                                list_sername.add(obj.getString("name"));
                                i++;
                            }

///

//

//                                pDialog.dismiss();
//                                Toast.makeText(activity, "Book made...", Toast.LENGTH_SHORT).show();

////
                            Log.d(TAG, "services ordered by branch id="+b_id+"---->  "+ jsonarray);



                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: services offered \"" + result + "\""+t.getMessage());
                            error_serv="There was a problem communicating with the servers. \n\n Try again later.";
//                                pDialog.dismiss();

//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                        }


//                            Log.d(TAG, "run: string" + result+new Dbhelper(activity).get_user_id());

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        error_serv="There was a problem communicating with the servers. \n\n Try again later.";
//                            pDialog.dismiss();

                    }



                }

            } catch(Exception e) {
                e.printStackTrace();
//                    Toast.makeText(getActivity(), "Problem Establishing Connection", Toast.LENGTH_SHORT).show();
//                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
//                            .setTitleText("There was a problem communicating with the servers.")
//                            .setContentText("Try again later.")
//                            .show();
                Log.d(TAG, "run: book connection error" );
                error_serv="There was a problem communicating with the servers. \n\n Try again later.";
//                pDialog.dismiss();
//                    pDialog.cancel();
//                    createDialog("Error", "Cannot Estabilish Connection");
            }





            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (error_serv.equals(" ")){
                ListView listView=alertLayout.findViewById(R.id.listview_service_offered);
                ArrayAdapter arrayAdapter=new ArrayAdapter(context,R.layout.spinner_services_items,R.id.textview_services,list_sername);

                listView.setAdapter(arrayAdapter);
            }
            else {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sorry, services could not be loaded")
                        .setContentText("click ok to exit")
                        .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
//                dismiss();
            }

            pDialog.dismiss();
        }
    }


}
