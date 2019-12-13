package com.praaktis.exerciseengine;

import android.app.Activity;

public class ExerciseEngine {
    public static Activity createActivity(String hostName, int port) {
        return new ExerciseEngineActivity();
    }
}
