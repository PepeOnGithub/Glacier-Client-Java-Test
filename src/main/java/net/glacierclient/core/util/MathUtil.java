package net.glacierclient.core.util;

public final class MathUtil {

    private MathUtil() {}

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp(t, 0, 1);
    }

    public static float smoothStep(float t) {
        t = clamp(t, 0, 1);
        return t * t * (3 - 2 * t);
    }

    public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1);
    }

    public static float wrapAngle(float angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
