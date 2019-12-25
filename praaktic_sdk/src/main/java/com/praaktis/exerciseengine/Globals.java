package com.praaktis.exerciseengine;

import android.graphics.Bitmap;

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

    public static float leftShoulderAngle =0.0f;
    public static float rightShoulderAngle =0.0f;
    public static float leftElbowAngle = 0.0f;
    public static float rightElbowAngle = 0.0f;

    public static volatile EngineState state;
    public static volatile boolean inBoundingBox = false;

    public static volatile Bitmap textureBitmap;

    public static String videoPath;

}
