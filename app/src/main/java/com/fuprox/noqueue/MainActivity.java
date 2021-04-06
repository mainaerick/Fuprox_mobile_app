package com.fuprox.noqueue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fuprox.noqueue.adapters.suggestion_adapter;
import com.fuprox.noqueue.fragment.Favourite_fragment;
import com.fuprox.noqueue.fragment.Services_fragment;
import com.fuprox.noqueue.fragment.account_fragment;
import com.fuprox.noqueue.fragment.bottom_sheet_fragment;
import com.fuprox.noqueue.fragment.ordersfragment;
import com.fuprox.noqueue.model.RecyclerTouch;
import com.fuprox.noqueue.model.SimpleAlarmManager;
import com.fuprox.noqueue.model.notification;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.Dbhelper;
import com.fuprox.noqueue.utils.booking_details;
import com.fuprox.noqueue.utils.suggestion_details;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import net.alhazmy13.catcho.library.Catcho;

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
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, MaterialSearchBar.OnSearchActionListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @SuppressLint("StaticFieldLeak")


    private static MaterialSearchBar searchBar;
    public  static String TAG="";
    List<suggestion_details> clonepost_params = new ArrayList<>();
    suggestion_adapter suggestion_adapter;
    public boolean permision_granted = false;
    ProgressDialog pDialog;
    static List<suggestion_details> post_params = new ArrayList<>();
    public BottomNavigationView navView;
    String booking_id="";
    public static String onbackpress="";

    int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE=500;
    int OVERLAY_ASK_PERMISSION_CODE=501;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_fav:
                    onbackpress="";

                    new ordersfragment().hideactivityitems(MainActivity.this);
                    loadFragment(new Favourite_fragment(MainActivity.this));
                    return true;
                case R.id.navigation_order:
                    onbackpress="";

                    new ordersfragment().hideactivityitems(MainActivity.this);
                    loadFragment(new ordersfragment(booking_id,MainActivity.this));
                    booking_id="";
                    return true;
                case R.id.navigation_queue:
                    onbackpress="exit";
                    new Services_fragment(MainActivity.this).hideactivityitems(MainActivity.this);
                    verify_permissions();
                    return true;
                case R.id.navigation_account:
                    onbackpress="";
                    new account_fragment(MainActivity.this).hideactivityitems(MainActivity.this);
                    loadFragment(new account_fragment(MainActivity.this));
                    return true;
            }
            return false;
        }
    };
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 123;
    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        // To prevent starting the service if the required permission is NOT granted.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
