package com.fuprox.noqueue.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.Receiver.FloatingWidgetService;
import com.fuprox.noqueue.Receiver.PM_verify_service;
import com.fuprox.noqueue.fragment.fragment_oder_more_details;
import com.fuprox.noqueue.model.notification;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.Dbhelper;
import com.fuprox.noqueue.utils.booking_details;
import com.google.android.material.bottomnavigation.BottomNavigationView;


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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;


public class orderadapter extends ArrayAdapter<booking_details> {
    public  static String TAG="";

    private Activity mActivity;
    private int mResource;
    private ArrayList<booking_details> orders_list;
    private ListView mListView;
    private static ProgressDialog pDialog;
    private int lastPosition = -1;


    private static class ViewHolder {
        TextView title, txtvbranch, txtvtime, tvticket,tvstatus;
        ImageView popup;
        CardView cardView;
    }

    public orderadapter(Activity activity, ListView listView, int resource, ArrayList<booking_details> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mListView = listView;
        mResource = resource;
        orders_list = objects;

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        final String name = Objects.requireNonNull(getItem(position)).getCo_name();
        final String branch_name_obj = Objects.requireNonNull(getItem(position)).getB_name();
        final String time_obj = Objects.requireNonNull(getItem(position)).getTime();
        final String id = Objects.requireNonNull(getItem(position)).getBooking_id();
        String branch_id = Objects.requireNonNull(getItem(position)).getBranch_id();
        String service_name= Objects.requireNonNull(getItem(position)).getService_name();
        String serviced= Objects.requireNonNull(getItem(position)).getServiced();
        final String _id = String.valueOf(Objects.requireNonNull(getItem(position)).getId());

//        Toast.makeText(mActivity, ""+service_name, Toast.LENGTH_SHORT).show();
        com.fuprox.noqueue.utils.booking_details booking_details = new booking_details(name, branch_name_obj, time_obj, id, branch_id, service_name, serviced);


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.titlenote);
            holder.txtvbranch = convertView.findViewById(R.id.order_detail);
            holder.txtvtime = convertView.findViewById(R.id.booking_time);
            holder.popup = convertView.findViewById(R.id.popupbtn);
            holder.cardView = convertView.findViewById(R.id.orders_cardview);
            holder.tvstatus= convertView.findViewById(R.id.book_active);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String book_info_coname = booking_details.getCo_name();
        String book_info_branch = booking_details.getB_name();
        String book_info_time = booking_details.getTime();
        final String book_info_branch_id = booking_details.getBranch_id();

        long booking_t_stamp = Long.parseLong(book_info_time);

//        time format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String time_string = simpleDateFormat.format(booking_t_stamp);

//        date format
        SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat("dd/MM/yyyy");
        String date_string = simpleDateFormat_date.format(booking_t_stamp);


//        check if it is today/tommorrow/yesterday

        Calendar today_calendar = Calendar.getInstance();
        Calendar yester_calendar = Calendar.getInstance();
        Calendar tommo_calendar = Calendar.getInstance();
        Calendar db_mili = Calendar.getInstance();
        int today, yesterday, tommorrow;
        today = today_calendar.get(Calendar.DAY_OF_WEEK);
        yester_calendar.add(Calendar.DAY_OF_WEEK, -1);
        yesterday = yester_calendar.get(Calendar.DAY_OF_WEEK);
        tommo_calendar.add(Calendar.DAY_OF_WEEK, 1);

        tommorrow = tommo_calendar.get(Calendar.DAY_OF_WEEK);

        db_mili.setTimeInMillis(booking_t_stamp);

        if (db_mili.get(Calendar.DAY_OF_WEEK) == today) {
            holder.txtvtime.setText("Booked : Today"+" "+time_string);
        } else if (db_mili.get(Calendar.DAY_OF_WEEK) == yesterday) {
            holder.txtvtime.setText("Booked : Yesterday"+" "+time_string);
        } else if (db_mili.get(Calendar.DAY_OF_WEEK) == tommorrow) {
            holder.txtvtime.setText("Booked for : Tommorrow"+" "+time_string);
        } else {
            holder.txtvtime.setText("Booked : " + date_string);
        }


        holder.title.setText(book_info_coname);
        holder.txtvbranch.setText(book_info_branch);

