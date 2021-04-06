package com.fuprox.noqueue.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fuprox.noqueue.Receiver.FloatingWidgetService;
import com.fuprox.noqueue.Receiver.PM_verify_service;
import com.fuprox.noqueue.model.SocketInstance;
import com.fuprox.noqueue.model.notification;
import com.fuprox.noqueue.model.strings_;
import com.fuprox.noqueue.utils.verifypayment_pending;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.adapters.service_offered_adapter;
import com.fuprox.noqueue.model.alertdialoghelper;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import br.com.simplepass.loadingbutton.customViews.OnAnimationEndListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;
@SuppressWarnings("ALL")
@SuppressLint("ValidFragment")
public class bottom_sheet_fragment extends BottomSheetDialogFragment {
    public  static String TAG="";
    String b_id,sname,saddress,sbranch,sopent,scloset,error=" ",error_serv=" ";
    String ismedical;
    DatePickerDialog picker;
    EditText editnumber;
    TextView txtcompanyname,branch_time_status;
    TextView txtaddress,error_disp;
    TextView txtbranch,datetv,timetv,shownumber,showcontrycode,tvaddnumber,tvnumberpeople,tvtellerno,tvlabelqueuesize,tvqueuesizeerror;
    int book_year=0;
    int book_month=0;
    int book_day=0;
    int book_hour=0;
    int book_min=0;
    long booking_milis;
    CircularProgressButton nomalbook,instantbook;
//    Button instantbook;
    ProgressBar progressBar;
            CheckBox chkinstantservice;
    String isinstant;

    String notopenmsg="";
    ArrayList<services_offered_details> servicesOfferedDetailsArrayList;
    ArrayList<String> list_sername;

    service_offered_adapter adapter;

    Date close_date = null;
    Date open_date=null;

//    SweetAlertDialog pDialog;
    ProgressDialog pDialog;
    Spinner spinner;
    String spinner_text;
    Activity activity;
    TextView warntv;


    // Creating JSON Parser object

    public bottom_sheet_fragment(Activity activity,String branch_id,String name,String address, String branch,String scloset,String sopent,String ismedical) {
        this.ismedical=ismedical;
        sname=name;
        saddress=address;
        sbranch=branch;
        b_id=branch_id;
        this.sopent=sopent;
        this.scloset=scloset;
        this.activity=activity;
    }