//            Intent intent1 = new Intent(this, FloatingWidgetService.class);
////
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("activity_background", true);
//            bundle.putString("booking_id","1");
//            intent1.putExtras(bundle);
//            startService(intent1);
        } else {
            errorToast();
        }
    }

    private void errorToast() {
        Toast.makeText(this, "Draw over other app permission not available. Can't start the application without the permission.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        Catcho.Builder(this)
                .recipients("niteric@gmail.com")
                .build();

//        startService(new Intent(MainActivity.this, FloatingWidgetService.class));
//        startService(new Intent(MainActivity.this, FloatingWidgetService.class).putExtra("activity_background", true));
//        finish();
        setContentView(R.layout.activity_main);
//        SharedPreferences sharedPreferences
//                = getSharedPreferences("checkinfront",
//                MODE_PRIVATE);
//        SharedPreferences.Editor myEdit
//                = sharedPreferences.edit();
//        myEdit.clear();
//        myEdit.commit();

        notification.createNotificationChannel(this);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (new Dbhelper(MainActivity.this).check_user()==1){
                askForSystemOverlayPermission();
                setupadapter();
            }
            else {
//                loadwithanimation("login");
//                sign_in(LoginActivity.this);
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        },1000);

    }

    private void setupadapter(){
        setalarm(this);
        navView= findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (getIntent().getExtras()!=null){
            if (getIntent().getExtras().getString("verify").equals("verify")){
                booking_id=getIntent().getExtras().getString("book_id");
                navView.setSelectedItemId(R.id.navigation_order);
//                loadFragment(new ordersfragment(booking_id));
            }
            else {

            }
        }
        else {
            navView.setSelectedItemId(R.id.navigation_queue);

//            new branches_search(" ").execute();
        }

        navView.getOrCreateBadge(R.id.navigation_order).setNumber(new Dbhelper(this).getactivebookings());

        RecyclerView recyclerView = findViewById(R.id.mt_recycler);
        recyclerView.clearAnimation();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        suggestion_adapter = new suggestion_adapter(inflater);
        searchBar = findViewById(R.id.searchBar_q);
        searchBar.showSuggestionsList();
        searchBar.setMaxSuggestionCount(5);
        searchBar.setText("");
        searchBar.setSpeechMode(true);
        searchBar.setPlaceHolder("Search a place to book");
//        searchBar.inflateMenu(R.menu.main_menu);
        searchBar.setOnSearchActionListener(this);
        Log.d("LOG_TAG", getClass().getSimpleName() + ": text " + searchBar.getText());
        searchBar.setCardViewElevation(10);
//        sendJson_getbranch_details(" ");
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (post_params.isEmpty()){
                    ArrayList arrayList=new ArrayList();
                    String[] lastsuggestions={"Searching..."};
                    for (int k=0; k<lastsuggestions.length; k++){
                        arrayList.add(lastsuggestions[i]);
                    }
                    searchBar.setLastSuggestions(arrayList);
                    searchBar.showSuggestionsList();
                }
                else {
                    if (charSequence.toString().isEmpty()){
                        searchBar.hideSuggestionsList();
                        searchBar.clearSuggestions();
                    }
                    else {
                        if (!searchBar.isSuggestionsVisible() && searchBar.getText().length()==2){
                            Log.d(TAG, "onTextChanged: "+post_params.size());
                            suggestion_adapter.setSuggestions(post_params);
                            searchBar.showSuggestionsList();
                        }
                    }
                    suggestion_adapter.getFilter().filter(charSequence.toString());
                }
//                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }

        });

        recyclerView.addOnItemTouchListener(
                new RecyclerTouch(getApplicationContext(), recyclerView, new RecyclerTouch.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

//                        Toast.makeText(MainActivity.this, "position"+position, Toast.LENGTH_SHORT).show();

                        TextView textView = view.findViewById(R.id.suggestion_title);
                        TextView searchcompany_id = view.findViewById(R.id.suggestion_long);
                        TextView searchbranch_id = view.findViewById(R.id.suggestion_lat);
                        TextView searchbranch_ismedical=view.findViewById(R.id.tvsuggestion_bmedical);

                        searchBar.setText("");
                        searchBar.disableSearch();

//                        Toast.makeText(MainActivity.this, "position" + position, Toast.LENGTH_SHORT).show();

//                        searchBar.setText(textView.getText().toString());
                        String branch_id=searchbranch_id.getText().toString(),
                                company_id=searchcompany_id.getText().toString(),
                                branch_name=textView.getText().toString(),
                                branch_close=post_params.get(position).getCloses(),
                                branch_open=post_params.get(position).getOpens(),
                                branchismedical=searchbranch_ismedical.getText().toString();

                        new getcompany_name(company_id,branch_id,branch_name,branch_close,branch_open,"book",branchismedical).execute();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void get_ipaddress() {
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("ip_");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
//                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Value is: " + value);
                if (new Dbhelper(MainActivity.this).get_ipaddress().equals("122")||
                        !new Dbhelper(MainActivity.this).get_ipaddress().equals(value)||
                        new Dbhelper(MainActivity.this).get_ipaddress()!=null){
                    new Dbhelper(MainActivity.this).set_ipaddress(value);
                }
                Toast.makeText(MainActivity.this, ""+value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(post_params.isEmpty()){
            new branches_search(" ").execute();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_SPEECH:
                openVoiceRecognizer();
        }
    }

    int VOICE_RECOGNITION_REQUEST_CODE=1211;

    public void openVoiceRecognizer(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Search places to book");
        startActivityForResult(intent,VOICE_RECOGNITION_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            int textlength = matches.get(0).length();

            ArrayList<booking_details> tempArrayList = new ArrayList<booking_details>();
//            for (booking_details c : post_params) {
//                if (textlength <= c.getB_name().length()) {
                    if (textlength == 0) {
                        searchBar.hideSuggestionsList();
                        searchBar.clearSuggestions();
                    } else {
                        if (!searchBar.isSuggestionsVisible()) {
                            Log.d(TAG, "onTextChanged: " + post_params.size());
                            suggestion_adapter.setSuggestions(post_params);
                            searchBar.showSuggestionsList();
                            searchBar.setHint(matches.get(0));
                            searchBar.enableSearch();
                        }
                    }
                    suggestion_adapter.getFilter().filter(matches.get(0).toLowerCase());
                    Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
        }
        else if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //Permission is not available. Display error text.
                    errorToast();
                    finish();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void setalarm(Activity activity){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.SECOND, new strings_().alaarmwaittime());
        new SimpleAlarmManager(activity)    //set alarm
                .setup(1,calendar.getTimeInMillis())
                .register(new strings_().alarmid())
                .start();
        Log.d(TAG, "doInBackground: alarm set"+  new SimpleAlarmManager(activity)    //set alarm
                .setup(1,calendar.getTimeInMillis())
                .register(new strings_().alarmid())
                .show_mili());
    }

    public class branches_search extends AsyncTask<String, String, String> {
        String searchtext;
        String found="0";
        String company,b_id,branch_name,closes,opens;

        public branches_search(String search_term){
            searchtext=search_term;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(MainActivity.this,AlertDialog.THEME_HOLO_LIGHT);
//            pDialog.setMessage("Searching. Please wait...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(MainActivity.this)+"/app/search");
                json.put("term", searchtext);
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


//                            CONVERT THE RESPONSE RESULT TO JSON FROM STRRING

                        JSONObject obj;
                        JSONArray jsonarray;
                        try {
//                                obj= new JSONObject(result);
                            jsonarray=new JSONArray(result);
                            if (!clonepost_params.isEmpty()){
                                clonepost_params.clear();
                            }
                            for (int i=0;i<jsonarray.length();i++){
                                obj=new JSONObject(jsonarray.getString(i));
                                String company=obj.getString("company");
                                String branch_name=obj.getString("name");
                                String longi=obj.getString("longitude");
                                String latti=obj.getString("latitude");
                                String opens=obj.getString("opens");
                                String closes=obj.getString("closes");
                                String b_id=obj.getString("id");

                                this.company=company;
                                this.b_id=b_id;
                                this.branch_name=branch_name;
                                this.closes=closes;
                                this.opens=opens;
////                                    post_params.add(company+ " " +branch_name);
                                suggestion_details suggestion_details=new suggestion_details();
                                suggestion_details.setCompany_id(obj.getString("company"));
                                suggestion_details.setTitle(branch_name);
                                suggestion_details.setOpens(opens);
                                suggestion_details.setCloses(closes);
                                suggestion_details.setId(obj.getString("id"));
                                boolean ismedical=obj.getBoolean("is_medical");
                                if (ismedical){
                                    suggestion_details.setIsmedical("1");
                                }
                                else {
                                    suggestion_details.setIsmedical("0");

                                }
                                if (clonepost_params.size()>jsonarray.length()){

                                }
                                else {
                                    found="1";
                                    clonepost_params.add(suggestion_details);
                                }

                                Log.d(TAG, "run: search results "+ obj.getString("company")+" "+obj.getString("name"));
                                Log.d(TAG, "run: search odj "+result);

                            }
                            Log.d(TAG, "run: search odj postparams"+post_params.size());

                        } catch (Throwable t) {
                            Log.d(TAG, "run: search result error "+ searchtext);

                            Log.e("My App", "search result error  " +t.getMessage());
                        }
                    } catch (Exception e) {
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
//            List <suggestion_details> clone_post_params = new ArrayList<>();
            super.onPostExecute(s);
//            pDialog.dismiss();
//            new Services_fragment().hideactivityitems(MainActivity.this);
//            loadFragment(new Services_fragment(MainActivity.this));
//            navView.setSelectedItemId(R.id.navigation_queue);
            for (int i=0;i< clonepost_params.size();i++){
                new getcompany_name(clonepost_params.get(i).getCompany_id(),clonepost_params.get(i).getId(),clonepost_params.get(i).getTitle(),clonepost_params.get(i).getCloses(),clonepost_params.get(i).getOpens()," ",clonepost_params.get(i).getIsmedical()).execute();
                Log.d(TAG, "onPostExecute company id= : "+clonepost_params.get(i).getCompany_id());
            }

        }
    }

    private class getcompany_name extends AsyncTask<String, String, String>{

        String company_id;
        String branch_id;
        String branch_name;
        String branch_close;
        String branch_open;
        String company_name=" ";
        String action;
        String gcnamerroe=" ";
        String ismedical;

        public getcompany_name(String company_id,String branch_id,String branch_name,String branch_close,String branch_open,String action,String ismedical){
            this.ismedical=ismedical;
            this.company_id=company_id;
            this.branch_id=branch_id;
            this.branch_name=branch_name;
            this.branch_close=branch_close;
            this.branch_open=branch_open;
            this.action=action;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action.equals("book")){
                pDialog = new ProgressDialog(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                pDialog.setMessage("Getting Services. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(MainActivity.this)+"/company/by/id");
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
                            obj= new JSONObject(result);
                            if (obj.length()==0){
                                gcnamerroe="empty company";
                            }
//                              jsonarray = new JSONArray(result);
                            company_name=obj.getString("name");

                            suggestion_details suggestion_details=new suggestion_details();
                            suggestion_details.setCompany(company_name);
                            suggestion_details.setCompany_id(company_id);
                            suggestion_details.setTitle(branch_name);
//                            suggestion_details.setLatitude();
//                            suggestion_details.setLongitude();

                            suggestion_details.setOpens(branch_open);
                            suggestion_details.setCloses(branch_close);
                            suggestion_details.setId(branch_id);
                            suggestion_details.setIsmedical(ismedical);

                            if (!action.equals("book")){

                                post_params.add(suggestion_details);
                            }

                            Log.d(TAG, "company details by id"+obj);

                        } catch (Throwable t) {

                            Log.e("My App", "Could not parse malformed JSON: getcomany name \"" + obj + "\""+t.getMessage());
                            Log.d(TAG, "doInBackground: company-d   "+company_id );
                            gcnamerroe="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";

                        }

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        gcnamerroe="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
                Log.d(TAG, "run: company get connection error" );
                gcnamerroe="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            pDialog.dismiss();
            if (gcnamerroe.equals(" ")){
                if (action.equals("book")){
                    bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment(MainActivity.this,branch_id,
                            company_name,
                            "",
                            branch_name,
                            branch_close,
                            branch_open,ismedical
                    );
                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
                else {
                    searchBar.setCustomSuggestionAdapter(suggestion_adapter);

                }
            }
            else {
                if (!action.equals("booking")){
                    new branches_search(" ").execute();

                }
            }

            if (action.equals("book")){
                pDialog.dismiss();
            }
        }


    }


    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        }
        else {
            if (onbackpress.equals("exit")){
                onbackpress="";
                fm.popBackStack();
                finish();
            }
            else if (onbackpress.equals("")){
                navView.setSelectedItemId(R.id.navigation_queue);

            }
            else {
                super.onBackPressed();

            }
            Log.i("MainActivity", "nothing on backstack, calling super");

        }
    }

    private void verify_permissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {   //Android M Or Over
                Handler handler = new Handler();
                Runnable checkOverlaySetting = new Runnable() {
                    @Override
                    @TargetApi(23)
                    public void run() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            return;
                        }
                        if (Settings.canDrawOverlays(MainActivity.this)) {
                            //You have the permission, re-launch MainActivity
                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);

                            return;
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_ASK_PERMISSION_CODE);
                handler.postDelayed(checkOverlaySetting, 1000);

            }
            else {
                if (permision_granted) {
                    if (isLocationEnabled()) {
                        new Services_fragment(MainActivity.this).hideactivityitems(MainActivity.this);
                        loadFragment(new Services_fragment(MainActivity.this));
                    } else {
                        Toast.makeText(MainActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    check_permission();
                }
            }
        }
        else {
            check_permission();
        }
    }
    private void check_permission(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    },11);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        else {
            if (isLocationEnabled()){
                loadFragment(new Services_fragment(MainActivity.this));
                permision_granted = true;

//                            loadFragment(new Services_fragment());
//                        startActivity(new Intent(MainActivity.this, Direction_activity.class));
            }
            else {
                Toast.makeText(MainActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Handler handler = new Handler();
                Runnable checkOverlaySetting = new Runnable() {
                    @Override
                    @TargetApi(23)
                    public void run() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            return;
                        }
                        if (Settings.canDrawOverlays(MainActivity.this)) {
                            //You have the permission, re-launch MainActivity
                            if (isLocationEnabled()){
                                Intent i = new Intent(MainActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                return;
                            }
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                handler.postDelayed(checkOverlaySetting, 1000);

//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//                finish();
            }
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

//                    permision_granted=true;
//                    loadFragment(new Services_fragment());
                    check_permission();

                } else {
                    permision_granted=false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onClick(View v) {
        searchBar = findViewById(R.id.searchBar_q);
//        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        searchBar.showSuggestionsList();
    }

    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }


}
