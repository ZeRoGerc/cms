package logic;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.Math.round;

public class RootFinder {

    private final int threads;

    private final ExecutorService executor;

    public RootFinder() {
        this.threads =  Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public List<Pair<Double, Double>> getRootsForInterval(double left, double right, double step) {
        List<Pair<Double, Double>> result = new ArrayList<>();

        long steps = round((right - left) / step);
        long forOne = steps / threads + 1;

        double current = left;
        List<Future<List<Pair<Double, Double>>>> futures = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            double l = current, r = Math.min(l + step * forOne, right);
            futures.add(executor.submit(getTaskForInterval(l, r, step)));
            current = r;
        }

        for (Future<List<Pair<Double, Double>>> future : futures) {
            try {
                result.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public void clear() {
        executor.shutdownNow();
    }

    private Callable<List<Pair<Double, Double>>> getTaskForInterval(double left, double right, double step) {
        return () -> {
            List<Pair<Double, Double>> temp = new ArrayList<>();
            for (double cur = left; cur < right && !Core.equals(cur, right); cur += step) {
                List<Double> roots = Core.findRoots(cur);
                for (double y : roots) {
                    temp.add(Pair.of(cur, y));
                }
            }
            return temp;
        };
    }
}
