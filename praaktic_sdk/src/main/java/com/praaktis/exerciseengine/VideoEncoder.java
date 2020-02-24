package com.praaktis.exerciseengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static java.lang.System.currentTimeMillis;


// VideoEncoder together with its primary function,
// also initiates the TCP connection
// and manages a Receiver object

class VideoEncoder {
    private Bitmap btm;
    private int myCntr = 0;

    private final String VIDEO_FORMAT = MediaFormat.MIMETYPE_VIDEO_AVC; // H.264
    public final int VIDEO_FRAME_PER_SECOND = 15;
    private final int VIDEO_I_FRAME_INTERVAL = 2;
    private final int VIDEO_BITRATE = 1024 * 500;
    private MediaFormat mOutputFormat;

    private Context mContext;
    private int mHeight;
    private int mWidth;
    private OutputStream mOutputStream;

    private MediaCodec mCodec;
    private boolean codec_started = false;

    private int mMetadataTrackIndex;
    private MediaMuxer mMuxer;
    private boolean muxer_sarted = false;

    private MediaCodec.BufferInfo mBufferInfo;
    private final long TIMEOUT_USEC = 10000L;
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
        prevOutputPTSUs = System.nanoTime() / 1000L;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    private void init() throws IOException {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(VIDEO_FORMAT, 640, 360);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_BITRATE);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, VIDEO_FRAME_PER_SECOND);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VIDEO_I_FRAME_INTERVAL);
//        mediaFormat.setInteger(MediaFormat.KEY_CREATE_INPUT_SURFACE_SUSPENDED, 1);
//        mediaFormat.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 100);

        mCodec = MediaCodec.createEncoderByType(VIDEO_FORMAT);
        mCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        mOutputFormat = mCodec.getOutputFormat();
//        String videoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
//                + "/test.mp4";

        String videoPath = Globals.mainActivity.getCacheDir().getPath() + "/test.mp4";
        System.out.println(videoPath);

        Log.d("CACHEDIR", videoPath);

        Globals.videoPath = videoPath;

        mMuxer = new MediaMuxer(videoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mCodec.start();
    }

    void signalEndOfStream() {
        mCodec.signalEndOfInputStream();
    }

    boolean getAndSend(int frameNumber) throws IOException {

        int index = mCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
        if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
            Log.d("INFO_CODEC", "TRY AGAIN LATER");
            return false;
        } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            Log.d("INFO_CODEC", "OUTPUT FORMAT CHANGED");
            mMetadataTrackIndex = mMuxer.addTrack(mCodec.getOutputFormat());
            mMuxer.start();
            muxer_sarted = true;
            mOutputFormat = mCodec.getOutputFormat();
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

    boolean encode(int flag){
        Log.d("INFO_CODEC", "INPUT BUFFER");
        byte[] frame;

        synchronized (Globals.capturedFrames) {
            if (Globals.capturedFrames.isEmpty()) return true;
            frame = Globals.capturedFrames.get(0);
            Globals.capturedFrames.remove(0);
        }

        //ToDo Play With TIMEOUT_USEC
        int inputBufferId = mCodec.dequeueInputBuffer(TIMEOUT_USEC);
        if (inputBufferId >= 0) {
            ByteBuffer inputBuffer = mCodec.getInputBuffer(inputBufferId);
            // fill inputBuffer with valid data
            inputBuffer.clear();

            int size = frame.length;
            inputBuffer.put(frame);

            mCodec.queueInputBuffer(inputBufferId, 0, size, getPTSUs(), flag);
        }
        return inputBufferId >= 0;
    }

    public void release() {
        if (muxer_sarted) {
            mMuxer.stop();
            mMuxer.release();
        }
        if (codec_started) {
            mCodec.stop();
            mCodec.release();
        }
        Globals.mainActivity.getSurface().release();
    }

    private long prevOutputPTSUs = 0;

    /**
     * get next encoding presentationTimeUs
     *
     * @return
     */
    protected long getPTSUs() {
        long result = (System.nanoTime()) / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }


}
