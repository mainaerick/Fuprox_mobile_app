package com.fuprox.noqueue.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fuprox.noqueue.model.SimpleAlarmManager;
import com.fuprox.noqueue.utils.Dbhelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeZoneChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Dbhelper dbHelper=new Dbhelper(context);
        SQLiteDatabase database=dbHelper.getWritableDatabase();
        Cursor cursor=database.rawQuery("Select * from booking",null);
//        Cursor cursor=dbHelper.getWritableDatabase().rawQuery("select * from booking;",null);


        if (cursor.moveToFirst()){
            Calendar calendar= Calendar.getInstance();

            do{
                String booking_t_stamp=cursor.getString(cursor.getColumnIndex("time_booked"));
                int id=cursor.getInt(cursor.getColumnIndex("_id"));


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
//                String time_string = simpleDateFormat.format(booking_t_stamp); //FORMAT TIME FROM DATABASE

//                FORMAT TO SHOW TODAY/TOMMORROW
                SimpleDateFormat Ddateformat = new SimpleDateFormat("dd/MM/yyyy");
//                String date_string=Ddateformat.format(booking_t_stamp);

                long mili= Long.parseLong(booking_t_stamp);
                Calendar modifybooking=Calendar.getInstance();
                modifybooking.setTimeInMillis(mili);
                modifybooking.setTimeZone(TimeZone.getDefault());

                modifybooking.add(Calendar.MINUTE,new Dbhelper(context).getnotidelay());
                Calendar now=Calendar.getInstance();
//                new SimpleAlarmManager(context).cancel(id);
                if (now.before(modifybooking)){
                    new SimpleAlarmManager(context)
                            .setup(1,modifybooking.getTimeInMillis())
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
