package com.example.currentplacedetailsonmap.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.currentplacedetailsonmap.model.SimpleAlarmManager;
import com.example.currentplacedetailsonmap.model.strings_;

import java.util.Calendar;



public class BootReceiver extends BroadcastReceiver {
    public  static String TAG="";

    @Override
    public void onReceive(Context context, Intent intent) {
        setalarm(context);
    }

    private void setalarm(Context activity){

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
}
