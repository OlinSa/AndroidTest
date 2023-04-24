package com.example.sharedmemory;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.SharedMemory;
import android.util.Log;

import java.io.FileDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class LocalClient {
    public static final String TAG = LocalClient.class.getSimpleName();
    public static final String SERVICE_ACTION = "com.example.sharedmemory.RemoteService";
    public static final String SERVICE_PACKAGE = "com.example.sharedmemory";
    int CONTENT_SIZE = 640 * 480;
    private IRemoteAidlInterface Client;
    private Context mContext;
    private boolean mHasBind;
    private IServiceListener mServiceListener;
    private MemoryFile mServiceShareMemory;
    private FileDescriptor mServiceShareFile;
    private ParcelFileDescriptor mParceServiceShareFile;
    private byte[] mContent = new byte[CONTENT_SIZE];
    private byte[] mContentCopy = new byte[CONTENT_SIZE];
    private ServiceConnection mServiceConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Client = IRemoteAidlInterface.Stub.asInterface(iBinder);
            mHasBind = true;
            Log.d(TAG, "onServiceConnected");
            onConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mHasBind = false;
            Log.d(TAG, "onServiceDisconnected");
            onDisonnect();
        }
    };

    public LocalClient(Context context) {
        mContext = context.getApplicationContext();
        createMemFile();

    }

    private void createMemFile() {
        try {
            mServiceShareMemory = new MemoryFile(TAG + System.currentTimeMillis(), mContent.length);
            Method method = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
            FileDescriptor fd = (FileDescriptor) method.invoke(mServiceShareMemory);
            mParceServiceShareFile = ParcelFileDescriptor.dup(fd);
            if (mServiceShareMemory != null) {
                mServiceShareMemory.allowPurging(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //connect to arservice, call onStart
    public boolean connectService() {
        Intent intent = new Intent(mContext.getApplicationContext(), RemoteService.class);
        boolean isBindService = mContext.getApplicationContext().bindService(intent, mServiceConnect, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bind services isBindService : " + isBindService);
        return isBindService;
    }

    //disconnet service, call onDestroy
    public void disConnectService() {
        if (!mHasBind) {
            return;
        }
        mContext.unbindService(mServiceConnect);
        mHasBind = false;
    }

    public void setServiceListener(IServiceListener listener) {
        mServiceListener = listener;
    }

    public void dataFlow(byte[] value) {
        if (mHasBind) {
            try {
                mServiceShareMemory.writeBytes(value, 0, 0, value.length);
                Client.dataFlow(mParceServiceShareFile, 2, 3, 4, 5, 6);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dataFlowSharyMemeryOnce(byte[] value) {
        if (mHasBind) {
            try {
                mServiceShareMemory.writeBytes(value, 0, 0, value.length);
                Client.dataFlow(mParceServiceShareFile, 2, 3, 4, 5, 6);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getStatus() {
        int status = 0;
        if (mHasBind) {
            try {
                status = Client.getStatus();
                Log.d(TAG, "Client  getStatus : " + status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    private void onConnect() {
        if (mServiceListener == null) {
            return;
        }
        mServiceListener.onServiceConnect();
    }

    private void onDisonnect() {
        releaseShareMemory();
        if (mServiceListener == null) {
            return;
        }
        mServiceListener.onServiceDisconnect();
    }

    private void releaseShareMemory() {
        try {
            mParceServiceShareFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mServiceShareMemory == null) {
            return;
        }
        mServiceShareMemory.close();
        mServiceShareMemory = null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        releaseShareMemory();
    }

    public interface IServiceListener {
        void onServiceConnect();

        void onServiceDisconnect();
    }
}
