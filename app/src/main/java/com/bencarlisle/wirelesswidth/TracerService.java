package com.bencarlisle.wirelesswidth;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class TracerService extends IntentService {

    static final int MAX_DISTANCE = 2;
    static final Pattern pattern = Pattern.compile("contact_tracer_(\\d{10})");

    void init() {

    }

    void findContacts() {

    }

    void emit() {

    }

    public TracerService() {
        super("contact-tracer-service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "tracer-service";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Tracer Service Running", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE));
            notificationManager.createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("").setContentText("").build();
            startForeground(1, notification);
        }
        init();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        int methodType = Objects.requireNonNull(intent.getExtras()).getInt("methodCode");
        if (methodType == 1) {
            emit();
        } else {
            findContacts();
        }
    }

    static double getDistance(int level, int freq) {
        //fspl
        return Math.pow(10.0, ((27.55 - (20 * Math.log10(freq)) + level) / 20));
    }

}
