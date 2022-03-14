package org.jfree.chart.axis;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.Args;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.List;

import static org.jfree.chart.axis.NumberAxis.DEFAULT_TICK_UNIT;

public abstract class NumberLogCommon extends ValueAxis {

    /** The tick unit for the axis. */
    protected TickUnit tickUnit;

    /**
     * Constructs a value axis.
     *
     * @param label             the axis label ({@code null} permitted).
     * @param standardTickUnits the source for standard tick units
     *                          ({@code null} permitted).
     */
    protected NumberLogCommon(String label, TickUnitSource standardTickUnits) {
        super(label, standardTickUnits);
        this.tickUnit = DEFAULT_TICK_UNIT;
    }

    /**
     * Returns the current tick unit.
     *
     * @return The current tick unit.
     *
     * @see #setTickUnit(NumberTickUnit)
     */
    public TickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Sets the tick unit for the axis and sends an {@link AxisChangeEvent} to
     * all registered listeners.  A side effect of calling this method is that
     * the "auto-select" feature for tick units is switched off (you can
     * restore it using the {@link ValueAxis#setAutoTickUnitSelection(boolean)}
     * method).
     *
     * @param unit  the new tick unit ({@code null} not permitted).
     *
     * @see #getTickUnit()
     */
    public void setTickUnit(NumberTickUnit unit) {
        // defer argument checking...
        setTickUnit(unit, true, true);
    }

    /**
     * Sets the tick unit for the axis and, if requested, sends an
     * {@link AxisChangeEvent} to all registered listeners.  In addition, an
     * option is provided to turn off the "auto-select" feature for tick units
     * (you can restore it using the
     * {@link ValueAxis#setAutoTickUnitSelection(boolean)} method).
     *
     * @param unit  the new tick unit ({@code null} not permitted).
     * @param notify  notify listeners?
     * @param turnOffAutoSelect  turn off the auto-tick selection?
     *
     * @see #getTickUnit()
     */
    public void setTickUnit(NumberTickUnit unit, boolean notify,
                            boolean turnOffAutoSelect) {
        Args.nullNotPermitted(unit, "unit");
        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axes and data should be drawn
     *                  ({@code null} not permitted).
     * @param dataArea  the area within which the data should be drawn
     *                  ({@code null} not permitted).
     * @param edge  the location of the axis ({@code null} not permitted).
     * @param plotState  collects information about the plot
     *                   ({@code null} permitted).
     *
     * @return The axis state (never {@code null}).
     */
    @Override
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea,
                          Rectangle2D dataArea, RectangleEdge edge,
                          PlotRenderingInfo plotState) {

        AxisState state;
        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            state = new AxisState(cursor);
            // even though the axis is not visible, we need ticks for the
            // gridlines...
            List ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }

        // draw the tick marks and labels...
        state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);

        if (getAttributedLabel() != null) {
            state = drawAttributedLabel(getAttributedLabel(), g2, plotArea,
                    dataArea, edge, state);

        } else {
            state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
        }
        createAndAddEntity(cursor, state, dataArea, edge, plotState);
        return state;

    }

    /**
     * Estimates the maximum tick label height.
     *
     * @param g2  the graphics device.
     *
     * @return The maximum height.
     *
     * @since 1.0.7
     */
    protected double estimateMaximumTickLabelHeight(Graphics2D g2) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();

        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        result += tickLabelFont.getLineMetrics("123", frc).getHeight();
        return result;
    }
}
