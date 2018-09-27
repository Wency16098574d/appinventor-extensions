package edu.polyu.appinventor.positioning;

import edu.polyu.appinventor.positioning.algorithm.*;
import edu.polyu.appinventor.positioning.convertor.*;
import edu.polyu.appinventor.positioning.filter.*;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class PositioningTest extends TestCase {

    private Positioning positioning;
    private Algorithm A;
    private List<Beacon> BeaconList;

    @Override
    protected void setUp() throws Exception {
        positioning = new Positioning();
    }

/*    public void testFilterMean() throws Exception {
        List<Beacon> BeaconList = new List<Beacon>();
        BeaconList.add("id1, 1, 1");
        assertEquals(positioning.CreateBeacon("1", (float)2, (float)3), "1,2.0,3.0");
    }*/
//
//    public void testOverlapArea(){
//        A = new OverlapArea();
//        BeaconList = new ArrayList();
//        BeaconList.add(new Beacon(00:A0:50:00:00:04, 4, 4));
//        BeaconList.add(new Beacon(00:A0:50:00:00:06, 8, 0));
//        BeaconList.add(new Beacon(00:A0:50:00:00:07, 0, 0));
//        A.calPosition(BeaconList, loc);
//    }

    public void testLeastRatio(){
        A = new Trilateration();
        BeaconList = new ArrayList();
        BeaconList.add(new Beacon("00:A0:50:00:00:04", 4, 4));
        BeaconList.add(new Beacon("00:A0:50:00:00:06", 8, 0));
        BeaconList.add(new Beacon("00:A0:50:00:00:07", 0, 0));
        BeaconList.get(0).setDistance(5);
        BeaconList.get(1).setDistance(4);
        BeaconList.get(2).setDistance(6);
        Location loc = new Location(0, 0);
        A.calPosition(BeaconList, loc);
        System.out.println("==============================" + loc.getLocX() + ", "+ loc.getLocY());
        assertEquals(loc.getLocX(), 5);
        assertEquals(loc.getLocY(), 4);

    }


}