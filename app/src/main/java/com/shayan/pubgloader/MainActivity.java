package com.shayan.pubgloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String PUBG_GLOBAL_PACKAGE = "com.tencent.ig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchButton = findViewById(R.id.btnLaunch);
        launchButton.setOnClickListener(view -> launchPubg());
    }

    private void launchPubg() {
        Intent launchIntent = getPackageManager()
                .getLaunchIntentForPackage(PUBG_GLOBAL_PACKAGE);

        if (launchIntent == null) {
            Toast.makeText(this, "PUBG Mobile is not installed.", Toast.LENGTH_LONG).show();
            return;
        }

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchIntent);
    }
}
