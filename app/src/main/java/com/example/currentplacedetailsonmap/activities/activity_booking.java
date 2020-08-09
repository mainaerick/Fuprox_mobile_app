package com.example.currentplacedetailsonmap.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.currentplacedetailsonmap.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class activity_booking extends AppCompatActivity {
    DatePickerDialog picker;
    static ProgressDialog pDialog;
    public  static String TAG="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


       /* final TextView tvbooktime=findViewById(R.id.booking_time_activity),tvbookdate=findViewById(R.id.booking_date_activity);
        tvbooktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(activity_booking.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mcurrentTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                        mcurrentTime.set(Calendar.MINUTE,selectedMinute);
                        SimpleDateFormat mFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                        SimpleDateFormat hFormat = new SimpleDateFormat("hh", Locale.getDefault());
                        SimpleDateFormat aFormat = new SimpleDateFormat("a", Locale.getDefault());

                        tvbooktime.setText(mFormat.format(mcurrentTime.getTimeInMillis()));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        tvbookdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(activity_booking.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvbookdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }


        });

        Button confirm_booking=findViewById(R.id.booking_confirm_activity);
        confirm_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadAllProducts_booking().execute();
            }
        });

*/


    }


    private class LoadAllProducts_booking extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity_booking.this);
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
//            booking_details bookingDetails=new booking_details();
            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//
////                post_params.add(new BasicNameValuePair("branch",bookingDetails.getB_name()));
////                post_params.add(new BasicNameValuePair("start",bookingDetails.getTime()));
////                post_params.add(new BasicNameValuePair("institution",String.valueOf(bookingDetails.getId())));
////
//            post_params.add(new BasicNameValuePair("branch","1"));
//            post_params.add(new BasicNameValuePair("start","start"));
//            post_params.add(new BasicNameValuePair("user",  "1"));
////
//            post_params.add(new BasicNameValuePair("institution","3"));
////
////
//            com.example.currentplacedetailsonmap.model.strings_ strings1=new strings_();// url getiing
//
////                 getting JSON Object
////                 Note that create product url accepts POST method
//            final JSONParser jParser_booking = new JSONParser();
//            JSONObject json_post_course = jParser_booking.makeHttpRequest("http://192.168.137.181:4000/book/make",
//                    "POST", post_params);
//
//
//
//            Log.d(TAG, "doInBackground_booking: "+ json_post_course.toString()+"---------- url "+strings1.book_url());
//
//            return null;


//
//            String Jsonresponse = null;
//
//
//            String Jsondata = null;
//
//            for (int i=0 ;i<post_params.size();i++){
//                Jsondata= post_params.toString();
//            }
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//            try {
//
//                URL url = new URL("http://192.168.43.162:4000/book/make");
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoOutput(true);
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setConnectTimeout(10000);
//                urlConnection.setRequestProperty("Accept","application/json");
//                urlConnection.setRequestProperty("Content-Type","application/json");
//
//                //Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
//                //writer.write(Jsondata);
//                // writer.close();
//
//                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream ());
//                printout.writeBytes("PostData=" + Jsondata);
//                printout.writeBytes(Jsondata);
//                Log.e("json", Jsondata);
//
//                // printout.flush ();
//                // printout.close ();
//
//                DataInputStream in = new DataInputStream(urlConnection.getInputStream());
//                Jsonresponse = convertStreamToString(in);
//                Log.e("json", Jsonresponse);
//
//
//
//
//                return Jsonresponse;
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            sendJson("1","start","2","1");



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            pDialog.dismiss();
        }
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    protected void sendJson(final String branch, final String start, final String institution, final String user_id) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost("http://192.168.43.162:4000/book/make");
                    json.put("branch", branch);
                    json.put("start", start);
                    json.put("institution",institution);
                    json.put("user",user_id);
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    }

                } catch(Exception e) {
                    e.printStackTrace();
//                    createDialog("Error", "Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }
}
