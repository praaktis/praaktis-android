package com.praaktis.exerciseengine;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

class Globals {
    static Exercise EXERCISE;
    static ExerciseEngineActivity mainActivity;

    static final int MSG_ERROR = 0;
    static final int MSG_RESULT = 1;
    static final int MSG_COPY_BITMAP = 2;
    static final int VIDEO_FRAME_PER_SECOND = 15;

    static final Object globalLock = new Object();

    static ArrayList<Float> scores = new ArrayList<>();

    static float score1 = 0.0f;
    static float score2 = 0.0f;
    static float score3 = 0.0f;

    static float leftShoulderAngle = 0.0f;
    static float rightShoulderAngle = 0.0f;
    static float leftElbowAngle = 0.0f;
    static float rightElbowAngle = 0.0f;

    static volatile EngineState state;
    static volatile boolean inBoundingBox = false;

    static volatile Bitmap textureBitmap;

    static String videoPath;

    static String message = null;
    static boolean isErr = false;

    static String LOGIN;
    static String PASSWORD;

    static final HashMap<String, Object> EXERCISE_CRITERIA = new HashMap<>();
    static final HashMap<String, int[]>  CRITERIA_POSITION = new HashMap<>();
    static final HashMap<String, Object> EXERCISE_SCORES   = new HashMap<>();

    static ArrayList<byte []> capturedFrames = new ArrayList<>();

    static void init(){
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
