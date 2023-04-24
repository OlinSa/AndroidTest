package com.example.sharedmemory;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.sharedmemory.utils.MemoryFileHelper;

import java.io.FileInputStream;
import java.io.IOException;

public class RemoteService extends Service {
    public static final String TAG = RemoteService.class.getSimpleName();
    int size = 1920 * 1080;
    byte[] dataContent = new byte[size];
    private byte[] mPackageContent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RemoteServiceBind();
    }

    private void createBufferIfNeed(int length) {
        if (length <= 0) {
            Log.d(TAG, "createBufferIfNeed length <= 0");
        }
        if (mPackageContent != null && mPackageContent.length == length) {
            return;
        }
        mPackageContent = new byte[length];
    }

    public class RemoteServiceBind extends IRemoteAidlInterface.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void dataFlow(ParcelFileDescriptor data, int format, int width, int height, int stride, int oritention) throws RemoteException {
            long time = System.currentTimeMillis();
            FileInputStream fileInputStream = new FileInputStream(data.getFileDescriptor());
            try {
                fileInputStream.read(dataContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            MemoryFile memoryFile = MemoryFileHelper.openMemoryFile(data, dataContent.length, MemoryFileHelper.OPEN_READWRITE);
//            try {
//                memoryFile.readBytes(dataContent, 0, 0, dataContent.length);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            memoryFile.close();
            Log.d(TAG, "Service dataFlow data: " + new String(dataContent));
        }

        @Override
        public int getStatus() throws RemoteException {
            Log.d(TAG, "Service getStatus");
            return 1;
        }
    }

}