    String invisi="invisible";
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset>0.5){

                }

            }
        };

        @SuppressLint("RestrictedApi")
        @Override
        public void setupDialog(Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            final View contentView = View.inflate(getContext(), R.layout.layout_bottom_sheet, null);
            dialog.setContentView(contentView);

//            socket_init("token","phonenumber","branch_id","service_name");

//            IO.Options options = IO.Options.builder()
//                    .setForceNew(true)
//                    .build();


            nomalbook=contentView.findViewById(R.id.book_click);
            instantbook=contentView.findViewById(R.id.instantbook);
            final TextView book_button=contentView.findViewById(R.id.booking_confirm);
            txtcompanyname=contentView.findViewById(R.id.bs_coname);
            txtaddress=contentView.findViewById(R.id.bs_address);
            txtbranch=contentView.findViewById(R.id.bs_branch);
            branch_time_status=contentView.findViewById(R.id.open_close_status);
            error_disp=contentView.findViewById(R.id.error_display);
            warntv=contentView.findViewById(R.id.bookwarning);
            timetv=contentView.findViewById(R.id.booking_time_activity);
            datetv=contentView.findViewById(R.id.booking_date_activity);
            shownumber=contentView.findViewById(R.id.shownumber);
            tvaddnumber=contentView.findViewById(R.id.tvaddnumber);
            tvnumberpeople=contentView.findViewById(R.id.tvnumberofpeople);
            editnumber=contentView.findViewById(R.id.edittextnumber);
            editnumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
            showcontrycode=contentView.findViewById(R.id.countrycode);
            progressBar = contentView.findViewById(R.id.queuesizeprogress);
            tvtellerno = contentView.findViewById(R.id.tvtellernumbers);
            tvlabelqueuesize = contentView.findViewById(R.id.lablequeuesize);
            tvqueuesizeerror = contentView.findViewById(R.id.queuesize_error);
//            editnumber.addTextChangedListener(new PhoneTextFormatter(editnumber, "+7 (###) ###-####"));
//            showcontrycode.setText(getCountryZipCode(getActivity()));
            showcontrycode.setText("254");


            if (!new Dbhelper(getContext()).getuser_no().equals("0")){
                int codesize=showcontrycode.getText().toString().length();
                String n=new Dbhelper(getContext()).getuser_no().substring(codesize);
                editnumber.setText(n);

            }


            branch_time_status.setText("Open "+sopent+" - "+scloset);

            final String add_no=getResources().getString(R.string.add_no);
            String myno=getResources().getString(R.string.my_no);

            txtcompanyname.setText(sname);
            txtaddress.setText(b_id);
            txtbranch.setText(sbranch);

            if (!new Dbhelper(getContext()).getuser_no().equals("0")){
                tvaddnumber.setText(myno);
                Drawable img = getResources().getDrawable(R.drawable.ic_close_black_24dp);
                shownumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                shownumber.setText(new Dbhelper(getContext()).getuser_no());
            }

            if (shownumber.getText().toString().length()<1){
                tvaddnumber.setText(add_no);
            }
            tvaddnumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvaddnumber.getText().toString().toLowerCase().equals(add_no.toLowerCase())){
                        final View alertLayout = getLayoutInflater().inflate(R.layout.number_input, null);
                        alertdialoghelper.number_input(getActivity(),alertLayout,shownumber,warntv);
                    }
                }
            });

            if (ismedical.equals("1")){
                instantbook.setVisibility(View.GONE);
                nomalbook.setText("Book");
                nomalbook.getLayoutParams().width= LinearLayout.LayoutParams.MATCH_PARENT;
                nomalbook.requestLayout();
                warntv.setText("Charges ksh 10");
            }
            nomalbook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout ln=contentView.findViewById(R.id.ll);
                    if (ismedical.equals("1")){
                        instantbook.setVisibility(View.GONE);
                    }
                    if(new Dbhelper(getContext()).check_user()!=0){
                        String bt = "";
                        if (ismedical.equals("1")){
                            isinstant="t";
                            bt = "instant";
                        }
                        else {
                            isinstant="";
                            bt = "normal";
                        }

                        bookclicked(contentView,nomalbook,bt);
                    }
                    else {
                        warntv.setVisibility(View.VISIBLE);
                        warntv.setText("No existing account");
                    }

                }
            });
            spinner=contentView.findViewById(R.id.spinner_serv_offered);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (spinner.getSelectedItem().equals("Select a service")){
//                        tvnumberpeople.setText("Select a service to check queue status/size");
                    }
                    else if(spinner.getSelectedItem().equals("No Services Available")){

                    }
                    else {
                        new Checkinfront(Integer.parseInt(b_id),spinner.getSelectedItem().toString()).execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            shownumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvaddnumber.setText(add_no);
                    if (!ismedical.equals("1")){
                        warntv.setText(getResources().getString(R.string.booking_info));
                    }
                    else {

                    }
                    shownumber.setText("");
//                    shownumber.setVisibility(View.INVISIBLE);
                    instantbook.setText("instant booking");
                    nomalbook.setText("normal booking");
                }
            });

            new get_services_offered(contentView).execute();

            instantbook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    nomalbook.setVisibility(View.GONE);
                    if(new Dbhelper(getContext()).check_user()!=0){
                        isinstant="t";

                        bookclicked(contentView,instantbook,"instant");
                    }
                    else {
                        warntv.setVisibility(View.VISIBLE);
                        warntv.setText("No existing account");

                    }
//                                branch id,bookingtime,user id,service selected, instant or not instant
//                    new make_booking(getActivity(),b_id,booking_time_format.format(nowcalendar.getTimeInMillis()),new Dbhelper(getContext()).get_user_id(),spinner_text).execute();
//                            dismiss();
                }
            });
            TextView sel_ser=contentView.findViewById(R.id.select_service);
//            sel_ser.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final View alertLayout = getLayoutInflater().inflate(R.layout.layout_services_offered, null);
//                    new alertdialoghelper().select_service(getContext(),alertLayout,b_id);
//                }
//            });


            timetv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            book_hour=selectedHour;
                            book_min=selectedMinute;

                            mcurrentTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                            mcurrentTime.set(Calendar.MINUTE,selectedMinute);
                            SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            SimpleDateFormat hFormat = new SimpleDateFormat("hh", Locale.getDefault());
                            SimpleDateFormat aFormat = new SimpleDateFormat("a", Locale.getDefault());
                            timetv.setText(mFormat.format(mcurrentTime.getTimeInMillis()));
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

