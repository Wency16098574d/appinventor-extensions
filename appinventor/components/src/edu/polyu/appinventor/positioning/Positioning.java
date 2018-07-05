package edu.polyu.appinventor.positioning;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import android.os.Handler;


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

    private final ComponentContainer container;
    private final Handler uiThread = new Handler();

    private List<String> BeaconListString = new ArrayList<String>();
    private List<Beacon> BeaconList = new ArrayList<Beacon>();
    private HashMap<Beacon, List<Float>> hmap = new HashMap<Beacon, List<Float>>();
    private double locX;
    private double locY;
    //Q5. maintain N vs getlength each time
    private int N;
    private long lastCalTime = Calendar.getInstance().getTimeInMillis(); //Q3. Intialize the time or later?
    private long Interval = 2000; //in microsecends


    private class Beacon {
        private String ID;
        private float X;
        private float Y;
        private List<Integer> Rssi;

        public Beacon(String ID, float X, float Y){
            this.ID = new String(ID);
            this.X = X;
            this.Y = Y;
            Rssi = new ArrayList<Integer>();
        }

        public String getBeacon(){  return ID + X + Y;  }

        public String getID(){  return ID;   }

        public float getX(){    return X;   }

        public float getY(){    return Y;   }

        public List<Integer> getRssi(){    return Rssi;    };

        public void setX(float X){    this.X = X; }

        public void setY(float Y){    this.Y = Y; }

        public void addRssi(int Rssi){  this.Rssi.add(Rssi);    }
    }


    /**
     * Creates a Positioning component.
     *
     * @param container container, component will be placed in
     */
    public Positioning(ComponentContainer container) {
        super(container.$form());
        this.container = container;

        //Q2: better?
//        Handler uiThread = new Handler();
//        BeaconListString = new ArrayList<String>();
//        BeaconList = new ArrayList<Beacon>();
//        hmap = new HashMap<Beacon, List<Float>>();

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
        //Q4. Check exsitence. cover vs. error message? --> exception?
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

    private float getY(String BeaconID){
        if(!beaconExist(BeaconID)) throw new IllegalArgumentException("Device does not exist: " + BeaconID);
        return BeaconList.get(getBeaconIndex(BeaconID)).getY();
    }

    @SimpleFunction
    public void DoPositioning(String BeaconID, int Rssi){
        //Q6. not exist --> return vs only connect exist
        if(!beaconExist(BeaconID)) return;
        BeaconList.get(getBeaconIndex(BeaconID)).addRssi(Rssi);

        long curTime = Calendar.getInstance().getTimeInMillis();
        if((lastCalTime - curTime) / 1e9 < Interval) return;

        lastCalTime = curTime;

        //=====filtering======
        List<Double> distance = filterMean();

        //=======positioning========
        positioningTrilateration(distance);

        for(int i = 0; i < N; i++)  BeaconList.get(i).getRssi().clear();
    }

    private double convertDistance(double rssi) {
        double txPower = -59; //hard coded power value. Usually ranges between -59 to -65
//      if (rssi == 0)    return -1.0;
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) return Math.pow(ratio, 10);
        else return ((0.89976) * Math.pow(ratio, 7.7095) + 0.111);
    }

    private List<Double> filterMean(){
        int m, temp;
        List<Integer> RssiList;
        List<Double> distance = new ArrayList<Double>(N);

        for(int i = 0; i < N; i++){
            RssiList = BeaconList.get(i).getRssi();
            m = RssiList.size();
            temp = 0;

            //Q9. no record? (not count by adding null and checking null)
            if(m == 0) {    distance.add(null); break;  }
            for(int j = 0; j < m; j++)    temp += RssiList.get(j);
            distance.add(convertDistance(temp/m));
        }
        return distance;
    }

    private void positioningTrilateration(List<Double> Distance){
        //Q7. parameter type list or array --> better to store in list, let algorithm to convert to array if necessary
        //preparing parameters
        int n = 0;
        for(int i = 0; i < N; i++)  if(Distance.get(i) != null) n++;
        double[][] positions = new double[n] [2];
        double[] distances = new double[n];

        int j = 0;
        for(int i = 0; i < N; i++){
            if(Distance.get(i) == null) break;
            positions[j++][0] = Double.parseDouble(String.valueOf(BeaconList.get(i).getX()));
            positions[j++][1] = Double.parseDouble(String.valueOf(BeaconList.get(i).getY()));
            distances[j++] = Double.parseDouble(String.valueOf(Distance.get(i)));
        }

        //algorithm
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        Optimum optimum = solver.solve();

        // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);

        // the answer
        double[] centroid = optimum.getPoint().toArray();
        locX = centroid[0];
        locY = centroid[1];

        //trigger the locationChanged event
        LocationChanged(locX, locY);
    }

    @SimpleEvent(description = "Trigger event when Location changes")
    public void LocationChanged(final double locX, final double locY) {
//        uiThread.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        EventDispatcher.dispatchEvent(this, "LocationChanged", Location);
//            }
//        }, 1000);
    }

    @SimpleProperty(description = "Returns a list of the Beacons.")
    public List<String> BeaconListString() {//string
        return BeaconListString;
    }

    //Q8. still need returning location <-- stop scanning, get last location
    @SimpleProperty(description = "Returns the location X.")
    public double locX() { return locX; }

    @SimpleProperty(description = "Returns the location Y.")
    public double locY() { return locY; }

}
