package com.itheamc.notificationexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.Permission;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final String CHANNEL_ID = "productchannelid";
    private Button notifyBtn;
    private TextView textView;
    private String lanCode = "ne";
    private static final int LOC_REQUEST_CODE = 706;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLanguage();
        textView = findViewById(R.id.textView);
        notifyBtn = findViewById(R.id.notifyButton);
        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showNotification();
//                Context context = LocaleHandler.setLocale(MainActivity.this, "ne");
//                textView.setText(context.getResources().getString(R.string.text_view_text));
//                notifyBtn.setText(context.getResources().getString(R.string.button_text));
//                setLanguage();

                if (isPermissionGranted()) {
                    LocationHandler.getInstance(MainActivity.this).getLastLocation(textView);
//                    LocationHandler.getInstance(MainActivity.this).requestUserLocation(textView);
//                    LocationHandler.getInstance(MainActivity.this).getCurrentLocation(textView);
                }

            }
        });

        createNotificationChannel();
        if (!isPermissionGranted()) {
            requestPermission();
        }
    }

    // Function to create notification channel
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "product channel";
            String description = "product channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    // Function to show notification
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Product")
                .setContentText("New product has been arrived")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(10101, builder.build());
    }


    // Function to change the app language as per the user setting
    public void setLanguage() {
        // Retrieving user language from the local storage
        if (lanCode.toLowerCase().equals("en")) {
            lanCode = "ne";
        } else {
            lanCode = "en";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getMainExecutor().execute(() -> {
                Locale locale = new Locale(lanCode);
                Locale.setDefault(locale);
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(locale);
                createConfigurationContext(config);
            });
        }
    }


    // Function to check whether permission is granted or not
    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // FUnction to request permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
        if (requestCode == LOC_REQUEST_CODE) {
            if (isAllGranted(grantResults)) {
                // All permission is granted
                Log.d(TAG, "onRequestPermissionsResult: All Permission granted");
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Denied");
            }
        }
    }

    // Checking whether all permission is granted or not after requesting
    private boolean isAllGranted(int[] grantResults) {
        boolean isGranted = false;
        for (int i: grantResults) {
            if (i == PackageManager.PERMISSION_DENIED) {
                break;
            } else {
                isGranted = true;
            }
        }

        return isGranted;
    }
}