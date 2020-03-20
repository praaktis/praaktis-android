package com.praaktis.exerciseengine.Engine;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * Analyzer class for Bicep Curls
 * <p>
 * Criteria:
 * <p>
 * 1. al->the minimum angle at the elbow i.e. when the upward movement of the curl stops and reverses
 * <p>
 * 2. bet->the smallest angle at the knee at any point in the exercise
 * <p>
 * 3. [gamma,delta]->the maximum angles from the vertical of the head compared with the feet (or if that is not practical the maximu angle of the back from the vertical (both leaning forward and back - so two measurements
 * <p>
 * 4. the maximum movement of the hips 9they tend to sway in some of the videos
 * <p>
 * <p>
 * Score A - Knee bend
 * <p>
 * = 100 - ((180 - result)/2)
 * so if the result is 160 then the Score A is 100-(20/2) = 90
 * <p>
 * Score B - Elbow angle
 * <p>
 * = 100 - (result - 35)
 * so if the result is 55 then the Score B is 100-(20) = 80
 * <p>
 * Have a Minimum Bound of 35 below which is a fail
 * <p>
 * Score C - Back bend (Min/Max)
 * <p>
 * calculate the Difference between Min and Max  so 4:11 = 7; -4:23 = 27
 * <p>
 * = 100 - (3 * Difference)
 * so if the result is 3:19 then the Difference is 16 and the Score C is 100-(3*16) = 52
 * <p>
 * Have a Minimum Bound of 35 below which is a fail
 * <p>
 * Score D - Hip Motion
 * <p>
 * = 100 - (5 * result)
 * <p>
 * so if the result is 7.28 then the Score D is 100 - 5* 7.28) = 63.6
 * <p>
 * The the Overall Score is
 * <p>
 * (10% Score A) + (20% Score B) + (40% Score C) + (30% Score D)
 */
class CurlExerciseAnalyzer extends ExerciseAnalyser {

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

    private Float mA, mB, mC, mD;
    private Float mMeanA = 0f, mMeanB = 0f, mMeanC = 0f, mMeanD = 0f;
    private Float mOverall = 0f;
    private Float mMeanOverall = 0f;

    int count = 0;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void analyze(float[] pose, int frameNum) {
        int angleAtKnee = getAngleAtKnee(pose, RIGHT_ARM);
        int angleAtElbow = getAngleAtElbow(pose, RIGHT_ARM);
        int angleAtBack = -getAngleAtBack(pose, RIGHT_ARM);

        float x0 = (pose[3 * JointsMap.RANKLE] + pose[3 * JointsMap.LANKLE]) / 2;
        float y0 = (pose[3 * JointsMap.RANKLE + 1] + pose[3 * JointsMap.LANKLE + 1]) / 2;

        float xp = pose[3 * JointsMap.PELV];
        float yp = pose[3 * JointsMap.PELV + 1];

        float xn = pose[3 * JointsMap.NECK];
        float yn = pose[3 * JointsMap.NECK + 1];

        float S = (x0 * yn + xn * yp + xp * y0 - y0 * xn - yn * xp - yp * x0) / 2;
        float epszet = (float) (S / (2 * Math.sqrt((x0 - xn) * (x0 - xn) + (y0 - yn) * (y0 - yn))));

        Log.d("UUUUP", angleAtKnee + "\t\t" + angleAtElbow);

        if (ALPHA > angleAtKnee && angleAtKnee > 0)
            ALPHA = angleAtKnee;

        if (GAMMA > angleAtBack)
            GAMMA = angleAtBack;
        Log.d("GAMMA", "ANGLE at back" + angleAtBack);

        if (DELTA < angleAtBack)
            DELTA = angleAtBack;

        if (EPSILON > epszet)
            EPSILON = epszet;

        if (ZETA < epszet)
            ZETA = epszet;

        if (state == UP) {
            if (angleAtElbow < downThresh) {
                state = DOWN;
            }
        } else {
            if (angleAtElbow > upThresh) {
                state = UP;
                Log.d("UUUUP----------->", +ALPHA + "\t\t" + BETA);
//                if(BETA > 90) BETA -= 180;
                float x = pose[3 * JointsMap.RANKLE] - pose[3 * JointsMap.RKNEE];
                float y = pose[3 * JointsMap.RANKLE + 1] - pose[3 * JointsMap.RKNEE + 1];

                float hip_rat = (float) ((ZETA - EPSILON) / Math.sqrt(x * x + y * y));
                hip_rat = (int) (5000 * hip_rat);
                hip_rat /= 50;

                mA = 100 - (180 - ALPHA) / 2f;
                mB = 100 - (BETA - 35f); if(mB > 100) mB = 100f;
                mC = 100 - 3f * (DELTA - GAMMA);
                mD = 100 - 5 * hip_rat;
                mOverall = (0.1f * mA + 0.2f * mB + 0.4f * mC + 0.3f * mD);

                if (GAMMA < 0) GAMMA = -GAMMA * 2 + 1;
                else GAMMA <<= 1;

                if (DELTA < 0) DELTA = -DELTA * 2 + 1;
                else DELTA <<= 1;

                synchronized (Globals.EXERCISE_CRITERIA) {
                    Globals.EXERCISE_CRITERIA.put("α knee", ALPHA);
                    Globals.EXERCISE_CRITERIA.put("α elbow", BETA);
                    Globals.EXERCISE_CRITERIA.put("α back mn-mx", GAMMA + 10000 * DELTA);
                    Globals.EXERCISE_CRITERIA.put("hip sway(%)", hip_rat);
                    if (mC > 35 && mB > 35) {
                        count++;
                        mMeanOverall += mOverall;
                        mMeanA += mA;
                        mMeanB += mB;
                        mMeanC += mC;
                        mMeanD += mD;
                    }
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

        if (state == DOWN) {
            if (BETA > angleAtElbow)
                BETA = angleAtElbow;
        }
    }

    @Override
    public void loadScores() {
        if(count > 0){
            Globals.EXERCISE_SCORES.put("Knee bend", mMeanA / count);
            Globals.EXERCISE_SCORES.put("Elbow angle", mMeanB / count);
            Globals.EXERCISE_SCORES.put("Back bend", mMeanC / count);
            Globals.EXERCISE_SCORES.put("Hip motion", mMeanD / count);
            Globals.EXERCISE_SCORES.put("OVERALL", mMeanOverall / count);
            Globals.EXERCISE_SCORES.put("Count", count + 0.f);
        }
    }
}
