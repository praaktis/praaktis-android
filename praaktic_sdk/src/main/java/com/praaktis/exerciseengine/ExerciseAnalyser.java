package com.praaktis.exerciseengine;

import java.util.ArrayList;


class ExerciseAnalyser {

    // private ArrayList<ArrayList<float []>> mPoses;

    public static final int LEFT_ARM = 0;
    public static final int RIGHT_ARM = 1;

    protected float getLinesLength(float x0, float y0,
                                 float x1, float y1) {
        return (float) Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
    }

    protected float getLinesAngle(float x1, float y1,
                                float x0, float y0,
                                float x2, float y2) {
        double vx1 = x1 - x0;
        double vx2 = x2 - x0;
        double vy1 = y1 - y0;
        double vy2 = y2 - y0;
        double angle = Math.atan2(vx1 * vy2 - vy1 * vx2, vx1 * vx2 + vy1 * vy2) * 180 / Math.PI;
        if (angle < 0)
            angle += 360.0;
        return (float) angle;
    }

    public float getElbowAngle(float[] pose, int arm) {
        float point1x, point1y;
        float point0x, point0y;
        float point2x, point2y;
        int idx;

        switch (arm) {
            case LEFT_ARM:
                idx = JointsMap.LWRIST * 3;
                point1x = pose[idx];
                point1y = pose[idx + 1];
                idx = JointsMap.LELBOW * 3;
                point0x = pose[idx];
                point0y = pose[idx + 1];
                idx = JointsMap.LSHOULDER * 3;
                point2x = pose[idx];
                point2y = pose[idx + 1];
                break;
            case RIGHT_ARM:
                idx = JointsMap.RWRIST * 3;
                point2x = pose[idx];
                point2y = pose[idx + 1];
                idx = JointsMap.RELBOW * 3;
                point0x = pose[idx];
                point0y = pose[idx + 1];
                idx = JointsMap.RSHOULDER * 3;
                point1x = pose[idx];
                point1y = pose[idx + 1];
                break;
            default:
                return 0.0f;
        }

        float tmp = getLinesAngle(point1x, point1y,
                point0x, point0y,
                point2x, point2y);

        if(tmp < 0) tmp += 360;
        if(tmp > 180) tmp = 360 - tmp;

        return tmp;
    }

    public float getArmAngle(float[] pose, int arm) {
        float point1x, point1y;
        float point0x, point0y;
        float point2x, point2y;
        int idx;

        switch (arm) {
            case LEFT_ARM:
                idx = JointsMap.LWRIST * 3;
                point1x = pose[idx];
                point1y = pose[idx + 1];
                idx = JointsMap.LSHOULDER * 3;
                point0x = pose[idx];
                point0y = pose[idx + 1];
                idx = JointsMap.NECK * 3;
                point2x = pose[idx];
                point2y = pose[idx + 1];
                break;
            case RIGHT_ARM:
                idx = JointsMap.RWRIST * 3;
                point2x = pose[idx];
                point2y = pose[idx + 1];
                idx = JointsMap.RSHOULDER * 3;
                point0x = pose[idx];
                point0y = pose[idx + 1];
                idx = JointsMap.NECK * 3;
                point1x = pose[idx];
                point1y = pose[idx + 1];
                break;
            default:
                return 0.0f;
        }
        float tmp = getLinesAngle(point1x, point1y,
                point0x, point0y,
                point2x, point2y) - 90;

        if(tmp < 0) tmp += 360;
        if(tmp > 180) tmp = 360 - tmp;

        return tmp;
    }

    // Returns angular speed of an arm in degrees per second
    //
    private float getArmAngularSpeed(float[] pose1, float[] pose2, int arm) {
        float angle1 = getArmAngle(pose1, arm);
        float angle2 = getArmAngle(pose2, arm);
        return (angle2 - angle1) * Globals.VIDEO_FRAME_PER_SECOND;
    }

    // Find the point in time when the excercise starts
    //
    protected int findStartOfExercise(ArrayList<float[]> poses) {
        int numPoses = poses.size();
        int maxPose = numPoses - 1;
        final int THRESHOLD = 5; // 5 deg/second

        for (int i = 0; i < maxPose; i++) {
            float angSpeedLeft = getArmAngularSpeed(poses.get(i), poses.get(i + 1), LEFT_ARM);
            float angSpeedRight = getArmAngularSpeed(poses.get(i), poses.get(i + 1), RIGHT_ARM);
            if (angSpeedLeft >= THRESHOLD || angSpeedRight >= THRESHOLD)
                return i;
        }
        return 0;
        //return -1;
    }

    // Find the point in time when the excercise ends
    // if excercise was in process after the expected time period
    // for completion, -1 is returned
    //
    private int findEndOfExercise(ArrayList<float[]> poses) {
        int numPoses = poses.size();
        int maxPose = numPoses - 1;
        return maxPose - 1;
//        final int THRESHOLD = 5; // 5 deg/second
//        for (int i = 0; i < maxPose; i++) {
//            float angSpeedLeft = getArmAngularSpeed(poses.get(i), poses.get(i + 1), LEFT_ARM);
//            float angSpeedRight = getArmAngularSpeed(poses.get(i), poses.get(i + 1), RIGHT_ARM);
//            if (angSpeedLeft < THRESHOLD && angSpeedRight < THRESHOLD)
//                return i;
//        }
        //return -1;
    }

    // Returns an array of 3 floats (score 1, score 2, score 3)
    //
    public float[] analyzeExcercise(ArrayList<float[]> poses) {
        int startOfExcercise = findStartOfExercise(poses);
        if (startOfExcercise == -1)
            return null;
        int endOfExcercise = findEndOfExercise(poses);
        if (endOfExcercise == -1)
            return null;
        float[] res = new float[3];

        float maxArmAngle = 0;
        float maxElbowAngle = 0;
        double sumElbowAngleLeft = 0.0;
        double sumElbowAngleRight = 0.0;

        for (int i = startOfExcercise; i < endOfExcercise; i++) {
            float armAngleLeft;
            float armAngleRight;

            armAngleLeft = getArmAngle(poses.get(i), LEFT_ARM);
            armAngleRight = getArmAngle(poses.get(i), RIGHT_ARM);
            maxArmAngle = Math.max(Math.min(armAngleLeft, armAngleRight), maxArmAngle);

            float elbowAngleLeft;
            float elbowAngleRight;

            elbowAngleLeft = getElbowAngle(poses.get(i), LEFT_ARM);
            elbowAngleRight = getElbowAngle(poses.get(i), RIGHT_ARM);
            maxElbowAngle = Math.max(Math.min(elbowAngleLeft, elbowAngleRight), maxElbowAngle);

            sumElbowAngleLeft += elbowAngleLeft;
            sumElbowAngleRight += elbowAngleRight;

        }

        float avgElbowAngleLeft = (float) (sumElbowAngleLeft / (endOfExcercise - startOfExcercise));
        float avgElbowAngleRight = (float) (sumElbowAngleRight / (endOfExcercise - startOfExcercise));
        float avgElbowAngle = (float) ((avgElbowAngleLeft + avgElbowAngleRight) / 2.0);

        res[0] = (maxArmAngle < 90) ? 0 : (maxArmAngle - 80);
        res[1] = (maxElbowAngle < 90) ? 0 : (maxElbowAngle - 80);
        res[2] = (avgElbowAngle < 90) ? 0 : (avgElbowAngle - 80);
        return res;
    }
}
