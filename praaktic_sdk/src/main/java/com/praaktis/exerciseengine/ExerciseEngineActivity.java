package com.praaktis.exerciseengine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;

import static android.content.ContentValues.TAG;
import static com.praaktis.exerciseengine.Exercise.SQUATS;
import static java.lang.System.currentTimeMillis;


public class ExerciseEngineActivity extends Activity implements SurfaceHolder.Callback {

    // Fields
    //

    private int mHeight;
    private int mWidth;
    private int mBoundingBoxX = 0;
    private int mBoundingBoxY = 0;
    private int mBoundingBoxH = 0;
    private int mBoundingBoxW = 0;
    private int mCounterSize = 10;

    private TextureView mTextureView;
    private CameraDevice mCameraDevice;
    private SurfaceHolder mSurfaceHolder;
    private final CameraDevice.StateCallback mCameraStateCallback = createCameraCallback();
    private final TextureView.SurfaceTextureListener mTextureListener = createTextureListener();
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private String mCameraId;
    private Size mImageDimension;
    private ImageReader mImageReader;

    private Handler mMsgHandler;
    private NetworkThread mNetworkThread;
    private RendererThread mRendererThread;

    private static final String sHost = "gauss.site.uz";
//    private static final String sHost = "10.10.1.24";
    private static final int sPort = 9080;

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    static {
        System.loadLibrary("native-lib");
    }

    // Constructor
    //
    public ExerciseEngineActivity() {
        Globals.mainActivity = this;
    }

    // Getters and setters
    //
    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    public TextureView getmTextureView() {
        return mTextureView;
    }

    // Other methods
    //
    void notifyConnectionFailed() {
        Globals.state = EngineState.CONNECTION_FAILED;
    }

    // Listeners and callbacks
    //
    private final TextureView.SurfaceTextureListener createTextureListener() {
        return new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                                  int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                    int width, int height) {}

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {}
        };
    }

    private CameraDevice.StateCallback createCameraCallback() {
        return new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                //This is called when the camera is open
                Log.e(TAG, "onOpened");
                mCameraDevice = cameraDevice;
                createCameraPreview();

                mNetworkThread.start();
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                mCameraDevice.close();
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        };
    }

    // Activity lifecycle methods
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        mTextureView = findViewById(R.id.texture);
        mTextureView.setSurfaceTextureListener(mTextureListener);
        SurfaceView surfaceView = findViewById(R.id.surface_view);

        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        surfaceView.setZOrderOnTop(true);

        mSurfaceHolder.addCallback(this);
        final Activity activity = this;

        Globals.LOGIN = getIntent().getStringExtra("LOGIN");
        Globals.PASSWORD = getIntent().getStringExtra("PASSWORD");
        Globals.EXERCISE = Exercise.values()[getIntent().getIntExtra("EXERCISE", SQUATS.ordinal())];

        File file = new File(getCacheDir().getPath() + "/test.mp4");
        if (file.isFile()) {
            file.delete();
        }

        mMsgHandler = new Handler() {
            private boolean mMsgShown = false;

            @Override
            public void handleMessage(Message msg) {
                String s = "";
                Intent intent = new Intent();

                if (msg.what == Globals.MSG_ERROR) {
                    mMsgShown = true;
                    s = (String) msg.obj;

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

                    alertDialogBuilder.setMessage(s)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Intent intent = new Intent();
                                    activity.setResult(Activity.RESULT_CANCELED, intent);
                                    activity.finish();
                                }
                            });
                    alertDialogBuilder.show();

                } else if (msg.what == Globals.MSG_RESULT) {
                    final Serializable scores = (Serializable) msg.obj;
                    intent.putExtra("result", scores);
                    intent.putExtra("VIDEO_PATH", Globals.videoPath);
                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                } else if (msg.what == Globals.MSG_COPY_BITMAP) {
                    Globals.textureBitmap = mTextureView.getBitmap();
                }
            }
        };
        Globals.init();
    }


    public Handler getMsgHandler() {
        return mMsgHandler;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            openCamera();

        } else {
            mTextureView.setSurfaceTextureListener(mTextureListener);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {

            if (mNetworkThread != null)
                mNetworkThread.setRunning(false);

            if (mRendererThread != null)
                mRendererThread.setRunning(false);

            if (mNetworkThread != null)
                mNetworkThread.join();

            if (mRendererThread != null)
                mRendererThread.join();

        } catch (InterruptedException ex) {
        }
        super.onDestroy();
    }

    // Camera handling
    //
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(mImageDimension.getWidth(), mImageDimension.getHeight());
            Surface surface = new Surface(texture);

            mImageReader = ImageReader.newInstance(mImageDimension.getWidth(), mImageDimension.getHeight(), ImageFormat.YUV_420_888, 2);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

            final CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            captureRequestBuilder.addTarget(mImageReader.getSurface());

            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == mCameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    try {
//                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //          Toast.makeText(RenderActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ExerciseEngineActivity.this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_CAMERA_PERMISSION);
                // ??? why return
                return;
            }
            // TODO: actually select the rear camera
            //
            mCameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);

