package com.example.reflection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setPackage("com.example.fragment");
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        useDexClassLoader(intent);
    }

    private void useDexClassLoader(Intent intent) {
        Log.d(TAG, "intent=" + intent);
        PackageManager pm = getApplicationContext().getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        dummpResolveInfo(resolveInfoList);
    }

    private void dummpResolveInfo(List<ResolveInfo> resolveInfos) {
        Log.d(TAG, "resolveInfos size=" + resolveInfos.size());
        for (ResolveInfo info : resolveInfos) {
            ActivityInfo activityInfo = info.activityInfo;
            String packageName = activityInfo.packageName;
            String apkPath = activityInfo.applicationInfo.sourceDir;
            String dexOutputDir = getApplicationInfo().dataDir;
            String nativeLibraryDir = activityInfo.applicationInfo.nativeLibraryDir;
            DexClassLoader calssLoader = new DexClassLoader(apkPath, dexOutputDir, nativeLibraryDir,
                    this.getClass().getClassLoader());
            Log.d(TAG, "packageName=" + packageName);
            Log.d(TAG, "apkPath=" + apkPath);
            Log.d(TAG, "dexOutputDir=" + dexOutputDir);
            Log.d(TAG, "calssLoader=" + calssLoader);

        }
    }


}