package logic;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Core {

    public static final double EPS = 1e-9;

    public static final int ITERATIONS = 10000;

    public static final int STEPS = 100;

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

        return retainUnique(result);
    }

    @NotNull
    public static List<Double> findConvergeSeries(double start, double r) {
        double current = start;

        List<Double> result = new ArrayList<>();
        result.add(current);

        double comp = current;
        for (int i = 0; i < ITERATIONS; i++) {
            current = f(r, current);

            if (equals(comp, current)) {
                break;
            }

            if ((i & (i - 1)) == 0) {
                comp = current;
            }

            result.add(current);
        }
        return result;
    }

    private static double iteratePoint(double r, double currentY) {
        double result = currentY;

        double comp = result;

        for (int i = 1; i < ITERATIONS; i++) {
            result = f(r, result);

            if (equals(result, comp)) {
                break;
            }

            if ((i & (i - 1)) == 0) {
                comp = result;
            }
        }
        return result;
    }

    /**
     * Warning: This method will sort given list!
     */
    @NotNull
    private static List<Double> retainUnique(@NotNull List<Double> list) {
        Collections.sort(list);

        List<Double> unique = new ArrayList<>();
        unique.add(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            if (!equals(list.get(i), unique.get(unique.size() - 1))) {
                unique.add(list.get(i));
            }
        }

        return unique;
    }
}
