package com.fuprox.noqueue.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.model.SimpleAlarmManager;
import com.fuprox.noqueue.utils.Dbhelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Settings_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        TextView help=findViewById(R.id.help);
        TextView invite=findViewById(R.id.invite);
        Spinner statespinner=findViewById(R.id.spinner_notification_state);
        Spinner timespinner=findViewById(R.id.spinner_notify_before);
//        Toast.makeText(this, "state="+new Dbhelper(this).getnotistate(), Toast.LENGTH_SHORT).show();

        String[] notification_state={"enabled","disabled"};
        String[] notification_time={"Disabled","10 minutes","20 minutes","30 minutes","1 hour"};
        ArrayList<String> statearrayList=new ArrayList<>();

        ArrayList<String> n_tinearrayList=new ArrayList<>();
        for (int i=0;i<notification_state.length;i++){
            statearrayList.add(notification_state[i]);
        }
        for (int i=0;i<notification_time.length;i++){
            n_tinearrayList.add(notification_time[i]);
        }

        ArrayAdapter n_statearrayAdapter=new ArrayAdapter(this,R.layout.spinner_services_items,R.id.textview_services,statearrayList);
        statespinner.setAdapter(n_statearrayAdapter);
        statespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tvstate=view.findViewById(R.id.textview_services);
                if (tvstate.getText().toString().toLowerCase().equals("enabled")){
                    new Dbhelper(Settings_activity.this).setnotistate("1");

                }
                else {
                    new Dbhelper(Settings_activity.this).setnotistate("0");

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter n_timearrayAdapter=new ArrayAdapter(this,R.layout.spinner_services_items,R.id.textview_services,n_tinearrayList);
        timespinner.setAdapter(n_timearrayAdapter);
        timespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tvdelay=view.findViewById(R.id.textview_services);
                if (position==0){
                    new Dbhelper(Settings_activity.this).setnotidelay(0);

                }
                else if (position==1){
                    new Dbhelper(Settings_activity.this).setnotidelay(10);

                }
                else if (position==2){
                    new Dbhelper(Settings_activity.this).setnotidelay(20);
                }
                else if (position==3){
                    new Dbhelper(Settings_activity.this).setnotidelay(30);
                }

                else if (position==4){
                    new Dbhelper(Settings_activity.this).setnotidelay(60);
                }
//                Toast.makeText(Settings_activity.this, ""+new Dbhelper(Settings_activity.this).getnotidelay(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        if (new Dbhelper(this).getnotistate().equals("1")){
            statespinner.setSelection(0);
        }
        else {
            statespinner.setSelection(1);
        }


        if (new Dbhelper(this).getnotidelay()==10){
            reloadnotification(10);
            timespinner.setSelection(1);
        }
        else if (new Dbhelper(this).getnotidelay()==0){
            reloadnotification(0);

            timespinner.setSelection(0);

        }

        else if (new Dbhelper(this).getnotidelay()==20){
            reloadnotification(20);

            timespinner.setSelection(2);

        }

        else if (new Dbhelper(this).getnotidelay()==30){
            reloadnotification(30);

            timespinner.setSelection(3);

        }
        else if (new Dbhelper(this).getnotidelay()==60){
            reloadnotification(60);

            timespinner.setSelection(4);
        }

    }

    public void reloadnotification(int delay){
        Dbhelper dbHelper=new Dbhelper(this);
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        Cursor cursor=database.rawQuery("Select * from booking",null);
//        Cursor cursor=dbHelper.getWritableDatabase().rawQuery("select * from booking;",null);


        if (cursor.moveToFirst()){
            Calendar calendar= Calendar.getInstance();

            do{
                String booking_t_stamp=cursor.getString(cursor.getColumnIndex("time_booked"));
                int id=cursor.getInt(cursor.getColumnIndex("booking_id"));


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
//                String time_string = simpleDateFormat.format(booking_t_stamp); //FORMAT TIME FROM DATABASE

//                FORMAT TO SHOW TODAY/TOMMORROW
                SimpleDateFormat Ddateformat = new SimpleDateFormat("dd/MM/yyyy");
//                String date_string=Ddateformat.format(booking_t_stamp);

                long mili= Long.parseLong(booking_t_stamp);
                Calendar modifycal=Calendar.getInstance();
                calendar.setTimeInMillis(mili);
                calendar.add(Calendar.MINUTE,-delay);

                if(calendar.getTimeInMillis()<mili){
                    new SimpleAlarmManager(this).cancel(id);
                    new SimpleAlarmManager(this)
                            .setup(1,calendar.getTimeInMillis())
                            .register(id)
                            .start();
                }



                //subtract time set by user to be notified before
//                calendar.add(Calendar.MINUTE,-30);
                String now_string=simpleDateFormat.format(calendar.getTimeInMillis());
            }while (cursor.moveToNext());
        }

    }
}
