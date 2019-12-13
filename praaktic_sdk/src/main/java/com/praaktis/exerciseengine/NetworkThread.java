package com.praaktis.exerciseengine;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class NetworkThread extends Thread {

    private Socket mSocket;
    private Handler mMessageHandler;
    private VideoEncoder mVideoEncoder;
    private Receiver mReceiver;
    private volatile boolean mRunning;

    private int mBoundingBoxX;
    private int mBoundingBoxY;
    private int mBoundingBoxW;
    private int mBoundingBoxH;

    private String mHost;
    private int mPort;
    private int mHeight;
    private int mWidth;

    NetworkThread(Handler msgHandler, String host, int port,
                  int w, int h,
                  Rect boundingBox) {
        mMessageHandler = msgHandler;
        mHost = host;
        mPort = port;
        mWidth = w;
        mHeight = h;
        mBoundingBoxX = boundingBox.left;
        mBoundingBoxY = boundingBox.top;
        mBoundingBoxW = boundingBox.width();
        mBoundingBoxH = boundingBox.height();
    }

    private void connect() throws IOException {
        mSocket = new Socket(mHost,mPort);
    }

    @Override
    public void run() {
        try {
            connect();
            OutputStream outputStream = mSocket.getOutputStream();
            InputStream inputStream = mSocket.getInputStream();
            mVideoEncoder = new VideoEncoder(outputStream, mWidth, mHeight);
            mReceiver = new Receiver(inputStream);

            mReceiver.setCanvasSize(mWidth, mHeight,
                                    mBoundingBoxX, mBoundingBoxY,
                                    mBoundingBoxW, mBoundingBoxH);
            mRunning = true;
            mReceiver.setRunning(true);
            mReceiver.start();

            int frameNumber = 1;

            Globals.state = EngineState.CALIBRATION;

            for (;;) {
                if (!mRunning) {
                    mVideoEncoder.signalEndOfStream();
                    mVideoEncoder.encodeAndSend(frameNumber++);
                    break;
                }
                if (mVideoEncoder.encodeAndSend(frameNumber++))
                    break;
                if (Globals.state == EngineState.CALIBRATION_FAILED ||
                        Globals.state == EngineState.EXERCISE_FAILED ||
                        Globals.state == EngineState.EXERCISE_COMPLETED) {
                    mVideoEncoder.stopRendered();
                    mRunning = false;
                    continue;
                }
            }
            mReceiver.setRunning(false);
        } catch (IOException exc) {
            Message msg = mMessageHandler.obtainMessage(Globals.MSG_ERROR);
            msg.obj = (Object)"Connection to the server has failed. Please try again later.";
            mMessageHandler.sendMessage(msg);
        } finally {
            if (mReceiver != null)
                try {
                    mReceiver.join();
                } catch (InterruptedException ex) {}
            if (mSocket != null)
                try {
                    mSocket.close();
                } catch (IOException exc) {}
        }
    }

   public void setRunning(boolean running) {
        mRunning = running;
   }
}
