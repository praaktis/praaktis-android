package com.praaktis.exerciseengine.Engine;

import java.util.ArrayList;
import java.util.HashMap;

class Globals {
    /**
     * Type of the exercise for the current session
     */
    static int EXERCISE_ID;
    static ExerciseEngineActivity mainActivity;

    /**
     * Error message indicator
     */
    static final int MSG_ERROR = 0;
    /**
     * Message with result indicator
     */
    static final int MSG_RESULT = 1;

    /**
     * Video frame rate for the codec
     */
    static final int VIDEO_FRAME_PER_SECOND = 15;

    /**
     * Engine state can take only values @Exercise
     */
    static volatile EngineState state;

    /**
     * Indicator of the person standing inside bounding box
     */
    static volatile boolean inBoundingBox = false;

    /**
     * The path of the replay video
     */
    static String videoPath;

    /**
     * The path of the replay video in raw h264 format, it is used by RawPlayer
     */
    static String rawVideoPath;

    /**
     * Message returned from the pose-recognition server
     */
    static String message = null;

    /**
     * Indicator of error
     */
    static boolean isErr = false;

    /**
     * Login or email of the user
     */
    static String LOGIN;

    /**
     * Login or password of the user
     */
    static String PASSWORD;

    /**
     * Calibration time.
     * The value is modified when new {@link ExerciseAnalyser} instance is created.
     */
    static int CALIBRATION_TIME_IN_SEC = 6;

    /**
     * Exercise time, can take large values for repetitive exercises.
     * The value is modified when new {@link ExerciseAnalyser} instance is created.
     */
    static int EXERCISE_TIME_IN_SEC = 6;

    /**
     * Criteria for the exercise
     */
    static final HashMap<String, Object> EXERCISE_CRITERIA = new HashMap<>();

    /**
     * Position of the text for the criteria to be displayed during replay.
     * Used bu RawPlayer
     */
    static final HashMap<String, int[]>  CRITERIA_POSITION = new HashMap<>();

    /**
     * Values of every exercise criteria for each repetition
     */
    static final HashMap<String, Object> EXERCISE_SCORES   = new HashMap<>();

    /**
     * Queue captured and resized yuv frames for codec
     */
    static ArrayList<byte []> capturedFrames = new ArrayList<>();

    /**
     * Initialization of default values
     */
    static void init(){
        EXERCISE_CRITERIA.clear();
        CRITERIA_POSITION.clear();
        EXERCISE_SCORES  .clear();

        switch (EXERCISE_ID){
            case ExerciseAnalyser.SQUATS_ID:{
                Globals.CALIBRATION_TIME_IN_SEC = 6;
                Globals.EXERCISE_TIME_IN_SEC = 60 * 2;
                CRITERIA_POSITION.put("back/shin diff", new int[]{50, 50});
                CRITERIA_POSITION.put("knee/hip angle", new int[]{50, 130});
                break;
            }
            case ExerciseAnalyser.CURLS_ID:{
                Globals.CALIBRATION_TIME_IN_SEC = 6;
                Globals.EXERCISE_TIME_IN_SEC = 60 * 2;
                CRITERIA_POSITION.put("α knee", new int[]{50, 50});
                CRITERIA_POSITION.put("α elbow", new int[]{50, 130});
                CRITERIA_POSITION.put("α back mn-mx", new int[]{0, 50});
                CRITERIA_POSITION.put("hip sway(%)", new int[]{0, 130});
                break;
            }
            default:{
                Globals.CALIBRATION_TIME_IN_SEC = 6;
                Globals.EXERCISE_TIME_IN_SEC = 6;
            }
        }
    }

}
