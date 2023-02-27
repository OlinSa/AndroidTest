package com.example.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainActivity extends AppCompatActivity {
    private IntentFilter connectivityChangeIntentFilter;
    private IntentFilter localIntentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        connectivityChangeIntentFilter = new IntentFilter();
        connectivityChangeIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, connectivityChangeIntentFilter);

        Button sendBroadcastButton = (Button) findViewById(R.id.SendBroadcastButton);
        sendBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.broadcast.MY_BROADCAST");
                sendBroadcast(intent);
            }
        });

        Button sendLocalBroadcastButton = (Button) findViewById(R.id.SendLocalBroadcastButton);
        sendLocalBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.broadcast.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent);
            }
        });
        localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("com.example.broadcast.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, localIntentFilter);
        Intent intent = new Intent(MainActivity.this, RemoteService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "network changes", Toast.LENGTH_SHORT).show();

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                Toast.makeText(context, "network is available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "network is unavailable", Toast.LENGTH_SHORT).show();
            }
        }

    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "receiver local broadcast", Toast.LENGTH_SHORT).show();
        }
    }
}