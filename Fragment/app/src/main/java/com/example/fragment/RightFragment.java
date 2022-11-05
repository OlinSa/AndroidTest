package com.example.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class RightFragment extends Fragment {
    private final String TAG = RightFragment.this.getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_right, null);
        Button button = view.findViewById(R.id.button2);
        button.setOnClickListener(v -> Toast.makeText(getActivity(), "我是fragment", Toast.LENGTH_SHORT).show());
        return view;
    }
}
