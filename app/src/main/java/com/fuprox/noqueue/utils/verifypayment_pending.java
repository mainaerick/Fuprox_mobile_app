package com.fuprox.noqueue.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fuprox.noqueue.MainActivity;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.Receiver.FloatingWidgetService;
import com.fuprox.noqueue.Receiver.PM_verify_service;
import com.fuprox.noqueue.model.notification;
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
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class verifypayment_pending extends AsyncTask<String, String, String> {
    String msg="null";
    Context activity;
    SharedPreferences prefs;
    String phonenumber,branch_id,start,service_name,user_id,isinstant,company_name,branch_name,token,action,booking_id;
    ProgressDialog pDialog;
    String errorms="";



    public verifypayment_pending(Context activity, String phonenumber,
                                 String branch_id, String start,
                                 String service_name, String user_id,
                                 String isinstant, String company_name,
                                 String branch_name, String token,String action){
        this.activity=activity;
        this.phonenumber = phonenumber;
        this.branch_id = branch_id;
        this.start = start;
        this.service_name = service_name;
        this.user_id = user_id;
        this.isinstant = isinstant;
        this.company_name = company_name;
        this.branch_name = branch_name;
        this.action = action;
        this.token = token;
//        prefs = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
//        phonenumber = prefs.getString("phonenumber", "No token defined");
//        branch_id = prefs.getString("branch_id", "branch_id undefined");
//        start = prefs.getString("start", "start undefined");
//        service_name = prefs.getString("service_name", "sername undefined");
//        user_id = prefs.getString("user_id", "user_id undefined");
//        isinstant = prefs.getString("is_instant", "is_instant undefined");
//        company_name = prefs.getString("company_name", "company undefined");
//        branch_name = prefs.getString("branch_name", "branch undefined");
////        token = prefs.getString("token", "token undefined");
//        token = "11068980e1f1e544cfef";

//            b8741c719336de7e16d5
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!action.equals("new")){
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Verifying Transaction...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
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
//                SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
//                editor.putString("start", String.valueOf(booking_milis));
//                editor.putString("user_id", new Dbhelper(activity).get_user_id());
//                editor.putString("branch_id", b_id);
//                editor.putString("service_name", service_name);
//                editor.putString("is_instant", isinstant);
//                editor.putString("phonenumber", phonenumber);
//                editor.putString("token", obj.getString("token"));
//                editor.putString("company_name", txtcompanyname.getText().toString());
//                editor.putString("branch_name",txtbranch.getText().toString());
            HttpPost post;
            if (token.equals("null")){
                post = new HttpPost(new strings_().get_ipaddress(activity)+"/verify/payment/number");
                Log.e(TAG, "Verify_payment by number: ");

            }
            else {

                post = new HttpPost(new strings_().get_ipaddress(activity)+"/verify/payment");
                Log.e(TAG, "Verify_payment by token: ");

            }
            json.put("branch_id", Integer.parseInt(branch_id.trim()));
            json.put("start", start);
            json.put("service_name",service_name);
            json.put("user_id",Integer.parseInt(user_id.trim()));
            json.put("is_instant",isinstant);
            json.put("phonenumber",phonenumber);
            json.put("token",token);
            int amount=5;
            if (isinstant.length()>=1){
                amount = 10;
            }
            else {
                amount = 5;
            }
            Log.d(TAG, "doInBackgrZound: "+amount);
            json.put("amount", amount);
//            json.put("token", "11068980e1f1e544cfef");// remove after tested successfully

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

//                  CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
                    JSONObject obj = null;
                    try {
                        obj= new JSONObject(result);
                        Log.d(TAG, "doInBackground: "+obj);
//                            JSONObject new_obj = obj.getJSONObject("msg");
                        if (obj.length()>2){
                            msg="Payment Successful";
                            String booking_id=obj.getString("id");
                            this.booking_id=booking_id;
                            if (action.equals("new")){
                                insert_in_db(company_name,branch_name,booking_id,branch_id,service_name,"1"); //put into db
                                create_bubble(booking_id);
                            }
                            else {
                                new Dbhelper(activity).update_pending_order(token+"_"+phonenumber,booking_id);
                            }
                        }
                        else {
                            msg="Payment Unsuccessful";
                        }
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: verify payment \"" + result + "\""+t.getMessage());
                        errorms=t.toString();
                    }
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result verify payment " + e.toString());
                    errorms=e.toString();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            Log.d(TAG, "run: book connection error verify payment" );
            errorms=e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!action.equals("new")){
            pDialog.dismiss();
        }
        prefs = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
        if (msg.equals("null")){
            if (prefs.getAll().size()==0 && action.equals("new")){ //   shared preferences are empty but service was called
                notification.createNotificationChannel(activity);
                notification.notify_payment_status(activity,1221,"Transaction Verification Failed ","Please open the app and try again.","");
            }
            else { //   failed to connection problems
                if (!action.equals("new")){
                   SweetAlertDialog sweetAlertDialog= new SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog
                            .setTitleText("Transaction verification Failed")
                            .setContentText("Please try again later.")
//                            .setContentText(errorms)
                            .show();
                    sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
                }
                else {
                    notification.createNotificationChannel(activity);
                    notification.notify_payment_status(activity,1221,"Transaction Verification Failed ","Please open the app and try again.","");
                    insert_in_db(company_name,branch_name,token+"_"+phonenumber,branch_id,service_name,isinstant);
                }
            }
        }
        else {  //  backend was hit
            if (msg.equals("Payment Successful")){  //  and payment was successful
                create_bubble(booking_id);
                notification.createNotificationChannel(activity);
                notification.notify_payment_status(activity,1221,"Successful Payment","You have Successfully created a queue booking in "+branch_name,booking_id);
                Toast.makeText(activity, "Booking made Successfully", Toast.LENGTH_SHORT).show();

                if (!action.equals("new")){
//                    BottomNavigationView navView;
//                    navView= new MainActivity().findViewById(R.id.nav_view);
//                    navView.setSelectedItemId(R.id.navigation_order);
                }
                else {
                    clear_prefs(prefs);

                    Intent intent1 = new Intent(activity, MainActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("book_id", booking_id);
                    bundle.putString("verify","verify");
                    intent1.putExtras(bundle);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent1);
                }
            }
            else {  //  payment was unsuccessful
                if (!action.equals("new")){
                    SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog
                            .setTitleText("Payment verification failed")
                            .setContentText("Please try again later")
                            .show();
                    sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));

                }
                else {
                    notification.createNotificationChannel(activity);
                    notification.notify_payment_status(activity,1221,"Transaction Verification Failed ","Please open the app and try again.","");
                    insert_in_db(company_name,branch_name,token+"_"+phonenumber,branch_id,service_name,isinstant);
                }
            }
        }
        clear_prefs(prefs);
        activity.stopService(new Intent(activity,PM_verify_service.class));
    }

    private void create_bubble(String booking_id){
        Intent intent1 = new Intent(activity, FloatingWidgetService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("activity_background", true);
        bundle.putString("booking_id",booking_id);
        intent1.putExtras(bundle);
        activity.startService(intent1);
    }

    private void clear_prefs(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
    private void insert_in_db(String company_name,String branch_name,String booking_id, String branch_id,String service_name,String serviced){
        //bookingid=token+number serviced=instant_is
        booking_details bookingDetails=new booking_details();
        Calendar book_milis=Calendar.getInstance();
        long booking_milis=book_milis.getTimeInMillis();
        bookingDetails.setCo_name(company_name);
        bookingDetails.setB_name(branch_name);
        bookingDetails.setTime(String.valueOf(booking_milis));
        bookingDetails.setBooking_id(booking_id);
        bookingDetails.setBranch_id(branch_id);
        bookingDetails.setService_name(service_name);
        bookingDetails.setServiced(serviced);
        new Dbhelper(activity).insert_booking(bookingDetails);
    }


}
