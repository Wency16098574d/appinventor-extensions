package edu.polyu.appinventor.positioning;
import edu.polyu.appinventor.positioning.algorithm.*;

import junit.framework.TestCase;
import java.util.ArrayList;
import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;
import edu.polyu.appinventor.positioning.Location;


public class PositioningTest extends TestCase {

    private Positioning positioning;
    private Algorithm A;
    private List<Beacon> BeaconList;

    @Override
    protected void setUp() throws Exception {
        positioning = new Positioning();
    }

    public void testCreateBeacon() throws Exception {
        assertEquals(positioning.CreateBeacon("1", (float)2, (float)3), "1,2.0,3.0");
    }

    public void testOverlapArea(){
        A = new OverlapArea();
        BeaconList = new ArrayList();
        BeaconList.add(new Beacon(00:A0:50:00:00:04, 4, 4));
        BeaconList.add(new Beacon(00:A0:50:00:00:06, 8, 0));
        BeaconList.add(new Beacon(00:A0:50:00:00:07, 0, 0));
        A.calPosition(BeaconList, loc);

    }
}