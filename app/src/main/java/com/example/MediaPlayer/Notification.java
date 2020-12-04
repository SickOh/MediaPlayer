package com.example.MediaPlayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class Notification {

    public static final String CHANNEL_ID = "CharSequence name = getString(R.string.channel_name);\n" +
            "        String description = getString(R.string.channel_description);\n" +
            "        int importance = NotificationManager.IMPORTANCE_DEFAULT;\n" +
            "        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);";
    RemoteViews notificationLayout;
    NotificationCompat.Builder notification;

    public Notification(Context context, NotificationManager notificationManager) {

        //String input = intent.getStringExtra("inputExtra");
        createNotificationChannel(notificationManager);
        Intent notificationIntent = new Intent(context, LockScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);

        notificationLayout = new RemoteViews("com.example.newmusicplayer", R.layout.notification);
        notificationLayout.setTextViewText(R.id.title, "Custom notification");

        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                //.setContentText(input)
                .setSmallIcon(R.drawable.locked_green)
                //.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContent(notificationLayout)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());

        //.build();

        notificationManager.notify(1, notification.build());
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setDescription("no sound");
            serviceChannel.setSound(null,null);
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    public void updateNotification(String title, NotificationManager manager){


        notificationLayout.setTextViewText(R.id.media_title, title);
        CharSequence name = "Name";
        String description = "Description";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        manager.createNotificationChannel(channel);
        manager.notify(1, notification.build());
    }

}
