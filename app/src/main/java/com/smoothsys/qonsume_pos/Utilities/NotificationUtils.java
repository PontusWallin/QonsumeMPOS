package com.smoothsys.qonsume_pos.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.smoothsys.qonsume_pos.FragmentsAndActivities.MainActivity;
import com.smoothsys.qonsume_pos.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {

    private static final String CHANNEL_ID = "Qonsume POS channel id";
    private static final String CHANNEL_NAME = "Qonsume POS channel name";

    public static void createNotification(int difference, Context context) {

        if(difference <= 0) {
            return;
        }

        createNotificationChannel(context);

        NotificationCompat.Builder mBuilder = makeBuilder(difference, context);
        Notification notification = mBuilder.build();
        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        mNotifyManager.notify(0,notification);
    }

    private static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String description = "This channel is for notifying merchant about new orders";

            int importance = NotificationManager.IMPORTANCE_MAX;    // This needs to be MAX - don't change it
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static NotificationCompat.Builder makeBuilder(int difference, Context context) {

        String msg = "";

        if(difference > 1) {
            msg = context.getResources().getString(R.string.new_order_plural);
        } else if(difference == 1){
            msg = context.getResources().getString(R.string.new_order_single);
        }

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("goToOrdersScreen", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context.getApplicationContext(), 0 , resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT
        );

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentTitle(msg)
                .setContentText("")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setVibrate(new long[] { 500, 500})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);
    }
}