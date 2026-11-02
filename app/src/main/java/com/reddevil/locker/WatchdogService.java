package com.reddevil.locker;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;

public class WatchdogService extends AccessibilityService {
    private long lastStartTime = 0;

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        long now = SystemClock.elapsedRealtime();
        if (now - lastStartTime > 2000) {
            lastStartTime = now;
            try {
                Intent i = new Intent(this, LockService.class);
                if (Build.VERSION.SDK_INT >= 26) startForegroundService(i);
                else startService(i);
            } catch (Exception e) {}
        }
    }
    @Override public void onInterrupt() {}
    @Override protected void onServiceConnected() {
        super.onServiceConnected();
        try {
            Intent i = new Intent(this, LockService.class);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(i);
            else startService(i);
        } catch (Exception e) {}
    }
}
