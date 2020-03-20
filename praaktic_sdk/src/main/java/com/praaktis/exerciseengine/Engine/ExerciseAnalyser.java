package com.praaktis.exerciseengine.Engine;

/**
 * Abstract Exercise Analyzer class, encapsulates algorithm of analyzing.
 *
 */
abstract class ExerciseAnalyser {

    protected Integer mFrameNum = 0;


    protected static final int LEFT_ARM = 0;
    protected static final int RIGHT_ARM = 1;

    /**
     *
     * @param exercise Exercise
     * @return ExerciseAnalyzer object
     */
    public static ExerciseAnalyser createAnalyzer(Exercise exercise) {

        switch (exercise){
            case SQUATS:{
                return new SquatExerciseAnalyzer();
            }

            case CURL: {
                return new CurlExerciseAnalyzer();
            }

            case STRETCHING_ARMS_UP:{
                return new StretchingArmsUpAnalyzer();
            }

            default:
                throw new IllegalStateException("Unexpected value: " + exercise);
        }
    }

    /**
     * This method is used to calculate distance betweem two points
     * @param x0 abscissa of the first point
     * @param y0 ordinate of the first point
     * @param x1 abscissa of the second point
     * @param y1 ordinate of the second point
     * @return float
     */
    protected float getLinesLength(float x0, float y0,
                                   float x1, float y1) {
        return (float) Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
    }

    /**
     * Find angle between vectors AB, AC, where
     * @param x1 abscissa of B
     * @param y1 ordinate of B
     * @param x0 abscissa of A
     * @param y0 ordinate of A
     * @param x2 abscissa of C
     * @param y2 ordinate of C
     * @return float
     */
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

    /**
     * Compute and return angle between back and vertical
     * @param person joint points of a person WRNCH map
     * @param side which side(LEFT or RIGHT)
     * @return angle
     */
    protected int getAngleAtBack(float[] person, int side) {
        float neckX = person[3 * (side == LEFT_ARM ? JointsMap.NECK : JointsMap.NECK)];
        float neckY = person[3 * (side == LEFT_ARM ? JointsMap.NECK : JointsMap.NECK) + 1];

        float pelvX = person[3 * (side == LEFT_ARM ? JointsMap.PELV : JointsMap.PELV)];
        float pelvY = person[3 * (side == LEFT_ARM ? JointsMap.PELV : JointsMap.PELV) + 1];

        float v1x = pelvX - pelvX;
        float v1y = 0 - pelvY;

        float v2x = neckX - pelvX;
        float v2y = neckY - pelvY;

        double pi = Math.PI;
        if(v2x < 0) pi = -pi;

        return (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / pi * 180);
    }

