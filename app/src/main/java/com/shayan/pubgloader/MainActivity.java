package com.shayan.pubgloader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PUBG_PACKAGE = "com.tencent.ig";

    private TextView txtGameStatus;
    private Button btnLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtGameStatus = findViewById(R.id.txtGameStatus);
        btnLaunch = findViewById(R.id.btnLaunch);

        updateGameStatus();

        btnLaunch.setOnClickListener(v -> launchPubg());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGameStatus();
    }

    private void updateGameStatus() {
        Intent launchIntent =
                getPackageManager().getLaunchIntentForPackage(PUBG_PACKAGE);

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
        PackageManager packageManager = getPackageManager();
        Intent launchIntent =
                packageManager.getLaunchIntentForPackage(PUBG_PACKAGE);

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntent);
        } else {
            Toast.makeText(
                    this,
                    "PUBG Mobile is not installed.",
                    Toast.LENGTH_LONG
            ).show();

            updateGameStatus();
        }
    }
}
