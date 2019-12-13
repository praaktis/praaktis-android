package com.praaktis.exerciseengine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import static com.praaktis.exerciseengine.EngineState.CALIBRATION_FAILED;
import static com.praaktis.exerciseengine.EngineState.EXERCISE;
import static com.praaktis.exerciseengine.EngineState.EXERCISE_COMPLETED;
import static java.lang.System.currentTimeMillis;


class RendererThread extends Thread {

    private final int CALIBRATION_TIME_IN_SEC = 6;
    private final int EXCERCISE_TIME_IN_SEC = 6;
    private boolean mRunning = false;

    private Paint mGreenPaint;
    private Paint mRedPaint;
    private Paint mTrPaint;
    private Paint mDigitPaint;
    private Paint mDynamicPaint;

    private boolean mCounterSet = false;
    private int mCounter = 0;
    private long mCounterEnd = 0;

    private SurfaceHolder mSurfaceHolder;

    private int mHeight;
    private int mWidth;
    private int mBoundingBoxX;
    private int mBoundingBoxY;
    private int mBoundingBoxW;
    private int mBoundingBoxH;

    private Handler mMessageHandler;

    private int rescale(int param) {
        double p = (double)param;
        // 1280px is the height of our test/developmnent phone's screen (Samsung J5).
        return (int)(mHeight * (param / 1280.0));
    }

    RendererThread(Handler msgHandler, SurfaceHolder holder, int width, int height, Rect boundingBox) {
        mSurfaceHolder = holder;
        mHeight = height;
        mWidth = width;

        mBoundingBoxX = boundingBox.left;
        mBoundingBoxY = boundingBox.top;
        mBoundingBoxW = boundingBox.width();
        mBoundingBoxH = boundingBox.height();

        mGreenPaint = new Paint();
        mGreenPaint.setColor(Color.GREEN);
        mGreenPaint.setStrokeWidth(rescale(5));
        mGreenPaint.setTextAlign(Paint.Align.CENTER);

        mRedPaint = new Paint();
        mRedPaint.setColor(Color.RED);
        mRedPaint.setStrokeWidth(rescale(5));
        mRedPaint.setTextSize(rescale(60));
        mRedPaint.setTextAlign(Paint.Align.CENTER);

        mDynamicPaint = new Paint();

        mDigitPaint = new Paint();
        mDigitPaint.setColor(Color.GREEN);
        mDigitPaint.setStrokeWidth(rescale(5));
        mDigitPaint.setTextSize(rescale(60));
        mDigitPaint.setTextAlign(Paint.Align.CENTER);

        mTrPaint = new Paint();
        mTrPaint.setColor(Color.TRANSPARENT);
        mTrPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mMessageHandler = msgHandler;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    void drawBoundingBox(Canvas canvas, Paint paint) {
        canvas.drawLine(mBoundingBoxX, mBoundingBoxY, mBoundingBoxX + 50, mBoundingBoxY, paint);
        canvas.drawLine(mBoundingBoxX, mBoundingBoxY, mBoundingBoxX, mBoundingBoxY + 50, paint);

        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY,
                mBoundingBoxX + mBoundingBoxW - 50, mBoundingBoxY, paint);
        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY,
                mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + 50, paint);

        canvas.drawLine(mBoundingBoxX, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX, mBoundingBoxY + mBoundingBoxH - 50 , paint);
        canvas.drawLine(mBoundingBoxX, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX + 50, mBoundingBoxY + mBoundingBoxH, paint);

        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + mBoundingBoxH - 50 , paint);
        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX + mBoundingBoxW - 50, mBoundingBoxY + mBoundingBoxH, paint);

    }

    public void run() {
        mCounterEnd = System.currentTimeMillis() + 1000 * CALIBRATION_TIME_IN_SEC;
        mCounterSet = false;
        mCounter = 1;
        Globals.state = EngineState.CONNECTION_FAILED;
        while (mRunning) {
            try {
                sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Canvas canvas = mSurfaceHolder.lockCanvas(null);
            if (canvas == null)
                continue;

            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mTrPaint);

            if (mCounterSet) {
                long cur = System.currentTimeMillis();
                mCounter = (int) ((mCounterEnd - cur) / 1000);
                canvas.drawText(mCounter + "", canvas.getWidth() / 2, rescale(150), mDigitPaint);
            }

            drawBoundingBox(canvas, Globals.inBoundingBox ? mGreenPaint : mRedPaint);

            String text = null;
            switch (Globals.state) {
                case CALIBRATION:
                    mCounterSet = true;
                    text = "calibration";
                    break;
                case EXERCISE:
                    text = "exercise";
                    break;
                case CALIBRATION_FAILED: {
                    Message msg = mMessageHandler.obtainMessage(Globals.MSG_ERROR);
                    msg.obj = (Object) "Calibration failed. \nPlease start again";
                    mMessageHandler.sendMessage(msg);
                    mRunning = false;
                    break;
                }
                case EXERCISE_FAILED: {
                    Message msg = mMessageHandler.obtainMessage(Globals.MSG_ERROR);
                    msg.obj = (Object) "No person in the area.\n Exercise failed";
                    mMessageHandler.sendMessage(msg);
                    mRunning = false;
                    break;
                }
                case SCORING: {
                    Message msg = mMessageHandler.obtainMessage(Globals.MSG_RESULT);
                    float [] scores = {Globals.score1, Globals.score2, Globals.score3};
                    msg.obj = (Object) scores;
                    mMessageHandler.sendMessage(msg);
                    mRunning = false;
                    break;
                }
                default:
                    text = null;
                    break;
            }

            if (!mRunning) {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                break;
            }

            if (text != null)
                canvas.drawText(text, canvas.getWidth() / 2, canvas.getHeight() - rescale(70), mDigitPaint);

            if (mCounter <= 0) {
                switch (Globals.state) {
                    case CALIBRATION:
                        if (Globals.inBoundingBox) {
                            Globals.state = EXERCISE;
                            mCounterEnd = currentTimeMillis() + EXCERCISE_TIME_IN_SEC * 1000;
                            mCounterSet = true;
                        } else {
                            Globals.state = CALIBRATION_FAILED;
                            mCounterSet = false;
                        }
                        break;
                    case EXERCISE:
                        mCounterSet = false;
                        Globals.state = EXERCISE_COMPLETED;
                        break;
                    default:
                        mCounter = 0;
                        break;
                }
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
}
