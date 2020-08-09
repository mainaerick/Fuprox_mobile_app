package com.example.currentplacedetailsonmap.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.MainActivity;
import com.example.currentplacedetailsonmap.R;
import com.example.currentplacedetailsonmap.adapters.orderadapter;
import com.example.currentplacedetailsonmap.model.JSONParser;
import com.example.currentplacedetailsonmap.model.strings_;
import com.example.currentplacedetailsonmap.utils.Dbhelper;
import com.example.currentplacedetailsonmap.utils.booking_details;
import com.mancj.materialsearchbar.MaterialSearchBar;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;


@SuppressLint("ValidFragment")
public class ordersfragment extends Fragment implements androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener, MaterialSearchBar.OnSearchActionListener {
    //recyclerview objects
    public  static String TAG="";

    public static final String KEY_FRIDAY_FRAGMENT = "Friday";
    private ListView listView;
    private orderadapter adapter;
    final JSONParser jParser = new JSONParser();
    ProgressDialog pDialog;
    MaterialSearchBar searchBar;
    String filter="";
    String booking_id="";

    public ordersfragment(){

    }
    Activity activity;
    Context context;
    public ordersfragment(String booking_id,Activity activity){
        this.activity=activity;
        this.booking_id=booking_id;
    }

    View view;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        context=getActivity();

        if (new Dbhelper(getContext()).get_booking().isEmpty()){ // no available bookings and user exist

            if (new Dbhelper(getContext()).get_user_id()!=null && new Dbhelper(getContext()).get_booking().isEmpty()){
//                sendJson_getbookings(getActivity(),view);
//                new get_all_bookings(getActivity()).execute();
                Log.d(TAG, "setupAdapter: orderfragment check iuser and adapter is ");

            }
            view = inflater.inflate(R.layout.orders_empty,container,false);

            ImageView refreshimg=view.findViewById(R.id.refresh_bookings);
            ImageView imageView=view.findViewById(R.id.noorders);
            Button restoreprevious=view.findViewById(R.id.getprevious_bookings);

            restoreprevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new get_all_bookings(getActivity()).execute();


                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new Dbhelper(getContext()).get_booking().isEmpty()){
                        Toast.makeText(getActivity(), "No bookings available", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new ordersfragment())
                                .commit();
                    }
                }
            });

        }
        else{
            if (booking_id.length()>=1){
                fragment_oder_more_details fragmentOderMoreDetails=new fragment_oder_more_details(booking_id,new Dbhelper(getActivity()).get_user_id());
                FragmentManager manager = getActivity().getSupportFragmentManager();
                fragmentOderMoreDetails.show(manager,fragmentOderMoreDetails.getTag());
            }
            view = inflater.inflate(R.layout.orders, container, false);
            setupAdapter(view);
            setupListViewMultiSelect();
        }



        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        return view;
    }


    private void rloadfragment(){
        FragmentManager fm = ((FragmentActivity) context)
                .getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, new ordersfragment("",activity))
                .commit();
    }
    private void setupAdapter(View view) {

        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings={"dsdsdsdsd","dsdsdsdsd","dsdsdsdsd","dsdsdsdsd",};
        List<String> list= Arrays.asList(strings);
        String name=null;
        for (int i = 0; i < strings.length; i++) {
            name = list.get(i);
            listNamesOfFiles.add(name);
            Log.d("TAG", "setupAdapter: " + name + "1-=" + i);

        }


        listView = view.findViewById(R.id.homeworklist);
        adapter = new orderadapter(getActivity(), listView, R.layout.listorders, new Dbhelper(getContext()).get_booking());

        listView.setAdapter(adapter);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        ArrayList<String> searchlist=new ArrayList<>();
        for (int i=0;i<new Dbhelper(getContext()).get_booking().size();i++){
            searchlist.add(new Dbhelper(getContext()).get_booking().get(i).getB_name());
        }
        searchBar = view.findViewById(R.id.bookingsearchBar);
        searchBar.setSpeechMode(true);
        //enable searchbar callbacks
        searchBar.setHint("Search bookings");
        searchBar.setOnSearchActionListener(this);
//        searchBar.setLastSuggestions(searchlist);
        //Inflate menu and setup OnMenuItemClickListener
        searchBar.inflateMenu(R.menu.fragment_order_menu);
        searchBar.getMenu().setOnMenuItemClickListener(this);

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.getFilter().filter(s.toString());
//                adapter = new orderadapter(getActivity(), listView, R.layout.listorders, adapter.getFilter().filter(s.toString()));
                ArrayList<booking_details> fromdblist=new ArrayList<>();

                int textlength = s.length();
                ArrayList<booking_details> tempArrayList = new ArrayList<booking_details>();
                for(booking_details c: new Dbhelper(getContext()).get_booking()){
                    if (textlength <= c.getB_name().length()) {
                        long booking_t_stamp = Long.parseLong(c.getTime());

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                        if (filter.equals("date")){
                            if (dateFormat.format(booking_t_stamp).toLowerCase().contains(s.toString().toLowerCase())) {
                                tempArrayList.add(c);
                            }
                        }
                        else if(filter.equals("company")){
                            if (c.getCo_name().toLowerCase().contains(s.toString().toLowerCase())) {
                                tempArrayList.add(c);
                            }
                        }
                        else {
                            if (c.getB_name().toLowerCase().contains(s.toString().toLowerCase())) {
                                tempArrayList.add(c);
                            }
                        }

                    }
                }
                adapter = new orderadapter(getActivity(), listView, R.layout.listorders, tempArrayList);
                listView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        ORDER menu Items

        final ImageView ordermenu=view.findViewById(R.id.order_menu);

        ordermenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(getContext(),ordermenu);
                popupMenu.getMenuInflater().inflate(R.menu.fragment_order_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.order_delete_all:
                                new Dbhelper(getContext()).delete_all_booking();
                                adapter = new orderadapter(getActivity(), listView, R.layout.listorders, new Dbhelper(getContext()).get_booking());
//                                getActivity().getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .replace(R.id.fragment_container, new ordersfragment())
//                                        .commit();

                                listView.setAdapter(adapter);
                                return true;
                            default:
                                return onMenuItemClick(item);
                        }

                    }
                });
                popupMenu.show();

            }
        });
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.byco:
                filter="company";
                menuItem.setChecked(true);
                searchBar.setHint("Search bookings by company");
                disablemenu(3);
                return true;
            case R.id.bydate:
                menuItem.setChecked(true);
                searchBar.setHint("Search bookings by date");
                disablemenu(2);
                filter="date";
                return true;
            case R.id.order_delete_all:
                new Dbhelper(getContext()).delete_all_booking();
                adapter = new orderadapter(getActivity(), listView, R.layout.listorders, new Dbhelper(getContext()).get_booking());
