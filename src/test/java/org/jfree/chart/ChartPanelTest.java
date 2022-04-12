/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2016, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -------------------
 * ChartPanelTest.java
 * -------------------
 * (C) Copyright 2004-2016, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 13-Jul-2004 : Version 1 (DG);
 * 12-Jan-2009 : Added test2502355() (DG);
 * 08-Jun-2009 : Added testSetMouseWheelEnabled() (DG);
 */

package org.jfree.chart;

import static org.jfree.chart.ChartPanel.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.CaretListener;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.*;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.junit.Test;

/**
 * Tests for the {@link ChartPanel} class.
 */
public class ChartPanelTest implements ChartChangeListener, ChartMouseListener {

    private List chartChangeEvents = new java.util.ArrayList();

    /**
     * Receives a chart change event and stores it in a list for later
     * inspection.
     *
     * @param event the event.
     */
    @Override
    public void chartChanged(ChartChangeEvent event) {
        this.chartChangeEvents.add(event);
    }

    /**
     * Test that the constructor will accept a null chart.
     */
    @Test
    public void testConstructor1() {
        ChartPanel panel = new ChartPanel(null);
        assertEquals(null, panel.getChart());
    }

    /**
     * Test that it is possible to set the panel's chart to null.
     */
    @Test
    public void testSetChart() {
        JFreeChart chart = new JFreeChart(new XYPlot());
        ChartPanel panel = new ChartPanel(chart);
        panel.setChart(null);
        assertEquals(null, panel.getChart());
    }

    /**
     * Check the behaviour of the getListeners() method.
     */
    @Test
    public void testGetListeners() {
        ChartPanel p = new ChartPanel(null);
        p.addChartMouseListener(this);
        EventListener[] listeners = p.getListeners(ChartMouseListener.class);
        assertEquals(1, listeners.length);
        assertEquals(this, listeners[0]);
        // try a listener type that isn't registered
        listeners = p.getListeners(CaretListener.class);
        assertEquals(0, listeners.length);
        p.removeChartMouseListener(this);
        listeners = p.getListeners(ChartMouseListener.class);
        assertEquals(0, listeners.length);

        // try a null argument
        boolean pass = false;
        try {
            listeners = p.getListeners((Class) null);
        } catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);

        // try a class that isn't a listener
        pass = false;
        try {
            listeners = p.getListeners(Integer.class);
        } catch (ClassCastException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    /**
     * Ignores a mouse click event.
     *
     * @param event the event.
     */
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // ignore
    }

