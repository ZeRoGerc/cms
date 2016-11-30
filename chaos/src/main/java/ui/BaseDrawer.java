package ui;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.tools.javac.util.Pair;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.navigation.NavigationEvent;
import de.erichseifert.gral.navigation.NavigationListener;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.PointND;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
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
    protected abstract List<Pair<Double, Double>> generateInitialData();

    protected void onPlotConstraintsChanged(double x1, double y1, double x2, double y2) {
        // no-op
    }

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

        updatePoints(generateInitialData());
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

        Shape circle = new Ellipse2D.Double(0, 0, 5.0, 5.0);
        plot.getPointRenderers(data).get(0).setShape(circle);
        plot.getPointRenderers(data).get(0).setColor(Color.BLUE);

        panel.repaint();
    }

    private void initPlot() {
        plot.getNavigator().setZoomMax(Double.POSITIVE_INFINITY);
        plot.getNavigator().setZoomMin(Double.NEGATIVE_INFINITY);
        plot.getAxis(XYPlot.AXIS_X).setAutoscaled(false);
        plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(false);
        plot.getNavigator().addNavigationListener(new NavigationListener() {
            @Override
            public void centerChanged(@NotNull NavigationEvent<PointND<? extends Number>> navigationEvent) {
                onNewNavigationEventUpdated(1.0);
            }

            @Override
            public void zoomChanged(@NotNull NavigationEvent<Double> navigationEvent) {
                onNewNavigationEventUpdated(navigationEvent.getValueOld() / navigationEvent.getValueNew());
            }
        });
    }

    private void initPanel() {
        getContentPane().add(panel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(@NotNull MouseEvent e) {
                super.mouseClicked(e);
                Axis axisX = plot.getAxis(XYPlot.AXIS_X);
                Axis axisY = plot.getAxis(XYPlot.AXIS_Y);

                double x = getXCoordinateForBounds(e.getX(), axisX.getMin().doubleValue(), axisX.getMax().doubleValue());
                double y = getYCoordinateForBounds(e.getY(), axisY.getMin().doubleValue(), axisY.getMax().doubleValue());
                onMouseClick(x, y);
            }
        });
    }

    public double getXCoordinateForBounds(int pix, double left, double right) {
        double len = right - left;
        return left + len * ((double) pix / getContentPane().getWidth());
    }

    public double getYCoordinateForBounds(int pix, double left, double right) {
        double len = right - left;
        return right - len * ((double) pix / getContentPane().getHeight());
    }

    private void onNewNavigationEventUpdated(double multiplier) {
        Axis axisX = plot.getAxis(XYPlot.AXIS_X);
        Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
        double partX = (axisX.getMax().doubleValue() - axisX.getMin().doubleValue()) / 2;
        double partY = (axisY.getMax().doubleValue() - axisY.getMin().doubleValue()) / 2;
        onPlotConstraintsChanged(
                axisX.getMin().doubleValue() + partX - partX * multiplier,
                axisY.getMin().doubleValue() + partY + partY * multiplier,
                axisX.getMin().doubleValue() + partX + partX * multiplier,
                axisY.getMin().doubleValue() + partY - partY * multiplier
        );
    }
}
