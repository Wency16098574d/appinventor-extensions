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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


//???????????????????????verion, nonvisible
@DesignerComponent(version = 20171107,
        description = "indoor " +
                "positioning",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        helpUrl = "http://",
        iconName = "images/positioning.png")
//???????external
@SimpleObject(external = true)
//???usesPermissions
@UsesPermissions(permissionNames = "android.permission.BLUETOOTH, " + "android.permission.BLUETOOTH_ADMIN,"
        + "android.permission.ACCESS_COARSE_LOCATION")














public class Positioning extends AndroidNonvisibleComponent implements Component {

    private static final String LOG_TAG = "Positioning";

    private final ComponentContainer container;

    private List<String> BeaconListString;
    private List<Beacon> BeaconList;
//    private HashMap<Beacon, List<Float>> hmap = new HashMap<Beacon, List<Float>>();
    private float x;
    private float y;





    /**
     * Creates a Positioning component.
     *
     * @param container container, component will be placed in
     */
    public Positioning(ComponentContainer container) {
        super(container.$form());
        this.container = container;
    }







    @SimpleFunction
    public String CreateBeacon(String BeaconID, float X, float Y) {
        BeaconList.add(new Beacon(BeaconID, X, Y));
        return BeaconID + "," + X + "," + Y; //string
    }


    @SimpleFunction
    public void AddBeacon(String Beacon) {
        BeaconListString.add(Beacon);   //string
        //BeaconString is returned by CretedBeacon, no need to add to BeaconList
    }

//    @SimpleFunction
//    public String GetBeacon(String BeaconID) {
//        for (String b : BeaconList)
//            if (BeaconID.equals(b.substring(0, BeaconID.length())))
//                return b;
//        return "error"; //Exception throwing need to be added
//    }

    @SimpleFunction
    public void DeleteBeacon(String BeaconID) {
        BeaconListString.remove(getBeaconIndex(BeaconID)); //string
        BeaconList.remove(getBeaconIndex(BeaconID));
    }


    private int getBeaconIndex(String BeaconID){
        int i;
        for(i = 0; i < BeaconList.size(); i++)
            if(BeaconID.equals(BeaconList.get(i).getID()))
                return i;
        return -1; //throw exception ?
    }


    @SimpleFunction
    public void SetBeacon(String BeaconID, float X, float Y) {
        BeaconListString.set(getBeaconIndex(BeaconID), CreateBeacon(BeaconID, X, Y) );//string
        Beacon beacon = BeaconList.get(getBeaconIndex(BeaconID));
        beacon.setX(X);
        beacon.setX(Y);
    }

    private float getX(String BeaconID){

//        String beacon = BeaconList.get(getBeaconIndex(BeaconID));
//
//        int beg = 0, end = 0;
//        do{ beg++; }while(beacon.charAt(beg) == ',');
//        end = beg;
//        do{ end++; }while(beacon.charAt(beg) == ',');
//
//        return Float.parseFloat(beacon.substring(++beg, end));
        return BeaconList.get(getBeaconIndex(BeaconID)).getX();
    }

    private float getY(String BeaconID){
        return BeaconList.get(getBeaconIndex(BeaconID)).getY();
    }

    @SimpleFunction
    public void DoPositioning(String DeviceList){

    }

    @SimpleEvent(description = "Trigger event when Location changes")
    public void LocationChanged() {
    }




    @SimpleProperty(description = "Returns a list of the Beacons.")
    public List<String> BeaconListString() {//string
        return BeaconListString;
    }

    @SimpleProperty(description = "Returns the location.")
    public String Location() {
        return x + ", " + y;
    }







    private class Beacon {
        String ID;
        float X;
        float Y;

        public Beacon(String ID, float X, float Y){
            this.ID = new String(ID);
            this.X = X;
            this.Y = Y;
        }

        public String getBeacon(){  return ID + X + Y;  }

        public String getID(){  return ID;   }

        public float getX(){    return X;   }

        public float getY(){    return Y;   }

        public void setX(float X){    this.X = X; }

        public void setY(float Y){    this.Y = Y; }


    }

}
