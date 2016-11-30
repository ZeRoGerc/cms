package ui;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.tools.javac.util.Pair;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class BaseDrawer extends JFrame {

    @NotNull
    protected final XYPlot plot = new XYPlot();

    @NotNull
    protected final InteractivePanel panel = new InteractivePanel(plot);

    @Nullable
    private LineRenderer lineRenderer;

    @NotNull
    private DataTable data = new DataTable(Double.class, Double.class);

    @NotNull
    protected abstract List<Pair<Double, Double>> generateData();

    protected void onMouseClick(double x, double y) {
        // no-op
    }

    public BaseDrawer() {
    }

    public BaseDrawer(@NotNull LineRenderer lineRenderer) {
        this.lineRenderer = lineRenderer;
    }

    public void init() {
        setSize(600, 600);

        updatePoints(generateData());

        initPlot();
        initPanel();
    }

    protected void updatePoints(@NotNull List<Pair<Double, Double>> points) {
        plot.remove(data);
        data.clear();

        for (Pair<Double, Double> point : points) {
            data.add(point.fst, point.snd);
        }

        plot.add(data);
        for (LineRenderer renderer : plot.getLineRenderers(data)) {
            renderer.setColor(Color.CYAN);
        }

        if (lineRenderer != null) {
            plot.setLineRenderers(data, lineRenderer);
        }

        plot.getPointRenderers(data).get(0).setColor(Color.BLUE);

        panel.repaint();
    }

    private void initPlot() {
        plot.getNavigator().setZoomMax(Double.POSITIVE_INFINITY);
        plot.getNavigator().setZoomMin(Double.NEGATIVE_INFINITY);
        plot.getAxis(XYPlot.AXIS_X).setAutoscaled(false);
        plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(false);
    }

    private void initPanel() {
        getContentPane().add(panel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Axis axisX = plot.getAxis(XYPlot.AXIS_X);
                Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
                Number numberX = plot.getAxisRenderer(XYPlot.AXIS_X).viewToWorld(axisX, e.getX(), true);
                Number numberY = plot.getAxisRenderer(XYPlot.AXIS_Y).viewToWorld(axisY, e.getY(), true);
                double x = numberX.doubleValue();
                double y = numberY.doubleValue();
                onMouseClick(x, y);
            }
        });
    }
}