//            DATE PICKER
            datetv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar cldr = Calendar.getInstance();
                    int day = cldr.get(Calendar.DAY_OF_MONTH);
                    int month = cldr.get(Calendar.MONTH);
                    int year = cldr.get(Calendar.YEAR);
                    // date picker dialog
                    picker = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    book_year=year;
                                    book_month=monthOfYear;
                                    book_day=dayOfMonth;
                                    Calendar calendar=Calendar.getInstance();
                                    calendar.set(Calendar.YEAR,book_year);
                                    calendar.set(Calendar.MONTH,book_month);
                                    calendar.set(Calendar.DAY_OF_MONTH,book_day);
                                    final SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                                    datetv.setText(dateFormat.format(calendar.getTimeInMillis()));
                                }
                            }, year, month, day);
                    picker.show();
                }
            });
            CoordinatorLayout.LayoutParams layoutParams =
                    (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
            CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
            if (behavior != null && behavior instanceof BottomSheetBehavior) {
                ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            }

            book_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isinstant="";

                   bookclicked(contentView,book_button,"normal");
                }
            });
        }
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
        public boolean verifynumber(){
            String strnumber=editnumber.getText().toString();

            if (editnumber.getText().toString().length()==9)
            {
                if (editnumber.getText().toString().startsWith("0")){
                    Toast.makeText(getContext(), "Check your number and try again", Toast.LENGTH_SHORT).show();
                    return false;
                }

                else {
//                    shownumber.setText(showcontrycode.getText().toString()+strnumber);
//                    Drawable img = getResources().getDrawable(R.drawable.ic_close_black_24dp);
//                    shownumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
//                    shownumber.setVisibility(View.VISIBLE);
//                        continue_book.setText("Confirm "+continue_book.getText().toString().toLowerCase().substring(0,continue_book.getText().toString().toLowerCase().indexOf(" ")));

//                        if (booktype.equals("normal")){
//                            showinfo.setText("Normal booking selected confirm to pay "+activity.getResources().getString(R.string.normal_amount));
//                        }
//                        else {
//                            showinfo.setText("Instant booking selected confirm to pay "+activity.getResources().getString(R.string.instant_amount));
//
//
//                        }
                    return true;
                }
            }
            else if (editnumber.getText().toString().length()<9){
                Toast.makeText(getContext(), "Check your number and try again", Toast.LENGTH_SHORT).show();
                return false;
            }
            return false;
        }

        private  void setup_service_adapter(final View main_view){
//            adapter = new service_offered_adapter(getActivity(),R.layout.spinner_services_items,servicesOfferedDetailsArrayList);
            ArrayAdapter arrayAdapter=new ArrayAdapter(getContext(),R.layout.spinner_services_items,R.id.textview_services,list_sername);
            spinner.setAdapter(arrayAdapter);
        }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
        private void bookclicked(View contentView,TextView textViewedit,String booktype){
            SimpleDateFormat format;

            hideKeyboardFrom(getContext(),contentView);
            if (scloset.length()<6){
                format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            }
            else {
                format=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            }

            SharedPreferences prefs = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
            if (prefs.getAll().size()!=0){
               SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);

                sweetAlertDialog.setTitleText("Incomplete Transaction.")
                        .setContentText("Verify transaction if paid or Cancel to book again")
                        .setCancelButton("Cancel Transaction", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                    dismiss();
                                if (prefs.getAll().size()==0){
//                                        Snackbar.make(getView(),"Sorry no transaction is available!",Snackbar.LENGTH_SHORT).show();
                                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Sorry no transaction available!")
                                            .setContentText("click to exit")
                                            .show();
                                }
                                else {
                                    SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
                                    editor.clear();
                                    editor.commit();
                                    sweetAlertDialog.dismiss();
                                    Toast.makeText(getContext(), "Transaction Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setConfirmButton("Verify Transaction", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                prefs = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
                                String phonenumber = prefs.getString("phonenumber", "No token defined");
                                String branch_id = prefs.getString("branch_id", "branch_id undefined");
                                String start = prefs.getString("start", "start undefined");
                                String service_name = prefs.getString("service_name", "sername undefined");
                                String user_id = prefs.getString("user_id", "user_id undefined");
                                String isinstant = prefs.getString("is_instant", "is_instant undefined");
                                String company_name = prefs.getString("company_name", "company undefined");
                                String branch_name = prefs.getString("branch_name", "branch undefined");
                                String token = prefs.getString("token", "token undefined");
                                new verifypayment_pending(getContext(),phonenumber,branch_id,start,service_name,user_id,isinstant,company_name,branch_name,token,"new").execute();
                                sweetAlertDialog.dismiss();

                            }
                        })
                        .show();
                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));

            }else {
                Calendar calendar_closet=Calendar.getInstance();
                SimpleDateFormat datecalecalendar=new SimpleDateFormat("dd/MM/yyyy");
                String date_string=datecalecalendar.format(calendar_closet.getTimeInMillis());

                String date_time=date_string+" "+scloset;
                String sopen_date=date_string+" "+sopent;

                try {
                    close_date = format.parse(date_time);
                    open_date = format.parse(sopen_date);
                    Log.d(TAG, "setupDialog: closing time formated to date "+close_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(TAG, "setupDialog: time conversion error   " + e);
                }

                Calendar book_milis=Calendar.getInstance();
//
                booking_milis=book_milis.getTimeInMillis();
//                        validate time if is less than today time now

                if (spinner.getSelectedItem().equals("Select a service")){
                    Toast.makeText(getContext(), "Please select a service to continue booking!", Toast.LENGTH_LONG).show();
                }
                else {
//
//                Date book_date=new Date();
//                book_date.setTime(booking_milis);

                    if (spinner.getSelectedItem().equals("No Services Available")){
                        spinner_text="";
                    }
                    else {
                        spinner_text=spinner.getSelectedItem().toString();
                    }

                    SimpleDateFormat booking_time_format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    booking_time_format.setTimeZone(TimeZone.getDefault());
                    booking_milis=book_milis.getTimeInMillis();
//                                branch id,bookingtime,user id,service selected, instant or not instant
//                                setalarm(getActivity(),2);
                    if(book_milis.after(close_date.getTime()) || book_milis.before(open_date.getTime())){
                        notopenmsg="This place is closed at this time book made for Tomorrow";
                    }
                    if (editnumber.getText().length()==0){
                        Toast.makeText(getContext(), "Please add a phone number to continue", Toast.LENGTH_LONG).show();
//                    final View alertLayout = getLayoutInflater().inflate(R.layout.number_input, null);
//                    alertdialoghelper.number_input(getActivity(),alertLayout,shownumber,textViewedit,warntv,booktype);
                    }
                    else {
                        if (verifynumber()){
                            String completenumber=showcontrycode.getText().toString()
                                    +editnumber.getText().toString();

                            new make_booking(getActivity(),b_id,booking_time_format.format(booking_milis),new Dbhelper(getContext()).get_user_id(),spinner_text,completenumber).execute();
//                        new make_booking(getActivity(),b_id,booking_time_format.format(booking_milis),new Dbhelper(getContext()).get_user_id(),spinner_text,"254740212762").execute();
                        }
                        else {

                        }
//                    new makepayment(contentView,completenumber).execute();

                    }
//                            dismiss();
                    final SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                    dateFormat.setTimeZone(TimeZone.getDefault());
                    Log.d(TAG, "onClick: booked---------------"+dateFormat.format(booking_milis));

                }
            }

        }


