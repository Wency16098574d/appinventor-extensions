package edu.polyu.appinventor.positioning.filter;

import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;

public interface Filter{
  int filtering(List<Beacon> BeaconList);
}