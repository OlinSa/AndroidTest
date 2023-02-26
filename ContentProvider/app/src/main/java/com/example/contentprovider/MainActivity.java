package com.example.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ContentResolver mContentResolver = null;
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView message = findViewById(R.id.message);
        mContentResolver = getContentResolver();
        message.setText("Add initial data");

        for (int i = 0; i < 10; i++) {
            ContentValues values = new ContentValues();
            values.put(Constant.COLUMN_NAME, "fanrunqi" + i);
            mContentResolver.insert(Constant.CONTENT_URI, values);
        }

        message.setText("Query data");
        cursor = mContentResolver.query(Constant.CONTENT_URI, new String[]{Constant.COLUMN_ID, Constant.COLUMN_NAME}, null, null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            String s = cursor.getString(cursor.getColumnIndex(Constant.COLUMN_NAME));
            Log.i(TAG, "i=" + (++i) + " data= " + s);
        }
    }
}