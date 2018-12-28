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
import edu.polyu.appinventor.positioning.postprocessor.*;


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
  private List<Beacon> BeaconList;
  private Location loc;
  private int N = 0;
  private long lastCalTime;
  private long lastUpdateTime;
  private long timeInterval;
  private int filter;
  private int convertor;
  private int algorithm;
  private int postprocessor;
  private Filter filterObject;
  private Convertor convertorObject;
  private Algorithm algorithmObject;
  private Postprocessor postprocessorObject;
  static final String FILTER_MEAN = "mean";
  static final String FILTER_MEDIAN = "median";
  static final String CONVERTOR_FORMULAR1 = "Formular1";
  static final String CONVERTOR_FORMULAR2 = "Formular2";
  static final String CONVERTOR_NUFO = "NUFO";
  static final String ALGORITHM_TRILATERATION = "trilateration";
  static final String ALGORITHM_LEASTRATIO = "leastRatio";
  static final String ALGORITHM_OVERLAPAREA = "overlaparea";
  static final String POSTPROCESSOR_VALIDATION = "validation";
  /**
   * Creates a positioning component for calculating the position.
   *
   * @param container container, component will be placed in
   */
  public Positioning(ComponentContainer container) {
    super(container.$form());
    BeaconList = new ArrayList<Beacon>();
    loc = new Location(0, 0);
    lastCalTime = Calendar.getInstance().getTimeInMillis();
    TimeInterval(2000);
    Algorithm("trilateration");
    Convertor("Formular1");
    Filter("mean");
    Postprocessor("validation");
  }

  /**
   * This constructor is for testing purposes only.
   */
  protected Positioning() {
    super(null);
    BeaconList = new ArrayList<Beacon>();
    loc = new Location(0, 0);
    lastCalTime = Calendar.getInstance().getTimeInMillis();
    TimeInterval(2000);
    Algorithm("trilateration");
    Convertor("Formular1");
    Filter("mean");
    Postprocessor("validation");
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

  public long getLastUpdateTime(){
    return lastUpdateTime;
  }

  /**
   * Add a new beacon with the BeaconID(mac address), x-axis location and y-axis location
   */
  @SimpleFunction
  public void AddBeacon(String BeaconID, float X, float Y) {
    if(beaconExist(BeaconID)) throw new IllegalArgumentException("Device already exists: " + BeaconID);
    Beacon beacon = new Beacon(BeaconID, X, Y);
    BeaconList.add(beacon);
    N++;
  }

  /**
   * Change the x and y location of the beacon with the same input BeaconID.
   */
  @SimpleFunction
  public void SetBeacon(String BeaconID, float X, float Y) {
    if(!beaconExist(BeaconID)) throw new IllegalArgumentException("Device does not exist: " + BeaconID);
    Beacon beacon = BeaconList.get(getBeaconIndex(BeaconID));
    beacon.setX(X);
    beacon.setX(Y);
  }

  /**
   * Delete the beacon with the same input BeaconID
   */
  @SimpleFunction
  public void DeleteBeacon(String BeaconID) {
    if(!beaconExist(BeaconID)) throw new IllegalArgumentException("Device does not exist: " + BeaconID);
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

  /**
   * This function stores the input BeaconID and Rssi,
   * calculates the current x, y value of the location after each time interval
   * by the selected filtering, converting and calculating algorithm,
   * and clear all the rssi records.
   */
  @SimpleFunction
  public void DoPositioning(String BeaconID, int Rssi){
    if(!beaconExist(BeaconID)) return;
    BeaconList.get(getBeaconIndex(BeaconID)).addRecord(Rssi);
    long curTime = Calendar.getInstance().getTimeInMillis();
    if((curTime - lastCalTime) < timeInterval) return;
    lastCalTime = curTime;
    if (filterObject.filtering(BeaconList) < 3) return;
    convertorObject.convert(BeaconList);
    Location newLoc = algorithmObject.calPosition(BeaconList);
    //trigger the locationChanged event
    if (postprocessorObject.processing(this, newLoc)) {
      lastUpdateTime = curTime;
      LocationChanged(newLoc.getLocX(), newLoc.getLocY());
    }
    for(int i = 0; i < N; i++)  BeaconList.get(i).getRecordList().clear();
  }


  @SimpleEvent(description = "Trigger event when Location changes")
  public void LocationChanged(final double locX, final double locY) {
    EventDispatcher.dispatchEvent(this, "LocationChanged", locX, locY);
  }

//  //This event is for test only
//  @SimpleEvent
//  public void TestPoint(final double a, final double b, final double c) {
//        EventDispatcher.dispatchEvent(this, "TestPoint", a, b, c);
//    }

  @SimpleProperty(description = "Returns a list of the BeaconID, x and y separeated by comma (string)." +
    "For example, \"00:A0:50:00:00:04,3,7\" is a string of a beacon with ID 00:A0:50:00:00:04, x-value 3 and y-value 7.")
  public List<String> BeaconListString() {//string
    List<String> BeaconListString = new ArrayList<String>();
    for(int i = 0; i < N; i++)
      BeaconListString.add(BeaconList.get(i).getBeacon());
    return BeaconListString;
    }

  @SimpleProperty(description = "Returns the latest location X.")
  public double locX() {
    return loc.getLocX();
  }

  @SimpleProperty(description = "Returns the latest location Y.")
  public double locY() {
    return loc.getLocY();
  }

  @SimpleProperty(description = "Returns the largest beacon location X.")
  public double MaxX(){
    double maxX = BeaconList.get(0).getX();
    for(int i = 1; i < N; i++)
      if(BeaconList.get(i).getX() > maxX) maxX = BeaconList.get(i).getX();
    return maxX;
  }

  @SimpleProperty(description = "Returns the largest beacon location Y.")
  public double MaxY(){
    double maxY = BeaconList.get(0).getY();
    for(int i = 1; i < N; i++)
      if(BeaconList.get(i).getY() > maxY) maxY = BeaconList.get(i).getY();
    return maxY;
  }

  @SimpleProperty(description = "Returns the smallest beacon location X.")
  public double MinX(){
    double minX = BeaconList.get(0).getX();
    for(int i = 1; i < N; i++)
      if(BeaconList.get(i).getY() < minX) minX = BeaconList.get(i).getX();
    return minX;
  }

  @SimpleProperty(description = "Returns the smallest beacon location Y.")
  public double MinY(){
    double minY = BeaconList.get(0).getY();
    for(int i = 1; i < N; i++)
      if(BeaconList.get(i).getY() < minY) minY = BeaconList.get(i).getY();
    return minY;
  }


  @SimpleProperty(
    category = PropertyCategory.APPEARANCE,
    userVisible = false)
  public String Algorithm() {
    switch(algorithm){
      case 0:
        return ALGORITHM_TRILATERATION;
      case 1:
        return ALGORITHM_LEASTRATIO;
      case 2:
        return ALGORITHM_OVERLAPAREA;
    }
    return ALGORITHM_TRILATERATION;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHOICES,
    defaultValue = ALGORITHM_TRILATERATION, editorArgs = {ALGORITHM_TRILATERATION, ALGORITHM_LEASTRATIO, ALGORITHM_OVERLAPAREA})
  @SimpleProperty(
    userVisible = false)
  public void Algorithm(String algorithm) {
    if(algorithm.equals(ALGORITHM_TRILATERATION)) {
      this.algorithmObject = new Trilateration();
      this.algorithm = 0;
    }
    else if(algorithm.equals(ALGORITHM_LEASTRATIO)){
      this.algorithmObject = new LeastRatioAlgorithm();
      this.algorithm = 1;
    }
    else if(algorithm.equals(ALGORITHM_OVERLAPAREA)){
      this.algorithmObject = new OverlapArea();
      this.algorithm = 2;
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
  public String Postprocessor() {
    switch(postprocessor){
      case 0:
        return POSTPROCESSOR_VALIDATION;
    }
    return POSTPROCESSOR_VALIDATION;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHOICES,
    defaultValue = POSTPROCESSOR_VALIDATION, editorArgs = {POSTPROCESSOR_VALIDATION})
  @SimpleProperty(
    userVisible = false)
  public void Postprocessor(String postprocessor) {
    if(postprocessor.equals(POSTPROCESSOR_VALIDATION)){
      this.postprocessorObject = new Validation();
      this.postprocessor = 0;
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
