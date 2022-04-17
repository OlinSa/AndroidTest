package com.example.surfaceviewswitch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private final int STATE_RL = 0;
    private final int STATE_LR = 1;
    // remote view
    private SurfaceView mRemoteView;
    private SurfaceHolder mRemoteHolder;
    private RelativeLayout mRemoteRl;

    // local view
    private SurfaceView mLocalView;
    private SurfaceHolder mLocalHolder;
    private RelativeLayout mLocalRl;

    private int mScreenW;
    private int mScreenH;

    private int defaultLocalHeight = 200;
    private int defaultLocalwidth = 400;
    private int mState = STATE_RL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenW = dm.widthPixels;
        mScreenH = dm.heightPixels - 500;

        mRemoteView = findViewById(R.id.remote_view);
        mRemoteRl = findViewById(R.id.remote_rl);
        mLocalView = findViewById(R.id.local_view);
        mLocalRl = findViewById(R.id.local_rl);

        mRemoteHolder = mRemoteView.getHolder();
        mLocalHolder = mLocalView.getHolder();

        LayoutParams params = new LayoutParams(mScreenW, mScreenH);
        mRemoteView.setLayoutParams(params);

        mRemoteHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                Log.i(TAG, "remote surface view created");
                fillColor(surfaceHolder, Color.RED, "remote view");
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int w, int h) {
                Log.i(TAG, String.format("remote surface view change!format=%d,w=%d,h=%d", format, w, h));
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });

        LayoutParams params1 = new LayoutParams(defaultLocalwidth, defaultLocalHeight);
        mLocalView.setLayoutParams(params1);
        mRemoteHolder.setFormat(PixelFormat.TRANSPARENT);
        mLocalHolder.setFormat(PixelFormat.TRANSPARENT);
        mLocalHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                Log.i(TAG, "local surface view created");
                fillColor(surfaceHolder, Color.YELLOW, "local view");
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int w, int h) {
                Log.i(TAG, String.format("local surface view change!format=%d,w=%d,h=%d", format, w, h));
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });

        mLocalView.setOnClickListener(this);
        mRemoteView.setOnClickListener(this);

//        zoomOpera(mRemoteView, mRemoteRl, defaultLocalwidth, defaultLocalHeight, RelativeLayout.CENTER_IN_PARENT);
        mLocalView.setZOrderOnTop(true);
        mState = STATE_RL;
    }

    private void fillColor(SurfaceHolder surfaceHolder, int color, String text) {
        Canvas canvas = surfaceHolder.lockCanvas();
        Paint p = new Paint();
        p.setColor(color);
        Rect a = new Rect(0, 0, surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());
        canvas.drawRect(a, p);
        canvas.drawText(text, 0, 0, p);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remote_view:
                Log.i(TAG, "remote view clicked！state=" + mState);
                if (mState != STATE_RL) {
                    /*
                     * 1. Place the local view on top and shrink it
                     * 2. Enlarge the remote view, which is located below the local view
                     */
                    int localWidth = mLocalView.getMeasuredHeight();
                    int localHeight = mLocalView.getMeasuredHeight();
                    int localLayoutMode = mLocalRl.getLayoutMode();
                    int remoteWidth = mRemoteView.getMeasuredHeight();
                    int remoteHeight = mRemoteView.getMeasuredHeight();
                    int remoteLayoutMode = mRemoteRl.getLayoutMode();
                    Log.i(TAG, String.format("remote(%d,%d) <--> local(%d,%d)", remoteWidth, remoteHeight, localWidth, localHeight));

                    RelativeLayout paretview = (RelativeLayout) mLocalRl.getParent();
                    paretview.removeView(mRemoteRl);
                    paretview.removeView(mLocalRl);

                    zoomOutView(mRemoteView, mRemoteRl, localWidth, localHeight, localLayoutMode);
                    zoomInView(mLocalView, mLocalRl, remoteWidth, remoteHeight, remoteLayoutMode);
                    paretview.addView(mRemoteRl);
                    paretview.addView(mLocalRl);
                    mLocalView.setZOrderOnTop(true);
                    mState = STATE_RL;
                }
                break;
            case R.id.local_view:
                Log.i(TAG, "local view clicked! state=" + mState);
                if (mState != STATE_LR) {
                    int localWidth = mLocalView.getMeasuredHeight();
                    int localHeight = mLocalView.getMeasuredHeight();
                    int localLayoutMode = mLocalRl.getLayoutMode();
                    int remoteWidth = mRemoteView.getMeasuredHeight();
                    int remoteHeight = mRemoteView.getMeasuredHeight();
                    int remoteLayoutMode = mRemoteRl.getLayoutMode();
                    Log.i(TAG, String.format("local(%d,%d) <--> remote(%d,%d)", localWidth, localHeight, remoteWidth, remoteHeight));

                    RelativeLayout paretview = (RelativeLayout) mLocalRl.getParent();
                    paretview.removeView(mLocalRl);
                    paretview.removeView(mRemoteRl);

                    zoomInView(mRemoteView, mRemoteRl, localWidth, localHeight, localLayoutMode);
                    zoomOutView(mLocalView, mLocalRl, remoteWidth, remoteHeight, remoteLayoutMode);
                    paretview.addView(mLocalRl);
                    paretview.addView(mRemoteRl);
                    mRemoteView.setZOrderOnTop(true);
                    mState = STATE_LR;
                }
                break;
            default:
                break;
        }
    }

    protected void zoomOutView(SurfaceView surfaceView, View view, int w, int h, int rule) {
        zoomOpera(surfaceView, view, w, h, rule);
    }

    protected void zoomInView(SurfaceView surfaceView, View view, int w, int h, int rule) {
        zoomOpera(surfaceView, view, w, h, rule);
    }

    private void zoomOpera(SurfaceView surfaceView, View view, int w, int h, int rule) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(rule, RelativeLayout.TRUE);
        surfaceView.setLayoutParams(params);
        surfaceView.setBackgroundResource(android.R.color.transparent);
        params = new RelativeLayout.LayoutParams(w, h);
        params.addRule(rule, RelativeLayout.TRUE);
        view.setLayoutParams(params);
    }
}