package com.example.currentplacedetailsonmap.Receiver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.model.notification;
import com.example.currentplacedetailsonmap.model.strings_;
import com.example.currentplacedetailsonmap.utils.Dbhelper;
import com.example.currentplacedetailsonmap.utils.booking_details;
import com.example.currentplacedetailsonmap.utils.verifypayment_pending;

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
import java.util.Calendar;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PM_verify_service extends Service {
    SharedPreferences prefs;
    String phonenumber,branch_id,start,service_name,user_id,isinstant,company_name,branch_name,token;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prefs = getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
        phonenumber = prefs.getString("phonenumber", "No token defined");
        branch_id = prefs.getString("branch_id", "branch_id undefined");
        start = prefs.getString("start", "start undefined");
        service_name = prefs.getString("service_name", "sername undefined");
        user_id = prefs.getString("user_id", "user_id undefined");
        isinstant = prefs.getString("is_instant", "is_instant undefined");
        company_name = prefs.getString("company_name", "company undefined");
        branch_name = prefs.getString("branch_name", "branch undefined");
        token = prefs.getString("token", "token undefined");
//        token = "11068980e1f1e544cfef";

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(PM_verify_service.this, ""+isinstant, Toast.LENGTH_SHORT).show();
                new verifypayment_pending(PM_verify_service.this,phonenumber,branch_id,start,service_name,user_id,isinstant,company_name,branch_name,token,"new").execute();
//                new verifypayment_pending().execute();
                Log.d(TAG, "run: paymeent verification process started ");
//                Toast.makeText(PM_verify_service.this, "Service started by user."+service_name, Toast.LENGTH_LONG).show();
            }
        }, 50000);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
//    private class verify_payment extends AsyncTask<String, String, String> {
//        String msg="null";
//        public verify_payment(){
//
////            b8741c719336de7e16d5
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//
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
//            try {
////                SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
////                editor.putString("start", String.valueOf(booking_milis));
////                editor.putString("user_id", new Dbhelper(activity).get_user_id());
////                editor.putString("branch_id", b_id);
////                editor.putString("service_name", service_name);
////                editor.putString("is_instant", isinstant);
////                editor.putString("phonenumber", phonenumber);
////                editor.putString("token", obj.getString("token"));
////                editor.putString("company_name", txtcompanyname.getText().toString());
////                editor.putString("branch_name",txtbranch.getText().toString());
//
//                HttpPost post = new HttpPost(new strings_().url()+"/verify/payment");
//                json.put("branch_id", Integer.parseInt(branch_id));
//                json.put("start", start);
//                json.put("service_name",service_name);
//                json.put("user_id",Integer.parseInt(user_id));
//                json.put("is_instant",isinstant);
//                json.put("phonenumber",phonenumber);
////                json.put("token",token);
//                json.put("token", token);// remove after tested successfully
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
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//                        JSONObject obj = null;
//                        JSONArray jsonarray;
//                        try {
//                            obj= new JSONObject(result);
////                            JSONObject new_obj = obj.getJSONObject("msg");
//
//                            if (obj.length()>2){
//                                msg="Payment Successful";
//                                String booking_id=obj.getString("id");
//                                insert_in_db(company_name,branch_name,booking_id,branch_id,service_name,"0"); //put into db
//                            }
//                            else {
//                                msg="Payment Unsuccessful";
//                            }
//
//                        } catch (Throwable t) {
//                            Log.e("My App", "Could not parse malformed JSON: services offered \"" + result + "\""+t.getMessage()+ " \n pref"+prefs.getAll());
//                        }
//
//                    } catch (Exception e) {
//                        Log.e("log_tag", "Error converting result " + e.toString());
//                    }
//
//                }
//
//            } catch(Exception e) {
//                e.printStackTrace();
//                Log.d(TAG, "run: book connection error" );
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if (msg.equals("null")){
//                prefs = getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
//                if (prefs.getAll().size()==0){ //   shared preferences are empty but service was called
//                    notification.createNotificationChannel(PM_verify_service.this);
//                    notification.notify_payment_status(PM_verify_service.this,1221,"Booking Payment Unsuccessful","Transaction could not complete");
//                }
//                else { //   failed to connection problems
//                    notification.createNotificationChannel(PM_verify_service.this);
//                    notification.notify_payment_status(PM_verify_service.this,1221,"Payment Verification Failed","Due to Network Connection,Open app to retry");
//                    insert_in_db(company_name,branch_name,"pending",branch_id,service_name,token);
//                }
//            }
//            else {  //  backend was hit
//                if (msg.equals("Payment Successful")){  //  and payment was successful
//                    notification.createNotificationChannel(PM_verify_service.this);
//                    notification.notify_payment_status(PM_verify_service.this,1221,"Booking Payment Successful","You have Successfully booked");
//                    Toast.makeText(PM_verify_service.this, "Booking made Successfully", Toast.LENGTH_SHORT).show();
//                }
//                else {  //  payment was unsuccessful
//                    notification.createNotificationChannel(PM_verify_service.this);
//                    notification.notify_payment_status(PM_verify_service.this,1221,"NoQueue Payment Unsuccessful", "Payment could not be processed");
//                }
//            }
//            Log.d(TAG, "onPostExecute: shared preference payment "+prefs.getAll()+ "Response ");
//
//            clear_prefs(prefs);
//
//            stopService(new Intent(PM_verify_service.this,PM_verify_service.class));
//        }
//    }

    private void clear_prefs(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
    private void insert_in_db(String company_name,String branch_name,String booking_id, String branch_id,String service_name,String serviced){
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
        new Dbhelper(PM_verify_service.this).insert_booking(bookingDetails);
    }
}
