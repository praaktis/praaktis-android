package com.praaktis.exerciseengine.Engine;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.InputStream;
import java.util.ArrayList;

import static com.praaktis.exerciseengine.Engine.NetworkIOConstants.MSG_ERROR;
import static com.praaktis.exerciseengine.Engine.NetworkIOConstants.MSG_FRAME_POINTS;
import static com.praaktis.exerciseengine.Engine.NetworkIOConstants.MSG_OK;

/**
 *  Class for receiving results from pose-estimation server
 *  Responsibilities:
 *  1. Check if a pose inside bounding box
 *  2. Switch Engine state
 */
class Receiver extends Thread{
    private volatile boolean mRunning = false;

    private final int[] mImportantPoints = {
            JointsMap.RHIP,
            JointsMap.LHIP,
            JointsMap.RSHOULDER,
            JointsMap.LSHOULDER,
            JointsMap.RKNEE,
            JointsMap.LKNEE
    };

    public static volatile boolean sizePassed = false;
    public static volatile boolean green = false;

    private int mHeight;
    private int mWidth;
    private int mBoundingBoxX;
    private int mBoundingBoxY;
    private int mBoundingBoxW;
    private int mBoundingBoxH;

    private InputStream mInputStream;

    private ArrayList<float[]> mPoints;

    Receiver(InputStream inputStream) {
        mPoints = new ArrayList<>();
        mInputStream = inputStream;
    }


    /**
     * Setting necessary parameters for canvas and bounding box
     * @param width
     * @param height
     * @param boundingBoxX
     * @param boundingBoxY
     * @param boundingBoxW
     * @param boundingBoxH
     */
    public void setCanvasSize(int width, int height,
                              int boundingBoxX, int boundingBoxY,
                              int boundingBoxW, int boundingBoxH) {
        mHeight = height;
        mWidth = width;
        mBoundingBoxW = boundingBoxW;
        mBoundingBoxH = boundingBoxH;
        mBoundingBoxX = boundingBoxX;
        mBoundingBoxY = boundingBoxY;
        sizePassed = true;
    }

    public void setRunning(boolean running) {
        mRunning = running;
    }

    private boolean isInBoundingBox(float[] points) {
        for (int p : mImportantPoints) {
            float x = points[p * 3];
            float y = points[p * 3 + 1];

            if (x < mBoundingBoxX || x > (mBoundingBoxX + mBoundingBoxW)
                    || y < mBoundingBoxY || y > (mBoundingBoxY + mBoundingBoxH)) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processStateMachine(ExerciseAnalyser exerciseAnalyser,
                                     ArrayList<float[]> scene,
                                     int person,
                                     int frameNum) {

        switch (Globals.state) {

            case CALIBRATION: {
                if (!mPoints.isEmpty())
                    mPoints.clear();

                if(person == -127){
                    Globals.state = EngineState.CALIBRATION_FAILED;
                }

                break;
            }

            case CALIBRATION_FAILED: {
                Globals.inBoundingBox = false;
                green = false;
                mRunning = false;
                break;
            }

            case EXERCISE_COMPLETED: {
                float height = 1;

                if (height <= 0)
                    Globals.state = EngineState.EXERCISE_FAILED;
                else {
                    Globals.state = EngineState.SCORING;
                }
                green = false;
                Globals.inBoundingBox = false;
                mRunning = false;
                break;
            }

            case EXERCISE: {
                if (person < 0) {
                    exerciseAnalyser.loadScores();
                    Globals.state = EngineState.EXERCISE_FAILED;
                    Globals.inBoundingBox = false;
                    green = false;
                    mRunning = false;
                    break;
                }

                exerciseAnalyser.analyze(scene.get(person), frameNum);

                mPoints.add(scene.get(person));
                break;
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        ExerciseAnalyser exerciseAnalyser = ExerciseAnalyser.createAnalyzer(Globals.EXERCISE_ID);
        while (mRunning) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!sizePassed)
                continue;
            //try
            {
                NetworkIO.ReceivePacketResult rpResult = new NetworkIO.ReceivePacketResult();
                if (!NetworkIO.receivePacket(mInputStream, rpResult)) {
                    break;
                }

                Log.d("MESSAGETYPE", rpResult.packetType + "");

                switch (rpResult.packetType) {

                    case MSG_OK: {
                        break;
                    }

                    case MSG_ERROR: {
                        Globals.isErr = true;
                        Log.d("ERRORMSG", "ENTER");
                        StringBuilder buf = new StringBuilder();

                        for (byte c : rpResult.packetData)
                            buf.appendCodePoint(c);
                        Globals.message = buf.substring(0);
                        processStateMachine(exerciseAnalyser, null, -127, 0);

                        Log.d("ERRORMSG", Globals.message);

                        break;
                    }

                    case MSG_FRAME_POINTS: {

                        byte[] data = rpResult.packetData;
                        int frameNum = Bytes.getIntAt(data, 0);

                        int numPersons = Bytes.getIntAt(data, 4);
                        int numPointsPerPerson = data[8];
                        int n = numPersons * numPointsPerPerson;
                        float scaleX = mWidth / 360.0f;
                        float scaleY = mHeight / 640.0f;
                        ArrayList<float[]> scene = new ArrayList<>();

                        // Collect points into a ArrayList<float[]>
                        //
                        int pos = 9;

                        for (int i = 0; i < numPersons; i++) {
                            float[] pointsArr = new float[numPointsPerPerson * 3];
                            for (int j = 0; j < numPointsPerPerson; j++) {
                                int idx = j * 3;
                                pointsArr[idx] = Bytes.getFloatAt(data, pos) * scaleX;
                                pos += 4;
                                pointsArr[idx + 1] = Bytes.getFloatAt(data, pos) * scaleY;
                                pos += 4;
                                pointsArr[idx + 2] = Bytes.getFloatAt(data, pos);
                                pos += 4;
                            }
                            scene.add(pointsArr);
                        }

                        int personIdx = -1;
                        float prevShoulderDistance = 0;

                        // Find the nearest person to
                        // the camera. (A primitive algorithm, just finds
                        // the person with the widest shoulders)
                        //
                        for (int i = 0; i < numPersons; i++) {
                            float[] personPoints = scene.get(i);
                            if (!isInBoundingBox(personPoints))
                                continue;

                            float d = dist(personPoints[JointsMap.RSHOULDER * 3],
                                    personPoints[JointsMap.RSHOULDER * 3 + 1],
                                    personPoints[JointsMap.LSHOULDER * 3],
                                    personPoints[JointsMap.LSHOULDER * 3 + 1]);

                            if (d > prevShoulderDistance) {
                                personIdx = i;
                                prevShoulderDistance = d;
                            }
                        }

                        green = personIdx >= 0;
                        Globals.inBoundingBox = green;
                        //if (false && person >= 0)
                        //  debugUpdateArmsAngle(data, excerciseAnalyser, person, numPointsPerPerson);

                        processStateMachine(exerciseAnalyser, scene, personIdx, frameNum);

                        break;
                    }

                    default:
                        Log.w("HEADER", String.valueOf(rpResult.packetType));
                        break;
                }
            }
            /*catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    private float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
}
