package org.jfree.chart.plot;

import org.jfree.chart.axis.*;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.general.DatasetChangeEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class CategoryXYCommon extends Plot {


    /** Storage for the range axes. */
    protected Map<Integer, ValueAxis> rangeAxes;

    /** Storage for the range axis locations. */
    protected Map<Integer, AxisLocation> rangeAxisLocations;


    /** The offset between the data area and the axes. */
    protected RectangleInsets axisOffset;

    /** The plot orientation. */
    protected PlotOrientation orientation;

    /** Storage for the domain axis locations. */
    protected Map<Integer, AxisLocation> domainAxisLocations;

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if there
     * is a parent plot).
     *
     * @return The range axis (possibly {@code null}).
     */
    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    public abstract ValueAxis getRangeAxis(int index);
    public abstract void setRangeAxis(ValueAxis axis);


    /**
     * Returns the number of range axes.
     *
     * @return The axis count.
     */
    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }


    /**
     * Clears the range axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     */
    public void clearRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the range axes.
     */
    public void configureRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.configure();
            }
        }
    }


    protected void dealWithEventWhenDatasetChanges(DatasetChangeEvent event) {
        if (getParent() != null) {
            getParent().datasetChanged(event);
        } else {
            PlotChangeEvent e = new PlotChangeEvent(this);
            e.setType(ChartChangeEventType.DATASET_UPDATED);
            notifyListeners(e);
        }
    }

    /**
     * Sets a range axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     * @param notify  notify listeners?
     */
    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = this.rangeAxes.get(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.rangeAxes.put(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     *
     * @see #getRangeAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation, boolean)
     */
    public void setRangeAxisLocation(int index, AxisLocation location) {
        setRangeAxisLocation(index, location, true);
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     * @param notify  notify listeners?
     *
     * @see #getRangeAxisLocation(int)
     * @see #setDomainAxisLocation(int, AxisLocation, boolean)
     */
    public void setRangeAxisLocation(int index, AxisLocation location,
                                     boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException(
                    "Null 'location' for index 0 not permitted.");
        }
        this.rangeAxisLocations.put(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }
    /**
     * Sets the location of the range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #setRangeAxisLocation(AxisLocation, boolean)
     * @see #setDomainAxisLocation(AxisLocation)
     */
    public void setRangeAxisLocation(AxisLocation location) {
        // defer argument checking...
        setRangeAxisLocation(location, true);
    }

    /**
     * Sets the location of the range axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #setDomainAxisLocation(AxisLocation, boolean)
     */
    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        setRangeAxisLocation(0, location, notify);
    }

    /**
     * Returns the location for a range axis.
     *
     * @param index  the axis index.
     *
     * @return The location.
     *
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = this.rangeAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getRangeAxisLocation(0));
        }
        return result;
    }

    protected int findRangeAxisIndex(ValueAxis axis) {
        for (Map.Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the range axis location.
     *
     * @return The location (never {@code null}).
     */
    public AxisLocation getRangeAxisLocation() {
        return getRangeAxisLocation(0);
    }


    /**
     * Sets the location of the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param location  the axis location ({@code null} not permitted).
     *
     * @see #getDomainAxisLocation()
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public void setDomainAxisLocation(AxisLocation location) {
        // delegate...
        setDomainAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the axis location ({@code null} not permitted).
     * @param notify  a flag that controls whether listeners are notified.
     */
    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        // delegate...
        setDomainAxisLocation(0, location, notify);
    }


    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     *
     * @see #getDomainAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public void setDomainAxisLocation(int index, AxisLocation location) {
        // delegate...
        setDomainAxisLocation(index, location, true);
    }

    /**
     * Returns the location for a domain axis.  If this hasn't been set
     * explicitly, the method returns the location that is opposite to the
     * primary domain axis location.
     *
     * @param index  the axis index (must be &gt;= 0).
     *
     * @return The location (never {@code null}).
     *
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = this.domainAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getDomainAxisLocation());
        }
        return result;
    }


    /**
     * Returns the domain axis location for the primary domain axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #getRangeAxisLocation()
     */
    public AxisLocation getDomainAxisLocation() {
        return getDomainAxisLocation(0);
    }

    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     * @param notify  notify listeners?
     *
     * @since 1.0.5
     *
     * @see #getDomainAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation, boolean)
     */
    public void setDomainAxisLocation(int index, AxisLocation location,
                                      boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException(
                    "Null 'location' for index 0 not permitted.");
        }
        this.domainAxisLocations.put(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    protected AxisSpace reserveSpaceForTheRangeAxes(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                int i = findRangeAxisIndex(yAxis);
                RectangleEdge edge = getRangeAxisEdge(i);
                space = yAxis.reserveSpace(g2, this, plotArea, edge, space);
            }
        }
        return space;
    }

    protected abstract RectangleEdge getRangeAxisEdge(int i);


    protected void addRangesToList(AxisCollection axisCollection) {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                int index = findRangeAxisIndex(axis);
                axisCollection.add(axis, getRangeAxisEdge(index));
            }
        }
    }

    protected Map<Axis, AxisState> extractedDraw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState, AxisCollection axisCollection) {
        // add range axes to lists...
        addRangesToList(axisCollection);

        Map<Axis, AxisState> axisStateMap = new HashMap<Axis, AxisState>();

        // draw the top axes
        Iterator iterator = axisCollection.getAxesAtTop().iterator();
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        drawTheAxes(iterator, cursor, g2, plotArea, dataArea, plotState, axisStateMap, RectangleEdge.TOP);

        // draw the bottom axes
        iterator = axisCollection.getAxesAtBottom().iterator();
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        drawTheAxes(iterator, cursor, g2, plotArea, dataArea, plotState, axisStateMap, RectangleEdge.BOTTOM);

        // draw the left axes
        iterator = axisCollection.getAxesAtLeft().iterator();
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        drawTheAxes(iterator, cursor, g2, plotArea, dataArea, plotState, axisStateMap, RectangleEdge.LEFT);

        // draw the right axes
        iterator = axisCollection.getAxesAtRight().iterator();
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        drawTheAxes(iterator, cursor, g2, plotArea, dataArea, plotState, axisStateMap, RectangleEdge.RIGHT);

        return axisStateMap;
    }


    private void drawTheAxes(Iterator iterator, double cursor, Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState, Map<Axis, AxisState> axisStateMap, RectangleEdge rectangleEdge) {
        while (iterator.hasNext()) {
            Axis axis = (Axis) iterator.next();
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea,
                        rectangleEdge, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
    }

    protected void trimAxisOffset(Rectangle2D dataArea) {
        this.axisOffset.trim(dataArea);
    }

    protected void zoomRangeAxesWithFactor(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        // perform the zoom on each range axis
        for (ValueAxis rangeAxis : this.rangeAxes.values()) {
            if (rangeAxis == null) {
                continue;
            }
            if (useAnchor) {
                // get the relevant source coordinate given the plot orientation
                double sourceY = source.getY();
                if (this.orientation.isHorizontal()) {
                    sourceY = source.getX();
                }
                double anchorY = rangeAxis.java2DToValue(sourceY,
                        info.getDataArea(), getRangeAxisEdge());
                rangeAxis.resizeRange2(factor, anchorY);
            } else {
                rangeAxis.resizeRange(factor);
            }
        }
    }

    protected abstract RectangleEdge getRangeAxisEdge();

    protected void zoomRangeAxesDefault(double lowerPercent, double upperPercent) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

}
