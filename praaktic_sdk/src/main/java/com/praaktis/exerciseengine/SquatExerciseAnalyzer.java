package com.praaktis.exerciseengine;

import android.util.Log;

public class SquatExerciseAnalyzer extends ExerciseAnalyser {

//    private ArrayList<Float> mHeights;
//    private ArrayList<Float> mWindows;
//    private ArrayList<Integer> mAngles;
//    private final static int WINDOW_SIZE = 10;
//    private int mSteps = 0;
//    private int mLastWindowSize = 0;
//    private float mLastWindowMin = 10000;
//    private int mLastWindowAlphaMin = 180;
//    private int mNumOfWindows = 0;

    private final int UP = 1;
    private final int DOWN = 0;
    private int state = DOWN;


    private int upThresh = 160;
    private int downThresh = 120;

    private float minY = 1000;
    private int ALPHA;
    private int BETA;

    SquatExerciseAnalyzer() {
//        mHeights = new ArrayList<>();
//        mWindows = new ArrayList<>();
//        mAngles = new ArrayList<>();
    }

    public boolean isAFullCycle(float[] mPoints) {
//        float mn = mPoints[JointsMap.RSHOULDER * 3 + 1];
//        float mx = mPoints[JointsMap.RHEEL * 3 + 1];
//
//        float t = mx - mn;
//        float curHeight = t;
//
//        int u = 100;
//        for (int i = 1; i <= u - 1; i++) {
//            if (mSteps - i < 0) {
//                curHeight += t;
//            } else curHeight += mHeights.get(mSteps - i);
//        }
//
//        curHeight /= u;
//
//        mHeights.add(curHeight);
//        mSteps++;

        int alpha = checkBackAndShin(mPoints, LEFT_ARM);
        int beta = checkHipAngle(mPoints, LEFT_ARM);
        int gamma = checkGamma(mPoints, LEFT_ARM);

        Log.d("UUUUP", alpha+ " \t\t" + beta + "\t\t" + gamma);

        boolean ok = false;

        if(state == UP){
            if(gamma < downThresh){
                state = DOWN;
            }
        } else {
            if(gamma > upThresh && minY < 0 && ALPHA < 60 && BETA < 50) {
                state = UP;
                ok = true;
                Log.d("UUUUP----------->", minY + "\t\t" + ALPHA + "\t\t" + BETA);
                minY = 1000;
                if(BETA > 90) BETA -= 180;
                Globals.ANGLE_BACK_SHIN = ALPHA;
                Globals.ANGLE_HIP_KNEE = BETA;
            }
        }

        if(state == DOWN){
            float y = -mPoints[JointsMap.RSHOULDER * 2 + 1];
            if(y < minY){
                minY = y;
                ALPHA = alpha;
                BETA = beta;
            }
        }
//        Log.d("HIP_ANGLE", beta + " ");
//
//        if (mLastWindowSize < WINDOW_SIZE) {
//            mLastWindowMin = Math.min(curHeight, mLastWindowMin);
//            mLastWindowAlphaMin = Math.min(alpha, mLastWindowAlphaMin);
//            mLastWindowSize++;
//            return false;
//        }
//        mWindows.add(mLastWindowMin);
//        mAngles.add(mLastWindowAlphaMin);
//        mNumOfWindows++;
//
//        boolean e = (mWindows.size() > 2) && (mWindows.get(mNumOfWindows - 3) > mWindows.get(mNumOfWindows - 2)) && (mWindows.get(mNumOfWindows - 2) < mLastWindowMin);
////        boolean f = mNumOfWindows <= 3 || mWindows.get(mNumOfWindows - 3) > mWindows.get(mNumOfWindows - 2);
//        Log.d("PARALLEL: ", mLastWindowMin + " --- " + mLastWindowAlphaMin + " -> " + (e ? "true" : "false"));
//
//        mLastWindowSize = 0;
//        mLastWindowMin = 10000;
//        mLastWindowAlphaMin = 180;
        return ok;
    }

    private int checkBackAndShin(float[] mPoints, int side) {
        float rShouldX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LSHOULDER : JointsMap.RSHOULDER)];
        float rShouldY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LSHOULDER : JointsMap.RSHOULDER) + 1];

        float rHipX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP)];
        float rHipY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP) + 1];

        float rKneeX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE)];
        float rKneeY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE) + 1];

        float rAnkleX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE)];
        float rAnkleY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE) + 1];

        float v1x = rShouldX - rHipX;
        float v1y = rShouldY - rHipY;

        float v2x = rKneeX - rAnkleX;
        float v2y = rKneeY - rAnkleY;
        int alpha = (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / Math.PI * 180);

        Log.d("PARALLEL: ", alpha + "");
//        float rShouldX = mPoints[3 * JointsMap.RSHOULDER];
//        float rShouldY = mPoints[3 * JointsMap.RSHOULDER + 1];
        return alpha;
    }

    private int checkGamma(float[] mPoints, int side) {
        float hipX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP)];
        float hipY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP) + 1];

        float kneeX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE)];
        float kneeY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE) + 1];

        float ankleX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE)];
        float ankleY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE) + 1];

        float v1x = ankleX - kneeX;
        float v1y = ankleY - kneeY;

        float v2x = hipX - kneeX;
        float v2y = hipY - kneeY;

        int gamma = (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / Math.PI * 180);

        return gamma;
    }

    private int checkHipAngle(float[] mPoints, int side) {
        float hipX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP)];
        float hipY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP) + 1];

        float kneeX = mPoints[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE)];
        float kneeY = mPoints[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE) + 1];

        float v1x = 100;
        float v1y = 0;

        float v2x = hipX - kneeX;
        float v2y = hipY - kneeY;

        int beta = (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / Math.PI * 180);
        if(beta > 90) beta = 180 - beta;

        if(hipY > kneeY) beta = -beta;

        return beta;
    }

}
