package com.praaktis.exerciseengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.zip.CRC32;

import static java.lang.System.console;
import static java.lang.System.currentTimeMillis;


// VideoEncoder together with its primary function,
// also initiates the TCP connection
// and manages a Receiver object

class VideoEncoder {
    private Bitmap btm;
    private int myCntr = 0;

    private final String VIDEO_FORMAT = MediaFormat.MIMETYPE_VIDEO_AVC; // H.264
    public final int VIDEO_FRAME_PER_SECOND = 30;
    private final int VIDEO_I_FRAME_INTERVAL = 2;
    private final int VIDEO_BITRATE = 1024 * 500;

    private Context mContext;
    private int mHeight;
    private int mWidth;
    private OutputStream mOutputStream;

    private MediaCodec mCodec;
    private MediaMuxer mMuxer;
    private MediaCodec.BufferInfo mBufferInfo;
    private final long TIMEOUT_USEC = 10000L;
    private VideoSurfaceRenderer mVideoSurfaceRenderer;
    private int mMetadataTrackIndex;
    private long start = -1;


    public VideoEncoder(OutputStream os, int w, int h) {
        mHeight = h;
        mWidth = w;
        mOutputStream = os;
        mBufferInfo = new MediaCodec.BufferInfo();

        btm = Bitmap.createBitmap(640, 360, Bitmap.Config.ARGB_8888);

        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private void init() throws IOException {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(VIDEO_FORMAT, 640, 360);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_BITRATE);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FRAME_PER_SECOND);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VIDEO_I_FRAME_INTERVAL);
//        mediaFormat.setInteger(MediaFormat.KEY_CREATE_INPUT_SURFACE_SUSPENDED, 1);
//        mediaFormat.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 100);

        mCodec = MediaCodec.createEncoderByType(VIDEO_FORMAT);
        mCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

//        String videoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
//                + "/test.mp4";

        String videoPath = Globals.mainActivity.getCacheDir().getPath() + "/test.mp4";
        System.out.println(videoPath);

        Log.d("CACHEDIR", videoPath);

        Globals.videoPath = videoPath;

        mMuxer = new MediaMuxer(videoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);


        Globals.mainActivity.setSurface(mCodec.createInputSurface());

        mVideoSurfaceRenderer = new VideoSurfaceRenderer(Globals.mainActivity.getSurface());
        mVideoSurfaceRenderer.start();

        mCodec.start();

    }

    void signalEndOfStream() {
        mCodec.signalEndOfInputStream();
    }

    boolean encodeAndSend(int frameNumber) throws IOException {

        int index = mCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
        if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
            return false;
        } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            Log.d("INFO_CODEC", "OUTPUT FORMAT CHANGED");
            mMetadataTrackIndex = mMuxer.addTrack(mCodec.getOutputFormat());
            mMuxer.start();
        } else if (index >= 0) {
            ByteBuffer outputBuffer = mCodec.getOutputBuffer(index);
            if (outputBuffer != null) {
                if (start == -1) start = currentTimeMillis();
                // EOF not reached
                boolean eofStream = (mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                if (!eofStream) {
                    byte[] buf = new byte[mBufferInfo.size + 8];
                    outputBuffer.position(mBufferInfo.offset);
                    outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                    outputBuffer.get(buf, 8, mBufferInfo.size);
                    long dataLen = mBufferInfo.size;
                    Bytes.setUInt32At(buf, 0, frameNumber | 0x80000000);
                    Bytes.setUInt32At(buf, 4, dataLen);
                    NetworkIO.sendPacket(mOutputStream, (byte) NetworkIOConstants.MSG_FRAME_DATA, buf);

                    //TODO add presentationTimeUs

                    mBufferInfo.presentationTimeUs = (long) (computePresentationTime(frameNumber) / 2.7);
                    mMuxer.writeSampleData(mMetadataTrackIndex, outputBuffer, mBufferInfo);

                } else {
                    Log.d("SENTVIDEO", currentTimeMillis() - start + "");

                }
                mCodec.releaseOutputBuffer(index, false);
                if (eofStream)
                    return true;
            }
        }
        return false;
    }


    public void release() {
        mMuxer.stop();
        mMuxer.release();
        mCodec.stop();
        mCodec.release();
        Globals.mainActivity.getSurface().release();
    }

    public void stopRendered() {
        mVideoSurfaceRenderer.stopAndWait();
        mVideoSurfaceRenderer = null;
    }

    class VideoSurfaceRenderer {
        Surface mSurface;
        Renderer mRenderer;
        long mTimeStart;
        Paint paint;
        Paint cPaint;

        public VideoSurfaceRenderer(Surface surface) {
            mSurface = surface;
        }

        protected void onDraw(Canvas canvas) {

            long start = currentTimeMillis();

            if (Globals.textureBitmap == null) return;

            Bitmap bmp = Globals.textureBitmap;

            canvas.drawBitmap(
                    Bitmap.createScaledBitmap(bmp, 640, 360, false),
                    0, 0, paint);

            long end = currentTimeMillis();
            Log.d("TIMETOCREATEBITMAP", end - start + "");
        }

        public void start() {
            mRenderer = new Renderer();
            mRenderer.setRunning(true);
            mRenderer.start();
            mTimeStart = currentTimeMillis();
            paint = new Paint();
            cPaint = new Paint();
            cPaint.setColor(Color.RED);
        }

        public void stopAndWait() {
            if (mRenderer != null) {
                mRenderer.setRunning(false);
                // we want to make sure complete drawing cycle, otherwise
                // unlockCanvasAndPost() will be the one who may or may not throw
                // IllegalStateException
                try {
                    mRenderer.join();
                } catch (InterruptedException ignore) {
                }
                mRenderer = null;
            }
        }

        class Renderer extends Thread {

            volatile boolean mRunning;

            public void setRunning(boolean running) {
                mRunning = running;
            }

            @Override
            public void run() {
                while (mRunning) {
                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {

                        Canvas canvas = null;

                        try {
                            if (mRunning && mSurface.isValid())
                                canvas = mSurface.lockCanvas(null);
                            else continue;
                            onDraw(canvas);

                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } finally {
                            if (mSurface.isValid() && mRunning)
                                mSurface.unlockCanvasAndPost(canvas);
                        }

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private long computePresentationTime(int frameIndex) {
        return 132 + frameIndex * 1000000 / VIDEO_FRAME_PER_SECOND;
    }
}
