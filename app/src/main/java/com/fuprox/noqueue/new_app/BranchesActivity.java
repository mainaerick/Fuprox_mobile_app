package com.fuprox.noqueue.new_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fuprox.noqueue.MainActivity;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.Receiver.FloatingWidgetService;
import com.fuprox.noqueue.activities.LatLong;
import com.fuprox.noqueue.activities.activity_booking;
import com.fuprox.noqueue.adapters.branches_adapter;
import com.fuprox.noqueue.adapters.suggestion_adapter;
import com.fuprox.noqueue.fragment.Branches_fragment;
import com.fuprox.noqueue.fragment.bottom_sheet_fragment;
import com.fuprox.noqueue.model.JSONParser;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.branches_details;
import com.fuprox.noqueue.utils.suggestion_details;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BranchesActivity extends AppCompatActivity {

    TextView txttitle;
    ImageView imgcompany;
    public  static String TAG="";

    public static final String KEY_FRIDAY_FRAGMENT = "Friday";
    public static String error=" ";
    final JSONParser jParser = new JSONParser();
    String institution_id,institution_name,imgurl;
    ArrayList<branches_details> branches_list = new ArrayList<>();
    MaterialSearchBar searchBar;
    com.fuprox.noqueue.adapters.suggestion_adapter suggestion_adapter;
    static List <suggestion_details> post_params = new ArrayList<>();
    LatLong my_latlong;
    private boolean mLocationPermissionGranted;
    String no_branche=" ";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    boolean list_status_nolist=false;
    LinearLayout error_linearlayout;
    ListView listView;
    String error_title="";
    Drawable drawable;

    LinearLayout layout_loading;
    SweetAlertDialog pDialog;
    TextView tv_companyname;
    RadioButton rb_nearme,rb_queuesize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);
        pDialog = new SweetAlertDialog(BranchesActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        tv_companyname = findViewById(R.id.company_name);
        rb_nearme = findViewById(R.id.radionearme);
        rb_queuesize = findViewById(R.id.radioqueusize);
        layout_loading = findViewById(R.id.layout_loading);

        if (getIntent().getExtras()!=null){
            institution_id=getIntent().getExtras().getString("institution_id");
            institution_name=getIntent().getExtras().getString("institution_name");
            tv_companyname.setText(institution_name);
            imgurl=getIntent().getExtras().getString("imgurl");
            setup_image();
        }
    }
    private void setupradiobtn(){
        if (rb_queuesize.isChecked()){
            Collections.sort(branches_list, new Comparator<branches_details>() {
                @Override
                public int compare(branches_details o1, branches_details o2) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
            });
            branches_adapter adapter = new branches_adapter(BranchesActivity.this, listView, R.layout.list_new_branches, branches_list,drawable);
            listView.setAdapter(adapter);
        }
        else {
            Collections.sort(branches_list, new Comparator<branches_details>() {
                @Override
                public int compare(branches_details o1, branches_details o2) {
                    return o1.getLattitude().compareTo(o2.getLattitude());
                }
            });
            branches_adapter adapter = new branches_adapter(BranchesActivity.this, listView, R.layout.list_new_branches, branches_list,drawable);
            listView.setAdapter(adapter);
        }
        rb_nearme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Collections.sort(branches_list, new Comparator<branches_details>() {
                        @Override
                        public int compare(branches_details o1, branches_details o2) {
                            return o1.getLattitude().compareTo(o2.getLattitude());
                        }
                    });
                    branches_adapter adapter = new branches_adapter(BranchesActivity.this, listView, R.layout.list_new_branches, branches_list,drawable);
                    listView.setAdapter(adapter);
                }
            }
        });
        rb_queuesize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Collections.sort(branches_list, new Comparator<branches_details>() {
                        @Override
                        public int compare(branches_details o1, branches_details o2) {
                            return o1.getTitle().compareTo(o2.getTitle());
                        }
                    });

                    branches_adapter adapter = new branches_adapter(BranchesActivity.this, listView, R.layout.list_new_branches, branches_list,drawable);

                    listView.setAdapter(adapter);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setup_image(){
        imgcompany = findViewById(R.id.imgcompany);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Picasso.get().load(imgurl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.i(TAG, "The image was obtained correctly");
                    imgcompany.setImageBitmap(bitmap);
                    pDialog.dismiss();
                    drawable = imgcompany.getDrawable();
                    new branch_get().execute();

                }
                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    pDialog.dismiss();
                    new branch_get().execute();
                    Log.e(TAG, "onPostExecute: is_id "+ institution_id);

                    //                    holder.pDialog.setVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "Getting ready to get the image");
                    //                    holder.pDialog.setVisibility(View.VISIBLE);
                    pDialog.getProgressHelper().setBarColor(getResources().getColor(android.R.color.transparent));
                    pDialog.setTitleText("Loading");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    //Here you should place a loading gif in the ImageView
                    //while image is being obtained.
                }
            });
        }
        else {
            new DownloadImageTask(imgcompany).execute(imgurl);
        }


    }
    private void setupAdapter(){
        txttitle = findViewById(R.id.company_name);


        listView = findViewById(R.id.branches_listview);
        error_linearlayout=findViewById(R.id.layout_error_disp);

        Collections.sort(branches_list, new Comparator<branches_details>() {
            @Override
            public int compare(branches_details o1, branches_details o2) {
                return o1.getLattitude().compareTo(o2.getLattitude());
            }
        });

        branches_adapter adapter = new branches_adapter(this, listView, R.layout.list_new_branches, branches_list,drawable);

        listView.setAdapter(adapter);
        setupradiobtn();

        if (!list_status_nolist){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView=view.findViewById(R.id.branch_title);
                    TextView textViewid=view.findViewById(R.id.txtbranchid);
//                    bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment(BranchesActivity.this,textViewid.getText().toString(),
//                            institution_name,"1",
//                            textView.getText().toString(),
//                            branches_list.get(position).getClose_time(),
//                            branches_list.get(position).getOpen_time(),branches_list.get(position).getIsmedical());
//                    assert getSupportFragmentManager() != null;
//                    bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());
                    Intent intent1 = new Intent(BranchesActivity.this, activity_booking.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("branch_id", textViewid.getText().toString());
                    bundle.putString("opens", branches_list.get(position).getOpen_time());
                    bundle.putString("closes", branches_list.get(position).getClose_time());
                    bundle.putString("branch_name", textView.getText().toString());
                    bundle.putString("company", institution_name);
                    bundle.putString("ismedical", branches_list.get(position).getIsmedical());
                    bundle.putString("longitude", branches_list.get(position).getLongitude());
                    bundle.putString("latitude", branches_list.get(position).getLattitude());

                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
            });
        }

        else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        TextView textView = view.findViewById(R.id.service_title);