        if (serviced.equals("1")){//not serviced
            holder.tvstatus.setText("Ticket Active");

        }
        else if (serviced.equals("0")){//serviced
            holder.tvstatus.setText("Ticket Served");
        }
        else {
            holder.tvstatus.setText("Payment Pending");
        }

//        holder.cardView.setCardBackgroundColor(note.getColor());

        View finalConvertView = convertView;
        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(mActivity, holder.popup);
                popup.getMenuInflater().inflate(R.menu.ordermenu, popup.getMenu());
                if (serviced.equals("0")||serviced.equals("1")){
                    popup.getMenu().getItem(0).setVisible(false);
                }
                else {
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.float_bubble:

                                Intent intent1 = new Intent(mActivity, FloatingWidgetService.class);

                                Bundle bundle = new Bundle();
                                bundle.putBoolean("activity_background", true);
                                bundle.putString("booking_id",id);
                                intent1.putExtras(bundle);
                                mActivity.startService(intent1);
                                return  true;

                            case R.id.remove_pending:
                                new Dbhelper(mActivity).delete_booking(_id);
                                orders_list.remove(position);
                                notifyDataSetChanged();
//                                Snackbar.make(finalConvertView,"Booking Removed",Snackbar.LENGTH_SHORT).show();
                                Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                                finalConvertView.startAnimation(animation);
                                lastPosition = position;
                                return true;
                            case R.id.verify_payment:
                                String phone_number = id.substring(id.indexOf("_")+1);
                                String token = id.substring(0,id.indexOf("_"));
                                Log.d(TAG, "onMenuItemClick: "+"phone--"+phone_number+"  booking_id--"+id);
                                new verifypayment_pending_(mActivity,phone_number,branch_id,book_info_time,service_name,new Dbhelper(mActivity).get_user_id(),
                                        serviced,book_info_coname,book_info_branch,token,"update").execute();
                                Log.d(TAG, "onMenuItemClick: "+phone_number+" , "+branch_id+" , "+book_info_time+" , "+service_name+" , "+new Dbhelper(mActivity).get_user_id()
                                        +" , "+ serviced+" , "+book_info_coname+" , "+book_info_branch+" , "+token);
                                return true;
                            case R.id.menu1:    // get Direction

                                new branch_get(book_info_branch_id).execute();
                                return true;

                            case R.id.menu3:// generate ticket
//                                Toast.makeText(mActivity, ""+id, Toast.LENGTH_SHORT).show();

//                                bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment(sname,saddress,sbranch);
                                fragment_oder_more_details fragmentOderMoreDetails=new fragment_oder_more_details(id,new Dbhelper(mActivity).get_user_id());
                                FragmentManager manager = ((AppCompatActivity)mActivity).getSupportFragmentManager();
                                fragmentOderMoreDetails.show(manager,fragmentOderMoreDetails.getTag());
//                                bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
                                return true;
                            default:
                                return onMenuItemClick(item);
                        }
                    }
                });
                popup.show();
            }
        });

        hidePopUpMenu(holder);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<booking_details> getNoteList() {
        return orders_list;
    }

