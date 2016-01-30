package com.linscott.smartmitten.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

import com.linscott.smartmitten.R;

public class ConnectedNotification {

    private Context context;
    private final int notificationID;

    public ConnectedNotification(Context c, int id){
        context = c;
        notificationID = id;
    }

    public void createNotification(String deviceName, String deviceAddress){

        final NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String title = String.format("Connected to %s ", deviceName);

        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentText(deviceAddress)
                .setOngoing(false)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setSound(alarm)
                .setSmallIcon(R.mipmap.ic_launcher);


        nm.notify(notificationID, builder.build());

    }
}
