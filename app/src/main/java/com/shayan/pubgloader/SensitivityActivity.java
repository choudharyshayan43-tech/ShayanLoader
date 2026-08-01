package com.shayan.pubgloader;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SensitivityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitivity);

        TextView btnBack = findViewById(R.id.btnBackSensitivity);
        btnBack.setOnClickListener(v -> finish());
    }
}
