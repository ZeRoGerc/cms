package logic;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Core {

    public static final double EPS = 1e-9;

    public static final int ITERATIONS = 10000;

    public static final int STEPS = 128;

    public static double f(double r, double x) {
        return r * x * (1 - x);
    }

    public static boolean equals(double x1, double x2) {
        return Math.abs(x1 - x2) < EPS;
    }

    @NotNull
    public static List<Double> findRoots(double r) {
        List<Double> result = new ArrayList<>(STEPS);

        double step = 1. / STEPS;
        double currentY = step;

        for (int i = 1; i < STEPS; i++) {
            result.add(iteratePoint(r, currentY));
            currentY += step;
        }

        return result;
    }

    @NotNull
    public static List<Double> findConvergeSeries(double r) {
        double current = 0.5;

        List<Double> result = new ArrayList<>();
        result.add(current);

        int cycleLength = 1;
        while (cycleLength <= ITERATIONS) {
            double comp = current;
            for (int i = 0; i < cycleLength; i++) {
                current = f(r, current);
                if (equals(comp, current)) {
                    break;
                }
                result.add(current);
            }
            cycleLength *= 2;
        }
        return result;
    }

    private static double iteratePoint(double r, double currentY) {
        double result = currentY;
        for (int i = 0; i < ITERATIONS; i++) {
            result = f(r, result);
        }
        return result;
    }
}
