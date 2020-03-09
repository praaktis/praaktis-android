package com.praaktis.exerciseengine;

import android.graphics.Rect;
import android.media.MediaCodec;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

class NetworkThread extends Thread {

    private SSLSocket mSocket;
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
    private int FLAG = 0;

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
        InputStream fis = Globals.mainActivity.getAssets().open("cert.pem");
        BufferedInputStream bis = new BufferedInputStream(fis);
        Certificate certificate;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificate = cf.generateCertificate(bis);
        } catch (CertificateException cex) {
            return;
        } finally {
            bis.close();
        }
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("BKS");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("certAlias", certificate);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext sslctx = SSLContext.getInstance("TLS");
            sslctx.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory factory = sslctx.getSocketFactory();

            mSocket = (SSLSocket) factory.createSocket(mHost, mPort);
            mSocket.setTcpNoDelay(true);

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            connect();
            OutputStream outputStream = mSocket.getOutputStream();
            InputStream inputStream = mSocket.getInputStream();

            if(!authorize(outputStream, inputStream)){
                return;
            }

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


            for (; ; ) {

                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("INFO_", "");
                if (!mRunning) {
                    FLAG = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                    break;
                }

                mVideoEncoder.encode(FLAG);

                if (mVideoEncoder.getAndSend(frameNumber++))
                    break;
                if (Globals.state == EngineState.CALIBRATION_FAILED ||
                        Globals.state == EngineState.EXERCISE_FAILED ||
                        Globals.state == EngineState.EXERCISE_COMPLETED) {
                    mVideoEncoder.release();
                    mRunning = false;
                    continue;
                }
            }
            mReceiver.setRunning(false);
        } catch (IOException exc) {
            Message msg = mMessageHandler.obtainMessage(Globals.MSG_ERROR);
            msg.obj = (Object) "Connection to the server has failed. Please try again later.";
            mMessageHandler.sendMessage(msg);
        } finally {
            if (mReceiver != null)
                try {
                    mReceiver.join();
                } catch (InterruptedException ex) {
                }
            if (mSocket != null)
                try {
                    NetworkIO.sendPacket(mSocket.getOutputStream(), (byte)NetworkIOConstants.MSG_CLOSE_CONNECTION, new byte[0]);
                    mSocket.close();
                } catch (IOException exc) {
                }
            if (Globals.message != null) {
                Message msg = mMessageHandler.obtainMessage(Globals.MSG_ERROR);
                msg.obj = Globals.message;
                mMessageHandler.sendMessage(msg);
                Globals.message = null;
                Globals.isErr = false;
            }
        }
    }

    private boolean authorize(OutputStream out, InputStream in) {
        try {

            String mLogin = Globals.LOGIN;
            String mPassword = Globals.PASSWORD;
            byte[] data = new byte[mLogin.length() + mPassword.length() + 2];
            int pos = 0;

            for (int i = 0; i < mLogin.length(); i++)
                data[pos++] = (byte) mLogin.charAt(i);
            data[pos++] = 0;

            for (int i = 0; i < mPassword.length(); i++)
                data[pos++] = (byte) mPassword.charAt(i);
            data[pos++] = 0;

            NetworkIO.sendPacket(out, (byte) NetworkIOConstants.MSG_LOGIN_PASSWD, data);

            NetworkIO.ReceivePacketResult rpResult = new NetworkIO.ReceivePacketResult();

            NetworkIO.receivePacket(in, rpResult);

            switch (rpResult.packetType) {
                case NetworkIOConstants.MSG_OK:
                    return true;
                case NetworkIOConstants.MSG_ERROR : {
                    Globals.isErr = true;

                    StringBuffer sb = new StringBuffer();

                    for(byte b: rpResult.packetData)
                        sb.appendCodePoint(b);

                    Globals.message = sb.toString();
                    break;
                }
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }
}
