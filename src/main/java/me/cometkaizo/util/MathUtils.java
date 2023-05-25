package me.cometkaizo.util;

public class MathUtils {

    public static double clamp(double num, double low, double high) {
        return num < low ? low : Math.min(num, high);
    }

    public static int clamp(int num, int low, int high) {
        return num < low ? low : Math.min(num, high);
    }

    public static double lerp(double percent, double start, double end) {
        return start + percent * (end - start);
    }

    public static double roundToMultipleOf(double num, double mul) {
        return mul*(Math.round(num/mul));
    }

    private MathUtils() {
        throw new AssertionError("No MathUtils instances for you!");
    }

}
