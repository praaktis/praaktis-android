package com.praaktis.exerciseengine;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

class CurlExerciseAnalyzer extends ExerciseAnalyser {

    /**
     *
     * 1. al->the minimum angle at the elbow i.e. when the upward movement of the curl stops and reverses
     * 2. bet->the smallest angle at the knee at any point in the exercise
     * 3. [gamma,delta]->the maximum angles from the vertical of the head compared with the feet (or if that is not practical the maximu angle of the back from the vertical (both leaning forward and back - so two measurements
     * 4. the maximum movement of the hips 9they tend to sway in some of the videos
     *
     */

    private final int UP = 1;
    private final int DOWN = 0;
    private int state = UP;

    private int upThresh = 160;
    private int downThresh = 120;

    private int ALPHA = 3000;
    private int BETA = 120;
    private int GAMMA = Integer.MAX_VALUE;
    private int DELTA = Integer.MIN_VALUE;
    private float EPSILON = Float.MAX_VALUE;
    private float ZETA = Float.MIN_VALUE;

    int count = 0;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void analyze(float[] points){
        int angleAtKnee = getAngleAtKnee(points, RIGHT_ARM);
        int angleAtElbow = getAngleAtElbow(points, RIGHT_ARM);
        int angleAtBack = -getAngleAtBack(points, RIGHT_ARM);

        float x0 = (points[3 * JointsMap.RANKLE] + points[3 * JointsMap.LANKLE]) / 2;
        float y0 = (points[3 * JointsMap.RANKLE + 1] + points[3 * JointsMap.LANKLE + 1]) / 2;

        float xp = points[3 * JointsMap.PELV];
        float yp = points[3 * JointsMap.PELV + 1];

        float xn = points[3 * JointsMap.NECK];
        float yn = points[3 * JointsMap.NECK + 1];

        float S = (x0 * yn + xn * yp + xp * y0 - y0 * xn - yn * xp - yp * x0) / 2;
        float epszet = (float) (S / (2 * Math.sqrt((x0 - xn) * (x0 - xn) + (y0 - yn) * (y0 - yn))));

        Log.d("UUUUP", angleAtKnee + "\t\t" + angleAtElbow);

        if(ALPHA > angleAtKnee && angleAtKnee > 0)
            ALPHA = angleAtKnee;

        if(GAMMA > angleAtBack)
            GAMMA = angleAtBack;
        Log.d("GAMMA", "ANGLE at back" + angleAtBack);

        if(DELTA < angleAtBack)
            DELTA = angleAtBack;

        if(EPSILON > epszet)
            EPSILON = epszet;

        if(ZETA < epszet)
            ZETA = epszet;

        if(state == UP){
            if(angleAtElbow < downThresh){
                state = DOWN;
            }
        } else {
            if(angleAtElbow > upThresh) {
                state = UP;
                Log.d("UUUUP----------->",  + ALPHA + "\t\t" + BETA);
//                if(BETA > 90) BETA -= 180;
                float x = points[3 * JointsMap.RANKLE] - points[3 * JointsMap.RKNEE];
                float y = points[3 * JointsMap.RANKLE + 1] - points[3 * JointsMap.RKNEE + 1];

                float hip_rat = (float) ((ZETA - EPSILON) / Math.sqrt(x * x + y * y));
                hip_rat = (int)(5000 * hip_rat);
                hip_rat /= 50;

                if(GAMMA < 0) GAMMA = -GAMMA * 2 + 1;
                else GAMMA <<= 1;

                if(DELTA < 0) DELTA = -DELTA * 2 + 1;
                else DELTA <<= 1;


                synchronized (Globals.EXERCISE_CRITERIA) {
                    Globals.EXERCISE_CRITERIA.put("α knee", ALPHA);
                    Globals.EXERCISE_CRITERIA.put("α elbow", BETA);
                    Globals.EXERCISE_CRITERIA.put("α back mn-mx", GAMMA + 10000 * DELTA);
                    Globals.EXERCISE_CRITERIA.put("hip sway(%)", hip_rat);
                    count ++;
                    Globals.EXERCISE_CRITERIA.put("COUNT", count);
                }

                ALPHA = 3000;
                BETA = 120;
                GAMMA = Integer.MAX_VALUE;
                DELTA = Integer.MIN_VALUE;
                EPSILON = Float.MAX_VALUE;
                ZETA = Float.MIN_VALUE;
            }
        }

        if(state == DOWN){
            if(BETA > angleAtElbow)
                BETA = angleAtElbow;
        }
    }

    @Override
    public void loadScores() {

    }
}
