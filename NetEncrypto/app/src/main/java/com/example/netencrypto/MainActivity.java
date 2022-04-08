package com.example.netencrypto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.this.getClass().getSimpleName();
    private Button mSndButton;
    private static final String URL = "https://www.baidu.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpRequest request = new HttpRequest(URL);
        mSndButton = findViewById(R.id.snd_button);
        mSndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.request(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.w(TAG, "Get http fail");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.w(TAG, "Get http success, result->"+response.body());
                    }
                });
            }
        });



    }
}