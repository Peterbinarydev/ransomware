package com.reddevil.locker;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

public class WatchdogJobService extends JobService {
    @Override public boolean onStartJob(JobParameters params) {
        try {
            Intent i = new Intent(this, LockService.class);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(i);
            else startService(i);
        } catch (Exception e) {}
        jobFinished(params, false);
        return false;
    }
    @Override public boolean onStopJob(JobParameters params) { return true; }
}