    /**
     * Ignores a mouse move event.
     *
     * @param event the event.
     */
    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        // ignore
    }

    /**
     * Checks that a call to the zoom() method generates just one
     * ChartChangeEvent.
     */
    @Test
    public void test2502355_zoom() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoom(new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the zoomInBoth() method generates just one
     * ChartChangeEvent.
     */
    @Test
    public void test2502355_zoomInBoth() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomInBoth(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the zoomOutBoth() method generates just one
     * ChartChangeEvent.
     */
    @Test
    public void test2502355_zoomOutBoth() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomOutBoth(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the restoreAutoBounds() method generates just one
     * ChartChangeEvent.
     */
    @Test
    public void test2502355_restoreAutoBounds() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.restoreAutoBounds();
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the zoomInDomain() method, for a plot with more
     * than one domain axis, generates just one ChartChangeEvent.
     */
    @Test
    public void test2502355_zoomInDomain() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomInDomain(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the zoomInRange() method, for a plot with more
     * than one range axis, generates just one ChartChangeEvent.
     */
    @Test
    public void test2502355_zoomInRange() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomInRange(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the zoomOutDomain() method, for a plot with more
     * than one domain axis, generates just one ChartChangeEvent.
     */
    @Test
    public void test2502355_zoomOutDomain() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomOutDomain(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the zoomOutRange() method, for a plot with more
     * than one range axis, generates just one ChartChangeEvent.
     */
    @Test
    public void test2502355_zoomOutRange() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomOutRange(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the restoreAutoDomainBounds() method, for a plot
     * with more than one range axis, generates just one ChartChangeEvent.
     */
    @Test
    public void test2502355_restoreAutoDomainBounds() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.restoreAutoDomainBounds();
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * Checks that a call to the restoreAutoRangeBounds() method, for a plot
     * with more than one range axis, generates just one ChartChangeEvent.
     */
    @Test
    public void test2502355_restoreAutoRangeBounds() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.restoreAutoRangeBounds();
        assertEquals(1, this.chartChangeEvents.size());
    }

    /**
     * In version 1.0.13 there is a bug where enabling the mouse wheel handler
     * twice would in fact disable it.
     */
    @Test
    public void testSetMouseWheelEnabled() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        assertTrue(panel.isMouseWheelEnabled());
        panel.setMouseWheelEnabled(true);
        assertTrue(panel.isMouseWheelEnabled());
        panel.setMouseWheelEnabled(false);
        assertFalse(panel.isMouseWheelEnabled());
    }

    @Test
    public void testGetToolTipText() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        assertEquals(null, panel.getToolTipText(null));
        // TODO test with a real MouseEvent
    }

    @Test
    public void testScale() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        // scale is 0.0 by default and is only set by paintComponent
        BufferedImage bi = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        panel.setSize(300, 200);
        panel.paintComponent(g2);
        // Insets are (0,0,0,0)
        assertEquals(1.0, panel.getScaleX(), 0.1);
        assertEquals(1.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setSize(150, 50);
        panel.paintComponent(g2);
        assertEquals(0.5, panel.getScaleX(), 0.1);
        assertEquals(0.25, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(5.0, 5.0, 15.0, 10.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setMaximumDrawWidth(500);
        panel.setSize(600, 400);
        panel.paintComponent(g2);
        assertEquals(1.2, panel.getScaleX(), 0.1);
        assertEquals(1.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(12.0, 20.0, 36.0, 40.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setMaximumDrawHeight(200);
        panel.setSize(600, 400);
        panel.paintComponent(g2);
        assertEquals(1.2, panel.getScaleX(), 0.1);
        assertEquals(2.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(12.0, 40.0, 36.0, 80.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        g2.dispose();
    }

    /**
     * Same test as the previous one, but specifying no buffer to check that it has the same behaviour.
     */
    @Test
    public void testPaintComponentNoBuffer() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart, false);
        // scale is 0.0 by default and is only set by paintComponent
        BufferedImage bi = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        panel.setSize(300, 200);
        panel.paintComponent(g2);
        // Insets are (0,0,0,0)
        assertEquals(1.0, panel.getScaleX(), 0.1);
        assertEquals(1.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setSize(150, 50);
        panel.paintComponent(g2);
        assertEquals(0.5, panel.getScaleX(), 0.1);
        assertEquals(0.25, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(5.0, 5.0, 15.0, 10.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setMaximumDrawWidth(500);
        panel.setSize(600, 400);
        panel.paintComponent(g2);
        assertEquals(1.2, panel.getScaleX(), 0.1);
        assertEquals(1.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(12.0, 20.0, 36.0, 40.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setMaximumDrawHeight(200);
        panel.setSize(600, 400);
        panel.paintComponent(g2);
        assertEquals(1.2, panel.getScaleX(), 0.1);
        assertEquals(2.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(12.0, 40.0, 36.0, 80.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        g2.dispose();
    }

    @Test
    public void testPaintComponentNullChart() {
        ChartPanel panel = new ChartPanel(null);
        // scale is 0.0 by default and is only set by paintComponent
        BufferedImage bi = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        panel.setSize(300, 200);
        panel.paintComponent(g2);

    }

    /**
     * Same test as the previous one, but specifying no buffer to check that it has the same behaviour.
     */
    @Test
    public void testPaintComponentWithOverlays() {
        CrosshairOverlay overlay = new CrosshairOverlay();
        overlay.addDomainCrosshair(new Crosshair());
        overlay.addRangeCrosshair(new Crosshair());
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart, false);
        panel.addOverlay(overlay);
        // scale is 0.0 by default and is only set by paintComponent
        BufferedImage bi = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        panel.setSize(300, 200);
        panel.paintComponent(g2);
        // Insets are (0,0,0,0)
        assertEquals(1.0, panel.getScaleX(), 0.1);
        assertEquals(1.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setSize(150, 50);
        panel.paintComponent(g2);
        assertEquals(0.5, panel.getScaleX(), 0.1);
        assertEquals(0.25, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(5.0, 5.0, 15.0, 10.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setMaximumDrawWidth(500);
        panel.setSize(600, 400);
        panel.paintComponent(g2);
        assertEquals(1.2, panel.getScaleX(), 0.1);
        assertEquals(1.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(12.0, 20.0, 36.0, 40.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        panel.setMaximumDrawHeight(200);
        panel.setSize(600, 400);
        panel.paintComponent(g2);
        assertEquals(1.2, panel.getScaleX(), 0.1);
        assertEquals(2.0, panel.getScaleY(), 0.1);
        assertEquals(new Rectangle2D.Double(12.0, 40.0, 36.0, 80.0),
                panel.scale(new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0)));
        g2.dispose();
    }

    @Test
    public void testActionPerformed() {
        final boolean[] plotChanged = {false};

        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        chart.getPlot().addChangeListener(new PlotChangeListener() {
            @Override
            public void plotChanged(PlotChangeEvent event) {
                plotChanged[0] = true;
            }
        });

        ChartPanel panel = new ChartPanel(chart);

        ActionEvent event = new ActionEvent(new Object(), 0, ZOOM_IN_BOTH_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        plotChanged[0] = false;
        event = new ActionEvent(new Object(), 0, ZOOM_IN_DOMAIN_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        plotChanged[0] = false;
        event = new ActionEvent(new Object(), 0, ZOOM_IN_RANGE_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        event = new ActionEvent(new Object(), 0, ZOOM_OUT_BOTH_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        plotChanged[0] = false;
        event = new ActionEvent(new Object(), 0, ZOOM_OUT_DOMAIN_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        plotChanged[0] = false;
        event = new ActionEvent(new Object(), 0, ZOOM_OUT_RANGE_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        event = new ActionEvent(new Object(), 0, ZOOM_RESET_BOTH_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        plotChanged[0] = false;
        event = new ActionEvent(new Object(), 0, ZOOM_RESET_BOTH_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        plotChanged[0] = false;
        event = new ActionEvent(new Object(), 0, ZOOM_RESET_BOTH_COMMAND);
        panel.actionPerformed(event);
        assertTrue(plotChanged[0]);

        event = new ActionEvent(new Object(), 0, COPY_COMMAND);
        panel.actionPerformed(event);
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        assertEquals("image/x-java-image; class=java.awt.Image", systemClipboard.getContents(this).getTransferDataFlavors()[0].getMimeType());

    }
}
