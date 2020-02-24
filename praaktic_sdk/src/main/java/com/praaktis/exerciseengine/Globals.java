package com.praaktis.exerciseengine;

import android.graphics.Bitmap;

import java.util.ArrayList;

class Globals {
    public static ExerciseEngineActivity mainActivity;

    public static final int MSG_ERROR = 0;
    public static final int MSG_RESULT = 1;
    public static final int MSG_COPY_BITMAP = 2;
    public static final int VIDEO_FRAME_PER_SECOND = 15;

    public static final Object globalLock = new Object();

    public static float score1 = 0.0f;
    public static float score2 = 0.0f;
    public static float score3 = 0.0f;

    public static float leftShoulderAngle = 0.0f;
    public static float rightShoulderAngle = 0.0f;
    public static float leftElbowAngle = 0.0f;
    public static float rightElbowAngle = 0.0f;

    public static volatile EngineState state;
    public static volatile boolean inBoundingBox = false;

    public static volatile Bitmap textureBitmap;

    public static String videoPath;

    public static String message = null;
    public static boolean isErr = false;

    public static String LOGIN;
    public static String PASSWORD;

    public static int count = 0;

    public static int ANGLE_BACK_SHIN = 0;
    public static int ANGLE_HIP_KNEE = 0;

    public static ArrayList<byte []> capturedFrames = new ArrayList<>();

    public static void init(){
        count = 0;
        ANGLE_HIP_KNEE = 0;
        ANGLE_BACK_SHIN = 0;
    };

}
