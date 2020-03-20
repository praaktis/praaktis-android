package com.praaktis.exerciseengine.RawPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.praaktis.exerciseengine.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class H264RawPlayerActivity extends AppCompatActivity {
    private static final int VIDEO_HEIGHT = 360;
    private static final int VIDEO_WIDTH = 640;

    private int mCounter;
    private Handler mHandler;
    private MyView mMyView;
    volatile boolean mFrameTimerRunning;
    private Thread mFrameTimerThread;
    private ArrayList<byte []> mH264Frames = new ArrayList<>();
    private H264Stream mH264Stream;
    private MediaCodec mMediaCodec;
    private int mFrameNumber = 0;
    private int mOutFrameNumber = 0;
    private Bitmap mBitmap;
    private boolean mPaused =false;
    private volatile boolean mFlushCodec = false;
    private ArrayList<Caption> mCaptions;
    private Object [] mLastCaptions = null;
    private HashMap<Integer, Object []> mCaptionValsMap;

    private class Caption {
        public Caption(String caption, float x, float y) {
            this.caption = caption;
            this.x = x;
            this.y = y;
        }
        public String caption;
        public float x;
        public float y;
    }

    static {
        System.loadLibrary("native-lib");
    }

    private void loadH264(String fname) {
       InputStream is = null;
        try {
            is = getAssets().open(fname);
            int fileSize = is.available();
            byte [] buf = new byte[fileSize];
            int toRead = fileSize;
            while (toRead > 0) {
                int r = is.read(buf, fileSize - toRead, toRead);
                toRead -= r;
            }
            int blockStart = 0;
            for (int pos = 4; pos <= fileSize - 4; pos++) {
                if (buf[pos] == 0 && buf[pos + 1] == 0 &&
                        buf[pos + 2] == 0 && buf[pos + 3] == 1) {
                    mH264Frames.add(Arrays.copyOfRange(buf, blockStart, pos));
                    Log.d("BUFSIZE2", "buf size:" + (pos - blockStart));
                    blockStart = pos;
                }
            }
            mH264Frames.add(Arrays.copyOfRange(buf, blockStart, fileSize));
        } catch (IOException ex) {
            Log.d("LOADH264", "EXCEPTION: " + ex.getMessage());
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException ex) {}
        }
    }

    private MediaCodec createAndConfigMediaCodec()  throws  IOException{
        MediaCodec mediaCodec = MediaCodec.createDecoderByType("video/avc");
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", VIDEO_WIDTH, VIDEO_HEIGHT);
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, VIDEO_HEIGHT * VIDEO_WIDTH);
        mediaFormat.setString(MediaFormat.KEY_MIME, "video/avc");
        mediaCodec.configure(mediaFormat, null, null, 0);
        mediaCodec.start();
        return mediaCodec;
    }

    private ArrayList<Caption> processCaptionsString(String captionsStr) {
        char sepChar = captionsStr.charAt(0);
        String regexpStr = "[" + Character.toString(sepChar) + "]";
        String [] chunks = captionsStr.split(regexpStr);
        int numCaptions = chunks.length / 3;
        ArrayList<Caption> res = new ArrayList<>();
        for (int i = 0; i < numCaptions; i++) {
            int idx = i * 3;
            String caption = chunks[idx + 1];
            Log.d("CHUNKS", chunks[idx + 2] + ", " + chunks[idx + 3]);
            float x = Float.parseFloat(chunks[idx + 2]);
            float y = Float.parseFloat(chunks[idx + 3]);
            res.add(new Caption(caption, x, y));
        }
        return res;
    }

    private HashMap<Integer, Object []> createCaptionValsMap(Object [] vals, int numValsPerFrame) {
        HashMap<Integer, Object []> res = new HashMap<>();
        int numVals = vals.length;
        int numFrames = (numVals - 1) / (numValsPerFrame + 1);
        int numEntriesPerFrame = numValsPerFrame + 1;
        for (int i = 0; i < numFrames; i++) {
            int idx = i * numEntriesPerFrame + 1;
            Object [] entryVals = new Object[numValsPerFrame];
            for (int j = 0; j < numValsPerFrame; j++)
                entryVals[j] = vals[idx + j + 1];
            res.put((Integer)vals[idx], entryVals);
        }
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        Button button = (Button) findViewById(R.id.button1);
        final Button buttonPause = (Button) findViewById(R.id.button2);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mMyView = new MyView(this);
        relativeLayout.addView(mMyView);
        mBitmap = Bitmap.createBitmap(VIDEO_WIDTH, VIDEO_HEIGHT, Bitmap.Config.ARGB_8888);
        mH264Stream = new H264Stream();

        // Gettting args from the intent
        String fileName = getIntent().getStringExtra("FILE_NAME");
        String captionsStr = getIntent().getStringExtra("CAPTIONS");
        Object [] captionVals = (Object [])getIntent().getSerializableExtra("CAPTION_VALS");
        mCaptionValsMap = createCaptionValsMap(captionVals, (Integer)captionVals[0]);
        mCaptions = processCaptionsString(captionsStr);
        try {
            mH264Stream.openStream(fileName);
            Log.d("NUMFRAMES", "Number of frames: " + mH264Stream.getNumberOfFrames());
        } catch (IOException ex) {

        }
//        loadH264(fileName);
        Log.d("NUMFRAMES", "Number of frames: " + mH264Frames.size());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCounter++;
                try {
                    mFrameNumber = mH264Stream.setCurrentFrame(0);
                    mOutFrameNumber = 0;
                    mFlushCodec = true;
                    mPaused = false;
                } catch (IOException ex) {}
                mMyView.invalidate();
                mLastCaptions = null;
            }
        });
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaused = !mPaused;
                if (mPaused)
                    buttonPause.setText("Play");
                else
                    buttonPause.setText("Pause");
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    mCounter++;
                    mMyView.invalidate();
                }
            }
        };
        mFrameTimerRunning = true;
        mFrameTimerThread = new Thread() {
            private byte [] mRowBytesY = new byte[VIDEO_HEIGHT * VIDEO_WIDTH];
            private byte [] mRowBytesR = null;
            private byte [] mRowBytesB = null;

            private void processCodecInput() {
                int inputBufferIdx = mMediaCodec.dequeueInputBuffer(5000);
                if (inputBufferIdx >= 0) {
                    ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferIdx);
                    inputBuffer.clear();
                    //inputBuffer.put(mH264Frames.get(mFrameNumber));
                    try {
                        inputBuffer.put(mH264Stream.getNextBuffer());
                    }catch (IOException ex) {};
                    int flags;
                    if (mH264Stream.getCurrentBlock() <= 2)
                        flags = MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
                    else
                        flags = 0;
                    mMediaCodec.queueInputBuffer(inputBufferIdx, 0, inputBuffer.limit(),
                                                        0, flags);
                    mFrameNumber = mH264Stream.getCurrentFrame();
                    if (mH264Stream.getCurrentBlock() >= mH264Stream.getNumberOfBlocks()) {
                        mFrameNumber = 0;
                        mOutFrameNumber = 0;
                        try {
                            mFrameNumber = mH264Stream.setCurrentFrame(mFrameNumber);
                        }catch (IOException ex) {};
                        mPaused = true;
                        //mMediaCodec.flush();
                    }
                }
            }

            private void processCodecOutput() {
                if (mPaused)
                    return;
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIdx = mMediaCodec.dequeueOutputBuffer(bufferInfo, 5000);
                if (outputBufferIdx == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {};
                }
                else if (outputBufferIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat format = mMediaCodec.getOutputFormat();
                    Log.w("FMTCHG", "video format changed: " + format.toString());
                }
                else if (outputBufferIdx >= 0) {
                    Image image = mMediaCodec.getOutputImage(outputBufferIdx);

                    int yRowStride = image.getPlanes()[0].getRowStride();
                    int uvRowStride = image.getPlanes()[1].getRowStride();
                    int pixelStride = image.getPlanes()[1].getPixelStride();

                    if (mRowBytesB == null) {
                        int uvSize = image.getPlanes()[1].getBuffer().limit();
                        mRowBytesB = new byte[uvSize];
                        mRowBytesR = new byte[uvSize];
                    }
                    image.getPlanes()[0].getBuffer().get(mRowBytesY);
                    image.getPlanes()[1].getBuffer().get(mRowBytesR);
                    image.getPlanes()[2].getBuffer().get(mRowBytesB);

                    yuvToRgb(mRowBytesY, mRowBytesR, mRowBytesB, mBitmap,
                            VIDEO_WIDTH, VIDEO_HEIGHT, yRowStride, uvRowStride,  pixelStride);

                    mMediaCodec.releaseOutputBuffer(outputBufferIdx, false);
                    mOutFrameNumber++;
                }
            }

            @Override
            public void  run() {
                try {
                    mMediaCodec = createAndConfigMediaCodec();
                } catch (IOException ex ) {
                    Log.d("ONCREATE", "");
                    return;
                }
                while (mFrameTimerRunning) {
                    if (!mFlushCodec) {
                        processCodecInput();
                        processCodecOutput();
                    } else {
                        mMediaCodec.flush();
                        mFlushCodec = false;
                    }
                    Message msg = mHandler.obtainMessage(1);
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(66);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        mFrameTimerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFrameTimerRunning = false;
        for (;;) {
            try {
                mFrameTimerThread.join();
                break;
            } catch (InterruptedException ex) {
                continue;
            }
        }
        mH264Stream.closeStream();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


    class MyView extends View {
        //private Bitmap mBitmap;
        private int [] mBuffer;
        private int mCounter2 = 0;

        public MyView(Context context) {
            super(context);
            //mBuffer = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            matrix.postTranslate(mBitmap.getHeight(), 0);
            matrix.postScale(1.5f, 1.5f);
            float canvasWidth = canvas.getWidth();
            float canvasHeight = canvas.getHeight();
            float bmpX = (canvasWidth + mBitmap.getHeight()) / 2.0f;
            float bmpY = (canvasHeight + mBitmap.getWidth()) / 2.0f;
            matrix.postTranslate(100, 100);
            canvas.drawBitmap(mBitmap, matrix, null);
            Paint grayPaint = new Paint();
            grayPaint.setColor(Color.GRAY);
            grayPaint.setStrokeWidth(3.0f);
            Paint redPaint = new Paint();
            redPaint.setColor(Color.RED);
            redPaint.setStrokeWidth(3.0f);
            float lineLen = canvasWidth - 20;
            canvas.drawLine(10,  30, 10 +lineLen, 30, grayPaint);
            canvas.drawLine(10,  30,10 + lineLen * ((mOutFrameNumber * 1.0f) / mH264Stream.getNumberOfFrames()), 30, redPaint);
            Paint captionsPaint = new Paint();
            captionsPaint.setColor(Color.WHITE);
            captionsPaint.setTextSize(canvasHeight / 25.0f);
            if (mCaptionValsMap.containsKey((Integer)mOutFrameNumber))
                mLastCaptions = mCaptionValsMap.get((Integer)mOutFrameNumber);
            for (Caption caption : mCaptions) {
                String captionStr = caption.caption;
                float x = (((caption.x > 0) ? caption.x : (1.0f + caption.x))) * canvasWidth;
                float y = (((caption.y > 0) ? caption.y : (1.0f + caption.y))) * canvasHeight;
                if (captionStr.charAt(0) == '@') {
                    if (mLastCaptions != null) {
                        int capIdx = Integer.parseInt(captionStr.substring(1));
                        captionStr = mLastCaptions[capIdx].toString();
                    }
                    else
                        continue;
                }
                canvas.drawText(captionStr, x, y, captionsPaint);
            }
        }

    }

    public native void yuvToRgb(byte[] bytesY, byte[] bytesB, byte[] bytesR,
                                Bitmap bmp, int width, int height,
                                int yRowStride, int uvRowStride, int pixelStride);
}






//        mediaFormat.setInteger(MediaFormat.KEY_WIDTH, VIDEO_WIDTH);
//        mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, VIDEO_HEIGHT);
//        mediaFormat.setInteger("max-width", VIDEO_WIDTH);
//        mediaFormat.setInteger("max-height", VIDEO_HEIGHT);
//        byte [] arr = Arrays.copyOf(mH264Frames.get(0), mH264Frames.get(0).length + mH264Frames.get(1).length);
//        System.arraycopy(mH264Frames.get(1), 0, arr, mH264Frames.get(0).length, mH264Frames.get(1).length);
//        mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(arr));
//mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(mH264Frames.get(1)));

//        try {
//            mMediaCodec = createAndConfigMediaCodec();
//        } catch (IOException ex ) {
//            Log.d("ONCREATE", "");
//            return;
//        }
