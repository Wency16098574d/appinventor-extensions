//comments?
package edu.polyu.appinventor.positioning;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;

import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.polyu.appinventor.positioning.filter.*;
import edu.polyu.appinventor.positioning.convertor.*;
import edu.polyu.appinventor.positioning.algorithm.*;


/**
 * This extension calculates the positioning based on RSSI from BLE extension.
 * The extension stores the new scaned RSSI from BLE extension, uses some filtering
 * methods, converts RSSI to distance and calculate the result.
 * Programmers could overwrite any of the filtering methods, converting formular or
 * caculation algorithm, or overwrite the whole doPositioning methods of own algorithm,
 */
//?verion, helpUrl(tutorials?)
@DesignerComponent(version = 20171107,
  description = "This component calculates the indoor position based on RSSI from BLE extension." +
    "It must be used together with BLE extension. " +
    "The user need to input Beacon ID and x y location first, and call doPositioning function "+
    "to calculate the location. ",
  category = ComponentCategory.EXTENSION,
  nonVisible = true,
  helpUrl = "http://",
  iconName = "aiwebres/positioning.png")
//?external
@SimpleObject(external = true)
@UsesLibraries(libraries = "commonsmath3.jar")
public class Positioning extends AndroidNonvisibleComponent implements Component {
  private List<String> BeaconListString;
  private List<Beacon> BeaconList;
  private Location loc;
  private int N = 0;
  private long lastCalTime;
  private long timeInterval;
  private int filter;
  private int convertor;
  private int algorithm;
  private Filter filterObject;
  private Convertor convertorObject;
  private Algorithm algorithmObject;
  static final String FILTER_MEAN = "mean";
  static final String FILTER_MEDIAN = "median";
  static final String CONVERTOR_FORMULAR1 = "Formular1";
  static final String CONVERTOR_FORMULAR2 = "Formular2";
  static final String CONVERTOR_NUFO = "NUFO";
  static final String ALGORITHM_TRILATERATION = "trilateration";
  static final String ALGORITHM_OVERLAPAREA = "overlaparea";
  /**
   * Creates a positioning component for calculating the position.
   *
   * @param container container, component will be placed in
   */
  public Positioning(ComponentContainer container) {
    super(container.$form());
    BeaconListString = new ArrayList<String>();
    BeaconList = new ArrayList<Beacon>();
    loc = new Location(0, 0);
    lastCalTime = Calendar.getInstance().getTimeInMillis();
    TimeInterval(2000);
    Algorithm("trilateration");
    Convertor("Formular1");
    Filter("mean");
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
    TimeInterval(2000);
    Algorithm("trilateration");
    Convertor("Formular1");
    Filter("mean");
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
    return BeaconID + ", " + X + ", " + Y; //string
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
    TestPoint(111, 111);
    if(!beaconExist(BeaconID)) return;
    BeaconList.get(getBeaconIndex(BeaconID)).addRecord(Rssi);
    long curTime = Calendar.getInstance().getTimeInMillis();
    if((curTime - lastCalTime) < timeInterval) return;
    TestPoint(curTime - lastCalTime, timeInterval);
    lastCalTime = curTime;
    if(filterObject.filtering(BeaconList) < 3) {TestPoint(222, filterObject.filtering(BeaconList));   return;}
    convertorObject.convert(BeaconList);
    algorithmObject.calPosition(BeaconList, loc);
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
  public double locX() {
    return loc.getLocX();
  }

  @SimpleProperty(description = "Returns the location Y.")
  public double locY() {
    return loc.getLocY();
  }

  @SimpleProperty(
    category = PropertyCategory.APPEARANCE,
    userVisible = false)
  public String Algorithm() {
    switch(algorithm){
      case 0:
        return ALGORITHM_TRILATERATION;
      case 1:
        return ALGORITHM_OVERLAPAREA;
    }
    return ALGORITHM_TRILATERATION;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHOICES,
    defaultValue = ALGORITHM_TRILATERATION, editorArgs = {ALGORITHM_TRILATERATION, ALGORITHM_OVERLAPAREA})
  @SimpleProperty(
    userVisible = false)
  public void Algorithm(String algorithm) {
    if(algorithm.equals(ALGORITHM_TRILATERATION)) {
      this.algorithmObject = new Trilateration();
      this.algorithm = 0;
    }
    else if(algorithm.equals(ALGORITHM_OVERLAPAREA)){
      this.algorithmObject = new OverlapArea();
      this.algorithm = 1;
    }
  }

  @SimpleProperty(
    category = PropertyCategory.APPEARANCE,
    userVisible = false)
  public String Convertor() {
    switch(convertor){
      case 0:
        return CONVERTOR_FORMULAR1;
      case 1:
        return CONVERTOR_FORMULAR2;
      case 2:
        return CONVERTOR_NUFO;
    }
    return CONVERTOR_FORMULAR1;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHOICES,
    defaultValue = CONVERTOR_FORMULAR1, editorArgs = {CONVERTOR_FORMULAR1, CONVERTOR_FORMULAR2, CONVERTOR_NUFO})
  @SimpleProperty(
    userVisible = false)
  public void Convertor(String convertor) {
      if(convertor.equals(CONVERTOR_FORMULAR1)) {
        this.convertorObject = new F1();
        this.convertor = 0;
      }
      else if(convertor.equals(CONVERTOR_FORMULAR2)) {
        this.convertorObject = new F2();
        this.convertor = 1;
      }
      else if(convertor.equals(CONVERTOR_NUFO)) {
        this.convertorObject = new NUFO();
        this.convertor = 2;
      }
  }

  @SimpleProperty(
    category = PropertyCategory.APPEARANCE,
    userVisible = false)
  public String Filter() {
    switch(filter){
      case 0:
        return FILTER_MEAN;
      case 1:
        return FILTER_MEDIAN;
    }
    return FILTER_MEAN;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHOICES,
    defaultValue = FILTER_MEAN, editorArgs = {FILTER_MEAN, FILTER_MEDIAN})
  @SimpleProperty(
     userVisible = false)
  public void Filter(String filter) {
    if(filter.equals(FILTER_MEAN)){
      this.filterObject = new Mean();
      this.filter = 0;
    }
    else if(filter.equals(FILTER_MEDIAN)) {
      this.filterObject = new Median();
      this.filter = 1;
    }
  }

  @SimpleProperty(
    category = PropertyCategory.APPEARANCE,
    userVisible = false)
  public long TimeInterval() {
        return timeInterval;
    }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_FLOAT,
    defaultValue = "2000")
  @SimpleProperty(
    userVisible = false)
  public void TimeInterval(long timeInterval) {
    this.timeInterval = timeInterval;
    }
}