//        private void setalarm(Activity activity,int booking_id){
//
//            Calendar calendar=Calendar.getInstance();
//            calendar.setTimeInMillis(booking_milis);
//            calendar.add(Calendar.SECOND,new strings_().alaarmwaittime());
//            new SimpleAlarmManager(activity)    //set alarm
//                    .setup(1,booking_milis)
//                    .register(booking_id)
//                    .start();
//            Log.d(TAG, "doInBackground: alarm set"+  new SimpleAlarmManager(activity)    //set alarm
//                    .setup(1,booking_milis)
//                    .register(booking_id)
//                    .show_mili());
//        }
    private class make_booking extends AsyncTask<String, String, String> {
            Activity activity;
            String branch_id,
                    start,
                    institution,
                    user_id,
                    service_name,
                    phonenumber,token;


            public make_booking(final Activity activity_c, final String branch_id, final String start,final String user_id,String service_name,String phonenumber){
                this.phonenumber=phonenumber;
                this.branch_id=branch_id;
                this.start=start;
                this.user_id=user_id;
                activity=activity_c;
                this.service_name=service_name;

            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                pDialog = new ProgressDialog(getContext());
//                pDialog.setMessage("Verifying details. Please wait...");
//                pDialog.setIndeterminate(false);
//                pDialog.setCancelable(false);
//                pDialog.show();

                if (isinstant.equals("")){
                    nomalbook.startAnimation();
                    instantbook.setClickable(false);

                }
                else{
                    instantbook.startAnimation();
                    nomalbook.setClickable(false);
                }
                error_disp.setText("");
            }
            @SuppressLint("WrongThread")
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
                    HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/book/make");
                    json.put("branch_id", branch_id);
                    json.put("start", start);
                    json.put("service_name",service_name);
                    json.put("user_id",user_id);
                    json.put("is_instant","");
                    json.put("phonenumber",phonenumber);
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    Log.d(TAG, "doInBackground: make booking details put  "+ branch_id+" "+start+" "+service_name+" "+user_id+" "+isinstant+" "+phonenumber);

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
//                              jsonarray = new JSONArray(result);

                                token = "null";
//                                InputStream in = response.getEntity().getContent(); //Get the data in the entity
//                                bookingDetails.setCo_name(txtcompanyname.getText().toString());
//                                bookingDetails.setB_name(txtbranch.getText().toString());
//                                bookingDetails.setTime(String.valueOf(booking_milis));
//                                bookingDetails.setBooking_id(obj.getString("booking_id"));
//                                bookingDetails.setBranch_id(b_id);
//                                bookingDetails.setService_name(service_name);
//                                bookingDetails.setServiced("0");





//                                setalarm(activity,Integer.parseInt(obj.getString("booking_id")));
//                                new SimpleAlarmManager(activity)    //set alarm
//                                        .setup(1,booking_milis)
//                                        .register(Integer.parseInt(obj.getString("booking_id")))
//                                        .start();
//                                Log.d(TAG, "doInBackground: alarm set"+  new SimpleAlarmManager(activity)    //set alarm
//                                        .setup(1,booking_milis)
//                                        .register(Integer.parseInt(obj.getString("booking_id")))
//                                        .show_mili());
//                                new Dbhelper(activity).insert_booking(bookingDetails);
                                Log.d(TAG, "start _booking details "+ " json: "+json);


                            } catch (Throwable t) {
                                Log.e("My App", "Could not parse malformed JSON: start_booking \"" + result + "\""+t.getMessage());
                                error="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";
                            }

                        } catch (Exception e) {
                            Log.e("log_tag", "Error converting result " + e.toString());
                            error="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";
                        }
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: book connection error" );
                    error="Couldn't book \n\n There was a problem communicating with the servers. \n\n Try again later.";
