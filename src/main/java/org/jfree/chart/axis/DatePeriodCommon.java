package org.jfree.chart.axis;

import org.jfree.chart.ui.RectangleEdge;

import java.awt.geom.Rectangle2D;

public abstract class DatePeriodCommon extends ValueAxis {

    /**
     * Constructs a value axis.
     *
     * @param label             the axis label ({@code null} permitted).
     * @param standardTickUnits the source for standard tick units
     *                          ({@code null} permitted).
     */
    protected DatePeriodCommon(String label, TickUnitSource standardTickUnits) {
        super(label, standardTickUnits);
    }

    protected double getCoordinates(double value, Rectangle2D area, RectangleEdge edge, double axisMin, double axisMax) {
        double result = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            if (isInverted()) {
                result = maxX + ((value - axisMin) / (axisMax - axisMin))
                        * (minX - maxX);
            }
            else {
                result = minX + ((value - axisMin) / (axisMax - axisMin))
                        * (maxX - minX);
            }
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            double v = ((value - axisMin) / (axisMax - axisMin))
                    * (maxY - minY);
            if (isInverted()) {
                result = minY + v;
            }
            else {
                result = maxY - v;
            }
        }
        return result;
    }

}
