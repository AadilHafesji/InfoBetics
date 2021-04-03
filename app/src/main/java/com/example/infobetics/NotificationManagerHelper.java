package com.example.infobetics;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationManagerHelper extends ContextWrapper {
    public static final String alarmID = "alarmID";
    public static final String alarmName = "Alarm Name";

    private NotificationManager mNotificationManager;

    public NotificationManagerHelper(Context base) {
        super(base);
        //If build greater than or equal to Oreo Device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAlarmChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createAlarmChannel() {
        NotificationChannel alarmChannel = new NotificationChannel(alarmID, alarmName, NotificationManager.IMPORTANCE_DEFAULT);

        //Settings
        alarmChannel.enableLights(true);
        alarmChannel.enableVibration(true);
        alarmChannel.setLightColor(R.color.colorPrimaryDark);
        alarmChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        //Notification Manager to Build Settings
        getNotificationManager().createNotificationChannel(alarmChannel);
    }

    public NotificationManager getNotificationManager() {
        //If manager is null, create new notification
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        //Allow user to select Notification
        Intent openNotificationIntent = new Intent(this, ReminderActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 1, openNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), alarmID)
                .setContentTitle("InfoBetics Alarm!")
                .setContentText("Reminder Alarm for Insulin")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentIntent(openPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setOnlyAlertOnce(true);
    }
}
