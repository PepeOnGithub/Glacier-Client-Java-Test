package net.glacierclient.core.util;

import java.util.HashMap;
import java.util.Map;

public final class AnimationUtil {

    private AnimationUtil() {}

    private static final Map<String, Double> animValues = new HashMap<>();
    private static final Map<String, Long> lastTimes = new HashMap<>();

    public static double animate(String key, double target, double speed) {
        double current = animValues.getOrDefault(key, target);
        long now = System.currentTimeMillis();
        long last = lastTimes.getOrDefault(key, now);
        double delta = Math.min((now - last) / 1000.0, 0.1);
        lastTimes.put(key, now);
        double animated = current + (target - current) * Math.min(1, speed * delta * 15);
        animValues.put(key, animated);
        return animated;
    }

    public static float easeInOut(float t) {
        return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    public static float easeOut(float t) {
        return 1 - (1 - t) * (1 - t);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static int lerpColor(int colorA, int colorB, float t) {
        int aa = (colorA >> 24) & 0xFF, ar = (colorA >> 16) & 0xFF, ag = (colorA >> 8) & 0xFF, ab = colorA & 0xFF;
        int ba = (colorB >> 24) & 0xFF, br = (colorB >> 16) & 0xFF, bg = (colorB >> 8) & 0xFF, bb = colorB & 0xFF;
        return ((int)(aa + (ba - aa) * t) << 24) | ((int)(ar + (br - ar) * t) << 16) |
               ((int)(ag + (bg - ag) * t) << 8) | (int)(ab + (bb - ab) * t);
    }
}
