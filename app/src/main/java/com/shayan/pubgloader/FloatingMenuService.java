package com.shayan.pubgloader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
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

    private int startX;
    private int startY;
    private float touchX;
    private float touchY;
    private boolean moved;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createFloatingLogo();
    }

    private void createFloatingLogo() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);

        TextView logo = new TextView(this);
        logo.setText("S");
        logo.setTextSize(25);
        logo.setTextColor(Color.parseColor("#00FF88"));
        logo.setGravity(Gravity.CENTER);
        logo.setTypeface(null, android.graphics.Typeface.BOLD);

        GradientDrawable logoBackground = new GradientDrawable();
        logoBackground.setColor(Color.parseColor("#10151D"));
        logoBackground.setCornerRadius(dp(18));
        logoBackground.setStroke(dp(2), Color.parseColor("#00FF88"));
        logo.setBackground(logoBackground);

        LinearLayout.LayoutParams logoSize =
                new LinearLayout.LayoutParams(dp(52), dp(52));

        root.addView(logo, logoSize);

        miniMenu = createMiniMenu();
        miniMenu.setVisibility(View.GONE);
        root.addView(miniMenu);

        int overlayType;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                Window
