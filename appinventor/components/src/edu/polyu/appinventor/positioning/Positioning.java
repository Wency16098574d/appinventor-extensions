package edu.polyu.appinventor.positioning;

import android.os.Handler;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.Deleteable;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import com.google.appinventor.components.runtime.util.SdkLevel;
import com.google.appinventor.components.runtime.util.YailList;
import gnu.lists.FString;

import java.lang.*;
import java.lang.Double;
import java.lang.String;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.polyu.appinventor.positioning.filter.*;
import edu.polyu.appinventor.positioning.convertor.*;
import edu.polyu.appinventor.positioning.algorithm.*;

@UsesLibraries(libraries = "commonsmath3.jar")



//?verion, nonvisible
@DesignerComponent(version = 20171107,
        description = "indoor " +
                "positioning",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        helpUrl = "http://",
        iconName = "images/positioning.png")
//?external
@SimpleObject(external = true)
//?usesPermissions
@UsesPermissions(permissionNames = "android.permission.BLUETOOTH, " + "android.permission.BLUETOOTH_ADMIN,"
        + "android.permission.ACCESS_COARSE_LOCATION")


public class Positioning extends AndroidNonvisibleComponent implements Component {

    private static final String LOG_TAG = "Positioning";
//    private final ComponentContainer container;
//    private final Handler uiThread;

    private List<String> BeaconListString;
    private List<Beacon> BeaconList;
    private Location loc;
    private int N = 0;
    private long lastCalTime;
    private long interval;
    private Filter F;
    private Convertor C;
    private Algorithm A;
    private int algorithm;
    private int convertor;
    private int filter;


    /**
     * Creates a Positioning component.
     *
     * @param container container, component will be placed in
     */
    public Positioning(ComponentContainer container) {
        super(container.$form());
//        this.container = container;
//        uiThread = new Handler();
        BeaconListString = new ArrayList<String>();
        BeaconList = new ArrayList<Beacon>();
        loc = new Location(0, 0);
        lastCalTime = Calendar.getInstance().getTimeInMillis();
        F = new Mean();
        C = new F1();
        A = new Trilateration();
        Interval(Component.POSITIONING_DEFAULT_INTERVAL);
        Algorithm(Component.POSITIONING_ALGORITHM_TRILATERATION);
        Convertor(Component.POSITIONING_CONVERTOR_F1);
        Filter(Component.POSITIONING_FILTER_MEAN);
    }

    /**
     * This constructor is for testing purposes only.
     */
    protected Positioning() {
        super(null);
        BeaconListString = new ArrayList<String>();
        BeaconList = new ArrayList<Beacon>();
        loc = new Location(0, 0);
        lastCalTime = Calendar.getInstance().getTimeInMillis();
        F = new Mean();
        C = new F1();
        A = new Trilateration();
        Filter(Component.POSITIONING_FILTER_MEAN);
    }

    private boolean beaconExist(String BeaconID){
        for(int i = 0; i < N; i++)
            if(BeaconList.get(i).getID().equals(BeaconID))
                return true;
        return false;
    }

    private int getBeaconIndex(String BeaconID){
        for(int i = 0; i < N; i++)
            if(BeaconID.equals(BeaconList.get(i).getID()))
                return i;
        return -1;
    }

    @SimpleFunction
    public String CreateBeacon(String BeaconID, float X, float Y) {
        if(beaconExist(BeaconID)) throw new IllegalArgumentException("Device already exists: " + BeaconID);
        Beacon beacon = new Beacon(BeaconID, X, Y);
        BeaconList.add(beacon);
        N++;
        return BeaconID + "," + X + "," + Y; //string
    }

    @SimpleFunction
    public void AddBeacon(String BeaconID) {
        if(beaconExist(BeaconID)) throw new IllegalArgumentException("Device already exists: " + BeaconID);
        BeaconListString.add(BeaconID);   //string
        //BeaconString is returned by CretedBeacon, no need to add to BeaconList
        N++;
    }

