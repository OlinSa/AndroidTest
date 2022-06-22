package com.example.mediacodec;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final static String MIME_FORMAT = "video/avc"; //support h.264

    private TextureView mCameraTexture;
    private TextureView mDecodeTexture;

    private VideoDecoder mVideoDecoder;
    private VideoEncoder mVideoEncoder;

    private Camera mCamera;
    private int mPreviewWidth;
    private int mPreviewHeight;

    private Camera.PreviewCallback mPreviewCallBack = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            byte[] i420bytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, i420bytes, 0, mPreviewWidth * mPreviewHeight);
            System.arraycopy(bytes, mPreviewWidth * mPreviewHeight + mPreviewWidth * mPreviewHeight / 4, i420bytes, mPreviewWidth * mPreviewHeight, mPreviewWidth * mPreviewHeight / 4);
            System.arraycopy(bytes, mPreviewWidth * mPreviewHeight, i420bytes, mPreviewWidth * mPreviewHeight + mPreviewWidth * mPreviewHeight / 4, mPreviewWidth * mPreviewHeight / 4);
            if (mVideoEncoder != null) {
                mVideoEncoder.inputFrameToEncoder(i420bytes);
            }
        }
    };

    private TextureView.SurfaceTextureListener mCameraTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            openCamera(surface, width, height);
            mVideoEncoder = new VideoEncoder(MIME_FORMAT, mPreviewWidth, mPreviewHeight);
            mVideoEncoder.startEncoder();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            if (mVideoEncoder != null) {
                mVideoEncoder.release();
            }
            closeCamera();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    private TextureView.SurfaceTextureListener mDecodeTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            System.out.println("----------" + width + " ," + height);
            mVideoDecoder = new VideoDecoder(MIME_FORMAT, new Surface(surface), mPreviewWidth, mPreviewHeight);
            mVideoDecoder.setEncoder(mVideoEncoder);
            mVideoDecoder.startDecoder();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            mVideoDecoder.stopDecoder();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Granted");
            initView();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    private void initView() {
        mCameraTexture = findViewById(R.id.camera);
        mDecodeTexture = findViewById(R.id.decode);
        mCameraTexture.setSurfaceTextureListener(mCameraTextureListener);
        mDecodeTexture.setSurfaceTextureListener(mDecodeTextureListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initView();
    }

    private void openCamera(SurfaceTexture texture, int width, int height) {
        if (texture == null) {
            Log.e(TAG, "openCamera need SurfaceTexture");
            return;
        }

        mCamera = Camera.open(0);
        try {
            mCamera.setPreviewTexture(texture);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.YV12);
            List<Camera.Size> list = parameters.getSupportedPreviewSizes();
            for (Camera.Size size : list) {
                System.out.println("----size width = " + size.width + " size height = " + size.height);
            }

            mPreviewWidth = 640;
            mPreviewHeight = 480;
            parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(mPreviewCallBack);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mCamera = null;
        }
    }

    private void closeCamera() {
        if (mCamera == null) {
            Log.e(TAG, "Camera not open");
            return;
        }
        mCamera.stopPreview();
        mCamera.release();
    }
}