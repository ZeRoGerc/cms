package ui;

import com.sun.istack.internal.NotNull;
import com.sun.tools.javac.util.Pair;
import logic.RootFinder;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChaosDrawer extends BaseDrawer {

    private static final double MINIMAL = -2;

    private static final double MAXIMUM = 4;

    private static final double STEPS = 1000;

    private final RootFinder finder = new RootFinder();

    @Nullable
    private SwingWorker<List<Pair<Double, Double>>, Void> currentPointsUpdater;

    public ChaosDrawer() {
        super();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    @NotNull
    protected List<Pair<Double, Double>> generateInitialData() {
        return finder.getRootsForInterval(MINIMAL, MAXIMUM, (MAXIMUM - MINIMAL) / STEPS);
    }

    @Override
    protected void onPlotConstraintsChanged(double x1, double y1, double x2, double y2) {
        if (currentPointsUpdater != null && !currentPointsUpdater.isCancelled()) {
            currentPointsUpdater.cancel(true);
        }

        currentPointsUpdater = new SwingWorker<List<Pair<Double, Double>>, Void>() {
            @Override
            protected List<Pair<Double, Double>> doInBackground() throws Exception {
                double left = Math.max(MINIMAL, x1);
                double right = Math.min(MAXIMUM, x2);

                return finder.getRootsForInterval(left, right, (right - left) / STEPS);
            }

            @Override
            protected void done() {
                try {
                    updatePoints(get());
                } catch (InterruptedException | ExecutionException e) {
                    finder.clear();
                }
            }
        };
        currentPointsUpdater.execute();
    }

    @Override
    protected void onMouseClick(double x, double y) {
        if (y > 0 && y < 1) {
            BaseDrawer series = new SeriesDrawer(y, x);
            series.init();
            series.setTitle("Series for r = " + String.valueOf(x) + ", start = " + String.valueOf(y));
            series.setVisible(true);
        }
    }
}
