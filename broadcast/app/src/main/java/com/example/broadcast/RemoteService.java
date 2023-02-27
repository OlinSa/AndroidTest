package com.example.broadcast;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class RemoteService extends Service {
    private static final String TAG = RemoteService.class.getSimpleName();
    MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter remoteIntentFilter;

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        myBroadcastReceiver = new MyBroadcastReceiver();
        remoteIntentFilter = new IntentFilter();
        remoteIntentFilter.addAction("com.example.broadcast.MY_BROADCAST");
        registerReceiver(myBroadcastReceiver, remoteIntentFilter);
        return super.onStartCommand(intent, flags, startId);
    }
}