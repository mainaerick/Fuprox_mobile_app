package com.fuprox.noqueue.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.fuprox.noqueue.Receiver.AlarmReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;



public class SimpleAlarmManager {
    public  static String TAG="";

    private Context context;
    private Intent alarmIntent;
    private int hourOfDay;
    private int minuteOfDay;
    private int secondOfDay;
    private Calendar calendar;
    private int id;
    private long interval;
    public final static long INTERVAL_DAY = AlarmManager.INTERVAL_DAY;
    private static Boolean isInitWithId = Boolean.FALSE;
    private PendingIntent pendingIntent,reminderpending;
    private int dayoftheweek;

    public SimpleAlarmManager(Context context) {
        this.context = context;
        this.alarmIntent = new Intent(context, AlarmReceiver.class);
    }
    public SimpleAlarmManager register(int id) {
        this.id = id;
        alarmIntent.putExtra("id", id);
        SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_manager", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if( sharedPreferences.contains("ids") ) {
            Set<String> set = sharedPreferences.getStringSet("ids", null);
            if( set != null && !set.isEmpty() ) {
                set.add(Integer.toString(id));
            }
        } else {
            Set<String> set = new HashSet<>();
            set.add(Integer.toString(id));
            editor.putStringSet("ids", set);
            editor.commit();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dayoftheweek",this.dayoftheweek);
            jsonObject.put("interval", this.interval);
            jsonObject.put("hourOfDay", this.hourOfDay);
            jsonObject.put("minuteOfDay", this.minuteOfDay);
            jsonObject.put("secondOfDay", this.secondOfDay);
            editor.putString("idExtra" + id, jsonObject.toString()).commit();
        } catch (JSONException e) {
        }


        return this;
    }
//    public static SimpleAlarmManager initWithId(Context context, int id) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_manager", Context.MODE_PRIVATE);
//        String registrationExtra = sharedPreferences.getString("idExtra" + id, null);
//        int interval, dayoftheweek,hourDay, minuteOfDay, secondOfDay;
//        if( registrationExtra != null ) {
//            try {
//                JSONObject jsonObject = new JSONObject(registrationExtra);
//                dayoftheweek=jsonObject.getInt("dayoftheweek");
//                interval = jsonObject.getInt("interval");
//                hourDay = jsonObject.getInt("hourOfDay");
//                minuteOfDay = jsonObject.getInt("minuteOfDay");
//                secondOfDay = jsonObject.getInt("secondOfDay");
//                isInitWithId = Boolean.TRUE;
//                return new SimpleAlarmManager(context).setup(interval,dayoftheweek, hourDay, minuteOfDay, secondOfDay).register(id);
//            } catch (JSONException e) {
//            }
//        }
//        return null;
//    }
    public static SimpleAlarmManager getwith(){


        return null;
    }


    public static Set<String> getAllRegistrationIds(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_manager", Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet("ids", null);
    }
    public SimpleAlarmManager setup(long interval,long mili) {
        this.interval = interval;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mili);
//        Calendar now = Calendar.getInstance();
//        if( now.after(calendar) )
//            calendar.add(Calendar.DAY_OF_WEEK, 7);
        return this;
    }

//    public SimpleAlarmManager setupreminder(long mili){
////        if( pendingIntent != null ) {
//        Calendar nowcal= Calendar.getInstance();
//            if (mili<=nowcal.getTimeInMillis()){
//                Log.d("TAG", "setupreminder: no alarm reminder set");
//            }
//            else {
//                reminderpending = PendingIntent.getBroadcast(context, id, reminderintent, PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                manager.cancel(reminderpending);
//
//                manager.set(AlarmManager.RTC_WAKEUP, mili, reminderpending);
//                Log.d("TAG", "setupreminder: alarm reminder set ");
//            }
//
//
////        }
//
//            return this;
//    }
//    public SimpleAlarmManager reset_alarm_db(long interval,int dayoftheweek,int hourOfDay,int minuteOfDay,int secondOfDay);
    public Intent getIntent() {
        return alarmIntent;
    }
    public SimpleAlarmManager start() {



//        if( isInitWithId == Boolean.FALSE ) {
//            if( PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_NO_CREATE) == null ) {
                pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                if( pendingIntent != null ) {
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);
                    if( interval == 1 ) {
                        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
                    }

                        SimpleDateFormat Ddateformat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        Log.d(TAG, "alarm set on: "+Ddateformat.format(calendar.getTimeInMillis()));
//                }
//            }
//        } else {
//            pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_NO_CREATE);
//            if( pendingIntent != null ) {
//                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                manager.cancel(pendingIntent);
//                if( interval == -1 ) {
//                    manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                } else {
//                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
//                }
//            }
//
//        }

        return this;
    }
    public long show_mili(){
        return calendar.getTimeInMillis();
    }
    public void cancel(int id){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
    }
//    public void cancelreminder(int id){
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        reminderpending = PendingIntent.getBroadcast(context, id, reminderintent, PendingIntent.FLAG_UPDATE_CURRENT);
//        manager.cancel(reminderpending);
//    }

}