//                                getActivity().getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .replace(R.id.fragment_container, new ordersfragment())
//                                        .commit();

                listView.setAdapter(adapter);
                return true;
            default:
                return onMenuItemClick(menuItem);
        }

    }

    public void disablemenu(int item){
        searchBar.getMenu().getMenu().getItem(item).setChecked(false);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_SPEECH:
                openVoiceRecognizer();
        }
    }
    int VOICE_RECOGNITION_REQUEST_CODE=2211;
    public void openVoiceRecognizer(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Search previous bookings");
        startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            int textlength = matches.get(0).length();
            ArrayList<booking_details> tempArrayList = new ArrayList<booking_details>();
            for(booking_details c: new Dbhelper(getContext()).get_booking()){
                if (textlength <= c.getB_name().length()) {
                    long booking_t_stamp = Long.parseLong(c.getTime());

                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                    if (filter.equals("date")){
                        if (dateFormat.format(booking_t_stamp).toLowerCase().contains(matches.get(0).toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }
                    else if(filter.equals("company")){
                        if (c.getCo_name().toLowerCase().contains(matches.get(0).toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }
                    else {
                        if (c.getB_name().toLowerCase().contains(matches.get(0).toLowerCase())) {
                            tempArrayList.add(c);
                        }
                    }

                }
            }

            if (tempArrayList.size()==0){
                searchBar.setHint("No "+ filter + " results in bookings for '"+matches.get(0)+"'!");
                searchBar.enableSearch();
            }
            else {
                searchBar.setHint(matches.get(0));
                searchBar.enableSearch();
            }

            adapter = new orderadapter(getActivity(), listView, R.layout.listorders, tempArrayList);
            listView.setAdapter(adapter);
        }
    }
    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    int count_booking;
    booking_details bookingDetails=new booking_details();


    private class get_all_bookings extends AsyncTask<String, String, String>{

//        View view;
        Activity activity;
        LayoutInflater inflater;
        ViewGroup container;
        Bundle savedInstanceState;
        JSONArray jsonarray = null;
        String result_ok=" ";


        public get_all_bookings(Activity activity_){
            activity=activity_;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Getting your previous bookings please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
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

                            count_booking=jsonarray.length();//4, 0,1,2,3
                            for(int i=0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                String booking_id = jsonobject.getString("id");
                                String branch_id = jsonobject.getString("branch_id");
                                String str_time = jsonobject.getString("start");
//                                String userid = jsonobject.getString("user_id");
                                String service_name=jsonobject.getString("service_name");
                                String ticket_id=jsonobject.getString("ticket");
                                boolean serviced=jsonobject.getBoolean("serviced");
                                if (serviced){
                                    bookingDetails.setServiced("1");
                                }
                                else {
                                    bookingDetails.setServiced("0");

                                }
//                                    Log.d(TAG, "run: view all bokings id="+booking_id+ "  branch_id="+branch_id);


                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


//                                Date date=format.parse(str_time);
                                Date date = new Date();
                                date.setTime(Long.parseLong(str_time));

                                String timestamp= String.valueOf(date.getTime());

                                if (i==count_booking-1){
                                    count_booking=0;
                                }
                                sendJson_getbranch_details(branch_id,activity,timestamp,booking_id,service_name,count_booking);//insert to db,
//
                            }
                            result_ok="bookings";

                                Log.d(TAG, "run: debug json array " +jsonarray.getString(0));



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
            pDialog.dismiss();

            if (booking_id.length()>0){
                fragment_oder_more_details fragmentOderMoreDetails=new fragment_oder_more_details(booking_id,new Dbhelper(activity).get_user_id());
                FragmentManager manager = ((AppCompatActivity)activity).getSupportFragmentManager();
                fragmentOderMoreDetails.show(manager,fragmentOderMoreDetails.getTag());
            }
            else {

            }

        }
    }

    private void setupListViewMultiSelect() {
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        listView.setMultiChoiceModeListener(FragmentHelper.setupListViewMultiSelect(getActivity(), listView, adapter, db));
    }
    public static void hideactivityitems(Activity activity){
        activity.findViewById(R.id.borderedsearchlay).setVisibility(View.GONE);
//        activity.findViewById(R.id.message).setVisibility(View.GONE);
    }
//

    private void sendJson_getbranch_details(final String branch_id, final Activity activity, final String book_time, final String booking_id, final String service_name,int count) {
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

                try {
                    HttpPost post = new HttpPost(new strings_().url()+"/branch/get/single");
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
                            try {
                                obj= new JSONObject(result);

                                // GET BRANCH NAME AND MOVE ON TO
                                new getcompany_name(obj.getString("company"),branch_id,booking_id,obj.getString("name"),book_time,service_name,count).execute();

//                                Log.d(TAG, "run: getting branch id company "+ obj.getString("company"));
                                Log.d(TAG, "run: getting branch id company "+ result);


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
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }


    private class getcompany_name extends AsyncTask<String, String, String>{
        String company_id;
        String branch_id;
        String branch_name;
        String book_time;
        String company_name=" ";
        String service_name=" ";
        String booking_id;
        String _error;
        int count;

        public getcompany_name(String company_id,String branch_id,String booking_id,String branch_name,String book_time,String service_name,int count){
            this.company_id=company_id;
            this.branch_id=branch_id;
            this.branch_name=branch_name;
            this.book_time=book_time;
            this.service_name=service_name;
            this.booking_id=booking_id;
            this.count=count;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(getContext());
//            pDialog.setMessage("Getting details. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
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
                HttpPost post = new HttpPost(new strings_().url()+"/company/by/id");
                json.put("id", company_id);

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
                        try {
                            obj= new JSONObject(result);
//                              jsonarray = new JSONArray(result);
                            company_name=obj.getString("name");
                            bookingDetails.setCo_name(company_name); //institution name
                            bookingDetails.setB_name(branch_name);    //branch name
                            bookingDetails.setTime(book_time);
                            bookingDetails.setBooking_id(booking_id);
                            bookingDetails.setBranch_id(branch_id);
                            bookingDetails.setService_name(service_name);
//                            new SimpleAlarmManager(getContext()).cancel(Integer.parseInt(booking_id));
//                            new SimpleAlarmManager(getContext()).
//                                    setup(1, Long.parseLong(book_time)).
//                                    register(Integer.parseInt(booking_id)).
//                                    start();
                            _error=" ";

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: start_booking \"" + obj + "\""+t.getMessage());
                            _error="error";
                        }


//                            Log.d(TAG, "run: string" + result+new Dbhelper(activity).get_user_id());

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        _error="error";

                    }

                }

            } catch(Exception e) {
                e.printStackTrace();
                Log.d(TAG, "run: book connection error" );
                _error="error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (_error.equals(" ")){

                Log.d(TAG, "onPostExecute: counting "+count);
                new Dbhelper(activity).insert_booking(bookingDetails);
                if(count==0){
//                    new MainActivity().loadFragment(new ordersfragment("",activity));
                    rloadfragment();
                }
            }
//            pDialog.dismiss();

        }
    }




}
