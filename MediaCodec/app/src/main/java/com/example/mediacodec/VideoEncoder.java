package com.example.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

public class VideoEncoder {
    private static final String TAG = VideoEncoder.class.getSimpleName();
    private final static int CACHE_BUFFER_SIZE = 8;
    //This video stream format must be I420
    private final static ArrayBlockingQueue<byte[]> mInputDatasQueue = new ArrayBlockingQueue<byte[]>(CACHE_BUFFER_SIZE);
    //Cachhe video stream which has been encoded.
    private final static ArrayBlockingQueue<byte[]> mOutputDatasQueue = new ArrayBlockingQueue<byte[]>(CACHE_BUFFER_SIZE);
    private final long kTimeoutUs = 5000;
    private MediaCodec mMediaCodec;
    private MediaFormat mMediaFormat;
    private int mViewWidth;
    private int mViewHeight;
    private Handler mVideoEncoderHandler;
    private boolean mIsAsync = false;
    private HandlerThread mVideoEncoderHandlerThread = new HandlerThread("VideoEncoder");
    private MediaCodec.Callback mCallback;

    public VideoEncoder(String mimeType, int viewwidth, int viewheight, boolean isAsync) {
        try {
            mMediaCodec = MediaCodec.createEncoderByType(mimeType);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mMediaCodec = null;
            return;
        }

        this.mViewWidth = viewwidth;
        this.mViewHeight = viewheight;
        this.mIsAsync = isAsync;
        mVideoEncoderHandlerThread.start();
        mVideoEncoderHandler = new Handler(mVideoEncoderHandlerThread.getLooper());

        mMediaFormat = MediaFormat.createVideoFormat(mimeType, mViewWidth, mViewHeight);
        mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1920 * 1280);
        mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        if (isAsync) {
            mCallback = new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec mediaCodec, int id) {
                    ByteBuffer inputBuffer = mediaCodec.getInputBuffer(id);
                    inputBuffer.clear();
                    byte[] dataSources = mInputDatasQueue.poll();
                    int length = 0;
                    if (dataSources != null) {
                        inputBuffer.put(dataSources);
                        length = dataSources.length;
                    }
                    mediaCodec.queueInputBuffer(id, 0, length, 0, 0);
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec mediaCodec, int id, @NonNull MediaCodec.BufferInfo bufferInfo) {
                    ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(id);
                    MediaFormat outputFormat = mMediaCodec.getOutputFormat(id);
                    if (outputBuffer != null && bufferInfo.size > 0) {
                        byte[] buffer = new byte[outputBuffer.remaining()];
                        outputBuffer.get(buffer);
                        if (!mOutputDatasQueue.offer(buffer)) {
                            Log.d(TAG, "Offer to queue failed, queue in full state");
                        }
                    }
                    mMediaCodec.releaseOutputBuffer(id, true);
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
        }
    }

    public void inputFrameToEncoder(byte[] needEncodeData) {
        boolean inputResult = mInputDatasQueue.offer(needEncodeData);
        Log.d(TAG, "-----> inputEncoder queue result = " + inputResult + " queue current size = " + mInputDatasQueue.size());
    }    protected final Thread mEncodeThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!mEncodeThread.isInterrupted()) {
                if (mMediaCodec != null) {
                    int index = mMediaCodec.dequeueInputBuffer(kTimeoutUs);
                    if (index >= 0) {
                        ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);
                        inputBuffer.clear();

                        byte[] dataSource = null;
                        byte[] dataSources = mInputDatasQueue.poll();
                        int length = 0;
                        if (dataSources != null) {
                            inputBuffer.put(dataSources);
                            length = dataSources.length;
                        }
                        mMediaCodec.queueInputBuffer(index, 0, length, 0, 0);
                    }

                    MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                    int outputBufIndex = mMediaCodec.dequeueOutputBuffer(info, kTimeoutUs);
                    if (outputBufIndex >= 0) {
                        ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufIndex);
                        byte[] buffer = new byte[outputBuffer.remaining()];
                        outputBuffer.get(buffer);
                        if (!mOutputDatasQueue.offer(buffer)) {
                            Log.d(TAG, "Offer to queue failed, queue in full state");
                        }
                        mMediaCodec.releaseOutputBuffer(outputBufIndex, 0);
                    }

                }
            }
        }
    });

    public byte[] pollFrameFromEncoder() {
        return mOutputDatasQueue.poll();
    }

    public void startEncoder() {
        if (mMediaCodec != null) {
            mMediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
            if (mIsAsync) {
                mMediaCodec.setCallback(mCallback, mVideoEncoderHandler);
            } else {
                if (mEncodeThread != null) {
                    mEncodeThread.start();
                }
            }
        } else {
            throw new IllegalArgumentException("startEncoder failed, is the MediaCodec has been init correct?");
        }
    }

    public void stopEncoder() {
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.setCallback(null);
        }
        if (!mIsAsync) {
            if (mEncodeThread != null) {
                mEncodeThread.stop();
            }
        }
    }

    public void release() {
        if (mMediaCodec != null) {
            mInputDatasQueue.clear();
            mOutputDatasQueue.clear();
            mMediaCodec.release();
        }
    }


}
