package me.cometkaizo.animation;

import java.util.Objects;

import static me.cometkaizo.util.MathUtils.clamp;
import static me.cometkaizo.util.MathUtils.lerp;

public class SimpleTransition implements Transition {
    private final double start;
    private final double end;
    private final Ease ease;
    private final double duration;

    public SimpleTransition(double start, double end, Ease ease, int duration) {
        Objects.requireNonNull(ease, "Ease cannot be null");
        throwIfIllegalDuration(duration);
        this.start = start;
        this.end = end;
        this.ease = ease;
        this.duration = duration;
    }

    private static void throwIfIllegalDuration(int duration) {
        if (duration < 0) throw new IllegalArgumentException("Duration " + duration + " cannot be negative");
    }

    public SimpleTransition(double start, double end, Ease ease, double speed) {
        this(start, end, ease, (int) ((end - start) / speed));
    }

    public double apply(int lengthPlayed) {
        double progress = lengthPlayed / duration;
        double easedProgress = ease.apply(clamp(progress, 0, 1));
        return lerp(easedProgress, start, end);
    }

    @Override
    public int getDuration() {
        return (int) duration;
    }

}
