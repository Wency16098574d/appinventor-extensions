package edu.polyu.appinventor.positioning.convertor;

import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;

public class NUFO implements Convertor{
  private final static int rssiNUbound = -62, rssiUFbound = -67;
  private final static double distN = 1.5, distU = 4.5, distF = 6;
  public void convert(List<Beacon> BeaconList) {
    int N = BeaconList.size();
    double rssi;
    for (int i = 0; i < N; i++) {
      rssi = BeaconList.get(i).getRssi();
      if (rssi == 0) BeaconList.get(i).setDistance(0);
      else if (rssi >= -62) BeaconList.get(i).setDistance(distN);
      else if (rssi >= -67) BeaconList.get(i).setDistance(distU);
      else BeaconList.get(i).setDistance(distF);
    }
  }
}