//
//            int[] available = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
//
//            for(int mFrameNum : available){
//                if(mFrameNum == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR){
//                    Log.d("MANUALSENSOR", "MANUALSENSOR");
//                }
//            }
//            Log.d("MANUALSENSOR", "MANUALSENSORFINISHED");

            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // TODO: it might be better to select some lower resolution
            for (Size sz : map.getOutputSizes(SurfaceTexture.class)) {
                //(sz.isAFullCycle() >= 1080)
                if (sz.getHeight() <= 1080 && sz.getWidth() * 9 == sz.getHeight() * 16) {
                    mImageDimension = sz;
                    break;
                }
            }

            Log.d("DIMENSIONS", mImageDimension.getWidth() + "X" + mImageDimension.getHeight());

            manager.openCamera(mCameraId, mCameraStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(ExerciseEngineActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void closeCamera() {
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    // SurfaceHolder.Callback methods
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        int width;
        int height;
        width = canvas.getWidth();
        height = canvas.getHeight();

        mHeight = height;
        mWidth = width;

        Log.d("Size: ", height + "x" + width);

        //sizePassed = true;
        mBoundingBoxW = (int) (width * (520.0 / 720));
        mBoundingBoxH = rescale(1232 - 300);
        mBoundingBoxX = (width - mBoundingBoxW) / 2;
        mBoundingBoxY = (height - mBoundingBoxH) / 2;
        mCounterSize = rescale(60);


        Rect boundingBox = new Rect(mBoundingBoxX, mBoundingBoxY,
                mBoundingBoxW + mBoundingBoxX, mBoundingBoxH + mBoundingBoxY);

        if (mNetworkThread == null)
            mNetworkThread = new NetworkThread(mMsgHandler, sHost, sPort,
                    mWidth, mHeight,
                    boundingBox);
        mNetworkThread.setRunning(true);

        if (mRendererThread == null)
            mRendererThread = new RendererThread(mMsgHandler, holder, width, height, boundingBox);
        mRendererThread.setRunning(true);
        mRendererThread.start();

        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //mPersonDrawer.setRunning(true);
        //if (mPersonDrawer.isInterrupted()) mPersonDrawer.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mRendererThread.setRunning(false);
        try {
            mRendererThread.join();
        } catch (InterruptedException ex) {
        }
        mRendererThread = null;
        mNetworkThread.setRunning(false);
        try {
            mNetworkThread.join();
        } catch (InterruptedException ex) {
        }
        mRendererThread = null;
    }

    private int rescale(int param) {
        double p = (double) param;
        // 1280px is the height of our test/developmnent phone's screen (Samsung J5).
        return (int) (mWidth * (param / 720.0));
    }

    // Create UI thread message loop  handler
    //
    private void createUIHandler() {

    }

    private static void sendByteArrayTcp(byte [] arr, String host, int port) {
        Socket skt = null;
        try {
            skt = new Socket(host, port);
            OutputStream os = skt.getOutputStream();
            int sent = 0;
            //do {
               os.write(arr, sent, arr.length - sent);
            //} while (sent < arr.length);
        } catch (IOException ex) {
            Log.e("SENDBYTEARRAYTCP", "Cannot connect.");
        } finally {
            if (skt != null)
                try {skt.close();} catch (IOException ex2) {};
        }
    }

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        private final int SCALE_FACTOR = 3;
        byte[] mRowBytesY = new byte[1920 * 1080];
        byte[] mRowBytesB = null;
        byte[] mRowBytesR = null;

        boolean ok = true;


        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image;
            try {

                image = reader.acquireLatestImage();

                ok = !ok;

                if(ok){
                    image.close();
                    return;
                }

                if (image != null) {
                    int n = image.getHeight() * image.getWidth();
                    if (image.getPlanes() == null) {
                        image.close();
                        return;
                    }

                    byte [] resizedYUV = new byte[(n / (SCALE_FACTOR * SCALE_FACTOR)) * 3 / 2];
                    int uvSize = image.getPlanes()[1].getBuffer().limit();

                    if (mRowBytesB == null) {
                        mRowBytesB = new byte[uvSize];
                        mRowBytesR = new byte[uvSize];
                    }

                    image.getPlanes()[0].getBuffer().get(mRowBytesY);
                    image.getPlanes()[1].getBuffer().get(mRowBytesB);
                    image.getPlanes()[2].getBuffer().get(mRowBytesR);

                    int yRowStride = image.getPlanes()[0].getRowStride();
                    int uvRowStride = image.getPlanes()[1].getRowStride();
                    int pixelStride = image.getPlanes()[1].getPixelStride();

                    long start = currentTimeMillis();
                    resizeYUV3(resizedYUV, mRowBytesY, mRowBytesR, mRowBytesB, yRowStride, uvRowStride, pixelStride);
                    Log.d("TIMETORESIZE", currentTimeMillis() - start + " ");

                    synchronized (Globals.capturedFrames) {
                        if (Globals.capturedFrames.size() < 10) {
                            Globals.capturedFrames.add(resizedYUV);
                        }
                    }

                }
                if (image != null)
                    image.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public native void resizeYUV3(byte [] outBuf,byte[] bytesY, byte[] bytesB, byte[] bytesR,
                                   int yRowStride, int uvRowstride, int pixelStride);

    public native void yuvToRGBGrayscale(byte[] buf, int[] pixels, int n);

    public native void yuvToRGB(byte[] bytesY, byte[] bytesB, byte[] bytesR, Bitmap bmp, int width, int height,
                                int yRowStride, int uvRowstride, int pixelStride);

//    public native void yuvPlanarToRGB(byte[] bytesY, byte[] bytesB, byte[] bytesR, Bitmap bmp, int width, int height, int yRowStride, int uvRowstride);

}