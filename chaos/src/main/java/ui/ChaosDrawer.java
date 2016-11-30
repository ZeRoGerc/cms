package ui;

import com.sun.istack.internal.NotNull;
import com.sun.tools.javac.util.Pair;
import logic.Core;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ChaosDrawer extends BaseDrawer {

    public ChaosDrawer() {
        super();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    @NotNull
    protected List<Pair<Double, Double>> generateData() {
        List<Pair<Double, Double>> result = new ArrayList<>();

        double x = -2;
        double step = 1e-2;

        while (x < 4.) {
            List<Double> yCoordinates = Core.findRoots(x);
            for (double y : yCoordinates) {
                result.add(Pair.of(x, y));
            }
            x += step;
        }

        return result;
    }

    @Override
    protected void onMouseClick(double x, double y) {
        BaseDrawer series = new SeriesDrawer(x);
        series.init();
        series.setTitle("Series for r = " + String.valueOf(x));
        series.setVisible(true);
    }
}
