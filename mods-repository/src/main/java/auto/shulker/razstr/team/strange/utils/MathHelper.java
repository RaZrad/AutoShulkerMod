package auto.shulker.razstr.team.strange.utils;

public class MathHelper {
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public static double round(double value, double increment) {
        return Math.round(value / increment) * increment;
    }
    public static float interpolate(float current, float old, double scale) {
        return (float) (old + (current - old) * scale);
    }
    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }
}
