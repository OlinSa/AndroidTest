package com.example.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BUMP_MSG = 1;
    IRemoteService iRemoteService = null;
    Button killButton;
    TextView callbackText;
    boolean isBind = false;
    private InternalHandler handler;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iRemoteService = IRemoteService.Stub.asInterface(service);
            callbackText.setText("Bind success.");
            killButton.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callbackText.setText("Service has unexpectedly disconnected");
            iRemoteService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.bind);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RemoteService.class);
            intent.setAction(IRemoteService.class.getName());
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            isBind = true;
            callbackText.setText("Binding.");
        });
        button = findViewById(R.id.unbind);
        button.setOnClickListener(v -> {
            if (isBind) {
                unbindService(mConnection);
            }
            killButton.setEnabled(false);
            isBind = false;
            callbackText.setText("Unbinding");
        });
        killButton = findViewById(R.id.kill);
        killButton.setOnClickListener(v -> {
            if (iRemoteService != null) {
                int pid = 0;
                try {
                    pid = iRemoteService.getPid();
                    Process.killProcess(pid);
                    Log.d(TAG, "Killed service process pid=" + pid);
                    callbackText.setText("Killed service process pid=" + pid);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
        killButton.setEnabled(false);
        callbackText = findViewById(R.id.callback);
        callbackText.setText("Not attached");
        handler = new InternalHandler(callbackText);
    }

    private static class InternalHandler extends Handler {
        private final WeakReference<TextView> weakTextView;

        InternalHandler(TextView textView) {
            weakTextView = new WeakReference<>(textView);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BUMP_MSG:
                    TextView textView = weakTextView.get();
                    if (textView != null) {
                        textView.setText("Received from service: " + msg.arg1);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}