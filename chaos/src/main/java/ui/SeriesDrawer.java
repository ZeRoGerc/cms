package ui;

import com.sun.tools.javac.util.Pair;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import logic.Core;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SeriesDrawer extends BaseDrawer {

    double start;

    double r;

    public SeriesDrawer(double start, double r) {
        super(new DefaultLineRenderer2D());
        this.start = start;
        this.r = r;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    @Override
    protected List<Pair<Double, Double>> generateInitialData() {
        List<Double> roots = Core.findConvergeSeries(start, r);
        List<Pair<Double, Double>> result = new ArrayList<>(roots.size());

        for (int i = 0; i < roots.size(); i++) {
            result.add(Pair.of((double) i, roots.get(i)));
        }

        return result;
    }
}