    /**
     * Compute and return angle at elbow
     * @param pose joint points of a person WRNCH map
     * @param arm which side(LEFT or RIGHT)
     * @return angle
     */
    protected float getElbowAngle(float[] pose, int arm) {
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

    /**
     * Compute and return angle at elbow
     * @param person joint points of a person WRNCH map
     * @param side which side(LEFT or RIGHT)
     * @return angle
     */
    protected int getAngleAtElbow(float[] person, int side) {
        float shoulderX = person[3 * (side == LEFT_ARM ? JointsMap.LSHOULDER : JointsMap.RSHOULDER)];
        float shoulderY = person[3 * (side == LEFT_ARM ? JointsMap.LSHOULDER : JointsMap.RSHOULDER) + 1];

        float elbowX = person[3 * (side == LEFT_ARM ? JointsMap.LELBOW : JointsMap.RELBOW)];
        float elbowY = person[3 * (side == LEFT_ARM ? JointsMap.LELBOW : JointsMap.RELBOW) + 1];

        float wristX = person[3 * (side == LEFT_ARM ? JointsMap.LWRIST : JointsMap.RWRIST)];
        float wristY = person[3 * (side == LEFT_ARM ? JointsMap.LWRIST : JointsMap.RWRIST) + 1];

        float v1x = wristX - elbowX;
        float v1y = wristY - elbowY;

        float v2x = shoulderX - elbowX;
        float v2y = shoulderY - elbowY;

        return  (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / Math.PI * 180);
    }

    /**
     * Compute and return angle at armpit
     * @param pose joint points of a person WRNCH map
     * @param arm which side(LEFT or RIGHT)
     * @return angle
     */
    protected float getArmAngle(float[] pose, int arm) {
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

    /**
     * Compute and return angle between back and shin
     * @param person joint points of a person WRNCH map
     * @param side which side(LEFT or RIGHT)
     * @return angle
     */
    protected int getAngleAtBackAndShin(float[] person, int side) {
        float rShouldX = person[3 * (side == LEFT_ARM ? JointsMap.LSHOULDER : JointsMap.RSHOULDER)];
        float rShouldY = person[3 * (side == LEFT_ARM ? JointsMap.LSHOULDER : JointsMap.RSHOULDER) + 1];

        float rHipX = person[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP)];
        float rHipY = person[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP) + 1];

        float rKneeX = person[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE)];
        float rKneeY = person[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE) + 1];

        float rAnkleX = person[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE)];
        float rAnkleY = person[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE) + 1];

        float v1x = rShouldX - rHipX;
        float v1y = rShouldY - rHipY;

        float v2x = rKneeX - rAnkleX;
        float v2y = rKneeY - rAnkleY;
        int alpha = (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / Math.PI * 180);
        return alpha;
    }

    /**
     * Compute and return angle at knee
     * @param person joint points of a person WRNCH map
     * @param side which side(LEFT or RIGHT)
     * @return angle
     */
    public int getAngleAtKnee(float[] person, int side) {
        float hipX = person[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP)];
        float hipY = person[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP) + 1];

        float kneeX = person[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE)];
        float kneeY = person[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE) + 1];

        float ankleX = person[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE)];
        float ankleY = person[3 * (side == LEFT_ARM ? JointsMap.LANKLE : JointsMap.RANKLE) + 1];

        float v1x = ankleX - kneeX;
        float v1y = ankleY - kneeY;

        float v2x = hipX - kneeX;
        float v2y = hipY - kneeY;

        return (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / Math.PI * 180);
    }

    /**
     * Compute and return angle between vertical and segment connecting hip and knee
     * @param person joint points of a person WRNCH map
     * @param side which side(LEFT or RIGHT)
     * @return angle
     */
    protected int getAngleAtHip(float[] person, int side) {
        float hipX = person[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP)];
        float hipY = person[3 * (side == LEFT_ARM ? JointsMap.LHIP : JointsMap.RHIP) + 1];

        float kneeX = person[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE)];
        float kneeY = person[3 * (side == LEFT_ARM ? JointsMap.LKNEE : JointsMap.RKNEE) + 1];

        float v1x = 100;
        float v1y = 0;

        float v2x = hipX - kneeX;
        float v2y = hipY - kneeY;

        int beta = (int) (Math.acos((v1x * v2x + v1y * v2y) / Math.sqrt(v1x * v1x + v1y * v1y) / Math.sqrt(v2x * v2x + v2y * v2y)) / Math.PI * 180);
        if(beta > 90) beta = 180 - beta;

        if(hipY > kneeY) beta = -beta;

        return beta;
    }

    /**
     * Returns angular speed of an arm in degrees per second
     * @param pose1 coordinates of key points of the first pose
     * @param pose2 coordinates of key points of the second pose
     * @param arm Indicator of from which side we should calculate the result
     * @return float
     */
    protected float getArmAngularSpeed(float[] pose1, float[] pose2, int arm) {
        float angle1 = getArmAngle(pose1, arm);
        float angle2 = getArmAngle(pose2, arm);
        return (angle2 - angle1) * Globals.VIDEO_FRAME_PER_SECOND;
    }

    /**
     *  Analyze a frame.
     * @param pose coordinates of key points
     * @param frameNum order number of the frame to be analyzed
     */
    public abstract void analyze(float[] pose, int frameNum);

    /**
     * After analyzing all frames this method should be called in order to fill the scores HashMap.
     */
    public abstract void loadScores();
}
