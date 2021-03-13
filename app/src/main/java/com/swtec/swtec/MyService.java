package com.swtec.swtec;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.os.PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED;
import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class MyService extends Service {

    private final String CHANNEL_ID = "1";
    private MyReceiver myReceiver;
    private Executor executor;
    private boolean isForegroundServiceRunning = false;
    private NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        Log.d("MyService", "onCreate");

        builder = createNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(ACTION_DEVICE_IDLE_MODE_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        executor = Executors.newFixedThreadPool(1);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStartCommand");
        if (intent != null) {
            if (intent.getStringExtra(MainActivity.SERVICE_MODE) != null) {
                if (intent.getStringExtra(MainActivity.SERVICE_MODE).equals("startForeground")) {
                    if (!isForegroundServiceRunning) {
                        isForegroundServiceRunning = true;
                        executor.execute(() -> startForeground(1, builder.build()));
                    }
                }

                if (intent.getStringExtra(MainActivity.SERVICE_MODE).equals("stopForeground")) {
                    if (isForegroundServiceRunning) {
                        Log.d("MyService", "stopForeground");
                        stopForeground(true);
                        isForegroundServiceRunning = false;
                        stopSelf();
                    }
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    private NotificationCompat.Builder createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        int priority = PRIORITY_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            priority = PRIORITY_HIGH;
        }
        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My ads")
                .setPriority(priority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PendingIntent pi = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    new Intent(getApplicationContext(), MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setFullScreenIntent(pi, true);
        }
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        final String CHANNEL_NAME = "Main notification";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(channel);
    }
}