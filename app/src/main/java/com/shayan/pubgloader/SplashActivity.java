package com.shayan.pubgloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

    private ProgressBar progressLoading;
    private TextView txtPercentage;
    private TextView txtLoading;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View splashContent = findViewById(R.id.splashContent);
        progressLoading = findViewById(R.id.progressLoading);
        txtPercentage = findViewById(R.id.txtPercentage);
        txtLoading = findViewById(R.id.txtLoading);

        splashContent.setScaleX(0.82f);
        splashContent.setScaleY(0.82f);

        splashContent.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(900)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        handler.postDelayed(this::updateProgress, 500);
    }

    private void updateProgress() {
        progress += 2;

        progressLoading.setProgress(progress);
        txtPercentage.setText(progress + "%");

        if (progress < 35) {
            txtLoading.setText("Initializing launcher...");
        } else if (progress < 70) {
            txtLoading.setText("Checking PUBG Mobile...");
        } else if (progress < 100) {
            txtLoading.setText("Loading dashboard...");
        } else {
            txtLoading.setText("Launcher ready");

            handler.postDelayed(() -> {
                Intent intent = new Intent(
                        SplashActivity.this,
                        MainActivity.class
                );
                startActivity(intent);
                finish();
            }, 500);

            return;
        }

        handler.postDelayed(this::updateProgress, 45);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

