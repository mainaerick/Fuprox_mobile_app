package com.example.currentplacedetailsonmap.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.currentplacedetailsonmap.model.SimpleAlarmManager;
import com.example.currentplacedetailsonmap.model.notification;
import com.example.currentplacedetailsonmap.model.strings_;
import com.example.currentplacedetailsonmap.utils.Dbhelper;
import com.example.currentplacedetailsonmap.utils.booking_details;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.currentplacedetailsonmap.model.notification.notify_;


public class AlarmReceiver extends BroadcastReceiver {

    String CHANNEL_ID="404";
    public  static String TAG="";

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar= Calendar.getInstance();
       notification.createNotificationChannel(context);

        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        SimpleDateFormat dayformat=new SimpleDateFormat("EEEE");

        if(new Dbhelper(context).check_user()!=0){
            new get_all_bookings(context).execute();
        }
        else {
            Log.d(TAG, "alarmreceiver onReceive: No User");
        }
    }

    public void get_booking_notify(Context context){
        Dbhelper dbHelper=new Dbhelper(context);
        Cursor cursor=dbHelper.getWritableDatabase().rawQuery("select * from booking;",null);
        if (cursor.moveToFirst()){
            Calendar calendarnow= Calendar.getInstance();
            do{
                String booking_t_stamp=cursor.getString(cursor.getColumnIndex("time_booked"));
                int id=cursor.getInt(cursor.getColumnIndex("_id"));
                String status=cursor.getString(cursor.getColumnIndex(new Dbhelper(context).booking_active));

                long book_tstamp= Long.parseLong(booking_t_stamp);
                Calendar bookingcalendar=Calendar.getInstance();
                bookingcalendar.setTimeInMillis(book_tstamp);

                if (status.equals("1")){
                    if (new Dbhelper(context).getnotistate().equals("1")){ //notify if notifications is enabled
                        String servicename,branch_id,branchname,booking_id;
                        servicename=cursor.getString(cursor.getColumnIndex(new Dbhelper(context).db_service_name));
                        branch_id=cursor.getString(cursor.getColumnIndex(new Dbhelper(context).db_branch_id));
                        branchname=cursor.getString(cursor.getColumnIndex(new Dbhelper(context).branch_name));
                        booking_id=cursor.getString(cursor.getColumnIndex(new Dbhelper(context).db_booking_id));

                        String content="Reminder for a queue booking";
//                        notify_(context,booking_id,id,content,"19 People in front of you",branchname,servicename);
//                        Context context, int id, String content,String branchname,String servicename,String booking_id
                        new Checkinfront(context,Integer.parseInt(branch_id),content,branchname,servicename,booking_id).execute();
                    }
                }
            }while (cursor.moveToNext());
        }
        dbHelper.close();
    }






    private void setalarm(Context activity){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, new strings_().alaarmwaittime());
        new SimpleAlarmManager(activity)    //set alarm
                .setup(1, calendar.getTimeInMillis())
                .register(new strings_().alarmid())
                .start();

    }
    private class get_all_bookings extends AsyncTask<String, String, String>{

        //        View view;
        Context activity;
        JSONArray jsonarray = null;
        String result_ok=" ";


        public get_all_bookings(Context activity_){
            activity=activity_;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {

//            sendJson_getbookings(activity);


            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();
            StringBuilder sb;
            InputStream is = null;
            String result = null;

            try {
                HttpPost post = new HttpPost(new strings_().url()+"/book/get/user");
                json.put("user_id", new Dbhelper(activity).get_user_id());
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
                                is, "iso-8859-1"), 8);
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
//                                jsonarray = new JSONArray(result);
                            jsonarray=obj.optJSONArray("booking_data");

                            for(int i=0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                String booking_id = jsonobject.getString("id");
                                boolean serviced=jsonobject.getBoolean("serviced");
                                String activeornot;
//                              Log.d(TAG, "run: view all bokings id="+booking_id+ "  branch_id="+branch_id);
                                if (serviced){
                                    activeornot="1";
                                }
                                else {
                                    activeornot="0";
                                }
                                new Dbhelper(activity).updateorder(booking_id,activeornot);
//                                sendJson_getbranch_details(branch_id,activity,timestamp,booking_id,service_name);                   //insert to db,
//
                            }
//                            result_ok="bookings";



                        } catch (Throwable t) {
                            result_ok="no_bookings";
                            Log.e("My App",  "  Could not parse malformed JSON: sendJson_getbookings \"" + t.getMessage()+  "\"   "+ result);

                        }


                    } catch (Exception e) {
                        result_ok="no_bookings";
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }


                }

            } catch(Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (result_ok.equals(" ")){
                Log.d(TAG, "onPostExecute: bookings updated, getbookingnotify called..");
                get_booking_notify(activity);
            }
            setalarm(activity);

        }
    }


    public class Checkinfront extends AsyncTask<String, String, String> {
        Context context;
        int id;
        int infront;
        String people;
        String content,branchname,servicename,booking_id;
        String msg="";
        String error_serv=" ";
        public Checkinfront(Context context, int id, String content, String branchname, String servicename, String booking_id){
            this.context=context;
            this.id=id;
            this.content=content;
            this.branchname=branchname;
            this.servicename=servicename;
            this.booking_id=booking_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                HttpPost post = new HttpPost(new strings_().url()+"/ahead/of/you/id");
                json.put("booking_id", booking_id);
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
                                is, "iso-8859-1"), 8);
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
                            obj= new JSONObject(result);
                            infront=obj.getInt("msg");

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: services offered \"" + result + "\""+t.getMessage());
                            error_serv="There was a problem communicating with the servers. \n\n Try again later.";
                        }

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        error_serv="There was a problem communicating with the servers. \n\n Try again later.";
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
                Log.d(TAG, "run: check infront connection error"+e.toString() );
                error_serv="There was a problem communicating with the servers. \n\n Try again later.";
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (error_serv.equals(" ")){
                if (infront==20){
                    notify_(context,booking_id,id,content,"20 People in front of you",branchname,servicename);
                }
                else if (infront==15){
                    notify_(context,booking_id,id,content,"15 People in front of you",branchname,servicename);
                }
                else if (infront==10){
                    notify_(context,booking_id,id,content,"10 People in front of you",branchname,servicename);
                }
                else if (infront >= 5 && infront != 1){
                    notify_(context,booking_id,id,content,"less than 5 People in front of you",branchname,servicename);
                }
                else if (infront==1){
                    notify_(context,booking_id,id,content,"You are next to be served",branchname,servicename);
                }
                else if (infront==0){
                    new Dbhelper(context).updateorder(String.valueOf(booking_id),"0");
                    notify_(context,booking_id,id,content,"You are currently being served",branchname,servicename);
                }
            }
            else {
                Log.d(TAG, "onPostExecute: check infront"+error_serv);

            }
            Log.d(TAG, "onPostExecute: "+error_serv);
        }
    }

}
