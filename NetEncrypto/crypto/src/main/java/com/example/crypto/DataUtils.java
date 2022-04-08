package com.example.crypto;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

public class DataUtils {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] base64Decode(String data) {
        try {
            return Base64.decode(data, Base64.NO_WRAP);
        }catch (Exception e) {
            return java.util.Base64.getMimeDecoder().decode(data);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String base64Encode(byte[] data) {
        try {
            return Base64.encodeToString(data, Base64.NO_WRAP);
        }catch (Exception e) {
            return java.util.Base64.getMimeEncoder().encodeToString(data);
        }

    }

}
