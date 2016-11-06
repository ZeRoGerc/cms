package example;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import org.jblas.ComplexDouble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.util.Collections.singletonList;

public class LinePlotExample extends JFrame {

    private ArrayList<ComplexDouble> allPoints = new ArrayList<>();

    private DataTable pathData = new DataTable(Double.class, Double.class);

    private DataTable dataCos = new DataTable(Double.class, Double.class);

    private DataTable dataSin = new DataTable(Double.class, Double.class);

    private LineRenderer lineRenderer = new DefaultLineRenderer2D();

    private XYPlot plot = new XYPlot();

    public LinePlotExample() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);

        fillWithData();
    }

    private void fillWithData() {
        for (double x = -5.0; x <= 5.0; x += 0.25) {
            double y = 5.0 * sin(x);
            dataSin.add(x, 5.0 * sin(x));
            dataCos.add(x, 5.0 * cos(x));
            allPoints.add(new ComplexDouble(x, 5.0 * sin(x)));
            allPoints.add(new ComplexDouble(x, 5.0 * cos(x)));
        }
        plot.add(dataSin);
        plot.add(dataCos);

        InteractivePanel panel = new InteractivePanel(plot);
        getContentPane().add(panel);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Axis axisX = plot.getAxis(XYPlot.AXIS_X);
                Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
                Number numberX = plot.getAxisRenderer(XYPlot.AXIS_X).viewToWorld(axisX, e.getX(), true);
                Number numberY = plot.getAxisRenderer(XYPlot.AXIS_Y).viewToWorld(axisY, e.getY(), true);
                double X = numberX.doubleValue();
                double Y = -numberY.doubleValue();
                drawPath(new ComplexDouble(X, Y));
                getContentPane().repaint();
            }
        });

        LineRenderer lines = new DefaultLineRenderer2D();
        plot.setLineRenderers(dataSin, singletonList(lines));
        plot.setLineRenderers(dataCos, singletonList(lines));

        Color red = new Color(0.9f, 0.1f, 0.2f);
        Color green = new Color(0.2f, 0.9f, 0.1f);
        Color blue = new Color(0.2f, 0.1f, 0.9f);

        plot.getPointRenderers(dataSin).get(0).setColor(green);
        plot.getPointRenderers(dataCos).get(0).setColor(blue);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Axis axisX = plot.getAxis(XYPlot.AXIS_X);
                Axis axisY = plot.getAxis(XYPlot.AXIS_Y);
                Number numberX = plot.getAxisRenderer(XYPlot.AXIS_X).viewToWorld(axisX, e.getX(), true);
                Number numberY = plot.getAxisRenderer(XYPlot.AXIS_Y).viewToWorld(axisY, e.getY(), true);
                double X = numberX.doubleValue();
                double Y = -numberY.doubleValue();
                drawPath(new ComplexDouble(X, Y));
                getContentPane().repaint();
            }
        });
    }

    private void drawPath(ComplexDouble point) {
        plot.remove(pathData);

        final ComplexDouble nearby = findNearest(point);
        pathData.clear();
        pathData.add(nearby.real(), nearby.imag());
        pathData.add(0.0, 0.0);
        plot.add(pathData);
        plot.setLineRenderers(pathData, lineRenderer);
    }

    private ComplexDouble findNearest(ComplexDouble point) {
        double dist = 1000;
        ComplexDouble result = point;
        for (ComplexDouble temp : allPoints) {
            ComplexDouble t = point.sub(temp);
            double curDist = t.real() * t.real() + t.imag() * t.imag();
            if (curDist < dist) {
                dist = curDist;
                result = temp;
            }
        }
        return result;
    }
}
