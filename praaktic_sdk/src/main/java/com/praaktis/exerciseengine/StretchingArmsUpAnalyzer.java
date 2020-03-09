package com.praaktis.exerciseengine;

public class StretchingArmsUpAnalyzer extends ExerciseAnalyser {
    private float[] mPrewPose;
    private final int THRESHOLD = 5; // 5 deg/second
    private boolean start = false;
    private float[] res = new float[3];
    private int numOfFrames = 0;
    private double sumElbowAngleLeft = 0;
    private double sumElbowAngleRight = 0;
    private float maxArmAngle = 0;
    private float maxElbowAngle = 0;

    @Override
    public void analyze(float[] person) {

        if (mPrewPose == null) {
            mPrewPose = person;
            return;
        }

        if (!start) {
            float angSpeedLeft = getArmAngularSpeed(mPrewPose, person, LEFT_ARM);
            float angSpeedRight = getArmAngularSpeed(mPrewPose, person, RIGHT_ARM);
            if (angSpeedLeft >= THRESHOLD || angSpeedRight >= THRESHOLD)
                start = true;
            else return;
        }
        numOfFrames++;


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
        float avgElbowAngleLeft = (float) (sumElbowAngleLeft / numOfFrames);
        float avgElbowAngleRight = (float) (sumElbowAngleRight / numOfFrames);
        float avgElbowAngle = (float) ((avgElbowAngleLeft + avgElbowAngleRight) / 2.0);

        res[0] = (maxArmAngle < 90) ? 0 : (maxArmAngle - 80);
        res[1] = (maxElbowAngle < 90) ? 0 : (maxElbowAngle - 80);
        res[2] = (avgElbowAngle < 90) ? 0 : (avgElbowAngle - 80);
        Globals.EXERCISE_SCORES.put("max arm angle", res[0]);
        Globals.EXERCISE_SCORES.put("max elbow angle", res[0]);
        Globals.EXERCISE_SCORES.put("avg elbow angle", res[0]);
    }
}
