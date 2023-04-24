package com.example.sharedmemory.utils;

import android.os.Build;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.SharedMemory;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class MemoryFileHelper {
    private static final int PROT_READ = 0x1;
    public static final int OPEN_READONLY = PROT_READ;
    private static final int PROT_WRITE = 0x2;
    public static final int OPEN_READWRITE = PROT_READ | PROT_WRITE;

    public static MemoryFile createMemoryFile(String name, int length) {
        try {
            return new MemoryFile(name, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MemoryFile openMemoryFile(ParcelFileDescriptor pfd, int length, int mode) {
        if (pfd == null) {
            throw new IllegalArgumentException("ParcelFileDescriptor is null");
        }
        return openMemoryFile(pfd, length, mode);
    }

    public static MemoryFile openMemoryFile(FileDescriptor fd, int length, int mode) {
        if (mode != OPEN_READONLY && mode != OPEN_READWRITE)
            throw new IllegalArgumentException("invalid mode, only support OPEN_READONLY and OPEN_READWRITE");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            return openMemoryFileV26(fd, length, mode);
        }

        return openMemoryFileV27(fd, mode);
    }

    private static MemoryFile openMemoryFileV27(FileDescriptor fd, int mode) {
        MemoryFile memoryFile = null;
        try {
            memoryFile = new MemoryFile("service.remote", 1);
            memoryFile.close();
            Class<?> c = Class.forName("android.os.SharedMemory");
            Object sharedMemory = InvokeUtil.newInstanceOrThrow(c, fd);
            //SharedMemory sm;

            ByteBuffer mapping = null;
            if (mode == OPEN_READONLY) {
                mapping = (ByteBuffer) InvokeUtil.invokeMethod(sharedMemory, "mapReadOnly");
            } else {
                mapping = (ByteBuffer) InvokeUtil.invokeMethod(sharedMemory, "mapReadWrite");
            }

            InvokeUtil.setValueOfField(memoryFile, "mSharedMemory", sharedMemory);
            InvokeUtil.setValueOfField(memoryFile, "mMapping", mapping);
            return memoryFile;
        } catch (Exception e) {
            throw new RuntimeException("openMemoryFile failed!", e);
        }

    }

    public static MemoryFile openMemoryFileV26(FileDescriptor fd, int length, int mode) {
        MemoryFile memoryFile = null;
        try {
            memoryFile = new MemoryFile("service.remote", 1);
            memoryFile.close();
            Class<?> c = MemoryFile.class;
            InvokeUtil.setValueOfField(memoryFile, "mFD", fd);
            InvokeUtil.setValueOfField(memoryFile, "mLength", length);
            long address = (long) InvokeUtil.invokeStaticMethod(c, "native_mmap", fd, length, mode);
            InvokeUtil.setValueOfField(memoryFile, "mAddress", address);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return memoryFile;
    }

    public static ParcelFileDescriptor getParcelFileDescriptor(MemoryFile memoryFile) {
        if (memoryFile == null) {
            throw new IllegalArgumentException("memoryFile is null");
        }
        ParcelFileDescriptor pfd = null;

        try {
            FileDescriptor fd = getFileDescriptor(memoryFile);
            pfd = (ParcelFileDescriptor) InvokeUtil.newInstanceOrThrow(ParcelFileDescriptor.class, fd);
            return pfd;
        } catch (Exception e) {
            throw new RuntimeException("InvokeUtil.newInstanceOrThrow failed", e);
        }
    }

    public static FileDescriptor getFileDescriptor(MemoryFile memoryFile) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvocationTargetException {
        if (memoryFile == null) {
            throw new IllegalArgumentException("memoryFile is null");
        }
        FileDescriptor fd;
        fd = (FileDescriptor) InvokeUtil.invokeMethod(memoryFile, "getFileDescriptor");
        return fd;
    }

}
