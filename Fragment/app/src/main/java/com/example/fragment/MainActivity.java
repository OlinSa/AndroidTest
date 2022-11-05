package com.example.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button1);
        button.setOnClickListener(v -> {
            //步骤一：添加一个FragmentTransaction的实例
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            //步骤二：用add()方法加上Fragment的对象rightFragment
            RightFragment rightFragment = new RightFragment();
            transaction.replace(R.id.right, rightFragment);

            //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
            transaction.commit();
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onDestroy");
        super.onRestart();
    }
}