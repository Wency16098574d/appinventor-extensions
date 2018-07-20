package edu.polyu.appinventor.positioning.convertor;

import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;

public class F1 implements Convertor{
  public void convert(List<Beacon> BeaconList) {
    int N = BeaconList.size();
    double txPower = -59, rssi; //hard coded power value. Usually ranges between -59 to -65
    for (int i = 0; i < N; i++) {
      rssi = BeaconList.get(i).getRssi();
      if(rssi == 0) { BeaconList.get(i).setDistance(0);   continue; };
      double ratio = rssi * 1.0 / txPower;
      if (ratio < 1.0) BeaconList.get(i).setDistance(Math.pow(ratio, 10));
      else BeaconList.get(i).setDistance((0.89976) * Math.pow(ratio, 7.7095) + 0.111);
    }
  }
}