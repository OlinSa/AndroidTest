package com.example.netencrypto;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequest {
    private Call mCall;
    HttpRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        mCall = client.newCall(request);
    }
    public void request(Callback callback) {
        if (mCall != null) {
            if (mCall.isExecuted()) {
                mCall.clone().enqueue(callback);
            } else {
                mCall.enqueue(callback);
            }
        }
    }
}