//                    pDialog.dismiss();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (error.equals(" ")){
//                    refreshnavview();
                    SharedPreferences prefs = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
                    if (prefs.getAll().size()!=0){
                       SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);
                       sweetAlertDialog.setTitleText("Incomplete Transaction.")
                               .setContentText("Verify transaction if paid or Cancel to book again")
                               .setCancelButton ("Cancel Transaction", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                    dismiss();
                                    if (prefs.getAll().size()==0){
//                                        Snackbar.make(getView(),"Sorry no transaction is available!",Snackbar.LENGTH_SHORT).show();
                                        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Sorry no transaction available!")
                                                .setContentText("click to exit")
                                                .show();
                                    }
                                    else {
                                        SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
                                        editor.clear();
                                        editor.commit();
                                        sweetAlertDialog.dismiss();
                                        Toast.makeText(getContext(), "Transaction Cancelled", Toast.LENGTH_SHORT).show();

                                    }
                                }})
                                .setConfirmButton("Verify Transaction", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                prefs = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE);
                                        String phonenumber = prefs.getString("phonenumber", "No token defined");
                                        String branch_id = prefs.getString("branch_id", "branch_id undefined");
                                        String start = prefs.getString("start", "start undefined");
                                        String service_name = prefs.getString("service_name", "sername undefined");
                                        String user_id = prefs.getString("user_id", "user_id undefined");
                                        String isinstant = prefs.getString("is_instant", "is_instant undefined");
                                        String company_name = prefs.getString("company_name", "company undefined");
                                        String branch_name = prefs.getString("branch_name", "branch undefined");
                                        String token = prefs.getString("token", "token undefined");
                                        new verifypayment_pending(getContext(),phonenumber,branch_id,start,service_name,user_id,isinstant,company_name,branch_name,token,"new").execute();
                                        sweetAlertDialog.dismiss();

                                    }
                                })
                                .show();
                        sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));

                    }
                    else{
                        Toast.makeText(activity, "Proceeding to Payment...", Toast.LENGTH_SHORT).show();
                        socket_init(token,phonenumber,branch_id,service_name);
                        dismiss();
//                        SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.SUCCESS_TYPE);
//                        sweetAlertDialog
//                                .setTitleText("Verification Successful")
//                                .setContentText("Booking a ticket for you, wait for notification")
//                                .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                        sweetAlertDialog.dismissWithAnimation();
//                                        refreshnavview();
//                                    }
//                                })
//
//                                .show();
//                        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialogInterface) {
//                                        refreshnavview();
//                                        dismiss();
//                                    }
//                                });
//                        sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.catcho_primary_dark));
//
//                        sweetAlertDialog.getButton(R.id.confirm_button).setTextColor(activity.getResources().getColor(R.color.colorPrimary));
//                        final Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                sweetAlertDialog.dismissWithAnimation();
//                                refreshnavview();
//
//                            }
//                        }, 5000);

                        SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
                        editor.putString("start", String.valueOf(booking_milis));
                        editor.putString("user_id", new Dbhelper(activity).get_user_id());
                        editor.putString("branch_id", b_id);
                        editor.putString("service_name", service_name);
                        editor.putString("is_instant", isinstant);
                        editor.putString("phonenumber", phonenumber);
                        editor.putString("token", token);
                        editor.putString("company_name", txtcompanyname.getText().toString());
                        editor.putString("branch_name",txtbranch.getText().toString());
                        editor.commit();
//                        insert_in_db(txtcompanyname.getText().toString(),txtbranch.getText().toString(),token+"_"+phonenumber,branch_id,service_name,isinstant);

//                        activity.start+-Service(new Intent(activity, PM_verify_service.class));
//                        dismiss();
                    }
                }
                else{
                    SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.commit();

                    SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(activity,SweetAlertDialog.WARNING_TYPE);
                    sweetAlertDialog
                            .setTitleText(error)
                            .setContentText("click ok to exit")
                            .setConfirmButton("OK ", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                    sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
                }
//                nomalbook.doneLoadingAnimation(activity.getResources().getColor(R.color.colorPrimary),drawableToBitmap(activity.getResources().getDrawable(R.drawable.ic_done_black_24dp)));
                if (isinstant.equals("")){
                    nomalbook.revertAnimation();
                    instantbook.setClickable(true);
                }
                else{
                    instantbook.revertAnimation();
                    nomalbook.setClickable(true);

                }


            }
        }




