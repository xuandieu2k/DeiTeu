package com.example.deiteu.model;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.deiteu.R;
import com.example.deiteu.activity.Details_Post;

import java.util.Random;

public class NotificationHelper {

    private static final String CHANNEL_ID = "deiteuapp_id";
    private static final String CHANNEL_NAME = "Deiteu";
    private static final String CHANNEL_DESCRIPTION = "Hẹn hò thôi.";

    private Context mContext;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    public void createNotification(String title, String message,String idPost,String idPoster, Class<?> activityClass) {
        createNotification(title, message,idPost,idPoster, activityClass, R.drawable.logonice_border_50, 0);
    }

    public void createNotification(String title, String message,String idPost,String idPoster, Class<?> activityClass, int icon) {
        createNotification(title, message,idPost,idPoster, activityClass, icon, 0);
    }
    public void createNotification(String title, String message, Class<?> targetClass, String targetFragmentTag, int icon, int notificationId) {
        Intent intent = new Intent(mContext, targetClass);
        intent.putExtra("FRAGMENT_TAG", targetFragmentTag); // truyền tag của fragment vào intent
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationId, builder.build());
    }
    public void createNotificationCustomLayout(String title, String message,String idPost,String idPoster, Class<?> activityClass, int notificationId) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.imageView, R.drawable.logo_deiteunice);
        remoteViews.setTextViewText(R.id.textViewTitle, title);
        remoteViews.setTextViewText(R.id.textViewMessage, message);

        Intent intent = new Intent(mContext, activityClass);
        intent.putExtra("keyidpost", idPost);
        intent.putExtra("keyidUserpost", idPoster);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_deiteunice)
                .setCustomContentView(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationId, builder.build());
    }


//    public void createNotification(String title, String message,String idPost,String idPoster, Class<?> activityClass, int icon, int notificationId) {
//        Intent intent = new Intent(mContext, activityClass);
//        intent.putExtra("keyidpost", idPost);
//        intent.putExtra("keyidUserpost", idPoster);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        icon = R.drawable.logo_deiteunice;
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
//                .setSmallIcon(icon)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription(CHANNEL_DESCRIPTION);
//            channel.enableLights(true);
//            channel.setLightColor(Color.BLUE);
//            channel.enableVibration(true);
//            notificationManager.createNotificationChannel(channel);
//        }
//        notificationManager.notify(notificationId, builder.build());
//    }
    public void createNotification(String title, String message,String idPost,String idPoster, Class<?> activityClass, int icon, int notificationId) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.imageView, R.drawable.logo_final);
        remoteViews.setTextViewText(R.id.textViewTitle, title);
        remoteViews.setTextViewText(R.id.textViewMessage, message);

        Intent intent = new Intent(mContext, activityClass);
        intent.putExtra("keyidpost", idPost);
        intent.putExtra("keyidUserpost", idPoster);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        icon = R.drawable.logo_final;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setContent(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(generateRandomNumber(idPost), builder.build());
    }
    public static int generateRandomNumber(String input) {
        int hash = input.hashCode();
        Random random = new Random(hash);
        return random.nextInt();
    }

}