    @SimpleFunction
    public void SetBeacon(String BeaconID, float X, float Y) {
        if(!beaconExist(BeaconID)) throw new IllegalArgumentException("Device does not exist: " + BeaconID);
        BeaconListString.set(getBeaconIndex(BeaconID), CreateBeacon(BeaconID, X, Y) );//string
        Beacon beacon = BeaconList.get(getBeaconIndex(BeaconID));
        beacon.setX(X);
        beacon.setX(Y);
    }

    @SimpleFunction
    public void DeleteBeacon(String BeaconID) {
        if(!beaconExist(BeaconID)) throw new IllegalArgumentException("Device does not exist: " + BeaconID);
        BeaconListString.remove(getBeaconIndex(BeaconID)); //string
        BeaconList.remove(getBeaconIndex(BeaconID));
        N--;
    }

    private double getX(String BeaconID){
        if(!beaconExist(BeaconID)) throw new IllegalArgumentException("Device does not exist: " + BeaconID);
        return BeaconList.get(getBeaconIndex(BeaconID)).getX();
    }

    private double getY(String BeaconID){
        if(!beaconExist(BeaconID)) throw new IllegalArgumentException("Device does not exist: " + BeaconID);
        return BeaconList.get(getBeaconIndex(BeaconID)).getY();
    }

    @SimpleFunction
    public void DoPositioning(String BeaconID, int Rssi){
        if(!beaconExist(BeaconID)) return;
        BeaconList.get(getBeaconIndex(BeaconID)).addRecord(Rssi);

        long curTime = Calendar.getInstance().getTimeInMillis();
        if((curTime - lastCalTime) < interval) return;

        TestPoint(curTime - lastCalTime, interval);
        lastCalTime = curTime;

        if(F.filtering(BeaconList) < 3) {TestPoint(111, F.filtering(BeaconList));   return;}

        C.convert(BeaconList);

        A.calPosition(BeaconList, loc);

        for(int i = 0; i < N; i++)  BeaconList.get(i).getRecordList().clear();

        //trigger the locationChanged event
        LocationChanged(loc.getLocX(), loc.getLocY());

    }

    @SimpleEvent(description = "Trigger event when Location changes")
    public void LocationChanged(final double locX, final double locY) {
        EventDispatcher.dispatchEvent(this, "LocationChanged", locX, locY);
    }

    @SimpleEvent
    public void TestPoint(final double a, final double b) {
        EventDispatcher.dispatchEvent(this, "TestPoint", a, b);
    }

    @SimpleProperty(description = "Returns a list of the Beacons.")
    public List<String> BeaconListString() {//string
        return BeaconListString;
    }

    @SimpleProperty(description = "Returns the location X.")
    public double locX() { return loc.getLocX(); }

    @SimpleProperty(description = "Returns the location Y.")
    public double locY() { return loc.getLocY(); }

    @SimpleProperty(
            category = PropertyCategory.APPEARANCE,
            userVisible = false)
    public int Algorithm() {
        return algorithm;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_POSITIONING_ALGORITHM,
            defaultValue = Component.POSITIONING_ALGORITHM_TRILATERATION + "")

    @SimpleProperty(
            userVisible = false)
    public void Algorithm(int algorithm) {
        this.algorithm = algorithm;
    }

    @SimpleProperty(
            category = PropertyCategory.APPEARANCE,
            userVisible = false)
    public int Convertor() {
        return convertor;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_POSITIONING_CONVERTOR,
            defaultValue = Component.POSITIONING_CONVERTOR_F1 + "")

    @SimpleProperty(
            userVisible = false)
    public void Convertor(int convertor) {
        this.convertor = convertor;
    }

    @SimpleProperty(
            category = PropertyCategory.APPEARANCE,
            userVisible = false)
    public int Filter() {
        return filter;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_POSITIONING_FILTER,
            defaultValue = Component.POSITIONING_FILTER_MEAN + "")

    @SimpleProperty(
            userVisible = false)
    public void Filter(int filter) {
        this.filter = filter;
    }

    @SimpleProperty(
            category = PropertyCategory.APPEARANCE,
            userVisible = false)
    public long Interval() {
        return interval;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_FLOAT,
            defaultValue = Component.POSITIONING_DEFAULT_INTERVAL + "")

    @SimpleProperty(
            userVisible = false)
    public void Interval(long interval) {
        this.interval = interval;
    }

}
