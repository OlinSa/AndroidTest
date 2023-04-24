// IRemoteAidlInterface.aidl
package com.example.sharedmemory;

// Declare any non-default types here with import statements

interface IRemoteAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void dataFlow(in ParcelFileDescriptor data, int format , int width, int height, int stride, int oritention);
    int getStatus();
}