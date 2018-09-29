package edu.polyu.appinventor.positioning.algorithm;

import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;
import edu.polyu.appinventor.positioning.Location;

public interface Algorithm{
  public Location calPosition(List<Beacon> BeaconList);
}