//    Socket mSocket;
    Context context;
    String[] dictionary={"name"};
    JSONObject jsonObject;
    String phonenumber=" ";
//    Socket mSocket;
    Socket socket;
    boolean socket_connected;
    private void socket_init(String token,String phonenumber,String branch_id,String service_name) {

        IO.Options options = new IO.Options();
        socket = IO.socket(URI.create("http://159.65.144.235:5000"), options); // the main namespace
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(socket.connected()); // true
                Log.e(TAG, "call: socket connected");
                socket_connected = true;
            }
        });
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(socket.connected()); // false
                Log.e(TAG, "call: socket disconnected");
                socket_connected = false;
            }
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
//                    options.auth.put("authorization", "bearer 1234");
                socket.connect();
                Log.e(TAG, "call: socket error " + args[0]);
            }
        });
        socket.on("mpesa_verify_data", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: mpesa_verify_data listening" );
                try {
                    JSONObject data = null;

                    String phone_number, verify_token;
                    data = new JSONObject((String) args[0]);
                    phone_number = data.getString("phone_number");

                    if (phone_number.equals(phonenumber)) {

                        verify_token = data.getString("token");
                        SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
                        editor.putString("token", verify_token);
                        editor.commit();
                        activity.startService(new Intent(activity, PM_verify_service.class));
                        socket.disconnect();
                        socket.off("mpesa_verify_data");
                    }
                    Log.e(TAG, "listener: " + data.toString());
                } catch (JSONException e) {
                    socket.disconnect();
                    socket.off("mpesa_verify_data");
                    insert_in_db(txtcompanyname.getText().toString(), txtbranch.getText().toString(), token + "_" + phonenumber, branch_id, service_name, isinstant);
                    e.printStackTrace();
                }
            }
        });
//        if (socket_connected){
//            payment_status_listener(token,phonenumber,branch_id,service_name);
//
//        }
                socket.connect();
//        SocketInstance instance = (SocketInstance)getActivity().getApplication();
//        mSocket = instance.getSocketInstance();
//        mSocket.connect();
//        this.phonenumber = phonenumber;
//        if (mSocket.connected()){
//            Toast.makeText(activity, "Socket Connected!!",Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "onCreateView: ,Socket connected" );
//        }
//        payment_status_listener(token,phonenumber,branch_id,service_name);

//
    }
    private void payment_status_listener(String token,String phonenumber,String branch_id,String service_name){
//        Toast.makeText(activity, "listener started", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "payment_status_listener: "+token);
        socket.on("mpesa_verify_data", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = null;

                            String phone_number,verify_token;
                            data = new JSONObject((String) args[0]);
                            phone_number = data.getString("phone_number");

                            if(phone_number.equals(phonenumber)){

                                verify_token = data.getString("token");
                                SharedPreferences.Editor editor = activity.getSharedPreferences("PAYMENT_VERIFICATION", MODE_PRIVATE).edit();
                                editor.putString("token", verify_token);
                                editor.commit();
                                activity.startService(new Intent(activity, PM_verify_service.class));
                                socket.disconnect();
                                socket.off("mpesa_verify_data");
                            }
                            Log.e(TAG, "listener: "+data.toString());
                        } catch (JSONException e) {
                            socket.disconnect();
                            socket.off("mpesa_verify_data");
                            insert_in_db(txtcompanyname.getText().toString(),txtbranch.getText().toString(),token+"_"+phonenumber,branch_id,service_name,isinstant);
                            e.printStackTrace();
                        }
                    }
                });
//                Toast.makeText(activity, "listening", Toast.LENGTH_SHORT).show();

//                Log.e(TAG, "calllistener: "+data );
            }
        });
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
    public void refreshnavview(){
        BottomNavigationView navView=activity.findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_queue);
