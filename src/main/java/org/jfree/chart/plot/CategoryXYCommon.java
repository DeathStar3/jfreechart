package org.jfree.chart.plot;

import org.jfree.chart.axis.*;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.Args;
import org.jfree.data.general.DatasetChangeEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

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
     * The stroke used to draw the range minor grid-lines.
     *
     * @since 1.0.13
     */
    protected transient Stroke rangeMinorGridlineStroke;
    /**
     * The paint used to draw the range minor grid-lines.
     *
     * @since 1.0.13
     */
    protected transient Paint rangeMinorGridlinePaint;
    /**
     * A flag that controls whether the grid-lines for the range axis are
     * visible.
     */
    protected boolean rangeGridlinesVisible;

    /** A map containing lists of markers for the domain axes. */
    protected Map foregroundDomainMarkers;

    /** A map containing lists of markers for the domain axes. */
    protected Map backgroundDomainMarkers;

    /** A map containing lists of markers for the range axes. */
    protected Map foregroundRangeMarkers;

    /** A map containing lists of markers for the range axes. */
    protected Map backgroundRangeMarkers;

    /**
     * A flag that controls whether or not gridlines are shown for the minor
     * tick values on the primary range axis.
     *
     * @since 1.0.13
     */
    protected boolean rangeMinorGridlinesVisible;

    /** The stroke used to draw the range axis grid-lines. */
    protected transient Stroke rangeGridlineStroke;

    /** The paint used to draw the range axis grid-lines. */
    protected transient Paint rangeGridlinePaint;


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

    /**
     * Returns the stroke used to draw the grid-lines against the range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeGridlineStroke(Stroke)
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke used to draw the grid-lines against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getRangeGridlineStroke()
     */
    public void setRangeGridlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.rangeGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to draw the grid-lines against the range axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeGridlinePaint(Paint)
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint used to draw the grid lines against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getRangeGridlinePaint()
     */
    public void setRangeGridlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the range axis minor grid is visible, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setRangeMinorGridlinesVisible(boolean)
     *
     * @since 1.0.13
     */
    public boolean isRangeMinorGridlinesVisible() {
        return this.rangeMinorGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not the range axis minor grid
     * lines are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isRangeMinorGridlinesVisible()
     *
     * @since 1.0.13
     */
    public void setRangeMinorGridlinesVisible(boolean visible) {
        if (this.rangeMinorGridlinesVisible != visible) {
            this.rangeMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the minor grid lines (if any) plotted against the
     * range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeMinorGridlineStroke(Stroke)
     *
     * @since 1.0.13
     */
    public Stroke getRangeMinorGridlineStroke() {
        return this.rangeMinorGridlineStroke;
    }

    /**
     * Sets the stroke for the minor grid lines plotted against the range axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getRangeMinorGridlineStroke()
     *
     * @since 1.0.13
     */
    public void setRangeMinorGridlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.rangeMinorGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the minor grid lines (if any) plotted against the
     * range axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeMinorGridlinePaint(Paint)
     *
     * @since 1.0.13
     */
    public Paint getRangeMinorGridlinePaint() {
        return this.rangeMinorGridlinePaint;
    }

    /**
     * Sets the paint for the minor grid lines plotted against the range axis
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getRangeMinorGridlinePaint()
     *
     * @since 1.0.13
     */
    public void setRangeMinorGridlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeMinorGridlinePaint = paint;
        fireChangeEvent();
    }


    /**
     * Clears all the domain markers for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @see #clearRangeMarkers()
     */
    public void clearDomainMarkers() {
        if (this.backgroundDomainMarkers != null) {
            Set keys = this.backgroundDomainMarkers.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                clearDomainMarkers(key.intValue());
            }
            this.backgroundDomainMarkers.clear();
        }
        if (this.foregroundDomainMarkers != null) {
            Set keys = this.foregroundDomainMarkers.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Integer key = (Integer) iterator.next();
                clearDomainMarkers(key.intValue());
            }
            this.foregroundDomainMarkers.clear();
        }
        fireChangeEvent();
    }
    /**
     * Clears all the domain markers for the specified renderer.
     *
     * @param index  the renderer index.
     *
     * @see #clearRangeMarkers(int)
     */
    public void clearDomainMarkers(int index) {
        Integer key = new Integer(index);
        if (this.backgroundDomainMarkers != null) {
            Collection markers
                    = (Collection) this.backgroundDomainMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundDomainMarkers != null) {
            Collection markers
                    = (Collection) this.foregroundDomainMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    /**
     * Adds a marker for display by a particular renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a domain axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @since 1.0.10
     *
     * @see #removeDomainMarker(int, Marker, Layer, boolean)
     */
    public void addDomainMarker(int index, CategoryMarker marker, Layer layer,
                                boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Args.nullNotPermitted(layer, "layer");
        Collection markers;
        if (layer == Layer.FOREGROUND) {
            markers = (Collection) this.foregroundDomainMarkers.get(
                    new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.foregroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            markers = (Collection) this.backgroundDomainMarkers.get(
                    new Integer(index));
            if (markers == null) {
                markers = new java.util.ArrayList();
                this.backgroundDomainMarkers.put(new Integer(index), markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }


    /**
     * Returns the list of domain markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of domain markers.
     */
    public Collection getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    /**
     * Returns a collection of domain markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
     */
    public Collection getDomainMarkers(int index, Layer layer) {
        Collection result = null;
        Integer key = new Integer(index);
        if (layer == Layer.FOREGROUND) {
            result = (Collection) this.foregroundDomainMarkers.get(key);
        }
        else if (layer == Layer.BACKGROUND) {
            result = (Collection) this.backgroundDomainMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    /**
     * Removes a marker for the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param marker  the marker.
     *
     * @return A boolean indicating whether or not the marker was actually
     *         removed.
     *
     * @since 1.0.7
     */
    public boolean removeDomainMarker(Marker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Removes a marker for the domain axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether or not the marker was actually
     *         removed.
     *
     * @since 1.0.7
     */
    public boolean removeDomainMarker(Marker marker, Layer layer) {
        return removeDomainMarker(0, marker, layer);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker.
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether or not the marker was actually
     *         removed.
     *
     * @since 1.0.7
     */
    public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker.
     * @param layer the layer (foreground or background).
     * @param notify  notify listeners?
     *
     * @return A boolean indicating whether or not the marker was actually
     *         removed.
     *
     * @since 1.0.10
     */
    public boolean removeDomainMarker(int index, Marker marker, Layer layer,
                                      boolean notify) {
        ArrayList markers;
        if (layer == Layer.FOREGROUND) {
            markers = (ArrayList) this.foregroundDomainMarkers.get(new Integer(
                    index));
        } else {
            markers = (ArrayList) this.backgroundDomainMarkers.get(new Integer(
                    index));
        }
        if (markers == null) {
            return false;
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    /**
     * Adds a marker for display (in the foreground) against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners. Typically a
     * marker will be drawn by the renderer as a line perpendicular to the
     * range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #removeRangeMarker(Marker)
     */
    public void addRangeMarker(Marker marker) {
        addRangeMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for display against the range axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.  Typically a marker
     * will be drawn by the renderer as a line perpendicular to the range axis,
     * however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background) ({@code null}
     *               not permitted).
     *
     * @see #removeRangeMarker(Marker, Layer)
     */
    public void addRangeMarker(Marker marker, Layer layer) {
        addRangeMarker(0, marker, layer);
    }

    /**
     * Adds a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     *
     * @see #clearRangeMarkers(int)
     * @see #addDomainMarker(int, Marker, Layer)
     */
    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, true);
    }

    protected abstract void addRangeMarker(int index, Marker marker, Layer layer, boolean notify);

    /**
     * Clears all the range markers and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @see #clearRangeMarkers()
     */
    public void clearRangeMarkers() {
        if (this.backgroundRangeMarkers != null) {
            Set<Integer> keys = this.backgroundRangeMarkers.keySet();
            for (Integer key : keys) {
                clearRangeMarkers(key);
            }
            this.backgroundRangeMarkers.clear();
        }
        if (this.foregroundRangeMarkers != null) {
            Set<Integer> keys = this.foregroundRangeMarkers.keySet();
            for (Integer key : keys) {
                clearRangeMarkers(key);
            }
            this.foregroundRangeMarkers.clear();
        }
        fireChangeEvent();
    }

    /**
     * Clears all the range markers for the specified renderer.
     *
     * @param index  the renderer index.
     *
     * @see #clearDomainMarkers(int)
     */
    public void clearRangeMarkers(int index) {
        Integer key = new Integer(index);
        if (this.backgroundRangeMarkers != null) {
            Collection markers
                    = (Collection) this.backgroundRangeMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            Collection markers
                    = (Collection) this.foregroundRangeMarkers.get(key);
            if (markers != null) {
                Iterator iterator = markers.iterator();
                while (iterator.hasNext()) {
                    Marker m = (Marker) iterator.next();
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    /**
     * Removes a marker for the range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param marker the marker.
     *
     * @return A boolean indicating whether or not the marker was actually
     *         removed.
     *
     * @since 1.0.7
     *
     * @see #addRangeMarker(Marker)
     */
    public boolean removeRangeMarker(Marker marker) {
        return removeRangeMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Removes a marker for the range axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether or not the marker was actually
     *         removed.
     *
     * @since 1.0.7
     *
     * @see #addRangeMarker(Marker, Layer)
     */
    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return removeRangeMarker(0, marker, layer);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker.
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether or not the marker was actually
     *         removed.
     *
     * @since 1.0.7
     *
     * @see #addRangeMarker(int, Marker, Layer)
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return removeRangeMarker(index, marker, layer, true);
    }

    protected abstract boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify);

    /**
     * Returns {@code true} if the range axis grid is visible, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setRangeGridlinesVisible(boolean)
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not grid-lines are drawn against
     * the range axis.  If the flag changes value, a {@link PlotChangeEvent} is
     * sent to all registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isRangeGridlinesVisible()
     */
    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            fireChangeEvent();
        }
    }
}
