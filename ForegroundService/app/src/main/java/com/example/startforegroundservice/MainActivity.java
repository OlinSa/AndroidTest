package com.example.startforegroundservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("dev2qa.com - Android Foreground Service Example.");
        Button startServiceButton = (Button) findViewById(R.id.start_foreground_service_button);
        startServiceButton.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, MyForeGroundService.class);
            intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }

            boolean bound = bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    Log.d(TAG, "onServiceConnected componentName=" + componentName.getClassName());
                    IForegroundyAidlInterface foregroundyAidlInterface = IForegroundyAidlInterface.Stub.asInterface(iBinder);

                    try {
                        foregroundyAidlInterface.say("Hi");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    Log.d(TAG, "onServiceDisconnected componentName=" + componentName.getClassName());
                }
            }, BIND_AUTO_CREATE);

        });
        Button stopServiceButton = (Button) findViewById(R.id.stop_foreground_service_button);
        stopServiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyForeGroundService.class);
            intent.setAction(MyForeGroundService.ACTION_STOP_FOREGROUND_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                stopService(intent);
            }
        });
    }
}