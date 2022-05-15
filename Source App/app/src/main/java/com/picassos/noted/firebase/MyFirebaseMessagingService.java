package com.picassos.noted.firebase;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.picassos.noted.R;
import com.picassos.noted.activities.NotificationPreviewActivity;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.databases.DAO;
import com.picassos.noted.entities.Notification;
import com.picassos.noted.sharedPreferences.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private SharedPref sharedPref;
    private DAO dao;

    @Override
    public void onNewToken(@NotNull String s) {
        super.onNewToken(s);
        sharedPref = new SharedPref(this);
        sharedPref.setFCMRregisterID(s);
        sharedPref.setNeedRegister(true);
        sharedPref.setSubscribeNotifications(false);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        sharedPref = new SharedPref(this);
        dao = APP_DATABASE.requestDatabase(this).dao();

        Log.d("GOOGLE_FIREBASE", "myFirebaseMessagingService - onMessageReceived - Message " + remoteMessage);

        try {
            Notification ne = null;
            if (remoteMessage.getData().size() > 0) {
                Object obj = remoteMessage.getData();
                ne = new Gson().fromJson(new Gson().toJson(obj), Notification.class);
            }
            if (remoteMessage.getNotification() != null) {
                RemoteMessage.Notification rn = remoteMessage.getNotification();
                if (ne == null) ne = new Notification();
                ne.title = rn.getTitle();
                ne.content = rn.getBody();
            }

            if (ne == null) return;
            ne.id = System.currentTimeMillis();
            ne.created_at = System.currentTimeMillis();
            ne.read = false;

            // request show notification
            showNotification(ne, null);

            // save notification to room db
            saveNotification(ne);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void showNotification(Notification notification, Bitmap bitmap) {
        Intent intent = NotificationPreviewActivity.navigateBase(this, notification, true);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
        }

        String channelId = "channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(notification.title);
        builder.setContentText(notification.content);
        builder.setSmallIcon(R.drawable.splash_logo);
        builder.setDefaults(android.app.Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        builder.setPriority(android.app.Notification.PRIORITY_HIGH);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        builder.setLargeIcon(largeIcon);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notification.content));
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(notification.content));
        }

        // display push notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
        int unique_id = (int) System.currentTimeMillis();
        Objects.requireNonNull(notificationManager).notify(unique_id, builder.build());
    }

    private void saveNotification(Notification notification) {
        dao.requestInsertNotification(notification);
    }

}
