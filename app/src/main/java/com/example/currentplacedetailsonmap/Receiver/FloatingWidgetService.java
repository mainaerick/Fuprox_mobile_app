package com.example.currentplacedetailsonmap.Receiver;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.andremion.counterfab.CounterFab;
import com.example.currentplacedetailsonmap.MainActivity;
import com.example.currentplacedetailsonmap.R;
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

import static com.example.currentplacedetailsonmap.Receiver.AlarmReceiver.TAG;

/**
 * Created by anupamchugh on 01/08/17.
 */

public class FloatingWidgetService extends Service {

    String booking_id;

    private WindowManager mWindowManager;
    private View mOverlayView;
    int mWidth;
    CounterFab counterFab;
    TextView details,servicename,closefab;
    boolean activity_background;
    LinearLayout baubledetaillayout;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            activity_background = intent.getBooleanExtra("activity_background", false);
            booking_id = intent.getStringExtra("booking_id");

        }

        if (mOverlayView == null) {

            mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
            int type;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }

            else{
                type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }

            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    type,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);



            //Specify the view position
            params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mOverlayView, params);

            Display display = mWindowManager.getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);

            counterFab = mOverlayView.findViewById(R.id.fabHead);
            details = mOverlayView.findViewById(R.id.bubble_detail);
            servicename =mOverlayView.findViewById(R.id.tvservicename);
            closefab = mOverlayView.findViewById(R.id.close_fab);
            baubledetaillayout = mOverlayView.findViewById(R.id.baubledetaillayout);
            closefab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopSelf();
                }
            });
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FloatingWidgetService.this, MainActivity.class);
                                    intent.putExtra("book_id", booking_id);
                                    intent.putExtra("verify","verify");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                    stopSelf();
                }
            });

            new Checkinfront(booking_id).execute();

            final RelativeLayout layout = mOverlayView.findViewById(R.id.layout);
            ViewTreeObserver vto = layout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = layout.getMeasuredWidth();

                    //To get the accurate middle of the screen we subtract the width of the floating widget.
                    mWidth = size.x - width;

                }
            });

            counterFab.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;


                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();


                            return true;
                        case MotionEvent.ACTION_UP:

                            //Only start the activity if the application is in background. Pass the current badge_count to the activity
                            if (activity_background) {

                                float xDiff = event.getRawX() - initialTouchX;
                                float yDiff = event.getRawY() - initialTouchY;

                                if ((Math.abs(xDiff) < 5) && (Math.abs(yDiff) < 5)) {
//                                    Intent intent = new Intent(FloatingWidgetService.this, MainActivity.class);
//                                    intent.putExtra("booking_id", booking_id);
//                                    intent.putExtra("verify","verify");
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
                                    if (baubledetaillayout.getVisibility()==View.VISIBLE){
                                        baubledetaillayout.setVisibility(View.GONE);
                                        layout.setBackgroundResource(0);
                                    }
                                    else {
//                                        String det_=new Dbhelper(FloatingWidgetService.this).get_booking_by_id(booking_id).get(0).getCo_name()+
//                                                "\n for "+
//                                                new Dbhelper(FloatingWidgetService.this).get_booking_by_id(booking_id).get(0).getService_name();
                                        String det_=new Dbhelper(FloatingWidgetService.this).get_booking_by_id(booking_id).get(0).getCo_name();
                                        String service_name= new Dbhelper(FloatingWidgetService.this).get_booking_by_id(booking_id).get(0).getService_name();;
                                        servicename.setText(service_name);
                                        details.setText(det_);
                                        baubledetaillayout.setVisibility(View.VISIBLE);

                                        layout.setBackground(getResources().getDrawable(R.drawable.border));
                                    }
                                    //close the service and remove the fab view
//                                    stopSelf();
                                }

                            }
                            //Logic to auto-position the widget based on where it is positioned currently w.r.t middle of the screen.
                            int middle = mWidth / 2;
                            float nearestXWall = params.x >= middle ? mWidth : 0;
                            params.x = (int) nearestXWall;


                            mWindowManager.updateViewLayout(mOverlayView, params);


                            return true;
                        case MotionEvent.ACTION_MOVE:


                            int xDiff = Math.round(event.getRawX() - initialTouchX);
                            int yDiff = Math.round(event.getRawY() - initialTouchY);


                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + xDiff;
                            params.y = initialY + yDiff;

                            //Update the layout with new X & Y coordinates
                            mWindowManager.updateViewLayout(mOverlayView, params);


                            return true;
                    }
                    return false;
                }
            });
        } else {

            new Checkinfront(booking_id).execute();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.AppTheme);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOverlayView != null)
            mWindowManager.removeView(mOverlayView);
    }


    public class Checkinfront extends AsyncTask<String, String, String> {
//        Context context;
        int infront;
        String people;
        String booking_id;
        String msg="";
        String error_serv=" ";
        public Checkinfront(String booking_id){
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
                HttpPost post = new HttpPost(new strings_().get_ipaddress(FloatingWidgetService.this)+"/ahead/of/you/id");
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
                counterFab.setCount(infront);
                if (infront==0){
                    stopSelf();
                }
                else {
                    new Checkinfront(booking_id).execute();
                }
            }
            else {
                Toast.makeText(FloatingWidgetService.this, "Connection error exiting...", Toast.LENGTH_SHORT).show();
                stopSelf();
//                stopService(new Intent(FloatingWidgetService.this,FloatingWidgetService.class));
                Log.d(TAG, "onPostExecute: check infront"+error_serv);
            }

            Log.d(TAG, "onPostExecute: "+error_serv);
        }
    }
}
