package com.example.infobetics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Build Notification and Communicate with Java File
        NotificationManagerHelper notificationManagerHelper = new NotificationManagerHelper(context);
        NotificationCompat.Builder notificationCompatBuilder = notificationManagerHelper.getChannelNotification();
        notificationManagerHelper.getNotificationManager().notify(1, notificationCompatBuilder.build());
    }
}
