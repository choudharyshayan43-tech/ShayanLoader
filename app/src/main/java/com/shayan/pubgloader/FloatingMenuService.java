package com.shayan.pubgloader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingMenuService extends Service {

    private static final String CHANNEL_ID = "shayan_overlay_channel";
    private static final int NOTIFICATION_ID = 101;
    private static final String PUBG_PACKAGE = "com.tencent.ig";

    private WindowManager windowManager;
    private View floatingView;
    private LinearLayout miniMenu;
    private WindowManager.LayoutParams params;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private boolean moved;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());

        windowManager =
                (WindowManager) getSystemService(WINDOW_SERVICE);

        createFloatingMenu();
    }

    private void createFloatingMenu() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView logo = new TextView(this);
        logo.setText("S");
        logo.setTextSize(25);
        logo.setTextColor(Color.parseColor("#00FF88"));
        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable logoBackground = new GradientDrawable();
        logoBackground.setColor(Color.parseColor("#10151D"));
        logoBackground.setCornerRadius(dp(18));
        logoBackground.setStroke(
                dp(2),
                Color.parseColor("#00FF88")
        );

        logo.setBackground(logoBackground);

        LinearLayout.LayoutParams logoParams =
                new LinearLayout.LayoutParams(
                        dp(52),
                        dp(52)
                );

        root.addView(logo, logoParams);

        miniMenu = createMiniMenu();
        miniMenu.setVisibility(View.GONE);
        root.addView(miniMenu);

        int overlayType;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayType =
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType =
                    WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = dp(20);
        params.y = dp(180);

        logo.setOnTouchListener((view, event) -> {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    moved = false;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX =
                            event.getRawX() - initialTouchX;

                    float deltaY =
                            event.getRawY() - initialTouchY;

                    if (Math.abs(deltaX) > 8
                            || Math.abs(deltaY) > 8) {
                        moved = true;
                    }

                    params.x = initialX + (int) deltaX;
                    params.y = initialY + (int) deltaY;

                    if (floatingView != null) {
                        windowManager.updateViewLayout(
                                floatingView,
                                params
                        );
                    }

                    return true;

                case MotionEvent.ACTION_UP:
                    if (!moved) {
                        toggleMenu();
                    }

                    return true;

                default:
                    return false;
            }
        });

        floatingView = root;
        windowManager.addView(floatingView, params);
    }

    private LinearLayout createMiniMenu() {
        LinearLayout menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);

        menu.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.parseColor("#EE10151D"));
        background.setCornerRadius(dp(14));

        background.setStroke(
                dp(1),
                Color.parseColor("#324052")
        );

        menu.setBackground(background);

        LinearLayout.LayoutParams menuParams =
                new LinearLayout.LayoutParams(
                        dp(180),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        menuParams.topMargin = dp(8);
        menu.setLayoutParams(menuParams);

        TextView title =
                createMenuItem(
                        "SHAYAN MENU",
                        "#00FF88"
                );

        title.setTypeface(Typeface.DEFAULT_BOLD);
        menu.addView(title);

        TextView launch =
                createMenuItem(
                        "▶  Launch PUBG",
                        "#FFFFFF"
                );

        launch.setOnClickListener(v -> launchPubg());
        menu.addView(launch);

        TextView hide =
                createMenuItem(
                        "—  Hide Menu",
                        "#FFFFFF"
                );

        hide.setOnClickListener(v ->
                miniMenu.setVisibility(View.GONE)
        );

        menu.addView(hide);

        TextView close =
                createMenuItem(
                        "✕  Close Overlay",
                        "#FF5F6D"
                );

        close.setOnClickListener(v -> stopSelf());
        menu.addView(close);

        return menu;
    }

    private TextView createMenuItem(
            String text,
            String color
    ) {
        TextView item = new TextView(this);

        item.setText(text);
        item.setTextColor(Color.parseColor(color));
        item.setTextSize(15);
        item.setGravity(Gravity.CENTER_VERTICAL);

        item.setPadding(
                dp(8),
                dp(11),
                dp(8),
                dp(11)
        );

        return item;
    }

    private void toggleMenu() {
        if (miniMenu.getVisibility() == View.VISIBLE) {
            miniMenu.setVisibility(View.GONE);
        } else {
            miniMenu.setVisibility(View.VISIBLE);
        }
    }

    private void launchPubg() {
        Intent launchIntent =
                getPackageManager()
                        .getLaunchIntentForPackage(
                                PUBG_PACKAGE
                        );

        if (launchIntent != null) {
            launchIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            startActivity(launchIntent);
            miniMenu.setVisibility(View.GONE);

        } else {
            Toast.makeText(
                    this,
                    "PUBG Mobile not installed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private Notification createNotification() {
        Intent openAppIntent =
                new Intent(
                        this,
                        MainActivity.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        openAppIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {

            builder =
                    new Notification.Builder(
                            this,
                            CHANNEL_ID
                    );

        } else {
            builder = new Notification.Builder(this);
        }

        return builder
                .setContentTitle("Shayan Floating Menu")
                .setContentText(
                        "Floating launcher is active"
                )
                .setSmallIcon(
                        android.R.drawable.ic_media_play
                )
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Floating Menu",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(
                        channel
                );
            }
        }
    }

    private int dp(int value) {
        return Math.round(
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    @Override
    public void onDestroy() {
        if (floatingView != null
                && windowManager != null) {

            windowManager.removeView(floatingView);
            floatingView = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

