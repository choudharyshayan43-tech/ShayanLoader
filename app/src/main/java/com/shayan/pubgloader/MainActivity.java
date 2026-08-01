package com.shayan.pubgloader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PUBG_PACKAGE = "com.tencent.ig";
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    private TextView txtGameStatus;
    private Button btnLaunch;
    private Button btnOpenMenu;
    private Button btnSensitivity;
    private Button btnGraphics;

    private boolean waitingForOverlayPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtGameStatus = findViewById(R.id.txtGameStatus);
        btnLaunch = findViewById(R.id.btnLaunch);
        btnOpenMenu = findViewById(R.id.btnOpenMenu);
        btnSensitivity = findViewById(R.id.btnSensitivity);
        btnGraphics = findViewById(R.id.btnGraphics);

        updateGameStatus();

        btnLaunch.setOnClickListener(v -> launchPubg());
        btnOpenMenu.setOnClickListener(v -> openFloatingMenu());

        btnSensitivity.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SensitivityActivity.class
            );
            startActivity(intent);
        });

        btnGraphics.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    GraphicsActivity.class
            );
            startActivity(intent);
        });
    }

    private void openFloatingMenu() {
        requestNotificationPermissionIfNeeded();

        if (!Settings.canDrawOverlays(this)) {
            waitingForOverlayPermission = true;

            Toast.makeText(
                    this,
                    "Display over other apps permission allow karo",
                    Toast.LENGTH_LONG
            ).show();

            Intent permissionIntent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
            );

            startActivity(permissionIntent);
            return;
        }

        startFloatingMenu();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    NOTIFICATION_PERMISSION_CODE
            );
        }
    }

    private void startFloatingMenu() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(
                    this,
                    "Overlay permission required",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Intent serviceIntent =
                new Intent(this, FloatingMenuService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        Toast.makeText(
                this,
                "Floating menu started",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateGameStatus();

        if (waitingForOverlayPermission
                && Settings.canDrawOverlays(this)) {

            waitingForOverlayPermission = false;
            startFloatingMenu();
        }
    }

    private void updateGameStatus() {
        Intent launchIntent =
                getPackageManager()
                        .getLaunchIntentForPackage(PUBG_PACKAGE);

        if (launchIntent != null) {
            txtGameStatus.setText("PUBG MOBILE DETECTED");
            btnLaunch.setEnabled(true);
            btnLaunch.setText("LAUNCH PUBG MOBILE");
        } else {
            txtGameStatus.setText("PUBG MOBILE NOT INSTALLED");
            btnLaunch.setEnabled(false);
            btnLaunch.setText("GAME NOT FOUND");
        }
    }

    private void launchPubg() {
        Intent launchIntent =
                getPackageManager()
                        .getLaunchIntentForPackage(PUBG_PACKAGE);

        if (launchIntent != null) {
            launchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(launchIntent);
        } else {
            Toast.makeText(
                    this,
                    "PUBG Mobile is not installed",
                    Toast.LENGTH_LONG
            ).show();

            updateGameStatus();
        }
    }
}

