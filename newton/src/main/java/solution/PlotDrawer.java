package solution;

import com.sun.istack.internal.NotNull;
import com.sun.tools.javac.util.Pair;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.navigation.NavigationEvent;
import de.erichseifert.gral.navigation.NavigationListener;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.PointND;
import org.jblas.ComplexDouble;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlotDrawer extends JFrame {

    private final int POINTS_IN_LINE = 100;

    private final PathFinder finder = new PathFinder();

    private final XYPlot plot = new XYPlot();

    private final InteractivePanel panel = new InteractivePanel(plot);

    private final DataTable firstRootPoints = new DataTable(Double.class, Double.class);

    private final DataTable secondRootPoints = new DataTable(Double.class, Double.class);

    private final DataTable thirdRootPoints = new DataTable(Double.class, Double.class);

    private DataTable pathData = new DataTable(Double.class, Double.class);

    private LineRenderer lineRenderer = new DefaultLineRenderer2D();

    @Nullable
    private SwingWorker<List<Pair<ComplexDouble, Root>>, Void> currentPointsUpdater;


    public PlotDrawer() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 600);

        init();
    }

    private void init() {
        List<Pair<ComplexDouble, Root>> points = getColoredPointsForConstraints(-10, -10, 10, 10);
        updateUI(points);

        setNavigation();
        setInteractivity();
    }

    private void setNavigation() {
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

    public void onNewNavigationEventUpdated(double multiplier) {
        Axis axisX = plot.getAxis(XYPlot.AXIS_X);
        Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
        double partX = (axisX.getMax().doubleValue() - axisX.getMin().doubleValue()) / 2;
        double partY = (axisY.getMax().doubleValue() - axisY.getMin().doubleValue()) / 2;
        updatedDataForConstraints(
                axisX.getMin().doubleValue() + partX - partX * multiplier,
                axisY.getMin().doubleValue() + partY + partY * multiplier,
                axisX.getMin().doubleValue() + partX + partX * multiplier,
                axisY.getMin().doubleValue() + partY - partY * multiplier
        );
    }

    private void setInteractivity() {
        getContentPane().add(panel);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Axis axisX = plot.getAxis(XYPlot.AXIS_X);
                Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
//                Number numberX = plot.getAxisRenderer(XYPlot.AXIS_X).viewToWorld(axisX, e.getX(), true);
//                Number numberY = plot.getAxisRenderer(XYPlot.AXIS_Y).viewToWorld(axisY, e.getY(), true);
                double X = getXCoordinateForBounds(e.getX(), axisX.getMin().doubleValue(), axisX.getMax().doubleValue());
                double Y = getYCoordinateForBounds(e.getY(), axisY.getMin().doubleValue(), axisY.getMax().doubleValue());
                drawPath(new ComplexDouble(X, Y));
                getContentPane().repaint();
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

    private void updatedDataForConstraints(double x1, double y1, double x2, double y2) {
        if (currentPointsUpdater != null && !currentPointsUpdater.isCancelled()) {
            currentPointsUpdater.cancel(true);
        }
        currentPointsUpdater = new SwingWorker<List<Pair<ComplexDouble, Root>>, Void>() {
            @Override
            protected List<Pair<ComplexDouble, Root>> doInBackground() throws Exception {
                return getColoredPointsForConstraints(x1, y1, x2, y2);
            }

            @Override
            protected void done() {
                try {
                    updateUI(get());
                } catch (InterruptedException | ExecutionException e) {
                    // Just ignore here. Because it's the case when we interrupt task ourself
                }
            }
        };
        currentPointsUpdater.execute();
    }

    @NotNull
    private List<Pair<ComplexDouble, Root>> getColoredPointsForConstraints(double x1, double y1, double x2, double y2) {
        List<ComplexDouble> points = getPointsForConstraints(x1, y1, x2, y2);
        List<Root> roots = finder.getRoots(points);

        List<Pair<ComplexDouble, Root>> result = new ArrayList<>(points.size());
        for (int i = 0; i < points.size(); i++) {
            result.add(new Pair<>(points.get(i), roots.get(i)));
        }
        return result;
    }

    private void updateUI(@NotNull List<Pair<ComplexDouble, Root>> newPoints) {
        plot.remove(firstRootPoints);
        plot.remove(secondRootPoints);
        plot.remove(thirdRootPoints);
        firstRootPoints.clear();
        secondRootPoints.clear();
        thirdRootPoints.clear();

        for (Pair<ComplexDouble, Root> cPoint : newPoints) {
            switch (cPoint.snd) {
                case FIRST:
                    firstRootPoints.add(cPoint.fst.real(), cPoint.fst.imag());
                    break;
                case SECOND:
                    secondRootPoints.add(cPoint.fst.real(), cPoint.fst.imag());
                    break;
                case THIRD:
                    thirdRootPoints.add(cPoint.fst.real(), cPoint.fst.imag());
                    break;
            }
        }

        plot.add(firstRootPoints);
        plot.add(secondRootPoints);
        plot.add(thirdRootPoints);

        plot.getPointRenderers(firstRootPoints).get(0).setColor(Color.RED);
        plot.getPointRenderers(secondRootPoints).get(0).setColor(Color.GREEN);
        plot.getPointRenderers(thirdRootPoints).get(0).setColor(Color.BLUE);
        panel.repaint();
    }

    @NotNull
    private List<ComplexDouble> getPointsForConstraints(double x1, double y1, double x2, double y2) {
        List<ComplexDouble> result = new ArrayList<>(POINTS_IN_LINE * POINTS_IN_LINE);
        double x, y;
        for (int i = 0; i < POINTS_IN_LINE; i++) {
            for (int j = 0; j < POINTS_IN_LINE; j++) {
                x = x1 + (x2 - x1) / POINTS_IN_LINE * i;
                y = y1 + (y2 - y1) / POINTS_IN_LINE * j;
                result.add(new ComplexDouble(x, y));
            }
        }
        return result;
    }

    private void drawPath(@NotNull ComplexDouble point) {
        plot.remove(pathData);
        pathData.clear();

        final List<ComplexDouble> result = finder.findPathForPoint(point);
        for (ComplexDouble temp : result) {
            pathData.add(temp.real(), temp.imag());
        }
        plot.add(pathData);
        plot.setLineRenderers(pathData, lineRenderer);
    }
}
