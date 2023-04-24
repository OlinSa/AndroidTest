package com.example.sharedmemory;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private LocalClient mLocalClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mLocalClient = new LocalClient(this);
        mLocalClient.setServiceListener(new LocalClient.IServiceListener() {
            @Override
            public void onServiceConnect() {
                Log.d(TAG, "onServiceConnect");
                mLocalClient.dataFlow("msg1".getBytes());
                mLocalClient.dataFlow("msg2".getBytes());
                mLocalClient.dataFlow("msg3".getBytes());
            }

            @Override
            public void onServiceDisconnect() {
                Log.d(TAG, "onServiceDisconnect");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalClient.disConnectService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocalClient.connectService();
    }
}