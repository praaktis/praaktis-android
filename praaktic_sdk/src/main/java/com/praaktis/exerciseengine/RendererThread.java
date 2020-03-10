package com.praaktis.exerciseengine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import static com.praaktis.exerciseengine.EngineState.CALIBRATION_FAILED;
import static com.praaktis.exerciseengine.EngineState.EXERCISE;
import static com.praaktis.exerciseengine.EngineState.EXERCISE_COMPLETED;
import static java.lang.System.currentTimeMillis;

class RendererThread extends Thread {

    private boolean mRunning = false;

    private Paint mGreenPaint;
    private Paint mRedPaint;
    private Paint mTrPaint;
    private Paint mDigitPaint;
    private Paint mAnglesPaint;
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
//        double p = (double) param;
        // 1280px is the height of our test/developmnent phone's screen (Samsung J5).
        return (int) (mHeight * (param / 1280.0));
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

        mAnglesPaint = new Paint(mDigitPaint);
        mAnglesPaint.setTextSize(40);
        mAnglesPaint.setTextAlign(Paint.Align.LEFT);

        mTrPaint = new Paint();
        mTrPaint.setColor(Color.TRANSPARENT);
        mTrPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mMessageHandler = msgHandler;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    private void drawBoundingBox(Canvas canvas, Paint paint) {
        Log.d("RENDERER", "BOUNDING BOX");
        canvas.drawLine(mBoundingBoxX, mBoundingBoxY, mBoundingBoxX + 50, mBoundingBoxY, paint);
        canvas.drawLine(mBoundingBoxX, mBoundingBoxY, mBoundingBoxX, mBoundingBoxY + 50, paint);

        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY,
                mBoundingBoxX + mBoundingBoxW - 50, mBoundingBoxY, paint);
        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY,
                mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + 50, paint);

        canvas.drawLine(mBoundingBoxX, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX, mBoundingBoxY + mBoundingBoxH - 50, paint);
        canvas.drawLine(mBoundingBoxX, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX + 50, mBoundingBoxY + mBoundingBoxH, paint);

        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + mBoundingBoxH - 50, paint);
        canvas.drawLine(mBoundingBoxX + mBoundingBoxW, mBoundingBoxY + mBoundingBoxH,
                mBoundingBoxX + mBoundingBoxW - 50, mBoundingBoxY + mBoundingBoxH, paint);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run() {
        mCounterEnd = System.currentTimeMillis() + 1000 * Globals.CALIBRATION_TIME_IN_SEC;
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
            int W = canvas.getWidth();
            int H = canvas.getHeight();

            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mTrPaint);

            if (mCounterSet) {
                long cur = System.currentTimeMillis();
                mCounter = (int) ((mCounterEnd - cur) / 1000);

                Globals.CRITERIA_POSITION.forEach((s, u) -> {
                    if (u[0] <= 0) u[0] = W / 2 - u[0] - 5;
                    Object val = Globals.EXERCISE_CRITERIA.getOrDefault(s, 0);
                    if (val instanceof Float || (int) val < 10000)
                        canvas.drawText(s + ": " + val, u[0], rescale(u[1]), mAnglesPaint);
                    else {
                        int fr = (int) val % 10000;
                        int sc = (int) val / 10000;

                        //Zig-zag logic
                        //positive numbers are doubled (n) n -> 2*n and negatives (n) n -> -2*n-1
                        if(fr % 2 == 0) fr >>= 1;
                        else fr = -(fr + 1) >> 1;
                        if(sc % 2 == 0) sc >>= 1;
                        else sc = -(sc + 1) >> 1;
                        canvas.drawText(s + ": " + fr + ":" + sc, u[0], rescale(u[1]), mAnglesPaint);
                    }
                });
                canvas.drawText(Globals.EXERCISE_CRITERIA.getOrDefault("COUNT", 0) + "", canvas.getWidth() - 100, canvas.getHeight() - 70, mRedPaint);
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
                    if (!Globals.isErr) {
                        Message msg = mMessageHandler.obtainMessage(Globals.MSG_ERROR);
                        msg.obj = "Calibration failed. \nPlease start again";
                        mMessageHandler.sendMessage(msg);
                    }
                    mRunning = false;
                    break;
                }
                case EXERCISE_FAILED: {
                    if (!Globals.isErr) {
//                        Message msg = mMessageHandler.obtainMessage(Globals.MSG_ERROR);
                        Message msg = mMessageHandler.obtainMessage(Globals.MSG_RESULT);
                        msg.obj = Globals.EXERCISE_SCORES.clone();
                        mMessageHandler.sendMessage(msg);
                    }
                    mRunning = false;
                    break;
                }
                case SCORING: {
                    if (!Globals.isErr) {
                        Message msg = mMessageHandler.obtainMessage(Globals.MSG_RESULT);
                        msg.obj = Globals.EXERCISE_SCORES.clone();
                        mMessageHandler.sendMessage(msg);
                    }
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
                canvas.drawText(text, canvas.getWidth() >> 1, canvas.getHeight() - rescale(70), mDigitPaint);

            if (mCounter <= 0) {
                switch (Globals.state) {
                    case CALIBRATION:
                        if (Globals.inBoundingBox) {
                            Globals.state = EXERCISE;
                            mCounterEnd = currentTimeMillis() + Globals.EXCERCISE_TIME_IN_SEC * 1000;
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