//        navView.getOrCreateBadge(R.id.navigation_order).setNumber(new Dbhelper(getContext()).getactivebookings());
    }

    private class get_services_offered extends AsyncTask<String, String, String>{
            View view;
        String no_items;
        TextView show_loading;
            public get_services_offered(View view){
                this.view=view;
            }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            show_loading=view.findViewById(R.id.select_service);
            show_loading.setVisibility(View.VISIBLE);
            show_loading.setText("Loading services.....");
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage("Loading services offered");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();

            nomalbook.setVisibility(View.INVISIBLE);
            instantbook.setVisibility(View.INVISIBLE);
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/services/get/all");
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
                            if (list_sername.size()==0){
                                no_items="No Services Available";
//                                list_sername.add(no_items);
                            }
                            else{
                                no_items="Select a service";
                            }

//                                pDialog.dismiss();
//                                Toast.makeText(activity, "Book made...", Toast.LENGTH_SHORT).show();

////
                            Log.d(TAG, "services ordered by branch id="+b_id+"---->  "+ list_sername.size());



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
                list_sername.add(0,no_items);
                try {

                    setup_service_adapter(view);

                }catch (Exception e){
                    Log.d(TAG, "onPostExecute: setup bottomsheet adapter"+e.toString());
                }
                show_loading.setVisibility(View.GONE);
                nomalbook.setVisibility(View.VISIBLE);
                instantbook.setVisibility(View.VISIBLE);

            }
            else {
                show_loading.setText("Click to refresh");
                show_loading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new get_services_offered(view).execute();
                    }
                });
                SweetAlertDialog sweetAlertDialog=new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog
                        .setTitleText("Sorry, services could not be loaded")
                        .setContentText("click ok to exit")
                        .setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                dismiss();
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
                sweetAlertDialog.findViewById(R.id.confirm_button).setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_color));
//                dismiss();
            }

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
        ArrayList<String> tellers = new ArrayList<>();

        public Checkinfront(int id, String servicename){
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
            progressBar.setVisibility(View.VISIBLE);
            tvqueuesizeerror.setVisibility(View.GONE);

//            tvnumberpeople.setText("Checking queue size...");
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(activity)+"/ahead/of/you");
                json.put("branch_id", id);
                json.put("service_name",servicename);
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
                            infront=obj.getInt("infront");
//                            infront = 1;
                            JSONArray teller = obj.getJSONArray("tellers");
                            JSONObject tellersobj= null;
                            for (int i=0;i<teller.length();i++){
                                tellersobj = teller.getJSONObject(i);
                                tellers.add(tellersobj.getString("number"));
                            }
//                            Log.d(TAG, "doInBackground: infront="+infront+"branc_id="+id+" object="+tellers.get(0));
                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: services offered check infront \"" + result + "\""+t.getMessage());
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
            progressBar.setVisibility(View.GONE);
            if (error_serv.equals(" ")){
                StringBuilder strlellerlist = new StringBuilder();

                if (tellers.size()==1){
                    strlellerlist.append(tellers.get(0));
                }
                else {
                    for (int i=0; i<tellers.size(); i++){
                    strlellerlist.append(tellers.get(i)+",");
                }
                }

                Log.d(TAG, "onPostExecute: check infront"+infront);
                if (infront==0){
                    tvnumberpeople.setText("First to be served");
                    tvlabelqueuesize.setVisibility(View.INVISIBLE);
                    tvtellerno.setText(strlellerlist.toString());

                }
                else if(infront==1){
                    tvnumberpeople.setText(infront+ " person");
                    tvtellerno.setText(strlellerlist.toString());
                    tvlabelqueuesize.setVisibility(View.VISIBLE);
                }
                else{
                    tvnumberpeople.setText(infront+ " people");
                    tvtellerno.setText(strlellerlist.toString());
                    tvlabelqueuesize.setVisibility(View.VISIBLE);
                }

            }
            else {
                Log.d(TAG, "onPostExecute: check infront"+error_serv);
//                tvnumberpeople.setText(error_serv);
                tvqueuesizeerror.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "onPostExecute: "+error_serv);
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
            SharedPreferences prefs;
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

        }
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



