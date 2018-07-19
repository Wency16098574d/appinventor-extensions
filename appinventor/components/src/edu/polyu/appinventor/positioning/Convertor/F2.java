package edu.polyu.appinventor.positioning.convertor;

import java.lang.Double;
import java.util.List;
import edu.polyu.appinventor.positioning.Beacon;

//https://forums.estimote.com/t/use-rssi-measure-the-distance/3665
//https://forums.estimote.com/t/determine-accurate-distance-of-signal/2858/2?u=mlusiak
public class F2 implements Convertor{
    public void convert(List<Beacon> BeaconList) {
        int N = BeaconList.size(), n = 2; // n from 2 to 2.5
        double txPower = -59; //hard coded power value. Usually ranges between -59 to -65
//      if (rssi == 0)    return -1.0;
        double rssi;
        for (int i = 0; i < N; i++) {
            rssi = BeaconList.get(i).getRssi();
            if(rssi == 0)   BeaconList.get(i).setDistance(0);
            else BeaconList.get(i).setDistance(Math.pow(10, (txPower - rssi) / (10 * n)));
        }
    }
}