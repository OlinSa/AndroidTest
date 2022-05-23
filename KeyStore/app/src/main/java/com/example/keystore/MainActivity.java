package com.example.keystore;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String value = "Password/Token to be encrypted";
        String encryptedValue = EncryptionUtils.encrypt(this, value);
        Timber.d(" Encrypted Value :" + encryptedValue);

        String decryptedValue = EncryptionUtils.decrypt(this, encryptedValue);
        Timber.d(" Decrypted Value :" + decryptedValue);
    }
}