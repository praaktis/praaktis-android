package com.praaktis.exerciseengine;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

class Globals {
    public static Exercise EXERCISE;
    public static ExerciseEngineActivity mainActivity;

    public static final int MSG_ERROR = 0;
    public static final int MSG_RESULT = 1;
    public static final int MSG_COPY_BITMAP = 2;
    public static final int VIDEO_FRAME_PER_SECOND = 15;

    public static final Object globalLock = new Object();

    public static ArrayList<Float> scores = new ArrayList<>();

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

    public static final HashMap<String, Object> EXERCISE_CRITERIA = new HashMap<>();
    public static final HashMap<String, int[]>  CRITERIA_POSITION = new HashMap<>();
    public static final HashMap<String, Object> EXERCISE_SCORES   = new HashMap<>();

    public static ArrayList<byte []> capturedFrames = new ArrayList<>();

    public static void init(){
        EXERCISE_CRITERIA.clear();
        CRITERIA_POSITION.clear();
        EXERCISE_SCORES  .clear();

        switch (EXERCISE){
            case SQUATS:{
                CRITERIA_POSITION.put("back/shin diff", new int[]{50, 50});
                CRITERIA_POSITION.put("knee/hip angle", new int[]{50, 130});
                break;
            }
            case CURL:{
                CRITERIA_POSITION.put("α knee", new int[]{50, 50});
                CRITERIA_POSITION.put("α elbow", new int[]{50, 130});
                CRITERIA_POSITION.put("α back mn-mx", new int[]{0, 50});
                CRITERIA_POSITION.put("hip sway(%)", new int[]{0, 130});
                break;
            }
            default:{

            }
        }
    }

}
