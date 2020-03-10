package com.praaktis.exerciseengine;

public class StretchingArmsUpAnalyzer extends ExerciseAnalyser {
    final int THRESHOLD = 5; // 5 deg/second
    float[] mPrewPerson = null;
    boolean mStarted = false;
    int mCnt = 0;
    private int mSumElbowAngleLeft = 0;
    private int mSumElbowAngleRight = 0;
    private int mSaxArmAngle = 0;
    private int mMaxElbowAngle = 0;

    @Override
    public void analyze(float[] person) {
        if (mPrewPerson == null) {
            mPrewPerson = person;
            return;
        }

        float angSpeedLeft = getArmAngularSpeed(mPrewPerson, person, LEFT_ARM);
        float angSpeedRight = getArmAngularSpeed(mPrewPerson, person, RIGHT_ARM);
        if (angSpeedLeft >= THRESHOLD || angSpeedRight >= THRESHOLD)
            mStarted = true;

        if (!mStarted) return;
        mCnt ++;

        float[] res = new float[3];

        float maxArmAngle = 0;
        float maxElbowAngle = 0;
        double sumElbowAngleLeft = 0.0;
        double sumElbowAngleRight = 0.0;

        float armAngleLeft;
        float armAngleRight;

        armAngleLeft = getArmAngle(person, LEFT_ARM);
        armAngleRight = getArmAngle(person, RIGHT_ARM);
        maxArmAngle = Math.max(Math.min(armAngleLeft, armAngleRight), maxArmAngle);

        float elbowAngleLeft;
        float elbowAngleRight;

        elbowAngleLeft = getElbowAngle(person, LEFT_ARM);
        elbowAngleRight = getElbowAngle(person, RIGHT_ARM);
        maxElbowAngle = Math.max(Math.min(elbowAngleLeft, elbowAngleRight), maxElbowAngle);

        sumElbowAngleLeft += elbowAngleLeft;
        sumElbowAngleRight += elbowAngleRight;

    }

    @Override
    public void loadScores() {

        float avgElbowAngleLeft = (float) (mSumElbowAngleLeft / mCnt);
        float avgElbowAngleRight = (float) (mSumElbowAngleRight / mCnt);
        float avgElbowAngle = (float) ((avgElbowAngleLeft + avgElbowAngleRight) / 2.0);

        Float[] res = new Float[3];
        res[0] = (mSaxArmAngle < 90) ? 0f : (mSaxArmAngle - 80);
        res[1] = (mMaxElbowAngle < 90) ? 0f : (mMaxElbowAngle - 80);
        res[2] = (avgElbowAngle < 90) ? 0f : (avgElbowAngle - 80);

        synchronized (Globals.EXERCISE_SCORES){
//            Globals.EXERCISE_SCORES.put("ANGLE AT HIGHEST POINT", res[0]);
//            Globals.EXERCISE_SCORES.put("ARMS STRIGHT",           res[1]);
//            Globals.EXERCISE_SCORES.put("ARMS MOVE SAME PACE",    res[2]);
            Globals.EXERCISE_SCORES.put("S1", res[0]);
            Globals.EXERCISE_SCORES.put("S2",  res[1]);
            Globals.EXERCISE_SCORES.put("S",    res[2]);
            Globals.EXERCISE_SCORES.put("OVERALL", res[0] * 0.45 + res[1] * 0.2 + res[2] * 0.35);
        }
    }
}
