package com.praaktis.exerciseengine;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class SquatExerciseAnalyzer extends ExerciseAnalyser {

    private final int UP = 1;
    private final int DOWN = 0;
    private int state = DOWN;

    private int upThresh = 160;
    private int downThresh = 120;

    private float minY = 1000;
    private int ALPHA;
    private int BETA;

    private ArrayList<Integer> mS1 = new ArrayList<>();
    private ArrayList<Integer> mS2 = new ArrayList<>();
    private ArrayList<Integer> mS  = new ArrayList<>();
    private Integer mMeanS1 = 0;
    private Integer mMeanS2 = 0;
    private Integer mMeanS = 0;
    int count = 0;
    int mFrameNum = 0;

    SquatExerciseAnalyzer() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void analyze(float[] mPoints) {
        int alpha = getAngleAtBackAndShin(mPoints, LEFT_ARM);
        int beta = getAngleAtHip(mPoints, LEFT_ARM);
        int gamma = getAngleAtKnee(mPoints, LEFT_ARM);

        if (state == UP) {
            if (gamma < downThresh) {
                state = DOWN;
            }
        } else {
            if (gamma > upThresh && minY < 0 && ALPHA < 60 && BETA < 40) {
                state = UP;
                minY = 1000;
                if (BETA > 90)  BETA -= 180;
                if (BETA < -10) BETA = -10;

                int S1 = 80  - (2 * BETA);
                int S2 = 100 - (2 * ALPHA);
                int  S = (int) (S1 * 0.6f + S2 * 0.4f);

                mS1.add(S1);
                mS2.add(S2);
                mS .add(S );

                mMeanS1 += S1;
                mMeanS2 += S2;
                mMeanS  +=  S;

                int[] scoresArr = new int[]{S1, S2, S};

                synchronized (Globals.EXERCISE_CRITERIA) {
                    Globals.EXERCISE_CRITERIA.put("back/shin diff", ALPHA);
                    Globals.EXERCISE_CRITERIA.put("knee/hip angle", BETA);

                    Globals.EXERCISE_SCORES.put(((Integer)mFrameNum).toString(), scoresArr);

                    if(BETA < 30 && ALPHA < 40) {
                        count ++;
                        Globals.EXERCISE_CRITERIA.put("COUNT", count);
                    }
                }
            }
        }

        if (state == DOWN) {
            float y = -mPoints[JointsMap.RSHOULDER * 2 + 1];
            if (y < minY) {
                minY  = y;
                ALPHA = alpha;
                BETA  = beta;
            }
        }
        mFrameNum ++;
    }

    @Override
    public void loadScores() {
        if(mS.isEmpty()) return;
        synchronized (Globals.EXERCISE_SCORES){
            int n = mS.size();
            Globals.EXERCISE_SCORES.put("meanS1", mMeanS1 / n);
            Globals.EXERCISE_SCORES.put("meanS2", mMeanS2 / n);
            Globals.EXERCISE_SCORES.put("meanS" ,  mMeanS / n);
            Globals.EXERCISE_SCORES.put("allS1" ,   mS1);
            Globals.EXERCISE_SCORES.put("allS2" ,   mS2);
            Globals.EXERCISE_SCORES.put("allS"  ,    mS);
            Globals.EXERCISE_SCORES.put("count" , count);
        }
    }
}