//    public Note getNote() {
//        return note;
//    }

     private void hidePopUpMenu(ViewHolder holder) {
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
//        if (checkedItems.size() > 0) {
//            for (int i = 0; i < checkedItems.size(); i++) {
//                int key = checkedItems.keyAt(i);
//                if (checkedItems.get(key)) {
//                    holder.popup.setVisibility(View.INVISIBLE);
//                    }
//            }
//        } else {
//            holder.popup.setVisibility(View.VISIBLE);
//        }
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = getNoteList().size();
                    filterResults.values = getNoteList();

                }else{
                    ArrayList<booking_details> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(booking_details itemsModel:getNoteList()){
                        if(itemsModel.getB_name().contains(searchStr) || itemsModel.getCo_name().contains(searchStr)){
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                 orders_list= (ArrayList<booking_details>) results.values;
                notifyDataSetChanged();

            }
        };
        return filter;
    }



    private class branch_get extends AsyncTask<String, String, String>{

        String branch_id;

        public branch_get(String branch_id){
            this.branch_id=branch_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Getting location info......");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(mActivity)+"/branch/get/single");
                json.put("branch_id", branch_id);
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
                        JSONArray jsonarray;
                        try {
                            obj= new JSONObject(result);
                            String longi = obj.getString("longitude"),lati = obj.getString("latitude");
                            double dlo= Double.parseDouble(longi),dla= Double.parseDouble(lati);

                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+ dla+","+ dlo);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            getContext().startActivity(mapIntent);

                            Log.d(TAG, "run: getting branch id "+ obj);


                        } catch (Throwable t) {
                            Log.d(TAG, "run: getting branch id company "+ result);

                            Log.e("My App", "get branches for booking  " +t.getMessage());

//                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
                        }


//                            Log.d(TAG, "run: string" + result);

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }


                }

            } catch(Exception e) {
                e.printStackTrace();

                //                    createDialog("Error", "Cannot Estabilish Connection");
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

        }
    }




    public class verifypayment_pending_ extends AsyncTask<String, String, String> {
        String msg="null";
        Context activity;
        SharedPreferences prefs;
        String phonenumber,branch_id,start,service_name,user_id,isinstant,company_name,branch_name,token,action,booking_id;
        ProgressDialog pDialog;
        String errorms="";

        public verifypayment_pending_(Context activity, String phonenumber,
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
                pDialog.setMessage("Verifying payment...");
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

                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/verify/payment");
                json.put("branch_id", Integer.parseInt(branch_id.trim()));
                json.put("start", start);
                json.put("service_name",service_name);
                json.put("user_id",Integer.parseInt(user_id.trim()));
                json.put("is_instant",isinstant);
                json.put("phonenumber",phonenumber);
                json.put("token",token);
                int amount=5;
                if (isinstant.equals("1")){
                    amount = 10;
                }
                else {
                    amount = 5;
                }
                Log.d(TAG, "doInBackground: "+amount);
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
                                new Dbhelper(activity).update_pending_order(token+"_"+phonenumber,booking_id);

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
            Log.e(TAG, "doInBackground: "+token);
            if (!action.equals("new")){
                pDialog.dismiss();
            }
            prefs = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
            if (msg.equals("null")){
                if (prefs.getAll().size()==0 && action.equals("new")){ //   shared preferences are empty but service was called
                    notification.createNotificationChannel(activity);
                    notification.notify_payment_status(activity,1221,"Payment Unsuccessful","Transaction could not complete","");
                }
                else { //   failed to connection problems

                        SweetAlertDialog sweetAlertDialog= new SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog
                                .setTitleText("Cannot establish connection")
                                .setContentText("click to exit")
//                            .setContentText(errorms)
                                .show();
                        sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
                }
            }
            else {  //  backend was hit
                if (msg.equals("Payment Successful")){  //  and payment was successful
                    create_bubble(booking_id);
                    notification.createNotificationChannel(activity);
                    notification.notify_payment_status(activity,1221,"Payment Successful","You have Successfully booked for a queue in "+branch_name,booking_id);
                    Toast.makeText(activity, "Booking made Successfully", Toast.LENGTH_SHORT).show();

                }
                else {  //  payment was unsuccessful

                        SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog
                                .setTitleText("Payment verification failed")
                                .setContentText("click to exit")
                                .show();
                        sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));

                }
            }
            clear_prefs(prefs);
            activity.stopService(new Intent(activity, PM_verify_service.class));

            Log.d(TAG, "onPostExecute:  token= "+token);
            BottomNavigationView navView;
            navView= mActivity.findViewById(R.id.nav_view);
            navView.setSelectedItemId(R.id.navigation_order);

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
            editor.apply();
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










//    private static void sendJson_getbranch_details(final String branch_id) {
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
//
//                try {
//                    HttpPost post = new HttpPost(new strings_().url()+"/branch/get/single");
//                    json.put("branch_id", branch_id);
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
//                            JSONArray jsonarray;
//                            try {
//                                obj= new JSONObject(result);
//
//
//
//                                Log.d(TAG, "run: getting branch id "+ obj);
//
//
//                            } catch (Throwable t) {
//                                Log.d(TAG, "run: getting branch id company "+ result);
//
//                                Log.e("My App", "get branches for booking  " +t.getMessage());
//
////                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                            }
//
//
////                            Log.d(TAG, "run: string" + result);
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
//
//                    //                    createDialog("Error", "Cannot Estabilish Connection");
//                }
//
//                Looper.loop(); //Loop in the message queue
//            }
//        };
//
//        t.start();
//    }





}