//    protected void sendJson(final Activity activity, final String branch, final String start, final String institution, final String user_id) {
//        Thread t = new Thread() {
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
//                    HttpPost post = new HttpPost(new strings_().url()+"/book/make");
//                    json.put("branch", branch);
//                    json.put("start", start);
//                    json.put("institution",institution);
//                    json.put("user",user_id);
//                    StringEntity se = new StringEntity( json.toString());
//                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                    post.setEntity(se);
//                    response = client.execute(post);
//                    HttpEntity entity = response.getEntity();
//                    is = entity.getContent();
//
//                    /*Checking response */
//                    if(response!=null){
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
//                            booking_details bookingDetails=new booking_details();
//                            try {
//                                obj= new JSONObject(result);
////                              jsonarray = new JSONArray(result);
//
////                                InputStream in = response.getEntity().getContent(); //Get the data in the entity
//
//                                bookingDetails.setCo_name(txtcompanyname.getText().toString());
//                                bookingDetails.setB_name(txtbranch.getText().toString());
//                                bookingDetails.setTime(String.valueOf(booking_milis));
//                                bookingDetails.setBooking_id(obj.getString("booking_id"));
//                                new Dbhelper(activity).insert_booking(bookingDetails);
//////
////
//
////                                pDialog.dismiss();
//                                Toast.makeText(activity, "Book made...", Toast.LENGTH_SHORT).show();
//////
//                                Log.d(TAG, "start _booking details "+ obj.getString("booking_id")+ " json: "+json);
//
//
//
//                            } catch (Throwable t) {
//                                Log.e("My App", "Could not parse malformed JSON: start_booking \"" + obj + "\""+t.getMessage());
//                                pDialog.dismiss();
//
////                                Log.e("My App", "Could not parse malformed JSON: \"" + json + "\""+t.getMessage());
//                            }
//
//
////                            Log.d(TAG, "run: string" + result+new Dbhelper(activity).get_user_id());
//
//                        } catch (Exception e) {
//                            Log.e("log_tag", "Error converting result " + e.toString());
////                            pDialog.dismiss();
//
//                        }
//
//
//
//                    }
//
//                } catch(Exception e) {
//                    e.printStackTrace();
////                    Toast.makeText(getActivity(), "Cannot Estabilish Connection", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "run: book connection error" );
////                    pDialog.setTitle("Connection Error");
////                    pDialog.dismiss();
//
////                    createDialog("Error", "Cannot Estabilish Connection");
//                }
//
//                pDialog.dismiss();
//
//                Looper.loop(); //Loop in the message queue
//            }
//        };
//
//        t.start();
//    }









//    SimpleDateFormat format;
//
//    Calendar calendar_closet;
//    String date_time=datetv.getText().toString()+" "+scloset;
//    String sopen_date=datetv.getText().toString()+" "+sopent;
//
//                    if (scloset.length()<6){
//        format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//    }
//                    else {
//        format=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
//    }
//
//                    try {
//        close_date = format.parse(date_time);
//        open_date = format.parse(sopen_date);
//        Log.d(TAG, "setupDialog: closing time formated to date "+close_date);
//    } catch (ParseException e) {
//        e.printStackTrace();
//        Log.d(TAG, "setupDialog: time conversion error   " + e);
//    }
//
//                    if (timetv.getText().toString().length()<1||datetv.getText().length()<1){
//        Toast.makeText(getContext(), "Please ensure Date and Time is Selected", Toast.LENGTH_SHORT).show();
//    }
//                    else
//    {
//
//        Calendar now=Calendar.getInstance();
////                        now.add(Calendar.MINUTE,new Dbhelper(getContext()).getnotidelay());
//        Calendar book_milis=Calendar.getInstance();
////
//        booking_milis=book_milis.getTimeInMillis();
////                        validate time if is less than today time now
//
//        if (booking_milis<=now.getTimeInMillis()){
//            Toast.makeText(getContext(), "Book for an upcoming date and time", Toast.LENGTH_LONG).show();
//        }
//        else if (spinner.getSelectedItem().equals("Select a service")){
//            Toast.makeText(getContext(), "Please select a service to continue booking!", Toast.LENGTH_LONG).show();
//        }
//
//        else {
//
//            Date book_date=new Date();
//            book_date.setTime(booking_milis);
//
////                            if (book_date.after(close_date) || book_date.before(open_date)) {
////                                Toast.makeText(getContext(), "The place is not open yet", Toast.LENGTH_SHORT).show();
////                            String test="test";
////                            if (!test.equals("test")){
////                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
////                                        .setTitleText("Oops...")
////                                        .setContentText("The place will not be opened at that booked time!")
////                                        .show();
////                            }
//            if (spinner.getSelectedItem().equals("No Services Available")){
//                spinner_text="banking";
//            }
//            else {
//                spinner_text=spinner.getSelectedItem().toString();
//
//            }
////                                new LoadAllProducts_booking().execute();
////                            sendJson(getActivity(),txtaddress.getText().toString(), String.valueOf(booking_milis),"101",new Dbhelper(getContext()).get_user_id());
//            SimpleDateFormat booking_time_format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            booking_time_format.setTimeZone(TimeZone.getDefault());
//            booking_milis=book_milis.getTimeInMillis();
//
////                                branch id,bookingtime,user id,service selected, instant or not instant
////                                setalarm(getActivity(),2);
//            isinstant="";
//            if(book_milis.after(close_date) || book_milis.before(open_date)){
//                notopenmsg="The institute is closed at this time book made for Tomorrow";
//            }
//            new make_booking(getActivity(),b_id,booking_time_format.format(booking_milis),new Dbhelper(getContext()).get_user_id(),spinner_text).execute();
////                            dismiss();
//            final SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
//            dateFormat.setTimeZone(TimeZone.getDefault());
//            Log.d(TAG, "onClick: booked---------------"+dateFormat.format(booking_milis));
//
//        }
//    }



}



