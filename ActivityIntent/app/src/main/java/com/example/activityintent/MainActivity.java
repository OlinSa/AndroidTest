package com.example.activityintent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.jump_second_activity);
        button.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "you click jump_second_activity", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(MainActivity.this, SecondActivity.class); //显式Intent
            Intent intent = new Intent(); //隐式Intent不指明目标组件的class，只定义希望的Action及Data等相关信息，由系统决定使用哪个目标组件
            intent.setAction("android.intent.action.ACTION_START");
            intent.addCategory("android.intent.action.MY_CATEGORY");
//            intent.addCategory("android.intent.category.DEFAULT");
            startActivityForResult(intent, 1);
        });
    }

    // onActivityResult事件处理程序将在父级触发
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String resultData = data.getStringExtra("data_return");

                    try {
                        Log.i("FirstActivity", resultData);
                        Toast.makeText(MainActivity.this, "FirstActivity" + resultData, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.i("FirstActivity", e.toString());
                    }
                }
                break;
            default:
                break;
        }
    }

}