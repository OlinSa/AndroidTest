package com.example.activityintent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Toast.makeText(SecondActivity.this, "SecondActivity", Toast.LENGTH_SHORT).show();
    }

    //This method is executed when the phone presses the back button.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data_return", "Hello First activity");
        setResult(RESULT_OK, intent);
        finish();
    }
}