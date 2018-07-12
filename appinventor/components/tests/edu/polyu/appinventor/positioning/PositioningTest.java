package edu.polyu.appinventor.positioning;

import junit.framework.TestCase;

public class PositioningTest extends TestCase {

    public void testCreateBeacon() throws Exception {
        Positioning positioning = new Positioning(null);
        assertEquals(positioning.CreateBeacon("1", 2, 3), "1,2,3");
    }
}