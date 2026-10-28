package com.example.broadcasttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private IntentFilter mIntentFilter;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    private TextView tvStatus, tvCount, tvLog;
    private int broadcastCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvCount = findViewById(R.id.tvCount);
        tvLog = findViewById(R.id.tvLog);
        Button btnClear = findViewById(R.id.btnClear);

        btnClear.setOnClickListener(v -> {
            broadcastCount = 0;
            tvCount.setText("Broadcasts received: 0");
            tvLog.setText("");
        });

        updateStatus();

        // Dynamic registration
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkChangeReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeReceiver);
    }

    // Returns true if connected, and updates the status text and color
    private boolean updateStatus() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            tvStatus.setText("Connected via " + info.getTypeName());
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            return true;
        } else {
            tvStatus.setText("Disconnected");
            tvStatus.setTextColor(Color.parseColor("#C62828"));
            return false;
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            broadcastCount++;
            boolean connected = updateStatus();

            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .format(new Date());
            String message = connected ? "Network connected" : "Network lost";

            tvCount.setText("Broadcasts received: " + broadcastCount);
            tvLog.setText("[" + time + "] " + message + "\n" + tvLog.getText());

            Toast.makeText(context, "network changes!", Toast.LENGTH_SHORT).show();
        }
    }
}