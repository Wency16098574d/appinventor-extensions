package edu.polyu.appinventor.positioning.algorithm;

import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;
import edu.polyu.appinventor.positioning.Location;

public interface Algorithm{   public void calPosition(List<Beacon> BeaconList, Location loc);  }