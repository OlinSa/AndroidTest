package com.example.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class VideoDecoder {
    private static final String TAG = VideoDecoder.class.getSimpleName();
    private final static int CONFIGURE_FLAG_DECODE = 0;
    private final static int CACHE_BUFFER_SIZE = 8;
    //This video stream format must be I420
    private final static ArrayBlockingQueue<byte[]> mInputDatasQueue = new ArrayBlockingQueue<byte[]>(CACHE_BUFFER_SIZE);
    //Cachhe video stream which has been encoded.
    private final static ArrayBlockingQueue<byte[]> mOutputDatasQueue = new ArrayBlockingQueue<byte[]>(CACHE_BUFFER_SIZE);

    private HandlerThread mVideoDecoderHandlerThread = new HandlerThread("VideoDecoder");
    private MediaCodec mMediaCodec;
    private MediaCodec.Callback mCallback = new MediaCodec.Callback() {

        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);
            inputBuffer.clear();

            byte[] dataSource = null;
            if (mVideoEncoder != null) {
                dataSource = mVideoEncoder.pollFrameFromEncoder();
            }
            int length = 0;
            if (dataSource != null) {
                inputBuffer.put(dataSource);
                length = dataSource.length;
            }
            codec.queueInputBuffer(index, 0, length, 0, 0);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(index);
            MediaFormat outputFormat = mMediaCodec.getOutputFormat(index);
            if (outputBuffer != null && info.size > 0) {
                byte[] buffer = new byte[outputBuffer.remaining()];
                outputBuffer.get(buffer);
            }
            mMediaCodec.releaseOutputBuffer(index, true);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            Log.d(TAG, "onError codec=" + codec + "exception e=" + e);
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            Log.d(TAG, "onOutputFormatChanged codec=" + codec + " format=" + format);
        }
    };
    private MediaCodec.OnFrameRenderedListener mOnFrameRenderedListener = new MediaCodec.OnFrameRenderedListener() {
        @Override
        public void onFrameRendered(@NonNull MediaCodec codec, long presentationTimeUs, long nanoTime) {
            Log.d(TAG, "onFrameRendered codec=" + codec + " presentationTimeUs=" + presentationTimeUs + " ns="+nanoTime);
        }
    };
    private MediaFormat mMediaFormat;
    private Surface mSurface;
    private int mViewWidth;
    private int mViewHeight;
    private Handler mVideoDecoderHandler;
    private VideoEncoder mVideoEncoder;

    public VideoDecoder(String mimeType, Surface surface, int viewWidth, int viewHeight) {
        try {
            mMediaCodec = MediaCodec.createDecoderByType(mimeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mSurface = surface;
        this.mViewWidth = viewWidth;
        this.mViewHeight = viewHeight;

        if (mMediaCodec == null || this.mSurface == null) {
            Log.e(TAG, "MediaCodec or Surface is null");
        }
        mVideoDecoderHandlerThread.start();
        mVideoDecoderHandler = new Handler(mVideoDecoderHandlerThread.getLooper());
        mMediaFormat = MediaFormat.createVideoFormat(mimeType, mViewWidth, mViewHeight);
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1920 * 1280);
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
    }

    public void setEncoder(VideoEncoder videoEncoder) {
        this.mVideoEncoder = videoEncoder;
    }

    public void startDecoder() {
        if (mMediaCodec != null && mSurface != null) {
            mMediaCodec.setCallback(mCallback, mVideoDecoderHandler);
            mMediaCodec.configure(mMediaFormat, mSurface, null, CONFIGURE_FLAG_DECODE);
            mMediaCodec.setOnFrameRenderedListener(mOnFrameRenderedListener, mVideoDecoderHandler);
            mMediaCodec.start();
        } else {
            Log.e(TAG, "startDecoder failed!");
        }
    }

    public void stopDecoder() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
        }
    }

    public void release() {
        if (mMediaCodec != null) {
            mMediaCodec.release();
            mMediaCodec = null;
        }
    }
}