//
//                        TextView textViewid = view.findViewById(R.id.serviceid);

                    new branch_get().execute();
                }
            });
        }



        findViewById(R.id.branches_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                loadFragment(new Institution_fragment(service_id));
            }
        });
    }

private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        SweetAlertDialog pDialog = new SweetAlertDialog(BranchesActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.getProgressHelper().setBarColor(getResources().getColor(android.R.color.transparent));
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
//            pDialog.setVisibility(View.GONE);
            pDialog.dismissWithAnimation();
            new branch_get().execute();
            Log.e(TAG, "onPostExecute: is_id "+ institution_id);

            bmImage.setImageBitmap(result);
            drawable = imgcompany.getDrawable();

        }
    }

    private class branch_get extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
//            pDialog.getProgressHelper().setBarColor(getResources().getColor(android.R.color.transparent));
//            pDialog.setTitleText("Loading");
//            pDialog.setCancelable(false);
//            pDialog.show();
            layout_loading.setVisibility(View.VISIBLE);
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(BranchesActivity.this)+"/branch/by/company");
                json.put("company", institution_id);

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
                        try {
//                            obj= new JSONObject(result);

                            jsonarray = new JSONArray(result);

                            String name = null;
                            int i=0;
                            while (i<jsonarray.length()){
                                branches_details branches_details = new branches_details();
                                obj=new JSONObject(jsonarray.getString(i));
                                branches_details.setTitle(obj.getString("name"));
                                branches_details.setId(obj.getString("id"));
                                branches_details.setOpen_time(obj.getString("opens"));
                                branches_details.setClose_time(obj.getString("closes"));
                                branches_details.setLongitude(obj.getString("longitude"));
                                branches_details.setLattitude(obj.getString("latitude"));
                                branches_details.setIcon_url(obj.getString("icon"));
                                boolean ismedical=obj.getBoolean("is_medical");
                                if (ismedical){
                                    branches_details.setIsmedical("1");
                                }
                                else {
                                    branches_details.setIsmedical("0");
                                }
                                Log.e(TAG, "get all branches "+ obj);
                                branches_list.add(branches_details);
                                i++;
                            }
                            if (branches_list.size()==0){
                                error_title="No Branches";
                                error="No Branches added yet!";
                            }
                            Log.d(TAG, "get all branches "+ result);
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: branches\"" + obj + "\""+t.getMessage());
                            error= "Oops.. \nThere was a problem communicating with the servers.";
                            error_title="Server error!";
                        }
                        Log.d(TAG, "run: string" + result);

                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                        error= "Oops.. \nThere was a problem communicating with the servers.";
                        error_title="Server error!";

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
                Log.d(TAG, "run: branches get" );
                error = "Oops.. \nThere was a problem establishing internet connection.";
                error_title = "Internet connection error!";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            pDialog.dismissWithAnimation();
            layout_loading.setVisibility(View.GONE);
//            pDialog.dismiss();
            setupAdapter();
            if (error_title.isEmpty()){
                listView.setVisibility(View.VISIBLE);
                error_linearlayout.setVisibility(View.GONE);
            }
            else {
                network_error(error_title,error);
            }
            Log.d(TAG, "onPostExecute: services  ");


        }
    }

    public void network_error(String error,String error_description){
        ArrayList<String> listNamesOfFiles = new ArrayList<>();
        String[] strings = {error};
        List<String> list= Arrays.asList(strings);
//                ArrayList<branches_details> branch_list = new ArrayList<>();

        error_linearlayout=findViewById(R.id.layout_error_disp);
        error_linearlayout.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        TextView txterror_title=findViewById(R.id.error_title);
        TextView error_desc=findViewById(R.id.error_description);
        txterror_title.setText(error);
        error_desc.setText(error_description);

        Button error_retry=findViewById(R.id.retry_btn);

        error_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error_title = "";
                error_linearlayout.setVisibility(View.INVISIBLE);

                new branch_get().execute();
            }
        });
//        String name = null;
//        branches_list=new ArrayList<>();
//        for (int i = 0; i < strings_.length; i++) {
//            branches_details branches_details = new branches_details();
//
//            branches_details.setTitle(list.get(i));
//            branches_list.add(branches_details);
//
//        }

        list_status_nolist=true;

    }
}