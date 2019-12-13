package com.praaktis.exerciseengine;

import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

import static com.praaktis.exerciseengine.NetworkIOConstants.MSG_FRAME_POINTS;
import static com.praaktis.exerciseengine.NetworkIOConstants.MSG_OK;

class Receiver extends Thread {
    private volatile boolean mRunning = false;

    private final int[] mImportantPoints = {
            JointsMap.RHIP, JointsMap.LHIP,
            JointsMap.RSHOULDER, JointsMap.LSHOULDER,
            JointsMap.RKNEE, JointsMap.LKNEE
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

    private void debugUpdateArmsAngle(ExerciseAnalyser exerciseAnalyser, float[] points) {
        synchronized (Globals.globalLock) {
            Globals.leftShoulderAngle = exerciseAnalyser.getArmAngle(points, ExerciseAnalyser.LEFT_ARM);
            Globals.rightShoulderAngle = exerciseAnalyser.getArmAngle(points, ExerciseAnalyser.RIGHT_ARM);
            Globals.leftElbowAngle = exerciseAnalyser.getElbowAngle(points, ExerciseAnalyser.LEFT_ARM);
            Globals.rightElbowAngle = exerciseAnalyser.getElbowAngle(points, ExerciseAnalyser.RIGHT_ARM);
        }

    }


    private void processStateMachine(ExerciseAnalyser exerciseAnalyser,
                                     ArrayList<float[]> scene,
                                     int person) {
        switch (Globals.state) {

            case CALIBRATION: {
                if (!mPoints.isEmpty())
                    mPoints.clear();
                break;
            }

            case CALIBRATION_FAILED:
                Globals.inBoundingBox = false;
                green = false;
                mRunning = false;
                break;


            case EXERCISE_COMPLETED: {
                float[] scores = exerciseAnalyser.analyzeExcercise(mPoints);
                if (scores == null)
                    Globals.state = EngineState.EXERCISE_FAILED;
                else {
                    synchronized (Globals.globalLock) {
                        Globals.score1 = scores[0];
                        Globals.score2 = scores[1];
                        Globals.score3 = scores[2];
                    }
                    Globals.state = EngineState.SCORING;
                }
                green = false;
                Globals.inBoundingBox = false;
                mRunning = false;
                break;
            }

            case EXERCISE: {
                if (person < 0) {
                    Globals.state = EngineState.EXERCISE_FAILED;
                    Globals.inBoundingBox = false;
                    green = false;
                    mRunning = false;
                    break;
                }
                mPoints.add(scene.get(person));
                break;
            }
        }
    }

    @Override
    public void run() {
        ExerciseAnalyser exerciseAnalyser = new ExerciseAnalyser();
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

                switch (rpResult.packetType) {

                    case MSG_OK:
                        break;

                    case MSG_FRAME_POINTS: {

                        byte[] data = rpResult.packetData;
                        int frameNum = Bytes.getIntAt(data, 0);
                        int numPersons = Bytes.getIntAt(data, 4);
                        int numPointsPerPerson = data[8];
                        int n = numPersons * numPointsPerPerson;
                        float scaleX = mWidth / 360.0f;
                        float scaleY = mHeight / 640.0f;
                        ArrayList<float[]> scene = new ArrayList<>();
                        Log.d("NUMBER OF PEOPLE", String.valueOf(numPersons) + " " + frameNum);
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

                        processStateMachine(exerciseAnalyser, scene, personIdx);
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
