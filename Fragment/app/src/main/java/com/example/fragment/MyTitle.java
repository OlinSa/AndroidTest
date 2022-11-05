package com.example.fragment;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MyTitle extends FrameLayout {
    private final Button leftButton;

    private final TextView titleText;

    public MyTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.my_title, this);
        titleText = (TextView) findViewById(R.id.title_text);
        leftButton = (Button) findViewById(R.id.button_left);
        leftButton.setOnClickListener(v -> ((Activity) getContext()).finish());
    }

    public void setTitleText(String text) {
        titleText.setText(text);
    }

    public void setLeftButtonText(String text) {
        leftButton.setText(text);
    }

    public void setLeftButtonListener(OnClickListener l) {
        leftButton.setOnClickListener(l);
    }

}