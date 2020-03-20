package com.praaktis.exerciseengine.Engine;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

/**
 *  Analyzer class for Squats
 */
public class SquatExerciseAnalyzer extends ExerciseAnalyser {

    private final int UP = 1;
    private final int DOWN = 0;
    private int state = DOWN;

    private int upThresh = 160;
    private int downThresh = 120;

    private float minY = 1000;
    private int ALPHA;
    private int BETA;

    private ArrayList<Float> mS1 = new ArrayList<>();
    private ArrayList<Float> mS2 = new ArrayList<>();
    private ArrayList<Float> mS  = new ArrayList<>();
    private Float mMeanS1 = 0f;
    private Float mMeanS2 = 0f;
    private Float mMeanS = 0f;
    int count = 0;

    private ArrayList<Float[]> mCaptionVals = new ArrayList<>();
    private ArrayList<Integer> mCaptionFrames = new ArrayList<>();

    SquatExerciseAnalyzer() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void analyze(float[] pose, int frameNum) {
        int alpha = getAngleAtBackAndShin(pose, LEFT_ARM);
        int beta = getAngleAtHip(pose, LEFT_ARM);
        int gamma = getAngleAtKnee(pose, LEFT_ARM);

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

                Float S1 = 80f  - (2 * BETA);
                Float S2 = 100f - (2 * ALPHA);
                Float  S = (S1 * 0.6f + S2 * 0.4f);

                mS1.add(S1);
                mS2.add(S2);
                mS .add(S );

                mMeanS1 += S1;
                mMeanS2 += S2;
                mMeanS  +=  S;

                Float[] scoresArr = new Float[]{S1, S2, S};
                mCaptionVals.add(scoresArr);
                mCaptionFrames.add(mFrameNum + frameNum);

                synchronized (Globals.EXERCISE_CRITERIA) {
                    Globals.EXERCISE_CRITERIA.put("back/shin diff", ALPHA);
                    Globals.EXERCISE_CRITERIA.put("knee/hip angle", BETA);

                    if(BETA < 30 && ALPHA < 40) {
                        count ++;
                        Globals.EXERCISE_CRITERIA.put("COUNT", count);
                    }
                }
            }
        }

        if (state == DOWN) {
            float y = -pose[JointsMap.RSHOULDER * 2 + 1];
            if (y < minY) {
                minY  = y;
                ALPHA = alpha;
                BETA  = beta;
            }
        }
    }

    @Override
    public void loadScores() {
        if(mS.isEmpty()) return;
        synchronized (Globals.EXERCISE_SCORES){
            int n = mS.size();
            Globals.EXERCISE_SCORES.put("Count" , new DetailPoint(count, 23));
            Globals.EXERCISE_SCORES.put("Angle of thigh", new DetailPoint(mMeanS1 / n, 24));
            Globals.EXERCISE_SCORES.put("Back / shin angle", new DetailPoint(mMeanS2 / n, 25));
            Globals.EXERCISE_SCORES.put("Overall", new DetailPoint(mMeanS / n, 26));
            Globals.EXERCISE_SCORES.put("NAMES", new String[]{"S1", "S2", "S"});
            Globals.EXERCISE_SCORES.put("CAPTION_VALS", mCaptionVals);
            Globals.EXERCISE_SCORES.put("CAPTION_FRAMES", mCaptionFrames);
        }
    }
}
