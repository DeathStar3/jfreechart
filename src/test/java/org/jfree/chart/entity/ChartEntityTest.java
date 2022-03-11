package org.jfree.chart.entity;

import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ChartEntityTest {

    private ChartEntity chartEntityRect, chartEntityPoly;

    @Before
    public void setUp() throws Exception {
        chartEntityRect = new ChartEntity(new Rectangle(1, 2, 3, 4));
        chartEntityPoly = new ChartEntity(new Polygon(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5},5));
    }

    @Test
    public void testgetShapeType() {
        assertEquals("rect", chartEntityRect.getShapeType());
        assertEquals("poly", chartEntityPoly.getShapeType());
    }

    @Test
    public void testgetShapeCoords() {
        assertEquals("1,2,4,6", chartEntityRect.getShapeCoords());
        assertEquals("1,1,2,2,3,3,4,4,5,5,5,5", chartEntityPoly.getShapeCoords());
    }

    @Test
    public void testgetImageMapAreaTag() {
        StandardToolTipTagFragmentGenerator toolTipGenerator = new StandardToolTipTagFragmentGenerator();
        StandardURLTagFragmentGenerator urlTagGenerator = new StandardURLTagFragmentGenerator();
        assertEquals("", chartEntityRect.getImageMapAreaTag(toolTipGenerator, urlTagGenerator));
        chartEntityRect.setToolTipText(" a nice rectangle");
        assertEquals("<area shape=\"rect\" coords=\"1,2,4,6\" title=\" a nice rectangle\" alt=\"\" nohref=\"nohref\"/>",
                chartEntityRect.getImageMapAreaTag(toolTipGenerator, urlTagGenerator));
        chartEntityRect.setURLText("http://www.example.com/jfreechart");
        assertEquals("<area shape=\"rect\" coords=\"1,2,4,6\" title=\" a nice rectangle\" alt=\"\" href=\"http://www.example.com/jfreechart\"/>",
                chartEntityRect.getImageMapAreaTag(toolTipGenerator, urlTagGenerator));
        chartEntityPoly.setURLText("http://www.example.com/jfreechart");
        assertEquals("<area shape=\"poly\" coords=\"1,1,2,2,3,3,4,4,5,5,5,5\" href=\"http://www.example.com/jfreechart\" alt=\"\"/>",
                chartEntityPoly.getImageMapAreaTag(toolTipGenerator, urlTagGenerator));
    }

    @Test
    public void testToString() {
        assertEquals("ChartEntity: tooltip = null", chartEntityRect.toString());
        chartEntityRect.setToolTipText(" a nice rectangle");
        assertEquals("ChartEntity: tooltip =  a nice rectangle", chartEntityRect.toString());
    }

    @Test
    public void testEquals() {
        assertNotEquals(chartEntityRect,null);
        assertEquals(chartEntityRect,chartEntityRect);
        assertNotEquals(chartEntityRect,chartEntityPoly);
        assertEquals(chartEntityRect, new ChartEntity(new Rectangle(1, 2, 3, 4)));
        assertNotEquals(chartEntityRect, new ChartEntity(new Rectangle(2, 2, 3, 4)));
        assertNotEquals(chartEntityRect, new ChartEntity(new Rectangle(1, 2, 3, 5)));
        assertNotEquals(chartEntityPoly, new ChartEntity(new Polygon(new int[]{1, 2, 3, 4}, new int[]{1, 2, 3, 4},4)));
        assertNotEquals(chartEntityPoly, new ChartEntity(new Polygon(new int[]{1, 2, 3, 4, 6}, new int[]{1, 2, 3, 4, 6},5)));
        ChartEntity rectToolTip = new ChartEntity(new Rectangle(1, 2, 3, 4),"toolTip");
        assertNotEquals(chartEntityRect, rectToolTip);
        assertEquals(rectToolTip, new ChartEntity(new Rectangle(1, 2, 3, 4),"toolTip"));
        ChartEntity rectToolUrl = new ChartEntity(new Rectangle(1, 2, 3, 4),"toolTip", "http://www.example.com/jfreechart");
        assertNotEquals(chartEntityRect, rectToolUrl);
        assertNotEquals(rectToolTip,rectToolUrl);
    }


}