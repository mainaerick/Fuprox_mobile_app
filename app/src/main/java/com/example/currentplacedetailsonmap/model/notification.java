package com.example.currentplacedetailsonmap.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.currentplacedetailsonmap.MainActivity;
import com.example.currentplacedetailsonmap.R;
import com.example.currentplacedetailsonmap.utils.Dbhelper;

import java.text.SimpleDateFormat;
import java.util.Random;

public class notification {
    static String CHANNEL_ID="404";
    static String PAYMENT_CHANNEL_ID="405";

    public notification(){

    }
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "brace_yourself"; // channel description
            String description = "notification_incoming";//channel description
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static int getNotificationIcon(int drawable, int mipmap) {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        if (useWhiteIcon){
            return drawable;
        }
        else {
            return mipmap;
        }
    }
    public static void notify_(Context context, String booking_id, int id, String content, String people, String branchname, String servicename){

        if (new Dbhelper(context).getnotistate().equals("1")) {
            String show_fromtime = null, show_totime = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            long[] vib = {500, 500};

            Intent intent1 = new Intent(context, MainActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("book_id", booking_id);
            bundle.putString("verify","verify");
            intent1.putExtras(bundle);

//        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, new Random().nextInt(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon_title)
                    .setVibrate(vib)
                    .setContentTitle(content)
                    .setContentText(people)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(people + "\n" + branchname + " for " + servicename + " service"))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.icon_notification_click, "View ticket",
                            pendingIntent);
//                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_signup))

//        branchname+" for "+servicename+" \n\n"+people
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(Integer.parseInt(booking_id), builder.build());
        }
    }

    public static void notify_payment_status(Context context, int id,String title, String content,String booking_id){
        if (new Dbhelper(context).getnotistate().equals("1")) {
            String show_fromtime = null, show_totime = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            long[] vib = {500, 500};
//        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
            if (title.equals("Payment Successful")){
                Intent intent1 = new Intent(context, MainActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("book_id", booking_id);
                bundle.putString("verify","verify");
                intent1.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, new Random().nextInt(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_title)
                        .setVibrate(vib)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(content))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.icon_notification_click, "View Status",
                                pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        branchname+" for "+servicename+" \n\n"+people
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(id, builder.build());
            }
            else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_title)
                        .setVibrate(vib)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(content))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        branchname+" for "+servicename+" \n\n"+people
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(id, builder.build());
            }

        }

    }
}
